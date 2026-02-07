# A-04: Estructura Técnica Frontend Vue 3
## Plataforma Interna de Arquitecsoft - Arquitectura SPA

**Fecha:** Febrero 2026  
**Versión:** 1.0  
**Clasificación:** Interno - Arquitecsoft

---

## 1. Estructura de Carpetas Recomendada

### 1.1 Vista General

```
src/
├── assets/                    # Recursos estáticos
│   ├── fonts/                 # Tipografías (Roboto, Google Sans)
│   ├── icons/                 # Material Symbols (SVG sprites)
│   └── images/                # Imágenes, logos
│
├── styles/                    # Sistema de estilos MD3
│   ├── tokens/                # Design tokens
│   │   ├── _index.css
│   │   ├── _reference.css     # Paletas tonales
│   │   ├── _system-light.css  # Tokens light mode
│   │   ├── _system-dark.css   # Tokens dark mode
│   │   ├── _typography.css    # Type scale
│   │   ├── _shape.css         # Border radius
│   │   ├── _elevation.css     # Shadows
│   │   ├── _spacing.css       # Spacing scale
│   │   └── _state.css         # State layers
│   │
│   ├── themes/                # Temas por cliente/unidad
│   │   ├── default.css        # Tema Arquitecsoft
│   │   └── [cliente].css      # Temas adicionales
│   │
│   ├── base/                  # Estilos globales
│   │   ├── _reset.css         # CSS reset
│   │   └── _globals.css       # Estilos base HTML
│   │
│   └── main.css               # Entry point estilos
│
├── layouts/                   # App Shell y layouts
│   ├── AppShell.vue           # Layout principal (rail + topbar + content)
│   ├── AuthLayout.vue         # Layout para login/auth
│   └── BlankLayout.vue        # Layout vacío (errores, loading)
│
├── components/                # UI Kit MD3 (genéricos, reutilizables)
│   ├── actions/               # Botones, FAB
│   │   ├── MdButton.vue
│   │   ├── MdIconButton.vue
│   │   ├── MdFab.vue
│   │   └── index.ts
│   │
│   ├── inputs/                # Campos de entrada
│   │   ├── MdTextField.vue
│   │   ├── MdTextArea.vue
│   │   ├── MdSelect.vue
│   │   ├── MdCheckbox.vue
│   │   ├── MdRadio.vue
│   │   ├── MdSwitch.vue
│   │   ├── MdDatePicker.vue
│   │   └── index.ts
│   │
│   ├── containers/            # Superficies y contenedores
│   │   ├── MdCard.vue
│   │   ├── MdDialog.vue
│   │   ├── MdBottomSheet.vue
│   │   ├── MdMenu.vue
│   │   └── index.ts
│   │
│   ├── navigation/            # Navegación
│   │   ├── MdNavigationRail.vue
│   │   ├── MdNavigationDrawer.vue
│   │   ├── MdTopAppBar.vue
│   │   ├── MdTabs.vue
│   │   └── index.ts
│   │
│   ├── feedback/              # Retroalimentación
│   │   ├── MdSnackbar.vue
│   │   ├── MdProgress.vue
│   │   ├── MdSkeleton.vue
│   │   └── index.ts
│   │
│   ├── data-display/          # Visualización de datos
│   │   ├── MdDataTable.vue
│   │   ├── MdList.vue
│   │   ├── MdListItem.vue
│   │   ├── MdChip.vue
│   │   ├── MdBadge.vue
│   │   ├── MdAvatar.vue
│   │   └── index.ts
│   │
│   ├── layout/                # Utilidades de layout
│   │   ├── MdDivider.vue
│   │   ├── MdSpacer.vue
│   │   └── index.ts
│   │
│   └── index.ts               # Export principal de componentes
│
├── composables/               # Lógica reutilizable (hooks)
│   ├── useTheme.ts            # Toggle light/dark
│   ├── useSnackbar.ts         # Mostrar snackbars
│   ├── useDialog.ts           # Control de diálogos
│   ├── useBreakpoint.ts       # Responsive breakpoints
│   ├── useForm.ts             # Validación de formularios
│   ├── usePagination.ts       # Lógica de paginación
│   └── index.ts
│
├── features/                  # Módulos de dominio (por funcionalidad)
│   ├── auth/                  # Autenticación
│   │   ├── components/        # Componentes específicos del módulo
│   │   │   ├── LoginForm.vue
│   │   │   └── UserMenu.vue
│   │   ├── views/             # Vistas/páginas del módulo
│   │   │   ├── LoginView.vue
│   │   │   └── LogoutView.vue
│   │   ├── services/          # API calls del módulo
│   │   │   └── auth.service.ts
│   │   ├── stores/            # Estado del módulo
│   │   │   └── auth.store.ts
│   │   ├── types/             # Tipos TypeScript del módulo
│   │   │   └── auth.types.ts
│   │   ├── routes.ts          # Rutas del módulo
│   │   └── index.ts           # Export del módulo
│   │
│   ├── dashboard/             # Dashboard/Home
│   │   ├── components/
│   │   ├── views/
│   │   ├── services/
│   │   ├── stores/
│   │   ├── types/
│   │   ├── routes.ts
│   │   └── index.ts
│   │
│   ├── empresas/              # Módulo Empresas
│   │   ├── components/
│   │   │   ├── EmpresaCard.vue
│   │   │   ├── EmpresaForm.vue
│   │   │   └── EmpresaFilters.vue
│   │   ├── views/
│   │   │   ├── EmpresasListView.vue
│   │   │   └── EmpresaDetailView.vue
│   │   ├── services/
│   │   │   └── empresas.service.ts
│   │   ├── stores/
│   │   │   └── empresas.store.ts
│   │   ├── types/
│   │   │   └── empresas.types.ts
│   │   ├── routes.ts
│   │   └── index.ts
│   │
│   ├── oportunidades/         # Módulo Oportunidades
│   │   └── [misma estructura]
│   │
│   ├── pipelines/             # Módulo Pipelines
│   │   └── [misma estructura]
│   │
│   └── configuracion/         # Módulo Configuración
│       └── [misma estructura]
│
├── services/                  # Servicios globales
│   ├── api/                   # Cliente HTTP y configuración
│   │   ├── client.ts          # Axios/fetch instance
│   │   ├── interceptors.ts    # Auth, error handling
│   │   └── endpoints.ts       # URLs base
│   │
│   ├── storage.service.ts     # localStorage/sessionStorage
│   └── index.ts
│
├── stores/                    # Estado global (Pinia)
│   ├── app.store.ts           # Estado de app (theme, sidebar)
│   ├── user.store.ts          # Usuario actual
│   └── index.ts               # Configuración Pinia
│
├── router/                    # Configuración de rutas
│   ├── index.ts               # Router principal
│   ├── guards.ts              # Navigation guards
│   └── routes.ts              # Agregación de rutas de features
│
├── types/                     # Tipos TypeScript globales
│   ├── api.types.ts           # Respuestas API genéricas
│   ├── common.types.ts        # Tipos compartidos
│   └── index.ts
│
├── utils/                     # Utilidades puras
│   ├── formatters.ts          # Formateo de fechas, números, moneda
│   ├── validators.ts          # Validadores reutilizables
│   ├── constants.ts           # Constantes globales
│   └── index.ts
│
├── App.vue                    # Componente raíz
├── main.ts                    # Entry point
└── env.d.ts                   # Tipos de environment
```

### 1.2 Detalle por Carpeta

#### `layouts/` - App Shell

Contiene los layouts estructurales de la aplicación.

```
layouts/
├── AppShell.vue          # Layout principal con navegación
│   ├── MdTopAppBar       # Barra superior
│   ├── MdNavigationRail  # Rail lateral (desktop)
│   ├── MdNavigationDrawer # Drawer (mobile)
│   └── <router-view>     # Contenido dinámico
│
├── AuthLayout.vue        # Layout para páginas de auth
│   └── Centrado, sin navegación
│
└── BlankLayout.vue       # Layout mínimo
    └── Solo contenido, para errores 404/500
```

**AppShell.vue - Estructura:**

```vue
<template>
  <div class="app-shell" :data-theme="theme">
    <MdTopAppBar 
      :title="pageTitle"
      @toggle-nav="toggleDrawer"
      @toggle-theme="toggleTheme"
    />
    
    <MdNavigationRail 
      v-if="isDesktop"
      :items="navItems"
      :active="currentRoute"
    />
    
    <MdNavigationDrawer
      v-else
      v-model="drawerOpen"
      :items="navItems"
      :active="currentRoute"
    />
    
    <main class="app-shell__content">
      <router-view />
    </main>
  </div>
</template>
```

#### `components/` - UI Kit MD3

Componentes genéricos sin lógica de negocio. Solo reciben props y emiten eventos.

**Reglas:**
- Prefijo `Md` para todos los componentes
- Sin dependencias de stores o services
- Sin llamadas a API
- Props tipadas con TypeScript
- Emits documentados
- Slots nombrados cuando aplique

**Ejemplo - MdButton.vue:**

```vue
<script setup lang="ts">
interface Props {
  variant?: 'filled' | 'tonal' | 'outlined' | 'text' | 'elevated'
  disabled?: boolean
  loading?: boolean
  icon?: string
  iconPosition?: 'leading' | 'trailing'
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'filled',
  disabled: false,
  loading: false,
  iconPosition: 'leading'
})

const emit = defineEmits<{
  click: [event: MouseEvent]
}>()
</script>

<template>
  <button
    :class="['md-button', `md-button--${variant}`]"
    :disabled="disabled || loading"
    :aria-busy="loading"
    @click="emit('click', $event)"
  >
    <span v-if="loading" class="md-button__spinner">
      <MdProgress type="circular" size="small" />
    </span>
    <span v-else-if="icon && iconPosition === 'leading'" class="md-button__icon">
      <MdIcon :name="icon" />
    </span>
    <span class="md-button__label">
      <slot />
    </span>
    <span v-if="icon && iconPosition === 'trailing'" class="md-button__icon">
      <MdIcon :name="icon" />
    </span>
  </button>
</template>

<style scoped>
/* Solo tokens MD3, nunca valores hardcoded */
.md-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: var(--md-sys-spacing-2);
  min-height: var(--md-sys-spacing-10);
  padding: var(--md-sys-spacing-2) var(--md-sys-spacing-6);
  border-radius: var(--md-sys-shape-corner-full);
  font-family: var(--md-sys-typescale-label-large-font);
  font-size: var(--md-sys-typescale-label-large-size);
  font-weight: var(--md-sys-typescale-label-large-weight);
  /* ... */
}
</style>
```

#### `features/` - Módulos de Dominio

Cada feature es un módulo autocontenido con su propia estructura.

**Reglas:**
- Un feature = una carpeta
- Componentes del feature NO se exportan globalmente
- Services del feature llaman a API específica
- Store del feature maneja solo su estado
- Routes del feature se registran en router principal

**Estructura de un feature:**

```
features/oportunidades/
├── components/                 # Componentes específicos
│   ├── OportunidadCard.vue     # Card de oportunidad
│   ├── OportunidadForm.vue     # Formulario create/edit
│   ├── OportunidadFilters.vue  # Panel de filtros
│   ├── OportunidadTimeline.vue # Timeline de actividades
│   └── MoverEtapaDialog.vue    # Diálogo para mover etapa
│
├── views/                      # Páginas/vistas
│   ├── OportunidadesListView.vue
│   ├── OportunidadDetailView.vue
│   └── OportunidadCreateView.vue
│
├── services/
│   └── oportunidades.service.ts
│
├── stores/
│   └── oportunidades.store.ts
│
├── types/
│   └── oportunidades.types.ts
│
├── routes.ts
└── index.ts
```

#### `services/api/` - Cliente HTTP

Configuración centralizada del cliente HTTP.

```typescript
// services/api/client.ts
import axios from 'axios'
import type { AxiosInstance } from 'axios'
import { useAuthStore } from '@/stores'

const apiClient: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Request interceptor - añadir token
apiClient.interceptors.request.use((config) => {
  const authStore = useAuthStore()
  if (authStore.token) {
    config.headers.Authorization = `Bearer ${authStore.token}`
  }
  return config
})

// Response interceptor - manejo de errores
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      const authStore = useAuthStore()
      authStore.logout()
    }
    return Promise.reject(error)
  }
)

export { apiClient }
```

#### `stores/` - Estado Global (Pinia)

Solo estado verdaderamente global. Estado de features va en su carpeta.

```typescript
// stores/app.store.ts
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAppStore = defineStore('app', () => {
  // State
  const theme = ref<'light' | 'dark'>('light')
  const sidebarCollapsed = ref(false)
  const pageTitle = ref('Dashboard')

  // Getters
  const isDarkMode = computed(() => theme.value === 'dark')

  // Actions
  function toggleTheme() {
    theme.value = theme.value === 'light' ? 'dark' : 'light'
    document.documentElement.setAttribute('data-theme', theme.value)
    localStorage.setItem('theme', theme.value)
  }

  function setPageTitle(title: string) {
    pageTitle.value = title
    document.title = `${title} | Arquitecsoft`
  }

  // Init
  function init() {
    const savedTheme = localStorage.getItem('theme') as 'light' | 'dark' | null
    if (savedTheme) {
      theme.value = savedTheme
      document.documentElement.setAttribute('data-theme', savedTheme)
    }
  }

  return {
    theme,
    sidebarCollapsed,
    pageTitle,
    isDarkMode,
    toggleTheme,
    setPageTitle,
    init
  }
})
```

#### `router/` - Configuración de Rutas

```typescript
// router/index.ts
import { createRouter, createWebHistory } from 'vue-router'
import { routes } from './routes'
import { authGuard } from './guards'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

router.beforeEach(authGuard)

export default router
```

```typescript
// router/routes.ts
import type { RouteRecordRaw } from 'vue-router'
import AppShell from '@/layouts/AppShell.vue'
import AuthLayout from '@/layouts/AuthLayout.vue'

// Importar rutas de features
import { authRoutes } from '@/features/auth/routes'
import { dashboardRoutes } from '@/features/dashboard/routes'
import { empresasRoutes } from '@/features/empresas/routes'
import { oportunidadesRoutes } from '@/features/oportunidades/routes'

export const routes: RouteRecordRaw[] = [
  // Auth (sin shell)
  {
    path: '/auth',
    component: AuthLayout,
    children: authRoutes
  },
  
  // App (con shell)
  {
    path: '/',
    component: AppShell,
    meta: { requiresAuth: true },
    children: [
      ...dashboardRoutes,
      ...empresasRoutes,
      ...oportunidadesRoutes
    ]
  },
  
  // 404
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFoundView.vue')
  }
]
```

---

## 2. Estrategia de Reutilización entre SPAs

### 2.1 Análisis: Monorepo vs Multi-repo

| Aspecto | Monorepo | Multi-repo + Paquete |
|---------|----------|----------------------|
| **Complejidad inicial** | Media | Baja |
| **Versionamiento** | Unificado | Independiente |
| **CI/CD** | Complejo (builds selectivos) | Simple por repo |
| **Reutilización** | Inmediata (imports locales) | Via npm/registry |
| **Equipo pequeño (≤5)** | ✅ Recomendado | Overkill |
| **Múltiples equipos** | Puede complicarse | ✅ Mejor aislamiento |
| **Consistencia forzada** | ✅ Fácil | Requiere disciplina |

### 2.2 Recomendación: Monorepo con Workspaces

Para Arquitecsoft (equipo pequeño, apps internas relacionadas):

```
arquitecsoft-platform/
├── packages/
│   ├── ui/                    # @arquitecsoft/ui - UI Kit MD3
│   │   ├── src/
│   │   │   ├── components/    # Componentes MD3
│   │   │   ├── composables/   # Hooks reutilizables
│   │   │   ├── styles/        # Tokens y estilos
│   │   │   └── index.ts
│   │   ├── package.json
│   │   └── vite.config.ts
│   │
│   ├── shared/                # @arquitecsoft/shared - Utilidades
│   │   ├── src/
│   │   │   ├── types/         # Tipos compartidos
│   │   │   ├── utils/         # Utilidades
│   │   │   └── constants/     # Constantes
│   │   └── package.json
│   │
│   └── eslint-config/         # @arquitecsoft/eslint-config
│       ├── index.js
│       └── package.json
│
├── apps/
│   ├── gestion-comercial/     # App principal
│   │   ├── src/
│   │   │   ├── features/      # Módulos de negocio
│   │   │   ├── layouts/       # Layouts específicos (si difieren)
│   │   │   └── ...
│   │   ├── package.json
│   │   └── vite.config.ts
│   │
│   └── [otra-app]/            # Futura app
│       └── ...
│
├── package.json               # Workspace root
├── pnpm-workspace.yaml        # Configuración pnpm
└── turbo.json                 # Turborepo (opcional)
```

**package.json (root):**

```json
{
  "name": "arquitecsoft-platform",
  "private": true,
  "workspaces": [
    "packages/*",
    "apps/*"
  ],
  "scripts": {
    "dev": "pnpm --filter gestion-comercial dev",
    "build": "pnpm --filter gestion-comercial build",
    "build:ui": "pnpm --filter @arquitecsoft/ui build",
    "lint": "pnpm -r lint"
  }
}
```

**pnpm-workspace.yaml:**

```yaml
packages:
  - 'packages/*'
  - 'apps/*'
```

### 2.3 Paquete Shared UI (`@arquitecsoft/ui`)

**Contenido del paquete:**

```
packages/ui/
├── src/
│   ├── components/
│   │   ├── actions/
│   │   │   ├── MdButton.vue
│   │   │   └── index.ts
│   │   ├── inputs/
│   │   ├── containers/
│   │   ├── navigation/
│   │   ├── feedback/
│   │   ├── data-display/
│   │   └── index.ts
│   │
│   ├── composables/
│   │   ├── useTheme.ts
│   │   ├── useSnackbar.ts
│   │   └── index.ts
│   │
│   ├── styles/
│   │   ├── tokens/
│   │   ├── themes/
│   │   └── main.css
│   │
│   └── index.ts              # Export principal
│
├── package.json
└── vite.config.ts
```

**package.json del UI:**

```json
{
  "name": "@arquitecsoft/ui",
  "version": "1.0.0",
  "type": "module",
  "main": "./dist/index.js",
  "types": "./dist/index.d.ts",
  "exports": {
    ".": {
      "import": "./dist/index.js",
      "types": "./dist/index.d.ts"
    },
    "./styles": "./dist/styles/main.css"
  },
  "files": ["dist"],
  "peerDependencies": {
    "vue": "^3.4.0"
  }
}
```

**Uso en app:**

```typescript
// apps/gestion-comercial/src/main.ts
import { createApp } from 'vue'
import App from './App.vue'

// Importar UI kit
import { MdButton, MdTextField, MdCard } from '@arquitecsoft/ui'
import '@arquitecsoft/ui/styles'

const app = createApp(App)

// Registrar componentes globalmente (opcional)
app.component('MdButton', MdButton)
app.component('MdTextField', MdTextField)
app.component('MdCard', MdCard)

// O importar donde se necesiten
```

### 2.4 Estrategia de Versionamiento

| Escenario | Acción |
|-----------|--------|
| Fix en UI que no rompe | Patch: `1.0.0` → `1.0.1` |
| Nueva feature en UI | Minor: `1.0.0` → `1.1.0` |
| Cambio breaking en UI | Major: `1.0.0` → `2.0.0` |
| Cambio en tokens | Minor o Major según impacto |

**Apps siempre usan versión específica:**

```json
{
  "dependencies": {
    "@arquitecsoft/ui": "workspace:^1.0.0"
  }
}
```

---

## 3. Estándar de Naming y Convenciones

### 3.1 Archivos y Carpetas

| Tipo | Convención | Ejemplo |
|------|------------|---------|
| Carpetas | kebab-case | `data-display/`, `auth/` |
| Componentes Vue | PascalCase | `MdButton.vue`, `EmpresaCard.vue` |
| Composables | camelCase con `use` | `useTheme.ts`, `useSnackbar.ts` |
| Services | camelCase con `.service` | `auth.service.ts` |
| Stores | camelCase con `.store` | `auth.store.ts` |
| Types | camelCase con `.types` | `auth.types.ts` |
| Utils | camelCase | `formatters.ts`, `validators.ts` |
| CSS/Tokens | kebab-case con `_` prefix | `_reference.css`, `_spacing.css` |

### 3.2 Componentes

| Tipo | Prefijo | Ejemplo |
|------|---------|---------|
| UI Kit MD3 (global) | `Md` | `MdButton`, `MdTextField`, `MdCard` |
| Feature-specific | Feature name | `EmpresaCard`, `OportunidadForm` |
| Layout | Descriptivo | `AppShell`, `AuthLayout` |
| View/Page | Sufijo `View` | `EmpresasListView`, `LoginView` |

### 3.3 Variables y Funciones

| Tipo | Convención | Ejemplo |
|------|------------|---------|
| Variables | camelCase | `isLoading`, `currentPage` |
| Constantes | SCREAMING_SNAKE_CASE | `MAX_PAGE_SIZE`, `API_TIMEOUT` |
| Funciones | camelCase, verbo | `fetchEmpresas()`, `handleSubmit()` |
| Eventos emit | kebab-case | `@update:modelValue`, `@item-click` |
| Props | camelCase | `:isDisabled`, `:itemCount` |
| CSS classes | BEM con prefijo | `.md-button__icon--leading` |

### 3.4 TypeScript

| Tipo | Convención | Ejemplo |
|------|------------|---------|
| Interfaces | PascalCase con `I` opcional | `Empresa`, `IEmpresa` |
| Types | PascalCase | `EmpresaResponse`, `ButtonVariant` |
| Enums | PascalCase | `EstadoOportunidad` |
| Enum values | SCREAMING_SNAKE_CASE | `ABIERTA`, `GANADA` |
| Generics | Single uppercase | `T`, `K`, `V` |

### 3.5 API y Endpoints

| Tipo | Convención | Ejemplo |
|------|------------|---------|
| Base URL | `/api/v1` | — |
| Resources | plural, kebab-case | `/empresas`, `/oportunidades` |
| Actions | POST con verbo | `POST /oportunidades/{id}/mover-etapa` |
| Query params | snake_case | `?page_size=10&sort_by=nombre` |

### 3.6 Tokens CSS

| Tipo | Prefijo | Ejemplo |
|------|---------|---------|
| Reference | `--md-ref-` | `--md-ref-palette-primary40` |
| System | `--md-sys-` | `--md-sys-color-primary` |
| Component | `--md-comp-` | `--md-comp-button-height` |

### 3.7 Git

| Tipo | Formato | Ejemplo |
|------|---------|---------|
| Branch feature | `feature/[ticket]-descripcion` | `feature/GC-123-empresa-form` |
| Branch fix | `fix/[ticket]-descripcion` | `fix/GC-456-validation-error` |
| Commits | Conventional Commits | `feat(empresas): add create form` |
| PR title | `[TICKET] Descripción` | `[GC-123] Add empresa create form` |

---

## 4. Asegurar Consistencia MD3 en PRs

### 4.1 Mecanismos de Validación

#### 4.1.1 ESLint Rules (Automatizado)

```javascript
// packages/eslint-config/rules/md3.js
module.exports = {
  rules: {
    // Prohibir colores hardcoded
    'no-restricted-syntax': [
      'error',
      {
        selector: 'Literal[value=/^#[0-9a-fA-F]{3,8}$/]',
        message: 'No usar colores hex directos. Usar tokens MD3: var(--md-sys-color-*)'
      },
      {
        selector: 'Literal[value=/^rgb/]',
        message: 'No usar rgb(). Usar tokens MD3: var(--md-sys-color-*)'
      }
    ]
  }
}
```

#### 4.1.2 Stylelint Rules (Automatizado)

```javascript
// .stylelintrc.js
module.exports = {
  rules: {
    // Prohibir valores de color directos
    'color-no-hex': true,
    'color-named': 'never',
    'function-disallowed-list': ['rgb', 'rgba', 'hsl', 'hsla'],
    
    // Forzar uso de custom properties
    'declaration-property-value-disallowed-list': {
      '/^color/': ['/^(?!var\\(--md-)/'],
      '/^background/': ['/^(?!var\\(--md-|transparent|none)/'],
      '/^border/': ['/^(?!var\\(--md-|none|0)/'],
      'font-size': ['/^(?!var\\(--md-)/'],
      'font-weight': ['/^(?!var\\(--md-)/'],
      'border-radius': ['/^(?!var\\(--md-)/'],
      'box-shadow': ['/^(?!var\\(--md-|none)/'],
      'padding': ['/^(?!var\\(--md-|0)/'],
      'margin': ['/^(?!var\\(--md-|0|auto)/'],
      'gap': ['/^(?!var\\(--md-)/']
    }
  }
}
```

#### 4.1.3 PR Template (Manual)

```markdown
<!-- .github/pull_request_template.md -->

## Checklist MD3

### Componentes
- [ ] Solo se usan componentes `Md*` del UI Kit
- [ ] No se importan componentes de otras librerías (Bootstrap, Ant, etc.)
- [ ] Nuevos componentes siguen anatomía MD3

### Tokens
- [ ] No hay colores hardcoded (#xxx, rgb, hsl)
- [ ] No hay tamaños hardcoded (px sin token)
- [ ] Todos los estilos usan `var(--md-sys-*)` o `var(--md-ref-*)`

### Estados
- [ ] Hover implementado donde aplica
- [ ] Focus visible implementado
- [ ] Disabled state implementado
- [ ] Error state implementado (formularios)

### Accesibilidad
- [ ] Touch targets ≥ 48dp
- [ ] Contraste verificado Light/Dark
- [ ] ARIA attributes correctos
- [ ] Navegación por keyboard funciona

### Screenshots
| Light Mode | Dark Mode |
|------------|-----------|
| [imagen]   | [imagen]  |
```

#### 4.1.4 CI Pipeline Checks

```yaml
# .github/workflows/pr-check.yml
name: PR Checks

on: [pull_request]

jobs:
  lint:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      
      - name: Setup Node
        uses: actions/setup-node@v4
        with:
          node-version: '20'
          
      - name: Install dependencies
        run: pnpm install
        
      - name: ESLint
        run: pnpm lint
        
      - name: Stylelint
        run: pnpm lint:styles
        
      - name: Type check
        run: pnpm type-check

  visual-regression:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      
      - name: Build Storybook
        run: pnpm build-storybook
        
      - name: Visual regression tests
        run: pnpm test:visual
        # Chromatic, Percy, o similar
```

### 4.2 Code Review Checklist

**Para reviewers:**

```markdown
## Review MD3 Compliance

### ❌ Rechazar si:
- [ ] Hay imports de Bootstrap, Vuetify, Ant Design, etc.
- [ ] Hay colores hex/rgb directos en CSS
- [ ] Hay tamaños en px sin usar tokens
- [ ] Componentes custom que duplican funcionalidad MD3
- [ ] Falta focus visible en elementos interactivos
- [ ] Touch targets < 48dp

### ⚠️ Solicitar cambios si:
- [ ] Faltan estados (hover, disabled, error)
- [ ] No funciona Dark mode
- [ ] Navegación keyboard incompleta
- [ ] ARIA attributes faltantes

### ✅ Aprobar si:
- [ ] Solo usa componentes Md* y tokens
- [ ] Todos los estados implementados
- [ ] Light/Dark mode funcionan
- [ ] Accesibilidad básica cubierta
```

### 4.3 Storybook para Documentación Visual

```
packages/ui/
├── .storybook/
│   ├── main.ts
│   └── preview.ts
├── src/
│   └── components/
│       └── actions/
│           ├── MdButton.vue
│           └── MdButton.stories.ts   # ← Storybook
```

**Ejemplo story:**

```typescript
// MdButton.stories.ts
import type { Meta, StoryObj } from '@storybook/vue3'
import MdButton from './MdButton.vue'

const meta: Meta<typeof MdButton> = {
  title: 'Actions/Button',
  component: MdButton,
  tags: ['autodocs'],
  argTypes: {
    variant: {
      control: 'select',
      options: ['filled', 'tonal', 'outlined', 'text', 'elevated']
    }
  }
}

export default meta
type Story = StoryObj<typeof meta>

export const Filled: Story = {
  args: {
    variant: 'filled',
    default: 'Button'
  }
}

export const AllVariants: Story = {
  render: () => ({
    components: { MdButton },
    template: `
      <div style="display: flex; gap: 16px;">
        <MdButton variant="filled">Filled</MdButton>
        <MdButton variant="tonal">Tonal</MdButton>
        <MdButton variant="outlined">Outlined</MdButton>
        <MdButton variant="text">Text</MdButton>
      </div>
    `
  })
}
```

### 4.4 Resumen de Mecanismos

| Mecanismo | Tipo | Qué detecta |
|-----------|------|-------------|
| ESLint MD3 rules | Automático | Colores hardcoded en JS/TS |
| Stylelint rules | Automático | Tokens faltantes en CSS |
| PR Template | Manual | Checklist de compliance |
| CI Pipeline | Automático | Lint + type check |
| Visual regression | Automático | Cambios visuales no intencionados |
| Code review checklist | Manual | Revisión humana de UX/accesibilidad |
| Storybook | Documentación | Referencia visual de componentes |

---

## 5. Resumen de Decisiones

| Decisión | Elección | Razón |
|----------|----------|-------|
| Estructura de carpetas | Por feature/dominio | Escalabilidad, encapsulación |
| Estrategia repos | Monorepo con workspaces | Equipo pequeño, consistencia |
| Package manager | pnpm | Workspaces nativos, performance |
| UI Kit compartido | `@arquitecsoft/ui` | Reutilización entre apps |
| Prefijo componentes UI | `Md` | Claridad, evita colisiones |
| Estado global | Pinia (composition API) | Oficial Vue 3, TypeScript |
| Validación MD3 | ESLint + Stylelint + PR template | Automático + manual |
| Documentación visual | Storybook | Referencia, testing visual |

---

*Fin del documento A-04 - Estructura Técnica Frontend*
