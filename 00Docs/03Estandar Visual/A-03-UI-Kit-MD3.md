# A-03: UI Kit - Componentes Base MD3
## Plataforma Interna de Arquitecsoft - Catálogo de Componentes

**Fecha:** Febrero 2026  
**Versión:** 1.0  
**Clasificación:** Interno - Arquitecsoft

---

## 1. Componentes Base Obligatorios

### 1.1 Buttons

#### Variantes Disponibles

| Variante | Visual | Token Background | Token Text |
|----------|--------|------------------|------------|
| **Filled** | Sólido, alta énfasis | `primary` | `on-primary` |
| **Filled Tonal** | Sólido suave, media énfasis | `secondary-container` | `on-secondary-container` |
| **Outlined** | Borde, media énfasis | `transparent` | `primary` |
| **Text** | Sin fondo, baja énfasis | `transparent` | `primary` |
| **Elevated** | Con sombra, media énfasis | `surface-container-low` | `primary` |

#### Anatomía

```
┌─────────────────────────────────────┐
│  [Icon]  Label Text  [Icon]         │
└─────────────────────────────────────┘
     ↑         ↑          ↑
  Leading   Content    Trailing
 (opcional)           (opcional)
```

| Elemento | Especificación |
|----------|----------------|
| Altura | `40dp` mínimo |
| Padding horizontal | `24dp` (sin icon), `16dp` (con icon) |
| Icon size | `18dp` |
| Gap icon-label | `8dp` |
| Border radius | `corner-full` (20dp) |
| Typography | `label-large` |

#### Estados

| Estado | Filled | Tonal | Outlined | Text |
|--------|--------|-------|----------|------|
| **Enabled** | `primary` bg | `secondary-container` bg | `outline` border | — |
| **Hovered** | +8% `on-primary` overlay | +8% `on-secondary-container` overlay | +8% `primary` overlay | +8% `primary` overlay |
| **Focused** | +12% overlay + focus ring | +12% overlay + focus ring | +12% overlay + focus ring | +12% overlay + focus ring |
| **Pressed** | +12% overlay | +12% overlay | +12% overlay | +12% overlay |
| **Disabled** | 12% `on-surface` bg, 38% text | 12% `on-surface` bg, 38% text | 12% `on-surface` border, 38% text | 38% text |

#### Cuándo Usar Cada Variante

| Variante | Uso | Ejemplo |
|----------|-----|---------|
| **Filled** | Acción principal, CTA único por pantalla | "Guardar", "Crear", "Enviar" |
| **Filled Tonal** | Acción importante pero no principal | "Agregar actividad", "Exportar" |
| **Outlined** | Acción secundaria, alternativa al principal | "Cancelar", "Volver" |
| **Text** | Acción terciaria, menos prominente | "Ver más", "Limpiar filtros" |
| **Elevated** | Acción sobre superficies con patrón | Raro, solo si outlined no contrasta |

#### Reglas de Jerarquía

```
Una pantalla típica:
┌────────────────────────────────────────────┐
│                                            │
│   [Text: Cancelar]  [Filled: Guardar]     │  ← Footer de form
│                                            │
└────────────────────────────────────────────┘

Máximo 1 Filled button por sección visible.
Máximo 2-3 buttons por grupo de acciones.
```

---

### 1.2 Text Fields (Inputs)

#### Variantes Disponibles

| Variante | Visual | Uso Recomendado |
|----------|--------|-----------------|
| **Filled** | Fondo con indicador inferior | Default para formularios densos |
| **Outlined** | Borde completo | Formularios con más espacio |

**Decisión plataforma:** Usar **Filled** como estándar.

#### Anatomía

```
┌─────────────────────────────────────────────────┐
│ Label text                                      │  ← Floating label
│ ┌─────────────────────────────────────────────┐ │
│ │ [🔍] Input value                      [×]   │ │  ← Container
│ └─────────────────────────────────────────────┘ │
│ Supporting text                            0/50 │  ← Helper / Counter
└─────────────────────────────────────────────────┘
```

| Elemento | Especificación |
|----------|----------------|
| Altura container | `56dp` |
| Padding horizontal | `16dp` |
| Leading icon | `24dp`, opcional |
| Trailing icon | `24dp`, opcional (clear, toggle, error) |
| Label | `body-large` → `body-small` (focused) |
| Input text | `body-large` |
| Supporting text | `body-small` |
| Border radius (filled) | `corner-extra-small` top only |
| Border radius (outlined) | `corner-extra-small` all |

#### Estados

| Estado | Indicator/Border | Label | Supporting |
|--------|------------------|-------|------------|
| **Enabled** | `on-surface-variant` | `on-surface-variant` | `on-surface-variant` |
| **Hovered** | `on-surface` | `on-surface-variant` | `on-surface-variant` |
| **Focused** | `primary` 2dp | `primary` | `on-surface-variant` |
| **Error** | `error` 2dp | `error` | `error` |
| **Disabled** | 38% opacity | 38% opacity | 38% opacity |

#### Tipos de Input

| Tipo | Trailing Icon | Comportamiento |
|------|---------------|----------------|
| Text | Clear `×` (si tiene valor) | — |
| Password | Toggle visibility `👁` | Alterna mostrar/ocultar |
| Search | Clear `×` | — |
| Number | — | Teclado numérico mobile |
| Date | Calendar `📅` | Abre date picker |
| Textarea | — | Multi-línea, resize vertical |

#### Supporting Text

| Tipo | Color | Icono | Uso |
|------|-------|-------|-----|
| Helper | `on-surface-variant` | Ninguno | Instrucciones, formato esperado |
| Error | `error` | `⚠` opcional | Mensaje de validación |
| Counter | `on-surface-variant` | Ninguno | "0/100" caracteres |

---

### 1.3 Selects (Dropdown Menu)

#### Variantes

| Variante | Implementación |
|----------|----------------|
| **Dropdown** | Text field + Menu |
| **Exposed dropdown** | Text field con menú siempre visible al focus |

**Decisión plataforma:** Usar **Dropdown** estándar.

#### Anatomía

```
Closed:
┌─────────────────────────────────────────────┐
│ Label                                       │
│ ┌─────────────────────────────────────────┐ │
│ │ Selected value                      [▼] │ │
│ └─────────────────────────────────────────┘ │
└─────────────────────────────────────────────┘

Open:
┌─────────────────────────────────────────────┐
│ Label                                       │
│ ┌─────────────────────────────────────────┐ │
│ │ Selected value                      [▲] │ │
│ ├─────────────────────────────────────────┤ │
│ │ ○ Option 1                              │ │
│ │ ● Option 2 (selected)                   │ │
│ │ ○ Option 3                              │ │
│ │ ○ Option 4                              │ │
│ └─────────────────────────────────────────┘ │
└─────────────────────────────────────────────┘
```

#### Menu Items

| Elemento | Especificación |
|----------|----------------|
| Altura item | `48dp` |
| Padding horizontal | `12dp` |
| Leading icon/check | `24dp` |
| Typography | `body-large` |
| Max visible items | 5-6 (scroll si más) |
| Max width | Igual o mayor que trigger |

#### Estados del Menu Item

| Estado | Background | Text |
|--------|------------|------|
| Enabled | `transparent` | `on-surface` |
| Hovered | 8% `on-surface` | `on-surface` |
| Focused | 12% `on-surface` | `on-surface` |
| Selected | `secondary-container` | `on-secondary-container` |
| Disabled | `transparent` | 38% `on-surface` |

---

### 1.4 Cards (Superficies)

#### Variantes

| Variante | Elevation | Border | Uso |
|----------|-----------|--------|-----|
| **Elevated** | Level 1 | Ninguno | Cards destacadas, interactivas |
| **Filled** | Level 0 | Ninguno | Agrupación visual sutil |
| **Outlined** | Level 0 | `outline-variant` | Separación clara, formularios |

#### Anatomía

```
┌─────────────────────────────────────────────────┐
│ ┌─────────────────────────────────────────────┐ │
│ │                Media (opcional)             │ │
│ └─────────────────────────────────────────────┘ │
│                                                 │
│   Headline                                      │
│   Subhead                                       │
│                                                 │
│   Supporting text that provides more           │
│   context about the card content.              │
│                                                 │
│                    [Text Button] [Filled Btn]  │
└─────────────────────────────────────────────────┘
```

| Elemento | Especificación |
|----------|----------------|
| Padding | `16dp` |
| Border radius | `corner-medium` (12dp) |
| Headline | `title-large` o `title-medium` |
| Subhead | `body-medium`, `on-surface-variant` |
| Supporting | `body-medium` |
| Actions gap | `8dp` |

#### Estados (Cards Interactivas)

| Estado | Elevated | Filled | Outlined |
|--------|----------|--------|----------|
| Enabled | Level 1 | `surface-container-highest` | `outline-variant` border |
| Hovered | Level 2 + 8% overlay | 8% overlay | 8% overlay |
| Focused | Level 1 + focus ring | focus ring | focus ring |
| Pressed | Level 1 + 12% overlay | 12% overlay | 12% overlay |
| Dragged | Level 4 | Level 4 | Level 4 |

#### Cuándo Usar Cada Variante

| Variante | Uso | Ejemplo |
|----------|-----|---------|
| **Elevated** | Contenido destacado, clickeable | Card de oportunidad en dashboard |
| **Filled** | Agrupación dentro de otra superficie | Sección dentro de formulario |
| **Outlined** | Contenido que necesita límite claro | Item en lista, panel de filtros |

---

### 1.5 Chips

#### Variantes

| Variante | Propósito | Interacción |
|----------|-----------|-------------|
| **Assist** | Acción contextual inteligente | Click → acción |
| **Filter** | Filtrar contenido | Toggle on/off |
| **Input** | Representar entrada del usuario | Removible |
| **Suggestion** | Sugerir opciones | Click → completa input |

**Uso en plataforma:**
- **Filter chips** → Filtros en listados
- **Input chips** → Tags, selección múltiple

#### Anatomía

```
┌────────────────────────────┐
│ [Icon] Label [×]           │
└────────────────────────────┘
```

| Elemento | Especificación |
|----------|----------------|
| Altura | `32dp` |
| Padding horizontal | `16dp` (sin icon), `8dp` (con icon) |
| Icon size | `18dp` |
| Typography | `label-large` |
| Border radius | `corner-small` (8dp) |
| Gap entre chips | `8dp` |

#### Estados - Filter Chip

| Estado | Background | Border | Text |
|--------|------------|--------|------|
| Unselected | `transparent` | `outline` | `on-surface-variant` |
| Unselected + Hover | 8% `on-surface-variant` | `outline` | `on-surface-variant` |
| Selected | `secondary-container` | ninguno | `on-secondary-container` |
| Selected + Hover | `secondary-container` + 8% | ninguno | `on-secondary-container` |
| Disabled | `transparent` | 12% `on-surface` | 38% `on-surface` |

#### Estados - Input Chip

| Estado | Background | Border | Text |
|--------|------------|--------|------|
| Enabled | `transparent` | `outline` | `on-surface-variant` |
| Hovered | 8% `on-surface-variant` | `outline` | `on-surface-variant` |
| Focused | 12% `on-surface-variant` | `primary` | `on-surface-variant` |
| Dragged | `surface-container-low` + Level 4 | — | — |

---

### 1.6 Dialogs (Modales)

#### Variantes

| Variante | Uso | Ancho |
|----------|-----|-------|
| **Basic** | Confirmaciones, mensajes simples | `280-560dp` |
| **Full-screen** | Formularios complejos (mobile obligatorio) | `100%` |

#### Anatomía - Basic Dialog

```
┌────────────────────────────────────────────────┐
│                                                │
│   [Icon]                                       │  ← Opcional
│                                                │
│   Headline                                     │  ← title-large
│                                                │
│   Supporting text that explains the dialog    │  ← body-medium
│   content and provides context for the        │
│   user's decision.                            │
│                                                │
│                     [Text Btn]  [Filled Btn]  │  ← Actions
│                                                │
└────────────────────────────────────────────────┘
```

| Elemento | Especificación |
|----------|----------------|
| Padding | `24dp` |
| Border radius | `corner-extra-large` (28dp) |
| Icon | `24dp`, color `secondary` |
| Headline | `headline-small` |
| Supporting | `body-medium`, `on-surface-variant` |
| Actions alignment | End (derecha) |
| Actions gap | `8dp` |
| Scrim | `scrim` al 32% opacity |
| Elevation | Level 3 |

#### Anatomía - Full-screen Dialog

```
┌─────────────────────────────────────────────────┐
│ [×]  Title                        [Action Btn] │  ← Top app bar
├─────────────────────────────────────────────────┤
│                                                 │
│   [Scrollable content area]                    │
│                                                 │
│   Form fields, lists, etc.                     │
│                                                 │
└─────────────────────────────────────────────────┘
```

| Elemento | Especificación |
|----------|----------------|
| Close icon | Leading, `×` |
| Title | `title-large` |
| Action | Trailing, Text button |
| Content padding | `24dp` horizontal, `16dp` vertical |
| Transición | Slide from bottom (mobile), fade (desktop) |

#### Tipos de Diálogo por Uso

| Tipo | Variante | Headline | Acciones |
|------|----------|----------|----------|
| Confirmación destructiva | Basic | "¿Eliminar [item]?" | "Cancelar" / "Eliminar" |
| Confirmación neutral | Basic | "¿Confirmar acción?" | "Cancelar" / "Confirmar" |
| Información | Basic | Título informativo | "Entendido" (solo 1) |
| Formulario simple | Basic (scrollable) | "Crear/Editar [item]" | "Cancelar" / "Guardar" |
| Formulario complejo | Full-screen | "Crear/Editar [item]" | "×" / "Guardar" |

---

### 1.7 Tables / Lists

#### Data Table (Desktop)

```
┌──────────────────────────────────────────────────────────────────┐
│ □   Column A ↑        Column B         Column C         Actions  │  ← Header
├──────────────────────────────────────────────────────────────────┤
│ □   Value A1          Value B1         Value C1         [⋮]     │  ← Row
│ □   Value A2          Value B2         Value C2         [⋮]     │
│ ■   Value A3          Value B3         Value C3         [⋮]     │  ← Selected
│ □   Value A4          Value B4         Value C4         [⋮]     │
├──────────────────────────────────────────────────────────────────┤
│     Rows per page: [10 ▼]      1-10 of 100       [<] [1] [2] [>] │  ← Footer
└──────────────────────────────────────────────────────────────────┘
```

| Elemento | Especificación |
|----------|----------------|
| Header height | `56dp` |
| Row height | `52dp` |
| Cell padding | `16dp` horizontal |
| Header text | `title-small`, `on-surface-variant` |
| Cell text | `body-medium` |
| Checkbox column | `52dp` ancho |
| Actions column | `52dp` mínimo |
| Dividers | `outline-variant` 1dp |

#### Estados de Fila

| Estado | Background |
|--------|------------|
| Default | `surface` |
| Hovered | `surface-container-highest` |
| Selected | `primary-container` opacity 16% |
| Selected + Hovered | `primary-container` opacity 24% |

#### List (Mobile / Alternativa)

```
┌─────────────────────────────────────────────────┐
│ [Avatar] Headline                         [→]  │
│          Supporting text                       │
├─────────────────────────────────────────────────┤
│ [Avatar] Headline                         [→]  │
│          Supporting text                       │
└─────────────────────────────────────────────────┘
```

| Elemento | Especificación |
|----------|----------------|
| Item height | `56dp` (1-line), `72dp` (2-line), `88dp` (3-line) |
| Leading | Avatar `40dp` o Icon `24dp` |
| Padding | `16dp` horizontal |
| Headline | `body-large` |
| Supporting | `body-medium`, `on-surface-variant` |
| Trailing | Icon `24dp` o metadata text |

---

### 1.8 Snackbars / Toasts

#### Anatomía

```
┌────────────────────────────────────────────────────────────┐
│   Message text                              [Action]  [×] │
└────────────────────────────────────────────────────────────┘
```

| Elemento | Especificación |
|----------|----------------|
| Min width | `288dp` |
| Max width | `568dp` |
| Padding | `16dp` |
| Border radius | `corner-extra-small` (4dp) |
| Background | `inverse-surface` |
| Text | `inverse-on-surface`, `body-medium` |
| Action | `inverse-primary`, `label-large` |
| Position | Bottom center, `16dp` from edge |
| Duration | 4s (sin action), 10s (con action) |
| Max lines | 2 |

#### Tipos

| Tipo | Acción | Close | Duración |
|------|--------|-------|----------|
| Informativo | Opcional | No | 4s |
| Con acción | "Deshacer", "Ver" | Opcional | 10s |
| Persistente | Requerida | Sí | Hasta dismiss |

#### Comportamiento

- Aparece con slide up + fade in
- Desaparece con fade out
- Solo 1 snackbar visible a la vez
- Queue si hay múltiples
- Swipe to dismiss (mobile)

---

## 2. Reglas de Uso por Componente

### 2.1 Matriz de Selección de Buttons

| Escenario | Componente |
|-----------|------------|
| Acción principal de página/form | **Filled** |
| Acción secundaria importante | **Filled Tonal** |
| Acción alternativa (cancelar) | **Outlined** |
| Acción menor, link-like | **Text** |
| Acción sobre imagen/patrón | **Elevated** |
| Acción flotante principal | **FAB** |
| Acción en toolbar | **Icon Button** |

### 2.2 Matriz de Selección de Inputs

| Escenario | Componente |
|-----------|------------|
| Texto corto (nombre, email) | **Text Field** |
| Texto largo (descripción) | **Text Area** |
| Selección única de lista | **Select (Dropdown)** |
| Selección múltiple | **Chips** o **Checkboxes** |
| Opción binaria | **Switch** |
| Opción de grupo exclusivo | **Radio Buttons** |
| Fecha | **Date Picker** |
| Búsqueda | **Search Field** |

### 2.3 Matriz de Selección de Containers

| Escenario | Componente |
|-----------|------------|
| Item clickeable en lista | **Elevated Card** |
| Agrupación de campos | **Outlined Card** o **Section** |
| Información destacada (KPI) | **Filled Card** |
| Panel de filtros | **Outlined Card** |
| Contenido modal | **Dialog** |
| Acciones contextuales | **Menu** |
| Notificación temporal | **Snackbar** |

### 2.4 Estados Obligatorios por Componente

| Componente | Hover | Focus | Active | Disabled | Error | Selected |
|------------|-------|-------|--------|----------|-------|----------|
| Button | ✅ | ✅ | ✅ | ✅ | — | — |
| Text Field | ✅ | ✅ | — | ✅ | ✅ | — |
| Select | ✅ | ✅ | ✅ | ✅ | ✅ | — |
| Checkbox | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| Radio | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| Chip | ✅ | ✅ | ✅ | ✅ | — | ✅ |
| Card | ✅* | ✅* | ✅* | — | — | ✅* |
| Table Row | ✅ | ✅ | — | — | — | ✅ |
| List Item | ✅ | ✅ | ✅ | ✅ | — | ✅ |

*Solo si es interactivo

---

## 3. Patrones de Composición

### 3.1 Form Layout

#### Estructura Estándar

```
┌─────────────────────────────────────────────────────────────────┐
│ [←]  Crear Oportunidad                                         │  ← Top bar contextual
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   INFORMACIÓN GENERAL                           ← Section title │
│   ───────────────────────────────────────────                   │
│                                                                 │
│   ┌─────────────────────────┐ ┌─────────────────────────┐      │
│   │ Nombre *                │ │ Código                  │      │  ← Grid 2 cols
│   │ [                     ] │ │ [                     ] │      │
│   └─────────────────────────┘ └─────────────────────────┘      │
│                                                                 │
│   ┌─────────────────────────────────────────────────────┐      │
│   │ Descripción                                         │      │  ← Full width
│   │ [                                                 ] │      │
│   │ [                                                 ] │      │
│   └─────────────────────────────────────────────────────┘      │
│                                                                 │
│   CONFIGURACIÓN                                                 │
│   ───────────────────────────────────────────                   │
│                                                                 │
│   ┌─────────────────────────┐ ┌─────────────────────────┐      │
│   │ Pipeline *              │ │ Etapa inicial *         │      │
│   │ [Select           ▼]   │ │ [Select           ▼]   │      │
│   └─────────────────────────┘ └─────────────────────────┘      │
│                                                                 │
├─────────────────────────────────────────────────────────────────┤
│                                [Cancelar]  [Guardar]           │  ← Actions footer
└─────────────────────────────────────────────────────────────────┘
```

#### Reglas de Layout

| Regla | Especificación |
|-------|----------------|
| Grid | 1 col (mobile), 2 cols (tablet+) |
| Section gap | `32dp` |
| Field gap | `16dp` vertical, `16dp` horizontal |
| Section title | `title-medium`, `on-surface-variant` |
| Divider | Opcional, `outline-variant` o solo spacing |
| Actions | Sticky footer en mobile, inline en desktop |
| Required indicator | `*` en label |

#### Orden de Campos

1. Campos más importantes primero
2. Campos relacionados en la misma fila
3. Campos opcionales al final o en sección colapsable
4. Acciones siempre al final

### 3.2 List + Filters + Actions

#### Estructura Estándar

```
┌─────────────────────────────────────────────────────────────────────┐
│  Oportunidades                                              [+ Nueva]│  ← Page header
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │ 🔍 Buscar por nombre o empresa...              [Filtros ▼]  │   │  ← Search bar
│  └─────────────────────────────────────────────────────────────┘   │
│                                                                     │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │ [Etapa: Propuesta ×] [Estado: Abierta ×]     [Limpiar todo] │   │  ← Active filters
│  └─────────────────────────────────────────────────────────────┘   │
│                                                                     │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │ □  Nombre          Empresa       Etapa      Valor    [⋮]   │   │
│  ├─────────────────────────────────────────────────────────────┤   │
│  │ □  Proyecto A      Acme Corp    Propuesta   $50K     [⋮]   │   │  ← Data table
│  │ □  Proyecto B      Tech Inc     Negociación $30K     [⋮]   │   │
│  │ ...                                                         │   │
│  ├─────────────────────────────────────────────────────────────┤   │
│  │     10 por página [▼]         1-10 de 45        [<] [>]    │   │  ← Pagination
│  └─────────────────────────────────────────────────────────────┘   │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘

Panel de filtros (expandido):
┌─────────────────────────────────────┐
│  Filtros                       [×] │
├─────────────────────────────────────┤
│                                     │
│  Etapa                              │
│  ○ Todas                            │
│  ○ Prospección                      │
│  ● Propuesta                        │
│  ○ Negociación                      │
│                                     │
│  Estado                             │
│  [✓] Abierta                        │
│  [ ] Ganada                         │
│  [ ] Perdida                        │
│                                     │
│  Rango de fecha                     │
│  [01/01/2026] - [31/12/2026]       │
│                                     │
│        [Limpiar]  [Aplicar]        │
└─────────────────────────────────────┘
```

#### Componentes Utilizados

| Elemento | Componente MD3 |
|----------|----------------|
| Título página | `headline-medium` |
| Botón crear | `Extended FAB` o `Filled button` |
| Search | `Search bar` |
| Botón filtros | `Filled tonal button` con badge |
| Chips activos | `Input chip` con close |
| Tabla | `Data table` |
| Pagination | Custom con `segmented button` + `menu` |
| Panel filtros | `Side sheet` (desktop) o `Bottom sheet` (mobile) |

#### Estados de Lista

| Estado | Visualización |
|--------|---------------|
| Loading | Skeleton rows o `Circular progress` center |
| Empty | Ilustración + mensaje + CTA |
| Error | Mensaje de error + `Retry button` |
| No results | Mensaje "Sin resultados" + sugerencia |

### 3.3 Dialog Create/Edit

#### Diálogo Simple (1-5 campos)

```
┌────────────────────────────────────────────────────────────┐
│  Agregar Actividad                                    [×] │
├────────────────────────────────────────────────────────────┤
│                                                            │
│  ┌──────────────────────────────────────────────────────┐ │
│  │ Tipo *                                               │ │
│  │ [Reunión                                        ▼]  │ │
│  └──────────────────────────────────────────────────────┘ │
│                                                            │
│  ┌──────────────────────────────────────────────────────┐ │
│  │ Fecha y hora *                                       │ │
│  │ [15/02/2026  10:00                             📅]  │ │
│  └──────────────────────────────────────────────────────┘ │
│                                                            │
│  ┌──────────────────────────────────────────────────────┐ │
│  │ Notas                                                │ │
│  │ [                                                  ] │ │
│  └──────────────────────────────────────────────────────┘ │
│                                                            │
├────────────────────────────────────────────────────────────┤
│                           [Cancelar]  [Guardar]           │
└────────────────────────────────────────────────────────────┘
```

#### Diálogo con Tabs (Múltiples secciones)

```
┌────────────────────────────────────────────────────────────┐
│  Editar Empresa                                       [×] │
├────────────────────────────────────────────────────────────┤
│  [General]  [Contactos]  [Documentos]                     │  ← Tabs
├────────────────────────────────────────────────────────────┤
│                                                            │
│  [Contenido del tab activo]                               │
│                                                            │
├────────────────────────────────────────────────────────────┤
│                           [Cancelar]  [Guardar]           │
└────────────────────────────────────────────────────────────┘
```

#### Full-screen (Mobile o formularios extensos)

```
┌─────────────────────────────────────────────────────────────┐
│ [×]  Nueva Oportunidad                          [Guardar]  │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  [Contenido scrolleable]                                   │
│                                                             │
│  ┌───────────────────────────────────────────────────────┐ │
│  │ Sección 1                                             │ │
│  │ ...campos...                                          │ │
│  └───────────────────────────────────────────────────────┘ │
│                                                             │
│  ┌───────────────────────────────────────────────────────┐ │
│  │ Sección 2                                             │ │
│  │ ...campos...                                          │ │
│  └───────────────────────────────────────────────────────┘ │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

#### Reglas de Diálogos

| Regla | Especificación |
|-------|----------------|
| Máx. campos en basic dialog | 5-6 |
| Scroll | Solo en content area, no header/footer |
| Close | `×` o click fuera (confirmaciones: solo botones) |
| Keyboard | `Esc` cierra, `Enter` confirma (si no hay textarea) |
| Focus trap | Obligatorio |
| Loading state | Botón primario con spinner, disable todo |

---

## 4. Accesibilidad por Componente

### 4.1 Buttons

| Requisito | Implementación |
|-----------|----------------|
| Semántica | `<button>` nativo |
| Focus visible | Ring `3dp` `primary` |
| Target size | Mínimo `48×48dp` touch area |
| Disabled | `aria-disabled="true"`, no `disabled` si necesita tooltip |
| Loading | `aria-busy="true"`, texto "Cargando..." para SR |
| Icon-only | `aria-label` descriptivo obligatorio |

### 4.2 Text Fields

| Requisito | Implementación |
|-----------|----------------|
| Label | `<label>` asociado con `for`/`id` |
| Required | `aria-required="true"` + visual `*` |
| Error | `aria-invalid="true"` + `aria-describedby` → mensaje |
| Helper text | `aria-describedby` → helper |
| Disabled | `disabled` attribute |
| Autocomplete | `autocomplete` attribute apropiado |

### 4.3 Selects

| Requisito | Implementación |
|-----------|----------------|
| Role | `combobox` + `listbox` |
| Expanded | `aria-expanded="true/false"` |
| Selected | `aria-selected="true"` en opción |
| Keyboard | `↑↓` navega, `Enter` selecciona, `Esc` cierra |
| Focus | Focus en trigger, `aria-activedescendant` para opciones |

### 4.4 Cards

| Requisito | Implementación |
|-----------|----------------|
| Interactive | `role="button"` o `<a>` si clickeable |
| Focus | `tabindex="0"` si interactiva |
| Label | `aria-label` o `aria-labelledby` → headline |

### 4.5 Chips

| Requisito | Implementación |
|-----------|----------------|
| Filter chip | `role="checkbox"` con `aria-checked` |
| Input chip | `role="button"` con `aria-label` incluye "remover" |
| Remove | Botón close con `aria-label="Remover [valor]"` |

### 4.6 Dialogs

| Requisito | Implementación |
|-----------|----------------|
| Role | `role="dialog"` o `role="alertdialog"` |
| Modal | `aria-modal="true"` |
| Label | `aria-labelledby` → título |
| Description | `aria-describedby` → contenido (opcional) |
| Focus trap | Focus no sale del dialog |
| Initial focus | Primer elemento interactivo o close |
| Close | `Esc` cierra (excepto alertdialog crítico) |

### 4.7 Tables

| Requisito | Implementación |
|-----------|----------------|
| Semántica | `<table>`, `<thead>`, `<tbody>`, `<th>`, `<td>` |
| Headers | `<th scope="col">` para columnas |
| Sort | `aria-sort="ascending/descending/none"` |
| Selection | `aria-selected` en `<tr>` |
| Caption | `<caption>` o `aria-label` en `<table>` |

### 4.8 Snackbars

| Requisito | Implementación |
|-----------|----------------|
| Announcement | `role="status"` o `aria-live="polite"` |
| Action | Focuseable, descriptivo |
| Dismiss | No requiere acción para mensajes informativos |
| Timing | Suficiente tiempo para leer (mín. 4s) |

### 4.9 Resumen de Target Sizes

| Componente | Tamaño mínimo |
|------------|---------------|
| Buttons | `48×48dp` área táctil |
| Icon buttons | `48×48dp` |
| Checkboxes | `48×48dp` |
| Radio buttons | `48×48dp` |
| List items | `48dp` altura |
| Chips | `32dp` altura, `48dp` touch target |
| Close buttons | `48×48dp` |

---

## 5. Componentes NO Permitidos

| ❌ Prohibido | ✅ Alternativa MD3 |
|-------------|-------------------|
| Bootstrap buttons | MD3 Buttons |
| Ant Design inputs | MD3 Text Fields |
| Vuetify cards | MD3 Cards |
| Custom dropdowns | MD3 Menu |
| Toast libraries externas | MD3 Snackbar |
| Modal libraries externas | MD3 Dialog |
| Custom checkboxes/radios | MD3 Checkbox/Radio |
| Icon fonts (FontAwesome) | Material Symbols |

---

## 6. Checklist de Implementación

### Por Componente Nuevo

- [ ] Usa solo tokens MD3 (colores, spacing, typography)
- [ ] Implementa todos los estados requeridos
- [ ] Touch target mínimo 48dp
- [ ] Focus visible implementado
- [ ] Atributos ARIA correctos
- [ ] Funciona con keyboard
- [ ] Probado en Light y Dark mode
- [ ] Responsive (mobile → desktop)

### Por Pantalla Nueva

- [ ] Layout sigue patrones definidos (form, list, dialog)
- [ ] Jerarquía de botones correcta (máx 1 filled)
- [ ] Estados de carga/error/vacío implementados
- [ ] Navegación por keyboard funcional
- [ ] Anunciado correctamente para screen readers

---

*Fin del documento A-03 - UI Kit MD3*
