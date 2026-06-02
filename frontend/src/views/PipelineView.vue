<template>
  <div class="pipeline">
    <!-- Toolbar: selector de pipeline/ámbito + métricas + tabs Kanban/Config -->
    <header class="pipeline__bar">
      <div class="pipeline__tabs">
        <button class="pipeline__tab" :class="{ 'pipeline__tab--active': tab === 'kanban' }" @click="goToKanban">Kanban</button>
        <button class="pipeline__tab" :class="{ 'pipeline__tab--active': tab === 'config' }" @click="tab = 'config'">Configuración</button>
      </div>

      <div class="pipeline__spacer"></div>

      <template v-if="tab === 'kanban'">
        <div class="pipeline__selectors">
          <select v-model="ambito" class="pipeline__select" @change="onAmbitoChange">
            <option value="">Todos los ámbitos</option>
            <option value="COMERCIAL">Comercial</option>
            <option value="CONTRATACION">Contratación</option>
          </select>
          <select v-model="selectedPipelineId" class="pipeline__select" @change="onPipelineChange">
            <option v-for="p in pipelinesFiltrados" :key="p.id" :value="p.id">{{ p.nombre }}</option>
          </select>
        </div>
        <GcButton variant="primary" icon="plus" @click="openCreateOportunidad">Nueva oportunidad</GcButton>
      </template>
      <template v-else>
        <GcButton variant="primary" icon="plus" @click="openCreatePipeline">Nuevo pipeline</GcButton>
      </template>
    </header>

    <!-- ===================== KANBAN (Tablero) ===================== -->
    <section v-if="tab === 'kanban'" class="pipeline__kanban">
      <div v-if="opStore.pipelineActivo" class="pipeline__stats">
        <span class="pipeline__stat"><strong class="gc-mono">{{ opStore.countAbiertas }}</strong> oportunidades</span>
        <span class="pipeline__statsep">·</span>
        <span class="pipeline__stat"><strong class="gc-mono">{{ fmtCurrency(opStore.totalValorPipeline) }}</strong> en pipeline</span>
      </div>

      <div v-if="opStore.loading && !opStore.etapasDelPipeline.length" class="pipeline__state"><GcSpinner :size="24" /></div>

      <GcEmpty v-else-if="!pipelinesFiltrados.length" icon="layout-kanban" message="No hay pipelines activos. Créalos en Configuración.">
        <template #action><GcButton variant="default" @click="tab = 'config'">Ir a Configuración</GcButton></template>
      </GcEmpty>

      <GcEmpty v-else-if="!opStore.etapasDelPipeline.length" icon="columns" message="Este pipeline no tiene etapas activas.">
        <template #action><GcButton variant="default" @click="tab = 'config'">Configurar etapas</GcButton></template>
      </GcEmpty>

      <div v-else class="board">
        <div
          v-for="etapa in opStore.etapasDelPipeline"
          :key="etapa.id"
          class="board__col"
          :class="{ 'board__col--over': dragOverId === etapa.id }"
          @dragover.prevent="onDragOver($event, etapa.id)"
          @dragleave="onDragLeave"
          @drop="onDrop($event, etapa.id)"
        >
          <div class="board__colhead">
            <span class="board__dot" :style="{ background: etapa.color || 'var(--gc-info)' }"></span>
            <span class="board__colname">{{ etapa.nombre }}</span>
            <span class="board__count gc-mono">{{ cards(etapa.id).length }}</span>
            <span v-if="etapa.probabilidadSugerida != null" class="board__prob gc-mono">{{ etapa.probabilidadSugerida }}%</span>
          </div>

          <div class="board__colbody">
            <article
              v-for="op in cards(etapa.id)"
              :key="op.id"
              class="card"
              :class="`card--${(op.estadoMacro || '').toLowerCase()}`"
              draggable="true"
              @dragstart="onDragStart($event, op)"
              @dragend="onDragEnd"
              @click="goToDetalle(op.id)"
            >
              <div class="card__name">{{ op.nombre }}</div>
              <div class="card__empresa">{{ op.empresaNombre }}</div>
              <div class="card__foot">
                <span v-if="op.valorEstimado" class="card__valor gc-mono">{{ fmtCurrency(op.valorEstimado, op.moneda) }}</span>
                <span v-if="op.probabilidad != null" class="card__prob gc-mono">{{ op.probabilidad }}%</span>
              </div>
              <div class="card__actions">
                <GcButton v-if="op.estadoMacro === 'GANADA'" variant="default" size="sm" icon="file-plus" @click.stop="openFormalizar(op)">Formalizar</GcButton>
                <GcButton v-else variant="ghost" size="sm" icon="circle-check" @click.stop="openCerrar(op)">Cerrar</GcButton>
              </div>
            </article>

            <div v-if="!cards(etapa.id).length" class="board__empty">Sin oportunidades</div>
          </div>
        </div>
      </div>
    </section>

    <!-- ===================== CONFIGURACIÓN ===================== -->
    <section v-else class="pipeline__config">
      <div class="config">
        <!-- Lista de pipelines -->
        <aside class="config__list">
          <div class="config__filters">
            <GcInput v-model="configSearch" placeholder="Buscar pipelines…" icon="search" @update:modelValue="onConfigSearch" />
            <div class="config__chips">
              <button class="chip" :class="{ 'chip--on': configEstado === '' }" @click="setConfigEstado('')">Todos</button>
              <button class="chip" :class="{ 'chip--on': configEstado === 'ACTIVO' }" @click="setConfigEstado('ACTIVO')">Activos</button>
              <button class="chip" :class="{ 'chip--on': configEstado === 'INACTIVO' }" @click="setConfigEstado('INACTIVO')">Inactivos</button>
            </div>
          </div>

          <div v-if="pipStore.loading && !pipStore.pipelines.length" class="pipeline__state"><GcSpinner :size="20" /></div>
          <GcEmpty v-else-if="!pipStore.pipelines.length" icon="layout-kanban" message="No hay pipelines" />
          <div v-else>
            <GcListRow
              v-for="p in pipStore.pipelines"
              :key="p.id"
              :tone="p.estado === 'ACTIVO' ? 'success' : 'neutral'"
              clickable
              :active="configSelectedId === p.id"
              @click="selectConfigPipeline(p.id)"
            >
              <div class="config__row">
                <span class="config__name">{{ p.nombre }}</span>
                <span class="config__meta">
                  <GcBadge :tone="p.ambito === 'CONTRATACION' ? 'accent' : 'info'" :label="p.ambito === 'CONTRATACION' ? 'Contratación' : 'Comercial'" />
                  <span class="config__etapas gc-mono">{{ p.etapas?.length || 0 }} etapas</span>
                </span>
              </div>
            </GcListRow>
          </div>
        </aside>

        <!-- Detalle del pipeline: etapas -->
        <div class="config__detail">
          <GcEmpty v-if="!configSelectedId" icon="hand-click" message="Selecciona un pipeline para ver sus etapas" />
          <template v-else-if="configSelectedPipeline">
            <header class="config__dethead">
              <div class="config__detheading">
                <h2 class="config__dettitle">{{ configSelectedPipeline.nombre }}</h2>
                <GcBadge :tone="configSelectedPipeline.estado === 'ACTIVO' ? 'success' : 'neutral'" :label="configSelectedPipeline.estado" />
                <GcBadge :tone="configSelectedPipeline.ambito === 'CONTRATACION' ? 'accent' : 'info'" :label="configSelectedPipeline.ambito === 'CONTRATACION' ? 'Contratación' : 'Comercial'" />
              </div>
              <GcButton variant="default" size="sm" icon="settings" @click="openEditPipeline">Editar</GcButton>
            </header>

            <div class="config__etapashead">
              <h3 class="config__etapastitle">Etapas</h3>
              <GcButton variant="default" size="sm" icon="plus" @click="openCreateEtapa">Nueva etapa</GcButton>
            </div>

            <div v-if="pipStore.loadingEtapas" class="pipeline__state"><GcSpinner :size="20" /></div>
            <GcEmpty v-else-if="!configEtapas.length" icon="columns" message="Sin etapas configuradas" />
            <div v-else class="config__etapas-list">
              <GcListRow
                v-for="e in configEtapas"
                :key="e.id"
                :tone="e.estado === 'ACTIVA' ? 'success' : 'neutral'"
                clickable
                @click="openEditEtapa(e)"
              >
                <template #lead><span class="config__etapaorden gc-mono">#{{ e.orden }}</span></template>
                <div class="config__etaparow">
                  <span class="config__etapaname">
                    <span class="config__etapacolor" :style="{ background: e.color || 'var(--gc-info)' }"></span>
                    {{ e.nombre }}
                  </span>
                  <span class="config__etapameta gc-mono">
                    <template v-if="e.probabilidadSugerida != null">{{ e.probabilidadSugerida }}%</template>
                  </span>
                </div>
                <template #actions>
                  <GcBadge :tone="e.estado === 'ACTIVA' ? 'success' : 'neutral'" :label="e.estado" />
                </template>
              </GcListRow>
            </div>
          </template>
        </div>
      </div>
    </section>

    <!-- Toast de error -->
    <Transition name="gc-fade">
      <div v-if="opStore.error || pipStore.error" class="pipeline__toast" @click="opStore.limpiarError(); pipStore.limpiarError();">
        <GcIcon name="alert-circle" :size="16" /> {{ opStore.error || pipStore.error }}
      </div>
    </Transition>

    <!-- Modales (legacy temporalmente; se reescriben en RF3-B) -->
    <OportunidadModal :visible="showOpModal" :oportunidad="editingOp" :empresas="opStore.empresasActivas" :pipelines="pipelinesFiltrados" :pipeline-preseleccionado="selectedPipelineId" :saving="opStore.saving" :error="modalError" @close="closeOpModal" @submit="handleOpSubmit" />
    <CerrarOportunidadModal :visible="showCerrarModal" :oportunidad-nombre="cerrandoOp?.nombre || ''" :saving="opStore.saving" :error="modalError" @close="showCerrarModal = false" @submit="handleCerrarSubmit" />
    <FormalizarContratoModal v-if="showFormalizarModal" :oportunidad-id="formalizandoOp?.id" :oportunidad-nombre="formalizandoOp?.nombre || ''" :moneda-default="formalizandoOp?.moneda || 'COP'" :valor-default="formalizandoOp?.valorEstimado" @close="showFormalizarModal = false" @created="onContratoFormalizado" />
    <PipelineModal :visible="showPipModal" :pipeline="editingPip" :saving="pipStore.saving" :error="modalError" @close="closePipModal" @submit="handlePipSubmit" />
    <EtapaModal :visible="showEtapaModal" :etapa="editingEtapa" :next-orden="nextEtapaOrden" :saving="pipStore.saving" :error="modalError" @close="closeEtapaModal" @submit="handleEtapaSubmit" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useShell } from '@/composables/useShell';
import { formatCurrency as fmtCurrency } from '@/utils/currency';
import { useOportunidadStore } from '@/stores/oportunidad.store';
import { usePipelineStore } from '@/stores/pipeline.store';
import GcButton from '@/components/ui/GcButton.vue';
import GcInput from '@/components/ui/GcInput.vue';
import GcBadge from '@/components/ui/GcBadge.vue';
import GcListRow from '@/components/ui/GcListRow.vue';
import GcEmpty from '@/components/ui/GcEmpty.vue';
import GcSpinner from '@/components/ui/GcSpinner.vue';
import GcIcon from '@/components/ui/GcIcon.vue';
import OportunidadModal from '@/components/oportunidad/OportunidadModal.vue';
import CerrarOportunidadModal from '@/components/oportunidad/CerrarOportunidadModal.vue';
import FormalizarContratoModal from '@/components/contrato/FormalizarContratoModal.vue';
import PipelineModal from '@/components/pipeline/PipelineModal.vue';
import EtapaModal from '@/components/pipeline/EtapaModal.vue';

const router = useRouter();
const { setRegions } = useShell();
const opStore = useOportunidadStore();
const pipStore = usePipelineStore();

const tab = ref('kanban');

// ---- Kanban ----
const ambito = ref('');
const selectedPipelineId = ref(null);
const dragOverId = ref(null);
const draggingOp = ref(null);

const pipelinesFiltrados = computed(() => {
  const all = opStore.pipelinesActivos || [];
  return ambito.value ? all.filter((p) => p.ambito === ambito.value) : all;
});

function cards(etapaId) {
  const all = opStore.oportunidadesPorEtapa[etapaId] || [];
  return all.filter((o) => ['ABIERTA', 'SEGUIMIENTO', 'GANADA'].includes(o.estadoMacro));
}

function onPipelineChange() { opStore.seleccionarPipeline(selectedPipelineId.value); }
function onAmbitoChange() {
  if (pipelinesFiltrados.value.length) {
    selectedPipelineId.value = pipelinesFiltrados.value[0].id;
    onPipelineChange();
  }
}

function onDragStart(e, op) { draggingOp.value = op; e.dataTransfer.effectAllowed = 'move'; e.dataTransfer.setData('text/plain', String(op.id)); e.target.classList.add('card--dragging'); }
function onDragEnd(e) { e.target.classList.remove('card--dragging'); draggingOp.value = null; dragOverId.value = null; }
function onDragOver(e, etapaId) { if (draggingOp.value) { e.dataTransfer.dropEffect = 'move'; dragOverId.value = etapaId; } }
function onDragLeave(e) { if (!e.currentTarget.contains(e.relatedTarget)) dragOverId.value = null; }
async function onDrop(e, etapaId) {
  dragOverId.value = null;
  const op = draggingOp.value;
  if (!op || op.etapaId === etapaId) return;
  try { await opStore.moverEtapa(op.id, etapaId); } catch { /* error mostrado por el store */ }
}

function goToDetalle(id) { router.push(`/oportunidades/${id}`); }

// Modales oportunidad
const showOpModal = ref(false);
const editingOp = ref(null);
const showCerrarModal = ref(false);
const cerrandoOp = ref(null);
const showFormalizarModal = ref(false);
const formalizandoOp = ref(null);
const modalError = ref(null);

async function openCreateOportunidad() { editingOp.value = null; modalError.value = null; await opStore.cargarEmpresasActivas(); showOpModal.value = true; }
function closeOpModal() { showOpModal.value = false; editingOp.value = null; modalError.value = null; }
async function handleOpSubmit(payload) {
  modalError.value = null;
  try {
    if (editingOp.value) await opStore.actualizarOportunidad(editingOp.value.id, payload);
    else await opStore.crearOportunidad(payload);
    closeOpModal();
  } catch (err) { modalError.value = err.response?.data?.message || 'Error al guardar oportunidad'; }
}

function openCerrar(op) { cerrandoOp.value = op; modalError.value = null; showCerrarModal.value = true; }
async function handleCerrarSubmit(payload) {
  modalError.value = null;
  try { await opStore.cerrarOportunidad(cerrandoOp.value.id, payload); showCerrarModal.value = false; cerrandoOp.value = null; }
  catch (err) { modalError.value = err.response?.data?.message || 'Error al cerrar oportunidad'; }
}

function openFormalizar(op) { formalizandoOp.value = op; modalError.value = null; showFormalizarModal.value = true; }
async function onContratoFormalizado() { showFormalizarModal.value = false; formalizandoOp.value = null; await opStore.cargarOportunidades(); }

// ---- Configuración ----
const configSearch = ref('');
const configEstado = ref('');
const configSelectedId = ref(null);
const showPipModal = ref(false);
const editingPip = ref(null);
const showEtapaModal = ref(false);
const editingEtapa = ref(null);
let searchTimer = null;

const configSelectedPipeline = computed(() => pipStore.pipelineSeleccionado);
const configEtapas = computed(() => [...(pipStore.etapas || [])].sort((a, b) => a.orden - b.orden));
const nextEtapaOrden = computed(() => (pipStore.etapas.length ? Math.max(...pipStore.etapas.map((e) => e.orden)) + 1 : 1));

function onConfigSearch() { clearTimeout(searchTimer); searchTimer = setTimeout(() => pipStore.setFiltros({ q: configSearch.value }), 400); }
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

function openCreateEtapa() { editingEtapa.value = null; modalError.value = null; showEtapaModal.value = true; }
function openEditEtapa(etapa) { editingEtapa.value = { ...etapa }; modalError.value = null; showEtapaModal.value = true; }
function closeEtapaModal() { showEtapaModal.value = false; editingEtapa.value = null; modalError.value = null; }
async function handleEtapaSubmit(payload) {
  modalError.value = null;
  try {
    if (editingEtapa.value) await pipStore.actualizarEtapa(configSelectedId.value, editingEtapa.value.id, payload);
    else await pipStore.crearEtapa(configSelectedId.value, payload);
    closeEtapaModal();
  } catch (err) { modalError.value = err.response?.data?.message || 'Error al guardar etapa'; }
}

// Política 1: recargar al cambiar a Kanban (los tabs no remontan la vista)
async function goToKanban() {
  tab.value = 'kanban';
  if (opStore.pipelineActivo?.id) await opStore.seleccionarPipeline(opStore.pipelineActivo.id);
}

onMounted(async () => {
  setRegions({ master: false, aside: false }); // Tablero: superficie a todo el ancho
  await opStore.cargarPipelinesActivos();
  if (opStore.pipelineActivo) selectedPipelineId.value = opStore.pipelineActivo.id;
  pipStore.cargarPipelines();
});
</script>

<style scoped>
.pipeline { display: flex; flex-direction: column; height: 100%; }

/* Barra superior de la vista */
.pipeline__bar {
  display: flex;
  align-items: center;
  gap: var(--gc-space-3);
  padding: var(--gc-space-3) var(--gc-space-5);
  border-bottom: 1px solid var(--gc-border);
}
.pipeline__tabs { display: flex; gap: var(--gc-space-1); }
.pipeline__tab {
  padding: var(--gc-space-2) var(--gc-space-3);
  background: transparent;
  border: none;
  border-radius: var(--gc-radius-sm);
  font-size: var(--gc-fs-md);
  color: var(--gc-text-2);
}
.pipeline__tab:hover { background: var(--gc-surface-2); color: var(--gc-text); }
.pipeline__tab--active { color: var(--gc-text); font-weight: var(--gc-fw-medium); box-shadow: inset 0 -2px 0 var(--gc-primary); }
.pipeline__spacer { flex: 1; }
.pipeline__selectors { display: flex; gap: var(--gc-space-2); }
.pipeline__select {
  height: 32px;
  padding: 0 var(--gc-space-3);
  background: var(--gc-surface-2);
  border: 1px solid var(--gc-border);
  border-radius: var(--gc-radius-md);
  color: var(--gc-text);
  font-size: var(--gc-fs-sm);
}

/* Kanban */
.pipeline__kanban { flex: 1; display: flex; flex-direction: column; min-height: 0; }
.pipeline__stats { display: flex; align-items: center; gap: var(--gc-space-2); padding: var(--gc-space-3) var(--gc-space-5); font-size: var(--gc-fs-sm); color: var(--gc-text-2); }
.pipeline__statsep { color: var(--gc-text-3); }
.pipeline__state { display: flex; justify-content: center; padding: var(--gc-space-12); color: var(--gc-text-3); }

.board { flex: 1; display: flex; gap: var(--gc-space-3); padding: 0 var(--gc-space-5) var(--gc-space-5); overflow-x: auto; min-height: 0; }
.board__col {
  width: 280px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  background: var(--gc-surface-2);
  border: 1px solid var(--gc-border);
  border-radius: var(--gc-radius-lg);
  transition: border-color var(--gc-t-fast), background var(--gc-t-fast);
}
.board__col--over { border-color: var(--gc-primary); background: var(--gc-surface); }
.board__colhead { display: flex; align-items: center; gap: var(--gc-space-2); padding: var(--gc-space-3); border-bottom: 1px solid var(--gc-border); }
.board__dot { width: 8px; height: 8px; border-radius: var(--gc-radius-full); flex-shrink: 0; }
.board__colname { font-size: var(--gc-fs-md); font-weight: var(--gc-fw-medium); flex: 1; }
.board__count { font-size: var(--gc-fs-xs); color: var(--gc-text-3); }
.board__prob { font-size: var(--gc-fs-xs); color: var(--gc-text-3); }
.board__colbody { flex: 1; display: flex; flex-direction: column; gap: var(--gc-space-2); padding: var(--gc-space-2); overflow-y: auto; }
.board__empty { padding: var(--gc-space-6) var(--gc-space-2); text-align: center; font-size: var(--gc-fs-sm); color: var(--gc-text-3); }

.card {
  position: relative;
  background: var(--gc-surface);
  border: 1px solid var(--gc-border);
  border-left: 2px solid var(--gc-border-strong);
  border-radius: var(--gc-radius-md);
  padding: var(--gc-space-3);
  cursor: pointer;
  transition: border-color var(--gc-t-fast);
}
.card:hover { border-color: var(--gc-border-strong); }
.card--dragging { opacity: 0.5; }
.card--ganada { border-left-color: var(--gc-success); }
.card--seguimiento { border-left-color: var(--gc-warning); }
.card--abierta { border-left-color: var(--gc-info); }
.card__name { font-size: var(--gc-fs-md); color: var(--gc-text); }
.card__empresa { margin-top: 2px; font-size: var(--gc-fs-xs); color: var(--gc-text-3); }
.card__foot { display: flex; align-items: center; gap: var(--gc-space-2); margin-top: var(--gc-space-2); }
.card__valor { font-size: var(--gc-fs-sm); color: var(--gc-text-2); }
.card__prob { font-size: var(--gc-fs-xs); color: var(--gc-text-3); margin-left: auto; }
.card__actions { margin-top: var(--gc-space-2); display: flex; }

/* Configuración */
.config { display: flex; gap: var(--gc-space-5); padding: var(--gc-space-5); height: 100%; }
.config__list { width: 360px; flex-shrink: 0; border: 1px solid var(--gc-border); border-radius: var(--gc-radius-lg); overflow: hidden; align-self: flex-start; }
.config__filters { display: flex; flex-direction: column; gap: var(--gc-space-2); padding: var(--gc-space-3); border-bottom: 1px solid var(--gc-border); }
.config__chips { display: flex; gap: var(--gc-space-1); }
.chip { padding: 4px 10px; background: var(--gc-surface-2); border: 1px solid var(--gc-border); border-radius: var(--gc-radius-full); font-size: var(--gc-fs-xs); color: var(--gc-text-2); }
.chip--on { background: var(--gc-primary); color: var(--gc-primary-text); border-color: var(--gc-primary); }
.config__row { display: flex; flex-direction: column; gap: 4px; }
.config__name { font-size: var(--gc-fs-md); }
.config__meta { display: flex; align-items: center; gap: var(--gc-space-2); }
.config__etapas { font-size: var(--gc-fs-xs); color: var(--gc-text-3); }

.config__detail { flex: 1; min-width: 0; }
.config__dethead { display: flex; align-items: center; gap: var(--gc-space-3); padding-bottom: var(--gc-space-4); border-bottom: 1px solid var(--gc-border); }
.config__detheading { display: flex; align-items: center; gap: var(--gc-space-2); flex: 1; }
.config__dettitle { font-size: var(--gc-fs-lg); }
.config__etapashead { display: flex; align-items: center; justify-content: space-between; margin: var(--gc-space-4) 0 var(--gc-space-2); }
.config__etapastitle { font-size: var(--gc-fs-xs); text-transform: uppercase; letter-spacing: 0.06em; color: var(--gc-text-3); }
.config__etapas-list { border: 1px solid var(--gc-border); border-radius: var(--gc-radius-lg); overflow: hidden; }
.config__etapas-list > :deep(.gc-row:last-child) { border-bottom: none; }
.config__etapaorden { font-size: var(--gc-fs-xs); color: var(--gc-text-3); }
.config__etaparow { display: flex; align-items: center; justify-content: space-between; gap: var(--gc-space-2); }
.config__etapaname { display: flex; align-items: center; gap: var(--gc-space-2); font-size: var(--gc-fs-md); }
.config__etapacolor { width: 8px; height: 8px; border-radius: var(--gc-radius-full); }
.config__etapameta { font-size: var(--gc-fs-xs); color: var(--gc-text-3); }

/* Toast */
.pipeline__toast {
  position: fixed;
  bottom: var(--gc-space-5);
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  align-items: center;
  gap: var(--gc-space-2);
  padding: var(--gc-space-3) var(--gc-space-4);
  background: var(--gc-danger);
  color: #fff;
  border-radius: var(--gc-radius-md);
  font-size: var(--gc-fs-sm);
  box-shadow: var(--gc-shadow-pop);
  z-index: var(--gc-z-toast);
  cursor: pointer;
}
.gc-fade-enter-active, .gc-fade-leave-active { transition: opacity var(--gc-t-normal); }
.gc-fade-enter-from, .gc-fade-leave-to { opacity: 0; }
</style>
