<template>
  <div class="dash">
    <header class="dash__head">
      <div>
        <h1 class="dash__title">Dashboard</h1>
        <p class="dash__sub">Análisis de oportunidades comerciales</p>
      </div>
      <div class="dash__filters">
        <select v-model="selectedPipelineId" class="dash__select" @change="loadStats">
          <option :value="null">Todos los pipelines</option>
          <option v-for="p in pipelines" :key="p.id" :value="p.id">{{ p.nombre }}</option>
        </select>
        <select v-model.number="selectedMeses" class="dash__select" @change="loadStats">
          <option :value="3">Últimos 3 meses</option>
          <option :value="6">Últimos 6 meses</option>
          <option :value="12">Últimos 12 meses</option>
        </select>
      </div>
    </header>

    <div v-if="loading" class="dash__state"><GcSpinner :size="24" /></div>

    <template v-else-if="stats">
      <!-- Franja de indicadores -->
      <GcStatStrip>
        <GcStat label="Valor en pipeline" :value="valorPipeline" />
        <GcStat label="Valor ganado" :value="valorGanado" :delta="stats.totalGanadas + ' ganadas'" delta-tone="positive" />
        <GcStat label="Valor perdido" :value="valorPerdido" :delta="stats.totalPerdidas + ' perdidas'" delta-tone="negative" />
        <GcStat label="Tasa de conversión" :value="tasaConversion" :delta="totalCerradas + ' cerradas'" delta-tone="neutral" />
        <GcStat label="Abiertas" :value="String(stats.totalAbiertas)" />
      </GcStatStrip>

      <div class="dash__grid">
        <!-- Embudo por etapa -->
        <section class="dash__panel">
          <h2 class="dash__h2">Embudo por etapa</h2>
          <GcEmpty v-if="!stats.porEtapa?.length" icon="chart-bar" message="Sin datos de etapas" />
          <div v-else class="bars">
            <div v-for="e in stats.porEtapa" :key="e.etapaId" class="bars__row">
              <div class="bars__head">
                <span class="bars__label">
                  <span class="bars__dot" :style="{ background: e.etapaColor || 'var(--gc-info)' }"></span>
                  {{ e.etapaNombre }}
                </span>
                <span class="bars__val gc-mono">{{ e.cantidad }} · {{ fmtCurrency(e.valorTotal) }}</span>
              </div>
              <div class="bars__track">
                <div class="bars__fill" :style="{ width: funnelWidth(e.cantidad) + '%', background: e.etapaColor || 'var(--gc-info)' }"></div>
              </div>
            </div>
          </div>
        </section>

        <!-- Por empresa -->
        <section class="dash__panel">
          <h2 class="dash__h2">Por empresa</h2>
          <GcEmpty v-if="!stats.porEmpresa?.length" icon="building" message="Sin datos por empresa" />
          <div v-else class="dash__list">
            <GcListRow v-for="em in stats.porEmpresa" :key="em.empresaId" tone="neutral">
              <div class="emp">
                <span class="emp__name">{{ em.empresaNombre }}</span>
                <span class="emp__meta gc-mono">{{ em.abiertas }} abiertas · {{ em.ganadas }} ganadas</span>
              </div>
              <template #actions>
                <span class="emp__valor gc-mono">{{ fmtCurrency(em.valorTotal) }}</span>
              </template>
            </GcListRow>
          </div>
        </section>

        <!-- Top oportunidades -->
        <section class="dash__panel">
          <h2 class="dash__h2">Top oportunidades</h2>
          <GcEmpty v-if="!stats.topOportunidades?.length" icon="trophy" message="Sin oportunidades" />
          <div v-else class="dash__list">
            <GcListRow
              v-for="(t, i) in stats.topOportunidades"
              :key="t.id"
              :tone="toneEstado(t.estadoMacro)"
              clickable
              @click="goToDetalle(t.id)"
            >
              <template #lead><span class="top__rank gc-mono">{{ i + 1 }}</span></template>
              <div class="top">
                <span class="top__name">{{ t.nombre }}</span>
                <span class="top__meta gc-mono">{{ t.empresaNombre }}<template v-if="t.etapaNombre"> · {{ t.etapaNombre }}</template></span>
              </div>
              <template #actions>
                <span class="top__valor gc-mono">{{ fmtCurrency(t.valorEstimado, t.moneda) }}</span>
              </template>
            </GcListRow>
          </div>
        </section>

        <!-- Tendencia mensual -->
        <section class="dash__panel">
          <h2 class="dash__h2">Tendencia mensual</h2>
          <GcEmpty v-if="!stats.porMes?.length" icon="chart-line" message="Sin datos mensuales" />
          <div v-else class="dash__chart"><canvas ref="trendChart"></canvas></div>
        </section>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue';
import { useRouter } from 'vue-router';
import { useShell } from '@/composables/useShell';
import { dashboardService } from '@/services/dashboard.service';
import { pipelineService } from '@/services/pipeline.service';
import { formatCurrency } from '@/utils/currency';
import GcStat from '@/components/ui/GcStat.vue';
import GcStatStrip from '@/components/ui/GcStatStrip.vue';
import GcListRow from '@/components/ui/GcListRow.vue';
import GcEmpty from '@/components/ui/GcEmpty.vue';
import GcSpinner from '@/components/ui/GcSpinner.vue';

const router = useRouter();
const { setRegions } = useShell();

const stats = ref(null);
const pipelines = ref([]);
const loading = ref(true);
const selectedPipelineId = ref(null);
const selectedMeses = ref(12);
const trendChart = ref(null);
let chartInstance = null;

// --- Valores agregados por moneda (toma la primera moneda con valor, o COP) ---
function primeraMoneda(map) {
  if (!map) return null;
  const entries = Object.entries(map);
  if (!entries.length) return null;
  const conValor = entries.find(([, v]) => Number(v) > 0) || entries[0];
  return { moneda: conValor[0], valor: conValor[1] };
}
function fmtMapa(map) {
  const m = primeraMoneda(map);
  return m ? fmtCurrency(m.valor, m.moneda) : fmtCurrency(0);
}

const valorPipeline = computed(() => fmtMapa(stats.value?.valorPipelinePorMoneda));
const valorGanado = computed(() => fmtMapa(stats.value?.valorGanadoPorMoneda));
const valorPerdido = computed(() => fmtMapa(stats.value?.valorPerdidoPorMoneda));
const tasaConversion = computed(() => (stats.value ? Math.round(stats.value.tasaConversion) + '%' : '—'));
const totalCerradas = computed(() => (stats.value ? stats.value.totalGanadas + stats.value.totalPerdidas + stats.value.totalNoConcretadas : 0));

const TONE = { ABIERTA: 'info', SEGUIMIENTO: 'warning', GANADA: 'success', CONTRATADA: 'accent', PERDIDA: 'danger', NO_CONCRETADA: 'neutral' };
function toneEstado(e) { return TONE[e] || 'neutral'; }

function fmtCurrency(v, m) { return formatCurrency(v, m); }
function goToDetalle(id) { router.push(`/oportunidades/${id}`); }

function funnelWidth(cantidad) {
  if (!stats.value?.porEtapa?.length) return 0;
  const max = Math.max(...stats.value.porEtapa.map((e) => e.cantidad));
  return max > 0 ? (cantidad / max) * 100 : 0;
}

async function loadStats() {
  loading.value = true;
  try {
    const params = { meses: selectedMeses.value };
    if (selectedPipelineId.value) params.pipeline_id = selectedPipelineId.value;
    stats.value = await dashboardService.obtenerStats(params);
    await nextTick();
    renderTrendChart();
  } catch (err) {
    console.error('Error cargando stats:', err);
  } finally {
    loading.value = false;
  }
}

// Tendencia con Chart.js (CDN), re-tematizada a la paleta "instrumento" (clara).
function cssVar(name) {
  return getComputedStyle(document.documentElement).getPropertyValue(name).trim();
}
async function renderTrendChart() {
  if (!trendChart.value || !stats.value?.porMes?.length) return;
  if (!window.Chart) {
    await new Promise((resolve, reject) => {
      const s = document.createElement('script');
      s.src = 'https://cdnjs.cloudflare.com/ajax/libs/Chart.js/4.4.1/chart.umd.js';
      s.onload = resolve; s.onerror = reject;
      document.head.appendChild(s);
    });
  }
  if (chartInstance) chartInstance.destroy();
  const grid = cssVar('--gc-border');
  const tick = cssVar('--gc-text-3');
  const ctx = trendChart.value.getContext('2d');
  chartInstance = new window.Chart(ctx, {
    type: 'bar',
    data: {
      labels: stats.value.porMes.map((m) => m.mesLabel),
      datasets: [
        { label: 'Nuevas', data: stats.value.porMes.map((m) => m.nuevas), backgroundColor: cssVar('--gc-info'), borderRadius: 3, barPercentage: 0.7 },
        { label: 'Ganadas', data: stats.value.porMes.map((m) => m.ganadas), backgroundColor: cssVar('--gc-success'), borderRadius: 3, barPercentage: 0.7 },
        { label: 'Perdidas', data: stats.value.porMes.map((m) => m.perdidas), backgroundColor: cssVar('--gc-danger'), borderRadius: 3, barPercentage: 0.7 },
      ],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: { legend: { display: true, position: 'top', labels: { color: tick, font: { size: 11 }, boxWidth: 12, padding: 16 } } },
      scales: {
        x: { grid: { color: grid }, ticks: { color: tick, font: { size: 10 }, maxRotation: 45 } },
        y: { grid: { color: grid }, ticks: { color: tick, font: { size: 10 }, stepSize: 1 }, beginAtZero: true },
      },
    },
  });
}

onMounted(async () => {
  setRegions({ master: false, aside: false }); // Panel: superficie completa
  try { pipelines.value = await pipelineService.listarActivos(); } catch { /* noop */ }
  await loadStats();
});
onUnmounted(() => { if (chartInstance) chartInstance.destroy(); });
</script>

<style scoped>
.dash { padding: var(--gc-space-6); display: flex; flex-direction: column; gap: var(--gc-space-5); }
.dash__head { display: flex; align-items: flex-start; justify-content: space-between; gap: var(--gc-space-4); }
.dash__title { font-size: var(--gc-fs-2xl); }
.dash__sub { margin-top: 2px; font-size: var(--gc-fs-md); color: var(--gc-text-2); }
.dash__filters { display: flex; gap: var(--gc-space-2); }
.dash__select {
  height: 34px;
  padding: 0 var(--gc-space-3);
  background: var(--gc-surface);
  border: 1px solid var(--gc-border);
  border-radius: var(--gc-radius-md);
  color: var(--gc-text);
  font-size: var(--gc-fs-sm);
}
.dash__state { display: flex; justify-content: center; padding: var(--gc-space-12); color: var(--gc-text-3); }

.dash__grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: var(--gc-space-5); }
.dash__panel { display: flex; flex-direction: column; gap: var(--gc-space-3); padding: var(--gc-space-5); background: var(--gc-surface); border: 1px solid var(--gc-border); border-radius: var(--gc-radius-lg); }
.dash__h2 { font-size: var(--gc-fs-xs); text-transform: uppercase; letter-spacing: 0.06em; color: var(--gc-text-3); }
.dash__list { display: flex; flex-direction: column; }
.dash__chart { position: relative; height: 280px; }

/* Barras del embudo */
.bars { display: flex; flex-direction: column; gap: var(--gc-space-3); }
.bars__row { display: flex; flex-direction: column; gap: var(--gc-space-1); }
.bars__head { display: flex; align-items: center; justify-content: space-between; }
.bars__label { display: flex; align-items: center; gap: var(--gc-space-2); font-size: var(--gc-fs-md); color: var(--gc-text); }
.bars__dot { width: 8px; height: 8px; border-radius: var(--gc-radius-full); }
.bars__val { font-size: var(--gc-fs-xs); color: var(--gc-text-3); }
.bars__track { height: 8px; background: var(--gc-surface-2); border-radius: var(--gc-radius-full); overflow: hidden; }
.bars__fill { height: 100%; border-radius: var(--gc-radius-full); transition: width var(--gc-t-normal); }

/* Empresa / Top filas */
.emp { display: flex; flex-direction: column; gap: 2px; }
.emp__name { font-size: var(--gc-fs-md); }
.emp__meta { font-size: var(--gc-fs-xs); color: var(--gc-text-3); }
.emp__valor { font-size: var(--gc-fs-sm); color: var(--gc-text-2); }
.top { display: flex; flex-direction: column; gap: 2px; }
.top__rank { font-size: var(--gc-fs-sm); color: var(--gc-text-3); }
.top__name { font-size: var(--gc-fs-md); }
.top__meta { font-size: var(--gc-fs-xs); color: var(--gc-text-3); }
.top__valor { font-size: var(--gc-fs-sm); color: var(--gc-text-2); }

@media (max-width: 980px) {
  .dash__grid { grid-template-columns: 1fr; }
}
</style>
