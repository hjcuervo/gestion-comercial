<template>
  <div class="fact">
    <header class="fact__head">
      <div>
        <h1 class="fact__title">Facturación</h1>
        <p class="fact__sub">{{ mesLabel }}</p>
      </div>
      <div class="fact__period">
        <GcButton variant="ghost" size="sm" icon="chevron-left" @click="cambiarMes(-1)" />
        <input type="month" v-model="anioMes" class="fact__month" @change="loadData" />
        <GcButton variant="ghost" size="sm" icon="chevron-right" @click="cambiarMes(1)" />
        <GcButton v-if="!esMesActual" variant="ghost" size="sm" icon="calendar" @click="irAlMesActual">Hoy</GcButton>
      </div>
    </header>

    <div v-if="vista?.notaTrm" class="fact__trm">
      <GcIcon name="info-circle" :size="14" /><span>{{ vista.notaTrm }}</span>
    </div>

    <!-- KPIs clicables -->
    <div class="fact__kpis">
      <button :class="['fkpi', activeFilter === 'arrastreMesesAnteriores' && 'fkpi--on', conteosKpi.arrastreMesesAnteriores > 0 && 'fkpi--alert']" @click="toggleFilter('arrastreMesesAnteriores')">
        <span class="fkpi__value gc-mono">{{ conteosKpi.arrastreMesesAnteriores || 0 }}</span>
        <span class="fkpi__label">Arrastre</span>
        <span class="fkpi__sub gc-mono">{{ fmtCop(montosKpi.arrastreMesesAnteriores) }}</span>
      </button>
      <button :class="['fkpi', activeFilter === 'mesActualVencidos' && 'fkpi--on', conteosKpi.mesActualVencidos > 0 && 'fkpi--warn']" @click="toggleFilter('mesActualVencidos')">
        <span class="fkpi__value gc-mono">{{ conteosKpi.mesActualVencidos || 0 }}</span>
        <span class="fkpi__label">Vencidos del mes</span>
        <span class="fkpi__sub gc-mono">{{ fmtCop(montosKpi.mesActualVencidos) }}</span>
      </button>
      <button :class="['fkpi', activeFilter === 'mesActualPorVencer' && 'fkpi--on']" @click="toggleFilter('mesActualPorVencer')">
        <span class="fkpi__value gc-mono">{{ conteosKpi.mesActualPorVencer || 0 }}</span>
        <span class="fkpi__label">Por vencer</span>
        <span class="fkpi__sub gc-mono">{{ fmtCop(montosKpi.mesActualPorVencer) }}</span>
      </button>
      <div class="fkpi fkpi--total">
        <span class="fkpi__value fkpi__value--ok gc-mono">{{ fmtPct(vista?.kpis?.porcentajeCumplimiento) }}</span>
        <span class="fkpi__label">Cumplimiento</span>
        <span class="fkpi__sub gc-mono">{{ fmtCop(vista?.kpis?.totalFacturadoCOP) }} / {{ fmtCop(vista?.kpis?.totalPresupuestadoCOP) }}</span>
      </div>
    </div>

    <!-- Distribución por estado -->
    <div v-if="distribucionEntries.length" class="fact__distrib">
      <span class="fact__distriblabel">Distribución:</span>
      <GcBadge v-for="[estado, cantidad] in distribucionEntries" :key="estado" :tone="estadoTone(estado)" :label="`${estadoLabel(estado)} ${cantidad}`" />
    </div>

    <div v-if="loading" class="fact__state"><GcSpinner :size="24" /></div>

    <!-- Filtro activo -->
    <section v-else-if="activeFilter" class="fact__section">
      <div class="fact__sechead">
        <h2 class="fact__h2">{{ filterTitle }} <span class="gc-mono">{{ filteredList.length }}</span></h2>
        <GcButton variant="ghost" size="sm" icon="x" @click="activeFilter = null">Ver todo</GcButton>
      </div>
      <GcEmpty v-if="!filteredList.length" icon="circle-check" message="No hay elementos para este filtro" />
      <div v-else class="fact__list">
        <FormaPagoCard v-for="fp in filteredList" :key="fp.id" :item="fp" @click="openGestion(fp)" />
      </div>
    </section>

    <!-- Vista por defecto: 3 secciones -->
    <template v-else-if="!loading">
      <section v-if="vista?.arrastreMesesAnteriores?.length" class="fact__section">
        <h2 class="fact__h2 fact__h2--danger">Arrastre de meses anteriores <span class="gc-mono">{{ vista.arrastreMesesAnteriores.length }}</span></h2>
        <div class="fact__list">
          <FormaPagoCard v-for="fp in vista.arrastreMesesAnteriores" :key="fp.id" :item="fp" variant="arrastre" @click="openGestion(fp)" />
        </div>
      </section>

      <section v-if="vista?.mesActualVencidos?.length" class="fact__section">
        <h2 class="fact__h2 fact__h2--warning">Vencidos en {{ mesLabel }} <span class="gc-mono">{{ vista.mesActualVencidos.length }}</span></h2>
        <div class="fact__list">
          <FormaPagoCard v-for="fp in vista.mesActualVencidos" :key="fp.id" :item="fp" variant="vencido" @click="openGestion(fp)" />
        </div>
      </section>

      <section v-if="vista?.mesActualPorVencer?.length" class="fact__section">
        <h2 class="fact__h2">Por vencer en {{ mesLabel }} <span class="gc-mono">{{ vista.mesActualPorVencer.length }}</span></h2>
        <div class="fact__list">
          <FormaPagoCard v-for="fp in vista.mesActualPorVencer" :key="fp.id" :item="fp" @click="openGestion(fp)" />
        </div>
      </section>

      <GcEmpty
        v-if="!vista?.arrastreMesesAnteriores?.length && !vista?.mesActualVencidos?.length && !vista?.mesActualPorVencer?.length"
        icon="circle-check"
        :message="`No hay formas de pago activas para ${mesLabel}`"
      />
    </template>

    <GestionPanel v-if="selectedFp" :forma-pago="selectedFp" @close="selectedFp = null" @updated="loadData" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useShell } from '@/composables/useShell';
import GestionPanel from '@/components/facturacion/GestionPanel.vue';
import FormaPagoCard from '@/components/facturacion/FormaPagoCard.vue';
import { facturacionService } from '@/services/facturacion.service';
import GcButton from '@/components/ui/GcButton.vue';
import GcBadge from '@/components/ui/GcBadge.vue';
import GcIcon from '@/components/ui/GcIcon.vue';
import GcEmpty from '@/components/ui/GcEmpty.vue';
import GcSpinner from '@/components/ui/GcSpinner.vue';

const { setRegions } = useShell();
setRegions({ master: false, aside: false });

const loading = ref(true);
const vista = ref(null);
const selectedFp = ref(null);
const activeFilter = ref(null);

const today = new Date();
const anioMes = ref(`${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}`);

const meses = ['', 'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'];
const mesLabel = computed(() => {
  if (!anioMes.value) return '';
  const [a, m] = anioMes.value.split('-');
  return `${meses[parseInt(m)]} ${a}`;
});
const esMesActual = computed(() => vista.value?.esMesActual ?? false);

const conteosKpi = computed(() => vista.value?.kpis?.conteosPorGrupo || {});
const montosKpi = computed(() => vista.value?.kpis?.montosPorGrupoCOP || {});
const distribucionEntries = computed(() => Object.entries(vista.value?.kpis?.distribucionEstado || {}));

const filteredList = computed(() => {
  if (!activeFilter.value || !vista.value) return [];
  return vista.value[activeFilter.value] || [];
});
const filterTitle = computed(() => {
  switch (activeFilter.value) {
    case 'arrastreMesesAnteriores': return 'Arrastre de meses anteriores';
    case 'mesActualVencidos': return `Vencidos en ${mesLabel.value}`;
    case 'mesActualPorVencer': return `Por vencer en ${mesLabel.value}`;
    default: return '';
  }
});

function toggleFilter(filter) { activeFilter.value = activeFilter.value === filter ? null : filter; }

function cambiarMes(delta) {
  const [a, m] = anioMes.value.split('-').map(Number);
  const d = new Date(a, m - 1 + delta, 1);
  anioMes.value = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`;
  loadData();
}
function irAlMesActual() {
  const d = new Date();
  anioMes.value = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`;
  loadData();
}

onMounted(loadData);

async function loadData() {
  loading.value = true;
  try {
    vista.value = await facturacionService.obtenerVistaPeriodo({ anioMes: anioMes.value });
  } catch (err) {
    console.error('Error cargando vista por periodo:', err);
    vista.value = null;
  } finally {
    loading.value = false;
  }
}

function openGestion(fp) { selectedFp.value = fp; }

function fmtCop(v) {
  if (v == null) return '$0';
  return new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }).format(v);
}
function fmtPct(v) {
  if (v == null) return '0%';
  return `${(Number(v) * 100).toFixed(0)}%`;
}
const ESTADO_TONE = {
  PENDIENTE_GESTION: 'neutral', EN_GESTION: 'info', COMPROMETIDO: 'info',
  PARCIALMENTE_CUMPLIDO: 'warning', REPROGRAMADO: 'warning', CUMPLIDO: 'success', NO_LOGRADO: 'danger',
};
function estadoTone(e) { return ESTADO_TONE[e] || 'neutral'; }
function estadoLabel(estado) {
  return {
    PENDIENTE_GESTION: 'Pendiente', EN_GESTION: 'En gestión', COMPROMETIDO: 'Comprometido',
    PARCIALMENTE_CUMPLIDO: 'Parcial', REPROGRAMADO: 'Reprogramado', CUMPLIDO: 'Cumplido', NO_LOGRADO: 'No logrado',
  }[estado] || estado;
}
</script>

<style scoped>
.fact { padding: var(--gc-space-6); display: flex; flex-direction: column; gap: var(--gc-space-5); }
.fact__head { display: flex; align-items: flex-start; justify-content: space-between; gap: var(--gc-space-4); }
.fact__title { font-size: var(--gc-fs-2xl); }
.fact__sub { margin-top: 2px; font-size: var(--gc-fs-md); color: var(--gc-text-2); }
.fact__period { display: flex; align-items: center; gap: var(--gc-space-2); }
.fact__month { height: 34px; padding: 0 var(--gc-space-3); background: var(--gc-surface); border: 1px solid var(--gc-border); border-radius: var(--gc-radius-md); color: var(--gc-text); font-size: var(--gc-fs-sm); }
.fact__trm { display: flex; align-items: center; gap: var(--gc-space-2); padding: var(--gc-space-2) var(--gc-space-3); background: var(--gc-warning-soft); border: 1px solid var(--gc-warning); border-radius: var(--gc-radius-md); font-size: var(--gc-fs-sm); color: var(--gc-text-2); }

.fact__kpis { display: grid; grid-template-columns: repeat(4, 1fr); gap: var(--gc-space-3); }
.fkpi { display: flex; flex-direction: column; gap: 2px; padding: var(--gc-space-4); background: var(--gc-surface); border: 1px solid var(--gc-border); border-radius: var(--gc-radius-lg); text-align: left; cursor: pointer; transition: border-color var(--gc-t-fast); }
.fkpi:hover { border-color: var(--gc-border-strong); }
.fkpi--on { border-color: var(--gc-primary); box-shadow: inset 0 0 0 1px var(--gc-primary); }
.fkpi--total { cursor: default; }
.fkpi--alert { border-left: 2px solid var(--gc-danger); }
.fkpi--warn { border-left: 2px solid var(--gc-warning); }
.fkpi__value { font-size: var(--gc-fs-2xl); font-weight: var(--gc-fw-semibold); color: var(--gc-text); }
.fkpi__value--ok { color: var(--gc-success); }
.fkpi__label { font-size: var(--gc-fs-sm); color: var(--gc-text-2); }
.fkpi__sub { font-size: var(--gc-fs-xs); color: var(--gc-text-3); }

.fact__distrib { display: flex; align-items: center; flex-wrap: wrap; gap: var(--gc-space-2); }
.fact__distriblabel { font-size: var(--gc-fs-sm); color: var(--gc-text-3); }
.fact__state { display: flex; justify-content: center; padding: var(--gc-space-12); color: var(--gc-text-3); }

.fact__section { display: flex; flex-direction: column; gap: var(--gc-space-3); }
.fact__sechead { display: flex; align-items: center; justify-content: space-between; }
.fact__h2 { font-size: var(--gc-fs-md); font-weight: var(--gc-fw-medium); color: var(--gc-text); }
.fact__h2--danger { color: var(--gc-danger); }
.fact__h2--warning { color: var(--gc-warning); }
.fact__h2 span { color: var(--gc-text-3); font-weight: var(--gc-fw-regular); }
.fact__list { display: flex; flex-direction: column; gap: var(--gc-space-2); }

@media (max-width: 980px) {
  .fact__kpis { grid-template-columns: repeat(2, 1fr); }
}
</style>
