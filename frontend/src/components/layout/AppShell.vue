<template>
  <div class="gc-shell">
    <!-- Cabecera fija: barra superior (módulos) + barra secundaria (pantallas) -->
    <div class="gc-shell__head">
      <header class="gc-shell__topbar">
        <div class="gc-shell__brand">
          <span class="gc-shell__mark"></span>
          <span class="gc-shell__brand-name">GestCom</span>
        </div>

        <!-- Nivel 1: módulos de dominio -->
        <nav class="gc-shell__modules">
          <RouterLink
            v-for="m in modules"
            :key="m.id"
            :to="m.landing"
            class="gc-shell__module"
            :class="{ 'gc-shell__module--active': isActiveModule(m) }"
          >
            {{ m.label }}
          </RouterLink>
        </nav>

        <div class="gc-shell__tools">
          <button class="gc-shell__icon-btn" type="button" @click="toggleDensity" :title="density === 'compact' ? 'Densidad cómoda' : 'Densidad compacta'" :aria-label="density === 'compact' ? 'Densidad cómoda' : 'Densidad compacta'">
            <GcIcon :name="density === 'compact' ? 'layout-rows' : 'layout-list'" :size="16" />
          </button>
          <button class="gc-shell__icon-btn" type="button" @click="toggleTheme" :title="theme === 'dark' ? 'Cambiar a tema claro' : 'Cambiar a tema oscuro'" :aria-label="theme === 'dark' ? 'Tema claro' : 'Tema oscuro'">
            <GcIcon :name="theme === 'dark' ? 'sun' : 'moon'" :size="16" />
          </button>
          <button class="gc-shell__cmdk" type="button" @click="openCommandPalette" title="Búsqueda y acciones">
            <GcIcon name="search" :size="14" />
            <span class="gc-shell__cmdk-keys gc-mono">{{ cmdKeyLabel }}</span>
          </button>

          <div class="gc-shell__user" ref="userMenuRef">
            <button class="gc-shell__user-btn" type="button" @click="userMenuOpen = !userMenuOpen">
              <span class="gc-shell__user-name">{{ userName }}</span>
              <GcIcon name="chevron-down" :size="14" />
            </button>
            <div v-if="userMenuOpen" class="gc-shell__user-menu">
              <span class="gc-shell__user-role gc-mono">{{ userRole || '—' }}</span>
              <button class="gc-shell__user-item" type="button" @click="logout">
                <GcIcon name="logout" :size="16" />
                <span>Cerrar sesión</span>
              </button>
            </div>
          </div>
        </div>
      </header>

      <!-- Nivel 2: pantallas del módulo activo -->
      <nav class="gc-shell__subnav" aria-label="Pantallas del módulo">
        <RouterLink
          v-for="item in subnav"
          :key="item.path"
          :to="item.path"
          class="gc-shell__tab"
          :class="{ 'gc-shell__tab--active': isActiveScreen(item) }"
        >
          <GcIcon :name="item.icon || 'circle'" :size="15" />
          <span>{{ item.label }}</span>
        </RouterLink>
      </nav>
    </div>

    <!-- Cuerpo: master (izq) + superficie (centro) + aside (der) -->
    <div class="gc-shell__body">
      <aside v-show="regions.master" class="gc-shell__master">
        <!-- Zona maestra: las vistas Operativo inyectan aquí vía Teleport -->
        <div id="gc-shell-master"></div>
      </aside>

      <main class="gc-shell__surface" :class="{ 'gc-shell__surface--bounded': !regions.master && !regions.aside }">
        <slot />
      </main>

      <aside v-show="regions.aside" class="gc-shell__aside">
        <!-- Zona contextual: captura/datos relacionados -->
        <div id="gc-shell-aside"></div>
      </aside>
    </div>

    <GcCommandPalette
      :open="cmdkOpen"
      :items="cmdkItems"
      @close="cmdkOpen = false"
      @navigate="onCmdkNavigate"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useAuthStore } from '@/stores/auth.store';
import { useShell } from '@/composables/useShell';
import { useTheme } from '@/composables/useTheme';
import { useDensity } from '@/composables/useDensity';
import { useModule, MODULES } from '@/composables/useModule';
import GcIcon from '@/components/ui/GcIcon.vue';
import GcCommandPalette from '@/components/ui/GcCommandPalette.vue';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();
const { regions, reset } = useShell();
const { theme, toggleTheme } = useTheme();
const { density, toggleDensity } = useDensity();
const { modules, activeModuleId, subnav } = useModule();

// Al ENTRAR al mundo 'app' (AppShell se crea de nuevo) partimos sin zonas.
// Se hace en setup (no en onMounted) para correr ANTES de que las vistas hijas
// fijen sus zonas en sus hooks mounted (que se ejecutan de hijo a padre).
reset();

const userName = computed(() => authStore.userName || 'Usuario');
const userRole = computed(() => authStore.userRole);

/* --- Navegación de 2 niveles --- */
// Nivel 1: módulo activo según route.meta.module.
function isActiveModule(m) {
  return activeModuleId.value === m.id;
}
// Nivel 2: pantalla activa dentro del módulo.
function isActiveScreen(item) {
  if (item.exact) return route.path === item.path;
  return route.path === item.path || route.path.startsWith(item.path + '/');
}

/* --- Atajos / paleta de comandos --- */
const isMac = typeof navigator !== 'undefined' && /Mac|iPhone|iPad/.test(navigator.platform);
const cmdKeyLabel = computed(() => (isMac ? '⌘K' : 'Ctrl K'));
const cmdkOpen = ref(false);
// Comandos agrupados por módulo (lectura "Módulo · Pantalla"), aplanados para el palette.
const cmdkItems = MODULES.flatMap((m) =>
  m.screens.map((s) => ({
    path: s.path,
    label: `${m.label} · ${s.label}`,
    icon: s.icon,
  }))
);
function openCommandPalette() {
  cmdkOpen.value = !cmdkOpen.value;
}
function onCmdkNavigate(path) {
  if (path && path !== route.path) router.push(path);
}
function onKeydown(e) {
  if ((e.metaKey || e.ctrlKey) && e.key.toLowerCase() === 'k') {
    e.preventDefault();
    openCommandPalette();
  }
}

/* --- Menú de usuario --- */
const userMenuOpen = ref(false);
const userMenuRef = ref(null);
function onClickOutside(e) {
  if (userMenuRef.value && !userMenuRef.value.contains(e.target)) userMenuOpen.value = false;
}
function logout() {
  authStore.logout();
  router.push('/login');
}

onMounted(() => {
  window.addEventListener('keydown', onKeydown);
  document.addEventListener('click', onClickOutside);
});
onUnmounted(() => {
  window.removeEventListener('keydown', onKeydown);
  document.removeEventListener('click', onClickOutside);
});
</script>

<style scoped>
.gc-shell {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: var(--gc-bg);
}

/* Cabecera (topbar + subnav) pinada arriba */
.gc-shell__head {
  position: sticky;
  top: 0;
  z-index: var(--gc-z-topbar);
}

/* Topbar — Nivel 1 */
.gc-shell__topbar {
  display: flex;
  align-items: center;
  gap: var(--gc-space-6);
  height: var(--gc-topbar-h);
  padding: 0 var(--gc-space-5);
  background: var(--gc-surface);
  border-bottom: 1px solid var(--gc-border);
}
.gc-shell__brand { display: flex; align-items: center; gap: var(--gc-space-2); }
.gc-shell__mark {
  width: 16px;
  height: 16px;
  border-radius: var(--gc-radius-sm);
  background: var(--gc-primary);
}
.gc-shell__brand-name {
  font-weight: var(--gc-fw-medium);
  font-size: var(--gc-fs-md);
  letter-spacing: -0.01em;
}

.gc-shell__modules { display: flex; align-items: center; gap: var(--gc-space-2); flex: 1; }
.gc-shell__module {
  padding: var(--gc-space-2) var(--gc-space-3);
  border-radius: var(--gc-radius-sm);
  font-size: var(--gc-fs-md);
  font-weight: var(--gc-fw-medium);
  color: var(--gc-text-3);
  transition: background var(--gc-t-fast), color var(--gc-t-fast);
}
.gc-shell__module:hover { background: var(--gc-surface-2); color: var(--gc-text); }
.gc-shell__module--active { color: var(--gc-text); }

.gc-shell__tools { display: flex; align-items: center; gap: var(--gc-space-3); }
.gc-shell__cmdk {
  display: flex;
  align-items: center;
  gap: var(--gc-space-2);
  height: 30px;
  padding: 0 var(--gc-space-3);
  background: var(--gc-surface-2);
  border: 1px solid var(--gc-border);
  border-radius: var(--gc-radius-md);
  color: var(--gc-text-3);
}
.gc-shell__cmdk:hover { border-color: var(--gc-border-strong); color: var(--gc-text-2); }
.gc-shell__cmdk-keys { font-size: var(--gc-fs-xs); }
.gc-shell__icon-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 30px;
  height: 30px;
  background: var(--gc-surface-2);
  border: 1px solid var(--gc-border);
  border-radius: var(--gc-radius-md);
  color: var(--gc-text-3);
  cursor: pointer;
  transition: border-color var(--gc-t-fast), color var(--gc-t-fast);
}
.gc-shell__icon-btn:hover { border-color: var(--gc-border-strong); color: var(--gc-text); }

.gc-shell__user { position: relative; }
.gc-shell__user-btn {
  display: flex;
  align-items: center;
  gap: var(--gc-space-2);
  height: 30px;
  padding: 0 var(--gc-space-2);
  background: transparent;
  border: none;
  color: var(--gc-text-2);
}
.gc-shell__user-btn:hover { color: var(--gc-text); }
.gc-shell__user-name { font-size: var(--gc-fs-md); }
.gc-shell__user-menu {
  position: absolute;
  top: calc(100% + 6px);
  right: 0;
  min-width: 180px;
  display: flex;
  flex-direction: column;
  background: var(--gc-surface);
  border: 1px solid var(--gc-border-strong);
  border-radius: var(--gc-radius-md);
  box-shadow: var(--gc-shadow-pop);
  padding: var(--gc-space-2);
  gap: var(--gc-space-1);
}
.gc-shell__user-role {
  font-size: var(--gc-fs-xs);
  color: var(--gc-text-3);
  text-transform: uppercase;
  padding: var(--gc-space-1) var(--gc-space-2);
}
.gc-shell__user-item {
  display: flex;
  align-items: center;
  gap: var(--gc-space-2);
  padding: var(--gc-space-2);
  background: transparent;
  border: none;
  border-radius: var(--gc-radius-sm);
  color: var(--gc-text);
  font-size: var(--gc-fs-md);
  text-align: left;
}
.gc-shell__user-item:hover { background: var(--gc-surface-2); }

/* Subnav — Nivel 2 (pantallas del módulo activo) */
.gc-shell__subnav {
  display: flex;
  align-items: center;
  gap: var(--gc-space-1);
  height: 38px;
  padding: 0 var(--gc-space-5);
  background: var(--gc-surface);
  border-bottom: 1px solid var(--gc-border);
  overflow-x: auto;
}
.gc-shell__tab {
  display: inline-flex;
  align-items: center;
  gap: var(--gc-space-2);
  height: 100%;
  padding: 0 var(--gc-space-2);
  font-size: var(--gc-fs-md);
  color: var(--gc-text-2);
  border-bottom: 2px solid transparent;
  white-space: nowrap;
  transition: color var(--gc-t-fast), border-color var(--gc-t-fast);
}
.gc-shell__tab:hover { color: var(--gc-text); }
.gc-shell__tab--active {
  color: var(--gc-text);
  font-weight: var(--gc-fw-medium);
  border-bottom-color: var(--gc-primary);
}

/* Cuerpo */
.gc-shell__body { flex: 1; display: flex; min-height: 0; }
.gc-shell__master {
  width: var(--gc-master-w);
  flex-shrink: 0;
  border-right: 1px solid var(--gc-border);
  background: var(--gc-surface);
  overflow-y: auto;
}
.gc-shell__surface { flex: 1; min-width: 0; overflow-y: auto; }
.gc-shell__surface--bounded > :deep(*) {
  max-width: var(--gc-content-max);
  margin-inline: auto;
}
.gc-shell__aside {
  width: var(--gc-aside-w);
  flex-shrink: 0;
  border-left: 1px solid var(--gc-border);
  background: var(--gc-surface);
  overflow-y: auto;
}
</style>
