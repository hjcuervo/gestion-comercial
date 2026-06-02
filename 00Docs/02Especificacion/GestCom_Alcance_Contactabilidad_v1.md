# GestCom — Alcance del Rediseño de Contactabilidad (v1)

**Empresa:** Arquitecsoft SAS
**Producto:** GestCom — Plataforma de Gestión Comercial, Contractual y Flujo de Facturación
**Repositorio:** https://github.com/hjcuervo/gestion-comercial
**Mundo afectado:** Mundo 1 (Gestión Comercial) — entidades `GcEmpresa` y `GcPersona`.
**Tipo de cambio:** Modelo de datos + backend + frontend. **No toca Mundo 2 (Contractual) ni Mundo 3 (Facturación).**
**Estado:** Borrador para acordar antes de codificar.

---

## 1. Propósito y origen

Hoy la información de contacto vive como campos sueltos en `GcEmpresa` / `GcPersona` (típicamente un `telefono` y un `email`). Eso no alcanza para operar comercialmente:

- Una **empresa** tiene varios canales (conmutador, celulares de área, WhatsApp corporativo, correos por departamento, redes sociales).
- Una **persona** tiene varios números clasificados por uso (personal, empresarial, WhatsApp…) y redes sociales propias.
- Se necesita **operar** sobre el dato, no solo guardarlo: marcar un número desde una plataforma externa de llamadas, enviar correo desde una plataforma externa de email, y abrir una red social con un clic.

Este documento define el paso de "campos de contacto sueltos" a un **modelo de contactabilidad multicanal, clasificable y accionable**, manteniendo la integridad referencial y las convenciones del proyecto.

---

## 2. Principios de diseño

1. **Un solo modelo de contacto** para empresas y personas: misma forma de dato, distinto propietario.
2. **El dato se guarda listo para operar.** Los teléfonos se normalizan a E.164 al registrar; los correos marcan si son enviables; las redes guardan su URL accesible directa. La integración con plataformas externas consume datos ya normalizados, no improvisa.
3. **Clasificación explícita.** Cada medio tiene un `tipoCanal` (qué es) y, para personas, una `categoria` (para qué se usa). WhatsApp es una *capacidad* de un número, no una categoría.
4. **Histórico, no borrado.** Los medios se desactivan (`activo = false`), no se borran físicamente, para preservar trazabilidad de llamadas/correos futuros.
5. **Cambios mínimos y compatibilidad.** Los campos legacy (`telefono`, `email`) se migran y se deprecan de forma ordenada; nada del Mundo 2/3 se toca.

---

## 3. Modelo de datos propuesto

### 3.1 Decisión estructural: una tabla `GC_CONTACTO`

Se propone **una sola tabla** con dos FK de propietario, de las cuales exactamente una se puebla:

- `empresa_id` (FK a `GC_EMPRESA`, nullable)
- `persona_id` (FK a `GC_PERSONA`, nullable)
- `CHECK` que obliga a que exactamente una esté presente.

**Por qué una sola tabla (recomendado):** evita duplicar entidad/repo/service/controller para empresa y persona; conserva FKs reales (integridad referencial garantizada por Oracle); consistente con la lección aprendida #2 (FKs nullable cuando no siempre se poblan). La alternativa (dos tablas paralelas `GC_EMPRESA_CONTACTO` / `GC_PERSONA_CONTACTO`) queda como **Decisión Abierta D-1**.

### 3.2 Enums (anidados en la entidad, `EnumType.STRING` + CHECK)

- **`TipoCanal`**: `TELEFONO`, `CELULAR`, `EMAIL`, `RED_SOCIAL`.
- **`Categoria`** (clasificación de uso, obligatoria en persona): `PERSONAL`, `EMPRESARIAL`, `OTRO`.

> **WhatsApp** no es un valor de enum: es la bandera `esWhatsapp` sobre medios `TELEFONO`/`CELULAR` (ver D-2). Así un número puede ser a la vez `EMPRESARIAL` y tener WhatsApp, y el link `wa.me/<e164>` se construye del número normalizado.

### 3.3 Catálogo `GC_RED_SOCIAL`

Catálogo transversal (prefijo `GC_`), semilla inicial editable:

| codigo | nombre | url_base (ref.) | icono (Tabler) |
|--------|--------|------------------|----------------|
| `LINKEDIN` | LinkedIn | `https://www.linkedin.com/` | `brand-linkedin` |
| `INSTAGRAM` | Instagram | `https://www.instagram.com/` | `brand-instagram` |
| `X` | X (Twitter) | `https://x.com/` | `brand-x` |
| `FACEBOOK` | Facebook | `https://www.facebook.com/` | `brand-facebook` |
| `TIKTOK` | TikTok | `https://www.tiktok.com/` | `brand-tiktok` |
| `YOUTUBE` | YouTube | `https://www.youtube.com/` | `brand-youtube` |
| `WEB` | Sitio web | — | `world` |
| `OTRA` | Otra | — | `link` |

`url_base` es referencial (validación/armado de link); `icono` lo consume `GcIcon` en el frontend.

### 3.4 Entidad `GcContacto` (forma, en `domain/contacto/`)

```
GcContacto
├── id                 Long
├── empresa            GcEmpresa  (LAZY, nullable)   ── exactamente uno
├── persona            GcPersona  (LAZY, nullable)   ──
├── tipoCanal          TipoCanal  (NOT NULL)
├── categoria          Categoria  (NULL en empresa; NOT NULL en persona)
├── etiqueta           String(60) ── "Conmutador", "Celular ventas", "Soporte"
├── valor              String(255) NOT NULL ── valor de visualización (tal como se digita)
│   — Teléfono/Celular:
├── indicativoPais     String(5)  ── "+57"
├── numeroE164         String(20) ── "+573001234567" (para la plataforma de llamadas)
├── extension          String(10) (nullable)
├── esWhatsapp         boolean DEFAULT false
│   — Email:
├── enviable           boolean DEFAULT true ── habilita envío vía plataforma externa
│   — Red social:
├── redSocial          GcRedSocial (LAZY, nullable) ── FK al catálogo
├── url                String(500) ── link accesible directo
│   — Comunes:
├── esPrincipal        boolean DEFAULT false ── principal de su tipoCanal
├── activo             boolean DEFAULT true  ── soft delete
├── observaciones      String(500) (nullable)
└── auditoría          creadoPor:Long, modificadoPor:Long, fechaCreacion, fechaModificacion
```

### 3.5 DDL Oracle (ilustrativo — final en fase de implementación)

```sql
CREATE TABLE GC_RED_SOCIAL (
    id        NUMBER(19) GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    codigo    VARCHAR2(20) NOT NULL,
    nombre    VARCHAR2(60) NOT NULL,
    url_base  VARCHAR2(200),
    icono     VARCHAR2(40),
    orden     NUMBER(3) DEFAULT 0,
    activo    NUMBER(1) DEFAULT 1 NOT NULL,
    CONSTRAINT UK_GC_RED_SOCIAL_COD UNIQUE (codigo)
);

CREATE TABLE GC_CONTACTO (
    id                 NUMBER(19) GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    empresa_id         NUMBER(19),
    persona_id         NUMBER(19),
    tipo_canal         VARCHAR2(20) NOT NULL,
    categoria          VARCHAR2(20),
    etiqueta           VARCHAR2(60),
    valor              VARCHAR2(255) NOT NULL,
    indicativo_pais    VARCHAR2(5),
    numero_e164        VARCHAR2(20),
    extension          VARCHAR2(10),
    es_whatsapp        NUMBER(1)  DEFAULT 0 NOT NULL,
    enviable           NUMBER(1)  DEFAULT 1 NOT NULL,
    red_social_id      NUMBER(19),
    url                VARCHAR2(500),
    es_principal       NUMBER(1)  DEFAULT 0 NOT NULL,
    activo             NUMBER(1)  DEFAULT 1 NOT NULL,
    observaciones      VARCHAR2(500),
    creado_por         NUMBER(19) NOT NULL,
    modificado_por     NUMBER(19),
    fecha_creacion     TIMESTAMP  NOT NULL,
    fecha_modificacion TIMESTAMP,
    CONSTRAINT FK_GC_CONTACTO_EMP   FOREIGN KEY (empresa_id)     REFERENCES GC_EMPRESA(id),
    CONSTRAINT FK_GC_CONTACTO_PER   FOREIGN KEY (persona_id)     REFERENCES GC_PERSONA(id),
    CONSTRAINT FK_GC_CONTACTO_RED   FOREIGN KEY (red_social_id)  REFERENCES GC_RED_SOCIAL(id),
    CONSTRAINT FK_GC_CONTACTO_USR_C FOREIGN KEY (creado_por)     REFERENCES GC_USUARIO(id),
    CONSTRAINT FK_GC_CONTACTO_USR_M FOREIGN KEY (modificado_por) REFERENCES GC_USUARIO(id),
    -- Exactamente un propietario
    CONSTRAINT CK_GC_CONTACTO_OWNER CHECK (
        (empresa_id IS NOT NULL AND persona_id IS NULL) OR
        (empresa_id IS NULL AND persona_id IS NOT NULL)
    ),
    CONSTRAINT CK_GC_CONTACTO_TIPO  CHECK (tipo_canal IN ('TELEFONO','CELULAR','EMAIL','RED_SOCIAL')),
    CONSTRAINT CK_GC_CONTACTO_CAT   CHECK (categoria IS NULL OR categoria IN ('PERSONAL','EMPRESARIAL','OTRO')),
    -- Teléfono/Celular exige E.164 normalizado
    CONSTRAINT CK_GC_CONTACTO_TEL   CHECK (tipo_canal NOT IN ('TELEFONO','CELULAR') OR numero_e164 IS NOT NULL),
    -- Red social exige catálogo + url
    CONSTRAINT CK_GC_CONTACTO_RED   CHECK (tipo_canal <> 'RED_SOCIAL' OR (red_social_id IS NOT NULL AND url IS NOT NULL)),
    -- WhatsApp solo sobre números
    CONSTRAINT CK_GC_CONTACTO_WA    CHECK (es_whatsapp = 0 OR tipo_canal IN ('TELEFONO','CELULAR'))
);

CREATE INDEX IDX_GC_CONTACTO_EMP   ON GC_CONTACTO(empresa_id);
CREATE INDEX IDX_GC_CONTACTO_PER   ON GC_CONTACTO(persona_id);
CREATE INDEX IDX_GC_CONTACTO_TIPO  ON GC_CONTACTO(tipo_canal);
CREATE INDEX IDX_GC_CONTACTO_E164  ON GC_CONTACTO(numero_e164);
```

> Las `CHECK` cross-columna van como CHECK de fila (Oracle las soporta). Recordar la lección #3: si se agrega un valor a un enum, actualizar la `CHECK` correspondiente.

---

## 4. Reglas de negocio (RB-20 … RB-31)

Continúan la matriz RB-XX del Mundo 1. Las validaciones viven en el `ContactoService`, devuelven `BusinessException(code, message)`.

| ID | Regla | Código sugerido |
|----|-------|-----------------|
| RB-20 | Un contacto pertenece a **exactamente una** empresa o persona (CHECK de propietario). | `CONTACTO_OWNER_INVALIDO` |
| RB-21 | Todo `TELEFONO`/`CELULAR` se almacena normalizado a **E.164** (`numeroE164`) además del `valor` de visualización; `indicativoPais` obligatorio. | `RB_21_E164_REQUERIDO` |
| RB-22 | `esWhatsapp = true` solo es válido sobre `TELEFONO`/`CELULAR` con `numeroE164` válido (el link es `wa.me/<e164>`). | `RB_22_WHATSAPP_INVALIDO` |
| RB-23 | `EMAIL` debe pasar validación de formato; `enviable` habilita su uso por la plataforma externa de correo. | `RB_23_EMAIL_INVALIDO` |
| RB-24 | `RED_SOCIAL` requiere `redSocial` (catálogo) + `url` accesible directa (`https://…`). | `RB_24_RED_INVALIDA` |
| RB-25 | A lo sumo **un** medio `esPrincipal = true` por `tipoCanal` y por propietario. Marcar uno nuevo desmarca el anterior. | `RB_25_PRINCIPAL_DUPLICADO` |
| RB-26 | En **persona**, `categoria` es obligatoria. En **empresa**, `categoria` se ignora (default lógico `EMPRESARIAL`). | `RB_26_CATEGORIA_REQUERIDA` |
| RB-27 | "Eliminar" un contacto es **soft delete** (`activo = false`); no se borra físicamente. | — |
| RB-28 | No se permiten dos medios idénticos activos para el mismo propietario (mismo `tipoCanal` + `numeroE164`/`valor`). | `RB_28_CONTACTO_DUPLICADO` |
| RB-29 | El propietario (empresa/persona) debe existir y no estar en estado inmutable cuando aplique. | `RB_29_PROPIETARIO_INVALIDO` |
| RB-30 | El `valor` y la `url` se sanitizan (trim, normalización básica) antes de persistir. | — |
| RB-31 | La acción de llamada/correo/red **solo** opera sobre medios `activo = true` (y `enviable = true` para correo). | `RB_31_MEDIO_NO_ACCIONABLE` |

> Pendiente al cerrar: matriz formal **RB-XX → endpoints** (como B-04 del Mundo 1).

---

## 5. API REST (`/api/v1/…`)

Contactos como **sub-recurso del propietario** para alta/listado; acciones sobre el id del contacto:

| Método | Ruta | Propósito |
|--------|------|-----------|
| `GET`  | `/api/v1/empresas/{empresaId}/contactos` | Lista contactos de una empresa (filtro `?tipo_canal=`, `?activo=`). |
| `POST` | `/api/v1/empresas/{empresaId}/contactos` | Crea contacto de empresa. |
| `GET`  | `/api/v1/personas/{personaId}/contactos` | Lista contactos de una persona. |
| `POST` | `/api/v1/personas/{personaId}/contactos` | Crea contacto de persona (con `categoria`). |
| `PUT`  | `/api/v1/contactos/{id}` | Actualiza un contacto. |
| `DELETE` | `/api/v1/contactos/{id}` | Soft delete (`activo = false`). |
| `POST` | `/api/v1/contactos/{id}/principal` | Marca como principal de su `tipoCanal` (RB-25). |
| `GET`  | `/api/v1/redes-sociales` | Catálogo `GC_RED_SOCIAL` (activos). |

**Acciones de integración externa (contrato de datos; integración real diferida):**

| Método | Ruta | Devuelve / hace |
|--------|------|------------------|
| `GET`  | `/api/v1/contactos/{id}/llamada` | Payload de marcado: `{ numeroE164, extension }` para la plataforma de llamadas (click-to-call). |
| `GET`  | `/api/v1/contactos/{id}/correo`  | Payload de envío: `{ email, enviable }` para la plataforma de correo. |

> WhatsApp y redes sociales se resuelven en el **frontend** abriendo `wa.me/<e164>` y `url` directamente (no requieren endpoint).

DTOs: POJO con `fromEntity` (relaciones aplanadas a id + nombre/codigo), validaciones Jakarta en los `*Request`, enums serializados como string. Listados con `PageResponse<T>` si crecen; un panel de contactos por propietario puede devolver `List<ContactoResponse>` directo (volumen acotado) — **Decisión Abierta D-4**.

---

## 6. Integración con plataformas externas (alcance del dato)

Este rediseño **prepara el dato**, no construye los conectores. Lo que se contempla ahora:

- **Llamadas:** `numeroE164` + `extension` listos para entregar a una plataforma de telefonía (Twilio/3CX/lo que se decida — **D-3**). El frontend disparará la acción; el backend ya expone el payload.
- **Correo:** `email` + `enviable` para una plataforma de envío (SMTP/SendGrid/Graph — **D-3**). `enviable` permite excluir correos de "no enviar".
- **Redes / WhatsApp:** se abren por link directo desde el frontend (`url`, `wa.me/<e164>`), `target="_blank"`.

La elección y cableado de cada plataforma externa será una fase/spec aparte una vez exista el dato.

---

## 7. Frontend ("Instrumento")

Gestión de contactos dentro de las pantallas Operativo de Empresas y Personas, en el **panel contextual (`#aside`)** o sección de detalle, con renglones `GcListRow` (no tarjetas):

- Cada contacto = un renglón: marca de estado lateral por `tipoCanal`/`activo`, `valor` en `.gc-mono`, `etiqueta` + `categoria` como `GcBadge`, y acciones al final (`GcIcon`):
  - `TELEFONO`/`CELULAR`: icono `phone` (click-to-call) + si `esWhatsapp`, `brand-whatsapp` → `wa.me`.
  - `EMAIL`: icono `mail` (mailto / plataforma) si `enviable`.
  - `RED_SOCIAL`: icono del catálogo (`redSocial.icono`) → abre `url`.
- Alta/edición vía `GcDrawer`/`GcModal` con campos según `tipoCanal` (mostrar/ocultar bloque teléfono/email/red).
- `<select>` de país para `indicativoPais` (ojo lección #2 frontend: País usa `nombre`/`codigo`).
- Tokens `--gc-*`, iconos Tabler vía `GcIcon`, formatters de `utils/`.

---

## 8. Backlog (Fx.y)

### F-CT1 — Modelo de datos y backend base
- **T-CT1.1** Catálogo `GC_RED_SOCIAL` (entidad `GcRedSocial`, repo, DDL, semilla).
- **T-CT1.2** Entidad `GcContacto` + enums anidados (`TipoCanal`, `Categoria`) + DDL con CHECKs.
- **T-CT1.3** `GcContactoRepository` (consultas por empresa/persona con filtros opcionales).
- **T-CT1.4** DTOs: `ContactoResponse`, `ContactoCreateRequest`, `ContactoUpdateRequest`, `RedSocialResponse`.
- **T-CT1.5** `ContactoService` con reglas RB-20…RB-31 (incluye `esPrincipal` y soft delete).
- **T-CT1.6** Controllers: contactos (sub-recurso empresa/persona) + `redes-sociales`.

### F-CT2 — Normalización y validaciones
- **T-CT2.1** Normalizador E.164 (`indicativoPais` + número → `numeroE164`); util backend.
- **T-CT2.2** Validación de email, URL de red social, y CHECK de duplicados (RB-28).
- **T-CT2.3** Endpoints de acción `/principal`, `/llamada`, `/correo` (payloads).

### F-CT3 — Frontend: contactos de Empresa
- **T-CT3.1** `contacto.service.js` (CRUD + redes + acciones).
- **T-CT3.2** Sección/aside de contactos en la vista de Empresa (renglones `GcListRow`).
- **T-CT3.3** `ContactoDrawer` (alta/edición por `tipoCanal`).
- **T-CT3.4** Acciones inline: llamar, WhatsApp, correo, abrir red.

### F-CT4 — Frontend: contactos de Persona
- **T-CT4.1** Reutilizar `contacto.service`/`ContactoDrawer` en la vista de Persona.
- **T-CT4.2** Categoría obligatoria (PERSONAL/EMPRESARIAL/OTRO) en la UI de persona.
- **T-CT4.3** Redes sociales de la persona.

### F-CT5 — Migración de datos legacy
- **T-CT5.1** Inventariar campos de contacto actuales en `GcEmpresa`/`GcPersona` (verificar en `main`).
- **T-CT5.2** Script de migración: volcar `telefono`/`email`/`sitioWeb` existentes a `GC_CONTACTO`.
- **T-CT5.3** Deprecar campos legacy (lectura puente → retiro) — **D-5**.

### F-CT6 — Integración externa (spec aparte)
- **T-CT6.1** Elegir y cablear plataforma de llamadas (**D-3**).
- **T-CT6.2** Elegir y cablear plataforma de correo (**D-3**).
- **T-CT6.3** Matriz RB-20…RB-31 → endpoints.

---

## 9. Decisiones abiertas

| # | Decisión | Recomendación |
|---|----------|---------------|
| D-1 | ¿Tabla única `GC_CONTACTO` (dos FK + CHECK) o dos tablas paralelas por propietario? | **Tabla única** (menos duplicación, FKs reales). |
| D-2 | ¿WhatsApp como bandera `esWhatsapp` o como valor de `TipoCanal`/`Categoria`? | **Bandera** sobre teléfono/celular. |
| D-3 | ¿Qué plataformas externas de llamadas y de correo? | Definir en F-CT6; por ahora solo el contrato de datos. |
| D-4 | ¿Listado de contactos paginado o `List` directa? | `List` directa por propietario (volumen acotado). |
| D-5 | ¿Se retiran ya los campos `telefono`/`email` legacy o se mantienen como puente? | Puente temporal, retiro tras migración (F-CT5). |
| D-6 | ¿`categoria` también aplicable a empresa (no solo persona)? | No por ahora; empresa = EMPRESARIAL implícito. |

---

## 10. Lo que NO cambia

- Stack (Spring Boot 3.4 + Java 21 + Oracle 23c + Vue 3 + Vite + Pinia).
- Mundo 2 (Contractual) y Mundo 3 (Facturación): intactos.
- Convenciones de dominio (prefijos `Gc`/`GC_`, auditoría con `creadoPor: Long`, backlog Fx.y, RB-XX, `BusinessException` con código, `GlobalExceptionHandler` mostrando causa raíz).
- Flujo de negocio crítico (GANADA → contractual → Formalizar → VIGENTE → CONTRATADA).

---

*Documento de alcance de contactabilidad — v1. Base para acordar antes de codificar. Confirmar D-1…D-6 antes de F-CT1.*
