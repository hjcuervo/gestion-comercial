<template>
  <AppLayout>
    <div class="detalle">
      <!-- Loading -->
      <div v-if="loading" class="loading-state">
        <Icon name="loader" :size="32" class="animate-spin" /><p>Cargando oportunidad...</p>
      </div>

      <!-- Error -->
      <div v-else-if="error" class="error-state glass">
        <Icon name="alert-circle" :size="40" color="var(--error)" />
        <p>{{ error }}</p>
        <button class="btn-back" @click="goBack"><Icon name="chevron-left" :size="16" /> Volver</button>
      </div>

      <template v-else-if="op">
        <!-- Top bar -->
        <section class="detalle__topbar animate-slideUp">
          <button class="btn-back" @click="goBack"><Icon name="chevron-left" :size="16" /> Volver</button>
          <span :class="['estado-badge', `estado-badge--${op.estadoMacro?.toLowerCase()}`]">{{ estadoLabel }}</span>
        </section>

        <!-- Header -->
        <section class="detalle__header animate-slideUp delay-1">
          <h1 class="detalle__nombre">{{ op.nombre }}</h1>
          <p class="detalle__empresa">{{ op.empresaNombre }}</p>
        </section>

        <!-- KPIs -->
        <section class="detalle__kpis animate-slideUp delay-2">
          <div class="mini-kpi">
            <span class="mini-kpi__label">Valor Estimado</span>
            <span class="mini-kpi__value mini-kpi__value--primary">{{ formatCurrencyFull(op.valorEstimado, op.moneda) }}</span>
          </div>
          <div class="mini-kpi">
            <span class="mini-kpi__label">Probabilidad</span>
            <span class="mini-kpi__value">{{ op.probabilidad != null ? op.probabilidad + '%' : '—' }}</span>
          </div>
          <div class="mini-kpi">
            <span class="mini-kpi__label">Pipeline</span>
            <span class="mini-kpi__value">{{ op.pipelineNombre }}</span>
          </div>
          <div class="mini-kpi">
            <span class="mini-kpi__label">Etapa Actual</span>
            <span class="mini-kpi__value">{{ op.etapaNombre }}</span>
          </div>
        </section>

        <!-- Info grid -->
        <section class="detalle__grid animate-slideUp delay-3">
          <!-- Datos generales -->
          <div class="info-card glass">
            <h3 class="info-card__title"><Icon name="note-add" :size="16" color="var(--primary)" /> Datos Generales</h3>
            <div class="info-rows">
              <div class="info-row"><span class="info-row__label">Fuente</span><span class="info-row__value">{{ op.fuente || '—' }}</span></div>
              <div class="info-row"><span class="info-row__label">Tipo de Servicio</span><span class="info-row__value">{{ op.tipoServicio || '—' }}</span></div>
              <div class="info-row"><span class="info-row__label">Moneda</span><span class="info-row__value">{{ op.moneda || 'COP' }}</span></div>
              <div class="info-row"><span class="info-row__label">Fecha Est. Cierre</span><span class="info-row__value">{{ formatDate(op.fechaEstimadaCierre) || '—' }}</span></div>
              <div class="info-row"><span class="info-row__label">Fecha Creación</span><span class="info-row__value">{{ formatDateTime(op.fechaCreacion) }}</span></div>
            </div>
          </div>

          <!-- Cierre o Resumen -->
          <div v-if="isCerrada" class="info-card glass">
            <h3 class="info-card__title">
              <Icon :name="op.estadoMacro === 'GANADA' ? 'trophy' : 'trending-down'" :size="16"
                :color="op.estadoMacro === 'GANADA' ? 'var(--success)' : 'var(--error)'" />
              Información de Cierre
            </h3>
            <div class="info-rows">
              <div class="info-row"><span class="info-row__label">Estado Final</span><span :class="['info-row__value', `text--${op.estadoMacro?.toLowerCase()}`]">{{ estadoLabel }}</span></div>
              <div class="info-row"><span class="info-row__label">Fecha de Cierre</span><span class="info-row__value">{{ formatDateTime(op.fechaCierre) }}</span></div>
              <div v-if="op.comentarioCierre" class="info-row info-row--full">
                <span class="info-row__label">Comentario de Cierre</span>
                <span class="info-row__value info-row__value--comment">{{ op.comentarioCierre }}</span>
              </div>
            </div>
          </div>
          <div v-else class="info-card glass">
            <h3 class="info-card__title"><Icon name="chart" :size="16" color="var(--secondary)" /> Resumen</h3>
            <div class="resumen-visual">
              <div class="prob-ring">
                <svg viewBox="0 0 100 100" class="prob-ring__svg">
                  <circle cx="50" cy="50" r="42" fill="none" stroke="var(--bg-surface)" stroke-width="8" />
                  <circle cx="50" cy="50" r="42" fill="none" :stroke="probColor" stroke-width="8"
                    stroke-linecap="round" :stroke-dasharray="probDash" stroke-dashoffset="0" transform="rotate(-90 50 50)" />
                </svg>
                <span class="prob-ring__text">{{ op.probabilidad ?? 0 }}%</span>
                <span class="prob-ring__label">Probabilidad</span>
              </div>
              <div class="resumen-stats">
                <div class="resumen-stat"><span class="resumen-stat__value">{{ formatCurrencyShort(op.valorEstimado) }}</span><span class="resumen-stat__label">Valor</span></div>
                <div class="resumen-stat"><span class="resumen-stat__value">{{ diasEnPipeline }}</span><span class="resumen-stat__label">Días en pipeline</span></div>
                <div class="resumen-stat"><span class="resumen-stat__value">{{ diasParaCierre }}</span><span class="resumen-stat__label">Días p/ cierre est.</span></div>
              </div>
            </div>
          </div>
        </section>

        <!-- ==================== ACTIVIDADES ==================== -->
        <section class="actividades-section animate-slideUp delay-4">
          <div class="section-header">
            <h2 class="section-title"><Icon name="calendar" :size="18" color="var(--primary)" /> Actividades</h2>
            <button v-if="!isCerrada" class="btn btn--primary btn--sm" @click="showActividadModal = true">
              <Icon name="plus" :size="14" /> Nueva Actividad
            </button>
          </div>

          <div v-if="loadingActividades" class="loading-state loading-state--sm">
            <Icon name="loader" :size="20" class="animate-spin" /><p>Cargando actividades...</p>
          </div>

          <div v-else-if="!actividades.length" class="empty-actividades glass">
            <Icon name="calendar" :size="32" color="var(--text-muted)" />
            <p>No hay actividades registradas</p>
            <button v-if="!isCerrada" class="btn btn--ghost btn--sm" @click="showActividadModal = true">Registrar primera actividad</button>
          </div>

          <div v-else class="timeline">
            <div v-for="act in actividades" :key="act.id" class="timeline-item glass">
              <div class="timeline-dot" :style="{ background: 'var(--primary)' }"></div>
              <div class="timeline-content">
                <!-- Header -->
                <div class="act-header">
                  <div class="act-header__left">
                    <Icon :name="getTipoIcono(act.tipoActividadId)" :size="14" class="act-icon" />
                    <span class="act-tipo">{{ getTipoNombre(act.tipoActividadId) }}</span>
                    <span v-if="act.duracionMinutos" class="act-duracion">{{ act.duracionMinutos }} min</span>
                  </div>
                  <span class="act-fecha">{{ formatDateTime(act.fechaHora) }}</span>
                </div>
                <!-- Notas -->
                <p v-if="act.notas" class="act-notas">{{ act.notas }}</p>
                <!-- Compromisos -->
                <div v-if="act.compromisos && act.compromisos.length" class="compromisos">
                  <span class="compromisos-title">Compromisos:</span>
                  <div v-for="comp in act.compromisos" :key="comp.id" class="compromiso-item">
                    <button
                      :class="['comp-check', { 'comp-check--done': comp.estado === 'COMPLETADO' }]"
                      @click="toggleCompromiso(comp)"
                      :disabled="isCerrada"
                      :title="comp.estado === 'COMPLETADO' ? 'Completado' : 'Marcar como completado'"
                    >
                      <Icon :name="comp.estado === 'COMPLETADO' ? 'check-circle' : 'clock'" :size="14" />
                    </button>
                    <div class="comp-info">
                      <span :class="['comp-desc', { 'comp-desc--done': comp.estado === 'COMPLETADO' }]">{{ comp.descripcion }}</span>
                      <span class="comp-fecha">{{ formatDateShort(comp.fechaCompromiso) }}
                        <span v-if="isCompromisoVencido(comp)" class="comp-vencido">Vencido</span>
                      </span>
                    </div>
                    <span :class="['comp-estado', `comp-estado--${comp.estado?.toLowerCase()}`]">{{ compromisoEstadoLabel(comp.estado) }}</span>
                  </div>
                </div>
                <!-- Agregar compromiso -->
                <button v-if="!isCerrada" class="btn-add-comp" @click="openCompromisoModal(act.id)">
                  <Icon name="plus" :size="12" /> Agregar compromiso
                </button>
              </div>
            </div>
          </div>
        </section>
      </template>

      <!-- Modals -->
      <ActividadModal
        v-if="showActividadModal"
        :oportunidad-id="Number(op.id)"
        @close="showActividadModal = false"
        @created="onActividadCreated"
      />
      <CompromisoModal
        v-if="showCompromisoModal"
        :actividad-id="selectedActividadId"
        @close="showCompromisoModal = false"
        @created="onCompromisoCreated"
      />
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import AppLayout from '@/components/layout/AppLayout.vue';
import Icon from '@/components/ui/Icon.vue';
import ActividadModal from '@/components/actividad/ActividadModal.vue';
import CompromisoModal from '@/components/actividad/CompromisoModal.vue';
import { oportunidadService } from '@/services/oportunidad.service';
import { actividadService } from '@/services/actividad.service';

const route = useRoute();
const router = useRouter();
const op = ref(null);
const loading = ref(true);
const error = ref(null);

// Actividades
const actividades = ref([]);
const loadingActividades = ref(false);
const tiposActividad = ref([]);
const showActividadModal = ref(false);
const showCompromisoModal = ref(false);
const selectedActividadId = ref(null);

onMounted(async () => {
  try {
    const id = route.params.id;
    op.value = await oportunidadService.obtenerPorId(id);
  } catch (err) {
    error.value = err.response?.data?.message || 'No se pudo cargar la oportunidad';
  } finally {
    loading.value = false;
  }

  // Load tipos and actividades in parallel
  try {
    tiposActividad.value = await actividadService.listarTipos();
  } catch {}

  if (op.value) {
    await loadActividades();
  }
});

async function loadActividades() {
  loadingActividades.value = true;
  try {
    const res = await actividadService.listarPorOportunidad(op.value.id);
    actividades.value = res.data || res || [];
  } catch (err) {
    console.error('Error cargando actividades:', err);
  } finally {
    loadingActividades.value = false;
  }
}

function getTipoNombre(tipoId) {
  const tipo = tiposActividad.value.find(t => t.id === tipoId);
  return tipo ? tipo.nombre : `Tipo ${tipoId}`;
}

function getTipoIcono(tipoId) {
  const tipo = tiposActividad.value.find(t => t.id === tipoId);
  return tipo?.icono || 'note-add';
}

function openCompromisoModal(actividadId) {
  selectedActividadId.value = actividadId;
  showCompromisoModal.value = true;
}

async function onActividadCreated() {
  showActividadModal.value = false;
  await loadActividades();
}

async function onCompromisoCreated() {
  showCompromisoModal.value = false;
  await loadActividades();
}

async function toggleCompromiso(comp) {
  if (isCerrada.value) return;
  try {
    const nuevoEstado = comp.estado === 'COMPLETADO' ? 'PENDIENTE' : 'COMPLETADO';
    await actividadService.actualizarCompromiso(comp.id, { estado: nuevoEstado });
    await loadActividades();
  } catch (err) {
    console.error('Error actualizando compromiso:', err);
  }
}

function isCompromisoVencido(comp) {
  if (comp.estado === 'COMPLETADO' || comp.estado === 'CANCELADO') return false;
  return new Date(comp.fechaCompromiso) < new Date();
}

function compromisoEstadoLabel(estado) {
  const map = { PENDIENTE: 'Pendiente', EN_PROGRESO: 'En Progreso', COMPLETADO: 'Completado', CANCELADO: 'Cancelado' };
  return map[estado] || estado;
}

// Computed
const isCerrada = computed(() => {
  const e = op.value?.estadoMacro;
  return e === 'GANADA' || e === 'PERDIDA' || e === 'NO_CONCRETADA';
});

const estadoLabel = computed(() => {
  const map = { ABIERTA: 'Abierta', SEGUIMIENTO: 'Seguimiento', GANADA: 'Ganada', PERDIDA: 'Perdida', NO_CONCRETADA: 'No Concretada' };
  return map[op.value?.estadoMacro] || op.value?.estadoMacro;
});

const probColor = computed(() => {
  const p = op.value?.probabilidad ?? 0;
  if (p >= 70) return 'var(--success)';
  if (p >= 40) return 'var(--warning)';
  return 'var(--error)';
});

const probDash = computed(() => {
  const p = op.value?.probabilidad ?? 0;
  const circ = 2 * Math.PI * 42;
  const filled = (p / 100) * circ;
  return `${filled} ${circ - filled}`;
});

const diasEnPipeline = computed(() => {
  if (!op.value?.fechaCreacion) return '—';
  return Math.floor((new Date() - new Date(op.value.fechaCreacion)) / (1000 * 60 * 60 * 24));
});

const diasParaCierre = computed(() => {
  if (!op.value?.fechaEstimadaCierre) return '—';
  const diff = Math.floor((new Date(op.value.fechaEstimadaCierre) - new Date()) / (1000 * 60 * 60 * 24));
  return diff >= 0 ? diff : `${Math.abs(diff)} vencidos`;
});

function goBack() { router.back(); }

function formatCurrencyFull(value, moneda) {
  const num = Number(value);
  if (!num) return '$0';
  const prefix = moneda === 'USD' ? 'US$' : moneda === 'EUR' ? '€' : '$';
  return prefix + num.toLocaleString('es-CO');
}

function formatCurrencyShort(value) {
  const num = Number(value);
  if (!num) return '$0';
  if (num >= 1000000000) return `$${(num / 1000000000).toFixed(1)}B`;
  if (num >= 1000000) return `$${(num / 1000000).toFixed(0)}M`;
  if (num >= 1000) return `$${(num / 1000).toFixed(0)}K`;
  return `$${num.toLocaleString()}`;
}

function formatDate(date) {
  if (!date) return null;
  return new Date(date).toLocaleDateString('es-CO', { day: 'numeric', month: 'long', year: 'numeric' });
}

function formatDateTime(dt) {
  if (!dt) return '—';
  return new Date(dt).toLocaleDateString('es-CO', { day: 'numeric', month: 'long', year: 'numeric', hour: '2-digit', minute: '2-digit' });
}

function formatDateShort(date) {
  if (!date) return '—';
  return new Date(date).toLocaleDateString('es-CO', { day: '2-digit', month: 'short' });
}
</script>

<style scoped>
.detalle { display: flex; flex-direction: column; gap: var(--space-5); }

.loading-state, .error-state { display: flex; flex-direction: column; align-items: center; justify-content: center; gap: var(--space-3); padding: var(--space-8); color: var(--text-tertiary); font-size: var(--text-sm); }
.loading-state--sm { padding: var(--space-4); }
.error-state { border-radius: var(--radius-xl); padding: var(--space-10); }

/* Top bar */
.detalle__topbar { display: flex; justify-content: space-between; align-items: center; }
.btn-back { display: flex; align-items: center; gap: var(--space-2); background: var(--glass-bg); border: 1px solid var(--glass-border); border-radius: var(--radius-full); padding: var(--space-2) var(--space-4); color: var(--text-secondary); font-family: var(--font-body); font-size: var(--text-xs); cursor: pointer; transition: all 0.15s; }
.btn-back:hover { background: var(--glass-hover); color: var(--text-primary); border-color: var(--primary); }

.estado-badge { padding: var(--space-2) var(--space-4); border-radius: var(--radius-full); font-size: var(--text-xs); font-weight: 600; text-transform: uppercase; letter-spacing: 0.5px; }
.estado-badge--abierta { background: var(--primary-soft); color: var(--primary); }
.estado-badge--seguimiento { background: var(--warning-soft); color: var(--warning); }
.estado-badge--ganada { background: var(--success-soft); color: var(--success); }
.estado-badge--perdida { background: var(--error-soft); color: var(--error); }
.estado-badge--no_concretada { background: var(--accent-soft); color: var(--accent); }

/* Header */
.detalle__header { display: flex; flex-direction: column; gap: var(--space-1); }
.detalle__nombre { font-family: var(--font-display); font-size: var(--text-3xl); font-weight: 700; color: var(--text-primary); margin: 0; }
.detalle__empresa { font-size: var(--text-sm); color: var(--text-tertiary); margin: 0; }

/* KPIs */
.detalle__kpis { display: grid; grid-template-columns: repeat(4, 1fr); gap: var(--space-4); }
.mini-kpi { background: var(--glass-bg); border: 1px solid var(--glass-border); border-radius: var(--radius-lg); padding: var(--space-4); display: flex; flex-direction: column; gap: var(--space-1); }
.mini-kpi__label { font-size: 10px; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.5px; }
.mini-kpi__value { font-family: var(--font-display); font-size: var(--text-lg); font-weight: 700; color: var(--text-primary); }
.mini-kpi__value--primary { color: var(--primary); }

/* Grid */
.detalle__grid { display: grid; grid-template-columns: 1fr 1fr; gap: var(--space-5); }
.info-card { border-radius: var(--radius-xl); padding: var(--space-5); display: flex; flex-direction: column; gap: var(--space-4); }
.info-card__title { display: flex; align-items: center; gap: var(--space-2); font-family: var(--font-display); font-size: var(--text-sm); font-weight: 600; color: var(--text-primary); margin: 0; }
.info-rows { display: flex; flex-direction: column; gap: var(--space-3); }
.info-row { display: flex; justify-content: space-between; align-items: flex-start; gap: var(--space-4); }
.info-row--full { flex-direction: column; gap: var(--space-1); }
.info-row__label { font-size: var(--text-xs); color: var(--text-muted); flex-shrink: 0; }
.info-row__value { font-size: var(--text-xs); color: var(--text-primary); font-weight: 500; text-align: right; }
.info-row--full .info-row__value { text-align: left; }
.info-row__value--comment { background: var(--bg-surface); padding: var(--space-3); border-radius: var(--radius-md); font-size: var(--text-xs); color: var(--text-secondary); line-height: 1.5; }
.text--ganada { color: var(--success); }
.text--perdida { color: var(--error); }
.text--no_concretada { color: var(--accent); }

/* Resumen visual */
.resumen-visual { display: flex; align-items: center; gap: var(--space-8); padding: var(--space-4) 0; }
.prob-ring { position: relative; display: flex; flex-direction: column; align-items: center; }
.prob-ring__svg { width: 120px; height: 120px; }
.prob-ring__text { position: absolute; top: 38px; left: 50%; transform: translateX(-50%); font-family: var(--font-display); font-size: var(--text-2xl); font-weight: 700; color: var(--text-primary); }
.prob-ring__label { font-size: 10px; color: var(--text-muted); margin-top: var(--space-1); }
.resumen-stats { display: flex; flex-direction: column; gap: var(--space-4); flex: 1; }
.resumen-stat { display: flex; flex-direction: column; }
.resumen-stat__value { font-family: var(--font-display); font-size: var(--text-lg); font-weight: 700; color: var(--text-primary); }
.resumen-stat__label { font-size: 10px; color: var(--text-muted); }

/* ==================== ACTIVIDADES ==================== */
.actividades-section { display: flex; flex-direction: column; gap: var(--space-4); }
.section-header { display: flex; justify-content: space-between; align-items: center; }
.section-title { display: flex; align-items: center; gap: var(--space-2); font-family: var(--font-display); font-size: var(--text-lg); font-weight: 700; color: var(--text-primary); margin: 0; }

.btn { display: flex; align-items: center; gap: var(--space-2); padding: var(--space-2) var(--space-5); border-radius: var(--radius-full); font-family: var(--font-body); font-size: var(--text-xs); font-weight: 600; cursor: pointer; transition: all 0.15s; border: 1px solid transparent; }
.btn--sm { padding: var(--space-2) var(--space-4); font-size: 11px; }
.btn--primary { background: var(--primary); color: #000; }
.btn--primary:hover { box-shadow: 0 0 20px var(--primary-glow); }
.btn--ghost { background: transparent; color: var(--text-secondary); border-color: var(--glass-border); }
.btn--ghost:hover { background: var(--glass-hover); }

.empty-actividades { display: flex; flex-direction: column; align-items: center; justify-content: center; gap: var(--space-3); padding: var(--space-8); border-radius: var(--radius-xl); color: var(--text-muted); font-size: var(--text-sm); }

/* Timeline */
.timeline { display: flex; flex-direction: column; gap: var(--space-4); position: relative; padding-left: var(--space-6); }
.timeline::before { content: ''; position: absolute; left: 7px; top: 8px; bottom: 8px; width: 2px; background: var(--glass-border); }

.timeline-item { position: relative; border-radius: var(--radius-lg); padding: var(--space-4); }
.timeline-dot { position: absolute; left: calc(-1 * var(--space-6) + 1px); top: var(--space-4); width: 14px; height: 14px; border-radius: 50%; border: 2px solid var(--bg-base); z-index: 1; }

.timeline-content { display: flex; flex-direction: column; gap: var(--space-3); }

.act-header { display: flex; justify-content: space-between; align-items: center; gap: var(--space-3); flex-wrap: wrap; }
.act-header__left { display: flex; align-items: center; gap: var(--space-2); }
.act-icon { color: var(--primary); }
.act-tipo { font-size: var(--text-xs); font-weight: 600; color: var(--text-primary); }
.act-duracion { font-size: 10px; color: var(--text-muted); background: var(--bg-surface); padding: 1px 6px; border-radius: var(--radius-full); }
.act-fecha { font-size: 10px; color: var(--text-muted); font-family: var(--font-mono); }

.act-notas { font-size: var(--text-xs); color: var(--text-secondary); line-height: 1.6; margin: 0; white-space: pre-wrap; }

/* Compromisos */
.compromisos { display: flex; flex-direction: column; gap: var(--space-2); padding-top: var(--space-2); border-top: 1px solid rgba(255,255,255,0.04); }
.compromisos-title { font-size: 10px; font-weight: 600; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.5px; }

.compromiso-item { display: flex; align-items: center; gap: var(--space-2); padding: var(--space-2); border-radius: var(--radius-md); transition: background 0.15s; }
.compromiso-item:hover { background: rgba(255,255,255,0.02); }

.comp-check { background: none; border: none; cursor: pointer; color: var(--text-muted); padding: 2px; border-radius: var(--radius-sm); transition: all 0.15s; display: flex; }
.comp-check:hover:not(:disabled) { color: var(--success); }
.comp-check--done { color: var(--success); }
.comp-check:disabled { cursor: default; opacity: 0.5; }

.comp-info { flex: 1; min-width: 0; }
.comp-desc { display: block; font-size: var(--text-xs); color: var(--text-primary); }
.comp-desc--done { text-decoration: line-through; color: var(--text-muted); }
.comp-fecha { font-size: 10px; color: var(--text-muted); }
.comp-vencido { color: var(--error); font-weight: 600; margin-left: 4px; }

.comp-estado { font-size: 9px; padding: 1px 6px; border-radius: var(--radius-full); font-weight: 600; flex-shrink: 0; }
.comp-estado--pendiente { background: var(--warning-soft); color: var(--warning); }
.comp-estado--en_progreso { background: var(--primary-soft); color: var(--primary); }
.comp-estado--completado { background: var(--success-soft); color: var(--success); }
.comp-estado--cancelado { background: var(--accent-soft); color: var(--accent); }

.btn-add-comp { display: flex; align-items: center; gap: 4px; background: none; border: none; color: var(--text-muted); font-family: var(--font-body); font-size: 10px; cursor: pointer; padding: var(--space-1) 0; transition: color 0.15s; }
.btn-add-comp:hover { color: var(--primary); }

@media (max-width: 900px) {
  .detalle__kpis { grid-template-columns: repeat(2, 1fr); }
  .detalle__grid { grid-template-columns: 1fr; }
}
@media (max-width: 600px) {
  .detalle__kpis { grid-template-columns: 1fr; }
}
</style>
