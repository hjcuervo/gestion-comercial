# GestCom — Mundo 3: Flujo de Facturación
## Especificación Funcional v3

---

## 1. Visión General

El Mundo 3 gestiona el **proceso de facturación** de los contratos vigentes con trazabilidad completa.

**GestCom NO genera facturas** — FACTRO maneja la facturación electrónica (DIAN). GestCom:

- Indica proactivamente qué se debe facturar cada mes
- Registra la gestión realizada para cada forma de pago (bitácora)
- Cruza facturas emitidas en FACTRO con las formas de pago
- Actualiza el valor real de la forma de pago con el valor facturado
- Mantiene trazabilidad: documentos, novedades, observaciones
- Genera el Excel EMI con conversión a COP para análisis consolidado
- Muestra KPIs de lo facturado vs lo pendiente

---

## 2. Conceptos Clave

### 2.1 Valor Presupuestado vs Valor Facturado

Las formas de pago nacen con un **valor estimado** (presupuesto). Cuando se cruza con una factura, se registra el **valor facturado** (real). La diferencia puede existir porque:

- Contratos con valor variable mes a mes (consumo, horas, etc.)
- Ajustes IPC aplicados
- Servicios parciales

```
Forma de Pago:
  valor (presupuestado): $10.000.000   ← lo que se planeó
  valor_facturado:       $9.500.000    ← lo que realmente se facturó
  diferencia:            -$500.000     ← se calcula automáticamente
```

### 2.2 TRM Mensual

Para consolidar análisis en COP, se mantiene una tabla de TRM mensual (USD → COP).

```
Ejemplo:
  2026-01: $4.180
  2026-02: $4.210
  2026-03: $4.195
  2026-04: $4.230  ← mes actual
```

El valor en COP de una forma de pago en USD se calcula: `valor × TRM del mes`.

### 2.3 Ciclo de vida de una Forma de Pago

```
Forma de Pago (PENDIENTE, valor presupuestado)
    │
    │ Llega su mes calendario → aparece en tablero
    │
    ├── Gestión normal:
    │   1. Validar servicio prestado
    │   2. Obtener soporte (acta, entregable)
    │   3. Registrar en bitácora
    │   4. Solicitar emisión en FACTRO
    │   5. Cruzar con factura → FACTURADA
    │      → Se actualiza valor_facturado con el valor de la factura
    │
    └── Novedad:
        → Registrar en bitácora (aplazamiento, cambio valor, etc.)
        → Sigue PENDIENTE con trazabilidad
```

---

## 3. Modelo de Datos

### 3.1 GC_TRM (nueva)

Tasa Representativa del Mercado mensual.

| Campo | Tipo | Null | Descripción |
|-------|------|------|-------------|
| id | NUMBER PK | No | Auto-generado |
| anio | NUMBER | No | Año (2026) |
| mes | NUMBER | No | Mes (1-12) |
| moneda_origen | VARCHAR2(3) | No | USD |
| moneda_destino | VARCHAR2(3) | No | COP |
| valor | NUMBER(16,4) | No | Tasa (ej: 4230.5000) |
| creado_por | NUMBER | No | Usuario |
| fecha_creacion | TIMESTAMP | No | Auto |

**Constraint**: UNIQUE(anio, mes, moneda_origen, moneda_destino)

### 3.2 GC_FACTURA (nueva)

| Campo | Tipo | Null | Descripción |
|-------|------|------|-------------|
| id | NUMBER PK | No | Auto-generado |
| empresa_id | NUMBER FK | No | Empresa cliente |
| empresa_facturacion_id | NUMBER FK | Sí | Filial que factura |
| numero_factura | VARCHAR2(50) | No | Número en FACTRO |
| prefijo | VARCHAR2(10) | Sí | Prefijo |
| fecha_emision | DATE | No | Fecha de emisión |
| fecha_vencimiento | DATE | Sí | Fecha de vencimiento |
| valor_base | NUMBER(16,2) | Sí | Valor antes de impuestos |
| valor_iva | NUMBER(16,2) | Sí | IVA |
| valor_total | NUMBER(16,2) | No | Total facturado |
| moneda | VARCHAR2(3) | No | COP, USD |
| factro_id | VARCHAR2(50) | Sí | ID interno de FACTRO |
| observaciones | VARCHAR2(500) | Sí | Notas |
| creado_por | NUMBER | No | Usuario |
| fecha_creacion | TIMESTAMP | No | Auto |

### 3.3 GC_FORMA_PAGO_GESTION (nueva)

Bitácora de gestión de cada forma de pago.

| Campo | Tipo | Null | Descripción |
|-------|------|------|-------------|
| id | NUMBER PK | No | Auto-generado |
| forma_pago_id | NUMBER FK | No | FK a GC_CONTRATO_FORMA_PAGO |
| tipo_gestion | VARCHAR2(30) | No | Tipo de entrada |
| descripcion | VARCHAR2(1000) | No | Detalle |
| fecha_gestion | DATE | No | Fecha de la gestión |
| creado_por | NUMBER | No | Usuario |
| fecha_creacion | TIMESTAMP | No | Auto |

**Tipos de gestión**:

| Código | Descripción |
|--------|-------------|
| VALIDACION_SERVICIO | Se validó que el servicio fue prestado |
| SOPORTE_OBTENIDO | Se obtuvo acta/entregable/aprobación |
| SOLICITUD_EMISION | Se solicitó emisión en FACTRO |
| FACTURA_CRUZADA | Se cruzó con factura (automático) |
| NOVEDAD_APLAZAMIENTO | Cliente solicitó aplazar |
| NOVEDAD_CAMBIO_VALOR | El valor cambió |
| NOVEDAD_SERVICIO_INCOMPLETO | Servicio no prestado completo |
| NOVEDAD_SUSPENSION | Contrato suspendido |
| NOVEDAD_OTRA | Otra novedad |
| OBSERVACION | Observación general |

### 3.4 Modificación a GC_CONTRATO_FORMA_PAGO (existente)

| Campo | Cambio | Descripción |
|-------|--------|-------------|
| factura_id | Ya existe | FK a GC_FACTURA, se actualiza al cruzar |
| estado | Ya existe | PENDIENTE → FACTURADA |
| valor_facturado | **NUEVO** | NUMBER(16,2), valor real de la factura cruzada |

### 3.5 Relaciones

```
GC_TRM (tabla independiente de tasas de cambio)

GC_CONTRATO
    └── GC_CONTRATO_FORMA_PAGO (1:N)
            ├── valor              → presupuestado
            ├── valor_facturado    → real (se actualiza al cruzar)
            ├── factura_id FK      → GC_FACTURA (N:1)
            ├── GC_FORMA_PAGO_GESTION (1:N) → bitácora
            └── GC_DOCUMENTO (1:N) → documentos soporte

GC_FACTURA
    ├── empresa_id FK
    ├── empresa_facturacion_id FK
    └── formas_pago cruzadas (1:N via factura_id en GC_CONTRATO_FORMA_PAGO)
```

---

## 4. Vistas y Funcionalidades

### 4.1 Tablero de Facturación (vista principal)

**Ruta**: `/facturacion`

#### a) KPIs superiores

| KPI | Cálculo |
|-----|---------|
| Pendientes este mes | Formas de pago PENDIENTE cuyo año-mes = mes actual (cantidad + valor en COP) |
| Vencidas (meses anteriores) | Formas de pago PENDIENTE cuyo año-mes < mes actual (cantidad + valor en COP) |
| Facturado este mes | Formas de pago FACTURADA cuyo cruce fue este mes (cantidad + valor_facturado en COP) |
| Pendiente total | Todas las formas de pago PENDIENTE (valor en COP) |
| Facturado acumulado año | Todas las FACTURADA del año actual (valor_facturado en COP) |

**Nota**: Los valores en USD se convierten a COP usando la TRM del mes correspondiente.

#### b) Pendientes del mes actual (sección principal)

Lista de formas de pago cuyo año-mes = mes actual y estado = PENDIENTE.

Cada ítem muestra:
- Empresa (razón social)
- Contrato (número interno + nombre)
- Forma de pago (descripción)
- Valor presupuestado (en moneda original + equivalente COP)
- Indicador de gestión: ícono que muestra si tiene entradas de bitácora o está sin tocar
- Última gestión: fecha y tipo de la última entrada de bitácora
- Click → abre panel de gestión

#### c) Vencidas (meses anteriores)

Misma estructura que (b) pero resaltadas en rojo/naranja. Agrupadas por mes (más antiguo primero).

#### d) Próximos meses (colapsable)

Formas de pago de los próximos 3 meses, agrupadas por mes. Solo informativo.

#### e) Facturadas del mes (colapsable)

Formas de pago que ya se cruzaron con factura este mes. Muestra valor presupuestado vs valor facturado.

### 4.2 Panel de Gestión de Forma de Pago

**Modal o panel lateral desde el tablero.**

| Sección | Contenido |
|---------|-----------|
| Encabezado | Contrato, empresa, forma de pago, valor, fecha, estado |
| Bitácora | Lista cronológica de gestiones + botón "Registrar gestión" |
| Documentos | Documentos soporte (reutiliza DocumentoModal) |
| Factura | Si ya está cruzada: datos de la factura. Si no: botón "Cruzar con factura" |
| Valor facturado | Si está cruzada: valor presupuestado vs facturado, diferencia |

### 4.3 Gestión de Facturas

**Ruta**: `/facturacion/facturas`

- Lista de facturas con filtros (fecha, empresa, moneda)
- Cada factura muestra: número, prefijo, fecha, empresa, valor, formas de pago cruzadas
- Modal para registrar factura manualmente
- Desde la factura se puede cruzar con formas de pago pendientes

### 4.4 Cruce Factura ↔ Forma de Pago

**Al cruzar**:
1. `forma_pago.factura_id` = factura.id
2. `forma_pago.estado` = FACTURADA
3. `forma_pago.valor_facturado` = valor que viene de la factura (puede ser diferente al presupuestado)
4. Se registra entrada automática en bitácora: `FACTURA_CRUZADA`

**Al descruzar** (corregir error):
1. `forma_pago.factura_id` = null
2. `forma_pago.estado` = PENDIENTE
3. `forma_pago.valor_facturado` = null

### 4.5 Configuración de TRM

**Ruta**: `/facturacion/trm` (o sección dentro de facturación)

- Tabla editable: año, mes, valor TRM
- Se puede cargar manualmente mes a mes
- Si no hay TRM para un mes, usa la del mes anterior más reciente

### 4.6 Exportar Excel EMI

**Botón en el tablero**: "Exportar EMI"

Genera Excel con 4 hojas (misma estructura actual):

**Hoja "Tabla COP"**: formas de pago PENDIENTE en COP
**Hoja "Tabla USD"**: formas de pago PENDIENTE en USD (valor original)
**Hoja "COL"**: pivot de Tabla COP por año × mes
**Hoja "PERU"**: pivot de Tabla USD por año × mes

**Columnas adicionales vs el Excel actual**:
- Valor COP equivalente (para USD, usando TRM del mes)
- Estado de gestión (tiene gestión registrada o no)
- Última novedad (si la hay)

**Hoja nueva "Resumen Facturado"**:
- Formas de pago ya FACTURADAS con valor presupuestado y valor facturado
- Diferencias

---

## 5. Reglas de Negocio

| # | Regla |
|---|-------|
| RN-01 | Una forma de pago solo puede cruzarse con UNA factura |
| RN-02 | Una factura puede cruzarse con MÚLTIPLES formas de pago |
| RN-03 | Al cruzar: estado → FACTURADA, se actualiza valor_facturado |
| RN-04 | Solo se pueden cruzar formas de pago PENDIENTES |
| RN-05 | Se puede descruzar — forma de pago vuelve a PENDIENTE, valor_facturado = null |
| RN-06 | Formas de pago del mes actual y anteriores son "activas" en el tablero |
| RN-07 | Formas de pago futuras son informativas |
| RN-08 | Facturas se identifican por numero_factura + prefijo (UNIQUE) |
| RN-09 | Entradas de bitácora son inmutables |
| RN-10 | Conversión USD→COP usa TRM del mes de la forma de pago |
| RN-11 | Si no hay TRM para un mes, se usa la del mes anterior más reciente |
| RN-12 | El valor_facturado puede ser diferente al valor presupuestado |
| RN-13 | Los KPIs y el Excel usan valores convertidos a COP para consolidar |

---

## 6. Fases de Implementación

### F1 — Modelo de Datos y Backend Base
| Tarea | Descripción |
|-------|-------------|
| T1.1 | SQL: crear GC_TRM, GC_FACTURA, GC_FORMA_PAGO_GESTION |
| T1.2 | SQL: ALTER GC_CONTRATO_FORMA_PAGO ADD valor_facturado |
| T1.3 | Entidades JPA: GcTrm, GcFactura, GcFormaPagoGestion |
| T1.4 | Repositories |
| T1.5 | DTOs |
| T1.6 | Services: TrmService, FacturaService, FormaPagoGestionService |
| T1.7 | Controllers |
| T1.8 | Endpoint emisiones pendientes con conversión COP |

### F2 — Frontend: Tablero de Facturación
| Tarea | Descripción |
|-------|-------------|
| T2.1 | FacturacionView.vue (tablero con KPIs + listas) |
| T2.2 | FormaPagoGestionPanel.vue (bitácora + documentos + cruce) |
| T2.3 | RegistrarGestionModal.vue |
| T2.4 | Router + NavRail |

### F3 — Frontend: Facturas y Cruce
| Tarea | Descripción |
|-------|-------------|
| T3.1 | FacturasListView.vue |
| T3.2 | RegistrarFacturaModal.vue |
| T3.3 | CruzarFacturaModal.vue |

### F4 — TRM y Excel EMI
| Tarea | Descripción |
|-------|-------------|
| T4.1 | Vista/sección TRM (tabla editable) |
| T4.2 | Endpoint backend: exportar EMI (Excel) |
| T4.3 | Botón frontend que descarga el Excel |

### F5 — Dashboard Integrado
| Tarea | Descripción |
|-------|-------------|
| T5.1 | Backend: stats facturación (pendiente, facturado, diferencias) |
| T5.2 | Integrar en dashboard principal |

---

## 7. Navegación

```
NavRail:
├── Inicio (Dashboard)
├── Pipeline
├── Contratos
├── Facturación              ← NUEVO
│   ├── /facturacion               → Tablero (pendientes, gestión)
│   ├── /facturacion/facturas      → Lista de facturas
│   └── /facturacion/trm           → Configuración TRM
├── Empresas
└── Personas
```

---

## 8. Ejemplo de Flujo

**Contrato SAAS, forma de pago "MES ABR 2026", presupuesto $9.748.820 COP**

1. **Abr 1**: Aparece en tablero como pendiente del mes
2. **Abr 3**: Gestión → `VALIDACION_SERVICIO` "Servicio OK en marzo"
3. **Abr 3**: Se sube acta de servicio (documento soporte)
4. **Abr 5**: Gestión → `SOLICITUD_EMISION` "Solicitado en FACTRO ref #4521"
5. **Abr 7**: Se registra factura ARQBS-4521 por $9.748.820
6. **Abr 7**: Se cruza → forma de pago FACTURADA, valor_facturado = $9.748.820
7. **Desaparece** del tablero de pendientes, aparece en "Facturadas del mes"

**Contrato variable USD, forma de pago "MES ABR 2026", presupuesto $42 USD**

1. TRM abril 2026 = $4.230 → presupuesto COP equivalente = $177.660
2. Se factura por $45 USD (valor real fue mayor)
3. Al cruzar: valor_facturado = $45, COP equivalente = $190.350
4. Diferencia: +$3 USD (+$12.690 COP)

---

*Última actualización: Abril 2026*
