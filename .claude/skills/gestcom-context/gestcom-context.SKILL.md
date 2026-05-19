---
name: gestcom-context
description: Provides foundational context for the GestCom platform — Arquitecsoft's commercial, contractual and billing-flow management system built with Spring Boot 3.4 + Java 21 + Oracle 23c + Vue 3. Use this skill at the start of ANY task related to GestCom, the gestion-comercial repository, the 3 functional "Mundos" (Comercial, Contractual, Flujo de Facturación), or when the user mentions GestCom-specific concepts like oportunidad, pipeline contractual, GcContrato, GcCompromisoIngreso, FACTRO, TRM, or Hector's Arquitecsoft project. This is the ROOT skill — load it before gestcom-backend or gestcom-frontend so all domain decisions are made with full project context.
---

# GestCom — Contexto del Proyecto

Esta skill establece el contexto base de la plataforma GestCom. **Cárgala primero** en cualquier tarea relacionada con el proyecto antes de pasar a las skills de backend o frontend.

> **Fuente de verdad:** este documento se mantiene alineado con `00Docs/04Sessiones/Estado_arte_05052026.md` (v2.1 o superior). Si esta skill entra en conflicto con el Estado del Arte más reciente del repo, **gana el Estado del Arte**. Reportar la divergencia para actualizar la skill.

---

## 1. Identidad del proyecto

- **Producto:** GestCom — Plataforma de Gestión Comercial, Contractual y Flujo de Facturación.
- **Empresa:** Arquitecsoft SAS.
- **Desarrollador principal:** Hector.
- **Repositorio:** `https://github.com/hjcuervo/gestion-comercial`.
- **Idioma del dominio:** Español. Nombres de entidades, campos, mensajes de UI y comentarios están en español. El código técnico (clases Java, métodos, variables) sigue convenciones estándar pero los nombres del dominio se mantienen en español (ej. `GcOportunidad`, `etapa`, `GcCompromisoIngreso`).
- **Sistema externo crítico:** **FACTRO** — sistema de facturación electrónica (DIAN) externo a GestCom. GestCom **no emite facturas**, solo cruza las que FACTRO emite.

---

## 2. Stack tecnológico (no negociable)

| Capa | Tecnología | Versión |
|------|------------|---------|
| Backend | Spring Boot | 3.4 |
| Lenguaje backend | Java | 21 |
| Base de datos | Oracle | 23c |
| ORM | Hibernate / JPA | (incluido en Spring Boot 3.4) |
| Migraciones | Flyway | (actualmente con deuda — ver DT-08) |
| Frontend | Vue | 3 (Composition API) |
| Build frontend | Vite | — |
| Estado frontend | Pinia | — |
| Sistema de diseño | **Aurora Dark** | (legacy "Luxury Tech" deprecado, ver §3) |
| Autenticación | JWT + Spring Security | — |
| RBAC | Admin / Comercial / Lectura-KPI | — |
| Infraestructura objetivo | OCI (Oracle Cloud Infrastructure) | — |

**Cuando trabajes en GestCom:** nunca propongas cambiar de stack, ni introducir librerías que no encajen con este (nada de MyBatis, Quasar, Vuex, MySQL, Tailwind, etc.).

---

## 3. Arquitectura

- **Monolito modular por dominios.** El backend organiza el código en paquetes por dominio funcional (`domain/{empresa,contrato,facturacion,...}`), no por capa técnica.
- **Infraestructura transversal en `infrastructure/`:** `config/`, `dto/` (PageResponse), `exception/` (BusinessException, ErrorResponse, GlobalExceptionHandler), `security/` (JWT + Spring Security). **No existe paquete `audit/` separado** — la auditoría son 4 columnas en cada entidad.
- **API-only.** Backend nunca renderiza HTML; expone REST y el frontend SPA consume.
- **Pipelines dinámicos parametrizables.** Motor genérico, se diferencia por `ambito` (`COMERCIAL` o `CONTRATACION`).
- **Auditoría obligatoria** en transiciones y cambios relevantes.
- **Sistema visual "Aurora Dark":** decisión congelada (reemplazó a "Luxury Tech"). Los componentes ya están renombrados a `Aurora*` (`AuroraLayout`, `AuroraCard`, etc.), **pero las variables CSS aún se llaman con la nomenclatura legacy** (`--primary`, `--bg-deep`, `--text-primary`, etc.). El archivo `tokens.css` todavía rotula "Luxury Tech Aesthetic". Migrar los tokens es deuda DT-05.

---

## 4. Los 3 Mundos

GestCom se organiza en **3 mundos funcionales** integrados:

| # | Mundo | Alcance | Estado actual |
|---|-------|---------|----------------|
| 1 | **Gestión Comercial** | Identificación de oportunidad → adjudicación. Pipeline comercial, actividades, compromisos, documentos. | ✅ Completo (~95%) |
| 2 | **Gestión Contractual** | Oportunidad GANADA → contrato VIGENTE. Pipeline contractual, contratos, modificaciones. | ✅ Funcional (~95%, deuda menor abierta) |
| 3 | **Flujo de Facturación** | Indicar qué facturar, registrar gestión por compromiso, cruzar con facturas FACTRO, KPIs, Excel EMI con conversión TRM. | 🟡 Backend ~85%, Frontend ~25% |

> **Histórico:** existió un "Mundo 4" (registro y análisis de facturas reales) que fue **fusionado dentro del Mundo 3** durante el rediseño de Mundo 3 v3. Si alguna sesión vieja lo menciona, ya no aplica.

---

## 5. Flujo de negocio crítico

```
Mundo 1 — Pipeline Comercial (ABIERTA → SEGUIMIENTO → GANADA)
        │
        └── al marcar GANADA
                ▼
Mundo 2 — Pipeline Contractual (etapas dinámicas)
                │
                └── botón "Formalizar Contrato" (visible mientras no exista contrato)
                        ▼
                GC_CONTRATO nace en estado VIGENTE
                        │
                        └── Oportunidad pasa a CONTRATADA → sale de los pipelines
                                │
Mundo 3 — Flujo de Facturación
                                ▼
        Compromisos de Ingreso (presupuestados al crear el contrato)
                │
                ├── llega su mes → entra al tablero de pendientes
                │
                ├── Gestiones: VALIDACION_SERVICIO, SOPORTE_OBTENIDO, SOLICITUD_EMISION,
                │              FACTURA_CRUZADA, NOVEDAD_*, OBSERVACION
                │
                ├── FACTRO emite factura → se registra en GestCom
                │
                └── Cruce factura ↔ compromiso → estado FACTURADA
                        + se actualiza valor_facturado (puede diferir del presupuestado)
```

**Reglas críticas que nunca se rompen:**

1. El contrato **no existe** durante el pipeline contractual. Solo existe `GcProcesoContratacion`.
2. El contrato nace directamente en estado **VIGENTE** (no hay EN_PROCESO).
3. En el pipeline contractual se usan **etapas**, no estados.
4. En el pipeline comercial: GANADA dispara formalización; PERDIDA / NO_CONCRETADA solo cierran.
5. Una oportunidad en estado CONTRATADA o cerrada (PERDIDA / NO_CONCRETADA) es **inmutable**.
6. Estados válidos del contrato: VIGENTE → SUSPENDIDO ↔ VIGENTE → TERMINADO → LIQUIDADO.
7. **GestCom no emite facturas.** FACTRO sí. GestCom solo cruza.
8. Una entrada de bitácora (gestión) es **inmutable** una vez creada.
9. Conversión USD → COP usa la TRM del mes del compromiso. Si no hay TRM para ese mes, se usa la del mes anterior más reciente.

---

## 6. Glosario de dominio

| Término | Significado |
|---------|-------------|
| **Oportunidad** | Núcleo del proceso comercial. Pertenece a 1 empresa, 1 pipeline, 1 etapa. |
| **Pipeline** | Flujo configurable de etapas. Tiene `ambito` (COMERCIAL / CONTRATACION). |
| **Etapa** | Fase dentro de un pipeline. Las etapas son ordenadas. |
| **Empresa** | Organización. Puede ser prospecto, cliente o aliado comercial. |
| **Persona** | Contacto individual con relación multi-empresa (N:M vía `GcPersonaEmpresa`). |
| **Actividad** | Interacción registrada (reunión, llamada, envío de propuesta). |
| **Compromiso (comercial)** | Tarea derivada de una actividad. **No confundir con Compromiso de Ingreso.** |
| **Proceso de Contratación** | Pipeline intermedio entre oportunidad GANADA y contrato VIGENTE. |
| **Contrato** | Documento formal que regula la relación. Tipos: Contrato Formal, Orden de Compra, Orden de Servicio, etc. |
| **Modificación** | Adición, prórroga, suspensión, reinicio sobre un contrato. Evento atómico (no tiene estado abierto/cerrado). |
| **Empresa de facturación** | Filial específica que factura, distinta a la empresa cliente principal. |
| **Compromiso de Ingreso** | Cuota o hito de facturación de un contrato. **Renombrado** desde "Forma de Pago" durante el rediseño Mundo 3. Tiene `valor` presupuestado y `valor_facturado` real. |
| **Gestión** | Entrada de bitácora sobre un compromiso. Tipos: `VALIDACION_SERVICIO`, `SOPORTE_OBTENIDO`, `SOLICITUD_EMISION`, `FACTURA_CRUZADA`, `NOVEDAD_*`, `OBSERVACION`. Inmutable. |
| **Factura** | Factura registrada en GestCom (no emitida). El número viene de FACTRO. Identificada por `numero_factura + prefijo` (UNIQUE). |
| **Cruce** | Vínculo factura ↔ compromiso. Al cruzar: estado del compromiso → FACTURADA, se actualiza `valor_facturado`. Es reversible. |
| **TRM** | Tasa Representativa del Mercado mensual (USD → COP). Una tasa por par de monedas por año-mes. |
| **FACTRO** | Sistema externo que emite facturas electrónicas a la DIAN. GestCom solo se integra con él vía registro de las facturas emitidas. |
| **Excel EMI** | Export consolidado con 5 hojas (Tabla COP, Tabla USD, COL pivot, PERU pivot, Resumen Facturado), con conversión a COP usando TRM. |

---

## 7. Convenciones globales

### 7.1 Nombres de tablas y prefijos

- Tablas de negocio: prefijo `GC_` (ej. `GC_OPORTUNIDAD`, `GC_CONTRATO`, `GC_COMPROMISO_INGRESO`).
- **Catálogos transversales también usan `GC_*`**, no `CAT_*`. Ejemplos reales: `GC_PAIS`, `GC_DEPARTAMENTO`, `GC_MUNICIPIO`, `GC_DOCUMENT_TYPE`, `GC_TIPO_CONTRATO`. **Si una skill o doc viejo menciona `CAT_*`, es histórico — la convención vigente es `GC_*` para todo.**

### 7.2 Nombres de entidades JPA

- Prefijo `Gc` + nombre en singular CamelCase: `GcOportunidad`, `GcContrato`, `GcCompromisoIngreso`, `GcTrm`.

### 7.3 Nombres de campos de auditoría

Patrón observado en entidades nuevas (no uniforme):

- `fechaCreacion` (`LocalDateTime`, NOT NULL, `updatable = false`).
- `fechaModificacion` (`LocalDateTime`, nullable) — **no todas las entidades nuevas lo tienen**.
- `creadoPor` (`Long`, NOT NULL) — **FK al usuario por ID**, no string.
- `modificadoPor` (`Long`, nullable) — no todas las entidades nuevas lo tienen.

`@PrePersist` setea `fechaCreacion`. `@PreUpdate` setea `fechaModificacion`. El `creadoPor`/`modificadoPor` se setea explícitamente desde el service usando el usuario autenticado.

### 7.4 Nombres de enums

Los enums se declaran como **enums anidados dentro de la entidad** y se persisten como VARCHAR2 con `@Enumerated(EnumType.STRING)` + CHECK constraint en Oracle. Ejemplos reales:

- `GcContrato.EstadoContrato`: `VIGENTE`, `SUSPENDIDO`, `TERMINADO`, `LIQUIDADO`.
- `GcProcesoContratacion.EstadoProceso`: `EN_CURSO`, `COMPLETADO`, `CANCELADO`.
- `GcContratoModificacion.TipoModificacion`: `ADICION`, `PRORROGA`, `SUSPENSION`, `REINICIO`, `OTRO`.
- Estado de oportunidad (en `GcOportunidad`): `ABIERTA`, `SEGUIMIENTO`, `GANADA`, `PERDIDA`, `NO_CONCRETADA`, `CONTRATADA`.
- Ámbito de pipeline (en `GcPipeline`): `COMERCIAL`, `CONTRATACION`.

---

## 8. Estructura del repositorio

```
gestion-comercial/
├── backend/
│   └── src/main/java/com/arquitecsoft/gestion/
│       ├── GestionApplication.java
│       ├── config/
│       │   └── HealthController.java
│       ├── domain/
│       │   ├── auth/
│       │   ├── empresa/         (Empresa + catálogos Pais, Departamento, Municipio, DocumentType)
│       │   ├── persona/         (Persona + PersonaEmpresa N:M)
│       │   ├── pipeline/        (Pipeline + Etapa)
│       │   ├── oportunidad/     (Oportunidad + Stats)
│       │   ├── actividad/       (Actividad + Compromiso comercial + TipoActividad)
│       │   ├── documento/       (Documento + TipoDocumentoOpp)
│       │   ├── contrato/        (Mundo 2: Contrato, ProcesoContratacion, Modificacion, TipoContrato)
│       │   ├── facturacion/     (Mundo 3: 6 entidades — ver §9.3)
│       │   └── usuario/
│       └── infrastructure/
│           ├── config/          (CorsFilter)
│           ├── dto/             (PageResponse)
│           ├── exception/       (BusinessException, ErrorResponse, GlobalExceptionHandler)
│           └── security/        (JwtAuthenticationFilter, JwtService, SecurityConfig, SecurityUtils, AuthenticatedUser)
├── frontend/
│   └── src/
│       ├── views/               (DashboardView, LoginView, EmpresasView, PersonasView, PipelineView, Oportunidades*, Contratos*, FacturacionView)
│       ├── components/
│       │   ├── actividad/       (ActividadModal, CompromisoModal)
│       │   ├── contrato/        (FormalizarContratoModal, IniciarProcesoModal)
│       │   ├── documento/       (DocumentoModal)
│       │   ├── empresa/         (EmpresaModal)
│       │   ├── facturacion/     (GestionPanel — único componente Mundo 3 por ahora)
│       │   ├── layout/          (AuroraLayout, AuroraHeader, AuroraSidebar, NavRail, TopAppBar)
│       │   ├── oportunidad/
│       │   ├── persona/
│       │   ├── pipeline/
│       │   └── ui/              (Aurora* + Md* + genéricos — convivencia de 3 generaciones, deuda DT-05)
│       ├── services/            (clientes HTTP)
│       ├── stores/              (Pinia)
│       ├── router/
│       ├── layouts/
│       └── assets/styles/       (tokens.css, global.css, style.css)
├── 00Docs/                      (especificaciones, alcance, sesiones)
└── .claude/skills/              (estas skills)
```

---

## 9. Estado actual (corte: 5 de mayo de 2026, post Bloque A)

### 9.1 Mundo 1 — Comercial ✅

CRUD completo de Empresas / Personas / Oportunidades / Pipelines / Actividades / Compromisos / Documentos. Pipeline Kanban, dashboard, login, RBAC. ~42 endpoints REST. Matriz RB-01 a RB-19 aplicada. Pendientes menores: reportes exportables, filtros persistentes, mejoras de paginación. **No bloquea nada.**

### 9.2 Mundo 2 — Contractual ✅ (con deuda)

Backend y frontend funcionales. "Formalizar Contrato" desde oportunidad GANADA opera. Listado y detalle de contratos, modificaciones, cambios de estado (suspender, reiniciar, terminar, liquidar).

**Nota crítica:** la antigua `GcContratoFormaPago` **ya no existe en el código activo**. Fue renombrada y movida a `GcCompromisoIngreso` en `domain/facturacion/` durante el rediseño Mundo 3. La relación `GcContrato.compromisos` navega a esa entidad.

**F6 "Dashboard contractual" no aplica.** Fue reemplazada por la F5 del Mundo 3 (Dashboard integrado).

### 9.3 Mundo 3 — Flujo de Facturación 🟡

**Documento base:** `00Docs/02Especificacion/GestCom_Mundo3_Especificacion_1.md` (spec v3).

**Entidades en `domain/facturacion/` (6):**

| Entidad | Rol |
|---------|-----|
| `GcCompromisoIngreso` | Compromiso de ingreso (antes `GcContratoFormaPago`). |
| `GcCompromisoFactura` | Relación N:M compromiso ↔ factura (la spec dice 1:1, la implementación generaliza — DT-07). |
| `GcCompromisoEvento` | Eventos del compromiso (event sourcing ligero, extra de la implementación). |
| `GcCompromisoGestion` | Gestiones registradas (bitácora). |
| `GcFactura` | Factura registrada (no emitida). |
| `GcTrm` | TRM mensual por par de monedas. |

**Backend ~85%:** 3 controllers (`CompromisoIngresoController` con 14 endpoints, `FacturaController`, `VistaPeriodoController`), 5 services (incluye `CompromisoEstadoMachine`), 6 repositorios, 17 DTOs.

**Frontend ~25%:** solo `FacturacionView.vue` y `components/facturacion/GestionPanel.vue`. Faltan: lista de facturas, modales de registrar/cruzar factura, modal de registrar gestión, sección TRM editable, botón export Excel EMI, integración al dashboard.

**Fases del Mundo 3:**

| Fase | Descripción | Estado |
|------|-------------|--------|
| F1 | Modelo de datos y backend base | ✅ ~95% |
| F2 | Frontend tablero de facturación | 🟡 Parcial |
| F3 | Frontend facturas y cruce | 🔲 No iniciado |
| F4 | TRM y Excel EMI | 🔲 No iniciado |
| F5 | Dashboard integrado | 🔲 No iniciado |

### 9.4 Reglas de negocio del Mundo 3 (resumen RN-01..RN-13)

Definidas en la spec v3. **Falta matriz formal RN-XX → endpoints** (equivalente a B-04 del Mundo 1).

Las críticas:

- RN-01: Una forma de pago solo puede cruzarse con UNA factura. **Implementación lo extiende a N:M — DT-07 abierta.**
- RN-02: Una factura puede cruzarse con MÚLTIPLES formas de pago.
- RN-03: Al cruzar: estado → FACTURADA, se actualiza `valor_facturado`.
- RN-04: Solo se cruzan compromisos PENDIENTES.
- RN-05: Cruce es reversible (descruzar → PENDIENTE, `valor_facturado = null`).
- RN-08: Facturas identificadas por `numero_factura + prefijo` (UNIQUE).
- RN-09: Entradas de bitácora inmutables.
- RN-10 / RN-11: Conversión USD→COP usa TRM del mes; si no hay, la del mes anterior.

### 9.5 Issues y deuda técnica abierta al 5-may-2026

**Cerrados en Bloque A (5-may-2026):** I-01, I-02, DT-01, DT-02, DT-03 (parcial), DT-04.

**Abiertos:**

| ID | Asunto | Severidad |
|----|--------|-----------|
| DT-03 (parcial) | `findByIdWithRelations` deliberadamente no fetcha `compromisos` (riesgo `MultipleBagFetchException`). Si se necesita, usar `EntityGraph` o `Set`. | 🟢 Baja |
| DT-05 | Convivencia de 3 generaciones de UI (`Md*`, `Aurora*`, genéricos) + tokens CSS aún con nomenclatura "Luxury Tech". | 🟡 Media |
| DT-06 | Skills desactualizadas vs código real (esta misma actualización ataca esto). | 🟡 Media |
| DT-07 | Implementación generaliza factura↔compromiso a N:M, contradice RN-01 spec v3. Decidir y reflejar en spec v4. | 🟡 Media |
| DT-08 | Sólo 1 archivo Flyway (`V1__initial_schema.sql`). Cambios M2/M3 no versionados. | 🟠 Alta |
| DT-09 | `PipelineView.vue` líneas 266-267 invoca `<FormalizarContratoModal>` dos veces. No tocar sin entender el flujo Kanban. | 🟢 Baja |

---

## 10. Cómo trabajamos juntos (preferencias del desarrollador)

1. **Cambios mínimos.** Cuando se pide una corrección, aplicarla aislada, sin reescribir archivos completos.
2. **Archivos descargables, no inline.** Cuando se generen archivos, presentarlos con `present_files`, no pegarlos como bloques de código en el chat (salvo fragmentos cortos).
3. **Verificar el repo antes de modificar.** Cuando hay duda sobre el estado actual, `git clone` o `git pull` antes de proponer cambios. Las skills pueden estar desactualizadas; el Estado del Arte más reciente y el código `main` ganan.
4. **Especificar antes de codificar.** Para nuevos mundos o features grandes, primero generar documento de alcance + backlog de tareas; nunca saltar a código directamente.
5. **Errores con causa raíz.** El `GlobalExceptionHandler` está configurado para mostrar la causa raíz en desarrollo. Mantener ese comportamiento; no envolver excepciones en mensajes genéricos durante debugging.
6. **Backlog formato Fx.y / Tx.y.** Las tareas se numeran como `T1.1`, `T1.2`, etc. dentro de fases `F1`, `F2`...
7. **Reglas de negocio formato RB-XX (M1) / RN-XX (M3).** M1 ya tiene matriz; M3 está pendiente.
8. **Deuda técnica formato DT-XX.** Ver §9.5.

---

## 11. Documentos de referencia en el repo

| Documento | Propósito |
|-----------|-----------|
| `00Docs/04Sessiones/Estado_arte_05052026.md` | **Fuente de verdad del estado del proyecto.** Esta skill se alinea con él. |
| `00Docs/01Infraestructura/01-STACK-SIMPLIFICADO.md` | Stack y decisiones de infra. |
| `00Docs/01Infraestructura/02-CHECKLIST-SETUP.md` | Setup local. |
| `00Docs/01Infraestructura/03-ARCHIVOS-CONFIGURACION.md` | Configuración (application.yml, etc.). |
| `00Docs/02Especificacion/B-01-Modelo-Logico-Datos.md` | Modelo lógico. |
| `00Docs/02Especificacion/B-02-Modelo-Fisico-DDL.md` | DDL Oracle. |
| `00Docs/02Especificacion/B-03-Contrato-API-OpenAPI.md` | Contrato API. |
| `00Docs/02Especificacion/B-04-Matriz-Reglas-Negocio.md` | Matriz RB-XX → endpoints (solo Mundo 1). |
| `00Docs/02Especificacion/Especificacion Funcional 1.md` | Espec Mundo 1. |
| `00Docs/02Especificacion/GestCom_Alcance_Contratacion_v1.md` | Espec Mundo 2. |
| `00Docs/02Especificacion/GestCom_Mundo3_Especificacion_1.md` | Espec Mundo 3 v3. |
| `00Docs/03Estandar Visual/A-01..A-05` | Estándar visual MD3 (puede estar desfasado vs Aurora). |
| `00Docs/04Sessiones/SESION-*.md` | Histórico de sesiones. |
| `00Docs/04Sessiones/Estado_arte_04052026.md` | ⚠️ Reemplazado por `Estado_arte_05052026.md`. |

**Documentos pendientes:**

- Matriz RN-01..RN-13 del Mundo 3 mapeada a endpoints.
- Spec v4 del Mundo 3 reconciliando 1:1 vs N:M (resolución de DT-07).
- Documento que oficialice "no hay Mundo 4".

---

## 12. Lecciones aprendidas (deuda anti-patrones)

Lecciones costosas de sesiones pasadas. **Tenlas presentes siempre:**

1. **`@OrderBy` debe apuntar a un campo Java existente en la entidad** (no al nombre de columna). Hibernate no valida en tiempo de compilación; un error aquí provoca `ORA-00904` con alias `M1_0` que despistan.
2. **`nullable = false` en FKs bloquea inserts cuando la FK no siempre se popula.** Caso real: `proceso_contratacion_id` en `GcContrato` se quitó `nullable = false` porque el contrato puede crearse sin proceso intermedio en escenarios especiales.
3. **Constraints CHECK de Oracle deben sincronizarse con los enums de Java.** Cuando se agrega un valor al enum (ej. `CONTRATADA`), hay que `ALTER` el constraint o el insert falla.
4. **Lazy-load fuera de transacción explota.** Consultas que se serializan después de cerrar la transacción deben usar `JOIN FETCH` explícito en JPQL.
5. **Caché de Hibernate puede enmascarar correcciones.** Después de cambios en anotaciones JPA críticas (`@OrderBy`, `@JoinColumn`), correr `mvn clean package -DskipTests` antes de probar.
6. **`MultipleBagFetchException` con dos colecciones a la vez.** Hibernate no permite `JOIN FETCH` de dos `List` (bags) simultáneamente. Soluciones: (a) usar `EntityGraph` con `LOAD` graph, (b) cambiar el tipo de una colección a `Set`, (c) hacer dos queries separadas. Caso real: `GcContrato` tiene `modificaciones` y `compromisos` como listas; `findByIdWithRelations` solo fetcha una de las dos.
7. **Las skills pueden quedar desactualizadas.** Si una skill entra en conflicto con el `Estado_arte_*.md` más reciente o con el código en `main`, **gana la realidad del repo**. Reportar la divergencia para sincronizar la skill.
8. **Nombre de sistema visual ≠ nomenclatura de tokens.** En transiciones de design system, los archivos CSS pueden seguir titulados con el sistema viejo aunque la decisión esté tomada. Caso real: `tokens.css` rotula "Luxury Tech" pero la decisión congelada es "Aurora Dark". Esto es deuda, no error — documentar al actualizar tokens.

---

## 13. Decisiones congeladas

Decisiones arquitectónicas que no se vuelven a discutir salvo cambio mayor:

1. **3 Mundos, no 4.** El registro de facturas reales se fusionó en el Mundo 3.
2. **GestCom no emite facturas.** FACTRO sí.
3. **Diseño visual: Aurora Dark.** "Luxury Tech" deprecado (tokens pendientes de renombrar).
4. **Estructura de paquetes: `domain/{dominio}/` + `infrastructure/`.** No usar `config/`, `security/`, `audit/` raíz.
5. **Auditoría con FK a usuario (`creadoPor: Long`)**, no string de usuario.
6. **Migraciones via Flyway** (actualmente con deuda DT-08).
7. **Una sola rama de trabajo: `main`.**
8. **Catálogos transversales con prefijo `GC_`**, no `CAT_`.

---

*Cuando esta skill esté activa, todas las decisiones de implementación deben respetar lo aquí descrito. Si algo aquí entra en conflicto con una instrucción puntual del usuario, mencionar el conflicto antes de proceder.*
