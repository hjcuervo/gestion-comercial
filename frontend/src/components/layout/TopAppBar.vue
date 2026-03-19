<template>
  <header class="top-app-bar">
    <div class="top-app-bar__leading">
      <h1 class="top-app-bar__title">{{ title }}</h1>
    </div>

    <div class="top-app-bar__trailing">
      <!-- Bell -->
      <div class="bell-wrap" ref="bellRef">
        <button class="bell-btn" @click="togglePanel" :class="{ 'bell-btn--active': showPanel }">
          <Icon name="bell" :size="20" />
          <span v-if="pendientes.length" class="bell-badge">{{ pendientes.length > 99 ? '99+' : pendientes.length }}</span>
        </button>

        <!-- Dropdown panel -->
        <div v-if="showPanel" class="bell-panel glass">
          <div class="panel-header">
            <span class="panel-title">Compromisos Pendientes</span>
            <span class="panel-count">{{ pendientes.length }}</span>
          </div>
          <div v-if="loadingPendientes" class="panel-loading">
            <Icon name="loader" :size="16" class="animate-spin" />
          </div>
          <div v-else-if="!pendientes.length" class="panel-empty">
            <Icon name="check-circle" :size="24" color="var(--success)" />
            <span>Sin compromisos pendientes</span>
          </div>
          <div v-else class="panel-list">
            <div
              v-for="comp in pendientes.slice(0, 15)"
              :key="comp.id"
              :class="['panel-item', { 'panel-item--vencido': isVencido(comp) }]"
              @click="goToOportunidad(comp.oportunidadId)"
            >
              <div class="panel-item__left">
                <Icon :name="isVencido(comp) ? 'alert-circle' : 'clock'" :size="14"
                  :color="isVencido(comp) ? 'var(--error)' : 'var(--warning)'" />
              </div>
              <div class="panel-item__body">
                <span class="panel-item__desc">{{ comp.descripcion }}</span>
                <span class="panel-item__oportunidad">{{ comp.oportunidadNombre }}</span>
              </div>
              <div class="panel-item__right">
                <span :class="['panel-item__fecha', { 'text--vencido': isVencido(comp) }]">{{ formatDate(comp.fechaCompromiso) }}</span>
                <span v-if="isVencido(comp)" class="tag-vencido">Vencido</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- User -->
      <div class="top-app-bar__user">
        <span class="top-app-bar__user-name">{{ userName }}</span>
        <div class="top-app-bar__avatar">
          <Icon name="user" :size="20" />
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { useRouter } from 'vue-router';
import Icon from '@/components/ui/Icon.vue';
import { actividadService } from '@/services/actividad.service';

defineProps({
  title: { type: String, default: '' },
  userName: { type: String, default: '' },
});

const router = useRouter();
const pendientes = ref([]);
const loadingPendientes = ref(false);
const showPanel = ref(false);
const bellRef = ref(null);
let pollInterval = null;

onMounted(() => {
  loadPendientes();
  // Poll every 60 seconds
  pollInterval = setInterval(loadPendientes, 60000);
  document.addEventListener('click', handleClickOutside);
});

onBeforeUnmount(() => {
  if (pollInterval) clearInterval(pollInterval);
  document.removeEventListener('click', handleClickOutside);
});

async function loadPendientes() {
  try {
    pendientes.value = await actividadService.listarCompromisosPendientes();
  } catch {}
}

function togglePanel() {
  showPanel.value = !showPanel.value;
  if (showPanel.value) loadPendientes();
}

function handleClickOutside(e) {
  if (bellRef.value && !bellRef.value.contains(e.target)) {
    showPanel.value = false;
  }
}

function goToOportunidad(id) {
  showPanel.value = false;
  router.push(`/oportunidades/${id}`);
}

function isVencido(comp) {
  return new Date(comp.fechaCompromiso) < new Date();
}

function formatDate(date) {
  if (!date) return '—';
  return new Date(date).toLocaleDateString('es-CO', { day: '2-digit', month: 'short' });
}
</script>

<style scoped>
.top-app-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: var(--header-height);
  padding: 0 var(--space-6);
  background: var(--bg-elevated);
  border-bottom: 1px solid var(--glass-border);
  position: sticky;
  top: 0;
  z-index: var(--z-sticky);
}

.top-app-bar__leading { display: flex; align-items: center; }
.top-app-bar__title { font-family: var(--font-display); font-size: var(--text-lg); font-weight: 600; color: var(--text-primary); margin: 0; }

.top-app-bar__trailing { display: flex; align-items: center; gap: var(--space-4); }

/* Bell */
.bell-wrap { position: relative; }
.bell-btn { position: relative; background: none; border: none; color: var(--text-secondary); cursor: pointer; padding: var(--space-2); border-radius: var(--radius-md); transition: all 0.15s; display: flex; }
.bell-btn:hover, .bell-btn--active { color: var(--text-primary); background: var(--glass-hover); }
.bell-badge { position: absolute; top: 2px; right: 0; min-width: 16px; height: 16px; border-radius: 8px; background: var(--error); color: #fff; font-size: 9px; font-weight: 700; display: flex; align-items: center; justify-content: center; padding: 0 3px; line-height: 1; font-family: var(--font-mono); }

/* Panel */
.bell-panel { position: absolute; top: calc(100% + 8px); right: 0; width: 380px; max-height: 480px; border-radius: var(--radius-xl); overflow: hidden; display: flex; flex-direction: column; box-shadow: var(--shadow-lg); z-index: var(--z-dropdown); }
.panel-header { display: flex; justify-content: space-between; align-items: center; padding: var(--space-4); border-bottom: 1px solid var(--glass-border); }
.panel-title { font-family: var(--font-display); font-size: var(--text-sm); font-weight: 600; color: var(--text-primary); }
.panel-count { font-size: var(--text-xs); color: var(--text-muted); font-family: var(--font-mono); background: var(--bg-surface); padding: 1px 8px; border-radius: var(--radius-full); }
.panel-loading { display: flex; align-items: center; justify-content: center; padding: var(--space-6); color: var(--text-muted); }
.panel-empty { display: flex; flex-direction: column; align-items: center; gap: var(--space-2); padding: var(--space-6); color: var(--text-muted); font-size: var(--text-xs); }

.panel-list { overflow-y: auto; max-height: 400px; }
.panel-item { display: flex; gap: var(--space-3); padding: var(--space-3) var(--space-4); cursor: pointer; transition: background 0.15s; border-bottom: 1px solid rgba(255,255,255,0.03); }
.panel-item:hover { background: rgba(0, 212, 255, 0.04); }
.panel-item--vencido { background: rgba(244, 63, 94, 0.03); }
.panel-item__left { flex-shrink: 0; padding-top: 2px; }
.panel-item__body { flex: 1; min-width: 0; }
.panel-item__desc { display: block; font-size: var(--text-xs); font-weight: 500; color: var(--text-primary); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.panel-item__oportunidad { display: block; font-size: 10px; color: var(--text-muted); margin-top: 1px; }
.panel-item__right { flex-shrink: 0; text-align: right; }
.panel-item__fecha { display: block; font-size: 10px; color: var(--text-muted); font-family: var(--font-mono); }
.text--vencido { color: var(--error); font-weight: 600; }
.tag-vencido { display: inline-block; font-size: 8px; background: var(--error-soft); color: var(--error); padding: 0 4px; border-radius: var(--radius-full); font-weight: 700; margin-top: 2px; }

/* User */
.top-app-bar__user { display: flex; align-items: center; gap: var(--space-2); }
.top-app-bar__user-name { font-family: var(--font-body); font-size: var(--text-xs); color: var(--text-secondary); }
.top-app-bar__avatar { width: 32px; height: 32px; border-radius: var(--radius-full); background: var(--primary-soft); display: flex; align-items: center; justify-content: center; color: var(--primary); }
</style>
