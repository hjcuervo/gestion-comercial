# A-02: Design Tokens MD3
## Plataforma Interna de Arquitecsoft - Sistema de Tokens

**Fecha:** Febrero 2026  
**Versión:** 1.0  
**Clasificación:** Interno - Arquitecsoft

---

## 1. Especificación de Tokens

### 1.1 Color - Sistema de Paletas Tonales MD3

MD3 utiliza **paletas tonales** de 13 niveles (0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 95, 99, 100) para cada color clave. Los tokens de color del sistema se derivan de estas paletas.

#### 1.1.1 Colores Clave (Key Colors)

| Color Clave | Propósito | Valor Base Sugerido |
|-------------|-----------|---------------------|
| **Primary** | Acción principal, elementos destacados | `#1A73E8` (azul corporativo) |
| **Secondary** | Acciones secundarias, filtros | `#5F6368` (gris neutro) |
| **Tertiary** | Acentos, badges, indicadores | `#1E8E3E` (verde éxito) |
| **Error** | Estados de error, validación | `#D93025` (rojo error) |
| **Neutral** | Superficies, textos, fondos | `#5F6368` (gris base) |
| **Neutral Variant** | Outlines, variantes sutiles | `#79747E` (gris variante) |

#### 1.1.2 Paleta Tonal - Primary

| Tono | Token | Light Mode | Dark Mode |
|------|-------|------------|-----------|
| 0 | `--md-ref-palette-primary0` | `#000000` | — |
| 10 | `--md-ref-palette-primary10` | `#001D36` | — |
| 20 | `--md-ref-palette-primary20` | `#003258` | — |
| 30 | `--md-ref-palette-primary30` | `#00497D` | — |
| 40 | `--md-ref-palette-primary40` | `#0061A4` | — |
| 50 | `--md-ref-palette-primary50` | `#1A73E8` | — |
| 60 | `--md-ref-palette-primary60` | `#4A90E2` | — |
| 70 | `--md-ref-palette-primary70` | `#7CACF8` | — |
| 80 | `--md-ref-palette-primary80` | `#A8C7FA` | — |
| 90 | `--md-ref-palette-primary90` | `#D3E3FD` | — |
| 95 | `--md-ref-palette-primary95` | `#ECF3FE` | — |
| 99 | `--md-ref-palette-primary99` | `#FDFCFF` | — |
| 100 | `--md-ref-palette-primary100` | `#FFFFFF` | — |

#### 1.1.3 Tokens de Sistema de Color

**Surface & Background:**

| Token | Descripción | Light | Dark |
|-------|-------------|-------|------|
| `--md-sys-color-surface` | Fondo principal | N-98 | N-6 |
| `--md-sys-color-surface-dim` | Superficie atenuada | N-87 | N-6 |
| `--md-sys-color-surface-bright` | Superficie brillante | N-98 | N-24 |
| `--md-sys-color-surface-container-lowest` | Container más bajo | N-100 | N-4 |
| `--md-sys-color-surface-container-low` | Container bajo | N-96 | N-10 |
| `--md-sys-color-surface-container` | Container medio | N-94 | N-12 |
| `--md-sys-color-surface-container-high` | Container alto | N-92 | N-17 |
| `--md-sys-color-surface-container-highest` | Container más alto | N-90 | N-22 |
| `--md-sys-color-on-surface` | Texto sobre surface | N-10 | N-90 |
| `--md-sys-color-on-surface-variant` | Texto secundario | NV-30 | NV-80 |

**Primary:**

| Token | Descripción | Light | Dark |
|-------|-------------|-------|------|
| `--md-sys-color-primary` | Color primario | P-40 | P-80 |
| `--md-sys-color-on-primary` | Texto sobre primary | P-100 | P-20 |
| `--md-sys-color-primary-container` | Container primary | P-90 | P-30 |
| `--md-sys-color-on-primary-container` | Texto sobre container | P-10 | P-90 |

**Secondary:**

| Token | Descripción | Light | Dark |
|-------|-------------|-------|------|
| `--md-sys-color-secondary` | Color secundario | S-40 | S-80 |
| `--md-sys-color-on-secondary` | Texto sobre secondary | S-100 | S-20 |
| `--md-sys-color-secondary-container` | Container secondary | S-90 | S-30 |
| `--md-sys-color-on-secondary-container` | Texto sobre container | S-10 | S-90 |

**Tertiary:**

| Token | Descripción | Light | Dark |
|-------|-------------|-------|------|
| `--md-sys-color-tertiary` | Color terciario | T-40 | T-80 |
| `--md-sys-color-on-tertiary` | Texto sobre tertiary | T-100 | T-20 |
| `--md-sys-color-tertiary-container` | Container tertiary | T-90 | T-30 |
| `--md-sys-color-on-tertiary-container` | Texto sobre container | T-10 | T-90 |

**Error:**

| Token | Descripción | Light | Dark |
|-------|-------------|-------|------|
| `--md-sys-color-error` | Color error | E-40 | E-80 |
| `--md-sys-color-on-error` | Texto sobre error | E-100 | E-20 |
| `--md-sys-color-error-container` | Container error | E-90 | E-30 |
| `--md-sys-color-on-error-container` | Texto sobre container | E-10 | E-90 |

**Outline & Misc:**

| Token | Descripción | Light | Dark |
|-------|-------------|-------|------|
| `--md-sys-color-outline` | Bordes, dividers | NV-50 | NV-60 |
| `--md-sys-color-outline-variant` | Bordes sutiles | NV-80 | NV-30 |
| `--md-sys-color-shadow` | Sombras | N-0 | N-0 |
| `--md-sys-color-scrim` | Overlay modales | N-0 | N-0 |
| `--md-sys-color-inverse-surface` | Surface invertida | N-20 | N-90 |
| `--md-sys-color-inverse-on-surface` | Texto invertido | N-95 | N-20 |
| `--md-sys-color-inverse-primary` | Primary invertido | P-80 | P-40 |

---

### 1.2 Tipografía - MD3 Type Scale

#### 1.2.1 Font Family

| Token | Valor | Uso |
|-------|-------|-----|
| `--md-sys-typescale-font-family-brand` | `'Google Sans', 'Roboto', sans-serif` | Títulos, headlines |
| `--md-sys-typescale-font-family-plain` | `'Roboto', sans-serif` | Body, labels |

#### 1.2.2 Type Scale Completo

**Display:**

| Token | Font | Weight | Size | Line Height | Letter Spacing |
|-------|------|--------|------|-------------|----------------|
| `--md-sys-typescale-display-large` | Brand | 400 | `57px` | `64px` | `-0.25px` |
| `--md-sys-typescale-display-medium` | Brand | 400 | `45px` | `52px` | `0px` |
| `--md-sys-typescale-display-small` | Brand | 400 | `36px` | `44px` | `0px` |

**Headline:**

| Token | Font | Weight | Size | Line Height | Letter Spacing |
|-------|------|--------|------|-------------|----------------|
| `--md-sys-typescale-headline-large` | Brand | 400 | `32px` | `40px` | `0px` |
| `--md-sys-typescale-headline-medium` | Brand | 400 | `28px` | `36px` | `0px` |
| `--md-sys-typescale-headline-small` | Brand | 400 | `24px` | `32px` | `0px` |

**Title:**

| Token | Font | Weight | Size | Line Height | Letter Spacing |
|-------|------|--------|------|-------------|----------------|
| `--md-sys-typescale-title-large` | Brand | 400 | `22px` | `28px` | `0px` |
| `--md-sys-typescale-title-medium` | Plain | 500 | `16px` | `24px` | `0.15px` |
| `--md-sys-typescale-title-small` | Plain | 500 | `14px` | `20px` | `0.1px` |

**Body:**

| Token | Font | Weight | Size | Line Height | Letter Spacing |
|-------|------|--------|------|-------------|----------------|
| `--md-sys-typescale-body-large` | Plain | 400 | `16px` | `24px` | `0.5px` |
| `--md-sys-typescale-body-medium` | Plain | 400 | `14px` | `20px` | `0.25px` |
| `--md-sys-typescale-body-small` | Plain | 400 | `12px` | `16px` | `0.4px` |

**Label:**

| Token | Font | Weight | Size | Line Height | Letter Spacing |
|-------|------|--------|------|-------------|----------------|
| `--md-sys-typescale-label-large` | Plain | 500 | `14px` | `20px` | `0.1px` |
| `--md-sys-typescale-label-medium` | Plain | 500 | `12px` | `16px` | `0.5px` |
| `--md-sys-typescale-label-small` | Plain | 500 | `11px` | `16px` | `0.5px` |

---

### 1.3 Border Radius (Shape)

MD3 define esquinas redondeadas en escalas predefinidas:

| Token | Valor | Uso |
|-------|-------|-----|
| `--md-sys-shape-corner-none` | `0px` | Elementos sin redondeo |
| `--md-sys-shape-corner-extra-small` | `4px` | Chips, badges pequeños |
| `--md-sys-shape-corner-small` | `8px` | Buttons, text fields |
| `--md-sys-shape-corner-medium` | `12px` | Cards, dialogs |
| `--md-sys-shape-corner-large` | `16px` | Modales grandes, sheets |
| `--md-sys-shape-corner-extra-large` | `28px` | FAB, containers destacados |
| `--md-sys-shape-corner-full` | `9999px` | Pills, avatares circulares |

**Aplicación por componente:**

| Componente | Shape Token |
|------------|-------------|
| Button (filled, outlined) | `corner-full` |
| Button (elevated, tonal) | `corner-full` |
| FAB | `corner-large` |
| Card | `corner-medium` |
| Dialog | `corner-extra-large` |
| Text field | `corner-extra-small` (top) |
| Chip | `corner-small` |
| Menu | `corner-extra-small` |
| Navigation rail | `corner-none` |
| Bottom sheet | `corner-extra-large` (top) |

---

### 1.4 Elevation

MD3 usa niveles de elevación que combinan sombra (light) y tint (dark):

| Token | Nivel | Shadow (Light) | Surface Tint (Dark) |
|-------|-------|----------------|---------------------|
| `--md-sys-elevation-level0` | 0 | none | 0% tint |
| `--md-sys-elevation-level1` | 1 | `0 1px 2px rgba(0,0,0,0.3), 0 1px 3px rgba(0,0,0,0.15)` | 5% tint |
| `--md-sys-elevation-level2` | 2 | `0 1px 2px rgba(0,0,0,0.3), 0 2px 6px rgba(0,0,0,0.15)` | 8% tint |
| `--md-sys-elevation-level3` | 3 | `0 4px 8px rgba(0,0,0,0.3), 0 1px 3px rgba(0,0,0,0.15)` | 11% tint |
| `--md-sys-elevation-level4` | 4 | `0 6px 10px rgba(0,0,0,0.3), 0 1px 3px rgba(0,0,0,0.15)` | 12% tint |
| `--md-sys-elevation-level5` | 5 | `0 8px 12px rgba(0,0,0,0.3), 0 1px 3px rgba(0,0,0,0.15)` | 14% tint |

**Aplicación por componente:**

| Componente | Estado | Elevation |
|------------|--------|-----------|
| Card (elevated) | Resting | Level 1 |
| Card (elevated) | Hovered | Level 2 |
| Navigation rail | — | Level 0 |
| Top app bar | Resting | Level 0 |
| Top app bar | Scrolled | Level 2 |
| FAB | Resting | Level 3 |
| FAB | Hovered | Level 4 |
| Dialog | — | Level 3 |
| Menu | — | Level 2 |

---

### 1.5 Spacing

Sistema de espaciado basado en múltiplos de 4dp:

| Token | Valor | Uso típico |
|-------|-------|------------|
| `--md-sys-spacing-0` | `0px` | Reset |
| `--md-sys-spacing-1` | `4px` | Gaps mínimos, padding interno icons |
| `--md-sys-spacing-2` | `8px` | Gap entre elementos relacionados |
| `--md-sys-spacing-3` | `12px` | Padding interno chips |
| `--md-sys-spacing-4` | `16px` | Padding estándar, gaps |
| `--md-sys-spacing-5` | `20px` | — |
| `--md-sys-spacing-6` | `24px` | Padding containers, secciones |
| `--md-sys-spacing-8` | `32px` | Separación entre secciones |
| `--md-sys-spacing-10` | `40px` | Márgenes grandes |
| `--md-sys-spacing-12` | `48px` | Altura elementos interactivos |
| `--md-sys-spacing-16` | `64px` | — |
| `--md-sys-spacing-20` | `80px` | Ancho navigation rail |

**Grid & Layout:**

| Token | Valor | Uso |
|-------|-------|-----|
| `--md-sys-layout-margin-compact` | `16px` | Margen lateral mobile |
| `--md-sys-layout-margin-medium` | `24px` | Margen lateral tablet+ |
| `--md-sys-layout-gutter-compact` | `8px` | Gutter mobile |
| `--md-sys-layout-gutter-medium` | `16px` | Gutter tablet |
| `--md-sys-layout-gutter-expanded` | `24px` | Gutter desktop |

---

### 1.6 State Layers

Opacidades para estados interactivos (aplicados sobre color base):

| Token | Opacidad | Uso |
|-------|----------|-----|
| `--md-sys-state-hover-state-layer-opacity` | `0.08` | Hover |
| `--md-sys-state-focus-state-layer-opacity` | `0.12` | Focus |
| `--md-sys-state-pressed-state-layer-opacity` | `0.12` | Pressed/Active |
| `--md-sys-state-dragged-state-layer-opacity` | `0.16` | Dragged |
| `--md-sys-state-disabled-container-opacity` | `0.12` | Container disabled |
| `--md-sys-state-disabled-content-opacity` | `0.38` | Content disabled |

---

## 2. Estrategia de Theming

### 2.1 Light / Dark Mode

**Implementación:**

```
┌─────────────────────────────────────────────────────────────┐
│                    TOKENS DE REFERENCIA                     │
│         (Paletas tonales - invariables por tema)            │
│  --md-ref-palette-primary0 ... --md-ref-palette-primary100  │
│  --md-ref-palette-secondary0 ... etc                        │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    TOKENS DE SISTEMA                        │
│              (Varían según Light/Dark)                      │
│                                                             │
│  ┌─────────────────┐        ┌─────────────────┐            │
│  │   LIGHT MODE    │        │   DARK MODE     │            │
│  │                 │        │                 │            │
│  │ --md-sys-color- │        │ --md-sys-color- │            │
│  │ primary: P-40   │        │ primary: P-80   │            │
│  │ surface: N-98   │        │ surface: N-6    │            │
│  │ ...             │        │ ...             │            │
│  └─────────────────┘        └─────────────────┘            │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                     COMPONENTES                             │
│            (Solo consumen tokens de sistema)                │
│                                                             │
│   background: var(--md-sys-color-surface);                  │
│   color: var(--md-sys-color-on-surface);                    │
└─────────────────────────────────────────────────────────────┘
```

**Mecanismo de cambio:**

| Método | Implementación |
|--------|----------------|
| Atributo HTML | `<html data-theme="dark">` |
| Detección sistema | `prefers-color-scheme: dark` |
| Persistencia | `localStorage.setItem('theme', 'dark')` |
| Toggle UI | Icon button en Top app bar |

**Prioridad de aplicación:**
1. Preferencia guardada del usuario (localStorage)
2. Preferencia del sistema operativo
3. Default: Light mode

### 2.2 Tematización por Cliente / Unidad de Negocio

**Principio:** Cambiar **colores clave** sin romper la estructura MD3.

**Qué se puede personalizar:**

| Elemento | Personalizable | Restricciones |
|----------|----------------|---------------|
| Primary color | ✅ Sí | Debe generar paleta tonal completa |
| Secondary color | ✅ Sí | Debe generar paleta tonal completa |
| Tertiary color | ✅ Sí | Debe generar paleta tonal completa |
| Logo | ✅ Sí | Formato y posición estándar |
| Nombre app | ✅ Sí | — |
| Typography | ⚠️ Limitado | Solo font-family, no sizes |
| Spacing | ❌ No | Rompe consistencia |
| Shapes | ❌ No | Rompe consistencia |
| Component behavior | ❌ No | Rompe consistencia |

**Estructura de temas:**

```
themes/
├── _base.css           ← Tokens MD3 base (obligatorio)
├── arquitecsoft.css    ← Tema default corporativo
├── unidad-a.css        ← Override solo de colores clave
└── unidad-b.css        ← Override solo de colores clave
```

**Ejemplo de tema personalizado:**

```css
/* unidad-a.css - Solo override de key colors */
:root {
  /* Regenerar paleta completa desde nuevo primary */
  --md-ref-palette-primary50: #6750A4;  /* Purple */
  /* ... resto de tonos generados ... */
  
  /* Los tokens de sistema se recalculan automáticamente */
}
```

**Generación de paletas:**

Para garantizar contraste y coherencia, usar:
- [Material Theme Builder](https://material-foundation.github.io/material-theme-builder/)
- Algoritmo HCT (Hue, Chroma, Tone) de MD3

**Validación obligatoria:**
- Contraste WCAG AA mínimo (4.5:1 texto, 3:1 componentes)
- Verificar Light y Dark mode
- Probar estados: hover, focus, disabled, error

---

## 3. Reglas - Lo que NO se Permite

### 3.1 Prohibiciones Absolutas

| ❌ Prohibido | ✅ Correcto | Razón |
|-------------|-------------|-------|
| `color: #1A73E8;` | `color: var(--md-sys-color-primary);` | Hardcode rompe theming |
| `font-size: 14px;` | `font-size: var(--md-sys-typescale-body-medium-size);` | Inconsistencia tipográfica |
| `padding: 15px;` | `padding: var(--md-sys-spacing-4);` | Spacing fuera de escala |
| `border-radius: 5px;` | `border-radius: var(--md-sys-shape-corner-small);` | Shape fuera de sistema |
| `box-shadow: 0 2px 4px...` | `box-shadow: var(--md-sys-elevation-level1);` | Elevation inconsistente |
| `opacity: 0.5;` | `opacity: var(--md-sys-state-disabled-content-opacity);` | Estados no estándar |

### 3.2 Validación de Colores

| ❌ Nunca | ✅ Siempre |
|---------|-----------|
| Colores hex directos en componentes | Tokens de sistema (`--md-sys-color-*`) |
| Colores RGB/HSL inline | Tokens de referencia para cálculos |
| `!important` en colores | Especificidad correcta via CSS |
| Opacidad manual sobre colores | State layers predefinidos |

### 3.3 Validación de Tipografía

| ❌ Nunca | ✅ Siempre |
|---------|-----------|
| `font-size` arbitrarios | Type scale tokens |
| `font-weight` sueltos | Peso definido en token |
| `line-height` custom | Line height del token |
| Fuentes no aprobadas | Font family tokens |

### 3.4 Validación de Layout

| ❌ Nunca | ✅ Siempre |
|---------|-----------|
| Margins/paddings arbitrarios | Spacing tokens |
| Breakpoints inventados | Breakpoints MD3 |
| Grid custom | Grid system MD3 |
| Componentes de otras librerías | Componentes MD3 únicamente |

### 3.5 Checklist de Code Review

Todo PR debe verificar:

- [ ] No hay valores hardcodeados de color
- [ ] No hay valores hardcodeados de tamaño/spacing
- [ ] Todos los componentes usan tokens MD3
- [ ] Estados (hover, focus, disabled) usan state layers correctos
- [ ] Contraste verificado en Light y Dark mode
- [ ] Sin dependencias de Bootstrap/Ant/Vuetify/otros

---

## 4. Estructura de Archivos para Tokens

### 4.1 Organización Recomendada

```
src/
├── styles/
│   ├── tokens/
│   │   ├── _index.css              ← Importa todos los tokens
│   │   ├── _reference.css          ← Paletas tonales (ref)
│   │   ├── _system-light.css       ← Tokens sistema Light
│   │   ├── _system-dark.css        ← Tokens sistema Dark
│   │   ├── _typography.css         ← Type scale
│   │   ├── _shape.css              ← Border radius
│   │   ├── _elevation.css          ← Shadows/tints
│   │   ├── _spacing.css            ← Spacing scale
│   │   └── _state.css              ← State layers
│   │
│   ├── themes/
│   │   ├── arquitecsoft.css        ← Tema default
│   │   └── [cliente].css           ← Temas adicionales
│   │
│   ├── base/
│   │   ├── _reset.css              ← CSS reset mínimo
│   │   └── _globals.css            ← Estilos globales
│   │
│   └── main.css                    ← Entry point
```

### 4.2 Orden de Importación

```css
/* main.css */

/* 1. Reset */
@import './base/_reset.css';

/* 2. Tokens de referencia (paletas) */
@import './tokens/_reference.css';

/* 3. Tokens de sistema */
@import './tokens/_system-light.css';
@import './tokens/_system-dark.css';
@import './tokens/_typography.css';
@import './tokens/_shape.css';
@import './tokens/_elevation.css';
@import './tokens/_spacing.css';
@import './tokens/_state.css';

/* 4. Tema activo */
@import './themes/arquitecsoft.css';

/* 5. Estilos globales */
@import './base/_globals.css';
```

### 4.3 Ejemplo de Archivo de Tokens

**`_spacing.css`:**

```css
:root {
  /* Spacing Scale - Múltiplos de 4 */
  --md-sys-spacing-0: 0px;
  --md-sys-spacing-1: 4px;
  --md-sys-spacing-2: 8px;
  --md-sys-spacing-3: 12px;
  --md-sys-spacing-4: 16px;
  --md-sys-spacing-5: 20px;
  --md-sys-spacing-6: 24px;
  --md-sys-spacing-8: 32px;
  --md-sys-spacing-10: 40px;
  --md-sys-spacing-12: 48px;
  --md-sys-spacing-16: 64px;
  --md-sys-spacing-20: 80px;

  /* Layout */
  --md-sys-layout-margin-compact: 16px;
  --md-sys-layout-margin-medium: 24px;
  --md-sys-layout-gutter-compact: 8px;
  --md-sys-layout-gutter-medium: 16px;
  --md-sys-layout-gutter-expanded: 24px;
}
```

**`_system-light.css`:**

```css
:root,
[data-theme="light"] {
  /* Primary */
  --md-sys-color-primary: var(--md-ref-palette-primary40);
  --md-sys-color-on-primary: var(--md-ref-palette-primary100);
  --md-sys-color-primary-container: var(--md-ref-palette-primary90);
  --md-sys-color-on-primary-container: var(--md-ref-palette-primary10);

  /* Surface */
  --md-sys-color-surface: var(--md-ref-palette-neutral98);
  --md-sys-color-on-surface: var(--md-ref-palette-neutral10);
  
  /* ... resto de tokens ... */
}
```

**`_system-dark.css`:**

```css
[data-theme="dark"] {
  /* Primary */
  --md-sys-color-primary: var(--md-ref-palette-primary80);
  --md-sys-color-on-primary: var(--md-ref-palette-primary20);
  --md-sys-color-primary-container: var(--md-ref-palette-primary30);
  --md-sys-color-on-primary-container: var(--md-ref-palette-primary90);

  /* Surface */
  --md-sys-color-surface: var(--md-ref-palette-neutral6);
  --md-sys-color-on-surface: var(--md-ref-palette-neutral90);
  
  /* ... resto de tokens ... */
}
```

### 4.4 Uso en Componentes Vue

```vue
<template>
  <button class="md-button md-button--filled">
    <slot />
  </button>
</template>

<style scoped>
.md-button {
  /* Tipografía */
  font-family: var(--md-sys-typescale-font-family-plain);
  font-size: var(--md-sys-typescale-label-large-size);
  font-weight: var(--md-sys-typescale-label-large-weight);
  line-height: var(--md-sys-typescale-label-large-line-height);
  letter-spacing: var(--md-sys-typescale-label-large-tracking);

  /* Spacing */
  padding: var(--md-sys-spacing-2) var(--md-sys-spacing-6);
  min-height: var(--md-sys-spacing-10);

  /* Shape */
  border-radius: var(--md-sys-shape-corner-full);

  /* Transición */
  transition: background-color 0.2s, box-shadow 0.2s;
}

.md-button--filled {
  background-color: var(--md-sys-color-primary);
  color: var(--md-sys-color-on-primary);
}

.md-button--filled:hover {
  box-shadow: var(--md-sys-elevation-level1);
}

.md-button--filled:focus-visible {
  outline: 3px solid var(--md-sys-color-primary);
  outline-offset: 2px;
}

.md-button--filled:disabled {
  background-color: var(--md-sys-color-on-surface);
  opacity: var(--md-sys-state-disabled-container-opacity);
  color: var(--md-sys-color-on-surface);
}
</style>
```

---

## 5. Resumen de Tokens por Categoría

| Categoría | Prefijo | Cantidad | Ejemplo |
|-----------|---------|----------|---------|
| Reference Palettes | `--md-ref-palette-*` | ~65 | `--md-ref-palette-primary40` |
| System Colors | `--md-sys-color-*` | ~30 | `--md-sys-color-primary` |
| Typography | `--md-sys-typescale-*` | ~75 | `--md-sys-typescale-body-large-size` |
| Shape | `--md-sys-shape-*` | 7 | `--md-sys-shape-corner-medium` |
| Elevation | `--md-sys-elevation-*` | 6 | `--md-sys-elevation-level2` |
| Spacing | `--md-sys-spacing-*` | 12 | `--md-sys-spacing-4` |
| State | `--md-sys-state-*` | 6 | `--md-sys-state-hover-state-layer-opacity` |

**Total aproximado:** ~200 tokens

---

*Fin del documento A-02 - Design Tokens MD3*
