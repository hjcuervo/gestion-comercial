<template>
  <AppLayout>
    <div class="dashboard">
      <section class="dashboard__header animate-slideUp">
        <div class="header-left">
          <h1 class="page-title gradient-text">Dashboard</h1>
          <p class="page-subtitle">Análisis de oportunidades comerciales</p>
        </div>
        <div class="header-filters">
          <select v-model="selectedPipelineId" class="filter-select" @change="loadStats">
            <option :value="null">Todos los pipelines</option>
            <option v-for="p in pipelines" :key="p.id" :value="p.id">{{ p.nombre }}</option>
          </select>
          <select v-model="selectedMeses" class="filter-select" @change="loadStats">
            <option :value="6">Últimos 6 meses</option>
            <option :value="12">Últimos 12 meses</option>
            <option :value="24">Últimos 24 meses</option>
          </select>
        </div>
      </section>

      <div v-if="loading" class="loading-state"><Icon name="loader" :size="32" class="animate-spin" /><p>Cargando estadísticas...</p></div>

      <template v-else-if="stats">
        <!-- KPI Cards -->
        <section class="kpi-grid animate-slideUp delay-1">
          <div class="kpi-card">
            <div class="kpi-icon kpi-icon--pipeline"><Icon name="pipeline" :size="20" /></div>
            <div class="kpi-body">
              <div class="kpi-valores">
                <span v-for="(valor, moneda) in stats.valorPipelinePorMoneda" :key="'pip-'+moneda" class="kpi-valor-line">
                  {{ fmtCurrency(valor, moneda) }}
                </span>
                <span v-if="!Object.keys(stats.valorPipelinePorMoneda || {}).length" class="kpi-valor-line">$0</span>
              </div>
              <span class="kpi-label">Valor en Pipeline</span>
            </div>
            <span class="kpi-count">{{ stats.totalAbiertas }} abiertas</span>
          </div>
          <div class="kpi-card">
            <div class="kpi-icon kpi-icon--ganada"><Icon name="trophy" :size="20" /></div>
            <div class="kpi-body">
              <div class="kpi-valores">
                <span v-for="(valor, moneda) in stats.valorGanadoPorMoneda" :key="'gan-'+moneda" class="kpi-valor-line">
                  {{ fmtCurrency(valor, moneda) }}
                </span>
                <span v-if="!Object.keys(stats.valorGanadoPorMoneda || {}).length" class="kpi-valor-line">$0</span>
              </div>
              <span class="kpi-label">Valor Ganado</span>
            </div>
            <span class="kpi-count">{{ stats.totalGanadas }} ganadas</span>
          </div>
          <div class="kpi-card">
            <div class="kpi-icon kpi-icon--perdida"><Icon name="trending-down" :size="20" /></div>
            <div class="kpi-body">
              <div class="kpi-valores">
                <span v-for="(valor, moneda) in stats.valorPerdidoPorMoneda" :key="'per-'+moneda" class="kpi-valor-line">
                  {{ fmtCurrency(valor, moneda) }}
                </span>
                <span v-if="!Object.keys(stats.valorPerdidoPorMoneda || {}).length" class="kpi-valor-line">$0</span>
              </div>
              <span class="kpi-label">Valor Perdido</span>
            </div>
            <span class="kpi-count">{{ stats.totalPerdidas }} perdidas</span>
          </div>
          <div class="kpi-card">
            <div class="kpi-icon kpi-icon--conversion"><Icon name="chart" :size="20" /></div>
            <div class="kpi-body">
              <span class="kpi-value-single">{{ stats.tasaConversion }}%</span>
              <span class="kpi-label">Tasa de Conversión</span>
            </div>
            <span class="kpi-count">{{ stats.totalGanadas + stats.totalPerdidas + stats.totalNoConcretadas }} cerradas</span>
          </div>
        </section>

        <!-- Row 1: Top Oportunidades + Por Empresa -->
        <section class="charts-row animate-slideUp delay-2">
          <div class="chart-card glass">
            <div class="chart-card__header">
              <h3 class="chart-card__title"><Icon name="wallet" :size="16" color="var(--primary)" /> Top Oportunidades</h3>
            </div>
            <div v-if="!stats.topOportunidades?.length" class="chart-empty">Sin oportunidades abiertas</div>
            <div v-else class="top-list">
              <div v-for="(op, i) in stats.topOportunidades" :key="op.id" class="top-item top-item--clickable" @click="goToDetalle(op.id)">
                <span class="top-rank">{{ i + 1 }}</span>
                <div class="top-info">
                  <span class="top-name">{{ op.nombre }}</span>
                  <span class="top-empresa">{{ op.empresaNombre }} · {{ op.etapaNombre }}</span>
                </div>
                <div class="top-right">
                  <span class="top-valor">{{ fmtCurrency(op.valorEstimado, op.moneda) }}</span>
                  <span v-if="op.probabilidad != null" class="top-prob">{{ op.probabilidad }}%</span>
                </div>
                <Icon name="chevron-right" :size="14" class="top-arrow" />
              </div>
            </div>
          </div>
          <div class="chart-card glass">
            <div class="chart-card__header">
              <h3 class="chart-card__title"><Icon name="business" :size="16" color="var(--accent)" /> Por Empresa</h3>
            </div>
            <div v-if="!stats.porEmpresa?.length" class="chart-empty">Sin datos de empresas</div>
            <div v-else class="empresa-list">
              <div v-for="emp in stats.porEmpresa" :key="emp.empresaId" class="empresa-item">
                <div class="empresa-item__info">
                  <span class="empresa-item__name">{{ emp.empresaNombre }}</span>
                  <div class="empresa-item__badges">
                    <span class="mini-badge mini-badge--open">{{ emp.abiertas }} abiertas</span>
                    <span class="mini-badge mini-badge--won">{{ emp.ganadas }} ganadas</span>
                  </div>
                </div>
                <span class="empresa-item__valor">{{ fmtCurrency(emp.valorTotal) }}</span>
              </div>
            </div>
          </div>
        </section>

        <!-- Row 2: Embudo + Tendencia -->
        <section class="charts-row animate-slideUp delay-3">
          <div class="chart-card glass">
            <div class="chart-card__header">
              <h3 class="chart-card__title"><Icon name="pipeline" :size="16" color="var(--primary)" /> Embudo por Etapa</h3>
            </div>
            <div v-if="!stats.porEtapa?.length" class="chart-empty">Sin datos de etapas</div>
            <div v-else class="funnel">
              <div v-for="etapa in stats.porEtapa" :key="etapa.etapaId" class="funnel-row">
                <span class="funnel-label">{{ etapa.etapaNombre }}</span>
                <div class="funnel-bar-wrap">
                  <div class="funnel-bar" :style="{ width: funnelWidth(etapa.cantidad) + '%', background: etapa.etapaColor || 'var(--primary)' }">
                    <span class="funnel-bar__value">{{ etapa.cantidad }}</span>
                  </div>
                </div>
                <span class="funnel-amount">{{ fmtCurrency(etapa.valorTotal) }}</span>
              </div>
            </div>
          </div>
          <div class="chart-card glass">
            <div class="chart-card__header">
              <h3 class="chart-card__title"><Icon name="trending-up" :size="16" color="var(--secondary)" /> Tendencia Mensual</h3>
            </div>
            <div class="chart-canvas-wrap"><canvas ref="trendChart"></canvas></div>
          </div>
        </section>
      </template>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue';
import { useRouter } from 'vue-router';
import AppLayout from '@/components/layout/AppLayout.vue';
import Icon from '@/components/ui/Icon.vue';
import { dashboardService } from '@/services/dashboard.service';
import { pipelineService } from '@/services/pipeline.service';
import { formatCurrency } from '@/utils/currency';

const router = useRouter();
const stats = ref(null);
const pipelines = ref([]);
const loading = ref(true);
const selectedPipelineId = ref(null);
const selectedMeses = ref(12);
const trendChart = ref(null);
let chartInstance = null;

onMounted(async () => {
  try { pipelines.value = await pipelineService.listarActivos(); } catch {}
  await loadStats();
});

async function loadStats() {
  loading.value = true;
  try {
    const params = { meses: selectedMeses.value };
    if (selectedPipelineId.value) params.pipeline_id = selectedPipelineId.value;
    stats.value = await dashboardService.obtenerStats(params);
    await nextTick();
    renderTrendChart();
  } catch (err) { console.error('Error cargando stats:', err); }
  finally { loading.value = false; }
}

function fmtCurrency(value, moneda) { return formatCurrency(value, moneda); }
function goToDetalle(id) { router.push(`/oportunidades/${id}`); }

function funnelWidth(cantidad) {
  if (!stats.value?.porEtapa?.length) return 0;
  const max = Math.max(...stats.value.porEtapa.map(e => e.cantidad));
  return max > 0 ? (cantidad / max) * 100 : 0;
}

async function renderTrendChart() {
  if (!trendChart.value || !stats.value?.porMes?.length) return;
  if (!window.Chart) {
    await new Promise((resolve, reject) => {
      const script = document.createElement('script');
      script.src = 'https://cdnjs.cloudflare.com/ajax/libs/Chart.js/4.4.1/chart.umd.js';
      script.onload = resolve; script.onerror = reject;
      document.head.appendChild(script);
    });
  }
  if (chartInstance) chartInstance.destroy();
  const ctx = trendChart.value.getContext('2d');
  chartInstance = new window.Chart(ctx, {
    type: 'bar',
    data: {
      labels: stats.value.porMes.map(m => m.mesLabel),
      datasets: [
        { label: 'Nuevas', data: stats.value.porMes.map(m => m.nuevas), backgroundColor: 'rgba(0, 212, 255, 0.7)', borderRadius: 4, barPercentage: 0.7 },
        { label: 'Ganadas', data: stats.value.porMes.map(m => m.ganadas), backgroundColor: 'rgba(16, 185, 129, 0.7)', borderRadius: 4, barPercentage: 0.7 },
        { label: 'Perdidas', data: stats.value.porMes.map(m => m.perdidas), backgroundColor: 'rgba(244, 63, 94, 0.7)', borderRadius: 4, barPercentage: 0.7 },
      ],
    },
    options: {
      responsive: true, maintainAspectRatio: false,
      plugins: { legend: { display: true, position: 'top', labels: { color: 'rgba(255,255,255,0.6)', font: { size: 11 }, boxWidth: 12, padding: 16 } } },
      scales: {
        x: { grid: { color: 'rgba(255,255,255,0.04)' }, ticks: { color: 'rgba(255,255,255,0.4)', font: { size: 10 }, maxRotation: 45 } },
        y: { grid: { color: 'rgba(255,255,255,0.04)' }, ticks: { color: 'rgba(255,255,255,0.4)', font: { size: 10 }, stepSize: 1 }, beginAtZero: true },
      },
    },
  });
}
</script>

<style scoped>
.dashboard { display: flex; flex-direction: column; gap: var(--space-6); }
.dashboard__header { display: flex; justify-content: space-between; align-items: center; gap: var(--space-4); flex-wrap: wrap; }
.page-title { font-family: var(--font-display); font-size: var(--text-3xl); font-weight: 700; margin: 0; }
.page-subtitle { color: var(--text-tertiary); font-size: var(--text-sm); margin: var(--space-1) 0 0; }
.header-filters { display: flex; gap: var(--space-3); }
.filter-select { background: var(--glass-bg); border: 1px solid var(--glass-border); border-radius: var(--radius-md); color: var(--text-primary); font-family: var(--font-body); font-size: var(--text-xs); padding: var(--space-2) var(--space-4); appearance: none; background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 24 24' fill='none' stroke='rgba(255,255,255,0.3)' stroke-width='2'%3E%3Cpath d='M6 9l6 6 6-6'/%3E%3C/svg%3E"); background-repeat: no-repeat; background-position: right 8px center; padding-right: 28px; }
.filter-select:focus { outline: none; border-color: var(--primary); }
.filter-select option { background: var(--bg-elevated); }
.loading-state { display: flex; flex-direction: column; align-items: center; justify-content: center; gap: var(--space-3); padding: var(--space-8); color: var(--text-tertiary); font-size: var(--text-sm); }

.kpi-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: var(--space-4); }
.kpi-card { background: var(--glass-bg); border: 1px solid var(--glass-border); border-radius: var(--radius-xl); padding: var(--space-5); display: flex; flex-direction: column; gap: var(--space-3); }
.kpi-icon { width: 40px; height: 40px; border-radius: var(--radius-md); display: flex; align-items: center; justify-content: center; }
.kpi-icon--pipeline { background: var(--primary-soft); color: var(--primary); }
.kpi-icon--ganada { background: var(--success-soft); color: var(--success); }
.kpi-icon--perdida { background: var(--error-soft); color: var(--error); }
.kpi-icon--conversion { background: var(--secondary-soft); color: var(--secondary); }
.kpi-body { display: flex; flex-direction: column; }
.kpi-valores { display: flex; flex-direction: column; gap: 2px; }
.kpi-valor-line { font-family: var(--font-display); font-size: var(--text-lg); font-weight: 700; color: var(--text-primary); line-height: 1.2; }
.kpi-value-single { font-family: var(--font-display); font-size: var(--text-2xl); font-weight: 700; color: var(--text-primary); }
.kpi-label { font-size: var(--text-xs); color: var(--text-tertiary); margin-top: 4px; }
.kpi-count { font-size: var(--text-xs); color: var(--text-muted); font-family: var(--font-mono); }

.charts-row { display: grid; grid-template-columns: 1fr 1fr; gap: var(--space-5); }
.chart-card { border-radius: var(--radius-xl); padding: var(--space-5); display: flex; flex-direction: column; }
.chart-card__header { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--space-4); }
.chart-card__title { display: flex; align-items: center; gap: var(--space-2); font-family: var(--font-display); font-size: var(--text-sm); font-weight: 600; color: var(--text-primary); margin: 0; }
.chart-empty { display: flex; align-items: center; justify-content: center; height: 120px; color: var(--text-muted); font-size: var(--text-xs); }

.funnel { display: flex; flex-direction: column; gap: var(--space-3); }
.funnel-row { display: flex; align-items: center; gap: var(--space-3); }
.funnel-label { font-size: var(--text-xs); color: var(--text-secondary); min-width: 100px; text-align: right; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.funnel-bar-wrap { flex: 1; height: 28px; background: var(--bg-surface); border-radius: var(--radius-sm); overflow: hidden; }
.funnel-bar { height: 100%; border-radius: var(--radius-sm); display: flex; align-items: center; padding-left: var(--space-3); min-width: 30px; transition: width 0.6s ease; }
.funnel-bar__value { font-size: 11px; font-weight: 700; color: #fff; text-shadow: 0 1px 2px rgba(0,0,0,0.3); }
.funnel-amount { font-size: var(--text-xs); color: var(--text-muted); font-family: var(--font-mono); min-width: 60px; text-align: right; }
.chart-canvas-wrap { position: relative; height: 260px; }

.top-list { display: flex; flex-direction: column; gap: var(--space-2); max-height: 360px; overflow-y: auto; }
.top-item { display: flex; align-items: center; gap: var(--space-3); padding: var(--space-3); border-radius: var(--radius-md); transition: background 0.15s; }
.top-item--clickable { cursor: pointer; } .top-item--clickable:hover { background: rgba(0, 212, 255, 0.06); }
.top-rank { font-family: var(--font-mono); font-size: var(--text-xs); color: var(--text-muted); min-width: 20px; text-align: center; font-weight: 600; }
.top-info { flex: 1; min-width: 0; }
.top-name { display: block; font-size: var(--text-xs); font-weight: 600; color: var(--text-primary); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.top-empresa { display: block; font-size: 10px; color: var(--text-muted); margin-top: 1px; }
.top-right { text-align: right; flex-shrink: 0; }
.top-valor { display: block; font-family: var(--font-mono); font-size: var(--text-xs); font-weight: 600; color: var(--primary); }
.top-prob { display: block; font-size: 9px; color: var(--text-muted); }
.top-arrow { color: var(--text-muted); flex-shrink: 0; opacity: 0; transition: opacity 0.15s; }
.top-item--clickable:hover .top-arrow { opacity: 1; }

.empresa-list { display: flex; flex-direction: column; gap: var(--space-2); max-height: 360px; overflow-y: auto; }
.empresa-item { display: flex; align-items: center; justify-content: space-between; padding: var(--space-3); border-radius: var(--radius-md); transition: background 0.15s; }
.empresa-item:hover { background: rgba(255,255,255,0.02); }
.empresa-item__info { flex: 1; min-width: 0; }
.empresa-item__name { display: block; font-size: var(--text-xs); font-weight: 600; color: var(--text-primary); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.empresa-item__badges { display: flex; gap: var(--space-2); margin-top: 3px; }
.mini-badge { font-size: 9px; padding: 1px 6px; border-radius: var(--radius-full); font-weight: 600; }
.mini-badge--open { background: var(--primary-soft); color: var(--primary); }
.mini-badge--won { background: var(--success-soft); color: var(--success); }
.empresa-item__valor { font-family: var(--font-mono); font-size: var(--text-xs); font-weight: 600; color: var(--text-secondary); flex-shrink: 0; }

@media (max-width: 1100px) { .kpi-grid { grid-template-columns: repeat(2, 1fr); } .charts-row { grid-template-columns: 1fr; } }
@media (max-width: 600px) { .kpi-grid { grid-template-columns: 1fr; } }
</style>
