# GestCom — Documento de Alcance: Proceso de Contratación

**Versión:** 1.1  
**Fecha:** Marzo 2026  
**Empresa:** Arquitecsoft SAS  
**Estado:** En revisión

---

## 1. Contexto y Visión General

GestCom es una plataforma integral que gestiona el ciclo de vida completo de la relación comercial de Arquitecsoft con sus clientes, desde la identificación de una oportunidad hasta la facturación del servicio contratado.

La plataforma se organiza en **4 mundos funcionales** integrados entre sí:

| Mundo | Alcance |
|-------|---------|
| **1. Gestión Comercial** | Desde la identificación de la oportunidad hasta la adjudicación del negocio. Incluye pipeline comercial, actividades, compromisos y documentos. |
| **2. Gestión Contractual** | Desde la oportunidad ganada hasta el contrato formalizado y en ejecución. Incluye pipeline de contratación, tipos de contrato, formas de pago y seguimiento. |
| **3. Flujo de Facturación** | Proyección futura de la facturación pendiente según contratos vigentes y sus formas de pago. |
| **4. Facturación** | Registro y análisis de facturas emitidas. No emite facturas, las recibe del sistema de facturación electrónica. |

> **Este documento especifica el alcance del Mundo 2: Gestión Contractual**, entendido como la continuación natural del proceso comercial (Mundo 1) que ya se encuentra implementado.

---

## 2. Flujo General del Proceso

El proceso de contratación **no es independiente del comercial**. Es la fase que inicia cuando una oportunidad comercial cambia su estado a GANADA dentro del pipeline comercial.

### Fase 1: Gestión Comercial (Ya implementada) ✅

1. Se identifica una oportunidad comercial (prospecto interesado en un servicio/plataforma).
2. Se registra la oportunidad con empresa, contactos, valor estimado, pipeline.
3. Se gestiona a través del pipeline comercial: actividades, compromisos, documentos.
4. La oportunidad se cierra como GANADA, PERDIDA o NO_CONCRETADA.

### Fase 2: Pipeline de Contratación (Alcance de este documento) 🔲

1. Cuando la oportunidad se marca como **GANADA**, se inicia un **proceso de contratación** asociado a esa oportunidad.
2. Este proceso se gestiona a través de un **pipeline de contratación** con etapas jurídicas y administrativas.
3. Durante este pipeline se realizan actividades, compromisos y se adjuntan documentos (igual que en el pipeline comercial).
4. **El contrato como entidad NO existe durante este proceso.** Lo que existe es un proceso de contratación en curso.
5. **Solo cuando se completa exitosamente la última etapa del pipeline de contratación**, se procede a formalizar el contrato.
6. En ese momento se genera la entidad contrato con los datos complementarios: número, tipo de documento contractual, fechas, interventor, formas de pago.

> **Principio clave:** Marcar una oportunidad como GANADA **no genera un contrato inmediatamente**. Inicia un proceso de contratación que debe completarse para que el contrato exista formalmente en la plataforma. El contrato es el **resultado** del pipeline de contratación, no su punto de partida.

### Fase 3: Ejecución Contractual 🔲

1. Una vez formalizado, el contrato entra en estado **VIGENTE** y comienza su ejecución.
2. Se definen las formas de pago que alimentarán el flujo de facturación futuro.
3. Se hace seguimiento a la ejecución, modificaciones y cumplimiento.

---

## 3. Proceso de Contratación (GC_PROCESO_CONTRATACION)

Esta es la entidad que conecta la oportunidad ganada con el pipeline de contratación. Existe desde que la oportunidad se marca como GANADA hasta que se formaliza el contrato.

### 3.1 Entidad: Proceso de Contratación

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long PK | Identificador interno autogenerado |
| oportunidad_id | Long FK | Referencia a la oportunidad GANADA que originó el proceso |
| empresa_id | Long FK | Empresa cliente (heredada de la oportunidad) |
| pipeline_id | Long FK | Pipeline de contratación asignado |
| etapa_id | Long FK | Etapa actual dentro del pipeline de contratación |
| estado | ENUM | EN_CURSO, COMPLETADO, CANCELADO |
| fecha_inicio | DATE | Fecha en que se inició el proceso (cuando se marcó GANADA) |
| fecha_completado | DATE null | Fecha en que se completó el pipeline (se formaliza el contrato) |
| responsable | VARCHAR 100 | Responsable interno del proceso de contratación |
| observaciones | VARCHAR 2000 | Observaciones del proceso |

**Estados del Proceso:**

| Estado | Descripción |
|--------|-------------|
| **EN_CURSO** | El proceso de contratación está activo, avanzando por las etapas del pipeline. |
| **COMPLETADO** | El pipeline se completó exitosamente. Se procede a formalizar el contrato. |
| **CANCELADO** | El proceso se canceló (el cliente se retractó, cambio de condiciones, etc.). La oportunidad podría volver a estado ABIERTA o quedar como NO_CONCRETADA. |

### 3.2 Actividades y Compromisos del Proceso

Durante el pipeline de contratación se pueden registrar actividades y compromisos, reutilizando la misma infraestructura que ya existe:

- Actividades asociadas al proceso (reuniones con jurídico, revisión de pólizas, etc.)
- Compromisos con seguimiento y estados
- Documentos (borradores de contrato, pólizas, actas, etc.)

---

## 4. Modelo de Datos — Contrato y Entidades Relacionadas

### 4.1 Contrato (GC_CONTRATO)

Entidad que **solo se crea cuando el proceso de contratación se completa exitosamente**. Hereda datos de la oportunidad y del proceso de contratación, y se complementa con información contractual formal.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long PK | Identificador interno autogenerado |
| proceso_contratacion_id | Long FK | Referencia al proceso de contratación que lo originó |
| oportunidad_id | Long FK | Referencia a la oportunidad comercial original |
| empresa_id | Long FK | Empresa cliente (heredada de la oportunidad) |
| empresa_facturacion_id | Long FK null | Empresa a la que se factura (filial). Si es null, se factura a empresa_id |
| tipo_contrato_id | Long FK | Tipo: Contrato Formal, Orden de Compra, Orden de Servicio, etc. |
| numero_contrato_interno | VARCHAR 50 | Número interno de contrato en Arquitecsoft |
| numero_contrato_cliente | VARCHAR 100 | Número de contrato asignado por el cliente |
| objeto | VARCHAR 1000 | Objeto del contrato según documento formal |
| moneda | VARCHAR 3 | Moneda del contrato: COP, USD, EUR |
| valor_contrato | DECIMAL 16,2 | Valor base del contrato |
| valor_ajuste | DECIMAL 16,2 | Valor de adiciones/modificaciones acumuladas (inicia en 0) |
| fecha_inicio | DATE | Fecha de inicio del contrato |
| fecha_fin | DATE | Fecha de finalización del contrato |
| estado | ENUM | VIGENTE, SUSPENDIDO, TERMINADO, LIQUIDADO |
| responsable_gestion | VARCHAR 100 | Responsable interno de gestionar el contrato en ejecución |
| interventor | VARCHAR 200 | Interventor designado por el cliente |
| observaciones | VARCHAR 2000 | Observaciones generales del contrato |

> **Nota:** El contrato ya NO tiene pipeline_id ni etapa_id. Esos campos pertenecen al proceso de contratación. El contrato nace directamente en estado **VIGENTE** porque el pipeline ya se completó.

### 4.2 Forma de Pago (GC_CONTRATO_FORMA_PAGO)

Define cómo se estructura el pago del contrato. Cada forma de pago genera una línea en el flujo de facturación futuro.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long PK | Identificador autogenerado |
| contrato_id | Long FK | Contrato al que pertenece |
| descripcion | VARCHAR 500 | Descripción de la forma de pago (ej: Hito 1 - Diseño, Mensualidad Marzo) |
| valor | DECIMAL 16,2 | Valor de esta forma de pago |
| fecha_estimada_pago | DATE | Fecha estimada en que se debe facturar/cobrar |
| estado | ENUM | PENDIENTE, FACTURADA, PARCIAL, CANCELADA |
| factura_id | Long FK null | Referencia a la factura cuando se cruza |
| empresa_facturacion_id | Long FK null | Empresa a facturar (si es diferente al contrato) |

### 4.3 Modificación de Contrato (GC_CONTRATO_MODIFICACION)

Registra adiciones, suspensiones, prórrogas y otras modificaciones al contrato original.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long PK | Identificador |
| contrato_id | Long FK | Contrato modificado |
| tipo_modificacion | ENUM | ADICION, PRORROGA, SUSPENSION, REINICIO, OTRO |
| modifica_tiempo | BOOLEAN | Indica si la modificación afecta el plazo |
| modifica_valor | BOOLEAN | Indica si la modificación afecta el valor |
| valor_modificacion | DECIMAL 16,2 | Monto de la modificación (puede ser positivo o negativo) |
| nueva_fecha_fin | DATE null | Nueva fecha de finalización si aplica |
| fecha_modificacion | DATE | Fecha en que se formalizó la modificación |
| observaciones | VARCHAR 1000 | Detalle de la modificación |

### 4.4 Tipo de Contrato (GC_TIPO_CONTRATO)

Catálogo dinámico de tipos de documento contractual, alimentado desde la base de datos.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long PK | Identificador |
| nombre | VARCHAR 50 | Nombre del tipo (Contrato Formal, Orden de Compra, Orden de Servicio, etc.) |
| descripcion | VARCHAR 200 | Descripción |
| activo | BOOLEAN | Si el tipo está activo para nuevos contratos |

---

## 5. Relaciones Clave

### 5.1 Oportunidad → Proceso de Contratación → Contrato

```
Oportunidad (GANADA)
    └── Proceso de Contratación (pipeline jurídico/administrativo)
            ├── Actividades, Compromisos, Documentos
            └── Al completarse → Contrato (VIGENTE)
                                    ├── Formas de Pago
                                    ├── Modificaciones
                                    └── Documentos
```

- Una oportunidad GANADA genera **un proceso de contratación**.
- Un proceso de contratación completado genera **uno o más contratos** (ej: un contrato por cada filial).
- El contrato hereda: empresa, valor, moneda, tipo de servicio.
- El contrato complementa: número, tipo de documento, fechas, interventor, formas de pago.

### 5.2 Empresa Matriz → Filiales (Facturación)

- El contrato puede ser adjudicado por una empresa (ej: Promigas) pero la facturación va a sus filiales (ej: Gases de Occidente, Surtigas).
- El campo `empresa_facturacion_id` en el contrato y en cada forma de pago permite esta diferenciación.
- El seguimiento contractual es con la empresa del contrato; la facturación es con la empresa de facturación.

### 5.3 Empresa → Cliente

- Una empresa se convierte en **CLIENTE** cuando tiene al menos un contrato vigente.
- Esto no requiere un campo adicional: se determina dinámicamente por la existencia de contratos.

### 5.4 Contrato → Formas de Pago → Facturación

- Cada contrato tiene N formas de pago con fechas estimadas.
- Las formas de pago PENDIENTES alimentan el Mundo 3 (Flujo de Facturación).
- Cuando se cruza una forma de pago con una factura, alimenta el Mundo 4 (Facturación).

---

## 6. Pipeline de Contratación

### 6.1 Características

- Reutiliza la misma infraestructura de pipelines dinámicos que ya existe (GC_PIPELINE + GC_ETAPA).
- Se pueden crear múltiples pipelines de contratación según el tipo de cliente o contrato.
- Un pipeline de contratación se diferencia de uno comercial por el campo **ámbito** del pipeline (`COMERCIAL` vs `CONTRATACION`).
- El proceso de contratación avanza por las etapas del pipeline.
- **Al completar la última etapa**, el proceso cambia a COMPLETADO y se habilita la creación del contrato.

### 6.2 Ejemplo de Pipeline de Contratación

| # | Etapa | Descripción |
|---|-------|-------------|
| 1 | Revisión Jurídica | Revisión de términos legales, pólizas, cláusulas |
| 2 | Negociación de Términos | Ajustes a condiciones, SLAs, penalizaciones |
| 3 | Firma del Contrato | Proceso de firmas por ambas partes |
| 4 | Legalización | Pólizas, garantías, registro |
| 5 | Acta de Inicio | Formalización del inicio de ejecución → Se genera el contrato |

### 6.3 Flujo Visual

```
OPORTUNIDAD GANADA
       │
       ▼
┌─────────────────────────────────────────────┐
│  PIPELINE DE CONTRATACIÓN                    │
│                                              │
│  [Rev. Jurídica] → [Neg. Términos] →        │
│  [Firma] → [Legalización] → [Acta Inicio]   │
│                                              │
│  Durante el pipeline:                        │
│  • Actividades y compromisos                 │
│  • Documentos (borradores, pólizas)          │
│  • Seguimiento de tareas pendientes          │
└──────────────────────┬──────────────────────┘
                       │ Pipeline COMPLETADO
                       ▼
              ┌─────────────────┐
              │    CONTRATO     │
              │   (VIGENTE)     │
              │                 │
              │  • Formas pago  │
              │  • Ejecución    │
              │  • Facturación  │
              └─────────────────┘
```

---

## 7. Estados del Contrato

El contrato **nace en estado VIGENTE** porque solo se crea cuando el pipeline de contratación se completó exitosamente.

| Estado | Descripción |
|--------|-------------|
| **VIGENTE** | El contrato está activo y en ejecución. Se pueden facturar las formas de pago. Estado inicial y operativo principal. |
| **SUSPENDIDO** | El contrato está temporalmente detenido. No se puede facturar hasta que se reinicie. |
| **TERMINADO** | El contrato finalizó su ejecución (por vencimiento o terminación anticipada). |
| **LIQUIDADO** | El contrato fue liquidado formalmente. Estado final, no se puede modificar. |

**Transiciones válidas:** VIGENTE → SUSPENDIDO ↔ VIGENTE → TERMINADO → LIQUIDADO

---

## 8. Reglas de Negocio

### 8.1 Proceso de Contratación

- **RN-01:** Un proceso de contratación solo se puede crear a partir de una oportunidad con estado GANADA.
- **RN-02:** Solo puede existir un proceso de contratación activo (EN_CURSO) por oportunidad.
- **RN-03:** Si el proceso se cancela, la oportunidad puede volver a gestionarse o quedar como NO_CONCRETADA.
- **RN-04:** Durante el proceso se pueden registrar actividades, compromisos y documentos.

### 8.2 Creación del Contrato

- **RN-05:** Un contrato solo se puede crear cuando el proceso de contratación está en estado COMPLETADO.
- **RN-06:** Los datos de empresa, valor, moneda y tipo de servicio se heredan de la oportunidad.
- **RN-07:** Un proceso completado puede generar uno o más contratos (ej: un contrato por cada filial).
- **RN-08:** El contrato nace en estado VIGENTE.

### 8.3 Formas de Pago

- **RN-09:** La suma de las formas de pago no puede exceder el valor total del contrato (base + ajustes).
- **RN-10:** Una forma de pago puede facturarse a una empresa diferente a la del contrato (filiales).
- **RN-11:** Solo se pueden facturar formas de pago de contratos en estado VIGENTE.

### 8.4 Modificaciones

- **RN-12:** Las adiciones incrementan el valor_ajuste del contrato.
- **RN-13:** Las prórrogas modifican la fecha_fin del contrato.
- **RN-14:** Solo se pueden modificar contratos en estado VIGENTE.

### 8.5 Multidivisa

- **RN-15:** Un contrato tiene una moneda definida (COP, USD, EUR).
- **RN-16:** Para análisis y KPIs, los valores en otras monedas se convierten a la moneda de visualización usando tasa de cambio configurable.

---

## 9. Compatibilidad con Modelo Existente

Mapeo entre las tablas del SQL existente y las nuevas entidades:

| Tabla Existente | Nueva Entidad | Observaciones |
|----------------|---------------|---------------|
| CONTRATO | GC_CONTRATO | Se agrega oportunidad_id, proceso_contratacion_id, empresa_facturacion_id, interventor |
| CONTRATO_FORMA_PAGO | GC_CONTRATO_FORMA_PAGO | Se agrega estado, empresa_facturacion_id |
| MODI_CONTRATO | GC_CONTRATO_MODIFICACION | Se agrega nueva_fecha_fin, observaciones |
| (nuevo) | GC_PROCESO_CONTRATACION | Entidad nueva que conecta oportunidad con pipeline de contratación |
| FACLIENTE | GC_EMPRESA | Ya migrado al modelo GestCom con catálogos |
| FAFACTURA | (Mundo 4) | Se integrará en la fase de Facturación |
| FACTCONT | (Relación) | Se reemplaza por factura_id en forma de pago |
| AGENTE / CALC_GESTION | (Fuera de alcance) | Gestión de comisiones de agentes, se evaluará después |

---

## 10. KPIs del Proceso de Contratación

### 10.1 KPIs en Tiempo Real

- Procesos de contratación en curso (EN_CURSO) con valor total de las oportunidades.
- Contratos vigentes con valor total y por moneda.
- Valor pendiente por facturar (formas de pago PENDIENTES de contratos VIGENTES).
- Contratos próximos a vencer (fecha_fin en los próximos 30/60/90 días).
- Formas de pago vencidas (fecha estimada pasada y estado PENDIENTE).
- Tiempo promedio en pipeline de contratación (ganada → contrato vigente).

### 10.2 KPIs de Cierre Mensual

- Nuevos contratos formalizados en el mes (cantidad y valor).
- Contratos terminados/liquidados en el mes.
- Valor facturado vs valor pendiente por contrato.
- Procesos de contratación cancelados (oportunidades que se ganaron pero no se concretaron).

---

## 11. Alcance de Implementación

### 11.1 Fase 1: Estructura Base 🔲

- [ ] Crear entidad GC_PROCESO_CONTRATACION (entity, repository, service, controller, DTOs).
- [ ] Crear entidades GC_CONTRATO, GC_CONTRATO_FORMA_PAGO, GC_CONTRATO_MODIFICACION, GC_TIPO_CONTRATO.
- [ ] Crear tablas en Oracle con campos de auditoría.
- [ ] Agregar campo ámbito al pipeline (COMERCIAL / CONTRATACION) para diferenciar pipelines.
- [ ] Endpoint para iniciar proceso de contratación desde oportunidad GANADA.
- [ ] Endpoint para avanzar proceso por etapas del pipeline.
- [ ] Endpoint para completar proceso y generar contrato (hereda datos de oportunidad).
- [ ] Endpoints CRUD: contratos, formas de pago, modificaciones.

### 11.2 Fase 2: Frontend 🔲

- [ ] En detalle de oportunidad GANADA: sección "Proceso de Contratación" con pipeline y etapas.
- [ ] Actividades, compromisos y documentos dentro del proceso de contratación.
- [ ] Al completar pipeline: formulario para complementar datos del contrato (número, tipo, fechas, interventor).
- [ ] Vista de lista de contratos con filtros (estado, empresa, moneda).
- [ ] Detalle de contrato con: datos generales, formas de pago, modificaciones, documentos.
- [ ] Dashboard de gestión contractual con KPIs.

### 11.3 Fase 3: Integración 🔲

- [ ] Conectar formas de pago con el flujo de facturación (Mundo 3).
- [ ] Migrar datos del modelo existente (CONTRATO, CONTRATO_FORMA_PAGO, FACLIENTE).
- [ ] Cierres mensuales con KPIs comparables.

---

## Control de Cambios

| Versión | Fecha | Cambio | Autor |
|---------|-------|--------|-------|
| 1.0 | Marzo 2026 | Documento inicial | Arquitecsoft |
| 1.1 | Marzo 2026 | Se aclara que el contrato NO se genera inmediatamente al ganar la oportunidad. Se agrega entidad GC_PROCESO_CONTRATACION como intermediaria. El pipeline de contratación debe completarse antes de crear el contrato. El contrato nace en estado VIGENTE. | Arquitecsoft |

---

*— Fin del Documento —*
