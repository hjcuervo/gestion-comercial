# GestCom — Mundo 2 Multicontrato (v1)

> **Documento de alcance** para la refactorización del modelo de contratación, oportunidades y empresas.
>
> **Estado:** Borrador 1 (pendiente de aprobación de Hector).
>
> **Fecha:** 21 de mayo de 2026.
>
> **Origen:** Hallazgos durante prueba integral de F2 (Mundo 3). Validación con Hector vía cuestionario funcional (19 decisiones registradas).
>
> **Documentos relacionados:**
> - `Estado_arte_05052026.md` — estado actual del proyecto.
> - `GestCom_Mundo3_Especificacion_v4.md` — spec del flujo de facturación.
> - `GestCom_Mundo3_Matriz_RN_Endpoints.md` — matriz de reglas Mundo 3.
> - `gestcom-context.SKILL.md` — convenciones y glosario del proyecto.

---

## 0. Bitácora de cambios

| Versión | Fecha | Cambio |
|---|---|---|
| v1 (borrador) | 2026-05-21 | Creación inicial post-cuestionario funcional |

---

## 1. Resumen ejecutivo

### 1.1 Qué cambia

GestCom incorpora dos cambios estructurales al **Mundo 2 (Contractual)**:

1. **Cardinalidad oportunidad → contrato pasa de 1:1 a 1:N.** Una oportunidad puede generar múltiples contratos antes de cerrarse.

2. **Las empresas dejan de ser entidades independientes.** Se introduce una jerarquía corporativa (árbol arbitrario) entre ellas. Conceptos como "Grupo Bancolombia" y sus filiales pasan a tener relación explícita en el modelo.

Estos cambios habilitan un escenario común de la operación real: **negociar con una empresa-grupo y formalizar contratos a nombre de sus filiales**, manteniendo trazabilidad de origen comercial e integridad fiscal.

### 1.2 Por qué cambia

Durante validación de F2 surgió un hallazgo conceptual: el modelo actual permite **cruzar facturas entre empresas distintas** dentro de un mismo contrato, lo cual no refleja la realidad fiscal del cliente:

- Una factura tiene un cliente (empresa que la recibe, con NIT).
- Una forma de pago pertenece al contrato (que tiene una empresa cliente).
- Si una factura cruza una forma de pago de otro cliente, se viola la realidad tributaria.

La causa raíz no es solo un check faltante, sino una limitación del modelo: hoy el contrato no impone que sus formas de pago pertenezcan a una sola empresa cliente, y la "empresa de facturación" del contrato se manejaba como un campo libre sin validación contra el grupo corporativo.

Resolver esto pidiendo solamente "que la factura pertenezca a la empresa del contrato" deja fuera un escenario real frecuente: negociar con "Grupo Bancolombia" y firmar varios contratos con filiales específicas. Por eso la refactorización incluye también la jerarquía.

### 1.3 Impacto

| Área | Impacto |
|---|---|
| Modelo de datos | Entidad `GcEmpresa` gana FK auto-referencial. Entidad `GcContrato` gana validación de empresa vs. rama de oportunidad. Entidad `GcCompromisoIngreso` pierde campo `empresa_facturacion` (redundante). |
| Backend | Nuevos endpoints de jerarquía. Validaciones nuevas (RN-23 a RN-30). |
| Frontend | Vista de empresa rediseñada (sección Grupo). Modal de formalización con typeahead filtrado. Ficha de oportunidad gana acciones nuevas. Kanban contractual pierde botón "Formalizar" y gana contador de contratos. |
| Datos existentes | No requiere migración formal (entorno de pruebas con <10 contratos). Limpieza libre antes de aplicar. |
| Spec Mundo 3 (v4) | Documento se actualizará a v5 para reflejar que la regla RN-23 ya está enforced estructuralmente. |

### 1.4 Lo que NO cambia

Para acotar el alcance, este cambio **no toca**:

- Mundo 1 (Comercial): pipelines, etapas, actividades, compromisos comerciales, documentos.
- Mundo 3 (Facturación): flujo de aplicar facturas, máquina de estados de compromisos, 7 estados, bitácoras, KPIs.
- Sistema de diseño (Aurora Dark) y convenciones de UI.
- Stack tecnológico.
- Mecanismos de autenticación/autorización (JWT + RBAC).

---

## 2. Modelo conceptual

### 2.1 DER simplificado (después del cambio)

```
GcEmpresa (auto-referencial)
   │
   │ empresaPadreId (opcional, FK a GcEmpresa.id)
   │
   ├─── 1 ───< GcOportunidad
   │              │
   │              │ (estado GANADA pasa a CONTRATADA solo por acción manual)
   │              │
   │              ├─── N ───< GcContrato
   │              │              │
   │              │              │ (empresa del contrato puede diferir de la
   │              │              │  oportunidad, pero debe estar en la misma rama
   │              │              │  de la jerarquía empresarial)
   │              │              │
   │              │              ├─── N ───< GcCompromisoIngreso
   │              │              │              │
   │              │              │              │ (empresa heredada del contrato:
   │              │              │              │  ya no tiene empresa propia)
   │              │              │              │
   │              │              │              └─── N:M ───> GcFactura
   │              │              │                              (vía GcCompromisoFactura)
   │              │              │
   │              │              └─── N ───< GcContratoModificacion
   │              │
   │              └─── 1 ───< GcProcesoContratacion
   │                              (sin cambios)
   │
   └─── N ───< GcFactura (la factura sigue perteneciendo a una empresa)
```

### 2.2 Conceptos clave

#### Grupo empresarial

Un **Grupo empresarial** es el conjunto de empresas conectadas en el árbol jerárquico. Cada empresa puede tener:
- Una empresa padre (opcional, FK auto-referencial).
- Cero o más empresas hijas (inverso de la relación).

Una empresa **raíz** es aquella sin padre. Una empresa **hoja** es aquella sin hijas. Una empresa puede ser raíz y hoja al mismo tiempo (empresas independientes, sin grupo).

#### Rama de la jerarquía

Una **rama** del árbol es el conjunto de empresas conectadas por una línea directa de ancestros y descendientes. Específicamente, dos empresas A y B están en la misma rama si:

- A es ancestro de B (existe camino descendente A→...→B), **o**
- B es ancestro de A (existe camino descendente B→...→A).

Esto excluye empresas que son "hermanas" o "primas" en el árbol — solo cuentan relaciones verticales directas.

**Ejemplo de jerarquía:**

```
🏢 Grupo Empresarial Antioqueño (GEA) — raíz
   ├─ 🏢 Cemargos
   │    ├─ 🏢 Argos USA
   │    │    └─ 🏢 Argos Florida — hoja
   │    └─ 🏢 Argos Honduras — hoja
   └─ 🏢 Sura
        └─ 🏢 Sura Vida — hoja
```

**Relaciones de rama:**

| Empresa A | Empresa B | ¿Misma rama? | Razón |
|---|---|---|---|
| Cemargos | Argos Florida | ✅ Sí | Cemargos es ancestro de Argos Florida |
| Argos USA | Argos Honduras | ❌ No | Hermanas: no hay relación ancestor↔descendant directa |
| Argos USA | Sura Vida | ❌ No | Primas: ancestro común (GEA) pero no en línea directa |
| GEA | Argos Florida | ✅ Sí | GEA es ancestro de Argos Florida |
| Argos Florida | Cemargos | ✅ Sí | Cemargos es ancestro de Argos Florida (subir también es válido) |

### 2.3 Glosario actualizado

Términos que se agregan o cambian sobre `gestcom-context.SKILL.md` §6:

| Término | Significado |
|---|---|
| **Grupo empresarial** | Conjunto de empresas conectadas en el árbol jerárquico, por relación padre-hijo. Sinónimo informal: "Grupo". |
| **Empresa padre** | Empresa que está un nivel arriba en la jerarquía. Una empresa tiene a lo sumo una padre. |
| **Empresa hija** | Empresa que tiene a otra como padre. Una empresa puede tener N hijas. |
| **Empresa raíz** | Empresa sin padre. Encabeza un grupo. |
| **Empresa hoja** | Empresa sin hijas. Está al final de su rama. |
| **Ancestro** | Empresa que aparece subiendo en la jerarquía (padre, abuelo, etc.). |
| **Descendiente** | Empresa que aparece bajando en la jerarquía (hija, nieta, etc.). |
| **Rama** | Línea directa de ancestros y descendientes que conecta dos empresas en el árbol. |
| **Contrato múltiple** (informal) | Una oportunidad que ha generado más de un contrato. |

---

## 3. Cambios al modelo de datos

### 3.1 Entidad `GcEmpresa`

#### Antes

```java
@Entity
@Table(name = "GC_EMPRESA")
public class GcEmpresa {
    @Id private Long id;
    // datos fiscales (numeroDocumento, tipoDocumento, etc.) — sin cambios
    // datos comerciales (razonSocial, dirección, contacto, etc.) — sin cambios
    // auditoría — sin cambios
}
```

#### Después

```java
@Entity
@Table(name = "GC_EMPRESA")
public class GcEmpresa {
    @Id private Long id;
    // datos fiscales — sin cambios
    // datos comerciales — sin cambios

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_padre_id")  // opcional, sin nullable=false
    private GcEmpresa empresaPadre;

    // auditoría — sin cambios
}
```

#### DDL Oracle

```sql
ALTER TABLE GC_EMPRESA ADD (
    empresa_padre_id NUMBER(19)
);

ALTER TABLE GC_EMPRESA ADD CONSTRAINT FK_GC_EMPRESA_PADRE
    FOREIGN KEY (empresa_padre_id) REFERENCES GC_EMPRESA(id);

CREATE INDEX IDX_GC_EMPRESA_PADRE ON GC_EMPRESA(empresa_padre_id);
```

### 3.2 Entidad `GcContrato`

Sin cambios estructurales en columnas — el campo `empresa_id` y `empresa_facturacion_id` ya existen. Lo que cambia es **la lógica de validación**:

- Al crear/actualizar un contrato, validar que `empresa_id` esté en la misma rama del árbol que `oportunidad.empresa_id`.
- El campo `empresa_facturacion_id` mantiene su semántica histórica (puede ser diferente a la empresa cliente, sin restricción adicional por ahora — si tiene valor, debe estar en la misma rama).

### 3.3 Entidad `GcCompromisoIngreso`

#### Cambio significativo: eliminación del campo `empresa_facturacion_id`

El campo `empresaFacturacion` a nivel de compromiso se considera redundante con el nuevo modelo. La empresa se hereda implícitamente del contrato.

#### Antes

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "empresa_facturacion_id")
private GcEmpresa empresaFacturacion;
```

#### Después

```java
// Campo eliminado. La empresa del compromiso es siempre la del contrato.
```

#### DDL Oracle

```sql
-- Pasos para entorno de pruebas (sin migración de datos):
ALTER TABLE GC_COMPROMISO_INGRESO DROP CONSTRAINT FK_GC_COMP_INGR_EMP_FACT;
ALTER TABLE GC_COMPROMISO_INGRESO DROP COLUMN empresa_facturacion_id;
```

### 3.4 Entidad `GcFactura`

Sin cambios estructurales. La factura sigue perteneciendo a una empresa (mediante `empresa_id`). La validación nueva es lógica:

- Al aplicar una factura a un compromiso de ingreso, validar que `factura.empresa_id == compromiso.contrato.empresa_id` (regla RN-23, ver §4).

### 3.5 Entidad `GcOportunidad`

Sin cambios estructurales. Lo que cambia son las **transiciones de estado**:

- `GANADA → CONTRATADA` solo ocurre por acción explícita ("Cerrar contratación").
- Mientras esté GANADA, la oportunidad permite formalizar múltiples contratos.

---

## 4. Reglas de negocio nuevas (RB-MC-XX)

Las reglas se identifican con prefijo `RB-MC-` (Multicontrato). Se integran al backlog general como parte del Mundo 2.

### RB-MC-01 — Jerarquía empresarial: estructura

Las empresas se organizan en árbol jerárquico de profundidad arbitraria. Cada empresa tiene a lo sumo una empresa padre (campo opcional `empresa_padre_id`). El árbol no admite ciclos.

**Enforce:** Validación al crear o actualizar empresa.

### RB-MC-02 — Detección de ciclos

Al asignar una empresa A como hija de una empresa B, el sistema verifica que B **no esté en el subárbol descendiente de A**. Si lo está, rechaza con error `JERARQUIA_CICLO`.

**Enforce:** Backend, antes de persistir el cambio en `empresa_padre_id`.

### RB-MC-03 — Empresa padre opcional al crear

Al crear una nueva empresa, el campo `empresaPadreId` es opcional. Si se omite, la empresa nace como raíz. Si se especifica, la nueva empresa queda como hija de la indicada.

**Enforce:** UI permite el campo opcional con typeahead; backend valida que el ID exista.

### RB-MC-04 — Asignación posterior

Una empresa sin padre puede asignársele uno desde la ficha de detalle. Una empresa con padre puede cambiarlo (sujeto a RB-MC-05 y RB-MC-06).

**Enforce:** Endpoint dedicado de actualización de padre. UI desde sección "Grupo" de la ficha.

### RB-MC-05 — Reasignación con confirmación

Si la empresa que se quiere asignar como hija ya tiene padre, el sistema muestra el padre actual y pide confirmación del cambio.

**Enforce:** UI del modal de asignación. Backend procesa siempre que pase RB-MC-06.

### RB-MC-06 — Bloqueo duro por vínculos activos

No se permite cambiar el padre de una empresa si ella misma o **alguna de sus descendientes** tiene:

- Oportunidades en estado `ABIERTA`, `SEGUIMIENTO` o `GANADA`, o
- Contratos en estado `VIGENTE`, `SUSPENDIDO` o `TERMINADO`.

El sistema responde con `EMPRESA_CON_VINCULOS_ACTIVOS` y un mensaje que detalla los conteos.

**Enforce:** Backend, antes de actualizar `empresa_padre_id`. Validación recursiva sobre todos los descendientes.

### RB-MC-07 — Empresas en la misma rama

Dos empresas están en la misma rama si una es ancestro de la otra. Específicamente:

```
mismaRama(A, B) = esAncestroDe(A, B) OR esAncestroDe(B, A)
```

Donde `esAncestroDe(X, Y)` es cierto si existe un camino descendente desde X que llega a Y.

**Enforce:** Función pura del backend, usada en validaciones de contrato (RB-MC-08).

### RB-MC-08 — Empresa del contrato debe estar en la rama de la oportunidad

Al formalizar un contrato, la empresa cliente del contrato (`empresa_id`) debe pertenecer a la misma rama de la jerarquía que la empresa de la oportunidad (`oportunidad.empresa_id`).

**Casos válidos:**
- Empresa del contrato = empresa de la oportunidad (caso por defecto, retrocompatible).
- Empresa del contrato es descendiente de la empresa de la oportunidad.
- Empresa del contrato es ancestro de la empresa de la oportunidad.

**Casos rechazados:**
- Empresas hermanas o primas en el árbol (no hay relación ancestor↔descendant directa).
- Empresas en grupos completamente distintos.

**Enforce:** Backend, al crear contrato y al cambiar empresa de contrato (RB-MC-09).

### RB-MC-09 — Cambio de empresa del contrato

La empresa cliente de un contrato puede modificarse solo si:

1. La nueva empresa cumple RB-MC-08 (misma rama de la oportunidad).
2. El contrato **no tiene formas de pago en estado `CUMPLIDO` o `PARCIALMENTE_CUMPLIDO`** (es decir, ninguna factura aplicada todavía).

Si el contrato tiene formas de pago facturadas, el cambio se rechaza con `CONTRATO_CON_FACTURACION`.

**Enforce:** Backend, endpoint dedicado de actualización.

### RB-MC-10 — Oportunidad puede generar múltiples contratos

Mientras una oportunidad esté en estado `GANADA`, permite formalizar nuevos contratos sin restricción de cantidad. Cada contrato puede tener una empresa distinta dentro de la rama válida.

**Enforce:** Backend permite múltiples POST a `/oportunidades/{id}/formalizar-contrato`. UI muestra botón "Formalizar nuevo contrato" en la ficha mientras estado = GANADA.

### RB-MC-11 — Cierre manual de la oportunidad

La transición `GANADA → CONTRATADA` ocurre solo por acción explícita del usuario ("Cerrar contratación"). El sistema **no cierra automáticamente** la oportunidad al formalizar el primer contrato.

**Enforce:** Backend, endpoint dedicado `POST /api/v1/oportunidades/{id}/cerrar-contratacion`. UI desde ficha de detalle.

### RB-MC-12 — Cierre requiere al menos un contrato

Una oportunidad solo puede cerrarse a `CONTRATADA` si tiene al menos un contrato formalizado. Si no tiene ningún contrato, el sistema rechaza con `OPORTUNIDAD_SIN_CONTRATOS`.

**Enforce:** Backend, validación en endpoint de cierre.

> **Nota:** este no estaba explícito en el cuestionario, pero se deriva lógicamente. Si una oportunidad GANADA no llega a generar ningún contrato, debería cerrarse vía `PERDIDA` o `NO_CONCRETADA`, no `CONTRATADA`.

### RB-MC-13 — Inmutabilidad de oportunidad CONTRATADA

Una vez cerrada (`CONTRATADA`), la oportunidad es inmutable: no permite formalizar nuevos contratos, no permite cambiar pipeline ni etapa, no permite editar datos básicos.

**Enforce:** Backend, todos los endpoints de modificación. (Ya existe en gran parte; ahora se refuerza con la nueva semántica.)

### RB-MC-14 — Empresa del compromiso heredada del contrato

La empresa cliente de una forma de pago (compromiso de ingreso) es siempre la del contrato. No existe campo `empresa_facturacion` a nivel de compromiso.

**Enforce:** Modelo de datos (campo eliminado). Service del compromiso usa siempre `compromiso.contrato.empresa`.

### RB-MC-15 — Factura debe pertenecer a la empresa del contrato

Al aplicar una factura a un compromiso de ingreso, la empresa de la factura debe coincidir con la empresa del contrato del compromiso.

```
factura.empresa.id == compromiso.contrato.empresa.id
```

Si no coincide, el sistema rechaza con `FACTURA_EMPRESA_INCORRECTA`.

**Reemplaza a RN-23 propuesta en sesión anterior**, ahora simplificada (sin jerarquía: la regla es estricta porque la jerarquía ya se enforce en el contrato, no a nivel de cruce).

**Enforce:** Backend, en `CompromisoIngresoService.registrarFactura()`.

### RB-MC-16 — Listado de facturas aplicables filtrado por empresa

El modal "Aplicar factura" muestra solo facturas cuya empresa coincide con la del contrato del compromiso. Esto se logra:

- UI: el modal pasa `?empresa_id={contrato.empresaId}&disponibles=true` al listar facturas.
- Backend: filtro automático en `GET /facturas?empresa_id=X` (ya existente).

**Enforce:** Combinación UI + backend.

---

## 5. Cambios de UI

### 5.1 Vista de detalle de empresa (`EmpresasView` → detalle)

Hoy no existe una vista de detalle dedicada por empresa. Esta refactorización requiere crearla o, alternativamente, expandir el modal de edición. Decisión preliminar: **crear vista dedicada `EmpresaDetalleView.vue`** con ruta `/empresas/:id`.

**Secciones:**

1. **Header:** nombre, NIT, badge de tipo (cliente / proveedor / aliado).
2. **Datos generales:** razón social, dirección, contacto, etc.
3. **Sección "Grupo empresarial"** (nueva):
   - Si tiene padre: breadcrumb ascendente "Esta empresa › Padre › Abuelo › ... › Raíz".
   - Si tiene hijas: lista expandible con conteo "N empresas hijas".
   - Acciones:
     - "Vincular a un grupo" (si no tiene padre).
     - "Cambiar padre" (si tiene padre, con typeahead).
     - "Desvincular del grupo" (si tiene padre, sujeto a RB-MC-06).
4. **Vínculos:** oportunidades, contratos, personas asociadas (vistas existentes ya cubren parcialmente).

### 5.2 Modal de edición/creación de empresa

Se agrega un campo opcional **"Empresa padre"** al modal `EmpresaModal.vue`:

- Por defecto vacío (la empresa nace como raíz).
- Campo typeahead con todas las empresas existentes (excluyendo la actual si es edición).
- Si se selecciona, valida que no genere ciclo (RB-MC-02) al guardar.

### 5.3 Modal de formalización de contrato

Cambios al modal `FormalizarContratoModal.vue`:

- Se agrega un campo "Empresa cliente del contrato" con typeahead.
- El typeahead filtra a las empresas válidas (la de la oportunidad + ancestros + descendientes).
- Preseleccionado: la empresa de la oportunidad.
- El listado lo provee un endpoint nuevo (`GET /api/v1/empresas/jerarquia?oportunidad_id=N`).

### 5.4 Ficha de detalle de oportunidad

Cambios a `OportunidadDetalleView.vue`:

- **Sección "Acciones"** (nueva o expandida):
  - Si estado = `GANADA` y comercial autorizado:
    - Botón "Formalizar nuevo contrato" (abre `FormalizarContratoModal`).
    - Botón "Cerrar contratación" (abre modal de confirmación → marca CONTRATADA).
  - Si estado = `CONTRATADA`: acciones de solo lectura.
- **Sección "Contratos formalizados"** (nueva): lista de todos los contratos derivados de esta oportunidad, con empresa, valor, estado, link a detalle.

### 5.5 Kanban contractual (`PipelineView.vue`)

Cambios visuales:

- **Eliminar** el botón "Formalizar" de las cards del Kanban contractual.
- **Agregar** badge contador "N contratos" en cards de oportunidades GANADA que tienen contratos formalizados.
- Click en la card → abre ficha de detalle (donde están todas las acciones).
- Click en el badge → opcional: expande lista o navega a la sección "Contratos formalizados" de la ficha.

### 5.6 Ficha de detalle de contrato

Cambios menores a `ContratoDetalleView.vue`:

- En "Datos del Contrato", el campo "Cliente" muestra la empresa del contrato (puede ser distinta a la de la oportunidad).
- Nueva acción "Cambiar empresa del contrato" (botón discreto cerca de "Cliente"), abre modal con typeahead filtrado a la rama válida. Sujeto a RB-MC-09.

---

## 6. Impacto en código existente

### 6.1 Backend (estimado)

| Archivo / paquete | Cambio |
|---|---|
| `GcEmpresa.java` | Agregar campo `empresaPadre` (FK auto-referencial). |
| `GcEmpresaRepository.java` | Métodos nuevos: `findDescendientes`, `findAncestros`, `findByEmpresaPadreId`, `countActivosByRama`. |
| `EmpresaService.java` | Métodos: `vincularAGrupo`, `desvincularDelGrupo`, `cambiarPadre`. Validaciones de ciclo y vínculos activos. |
| `EmpresaController.java` | Endpoints nuevos: `PUT /empresas/{id}/padre`, `GET /empresas/{id}/grupo`, `GET /empresas/jerarquia`. |
| `EmpresaCreateRequest.java` / `EmpresaUpdateRequest.java` | Agregar campo opcional `empresaPadreId`. |
| `EmpresaResponse.java` | Agregar campos `empresaPadreId`, `empresaPadreNombre`, `linaje`, `tieneHijas`. |
| `GcContrato.java` | Sin cambios estructurales (campos ya existen). |
| `ContratoService.java` | Modificar `formalizarContrato` para validar RB-MC-08. Método nuevo `cambiarEmpresa` con RB-MC-09. |
| `ContratoController.java` | Endpoint nuevo: `PUT /contratos/{id}/empresa`. |
| `OportunidadService.java` | Modificar lógica: GANADA no transiciona automáticamente. Nuevo método `cerrarContratacion`. |
| `OportunidadController.java` | Endpoint nuevo: `POST /oportunidades/{id}/cerrar-contratacion`. Mantener `formalizar-contrato` permitiendo múltiples llamadas. |
| `GcCompromisoIngreso.java` | Eliminar campo `empresaFacturacion`. |
| `CompromisoIngresoCreateRequest.java` | Eliminar campo `empresaFacturacionId`. |
| `CompromisoIngresoResponse.java` | Eliminar campos `empresaFacturacionId`, `empresaFacturacionNombre`. |
| `CompromisoIngresoService.java` | Eliminar lógica que usa `empresaFacturacion`. Reforzar RB-MC-15 al aplicar factura. |
| `V*.sql` (Flyway) | DDL nuevo: `ALTER TABLE GC_EMPRESA ADD empresa_padre_id`, `ALTER TABLE GC_COMPROMISO_INGRESO DROP empresa_facturacion_id`. |

### 6.2 Frontend (estimado)

| Archivo / componente | Cambio |
|---|---|
| `EmpresaModal.vue` | Agregar campo "Empresa padre" con typeahead. |
| `EmpresasView.vue` | Agregar click en fila → navegación a `/empresas/:id`. |
| `EmpresaDetalleView.vue` | **Nuevo archivo.** Vista completa con sección Grupo. |
| `EmpresaGrupoSection.vue` | **Nuevo componente.** Maneja la visualización y acciones del grupo. |
| `EmpresaSearchSelect.vue` | **Nuevo componente reutilizable.** Typeahead de empresas con filtro opcional por rama. |
| `empresa.service.js` | Métodos nuevos: `vincularAGrupo`, `cambiarPadre`, `obtenerGrupo`, `listarPorJerarquia`. |
| `empresa.store.js` | Adaptar al nuevo formato de `EmpresaResponse` (campos `empresaPadre*`). |
| `FormalizarContratoModal.vue` | Usar `EmpresaSearchSelect` filtrado por jerarquía de la oportunidad. |
| `OportunidadDetalleView.vue` | Agregar sección "Acciones" con botones "Formalizar nuevo" y "Cerrar contratación". Agregar sección "Contratos formalizados". |
| `oportunidad.service.js` | Método nuevo: `cerrarContratacion`. |
| `PipelineView.vue` | Quitar botón "Formalizar" de cards Kanban. Agregar badge contador "N contratos". |
| `ContratoDetalleView.vue` | Botón "Cambiar empresa del contrato". Adaptar a campos sin `empresaFacturacion`. |
| `contrato.service.js` | Método nuevo: `cambiarEmpresa`. |
| `GestionPanel.vue` (Mundo 3) | Adaptar a `CompromisoIngresoResponse` sin `empresaFacturacion`. |
| `router/index.js` | Agregar ruta `/empresas/:id`. |
| `AppLayout.vue` | Sin cambios. |

### 6.3 Skills y documentación

| Documento | Cambio |
|---|---|
| `gestcom-context.SKILL.md` | Glosario actualizado (§6, §11). Decisiones congeladas: jerarquía empresarial, multicontrato. |
| `gestcom-backend.SKILL.md` | Patrones nuevos: auto-referencia JPA, queries recursivas (CTE), validaciones de jerarquía. |
| `gestcom-frontend.SKILL.md` | Patrón nuevo: typeahead reutilizable, vistas de detalle anidadas. |
| `GestCom_Mundo3_Especificacion_v4.md` → v5 | Actualizar RN-23 a RB-MC-15. Eliminar referencias a `empresaFacturacion` en compromisos. |
| `Estado_arte_*.md` (próximo corte) | Documentar que F-MC se completó. |

---

## 7. Plan de implementación

### 7.1 Estrategia general

Dado que no hay migración formal (datos de prueba), la estrategia es **construir limpio**:

1. Backup completo de la BD (precaución mínima).
2. Aplicar cambios al modelo (DDL + entidades JPA + repos).
3. Limpiar tablas afectadas con `DELETE`.
4. Implementar nuevas funcionalidades (servicios + controllers).
5. Aplicar cambios frontend.
6. Recrear datos de prueba consistentes con el nuevo modelo y validar.

### 7.2 Fases (backlog F-MC)

#### F-MC.1 — Modelo de datos y entidades base

**Objetivo:** dejar la BD y las entidades JPA al nuevo modelo.

- T1.1: Agregar columna `empresa_padre_id` a `GC_EMPRESA` + FK + índice (script Flyway).
- T1.2: Eliminar columna `empresa_facturacion_id` de `GC_COMPROMISO_INGRESO` (script Flyway).
- T1.3: Actualizar entidad `GcEmpresa` con FK auto-referencial.
- T1.4: Actualizar entidad `GcCompromisoIngreso` (eliminar `empresaFacturacion`).
- T1.5: Actualizar repos: `findDescendientes`, `findAncestros`, `countActivosByRama` (con CTE recursivo).

**Estimado:** 1 sesión.

#### F-MC.2 — Servicios y validaciones backend

**Objetivo:** lógica de negocio de jerarquía y validaciones.

- T2.1: `EmpresaService.vincularAGrupo`, `desvincularDelGrupo`, `cambiarPadre`, con RB-MC-02 (ciclo) y RB-MC-06 (vínculos).
- T2.2: `ContratoService.formalizarContrato` con RB-MC-08 (rama).
- T2.3: `ContratoService.cambiarEmpresa` con RB-MC-09.
- T2.4: `OportunidadService` adaptado: no auto-cierre + nuevo método `cerrarContratacion` (RB-MC-11, RB-MC-12).
- T2.5: `CompromisoIngresoService.registrarFactura` refuerza RB-MC-15.

**Estimado:** 1-2 sesiones.

#### F-MC.3 — Controllers y DTOs

**Objetivo:** API REST nueva expuesta.

- T3.1: Endpoints nuevos de empresa: `PUT /padre`, `GET /grupo`, `GET /jerarquia`.
- T3.2: Endpoint nuevo de contrato: `PUT /empresa`.
- T3.3: Endpoint nuevo de oportunidad: `POST /cerrar-contratacion`.
- T3.4: DTOs actualizados (`EmpresaCreateRequest`, `EmpresaResponse`, etc.).

**Estimado:** 0.5 sesión.

#### F-MC.4 — Vista de detalle de empresa

**Objetivo:** sustento de UI para gestionar jerarquía.

- T4.1: `EmpresaDetalleView.vue` (vista nueva).
- T4.2: `EmpresaGrupoSection.vue` (componente sección Grupo).
- T4.3: `EmpresaSearchSelect.vue` (componente typeahead reutilizable).
- T4.4: Ruta `/empresas/:id`.
- T4.5: `EmpresaModal.vue` con campo "Empresa padre".

**Estimado:** 1-1.5 sesiones.

#### F-MC.5 — Refactor del flujo de formalización

**Objetivo:** aplicar las decisiones C, E al UI.

- T5.1: `FormalizarContratoModal.vue` usa `EmpresaSearchSelect`.
- T5.2: `OportunidadDetalleView.vue` con sección "Acciones" + "Contratos formalizados".
- T5.3: `PipelineView.vue`: quitar "Formalizar" del Kanban + agregar badge contador.
- T5.4: `ContratoDetalleView.vue` con botón "Cambiar empresa del contrato".

**Estimado:** 1-1.5 sesiones.

#### F-MC.6 — Cierre y validación

**Objetivo:** datos de prueba consistentes + pruebas integrales.

- T6.1: Crear datos de prueba: árbol de "Grupo Bancolombia" con 3 niveles.
- T6.2: Probar flujo completo: oportunidad → múltiples contratos → formas de pago → facturas.
- T6.3: Validar las 15 reglas RB-MC-XX una por una.
- T6.4: Actualizar skills `gestcom-context`, `gestcom-backend`, `gestcom-frontend`.
- T6.5: Generar spec v5 del Mundo 3 (incorpora RB-MC-15).
- T6.6: Generar `Estado_arte_*.md` actualizado.

**Estimado:** 1 sesión.

### 7.3 Total estimado

**6 fases ≈ 5.5–7 sesiones de trabajo.**

Recomendación: ejecutar las fases en orden estricto. Cada fase termina con un estado validable; no avanzar si la fase actual no quedó verde.

---

## 8. Impacto en spec del Mundo 3 (v4 → v5)

El documento `GestCom_Mundo3_Especificacion_v4.md` se actualizará a v5 una vez que F-MC.5 esté listo. Cambios principales:

| Sección de v4 | Cambio en v5 |
|---|---|
| §2.1 Entidades — `GcCompromisoIngreso` | Eliminar campo `empresa_facturacion_id`. Documentar que la empresa se hereda del contrato. |
| §3 Reglas RN-XX | Reemplazar RN-23 (que estaba propuesta pero no formalizada en v4) por RB-MC-15. |
| §0 Bitácora | Agregar entrada del cambio. |

La matriz `GestCom_Mundo3_Matriz_RN_Endpoints.md` se actualiza correspondientemente.

---

## 9. Decisiones congeladas (referencia del cuestionario)

Estas decisiones se documentan aquí como **referencia inmutable**. Cualquier cambio futuro requiere actualizar este documento.

| ID | Decisión |
|---|---|
| A.1 | Oportunidad → CONTRATADA solo por acción manual del comercial |
| A.2 | Todo contrato nace de una oportunidad. Empresa del contrato puede diferir, dentro de la rama del árbol |
| A.3.1 | Jerarquía con profundidad arbitraria (árbol) |
| A.3.2 | "Mismo grupo" = ancestro común (línea directa, no hermanas) |
| A.3.3 | Dirección: subir o bajar libremente en la rama |
| B.1 | Padre se declara al crear (opcional) o después desde la ficha |
| B.2 | Cambiar padre con vínculos activos: bloqueo duro |
| B.3 | Empresa ya con padre: reasignación con confirmación |
| B.4 | Ciclos: bloqueo silencioso con mensaje claro |
| B.5 | Todas las empresas son entidades fiscales con NIT propio |
| C.1 | "Cerrar oportunidad" desde ficha de detalle |
| C.2 | Contratos activos = VIGENTE + SUSPENDIDO + TERMINADO |
| C.3 | Cambiar empresa del contrato: solo si no hay formas de pago facturadas |
| C.4 | Kanban: card con empresa oportunidad + contador "N contratos" |
| D.2 | Sin migración formal (datos de prueba) |
| E.1 | Selector con typeahead filtrado a la rama válida |
| E.2 | Contratos adicionales: solo desde ficha |
| E.3 | Primer contrato también desde ficha. Quitar "Formalizar" del Kanban |
| E.4 | Término oficial: "Grupo empresarial" / "Grupo" |
| E.5 | Sección "Grupo" en ficha con árbol + acciones |

---

## 10. Próximos pasos

1. **Revisión y aprobación** de este documento por Hector.
2. **Ajustes** si surgen observaciones.
3. **Arranque de F-MC.1** (modelo de datos + entidades base).
4. Iteración por fases hasta F-MC.6.

---

*Fin del documento.*
