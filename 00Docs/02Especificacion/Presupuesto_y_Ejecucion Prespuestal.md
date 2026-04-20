Aquí tienes una **especificación funcional clara, estructurada y lista para implementación**. Está pensada para que un equipo técnico la pueda traducir directamente a sistema sin ambigüedades.

---

# 📘 Especificación Funcional

## Gestión de Presupuesto de Ingresos vs Ejecución Comercial

---

## 1. 🎯 Objetivo del módulo

Implementar un sistema que permita:

1. Garantizar que **ningún ingreso presupuestado quede sin gestión**
2. Asegurar visibilidad de **qué debe gestionarse en cada periodo**
3. Proyectar **flujo de ingresos y flujo de caja esperado**
4. Mantener **trazabilidad completa de desviaciones**, incluso entre periodos
5. Gestionar explícitamente **cumplimientos parciales y saldos pendientes**

---

## 2. 🧱 Concepto central

### 2.1 Entidad principal: **Compromiso de Ingreso**

Unidad mínima de control del sistema.

> Representa un ingreso esperado (presupuestado) que debe ser gestionado hasta su resolución final.

---

## 3. 📊 Modelo funcional (nivel de negocio)

### 3.1 Atributos obligatorios

Cada Compromiso de Ingreso debe contener:

| Campo                     | Descripción                           |
| ------------------------- | ------------------------------------- |
| ID                        | Identificador único (persistente)     |
| Cliente                   | Cuenta asociada                       |
| Responsable               | Usuario encargado                     |
| Monto Presupuestado       | Valor original comprometido           |
| Monto Facturado Acumulado | Suma de facturación real              |
| Saldo Pendiente           | Monto Presupuestado - Facturado       |
| Fecha Esperada Original   | Fecha inicial comprometida            |
| Fecha Esperada Actual     | Fecha actual proyectada               |
| Estado                    | Estado actual del compromiso          |
| Tipo                      | (Nuevo, recurrente, renovación, etc.) |

---

### 3.2 Atributos derivados

| Campo           | Regla                     |
| --------------- | ------------------------- |
| Saldo Pendiente | = Presupuesto - Facturado |
| % Cumplimiento  | = Facturado / Presupuesto |

---

## 4. 🔄 Estados del Compromiso

El sistema debe soportar los siguientes estados:

* **Pendiente de gestión**
* **En gestión**
* **Comprometido** (alta probabilidad de cierre)
* **Parcialmente cumplido**
* **Cumplido**
* **Reprogramado**
* **No logrado (perdido)**

---

## 5. ⚙️ Reglas de negocio obligatorias

---

### 5.1 Persistencia

* Un compromiso **NO puede eliminarse**
* Un compromiso **NO depende de un periodo**
* Debe existir hasta que:

  * Se cumpla completamente
  * Se marque como perdido

---

### 5.2 Responsabilidad

* Todo compromiso debe tener:

  * Responsable asignado
  * Estado definido

❌ No pueden existir compromisos sin responsable o estado

---

### 5.3 Gestión activa

El sistema debe garantizar:

* Todo compromiso debe tener actividad reciente
* Si no hay actividad en un periodo definido → marcar como:

  * “Sin gestión” o “En riesgo”

---

### 5.4 Manejo de cumplimiento parcial

Cuando:

**Monto Facturado < Monto Presupuestado**

Entonces:

1. El compromiso pasa a estado:
   → **Parcialmente cumplido**

2. Se calcula automáticamente:

   * Saldo pendiente

3. El saldo pendiente debe:

   * Permanecer activo
   * Tener nueva fecha esperada
   * Seguir asignado al mismo responsable

---

### 5.5 Manejo de desviaciones

Toda diferencia debe clasificarse en una de estas categorías:

* Reprogramada (se ejecutará después)
* Perdida (no se ejecutará)
* Reemplazada (otro ingreso cubrirá la diferencia)

---

### 5.6 Reprogramación

Cuando un compromiso no se cumple en el periodo:

* NO se cierra
* Se actualiza:

  * Fecha Esperada Actual
  * Estado → “Reprogramado”

---

### 5.7 Cierre de compromiso

Un compromiso solo puede cerrarse cuando:

#### Caso 1: Cumplido

* Facturado acumulado = monto presupuestado

#### Caso 2: Perdido

* Saldo pendiente se clasifica como no recuperable

---

### 5.8 Regla de integridad

👉 **Toda diferencia entre presupuesto y ejecución debe permanecer visible y gestionable hasta su resolución**

---

## 6. 📅 Gestión por periodos

---

### 6.1 Definición

Los periodos (meses) son:

* Una **vista de análisis**
* NO un contenedor de datos

---

### 6.2 Vista de periodo

El sistema debe permitir consultar:

👉 “Compromisos con Fecha Esperada Actual dentro del periodo”

---

### 6.3 Inclusión de compromisos

Un compromiso pertenece a un periodo si:

* Su Fecha Esperada Actual cae dentro del periodo

---

## 7. 💰 Proyección de ingresos y flujo de caja

---

### 7.1 Ingreso proyectado

Se calcula con:

* Monto pendiente de compromisos activos
* Segmentado por Fecha Esperada Actual

---

### 7.2 Flujo de caja

Cada compromiso debe permitir:

* Definir condición de pago

Entonces el sistema debe calcular:

* Fecha esperada de recaudo

---

## 8. 🔍 Seguimiento y control

---

### 8.1 Indicadores obligatorios

El sistema debe permitir visualizar:

* Total presupuestado vs facturado
* % de cumplimiento
* Monto pendiente
* Monto reprogramado
* Monto perdido
* Aging de pendientes (antigüedad)
* Compromisos sin gestión reciente

---

### 8.2 Alertas

El sistema debe generar alertas cuando:

* Un compromiso no tiene actividad
* Un compromiso está en riesgo
* Un compromiso supera su fecha esperada

---

## 9. 🧾 Historial y trazabilidad

---

### 9.1 Registro de cambios

El sistema debe almacenar:

* Cambios de estado
* Cambios de monto
* Cambios de fecha
* Cambios de responsable

---

### 9.2 Trazabilidad

Debe ser posible responder:

* Qué se presupuestó originalmente
* Qué se ejecutó
* Qué se desvió
* Qué sigue pendiente
* Qué se perdió

---

## 10. 🔁 Flujo operativo

---

### 10.1 Creación

* Se generan compromisos desde el presupuesto

---

### 10.2 Activación

* Todos los compromisos inician en:

  * “Pendiente de gestión”

---

### 10.3 Ejecución

* Se actualizan estados según avance

---

### 10.4 Desviaciones

* Se gestionan en tiempo real
* Se reclasifican

---

### 10.5 Cierre

* Solo cuando se cumple o se pierde

---

## 11. 🚫 Restricciones clave

* No se permite:

  * Eliminar compromisos
  * Sobrescribir el monto presupuestado original
  * Cerrar compromisos parcialmente sin clasificar el saldo
  * Crear nuevos compromisos sin relación cuando existe un saldo pendiente

---

## 12. 🧠 Principio fundamental del sistema

> **Los compromisos viven hasta resolverse.
> Los periodos solo los observan.
> Las desviaciones nunca se ocultan: se gestionan.**

---


La máquina de estados define:

* **qué estados existen**
* **cómo se puede pasar de uno a otro**
* **qué reglas se ejecutan en cada transición**

Sin esto, cada usuario “interpreta” el proceso y pierdes control.

---

# 🔄 Máquina de Estados

## Compromiso de Ingreso

---

## 1. 🎯 Objetivo

Controlar el ciclo de vida completo de un compromiso de ingreso, garantizando:

* Gestión continua
* Trazabilidad
* Control de desviaciones
* Persistencia hasta resolución

---

## 2. 🧱 Estados definidos

### Estados principales:

1. **Pendiente de gestión**
2. **En gestión**
3. **Comprometido**
4. **Parcialmente cumplido**
5. **Cumplido (final)**
6. **Reprogramado**
7. **No logrado (final)**

---

## 3. 🔀 Transiciones permitidas

A continuación, las transiciones válidas:

---

### 🟡 1. Pendiente de gestión

Estado inicial.

**Puede pasar a:**

* En gestión
* No logrado

**Reglas:**

* Debe asignarse responsable
* Debe iniciarse actividad

---

### 🔵 2. En gestión

El compromiso está siendo trabajado.

**Puede pasar a:**

* Comprometido
* Parcialmente cumplido
* Reprogramado
* No logrado

**Reglas:**

* Debe existir actividad reciente
* Puede actualizar monto y fecha esperada

---

### 🟢 3. Comprometido

Alta probabilidad de cierre.

**Puede pasar a:**

* Parcialmente cumplido
* Cumplido
* Reprogramado
* No logrado

**Reglas:**

* Debe existir validación comercial (ej: acuerdo con cliente)

---

### 🟠 4. Parcialmente cumplido

Se ejecutó una parte del compromiso.

**Puede pasar a:**

* En gestión
* Comprometido
* Cumplido
* Reprogramado
* No logrado

**Reglas obligatorias:**

* Debe existir:

  * Monto facturado > 0
  * Monto facturado < monto presupuestado
* El saldo pendiente sigue activo

---

### 🟣 5. Reprogramado

No se cumplió en la fecha esperada.

**Puede pasar a:**

* En gestión
* Comprometido
* No logrado

**Reglas:**

* Debe actualizarse:

  * Fecha esperada actual
* No cambia el monto presupuestado

---

### 🔴 6. No logrado (estado final)

El compromiso no se cumplirá.

**No puede salir de este estado**

**Reglas:**

* Debe registrarse causa de pérdida
* El saldo pendiente se considera cerrado como pérdida

---

### 🟢 7. Cumplido (estado final)

El compromiso se ejecutó completamente.

**No puede salir de este estado**

**Reglas:**

* Monto facturado acumulado = monto presupuestado

---

## 4. 📊 Diagrama lógico (simplificado)

```
Pendiente
   ↓
En gestión ↔ Comprometido
   ↓           ↓
Parcial ←──────
   ↓
Reprogramado → En gestión
   ↓
No logrado (final)

Parcial → Cumplido (final)
Comprometido → Cumplido (final)
```

---

## 5. ⚙️ Reglas automáticas del sistema

---

### 5.1 Cambio automático a “Parcialmente cumplido”

Cuando:

* Se registra facturación
* Y no cubre el total

👉 El sistema debe forzar estado:
**Parcialmente cumplido**

---

### 5.2 Cambio automático a “Cumplido”

Cuando:

* Facturación acumulada = presupuesto

👉 Estado final automático

---

### 5.3 Bloqueo de cierre incorrecto

❌ No se puede marcar como “Cumplido” si:

* Facturación < presupuesto

---

### 5.4 Obligación de clasificar reprogramaciones

Cuando cambia la fecha esperada:

* El sistema debe exigir:

  * Justificación
  * Nueva fecha

---

### 5.5 Control de inactividad

Si no hay actividad en X días:

* El sistema debe marcar:

  * “En riesgo” (flag, no estado)

---

## 6. 🧩 Estados vs comportamiento del negocio

| Estado       | ¿Sigue en forecast?    | ¿Requiere gestión? | ¿Cuenta como ingreso real? |
| ------------ | ---------------------- | ------------------ | -------------------------- |
| Pendiente    | Sí                     | Sí                 | No                         |
| En gestión   | Sí                     | Sí                 | No                         |
| Comprometido | Sí (alta probabilidad) | Sí                 | No                         |
| Parcial      | Sí (saldo pendiente)   | Sí                 | Parcial                    |
| Reprogramado | Sí (nuevo periodo)     | Sí                 | No                         |
| Cumplido     | No                     | No                 | Sí                         |
| No logrado   | No                     | No                 | No                         |

---

## 7. 🚫 Reglas críticas

* No se puede:

  * Saltar de Pendiente → Cumplido sin facturación
  * Cerrar parcial sin clasificar saldo
  * Reprogramar sin cambiar fecha
  * Revivir estados finales

---

## 8. 🧠 Principio clave

> **Un compromiso solo deja de existir cuando se cumple completamente o se pierde explícitamente.
> Todo lo demás es estado intermedio gestionable.**

---

## 9. 💡 Recomendación de diseño funcional

Aunque no lo pediste técnico, funcionalmente es clave:

👉 Separar:

* Estado del compromiso
* Flags (riesgo, atraso, sin gestión)

Esto evita contaminar la lógica principal.

---

Si quieres, el siguiente nivel sería:

* definir **eventos del sistema** (ej: “se registró factura”, “se cambió fecha”)
* o mapear esto a **casos reales paso a paso**

Eso ayuda muchísimo a evitar errores en implementación.


Perfecto—los **eventos** son lo que activa la máquina de estados y garantiza que el sistema sea consistente sin depender de interpretación humana.

Aquí no estamos hablando de eventos técnicos, sino de **eventos de negocio**: cosas que ocurren y obligan al sistema a reaccionar.

---

# 📘 Especificación Funcional

## Eventos del Sistema – Gestión de Compromisos de Ingreso

---

## 1. 🎯 Objetivo

Definir los eventos que:

* Disparan cambios de estado
* Actualizan información crítica
* Garantizan reglas de negocio
* Mantienen trazabilidad

---

## 2. 🧱 Principio clave

> **Todo cambio relevante debe ocurrir a través de un evento explícito.**

❌ No se permiten cambios directos de estado o datos sin evento asociado.

---

## 3. 📊 Tipos de eventos

Se agrupan en 5 categorías:

1. Creación
2. Gestión comercial
3. Ejecución (facturación)
4. Reprogramación / desviaciones
5. Cierre

---

# 🔹 4. Eventos definidos

---

## 4.1 🟡 Evento: Creación de compromiso

### Nombre:

`CompromisoCreado`

### Dispara:

* Creación inicial desde presupuesto

### Efectos:

* Estado = Pendiente de gestión
* Se asigna responsable
* Se registra fecha esperada original y actual

---

## 4.2 🔵 Evento: Inicio de gestión

### Nombre:

`GestionIniciada`

### Condición:

* El responsable inicia actividad

### Efectos:

* Estado → En gestión
* Se registra actividad

---

## 4.3 🔵 Evento: Actualización de gestión

### Nombre:

`GestionActualizada`

### Condición:

* Hay interacción relevante (llamada, propuesta, etc.)

### Efectos:

* Actualiza historial
* Refresca última actividad

---

## 4.4 🟢 Evento: Compromiso comercial

### Nombre:

`CompromisoConfirmado`

### Condición:

* Existe acuerdo con el cliente

### Efectos:

* Estado → Comprometido

---

## 4.5 🟠 Evento: Registro de facturación

### Nombre:

`FacturaRegistrada`

### Inputs:

* Monto facturado
* Fecha de factura

---

### Reglas automáticas:

#### Caso 1: Facturación parcial

Si:

* Facturado acumulado < presupuesto

Entonces:

* Estado → Parcialmente cumplido

---

#### Caso 2: Facturación total

Si:

* Facturado acumulado = presupuesto

Entonces:

* Estado → Cumplido (final)

---

### Efectos adicionales:

* Actualiza monto facturado acumulado
* Recalcula saldo pendiente

---

## 4.6 🟣 Evento: Reprogramación

### Nombre:

`FechaReprogramada`

### Condición:

* No se logra cierre en fecha esperada

### Inputs:

* Nueva fecha esperada
* Motivo

### Efectos:

* Estado → Reprogramado
* Actualiza fecha esperada actual

---

## 4.7 🟠 Evento: Ajuste de monto

### Nombre:

`MontoAjustado`

### Condición:

* Cambio en expectativa de ingreso

### Inputs:

* Nuevo monto esperado
* Motivo

### Efectos:

* NO modifica el presupuesto original
* Puede afectar forecast

---

## 4.8 🔴 Evento: Clasificación de pérdida

### Nombre:

`CompromisoPerdido`

### Condición:

* Se determina que no se logrará el ingreso

### Inputs:

* Motivo de pérdida

### Efectos:

* Estado → No logrado (final)
* Saldo pendiente se cierra

---

## 4.9 🔵 Evento: Reactivación

### Nombre:

`CompromisoReactivado`

### Condición:

* Un compromiso reprogramado vuelve a gestión activa

### Efectos:

* Estado → En gestión

---

## 4.10 ⚠️ Evento: Detección de inactividad

### Nombre:

`InactividadDetectada`

### Condición:

* No hay actividad en X días

### Efectos:

* Marca flag:

  * “En riesgo”
* No cambia estado

---

## 4.11 ⚠️ Evento: Desviación detectada

### Nombre:

`DesviacionDetectada`

### Condición:

* Diferencia entre presupuesto y ejecución

### Efectos:

* Obliga clasificación:

  * Reprogramado
  * Perdido
  * En gestión

---

---

# 🔹 5. Reglas de orquestación

---

## 5.1 Eventos obligatorios

El sistema debe garantizar:

* No hay cambios de estado sin evento
* No hay cambios de monto sin evento
* No hay cambios de fecha sin evento

---

## 5.2 Secuencia válida (ejemplo)

```id="0w6l2k"
CompromisoCreado
   ↓
GestionIniciada
   ↓
CompromisoConfirmado
   ↓
FacturaRegistrada (parcial)
   ↓
FechaReprogramada
   ↓
FacturaRegistrada (final)
```

---

## 5.3 Idempotencia (nivel funcional)

* Registrar la misma factura no debe duplicar impacto
* Eventos deben poder validarse contra duplicados

---

## 5.4 Consistencia

Después de cada evento el sistema debe garantizar:

* Estado coherente
* Montos actualizados
* Historial completo

---

# 🔹 6. Relación eventos ↔ estados

| Evento                      | Estado resultante |
| --------------------------- | ----------------- |
| CompromisoCreado            | Pendiente         |
| GestionIniciada             | En gestión        |
| CompromisoConfirmado        | Comprometido      |
| FacturaRegistrada (parcial) | Parcial           |
| FacturaRegistrada (total)   | Cumplido          |
| FechaReprogramada           | Reprogramado      |
| CompromisoPerdido           | No logrado        |
| CompromisoReactivado        | En gestión        |

---

# 🔹 7. 🚫 Reglas críticas

* No se puede:

  * Cambiar estado manualmente sin evento
  * Facturar sin generar evento
  * Reprogramar sin motivo
  * Cerrar sin clasificar

---

# 🧠 Principio final

> **El sistema no cambia por edición, cambia por eventos.
> Los eventos construyen la historia del compromiso.**

---

Si quieres, el siguiente paso más potente sería:

👉 definir **casos extremos reales (edge cases)**
Ej:

* múltiples facturas
* cambio de cliente
* cancelaciones parciales
* reemplazo de ingresos

Eso es lo que realmente rompe sistemas mal diseñados.



Perfecto—este es el nivel donde los sistemas suelen romperse si no se define bien.
Los **casos extremos (edge cases)** son los que garantizan que tu modelo sea **robusto en producción**, no solo en el escenario ideal.

Voy a estructurarlos como **escenarios + reglas obligatorias**, para que sean directamente implementables.

---

# 📘 Especificación Funcional

## Casos Extremos – Gestión de Compromisos de Ingreso

---

## 1. 🎯 Objetivo

Definir el comportamiento del sistema ante situaciones no ideales, garantizando:

* Consistencia de datos
* Trazabilidad completa
* No pérdida de información
* Cumplimiento de reglas del negocio

---

## 2. 🧱 Principio rector

> **Ningún evento elimina información.
> Toda desviación se modela, no se oculta.**

---

# 🔴 3. Casos extremos definidos

---

## 3.1 📉 Facturación múltiple parcial

### Escenario:

Un compromiso se factura en múltiples partes.

Ejemplo:

* Presupuesto: 10,000
* Facturas: 3,000 + 2,000 + 5,000

---

### Reglas:

* Cada factura genera un evento `FacturaRegistrada`
* El sistema debe:

  * Acumular el monto facturado
  * Recalcular saldo pendiente

---

### Estados:

* Después de cada factura parcial:
  → **Parcialmente cumplido**

* Cuando suma total = presupuesto:
  → **Cumplido**

---

## 3.2 📉 Sobre-ejecución (facturación mayor al presupuesto)

### Escenario:

* Presupuesto: 10,000
* Facturado: 12,000

---

### Reglas:

* El sistema debe permitirlo
* No debe truncar el valor

---

### Efectos:

* Estado → Cumplido
* Se registra:

  * Variación positiva (+2,000)

---

### Restricción:

* El monto presupuestado original **no cambia**

---

## 3.3 📉 Sub-ejecución con cierre explícito

### Escenario:

* Presupuesto: 10,000
* Facturado: 7,000
* Se decide no recuperar los 3,000 restantes

---

### Reglas:

* Se debe generar evento:
  → `CompromisoPerdido` (parcial)

---

### Efectos:

* Estado → No logrado
* Facturado acumulado se mantiene
* Saldo pendiente se cierra como pérdida

---

### Restricción:

* No se puede cerrar sin clasificar el saldo

---

## 3.4 🔁 Reprogramaciones múltiples

### Escenario:

Un compromiso cambia de fecha varias veces.

---

### Reglas:

* Cada cambio genera:
  → `FechaReprogramada`

* Se debe registrar:

  * Fecha anterior
  * Nueva fecha
  * Motivo

---

### Restricción:

* No hay límite de reprogramaciones
* El historial debe ser completo

---

### Métrica derivada:

* Número de reprogramaciones por compromiso

---

## 3.5 🕓 Compromisos “arrastrados” por varios periodos

### Escenario:

Un compromiso no se cumple durante varios meses.

---

### Reglas:

* El compromiso sigue activo
* No se duplica
* No se reinicia

---

### Efectos:

* Se acumula:

  * Aging (antigüedad)
  * Historial de reprogramaciones

---

## 3.6 🔄 Reemplazo de ingresos

### Escenario:

Un compromiso no se logrará, pero será reemplazado por otro.

---

### Reglas:

1. El compromiso original:

   * Debe cerrarse como:
     → No logrado

2. Se crea un nuevo compromiso

3. Debe existir relación explícita:

   * Campo: “Reemplaza a” (ID del compromiso original)

---

### Restricción:

* No se permite “editar” el compromiso original para simular reemplazo

---

## 3.7 🧾 Cambio de monto esperado (sin facturación)

### Escenario:

Se ajusta la expectativa antes de facturar.

Ejemplo:

* Presupuesto: 10,000
* Nueva expectativa: 8,000

---

### Reglas:

* Se genera evento:
  → `MontoAjustado`

---

### Efectos:

* NO cambia el monto presupuestado original
* Cambia el forecast

---

### Resultado:

* Se genera desviación esperada (-2,000)

---

## 3.8 🔁 Cancelación de factura (reversión)

### Escenario:

Una factura registrada se anula.

---

### Reglas:

* Se debe generar evento:
  → `FacturaRevertida`

---

### Efectos:

* Se descuenta del monto facturado acumulado
* Se recalcula saldo pendiente

---

### Posibles estados:

* Puede volver a:

  * Parcialmente cumplido
  * En gestión

---

### Restricción:

* No se elimina la factura original (solo se revierte)

---

## 3.9 🔄 Cambio de responsable

### Escenario:

Se reasigna el compromiso.

---

### Reglas:

* Evento:
  → `ResponsableCambiado`

---

### Efectos:

* Nuevo responsable asume gestión
* Historial registra el cambio

---

### Restricción:

* No se pierde trazabilidad del responsable anterior

---

## 3.10 ⚠️ Compromiso sin actividad prolongada

### Escenario:

No hay gestión durante un periodo largo.

---

### Reglas:

* Se genera evento automático:
  → `InactividadDetectada`

---

### Efectos:

* Flag:

  * “En riesgo”
* No cambia estado

---

---

## 3.11 🔄 Cambio de cliente

### Escenario:

El ingreso se transfiere a otro cliente.

---

### Reglas:

Opción recomendada:

1. Cerrar compromiso original:
   → No logrado

2. Crear nuevo compromiso con nuevo cliente

3. Relacionarlos:

   * “Origen” / “Derivado de”

---

### Restricción:

* No se permite cambiar cliente directamente (rompe trazabilidad)

---

## 3.12 📉 Facturación fuera de periodo esperado

### Escenario:

Se factura después de la fecha esperada.

---

### Reglas:

* Se permite facturación
* Se registra desviación de tiempo

---

### Métrica:

* Delay (días de retraso)

---

## 3.13 📊 Compromiso creado sin ejecución posterior

### Escenario:

Nunca se gestiona.

---

### Reglas:

* Debe escalar automáticamente
* Debe marcarse como:

  * En riesgo

---

### Resultado:

* Debe terminar en:

  * Reprogramado
  * No logrado

---

# 🔹 4. Reglas globales reforzadas

---

## 4.1 Nunca perder información

* No eliminar registros
* No sobrescribir valores originales

---

## 4.2 Todo debe ser trazable

* Cada cambio tiene evento
* Cada evento queda registrado

---

## 4.3 Las desviaciones son obligatorias

* No pueden ignorarse
* Deben clasificarse

---

## 4.4 Persistencia total

* Un compromiso vive hasta:

  * Cumplirse
  * O perderse

---

# 🧠 Insight final

> **Los edge cases no son excepciones.
> Son el comportamiento real del negocio.**

---

Si quieres, el siguiente paso (muy potente) sería:

👉 definir **queries funcionales clave**
(tipo: “qué debería estar pasando hoy vs qué está pasando”)

Eso conecta todo esto con dashboards y operación diaria.
