# GestCom — Estado del Arte de la Plataforma

**Empresa:** Arquitecsoft SAS
**Producto:** GestCom — Plataforma de Gestión Comercial y Contractual
**Repositorio:** https://github.com/hjcuervo/gestion-comercial
**Stack:** Spring Boot 3.4 + Java 21 + Oracle 23c + Vue 3 + Vite + Pinia
**Fecha del corte:** Mayo 2026
**Propósito del documento:** Servir como línea base de contexto para una nueva versión del proyecto, dejando claro qué está terminado, qué está en curso y qué falta por especificar/implementar.

---

## 1. Visión Global de la Plataforma

GestCom gestiona el ciclo de vida completo de la relación comercial de Arquitecsoft con sus clientes, desde la identificación de una oportunidad hasta la facturación del servicio contratado.

La plataforma se organiza en **4 mundos funcionales** integrados:

| # | Mundo | Alcance | Estado General |
|---|-------|---------|----------------|
| 1 | **Gestión Comercial** | Desde la identificación de la oportunidad hasta la adjudicación del negocio. Pipeline comercial, actividades, compromisos, documentos. | ✅ Implementado |
| 2 | **Gestión Contractual** | Desde la oportunidad ganada hasta el contrato formalizado y en ejecución. Pipeline de contratación, contratos, formas de pago, modificaciones. | 🟡 En ejecución (≈70%) |
| 3 | **Flujo de Facturación** | Proyección futura de la facturación pendiente según contratos vigentes y sus formas de pago. | 🔲 Sin especificar |
| 4 | **Facturación** | Registro y análisis de facturas emitidas. No emite facturas, las recibe del sistema de facturación electrónica. | 🔲 Sin especificar |

---

## 2. Arquitectura y Stack

| Capa | Tecnología |
|------|------------|
| Backend | Spring Boot 3.4 + Java 21 |
| Persistencia | Oracle 23c (JPA / Hibernate) |
| Frontend | Vue 3 + Vite + Pinia |
| Sistema de diseño | "Luxury Tech" (cyan #00D4FF + violeta #A855F7) |
| Autenticación | JWT, RBAC (Admin / Comercial / Lectura-KPI) |
| Infraestructura objetivo | OCI (Oracle Cloud Infrastructure) |
| Arquitectura | Monolito modular por dominios |

**Convenciones del proyecto:**
- Prefijo `GC_` para todas las tablas de negocio.
- Prefijo `CAT_` para catálogos transversales.
- Auditoría obligatoria en todas las transiciones relevantes.
- Pipelines dinámicos parametrizables por ámbito (`COMERCIAL` / `CONTRATACION`).
- Modo guía por defecto (no bloquea avance entre etapas).

---

## 3. Mundo 1 — Gestión Comercial ✅

### 3.1 Estado: Completado

### 3.2 Entidades implementadas

| Entidad | Descripción |
|---------|-------------|
| **Empresa** | Organizaciones (prospecto / cliente / aliado). Incluye perfiles, sitio web, catálogos. |
| **Persona** | Contactos con relación multi-empresa y roles diferenciados. |
| **Oportunidad** | Núcleo del proceso comercial. Asociada a 1 empresa, 1 pipeline, 1 etapa. |
| **Pipeline / Etapa** | Pipelines dinámicos configurables, con etapas ordenadas. |
| **Actividad** | Interacciones registradas (reunión, llamada, envío de propuesta, etc.). |
| **Compromiso** | Tareas derivadas de actividades. |
| **Documento** | Metadatos de documentos asociados a oportunidades. |

### 3.3 Funcionalidades operativas

- ✅ CRUD completo de Empresas, Personas y Oportunidades.
- ✅ Pipeline Kanban con drag & drop entre etapas.
- ✅ Registro de actividades y compromisos.
- ✅ Gestión documental (metadatos).
- ✅ Cierre de oportunidades: GANADA / PERDIDA / NO_CONCRETADA / CONTRATADA.
- ✅ Dashboard analítico comercial.
- ✅ Sistema de alertas (campana).
- ✅ Login y RBAC (admin/admin123 como usuario base de pruebas).
- ✅ 42 endpoints REST funcionales.
- ✅ Matriz de reglas de negocio aplicada (RB-01 a RB-19).

### 3.4 Reglas de negocio críticas aplicadas

| ID | Regla |
|----|-------|
| RB-01 | Una oportunidad pertenece a 1 pipeline y 1 etapa. |
| RB-04 | Motivo obligatorio cuando el cierre es PERDIDA o NO_CONCRETADA. |
| RB-14 | Pipeline inmutable después de creación de la oportunidad. |
| RB-15 | La etapa debe pertenecer al mismo pipeline de la oportunidad. |
| RB-19 | Una oportunidad cerrada es inmutable (solo lectura). |

### 3.5 Pendientes menores del Mundo 1

> No bloquean la nueva versión, pero deben considerarse:

- [ ] Reportes exportables (Excel / PDF) sobre listados.
- [ ] Filtros avanzados persistentes por usuario en listados.
- [ ] Mejoras de paginación y rendimiento sobre tablas grandes.

---

## 4. Mundo 2 — Gestión Contractual 🟡

### 4.1 Estado: En ejecución (~70%)

El proceso de contratación **no es independiente del comercial**. Inicia cuando una oportunidad cambia a estado GANADA y termina cuando se formaliza un contrato (`GC_CONTRATO`).

### 4.2 Flujo definido

```
Pipeline Comercial (ABIERTA → SEGUIMIENTO → GANADA)
        │
        └── al marcar GANADA
                │
                ▼
        Pipeline Contractual (etapas dinámicas: revisión jurídica, negociación, firma, legalización...)
                │
                └── botón "Formalizar Contrato" (visible en cualquier etapa, desaparece al crear el contrato)
                        │
                        ▼
                GC_CONTRATO creado en estado VIGENTE
                        │
                        └── Oportunidad pasa a estado CONTRATADA → sale de los pipelines
```

### 4.3 Decisiones de modelo confirmadas

- Las **etapas** rigen la posición en el pipeline contractual; **no se usan estados** en la pipeline.
- En la pipeline comercial, el botón "Cerrar" se comporta así:
  - GANADA → dispara la formalización del contrato.
  - PERDIDA / NO_CONCRETADA → permanece como "Cerrar".
- El contrato **no existe** hasta completar el pipeline contractual; lo que existe durante el proceso es un `GC_PROCESO_CONTRATACION`.
- El contrato nace directamente en estado **VIGENTE** (se eliminó el estado EN_PROCESO).

### 4.4 Tareas completadas (F1 → F5)

| Fase | Tareas | Estado |
|------|--------|--------|
| **F1 — Preparación del modelo** | T1.1 a T1.5 | ✅ Completada |
| **F2 — Backend Proceso de Contratación** | T2.1 a T2.4 | ✅ Completada |
| **F3 — Backend Contratos** | T3.1 a T3.5 | ✅ Completada |
| **F4 — Frontend Proceso de Contratación** | T4.1 a T4.4 | ✅ Completada |
| **F5 — Frontend Contratos** | T5.1 a T5.5 | ✅ Completada |

#### Detalle de lo construido

**F1 — Modelo de datos:**
- ✅ Campo `ambito` (COMERCIAL / CONTRATACION) en `GC_PIPELINE`.
- ✅ Tabla `GC_TIPO_CONTRATO` (catálogo: Contrato Formal, Orden de Compra, Orden de Servicio, etc.).
- ✅ Tabla `GC_PROCESO_CONTRATACION` (conecta oportunidad con pipeline contractual).
- ✅ Tabla `GC_CONTRATO` (contrato formalizado).
- ✅ Tabla `GC_CONTRATO_FORMA_PAGO` (formas de pago).
- ✅ Tabla `GC_CONTRATO_MODIFICACION` (adiciones, prórrogas, suspensiones).
- ✅ Constraint `CK_GC_OPORTUNIDAD_ESTADO` actualizado con valor `CONTRATADA`.

**F2 — Backend Proceso:**
- ✅ Entidad `GcProcesoContratacion`, repository, service y controller.
- ✅ DTOs y endpoints para mover el proceso entre etapas.

**F3 — Backend Contratos:**
- ✅ Entidades `GcContrato`, `GcContratoFormaPago`, `GcContratoModificacion`.
- ✅ Services para creación, formas de pago, modificaciones y cambios de estado (suspender / reiniciar / terminar / liquidar).
- ✅ Controller `/api/v1/contratos` con endpoints CRUD.
- ✅ `findByIdWithRelations` con `JOIN FETCH c.oportunidad` para evitar lazy-load.
- ✅ `GlobalExceptionHandler` mostrando causa raíz para acelerar debugging.

**F4 — Frontend Proceso:**
- ✅ Service de proceso de contratación.
- ✅ Pipeline contractual visible en el detalle de la oportunidad ganada.
- ✅ Botón "Formalizar Contrato" visible mientras no exista contrato.
- ✅ Modal de formalización con tipo, números, fechas, interventor, responsable, empresa de facturación.

**F5 — Frontend Contratos:**
- ✅ `ContratosListView.vue` — tabla filtrable por estado, paginada, click-to-detail.
- ✅ `ContratoDetalleView.vue` — KPIs (valor, ajustes, total, días restantes), grid de datos, formas de pago (tabla + modal), modificaciones (lista + modal), botones de cambio de estado.
- ✅ Rutas `/contratos` y `/contratos/:id` en `router/index.js`.
- ✅ Ítem "Contratos" en NavRail con icono `note-add`.

### 4.5 Tareas pendientes del Mundo 2

#### F6 — Dashboard Contractual 🔲

| Tarea | Descripción | Estado |
|-------|-------------|--------|
| **T6.1** | Endpoint backend de estadísticas contractuales | 🔲 |
| **T6.2** | Vista frontend de Dashboard de Gestión Contractual | 🔲 |
| **T6.3** | Integración con Dashboard general (Mundo 1 + Mundo 2) | 🔲 |

### 4.6 Issues abiertos / deuda técnica del Mundo 2

| # | Issue | Severidad | Acción requerida |
|---|-------|-----------|------------------|
| I-01 | Error `ORA-00904: "M1_0"."FECHAMODIFICACION"` por caché stale de Hibernate generado por un `@OrderBy` incorrecto en `GcContratoModificacion`. | Alta | Ejecutar `mvn clean package -DskipTests` y verificar que el campo correcto en la entidad es `fechaCreacion`. |
| I-02 | El repo `gestion-comercial` no tenía las últimas correcciones al cierre de la última sesión. `GcContrato.java` aún contenía el viejo `nullable = false` en `proceso_contratacion_id` y el `@OrderBy` incorrecto. | Alta | Verificar estado del repo en GitHub vs. archivos locales antes de continuar; re-deploy de los archivos corregidos. |

---

## 5. Mundo 3 — Flujo de Facturación 🔲

### 5.1 Estado: Sin especificar

Está identificado como el siguiente módulo a abordar. **No hay especificación funcional formal**; sólo notas previas y un esbozo de flujo discutido.

### 5.2 Esbozo de flujo (a validar)

```
Contrato VIGENTE
    │
    └── Formas de Pago (estado PENDIENTE)
            │
            ├── Generar Factura desde forma de pago
            │   → Se crea GC_FACTURA con datos heredados
            │   → La forma de pago pasa a FACTURADA
            │
            └── GC_FACTURA
                    │ Estados: EMITIDA → ENVIADA → COBRADA / ANULADA
                    │
                    ├── Datos: número, fecha emisión, fecha vencimiento, valor, IVA
                    ├── Empresa facturación (filial)
                    ├── Documentos (factura PDF, soporte)
                    └── Pagos parciales o totales
```

### 5.3 Decisiones funcionales pendientes

| # | Pregunta | Opciones |
|---|----------|----------|
| Q1 | ¿Cómo se genera la factura? | Desde forma de pago / Manual / Ambas |
| Q2 | ¿Se manejan IVA / impuestos? | No / Solo IVA / Múltiples impuestos |
| Q3 | ¿Se permiten pagos parciales? | Sí / No / Después |
| Q4 | ¿Integración con DIAN (facturación electrónica)? | Sí / No / Solo recepción |
| Q5 | ¿Existe flujo de aprobación interno antes de emitir? | Sí / No |
| Q6 | ¿Cómo se hace seguimiento al cobro? | Estado en la factura / Workflow de cartera |
| Q7 | Manejo multidivisa con conversión a la fecha de emisión | Sí / No |

### 5.4 Tareas requeridas (a especificar antes de ejecutar)

#### F7 — Especificación funcional del Mundo 3 🔲
- [ ] **T7.1** — Documento de alcance del Flujo de Facturación (entradas, salidas, actores, reglas).
- [ ] **T7.2** — Modelo de datos detallado: entidades, atributos, relaciones, estados.
- [ ] **T7.3** — Reglas de negocio (RB-XX) específicas de facturación.
- [ ] **T7.4** — Matriz de transiciones de estado de la factura.
- [ ] **T7.5** — Flujo de generación desde forma de pago (paso a paso).
- [ ] **T7.6** — Definir si y cómo se integra con DIAN.

#### F8 — Backend Mundo 3 🔲
- [ ] **T8.1** — Tablas Oracle: `GC_FACTURA`, `GC_FACTURA_DETALLE`, `GC_FACTURA_PAGO`, catálogos.
- [ ] **T8.2** — Entidades JPA, repositories y mapeos.
- [ ] **T8.3** — Service de facturación: emisión desde forma de pago, edición, anulación.
- [ ] **T8.4** — Service de pagos / cobros (parciales y totales).
- [ ] **T8.5** — Controller `/api/v1/facturas` con endpoints CRUD + acciones.
- [ ] **T8.6** — Endpoint para proyección futura (qué se debería facturar en los próximos N meses).

#### F9 — Frontend Mundo 3 🔲
- [ ] **T9.1** — Service `factura.service.js`.
- [ ] **T9.2** — Vista `/facturas` con lista filtrable.
- [ ] **T9.3** — Vista `/facturas/:id` con detalle, pagos, documentos.
- [ ] **T9.4** — Botón "Facturar" en cada forma de pago del contrato.
- [ ] **T9.5** — Modal de emisión / anulación.
- [ ] **T9.6** — Vista de proyección de facturación (calendario / listado por mes).

---

## 6. Mundo 4 — Facturación (registro y análisis) 🔲

### 6.1 Estado: Sin especificar

Conceptualmente diferente del Mundo 3:
- **Mundo 3 — Flujo de Facturación:** *qué se debería facturar* (proyección y emisión).
- **Mundo 4 — Facturación:** *registro y análisis de lo facturado* (importación desde sistema externo, conciliación, KPIs de cartera).

### 6.2 Decisiones pendientes

| # | Pregunta | Pendiente |
|---|----------|-----------|
| Q1 | ¿De dónde se importan las facturas reales? | Sistema de facturación electrónica externo (¿cuál?) |
| Q2 | ¿Modo de ingesta? | Manual / API / Archivo |
| Q3 | ¿Se concilia automáticamente con el Mundo 3? | Por número, valor, contrato |
| Q4 | ¿Qué KPIs deben mostrarse? | Cartera, antigüedad, recaudo |

### 6.3 Tareas requeridas (a especificar antes de ejecutar)

#### F10 — Especificación funcional del Mundo 4 🔲
- [ ] **T10.1** — Documento de alcance del registro de facturación.
- [ ] **T10.2** — Definición del mecanismo de ingesta (API / archivo / manual).
- [ ] **T10.3** — Reglas de conciliación con el Mundo 3.
- [ ] **T10.4** — Definición de KPIs (cartera, recaudo, antigüedad).

#### F11 — Backend Mundo 4 🔲
- [ ] **T11.1** — Tablas Oracle de registro de facturación real.
- [ ] **T11.2** — Service de ingesta y conciliación.
- [ ] **T11.3** — Endpoints de consulta y KPIs.

#### F12 — Frontend Mundo 4 🔲
- [ ] **T12.1** — Vista de registro de facturas reales.
- [ ] **T12.2** — Vista de cartera por cliente / contrato.
- [ ] **T12.3** — Dashboard de cartera y recaudo.

---

## 7. Resumen Ejecutivo de Avance

| Mundo | Total tareas mayores | Completadas | Pendientes | Avance |
|-------|----------------------|-------------|------------|--------|
| 1 — Comercial | (base completa) | ✅ | Mejoras menores | ~95% |
| 2 — Contractual | 26 (F1–F6) | 22 (F1–F5) | 4 (F6 + 2 issues) | ~70% |
| 3 — Flujo de Facturación | A definir | 0 | Todo (F7–F9) | 0% |
| 4 — Facturación | A definir | 0 | Todo (F10–F12) | 0% |

---

## 8. Plan recomendado para la nueva versión

Para arrancar la nueva versión con orden, este es el camino sugerido:

### Bloque A — Cierre del Mundo 2 (corto plazo)
1. Resolver issues técnicos abiertos (clean build + verificar repo).
2. Implementar F6 (Dashboard contractual): T6.1 → T6.2 → T6.3.

### Bloque B — Especificación del Mundo 3 (antes de codificar)
1. Sesión de definición funcional: responder Q1–Q7.
2. Generar `GestCom_Alcance_Facturacion.md` (equivalente al de contratación).
3. Generar el backlog de tareas F7–F9 con dependencias.

### Bloque C — Implementación del Mundo 3
1. F7 (especificación) → F8 (backend) → F9 (frontend).
2. Conectar con el Dashboard contractual del Mundo 2 (datos reales de cobro).

### Bloque D — Especificación e implementación del Mundo 4
1. F10 (especificación, con foco en mecanismo de ingesta y conciliación).
2. F11 (backend) → F12 (frontend).

### Bloque E — Mejoras transversales
1. Reportes exportables (Excel/PDF) en todos los listados.
2. Refinamiento del Dashboard general (los 4 mundos integrados).
3. Tests automatizados (cobertura de servicios críticos).

---

## 9. Documentos de referencia existentes

| Documento | Descripción |
|-----------|-------------|
| `GestCom_Alcance_Contratacion_v1.md` | Especificación funcional del Mundo 2. |
| `GestCom_Tareas_Mundo2.md` | Backlog detallado de las 26 tareas del Mundo 2. |
| `T-00 Análisis Técnico de la Especificación` | Análisis técnico inicial del alcance v0.1 (Mundo 1). |
| `B-04 Matriz de Reglas de Negocio` | Matriz RB-XXX → endpoints (Mundo 1). |
| `Gestion_Contratos.sql` | Estructura SQL preexistente del módulo de contratos (insumo del Mundo 2). |

---

## 10. Control de cambios

| Versión | Fecha | Cambio |
|---------|-------|--------|
| 1.0 | Mayo 2026 | Documento inicial de estado del arte. Línea base para nueva versión. |

---

*— Fin del Documento —*
