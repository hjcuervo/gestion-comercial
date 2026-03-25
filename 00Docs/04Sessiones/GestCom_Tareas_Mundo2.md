# GestCom — Backlog de Tareas: Mundo 2 - Gestión Contractual

**Fecha:** Marzo 2026  
**Estado:** En ejecución

---

## Resumen de Fases

| Fase | Descripción | Tareas | Estado |
|------|-------------|--------|--------|
| F1 | Preparación del modelo | 5 | 🔲 |
| F2 | Backend - Proceso de Contratación | 4 | 🔲 |
| F3 | Backend - Contratos | 5 | 🔲 |
| F4 | Frontend - Proceso de Contratación | 4 | 🔲 |
| F5 | Frontend - Contratos | 5 | 🔲 |
| F6 | Dashboard Contractual | 3 | 🔲 |

---

## F1. Preparación del Modelo

Tablas, catálogos y ajustes al modelo existente.

### T1.1 — Agregar campo ámbito al pipeline
- **Descripción:** Agregar campo `ambito` (VARCHAR 20) a la tabla `GC_PIPELINE` para diferenciar entre pipelines `COMERCIAL` y `CONTRATACION`. Actualizar la entidad JPA, DTOs y el frontend de configuración de pipelines.
- **Impacto:** Backend (entidad, migración) + Frontend (selector de ámbito en PipelineModal)
- **Dependencias:** Ninguna
- **Estado:** 🔲

### T1.2 — Crear tabla GC_TIPO_CONTRATO
- **Descripción:** Crear tabla catálogo de tipos de contrato (Contrato Formal, Orden de Compra, Orden de Servicio, etc.). Crear entidad JPA, repository y endpoint en CatalogoController.
- **SQL:**
```sql
CREATE TABLE GC_TIPO_CONTRATO (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre VARCHAR2(50) NOT NULL,
    descripcion VARCHAR2(200),
    activo NUMBER(1) DEFAULT 1 NOT NULL
);
```
- **Dependencias:** Ninguna
- **Estado:** 🔲

### T1.3 — Crear tabla GC_PROCESO_CONTRATACION
- **Descripción:** Crear tabla para el proceso de contratación que conecta oportunidad ganada con el pipeline de contratación.
- **SQL:**
```sql
CREATE TABLE GC_PROCESO_CONTRATACION (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    oportunidad_id NUMBER NOT NULL,
    empresa_id NUMBER NOT NULL,
    pipeline_id NUMBER NOT NULL,
    etapa_id NUMBER NOT NULL,
    estado VARCHAR2(20) DEFAULT 'EN_CURSO' NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_completado DATE,
    responsable VARCHAR2(100),
    observaciones VARCHAR2(2000),
    creado_por NUMBER NOT NULL,
    modificado_por NUMBER,
    fecha_creacion TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    fecha_modificacion TIMESTAMP,
    CONSTRAINT fk_proc_oportunidad FOREIGN KEY (oportunidad_id) REFERENCES GC_OPORTUNIDAD(id),
    CONSTRAINT fk_proc_empresa FOREIGN KEY (empresa_id) REFERENCES GC_EMPRESA(id),
    CONSTRAINT fk_proc_pipeline FOREIGN KEY (pipeline_id) REFERENCES GC_PIPELINE(id),
    CONSTRAINT fk_proc_etapa FOREIGN KEY (etapa_id) REFERENCES GC_ETAPA(id)
);
```
- **Dependencias:** T1.1
- **Estado:** 🔲

### T1.4 — Crear tabla GC_CONTRATO
- **Descripción:** Crear tabla principal de contratos.
- **SQL:**
```sql
CREATE TABLE GC_CONTRATO (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    proceso_contratacion_id NUMBER NOT NULL,
    oportunidad_id NUMBER NOT NULL,
    empresa_id NUMBER NOT NULL,
    empresa_facturacion_id NUMBER,
    tipo_contrato_id NUMBER NOT NULL,
    numero_contrato_interno VARCHAR2(50),
    numero_contrato_cliente VARCHAR2(100),
    objeto VARCHAR2(1000),
    moneda VARCHAR2(3) DEFAULT 'COP',
    valor_contrato NUMBER(16,2),
    valor_ajuste NUMBER(16,2) DEFAULT 0,
    fecha_inicio DATE,
    fecha_fin DATE,
    estado VARCHAR2(20) DEFAULT 'VIGENTE' NOT NULL,
    responsable_gestion VARCHAR2(100),
    interventor VARCHAR2(200),
    observaciones VARCHAR2(2000),
    creado_por NUMBER NOT NULL,
    modificado_por NUMBER,
    fecha_creacion TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    fecha_modificacion TIMESTAMP,
    CONSTRAINT fk_cont_proceso FOREIGN KEY (proceso_contratacion_id) REFERENCES GC_PROCESO_CONTRATACION(id),
    CONSTRAINT fk_cont_oportunidad FOREIGN KEY (oportunidad_id) REFERENCES GC_OPORTUNIDAD(id),
    CONSTRAINT fk_cont_empresa FOREIGN KEY (empresa_id) REFERENCES GC_EMPRESA(id),
    CONSTRAINT fk_cont_empresa_fact FOREIGN KEY (empresa_facturacion_id) REFERENCES GC_EMPRESA(id),
    CONSTRAINT fk_cont_tipo FOREIGN KEY (tipo_contrato_id) REFERENCES GC_TIPO_CONTRATO(id)
);
```
- **Dependencias:** T1.2, T1.3
- **Estado:** 🔲

### T1.5 — Crear tablas GC_CONTRATO_FORMA_PAGO y GC_CONTRATO_MODIFICACION
- **Descripción:** Crear tablas de formas de pago y modificaciones del contrato.
- **SQL:**
```sql
CREATE TABLE GC_CONTRATO_FORMA_PAGO (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    contrato_id NUMBER NOT NULL,
    descripcion VARCHAR2(500) NOT NULL,
    valor NUMBER(16,2) NOT NULL,
    fecha_estimada_pago DATE,
    estado VARCHAR2(20) DEFAULT 'PENDIENTE' NOT NULL,
    factura_id NUMBER,
    empresa_facturacion_id NUMBER,
    creado_por NUMBER NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT fk_fp_contrato FOREIGN KEY (contrato_id) REFERENCES GC_CONTRATO(id),
    CONSTRAINT fk_fp_empresa_fact FOREIGN KEY (empresa_facturacion_id) REFERENCES GC_EMPRESA(id)
);

CREATE TABLE GC_CONTRATO_MODIFICACION (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    contrato_id NUMBER NOT NULL,
    tipo_modificacion VARCHAR2(20) NOT NULL,
    modifica_tiempo NUMBER(1) DEFAULT 0,
    modifica_valor NUMBER(1) DEFAULT 0,
    valor_modificacion NUMBER(16,2),
    nueva_fecha_fin DATE,
    fecha_modificacion DATE NOT NULL,
    observaciones VARCHAR2(1000),
    creado_por NUMBER NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT fk_mod_contrato FOREIGN KEY (contrato_id) REFERENCES GC_CONTRATO(id)
);
```
- **Dependencias:** T1.4
- **Estado:** 🔲

---

## F2. Backend — Proceso de Contratación

Entidades JPA, repositories, services y controllers para el proceso de contratación.

### T2.1 — Entidad y Repository GcProcesoContratacion
- **Descripción:** Crear entidad JPA `GcProcesoContratacion` con relaciones a oportunidad, empresa, pipeline y etapa. Crear repository con queries: findByOportunidadId, findByEstado, findByIdWithRelations.
- **Archivos:** entity/GcProcesoContratacion.java, repository/GcProcesoContratacionRepository.java
- **Dependencias:** T1.3
- **Estado:** 🔲

### T2.2 — Service ProcesoContratacionService
- **Descripción:** Crear servicio con lógica de negocio:
  - `iniciarProceso(oportunidadId, pipelineId)` — valida que la oportunidad está GANADA, crea el proceso en estado EN_CURSO.
  - `moverEtapa(procesoId, etapaId)` — avanza el proceso por el pipeline.
  - `completarProceso(procesoId)` — cambia estado a COMPLETADO, habilita creación de contrato.
  - `cancelarProceso(procesoId)` — cambia estado a CANCELADO.
- **Reglas:** RN-01, RN-02, RN-03, RN-04
- **Dependencias:** T2.1
- **Estado:** 🔲

### T2.3 — Controller ProcesoContratacionController
- **Descripción:** Endpoints REST:
  - `POST /api/v1/procesos-contratacion` — iniciar proceso desde oportunidad ganada
  - `GET /api/v1/procesos-contratacion?oportunidad_id=&estado=` — listar procesos
  - `GET /api/v1/procesos-contratacion/{id}` — obtener proceso con detalle
  - `POST /api/v1/procesos-contratacion/{id}/mover-etapa` — avanzar etapa
  - `POST /api/v1/procesos-contratacion/{id}/completar` — completar proceso
  - `POST /api/v1/procesos-contratacion/{id}/cancelar` — cancelar proceso
- **Dependencias:** T2.2
- **Estado:** 🔲

### T2.4 — DTOs del Proceso de Contratación
- **Descripción:** Crear DTOs: ProcesoContratacionResponse, ProcesoContratacionCreateRequest, MoverEtapaRequest.
- **Dependencias:** T2.1
- **Estado:** 🔲

---

## F3. Backend — Contratos

Entidades JPA, repositories, services y controllers para contratos, formas de pago y modificaciones.

### T3.1 — Entidades JPA: GcContrato, GcContratoFormaPago, GcContratoModificacion, GcTipoContrato
- **Descripción:** Crear las 4 entidades JPA con sus relaciones y repositories.
- **Dependencias:** T1.4, T1.5
- **Estado:** 🔲

### T3.2 — Service ContratoService
- **Descripción:** Crear servicio con:
  - `crearDesdeProcesoCompletado(procesoId, datosContrato)` — valida que el proceso está COMPLETADO, hereda datos de la oportunidad, crea contrato en estado VIGENTE.
  - `obtenerPorId`, `listar` con filtros (estado, empresa, moneda).
  - `suspender`, `reiniciar`, `terminar`, `liquidar` — transiciones de estado.
- **Reglas:** RN-05, RN-06, RN-07, RN-08
- **Dependencias:** T3.1, T2.2
- **Estado:** 🔲

### T3.3 — Service FormaPagoService
- **Descripción:** CRUD de formas de pago + validación de que la suma no excede el valor del contrato (RN-09). Cambio de estado de formas de pago.
- **Reglas:** RN-09, RN-10, RN-11
- **Dependencias:** T3.1
- **Estado:** 🔲

### T3.4 — Service ModificacionService
- **Descripción:** Registrar modificaciones (adición, prórroga, suspensión, reinicio). Actualizar valor_ajuste y/o fecha_fin del contrato automáticamente.
- **Reglas:** RN-12, RN-13, RN-14
- **Dependencias:** T3.1
- **Estado:** 🔲

### T3.5 — Controller ContratoController
- **Descripción:** Endpoints REST:
  - `POST /api/v1/contratos` — crear contrato desde proceso completado
  - `GET /api/v1/contratos` — listar con filtros
  - `GET /api/v1/contratos/{id}` — detalle con formas de pago y modificaciones
  - `POST /api/v1/contratos/{id}/suspender|reiniciar|terminar|liquidar`
  - CRUD formas de pago: `GET|POST /api/v1/contratos/{id}/formas-pago`
  - CRUD modificaciones: `GET|POST /api/v1/contratos/{id}/modificaciones`
  - Endpoint catálogo: `GET /api/v1/catalogos/tipos-contrato`
- **Dependencias:** T3.2, T3.3, T3.4
- **Estado:** 🔲

---

## F4. Frontend — Proceso de Contratación

Vistas y componentes para gestionar el proceso de contratación desde el detalle de oportunidad.

### T4.1 — Service y Store de Proceso de Contratación
- **Descripción:** Crear `procesoContratacion.service.js` con llamadas a los endpoints. Store si es necesario.
- **Dependencias:** T2.3
- **Estado:** 🔲

### T4.2 — Sección "Proceso de Contratación" en Detalle de Oportunidad
- **Descripción:** En el detalle de una oportunidad GANADA, mostrar sección con:
  - Botón "Iniciar Proceso de Contratación" (si no existe proceso activo)
  - Si existe proceso EN_CURSO: pipeline visual con etapas, etapa actual destacada
  - Botón para avanzar etapa
  - Actividades, compromisos y documentos del proceso
  - Botón "Completar Proceso" en la última etapa
- **Dependencias:** T4.1
- **Estado:** 🔲

### T4.3 — Modal de Inicio de Proceso de Contratación
- **Descripción:** Modal para seleccionar el pipeline de contratación (filtrado por ámbito=CONTRATACION), responsable y observaciones iniciales.
- **Dependencias:** T4.1, T1.1
- **Estado:** 🔲

### T4.4 — Modal de Formalización del Contrato
- **Descripción:** Modal que aparece al completar el proceso. Formulario para complementar datos del contrato: tipo de contrato (catálogo), número interno, número cliente, objeto, fechas, interventor, responsable, empresa de facturación (si es diferente).
- **Dependencias:** T4.1, T3.5
- **Estado:** 🔲

---

## F5. Frontend — Contratos

Vistas para gestionar contratos formalizados.

### T5.1 — Service de Contratos
- **Descripción:** Crear `contrato.service.js` con llamadas a endpoints de contratos, formas de pago y modificaciones.
- **Dependencias:** T3.5
- **Estado:** 🔲

### T5.2 — Vista de Lista de Contratos
- **Descripción:** Nueva vista `/contratos` con tabla de contratos, filtros por estado, empresa, moneda. Click en fila navega al detalle. Agregar ítem "Contratos" al NavRail.
- **Archivos:** OportunidadesListView.vue (referencia), router, AppLayout
- **Dependencias:** T5.1
- **Estado:** 🔲

### T5.3 — Vista de Detalle de Contrato
- **Descripción:** Nueva vista `/contratos/:id` con:
  - Header: nombre, empresa, valor, estado, moneda
  - Datos generales: tipo, números, fechas, interventor, responsable
  - Formas de pago: tabla con estado, valor, fecha estimada, empresa facturación
  - Modificaciones: historial de adiciones, prórrogas, etc.
  - Documentos asociados
- **Dependencias:** T5.1
- **Estado:** 🔲

### T5.4 — Modales de Formas de Pago y Modificaciones
- **Descripción:** Modal para agregar forma de pago (descripción, valor, fecha estimada, empresa facturación). Modal para registrar modificación (tipo, valor, fecha, observaciones).
- **Dependencias:** T5.3
- **Estado:** 🔲

### T5.5 — Cambio de Estado del Contrato
- **Descripción:** Botones en el detalle del contrato para cambiar estado (Suspender, Reiniciar, Terminar, Liquidar) con confirmación y validación de transiciones.
- **Dependencias:** T5.3
- **Estado:** 🔲

---

## F6. Dashboard Contractual

KPIs y análisis del proceso contractual.

### T6.1 — Backend: Endpoint de Estadísticas Contractuales
- **Descripción:** Crear `ContratoStatsController` con endpoint `GET /api/v1/contratos/stats` que retorne:
  - KPIs: contratos vigentes (cantidad y valor por moneda), en proceso, valor pendiente facturar
  - Contratos próximos a vencer
  - Formas de pago vencidas
  - Distribución por empresa
- **Dependencias:** T3.5
- **Estado:** 🔲

### T6.2 — Frontend: Dashboard de Gestión Contractual
- **Descripción:** Vista/sección de dashboard con KPIs contractuales, gráficos de estado, próximos vencimientos y formas de pago pendientes.
- **Dependencias:** T6.1
- **Estado:** 🔲

### T6.3 — Integración con Dashboard General
- **Descripción:** Agregar sección de KPIs contractuales al Dashboard principal (Mundo 1 + Mundo 2 integrados).
- **Dependencias:** T6.2
- **Estado:** 🔲

---

## Diagrama de Dependencias

```
T1.1 (ámbito pipeline)
  │
  ├── T1.3 (tabla proceso) ── T2.1 ── T2.2 ── T2.3
  │                                      │       │
  │                                     T2.4    T4.1 ── T4.2
  │                                              │       T4.3
  │                                              │       T4.4
  │
T1.2 (tipo contrato)
  │
  └── T1.4 (tabla contrato) ── T1.5 (formas pago + modificaciones)
                                  │
                                  └── T3.1 ── T3.2 ── T3.5 ── T5.1 ── T5.2
                                        │      T3.3           │       T5.3 ── T5.4
                                        │      T3.4           │              T5.5
                                        │                     │
                                        └── T6.1 ── T6.2 ── T6.3
```

---

## Orden de Ejecución Sugerido

| Orden | Tarea | Descripción corta |
|-------|-------|-------------------|
| 1 | T1.1 | Agregar ámbito al pipeline |
| 2 | T1.2 | Catálogo tipos de contrato |
| 3 | T1.3 | Tabla proceso contratación |
| 4 | T1.4 | Tabla contrato |
| 5 | T1.5 | Tablas formas de pago + modificaciones |
| 6 | T2.1 + T2.4 | Entidad + DTOs proceso contratación |
| 7 | T2.2 | Service proceso contratación |
| 8 | T2.3 | Controller proceso contratación |
| 9 | T3.1 | Entidades JPA contratos |
| 10 | T3.2 + T3.3 + T3.4 | Services contratos |
| 11 | T3.5 | Controller contratos |
| 12 | T4.1 | Service frontend proceso |
| 13 | T4.2 + T4.3 | UI proceso en detalle oportunidad |
| 14 | T4.4 | Modal formalización contrato |
| 15 | T5.1 | Service frontend contratos |
| 16 | T5.2 | Lista de contratos |
| 17 | T5.3 + T5.4 + T5.5 | Detalle contrato completo |
| 18 | T6.1 | Backend stats contractuales |
| 19 | T6.2 + T6.3 | Dashboard contractual |

---

## Control de Avance

| Fase | Total | Completadas | Progreso |
|------|-------|-------------|----------|
| F1 | 5 | 0 | 0% |
| F2 | 4 | 0 | 0% |
| F3 | 5 | 0 | 0% |
| F4 | 4 | 0 | 0% |
| F5 | 5 | 0 | 0% |
| F6 | 3 | 0 | 0% |
| **Total** | **26** | **0** | **0%** |

---

*Última actualización: Marzo 2026*
