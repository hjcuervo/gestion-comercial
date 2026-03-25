# GestCom — Documento de Alcance: Proceso de Contratación

**Versión:** 1.0  
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

### Fase 2: Proceso de Contratación (Alcance de este documento) 🔲

1. Cuando la oportunidad se marca como GANADA, se habilita la creación de un contrato.
2. Se inicia un pipeline de contratación (jurídico/administrativo) para formalizar el contrato.
3. Se complementan los datos: número de contrato, tipo, fechas, interventor, formas de pago.
4. El contrato pasa por sus etapas hasta quedar en estado VIGENTE.
5. Se definen las formas de pago que alimentarán el flujo de facturación futuro.

> **Principio clave:** El contrato NO se crea desde una pantalla independiente. Se genera a partir de los insumos de la oportunidad ganada, complementando únicamente los datos que no existían en la negociación.

---

## 3. Modelo de Datos — Entidades Principales

### 3.1 Contrato (GC_CONTRATO)

Entidad central del proceso de contratación. Se crea a partir de una oportunidad ganada.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long PK | Identificador interno autogenerado |
| oportunidad_id | Long FK | Referencia a la oportunidad comercial que originó el contrato |
| empresa_id | Long FK | Empresa cliente (heredada de la oportunidad) |
| empresa_facturacion_id | Long FK null | Empresa a la que se factura (filial). Si es null, se factura a empresa_id |
| tipo_contrato_id | Long FK | Tipo: Contrato Formal, Orden de Compra, Orden de Servicio, etc. |
| numero_contrato_interno | VARCHAR 50 | Número interno de contrato en Arquitecsoft |
| numero_contrato_cliente | VARCHAR 100 | Número de contrato asignado por el cliente |
| objeto | VARCHAR 1000 | Objeto del contrato según documento formal |
| moneda | VARCHAR 3 | Moneda del contrato: COP, USD, EUR |
| valor_contrato | DECIMAL 16,2 | Valor base del contrato |
| valor_ajuste | DECIMAL 16,2 | Valor de adiciones/modificaciones acumuladas |
| fecha_inicio | DATE | Fecha de inicio del contrato |
| fecha_fin | DATE | Fecha de finalización del contrato |
| estado | ENUM | EN_PROCESO, VIGENTE, SUSPENDIDO, TERMINADO, LIQUIDADO |
| responsable_gestion | VARCHAR 100 | Responsable interno de gestionar el contrato |
| interventor | VARCHAR 200 | Interventor designado por el cliente |
| observaciones | VARCHAR 2000 | Observaciones generales del contrato |
| pipeline_id | Long FK null | Pipeline de contratación asignado (proceso jurídico/administrativo) |
| etapa_id | Long FK null | Etapa actual dentro del pipeline de contratación |

### 3.2 Forma de Pago (GC_CONTRATO_FORMA_PAGO)

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

### 3.3 Modificación de Contrato (GC_CONTRATO_MODIFICACION)

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

### 3.4 Tipo de Contrato (GC_TIPO_CONTRATO)

Catálogo dinámico de tipos de contrato, alimentado desde la base de datos.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long PK | Identificador |
| nombre | VARCHAR 50 | Nombre del tipo (Contrato Formal, Orden de Compra, Orden de Servicio, etc.) |
| descripcion | VARCHAR 200 | Descripción |
| activo | BOOLEAN | Si el tipo está activo para nuevos contratos |

---

## 4. Relaciones Clave

### 4.1 Oportunidad → Contrato

- Una oportunidad GANADA puede generar uno o más contratos.
- El contrato hereda: empresa, valor, moneda, tipo de servicio, contactos.
- El contrato complementa: número, tipo, fechas, interventor, formas de pago.

### 4.2 Empresa Matriz → Filiales (Facturación)

- El contrato puede ser adjudicado por una empresa (ej: Promigas) pero la facturación va a sus filiales (ej: Gases de Occidente, Surtigas).
- El campo `empresa_facturacion_id` en el contrato y en cada forma de pago permite esta diferenciación.
- El seguimiento contractual es con la empresa del contrato; la facturación es con la empresa de facturación.

### 4.3 Empresa → Cliente

- Una empresa se convierte en **CLIENTE** cuando tiene al menos un contrato vigente.
- Esto no requiere un campo adicional: se determina dinámicamente por la existencia de contratos.

### 4.4 Contrato → Formas de Pago → Facturación

- Cada contrato tiene N formas de pago con fechas estimadas.
- Las formas de pago PENDIENTES alimentan el Mundo 3 (Flujo de Facturación).
- Cuando se cruza una forma de pago con una factura, alimenta el Mundo 4 (Facturación).

---

## 5. Pipeline de Contratación

Después de que una oportunidad es adjudicada (GANADA), inician procesos jurídicos y administrativos para formalizar el contrato. Este proceso se gestiona a través de un pipeline específico de contratación.

### 5.1 Características

- Reutiliza la misma infraestructura de pipelines dinámicos que ya existe (GC_PIPELINE + GC_ETAPA).
- Se pueden crear múltiples pipelines de contratación según el tipo de cliente o contrato.
- Un pipeline de contratación se diferencia de uno comercial por el campo **ámbito** del pipeline (`COMERCIAL` vs `CONTRATACION`).
- El contrato avanza por las etapas del pipeline hasta alcanzar el estado VIGENTE.

### 5.2 Ejemplo de Pipeline de Contratación

| # | Etapa | Descripción |
|---|-------|-------------|
| 1 | Revisión Jurídica | Revisión de términos legales, pólizas, cláusulas |
| 2 | Negociación de Términos | Ajustes a condiciones, SLAs, penalizaciones |
| 3 | Firma del Contrato | Proceso de firmas por ambas partes |
| 4 | Legalización | Pólizas, garantías, registro |
| 5 | Acta de Inicio | Formalización del inicio de ejecución |

---

## 6. Estados del Contrato

| Estado | Descripción |
|--------|-------------|
| **EN_PROCESO** | El contrato está en proceso de formalización (pasando por el pipeline de contratación). Aún no se puede facturar. |
| **VIGENTE** | El contrato está activo y en ejecución. Se pueden facturar las formas de pago. Estado operativo principal. |
| **SUSPENDIDO** | El contrato está temporalmente detenido. No se puede facturar hasta que se reinicie. |
| **TERMINADO** | El contrato finalizó su ejecución (por vencimiento o terminación anticipada). |
| **LIQUIDADO** | El contrato fue liquidado formalmente. Estado final, no se puede modificar. |

**Transiciones válidas:** EN_PROCESO → VIGENTE → SUSPENDIDO ↔ VIGENTE → TERMINADO → LIQUIDADO

---

## 7. Reglas de Negocio

### 7.1 Creación del Contrato

- **RN-01:** Un contrato solo se puede crear a partir de una oportunidad con estado GANADA.
- **RN-02:** Los datos de empresa, valor, moneda y tipo de servicio se heredan de la oportunidad.
- **RN-03:** Una oportunidad puede generar múltiples contratos (ej: un contrato por cada filial).
- **RN-04:** El contrato inicia en estado EN_PROCESO.

### 7.2 Formas de Pago

- **RN-05:** La suma de las formas de pago no puede exceder el valor total del contrato (base + ajustes).
- **RN-06:** Una forma de pago puede facturarse a una empresa diferente a la del contrato (filiales).
- **RN-07:** Solo se pueden facturar formas de pago de contratos en estado VIGENTE.

### 7.3 Modificaciones

- **RN-08:** Las adiciones incrementan el valor_ajuste del contrato.
- **RN-09:** Las prórrogas modifican la fecha_fin del contrato.
- **RN-10:** Solo se pueden modificar contratos en estado VIGENTE o EN_PROCESO.

### 7.4 Multidivisa

- **RN-11:** Un contrato tiene una moneda definida (COP, USD, EUR).
- **RN-12:** Para análisis y KPIs, los valores en otras monedas se convierten a la moneda de visualización usando tasa de cambio configurable.

---

## 8. Compatibilidad con Modelo Existente

Mapeo entre las tablas del SQL existente y las nuevas entidades:

| Tabla Existente | Nueva Entidad | Observaciones |
|----------------|---------------|---------------|
| CONTRATO | GC_CONTRATO | Se agrega oportunidad_id, empresa_facturacion_id, pipeline, etapa, interventor |
| CONTRATO_FORMA_PAGO | GC_CONTRATO_FORMA_PAGO | Se agrega estado, empresa_facturacion_id |
| MODI_CONTRATO | GC_CONTRATO_MODIFICACION | Se agrega nueva_fecha_fin, observaciones |
| FACLIENTE | GC_EMPRESA | Ya migrado al modelo GestCom con catálogos |
| FAFACTURA | (Mundo 4) | Se integrará en la fase de Facturación |
| FACTCONT | (Relación) | Se reemplaza por factura_id en forma de pago |
| AGENTE / CALC_GESTION | (Fuera de alcance) | Gestión de comisiones de agentes, se evaluará después |

---

## 9. KPIs del Proceso de Contratación

### 9.1 KPIs en Tiempo Real

- Contratos en proceso de formalización (EN_PROCESO) con valor total.
- Contratos vigentes con valor total y por moneda.
- Valor pendiente por facturar (formas de pago PENDIENTES de contratos VIGENTES).
- Contratos próximos a vencer (fecha_fin en los próximos 30/60/90 días).
- Formas de pago vencidas (fecha estimada pasada y estado PENDIENTE).

### 9.2 KPIs de Cierre Mensual

- Nuevos contratos del mes (cantidad y valor).
- Contratos terminados/liquidados en el mes.
- Valor facturado vs valor pendiente por contrato.
- Tiempo promedio de formalización (oportunidad ganada → contrato vigente).

---

## 10. Alcance de Implementación

### 10.1 Fase 1: Estructura Base 🔲

- [ ] Crear entidades JPA: GC_CONTRATO, GC_CONTRATO_FORMA_PAGO, GC_CONTRATO_MODIFICACION, GC_TIPO_CONTRATO.
- [ ] Crear tablas en Oracle con campos de auditoría.
- [ ] Agregar campo ámbito al pipeline (COMERCIAL / CONTRATACION) para diferenciar pipelines.
- [ ] Endpoints CRUD: contratos, formas de pago, modificaciones.
- [ ] Endpoint para crear contrato desde oportunidad ganada (hereda datos).

### 10.2 Fase 2: Frontend 🔲

- [ ] Sección de Contratos en el detalle de oportunidad (solo para oportunidades GANADAS).
- [ ] Vista de lista de contratos con filtros (estado, empresa, moneda).
- [ ] Detalle de contrato con: datos generales, formas de pago, modificaciones, documentos.
- [ ] Kanban de contratación (pipeline de contratación con etapas).
- [ ] Dashboard de gestión contractual con KPIs.

### 10.3 Fase 3: Integración 🔲

- [ ] Conectar formas de pago con el flujo de facturación (Mundo 3).
- [ ] Migrar datos del modelo existente (CONTRATO, CONTRATO_FORMA_PAGO, FACLIENTE).
- [ ] Cierres mensuales con KPIs comparables.

---

## Control de Cambios

| Versión | Fecha | Cambio | Autor |
|---------|-------|--------|-------|
| 1.0 | Marzo 2026 | Documento inicial | Arquitecsoft |

---

*— Fin del Documento —*
