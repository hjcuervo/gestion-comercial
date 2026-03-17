<template>
  <AppLayout>
    <div class="pipeline-view">
      <!-- ========== HEADER ========== -->
      <section class="pipeline-view__header animate-slideUp">
        <div class="header-left">
          <h1 class="page-title gradient-text">Pipeline de Ventas</h1>
          <p class="page-subtitle">Configura tus procesos comerciales y sus etapas</p>
        </div>
        <Button variant="primary" icon="plus" @click="openCreatePipeline">
          Nuevo Pipeline
        </Button>
      </section>

      <!-- ========== FILTERS ========== -->
      <section class="pipeline-view__filters animate-slideUp delay-1">
        <div class="search-wrapper">
          <Icon name="search" :size="18" color="var(--text-muted)" />
          <input
            v-model="searchQuery"
            type="text"
            class="search-input"
            placeholder="Buscar pipelines..."
            @input="onSearch"
          />
          <button v-if="searchQuery" class="search-clear" @click="clearSearch">
            <Icon name="x" :size="16" />
          </button>
        </div>
        <div class="filter-chips">
          <button class="chip" :class="{ active: filtroEstado === '' }" @click="setEstadoFilter('')">Todos</button>
          <button class="chip" :class="{ active: filtroEstado === 'ACTIVO' }" @click="setEstadoFilter('ACTIVO')">
            <span class="chip-dot chip-dot--activo"></span> Activos
          </button>
          <button class="chip" :class="{ active: filtroEstado === 'INACTIVO' }" @click="setEstadoFilter('INACTIVO')">
            <span class="chip-dot chip-dot--inactivo"></span> Inactivos
          </button>
        </div>
      </section>

      <!-- ========== CONTENT ========== -->
      <section class="pipeline-view__content animate-slideUp delay-2">
        <!-- Loading -->
        <div v-if="store.loading && !store.pipelines.length" class="loading-state">
          <Icon name="loader" :size="32" class="animate-spin" />
          <p>Cargando pipelines...</p>
        </div>

        <!-- Empty -->
        <div v-else-if="!store.pipelines.length" class="empty-state glass">
          <div class="empty-icon">
            <Icon name="pipeline" :size="48" color="var(--primary)" />
          </div>
          <h3>No hay pipelines</h3>
          <p>Crea tu primer pipeline para comenzar a gestionar tu proceso de ventas.</p>
          <Button variant="primary" icon="plus" @click="openCreatePipeline">Crear Pipeline</Button>
        </div>

        <!-- Master-Detail -->
        <div v-else class="master-detail">
          <!-- LEFT: Pipeline List -->
          <div class="master-panel glass">
            <div class="master-panel__header">
              <span class="master-panel__count">
                {{ store.pipelines.length }} pipeline{{ store.pipelines.length !== 1 ? 's' : '' }}
              </span>
            </div>
            <div class="pipeline-list">
              <div
                v-for="pipeline in store.pipelines"
                :key="pipeline.id"
                class="pipeline-card"
                :class="{
                  'pipeline-card--selected': selectedPipelineId === pipeline.id,
                  'pipeline-card--inactive': pipeline.estado === 'INACTIVO',
                }"
                @click="selectPipeline(pipeline.id)"
              >
                <div
                  class="pipeline-card__indicator"
                  :style="{ background: pipeline.estado === 'ACTIVO' ? 'var(--success)' : 'var(--text-muted)' }"
                ></div>
                <div class="pipeline-card__body">
                  <div class="pipeline-card__name">{{ pipeline.nombre }}</div>
                  <div class="pipeline-card__meta">
                    <span class="badge" :class="pipeline.estado === 'ACTIVO' ? 'badge--success' : 'badge--muted'">
                      {{ pipeline.estado }}
                    </span>
                    <span v-if="pipeline.esDefault" class="badge badge--primary">Default</span>
                    <span class="pipeline-card__etapas">
                      {{ pipeline.etapas?.length || 0 }} etapa{{ (pipeline.etapas?.length || 0) !== 1 ? 's' : '' }}
                    </span>
                  </div>
                </div>
                <Icon name="chevron-right" :size="16" color="var(--text-muted)" />
              </div>
            </div>
            <!-- Pagination -->
            <div v-if="store.pagination.totalPages > 1" class="pagination">
              <button class="pagination-btn" :disabled="store.pagination.page <= 1" @click="store.setPagina(store.pagination.page - 1)">
                <Icon name="chevron-left" :size="16" />
              </button>
              <span class="pagination-info">{{ store.pagination.page }} / {{ store.pagination.totalPages }}</span>
              <button class="pagination-btn" :disabled="store.pagination.page >= store.pagination.totalPages" @click="store.setPagina(store.pagination.page + 1)">
                <Icon name="chevron-right" :size="16" />
              </button>
            </div>
          </div>

          <!-- RIGHT: Detail -->
          <div class="detail-panel">
            <div v-if="!selectedPipelineId" class="detail-empty glass">
              <Icon name="pipeline" :size="40" color="var(--text-muted)" />
              <p>Selecciona un pipeline para ver sus etapas</p>
            </div>

            <div v-else-if="selectedPipeline" class="detail-content">
              <!-- Pipeline Header -->
              <Card>
                <template #default>
                  <div class="detail-header">
                    <div class="detail-header__info">
                      <h2 class="detail-header__name">{{ selectedPipeline.nombre }}</h2>
                      <div class="detail-header__meta">
                        <span class="badge" :class="selectedPipeline.estado === 'ACTIVO' ? 'badge--success' : 'badge--muted'">
                          {{ selectedPipeline.estado }}
                        </span>
                        <span v-if="selectedPipeline.esDefault" class="badge badge--primary">Default</span>
                        <span class="detail-header__version">v{{ selectedPipeline.version }}</span>
                        <span class="detail-header__ambito">{{ selectedPipeline.ambito }}</span>
                      </div>
                    </div>
                    <Button variant="ghost" icon="settings" size="sm" @click="openEditPipeline">Editar</Button>
                  </div>
                </template>
              </Card>

              <!-- Etapas -->
              <div class="etapas-section">
                <div class="etapas-header">
                  <h3 class="etapas-title">
                    <Icon name="chart" :size="18" color="var(--secondary)" />
                    Etapas del Pipeline
                  </h3>
                  <Button variant="secondary" icon="plus" size="sm" @click="openCreateEtapa">Nueva Etapa</Button>
                </div>

                <div v-if="store.loadingEtapas" class="loading-state loading-state--small">
                  <Icon name="loader" :size="20" class="animate-spin" />
                  <span>Cargando etapas...</span>
                </div>

                <div v-else-if="!etapasOrdenadas.length" class="etapas-empty glass">
                  <p>Este pipeline aún no tiene etapas configuradas.</p>
                  <Button variant="secondary" icon="plus" size="sm" @click="openCreateEtapa">Agregar primera etapa</Button>
                </div>

                <div v-else class="etapas-flow">
                  <div v-for="(etapa, index) in etapasOrdenadas" :key="etapa.id" class="etapa-item" :class="{ 'etapa-item--inactive': etapa.estado === 'INACTIVA' }">
                    <div v-if="index > 0" class="etapa-connector">
                      <svg width="24" height="20" viewBox="0 0 24 20">
                        <path d="M12 0 L12 20" stroke="var(--glass-border)" stroke-width="2" stroke-dasharray="4 3" />
                        <path d="M6 14 L12 20 L18 14" stroke="var(--glass-border)" stroke-width="2" fill="none" />
                      </svg>
                    </div>
                    <div class="etapa-card glass" @click="openEditEtapa(etapa)">
                      <div class="etapa-card__color" :style="{ background: etapa.color || 'var(--primary)' }"></div>
                      <div class="etapa-card__body">
                        <div class="etapa-card__top">
                          <span class="etapa-card__orden">#{{ etapa.orden }}</span>
                          <span class="etapa-card__name">{{ etapa.nombre }}</span>
                        </div>
                        <div class="etapa-card__bottom">
                          <span v-if="etapa.probabilidadSugerida != null" class="etapa-card__prob">
                            <Icon name="trending-up" :size="12" /> {{ etapa.probabilidadSugerida }}%
                          </span>
                          <span v-if="etapa.modoBloqueo === 1" class="etapa-card__lock">
                            <Icon name="lock" :size="12" /> Bloqueado
                          </span>
                          <span class="badge badge--sm" :class="etapa.estado === 'ACTIVA' ? 'badge--success' : 'badge--muted'">
                            {{ etapa.estado }}
                          </span>
                        </div>
                      </div>
                      <Icon name="settings" :size="14" color="var(--text-muted)" class="etapa-card__edit" />
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- Error Toast -->
      <Transition name="toast">
        <div v-if="store.error" class="error-toast" @click="store.limpiarError()">
          <Icon name="alert-circle" :size="16" />
          {{ store.error }}
          <Icon name="x" :size="14" class="error-toast__close" />
        </div>
      </Transition>

      <!-- Modals -->
      <PipelineModal
        :visible="showPipelineModal"
        :pipeline="editingPipeline"
        :saving="store.saving"
        :error="modalError"
        @close="closePipelineModal"
        @submit="handlePipelineSubmit"
      />
      <EtapaModal
        :visible="showEtapaModal"
        :etapa="editingEtapa"
        :next-orden="nextEtapaOrden"
        :saving="store.saving"
        :error="modalError"
        @close="closeEtapaModal"
        @submit="handleEtapaSubmit"
      />
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import AppLayout from '@/components/layout/AppLayout.vue';
import Card from '@/components/ui/Card.vue';
import Button from '@/components/ui/Button.vue';
import Icon from '@/components/ui/Icon.vue';
import PipelineModal from '@/components/pipeline/PipelineModal.vue';
import EtapaModal from '@/components/pipeline/EtapaModal.vue';
import { usePipelineStore } from '@/stores/pipeline.store';

const store = usePipelineStore();

const searchQuery = ref('');
const filtroEstado = ref('');
const selectedPipelineId = ref(null);
const showPipelineModal = ref(false);
const editingPipeline = ref(null);
const showEtapaModal = ref(false);
const editingEtapa = ref(null);
const modalError = ref(null);
let searchTimeout = null;

const selectedPipeline = computed(() => store.pipelineSeleccionado);
const etapasOrdenadas = computed(() => [...(store.etapas || [])].sort((a, b) => a.orden - b.orden));
const nextEtapaOrden = computed(() => {
  if (!store.etapas.length) return 1;
  return Math.max(...store.etapas.map((e) => e.orden)) + 1;
});

onMounted(() => { store.cargarPipelines(); });

// Search & Filters
function onSearch() {
  clearTimeout(searchTimeout);
  searchTimeout = setTimeout(() => { store.setFiltros({ q: searchQuery.value }); }, 400);
}
function clearSearch() { searchQuery.value = ''; store.setFiltros({ q: '' }); }
function setEstadoFilter(estado) { filtroEstado.value = estado; store.setFiltros({ estado }); }

// Pipeline selection
function selectPipeline(id) { selectedPipelineId.value = id; store.seleccionarPipeline(id); }

// Pipeline modal
function openCreatePipeline() { editingPipeline.value = null; modalError.value = null; showPipelineModal.value = true; }
function openEditPipeline() { editingPipeline.value = { ...selectedPipeline.value }; modalError.value = null; showPipelineModal.value = true; }
function closePipelineModal() { showPipelineModal.value = false; editingPipeline.value = null; modalError.value = null; }

async function handlePipelineSubmit(payload) {
  modalError.value = null;
  try {
    if (editingPipeline.value) {
      await store.actualizarPipeline(editingPipeline.value.id, payload);
      if (selectedPipelineId.value === editingPipeline.value.id) store.seleccionarPipeline(selectedPipelineId.value);
    } else {
      const nuevo = await store.crearPipeline(payload);
      selectPipeline(nuevo.id);
    }
    closePipelineModal();
  } catch (err) {
    modalError.value = err.response?.data?.message || 'Error al guardar pipeline';
  }
}

// Etapa modal
function openCreateEtapa() { editingEtapa.value = null; modalError.value = null; showEtapaModal.value = true; }
function openEditEtapa(etapa) { editingEtapa.value = { ...etapa }; modalError.value = null; showEtapaModal.value = true; }
function closeEtapaModal() { showEtapaModal.value = false; editingEtapa.value = null; modalError.value = null; }

async function handleEtapaSubmit(payload) {
  modalError.value = null;
  try {
    if (editingEtapa.value) {
      await store.actualizarEtapa(selectedPipelineId.value, editingEtapa.value.id, payload);
    } else {
      await store.crearEtapa(selectedPipelineId.value, payload);
    }
    closeEtapaModal();
  } catch (err) {
    modalError.value = err.response?.data?.message || 'Error al guardar etapa';
  }
}
</script>

<style scoped>
.pipeline-view { display: flex; flex-direction: column; gap: var(--space-6); position: relative; }

/* Header */
.pipeline-view__header { display: flex; justify-content: space-between; align-items: flex-start; gap: var(--space-4); }
.page-title { font-family: var(--font-display); font-size: var(--text-3xl); font-weight: 700; margin: 0; line-height: 1.2; }
.page-subtitle { color: var(--text-tertiary); font-size: var(--text-sm); margin: var(--space-1) 0 0; }

/* Filters */
.pipeline-view__filters { display: flex; gap: var(--space-4); align-items: center; flex-wrap: wrap; }

.search-wrapper { position: relative; display: flex; align-items: center; flex: 1; min-width: 240px; max-width: 400px; }
.search-wrapper > :first-child { position: absolute; left: var(--space-4); pointer-events: none; }
.search-input {
  width: 100%; background: var(--glass-bg); border: 1px solid var(--glass-border);
  border-radius: var(--radius-lg); color: var(--text-primary); font-family: var(--font-body);
  font-size: var(--text-sm); padding: var(--space-3) var(--space-4) var(--space-3) calc(var(--space-4) + 24px);
  transition: all 0.2s; box-sizing: border-box;
}
.search-input:focus { outline: none; border-color: var(--primary); box-shadow: 0 0 0 3px var(--primary-soft); }
.search-input::placeholder { color: var(--text-muted); }
.search-clear { position: absolute; right: var(--space-3); background: none; border: none; color: var(--text-muted); cursor: pointer; display: flex; padding: var(--space-1); }

.filter-chips { display: flex; gap: var(--space-2); }
.chip {
  display: flex; align-items: center; gap: var(--space-2); padding: var(--space-2) var(--space-4);
  background: var(--glass-bg); border: 1px solid var(--glass-border); border-radius: var(--radius-full);
  color: var(--text-secondary); font-family: var(--font-body); font-size: var(--text-xs);
  font-weight: 500; cursor: pointer; transition: all 0.2s; white-space: nowrap;
}
.chip:hover { background: rgba(255, 255, 255, 0.06); }
.chip.active { border-color: var(--primary); color: var(--primary); background: var(--primary-soft); }
.chip-dot { width: 6px; height: 6px; border-radius: 50%; }
.chip-dot--activo { background: var(--success); }
.chip-dot--inactivo { background: var(--text-muted); }

/* Loading & Empty */
.loading-state { display: flex; flex-direction: column; align-items: center; justify-content: center; gap: var(--space-3); padding: var(--space-8); color: var(--text-tertiary); font-size: var(--text-sm); }
.loading-state--small { flex-direction: row; padding: var(--space-6); }

.empty-state {
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  gap: var(--space-4); padding: calc(var(--space-8) * 2); border-radius: var(--radius-xl); text-align: center;
}
.empty-icon { width: 80px; height: 80px; display: flex; align-items: center; justify-content: center; background: var(--primary-soft); border-radius: var(--radius-xl); }
.empty-state h3 { color: var(--text-primary); font-family: var(--font-display); margin: 0; }
.empty-state p { color: var(--text-tertiary); font-size: var(--text-sm); max-width: 320px; margin: 0; }

/* Master-Detail */
.master-detail { display: grid; grid-template-columns: 360px 1fr; gap: var(--space-6); min-height: 500px; }

.master-panel { border-radius: var(--radius-xl); overflow: hidden; display: flex; flex-direction: column; height: fit-content; max-height: calc(100vh - 280px); }
.master-panel__header { padding: var(--space-4) var(--space-5); border-bottom: 1px solid var(--glass-border); }
.master-panel__count { font-size: var(--text-xs); color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.05em; font-weight: 600; }

.pipeline-list { overflow-y: auto; flex: 1; }
.pipeline-card {
  display: flex; align-items: center; gap: var(--space-3); padding: var(--space-4) var(--space-5);
  cursor: pointer; transition: all 0.15s; border-bottom: 1px solid rgba(255, 255, 255, 0.03);
}
.pipeline-card:hover { background: rgba(255, 255, 255, 0.03); }
.pipeline-card--selected { background: var(--primary-soft); border-left: 3px solid var(--primary); }
.pipeline-card--inactive { opacity: 0.6; }
.pipeline-card__indicator { width: 8px; height: 8px; min-width: 8px; border-radius: 50%; }
.pipeline-card__body { flex: 1; min-width: 0; }
.pipeline-card__name { font-family: var(--font-body); font-size: var(--text-sm); font-weight: 600; color: var(--text-primary); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.pipeline-card__meta { display: flex; align-items: center; gap: var(--space-2); margin-top: var(--space-1); }
.pipeline-card__etapas { font-size: var(--text-xs); color: var(--text-muted); }

/* Badges */
.badge { display: inline-flex; align-items: center; padding: 1px var(--space-2); border-radius: var(--radius-full); font-size: 10px; font-weight: 600; text-transform: uppercase; letter-spacing: 0.04em; }
.badge--sm { font-size: 9px; }
.badge--success { background: rgba(16, 185, 129, 0.15); color: var(--success); }
.badge--muted { background: rgba(255, 255, 255, 0.06); color: var(--text-muted); }
.badge--primary { background: var(--primary-soft); color: var(--primary); }

/* Pagination */
.pagination { display: flex; align-items: center; justify-content: center; gap: var(--space-3); padding: var(--space-3); border-top: 1px solid var(--glass-border); }
.pagination-btn { background: var(--glass-bg); border: 1px solid var(--glass-border); border-radius: var(--radius-sm); color: var(--text-secondary); padding: var(--space-2); cursor: pointer; display: flex; transition: all 0.15s; }
.pagination-btn:hover:not(:disabled) { background: rgba(255, 255, 255, 0.06); color: var(--text-primary); }
.pagination-btn:disabled { opacity: 0.3; cursor: not-allowed; }
.pagination-info { font-size: var(--text-xs); color: var(--text-muted); font-family: var(--font-mono); }

/* Detail */
.detail-panel { display: flex; flex-direction: column; gap: var(--space-5); }
.detail-empty { display: flex; flex-direction: column; align-items: center; justify-content: center; gap: var(--space-3); padding: calc(var(--space-8) * 2); border-radius: var(--radius-xl); text-align: center; color: var(--text-muted); font-size: var(--text-sm); }
.detail-content { display: flex; flex-direction: column; gap: var(--space-5); }
.detail-header { display: flex; justify-content: space-between; align-items: flex-start; gap: var(--space-4); }
.detail-header__name { font-family: var(--font-display); font-size: var(--text-2xl); font-weight: 700; color: var(--text-primary); margin: 0; }
.detail-header__meta { display: flex; align-items: center; gap: var(--space-2); margin-top: var(--space-2); }
.detail-header__version, .detail-header__ambito { font-size: var(--text-xs); color: var(--text-muted); font-family: var(--font-mono); }

/* Etapas */
.etapas-section { display: flex; flex-direction: column; gap: var(--space-4); }
.etapas-header { display: flex; justify-content: space-between; align-items: center; }
.etapas-title { display: flex; align-items: center; gap: var(--space-2); font-family: var(--font-display); font-size: var(--text-lg); font-weight: 600; color: var(--text-primary); margin: 0; }
.etapas-empty { display: flex; flex-direction: column; align-items: center; gap: var(--space-3); padding: var(--space-8); border-radius: var(--radius-lg); text-align: center; color: var(--text-muted); font-size: var(--text-sm); }
.etapas-empty p { margin: 0; }

.etapas-flow { display: flex; flex-direction: column; align-items: stretch; }
.etapa-item { display: flex; flex-direction: column; align-items: center; }
.etapa-connector { display: flex; justify-content: center; padding: var(--space-1) 0; }

.etapa-card {
  display: flex; align-items: center; gap: var(--space-4); padding: var(--space-4) var(--space-5);
  border-radius: var(--radius-lg); cursor: pointer; transition: all 0.2s; width: 100%; box-sizing: border-box;
}
.etapa-card:hover { background: rgba(255, 255, 255, 0.04); border-color: rgba(255, 255, 255, 0.1); }
.etapa-card:hover .etapa-card__edit { opacity: 1; }
.etapa-item--inactive .etapa-card { opacity: 0.5; }

.etapa-card__color { width: 4px; height: 40px; min-width: 4px; border-radius: 2px; }
.etapa-card__body { flex: 1; min-width: 0; }
.etapa-card__top { display: flex; align-items: center; gap: var(--space-2); }
.etapa-card__orden { font-family: var(--font-mono); font-size: var(--text-xs); color: var(--text-muted); min-width: 24px; }
.etapa-card__name { font-family: var(--font-body); font-size: var(--text-sm); font-weight: 600; color: var(--text-primary); }
.etapa-card__bottom { display: flex; align-items: center; gap: var(--space-3); margin-top: var(--space-1); padding-left: calc(24px + var(--space-2)); }
.etapa-card__prob, .etapa-card__lock { display: flex; align-items: center; gap: 4px; font-size: var(--text-xs); color: var(--text-muted); }
.etapa-card__edit { opacity: 0; transition: opacity 0.2s; }

/* Error Toast */
.error-toast {
  position: fixed; bottom: var(--space-6); right: var(--space-6); display: flex; align-items: center; gap: var(--space-3);
  padding: var(--space-4) var(--space-5); background: rgba(244, 63, 94, 0.15); border: 1px solid rgba(244, 63, 94, 0.3);
  border-radius: var(--radius-lg); color: var(--error); font-size: var(--text-sm); cursor: pointer; z-index: 900; backdrop-filter: blur(12px);
}
.error-toast__close { opacity: 0.5; }

.toast-enter-active { transition: all 0.3s ease; }
.toast-leave-active { transition: all 0.2s ease; }
.toast-enter-from { transform: translateY(20px); opacity: 0; }
.toast-leave-to { transform: translateY(10px); opacity: 0; }

/* Responsive */
@media (max-width: 900px) {
  .master-detail { grid-template-columns: 1fr; }
  .master-panel { max-height: 300px; }
}
</style>
