<template>
  <AppLayout>
    <div class="facturacion">
      <!-- Header con selector de mes -->
      <section class="page-header animate-slideUp">
        <div>
          <h1 class="page-title gradient-text">Facturación</h1>
          <p class="page-subtitle">{{ mesLabel }}</p>
        </div>
        <div class="period-controls">
          <button class="btn btn--ghost btn--sm" @click="cambiarMes(-1)" title="Mes anterior">
            <Icon name="chevron-left" :size="16" />
          </button>
          <input
            type="month"
            v-model="anioMes"
            class="period-input"
            @change="loadData"
          />
          <button class="btn btn--ghost btn--sm" @click="cambiarMes(1)" title="Mes siguiente">
            <Icon name="chevron-right" :size="16" />
          </button>
          <button v-if="!esMesActual" class="btn btn--ghost btn--sm" @click="irAlMesActual">
            <Icon name="calendar" :size="14" /> Hoy
          </button>
        </div>
      </section>

      <!-- Nota TRM (aproximaciones) -->
      <div v-if="vista?.notaTrm" class="trm-note glass animate-slideUp">
        <Icon name="info" :size="14" color="var(--warning)" />
        <span>{{ vista.notaTrm }}</span>
      </div>

      <!-- KPIs clickeables -->
      <section class="kpi-row animate-slideUp delay-1">
        <div
          :class="['kpi glass', activeFilter === 'arrastreMesesAnteriores' && 'kpi--selected', conteosKpi.arrastreMesesAnteriores > 0 && 'kpi--alert']"
          @click="toggleFilter('arrastreMesesAnteriores')"
        >
          <div class="kpi__icon kpi__icon--error"><Icon name="alert-circle" :size="20" /></div>
          <div class="kpi__data">
            <span class="kpi__value kpi__value--error">{{ conteosKpi.arrastreMesesAnteriores || 0 }}</span>
            <span class="kpi__label">Arrastre</span>
            <span class="kpi__sub">{{ fmtCop(montosKpi.arrastreMesesAnteriores) }}</span>
          </div>
        </div>

        <div
          :class="['kpi glass', activeFilter === 'mesActualVencidos' && 'kpi--selected', conteosKpi.mesActualVencidos > 0 && 'kpi--alert']"
          @click="toggleFilter('mesActualVencidos')"
        >
          <div class="kpi__icon kpi__icon--warning"><Icon name="clock" :size="20" /></div>
          <div class="kpi__data">
            <span class="kpi__value kpi__value--warning">{{ conteosKpi.mesActualVencidos || 0 }}</span>
            <span class="kpi__label">Vencidos del mes</span>
            <span class="kpi__sub">{{ fmtCop(montosKpi.mesActualVencidos) }}</span>
          </div>
        </div>

        <div
          :class="['kpi glass', activeFilter === 'mesActualPorVencer' && 'kpi--selected']"
          @click="toggleFilter('mesActualPorVencer')"
        >
          <div class="kpi__icon kpi__icon--primary"><Icon name="calendar" :size="20" /></div>
          <div class="kpi__data">
            <span class="kpi__value kpi__value--primary">{{ conteosKpi.mesActualPorVencer || 0 }}</span>
            <span class="kpi__label">Por vencer</span>
            <span class="kpi__sub">{{ fmtCop(montosKpi.mesActualPorVencer) }}</span>
          </div>
        </div>

        <div class="kpi glass kpi--total">
          <div class="kpi__icon kpi__icon--success"><Icon name="check-circle" :size="20" /></div>
          <div class="kpi__data">
            <span class="kpi__value kpi__value--success">{{ fmtPct(vista?.kpis?.porcentajeCumplimiento) }}</span>
            <span class="kpi__label">Cumplimiento</span>
            <span class="kpi__sub">{{ fmtCop(vista?.kpis?.totalFacturadoCOP) }} / {{ fmtCop(vista?.kpis?.totalPresupuestadoCOP) }}</span>
          </div>
        </div>
      </section>

      <!-- Distribución por estado (chip row) -->
      <section v-if="distribucionEntries.length" class="distrib-row animate-slideUp delay-1">
        <span class="distrib-label">Distribución por estado:</span>
        <span
          v-for="[estado, cantidad] in distribucionEntries"
          :key="estado"
          :class="['estado-pill', `estado-pill--${estado.toLowerCase()}`]"
        >
          {{ estadoLabel(estado) }} <strong>{{ cantidad }}</strong>
        </span>
      </section>

      <!-- Loading -->
      <div v-if="loading" class="loading-state">
        <Icon name="loader" :size="24" class="animate-spin" /> Cargando...
      </div>

      <!-- Filtro activo: muestra solo una de las 3 listas -->
      <section v-else-if="activeFilter" class="section animate-slideUp delay-2">
        <div class="section-header">
          <h2 :class="['section-title', filterTitleClass]">
            <Icon :name="filterIcon" :size="18" :color="filterColor" />
            {{ filterTitle }} <span class="section-count">{{ filteredList.length }}</span>
          </h2>
          <button class="btn btn--ghost btn--sm" @click="activeFilter = null">
            <Icon name="x" :size="14" /> Ver todo
          </button>
        </div>

        <div v-if="!filteredList.length" class="empty-state glass">
          <Icon name="check-circle" :size="32" color="var(--text-muted)" />
          <p>No hay elementos para este filtro</p>
        </div>

        <div v-else class="fp-list">
          <FormaPagoCard
            v-for="fp in filteredList"
            :key="fp.id"
            :item="fp"
            @click="openGestion(fp)"
          />
        </div>
      </section>

      <!-- Vista por defecto: las 3 secciones del backend -->
      <template v-else>
        <!-- Arrastre (meses anteriores) -->
        <section v-if="vista?.arrastreMesesAnteriores?.length" class="section animate-slideUp delay-2">
          <div class="section-header">
            <h2 class="section-title section-title--error">
              <Icon name="alert-circle" :size="18" color="var(--error)" />
              Arrastre de meses anteriores
              <span class="section-count">{{ vista.arrastreMesesAnteriores.length }}</span>
            </h2>
          </div>
          <div class="fp-list">
            <FormaPagoCard
              v-for="fp in vista.arrastreMesesAnteriores"
              :key="fp.id"
              :item="fp"
              variant="arrastre"
              @click="openGestion(fp)"
            />
          </div>
        </section>

        <!-- Vencidos del mes -->
        <section v-if="vista?.mesActualVencidos?.length" class="section animate-slideUp delay-3">
          <div class="section-header">
            <h2 class="section-title section-title--warning">
              <Icon name="clock" :size="18" color="var(--warning)" />
              Vencidos en {{ mesLabel }}
              <span class="section-count">{{ vista.mesActualVencidos.length }}</span>
            </h2>
          </div>
          <div class="fp-list">
            <FormaPagoCard
              v-for="fp in vista.mesActualVencidos"
              :key="fp.id"
              :item="fp"
              variant="vencido"
              @click="openGestion(fp)"
            />
          </div>
        </section>

        <!-- Por vencer del mes -->
        <section v-if="vista?.mesActualPorVencer?.length" class="section animate-slideUp delay-3">
          <div class="section-header">
            <h2 class="section-title">
              <Icon name="calendar" :size="18" color="var(--primary)" />
              Por vencer en {{ mesLabel }}
              <span class="section-count">{{ vista.mesActualPorVencer.length }}</span>
            </h2>
          </div>
          <div class="fp-list">
            <FormaPagoCard
              v-for="fp in vista.mesActualPorVencer"
              :key="fp.id"
              :item="fp"
              @click="openGestion(fp)"
            />
          </div>
        </section>

        <!-- Sin nada -->
        <div
          v-if="!vista?.arrastreMesesAnteriores?.length && !vista?.mesActualVencidos?.length && !vista?.mesActualPorVencer?.length"
          class="empty-state glass"
        >
          <Icon name="check-circle" :size="48" color="var(--success)" />
          <p>No hay formas de pago activas para {{ mesLabel }}</p>
        </div>
      </template>
    </div>

    <!-- Panel de Gestión lateral (se adapta en D2.0c) -->
    <GestionPanel
      v-if="selectedFp"
      :forma-pago="selectedFp"
      @close="selectedFp = null"
      @updated="loadData"
    />
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import AppLayout from '@/components/layout/AppLayout.vue';
import Icon from '@/components/ui/Icon.vue';
import GestionPanel from '@/components/facturacion/GestionPanel.vue';
import FormaPagoCard from '@/components/facturacion/FormaPagoCard.vue';
import { facturacionService } from '@/services/facturacion.service';

const loading = ref(true);
const vista = ref(null);                    // VistaPeriodoResponse del backend
const selectedFp = ref(null);
const activeFilter = ref(null);             // 'arrastreMesesAnteriores' | 'mesActualVencidos' | 'mesActualPorVencer'

// Mes activo (anio-mes formato YYYY-MM)
const today = new Date();
const anioMes = ref(`${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}`);

const meses = ['', 'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'];
const mesLabel = computed(() => {
  if (!anioMes.value) return '';
  const [a, m] = anioMes.value.split('-');
  return `${meses[parseInt(m)]} ${a}`;
});
const esMesActual = computed(() => vista.value?.esMesActual ?? false);

// Conteos y montos de los KPIs (mapas del backend)
const conteosKpi = computed(() => vista.value?.kpis?.conteosPorGrupo || {});
const montosKpi = computed(() => vista.value?.kpis?.montosPorGrupoCOP || {});

// Distribución por estado (orden alfabético — backend ya lo entrega ordenado)
const distribucionEntries = computed(() => {
  const dist = vista.value?.kpis?.distribucionEstado || {};
  return Object.entries(dist);
});

// Lista filtrada cuando hay KPI seleccionado
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

const filterTitleClass = computed(() => {
  switch (activeFilter.value) {
    case 'arrastreMesesAnteriores': return 'section-title--error';
    case 'mesActualVencidos': return 'section-title--warning';
    default: return '';
  }
});

const filterIcon = computed(() => {
  switch (activeFilter.value) {
    case 'arrastreMesesAnteriores': return 'alert-circle';
    case 'mesActualVencidos': return 'clock';
    case 'mesActualPorVencer': return 'calendar';
    default: return 'calendar';
  }
});

const filterColor = computed(() => {
  switch (activeFilter.value) {
    case 'arrastreMesesAnteriores': return 'var(--error)';
    case 'mesActualVencidos': return 'var(--warning)';
    case 'mesActualPorVencer': return 'var(--primary)';
    default: return 'var(--primary)';
  }
});

function toggleFilter(filter) {
  activeFilter.value = activeFilter.value === filter ? null : filter;
}

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

function openGestion(fp) {
  selectedFp.value = fp;
}

// Helpers de formato
function fmtCop(v) {
  if (v == null) return '$0';
  return new Intl.NumberFormat('es-CO', {
    style: 'currency', currency: 'COP', maximumFractionDigits: 0,
  }).format(v);
}

function fmtPct(v) {
  if (v == null) return '0%';
  // Backend devuelve fracción 0..1 (ej. 0.85 = 85%)
  const pct = Number(v) * 100;
  return `${pct.toFixed(0)}%`;
}

function estadoLabel(estado) {
  return {
    PENDIENTE_GESTION: 'Pendiente',
    EN_GESTION: 'En gestión',
    COMPROMETIDO: 'Comprometido',
    PARCIALMENTE_CUMPLIDO: 'Parcial',
    REPROGRAMADO: 'Reprogramado',
    CUMPLIDO: 'Cumplido',
    NO_LOGRADO: 'No logrado',
  }[estado] || estado;
}
</script>

<style scoped>
.facturacion {
  display: flex;
  flex-direction: column;
  gap: var(--space-5);
}

/* Header */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: var(--space-4);
}
.page-title {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  margin: 0;
}
.page-subtitle {
  color: var(--text-secondary);
  margin: var(--space-1) 0 0 0;
  font-size: var(--text-sm);
}
.period-controls {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}
.period-input {
  background: var(--bg-elevated);
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  padding: var(--space-2) var(--space-3);
  color: var(--text-primary);
  font-family: var(--font-body);
  font-size: var(--text-sm);
  color-scheme: dark;
}
.period-input:focus {
  outline: none;
  border-color: var(--primary);
}

/* Nota TRM */
.trm-note {
  padding: var(--space-3) var(--space-4);
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--text-xs);
  color: var(--warning);
  border-left: 3px solid var(--warning);
}

/* KPIs */
.kpi-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--space-4);
}
.kpi {
  padding: var(--space-4);
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  gap: var(--space-4);
  transition: all 0.2s;
  cursor: pointer;
  border: 1px solid transparent;
}
.kpi:hover {
  border-color: var(--glass-border);
  background: rgba(255, 255, 255, 0.03);
}
.kpi--selected {
  border-color: var(--primary) !important;
  background: rgba(0, 212, 255, 0.05) !important;
}
.kpi--alert {
  border-color: rgba(244, 63, 94, 0.3);
}
.kpi--total {
  cursor: default;
}
.kpi--total:hover {
  border-color: transparent;
  background: var(--glass-bg);
}
.kpi__icon {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.kpi__icon--error    { background: var(--error-soft);    color: var(--error); }
.kpi__icon--warning  { background: var(--warning-soft);  color: var(--warning); }
.kpi__icon--success  { background: var(--success-soft);  color: var(--success); }
.kpi__icon--primary  { background: var(--primary-soft);  color: var(--primary); }
.kpi__data {
  display: flex;
  flex-direction: column;
  min-width: 0;
}
.kpi__value {
  font-family: var(--font-display);
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
}
.kpi__value--error   { color: var(--error); }
.kpi__value--warning { color: var(--warning); }
.kpi__value--success { color: var(--success); }
.kpi__value--primary { color: var(--primary); }
.kpi__label {
  font-size: 10px;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.5px;
  font-weight: var(--font-semibold);
}
.kpi__sub {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
  font-family: var(--font-mono, monospace);
  margin-top: 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* Distribución */
.distrib-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: var(--space-2);
  padding: 0 var(--space-1);
}
.distrib-label {
  font-size: var(--text-xs);
  color: var(--text-muted);
  margin-right: var(--space-2);
}

/* Estado pills BEM */
.estado-pill {
  padding: 2px 10px;
  border-radius: var(--radius-full);
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
.estado-pill strong {
  font-weight: var(--font-bold);
  margin-left: 2px;
}
.estado-pill--pendiente_gestion    { background: var(--bg-surface);     color: var(--text-secondary); }
.estado-pill--en_gestion           { background: var(--primary-soft);   color: var(--primary); }
.estado-pill--comprometido         { background: var(--secondary-soft); color: var(--secondary); }
.estado-pill--parcialmente_cumplido{ background: var(--warning-soft);   color: var(--warning); }
.estado-pill--reprogramado         { background: var(--warning-soft);   color: var(--warning); opacity: 0.85; }
.estado-pill--cumplido             { background: var(--success-soft);   color: var(--success); }
.estado-pill--no_logrado           { background: var(--error-soft);     color: var(--error); }

/* Loading / empty */
.loading-state {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-3);
  padding: var(--space-8);
  color: var(--text-muted);
  font-size: var(--text-sm);
}
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--space-3);
  padding: var(--space-10);
  border-radius: var(--radius-xl);
  color: var(--text-muted);
  font-size: var(--text-sm);
}

/* Sección genérica */
.section {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.section-title {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-family: var(--font-display);
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0;
}
.section-title--error   { color: var(--error); }
.section-title--warning { color: var(--warning); }
.section-count {
  font-size: var(--text-xs);
  color: var(--text-muted);
  font-weight: var(--font-regular);
  background: var(--bg-surface);
  padding: 1px 8px;
  border-radius: var(--radius-full);
  margin-left: var(--space-1);
}

/* Lista */
.fp-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

/* Botones reutilizables */
.btn {
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-4);
  border-radius: var(--radius-full);
  font-family: var(--font-body);
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  cursor: pointer;
  transition: all 0.15s;
  border: 1px solid transparent;
  background: transparent;
  color: var(--text-secondary);
}
.btn--sm {
  padding: var(--space-1) var(--space-3);
  font-size: 11px;
}
.btn--ghost {
  border-color: var(--glass-border);
}
.btn--ghost:hover {
  background: var(--bg-hover);
  color: var(--text-primary);
}

/* Responsive */
@media (max-width: 1100px) {
  .kpi-row { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 600px) {
  .kpi-row { grid-template-columns: 1fr; }
  .page-header { flex-direction: column; align-items: flex-start; }
}
</style>
