# A-05: Checklist de Validación MD3 y Accesibilidad
## Plataforma Interna de Arquitecsoft

**Fecha:** Febrero 2026  
**Versión:** 1.0  
**Uso:** Validar pantallas antes de merge/deploy

---

## Instrucciones de Uso

1. Aplicar este checklist a **cada pantalla nueva o modificada**
2. Marcar ✅ (cumple), ❌ (no cumple), o N/A (no aplica)
3. **Todos los items deben ser ✅ o N/A** para aprobar
4. Documentar excepciones con justificación

---

## 1. Color & Contraste

### 1.1 Uso de Tokens

| # | Criterio | ✅/❌ |
|---|----------|-------|
| 1.1.1 | No hay colores hex hardcoded (#xxx) | |
| 1.1.2 | No hay colores rgb/rgba/hsl directos | |
| 1.1.3 | Todos los colores usan `var(--md-sys-color-*)` | |
| 1.1.4 | Backgrounds usan tokens `surface-*` correctos | |
| 1.1.5 | Textos usan tokens `on-surface*` correspondientes | |

### 1.2 Paletas Tonales

| # | Criterio | ✅/❌ |
|---|----------|-------|
| 1.2.1 | Primary se usa para acciones principales | |
| 1.2.2 | Secondary se usa para elementos secundarios | |
| 1.2.3 | Tertiary se usa solo para acentos (badges, indicadores) | |
| 1.2.4 | Error se usa exclusivamente para estados de error | |
| 1.2.5 | No se mezclan colores de diferentes propósitos | |

### 1.3 Contraste

| # | Criterio | ✅/❌ |
|---|----------|-------|
| 1.3.1 | Texto normal: contraste ≥ 4.5:1 | |
| 1.3.2 | Texto grande (≥18px bold o ≥24px): contraste ≥ 3:1 | |
| 1.3.3 | Componentes UI: contraste ≥ 3:1 con fondo | |
| 1.3.4 | Funciona correctamente en Light mode | |
| 1.3.5 | Funciona correctamente en Dark mode | |

### 1.4 Estados de Color

| # | Criterio | ✅/❌ |
|---|----------|-------|
| 1.4.1 | Hover usa state layer (8% opacity) | |
| 1.4.2 | Focus usa state layer (12% opacity) | |
| 1.4.3 | Pressed usa state layer (12% opacity) | |
| 1.4.4 | Disabled usa 38% opacity para contenido | |
| 1.4.5 | Disabled usa 12% opacity para containers | |
| 1.4.6 | Selected usa `*-container` tokens | |
| 1.4.7 | Error usa tokens `error` / `on-error` | |

---

## 2. Tipografía

### 2.1 Type Scale

| # | Criterio | ✅/❌ |
|---|----------|-------|
| 2.1.1 | No hay font-size hardcoded (px sin token) | |
| 2.1.2 | No hay font-weight hardcoded | |
| 2.1.3 | No hay line-height hardcoded | |
| 2.1.4 | Todos los textos usan `var(--md-sys-typescale-*)` | |
| 2.1.5 | Solo se usan fuentes aprobadas (Roboto, Google Sans) | |

### 2.2 Jerarquía Correcta

| # | Criterio | ✅/❌ |
|---|----------|-------|
| 2.2.1 | Títulos de página usan `headline-*` | |
| 2.2.2 | Títulos de sección usan `title-*` | |
| 2.2.3 | Cuerpo de texto usa `body-*` | |
| 2.2.4 | Labels y captions usan `label-*` | |
| 2.2.5 | Hay jerarquía visual clara (no todo igual) | |
| 2.2.6 | Máximo 3-4 niveles tipográficos por pantalla | |

### 2.3 Legibilidad

| # | Criterio | ✅/❌ |
|---|----------|-------|
| 2.3.1 | Líneas de texto ≤ 80 caracteres | |
| 2.3.2 | Párrafos tienen espaciado adecuado | |
| 2.3.3 | No hay texto truncado sin indicador (...) | |
| 2.3.4 | Texto importante no depende solo del color | |

---

## 3. Componentes

### 3.1 Uso Correcto de Variantes

| # | Criterio | ✅/❌ |
|---|----------|-------|
| 3.1.1 | Solo se usan componentes `Md*` del UI Kit | |
| 3.1.2 | No hay imports de Bootstrap/Vuetify/Ant/otros | |
| 3.1.3 | Máximo 1 Filled Button por sección visible | |
| 3.1.4 | Botones secundarios usan Outlined o Text | |
| 3.1.5 | Text Fields usan variante Filled (estándar) | |
| 3.1.6 | Cards usan variante apropiada al contexto | |
| 3.1.7 | Chips Filter para filtros, Input para tags/selección | |

### 3.2 Anatomía de Componentes

| # | Criterio | ✅/❌ |
|---|----------|-------|
| 3.2.1 | Buttons tienen altura mínima 40dp | |
| 3.2.2 | Text Fields tienen altura 56dp | |
| 3.2.3 | List items tienen altura mínima 48dp | |
| 3.2.4 | Chips tienen altura 32dp | |
| 3.2.5 | Icons son 24dp (default) o 18dp (en buttons) | |
| 3.2.6 | Avatars son 40dp (default) | |

### 3.3 Consistencia

| # | Criterio | ✅/❌ |
|---|----------|-------|
| 3.3.1 | Mismo componente = mismo estilo en toda la app | |
| 3.3.2 | No hay variantes custom inventadas | |
| 3.3.3 | No hay estilos inline que overriden tokens | |
| 3.3.4 | No hay `!important` en estilos | |

---

## 4. Layout & Spacing

### 4.1 Spacing

| # | Criterio | ✅/❌ |
|---|----------|-------|
| 4.1.1 | No hay padding/margin hardcoded | |
| 4.1.2 | Todos los espaciados usan `var(--md-sys-spacing-*)` | |
| 4.1.3 | Espaciado entre secciones: 24-32dp | |
| 4.1.4 | Espaciado entre elementos relacionados: 8-16dp | |
| 4.1.5 | Padding de containers: 16-24dp | |
| 4.1.6 | Gap entre botones de acción: 8dp | |

### 4.2 Grid & Alignment

| # | Criterio | ✅/❌ |
|---|----------|-------|
| 4.2.1 | Contenido alineado a grid de 4dp | |
| 4.2.2 | Formularios usan grid 1-2 columnas según breakpoint | |
| 4.2.3 | Elementos están alineados visualmente | |
| 4.2.4 | No hay desalineaciones entre componentes | |

### 4.3 Superficies

| # | Criterio | ✅/❌ |
|---|----------|-------|
| 4.3.1 | Jerarquía de superficies clara (elevation/color) | |
| 4.3.2 | Cards usan `corner-medium` (12dp) | |
| 4.3.3 | Dialogs usan `corner-extra-large` (28dp) | |
| 4.3.4 | Buttons usan `corner-full` | |
| 4.3.5 | No hay border-radius hardcoded | |

### 4.4 Responsive

| # | Criterio | ✅/❌ |
|---|----------|-------|
| 4.4.1 | Funciona en Compact (< 600dp) - Mobile | |
| 4.4.2 | Funciona en Medium (600-839dp) - Tablet | |
| 4.4.3 | Funciona en Expanded (840dp+) - Desktop | |
| 4.4.4 | Navigation rail visible en desktop | |
| 4.4.5 | Navigation drawer en mobile | |
| 4.4.6 | Formularios adaptan columnas según breakpoint | |
| 4.4.7 | Tablas se adaptan o usan scroll horizontal | |
| 4.4.8 | Dialogs son full-screen en mobile | |

---

## 5. Interacciones & Estados

### 5.1 Estados Hover

| # | Criterio | ✅/❌ |
|---|----------|-------|
| 5.1.1 | Buttons muestran hover (state layer 8%) | |
| 5.1.2 | Cards clickeables muestran hover | |
| 5.1.3 | List items muestran hover | |
| 5.1.4 | Table rows muestran hover | |
| 5.1.5 | Links muestran hover | |

### 5.2 Estados Focus

| # | Criterio | ✅/❌ |
|---|----------|-------|
| 5.2.1 | Todos los elementos interactivos tienen focus visible | |
| 5.2.2 | Focus ring usa color `primary` | |
| 5.2.3 | Focus ring tiene offset de 2dp | |
| 5.2.4 | Focus ring tiene grosor 2-3dp | |
| 5.2.5 | Orden de focus es lógico (izq→der, arriba→abajo) | |
| 5.2.6 | No hay elementos focuseables ocultos | |

### 5.3 Estados Disabled

| # | Criterio | ✅/❌ |
|---|----------|-------|
| 5.3.1 | Elementos disabled tienen 38% opacity (contenido) | |
| 5.3.2 | Elementos disabled no son clickeables | |
| 5.3.3 | Cursor cambia a `not-allowed` en disabled | |
| 5.3.4 | Disabled es visualmente distinguible | |

### 5.4 Estados Error

| # | Criterio | ✅/❌ |
|---|----------|-------|
| 5.4.1 | Campos con error muestran borde `error` | |
| 5.4.2 | Mensaje de error visible debajo del campo | |
| 5.4.3 | Mensaje de error usa color `error` | |
| 5.4.4 | Icono de error opcional pero consistente | |
| 5.4.5 | Error no desaparece hasta que se corrige | |

### 5.5 Estados Loading

| # | Criterio | ✅/❌ |
|---|----------|-------|
| 5.5.1 | Botones muestran spinner al cargar | |
| 5.5.2 | Botones se deshabilitan durante carga | |
| 5.5.3 | Listas muestran skeleton o spinner | |
| 5.5.4 | Páginas muestran indicador de carga | |
| 5.5.5 | Loading no bloquea toda la UI innecesariamente | |

### 5.6 Estados Vacío/Error de Datos

| # | Criterio | ✅/❌ |
|---|----------|-------|
| 5.6.1 | Estado vacío tiene mensaje claro | |
| 5.6.2 | Estado vacío tiene CTA cuando aplica | |
| 5.6.3 | Error de carga tiene mensaje descriptivo | |
| 5.6.4 | Error de carga tiene botón "Reintentar" | |

---

## 6. Accesibilidad

### 6.1 Focus & Navegación Keyboard

| # | Criterio | ✅/❌ |
|---|----------|-------|
| 6.1.1 | Todos los elementos interactivos son alcanzables con Tab | |
| 6.1.2 | Focus visible en todos los elementos interactivos | |
| 6.1.3 | Orden de tab es lógico y predecible | |
| 6.1.4 | No hay trampas de focus (excepto modales) | |
| 6.1.5 | Esc cierra modales/menus | |
| 6.1.6 | Enter activa buttons y submits | |
| 6.1.7 | Space activa checkboxes y buttons | |
| 6.1.8 | Arrows navegan en menus/selects/radio groups | |

### 6.2 Tamaños Táctiles

| # | Criterio | ✅/❌ |
|---|----------|-------|
| 6.2.1 | Todos los touch targets ≥ 48x48dp | |
| 6.2.2 | Espacio entre targets ≥ 8dp | |
| 6.2.3 | Checkboxes tienen área táctil 48x48dp | |
| 6.2.4 | Radio buttons tienen área táctil 48x48dp | |
| 6.2.5 | Icon buttons tienen área táctil 48x48dp | |
| 6.2.6 | Close buttons (×) tienen área táctil 48x48dp | |

### 6.3 Labels & ARIA

| # | Criterio | ✅/❌ |
|---|----------|-------|
| 6.3.1 | Todos los inputs tienen label asociado | |
| 6.3.2 | Labels usan `for`/`id` o `aria-label` | |
| 6.3.3 | Campos requeridos tienen `aria-required="true"` | |
| 6.3.4 | Campos con error tienen `aria-invalid="true"` | |
| 6.3.5 | Errores vinculados con `aria-describedby` | |
| 6.3.6 | Buttons tienen texto o `aria-label` | |
| 6.3.7 | Icon-only buttons tienen `aria-label` descriptivo | |
| 6.3.8 | Imágenes decorativas tienen `aria-hidden="true"` | |
| 6.3.9 | Imágenes informativas tienen `alt` descriptivo | |

### 6.4 Modales & Dialogs

| # | Criterio | ✅/❌ |
|---|----------|-------|
| 6.4.1 | Modal tiene `role="dialog"` | |
| 6.4.2 | Modal tiene `aria-modal="true"` | |
| 6.4.3 | Modal tiene `aria-labelledby` → título | |
| 6.4.4 | Focus queda atrapado dentro del modal | |
| 6.4.5 | Focus inicial en primer elemento o close | |
| 6.4.6 | Al cerrar, focus vuelve al elemento que abrió | |
| 6.4.7 | Esc cierra el modal | |

### 6.5 Tablas de Datos

| # | Criterio | ✅/❌ |
|---|----------|-------|
| 6.5.1 | Usa elementos semánticos `<table>`, `<th>`, `<td>` | |
| 6.5.2 | Headers tienen `scope="col"` o `scope="row"` | |
| 6.5.3 | Tabla tiene `<caption>` o `aria-label` | |
| 6.5.4 | Ordenamiento indica `aria-sort` | |
| 6.5.5 | Filas seleccionables tienen `aria-selected` | |

### 6.6 Feedback & Anuncios

| # | Criterio | ✅/❌ |
|---|----------|-------|
| 6.6.1 | Snackbars tienen `role="status"` o `aria-live` | |
| 6.6.2 | Errores de form se anuncian a screen readers | |
| 6.6.3 | Cambios de página se anuncian | |
| 6.6.4 | Loading states se anuncian (`aria-busy`) | |

### 6.7 Contenido

| # | Criterio | ✅/❌ |
|---|----------|-------|
| 6.7.1 | Información no depende solo del color | |
| 6.7.2 | Links son distinguibles (no solo por color) | |
| 6.7.3 | Texto no está en imágenes | |
| 6.7.4 | No hay contenido que parpadea >3 veces/segundo | |

---

## 7. Ejemplos de Aplicación

### 7.1 Ejemplo: Formulario de Creación

**Pantalla:** Crear Oportunidad

#### Checklist Aplicado

| Sección | Item | ✅/❌ | Notas |
|---------|------|-------|-------|
| **1. Color** | 1.1.1 No hex hardcoded | ✅ | |
| | 1.1.3 Usa tokens `--md-sys-color-*` | ✅ | |
| | 1.3.4 Funciona Light mode | ✅ | |
| | 1.3.5 Funciona Dark mode | ✅ | |
| | 1.4.7 Error usa tokens error | ✅ | Campo "Nombre" muestra error |
| **2. Tipografía** | 2.2.1 Título usa headline | ✅ | `headline-small`: "Crear Oportunidad" |
| | 2.2.2 Secciones usan title | ✅ | `title-medium`: "Información General" |
| | 2.2.3 Labels usan label/body | ✅ | |
| **3. Componentes** | 3.1.1 Solo componentes Md* | ✅ | MdTextField, MdSelect, MdButton |
| | 3.1.3 Máx 1 Filled button | ✅ | Solo "Guardar" es Filled |
| | 3.1.4 Secundarios Outlined/Text | ✅ | "Cancelar" es Text |
| | 3.2.2 Text Fields 56dp altura | ✅ | |
| **4. Layout** | 4.1.2 Spacing usa tokens | ✅ | |
| | 4.2.2 Grid 2 columnas desktop | ✅ | |
| | 4.4.1 Funciona mobile | ✅ | Grid 1 columna |
| **5. Estados** | 5.2.1 Focus visible | ✅ | Ring azul en campos |
| | 5.4.1 Error muestra borde | ✅ | |
| | 5.4.2 Mensaje error visible | ✅ | "Este campo es requerido" |
| | 5.5.1 Botón spinner al guardar | ✅ | |
| **6. Accesibilidad** | 6.1.1 Tab alcanza todos los campos | ✅ | |
| | 6.2.1 Touch targets 48dp | ✅ | |
| | 6.3.1 Inputs tienen label | ✅ | Floating labels |
| | 6.3.3 Required tiene aria-required | ✅ | |
| | 6.3.5 Errores con aria-describedby | ✅ | |

**Resultado:** ✅ **APROBADO**

---

### 7.2 Ejemplo: Listado con Filtros

**Pantalla:** Lista de Oportunidades

#### Checklist Aplicado

| Sección | Item | ✅/❌ | Notas |
|---------|------|-------|-------|
| **1. Color** | 1.1.1 No hex hardcoded | ✅ | |
| | 1.2.1 Primary en acciones principales | ✅ | Botón "+ Nueva" |
| | 1.4.1 Hover state layer 8% | ✅ | En filas de tabla |
| | 1.4.6 Selected usa container | ✅ | Chips filtro seleccionados |
| **2. Tipografía** | 2.2.1 Título página headline | ✅ | `headline-medium`: "Oportunidades" |
| | 2.2.3 Contenido tabla body | ✅ | `body-medium` |
| **3. Componentes** | 3.1.1 Solo componentes Md* | ✅ | MdDataTable, MdChip, MdButton |
| | 3.1.7 Filter chips para filtros | ✅ | |
| | 3.2.3 List items 48dp+ | ✅ | Filas 52dp |
| **4. Layout** | 4.1.4 Spacing elementos 8-16dp | ✅ | Gap entre chips: 8dp |
| | 4.4.7 Tabla adapta o scroll | ✅ | Scroll horizontal en mobile |
| **5. Estados** | 5.1.4 Table rows hover | ✅ | |
| | 5.5.3 Lista skeleton al cargar | ✅ | |
| | 5.6.1 Estado vacío mensaje | ✅ | "No hay oportunidades" |
| | 5.6.2 Estado vacío CTA | ✅ | "Crear primera oportunidad" |
| **6. Accesibilidad** | 6.1.1 Tab navega tabla | ✅ | |
| | 6.5.1 Usa `<table>` semántico | ✅ | |
| | 6.5.2 Headers con scope | ✅ | `scope="col"` |
| | 6.5.4 Sort indica aria-sort | ✅ | |

**Resultado:** ✅ **APROBADO**

---

### 7.3 Ejemplo: Diálogo de Confirmación

**Pantalla:** Diálogo "Eliminar Oportunidad"

#### Checklist Aplicado

| Sección | Item | ✅/❌ | Notas |
|---------|------|-------|-------|
| **1. Color** | 1.2.4 Error para acciones destructivas | ✅ | Botón "Eliminar" usa error |
| | 1.3.3 Contraste componentes 3:1 | ✅ | |
| **2. Tipografía** | 2.2.1 Título usa headline | ✅ | `headline-small` |
| | 2.2.3 Cuerpo usa body | ✅ | `body-medium` |
| **3. Componentes** | 3.1.1 Solo Md* | ✅ | MdDialog, MdButton |
| | 3.1.3 Máx 1 Filled | ✅ | Solo "Eliminar" destacado |
| | 3.2.1 Buttons 40dp altura | ✅ | |
| **4. Layout** | 4.3.3 Dialog corner-extra-large | ✅ | 28dp radius |
| | 4.1.5 Padding 24dp | ✅ | |
| **5. Estados** | 5.2.1 Focus visible en botones | ✅ | |
| | 5.2.5 Orden focus lógico | ✅ | Cancelar → Eliminar |
| | 5.5.1 Spinner al confirmar | ✅ | |
| **6. Accesibilidad** | 6.4.1 role="dialog" | ✅ | |
| | 6.4.2 aria-modal="true" | ✅ | |
| | 6.4.3 aria-labelledby | ✅ | → título |
| | 6.4.4 Focus trap | ✅ | |
| | 6.4.5 Focus inicial en Cancelar | ✅ | Acción menos destructiva |
| | 6.4.7 Esc cierra | ✅ | |

**Resultado:** ✅ **APROBADO**

---

## 8. Resumen Rápido (Quick Check)

Para validaciones rápidas, usar esta versión condensada:

### ✅ Quick Check - 20 Items Críticos

| # | Criterio | ✅/❌ |
|---|----------|-------|
| 1 | No hay colores hardcoded | |
| 2 | No hay tamaños hardcoded | |
| 3 | Solo componentes Md* del UI Kit | |
| 4 | Máximo 1 Filled Button por sección | |
| 5 | Funciona en Light mode | |
| 6 | Funciona en Dark mode | |
| 7 | Funciona en mobile | |
| 8 | Funciona en desktop | |
| 9 | Todos los interactivos tienen hover | |
| 10 | Todos los interactivos tienen focus visible | |
| 11 | Estados disabled visualmente claros | |
| 12 | Estados error visibles y con mensaje | |
| 13 | Loading states implementados | |
| 14 | Estado vacío con mensaje y CTA | |
| 15 | Touch targets ≥ 48dp | |
| 16 | Todos los inputs tienen labels | |
| 17 | Tab navega todos los elementos | |
| 18 | Esc cierra modales | |
| 19 | Screen reader puede entender la página | |
| 20 | No hay trampas de focus | |

**Regla:** Si alguno es ❌, no aprobar el PR.

---

## 9. Herramientas de Validación

| Herramienta | Uso | URL |
|-------------|-----|-----|
| Lighthouse | Auditoría accesibilidad | Chrome DevTools |
| axe DevTools | Testing a11y detallado | chrome.google.com/webstore |
| WAVE | Evaluación visual a11y | wave.webaim.org |
| Contrast Checker | Verificar ratios | webaim.org/resources/contrastchecker |
| Material Theme Builder | Validar paletas | material-foundation.github.io |

---

*Fin del documento A-05 - Checklist de Validación MD3*
