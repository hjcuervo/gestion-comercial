---
name: gestcom-context
description: Provides foundational context for the GestCom platform — Arquitecsoft's commercial and contract management system built with Spring Boot 3.4 + Java 21 + Oracle 23c + Vue 3. Use this skill at the start of ANY task related to GestCom, the gestion-comercial repository, the 4 functional "Mundos" (Comercial, Contractual, Flujo de Facturación, Facturación), or when the user mentions GestCom-specific concepts like oportunidad, pipeline contractual, GcContrato, formas de pago, or Hector's Arquitecsoft project. This is the ROOT skill — load it before gestcom-backend or gestcom-frontend so all domain decisions are made with full project context.
---

# GestCom — Contexto del Proyecto

Esta skill establece el contexto base de la plataforma GestCom. **Cárgala primero** en cualquier tarea relacionada con el proyecto antes de pasar a las skills de backend o frontend.

---

## 1. Identidad del proyecto

- **Producto:** GestCom — Plataforma de Gestión Comercial y Contractual.
- **Empresa:** Arquitecsoft SAS.
- **Desarrollador principal:** Hector.
- **Repositorio:** `https://github.com/hjcuervo/gestion-comercial`.
- **Idioma del dominio:** Español. Los nombres de entidades, campos, mensajes de UI y comentarios están en español. El código (clases Java, métodos, variables) sigue convenciones técnicas estándar pero los nombres del dominio se mantienen en español (ej. `GcOportunidad`, `etapa`, `formaPago`).

---

## 2. Stack tecnológico (no negociable)

| Capa | Tecnología | Versión |
|------|------------|---------|
| Backend | Spring Boot | 3.4 |
| Lenguaje backend | Java | 21 |
| Base de datos | Oracle | 23c |
| ORM | Hibernate / JPA | (incluido en Spring Boot 3.4) |
| Frontend | Vue | 3 (Composition API) |
| Build frontend | Vite | — |
| Estado frontend | Pinia | — |
| Autenticación | JWT | — |
| Infraestructura objetivo | OCI (Oracle Cloud Infrastructure) | — |

**Cuando trabajes en GestCom:** nunca propongas cambiar de stack, ni introducir librerías que no encajen con este (ej. nada de MyBatis, Quasar, Vuex, MySQL, etc.).

---

## 3. Arquitectura

- **Monolito modular por dominios.** El backend organiza el código en paquetes por dominio funcional, no por capa técnica.
- **API-only en el backend.** El backend nunca renderiza HTML; expone REST y el frontend SPA consume.
- **Pipelines dinámicos parametrizables.** El motor de pipelines es genérico y se diferencia por `ambito` (`COMERCIAL` o `CONTRATACION`).
- **Auditoría obligatoria** en transiciones y cambios relevantes.
- **RBAC** con tres roles base: Admin / Comercial / Lectura-KPI.

---

## 4. Los 4 Mundos

GestCom se organiza en 4 mundos funcionales integrados:

| # | Mundo | Alcance | Estado actual |
|---|-------|---------|----------------|
| 1 | **Gestión Comercial** | Identificación de oportunidad → adjudicación. Pipeline comercial, actividades, compromisos, documentos. | ✅ Implementado (~95%) |
| 2 | **Gestión Contractual** | Oportunidad GANADA → contrato VIGENTE. Pipeline contractual, contratos, formas de pago, modificaciones. | 🟡 En ejecución (~70%, falta F6 Dashboard) |
| 3 | **Flujo de Facturación** | Proyección y emisión de facturas desde formas de pago. | 🔲 Sin especificar |
| 4 | **Facturación** | Registro y análisis de facturas reales emitidas. No las emite, las recibe. | 🔲 Sin especificar |

---

## 5. Flujo de negocio crítico

```
Pipeline Comercial (ABIERTA → SEGUIMIENTO → GANADA)
        │
        └── al marcar GANADA
                ▼
        Pipeline Contractual (etapas dinámicas)
                │
                └── botón "Formalizar Contrato" (visible mientras no exista contrato)
                        ▼
                GC_CONTRATO en estado VIGENTE
                        │
                        └── Oportunidad pasa a CONTRATADA → sale de los pipelines
```

**Reglas críticas que nunca se rompen:**

1. El contrato **no existe** durante el pipeline contractual. Solo existe `GcProcesoContratacion`.
2. El contrato nace directamente en estado **VIGENTE** (no hay EN_PROCESO).
3. En el pipeline contractual se usan **etapas**, no estados.
4. En el pipeline comercial: GANADA dispara formalización; PERDIDA / NO_CONCRETADA solo cierran.
5. Una oportunidad en estado CONTRATADA o cerrada (PERDIDA / NO_CONCRETADA) es **inmutable**.
6. Estados válidos del contrato: VIGENTE → SUSPENDIDO ↔ VIGENTE → TERMINADO → LIQUIDADO.

---

## 6. Glosario de dominio

| Término | Significado |
|---------|-------------|
| **Oportunidad** | Núcleo del proceso comercial. Pertenece a 1 empresa, 1 pipeline, 1 etapa. |
| **Pipeline** | Flujo configurable de etapas. Tiene `ambito` (COMERCIAL / CONTRATACION). |
| **Etapa** | Fase dentro de un pipeline. Las etapas son ordenadas. |
| **Empresa** | Organización. Puede ser prospecto, cliente o aliado comercial. |
| **Persona** | Contacto individual con relación multi-empresa. |
| **Actividad** | Interacción registrada (reunión, llamada, envío de propuesta). |
| **Compromiso** | Tarea derivada de una actividad. |
| **Proceso de Contratación** | Etapa intermedia entre oportunidad ganada y contrato. Es un pipeline. |
| **Contrato** | Documento formal que regula la relación. Tipos: Contrato Formal, Orden de Compra, Orden de Servicio, etc. |
| **Forma de Pago** | Cuota o hito de facturación dentro de un contrato. |
| **Modificación** | Adición, prórroga o suspensión sobre un contrato. |
| **Empresa de facturación** | Filial específica que factura, distinta a la empresa cliente principal. |

---

## 7. Convenciones globales

### 7.1 Nombres de tablas y prefijos

- Tablas de negocio: prefijo `GC_` (ej. `GC_OPORTUNIDAD`, `GC_CONTRATO`).
- Catálogos transversales: prefijo `CAT_` (ej. `CAT_TIPO_ACTIVIDAD`).
- Catálogos específicos del módulo: pueden ir con `GC_TIPO_*` (ej. `GC_TIPO_CONTRATO`).

### 7.2 Nombres de entidades JPA

- Prefijo `Gc` + nombre en singular CamelCase: `GcOportunidad`, `GcContrato`, `GcContratoFormaPago`.
- Catálogos transversales: prefijo `Cat`: `CatTipoActividad`.

### 7.3 Nombres de campos de auditoría

Todas las entidades de negocio incluyen:
- `fechaCreacion` (Timestamp, NOT NULL)
- `fechaModificacion` (Timestamp, nullable)
- `usuarioCreacion` (String, NOT NULL)
- `usuarioModificacion` (String, nullable)

### 7.4 Nombres de enums

Los enums se almacenan como VARCHAR2 con CHECK constraint en Oracle. Ejemplos:
- Estado de oportunidad: `ABIERTA`, `SEGUIMIENTO`, `GANADA`, `PERDIDA`, `NO_CONCRETADA`, `CONTRATADA`.
- Estado de contrato: `VIGENTE`, `SUSPENDIDO`, `TERMINADO`, `LIQUIDADO`.
- Ámbito de pipeline: `COMERCIAL`, `CONTRATACION`.

---

## 8. Estructura del repositorio

```
gestion-comercial/
├── backend/
│   └── src/main/java/com/arquitecsoft/gestion/
│       ├── domain/
│       │   ├── empresa/         (entity, repository, service, controller, dto)
│       │   ├── persona/
│       │   ├── oportunidad/
│       │   ├── pipeline/
│       │   ├── actividad/
│       │   ├── documento/
│       │   ├── contrato/        (incluye proceso de contratación)
│       │   └── catalogo/
│       ├── security/            (JWT, RBAC)
│       ├── audit/               (entidad y service de auditoría)
│       └── config/              (config global, exception handler)
├── frontend/
│   └── src/
│       ├── views/               (vistas por dominio)
│       ├── components/          (componentes reutilizables)
│       ├── services/            (clientes HTTP)
│       ├── stores/              (Pinia)
│       ├── router/
│       └── layouts/             (AppLayout con NavRail)
├── docs/                        (especificaciones, alcance, backlog)
└── .claude/
    └── skills/                  (estas skills)
```

---

## 9. Estado actual al congelar (Mayo 2026)

### 9.1 Lo que está funcionando

- **Mundo 1 completo:** CRUD de Empresas / Personas / Oportunidades, Pipeline Kanban, actividades, compromisos, documentos, dashboard comercial, alertas, login, RBAC, 42 endpoints REST.
- **Mundo 2 (F1–F5):**
  - Modelo de datos contractual (5 tablas nuevas + ajuste a `GC_PIPELINE`).
  - Backend del proceso de contratación.
  - Backend de contratos (CRUD, formas de pago, modificaciones, cambios de estado).
  - `ContratosListView.vue` y `ContratoDetalleView.vue`.
  - Rutas `/contratos` y `/contratos/:id`, ítem en NavRail.

### 9.2 Lo pendiente del Mundo 2

- **F6 Dashboard contractual:** T6.1 (endpoint stats), T6.2 (vista dashboard), T6.3 (integración con dashboard general).

### 9.3 Issues técnicos abiertos

- **I-01:** Error `ORA-00904: "M1_0"."FECHAMODIFICACION"` por caché stale de Hibernate. Causa raíz: `@OrderBy` apuntaba a un campo que no existe (el campo correcto en `GcContratoModificacion` es `fechaCreacion`, no `fechaModificacion`). Acción: `mvn clean package -DskipTests` y verificar entidad.
- **I-02:** El repositorio en GitHub puede estar desincronizado respecto a las correcciones más recientes. Verificar antes de modificar archivos del Mundo 2.

### 9.4 Mundos 3 y 4

Sin especificación funcional formal. Cualquier trabajo aquí debe arrancar por una sesión de definición de alcance, no por código.

---

## 10. Cómo trabajamos juntos (preferencias del desarrollador)

Estas preferencias son parte del contrato de trabajo con Hector:

1. **Cambios mínimos.** Cuando se pide una corrección, aplicarla aislada, sin reescribir archivos completos.
2. **Archivos descargables, no inline.** Cuando se generen archivos, presentarlos con `present_files`, no pegarlos como bloques de código en el chat (salvo que sean fragmentos cortos).
3. **Verificar el repo antes de modificar.** Si una sesión reciente cambió archivos, confirmar el estado actual del repo antes de proponer cambios sobre archivos del Mundo 2.
4. **Especificar antes de codificar.** Para nuevos mundos o features grandes, primero generar documento de alcance + backlog de tareas; nunca saltar a código directamente.
5. **Errores con causa raíz.** El `GlobalExceptionHandler` está configurado para mostrar la causa raíz en desarrollo. Mantener ese comportamiento; no envolver excepciones en mensajes genéricos durante debugging.
6. **Backlog formato Fx.y.** Las tareas se numeran como `T1.1`, `T1.2`, etc. dentro de fases `F1`, `F2`...
7. **Reglas de negocio formato RB-XX.** Las reglas se identifican como `RB-01`, `RB-02`, etc. y se documentan en una matriz que las cruza con los endpoints donde aplican.

---

## 11. Documentos de referencia en el repo

| Documento | Propósito |
|-----------|-----------|
| `docs/GestCom_Estado_del_Arte.md` | Línea base de avance para nueva versión. |
| `docs/GestCom_Alcance_Contratacion_v1.md` | Especificación del Mundo 2. |
| `docs/GestCom_Tareas_Mundo2.md` | Backlog detallado de las 26 tareas del Mundo 2. |
| `docs/B-04-Matriz-Reglas-Negocio.md` | Matriz RB-XXX → endpoints (Mundo 1). |

---

## 12. Lecciones aprendidas (deuda anti-patrones)

Estas son lecciones costosas de sesiones pasadas. **Tenlas presentes siempre:**

1. **`@OrderBy` debe apuntar a un campo existente en la entidad** (no en la tabla). Hibernate no valida esto en tiempo de compilación; un error aquí provoca `ORA-00904` con campos en alias `M1_0` que despistan.
2. **`nullable = false` en FKs bloquea inserts cuando la FK no siempre se popula.** Caso real: `proceso_contratacion_id` en `GcContrato` se quitó `nullable = false` porque el contrato puede crearse sin proceso intermedio en escenarios especiales.
3. **Constraints CHECK de Oracle deben sincronizarse con los enums de Java.** Cuando se agrega un valor al enum (ej. `CONTRATADA`), hay que `ALTER` el constraint o el insert falla.
4. **Lazy-load fuera de transacción explota.** Cualquier consulta que se serializa después de cerrar la transacción debe usar `JOIN FETCH` explícito en JPQL. Caso real: `GcContratoRepository.findByIdWithRelations` faltaba `JOIN FETCH c.oportunidad`.
5. **Caché de Hibernate puede enmascarar correcciones.** Después de cambios en anotaciones JPA críticas (`@OrderBy`, `@JoinColumn`), correr `mvn clean package -DskipTests` antes de probar.

---

*Cuando esta skill esté activa, todas las decisiones de implementación deben respetar lo aquí descrito. Si algo aquí entra en conflicto con una instrucción puntual del usuario, mencionar el conflicto antes de proceder.*
