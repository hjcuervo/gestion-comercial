<template>
  <AppLayout>
    <div class="detalle">
      <div v-if="loading" class="loading-state"><Icon name="loader" :size="32" class="animate-spin" /><p>Cargando oportunidad...</p></div>
      <div v-else-if="error" class="error-state glass"><Icon name="alert-circle" :size="40" color="var(--error)" /><p>{{ error }}</p><button class="btn-back" @click="goBack"><Icon name="chevron-left" :size="16" /> Volver</button></div>

      <template v-else-if="op">
        <section class="detalle__topbar">
          <button class="btn-back" @click="goBack"><Icon name="chevron-left" :size="16" /> Volver</button>
          <div class="topbar-right">
            <button v-if="!isCerrada" class="btn btn--ghost btn--sm" @click="openEditModal"><Icon name="settings" :size="14" /> Editar</button>
            <span :class="['estado-badge', `estado-badge--${op.estadoMacro?.toLowerCase()}`]">{{ estadoLabel }}</span>
          </div>
        </section>

        <section class="detalle__hero">
          <div class="hero-left">
            <h1 class="detalle__nombre">{{ op.nombre }}</h1>
            <p class="detalle__empresa">{{ op.empresaNombre }}</p>
          </div>
          <div class="hero-right">
            <span class="hero-valor">{{ fmtCurrencyFull(op.valorEstimado, op.moneda) }}</span>
            <div class="hero-meta">
              <span v-if="op.probabilidad != null" class="hero-prob">{{ op.probabilidad }}% probabilidad</span>
              <span class="hero-dias">{{ diasEnPipeline }} días en pipeline</span>
            </div>
          </div>
        </section>

        <section class="detalle__info glass">
          <div class="info-grid">
            <div class="info-item"><span class="info-item__label">Tipo de Servicio</span><span class="info-item__value">{{ op.tipoServicio || '—' }}</span></div>
            <div class="info-item"><span class="info-item__label">Fuente</span><span class="info-item__value">{{ op.fuente || '—' }}</span></div>
            <div class="info-item"><span class="info-item__label">Moneda</span><span class="info-item__value">{{ op.moneda || 'COP' }}</span></div>
            <div class="info-item">
              <span class="info-item__label">Cierre Estimado</span>
              <span :class="['info-item__value', { 'text--vencido': isVencida }]">{{ fmtDate(op.fechaEstimadaCierre) || '—' }}
                <span v-if="!isCerrada && diasParaCierre !== '—'" class="info-item__sub">({{ diasParaCierre }})</span>
              </span>
            </div>
            <div class="info-item"><span class="info-item__label">Creación</span><span class="info-item__value">{{ fmtDate(op.fechaCreacion) }}</span></div>
          </div>
          <div v-if="isCerrada || isGanada" class="cierre-info">
            <div class="cierre-header">
              <Icon :name="op.estadoMacro === 'GANADA' ? 'trophy' : 'trending-down'" :size="16" :color="op.estadoMacro === 'GANADA' ? 'var(--success)' : 'var(--error)'" />
              <span class="cierre-title">Cerrada como <strong :class="`text--${op.estadoMacro?.toLowerCase()}`">{{ estadoLabel }}</strong> el {{ fmtDate(op.fechaCierre) }}</span>
            </div>
            <p v-if="op.comentarioCierre" class="cierre-comment">{{ op.comentarioCierre }}</p>
          </div>
        </section>

        <!-- PIPELINE CONTRACTUAL (solo para oportunidades GANADAS) -->
        <section v-if="isGanada" class="proceso-section">
          <div class="proceso-card glass">
            <div class="proceso-card__header">
              <div class="proceso-card__info">
                <Icon name="note-add" :size="18" color="var(--secondary)" />
                <span class="proceso-card__pipeline">Pipeline Contractual: {{ op.pipelineNombre }}</span>
              </div>
              <span class="proceso-badge proceso-badge--en_curso">En Proceso</span>
            </div>
            <div class="proceso-card__current">
              <span class="proceso-card__label">Etapa actual:</span>
              <span class="proceso-card__etapa">{{ op.etapaNombre }}</span>
            </div>
            <div class="ganada-hint">
              <Icon name="pipeline" :size="14" color="var(--text-muted)" />
              <span>Esta oportunidad está en el pipeline contractual. Gestione las etapas desde el Kanban. Puede registrar actividades, compromisos y documentos aquí.</span>
            </div>
          </div>
        </section>

        <!-- COMPROMISOS PENDIENTES -->
        <section v-if="compromisosPendientes.length" class="compromisos-section">
          <h2 class="section-title"><Icon name="clock" :size="18" color="var(--warning)" /> Compromisos Pendientes <span class="section-count">{{ compromisosPendientes.length }}</span></h2>
          <div class="compromisos-grid">
            <div v-for="comp in compromisosPendientes" :key="comp.id" class="compromiso-card glass">
              <div class="comp-card__top">
                <div class="comp-card__info">
                  <span class="comp-card__desc">{{ comp.descripcion }}</span>
                  <span class="comp-card__actividad">{{ getActividadResumen(comp.actividadId) }}</span>
                </div>
              </div>
              <div class="comp-card__bottom">
                <span :class="['comp-card__fecha', { 'text--vencido': isCompromisoVencido(comp) }]">
                  <Icon name="calendar" :size="12" /> {{ fmtDateShort(comp.fechaCompromiso) }}
                  <span v-if="isCompromisoVencido(comp)" class="tag-vencido">Vencido</span>
                </span>
                <select :value="comp.estado" @change="cambiarEstadoCompromiso(comp, $event.target.value)" :class="['comp-estado-select', `comp-estado-select--${comp.estado?.toLowerCase()}`]" :disabled="isCerrada">
                  <option value="PENDIENTE">Pendiente</option><option value="EN_PROGRESO">En Progreso</option><option value="COMPLETADO">Completado</option><option value="CANCELADO">Cancelado</option>
                </select>
              </div>
            </div>
          </div>
        </section>

        <!-- DOCUMENTOS -->
        <section class="documentos-section">
          <div class="section-header">
            <h2 class="section-title"><Icon name="external-link" :size="18" color="var(--secondary)" /> Documentos <span class="section-count">{{ documentos.length }}</span></h2>
            <button v-if="!isCerrada" class="btn btn--primary btn--sm" @click="showDocumentoModal = true"><Icon name="plus" :size="14" /> Agregar Documento</button>
          </div>
          <div v-if="loadingDocumentos" class="loading-state loading-state--sm"><Icon name="loader" :size="20" class="animate-spin" /></div>
          <div v-else-if="!documentos.length" class="empty-state glass">
            <Icon name="external-link" :size="32" color="var(--text-muted)" /><p>No hay documentos asociados</p>
            <button v-if="!isCerrada" class="btn btn--ghost btn--sm" @click="showDocumentoModal = true">Agregar primer documento</button>
          </div>
          <div v-else class="docs-list">
            <div v-for="doc in documentos" :key="doc.id" class="doc-card glass">
              <div class="doc-card__icon"><Icon name="external-link" :size="16" /></div>
              <div class="doc-card__info">
                <a :href="doc.urlStorage" target="_blank" rel="noopener" class="doc-card__name">{{ doc.nombre }}</a>
                <span class="doc-card__meta">{{ getTipoDocNombre(doc.tipoDocumentoId) }} · {{ fmtDate(doc.fechaCarga) }}</span>
              </div>
              <button v-if="!isCerrada" class="doc-card__delete" @click="eliminarDocumento(doc.id)" title="Eliminar"><Icon name="x" :size="14" /></button>
            </div>
          </div>
        </section>

        <!-- ACTIVIDADES -->
        <section class="actividades-section">
          <div class="section-header">
            <h2 class="section-title"><Icon name="calendar" :size="18" color="var(--primary)" /> Actividades <span class="section-count">{{ actividades.length }}</span></h2>
            <button v-if="!isCerrada" class="btn btn--primary btn--sm" @click="showActividadModal = true"><Icon name="plus" :size="14" /> Nueva Actividad</button>
          </div>
          <div v-if="loadingActividades" class="loading-state loading-state--sm"><Icon name="loader" :size="20" class="animate-spin" /></div>
          <div v-else-if="!actividades.length" class="empty-state glass">
            <Icon name="calendar" :size="32" color="var(--text-muted)" /><p>No hay actividades registradas</p>
            <button v-if="!isCerrada" class="btn btn--ghost btn--sm" @click="showActividadModal = true">Registrar primera actividad</button>
          </div>
          <div v-else class="actividades-list">
            <div v-for="act in actividades" :key="act.id" class="actividad-card glass">
              <div class="act-header" @click="toggleActividad(act.id)">
                <div class="act-header__left">
                  <div class="act-tipo-badge"><Icon :name="getTipoIcono(act.tipoActividadId)" :size="14" /></div>
                  <div class="act-header__info"><span class="act-tipo-name">{{ getTipoNombre(act.tipoActividadId) }}</span><span class="act-fecha">{{ fmtDate(act.fechaHora) }}</span></div>
                </div>
                <div class="act-header__right">
                  <span v-if="getActPendingCount(act)" class="act-comp-count">{{ getActPendingCount(act) }} pendientes</span>
                  <Icon :name="expandedActs.has(act.id) ? 'chevron-down' : 'chevron-right'" :size="16" class="act-chevron" />
                </div>
              </div>
              <div v-if="expandedActs.has(act.id)" class="act-body">
                <p v-if="act.notas" class="act-notas">{{ act.notas }}</p>
                <p v-else class="act-notas act-notas--empty">Sin notas</p>
                <div v-if="act.compromisos?.length" class="act-compromisos">
                  <span class="act-compromisos__title">Compromisos</span>
                  <div v-for="comp in act.compromisos" :key="comp.id" class="comp-row">
                    <span :class="['comp-row__desc', { 'comp-row__desc--done': comp.estado === 'COMPLETADO' || comp.estado === 'CANCELADO' }]">{{ comp.descripcion }}</span>
                    <span :class="['comp-row__fecha', { 'text--vencido': isCompromisoVencido(comp) }]">{{ fmtDateShort(comp.fechaCompromiso) }}</span>
                    <select :value="comp.estado" @change="cambiarEstadoCompromiso(comp, $event.target.value)" :class="['comp-estado-select comp-estado-select--sm', `comp-estado-select--${comp.estado?.toLowerCase()}`]" :disabled="isCerrada">
                      <option value="PENDIENTE">Pendiente</option><option value="EN_PROGRESO">En Progreso</option><option value="COMPLETADO">Completado</option><option value="CANCELADO">Cancelado</option>
                    </select>
                  </div>
                </div>
                <button v-if="!isCerrada" class="btn-add-comp" @click.stop="openCompromisoModal(act.id)"><Icon name="plus" :size="12" /> Agregar compromiso</button>
              </div>
            </div>
          </div>
        </section>
      </template>

      <ActividadModal v-if="showActividadModal" :oportunidad-id="Number(op.id)" @close="showActividadModal = false" @created="onActividadCreated" />
      <CompromisoModal v-if="showCompromisoModal" :actividad-id="selectedActividadId" @close="showCompromisoModal = false" @created="onCompromisoCreated" />
      <DocumentoModal v-if="showDocumentoModal" :oportunidad-id="Number(op.id)" @close="showDocumentoModal = false" @created="onDocumentoCreated" />
      <OportunidadModal :visible="showEditModal" :oportunidad="op" :empresas="empresasEdit" :pipelines="[]" :saving="savingEdit" :error="editError" @close="showEditModal = false" @submit="handleEditSubmit" />
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
import DocumentoModal from '@/components/documento/DocumentoModal.vue';
import OportunidadModal from '@/components/oportunidad/OportunidadModal.vue';
import { oportunidadService } from '@/services/oportunidad.service';
import { actividadService } from '@/services/actividad.service';
import { documentoService } from '@/services/documento.service';
import { empresaService } from '@/services/empresa.service';
import { formatCurrencyFull } from '@/utils/currency';

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
const expandedActs = ref(new Set());

// Documentos
const documentos = ref([]);
const loadingDocumentos = ref(false);
const tiposDocumento = ref([]);
const showDocumentoModal = ref(false);

// Editar oportunidad
const showEditModal = ref(false);
const savingEdit = ref(false);
const editError = ref(null);
const empresasEdit = ref([]);

onMounted(async () => {
  try { op.value = await oportunidadService.obtenerPorId(route.params.id); }
  catch (err) { error.value = err.response?.data?.message || 'No se pudo cargar la oportunidad'; }
  finally { loading.value = false; }

  try { tiposActividad.value = await actividadService.listarTipos(); } catch {}
  try { tiposDocumento.value = await documentoService.listarTipos(); } catch {}

  if (op.value) {
    await Promise.all([loadActividades(), loadDocumentos()]);
  }
});

// === Actividades ===
async function loadActividades() {
  loadingActividades.value = true;
  try { const res = await actividadService.listarPorOportunidad(op.value.id); actividades.value = res.data || res || []; expandedActs.value = new Set(actividades.value.map(a => a.id)); }
  catch (err) { console.error('Error cargando actividades:', err); }
  finally { loadingActividades.value = false; }
}

const compromisosPendientes = computed(() => {
  const pending = [];
  for (const act of actividades.value) {
    if (act.compromisos) {
      for (const comp of act.compromisos) {
        if (comp.estado === 'PENDIENTE' || comp.estado === 'EN_PROGRESO') pending.push({ ...comp, actividadId: act.id });
      }
    }
  }
  return pending.sort((a, b) => new Date(a.fechaCompromiso) - new Date(b.fechaCompromiso));
});

function getActPendingCount(act) { return act.compromisos ? act.compromisos.filter(c => c.estado === 'PENDIENTE' || c.estado === 'EN_PROGRESO').length : 0; }
function getActividadResumen(actividadId) { const act = actividades.value.find(a => a.id === actividadId); return act ? `${getTipoNombre(act.tipoActividadId)} — ${fmtDateShort(act.fechaHora)}` : ''; }
function getTipoNombre(tipoId) { const t = tiposActividad.value.find(t => t.id === tipoId); return t ? t.nombre : `Tipo ${tipoId}`; }
function getTipoIcono(tipoId) { const t = tiposActividad.value.find(t => t.id === tipoId); return t?.icono || 'note-add'; }
function toggleActividad(id) { expandedActs.value.has(id) ? expandedActs.value.delete(id) : expandedActs.value.add(id); expandedActs.value = new Set(expandedActs.value); }
function openCompromisoModal(actId) { selectedActividadId.value = actId; showCompromisoModal.value = true; }
async function onActividadCreated() { showActividadModal.value = false; await loadActividades(); }
async function onCompromisoCreated() { showCompromisoModal.value = false; await loadActividades(); }
async function cambiarEstadoCompromiso(comp, nuevoEstado) { if (isCerrada.value || comp.estado === nuevoEstado) return; try { await actividadService.actualizarCompromiso(comp.id, { estado: nuevoEstado }); await loadActividades(); } catch {} }
function isCompromisoVencido(comp) { if (comp.estado === 'COMPLETADO' || comp.estado === 'CANCELADO') return false; return new Date(comp.fechaCompromiso) < new Date(); }

// === Documentos ===
async function loadDocumentos() {
  loadingDocumentos.value = true;
  try { documentos.value = await documentoService.listarPorOportunidad(op.value.id); }
  catch (err) { console.error('Error cargando documentos:', err); }
  finally { loadingDocumentos.value = false; }
}

function getTipoDocNombre(tipoId) { const t = tiposDocumento.value.find(t => t.id === tipoId); return t ? t.nombre : 'Documento'; }
async function onDocumentoCreated() { showDocumentoModal.value = false; await loadDocumentos(); }
async function eliminarDocumento(id) { if (!confirm('¿Eliminar este documento?')) return; try { await documentoService.eliminar(id); await loadDocumentos(); } catch {} }

// === Editar Oportunidad ===
async function openEditModal() {
  editError.value = null;
  try { const res = await empresaService.listar({ page_size: 100 }); empresasEdit.value = res.data || []; } catch {}
  showEditModal.value = true;
}

async function handleEditSubmit(payload) {
  savingEdit.value = true;
  editError.value = null;
  try {
    await oportunidadService.actualizar(op.value.id, payload);
    showEditModal.value = false;
    op.value = await oportunidadService.obtenerPorId(route.params.id);
  } catch (err) {
    editError.value = err.response?.data?.message || 'Error al actualizar oportunidad';
  } finally { savingEdit.value = false; }
}

// === Computed ===
const isCerrada = computed(() => { const e = op.value?.estadoMacro; return e === 'PERDIDA' || e === 'NO_CONCRETADA'; });
const isGanada = computed(() => op.value?.estadoMacro === 'GANADA');
const estadoLabel = computed(() => ({ ABIERTA: 'Abierta', SEGUIMIENTO: 'Seguimiento', GANADA: 'Ganada', PERDIDA: 'Perdida', NO_CONCRETADA: 'No Concretada' }[op.value?.estadoMacro] || op.value?.estadoMacro));
const diasEnPipeline = computed(() => { if (!op.value?.fechaCreacion) return 0; return Math.floor((new Date() - new Date(op.value.fechaCreacion)) / 86400000); });
const diasParaCierre = computed(() => { if (!op.value?.fechaEstimadaCierre) return '—'; const d = Math.floor((new Date(op.value.fechaEstimadaCierre) - new Date()) / 86400000); return d >= 0 ? `${d} días` : `${Math.abs(d)} días vencido`; });
const isVencida = computed(() => { if (isCerrada.value || !op.value?.fechaEstimadaCierre) return false; return new Date(op.value.fechaEstimadaCierre) < new Date(); });

function goBack() { router.back(); }
function fmtCurrencyFull(v, m) { return formatCurrencyFull(v, m); }
function fmtDate(dt) { if (!dt) return null; return new Date(dt).toLocaleDateString('es-CO', { day: 'numeric', month: 'long', year: 'numeric' }); }
function fmtDateShort(d) { if (!d) return '—'; return new Date(d).toLocaleDateString('es-CO', { day: '2-digit', month: 'short' }); }
</script>

<style scoped>
.detalle { display: flex; flex-direction: column; gap: var(--space-5); padding-bottom: var(--space-8); }
.loading-state, .error-state { display: flex; flex-direction: column; align-items: center; justify-content: center; gap: var(--space-3); padding: var(--space-8); color: var(--text-tertiary); font-size: var(--text-sm); }
.loading-state--sm { padding: var(--space-4); }
.error-state { border-radius: var(--radius-xl); padding: var(--space-10); }

.detalle__topbar { display: flex; justify-content: space-between; align-items: center; }
.topbar-right { display: flex; align-items: center; gap: var(--space-3); }
.btn-back { display: flex; align-items: center; gap: var(--space-2); background: var(--glass-bg); border: 1px solid var(--glass-border); border-radius: var(--radius-full); padding: var(--space-2) var(--space-4); color: var(--text-secondary); font-family: var(--font-body); font-size: var(--text-xs); cursor: pointer; transition: all 0.15s; }
.btn-back:hover { background: var(--glass-hover); color: var(--text-primary); border-color: var(--primary); }
.estado-badge { padding: var(--space-2) var(--space-4); border-radius: var(--radius-full); font-size: var(--text-xs); font-weight: 600; text-transform: uppercase; letter-spacing: 0.5px; }
.estado-badge--abierta { background: var(--primary-soft); color: var(--primary); }
.estado-badge--seguimiento { background: var(--warning-soft); color: var(--warning); }
.estado-badge--ganada { background: var(--success-soft); color: var(--success); }
.estado-badge--perdida { background: var(--error-soft); color: var(--error); }
.estado-badge--no_concretada { background: var(--accent-soft); color: var(--accent); }

.detalle__hero { display: flex; justify-content: space-between; align-items: flex-start; gap: var(--space-6); flex-wrap: wrap; }
.detalle__nombre { font-family: var(--font-display); font-size: var(--text-3xl); font-weight: 700; color: var(--text-primary); margin: 0; }
.detalle__empresa { font-size: var(--text-sm); color: var(--text-tertiary); margin: var(--space-1) 0 0; }
.hero-right { text-align: right; }
.hero-valor { font-family: var(--font-display); font-size: var(--text-2xl); font-weight: 700; color: var(--primary); }
.hero-meta { display: flex; gap: var(--space-4); margin-top: var(--space-1); }
.hero-prob, .hero-dias { font-size: var(--text-xs); color: var(--text-muted); }

.detalle__info { border-radius: var(--radius-xl); padding: var(--space-5); }
.info-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(160px, 1fr)); gap: var(--space-4); }
.info-item { display: flex; flex-direction: column; gap: 2px; }
.info-item__label { font-size: 10px; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.5px; }
.info-item__value { font-size: var(--text-xs); color: var(--text-primary); font-weight: 500; }
.info-item__sub { color: var(--text-muted); font-weight: 400; }
.text--vencido { color: var(--error); }
.cierre-info { margin-top: var(--space-4); padding-top: var(--space-4); border-top: 1px solid var(--glass-border); }
.cierre-header { display: flex; align-items: center; gap: var(--space-2); font-size: var(--text-xs); color: var(--text-secondary); }
.cierre-comment { margin: var(--space-3) 0 0; font-size: var(--text-xs); color: var(--text-secondary); background: var(--bg-surface); padding: var(--space-3); border-radius: var(--radius-md); line-height: 1.5; }
.text--ganada { color: var(--success); } .text--perdida { color: var(--error); } .text--no_concretada { color: var(--accent); }

/* Sections shared */
.section-header { display: flex; justify-content: space-between; align-items: center; }
.section-title { display: flex; align-items: center; gap: var(--space-2); font-family: var(--font-display); font-size: var(--text-lg); font-weight: 700; color: var(--text-primary); margin: 0; }
.section-count { font-size: var(--text-xs); color: var(--text-muted); font-weight: 400; background: var(--bg-surface); padding: 1px 8px; border-radius: var(--radius-full); margin-left: var(--space-1); }
.btn { display: flex; align-items: center; gap: var(--space-2); padding: var(--space-2) var(--space-5); border-radius: var(--radius-full); font-family: var(--font-body); font-size: var(--text-xs); font-weight: 600; cursor: pointer; transition: all 0.15s; border: 1px solid transparent; }
.btn--sm { padding: var(--space-2) var(--space-4); font-size: 11px; }
.btn--primary { background: var(--primary); color: #000; } .btn--primary:hover { box-shadow: 0 0 20px var(--primary-glow); }
.btn--ghost { background: transparent; color: var(--text-secondary); border-color: var(--glass-border); } .btn--ghost:hover { background: var(--glass-hover); }
.empty-state { display: flex; flex-direction: column; align-items: center; justify-content: center; gap: var(--space-3); padding: var(--space-8); border-radius: var(--radius-xl); color: var(--text-muted); font-size: var(--text-sm); }

/* Compromisos pendientes */
.compromisos-section { display: flex; flex-direction: column; gap: var(--space-4); }
.compromisos-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(320px, 1fr)); gap: var(--space-3); }
.compromiso-card { border-radius: var(--radius-lg); padding: var(--space-4); display: flex; flex-direction: column; gap: var(--space-3); }
.comp-card__top { display: flex; gap: var(--space-3); }
.comp-card__info { flex: 1; min-width: 0; }
.comp-card__desc { display: block; font-size: var(--text-xs); font-weight: 600; color: var(--text-primary); }
.comp-card__actividad { display: block; font-size: 10px; color: var(--text-muted); margin-top: 2px; }
.comp-card__bottom { display: flex; justify-content: space-between; align-items: center; }
.comp-card__fecha { display: flex; align-items: center; gap: 4px; font-size: 10px; color: var(--text-muted); }
.tag-vencido { background: var(--error-soft); color: var(--error); padding: 0 4px; border-radius: var(--radius-full); font-size: 9px; font-weight: 600; }

.comp-estado-select { appearance: none; border: 1px solid var(--glass-border); border-radius: var(--radius-full); font-family: var(--font-body); font-size: 10px; font-weight: 600; padding: 2px 22px 2px 8px; cursor: pointer; background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='10' height='10' viewBox='0 0 24 24' fill='none' stroke='rgba(255,255,255,0.3)' stroke-width='2'%3E%3Cpath d='M6 9l6 6 6-6'/%3E%3C/svg%3E"); background-repeat: no-repeat; background-position: right 6px center; }
.comp-estado-select option { background: var(--bg-elevated); }
.comp-estado-select:focus { outline: none; border-color: var(--primary); }
.comp-estado-select:disabled { opacity: 0.5; cursor: not-allowed; }
.comp-estado-select--pendiente { background: var(--warning-soft); color: var(--warning); border-color: rgba(245,158,11,0.3); }
.comp-estado-select--en_progreso { background: var(--primary-soft); color: var(--primary); border-color: rgba(0,212,255,0.3); }
.comp-estado-select--completado { background: var(--success-soft); color: var(--success); border-color: rgba(16,185,129,0.3); }
.comp-estado-select--cancelado { background: var(--accent-soft); color: var(--accent); border-color: rgba(255,107,107,0.3); }
.comp-estado-select--sm { font-size: 9px; padding: 1px 20px 1px 6px; }

/* Documentos */
.documentos-section { display: flex; flex-direction: column; gap: var(--space-4); }

/* Proceso de Contratación */
.proceso-section { display: flex; flex-direction: column; gap: var(--space-4); }
.procesos-list { display: flex; flex-direction: column; gap: var(--space-3); }
.proceso-card { border-radius: var(--radius-lg); padding: var(--space-4); display: flex; flex-direction: column; gap: var(--space-3); }
.proceso-card__header { display: flex; justify-content: space-between; align-items: center; }
.proceso-card__info { display: flex; align-items: center; gap: var(--space-2); }
.proceso-card__pipeline { font-size: var(--text-xs); font-weight: 600; color: var(--text-primary); }
.proceso-card__fecha { font-size: 10px; color: var(--text-muted); }
.proceso-badge { display: inline-flex; padding: 1px 8px; border-radius: var(--radius-full); font-size: 10px; font-weight: 600; text-transform: uppercase; }
.proceso-badge--sm { font-size: 9px; padding: 0 6px; }
.proceso-badge--en_curso { background: var(--primary-soft); color: var(--primary); }
.proceso-badge--completado { background: var(--success-soft); color: var(--success); }
.proceso-badge--cancelado { background: var(--error-soft); color: var(--error); }
.proceso-badge--vigente { background: var(--success-soft); color: var(--success); }
.proceso-badge--suspendido { background: var(--warning-soft); color: var(--warning); }
.proceso-badge--terminado { background: var(--accent-soft); color: var(--accent); }

.proceso-pipeline { display: flex; align-items: center; gap: 2px; overflow-x: auto; padding: var(--space-2) 0; }
.proceso-etapa { display: flex; align-items: center; gap: var(--space-2); padding: var(--space-2) var(--space-3); border-radius: var(--radius-full); cursor: pointer; transition: all 0.15s; flex-shrink: 0; background: var(--bg-surface); border: 1px solid var(--glass-border); }
.proceso-etapa:hover { border-color: var(--primary); }
.proceso-etapa--active { background: var(--primary-soft); border-color: var(--primary); }
.proceso-etapa--done { background: rgba(16,185,129,0.1); border-color: rgba(16,185,129,0.3); }
.proceso-etapa__dot { width: 8px; height: 8px; border-radius: 50%; background: var(--text-muted); flex-shrink: 0; }
.proceso-etapa--active .proceso-etapa__dot { background: var(--primary); box-shadow: 0 0 6px var(--primary); }
.proceso-etapa--done .proceso-etapa__dot { background: var(--success); }
.proceso-etapa__name { font-size: 10px; font-weight: 500; color: var(--text-secondary); white-space: nowrap; }
.proceso-etapa--active .proceso-etapa__name { color: var(--primary); font-weight: 600; }

.proceso-card__current { display: flex; align-items: center; gap: var(--space-2); }
.proceso-card__label { font-size: 10px; color: var(--text-muted); }
.proceso-card__etapa { font-size: var(--text-xs); font-weight: 600; }
.proceso-card__actions { display: flex; gap: var(--space-2); }

.ganada-hint { display: flex; align-items: flex-start; gap: var(--space-2); font-size: var(--text-xs); color: var(--text-muted); line-height: 1.5; padding-top: var(--space-2); border-top: 1px solid var(--glass-border); }

.btn--secondary { background: var(--secondary-soft); color: var(--secondary); border-color: rgba(168,85,247,0.3); }
.btn--secondary:hover { box-shadow: 0 0 15px rgba(168,85,247,0.2); }
.docs-list { display: flex; flex-direction: column; gap: var(--space-2); }
.doc-card { display: flex; align-items: center; gap: var(--space-3); padding: var(--space-3) var(--space-4); border-radius: var(--radius-lg); transition: background 0.15s; }
.doc-card:hover { background: rgba(255,255,255,0.02); }
.doc-card__icon { width: 32px; height: 32px; border-radius: var(--radius-md); background: var(--secondary-soft); color: var(--secondary); display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.doc-card__info { flex: 1; min-width: 0; }
.doc-card__name { display: block; font-size: var(--text-xs); font-weight: 600; color: var(--primary); text-decoration: none; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.doc-card__name:hover { text-decoration: underline; }
.doc-card__meta { display: block; font-size: 10px; color: var(--text-muted); margin-top: 1px; }
.doc-card__delete { background: none; border: none; color: var(--text-muted); cursor: pointer; padding: 4px; border-radius: var(--radius-sm); transition: all 0.15s; display: flex; flex-shrink: 0; }
.doc-card__delete:hover { color: var(--error); background: var(--error-soft); }

/* Actividades */
.actividades-section { display: flex; flex-direction: column; gap: var(--space-4); }
.actividades-list { display: flex; flex-direction: column; gap: var(--space-3); }
.actividad-card { border-radius: var(--radius-lg); overflow: hidden; }
.act-header { display: flex; justify-content: space-between; align-items: center; padding: var(--space-4); cursor: pointer; transition: background 0.15s; }
.act-header:hover { background: rgba(255,255,255,0.02); }
.act-header__left { display: flex; align-items: center; gap: var(--space-3); }
.act-tipo-badge { width: 32px; height: 32px; border-radius: var(--radius-md); background: var(--primary-soft); color: var(--primary); display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.act-header__info { display: flex; flex-direction: column; }
.act-tipo-name { font-size: var(--text-xs); font-weight: 600; color: var(--text-primary); }
.act-fecha { font-size: 10px; color: var(--text-muted); }
.act-header__right { display: flex; align-items: center; gap: var(--space-3); }
.act-comp-count { font-size: 10px; color: var(--warning); background: var(--warning-soft); padding: 1px 8px; border-radius: var(--radius-full); font-weight: 600; }
.act-chevron { color: var(--text-muted); }
.act-body { padding: 0 var(--space-4) var(--space-4); display: flex; flex-direction: column; gap: var(--space-3); border-top: 1px solid rgba(255,255,255,0.04); }
.act-notas { font-size: var(--text-xs); color: var(--text-secondary); line-height: 1.6; margin: var(--space-3) 0 0; white-space: pre-wrap; }
.act-notas--empty { color: var(--text-muted); font-style: italic; }
.act-compromisos { display: flex; flex-direction: column; gap: var(--space-2); }
.act-compromisos__title { font-size: 10px; font-weight: 600; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.5px; }
.comp-row { display: flex; align-items: center; gap: var(--space-2); padding: var(--space-2) var(--space-1); border-radius: var(--radius-sm); }
.comp-row:hover { background: rgba(255,255,255,0.02); }
.comp-row__desc { flex: 1; font-size: var(--text-xs); color: var(--text-primary); min-width: 0; }
.comp-row__desc--done { text-decoration: line-through; color: var(--text-muted); }
.comp-row__fecha { font-size: 10px; color: var(--text-muted); flex-shrink: 0; }
.btn-add-comp { display: flex; align-items: center; gap: 4px; background: none; border: none; color: var(--text-muted); font-family: var(--font-body); font-size: 10px; cursor: pointer; padding: var(--space-1) 0; transition: color 0.15s; }
.btn-add-comp:hover { color: var(--primary); }

@media (max-width: 768px) { .detalle__hero { flex-direction: column; } .hero-right { text-align: left; } .compromisos-grid { grid-template-columns: 1fr; } }
</style>
