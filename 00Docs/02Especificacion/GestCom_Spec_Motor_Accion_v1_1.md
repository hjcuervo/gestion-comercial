# GestCom — Spec del Subsistema: Motor de Acción (v1)

**Empresa:** Arquitecsoft SAS · **Producto:** GestCom
**Repositorio:** https://github.com/hjcuervo/gestion-comercial
**Documentos base:** `GestCom_Alcance_Motor_Ejecucion_Guiada_v1.md` · `GestCom_Spec_Funcional_Motor_Ejecucion_Guiada_v1.md` (decisiones D-1 a D-7 adoptadas).
**Rol:** Base compartida. Sobre este subsistema se construyen **Generación de Demanda** y **Avance Guiado del Pipeline** (specs propias).
**Tipo:** Especificación funcional implementable. **No incluye DDL ni contrato de API** (entregables posteriores).
**Estado:** Borrador para acordar antes de codificar.

---

## 0. Convenciones

- Estados en MAYÚSCULAS; terminales marcados **(T)**.
- Reglas con prefijo `RB-MA-xx` (motor de acción).
- "Modelo de datos funcional" = atributos y semántica, **no** DDL.
- Auditoría según convención backend (`creadoPor`/`modificadoPor` `Long`, `fechaCreacion`/`fechaModificacion`); se omite en las tablas por brevedad.

---

## 1. Propósito y rol

El Motor de Acción provee la **maquinaria genérica de trabajo** de GestCom: la unidad de trabajo (`GcAccion`), su ciclo de vida, la cola dosificada del usuario, el motor de disposiciones/efectos que encadena el trabajo, la emisión automática de trazabilidad y la reconciliación que evita acciones obsoletas.

**Es deliberadamente agnóstico de dominio.** No sabe qué es una "secuencia de demanda" ni una "etapa de pipeline". Define mecanismos genéricos y un **contrato de extensión** (§11) que cada subsistema implementa. Esto permite especificar y construir Demanda y Avance por separado, ambos apoyados en esta base sin duplicarla.

**Provee:** `GcAccion`, `GcDisposicion`, `GcAccionEvento`, la cola, el dispatch de efectos, la traza y la reconciliación.
**No contiene:** lógica de secuencias/conversión (Demanda) ni de criterios/porcentaje de etapa (Avance).

---

## 2. Modelo de datos funcional

### 2.1 `GcAccion` (entidad central)

| Atributo | Tipo (funcional) | Notas |
|---|---|---|
| `id` | Long | PK. |
| `responsableId` | Long | Usuario asignado (FK `GC_USUARIO`). |
| `estado` | enum | Máquina §3. |
| `tipoAccion` | enum/catálogo | Canal/gestión: `LLAMADA`, `EMAIL`, `WHATSAPP`, `RED_SOCIAL`, `REUNION`, `ENVIO_PROPUESTA`, `TAREA_MANUAL`… Cada subsistema declara los que usa (§11). |
| `prioridad` | enum | `ALTA` / `MEDIA` / `BAJA`. Heredada del origen. |
| `origen` | enum | `ENROLAMIENTO` \| `FLUJO_ETAPA` \| `MANUAL`. Identifica al subsistema dueño. |
| `origenRefId` | Long (opc.) | Id de la fuente que la generó (paso de secuencia / criterio de etapa). Soporta REINTENTAR y reconciliación. |
| `referenciaTipo` | enum | `CONTACTO_PERSONA` \| `CONTACTO_EMPRESA` \| `OPORTUNIDAD`. |
| `referenciaId` | Long | Id de la entidad sobre la que se trabaja. |
| `disponibleDesde` | DateTime | Elegibilidad temporal (§4). |
| `vencimiento` | DateTime (opc.) | SLA (§4). |
| `disposicionId` | Long (opc.) | Se llena al pasar a COMPLETADA. |
| `motivo` | texto (opc.) | Se llena al pasar a POSPUESTA / OMITIDA. |

### 2.2 `GcDisposicion` (catálogo configurable — D-4)

| Atributo | Tipo | Notas |
|---|---|---|
| `id` / `codigo` | Long / String | Identificación. |
| `nombre` | String | Etiqueta visible ("No contesta", "Reunión agendada"). |
| `efecto` | enum | Catálogo fijo del motor: `REINTENTAR`, `AVANZAR_PASO`, `CALIFICAR`, `DESCARTAR`, `CUMPLE_REQUISITO`, `NO_APLICA` (§5). |
| `parametros` | JSON/estructura | Según efecto (p. ej. `esperaReintento`). |
| `origenAplicable` | enum/set | A qué `origen` aplica (validación, §5). |
| `activo` | bool | — |

### 2.3 `GcAccionEvento` (bitácora del ciclo de vida)

Registra cada transición. Mismo patrón que el `GcCompromisoEvento` existente del Mundo 3 ("event sourcing ligero").

| Atributo | Tipo | Notas |
|---|---|---|
| `accionId` | Long | FK. |
| `tipoEvento` | enum | `CREADA`, `TOMADA`, `COMPLETADA`, `POSPUESTA`, `OMITIDA`, `INVALIDADA`, `REASIGNADA`. |
| `actorId` | Long | Quién la causó. |
| `motivo` | texto (opc.) | Posposición/omisión/override. |
| `datos` | JSON (opc.) | Disposición aplicada, nuevo responsable, etc. |
| `fechaEvento` | DateTime | — |

---

## 3. Ciclo de vida de `GcAccion`

**Estados:** `PENDIENTE`, `EN_CURSO`, `COMPLETADA` **(T)**, `POSPUESTA`, `OMITIDA` **(T)**, `INVALIDADA` **(T)**.

```
crear
  │
  ▼
PENDIENTE ──(tomar, opcional)──▶ EN_CURSO
  │  │  │                          │
  │  │  └── completar(disp) ───────┴──▶ COMPLETADA (T) ─▶ dispatch efecto (§5) + traza (§8)
  │  │
  │  └── posponer(motivo, nuevaFecha) ─▶ POSPUESTA ──(llega nuevaFecha)──▶ PENDIENTE
  │
  ├── omitir(motivo) ─▶ OMITIDA (T)
  │
  └── reconciliación ─▶ INVALIDADA (T)   (§9)
```

| Transición | Disparador | Guardas / efectos |
|---|---|---|
| → PENDIENTE | creación | Elegible para la cola cuando `disponibleDesde ≤ ahora`. |
| PENDIENTE → EN_CURSO | `tomar()` | Opcional; marca que el responsable la está ejecutando. |
| PENDIENTE/EN_CURSO → COMPLETADA | `completar(disposicionId)` | Exige disposición compatible con `origen` (RB-MA-02/06). Dispara §5 y §8. |
| PENDIENTE/EN_CURSO → POSPUESTA | `posponer(motivo, nuevaFecha)` | `motivo` y `nuevaFecha` obligatorios; reaparece como PENDIENTE en `nuevaFecha`. |
| PENDIENTE/EN_CURSO → OMITIDA | `omitir(motivo)` | `motivo` obligatorio; terminal, no encadena nada. |
| no-terminal → INVALIDADA | reconciliación | La entidad referida dejó de admitir trabajo (§9). |
| terminal → * | — | Rechazado (RB-MA-13). |

---

## 4. Semántica temporal: `disponibleDesde` vs `vencimiento` vs `prioridad`

- **`disponibleDesde`** = *no antes de*. La acción no es elegible para la cola hasta ese momento (espera de secuencia, posposición, espera de reintento). Es lo que mantiene la cola manejable: aunque existan miles de acciones, solo las disponibles compiten por la cola.
- **`vencimiento`** = *no después de* (SLA, opcional). Si se pasa, la acción es **vencida**: sube a la cabeza de la cola y cuenta para los KPI de incumplimiento.
- **`prioridad`** = peso relativo dentro de las disponibles, heredado del origen (la secuencia o la etapa).

- **RB-MA-11** — `disponibleDesde` gobierna la elegibilidad; `vencimiento` gobierna la urgencia/SLA. Son independientes.

---

## 5. Disposiciones y motor de efectos

Completar una acción exige una **disposición**, cuyo **efecto** decide el encadenamiento. El motor distingue efectos **genéricos** (los resuelve él mismo) de **delegados** (los resuelve el subsistema dueño del `origen`, vía §11).

| Efecto | Tipo | Resultado |
|---|---|---|
| `REINTENTAR` | Genérico | Clona la acción (mismo `origen`/`origenRefId`/`referencia`/`tipoAccion`/`responsable`/`prioridad`) con `disponibleDesde = ahora + parametros.esperaReintento`. |
| `NO_APLICA` | Genérico | Cierra la acción sin encadenar nada (omisión tipificada con disposición). |
| `AVANZAR_PASO` | Delegado (ENROLAMIENTO) | El subsistema Demanda genera la acción del siguiente paso. |
| `CALIFICAR` | Delegado (ENROLAMIENTO) | Demanda crea la oportunidad y cierra el enrolamiento. |
| `DESCARTAR` | Delegado (ENROLAMIENTO) | Demanda descarta el enrolamiento. |
| `CUMPLE_REQUISITO` | Delegado (FLUJO_ETAPA) | Avance marca el requisito cumplido y recalcula el `%`. |

**Algoritmo de `completar(accion, disposicion)`:**
```
1. Validar: accion.estado no terminal; disposicion.origenAplicable incluye accion.origen.
2. accion.estado = COMPLETADA; accion.disposicionId = disposicion.id.
3. Emitir traza (§8).
4. Dispatch del efecto:
     - genérico  → el motor lo ejecuta (REINTENTAR / NO_APLICA).
     - delegado   → invocar handler del subsistema dueño de accion.origen (§11),
                    que devuelve 0..n "solicitudes de acción" + efectos sobre la referencia.
5. Materializar las acciones solicitadas (estado PENDIENTE, disponibleDesde según corresponda).
6. Registrar GcAccionEvento(COMPLETADA, datos=disposicion).
```

- **RB-MA-05** — El encadenamiento ocurre **solo** por el efecto de una disposición o por la generación inicial de un origen. Ninguna acción "siguiente" se crea por otra vía.
- **RB-MA-06** — Una disposición solo puede aplicarse a una acción cuyo `origen` esté en su `origenAplicable` (p. ej. `CALIFICAR` solo sobre `ENROLAMIENTO`).
- **RB-MA-14** — El motor no conoce la semántica de los efectos delegados; los resuelve el subsistema dueño del `origen`.

---

## 6. La cola (bandeja) — modelo y dosificación

La cola es **derivada** (no es tabla): se calcula sobre las `GcAccion` del usuario. Es el modelo detrás de la pantalla central de la identidad "Instrumento" (extiende la Consola de Actividades RF2; el render es del frontend, fuera de esta spec).

**Para un usuario `U` en el momento `ahora`:**
```
Disponibles(U)   = { a : a.responsable=U ∧ a.estado=PENDIENTE ∧ a.disponibleDesde ≤ ahora }
Próximas(U)      = { a : a.responsable=U ∧ a.estado=PENDIENTE ∧ a.disponibleDesde > ahora }

Orden de Disponibles:
  1) vencidas (vencimiento ≠ null ∧ vencimiento < ahora) antes que no vencidas
  2) prioridad ALTA > MEDIA > BAJA
  3) vencimiento ascendente (null al final)
  4) id ascendente

Hoy(U)     = primeras N de Disponibles, con N = topeVisible (config; p. ej. 50)
En_espera  = Disponibles \ Hoy(U)
```

**Dosificación (resuelve los miles de contactos):** aunque haya 1.000 acciones disponibles, **`Hoy` muestra a lo sumo `topeVisible`**, ordenadas. Al cerrar (completar/posponer/omitir) una de `Hoy`, se rellena con la siguiente de `En_espera`. Opcionalmente un `topeDiario` limita cuántas se presentan/cuentan por día hábil; al alcanzarlo no se rellena más hasta el día siguiente.

- **RB-MA-07** — Ninguna vista presenta más de `topeVisible` acciones a la vez; el resto queda en `En_espera`/`Próximas`, nunca se descarta.
- **RB-MA-08** — Una acción con `disponibleDesde` futuro (POSPUESTA o espera de secuencia) no es elegible hasta esa fecha.

---

## 7. Posponer / omitir

- **Posponer**: `motivo` + `nuevaFecha`. Sale de `Hoy`, no rompe el encadenamiento, reaparece en la fecha. Puede repetirse (cada vez deja `GcAccionEvento`).
- **Omitir**: `motivo`. Cierra como OMITIDA, **no** encadena. Si el negocio quiere que "no aplica" avance el flujo, se modela con una disposición de efecto `AVANZAR_PASO`/`NO_APLICA`, no con omitir.

- **RB-MA-03** — POSPUESTA y OMITIDA exigen `motivo`; OMITIDA es terminal y no reaparece salvo recreación por su subsistema.

---

## 8. Trazabilidad automática

- **RB-MA-09** — Al pasar a COMPLETADA, el motor emite automáticamente un registro `GcActividad` **inmutable** (fecha, responsable, `tipoActividad` derivado del `tipoAccion`, disposición, referencia). El usuario **no** escribe la bitácora.
- El mapeo `tipoAccion → tipoActividad` lo declara el subsistema (§11); si no hay mapeo, se usa un `tipoActividad` genérico configurable.
- Posponer/omitir dejan `GcAccionEvento` (con motivo), pero **no** emiten actividad de gestión cumplida.

---

## 9. Reconciliación e invalidación

Evita que la cola "mienta" mostrando trabajo que ya no aplica.

- **RB-MA-10** — Cuando la entidad referida cambia de estado de modo que el trabajo pendiente deja de tener sentido, sus acciones PENDIENTE/POSPUESTA pasan a INVALIDADA (con `GcAccionEvento`).
- El motor provee el **mecanismo** (al notificarse un cambio de estado de una entidad referida, buscar acciones no terminales que la referencian e invalidarlas). **Las reglas concretas** (qué cambio invalida qué) las declara cada subsistema (§11). Casos conocidos:
  - Oportunidad → CONTRATADA o cerrada (PERDIDA/NO_CONCRETADA): invalida sus acciones `FLUJO_ETAPA`.
  - Enrolamiento → CONVERTIDO/DESCARTADO/FINALIZADO: invalida sus acciones `ENROLAMIENTO` pendientes.

---

## 10. Reasignación y autorización (RBAC)

| Capacidad | Comercial | Admin | Lectura-KPI |
|---|---|---|---|
| Ver su propia cola | Sí (propia) | Sí (cualquiera) | Solo lectura (agregado) |
| Completar / posponer / omitir | Sí (las suyas) | Sí | No |
| Tomar (EN_CURSO) | Sí | Sí | No |
| Reasignar `responsable` | No | Sí | No |

- **RB-MA-04** — Solo el `responsable` o un Admin opera una acción.
- **RB-MA-12** — La reasignación es exclusiva de Admin y queda registrada (`GcAccionEvento` REASIGNADA). Al reasignar, la acción se mueve de cola.

---

## 11. Contrato de extensión para subsistemas

Para que el motor sea reusable, cada subsistema (Demanda, Avance) **registra** lo siguiente. Las specs de Demanda y Avance se escriben como implementaciones de este contrato.

1. **`origen` propio** (`ENROLAMIENTO` para Demanda, `FLUJO_ETAPA` para Avance) y los `tipoAccion`/canales que utiliza.
2. **Disposiciones** y su `efecto` (config), declarando su `origenAplicable`.
3. **Handler de efecto delegado**: dado `(accion completada, disposicion)`, devuelve las solicitudes de siguiente acción (con su `disponibleDesde`, `referencia`, `tipoAccion`, `prioridad`, `origenRefId`) y los efectos sobre la entidad referida (p. ej. crear oportunidad, recalcular `%`).
4. **Reglas de reconciliación**: qué cambios de estado de su entidad referida invalidan qué acciones (§9).
5. **Mapeo `tipoAccion → tipoActividad`** para la traza (§8).
6. **Generador inicial**: cómo se materializa la primera acción cuando nace un origen (al enrolar / al entrar a una etapa).

- **RB-MA-14** (ver §5) — el motor delega; no codifica semántica de subsistema.

---

## 12. Concurrencia y casos borde

- **RB-MA-13** — Completar/posponer/omitir una acción ya terminal se rechaza (no-op idempotente); evita doble efecto por doble clic o reintento de red.
- **Concurrencia** Admin + responsable sobre la misma acción: bloqueo optimista (versión); gana la primera, la segunda recibe conflicto.
- **Acción huérfana** (referencia inexistente): se invalida en reconciliación.
- **Posposición a fecha pasada**: se normaliza a `ahora` (vuelve a `Hoy`).
- **Reintento sin `esperaReintento`**: `disponibleDesde = ahora` (reaparece de inmediato).
- **Tope alcanzado**: las disponibles excedentes quedan en `En_espera`; no se pierden.

---

## 13. Relación con lo existente

- **`GcCompromiso` (comercial) coexiste (D-5).** No se migra en el piloto. `GcAccion` es la unidad nueva y genérica; la reconciliación `GcCompromiso ↔ GcAccion` es una fase posterior (posible DT). No se toca el Mundo 1 que funciona.
- **`GcActividad` / `TipoActividad`** se reusan como destino de la traza (§8); no se modifican sus reglas.
- **Consola de Actividades "Instrumento" (RF2)** es la pantalla que evoluciona para mostrar la cola; el detalle de frontend es de la fase F4, fuera de esta spec.
- **`propietarioId` y RBAC** existentes son la base de `responsableId` y los permisos.

---

## 14. Reglas de negocio (consolidado `RB-MA`)

- **RB-MA-01** — Toda acción tiene exactamente un `responsableId` y un `estado`.
- **RB-MA-02** — No se pasa a COMPLETADA sin `disposicionId`.
- **RB-MA-03** — POSPUESTA/OMITIDA exigen `motivo`; OMITIDA es terminal.
- **RB-MA-04** — Solo el responsable o Admin opera una acción.
- **RB-MA-05** — El encadenamiento ocurre solo por efecto de disposición o generación inicial de un origen.
- **RB-MA-06** — Una disposición solo aplica a acciones cuyo `origen` esté en su `origenAplicable`.
- **RB-MA-07** — Ninguna vista presenta más de `topeVisible` acciones a la vez; el resto se programa, no se descarta.
- **RB-MA-08** — `disponibleDesde` futuro impide la elegibilidad hasta esa fecha.
- **RB-MA-09** — Completar emite `GcActividad` inmutable (traza automática).
- **RB-MA-10** — Cambios de estado de la entidad referida invalidan acciones pendientes según las reglas del subsistema.
- **RB-MA-11** — `disponibleDesde` (elegibilidad) y `vencimiento` (SLA) son independientes.
- **RB-MA-12** — Reasignar es exclusivo de Admin y queda registrado.
- **RB-MA-13** — Operar una acción terminal se rechaza (idempotencia).
- **RB-MA-14** — El motor delega los efectos de subsistema; no codifica su semántica.
- **RB-MA-15** — Toda transición de estado registra un `GcAccionEvento` con actor y, cuando aplica, motivo.

---

## 15. Criterios de aceptación

1. Crear acción → tomar → completar con disposición de efecto genérico `REINTENTAR`; verificar que (a) se emite `GcActividad` inmutable, (b) se crea la acción clon con la espera correcta, (c) hay `GcAccionEvento` COMPLETADA.
2. Completar con un efecto delegado mockeado; verificar que el motor invoca el handler del subsistema y materializa las acciones que devuelve.
3. Cargar 1.000 acciones disponibles a un usuario con `topeVisible=50`; verificar que `Hoy` muestra 50 y que al cerrar una se rellena con la siguiente.
4. Posponer con motivo y fecha futura; verificar salida de `Hoy`, reaparición en fecha y evento registrado.
5. Omitir con motivo; verificar estado OMITIDA terminal sin encadenamiento.
6. Cambiar la entidad referida a un estado que invalida; verificar INVALIDADA + evento.
7. Intentar completar una acción ya COMPLETADA; verificar rechazo idempotente.
8. Reasignar como Comercial (rechazo) y como Admin (éxito + evento + cambio de cola).

---

## 16. Fuera de alcance (siguientes entregables)

- Modelo lógico (B-01) y DDL Oracle (B-02) de `GcAccion`, `GcDisposicion`, `GcAccionEvento`.
- Matriz `RB-MA` → endpoints (estilo B-04).
- Spec del subsistema **Generación de Demanda** (implementa el contrato §11 para `origen = ENROLAMIENTO`).
- Spec del subsistema **Avance Guiado del Pipeline** (implementa el contrato §11 para `origen = FLUJO_ETAPA`).
- Detalle de frontend de la cola (fase F4) y parámetros de configuración (`topeVisible`, `topeDiario`, horario laboral).

---

*Spec del subsistema Motor de Acción — v1. Define la base genérica y el contrato de extensión sobre el que se especifican Generación de Demanda y Avance Guiado. Base para el modelo lógico, el DDL y el contrato de API de este subsistema.*
