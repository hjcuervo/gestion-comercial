<template>
  <div class="ctd">
    <!-- Acciones de estado en el panel contextual -->
    <Teleport to="#gc-shell-aside" :disabled="!asideReady">
      <div v-if="contrato" class="ctd__aside">
        <h3 class="ctd__asidetitle">Acciones</h3>
        <div class="ctd__actions">
          <GcButton v-if="contrato.estado === 'VIGENTE'" variant="default" full-width icon="player-pause" @click="cambiarEstado('suspender')">Suspender</GcButton>
          <GcButton v-if="contrato.estado === 'SUSPENDIDO'" variant="default" full-width icon="player-play" @click="cambiarEstado('reiniciar')">Reiniciar</GcButton>
          <GcButton v-if="['VIGENTE', 'SUSPENDIDO'].includes(contrato.estado)" variant="default" full-width icon="circle-check" @click="cambiarEstado('terminar')">Terminar</GcButton>
          <GcButton v-if="contrato.estado === 'TERMINADO'" variant="default" full-width icon="checks" @click="cambiarEstado('liquidar')">Liquidar</GcButton>
          <p v-if="contrato.estado === 'LIQUIDADO'" class="ctd__asidehint">Contrato liquidado. No hay más acciones de estado.</p>
        </div>

        <div class="ctd__asidemeta">
          <div class="ctd__metarow"><span class="ctd__metalabel">Estado</span><GcBadge :tone="estadoTone" :label="estadoLabel" /></div>
          <div class="ctd__metarow"><span class="ctd__metalabel">Vence</span><span class="gc-mono">{{ diasRestantesLabel }}</span></div>
        </div>
      </div>
    </Teleport>

    <div v-if="loading" class="ctd__state"><GcSpinner :size="24" /></div>

    <template v-else-if="contrato">
      <header class="ctd__head">
        <div class="ctd__heading">
          <button class="ctd__back" @click="$router.push('/contratos')"><GcIcon name="arrow-left" :size="16" /></button>
          <h1 class="ctd__title">{{ contrato.numeroContratoInterno || ('Contrato #' + contrato.id) }}</h1>
          <GcBadge :tone="estadoTone" :label="estadoLabel" />
        </div>
      </header>
      <p class="ctd__sub">
        {{ contrato.empresaNombre }}
        <template v-if="contrato.tipoContratoNombre"> · {{ contrato.tipoContratoNombre }}</template>
        · <span class="gc-mono">{{ fmtCurrency(contrato.valorTotal ?? contrato.valorContrato) }}</span>
      </p>

      <nav class="ctd__tabs">
        <button v-for="t in tabs" :key="t.key" class="ctd__tab" :class="{ 'ctd__tab--active': tab === t.key }" @click="tab = t.key">
          {{ t.label }}<span v-if="t.count != null" class="ctd__tabcount gc-mono">{{ t.count }}</span>
        </button>
      </nav>

      <!-- ===== RESUMEN ===== -->
      <section v-show="tab === 'resumen'" class="ctd__panel">
        <GcStatStrip>
          <GcStat label="Valor contrato" :value="fmtCurrency(contrato.valorContrato)" />
          <GcStat label="Ajustes" :value="fmtCurrency(contrato.valorAjuste)" />
          <GcStat label="Valor total" :value="fmtCurrency(contrato.valorTotal ?? contrato.valorContrato)" />
          <GcStat label="Formas de pago" :value="fmtCurrency(sumaFormasPago)" />
        </GcStatStrip>

        <div class="ctd__grid">
          <div class="ctd__field"><span class="ctd__label">N° cliente</span><span class="ctd__value">{{ contrato.numeroContratoCliente || '—' }}</span></div>
          <div class="ctd__field"><span class="ctd__label">Empresa facturación</span><span class="ctd__value">{{ contrato.empresaFacturacionNombre || '—' }}</span></div>
          <div class="ctd__field"><span class="ctd__label">Inicio</span><span class="ctd__value gc-mono">{{ fmtDate(contrato.fechaInicio) }}</span></div>
          <div class="ctd__field"><span class="ctd__label">Fin</span><span class="ctd__value gc-mono">{{ fmtDate(contrato.fechaFin) }}</span></div>
          <div class="ctd__field"><span class="ctd__label">Responsable</span><span class="ctd__value">{{ contrato.responsableGestion || '—' }}</span></div>
          <div class="ctd__field"><span class="ctd__label">Interventor</span><span class="ctd__value">{{ contrato.interventor || '—' }}</span></div>
        </div>

        <div v-if="contrato.objeto" class="ctd__objeto">
          <span class="ctd__label">Objeto</span>
          <p>{{ contrato.objeto }}</p>
        </div>

        <!-- Documentos del contrato -->
        <div class="ctd__sec">
          <div class="ctd__sechead">
            <h2 class="ctd__h2">Documentos</h2>
            <GcButton variant="default" size="sm" icon="plus" @click="openDocModal({ contratoId: contrato.id })">Agregar</GcButton>
          </div>
          <GcEmpty v-if="!documentos.length" icon="file-off" message="Sin documentos" />
          <div v-else class="ctd__list">
            <GcListRow v-for="d in documentos" :key="d.id" tone="info">
              <template #lead><GcIcon name="external-link" :size="16" /></template>
              <div class="ctd__docmain">
                <a :href="d.urlStorage" target="_blank" rel="noopener" class="ctd__docname">{{ d.nombre }}</a>
                <span class="ctd__docmeta gc-mono">{{ tipoDocNombre(d.tipoDocumentoId) }} · {{ fmtDate(d.fechaCarga) }}</span>
              </div>
              <template #actions><GcButton variant="ghost" size="sm" icon="trash" @click="eliminarDocumento(d.id)" /></template>
            </GcListRow>
          </div>
        </div>
      </section>

      <!-- ===== FORMAS DE PAGO ===== -->
      <section v-show="tab === 'formas'" class="ctd__panel">
        <div class="ctd__sechead">
          <h2 class="ctd__h2">Formas de pago</h2>
          <GcButton variant="default" size="sm" icon="plus" @click="showFpModal = true">Nueva</GcButton>
        </div>
        <GcEmpty v-if="!formasPago.length" icon="cash" message="Sin formas de pago registradas" />
        <div v-else class="ctd__list">
          <GcListRow v-for="fp in formasPago" :key="fp.id" :tone="fpTone(fp.estado)">
            <div class="ctd__fpmain">
              <span class="ctd__fpdesc">{{ fp.descripcion }}</span>
              <span class="ctd__fpmeta gc-mono">
                Esp: {{ fmtDate(fp.fechaEsperadaActual || fp.fechaEsperadaOriginal) }}
                <template v-if="fp.porcentajeCumplimiento != null"> · {{ Math.round(fp.porcentajeCumplimiento) }}%</template>
              </span>
            </div>
            <template #actions>
              <span class="ctd__fpvalor gc-mono">{{ fmtCurrency(fp.montoPresupuestado) }}</span>
              <GcBadge :tone="fpTone(fp.estado)" :label="fpLabel(fp.estado)" />
            </template>
          </GcListRow>
        </div>
      </section>

      <!-- ===== MODIFICACIONES ===== -->
      <section v-show="tab === 'modificaciones'" class="ctd__panel">
        <div class="ctd__sechead">
          <h2 class="ctd__h2">Modificaciones</h2>
          <GcButton variant="default" size="sm" icon="plus" @click="showModModal = true">Nueva</GcButton>
        </div>
        <GcEmpty v-if="!modificaciones.length" icon="file-diff" message="Sin modificaciones registradas" />
        <div v-else class="ctd__list">
          <GcListRow v-for="m in modificaciones" :key="m.id" tone="accent">
            <template #lead><GcIcon name="file-diff" :size="16" /></template>
            <div class="ctd__modmain">
              <span class="ctd__modtipo">{{ tipoModLabel(m.tipoModificacion) }}</span>
              <span class="ctd__modmeta gc-mono">
                {{ fmtDate(m.fechaModificacion) }}
                <template v-if="m.modificaValor && m.valorModificacion"> · {{ fmtCurrency(m.valorModificacion) }}</template>
                <template v-if="m.modificaTiempo && m.nuevaFechaFin"> · → {{ fmtDate(m.nuevaFechaFin) }}</template>
              </span>
              <p v-if="m.observaciones" class="ctd__modobs">{{ m.observaciones }}</p>
            </div>
          </GcListRow>
        </div>
      </section>
    </template>

    <FormaPagoModal v-if="showFpModal" :saving="fpSaving" :error="fpError" @close="showFpModal = false" @submit="submitFp" />
    <ModificacionModal v-if="showModModal" :saving="modSaving" :error="modError" @close="showModModal = false" @submit="submitMod" />
    <DocumentoModal v-if="showDocModal" :oportunidad-id="null" @close="showDocModal = false" @created="onDocCreated" />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted, watch, nextTick } from 'vue';
import { useRoute } from 'vue-router';
import { useShell } from '@/composables/useShell';
import { contratoService } from '@/services/contrato.service';
import { documentoService } from '@/services/documento.service';
import { formatCurrency } from '@/utils/currency';
import { formatDate as fmtDate } from '@/utils/datetime';
import FormaPagoModal from '@/components/contrato/FormaPagoModal.vue';
import ModificacionModal from '@/components/contrato/ModificacionModal.vue';
import DocumentoModal from '@/components/documento/DocumentoModal.vue';
import GcButton from '@/components/ui/GcButton.vue';
import GcBadge from '@/components/ui/GcBadge.vue';
import GcListRow from '@/components/ui/GcListRow.vue';
import GcStat from '@/components/ui/GcStat.vue';
import GcStatStrip from '@/components/ui/GcStatStrip.vue';
import GcEmpty from '@/components/ui/GcEmpty.vue';
import GcSpinner from '@/components/ui/GcSpinner.vue';
import GcIcon from '@/components/ui/GcIcon.vue';

const route = useRoute();
const { setRegions } = useShell();

// Activar la zona aside en setup (no en onMounted): así el contenedor
// #gc-shell-aside ya está visible cuando el Teleport del detalle se monta.
setRegions({ aside: true });

const contrato = ref(null);
const asideReady = ref(false);
const formasPago = ref([]);
const modificaciones = ref([]);
const documentos = ref([]);
const tiposDocumento = ref([]);
const loading = ref(true);
const tab = ref('resumen');

const showFpModal = ref(false);
const fpSaving = ref(false);
const fpError = ref(null);
const showModModal = ref(false);
const modSaving = ref(false);
const modError = ref(null);
const showDocModal = ref(false);

const tabs = computed(() => [
  { key: 'resumen', label: 'Resumen' },
  { key: 'formas', label: 'Formas de pago', count: formasPago.value.length },
  { key: 'modificaciones', label: 'Modificaciones', count: modificaciones.value.length },
]);

// Estado del contrato
const CONTRATO_TONE = { VIGENTE: 'success', SUSPENDIDO: 'warning', TERMINADO: 'danger', LIQUIDADO: 'neutral' };
const estadoTone = computed(() => CONTRATO_TONE[contrato.value?.estado] || 'neutral');
const estadoLabel = computed(() => ({ VIGENTE: 'Vigente', SUSPENDIDO: 'Suspendido', TERMINADO: 'Terminado', LIQUIDADO: 'Liquidado' }[contrato.value?.estado] || contrato.value?.estado));
const diasRestantes = computed(() => (contrato.value?.fechaFin ? Math.floor((new Date(contrato.value.fechaFin) - new Date()) / 86400000) : null));
const diasRestantesLabel = computed(() => { const d = diasRestantes.value; if (d == null) return '—'; return d < 0 ? `${Math.abs(d)}d vencido` : `${d} días`; });
const sumaFormasPago = computed(() => formasPago.value.reduce((s, fp) => s + Number(fp.montoPresupuestado || 0), 0));

// Estados del compromiso (forma de pago): 7 estados
const FP_TONE = { PENDIENTE_GESTION: 'neutral', EN_GESTION: 'info', COMPROMETIDO: 'info', PARCIALMENTE_CUMPLIDO: 'warning', REPROGRAMADO: 'warning', CUMPLIDO: 'success', NO_LOGRADO: 'danger' };
function fpTone(e) { return FP_TONE[e] || 'neutral'; }
function fpLabel(e) {
  return { PENDIENTE_GESTION: 'Pendiente', EN_GESTION: 'En gestión', COMPROMETIDO: 'Comprometido', PARCIALMENTE_CUMPLIDO: 'Parcial', REPROGRAMADO: 'Reprogramado', CUMPLIDO: 'Cumplido', NO_LOGRADO: 'No logrado' }[e] || e;
}
function tipoModLabel(t) { return { ADICION: 'Adición', PRORROGA: 'Prórroga', SUSPENSION: 'Suspensión', REINICIO: 'Reinicio', OTRO: 'Otro' }[t] || t; }

function fmtCurrency(v) { return formatCurrency(v, contrato.value?.moneda || 'COP'); }
function tipoDocNombre(id) { const t = tiposDocumento.value.find((x) => x.id === id); return t ? t.nombre : 'Documento'; }

async function cargarTodo() {
  loading.value = true;
  try {
    contrato.value = await contratoService.obtenerPorId(route.params.id);
    modificaciones.value = contrato.value.modificaciones || [];
    try { formasPago.value = await contratoService.listarFormasPago(route.params.id); } catch { formasPago.value = []; }
    if (!modificaciones.value.length) {
      try { modificaciones.value = await contratoService.listarModificaciones(route.params.id); } catch { /* noop */ }
    }
  } catch (err) {
    console.error('Error cargando contrato:', err);
  } finally {
    loading.value = false;
  }
  try { tiposDocumento.value = await documentoService.listarTipos(); } catch { /* noop */ }
  await cargarDocumentos();
}
async function cargarDocumentos() {
  if (!contrato.value) return;
  try { documentos.value = await documentoService.listarPorContrato(contrato.value.id); } catch { documentos.value = []; }
}

async function cambiarEstado(accion) {
  if (!confirm(`¿Está seguro de ${accion} este contrato?`)) return;
  try { contrato.value = await contratoService[accion](contrato.value.id); }
  catch (err) { alert(err.response?.data?.message || 'Error al cambiar estado'); }
}

async function submitFp(payload) {
  fpError.value = null;
  fpSaving.value = true;
  try {
    const created = await contratoService.crearFormaPago(contrato.value.id, {
      descripcion: payload.descripcion,
      montoPresupuestado: payload.valor,
      fechaEsperada: payload.fechaEstimadaPago || null,
      moneda: contrato.value.moneda || 'COP',
      tipo: 'NUEVO',
    });
    formasPago.value.push(created);
    showFpModal.value = false;
  } catch (err) { fpError.value = err.response?.data?.message || 'Error al crear forma de pago'; }
  finally { fpSaving.value = false; }
}

async function submitMod(payload) {
  modError.value = null;
  modSaving.value = true;
  try {
    const created = await contratoService.crearModificacion(contrato.value.id, payload);
    modificaciones.value.unshift(created);
    showModModal.value = false;
    contrato.value = await contratoService.obtenerPorId(route.params.id);
  } catch (err) { modError.value = err.response?.data?.message || 'Error al crear modificación'; }
  finally { modSaving.value = false; }
}

function openDocModal() { showDocModal.value = true; }
async function onDocCreated() { showDocModal.value = false; await cargarDocumentos(); }
async function eliminarDocumento(id) {
  if (!confirm('¿Eliminar este documento?')) return;
  try { await documentoService.eliminar(id); await cargarDocumentos(); } catch (err) { console.error(err); }
}

onMounted(async () => {
  await nextTick();
  asideReady.value = true;
  cargarTodo();
});
onUnmounted(() => setRegions({ aside: false }));
watch(() => route.params.id, () => { tab.value = 'resumen'; cargarTodo(); });
</script>

<style scoped>
.ctd { padding: var(--gc-space-6); display: flex; flex-direction: column; gap: var(--gc-space-5); }
.ctd__state { display: flex; justify-content: center; padding: var(--gc-space-12); color: var(--gc-text-3); }

.ctd__aside { padding: var(--gc-space-5); display: flex; flex-direction: column; gap: var(--gc-space-5); }
.ctd__asidetitle { font-size: var(--gc-fs-lg); }
.ctd__actions { display: flex; flex-direction: column; gap: var(--gc-space-2); }
.ctd__asidehint { font-size: var(--gc-fs-sm); color: var(--gc-text-3); }
.ctd__asidemeta { display: flex; flex-direction: column; gap: var(--gc-space-2); padding-top: var(--gc-space-4); border-top: 1px solid var(--gc-border); }
.ctd__metarow { display: flex; align-items: center; justify-content: space-between; font-size: var(--gc-fs-sm); color: var(--gc-text-2); }
.ctd__metalabel { color: var(--gc-text-3); }

.ctd__head { display: flex; align-items: center; justify-content: space-between; }
.ctd__heading { display: flex; align-items: center; gap: var(--gc-space-3); min-width: 0; }
.ctd__back { display: inline-flex; padding: var(--gc-space-1); background: transparent; border: none; color: var(--gc-text-2); border-radius: var(--gc-radius-sm); }
.ctd__back:hover { background: var(--gc-surface-2); color: var(--gc-text); }
.ctd__title { font-size: var(--gc-fs-xl); }
.ctd__sub { font-size: var(--gc-fs-md); color: var(--gc-text-2); }

.ctd__tabs { display: flex; gap: var(--gc-space-4); border-bottom: 1px solid var(--gc-border); }
.ctd__tab { display: flex; align-items: center; gap: var(--gc-space-2); padding: var(--gc-space-2) 0; background: transparent; border: none; font-size: var(--gc-fs-md); color: var(--gc-text-3); }
.ctd__tab:hover { color: var(--gc-text-2); }
.ctd__tab--active { color: var(--gc-text); font-weight: var(--gc-fw-medium); box-shadow: inset 0 -2px 0 var(--gc-primary); }
.ctd__tabcount { font-size: var(--gc-fs-xs); color: var(--gc-text-3); }

.ctd__panel { display: flex; flex-direction: column; gap: var(--gc-space-5); }
.ctd__grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: var(--gc-space-4); }
.ctd__field { display: flex; flex-direction: column; gap: 2px; }
.ctd__label { font-size: var(--gc-fs-xs); text-transform: uppercase; letter-spacing: 0.04em; color: var(--gc-text-3); }
.ctd__value { font-size: var(--gc-fs-md); color: var(--gc-text); }
.ctd__objeto { display: flex; flex-direction: column; gap: var(--gc-space-1); font-size: var(--gc-fs-md); color: var(--gc-text-2); }

.ctd__sec { display: flex; flex-direction: column; gap: var(--gc-space-3); }
.ctd__sechead { display: flex; align-items: center; justify-content: space-between; }
.ctd__h2 { font-size: var(--gc-fs-xs); text-transform: uppercase; letter-spacing: 0.06em; color: var(--gc-text-3); }
.ctd__list { border: 1px solid var(--gc-border); border-radius: var(--gc-radius-lg); overflow: hidden; }
.ctd__list > :deep(.gc-row:last-child) { border-bottom: none; }

.ctd__docmain, .ctd__fpmain, .ctd__modmain { display: flex; flex-direction: column; gap: 2px; min-width: 0; }
.ctd__docname { font-size: var(--gc-fs-md); color: var(--gc-info); }
.ctd__docname:hover { text-decoration: underline; }
.ctd__docmeta, .ctd__fpmeta, .ctd__modmeta { font-size: var(--gc-fs-xs); color: var(--gc-text-3); }
.ctd__fpdesc, .ctd__modtipo { font-size: var(--gc-fs-md); color: var(--gc-text); }
.ctd__fpvalor { font-size: var(--gc-fs-sm); color: var(--gc-text-2); }
.ctd__modobs { margin-top: 2px; font-size: var(--gc-fs-base); color: var(--gc-text-2); }
</style>
