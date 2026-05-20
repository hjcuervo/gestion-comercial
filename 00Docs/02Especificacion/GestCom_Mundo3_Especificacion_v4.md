# GestCom — Mundo 3: Flujo de Facturación
## Especificación Funcional v4

> **Cambios respecto a v3:** ver §0 (Bitácora de cambios) al final del documento. Esta v4 cierra **DT-07** reconciliando spec ↔ código.

---

## 1. Visión General

El Mundo 3 cubre el ciclo desde que un contrato VIGENTE define sus **formas de pago** (cuotas o hitos a facturar) hasta que esas formas de pago quedan **cumplidas** mediante facturas reales emitidas por FACTRO.

**GestCom no emite facturas.** Las facturas las emite FACTRO contra la DIAN. GestCom solo:
1. Presupuesta las formas de pago al crear el contrato.
2. Acompaña su **gestión** (validar servicio, obtener soporte, solicitar emisión).
3. Registra las facturas que FACTRO emite.
4. **Cruza** factura ↔ forma de pago para cerrar el ciclo.
5. Consolida KPIs y exporta el Excel EMI con conversión TRM (USD → COP).

> **Nota de terminología.** A nivel funcional, de UI y de documentación, hablamos siempre de **"Forma de Pago"**. A nivel de código, la entidad correspondiente se llama `GcCompromisoIngreso` (renombre interno hecho durante el rediseño Mundo 3). Ambos términos refieren al mismo concepto; esta spec usa exclusivamente "Forma de Pago".

---

## 2. Conceptos Clave

### 2.1 Tres montos por forma de pago

Una forma de pago lleva tres valores monetarios, no uno:

| Campo | Inmutable | Significado |
|---|---|---|
| `monto_presupuestado` | ✅ Sí | Lo que se acordó originalmente al firmar el contrato. Histórico, nunca cambia. |
| `monto_esperado_actual` | ❌ No | Lo que hoy esperamos facturar. Cambia con eventos `MONTO_AJUSTADO` o `NOVEDAD_CAMBIO_VALOR`. |
| `monto_facturado_acumulado` | Calculado | Suma de las aplicaciones de factura vigentes (no revertidas). Se recalcula automáticamente. |

**Por qué tres.** En la operación real, lo presupuestado al firmar no siempre coincide con lo finalmente facturable (alcances que se reducen, condiciones que cambian). Conservar los tres permite reportar tanto **desviación** (esperado vs presupuestado) como **cumplimiento** (facturado vs esperado).

### 2.2 TRM Mensual

Tasa Representativa del Mercado (USD → COP), una entrada por par de monedas por año-mes. Se usa para consolidar KPIs y el Excel EMI cuando hay formas de pago en USD.

**Regla de búsqueda:** si no hay TRM para el mes consultado, se usa la del mes anterior más reciente disponible (ver RN-11).

### 2.3 Ciclo de vida de una Forma de Pago — 7 estados

A diferencia del modelo simple PENDIENTE/FACTURADA de la v3, hoy la forma de pago atraviesa **siete estados** con una máquina de transiciones explícita:

```
                  ┌──────────────────────────────────────────┐
                  ▼                                          │
        PENDIENTE_GESTION                                    │
                  │                                          │
                  │ inicia gestión                           │
                  ▼                                          │
            EN_GESTION ◄────────────────┐                    │
                  │                     │                    │
                  ├─► COMPROMETIDO ─────┼─► CUMPLIDO ●       │
                  │      │              │                    │
                  │      ▼              │                    │
                  ├─► PARCIALMENTE ─────┤                    │
                  │      _CUMPLIDO      │                    │
                  │      │              │                    │
                  │      ▼              │                    │
                  ├─► REPROGRAMADO ─────┘                    │
                  │                                          │
                  └─► NO_LOGRADO ●  ───── (reactivar) ───────┘
                                                              
            ● = estado final (sin salidas directas, salvo
                NO_LOGRADO que puede reactivarse a EN_GESTION)
```

**Significado:**

| Estado | Significado |
|---|---|
| `PENDIENTE_GESTION` | Recién creada al formalizar contrato. Aún nadie la trabaja. |
| `EN_GESTION` | Hay gestiones registradas (validación, soporte, etc.). |
| `COMPROMETIDO` | El cliente confirmó que pagará en una fecha/monto definido. |
| `PARCIALMENTE_CUMPLIDO` | Hay factura(s) aplicada(s), pero el acumulado < esperado. Sigue activa. |
| `REPROGRAMADO` | Se aplazó la fecha esperada. No es novedad menor — implica reagendamiento formal. |
| `CUMPLIDO` ● | Acumulado facturado cubre el esperado. Cierre exitoso. |
| `NO_LOGRADO` ● | Se decidió no facturar (incobrable, alcance cancelado). Puede reactivarse. |

**Estados finales (●):** `CUMPLIDO` y `NO_LOGRADO`. Cualquier intento de transición desde ellos requiere reactivación explícita (`COMPROMISO_REACTIVADO`).

### 2.4 Eventos vs Gestiones (dos bitácoras distintas)

Cada forma de pago tiene **dos historias paralelas**:

**Eventos** (`GcCompromisoEvento`) — bitácora **sistémica**, generada por el código:
- `COMPROMISO_CREADO`, `GESTION_INICIADA`, `GESTION_ACTUALIZADA`, `COMPROMISO_CONFIRMADO`, `FACTURA_REGISTRADA`, `FACTURA_REVERTIDA`, `FECHA_REPROGRAMADA`, `MONTO_AJUSTADO`, `COMPROMISO_PERDIDO`, `COMPROMISO_REACTIVADO`, `INACTIVIDAD_DETECTADA`, `DESVIACION_DETECTADA`.
- Inmutables. Llevan `estadoAnterior` / `estadoNuevo` y payload estructurado (`monto_anterior`, `monto_nuevo`, `fecha_anterior`, `fecha_nueva`, etc.).

**Gestiones** (`GcCompromisoGestion`) — bitácora **humana**, capturada por el usuario:
- `VALIDACION_SERVICIO`, `SOPORTE_OBTENIDO`, `SOLICITUD_EMISION`, `FACTURA_CRUZADA`, `NOVEDAD_APLAZAMIENTO`, `NOVEDAD_CAMBIO_VALOR`, `NOVEDAD_SERVICIO_INCOMPLETO`, `NOVEDAD_SUSPENSION`, `NOVEDAD_OTRA`, `OBSERVACION`.
- Inmutables (RN-09).

**Diferencia clave:** un evento `FACTURA_REGISTRADA` se emite automáticamente cuando el usuario cruza una factura; al mismo tiempo se crea una gestión `FACTURA_CRUZADA` para que la persona vea la trazabilidad en la UI.

### 2.5 Tipos de Forma de Pago

`tipo` ∈ { `NUEVO`, `RECURRENTE` }. Si es recurrente, `reemplaza_a_id` apunta a la forma de pago del ciclo anterior (relevante para servicios mensuales o trimestrales del mismo contrato).

---

## 3. Modelo de Datos

### 3.1 GC_TRM

Tasa Representativa del Mercado mensual.

| Campo | Tipo | Null | Descripción |
|---|---|---|---|
| id | NUMBER PK | No | Auto |
| moneda_origen | VARCHAR2(3) | No | `USD` |
| moneda_destino | VARCHAR2(3) | No | `COP` |
| anio | NUMBER(4) | No | Año |
| mes | NUMBER(2) | No | Mes |
| valor | NUMBER(16,4) | No | Tasa |
| creado_por | NUMBER FK | No | Usuario |
| fecha_creacion | TIMESTAMP | No | Auto |
| UNIQUE | (moneda_origen, moneda_destino, anio, mes) |

### 3.2 GC_FACTURA

Factura emitida por FACTRO y registrada en GestCom. **Sin enum de estado** — es un registro plano; su "estado" se infiere de si tiene aplicaciones vigentes o no.

| Campo | Tipo | Null | Descripción |
|---|---|---|---|
| id | NUMBER PK | No | Auto |
| empresa_id | NUMBER FK | No | Empresa cliente |
| empresa_facturacion_id | NUMBER FK | Sí | Filial que factura (si aplica) |
| numero_factura | VARCHAR2(50) | No | Asignado por FACTRO |
| prefijo | VARCHAR2(10) | Sí | Prefijo DIAN |
| fecha_emision | DATE | No | |
| valor_total | NUMBER(16,2) | No | |
| moneda | VARCHAR2(3) | No | |
| anulada | BOOLEAN | No | Default false |
| fecha_anulacion | TIMESTAMP | Sí | |
| motivo_anulacion | VARCHAR2(500) | Sí | |
| creado_por, modificado_por, fecha_creacion, fecha_modificacion | (auditoría estándar) |
| UNIQUE | (numero_factura, prefijo) |

### 3.3 GC_COMPROMISO_INGRESO (= Forma de Pago)

| Campo | Tipo | Null | Descripción |
|---|---|---|---|
| id | NUMBER PK | No | Auto |
| contrato_id | NUMBER FK | No | Contrato origen |
| descripcion | VARCHAR2(500) | No | Texto libre |
| monto_presupuestado | NUMBER(16,2) | No | Inmutable (`updatable = false`) |
| monto_esperado_actual | NUMBER(16,2) | No | Ajustable |
| monto_facturado_acumulado | NUMBER(16,2) | No | Calculado, default 0 |
| fecha_esperada_original | DATE | No | Inmutable |
| fecha_esperada_actual | DATE | No | Ajustable |
| estado | VARCHAR2(25) | No | Enum `EstadoCompromiso` |
| tipo | VARCHAR2(20) | No | `NUEVO` / `RECURRENTE` |
| en_riesgo | BOOLEAN | No | Flag derivado por reglas (inactividad, desviación) |
| fecha_ultima_actividad | TIMESTAMP | Sí | Última gestión registrada |
| motivo_perdida | VARCHAR2(500) | Sí | Solo si `NO_LOGRADO` |
| reemplaza_a_id | NUMBER FK | Sí | Forma de pago anterior si `RECURRENTE` |
| empresa_facturacion_id | NUMBER FK | Sí | Filial que facturará |
| moneda | VARCHAR2(3) | No | |
| (auditoría estándar) | | | |

**CHECK constraint `estado`:** `IN ('PENDIENTE_GESTION','EN_GESTION','COMPROMETIDO','PARCIALMENTE_CUMPLIDO','CUMPLIDO','REPROGRAMADO','NO_LOGRADO')`.

### 3.4 GC_COMPROMISO_FACTURA (entidad puente, N:M)

> **Cambio clave v4 sobre v3:** la relación es **N:M**, no 1:1. Una factura puede aplicarse contra varios compromisos (consolidada) y un compromiso puede recibir varias facturas (parciales). En la operación de Arquitecsoft, **lo normal es 1:1; los casos N:M existen pero son excepción**. La N:M cubre ambos sin imponer límites.

| Campo | Tipo | Null | Descripción |
|---|---|---|---|
| id | NUMBER PK | No | Auto |
| compromiso_id | NUMBER FK | No | → GC_COMPROMISO_INGRESO |
| factura_id | NUMBER FK | No | → GC_FACTURA |
| monto_aplicado | NUMBER(16,2) | No | Cuánto de esa factura se imputa a este compromiso |
| revertida | BOOLEAN | No | Default false |
| fecha_reversion | TIMESTAMP | Sí | |
| motivo_reversion | VARCHAR2(500) | Sí | |
| creado_por | NUMBER FK | No | Usuario |
| fecha_creacion | TIMESTAMP | No | Auto |
| UNIQUE | (compromiso_id, factura_id) — no permite duplicar la misma aplicación |

**Reglas estructurales:**
- `monto_aplicado > 0`.
- **Suma de `monto_aplicado` vigente** (donde `revertida = false`) **por factura ≤ `valor_total` de la factura**. Validación a cargo del servicio.
- Una aplicación **nunca se elimina**; se marca `revertida = true` para preservar trazabilidad (caso 3.8 del flujo legacy).

### 3.5 GC_COMPROMISO_EVENTO (event sourcing ligero)

Bitácora sistémica de todo lo que le pasa a una forma de pago. **No existía en v3.**

| Campo | Tipo | Null | Descripción |
|---|---|---|---|
| id | NUMBER PK | No | |
| compromiso_id | NUMBER FK | No | Inmutable |
| tipo_evento | VARCHAR2(30) | No | Enum `TipoEvento` (12 valores, ver §2.4) |
| estado_anterior | VARCHAR2(25) | Sí | Null para `COMPROMISO_CREADO` |
| estado_nuevo | VARCHAR2(25) | Sí | Null para eventos que no cambian estado |
| monto_anterior | NUMBER(16,2) | Sí | |
| monto_nuevo | NUMBER(16,2) | Sí | |
| fecha_anterior | DATE | Sí | |
| fecha_nueva | DATE | Sí | |
| compromiso_factura_id | NUMBER | Sí | FK lógica a `GC_COMPROMISO_FACTURA` (no constraint, para no bloquear borrados teóricos) |
| motivo | VARCHAR2(1000) | Sí | |
| payload_json | VARCHAR2(2000) | Sí | Datos extra del evento |
| usuario_id | NUMBER FK | Sí | Quien gatilló el evento |
| fecha_evento | TIMESTAMP | No | Auto |

**Todas las columnas son `updatable = false`.** Un evento, una vez creado, nunca se modifica.

### 3.6 GC_COMPROMISO_GESTION (bitácora humana)

Equivalente al `GC_FORMA_PAGO_GESTION` de la v3, ampliado.

| Campo | Tipo | Null | Descripción |
|---|---|---|---|
| id | NUMBER PK | No | |
| compromiso_id | NUMBER FK | No | |
| tipo_gestion | VARCHAR2(30) | No | Enum `TipoGestion` (10 valores) |
| descripcion | VARCHAR2(2000) | Sí | |
| creado_por | NUMBER FK | No | |
| fecha_creacion | TIMESTAMP | No | Inmutable (RN-09) |

### 3.7 Diagrama de relaciones

```
GC_TRM   (independiente)
GC_USUARIO (referenciado por auditoría)

GC_CONTRATO
   └── GC_COMPROMISO_INGRESO  (1:N)
            ├── GC_COMPROMISO_FACTURA   (N:M con GC_FACTURA)
            │                                │
            │                                └── GC_FACTURA (1:N hacia GC_COMPROMISO_FACTURA)
            ├── GC_COMPROMISO_EVENTO    (1:N — bitácora sistémica)
            └── GC_COMPROMISO_GESTION   (1:N — bitácora humana)
```

---

## 4. Vistas y Funcionalidades

> **Nota:** el detalle de vistas v3 §4 (Tablero, Panel de Gestión, Cruce, etc.) sigue siendo la referencia funcional. Esta versión solo cambia lo estructural del modelo y las reglas. La revisión visual queda dentro de las fases F2/F3/F4/F5 del backlog (ver §6).

Resumen de las vistas previstas:

- **4.1 Tablero de Facturación** — KPIs del mes, pendientes del mes actual, vencidas, próximos meses, facturadas del mes.
- **4.2 Panel de Gestión de Forma de Pago** — bitácora humana + acciones (registrar gestión, ajustar monto, reprogramar, marcar no logrado).
- **4.3 Gestión de Facturas** — listado, registro de factura, anulación.
- **4.4 Cruce Factura ↔ Forma de Pago** — al cruzar: crear `GC_COMPROMISO_FACTURA` con `monto_aplicado`; emitir evento `FACTURA_REGISTRADA`; gestión `FACTURA_CRUZADA`. Al revertir: marcar `revertida = true`, recalcular acumulado, emitir `FACTURA_REVERTIDA`.
- **4.5 Configuración de TRM** — alta mensual.
- **4.6 Export Excel EMI** — 5 hojas con conversión a COP.

---

## 5. Reglas de Negocio

> **Numeración:** las reglas mantienen el código `RN-XX` de la v3 cuando aplica; las nuevas se numeran RN-14 en adelante.

### 5.1 Aplicaciones de factura

| # | Regla |
|---|---|
| **RN-01** *(reescrita)* | **Una forma de pago PUEDE recibir múltiples aplicaciones de factura.** El caso normal es 1:1 (una factura cubre completamente un compromiso). El caso N:M existe para parciales y consolidadas. |
| RN-02 | Una factura puede aplicarse contra múltiples formas de pago (consolidada). |
| **RN-03** *(reescrita)* | Al registrar una aplicación: se crea `GC_COMPROMISO_FACTURA`, se incrementa `monto_facturado_acumulado` del compromiso por `monto_aplicado`, se emite evento `FACTURA_REGISTRADA`, se crea gestión `FACTURA_CRUZADA`. El estado del compromiso evoluciona según RN-15. |
| **RN-04** *(reescrita)* | Solo se aplican facturas contra formas de pago que **no estén en estado final** (`CUMPLIDO`, `NO_LOGRADO`). |
| **RN-05** *(reescrita)* | Una aplicación NO se elimina. Para revertirla se marca `revertida = true` con `motivo_reversion`. Se descuenta del `monto_facturado_acumulado` y se emite evento `FACTURA_REVERTIDA`. |
| **RN-14** *(nueva)* | La suma de `monto_aplicado` vigente por factura no puede exceder `valor_total` de la factura. |
| **RN-15** *(nueva)* | Estado tras aplicación: si `monto_facturado_acumulado >= monto_esperado_actual` → `CUMPLIDO`; si `> 0` y `<` esperado → `PARCIALMENTE_CUMPLIDO`. |
| **RN-16** *(nueva)* | Anulación de factura: si la factura tiene aplicaciones vigentes, la anulación se bloquea con `BusinessException`. Hay que revertir todas las aplicaciones primero. |

### 5.2 Visibilidad y ciclo

| # | Regla |
|---|---|
| RN-06 | Formas de pago con `fecha_esperada_actual` del mes actual o anteriores son "activas" en el tablero. |
| RN-07 | Formas de pago con fecha futura son informativas (visibles pero sin gestión activa). |
| RN-08 | Facturas se identifican por `numero_factura + prefijo` (UNIQUE). |
| RN-09 | Entradas de bitácora (gestiones) son inmutables. |

### 5.3 Conversión y consolidación

| # | Regla |
|---|---|
| RN-10 | Conversión USD→COP usa TRM del mes de `fecha_esperada_actual`. |
| RN-11 | Si no hay TRM para ese mes, se usa la del mes anterior más reciente disponible. |
| RN-12 | `monto_facturado_acumulado` puede diferir de `monto_esperado_actual` (parciales, sobrefacturación). |
| RN-13 | KPIs y Excel EMI consolidan en COP. |

### 5.4 Estados y transiciones

| # | Regla |
|---|---|
| **RN-17** *(nueva)* | Las transiciones de estado están gobernadas por la máquina `CompromisoEstadoMachine`. Cualquier transición no autorizada lanza `BusinessException("ESTADO_INVALIDO", ...)`. |
| **RN-18** *(nueva)* | Estados finales (`CUMPLIDO`, `NO_LOGRADO`) no transicionan salvo por reactivación explícita (`COMPROMISO_REACTIVADO`), que requiere motivo. |
| **RN-19** *(nueva)* | `monto_presupuestado` y `fecha_esperada_original` son inmutables (`updatable = false`). Cualquier ajuste se hace sobre `monto_esperado_actual` o `fecha_esperada_actual` y deja evento `MONTO_AJUSTADO` o `FECHA_REPROGRAMADA`. |
| **RN-20** *(nueva)* | Marcar `NO_LOGRADO` requiere `motivo_perdida`. |
| **RN-21** *(nueva)* | Eventos son inmutables (todas las columnas con `updatable = false`). |

### 5.5 Riesgo

| # | Regla |
|---|---|
| **RN-22** *(nueva)* | El flag `en_riesgo` se setea automáticamente cuando: (a) handler de inactividad detecta > N días sin gestión (evento `INACTIVIDAD_DETECTADA`), o (b) handler de desviación detecta diferencia significativa entre esperado y acumulado parcial (evento `DESVIACION_DETECTADA`). Las constantes N y el umbral se documentan en el código del service. |

---

## 6. Fases de Implementación — estado al 19-may-2026

| Fase | Descripción | Estado |
|---|---|---|
| F1 | Modelo de datos y backend base | ✅ ~95% — modelo v4 reflejado, falta limpiar el wording legacy en logs |
| F2 | Frontend tablero de facturación | 🟡 Parcial — `FacturacionView.vue` + `GestionPanel.vue`, faltan filtros y secciones colapsables |
| F3 | Frontend facturas y cruce | 🔲 No iniciado |
| F4 | TRM y Excel EMI | 🔲 No iniciado |
| F5 | Dashboard integrado | 🔲 No iniciado |

---

## 7. Matriz de Reglas RN-XX → endpoints (anexo)

La matriz completa vive en un documento separado, para mantenerla como referencia operativa actualizable sin tocar esta spec funcional:

📄 **`GestCom_Mundo3_Matriz_RN_Endpoints.md`** (carpeta `00Docs/02Especificacion/`).

Ese documento:

- Lista los **20 endpoints reales** del Mundo 3 (15 de compromisos, 4 de facturas, 1 de vista por período), validados contra `CompromisoIngresoController`, `FacturaController` y `VistaPeriodoController` el 19-may-2026.
- Mapea cada una de las 22 reglas (`RN-01..RN-22`) al endpoint que la enforce o a la lógica de servicio responsable.
- Identifica gaps actuales: **no existe controller público para TRM** (CRUD pendiente) ni para **export Excel EMI** (RN-13 parcial). Ambos son trabajo de F4.

Esta matriz reemplaza a `B-04-Matriz-Reglas-Negocio.md` en lo que respecta al Mundo 3 (B-04 sigue siendo la referencia para Mundo 1).

---

## 8. Ejemplo de Flujo (actualizado)

```
Empresa "Cliente ABC" → Contrato VIGENTE → 12 formas de pago mensuales de USD 5.000 c/u

Mes en curso (mayo 2026):
  Forma de pago "Cuota mayo" — PENDIENTE_GESTION
    │
    ├─ Usuario registra gestión VALIDACION_SERVICIO → estado: EN_GESTION
    ├─ Usuario registra gestión SOPORTE_OBTENIDO    → estado: EN_GESTION
    ├─ Usuario registra gestión SOLICITUD_EMISION   → estado: EN_GESTION
    │
    │  FACTRO emite factura F-2026-00123 por USD 5.000
    │
    ├─ Usuario registra la factura en GestCom (GC_FACTURA)
    ├─ Usuario cruza la factura con la forma de pago
    │    └─ Sistema:
    │       • crea GC_COMPROMISO_FACTURA (monto_aplicado = 5.000)
    │       • acumulado = 5.000
    │       • acumulado >= esperado → estado: CUMPLIDO
    │       • emite evento FACTURA_REGISTRADA
    │       • crea gestión FACTURA_CRUZADA
    │
    └─ Cierre exitoso del ciclo.

Caso excepcional (parcial):
  Forma de pago "Cuota junio" esperado USD 5.000
    │
    ├─ FACTRO emite F-2026-00200 por USD 3.000 — se aplica → acumulado 3.000 → PARCIALMENTE_CUMPLIDO
    ├─ FACTRO emite F-2026-00201 por USD 2.000 — se aplica → acumulado 5.000 → CUMPLIDO
    │
    └─ Conversión a COP usa TRM de junio 2026.
```

---

## 0. Bitácora de cambios v3 → v4

| Cambio | Motivo |
|---|---|
| RN-01 reescrita: 1:1 → N:M permitida | Reconciliación con código real (DT-07). Negocio confirma que el caso N:M existe aunque es excepción. |
| Ciclo de vida 2 estados → 7 estados | El código implementa máquina de transiciones desde rediseño Mundo 3. La v3 estaba subespecificada. |
| 3 montos por forma de pago (presupuestado, esperado, acumulado) | Diferenciar histórico, expectativa actual y realidad facturada. |
| Tipos de gestión: 6 → 10 | Agregan `NOVEDAD_SERVICIO_INCOMPLETO`, `NOVEDAD_SUSPENSION`, `NOVEDAD_OTRA`, `OBSERVACION`. |
| Nueva entidad `GC_COMPROMISO_EVENTO` (event sourcing ligero) | Trazabilidad sistémica separada de la humana. Existía en código, faltaba en spec. |
| Entidad puente `GC_COMPROMISO_FACTURA` con `monto_aplicado` + `revertida` | Estructura real de la N:M, no contemplada en v3. |
| `GC_FACTURA` sin enum de estado | Spec v3 implicaba estados; el código la deja plana con flags (`anulada`). |
| Reglas RN-14..RN-22 nuevas | Capturan invariantes y guards reales del backend. |
| Tipo NUEVO / RECURRENTE + `reemplaza_a_id` | Documenta lógica de servicios mensuales/trimestrales que ya existía en código. |
| Wording: se mantiene "Forma de Pago" en toda la spec/UI/reportes | Decisión de proyecto del 19-may-2026. El código sigue usando `GcCompromisoIngreso`. |
| Matriz RN → endpoints extraída a documento separado | §7 referencia ahora `GestCom_Mundo3_Matriz_RN_Endpoints.md`, validado contra los 3 controllers reales (cierra D1.3 del plan). Versión previa de §7 contenía paths propuestos no verificados. |

---

*Spec v4 — generada el 19-may-2026. Reconcilia spec con código y cierra DT-07.*
