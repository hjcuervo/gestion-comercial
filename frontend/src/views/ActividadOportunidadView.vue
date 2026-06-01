<template>
  <div class="op-detalle">
    <!-- Captura teletransportada al panel contextual derecho -->
    <Teleport to="#gc-shell-aside">
      <CapturaActividadPanel
        v-if="oportunidadId"
        :oportunidad-id="oportunidadId"
        :tipos="tipos"
        @creado="recargar"
      />
    </Teleport>

    <div v-if="loading" class="op-detalle__state"><GcSpinner :size="24" /></div>

    <template v-else>
      <!-- Encabezado -->
      <header class="op-detalle__head">
        <div class="op-detalle__heading">
          <h1 class="op-detalle__title">{{ oportunidad?.nombre || 'Oportunidad' }}</h1>
          <GcBadge :tone="estadoTone(oportunidad?.estadoMacro)" :label="estadoLabel(oportunidad?.estadoMacro)" />
        </div>
        <p class="op-detalle__sub">
          {{ oportunidad?.empresaNombre || '—' }}
          <template v-if="oportunidad?.etapaNombre"> · {{ oportunidad.etapaNombre }}</template>
          <template v-if="oportunidad?.valorEstimado"> · <span class="gc-mono">{{ fmtCurrency(oportunidad.valorEstimado, oportunidad.moneda) }}</span></template>
        </p>
      </header>

      <!-- Tabs (Actividades activa; Resumen / Proceso llegan en RF3) -->
      <nav class="op-detalle__tabs">
        <span class="op-detalle__tab op-detalle__tab--active">Actividades</span>
        <span class="op-detalle__tab op-detalle__tab--soon" title="Disponible en RF3">Resumen</span>
        <span class="op-detalle__tab op-detalle__tab--soon" title="Disponible en RF3">Proceso contractual</span>
      </nav>

      <!-- Compromisos agrupados -->
      <section class="op-detalle__sec">
        <h2 class="op-detalle__h2">Compromisos</h2>

        <div v-if="!compromisos.length" class="op-detalle__emptybox">
          <GcEmpty icon="checklist" message="Sin compromisos registrados" />
        </div>

        <template v-else>
          <CompromisoGroup
            v-for="g in grupos"
            :key="g.key"
            :titulo="g.titulo"
            :tone="g.tone"
            :items="g.items"
            @completar="completar"
          />
        </template>
      </section>

      <!-- Bitácora de actividades -->
      <section class="op-detalle__sec">
        <h2 class="op-detalle__h2">Bitácora</h2>

        <GcEmpty v-if="!actividades.length" icon="notes" message="Sin actividades registradas" />

        <div v-else class="op-detalle__bitacora">
          <GcListRow v-for="a in actividadesOrdenadas" :key="a.id" tone="info">
            <template #lead><GcIcon name="point" :size="16" /></template>
            <div class="op-detalle__act">
              <span class="op-detalle__act-tipo">{{ tipoNombre(a.tipoActividadId) }}</span>
              <span class="op-detalle__act-fecha gc-mono">{{ fmtDateTime(a.fechaHora) }}<template v-if="a.duracionMinutos"> · {{ a.duracionMinutos }} min</template></span>
              <p v-if="a.notas" class="op-detalle__act-notas">{{ a.notas }}</p>
            </div>
          </GcListRow>
        </div>
      </section>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue';
import { useRoute } from 'vue-router';
import { useShell } from '@/composables/useShell';
import { oportunidadService } from '@/services/oportunidad.service';
import { actividadService } from '@/services/actividad.service';
import { formatCurrency as fmtCurrency } from '@/utils/currency';
import { formatDateTime as fmtDateTime } from '@/utils/datetime';
import CapturaActividadPanel from '@/components/actividad/CapturaActividadPanel.vue';
import CompromisoGroup from '@/components/actividad/CompromisoGroup.vue';
import GcListRow from '@/components/ui/GcListRow.vue';
import GcBadge from '@/components/ui/GcBadge.vue';
import GcEmpty from '@/components/ui/GcEmpty.vue';
import GcSpinner from '@/components/ui/GcSpinner.vue';
import GcIcon from '@/components/ui/GcIcon.vue';

const route = useRoute();
const { setRegions } = useShell();

const oportunidadId = computed(() => route.params.oportunidadId);
const oportunidad = ref(null);
const actividades = ref([]);
const tipos = ref([]);
const loading = ref(true);

const TONE = {
  ABIERTA: 'info', SEGUIMIENTO: 'warning', GANADA: 'success',
  CONTRATADA: 'accent', PERDIDA: 'danger', NO_CONCRETADA: 'neutral',
};
const LABEL = {
  ABIERTA: 'Abierta', SEGUIMIENTO: 'Seguimiento', GANADA: 'Ganada',
  CONTRATADA: 'Contratada', PERDIDA: 'Perdida', NO_CONCRETADA: 'No concretada',
};
function estadoTone(e) { return TONE[e] || 'neutral'; }
function estadoLabel(e) { return LABEL[e] || e || '—'; }

const tipoMap = computed(() => {
  const m = {};
  tipos.value.forEach((t) => { m[t.id] = t.nombre; });
  return m;
});
function tipoNombre(id) { return tipoMap.value[id] || 'Actividad'; }

const actividadesOrdenadas = computed(() =>
  [...actividades.value].sort((a, b) => new Date(b.fechaHora) - new Date(a.fechaHora))
);

// Aplanar compromisos de todas las actividades
const compromisos = computed(() => {
  const out = [];
  actividades.value.forEach((a) => {
    (a.compromisos || []).forEach((c) => out.push(c));
  });
  return out;
});

function parseLocalDate(iso) {
  if (!iso) return null;
  const [y, m, d] = iso.split('-').map(Number);
  return new Date(y, (m || 1) - 1, d || 1);
}
function isCerrado(estado) { return estado === 'COMPLETADO' || estado === 'CANCELADO'; }

const grupos = computed(() => {
  const today = new Date(); today.setHours(0, 0, 0, 0);
  const vencidas = [], proximas = [], completadas = [];
  compromisos.value.forEach((c) => {
    if (isCerrado(c.estado)) { completadas.push(c); return; }
    const f = parseLocalDate(c.fechaCompromiso);
    if (f && f < today) vencidas.push(c); else proximas.push(c);
  });
  const byFecha = (a, b) => new Date(a.fechaCompromiso) - new Date(b.fechaCompromiso);
  vencidas.sort(byFecha); proximas.sort(byFecha); completadas.sort(byFecha);
  return [
    { key: 'venc', titulo: 'Vencidas', tone: 'danger', items: vencidas },
    { key: 'prox', titulo: 'Próximas', tone: 'warning', items: proximas },
    { key: 'comp', titulo: 'Completadas', tone: 'success', items: completadas },
  ].filter((g) => g.items.length);
});

async function recargar() {
  if (!oportunidadId.value) return;
  loading.value = true;
  try {
    const [op, acts] = await Promise.all([
      oportunidadService.obtenerPorId(oportunidadId.value),
      actividadService.listarPorOportunidad(oportunidadId.value),
    ]);
    oportunidad.value = op;
    actividades.value = acts.data || [];
  } catch (err) {
    console.error('Error cargando detalle de oportunidad:', err);
  } finally {
    loading.value = false;
  }
}

async function completar(compromisoId) {
  try {
    await actividadService.actualizarCompromiso(compromisoId, { estado: 'COMPLETADO' });
    await recargar();
  } catch (err) {
    console.error('Error completando compromiso:', err);
  }
}

async function cargarTipos() {
  try { tipos.value = await actividadService.listarTipos(); }
  catch (err) { console.error('Error cargando tipos de actividad:', err); }
}

onMounted(async () => {
  setRegions({ aside: true });
  await cargarTipos();
  await recargar();
});
onUnmounted(() => setRegions({ aside: false }));

// Cambiar de oportunidad (mismo componente reutilizado) → recargar
watch(oportunidadId, () => recargar());
</script>

<style scoped>
.op-detalle { padding: var(--gc-space-6); display: flex; flex-direction: column; gap: var(--gc-space-6); }
.op-detalle__state { display: flex; justify-content: center; padding: var(--gc-space-12); color: var(--gc-text-3); }
.op-detalle__heading { display: flex; align-items: center; gap: var(--gc-space-3); }
.op-detalle__title { font-size: var(--gc-fs-xl); }
.op-detalle__sub { margin-top: var(--gc-space-1); font-size: var(--gc-fs-md); color: var(--gc-text-2); }

.op-detalle__tabs { display: flex; gap: var(--gc-space-4); border-bottom: 1px solid var(--gc-border); }
.op-detalle__tab { padding: var(--gc-space-2) 0; font-size: var(--gc-fs-md); color: var(--gc-text-3); }
.op-detalle__tab--active {
  color: var(--gc-text);
  font-weight: var(--gc-fw-medium);
  box-shadow: inset 0 -2px 0 var(--gc-primary);
}
.op-detalle__tab--soon { opacity: 0.5; cursor: not-allowed; }

.op-detalle__sec { display: flex; flex-direction: column; gap: var(--gc-space-3); }
.op-detalle__h2 {
  font-size: var(--gc-fs-xs);
  text-transform: uppercase;
  letter-spacing: 0.06em;
  color: var(--gc-text-3);
}
.op-detalle__bitacora,
.op-detalle__emptybox {
  border: 1px solid var(--gc-border);
  border-radius: var(--gc-radius-lg);
  overflow: hidden;
}
.op-detalle__bitacora > :deep(.gc-row:last-child) { border-bottom: none; }
.op-detalle__act { display: flex; flex-direction: column; gap: 2px; }
.op-detalle__act-tipo { font-size: var(--gc-fs-md); color: var(--gc-text); }
.op-detalle__act-fecha { font-size: var(--gc-fs-xs); color: var(--gc-text-3); }
.op-detalle__act-notas { margin-top: 2px; font-size: var(--gc-fs-base); color: var(--gc-text-2); }
</style>
