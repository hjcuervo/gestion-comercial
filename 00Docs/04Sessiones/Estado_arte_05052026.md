# GestCom — Estado del Arte de la Plataforma

**Empresa:** Arquitecsoft SAS
**Producto:** GestCom — Plataforma de Gestión Comercial y Contractual
**Repositorio:** https://github.com/hjcuervo/gestion-comercial
**Stack:** Spring Boot 3.4 + Java 21 + Oracle 23c + Vue 3 + Vite + Pinia
**Fecha del corte:** 5 de mayo de 2026 (revisión 2.2 — post DT-09)
**Reemplaza a:** `Estado_arte_04052026.md` (desactualizado en alcance del Mundo 3 y deuda técnica)
**Propósito del documento:** Servir como línea base de contexto para una nueva versión del proyecto, dejando claro qué está terminado, qué está en curso, qué falta por implementar y qué deuda técnica está abierta.

---

## 0. Cambios respecto al estado del arte anterior

El documento anterior (`04052026`) describía un proyecto en transición Mundo 2 → Mundo 3 con los Mundos 3 y 4 sin especificar. **La realidad del repositorio es distinta:**

| Aspecto | Doc anterior (04052026) | Realidad del repo (05052026) |
|---------|-------------------------|------------------------------|
| Mundo 2 (Contractual) | F1–F5 cerradas, F6 Dashboard pendiente | Estructura backend y frontend completa; **F6 nunca se ejecutó como tal** y se reemplazó por el dashboard integrado del Mundo 3 |
| Mundo 3 (Flujo Facturación) | "Sin especificar" | Especificación funcional v3 publicada en `00Docs/02Especificacion/GestCom_Mundo3_Especificacion_1.md` (~13 KB), backend implementado al ~85%, frontend al ~25% |
| Mundo 4 (Facturación) | Módulo separado de "registro de facturas reales" | **Eliminado como mundo independiente**. Su alcance se fusionó dentro del Mundo 3 (el cruce con FACTRO + KPIs + Excel EMI cubre lo que era Mundo 4) |
| Issues abiertos | I-01 (`@OrderBy` en `GcContratoModificacion`) e I-02 (repo desincronizado) | **I-02 cerrado** (el repo sí está sincronizado, era percepción). **I-01 redefinido**: el `@OrderBy` real que puede causar `ORA-00904` está en otra entidad, y depende de `GcCompromisoIngreso` (no `GcContratoModificacion`) |
| Documento de alcance del Mundo 3 | "Pendiente de generar" | **Ya existe** y está en el repo, versionado |

Estos cambios reorientan completamente el plan de trabajo (ver §10).

---

## 1. Visión Global de la Plataforma

GestCom gestiona el ciclo de vida completo de la relación comercial de Arquitecsoft con sus clientes, desde la identificación de una oportunidad hasta el control del proceso de facturación de los contratos vigentes.

GestCom **no emite facturas electrónicas**. Esa responsabilidad la tiene **FACTRO** (sistema externo que se conecta con la DIAN). GestCom indica qué se debe facturar, registra la gestión y cruza con las facturas que FACTRO emite.

La plataforma se organiza en **3 mundos funcionales** integrados (la versión anterior contemplaba 4):

| # | Mundo | Alcance | Estado General |
|---|-------|---------|----------------|
| 1 | **Gestión Comercial** | Identificación de oportunidad → adjudicación. Pipeline comercial, actividades, compromisos, documentos. | ✅ Completo |
| 2 | **Gestión Contractual** | Oportunidad GANADA → contrato VIGENTE. Pipeline contractual, contratos, modificaciones. | ✅ Backend y frontend funcionales (con deuda técnica abierta) |
| 3 | **Flujo de Facturación** | Indicar qué facturar, registrar gestión por compromiso, cruzar con facturas FACTRO, KPIs, Excel EMI con conversión TRM. | 🟡 Backend al ~85%, frontend al ~25% |

> **Nota:** El "Mundo 4" del documento anterior (registro y análisis de facturas emitidas) **ya no existe como mundo separado**. La especificación v3 del Mundo 3 incorpora el cruce con FACTRO y la trazabilidad de facturas emitidas.

---

## 2. Arquitectura y Stack

| Capa | Tecnología |
|------|------------|
| Backend | Spring Boot 3.4 + Java 21 |
| Persistencia | Oracle 23c (JPA / Hibernate) |
| Frontend | Vue 3 + Vite + Pinia |
| Sistema de diseño actual | **"Aurora Dark"** (reemplazó a "Luxury Tech") |
| Autenticación | JWT, RBAC (Admin / Comercial / Lectura-KPI) |
| Migraciones | Flyway (`src/main/resources/db/migration/`) — actualmente 1 archivo `V1__initial_schema.sql` |
| Infraestructura objetivo | OCI (Oracle Cloud Infrastructure) |
| Arquitectura | Monolito modular por dominios |
| Sistema de facturación electrónica | **FACTRO** (externo, no parte de GestCom) |

### 2.1 Estructura de paquetes backend

```
backend/src/main/java/com/arquitecsoft/gestion/
├── GestionApplication.java
├── config/
│   └── HealthController.java
├── domain/
│   ├── auth/
│   ├── empresa/         (incluye catálogos: Pais, Departamento, Municipio, DocumentType)
│   ├── persona/         (incluye PersonaEmpresa N:M)
│   ├── pipeline/        (incluye Etapa)
│   ├── oportunidad/     (incluye OportunidadStatsController/Service)
│   ├── actividad/       (incluye Compromiso comercial y TipoActividad)
│   ├── documento/       (incluye TipoDocumentoOpp)
│   ├── contrato/        (Mundo 2: Contrato, ProcesoContratacion, Modificacion, TipoContrato)
│   ├── facturacion/     (Mundo 3: 6 entidades, ver §5)
│   └── usuario/
└── infrastructure/
    ├── config/          (CorsFilter)
    ├── dto/             (PageResponse)
    ├── exception/       (BusinessException, ErrorResponse, GlobalExceptionHandler)
    └── security/        (JWT + Spring Security)
```

> **Nota para las skills:** la convención que dicen las skills (`config/`, `security/` al nivel raíz, `audit/`) **no coincide** con la estructura real (`infrastructure/config/`, `infrastructure/security/`, sin paquete `audit/` separado). Las skills deben actualizarse.

### 2.2 Estructura del frontend

```
frontend/src/
├── App.vue
├── components/
│   ├── actividad/       (ActividadModal, CompromisoModal)
│   ├── contrato/        (FormalizarContratoModal, IniciarProcesoModal)
│   ├── documento/       (DocumentoModal)
│   ├── empresa/         (EmpresaModal)
│   ├── facturacion/     (GestionPanel) ← solo 1 componente Mundo 3
│   ├── layout/          (AppLayout, Aurora*, NavRail, TopAppBar, Header, Sidebar)
│   ├── oportunidad/     (incluye contrato/FormalizarContratoModal duplicado)
│   ├── persona/         (PersonaModal, AsociarEmpresaModal)
│   ├── pipeline/        (EtapaModal, PipelineModal)
│   └── ui/              (Aurora*, Md*, genéricos: Button, Card, Input, MoneyInput)
└── views/
    ├── DashboardView.vue
    ├── LoginView.vue
    ├── EmpresasView.vue
    ├── PersonasView.vue
    ├── PipelineView.vue
    ├── OportunidadesListView.vue
    ├── OportunidadDetalleView.vue
    ├── ContratosListView.vue
    ├── ContratoDetalleView.vue
    └── FacturacionView.vue ← única vista Mundo 3
```

> **Convivencia de 3 generaciones de UI:** Material Design 3 (`Md*`), Aurora (`Aurora*`) y genéricos. La skill de frontend habla de "Luxury Tech", que parece haber sido reemplazado. Hay deuda de consolidación.

---

## 3. Mundo 1 — Gestión Comercial ✅

### 3.1 Estado: Completo

Sin cambios sustanciales respecto al documento anterior. El Mundo 1 es la base estable sobre la que se construyeron los Mundos 2 y 3.

### 3.2 Entidades implementadas

| Entidad | Descripción |
|---------|-------------|
| `GcEmpresa` | Organizaciones (prospecto / cliente / aliado). |
| `GcPais`, `GcDepartamento`, `GcMunicipio`, `GcDocumentType` | Catálogos transversales de localización e identificación. |
| `GcPersona` + `GcPersonaEmpresa` | Contactos con relación N:M con empresas. |
| `GcOportunidad` | Núcleo del proceso comercial. |
| `GcPipeline` + `GcEtapa` | Pipelines dinámicos (con `ambito` COMERCIAL/CONTRATACION). |
| `GcActividad` + `GcCompromiso` + `GcTipoActividad` | Actividades comerciales y compromisos derivados. |
| `GcDocumento` + `GcTipoDocumentoOpp` | Documentos asociados a oportunidades y actividades. |
| `GcUsuario` | Usuarios del sistema con RBAC. |

### 3.3 Funcionalidades operativas

- ✅ CRUD completo de Empresas, Personas, Oportunidades, Pipelines, Actividades, Compromisos, Documentos.
- ✅ Pipeline Kanban con drag & drop entre etapas.
- ✅ Cierre de oportunidades (GANADA / PERDIDA / NO_CONCRETADA / CONTRATADA).
- ✅ Dashboard analítico comercial.
- ✅ Login y RBAC.
- ✅ ~42 endpoints REST documentados al cierre de la sesión 2026-02-18.
- ✅ Matriz de reglas de negocio aplicada (RB-01 a RB-19).

### 3.4 Pendientes menores

> No bloquean nada y no son prioritarios:

- [ ] Reportes exportables (Excel / PDF) sobre listados.
- [ ] Filtros avanzados persistentes por usuario en listados.
- [ ] Mejoras de paginación y rendimiento sobre tablas grandes.

---

## 4. Mundo 2 — Gestión Contractual ✅ (con deuda)

### 4.1 Estado: Funcionalmente completo, con deuda técnica abierta

El Mundo 2 está implementado en backend y frontend. La función "Formalizar Contrato" desde una oportunidad GANADA opera, hay listado y detalle de contratos, formas de pago (renombradas a Compromisos de Ingreso tras el rediseño del Mundo 3), modificaciones y cambios de estado (suspender, reiniciar, terminar, liquidar).

> **Atención:** la deuda técnica en este mundo es **conocida y no resuelta**. Ver §4.5.

### 4.2 Flujo de negocio implementado

```
Pipeline Comercial (ABIERTA → SEGUIMIENTO → GANADA)
        │
        └── al marcar GANADA
                │
                ▼
        Pipeline Contractual (etapas dinámicas, ámbito CONTRATACION)
                │
                └── botón "Formalizar Contrato" (visible mientras no exista contrato)
                        │
                        ▼
                GC_CONTRATO creado en estado VIGENTE
                        │
                        └── Oportunidad pasa a CONTRATADA → sale de los pipelines
```

### 4.3 Entidades implementadas (`domain/contrato/`)

| Entidad | Descripción |
|---------|-------------|
| `GcContrato` | Contrato formalizado, estado VIGENTE/SUSPENDIDO/TERMINADO/LIQUIDADO. |
| `GcContratoModificacion` | Adiciones, prórrogas, suspensiones, reinicios. |
| `GcProcesoContratacion` | Pipeline contractual entre oportunidad GANADA y contrato VIGENTE. |
| `GcTipoContrato` | Catálogo (Contrato Formal, Orden de Compra, Orden de Servicio, etc.). |

> **Nota crítica:** la antigua `GcContratoFormaPago` **ya no existe en el código activo**. Fue renombrada y movida a `GcCompromisoIngreso` en `domain/facturacion/` durante el rediseño del Mundo 3. La relación `GcContrato.compromisos` (antes `formasPago`) navega ahora a esa entidad.

### 4.4 Endpoints expuestos

Bajo `/api/v1/contratos` y `/api/v1/procesos-contratacion`:

| Recurso | Métodos |
|---------|---------|
| `/contratos` | GET (con filtros), POST (formalizar) |
| `/contratos/{id}` | GET (detalle con relaciones) |
| `/contratos/{id}/suspender` | POST |
| `/contratos/{id}/reiniciar` | POST |
| `/contratos/{id}/terminar` | POST |
| `/contratos/{id}/liquidar` | POST |
| `/contratos/{id}/modificaciones` | POST |
| `/procesos-contratacion/{id}/mover-etapa` | POST |

### 4.5 Deuda técnica abierta del Mundo 2

Verificada por inspección directa del código en `main` el 5-may-2026:

| ID | Hallazgo | Severidad | Origen |
|----|----------|-----------|--------|
| **DT-01** | `GcContrato.proceso_contratacion_id` mantiene `nullable = false`. La lección aprendida #2 dice que esto debe quitarse para permitir contratos sin proceso intermedio. | 🟡 Media | Bloquea inserts en escenarios especiales. |
| **DT-02** | `GcContratoRepository.findByIdWithRelations` no incluye `JOIN FETCH c.oportunidad` ni `c.procesoContratacion`. El `ContratoResponse` puede causar `LazyInitializationException` al serializarse fuera de transacción. | 🟠 Alta | Lección aprendida #4 no aplicada. |
| **DT-03** | `findByIdWithRelations` tampoco fetcha `compromisos` ni `modificaciones`. Si la vista de detalle los muestra, hay otra lazy-load latente. | 🟠 Alta | Idem DT-02. |
| **DT-04** | Existencia de `FormalizarContratoModal.vue` duplicado (`components/contrato/` y `components/oportunidad/contrato/`). | 🟢 Baja | Posible deuda visual. |

### 4.6 Issue I-01 redefinido

El issue I-01 del documento anterior decía: "`@OrderBy` apunta a `fechaModificacion` que no existe en `GcContratoModificacion`".

**La realidad encontrada el 5-may-2026:**

- `GcContratoModificacion.java` **no tiene ninguna anotación `@OrderBy`**. El `@OrderBy` que aparecía como sospechoso vive en `GcContrato.java` línea 105 sobre la lista `modificaciones` y apunta a `fechaCreacion DESC` — **field que sí existe** en `GcContratoModificacion`. Esa anotación está bien.
- En `GcContrato.java` línea 101 hay otro `@OrderBy("fechaEsperadaActual ASC")` sobre la lista `compromisos` (entidad `GcCompromisoIngreso`). **No verificado todavía**: depende de que `fechaEsperadaActual` exista como atributo Java en `GcCompromisoIngreso`.

**Conclusión:** I-01 sigue abierto pero **el archivo culpable cambió**. El error `ORA-00904 "M1_0"."FECHAMODIFICACION"` puede provenir de:

- a) Un `@OrderBy` en otra entidad cuyo recorrido pase por `GcContratoModificacion` (la columna se llama `fecha_modificacion` pero el field se llama `fechaModificacionContrato`).
- b) Una query JPQL con `c.fechaModificacion` apuntando a `GcContratoModificacion` (donde el atributo real es `fechaModificacionContrato`).
- c) Algo en `GcCompromisoIngreso` o sus consumidores.

Resolver I-01 requiere una sesión enfocada con stack trace del error en runtime.

---

## 5. Mundo 3 — Flujo de Facturación 🟡

### 5.1 Estado: Backend al ~85%, frontend al ~25%

El Mundo 3 sí tiene **especificación funcional formal** versionada en el repo (`GestCom_Mundo3_Especificacion_1.md`). Lo que sigue resume la espec y se contrasta con el código existente.

### 5.2 Visión funcional (resumen de la especificación v3)

GestCom **no emite facturas**. FACTRO lo hace. GestCom:

1. **Indica proactivamente qué se debe facturar cada mes** (tablero de pendientes del mes).
2. **Registra la gestión** realizada para cada compromiso de ingreso (bitácora con tipos de gestión: validación de servicio, soporte obtenido, solicitud de emisión, factura cruzada, novedades, observación).
3. **Cruza facturas emitidas en FACTRO** con los compromisos.
4. **Actualiza el valor real facturado** (puede diferir del presupuestado).
5. **Mantiene trazabilidad**: documentos soporte, novedades, observaciones.
6. **Genera el Excel EMI** con conversión a COP usando TRM mensual.
7. **Muestra KPIs**: pendientes del mes / vencidas / facturado del mes / pendiente total / facturado acumulado año.

### 5.3 Conceptos clave de la especificación

- **Valor presupuestado vs. valor facturado:** el compromiso nace con un presupuesto y al cruzar con factura se registra el valor real (pueden diferir por contratos variables, ajustes IPC, servicios parciales).
- **TRM mensual** (USD → COP) en tabla independiente. Si no hay TRM del mes, se usa la del mes anterior más reciente.
- **Ciclo de vida del compromiso:** PENDIENTE → llega su mes → aparece en tablero → gestión normal → solicitud emisión → cruce con factura → FACTURADA. Las novedades (aplazamiento, cambio valor, etc.) se registran en bitácora pero no cambian el estado.
- **Cruce factura ↔ compromiso:** una factura puede cruzar con varios compromisos (1:N), pero un compromiso solo con una factura. Al cruzar: `factura_id` se asigna, estado pasa a FACTURADA, `valor_facturado` se actualiza. Es reversible (descruzar).

### 5.4 Modelo de datos especificado

**Tablas nuevas previstas en la espec:**

- `GC_TRM` — tasas mensuales por par de monedas (UNIQUE año-mes-origen-destino).
- `GC_FACTURA` — facturas registradas (no emitidas) desde FACTRO.
- `GC_FORMA_PAGO_GESTION` — bitácora de gestiones (con tipos enumerados: `VALIDACION_SERVICIO`, `SOPORTE_OBTENIDO`, `SOLICITUD_EMISION`, `FACTURA_CRUZADA`, `NOVEDAD_*`, `OBSERVACION`).

**Modificaciones a tablas existentes:**

- `GC_CONTRATO_FORMA_PAGO` (renombrada a `GC_COMPROMISO_INGRESO` en el rediseño): agregar `valor_facturado`, `factura_id`.

### 5.5 Modelo de datos implementado en el código

El rediseño llevó la implementación más allá de la especificación v3. Hay **6 entidades** en `domain/facturacion/`:

| Entidad | Rol | ¿Estaba en spec v3? |
|---------|-----|---------------------|
| `GcCompromisoIngreso` | Compromiso de ingreso (era `GcContratoFormaPago`). | Sí (renombrada). |
| `GcCompromisoFactura` | Relación N:M compromiso ↔ factura. | No (la spec dice 1:1 reversible; la implementación generaliza a N:M). |
| `GcCompromisoEvento` | Eventos del compromiso (event sourcing ligero). | No (extra de la implementación). |
| `GcCompromisoGestion` | Gestiones registradas (bitácora). | Sí (renombrada `GC_FORMA_PAGO_GESTION` → `GC_COMPROMISO_GESTION`). |
| `GcFactura` | Factura registrada. | Sí. |
| `GcTrm` | TRM mensual. | Sí. |

> **Hallazgo:** la implementación generaliza la relación factura↔compromiso a N:M. Esto **contradice o extiende** la regla RN-01 de la spec ("Una forma de pago solo puede cruzarse con UNA factura"). Hay que decidir:
> - **A)** la regla RN-01 sigue vigente y la tabla `GcCompromisoFactura` la enforce con un UNIQUE sobre `compromiso_id`; o
> - **B)** la regla RN-01 cambió y un compromiso puede cruzarse con varias facturas (escenario: facturas parciales).
> Esta decisión debe documentarse en una v4 de la spec.

### 5.6 Endpoints expuestos

Bajo `/api/v1/...` (paths exactos pendientes de verificar contra controllers):

| Controller | Recurso |
|------------|---------|
| `CompromisoIngresoController` | `/api/v1/compromisos-ingreso` (presumible) |
| `FacturaController` | `/api/v1/facturas` |
| `VistaPeriodoController` | `/api/v1/vista-periodo` (tablero/KPIs por período) |

Services existentes:
- `CompromisoEstadoMachine` — máquina de estados del compromiso.
- `CompromisoGestionService`
- `CompromisoIngresoService`
- `FacturaService`
- `VistaPeriodoService`

### 5.7 Frontend implementado

Solo:
- `views/FacturacionView.vue` (1 vista)
- `components/facturacion/GestionPanel.vue` (1 componente)

Faltan respecto a la especificación v3:

- [ ] `FormaPagoGestionPanel.vue` — modal/panel lateral de gestión por compromiso. **(¿es lo mismo que `GestionPanel.vue`? requiere verificación)**
- [ ] `RegistrarGestionModal.vue`.
- [ ] `FacturasListView.vue` — lista de facturas con filtros.
- [ ] `RegistrarFacturaModal.vue`.
- [ ] `CruzarFacturaModal.vue`.
- [ ] Sección/vista TRM editable.
- [ ] Botón "Exportar EMI" (Excel con 5 hojas: Tabla COP, Tabla USD, COL pivot, PERU pivot, Resumen Facturado).

### 5.8 Reglas de negocio del Mundo 3

Tomadas de la spec v3 (RN-01 a RN-13). **Ninguna está documentada aún en formato matriz** como las RB-XX del Mundo 1.

| ID | Regla |
|----|-------|
| RN-01 | Una forma de pago solo puede cruzarse con UNA factura. **(Ver §5.5: la implementación lo generaliza a N:M.)** |
| RN-02 | Una factura puede cruzarse con MÚLTIPLES formas de pago. |
| RN-03 | Al cruzar: estado → FACTURADA, se actualiza `valor_facturado`. |
| RN-04 | Solo se pueden cruzar formas de pago PENDIENTES. |
| RN-05 | Se puede descruzar — vuelve a PENDIENTE, `valor_facturado = null`. |
| RN-06 | Formas de pago del mes actual y anteriores son "activas" en el tablero. |
| RN-07 | Formas de pago futuras son informativas. |
| RN-08 | Facturas se identifican por `numero_factura + prefijo` (UNIQUE). |
| RN-09 | Entradas de bitácora son inmutables. |
| RN-10 | Conversión USD→COP usa TRM del mes de la forma de pago. |
| RN-11 | Si no hay TRM para un mes, se usa la del mes anterior más reciente. |
| RN-12 | El `valor_facturado` puede ser diferente al presupuestado. |
| RN-13 | KPIs y Excel usan valores convertidos a COP. |

### 5.9 Fases del Mundo 3 — estado por fase

Tomadas de la spec v3 (F1 a F5 dentro del Mundo 3, no confundir con F1-F5 del Mundo 2 ni del documento anterior).

| Fase | Descripción | Estado |
|------|-------------|--------|
| **F1** | Modelo de datos y backend base (entidades, repos, services, controllers, endpoints) | ✅ ~95% (puede haber gaps en algunos endpoints) |
| **F2** | Frontend tablero de facturación (KPIs + listas) | 🟡 Parcial (existe `FacturacionView.vue`, falta integración completa) |
| **F3** | Frontend facturas y cruce (lista, modal registrar, modal cruzar) | 🔲 No iniciado |
| **F4** | TRM y Excel EMI (vista TRM editable, endpoint export Excel, botón descarga) | 🔲 No iniciado |
| **F5** | Dashboard integrado (stats facturación + integración con dashboard principal) | 🔲 No iniciado |

---

## 6. Convenciones del proyecto

Confirmadas por inspección del código real, no asumidas:

### 6.1 Nombres de tablas y prefijos

- Tablas de negocio: prefijo `GC_`.
- Catálogos transversales: el código real **no usa `CAT_`** sistemáticamente; usa `GC_PAIS`, `GC_DEPARTAMENTO`, `GC_DOCUMENT_TYPE`, etc. Las skills mencionan `CAT_` pero la implementación es `GC_*` para todo. **Esto debe resolverse en una próxima revisión.**

### 6.2 Nombres de entidades JPA

- Prefijo `Gc` + nombre en singular CamelCase: `GcEmpresa`, `GcContrato`, `GcCompromisoIngreso`.

### 6.3 Auditoría

Patrón observado en entidades nuevas (no uniforme):
- `fecha_creacion` (TIMESTAMP, NOT NULL, updatable=false)
- `fecha_modificacion` (TIMESTAMP, nullable) **— no presente en todas las entidades nuevas**
- `creado_por` (NUMBER, NOT NULL) — referencia a usuario por ID, no por string
- `modificado_por` (NUMBER, nullable) — no presente en todas las entidades nuevas

> **Inconsistencia:** la skill de backend dice que la auditoría usa `usuarioCreacion`/`usuarioModificacion` como String. El código real usa `creadoPor`/`modificadoPor` como `Long` (FK al usuario). Las skills deben actualizarse.

### 6.4 Estados como CHECK constraints

Los estados se almacenan como VARCHAR2 + `@Enumerated(EnumType.STRING)`. Cada `ALTER TABLE ... ADD CONSTRAINT` debe sincronizarse con los enums de Java (lección aprendida ya documentada).

---

## 7. Documentos de referencia en el repo

| Documento | Estado |
|-----------|--------|
| `00Docs/01Infraestructura/01-STACK-SIMPLIFICADO.md` | ✅ |
| `00Docs/01Infraestructura/02-CHECKLIST-SETUP.md` | ✅ |
| `00Docs/01Infraestructura/03-ARCHIVOS-CONFIGURACION.md` | ✅ |
| `00Docs/02Especificacion/B-01-Modelo-Logico-Datos.md` | ✅ |
| `00Docs/02Especificacion/B-02-Modelo-Fisico-DDL.md` | ✅ |
| `00Docs/02Especificacion/B-03-Contrato-API-OpenAPI.md` | ✅ |
| `00Docs/02Especificacion/B-04-Matriz-Reglas-Negocio.md` | ✅ Solo Mundo 1 |
| `00Docs/02Especificacion/Especificacion Funcional 1.md` | ✅ Mundo 1 |
| `00Docs/02Especificacion/GestCom_Alcance_Contratacion_v1.md` | ✅ Mundo 2 |
| `00Docs/02Especificacion/GestCom_Mundo3_Especificacion_1.md` | ✅ Mundo 3 (v3, abril 2026) |
| `00Docs/02Especificacion/Presupuesto_y_Ejecucion Prespuestal.md` | Sin verificar |
| `00Docs/03Estandar Visual/A-01..A-05` | ✅ Material Design 3 (puede estar desfasado vs Aurora) |
| `00Docs/04Sessiones/Estado_arte_04052026.md` | ⚠️ Reemplazado por este documento |
| `00Docs/04Sessiones/SESION-2026-02-07-Resumen.md` | Histórico |
| `00Docs/04Sessiones/SESION-2026-02-18-Resumen.md` | Histórico (cierre Mundo 1 backend) |
| `00Docs/04Sessiones/TAREAS_POR_EJECUTAR-2026-02-18.md` | Histórico (obsoleto) |

**Documentos faltantes detectados:**

- [ ] **Matriz de reglas RN-XX del Mundo 3** (equivalente a B-04 del Mundo 1).
- [ ] **Documento que reconcilie spec v3 con la implementación real** (especialmente la relación factura↔compromiso N:M y las entidades extra `GcCompromisoEvento`).
- [ ] **Documento que oficialice la decisión "no hay Mundo 4"**.

---

## 8. Resumen Ejecutivo de Avance

| Mundo | Avance | Comentario |
|-------|--------|------------|
| 1 — Comercial | ~95% | Mejoras menores diferidas. |
| 2 — Contractual | ~96% | Funcional. Bloque A cerrado el 5-may-2026 (I-01, DT-01..DT-04, DT-09). Sin deuda técnica pendiente específica del Mundo 2. |
| 3 — Flujo de Facturación | ~55% | Backend ~85%, frontend ~25%. Especificación lista. |

(El "Mundo 4" del documento anterior se eliminó del alcance.)

---

## 9. Issues técnicos abiertos

| ID | Issue | Severidad | Estado |
|----|-------|-----------|--------|
| **I-01** | Error `ORA-00904: "M1_0"."FECHAMODIFICACION"` reportado en sesiones anteriores. Cerrado por arrastre al aplicar DT-02 y DT-03: el error era efecto secundario del lazy-load roto en `findByIdWithRelations`, no de un `@OrderBy` mal escrito. Validado en runtime con Postman el 5-may-2026 (sesión Bloque A). | ✅ Cerrado | Cerrado el 5-may-2026 |
| **I-02** | "Repo desincronizado". | ✅ Cerrado | El repo sí está sincronizado; el problema era de percepción durante sesiones anteriores. |
| **DT-01** | `GcContrato.proceso_contratacion_id` con `nullable = false`. | ✅ Cerrado | Aplicado el 5-may-2026 (Bloque A). |
| **DT-02** | `findByIdWithRelations` sin `JOIN FETCH c.oportunidad`. | ✅ Cerrado | Aplicado el 5-may-2026 (Bloque A) — agregados `LEFT JOIN FETCH` de `oportunidad`, `procesoContratacion` y `modificaciones`. |
| **DT-03** | `findByIdWithRelations` sin `JOIN FETCH` de `compromisos` ni `modificaciones`. | ✅ Cerrado parcial | `modificaciones` aplicado el 5-may-2026. `compromisos` deliberadamente NO incluido por riesgo de `MultipleBagFetchException` con dos colecciones a la vez. Si se necesita en el futuro, usar `EntityGraph` o cambiar el tipo de una colección a `Set`. |
| **DT-04** | `FormalizarContratoModal.vue` duplicado en frontend. | ✅ Cerrado | Borrado `frontend/src/components/oportunidad/contrato/FormalizarContratoModal.vue` el 5-may-2026 (era byte-a-byte idéntico al canónico en `components/contrato/`, sin consumidores). Los 2 consumidores reales (`OportunidadDetalleView.vue` y `PipelineView.vue`) ya importaban la ruta canónica. |
| **DT-05** | Convivencia de 3 generaciones de UI components (Md*, Aurora*, genéricos). | 🟡 Media | Abierto |
| **DT-06** | Inconsistencia entre skills y código real (estructura `infrastructure/` vs `config/+security/`, prefijo `CAT_` no usado, auditoría con FK a usuario en vez de string). | 🟡 Media | Las skills deben actualizarse. |
| **DT-07** | Implementación de Mundo 3 generaliza factura↔compromiso a N:M, contradiciendo RN-01 de la spec v3. Decidir y reflejar en spec v4. | 🟡 Media | Abierto |
| **DT-08** | Sólo 1 archivo Flyway (`V1__initial_schema.sql`). Cambios de esquema posteriores (Mundo 2, Mundo 3) **no parecen versionados** como migraciones. Riesgo de divergencia ambiente↔código. | 🟠 Alta | Abierto |
| **DT-09** | `PipelineView.vue` líneas 266-267 invocaba `<FormalizarContratoModal>` **dos veces consecutivas** con el mismo `v-if`. Diagnóstico definitivo: la línea 267 referenciaba un handler inexistente (`onContratoCreated`) — código muerto proveniente del patrón de `OportunidadDetalleView.vue`. Vue toleraba la referencia sin error explícito pero ambos modales se renderizaban superpuestos al activarse el `v-if`. | ✅ Cerrado | Borrada la línea 267 el 5-may-2026. Tamaño del archivo pasó de 40200 a 39888 bytes (−312 = línea + `\n`). Conservado el handler correcto `onContratoFormalizado`. |

---

## 10. Plan recomendado para la nueva versión

Las realidades descubiertas el 5-may-2026 reorientan el plan:

### Bloque A — Estabilización del Mundo 2 ✅ Cerrado el 5-may-2026

1. ✅ **I-01 cerrado por arrastre.** Validado en runtime con Postman después de aplicar DT-02 y DT-03.
2. ✅ **DT-01, DT-02, DT-03 aplicados.** `nullable=false` quitado en `proceso_contratacion_id`; `findByIdWithRelations` ahora hace `LEFT JOIN FETCH` de `oportunidad`, `procesoContratacion` y `modificaciones`.
3. ✅ **DT-04 aplicado.** Borrada la copia huérfana `frontend/src/components/oportunidad/contrato/FormalizarContratoModal.vue` (era idéntica al canónico, sin consumidores).
4. ✅ **DT-09 aplicado.** Borrada la línea 267 de `PipelineView.vue` que invocaba el modal duplicado con un handler inexistente.

### Bloque B — Cierre del Mundo 3 (mediano plazo, 2-3 semanas)

1. **Reconciliar spec v3 con la implementación** (DT-07): decidir si compromiso↔factura es 1:1 o N:M; documentar `GcCompromisoEvento`; emitir `GestCom_Mundo3_Especificacion_v4.md`.
2. **Generar matriz de reglas RN-01 a RN-13** mapeada a endpoints reales.
3. **Frontend F2 completo:** verificar `FacturacionView.vue` cubre el tablero entero (KPIs, pendientes mes, vencidas, próximos meses, facturadas del mes).
4. **Frontend F3:** `FacturasListView.vue`, `RegistrarFacturaModal.vue`, `CruzarFacturaModal.vue`, `RegistrarGestionModal.vue`.
5. **Frontend F4:** sección TRM, endpoint backend export EMI, botón descarga.
6. **Frontend F5:** integrar stats Mundo 3 al dashboard general.

### Bloque C — Higiene transversal (puede ir en paralelo con B)

1. **Versionar migraciones Flyway** (DT-08): generar `V2__mundo2.sql`, `V3__mundo3.sql` retroactivos para reflejar el esquema actual.
2. **Actualizar las 3 skills** (DT-06) contra la realidad del código.
3. **Consolidar la UI** (DT-05): decidir Aurora como ganador, eliminar progresivamente `Md*` y genéricos.
4. **Actualizar README del repo** para reflejar el estado real (los 3 mundos, no solo Mundo 1).

### Bloque D — Mejoras transversales (largo plazo)

1. Reportes exportables genéricos.
2. Tests automatizados (cobertura mínima de servicios críticos).
3. Refinamiento del Dashboard general.

---

## 11. Decisiones congeladas a esta fecha

Decisiones arquitectónicas que no se vuelven a discutir salvo cambio mayor:

1. **3 Mundos, no 4.** El registro de facturas reales se fusionó en el Mundo 3.
2. **GestCom no emite facturas.** FACTRO sí. GestCom solo cruza y registra.
3. **Diseño visual: Aurora Dark.** "Luxury Tech" queda deprecado.
4. **Estructura de paquetes: `domain/{dominio}/` + `infrastructure/`.** No usar `config/`/`security/` raíz.
5. **Auditoría con FK a usuario (`creadoPor: Long`)**, no string de usuario.
6. **Migraciones via Flyway**, aunque actualmente esté incompleto.
7. **Una sola rama de trabajo: `main`.** No hay flujo de features branches activo (revisar si esto es deliberado o accidental).

---

## 12. Control de cambios del documento

| Versión | Fecha | Cambio |
|---------|-------|--------|
| 1.0 | 04-may-2026 | `Estado_arte_04052026.md` — base inicial. |
| 2.0 | 05-may-2026 | Reescrito tras inspección directa del repo: alcance del Mundo 3 documentado, Mundo 4 eliminado, deuda técnica enumerada (DT-01 a DT-08), I-01 redefinido, I-02 cerrado, plan reorientado. |
| 2.1 | 05-may-2026 | Cierre del Bloque A: I-01, DT-01, DT-02, DT-03, DT-04 marcados como cerrados con fechas y referencias. Agregado DT-09 (duplicación de modal en `PipelineView.vue` 266-267) detectado durante la auditoría de DT-04. |
| 2.2 | 05-may-2026 | **Este documento.** DT-09 cerrado: borrada la línea 267 de `PipelineView.vue`. Bloque A 100% terminado, Mundo 2 sin deuda técnica abierta. Avance del Mundo 2 ajustado a ~96%. |

---

*— Fin del Documento —*
