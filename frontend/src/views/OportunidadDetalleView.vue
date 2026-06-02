<template>
  <div class="opd">
    <!-- Captura en el panel contextual (solo si no está cerrada) -->
    <Teleport to="#gc-shell-aside">
      <CapturaActividadPanel
        v-if="op && !isCerrada"
        :oportunidad-id="op.id"
        :tipos="tiposActividad"
        @creado="onActividadCreada"
      />
      <div v-else-if="op" class="opd__aside-readonly">
        <GcEmpty icon="lock" message="Oportunidad cerrada — solo lectura" />
      </div>
    </Teleport>

    <div v-if="loading" class="opd__state"><GcSpinner :size="24" /></div>

    <div v-else-if="error" class="opd__error">
      <GcIcon name="alert-circle" :size="28" color="var(--gc-danger)" />
      <p>{{ error }}</p>
      <GcButton variant="default" icon="arrow-left" @click="goBack">Volver</GcButton>
    </div>

    <template v-else-if="op">
      <!-- Encabezado -->
      <header class="opd__head">
        <div class="opd__heading">
          <button class="opd__back" @click="goBack"><GcIcon name="arrow-left" :size="16" /></button>
          <h1 class="opd__title">{{ op.nombre }}</h1>
          <GcBadge :tone="estadoTone" :label="estadoLabel" />
        </div>
        <GcButton v-if="!isCerrada" variant="default" size="sm" icon="edit" @click="openEdit">Editar</GcButton>
      </header>
      <p class="opd__sub">
        {{ op.empresaNombre }}
        <template v-if="op.etapaNombre"> · {{ op.etapaNombre }}</template>
        · <span class="gc-mono">{{ fmtCurrencyFull(op.valorEstimado, op.moneda) }}</span>
      </p>

      <!-- Tabs -->
      <nav class="opd__tabs">
        <button v-for="t in tabs" :key="t.key" class="opd__tab" :class="{ 'opd__tab--active': tab === t.key }" @click="tab = t.key">
          {{ t.label }}
        </button>
      </nav>

      <!-- ===== TAB RESUMEN ===== -->
      <section v-show="tab === 'resumen'" class="opd__panel">
        <GcStatStrip>
          <GcStat label="Valor estimado" :value="fmtCurrency(op.valorEstimado, op.moneda)" />
          <GcStat label="Probabilidad" :value="op.probabilidad != null ? op.probabilidad + '%' : '—'" />
          <GcStat label="Días en pipeline" :value="diasEnPipeline" />
          <GcStat label="Cierre estimado" :value="fmtDate(op.fechaEstimadaCierre) || '—'" />
        </GcStatStrip>

        <div class="opd__grid">
          <div class="opd__field"><span class="opd__label">Tipo de servicio</span><span class="opd__value">{{ op.tipoServicio || '—' }}</span></div>
          <div class="opd__field"><span class="opd__label">Fuente</span><span class="opd__value">{{ op.fuente || '—' }}</span></div>
          <div class="opd__field"><span class="opd__label">Moneda</span><span class="opd__value gc-mono">{{ op.moneda || 'COP' }}</span></div>
          <div class="opd__field"><span class="opd__label">Creación</span><span class="opd__value gc-mono">{{ fmtDate(op.fechaCreacion) }}</span></div>
        </div>

        <div v-if="isCerrada" class="opd__cierre">
          <GcIcon name="circle-x" :size="16" color="var(--gc-danger)" />
          <span>Cerrada como <strong>{{ estadoLabel }}</strong> el <span class="gc-mono">{{ fmtDate(op.fechaCierre) }}</span></span>
          <p v-if="op.comentarioCierre" class="opd__cierre-coment">{{ op.comentarioCierre }}</p>
        </div>

        <!-- Documentos -->
        <div class="opd__sec">
          <div class="opd__sechead">
            <h2 class="opd__h2">Documentos</h2>
            <GcButton v-if="!isCerrada" variant="default" size="sm" icon="plus" @click="showDocumentoModal = true">Agregar</GcButton>
          </div>
          <div v-if="loadingDocs" class="opd__state"><GcSpinner :size="20" /></div>
          <GcEmpty v-else-if="!documentos.length" icon="file-off" message="Sin documentos asociados" />
          <div v-else class="opd__list">
            <GcListRow v-for="d in documentos" :key="d.id" tone="info">
              <template #lead><GcIcon name="external-link" :size="16" /></template>
              <div class="opd__docmain">
                <a :href="d.urlStorage" target="_blank" rel="noopener" class="opd__docname">{{ d.nombre }}</a>
                <span class="opd__docmeta gc-mono">{{ tipoDocNombre(d.tipoDocumentoId) }} · {{ fmtDate(d.fechaCarga) }}</span>
              </div>
              <template #actions>
                <GcButton v-if="!isCerrada" variant="ghost" size="sm" icon="trash" @click="eliminarDocumento(d.id)" />
              </template>
            </GcListRow>
          </div>
        </div>
      </section>

      <!-- ===== TAB ACTIVIDADES ===== -->
      <section v-show="tab === 'actividades'" class="opd__panel">
        <!-- Compromisos agrupados -->
        <div class="opd__sec">
          <h2 class="opd__h2">Compromisos</h2>
          <GcEmpty v-if="!compromisos.length" icon="checklist" message="Sin compromisos registrados" />
          <template v-else>
            <CompromisoGroup
              v-for="g in grupos"
              :key="g.key"
              :titulo="g.titulo"
              :tone="g.tone"
              :items="g.items"
              @completar="completarCompromiso"
            />
          </template>
        </div>

        <!-- Bitácora -->
        <div class="opd__sec">
          <h2 class="opd__h2">Bitácora</h2>
          <div v-if="loadingActs" class="opd__state"><GcSpinner :size="20" /></div>
          <GcEmpty v-else-if="!actividades.length" icon="notes" message="Sin actividades registradas" />
          <div v-else class="opd__list">
            <GcListRow v-for="a in actividadesOrdenadas" :key="a.id" tone="info">
              <template #lead><GcIcon :name="tipoIcono(a.tipoActividadId)" :size="16" /></template>
              <div class="opd__actmain">
                <span class="opd__acttipo">{{ tipoNombre(a.tipoActividadId) }}</span>
                <span class="opd__actfecha gc-mono">{{ fmtDateTime(a.fechaHora) }}<template v-if="a.duracionMinutos"> · {{ a.duracionMinutos }} min</template></span>
                <p v-if="a.notas" class="opd__actnotas">{{ a.notas }}</p>
              </div>
            </GcListRow>
          </div>
        </div>
      </section>

      <!-- ===== TAB PROCESO CONTRACTUAL ===== -->
      <section v-show="tab === 'proceso'" class="opd__panel">
        <div v-if="isGanada" class="opd__proceso">
          <div class="opd__procesohead">
            <h2 class="opd__h2">Pipeline contractual: {{ op.pipelineNombre }}</h2>
            <GcBadge tone="info" label="En proceso" />
          </div>
          <p class="opd__procesoetapa">Etapa actual: <strong>{{ op.etapaNombre }}</strong></p>
          <p class="opd__procesohint">Gestione las etapas desde el Kanban. Cuando esté listo, formalice el contrato para completar el proceso.</p>
          <GcButton variant="primary" icon="file-plus" @click="showFormalizarModal = true">Formalizar contrato</GcButton>
        </div>

        <div v-else-if="isContratada" class="opd__proceso">
          <div class="opd__procesohead">
            <h2 class="opd__h2">Contrato formalizado</h2>
            <GcBadge tone="accent" label="Contratada" />
          </div>
          <p class="opd__procesohint">Esta oportunidad fue formalizada como contrato. Gestiónelo desde el módulo de Contratos.</p>
          <GcButton variant="default" icon="arrow-right" @click="$router.push('/contratos')">Ir a Contratos</GcButton>
        </div>

        <GcEmpty v-else icon="file-search" message="El proceso contractual aparece cuando la oportunidad es GANADA" />
      </section>
    </template>

    <!-- Modales (legacy temporalmente; se reescriben en RF3-B) -->
    <DocumentoModal v-if="showDocumentoModal && op" :oportunidad-id="Number(op.id)" @close="showDocumentoModal = false" @created="onDocumentoCreado" />
    <OportunidadModal :visible="showEditModal" :oportunidad="op" :empresas="empresasEdit" :pipelines="[]" :saving="savingEdit" :error="editError" @close="showEditModal = false" @submit="handleEditSubmit" />
    <FormalizarContratoModal v-if="showFormalizarModal && op" :oportunidad-id="Number(op.id)" :oportunidad-nombre="op.nombre" :moneda-default="op.moneda || 'COP'" :valor-default="op.valorEstimado" @close="showFormalizarModal = false" @created="onContratoCreado" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useShell } from '@/composables/useShell';
import { oportunidadService } from '@/services/oportunidad.service';
import { actividadService } from '@/services/actividad.service';
import { documentoService } from '@/services/documento.service';
import { empresaService } from '@/services/empresa.service';
import { formatCurrency as fmtCurrency, formatCurrencyFull as fmtCurrencyFull } from '@/utils/currency';
import { formatDate as fmtDate, formatDateTime as fmtDateTime } from '@/utils/datetime';
import CapturaActividadPanel from '@/components/actividad/CapturaActividadPanel.vue';
import CompromisoGroup from '@/components/actividad/CompromisoGroup.vue';
import DocumentoModal from '@/components/documento/DocumentoModal.vue';
import OportunidadModal from '@/components/oportunidad/OportunidadModal.vue';
import FormalizarContratoModal from '@/components/contrato/FormalizarContratoModal.vue';
import GcButton from '@/components/ui/GcButton.vue';
import GcBadge from '@/components/ui/GcBadge.vue';
import GcListRow from '@/components/ui/GcListRow.vue';
import GcStat from '@/components/ui/GcStat.vue';
import GcStatStrip from '@/components/ui/GcStatStrip.vue';
import GcEmpty from '@/components/ui/GcEmpty.vue';
import GcSpinner from '@/components/ui/GcSpinner.vue';
import GcIcon from '@/components/ui/GcIcon.vue';

const route = useRoute();
const router = useRouter();
const { setRegions } = useShell();

// Funciona tanto en /oportunidades/:id como en /actividades/:oportunidadId
const opId = computed(() => route.params.id || route.params.oportunidadId);

const op = ref(null);
const loading = ref(true);
const error = ref(null);
const tab = ref('resumen');

const tabs = [
  { key: 'resumen', label: 'Resumen' },
  { key: 'actividades', label: 'Actividades' },
  { key: 'proceso', label: 'Proceso contractual' },
];

const actividades = ref([]);
const loadingActs = ref(false);
const tiposActividad = ref([]);
const documentos = ref([]);
const loadingDocs = ref(false);
const tiposDocumento = ref([]);

const showDocumentoModal = ref(false);
const showEditModal = ref(false);
const showFormalizarModal = ref(false);
const savingEdit = ref(false);
const editError = ref(null);
const empresasEdit = ref([]);

// --- Estado ---
const isCerrada = computed(() => ['PERDIDA', 'NO_CONCRETADA'].includes(op.value?.estadoMacro));
const isGanada = computed(() => op.value?.estadoMacro === 'GANADA');
const isContratada = computed(() => op.value?.estadoMacro === 'CONTRATADA');
const LABEL = { ABIERTA: 'Abierta', SEGUIMIENTO: 'Seguimiento', GANADA: 'Ganada', CONTRATADA: 'Contratada', PERDIDA: 'Perdida', NO_CONCRETADA: 'No concretada' };
const TONE = { ABIERTA: 'info', SEGUIMIENTO: 'warning', GANADA: 'success', CONTRATADA: 'accent', PERDIDA: 'danger', NO_CONCRETADA: 'neutral' };
const estadoLabel = computed(() => LABEL[op.value?.estadoMacro] || op.value?.estadoMacro || '—');
const estadoTone = computed(() => TONE[op.value?.estadoMacro] || 'neutral');
const diasEnPipeline = computed(() => (op.value?.fechaCreacion ? Math.floor((Date.now() - new Date(op.value.fechaCreacion)) / 86400000) + ' días' : '—'));

// --- Tipos / mapas ---
const tipoActMap = computed(() => Object.fromEntries(tiposActividad.value.map((t) => [t.id, t])));
function tipoNombre(id) { return tipoActMap.value[id]?.nombre || 'Actividad'; }
function tipoIcono(id) { return tipoActMap.value[id]?.icono || 'point'; }
const tipoDocMap = computed(() => Object.fromEntries(tiposDocumento.value.map((t) => [t.id, t.nombre])));
function tipoDocNombre(id) { return tipoDocMap.value[id] || 'Documento'; }

const actividadesOrdenadas = computed(() => [...actividades.value].sort((a, b) => new Date(b.fechaHora) - new Date(a.fechaHora)));

// --- Compromisos agrupados ---
const compromisos = computed(() => {
  const out = [];
  actividades.value.forEach((a) => (a.compromisos || []).forEach((c) => out.push(c)));
  return out;
});
function parseLocalDate(iso) { if (!iso) return null; const [y, m, d] = iso.split('-').map(Number); return new Date(y, (m || 1) - 1, d || 1); }
function isCerrado(e) { return e === 'COMPLETADO' || e === 'CANCELADO'; }
const grupos = computed(() => {
  const today = new Date(); today.setHours(0, 0, 0, 0);
  const v = [], p = [], c = [];
  compromisos.value.forEach((x) => {
    if (isCerrado(x.estado)) { c.push(x); return; }
    const f = parseLocalDate(x.fechaCompromiso);
    if (f && f < today) v.push(x); else p.push(x);
  });
  const by = (a, b) => new Date(a.fechaCompromiso) - new Date(b.fechaCompromiso);
  v.sort(by); p.sort(by); c.sort(by);
  return [
    { key: 'v', titulo: 'Vencidas', tone: 'danger', items: v },
    { key: 'p', titulo: 'Próximas', tone: 'warning', items: p },
    { key: 'c', titulo: 'Completadas', tone: 'success', items: c },
  ].filter((g) => g.items.length);
});

// --- Carga ---
async function cargarOportunidad() {
  loading.value = true;
  error.value = null;
  try { op.value = await oportunidadService.obtenerPorId(opId.value); }
  catch (err) { error.value = err.response?.data?.message || 'No se pudo cargar la oportunidad'; }
  finally { loading.value = false; }
}
async function cargarActividades() {
  if (!op.value) return;
  loadingActs.value = true;
  try { const res = await actividadService.listarPorOportunidad(op.value.id); actividades.value = res.data || []; }
  catch (err) { console.error('Error cargando actividades:', err); }
  finally { loadingActs.value = false; }
}
async function cargarDocumentos() {
  if (!op.value) return;
  loadingDocs.value = true;
  try { documentos.value = await documentoService.listarPorOportunidad(op.value.id); }
  catch (err) { console.error('Error cargando documentos:', err); }
  finally { loadingDocs.value = false; }
}

async function completarCompromiso(id) {
  try { await actividadService.actualizarCompromiso(id, { estado: 'COMPLETADO' }); await cargarActividades(); }
  catch (err) { console.error('Error completando compromiso:', err); }
}
async function onActividadCreada() { await cargarActividades(); }
async function onDocumentoCreado() { showDocumentoModal.value = false; await cargarDocumentos(); }
async function eliminarDocumento(id) {
  if (!confirm('¿Eliminar este documento?')) return;
  try { await documentoService.eliminar(id); await cargarDocumentos(); } catch (err) { console.error(err); }
}

async function openEdit() {
  editError.value = null;
  try { const res = await empresaService.listar({ page_size: 100 }); empresasEdit.value = res.data || []; } catch { /* noop */ }
  showEditModal.value = true;
}
async function handleEditSubmit(payload) {
  savingEdit.value = true;
  editError.value = null;
  try { await oportunidadService.actualizar(op.value.id, payload); showEditModal.value = false; await cargarOportunidad(); }
  catch (err) { editError.value = err.response?.data?.message || 'Error al actualizar'; }
  finally { savingEdit.value = false; }
}
async function onContratoCreado() { showFormalizarModal.value = false; await cargarOportunidad(); }

function goBack() { router.back(); }

onMounted(async () => {
  setRegions({ aside: true }); // Operativo: panel contextual a la derecha
  await cargarOportunidad();
  try { tiposActividad.value = await actividadService.listarTipos(); } catch { /* noop */ }
  try { tiposDocumento.value = await documentoService.listarTipos(); } catch { /* noop */ }
  if (op.value) await Promise.all([cargarActividades(), cargarDocumentos()]);
});
onUnmounted(() => setRegions({ aside: false }));

// Al cambiar de oportunidad dentro de la consola (mismo componente reutilizado)
watch(opId, async () => {
  tab.value = 'resumen';
  await cargarOportunidad();
  if (op.value) await Promise.all([cargarActividades(), cargarDocumentos()]);
});
</script>

<style scoped>
.opd { padding: var(--gc-space-6); display: flex; flex-direction: column; gap: var(--gc-space-5); }
.opd__state { display: flex; justify-content: center; padding: var(--gc-space-12); color: var(--gc-text-3); }
.opd__error { display: flex; flex-direction: column; align-items: center; gap: var(--gc-space-3); padding: var(--gc-space-12); color: var(--gc-text-2); }
.opd__aside-readonly { padding: var(--gc-space-5); }

.opd__head { display: flex; align-items: center; justify-content: space-between; gap: var(--gc-space-3); }
.opd__heading { display: flex; align-items: center; gap: var(--gc-space-3); min-width: 0; }
.opd__back { display: inline-flex; padding: var(--gc-space-1); background: transparent; border: none; color: var(--gc-text-2); border-radius: var(--gc-radius-sm); }
.opd__back:hover { background: var(--gc-surface-2); color: var(--gc-text); }
.opd__title { font-size: var(--gc-fs-xl); }
.opd__sub { font-size: var(--gc-fs-md); color: var(--gc-text-2); }

.opd__tabs { display: flex; gap: var(--gc-space-4); border-bottom: 1px solid var(--gc-border); }
.opd__tab { padding: var(--gc-space-2) 0; background: transparent; border: none; font-size: var(--gc-fs-md); color: var(--gc-text-3); }
.opd__tab:hover { color: var(--gc-text-2); }
.opd__tab--active { color: var(--gc-text); font-weight: var(--gc-fw-medium); box-shadow: inset 0 -2px 0 var(--gc-primary); }

.opd__panel { display: flex; flex-direction: column; gap: var(--gc-space-5); }
.opd__grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: var(--gc-space-4); }
.opd__field { display: flex; flex-direction: column; gap: 2px; }
.opd__label { font-size: var(--gc-fs-xs); text-transform: uppercase; letter-spacing: 0.04em; color: var(--gc-text-3); }
.opd__value { font-size: var(--gc-fs-md); color: var(--gc-text); }

.opd__cierre { display: flex; flex-direction: column; gap: var(--gc-space-1); padding: var(--gc-space-4); background: var(--gc-danger-soft); border-radius: var(--gc-radius-md); font-size: var(--gc-fs-md); color: var(--gc-text); }
.opd__cierre-coment { font-size: var(--gc-fs-base); color: var(--gc-text-2); }

.opd__sec { display: flex; flex-direction: column; gap: var(--gc-space-3); }
.opd__sechead { display: flex; align-items: center; justify-content: space-between; }
.opd__h2 { font-size: var(--gc-fs-xs); text-transform: uppercase; letter-spacing: 0.06em; color: var(--gc-text-3); }
.opd__list { border: 1px solid var(--gc-border); border-radius: var(--gc-radius-lg); overflow: hidden; }
.opd__list > :deep(.gc-row:last-child) { border-bottom: none; }
.opd__docmain { display: flex; flex-direction: column; gap: 2px; }
.opd__docname { font-size: var(--gc-fs-md); color: var(--gc-info); }
.opd__docname:hover { text-decoration: underline; }
.opd__docmeta { font-size: var(--gc-fs-xs); color: var(--gc-text-3); }
.opd__actmain { display: flex; flex-direction: column; gap: 2px; }
.opd__acttipo { font-size: var(--gc-fs-md); color: var(--gc-text); }
.opd__actfecha { font-size: var(--gc-fs-xs); color: var(--gc-text-3); }
.opd__actnotas { margin-top: 2px; font-size: var(--gc-fs-base); color: var(--gc-text-2); }

.opd__proceso { display: flex; flex-direction: column; gap: var(--gc-space-3); align-items: flex-start; padding: var(--gc-space-5); border: 1px solid var(--gc-border); border-radius: var(--gc-radius-lg); }
.opd__procesohead { display: flex; align-items: center; gap: var(--gc-space-2); }
.opd__procesoetapa { font-size: var(--gc-fs-md); color: var(--gc-text); }
.opd__procesohint { font-size: var(--gc-fs-base); color: var(--gc-text-2); }
</style>
