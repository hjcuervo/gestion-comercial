# GestCom — Alcance: Directorio unificado (Empresas + Personas)

**Empresa:** Arquitecsoft SAS · **Producto:** GestCom
**Tipo de cambio:** Capa de presentación (frontend). **No toca backend, entidades, endpoints ni reglas de negocio.**
**Identidad:** "Instrumento" (se respeta tal cual; tokens `--gc-*`, renglones, Tabler).
**Estado:** Borrador para acordar antes de codificar.

---

## 1. Propósito

Hoy Empresas y Personas son **dos módulos separados** en la barra superior, cada uno con su pantalla maestro-detalle. El objetivo es **un solo lugar — "Directorio"** para mantener ambos sin saltar de módulo, navegando entre empresa y persona a través de la relación que ya existe (`GcPersonaEmpresa`).

**Enfoque elegido (A):** Directorio con **conmutador Empresas | Personas** en la lista maestra. No se mezclan ambas entidades en una sola lista; se conmuta el modo y el detalle muestra la entidad seleccionada. El puente entre ambas es la navegación cruzada por la relación.

---

## 2. Qué NO cambia

- **Backend:** nada. Mismos endpoints (`/empresas`, `/personas`, `/personas/{id}/empresas`, `/catalogos/*`, `/usuarios`).
- **Reglas de negocio, entidades, servicios, stores, utils.**
- **Identidad visual "Instrumento"** y la plantilla **Operativo** (master + surface + aside).
- **Modales y paneles ya hechos:** `EmpresaModal`, `PersonaModal`, `AsociarEmpresaModal`, `ContactosPanel`, `ContactoDrawer` se **reutilizan tal cual**.
- Los demás módulos (Venta/Oportunidades/Pipeline, Contratación, Facturación) quedan intactos.

---

## 3. Decisiones de diseño

1. **Un solo ítem de navegación "Directorio"** reemplaza a "Empresas" y "Personas" en el subnav del módulo `venta`.
2. **Conmutador en la cabecera de la lista maestra:** dos pestañas/segmentos `Empresas` | `Personas`. El modo activo decide qué lista el `#master` y qué detalle pinta la superficie.
3. **El modo se refleja en la URL** (deep-link y botón atrás): `/directorio/empresa/:id?` y `/directorio/persona/:id?`. Sin `:id` = lista sin selección (estado vacío "selecciona…").
4. **Navegación cruzada (el corazón de la unificación):**
   - En el detalle de **empresa**, la sección de personas asociadas es **clicable** → cambia a modo Personas y abre esa persona.
   - En el detalle de **persona**, la lista de empresas asociadas es **clicable** → cambia a modo Empresas y abre esa empresa.
5. **El conmutador preserva contexto mínimo:** al cambiar de modo se va a la lista de ese modo (sin arrastrar selección), salvo en la navegación cruzada (que sí abre el destino concreto).
6. **CRUD completo en ambos modos**, idéntico al actual: crear/editar empresa o persona, asociar/desasociar, gestionar contactos. Nada se pierde respecto a las pantallas separadas.

---

## 4. Arquitectura (cómo se implementa sin romper el shell)

- **Nueva vista contenedora `DirectorioView.vue`** con plantilla Operativo:
  - Lee el **modo** desde la ruta (`empresa` | `persona`).
  - Renderiza el **conmutador** + la **lista maestra** del modo activo en `#master` (Teleport, patrón actual con `setRegions`/`reset`).
  - En la superficie, delega el **detalle** según el modo. Para no reescribir, se extrae el cuerpo de detalle actual de cada vista a dos componentes reutilizables:
    - `directorio/EmpresaDetalle.vue` (lo que hoy vive en `EmpresasView`).
    - `directorio/PersonaDetalle.vue` (lo que hoy vive en `PersonasView`).
  - El `#aside` lo provee el detalle activo (contactos + relaciones), igual que hoy.
- **Router:** se reemplazan las rutas `/empresas/:id?` y `/personas/:id?` por:
  - `/directorio/empresa/:id?` → `DirectorioView` (modo empresa)
  - `/directorio/persona/:id?` → `DirectorioView` (modo persona)
  - `/directorio` → redirige a `/directorio/empresa` (modo por defecto).
  - **Redirecciones de compatibilidad:** `/empresas/:id?` → `/directorio/empresa/:id?` y `/personas/:id?` → `/directorio/persona/:id?` (no rompe enlaces ni hábitos).
- **`useModule.js`:** en el subnav de `venta`, las dos entradas `Empresas` y `Personas` se sustituyen por una sola `{ path: '/directorio', label: 'Directorio', icon: 'address-book' }`. El Ctrl+K se actualiza solo (deriva de `MODULES`).
- **Composición de zonas:** se mantiene `useShell` + Teleport a `#gc-shell-master` / `#gc-shell-aside`, con el cuidado de timing ya conocido (`:disabled` hasta `nextTick`).

---

## 5. Backlog (F-DIR)

| Tarea | Descripción | Esf. |
|-------|-------------|------|
| **T-DIR.1** | Extraer el detalle actual de Empresa a `directorio/EmpresaDetalle.vue` (sin cambiar comportamiento). | M |
| **T-DIR.2** | Extraer el detalle actual de Persona a `directorio/PersonaDetalle.vue`. | M |
| **T-DIR.3** | `DirectorioView.vue`: conmutador + lista maestra por modo + montaje del detalle según ruta. | L |
| **T-DIR.4** | Router: rutas `/directorio/...`, redirección por defecto y redirecciones de compatibilidad desde `/empresas` y `/personas`. | S |
| **T-DIR.5** | `useModule.js`: subnav de venta con un solo "Directorio" (verificar icono Tabler `address-book`). | S |
| **T-DIR.6** | Navegación cruzada empresa↔persona desde los detalles (clic en relación → cambia modo y abre destino). | M |
| **T-DIR.7** | Limpieza: retirar `EmpresasView.vue` y `PersonasView.vue` una vez `DirectorioView` los cubre (o dejarlos como thin-wrappers temporales). | S |
| **T-DIR.8** | Actualizar skill `gestcom-frontend` (un módulo Directorio; deja de aplicar F-UI5/RF6 separados). | S |

**Orden sugerido:** T-DIR.1 y T-DIR.2 (extraer detalles, sin riesgo) → T-DIR.3/T-DIR.4/T-DIR.5 (arman la vista y el ruteo) → T-DIR.6 (cross-link) → T-DIR.7/T-DIR.8 (cierre).

---

## 6. Riesgos y mitigaciones

| Riesgo | Mitigación |
|--------|------------|
| Romper deep-links/hábitos a `/empresas` y `/personas`. | Redirecciones de compatibilidad (T-DIR.4). |
| Regresión al extraer detalles a componentes. | Extraer sin cambiar lógica; probar CRUD y aside antes de seguir. |
| Conflicto de zonas master/aside al conmutar modo. | Un solo `DirectorioView` controla `setRegions`/`reset`; el detalle activo provee el aside. |
| Icono Tabler inexistente. | Verificar `address-book`; si no, usar `users` o `building`. |
| Desviación del plan "Instrumento" (F-UI5/RF6). | Registrada aquí; se actualiza la skill en T-DIR.8. |

---

## 7. Criterio de aceptación

- Un solo "Directorio" en la barra; conmutador Empresas | Personas funcional.
- CRUD de empresa y de persona, contactos y asociación, **sin pérdida** frente a las pantallas separadas.
- Deep-link a `/directorio/empresa/:id` y `/directorio/persona/:id` + botón atrás OK; `/empresas` y `/personas` redirigen.
- Desde una empresa se salta a una de sus personas y viceversa, en la misma página.

---

## 8. Decisiones abiertas

| # | Decisión | Recomendación |
|---|----------|---------------|
| D-1 | Etiqueta e icono del módulo | "Directorio" + `address-book` (fallback `users`). |
| D-2 | Modo por defecto al entrar | Empresas. |
| D-3 | ¿Retirar las vistas viejas ya (T-DIR.7) o dejarlas como wrappers un sprint? | Retirar al validar; bajo riesgo por las redirecciones. |
| D-4 | ¿El conmutador recuerda el último modo usado? | Opcional; por defecto respeta la URL. |

---

*Documento de alcance — Directorio unificado (enfoque A). Base para acordar antes de codificar.*
