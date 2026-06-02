---
name: gestcom-frontend
description: Use this skill whenever creating, modifying, or reviewing frontend code for the GestCom platform — Vue 3 + Vite + Pinia. Triggers include writing or editing .vue views and components, Pinia stores, axios services, router routes, AppShell zones, list rows, status badges, the command palette, theme/density toggles, the unified Directorio (Empresas + Personas), contact channels (contactabilidad), or any UI work in the gestion-comercial project. The visual system is "Instrumento": a LIGHT foundation (warm paper), graphite primary (color is reserved for state only), monospaced data, hairline rows instead of cards, and Tabler outline icons, implemented through --gc-* design tokens. Always load gestcom-context first to align with the broader project conventions.
---

# GestCom — Convenciones Frontend ("Instrumento")

Esta skill define **cómo se construye el frontend de GestCom** tras el rediseño completo a la identidad **"Instrumento"** (rama `feature/rediseno-instrumento`, mergeada a `main`). Reemplaza por completo al sistema anterior "Aurora Dark" / "Luxury Tech", que fue **demolido** (componentes y estilos eliminados del repo).

> **Prerequisito:** Cargar primero `gestcom-context`.
>
> **Fuente de verdad:** los patrones aquí descritos se validaron contra el código en `main`. Si el código real diverge de esta skill, **gana el código real** — actualizar la skill.

---

## 1. Identidad "Instrumento"

GestCom dejó de ser una "vitrina" oscura para ser una **consola operativa** clara. Principios:

1. **La operación primero.** Si una decisión visual no ayuda a operar más rápido o ver más, no entra.
2. **El color SOLO significa estado.** success/warning/danger/info/accent se reservan a estados de negocio. El color nunca es decorativo. El primario de marca es **grafito** (casi negro), no un color.
3. **Los datos van en monoespaciado** (DM Mono): valores, fechas, IDs, conteos, códigos de estado. Alinea cifras y da carácter de instrumento. Clase utilitaria global: `.gc-mono`.
4. **Renglones, no tarjetas.** Las listas son renglones (`GcListRow`) separados por líneas finas (hairline), sin sombras flotantes.
5. **Densidad alta** con toggle cómodo/compacto.
6. **Sin saltos de contexto.** Lista maestra + detalle en la misma pantalla (plantilla Operativo).

Fundación **clara por defecto**; modo oscuro suave opcional (slate de bajo contraste, nunca negro puro) por toggle.

---

## 2. Stack y filosofía

- **Vue 3** con **Composition API** (`<script setup>`) en vistas y componentes.
- **Pinia** con **Options API** en stores (disonancia heredada; mantener la convención del repo).
- **Vite** como bundler. Alias `@` → `frontend/src/`.
- **Axios** para HTTP (interceptor JWT + redirección en 401).
- **Iconos: Tabler outline** vía webfont (`@tabler/icons-webfont`), un solo set, expuesto por `GcIcon`.
- **Tipografías:** DM Sans (UI) + DM Mono (datos), importadas en `instrument.css`.
- **No Tailwind, no framework de UI prefabricado, no Vuex.** El styling combina `instrument.css` (tokens + reset global + base) con `<style scoped>` por SFC, basado en tokens `--gc-*`.

---

## 3. Sistema de diseño — tokens `--gc-*`

Todos los tokens viven en `assets/styles/instrument.css`, en `:root` (tema claro) y `[data-theme="dark"]` (tema oscuro). **Nunca hardcodear colores ni medidas; usar siempre tokens `--gc-*`.**

### 3.1 Neutros (fundación clara)

| Token | Rol | Claro |
|-------|-----|-------|
| `--gc-bg` | Fondo de la app (papel tibio) | `#f5f4f1` |
| `--gc-surface` | Superficie: paneles, renglones | `#ffffff` |
| `--gc-surface-2` | Superficie alterna / hover | `#f0eee9` |
| `--gc-border` | Línea fina por defecto | `rgba(0,0,0,0.08)` |
| `--gc-border-strong` | Línea de énfasis | `rgba(0,0,0,0.14)` |
| `--gc-text` | Texto principal | `#1a1917` |
| `--gc-text-2` | Texto secundario | `#6b6860` |
| `--gc-text-3` | Texto terciario / pistas | `#9e9c96` |

### 3.2 Primario (grafito) y estado

| Token | Rol | Claro |
|-------|-----|-------|
| `--gc-primary` | Primario / marca (grafito) | `#1a1917` |
| `--gc-primary-hover` | Hover del primario | `#322f2b` |
| `--gc-primary-text` | Texto sobre primario | `#ffffff` |
| `--gc-success` | Estado positivo (VIGENTE, GANADA, FACTURADA, CLIENTE) | `#1d9e75` |
| `--gc-warning` | Estado intermedio (SUSPENDIDO, SEGUIMIENTO) | `#ef9f27` |
| `--gc-danger` | Estado negativo (PERDIDA, TERMINADO, error) | `#e24b4a` |
| `--gc-info` | Estado informativo (ABIERTA, PROSPECTO) | `#185fa5` |
| `--gc-accent` | Acento secundario (CONTRATADA, ALIADO, contacto principal) | `#534ab7` |

Cada estado tiene su variante `*-soft` (tinte sutil de fondo): `--gc-success-soft`, `--gc-warning-soft`, `--gc-danger-soft`, `--gc-info-soft`, `--gc-accent-soft`, `--gc-neutral-soft`.

### 3.3 Espaciado, radios, tipografía, sombras, layout, z, transición

- **Espaciado** (múltiplos de 4px): `--gc-space-1` (4px) … `--gc-space-12` (48px).
- **Radios** (sobrios): `--gc-radius-sm` (4px), `--gc-radius-md` (6px), `--gc-radius-lg` (10px), `--gc-radius-full`.
- **Fuente:** `--gc-font-sans` (DM Sans), `--gc-font-mono` (DM Mono). Pesos: `--gc-fw-regular` (400), `--gc-fw-medium` (500). Tamaños: `--gc-fs-xs` (11px) … `--gc-fs-2xl` (26px); base `--gc-fs-md` (14px).
- **Line-height:** `--gc-lh-tight` (1.25), `--gc-lh-normal` (1.45).
- **Sombras** (mínimas, solo capas flotantes): `--gc-shadow-pop`, `--gc-shadow-drawer`.
- **Layout del shell:** `--gc-topbar-h` (50px), `--gc-master-w` (280px), `--gc-aside-w` (340px), `--gc-content-max` (1440px).
- **Z-index:** `--gc-z-topbar` (100), `--gc-z-drawer` (200), `--gc-z-modal` (300), `--gc-z-toast` (400).
- **Transición:** `--gc-t-fast` (120ms), `--gc-t-normal` (200ms).
- **Densidad:** `--gc-row-pad-y` / `--gc-row-pad-x` / `--gc-row-gap` (ver §7).

`instrument.css` también incluye el **reset global** (`*`, `html`, `body` con `--gc-bg`, headings, scrollbar sobrio, focus-visible) y `.animate-spin`. No hay otra hoja de estilos global.

---

## 4. Arquitectura: shell único + 3 plantillas

### 4.1 `AppShell.vue` (único layout)

Toda la app vive dentro de **`components/layout/AppShell.vue`**, montado desde `App.vue` según `route.meta.layout`:

- `'blank'` → login (sin chasis).
- `'app'` → AppShell (todas las pantallas migradas; es el valor por defecto).

El `AppShell` provee: barra superior (marca + navegación de módulos horizontal + Ctrl+K + toggles de densidad/tema + menú de usuario) y una región de contenido con tres zonas: **master** (izquierda), **surface** (centro), **aside** (derecha).

La navegación tiene **dos niveles**, derivados de la constante `MODULES` en `composables/useModule.js`: el nivel de módulo (`route.meta.module`: `venta`, `contratacion`, `facturacion`) y el subnav de pantallas de ese módulo. El módulo `venta` agrupa Dashboard, Oportunidades, Pipeline y **Directorio** (ver §6.4).

### 4.2 Las 3 plantillas (por zonas activas)

| Plantilla | Zonas | Pantallas |
|-----------|-------|-----------|
| **Operativo** | master + surface + aside | Oportunidades/Actividades, Contratos, **Directorio** (Empresas + Personas) |
| **Tablero** | solo surface (full width) | Pipeline Kanban |
| **Panel** | solo surface (grilla) | Dashboard |

### 4.3 `useShell` — control de zonas

Las zonas se controlan con el composable **`composables/useShell.js`** (singleton reactivo: `{ regions, setRegions, reset }`). Como el contenido de la vista se inyecta en el árbol de `App.vue` (no de `AppShell`), se usa un singleton de módulo en vez de provide/inject.

**Convención obligatoria por vista 'app':**
- En `setup`/`onMounted`: fijar las zonas que usa con `setRegions({ master: true, aside: true })`.
- En `onUnmounted`: `reset()`.
- El `AppShell` llama `reset()` en su propio setup al entrar al mundo 'app'.

Para inyectar contenido en las zonas master/aside, las vistas usan **Teleport** a los contenedores del shell (`#gc-shell-master`, `#gc-shell-aside`). Patrón con cuidado de timing: activar la zona en setup y usar `:disabled` del Teleport (o un guard `asideReady`) hasta que el target exista (`nextTick`).

---

## 5. Biblioteca de componentes `Gc*`

Una sola implementación por tipo, en `components/ui/`. **Reutilizar; no reinventar.**

| Componente | Rol / API resumida |
|-----------|--------------------|
| `GcButton` | Botón. Variantes por prop (`primary` grafito, `default`, `danger`/`ghost`); tamaños `sm`/`md`/`lg`. Prop `full-width`, `icon`. |
| `GcInput`, `GcSelect`, `GcTextarea` | Campos de formulario (alto 34px, tokens `--gc-*`). `GcInput` admite `icon`, `mono`, `type` (incl. `date`), `error`. `GcSelect` recibe `:options` `[{value,label}]`. |
| `GcBadge` | Marca de estado. `tone`: success\|warning\|danger\|info\|accent\|neutral; `soft` para fondo tenue. |
| `GcListRow` | **Renglón de lista/bitácora.** Marca de estado lateral 2px + línea fina inferior, sin sombra. Slots: `lead` (icono/avatar), default (contenido), `actions` (al final). Props: `tone`, `clickable`, `active`. Emite `click`. Si hay acciones dentro de un renglón clicable, usar `@click.stop` en el botón para no disparar el clic del renglón. |
| `GcStat`, `GcStatStrip` | Métrica individual / franja compacta de métricas (Dashboard). |
| `GcModal` | Diálogo centrado. Props: `open`, `title`, `width`. Slot `#footer`. |
| `GcDrawer` | Cajón lateral (props incluyen `title`, `subtitle`). |
| `GcEmpty` | Estado vacío. Props: `icon` (Tabler, default `inbox`), `message`. |
| `GcSpinner` | Loader. Prop `size`. Sustituye al icono girando del sistema viejo. |
| `GcIcon` | Icono Tabler outline. Props: `name` (id Tabler SIN prefijo `ti-`), `size`, `color`. |
| `GcCommandPalette` | Paleta de comandos (Ctrl+K). Props: `open`, `items` ([{path,label,icon}]). Emite `close`, `navigate`. Navegación por teclado ↑/↓/Enter/Esc + ratón. |

**Iconos Tabler comunes usados:** `search`, `alert-circle`, `x`, `chevron-down`, `logout`, `dashboard`, `target`, `layout-kanban`, `file-text`, `receipt`, `building`, `building-off`, `users`, `user-off`, `address-book`, `hand-click`, `briefcase-off`, `unlink`, `plus`, `edit`, `external-link`, `layout-rows`, `layout-list`, `sun`, `moon`. El `name` es el id Tabler sin `ti-`. Ante duda de si un icono existe, preferir nombres básicos y estables.

---

## 6. Convenciones de vistas

### 6.1 Vista de plantilla Operativo

1. `<script setup>` + `setRegions({ master: true, aside: <bool> })` en setup/`onMounted` y `onUnmounted(reset)`.
2. Importa su `service` (no axios directo).
3. `loading` / empty (`GcEmpty`) / error manejados visualmente.
4. Lista maestra con `GcListRow` (no tablas con tarjetas). Estado por `tone` de la marca lateral.
5. Detalle en la superficie; captura/relaciones en el aside.
6. Formatters de `utils/` (ver §9). Iconos por `GcIcon`. Tokens `--gc-*`, nunca hardcodear.

### 6.2 Vista de plantilla Tablero / Panel

- Tablero (Pipeline): no fija master/aside (`reset()` o no setear). Superficie full width.
- Panel (Dashboard): grilla de `GcStat`/`GcStatStrip`; sin master/aside.

### 6.3 Modales y drawers

- En `components/{dominio}/`. Se abren por estado local (`ref(false)`), reciben props, emiten `@close`/`@submit`/`@guardar` (verbo según el caso).
- Usar `GcModal` o `GcDrawer` como envoltura; estilos con tokens `--gc-*`. No usar `<form>` con submit nativo; se usan handlers (`@click`).
- Modales/drawers de dominio vigentes: `empresa/EmpresaModal`, `persona/PersonaModal`, `persona/AsociarEmpresaModal`, `contacto/ContactoDrawer`, `contacto/ContactosPanel` (panel reutilizable, no modal).

### 6.4 Directorio unificado (Empresas + Personas)

Empresas y Personas **no son pantallas separadas**: viven en un solo módulo **"Directorio"** (subnav de `venta`, icono Tabler `address-book`). Patrón:

- **`views/DirectorioView.vue`** (plantilla Operativo) es el contenedor: controla `setRegions`/`reset`, monta el **conmutador** Empresas|Personas en `#gc-shell-master`, posee las **dos listas maestras** (cada una con su `service`, búsqueda con debounce, `select`), y monta el **detalle activo** según `route.params.modo`.
- **`components/directorio/EmpresaDetalle.vue`** y **`components/directorio/PersonaDetalle.vue`** encapsulan el detalle de cada entidad. Contrato:
  - Prop `id` (la entidad a mostrar; `null` = estado vacío "selecciona…").
  - `defineExpose({ openCreate })` para que el botón "Nueva…" del maestro abra el modal de creación vía template `ref`.
  - Emite `updated` (el contenedor recarga la lista) y `open-empresa` / `open-persona` (navegación cruzada por la relación).
  - El detalle teletransporta su propio **aside** (`#gc-shell-aside`) con `ContactosPanel` + relaciones; el contenedor solo controla el master.
- **Navegación cruzada:** desde una empresa se salta a sus personas y viceversa con `router.push({ name: 'Directorio', params: { modo, id } })`. La lista persiste; solo cambian el modo y el `:id`. El componente `DirectorioView` es el **mismo** entre modos (se reusa la instancia; el detalle se intercambia por `v-if`).
- Los modales (`EmpresaModal`, `PersonaModal`, `AsociarEmpresaModal`) y `components/contacto/` se **reutilizan** desde los detalles; no se duplican.
- **Eliminadas:** `views/EmpresasView.vue` y `views/PersonasView.vue` (su contenido se extrajo a los `*Detalle.vue`). Las rutas legacy redirigen (ver §8.3).

### 6.5 Contactabilidad (medios de contacto)

- Dominio `contacto`: un medio (`GcContacto`) pertenece a **una empresa O una persona** (exactamente uno). Tipos de canal: teléfono/celular/email/red social; WhatsApp es bandera, no canal. Teléfonos se normalizan a E.164 en backend.
- **`components/contacto/ContactosPanel.vue`** — panel reutilizable (empresa y persona). Props: `contactos`, `loading`. Emite `create`, `edit`, `principal`, `eliminar`. Acciones inline llamar/WhatsApp/correo/abrir-red.
- **`components/contacto/ContactoDrawer.vue`** — alta/edición. Prop `owner` (`'empresa'|'persona'`), `owner-nombre`, `contacto`, `redes`, `saving`, `server-error`. Emite `close`, `submit` (`{ id, payload }`).
- **`categoria`** (PERSONAL/EMPRESARIAL/OTRO) responde "de quién/qué tipo es"; **`etiqueta`** (texto libre) desambigua varios canales del mismo tipo. No son redundantes.

---

## 7. Tema oscuro y densidad

Ambos siguen el mismo patrón: atributo en `<html>` + composable singleton + `localStorage`, inicializados en `App.vue` (setup) para aplicar desde el arranque (incluido login).

### 7.1 Tema — `composables/useTheme.js`

- API: `{ theme, setTheme, toggleTheme }`. Valores `'light'|'dark'`.
- Aplica `data-theme="dark"` al `<html>`; los tokens oscuros viven bajo `[data-theme="dark"]` en `instrument.css`.
- Persiste en `localStorage` (`gc-theme`); si no hay preferencia, respeta `prefers-color-scheme`. Default claro.
- Toggle (icono sun/moon) en la barra superior del `AppShell`.

### 7.2 Densidad — `composables/useDensity.js`

- API: `{ density, setDensity, toggleDensity }`. Valores `'comfortable'|'compact'`.
- Aplica `data-density="compact"` al `<html>`. Persiste en `localStorage` (`gc-density`). Default cómodo.
- Toggle (icono layout-rows/layout-list) en la barra superior.

**Cómo funciona la densidad (lección clave):** el modo compacto reduce variables `--gc-row-pad-y/x/gap` bajo `[data-density="compact"]`. `GcListRow` **consume esas variables** en vez de paddings fijos. Se usan **CSS variables y NO overrides de clase** porque las vars heredan por el árbol DOM y **atraviesan el `<style scoped>`** de los componentes — un selector de clase externo no puede, por el atributo `[data-v-*]` del scoping de Vue. La densidad afecta sobre todo a los renglones (que dominan la UI); celdas con `<style scoped>` propio de una vista no se compactan salvo que también usen las variables.

---

## 8. Services, stores, router

### 8.1 Services (`services/*.js`)

- Un service por dominio. Constante `BASE` al inicio. `async/await` con `const { data } = await api.x(...)`.
- Named export + default. **No try/catch dentro del service** (los errores burbujean a la vista).
- `api.js`: instancia axios con interceptor JWT y redirección a `/login` en 401. **No tocar** salvo interceptors transversales.
- Respuesta paginada: `{ data, pagination: { page, pageSize, totalItems, totalPages } }`. Acceder a `res.pagination.*` (o usar `utils/pagination.js → extractPagination`).
- **Services vigentes relevantes:** `empresa.service` (incluye `listarSectores`, `listarOrigenes`, además de país/depto/municipio/tiposDoc bajo `/catalogos`), `persona.service` (incluye `asociarEmpresa`/`desasociarEmpresa`), `contacto.service` (medios + redes sociales + acciones), `usuario.service` (`listar()` → usuarios activos, para selección de **propietario**/responsable), `oportunidad.service`, etc.

### 8.2 Stores Pinia (Options API)

- Solo estado global persistente entre vistas (sesión, etc.). Listas pedidas por API en cada vista NO van al store.
- `state` siempre función. Archivo `*.store.js`, función `use{Nombre}Store`.

### 8.3 Router (`router/index.js`)

- Componentes lazy-loaded. `meta: { requiresAuth, layout, module }`. `layout: 'app'` por defecto, `'blank'` solo login.
- Rutas anidadas para maestro-detalle (la lista persiste, el `:id` pinta el centro). Ej: `/actividades/:oportunidadId`, `/contratos/:id`.
- **Directorio unificado:** `/directorio/:modo(empresa|persona)/:id?` (un solo módulo, conmutador Empresas|Personas). `/directorio` redirige a `empresa`. Las rutas legacy `/empresas/:id?` y `/personas/:id?` **redirigen** al Directorio (compatibilidad de deep-links); no montan vistas propias.
- Guard global de auth. Wildcard final → `/`.
- La navegación de módulos está en el **AppShell** (barra superior horizontal), no en un NavRail vertical; el subnav se deriva de `MODULES` en `useModule.js`.

---

## 9. Utilidades y formatters

| Archivo | Exporta | Uso |
|---------|---------|-----|
| `utils/currency.js` | `formatCurrency`, `formatCurrencyFull`, `parseMoney`, `formatMoneyInput` | Dinero. **NO contiene funciones de fecha.** |
| `utils/datetime.js` | `formatDate`, `formatDateTime` | Fechas. **Importar de aquí, no de currency.** |
| `utils/pagination.js` | `extractPagination` | Normaliza la paginación de la respuesta. |

**Nunca** formatear dinero con `toFixed`/`toString` ni fechas a mano: usar estos formatters.

---

## 10. Lecciones aprendidas

Lecciones que costaron tiempo. **Tenerlas presentes:**

1. **`utils/currency.js` NO exporta funciones de fecha.** Las fechas viven en `utils/datetime.js` (`formatDate`/`formatDateTime`). Importar `formatDate` desde `currency` rompe el build.
2. **Catálogos no uniformes.** País usa `nombre`/`codigo` y TipoDocumento usa `nombre`, pero **Departamento y Municipio usan `descripcion`/`codigo`** (no `nombre`). Mapear el campo correcto en los `<select>` en cascada (país→depto→municipio). Sector/Origen (catálogos comerciales) usan `codigo`/`nombre`.
3. **Densidad vía CSS vars que atraviesan `scoped`.** Para que un toggle global afecte a componentes con `<style scoped>`, usar variables CSS heredadas (no overrides de clase). Ver §7.2.
4. **Tema/densidad inicializados en `App.vue`** (setup), no solo en `AppShell`, para que apliquen también en login (que no monta el shell).
5. **Teleport y timing.** Inyectar contenido en zonas master/aside del shell requiere que el target exista; activar la zona en setup y usar `:disabled` del Teleport (o guard `asideReady`) hasta `nextTick`. Igual para enfocar el input de un overlay dentro de `<Transition>`: usar el hook `@after-enter` (o reintento por `requestAnimationFrame`), no solo `nextTick`.
6. **El color del marcador de estado por defecto es grafito** (`neutral`), no un color; reservar color para estados reales.
7. **Vite cachea componentes grandes.** Tras reemplazar un componente central (p. ej. `AppShell`), reiniciar `npm run dev` y hacer hard reload; si la consola muestra logs de una versión vieja, el archivo nuevo no se aplicó o el dev server no recargó.
8. **El reset global vive en `instrument.css`** (no hay `global.css`). Si se añade base global, va ahí, con tokens `--gc-*`.
9. **Persona ya no tiene `email`/`telefono`** (se retiraron en backend, F-RP1). La contactabilidad de una persona vive en su dominio `contacto`. No referenciar `persona.email`/`persona.telefono` en plantillas (quedan `undefined`); mostrar `numeroDocumento` u otro campo.
10. **El propietario llega como `propietarioId` (Long)**, no como nombre. Para mostrar el nombre se resuelve contra `usuario.service.listar()` en la vista/detalle (helper `propietarioNombre(id)`), no se asume un campo `propietarioNombre` del backend (las empresas no lo traen; persona sí trae `reportaANombre`/`origenNombre` aplanados, pero el propietario no).
11. **Detalle unificado vía `defineExpose`.** Para que un botón del maestro (en el contenedor) dispare una acción de un detalle hijo montado por `v-if`, el hijo expone el método (`defineExpose({ openCreate })`) y el contenedor lo llama por template `ref` (`ref?.openCreate()`), comprobando que el ref no sea null según el modo activo.
12. **Navegación cruzada por ruta, no por estado.** Saltar empresa↔persona se hace con `router.push` al mismo named route cambiando `modo`/`id`; la vista reacciona al cambio de params (no se desmonta el contenedor). Evita estado compartido frágil.

---

## 11. Checklist al crear / modificar una vista

- [ ] `<script setup>` (Composition API).
- [ ] Si es 'app': `setRegions(...)` en setup/`onMounted` y `reset()` en `onUnmounted` (§4.3).
- [ ] Importa el service correspondiente, no axios directo.
- [ ] Tokens `--gc-*`; nunca hardcodear colores ni medidas.
- [ ] Renglones con `GcListRow` (no tarjetas). Estado por `tone`.
- [ ] Estados loading / empty (`GcEmpty`) / error manejados.
- [ ] Iconos vía `GcIcon name="..."` (id Tabler sin `ti-`), no `<svg>` ni icono legacy.
- [ ] Formatters: dinero de `utils/currency`, fechas de `utils/datetime`.
- [ ] Paginación: `res.pagination.*` o `extractPagination`.
- [ ] Modales/drawers en `components/{dominio}/` con `GcModal`/`GcDrawer`.
- [ ] Ruta nueva: registrada con `meta: { requiresAuth: true, layout: 'app', module }` (o `'blank'`).
- [ ] Módulo/subnav nuevo: agregar a `MODULES` en `composables/useModule.js` (el Ctrl+K se deriva de ahí) con su icono Tabler.
- [ ] Propietario/responsable: resolver `id → nombre` con `usuario.service`; no asumir nombre del backend.

---

## 12. Cómo entregar archivos

1. Cada vista/componente en su `.vue`; cada service/composable en su `.js`.
2. Al modificar archivos existentes (router, AppShell, instrument.css, useModule), entregar el archivo completo, no diffs.
3. Presentar con `present_files`.

---

*Esta skill refleja el frontend de GestCom tras el rediseño "Instrumento" (RF1–RF8) y la unificación del **Directorio** (Empresas + Personas, F-DIR), la contactabilidad multicanal y el enriquecimiento CRM de Empresa/Persona. El sistema "Aurora Dark"/"Luxury Tech" fue eliminado por completo del repo. La parte F-UI5/RF6 del plan de rediseño (Empresas y Personas como pantallas separadas) queda superada por el Directorio.*
