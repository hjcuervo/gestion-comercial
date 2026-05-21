<template>
  <AppLayout>
    <div class="pipeline-view">
      <!-- Header -->
      <section class="pipeline-view__header animate-slideUp">
        <div class="header-left">
          <h1 class="page-title gradient-text">Pipeline</h1>
          <p class="page-subtitle">Gestiona tu proceso comercial y oportunidades</p>
        </div>
        <div class="header-actions">
          <Button v-if="activeTab === 'kanban'" variant="primary" icon="plus" @click="openCreateOportunidad">Nueva Oportunidad</Button>
          <Button v-if="activeTab === 'config'" variant="primary" icon="plus" @click="openCreatePipeline">Nuevo Pipeline</Button>
        </div>
      </section>

      <!-- Tabs -->
      <div class="tabs animate-slideUp delay-1">
        <button class="tab" :class="{ 'tab--active': activeTab === 'kanban' }" @click="goToKanban">
          <Icon name="pipeline" :size="16" /> Kanban
        </button>
        <button class="tab" :class="{ 'tab--active': activeTab === 'config' }" @click="activeTab = 'config'">
          <Icon name="settings" :size="16" /> Configuración
        </button>
      </div>

      <!-- ==================== TAB: KANBAN ==================== -->
      <section v-if="activeTab === 'kanban'" class="kanban-section animate-slideUp delay-2">
        <!-- Pipeline Selector -->
        <div class="kanban-toolbar">
          <div class="pipeline-selector">
            <label class="selector-label">Ámbito:</label>
            <select v-model="kanbanAmbito" class="pipeline-select pipeline-select--sm" @change="onAmbitoChange">
              <option value="">Todos</option>
              <option value="COMERCIAL">Comercial</option>
              <option value="CONTRATACION">Contratación</option>
            </select>
            <label class="selector-label">Pipeline:</label>
            <select v-model="selectedPipelineId" class="pipeline-select" @change="onPipelineChange">
              <option v-for="p in kanbanPipelinesFiltrados" :key="p.id" :value="p.id">{{ p.nombre }}</option>
            </select>
          </div>
          <div class="kanban-stats" v-if="opStore.pipelineActivo">
            <span class="stat-item"><strong>{{ opStore.countAbiertas }}</strong> oportunidad{{ opStore.countAbiertas !== 1 ? 'es' : '' }}</span>
            <span class="stat-divider">·</span>
            <span class="stat-item stat-item--valor"><strong>{{ formatCurrency(opStore.totalValorPipeline) }}</strong> en pipeline</span>
          </div>
        </div>

        <!-- Loading -->
        <div v-if="opStore.loading && !opStore.etapasDelPipeline.length" class="loading-state">
          <Icon name="loader" :size="32" class="animate-spin" /><p>Cargando pipeline...</p>
        </div>

        <!-- Empty -->
        <div v-else-if="!kanbanPipelinesFiltrados.length" class="empty-state glass">
          <div class="empty-icon"><Icon name="pipeline" :size="48" color="var(--primary)" /></div>
          <h3>No hay pipelines activos</h3>
          <p>Ve a la pestaña Configuración para crear tu primer pipeline con sus etapas.</p>
          <Button variant="secondary" @click="activeTab = 'config'">Ir a Configuración</Button>
        </div>

        <div v-else-if="!opStore.etapasDelPipeline.length" class="empty-state glass">
          <div class="empty-icon"><Icon name="chart" :size="48" color="var(--secondary)" /></div>
          <h3>Pipeline sin etapas</h3>
          <p>Este pipeline no tiene etapas configuradas. Agrégalas en la pestaña Configuración.</p>
          <Button variant="secondary" @click="activeTab = 'config'">Configurar Etapas</Button>
        </div>

        <!-- Kanban Board -->
        <div v-else class="kanban-board">
          <div class="kanban-scroll">
            <div
              v-for="etapa in opStore.etapasDelPipeline"
              :key="etapa.id"
              class="kanban-column"
              :class="{ 'kanban-column--drag-over': dragOverEtapaId === etapa.id }"
              @dragover.prevent="onDragOver($event, etapa.id)"
              @dragleave="onDragLeave($event)"
              @drop="onDrop($event, etapa.id)"
            >
              <div class="column-header">
                <div class="column-header__left">
                  <div class="column-color" :style="{ background: etapa.color || 'var(--primary)' }"></div>
                  <span class="column-name">{{ etapa.nombre }}</span>
                  <span class="column-count">{{ getActiveOportunidades(etapa.id).length }}</span>
                </div>
                <span v-if="etapa.probabilidadSugerida != null" class="column-prob">{{ etapa.probabilidadSugerida }}%</span>
              </div>

              <div class="column-body">
                <div
                  v-for="op in getActiveOportunidades(etapa.id)"
                  :key="op.id"
                  class="kanban-card glass"
                  draggable="true"
                  @dragstart="onDragStart($event, op)"
                  @dragend="onDragEnd"
                  @click="openEditOportunidad(op)"
                >
                  <div class="card-header">
                    <span class="card-name">{{ op.nombre }}</span>
                  </div>
                  <div class="card-empresa">{{ op.empresaNombre }}</div>
                  <div class="card-footer">
                    <span v-if="op.valorEstimado" class="card-valor">{{ formatCurrency(op.valorEstimado, op.moneda) }}</span>
                    <span v-if="op.probabilidad != null" class="card-prob">{{ op.probabilidad }}%</span>
                    <span v-if="op.fechaEstimadaCierre" class="card-fecha">
                      <Icon name="clock" :size="10" /> {{ formatDate(op.fechaEstimadaCierre) }}
                    </span>
                  </div>
                  <div class="card-actions">
                    <button v-if="op.estadoMacro === 'GANADA'" class="card-btn card-btn--formalizar" @click.stop="openFormalizar(op)" title="Formalizar contrato">
                      <Icon name="note-add" :size="14" /> Formalizar
                    </button>
                    <button v-else class="card-btn card-btn--cerrar" @click.stop="openCerrar(op)" title="Cerrar oportunidad">
                      <Icon name="check-circle" :size="14" /> Cerrar
                    </button>
                  </div>
                </div>

                <div v-if="!getActiveOportunidades(etapa.id).length" class="column-empty">
                  <span>Sin oportunidades</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- ==================== TAB: CONFIGURACIÓN ==================== -->
      <section v-if="activeTab === 'config'" class="config-section animate-slideUp delay-2">
        <!-- Search & Filters -->
        <div class="config-filters">
          <div class="search-wrapper">
            <Icon name="search" :size="18" color="var(--text-muted)" />
            <input v-model="configSearch" type="text" class="search-input" placeholder="Buscar pipelines..." @input="onConfigSearch" />
            <button v-if="configSearch" class="search-clear" @click="clearConfigSearch"><Icon name="x" :size="16" /></button>
          </div>
          <div class="filter-chips">
            <button class="chip" :class="{ active: configEstado === '' }" @click="setConfigEstado('')">Todos</button>
            <button class="chip" :class="{ active: configEstado === 'ACTIVO' }" @click="setConfigEstado('ACTIVO')">
              <span class="chip-dot chip-dot--activo"></span> Activos
            </button>
            <button class="chip" :class="{ active: configEstado === 'INACTIVO' }" @click="setConfigEstado('INACTIVO')">
              <span class="chip-dot chip-dot--inactivo"></span> Inactivos
            </button>
          </div>
        </div>

        <!-- Loading -->
        <div v-if="pipStore.loading && !pipStore.pipelines.length" class="loading-state">
          <Icon name="loader" :size="32" class="animate-spin" /><p>Cargando pipelines...</p>
        </div>

        <!-- Empty -->
        <div v-else-if="!pipStore.pipelines.length" class="empty-state glass">
          <div class="empty-icon"><Icon name="pipeline" :size="48" color="var(--primary)" /></div>
          <h3>No hay pipelines</h3>
          <p>Crea tu primer pipeline para comenzar a configurar tu proceso de ventas.</p>
          <Button variant="primary" icon="plus" @click="openCreatePipeline">Crear Pipeline</Button>
        </div>

        <!-- Master-Detail -->
        <div v-else class="master-detail">
          <div class="master-panel glass">
            <div class="master-panel__header">
              <span class="master-panel__count">{{ pipStore.pipelines.length }} pipeline{{ pipStore.pipelines.length !== 1 ? 's' : '' }}</span>
            </div>
            <div class="pipeline-list">
              <div v-for="pipeline in pipStore.pipelines" :key="pipeline.id" class="pipeline-card"
                :class="{ 'pipeline-card--selected': configSelectedId === pipeline.id, 'pipeline-card--inactive': pipeline.estado === 'INACTIVO' }"
                @click="selectConfigPipeline(pipeline.id)">
                <div class="pipeline-card__indicator" :style="{ background: pipeline.estado === 'ACTIVO' ? 'var(--success)' : 'var(--text-muted)' }"></div>
                <div class="pipeline-card__body">
                  <div class="pipeline-card__name">{{ pipeline.nombre }}</div>
                  <div class="pipeline-card__meta">
                    <span class="badge" :class="pipeline.estado === 'ACTIVO' ? 'badge--success' : 'badge--muted'">{{ pipeline.estado }}</span>
                    <span class="badge" :class="pipeline.ambito === 'CONTRATACION' ? 'badge--secondary' : 'badge--primary'">{{ pipeline.ambito === 'CONTRATACION' ? 'Contratación' : 'Comercial' }}</span>
                    <span v-if="pipeline.esDefault" class="badge badge--primary">Default</span>
                    <span class="pipeline-card__etapas">{{ pipeline.etapas?.length || 0 }} etapa{{ (pipeline.etapas?.length || 0) !== 1 ? 's' : '' }}</span>
                  </div>
                </div>
                <Icon name="chevron-right" :size="16" color="var(--text-muted)" />
              </div>
            </div>
          </div>

          <div class="detail-panel">
            <div v-if="!configSelectedId" class="detail-empty glass">
              <Icon name="pipeline" :size="40" color="var(--text-muted)" />
              <p>Selecciona un pipeline para ver sus etapas</p>
            </div>

            <div v-else-if="configSelectedPipeline" class="detail-content">
              <Card>
                <template #default>
                  <div class="detail-header">
                    <div class="detail-header__info">
                      <h2 class="detail-header__name">{{ configSelectedPipeline.nombre }}</h2>
                      <div class="detail-header__meta">
                        <span class="badge" :class="configSelectedPipeline.estado === 'ACTIVO' ? 'badge--success' : 'badge--muted'">{{ configSelectedPipeline.estado }}</span>
                        <span class="badge" :class="configSelectedPipeline.ambito === 'CONTRATACION' ? 'badge--secondary' : 'badge--primary'">{{ configSelectedPipeline.ambito === 'CONTRATACION' ? 'Contratación' : 'Comercial' }}</span>
                        <span v-if="configSelectedPipeline.esDefault" class="badge badge--primary">Default</span>
                        <span class="detail-header__version">v{{ configSelectedPipeline.version }}</span>
                      </div>
                    </div>
                    <Button variant="ghost" icon="settings" size="sm" @click="openEditPipeline">Editar</Button>
                  </div>
                </template>
              </Card>

              <div class="etapas-section">
                <div class="etapas-header">
                  <h3 class="etapas-title"><Icon name="chart" :size="18" color="var(--secondary)" /> Etapas del Pipeline</h3>
                  <Button variant="secondary" icon="plus" size="sm" @click="openCreateEtapa">Nueva Etapa</Button>
                </div>

                <div v-if="pipStore.loadingEtapas" class="loading-state loading-state--small">
                  <Icon name="loader" :size="20" class="animate-spin" /><span>Cargando etapas...</span>
                </div>

                <div v-else-if="!configEtapas.length" class="etapas-empty glass">
                  <p>Este pipeline aún no tiene etapas configuradas.</p>
                  <Button variant="secondary" icon="plus" size="sm" @click="openCreateEtapa">Agregar primera etapa</Button>
                </div>

                <div v-else class="etapas-flow">
                  <div v-for="(etapa, index) in configEtapas" :key="etapa.id" class="etapa-item" :class="{ 'etapa-item--inactive': etapa.estado === 'INACTIVA' }">
                    <div v-if="index > 0" class="etapa-connector">
                      <svg width="24" height="20" viewBox="0 0 24 20"><path d="M12 0 L12 20" stroke="var(--glass-border)" stroke-width="2" stroke-dasharray="4 3" /><path d="M6 14 L12 20 L18 14" stroke="var(--glass-border)" stroke-width="2" fill="none" /></svg>
                    </div>
                    <div class="etapa-card glass" @click="openEditEtapa(etapa)">
                      <div class="etapa-card__color" :style="{ background: etapa.color || 'var(--primary)' }"></div>
                      <div class="etapa-card__body">
                        <div class="etapa-card__top">
                          <span class="etapa-card__orden">#{{ etapa.orden }}</span>
                          <span class="etapa-card__name">{{ etapa.nombre }}</span>
                        </div>
                        <div class="etapa-card__bottom">
                          <span v-if="etapa.probabilidadSugerida != null" class="etapa-card__prob"><Icon name="trending-up" :size="12" /> {{ etapa.probabilidadSugerida }}%</span>
                          <span v-if="etapa.modoBloqueo === 1" class="etapa-card__lock"><Icon name="lock" :size="12" /> Bloqueado</span>
                          <span class="badge badge--sm" :class="etapa.estado === 'ACTIVA' ? 'badge--success' : 'badge--muted'">{{ etapa.estado }}</span>
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

      <!-- Error Toasts -->
      <Transition name="toast">
        <div v-if="opStore.error || pipStore.error" class="error-toast" @click="opStore.limpiarError(); pipStore.limpiarError();">
          <Icon name="alert-circle" :size="16" /> {{ opStore.error || pipStore.error }}
          <Icon name="x" :size="14" />
        </div>
      </Transition>

      <!-- Modals: Oportunidad -->
      <OportunidadModal :visible="showOpModal" :oportunidad="editingOp" :empresas="opStore.empresasActivas" :pipelines="kanbanPipelinesFiltrados" :pipeline-preseleccionado="selectedPipelineId" :saving="opStore.saving" :error="modalError" @close="closeOpModal" @submit="handleOpSubmit" />
      <CerrarOportunidadModal :visible="showCerrarModal" :oportunidad-nombre="cerrandoOp?.nombre || ''" :saving="opStore.saving" :error="modalError" @close="showCerrarModal = false" @submit="handleCerrarSubmit" />
      <FormalizarContratoModal v-if="showFormalizarModal" :oportunidad-id="formalizandoOp?.id" :oportunidad-nombre="formalizandoOp?.nombre || ''" :moneda-default="formalizandoOp?.moneda || 'COP'" :valor-default="formalizandoOp?.valorEstimado" @close="showFormalizarModal = false" @created="onContratoFormalizado" />

      <!-- Modals: Pipeline Config -->
      <PipelineModal :visible="showPipModal" :pipeline="editingPip" :saving="pipStore.saving" :error="modalError" @close="closePipModal" @submit="handlePipSubmit" />
      <EtapaModal :visible="showEtapaModal" :etapa="editingEtapa" :next-orden="nextEtapaOrden" :saving="pipStore.saving" :error="modalError" @close="closeEtapaModal" @submit="handleEtapaSubmit" />
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { formatCurrency } from '@/utils/currency';
import AppLayout from '@/components/layout/AppLayout.vue';
import Card from '@/components/ui/Card.vue';
import Button from '@/components/ui/Button.vue';
import Icon from '@/components/ui/Icon.vue';
import OportunidadModal from '@/components/oportunidad/OportunidadModal.vue';
import CerrarOportunidadModal from '@/components/oportunidad/CerrarOportunidadModal.vue';
import FormalizarContratoModal from '@/components/contrato/FormalizarContratoModal.vue';
import PipelineModal from '@/components/pipeline/PipelineModal.vue';
import EtapaModal from '@/components/pipeline/EtapaModal.vue';
import { useOportunidadStore } from '@/stores/oportunidad.store';
import { usePipelineStore } from '@/stores/pipeline.store';

const opStore = useOportunidadStore();
const pipStore = usePipelineStore();

const activeTab = ref('kanban');

/**
 * Cambia al tab Kanban y recarga el pipeline activo desde el backend.
 * Esto garantiza que cambios hechos en Configuración (etapas activadas/desactivadas,
 * renombradas, recoloreadas, etc.) se reflejen al instante en el Kanban,
 * sin necesidad de refresh manual.
 */
async function goToKanban() {
  activeTab.value = 'kanban';
  if (opStore.pipelineActivo?.id) {
    await opStore.seleccionarPipeline(opStore.pipelineActivo.id);
  }
}

// ==================== KANBAN STATE ====================
const selectedPipelineId = ref(null);
const showOpModal = ref(false);
const editingOp = ref(null);
const showCerrarModal = ref(false);
const cerrandoOp = ref(null);
const showFormalizarModal = ref(false);
const formalizandoOp = ref(null);
const modalError = ref(null);
const dragOverEtapaId = ref(null);
const draggingOp = ref(null);

// ==================== CONFIG STATE ====================
const configSearch = ref('');
const configEstado = ref('');
const configSelectedId = ref(null);
const showPipModal = ref(false);
const editingPip = ref(null);
const showEtapaModal = ref(false);
const editingEtapa = ref(null);
let searchTimeout = null;

const configSelectedPipeline = computed(() => pipStore.pipelineSeleccionado);
const configEtapas = computed(() => [...(pipStore.etapas || [])].sort((a, b) => a.orden - b.orden));
const nextEtapaOrden = computed(() => {
  if (!pipStore.etapas.length) return 1;
  return Math.max(...pipStore.etapas.map(e => e.orden)) + 1;
});

// Filtro de ámbito para Kanban y Configuración
const kanbanAmbito = ref('');
const kanbanPipelinesFiltrados = computed(() => {
  const todos = opStore.pipelinesActivos || [];
  if (!kanbanAmbito.value) return todos;
  return todos.filter(p => p.ambito === kanbanAmbito.value);
});

// ==================== LIFECYCLE ====================
onMounted(async () => {
  await opStore.cargarPipelinesActivos();
  if (opStore.pipelineActivo) selectedPipelineId.value = opStore.pipelineActivo.id;
  pipStore.cargarPipelines();
});

// ==================== KANBAN: Only active opportunities ====================
function getActiveOportunidades(etapaId) {
  const all = opStore.oportunidadesPorEtapa[etapaId] || [];
  return all.filter(o => o.estadoMacro === 'ABIERTA' || o.estadoMacro === 'SEGUIMIENTO' || o.estadoMacro === 'GANADA');
}

function onPipelineChange() { opStore.seleccionarPipeline(selectedPipelineId.value); }

function onAmbitoChange() {
  // Select first pipeline of the filtered list
  if (kanbanPipelinesFiltrados.value.length) {
    selectedPipelineId.value = kanbanPipelinesFiltrados.value[0].id;
    onPipelineChange();
  }
}

// Drag & Drop
function onDragStart(event, op) {
  draggingOp.value = op;
  event.dataTransfer.effectAllowed = 'move';
  event.dataTransfer.setData('text/plain', op.id);
  event.target.classList.add('dragging');
}
function onDragEnd(event) { event.target.classList.remove('dragging'); draggingOp.value = null; dragOverEtapaId.value = null; }
function onDragOver(event, etapaId) { if (draggingOp.value) { event.dataTransfer.dropEffect = 'move'; dragOverEtapaId.value = etapaId; } }
function onDragLeave(event) { if (!event.currentTarget.contains(event.relatedTarget)) dragOverEtapaId.value = null; }
async function onDrop(event, etapaId) {
  dragOverEtapaId.value = null;
  const op = draggingOp.value;
  if (!op || op.etapaId === etapaId) return;
  try { await opStore.moverEtapa(op.id, etapaId); } catch {}
}

// Oportunidad CRUD
async function openCreateOportunidad() { editingOp.value = null; modalError.value = null; await opStore.cargarEmpresasActivas(); showOpModal.value = true; }
async function openEditOportunidad(op) { editingOp.value = { ...op }; modalError.value = null; await opStore.cargarEmpresasActivas(); showOpModal.value = true; }
function closeOpModal() { showOpModal.value = false; editingOp.value = null; modalError.value = null; }
async function handleOpSubmit(payload) {
  modalError.value = null;
  try {
    if (editingOp.value) { await opStore.actualizarOportunidad(editingOp.value.id, payload); }
    else { await opStore.crearOportunidad(payload); }
    closeOpModal();
  } catch (err) { modalError.value = err.response?.data?.message || 'Error al guardar oportunidad'; }
}

// Cerrar
function openCerrar(op) { cerrandoOp.value = op; modalError.value = null; showCerrarModal.value = true; }
async function handleCerrarSubmit(payload) {
  modalError.value = null;
  try { await opStore.cerrarOportunidad(cerrandoOp.value.id, payload); showCerrarModal.value = false; cerrandoOp.value = null; }
  catch (err) { modalError.value = err.response?.data?.message || 'Error al cerrar oportunidad'; }
}

// Formalizar contrato
function openFormalizar(op) { formalizandoOp.value = op; modalError.value = null; showFormalizarModal.value = true; }
async function onContratoFormalizado() {
  showFormalizarModal.value = false;
  formalizandoOp.value = null;
  // Recargar oportunidades — la oportunidad CONTRATADA desaparece del Kanban
  await opStore.cargarOportunidades();
}

// ==================== CONFIG: Pipeline CRUD ====================
function onConfigSearch() { clearTimeout(searchTimeout); searchTimeout = setTimeout(() => pipStore.setFiltros({ q: configSearch.value }), 400); }
function clearConfigSearch() { configSearch.value = ''; pipStore.setFiltros({ q: '' }); }
function setConfigEstado(estado) { configEstado.value = estado; pipStore.setFiltros({ estado }); }
function selectConfigPipeline(id) { configSelectedId.value = id; pipStore.seleccionarPipeline(id); }

function openCreatePipeline() { editingPip.value = null; modalError.value = null; showPipModal.value = true; }
function openEditPipeline() { editingPip.value = { ...configSelectedPipeline.value }; modalError.value = null; showPipModal.value = true; }
function closePipModal() { showPipModal.value = false; editingPip.value = null; modalError.value = null; }
async function handlePipSubmit(payload) {
  modalError.value = null;
  try {
    if (editingPip.value) {
      await pipStore.actualizarPipeline(editingPip.value.id, payload);
      if (configSelectedId.value === editingPip.value.id) pipStore.seleccionarPipeline(configSelectedId.value);
    } else {
      const nuevo = await pipStore.crearPipeline(payload);
      selectConfigPipeline(nuevo.id);
    }
    closePipModal();
  } catch (err) { modalError.value = err.response?.data?.message || 'Error al guardar pipeline'; }
}

// Etapa CRUD
function openCreateEtapa() { editingEtapa.value = null; modalError.value = null; showEtapaModal.value = true; }
function openEditEtapa(etapa) { editingEtapa.value = { ...etapa }; modalError.value = null; showEtapaModal.value = true; }
function closeEtapaModal() { showEtapaModal.value = false; editingEtapa.value = null; modalError.value = null; }
async function handleEtapaSubmit(payload) {
  modalError.value = null;
  try {
    if (editingEtapa.value) { await pipStore.actualizarEtapa(configSelectedId.value, editingEtapa.value.id, payload); }
    else { await pipStore.crearEtapa(configSelectedId.value, payload); }
    closeEtapaModal();
  } catch (err) { modalError.value = err.response?.data?.message || 'Error al guardar etapa'; }
}

// Formatters
function formatDate(date) {
  if (!date) return '';
  return new Date(date).toLocaleDateString('es-CO', { day: 'numeric', month: 'short' });
}
</script>

<style scoped>
.pipeline-view { display: flex; flex-direction: column; gap: var(--space-5); height: calc(100vh - var(--header-height) - var(--space-6) * 2); overflow: hidden; }

/* Header */
.pipeline-view__header { display: flex; justify-content: space-between; align-items: center; gap: var(--space-4); flex-shrink: 0; flex-wrap: wrap; }
.page-title { font-family: var(--font-display); font-size: var(--text-3xl); font-weight: 700; margin: 0; line-height: 1.2; }
.page-subtitle { color: var(--text-tertiary); font-size: var(--text-sm); margin: var(--space-1) 0 0; }
.header-actions { flex-shrink: 0; }

/* Tabs */
.tabs { display: flex; gap: var(--space-1); background: var(--glass-bg); border: 1px solid var(--glass-border); border-radius: var(--radius-lg); padding: 3px; flex-shrink: 0; width: fit-content; }
.tab { display: flex; align-items: center; gap: var(--space-2); padding: var(--space-2) var(--space-5); border-radius: var(--radius-md); border: none; background: transparent; color: var(--text-muted); font-family: var(--font-body); font-size: var(--text-sm); font-weight: 500; cursor: pointer; transition: all 0.2s; }
.tab:hover { color: var(--text-secondary); }
.tab--active { background: var(--primary-soft); color: var(--primary); }

/* ==================== KANBAN ==================== */
.kanban-section { display: flex; flex-direction: column; gap: var(--space-4); flex: 1; min-height: 0; overflow: hidden; }

.kanban-toolbar { display: flex; align-items: center; justify-content: space-between; gap: var(--space-4); flex-shrink: 0; flex-wrap: wrap; }
.pipeline-selector { display: flex; align-items: center; gap: var(--space-3); }
.selector-label { font-size: var(--text-sm); color: var(--text-secondary); font-weight: 500; }
.pipeline-select { background: var(--glass-bg); border: 1px solid var(--glass-border); border-radius: var(--radius-md); color: var(--text-primary); font-family: var(--font-body); font-size: var(--text-sm); font-weight: 500; padding: var(--space-2) var(--space-8) var(--space-2) var(--space-4); appearance: none; background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 24 24' fill='none' stroke='rgba(255,255,255,0.3)' stroke-width='2'%3E%3Cpath d='M6 9l6 6 6-6'/%3E%3C/svg%3E"); background-repeat: no-repeat; background-position: right 10px center; }
.pipeline-select:focus { outline: none; border-color: var(--primary); }
.pipeline-select option { background: var(--bg-elevated); }
.pipeline-select--sm { font-size: var(--text-xs); padding: var(--space-2) var(--space-6) var(--space-2) var(--space-3); }
.kanban-stats { display: flex; align-items: center; gap: var(--space-2); font-size: var(--text-xs); color: var(--text-muted); }
.stat-divider { color: var(--glass-border); }
.stat-item strong { color: var(--text-secondary); }
.stat-item--valor strong { color: var(--primary); }

/* Board with scroll */
.kanban-board { flex: 1; min-height: 0; overflow: hidden; }
.kanban-scroll { display: flex; gap: var(--space-4); overflow-x: auto; overflow-y: hidden; height: 100%; padding-bottom: var(--space-3); -webkit-overflow-scrolling: touch; }
.kanban-scroll::-webkit-scrollbar { height: 8px; }
.kanban-scroll::-webkit-scrollbar-track { background: var(--bg-surface); border-radius: 4px; }
.kanban-scroll::-webkit-scrollbar-thumb { background: rgba(255,255,255,0.15); border-radius: 4px; }
.kanban-scroll::-webkit-scrollbar-thumb:hover { background: rgba(255,255,255,0.25); }

.kanban-column { flex: 0 0 280px; display: flex; flex-direction: column; background: rgba(255,255,255,0.01); border: 1px solid var(--glass-border); border-radius: var(--radius-xl); overflow: hidden; transition: border-color 0.2s, background 0.2s; height: 100%; min-height: 300px; }
.kanban-column--drag-over { border-color: var(--primary); background: var(--primary-soft); }

.column-header { display: flex; justify-content: space-between; align-items: center; padding: var(--space-3) var(--space-4); border-bottom: 1px solid var(--glass-border); flex-shrink: 0; }
.column-header__left { display: flex; align-items: center; gap: var(--space-2); }
.column-color { width: 4px; height: 18px; border-radius: 2px; }
.column-name { font-family: var(--font-body); font-size: var(--text-xs); font-weight: 600; color: var(--text-primary); }
.column-count { font-size: 10px; color: var(--text-muted); background: var(--bg-surface); padding: 0 5px; border-radius: var(--radius-full); font-weight: 600; }
.column-prob { font-size: 10px; color: var(--text-muted); font-family: var(--font-mono); }

.column-body { flex: 1; overflow-y: auto; padding: var(--space-3); display: flex; flex-direction: column; gap: var(--space-2); min-height: 80px; }
.column-body::-webkit-scrollbar { width: 4px; }
.column-body::-webkit-scrollbar-track { background: transparent; }
.column-body::-webkit-scrollbar-thumb { background: var(--glass-border); border-radius: 2px; }

.column-empty { display: flex; align-items: center; justify-content: center; height: 60px; color: var(--text-muted); font-size: var(--text-xs); border: 1px dashed var(--glass-border); border-radius: var(--radius-md); }

/* Card */
.kanban-card { padding: var(--space-3); border-radius: var(--radius-md); cursor: grab; transition: all 0.15s; }
.kanban-card:hover { background: rgba(255,255,255,0.04); border-color: rgba(255,255,255,0.1); }
.kanban-card:active { cursor: grabbing; }
.kanban-card.dragging { opacity: 0.4; }

.card-header { display: flex; justify-content: space-between; align-items: flex-start; gap: var(--space-2); }
.card-name { font-family: var(--font-body); font-size: var(--text-xs); font-weight: 600; color: var(--text-primary); line-height: 1.3; }
.card-empresa { font-size: 10px; color: var(--text-secondary); margin-top: 2px; }
.card-footer { display: flex; align-items: center; gap: var(--space-2); margin-top: var(--space-2); flex-wrap: wrap; }
.card-valor { font-family: var(--font-mono); font-size: 10px; font-weight: 600; color: var(--primary); }
.card-prob { font-size: 9px; color: var(--text-muted); background: var(--bg-surface); padding: 0 4px; border-radius: var(--radius-full); }
.card-fecha { display: flex; align-items: center; gap: 2px; font-size: 9px; color: var(--text-muted); }

.card-actions { display: flex; justify-content: flex-end; margin-top: var(--space-2); padding-top: var(--space-2); border-top: 1px solid var(--glass-border); }
.card-btn { display: flex; align-items: center; gap: 4px; padding: 4px 10px; border-radius: var(--radius-full); border: 1px solid transparent; font-family: var(--font-body); font-size: 10px; font-weight: 600; cursor: pointer; transition: all 0.2s; }
.card-btn--cerrar { background: rgba(16,185,129,0.08); color: var(--success); border-color: rgba(16,185,129,0.2); }
.card-btn--cerrar:hover { background: rgba(16,185,129,0.2); border-color: var(--success); }
.card-btn--formalizar { background: rgba(168,85,247,0.08); color: var(--secondary); border-color: rgba(168,85,247,0.2); }
.card-btn--formalizar:hover { background: rgba(168,85,247,0.2); border-color: var(--secondary); }
.card-btn--formalizar { background: rgba(168,85,247,0.08); color: var(--secondary); border-color: rgba(168,85,247,0.2); }
.card-btn--formalizar:hover { background: rgba(168,85,247,0.2); border-color: var(--secondary); }

/* ==================== CONFIG ==================== */
.config-section { display: flex; flex-direction: column; gap: var(--space-5); flex: 1; min-height: 0; overflow-y: auto; }

.config-filters { display: flex; gap: var(--space-4); align-items: center; flex-wrap: wrap; flex-shrink: 0; }
.search-wrapper { position: relative; display: flex; align-items: center; flex: 1; min-width: 240px; max-width: 400px; }
.search-wrapper > :first-child { position: absolute; left: var(--space-4); pointer-events: none; }
.search-input { width: 100%; background: var(--glass-bg); border: 1px solid var(--glass-border); border-radius: var(--radius-lg); color: var(--text-primary); font-family: var(--font-body); font-size: var(--text-sm); padding: var(--space-3) var(--space-4) var(--space-3) calc(var(--space-4) + 24px); box-sizing: border-box; }
.search-input:focus { outline: none; border-color: var(--primary); box-shadow: 0 0 0 3px var(--primary-soft); }
.search-input::placeholder { color: var(--text-muted); }
.search-clear { position: absolute; right: var(--space-3); background: none; border: none; color: var(--text-muted); cursor: pointer; display: flex; padding: var(--space-1); }
.filter-chips { display: flex; gap: var(--space-2); }
.chip { display: flex; align-items: center; gap: var(--space-2); padding: var(--space-2) var(--space-4); background: var(--glass-bg); border: 1px solid var(--glass-border); border-radius: var(--radius-full); color: var(--text-secondary); font-family: var(--font-body); font-size: var(--text-xs); font-weight: 500; cursor: pointer; transition: all 0.2s; }
.chip:hover { background: rgba(255,255,255,0.06); }
.chip.active { border-color: var(--primary); color: var(--primary); background: var(--primary-soft); }
.chip-dot { width: 6px; height: 6px; border-radius: 50%; }
.chip-dot--activo { background: var(--success); }
.chip-dot--inactivo { background: var(--text-muted); }

/* Master-Detail (config) */
.master-detail { display: grid; grid-template-columns: 340px 1fr; gap: var(--space-6); flex: 1; }
.master-panel { border-radius: var(--radius-xl); overflow: hidden; display: flex; flex-direction: column; height: fit-content; max-height: 500px; }
.master-panel__header { padding: var(--space-3) var(--space-4); border-bottom: 1px solid var(--glass-border); }
.master-panel__count { font-size: var(--text-xs); color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.05em; font-weight: 600; }
.pipeline-list { overflow-y: auto; flex: 1; }
.pipeline-card { display: flex; align-items: center; gap: var(--space-3); padding: var(--space-3) var(--space-4); cursor: pointer; transition: all 0.15s; border-bottom: 1px solid rgba(255,255,255,0.03); }
.pipeline-card:hover { background: rgba(255,255,255,0.03); }
.pipeline-card--selected { background: var(--primary-soft); border-left: 3px solid var(--primary); }
.pipeline-card--inactive { opacity: 0.6; }
.pipeline-card__indicator { width: 8px; height: 8px; min-width: 8px; border-radius: 50%; }
.pipeline-card__body { flex: 1; min-width: 0; }
.pipeline-card__name { font-size: var(--text-sm); font-weight: 600; color: var(--text-primary); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.pipeline-card__meta { display: flex; align-items: center; gap: var(--space-2); margin-top: 2px; }
.pipeline-card__etapas { font-size: var(--text-xs); color: var(--text-muted); }

.detail-panel { display: flex; flex-direction: column; gap: var(--space-5); }
.detail-empty { display: flex; flex-direction: column; align-items: center; justify-content: center; gap: var(--space-3); padding: var(--space-8); border-radius: var(--radius-xl); text-align: center; color: var(--text-muted); font-size: var(--text-sm); }
.detail-content { display: flex; flex-direction: column; gap: var(--space-5); }
.detail-header { display: flex; justify-content: space-between; align-items: flex-start; gap: var(--space-4); }
.detail-header__name { font-family: var(--font-display); font-size: var(--text-2xl); font-weight: 700; color: var(--text-primary); margin: 0; }
.detail-header__meta { display: flex; align-items: center; gap: var(--space-2); margin-top: var(--space-2); }
.detail-header__version { font-size: var(--text-xs); color: var(--text-muted); font-family: var(--font-mono); }

.etapas-section { display: flex; flex-direction: column; gap: var(--space-4); }
.etapas-header { display: flex; justify-content: space-between; align-items: center; }
.etapas-title { display: flex; align-items: center; gap: var(--space-2); font-family: var(--font-display); font-size: var(--text-lg); font-weight: 600; color: var(--text-primary); margin: 0; }
.etapas-empty { display: flex; flex-direction: column; align-items: center; gap: var(--space-3); padding: var(--space-6); border-radius: var(--radius-lg); text-align: center; color: var(--text-muted); font-size: var(--text-sm); }
.etapas-empty p { margin: 0; }
.etapas-flow { display: flex; flex-direction: column; align-items: stretch; }
.etapa-item { display: flex; flex-direction: column; align-items: center; }
.etapa-connector { display: flex; justify-content: center; padding: var(--space-1) 0; }
.etapa-card { display: flex; align-items: center; gap: var(--space-4); padding: var(--space-3) var(--space-4); border-radius: var(--radius-lg); cursor: pointer; transition: all 0.2s; width: 100%; box-sizing: border-box; }
.etapa-card:hover { background: rgba(255,255,255,0.04); }
.etapa-card:hover .etapa-card__edit { opacity: 1; }
.etapa-item--inactive .etapa-card { opacity: 0.5; }
.etapa-card__color { width: 4px; height: 36px; min-width: 4px; border-radius: 2px; }
.etapa-card__body { flex: 1; min-width: 0; }
.etapa-card__top { display: flex; align-items: center; gap: var(--space-2); }
.etapa-card__orden { font-family: var(--font-mono); font-size: var(--text-xs); color: var(--text-muted); min-width: 20px; }
.etapa-card__name { font-size: var(--text-sm); font-weight: 600; color: var(--text-primary); }
.etapa-card__bottom { display: flex; align-items: center; gap: var(--space-3); margin-top: 2px; padding-left: calc(20px + var(--space-2)); }
.etapa-card__prob, .etapa-card__lock { display: flex; align-items: center; gap: 3px; font-size: var(--text-xs); color: var(--text-muted); }
.etapa-card__edit { opacity: 0; transition: opacity 0.2s; }

/* Shared */
.badge { display: inline-flex; align-items: center; padding: 1px var(--space-2); border-radius: var(--radius-full); font-size: 10px; font-weight: 600; text-transform: uppercase; letter-spacing: 0.04em; }
.badge--sm { font-size: 9px; }
.badge--success { background: rgba(16,185,129,0.15); color: var(--success); }
.badge--muted { background: rgba(255,255,255,0.06); color: var(--text-muted); }
.badge--primary { background: var(--primary-soft); color: var(--primary); }
.badge--secondary { background: var(--secondary-soft); color: var(--secondary); }

.loading-state { display: flex; flex-direction: column; align-items: center; justify-content: center; gap: var(--space-3); padding: var(--space-8); color: var(--text-tertiary); font-size: var(--text-sm); }
.loading-state--small { flex-direction: row; padding: var(--space-6); }
.empty-state { display: flex; flex-direction: column; align-items: center; justify-content: center; gap: var(--space-4); padding: calc(var(--space-8) * 2); border-radius: var(--radius-xl); text-align: center; }
.empty-icon { width: 80px; height: 80px; display: flex; align-items: center; justify-content: center; background: var(--primary-soft); border-radius: var(--radius-xl); }
.empty-state h3 { color: var(--text-primary); font-family: var(--font-display); margin: 0; }
.empty-state p { color: var(--text-tertiary); font-size: var(--text-sm); max-width: 320px; margin: 0; }

.error-toast { position: fixed; bottom: var(--space-6); right: var(--space-6); display: flex; align-items: center; gap: var(--space-3); padding: var(--space-4) var(--space-5); background: rgba(244,63,94,0.15); border: 1px solid rgba(244,63,94,0.3); border-radius: var(--radius-lg); color: var(--error); font-size: var(--text-sm); cursor: pointer; z-index: 900; backdrop-filter: blur(12px); }
.toast-enter-active { transition: all 0.3s ease; } .toast-leave-active { transition: all 0.2s ease; }
.toast-enter-from { transform: translateY(20px); opacity: 0; } .toast-leave-to { transform: translateY(10px); opacity: 0; }

@media (max-width: 900px) {
  .master-detail { grid-template-columns: 1fr; }
  .kanban-column { flex: 0 0 260px; }
}
</style>
