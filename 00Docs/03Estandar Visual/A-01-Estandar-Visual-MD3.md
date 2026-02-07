# A-01: Estándar Visual Material Design 3 (MD3)
## Plataforma Interna de Arquitecsoft - Apps SPA

**Fecha:** Febrero 2026  
**Versión:** 1.0  
**Clasificación:** Interno - Arquitecsoft

---

## 1. App Shell MD3

El App Shell define la estructura visual persistente de toda aplicación SPA bajo esta plataforma.

### 1.1 Estructura General

```
┌─────────────────────────────────────────────────────────────────┐
│                        Top App Bar                              │
│  [≡ Menu]  [Logo/Título]              [Search] [Theme] [Avatar] │
├────────┬────────────────────────────────────────────────────────┤
│        │                                                        │
│  Nav   │                                                        │
│  Rail  │                    Content Area                        │
│        │                                                        │
│  [🏠]  │    ┌──────────────────────────────────────────────┐   │
│  [📊]  │    │                                              │   │
│  [👥]  │    │              Main Content                    │   │
│  [⚙️]  │    │                                              │   │
│        │    └──────────────────────────────────────────────┘   │
│        │                                                        │
│  ────  │                                                        │
│  [?]   │                                                        │
└────────┴────────────────────────────────────────────────────────┘
```

### 1.2 Top App Bar

**Componente MD3:** `Top app bar` - Variante `Small` o `Medium`

| Elemento | Posición | Componente MD3 | Comportamiento |
|----------|----------|----------------|----------------|
| Menu toggle | Leading | `Icon button` | Visible solo en mobile/tablet, expande Navigation drawer |
| Logo/Título | Leading | `Title text` | Typography: `Title Large` |
| Search | Trailing | `Icon button` → `Search bar` | Expandible on click |
| Theme toggle | Trailing | `Icon button` | Alterna Light/Dark |
| User avatar | Trailing | `Icon button` con `Avatar` | Abre menú de usuario |

**Tokens de estilo:**
- Altura: `64dp` (desktop), `56dp` (mobile)
- Color superficie: `surface-container`
- Elevación: `0` (scroll top) → `2` (on scroll)
- Color on-surface para iconos y texto

**Comportamiento:**
- Sticky en scroll
- Elevation cambia de `0` a `Level 2` al hacer scroll
- En mobile: hamburger menu reemplaza navigation rail

### 1.3 Navigation Rail

**Componente MD3:** `Navigation rail`

| Característica | Especificación MD3 |
|----------------|-------------------|
| Ancho | `80dp` (collapsed) |
| Posición | Fixed left |
| Alineación items | Center vertical |
| Máximo items | 3-7 destinos |
| FAB opcional | Posición top del rail |

**Anatomía de cada item:**
```
┌──────────┐
│   Icon   │  ← Icon: 24dp, outlined (inactive) / filled (active)
│  Label   │  ← Typography: Label Medium
└──────────┘
```

**Estados:**
| Estado | Icon | Label | Indicator |
|--------|------|-------|-----------|
| Inactive | `on-surface-variant` | `on-surface-variant` | None |
| Active | `on-secondary-container` | `on-surface` | `secondary-container` pill |
| Hover | `on-surface` | `on-surface` | `surface-container-highest` |
| Focused | `on-surface` | `on-surface` | Focus ring `primary` |

**Items estándar plataforma:**
1. Inicio / Dashboard
2. [Módulos según app]
3. Configuración (bottom aligned)
4. Ayuda (bottom aligned)

### 1.4 Content Area

**Layout MD3:** `Canonical layouts` - Variante según contenido

| Tipo contenido | Layout MD3 | Uso |
|----------------|------------|-----|
| Dashboard | `Feed` | KPIs, cards informativas |
| Listado | `List-detail` | Tablas con vista detalle |
| Formulario | `Supporting pane` | Formularios con contexto |
| Detalle | `Full-screen dialog` o `Pane` | Vista completa de entidad |

**Espaciado interno:**
- Padding horizontal: `24dp` (desktop), `16dp` (mobile)
- Padding vertical: `24dp`
- Gap entre secciones: `24dp`
- Gap entre elementos: `16dp`

**Tokens de superficie:**
- Background: `surface`
- Cards/Containers: `surface-container-low` o `surface-container`

---

## 2. Patrones MD3 para Casos de Uso

### 2.1 Formularios de Negocio (Create/Edit)

**Layout:** Contenido en `Card` o directamente sobre `surface`

```
┌─────────────────────────────────────────────────────────────┐
│ [←]  Crear Oportunidad                          [Cancelar]  │  ← Top app bar contextual
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  Información General                    ← Section header    │
│  ─────────────────────────────────────                      │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ Nombre *                                            │   │  ← Filled text field
│  │ [                                                 ] │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌─────────────────────┐  ┌─────────────────────────────┐  │
│  │ Empresa *           │  │ Pipeline *                  │  │  ← Grid 2 cols
│  │ [Select        ▼]   │  │ [Select              ▼]    │  │
│  └─────────────────────┘  └─────────────────────────────┘  │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ Valor estimado                                      │   │
│  │ [$] [                                             ] │   │  ← Text field con prefix
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│                                                             │
│  Configuración                          ← Section header    │
│  ─────────────────────────────────────                      │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ [○] Modo guía   [●] Modo bloqueo                   │   │  ← Radio buttons
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
├─────────────────────────────────────────────────────────────┤
│                              [Cancelar]  [Guardar]          │  ← Actions footer
└─────────────────────────────────────────────────────────────┘
```

**Componentes MD3 utilizados:**

| Elemento | Componente MD3 | Variante |
|----------|----------------|----------|
| Campos de texto | `Text field` | `Filled` (preferido) o `Outlined` |
| Selectores | `Menu` | Dropdown dentro de text field |
| Fechas | `Date picker` | Modal o Docked |
| Opciones únicas | `Radio button` | Agrupados |
| Opciones múltiples | `Checkbox` | Agrupados |
| Botón principal | `Button` | `Filled` |
| Botón secundario | `Button` | `Outlined` o `Text` |

**Reglas de layout:**
- Grid: `1 columna` (mobile), `2 columnas` (tablet+)
- Campos relacionados: misma fila cuando sea lógico
- Secciones separadas por `Divider` o espaciado `32dp`
- Labels: dentro del field (floating label MD3)
- Campos requeridos: indicador `*` en label
- Errores: `Supporting text` en color `error` bajo el campo

**Estados de validación:**
| Estado | Border/Indicator | Supporting text | Icon |
|--------|------------------|-----------------|------|
| Default | `outline` | Helper text | None |
| Focused | `primary` | Helper text | None |
| Error | `error` | Error message | `error` icon |
| Disabled | `on-surface` 38% | — | None |

### 2.2 Listados con Filtros y Acciones

**Layout:** `List-detail` o tabla con toolbar

```
┌─────────────────────────────────────────────────────────────────┐
│  Oportunidades                                                  │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ [🔍 Buscar...                    ]  [Filtros ▼]  [+ Nueva] │   │  ← Toolbar
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ [Chip: Etapa=Prospección ×] [Chip: Estado=Abierta ×]   │   │  ← Filter chips
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ □  Nombre           Empresa        Etapa       Valor    │   │
│  ├─────────────────────────────────────────────────────────┤   │
│  │ □  Proyecto Alpha   Acme Corp     Propuesta   $50,000  │→│ │
│  │ □  Renovación Beta  Tech Inc      Negociación $30,000  │→│ │
│  │ ■  Consultoría XYZ  Global SA     Prospección $15,000  │→│ │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │        ← 1  2  3  ...  10 →     [10 ▼] por página      │   │  ← Pagination
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

**Componentes MD3 utilizados:**

| Elemento | Componente MD3 | Notas |
|----------|----------------|-------|
| Barra búsqueda | `Search bar` | Variante `View` o dentro de text field |
| Botón filtros | `Icon button` o `Filled tonal button` | Abre panel/bottom sheet |
| Botón crear | `FAB` o `Extended FAB` | Acción primaria |
| Chips filtro | `Filter chip` | Con `×` para remover |
| Tabla | `Data table` | Con sorting, selection |
| Checkbox | `Checkbox` | Para selección múltiple |
| Row action | `Icon button` | Chevron o menú |
| Paginación | `Segmented button` + `Menu` | Custom pero MD3 tokens |

**Interacciones:**
- Hover en fila: `surface-container-highest`
- Selected: `secondary-container` background
- Click en fila: navega a detalle o expande panel
- Ordenamiento: click en header, indicador `↑↓`

**Acciones bulk (selección múltiple):**
```
┌─────────────────────────────────────────────────────────────┐
│ 3 seleccionados                    [Exportar] [Eliminar]   │  ← Contextual toolbar
└─────────────────────────────────────────────────────────────┘
```

### 2.3 Diálogos Modales

**Componente MD3:** `Dialog` - Variantes según uso

#### 2.3.1 Diálogo de Confirmación

```
┌────────────────────────────────────────┐
│                                        │
│  [!]  ¿Eliminar oportunidad?          │  ← Icon (opcional) + Title
│                                        │
│  Esta acción no se puede deshacer.    │  ← Supporting text
│  Se eliminarán todas las actividades  │
│  asociadas.                           │
│                                        │
│              [Cancelar]  [Eliminar]   │  ← Actions
│                                        │
└────────────────────────────────────────┘
```

**Especificaciones:**
- Ancho: `280dp` min, `560dp` max
- Padding: `24dp`
- Icon: `24dp`, color `error` o `primary` según contexto
- Title: `Headline Small`
- Body: `Body Medium`
- Buttons: alineados derecha, `Text` buttons

#### 2.3.2 Diálogo de Creación/Edición Rápida

```
┌────────────────────────────────────────────────────┐
│  Agregar Actividad                            [×] │  ← Title + Close
├────────────────────────────────────────────────────┤
│                                                    │
│  ┌──────────────────────────────────────────────┐ │
│  │ Tipo de actividad *                          │ │
│  │ [Reunión                               ▼]   │ │
│  └──────────────────────────────────────────────┘ │
│                                                    │
│  ┌──────────────────────────────────────────────┐ │
│  │ Fecha y hora *                               │ │
│  │ [15/02/2026  10:00 AM               📅]     │ │
│  └──────────────────────────────────────────────┘ │
│                                                    │
│  ┌──────────────────────────────────────────────┐ │
│  │ Notas                                        │ │
│  │ [                                          ] │ │
│  │ [                                          ] │ │
│  └──────────────────────────────────────────────┘ │
│                                                    │
├────────────────────────────────────────────────────┤
│                      [Cancelar]  [Guardar]        │
└────────────────────────────────────────────────────┘
```

**Especificaciones:**
- Variante: `Basic dialog` con scroll interno si necesario
- Ancho: `560dp` para formularios
- Divider opcional entre header/content/actions
- Scroll: solo en content area, header/actions fixed

#### 2.3.3 Full-Screen Dialog (Mobile/Formularios complejos)

```
┌─────────────────────────────────────────────────────┐
│ [×]  Nueva Empresa                      [Guardar]  │  ← App bar style
├─────────────────────────────────────────────────────┤
│                                                     │
│  [Contenido scrolleable del formulario]            │
│                                                     │
└─────────────────────────────────────────────────────┘
```

**Uso:**
- Mobile: siempre para formularios
- Desktop: opcional para formularios muy largos
- Transición: slide from bottom (mobile), fade (desktop)

---

## 3. Responsividad

### 3.1 Breakpoints MD3

| Breakpoint | Rango | Clasificación | Layout |
|------------|-------|---------------|--------|
| Compact | `0-599dp` | Mobile | 1 columna, Nav drawer |
| Medium | `600-839dp` | Tablet portrait | 1-2 columnas, Nav rail |
| Expanded | `840-1199dp` | Tablet landscape / Desktop | 2 columnas, Nav rail |
| Large | `1200-1599dp` | Desktop | 2-3 columnas, Nav rail |
| Extra-large | `1600dp+` | Desktop wide | 3 columnas, Nav rail expandido |

### 3.2 Adaptaciones por Breakpoint

| Elemento | Compact | Medium | Expanded+ |
|----------|---------|--------|-----------|
| Navegación | Drawer (hamburger) | Rail `80dp` | Rail `80dp` |
| Top app bar | `Small` | `Small` | `Medium` opcional |
| Content padding | `16dp` | `24dp` | `24dp` |
| Form columns | 1 | 2 | 2-3 |
| Table | Cards o List | Table scroll | Table completa |
| Dialog | Full-screen | Basic | Basic |
| FAB | Regular | Extended | Extended |

### 3.3 Grid System

**Basado en MD3 Layout Grid:**

| Breakpoint | Columnas | Margins | Gutter |
|------------|----------|---------|--------|
| Compact | 4 | `16dp` | `8dp` |
| Medium | 8 | `24dp` | `16dp` |
| Expanded | 12 | `24dp` | `16dp` |
| Large+ | 12 | `24dp` | `24dp` |

---

## 4. Soporte Light/Dark Mode

### 4.1 Criterios de Implementación

**Sistema de color MD3 - Tonal Palettes:**

Cada color clave genera una paleta tonal de 13 tonos (0-100):
- `Primary`, `Secondary`, `Tertiary`
- `Error`
- `Neutral`, `Neutral Variant`

**Tokens de color (no hardcodear):**

| Token | Light Mode | Dark Mode | Uso |
|-------|------------|-----------|-----|
| `primary` | P-40 | P-80 | Elementos principales |
| `on-primary` | P-100 | P-20 | Texto sobre primary |
| `primary-container` | P-90 | P-30 | Containers primary |
| `surface` | N-98 | N-6 | Background principal |
| `surface-container` | N-94 | N-12 | Cards, containers |
| `on-surface` | N-10 | N-90 | Texto principal |
| `outline` | NV-50 | NV-60 | Borders, dividers |

### 4.2 Reglas de Aplicación

1. **Nunca hardcodear colores** - Usar siempre tokens CSS/variables
2. **Contraste mínimo:** 4.5:1 para texto normal, 3:1 para texto grande
3. **Elevation en dark mode:** usar `surface` tints, no sombras
4. **Imágenes/iconos:** verificar visibilidad en ambos modos
5. **Toggle:** persistir preferencia del usuario en localStorage

### 4.3 Implementación Técnica

```css
/* Ejemplo de tokens (NO hardcodear valores directos en componentes) */
:root {
  --md-sys-color-primary: /* generado */;
  --md-sys-color-on-primary: /* generado */;
  --md-sys-color-surface: /* generado */;
  /* ... resto de tokens */
}

[data-theme="dark"] {
  --md-sys-color-primary: /* valor dark */;
  --md-sys-color-on-primary: /* valor dark */;
  --md-sys-color-surface: /* valor dark */;
  /* ... resto de tokens */
}
```

---

## 5. Accesibilidad Mínima

### 5.1 Focus Visible

**Requisito:** Todo elemento interactivo debe mostrar indicador de foco visible.

| Elemento | Focus indicator |
|----------|-----------------|
| Buttons | Outline `3dp` color `primary` |
| Text fields | Border cambia a `primary` |
| Checkboxes/Radios | Ring `3dp` |
| Links | Outline `2dp` |
| Cards clickeables | Outline `3dp` |

**Implementación:**
```css
:focus-visible {
  outline: 3px solid var(--md-sys-color-primary);
  outline-offset: 2px;
}
```

### 5.2 Tamaños Táctiles

**MD3 Target sizes:**

| Elemento | Tamaño mínimo | Tamaño recomendado |
|----------|---------------|-------------------|
| Icon buttons | `40dp × 40dp` | `48dp × 48dp` |
| Buttons | `40dp` altura | `48dp` altura |
| Checkboxes | `48dp × 48dp` área táctil | — |
| List items | `48dp` altura mínima | `56dp` o `72dp` |
| Touch target spacing | `8dp` mínimo | — |

### 5.3 Estados de Componentes

**Todos los componentes interactivos deben tener:**

| Estado | Visual | Requisito |
|--------|--------|-----------|
| Enabled | Default | — |
| Disabled | `38%` opacity | `aria-disabled="true"` |
| Hovered | State layer `8%` | — |
| Focused | Focus ring | `tabindex`, `:focus-visible` |
| Pressed | State layer `12%` | — |
| Dragged | Elevation + state layer | Si aplica |
| Selected | Container color change | `aria-selected="true"` |
| Error | `error` color | `aria-invalid="true"` |

### 5.4 Semántica y ARIA

**Requisitos mínimos:**

| Elemento | Atributos requeridos |
|----------|---------------------|
| Botones | `<button>` semántico o `role="button"` |
| Links | `<a href>` con texto descriptivo |
| Forms | `<label>` asociado o `aria-label` |
| Errores | `aria-describedby` apuntando al mensaje |
| Modals | `role="dialog"`, `aria-modal="true"`, focus trap |
| Loading | `aria-busy="true"`, `aria-live` para anuncios |
| Tables | `<th scope>`, `<caption>` si necesario |

### 5.5 Navegación por Teclado

| Contexto | Teclas |
|----------|--------|
| General | `Tab` avanza, `Shift+Tab` retrocede |
| Menus | `↑↓` navega, `Enter` selecciona, `Esc` cierra |
| Dialogs | `Esc` cierra, focus trapped |
| Radio groups | `↑↓` o `←→` cambia selección |
| Tabs | `←→` cambia tab, `Enter` activa |

---

## 6. Componentes MD3 Referenciados

### Lista completa de componentes MD3 a utilizar:

| Categoría | Componentes |
|-----------|-------------|
| Actions | `Button` (Filled, Outlined, Text, Elevated, Tonal), `FAB`, `Extended FAB`, `Icon button`, `Segmented button` |
| Communication | `Badge`, `Progress indicator` (Linear, Circular), `Snackbar` |
| Containment | `Card` (Elevated, Filled, Outlined), `Dialog`, `Divider`, `Bottom sheet`, `Side sheet` |
| Navigation | `Navigation rail`, `Navigation drawer`, `Top app bar`, `Tabs` |
| Selection | `Checkbox`, `Chip` (Assist, Filter, Input, Suggestion), `Date picker`, `Menu`, `Radio button`, `Switch` |
| Text inputs | `Text field` (Filled, Outlined) |

### Componentes NO utilizados en esta plataforma:

- `Bottom app bar` (usamos Top app bar + Navigation rail)
- `Carousel` (no aplica a apps de gestión)
- `Time picker` (usar text field con formato)

---

## 7. Tokens de Referencia Rápida

### Spacing
| Token | Valor |
|-------|-------|
| `spacing-xs` | `4dp` |
| `spacing-sm` | `8dp` |
| `spacing-md` | `16dp` |
| `spacing-lg` | `24dp` |
| `spacing-xl` | `32dp` |

### Typography (MD3 Type Scale)
| Token | Uso |
|-------|-----|
| `display-large` | Heros, números grandes |
| `headline-large` | Page titles |
| `headline-medium` | Section headers |
| `title-large` | App bar title |
| `title-medium` | Card titles |
| `body-large` | Body text principal |
| `body-medium` | Body text secundario |
| `label-large` | Buttons |
| `label-medium` | Navigation labels |

### Elevation (MD3 Levels)
| Level | Uso |
|-------|-----|
| `0` | Surfaces planas |
| `1` | Cards, Navigation rail |
| `2` | Top app bar on scroll |
| `3` | FAB, Dialogs |

---

*Fin del documento A-01 - Estándar Visual MD3*
