---
name: gestcom-frontend
description: Use this skill whenever creating, modifying, or reviewing frontend code for the GestCom platform — Vue 3 + Vite + Pinia. Triggers include: writing or editing .vue views and components, Pinia stores, axios services, router routes, AppLayout/NavRail items; styling with the "Luxury Tech" design system (cyan #00D4FF + violet #A855F7); building list views, detail views, modals, or forms for the gestion-comercial project. Always load gestcom-context first to align with the broader project conventions.
---

# GestCom — Convenciones Frontend

Esta skill define **cómo se construye el frontend de GestCom**. Aplica todas las reglas aquí descritas al generar vistas, componentes, services, stores y rutas.

> **Prerequisito:** Cargar primero `gestcom-context` para tener el panorama del proyecto.

---

## 1. Stack y filosofía

- Vue 3 con **Composition API** (`<script setup>`).
- Vite como bundler.
- Pinia para estado global.
- Axios para HTTP (con interceptor JWT).
- Sistema de diseño propio "Luxury Tech".
- **No se usa Vuex.** No se usa Options API en código nuevo.
- **No se usa Tailwind ni un framework de UI prefabricado.** El styling es CSS en SFC con variables CSS del design system.

---

## 2. Estructura de carpetas

```
frontend/src/
├── views/                      Vistas conectadas al router
│   ├── LoginView.vue
│   ├── DashboardView.vue
│   ├── EmpresasListView.vue
│   ├── OportunidadesListView.vue
│   ├── OportunidadDetalleView.vue
│   ├── ContratosListView.vue
│   └── ContratoDetalleView.vue
├── components/                 Componentes reutilizables
│   ├── common/                 Botones, inputs, modales genéricos
│   ├── pipeline/               Kanban, tarjetas
│   └── {dominio}/              Componentes específicos de un dominio
├── services/                   Clientes HTTP
│   ├── api.js                  axios instance + interceptor JWT
│   ├── empresa.service.js
│   ├── oportunidad.service.js
│   └── contrato.service.js
├── stores/                     Pinia
│   ├── auth.store.js
│   └── ...
├── router/
│   └── index.js
├── layouts/
│   └── AppLayout.vue           NavRail + área de contenido
├── assets/
│   └── icons/                  SVGs personalizados
└── main.js
```

**Regla:** un dominio nuevo crea sus vistas en `views/` y, si tiene componentes específicos, una carpeta en `components/{dominio}/`.

---

## 3. Sistema de diseño "Luxury Tech"

### 3.1 Colores

| Token | Valor | Uso |
|-------|-------|-----|
| `--lt-cyan` | `#00D4FF` | Acción primaria, énfasis, brand. |
| `--lt-violet` | `#A855F7` | Acento secundario, gradientes. |
| `--lt-bg-deep` | `#0A0A14` | Fondo principal de la app. |
| `--lt-bg-card` | `#13131F` | Fondo de cards y paneles. |
| `--lt-bg-elev` | `#1B1B2A` | Fondo elevado (modales, hovers). |
| `--lt-border` | `#2A2A3D` | Bordes sutiles. |
| `--lt-text-primary` | `#EAEAF2` | Texto principal. |
| `--lt-text-muted` | `#8888A0` | Texto secundario. |
| `--lt-success` | `#22C55E` | Estados positivos (VIGENTE, GANADA). |
| `--lt-warning` | `#F59E0B` | Estados intermedios (SUSPENDIDO, SEGUIMIENTO). |
| `--lt-danger` | `#EF4444` | Estados negativos (PERDIDA, TERMINADO). |
| `--lt-info` | `#3B82F6` | Estados informativos. |

### 3.2 Tipografía

- Familia: `Inter`, fallback `system-ui, -apple-system, sans-serif`.
- Pesos: 400 (normal), 500 (medio), 600 (semibold), 700 (bold).
- Tamaños base: 14px texto normal, 13px tablas, 12px etiquetas pequeñas, 16px títulos de sección, 20-24px headers.

### 3.3 Espaciado y radios

- Espaciado base: múltiplos de 4px (4, 8, 12, 16, 24, 32).
- Radio de botones: 6px.
- Radio de cards: 10px.
- Radio de modales: 12px.

### 3.4 Componentes visuales clave

- **Botón primario:** fondo `--lt-cyan`, texto oscuro, hover ligeramente más claro.
- **Botón secundario:** transparente con borde `--lt-cyan`, texto cyan.
- **Botón destructivo:** fondo `--lt-danger` o transparente con borde rojo.
- **Cards:** fondo `--lt-bg-card`, borde `--lt-border`, padding 16-24px.
- **Tablas:** filas con hover `--lt-bg-elev`, header con texto `--lt-text-muted` en mayúsculas pequeñas.
- **Badges de estado:** redondeado, fondo con opacidad reducida del color del estado, texto en color sólido.

---

## 4. Convenciones de vistas

### 4.1 Vista de listado — Plantilla

```vue
<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import contratoService from '@/services/contrato.service'

const router = useRouter()
const contratos = ref([])
const cargando = ref(false)
const filtroEstado = ref('')

const cargar = async () => {
  cargando.value = true
  try {
    contratos.value = await contratoService.listar({ estado: filtroEstado.value })
  } finally {
    cargando.value = false
  }
}

const irADetalle = (id) => {
  router.push(`/contratos/${id}`)
}

onMounted(cargar)
</script>

<template>
  <div class="vista-listado">
    <header class="vista-header">
      <h1>Contratos</h1>
      <div class="acciones">
        <select v-model="filtroEstado" @change="cargar">
          <option value="">Todos</option>
          <option value="VIGENTE">Vigentes</option>
          <option value="SUSPENDIDO">Suspendidos</option>
          <option value="TERMINADO">Terminados</option>
          <option value="LIQUIDADO">Liquidados</option>
        </select>
      </div>
    </header>

    <table class="tabla-datos">
      <thead>
        <tr>
          <th>Número</th>
          <th>Oportunidad</th>
          <th>Valor</th>
          <th>Estado</th>
          <th>Inicio</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="c in contratos" :key="c.id" @click="irADetalle(c.id)">
          <td>{{ c.numeroInterno }}</td>
          <td>{{ c.oportunidadNombre }}</td>
          <td>{{ formatearMoneda(c.valor) }}</td>
          <td><span :class="['badge', `badge-${c.estado.toLowerCase()}`]">{{ c.estado }}</span></td>
          <td>{{ c.fechaInicio }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
/* Usa variables --lt-* */
</style>
```

### 4.2 Vista de detalle — Patrón

Estructura típica de una vista de detalle:

1. **Header** con título, badge de estado y botones de acción.
2. **KPIs / cards superiores** con métricas clave.
3. **Grid de datos generales** (campo: valor en columnas).
4. **Tablas / listas de relaciones** (formas de pago, modificaciones, etc.) con sus modales.
5. **Botones de cambio de estado** con confirmación.

### 4.3 Modales

- Componente reutilizable en `components/common/Modal.vue`.
- Se abren por estado local (`mostrarModal = ref(false)`), no por router.
- Envían eventos `@guardar` y `@cerrar`.
- Validación de campos antes de emitir `@guardar`.

---

## 5. Services (clientes HTTP)

```javascript
// services/contrato.service.js
import api from './api'

const RESOURCE = '/contratos'

export default {
  listar(params = {}) {
    return api.get(RESOURCE, { params }).then(r => r.data)
  },

  obtener(id) {
    return api.get(`${RESOURCE}/${id}`).then(r => r.data)
  },

  crear(payload) {
    return api.post(RESOURCE, payload).then(r => r.data)
  },

  suspender(id) {
    return api.post(`${RESOURCE}/${id}/suspender`).then(r => r.data)
  },

  reiniciar(id) {
    return api.post(`${RESOURCE}/${id}/reiniciar`).then(r => r.data)
  },

  terminar(id, payload) {
    return api.post(`${RESOURCE}/${id}/terminar`, payload).then(r => r.data)
  },

  liquidar(id, payload) {
    return api.post(`${RESOURCE}/${id}/liquidar`, payload).then(r => r.data)
  }
}
```

**Reglas:**

1. Un service por dominio backend.
2. Importar el axios instance común desde `services/api.js`.
3. Constante `RESOURCE` al inicio para centralizar el base path.
4. Métodos retornan `Promise` directamente con `.then(r => r.data)`.
5. **No hacer manejo de errores en el service**; eso pertenece a la vista (toast / modal de error).

---

## 6. Stores Pinia

Solo para estado global persistente entre vistas (sesión, configuración de usuario, alertas).

```javascript
// stores/auth.store.js
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const usuario = ref(JSON.parse(localStorage.getItem('usuario') || 'null'))

  const autenticado = computed(() => !!token.value)

  const login = (nuevoToken, nuevoUsuario) => {
    token.value = nuevoToken
    usuario.value = nuevoUsuario
    localStorage.setItem('token', nuevoToken)
    localStorage.setItem('usuario', JSON.stringify(nuevoUsuario))
  }

  const logout = () => {
    token.value = ''
    usuario.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('usuario')
  }

  return { token, usuario, autenticado, login, logout }
})
```

**Regla:** no meter en stores datos que se piden por API en cada vista (lista de contratos, etc.). Eso vive en el componente.

---

## 7. Router y NavRail

### 7.1 Agregar una ruta nueva

`router/index.js`:

```javascript
import ContratosListView from '@/views/ContratosListView.vue'
import ContratoDetalleView from '@/views/ContratoDetalleView.vue'

const routes = [
  // ... rutas existentes
  { path: '/contratos',     name: 'ContratosList',    component: ContratosListView,    meta: { requiereAuth: true } },
  { path: '/contratos/:id', name: 'ContratoDetalle',  component: ContratoDetalleView,  meta: { requiereAuth: true }, props: true },
]
```

### 7.2 Agregar al NavRail

`layouts/AppLayout.vue`:

```javascript
const itemsNav = [
  { ruta: '/dashboard',     etiqueta: 'Dashboard',     icono: 'dashboard' },
  { ruta: '/empresas',      etiqueta: 'Empresas',      icono: 'business' },
  { ruta: '/oportunidades', etiqueta: 'Oportunidades', icono: 'trending_up' },
  { ruta: '/contratos',     etiqueta: 'Contratos',     icono: 'note_add' },
]
```

**Reglas:**

1. Toda ruta nueva con `meta: { requiereAuth: true }` salvo el login.
2. Los iconos son SVGs propios en `assets/icons/`. No usar librerías de iconos externos.
3. Etiquetas del NavRail en español, en plural.

---

## 8. Estado y formatters comunes

### 8.1 Badges de estado

Convención de clases CSS:

```css
.badge { padding: 2px 8px; border-radius: 4px; font-size: 12px; font-weight: 600; }
.badge-vigente    { background: rgba(34, 197, 94, 0.15);  color: var(--lt-success); }
.badge-suspendido { background: rgba(245, 158, 11, 0.15); color: var(--lt-warning); }
.badge-terminado  { background: rgba(239, 68, 68, 0.15);  color: var(--lt-danger);  }
.badge-liquidado  { background: rgba(136, 136, 160, 0.15); color: var(--lt-text-muted); }
.badge-ganada     { background: rgba(34, 197, 94, 0.15);  color: var(--lt-success); }
.badge-perdida    { background: rgba(239, 68, 68, 0.15);  color: var(--lt-danger);  }
.badge-abierta    { background: rgba(0, 212, 255, 0.15);  color: var(--lt-cyan);    }
.badge-seguimiento{ background: rgba(168, 85, 247, 0.15); color: var(--lt-violet);  }
.badge-contratada { background: rgba(34, 197, 94, 0.15);  color: var(--lt-success); }
```

### 8.2 Formatters

Helpers comunes en `utils/format.js`:

```javascript
export const formatearMoneda = (valor, moneda = 'COP') =>
  new Intl.NumberFormat('es-CO', { style: 'currency', currency: moneda }).format(valor || 0)

export const formatearFecha = (iso) =>
  iso ? new Date(iso).toLocaleDateString('es-CO') : ''

export const formatearFechaHora = (iso) =>
  iso ? new Date(iso).toLocaleString('es-CO') : ''
```

---

## 9. Reglas de UI vinculadas a reglas de negocio

| Regla | Implementación UI |
|-------|-------------------|
| RB-04 — motivo obligatorio si PERDIDA / NO_CONCRETADA | Modal de cierre con select condicional. |
| RB-14 — pipeline inmutable post-creación | Campo `disabled` en edición de oportunidad. |
| RB-15 — etapa debe ser del mismo pipeline | Validar en drag & drop del Kanban. |
| RB-19 — oportunidad cerrada inmutable | Vista de detalle en modo solo lectura. |
| Contrato VIGENTE → SUSPENDIDO solo desde VIGENTE | Botón "Suspender" deshabilitado fuera de VIGENTE. |
| "Formalizar Contrato" desaparece si ya hay contrato | `v-if="!oportunidad.contratoId"`. |

---

## 10. Checklist al crear / modificar una vista

- [ ] Usa `<script setup>` (Composition API).
- [ ] Importa el service correspondiente, no hace `axios` directo.
- [ ] Usa variables CSS `--lt-*`, no hardcodea colores.
- [ ] Estados de carga (`cargando`) y vacíos manejados visualmente.
- [ ] Errores de API mostrados al usuario (no solo `console.log`).
- [ ] Badges de estado usan las clases convencionales (`badge-{estado-en-minúscula}`).
- [ ] Formatters usados para fechas y monedas (no `toString()` crudo).
- [ ] Si es ruta nueva: registrada en `router/index.js` con `meta: { requiereAuth: true }`.
- [ ] Si va al NavRail: agregada en `AppLayout.vue` con icono y etiqueta en español plural.
- [ ] Modales usan el componente común, emiten `@guardar` y `@cerrar`.

---

## 11. Cómo entregar archivos

1. Cada vista en su `.vue` propio.
2. Cada service en su `.js` propio.
3. Cuando se modifiquen archivos existentes (ej. `router/index.js`, `AppLayout.vue`), entregar el archivo completo modificado, no diffs.
4. Presentar con `present_files`.

---

*Esta skill garantiza que el frontend de GestCom mantenga consistencia visual y arquitectónica entre todas las vistas y entre los 4 mundos.*
