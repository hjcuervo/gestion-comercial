---
name: gestcom-frontend
description: Use this skill whenever creating, modifying, or reviewing frontend code for the GestCom platform — Vue 3 + Vite + Pinia. Triggers include writing or editing .vue views and components, Pinia stores, axios services, router routes, AppLayout/NavRail items, glassmorphism cards, gradient texts, animated entrances, or any UI work in the gestion-comercial project. The visual system is "Aurora Dark" (cyan #00d4ff + violet #a855f7 + coral #ff6b6b) implemented through legacy CSS variables. Always load gestcom-context first to align with the broader project conventions.
---

# GestCom — Convenciones Frontend

Esta skill define **cómo se construye el frontend de GestCom**. Aplica todas las reglas aquí descritas al generar vistas, componentes, services, stores y rutas.

> **Prerequisito:** Cargar primero `gestcom-context`.
>
> **Fuente de verdad:** los patrones aquí descritos se validaron contra el código en `main` el 19-may-2026. Si el código real diverge de esta skill, **gana el código real** — actualizar la skill.

---

## 1. Stack y filosofía

- **Vue 3** con **Composition API** (`<script setup>`) — en vistas y componentes.
- **Pinia** con **Options API** (`state`, `getters`, `actions`) — en stores. (Sí, hay disonancia con Vue Composition: es así en el repo).
- **Vite** como bundler. Alias `@` apunta a `frontend/src/`.
- **Axios** para HTTP (interceptor JWT + redirección en 401).
- **Sistema de diseño propio "Aurora Dark"** (tokens legacy nombrados como "Luxury Tech" — ver §3).
- **No se usa Vuex.** No se usa Options API en código nuevo de vistas/componentes.
- **No se usa Tailwind ni framework de UI prefabricado.** El styling combina CSS global (`global.css`) con `<style scoped>` por SFC, todo basado en tokens CSS de `tokens.css`.

---

## 2. Estructura de carpetas

```
frontend/src/
├── App.vue
├── main.js
├── style.css                       Punto de entrada que importa tokens + global
├── assets/styles/
│   ├── tokens.css                  Variables CSS (paleta, fuentes, espaciado, radios, sombras, z-index)
│   ├── global.css                  Clases utilitarias: .glass, .gradient-text, .animate-*, etc.
│   └── style.css
├── router/
│   └── index.js                    Routes + guard global
├── stores/                         Pinia (Options API)
│   ├── auth.store.js
│   ├── empresa.store.js
│   ├── oportunidad.store.js
│   ├── persona.store.js
│   └── pipeline.store.js
├── services/                       Clientes HTTP (axios)
│   ├── api.js                      Instance + interceptors
│   ├── auth.service.js
│   ├── contrato.service.js
│   ├── empresa.service.js
│   ├── facturacion.service.js
│   ├── oportunidad.service.js
│   └── persona.service.js
├── views/                          Vistas conectadas al router
│   ├── DashboardView.vue
│   ├── LoginView.vue
│   ├── EmpresasView.vue
│   ├── PersonasView.vue
│   ├── PipelineView.vue
│   ├── OportunidadDetalleView.vue
│   ├── ContratosListView.vue
│   ├── ContratoDetalleView.vue
│   └── FacturacionView.vue
├── components/
│   ├── layout/                     AppLayout, NavRail, TopAppBar (los activos)
│   │                               AuroraLayout, AuroraHeader, AuroraSidebar (alternativos, no usados en vistas)
│   ├── ui/                         Aurora* + Md* + genéricos (convivencia — deuda DT-05)
│   │                               Icon.vue (componente de iconos centralizado)
│   ├── actividad/                  Modales y componentes específicos
│   ├── contrato/
│   ├── documento/
│   ├── empresa/
│   ├── facturacion/                GestionPanel (único componente Mundo 3 hoy)
│   ├── oportunidad/
│   ├── persona/
│   └── pipeline/
└── utils/
    └── currency.js                 formatCurrency, formatDate, etc.
```

**Reglas:**

1. Un dominio nuevo crea sus vistas en `views/` y, si tiene componentes específicos, una carpeta nueva en `components/{dominio}/`.
2. **No mezclar componentes de dominios diferentes.**
3. Los componentes de UI puramente presentacionales (sin lógica de negocio) viven en `components/ui/`.
4. Layout activo: **`AppLayout`**, no `AuroraLayout` (parte de DT-05).

---

## 3. Sistema de diseño "Aurora Dark"

### 3.1 Nota sobre nomenclatura

El sistema visual se llama **"Aurora Dark"** (decisión congelada). Sin embargo, el archivo `tokens.css` todavía rotula "Luxury Tech Aesthetic" y las variables CSS no tienen prefijo de marca — son globales (`--primary`, `--bg-deep`). Migrar los nombres es deuda DT-05; mientras tanto, **usar los nombres de variables que ya existen**.

### 3.2 Paleta de colores

Tokens reales en `tokens.css`:

| Variable | Valor | Uso |
|----------|-------|-----|
| `--primary` | `#00d4ff` | Electric cyan — acción primaria, brand, énfasis. |
| `--primary-soft` | `rgba(0, 212, 255, 0.15)` | Fondos atenuados (pills, hovers). |
| `--primary-glow` | `rgba(0, 212, 255, 0.4)` | Sombras de glow. |
| `--secondary` | `#a855f7` | Vivid violet — acento, gradientes. |
| `--secondary-soft` | `rgba(168, 85, 247, 0.15)` | Variante atenuada. |
| `--accent` | `#ff6b6b` | Warm coral — destacar acciones secundarias. |
| `--success` | `#10b981` | Mint — VIGENTE, GANADA, FACTURADA. |
| `--warning` | `#f59e0b` | Amber — SUSPENDIDO, SEGUIMIENTO. |
| `--error` | `#f43f5e` | Rose — PERDIDA, TERMINADO, errores. |
| `--bg-deep` | `#08090d` | Fondo principal de la app. |
| `--bg-base` | `#0d0f14` | Fondo de áreas planas. |
| `--bg-elevated` | `#13161d` | Cards y paneles. |
| `--bg-surface` | `#1a1d26` | Modales, dropdowns. |
| `--bg-hover` | `#22262f` | Estados hover. |
| `--glass-bg` | `rgba(255, 255, 255, 0.02)` | Fondo de `.glass`. |
| `--glass-border` | `rgba(255, 255, 255, 0.06)` | Borde de `.glass`. |
| `--text-primary` | `#ffffff` | Texto principal. |
| `--text-secondary` | `rgba(255,255,255,0.7)` | Texto subordinado. |
| `--text-tertiary` | `rgba(255,255,255,0.5)` | Etiquetas, headers de tabla. |
| `--text-muted` | `rgba(255,255,255,0.3)` | Iconos secundarios, empty states. |
| `--border` | `rgba(255,255,255,0.08)` | Borde por defecto. |
| `--border-light` | `rgba(255,255,255,0.12)` | Hover de bordes. |

**Gradientes:**
- `--gradient-primary` — cyan → violet (135°). Para `.gradient-text` y CTAs principales.
- `--gradient-surface` — sutil top-down. Realces de panel.
- `--gradient-glow` — radial glow del primary.
- `--aurora` — tres radials suaves (cyan + violet + coral). Para fondos atmosféricos.

### 3.3 Tipografía

- Familia display y body: **Outfit** (Google Fonts, pesos 300-800). Importada en `tokens.css`.
- Familia mono: **JetBrains Mono** (códigos, IDs).
- Tamaños: `--text-xs` (0.75rem) hasta `--text-5xl` (3.5rem). Texto base = `1rem`.
- Pesos: `--font-light` (300) hasta `--font-extrabold` (800).

### 3.4 Espaciado y radios

- Escala de espaciado: `--space-1` (0.25rem) hasta `--space-16` (4rem). Múltiplos de 4px.
- Radios: `--radius-sm` (8px), `--radius-md` (12px), `--radius-lg` (16px), `--radius-xl` (20px), `--radius-2xl` (28px), `--radius-full` (9999px).
- Sombras: `--shadow-sm`, `--shadow-md`, `--shadow-lg` (oscuras), `--shadow-glow` (cyan), `--shadow-glow-sm`.

### 3.5 Layout fijos

- `--sidebar-width: 72px` (colapsada), `--sidebar-expanded: 260px`.
- `--header-height: 64px`.
- `--content-max-width: 1440px`.

### 3.6 Clases globales en `global.css`

Usar siempre que apliquen, en lugar de re-implementar el efecto:

| Clase | Efecto |
|-------|--------|
| `.glass` | Glassmorphism: `--glass-bg` + `--glass-border` + `backdrop-filter: blur(20px)`. Hover incluido. |
| `.gradient-text` | Texto con `--gradient-primary` (cyan → violet) como fill. |
| `.animate-fadeIn` | Fade-in suave al cargar. |
| `.animate-slideUp` | Slide-up de 20px al cargar. Variante con `.delay-1`, `.delay-2`, etc. |
| `.animate-slideIn` | Slide lateral. |
| `.animate-scaleIn` | Scale-in con spring. |
| `.animate-pulse` | Pulse infinito (2s). Útil para skeletons. |
| `.animate-spin` | Rotación infinita (1s). Para loaders. |

### 3.7 Patrones visuales clave

- **Botón primario:** fondo `--primary`, texto `--bg-deep`, hover con glow `--shadow-glow-sm`.
- **Botón secundario:** `background: transparent`, borde `1px solid --primary`, texto `--primary`.
- **Botón destructivo:** `--error` o variante outline.
- **Cards:** clase `.glass` con `padding: var(--space-6)`, `border-radius: var(--radius-lg)`.
- **Tablas (`.data-table`):** header con `--text-tertiary` en uppercase pequeño, filas con hover `--bg-hover`, celda numérica `text-align: right`.
- **Estado pills (BEM):** `.estado-pill estado-pill--{estado}` — fondo del color soft, texto del color sólido.

---

## 4. Convenciones de vistas

### 4.1 Plantilla canónica de vista de listado

Basada en `ContratosListView.vue` real.

```vue
<template>
  <AppLayout>
    <div class="contratos-list">
      <section class="list-header animate-slideUp">
        <div>
          <h1 class="page-title gradient-text">Contratos</h1>
          <p class="page-subtitle">Gestión de contratos formalizados</p>
        </div>
      </section>

      <section class="list-filters animate-slideUp delay-1">
        <div class="filter-bar glass">
          <div class="search-wrap">
            <Icon name="search" :size="16" class="search-icon" />
            <input v-model="searchQuery" type="text" class="search-input"
                   placeholder="Buscar..." @input="debouncedSearch" />
          </div>
          <select v-model="filtroEstado" class="filter-select" @change="loadContratos(1)">
            <option value="">Todos los estados</option>
            <option value="VIGENTE">Vigentes</option>
            <option value="SUSPENDIDO">Suspendidos</option>
            <option value="TERMINADO">Terminados</option>
            <option value="LIQUIDADO">Liquidados</option>
          </select>
        </div>
      </section>

      <div v-if="loading" class="loading-state">
        <Icon name="loader" :size="32" class="animate-spin" />
        <p>Cargando contratos...</p>
      </div>

      <section v-else class="list-table animate-slideUp delay-2">
        <div v-if="!contratos.length" class="empty-state glass">
          <Icon name="note-add" :size="40" color="var(--text-muted)" />
          <p>No se encontraron contratos</p>
        </div>
        <div v-else class="table-wrap glass">
          <table class="data-table">
            <!-- ... -->
            <tr v-for="c in contratos" :key="c.id" class="data-row" @click="goToDetalle(c.id)">
              <td>{{ c.numeroContratoInterno || c.oportunidadNombre }}</td>
              <td>
                <span :class="['estado-pill', `estado-pill--${c.estado?.toLowerCase()}`]">
                  {{ estadoLabel(c.estado) }}
                </span>
              </td>
              <td class="text-right">{{ fmtCurrency(c.valorContrato, c.moneda) }}</td>
            </tr>
          </table>
        </div>
      </section>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import AppLayout from '@/components/layout/AppLayout.vue';
import Icon from '@/components/ui/Icon.vue';
import { contratoService } from '@/services/contrato.service';
import { formatCurrency as fmtCurrency, formatDate as fmtDate } from '@/utils/currency';

const router = useRouter();
const contratos = ref([]);
const loading = ref(true);
const searchQuery = ref('');
const filtroEstado = ref('');
let searchTimer = null;

onMounted(() => loadContratos(1));

async function loadContratos(page = 1) {
  loading.value = true;
  try {
    const params = { page, page_size: 20 };
    if (filtroEstado.value) params.estado = filtroEstado.value;
    const res = await contratoService.listar(params);
    contratos.value = res.data || [];
    // OJO: paginación viene anidada en res.pagination.{page,totalPages,...}
  } catch (err) {
    console.error('Error cargando contratos:', err);
  } finally {
    loading.value = false;
  }
}

function debouncedSearch() {
  clearTimeout(searchTimer);
  searchTimer = setTimeout(() => loadContratos(1), 400);
}

function goToDetalle(id) { router.push(`/contratos/${id}`); }

function estadoLabel(estado) {
  return {
    VIGENTE: 'Vigente', SUSPENDIDO: 'Suspendido',
    TERMINADO: 'Terminado', LIQUIDADO: 'Liquidado'
  }[estado] || estado;
}
</script>

<style scoped>
/* Tokens CSS — nunca hardcodear colores ni medidas */
.contratos-list { padding: var(--space-6); max-width: var(--content-max-width); }
.page-title { font-size: var(--text-3xl); font-weight: var(--font-bold); }
.estado-pill { padding: 2px 8px; border-radius: var(--radius-full); font-size: var(--text-xs); }
.estado-pill--vigente { background: var(--success-soft); color: var(--success); }
.estado-pill--suspendido { background: var(--warning-soft); color: var(--warning); }
.estado-pill--terminado { background: var(--error-soft); color: var(--error); }
.estado-pill--liquidado { background: var(--glass-bg); color: var(--text-muted); }
</style>
```

### 4.2 Vista de detalle — Patrón

Estructura típica:

1. **Header** con `.gradient-text`, badge de estado, y botones de acción primarios.
2. **KPIs / cards superiores** con clase `.glass`, métricas clave.
3. **Grid de datos generales** (etiquetas + valores) en 2-3 columnas.
4. **Tablas / secciones de relaciones** (compromisos, modificaciones, gestiones) con sus modales asociados.
5. **Botones de cambio de estado** con confirmación inline (modal o `confirm`).

### 4.3 Modales

- Cada dominio tiene su carpeta `components/{dominio}/` con modales específicos (`EmpresaModal.vue`, `FormalizarContratoModal.vue`, etc.).
- Se abren por estado local de la vista (`mostrarModal = ref(false)`), no por router.
- Reciben datos por props, emiten `@guardar` y `@cerrar`.
- Validación de campos antes de emitir `@guardar`.
- Aplicar `.glass` al panel del modal + animación `animate-scaleIn`.

---

## 5. Services (clientes HTTP)

### 5.1 Plantilla canónica

Basada en `contrato.service.js` real.

```javascript
import api from './api';

const BASE = '/contratos';

export const contratoService = {
  async listar(params = {}) {
    const { data } = await api.get(BASE, { params });
    return data;
  },

  async obtenerPorId(id) {
    const { data } = await api.get(`${BASE}/${id}`);
    return data;
  },

  async formalizarContrato(payload) {
    const { data } = await api.post(`${BASE}/formalizar`, payload);
    return data;
  },

  async suspender(id) {
    const { data } = await api.post(`${BASE}/${id}/suspender`);
    return data;
  },

  async terminar(id) {
    const { data } = await api.post(`${BASE}/${id}/terminar`);
    return data;
  },
};

export default contratoService;
```

### 5.2 `api.js` — axios instance común

```javascript
import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api/v1',
  headers: { 'Content-Type': 'application/json' },
});

// Token JWT en cada request
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// 401 → cerrar sesión y redirigir a /login
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
```

**Reglas:**

1. Un service por dominio backend.
2. Constante `BASE` al inicio para centralizar el path raíz del recurso.
3. **`async/await` con destructuring `const { data } = await api.x(...)`** — no `.then(r => r.data)`.
4. Exportar como **named export y default** (`export const xService = {...}` + `export default xService`).
5. **No hacer try/catch dentro del service** — los errores burbujean a la vista, que decide cómo mostrarlos (toast, modal).
6. Params para listados van como objeto: `api.get(BASE, { params: { page, page_size, estado } })`. Spring lee snake_case.
7. **No tocar `api.js`** salvo para agregar interceptors transversales (logging, refresh token, etc.) — los nuevos services lo importan tal cual.

### 5.3 Formato de respuesta paginada

El backend devuelve `PageResponse<T>`:

```json
{
  "data": [...],
  "pagination": {
    "page": 1,
    "pageSize": 20,
    "totalItems": 142,
    "totalPages": 8
  }
}
```

**Importante:** acceder con `res.pagination.page`, `res.pagination.totalPages`, etc. Hay vistas en el repo que leen `res.page` y `res.totalPages` directos — eso es un bug (los controles de paginación nunca aparecen). No replicar el error.

---

## 6. Stores Pinia (Options API)

Los stores del repo usan **Options API**, no Composition. Mantener la convención.

```javascript
// stores/auth.store.js
import { defineStore } from 'pinia';
import { authService } from '@/services/auth.service';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: authService.getUser(),
    token: authService.getToken(),
    loading: false,
    error: null,
  }),

  getters: {
    isAuthenticated: (state) => !!state.token,
    userName: (state) => state.user?.nombreCompleto || state.user?.username || '',
    userRole: (state) => state.user?.rol || '',
  },

  actions: {
    async login(username, password) {
      this.loading = true;
      this.error = null;
      try {
        const data = await authService.login(username, password);
        this.token = data.token;
        this.user = data.user;
        return true;
      } catch (error) {
        this.error = error.response?.data?.message || 'Error al iniciar sesión';
        return false;
      } finally {
        this.loading = false;
      }
    },

    logout() {
      authService.logout();
      this.user = null;
      this.token = null;
    },
  },
});
```

**Reglas:**

1. **Stores solo para estado global persistente entre vistas** (sesión, configuración de usuario, alertas globales). Listas que se piden por API en cada vista NO van al store.
2. `state` siempre es una función (`state: () => ({...})`).
3. Stores activos hoy: `auth`, `empresa`, `oportunidad`, `persona`, `pipeline`.
4. **Nombrar el archivo `*.store.js`** (singular) y la función `use{Nombre}Store`.

---

## 7. Router y NavRail

### 7.1 Router (`router/index.js`)

```javascript
import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '@/stores/auth.store';

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginView.vue'),
    meta: { requiresAuth: false, layout: 'blank' }
  },
  {
    path: '/contratos',
    name: 'Contratos',
    component: () => import('@/views/ContratosListView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/contratos/:id',
    name: 'ContratoDetalle',
    component: () => import('@/views/ContratoDetalleView.vue'),
    meta: { requiresAuth: true }
  },
  // ... resto
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
];

const router = createRouter({ history: createWebHistory(), routes });

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next({ name: 'Login' });
  } else if (to.name === 'Login' && authStore.isAuthenticated) {
    next({ name: 'Dashboard' });
  } else {
    next();
  }
});

export default router;
```

**Reglas:**

1. **Componentes lazy-loaded**: `component: () => import('@/views/...')`.
2. **`meta: { requiresAuth: true }` por defecto.** Solo `Login` lleva `requiresAuth: false` + `layout: 'blank'`.
3. Path params siguen el dominio: `/contratos/:id`. No usar `props: true` salvo necesidad puntual; las vistas leen el id con `useRoute().params.id`.
4. **Ruta wildcard** al final que redirige a `/`.

### 7.2 NavRail (items en `AppLayout`)

Los items del NavRail están **hardcoded en `AppLayout.vue`**, no en el componente `NavRail.vue`. Para agregar una entrada:

```javascript
// frontend/src/components/layout/AppLayout.vue
const navItems = [
  { path: '/',            label: 'Inicio',      icon: 'dashboard' },
  { path: '/pipeline',    label: 'Pipeline',    icon: 'view_kanban' },
  { path: '/contratos',   label: 'Contratos',   icon: 'description' },
  { path: '/facturacion', label: 'Facturación', icon: 'receipt' },
  { path: '/empresas',    label: 'Empresas',    icon: 'business' },
  { path: '/personas',    label: 'Personas',    icon: 'people' },
];
```

**Reglas:**

1. `icon` es el nombre que entiende el componente `<Icon>` (estilo Material: `dashboard`, `view_kanban`, `description`, `receipt`, `business`, `people`, etc.).
2. `label` en español, singular o plural según convenga al ítem.
3. También agregar la entrada en `pageTitle` del mismo `AppLayout.vue` para que el `TopAppBar` muestre el título correcto.

---

## 8. Utilidades y formatters

Funciones de formato en `utils/`:

```javascript
// utils/currency.js (versión observada en el repo)
export const formatCurrency = (valor, moneda = 'COP') =>
  new Intl.NumberFormat('es-CO', { style: 'currency', currency: moneda }).format(valor || 0);

export const formatDate = (iso) =>
  iso ? new Date(iso).toLocaleDateString('es-CO') : '';

export const formatDateTime = (iso) =>
  iso ? new Date(iso).toLocaleString('es-CO') : '';
```

**Reglas:**

1. **Nunca usar `toString()` crudo** ni `Number(x).toFixed(2)` para mostrar dinero — usar `formatCurrency`.
2. **Nunca formatear fechas manualmente** — usar `formatDate` / `formatDateTime`.
3. Si surge una utilidad transversal nueva, vive en `utils/` (no en `components/` ni en el SFC de la vista).

---

## 9. Reglas de UI vinculadas a reglas de negocio

| Regla | Implementación UI |
|-------|-------------------|
| RB-04 — motivo obligatorio si PERDIDA / NO_CONCRETADA | Modal de cierre con `<select>` condicional. |
| RB-14 — pipeline inmutable post-creación | Campo `disabled` en edición de oportunidad. |
| RB-15 — etapa debe ser del mismo pipeline | Validar en drag & drop del Kanban. |
| RB-19 — oportunidad cerrada inmutable | Vista de detalle en modo solo lectura (`disabled` global + ocultar botones de acción). |
| Contrato VIGENTE → SUSPENDIDO solo desde VIGENTE | Botón "Suspender" deshabilitado fuera de VIGENTE. |
| "Formalizar Contrato" desaparece si ya hay contrato | `v-if="!oportunidad.contratoId"`. |
| RN-04 (M3) — solo se cruzan compromisos PENDIENTES | Botón "Cruzar" deshabilitado o ausente si estado != PENDIENTE. |
| RN-09 (M3) — gestiones inmutables | Sin botón editar/borrar en filas de bitácora. |

---

## 10. Checklist al crear / modificar una vista

- [ ] Usa `<script setup>` (Composition API).
- [ ] Envuelta en `<AppLayout>` (no AuroraLayout salvo decisión explícita).
- [ ] Importa el service correspondiente, no hace `axios` directo.
- [ ] Usa variables CSS (`--primary`, `--bg-elevated`, etc.), no hardcodea colores ni medidas.
- [ ] Clases globales (`.glass`, `.gradient-text`, `.animate-slideUp`) cuando apliquen, en vez de re-implementar.
- [ ] Estados de carga (`loading`), vacíos (empty-state) y error manejados visualmente.
- [ ] Estado pills usan BEM: `.estado-pill estado-pill--{estado-en-minúscula}`.
- [ ] Iconos vía `<Icon name="..." :size="..." />`, no `<svg>` inline.
- [ ] Formatters de `utils/` para fechas y monedas.
- [ ] Si es ruta nueva: registrada en `router/index.js` con `meta: { requiresAuth: true }`.
- [ ] Si va al NavRail: agregada en `AppLayout.vue` (array `navItems`) con icono Material y entrada en `pageTitle`.
- [ ] Si lee paginación: accede a `res.pagination.page` / `res.pagination.totalPages`, no a `res.page` directo.
- [ ] Modales en `components/{dominio}/`, emiten `@guardar` y `@cerrar`.

---

## 11. Cómo entregar archivos

1. Cada vista en su `.vue` propio.
2. Cada service en su `.js` propio.
3. Cuando se modifiquen archivos existentes (ej. `router/index.js`, `AppLayout.vue`), entregar el archivo completo modificado, no diffs.
4. Presentar con `present_files`.

---

*Esta skill garantiza que el frontend de GestCom mantenga consistencia visual, arquitectónica y de patrones con el código real del repo.*
