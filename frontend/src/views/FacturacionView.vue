<template>
  <AppLayout>
    <div class="facturacion">
      <!-- KPIs clickeables -->
      <section class="kpi-row animate-slideUp">
        <div :class="['kpi glass', activeFilter === 'vencidas' && 'kpi--selected', kpis.vencidasCantidad > 0 && 'kpi--alert']" @click="toggleFilter('vencidas')">
          <div class="kpi__icon kpi__icon--error"><Icon name="alert-circle" :size="20" /></div>
          <div class="kpi__data">
            <span class="kpi__value kpi__value--error">{{ kpis.vencidasCantidad || 0 }}</span>
            <span class="kpi__label">Vencidas</span>
            <span class="kpi__sub">{{ fmtCurrency(kpis.vencidasValorCop) }}</span>
          </div>
        </div>
        <div :class="['kpi glass', activeFilter === 'pendientesMes' && 'kpi--selected']" @click="toggleFilter('pendientesMes')">
          <div class="kpi__icon kpi__icon--warning"><Icon name="clock" :size="20" /></div>
          <div class="kpi__data">
            <span class="kpi__value kpi__value--warning">{{ kpis.pendientesMesCantidad || 0 }}</span>
            <span class="kpi__label">Pendientes este mes</span>
            <span class="kpi__sub">{{ fmtCurrency(kpis.pendientesMesValorCop) }}</span>
          </div>
        </div>
        <div :class="['kpi glass', activeFilter === 'facturadasMes' && 'kpi--selected']" @click="toggleFilter('facturadasMes')">
          <div class="kpi__icon kpi__icon--success"><Icon name="check-circle" :size="20" /></div>
          <div class="kpi__data">
            <span class="kpi__value kpi__value--success">{{ kpis.facturadasMesCantidad || 0 }}</span>
            <span class="kpi__label">Facturadas este mes</span>
            <span class="kpi__sub">{{ fmtCurrency(kpis.facturadasMesValorCop) }}</span>
          </div>
        </div>
        <div :class="['kpi glass', activeFilter === 'pendienteTotal' && 'kpi--selected']" @click="toggleFilter('pendienteTotal')">
          <div class="kpi__icon kpi__icon--primary"><Icon name="wallet" :size="20" /></div>
          <div class="kpi__data">
            <span class="kpi__value kpi__value--primary">{{ kpis.pendienteTotalCantidad || 0 }}</span>
            <span class="kpi__label">Pendiente total</span>
            <span class="kpi__sub">{{ fmtCurrency(kpis.pendienteTotalValorCop) }}</span>
          </div>
        </div>
      </section>

      <!-- Loading -->
      <div v-if="loading" class="loading-state"><Icon name="loader" :size="24" class="animate-spin" /> Cargando...</div>

      <!-- Título de la sección filtrada -->
      <section v-if="!loading && activeFilter" class="section animate-slideUp delay-1">
        <div class="section-header">
          <h2 class="section-title" :class="filterTitleClass">
            <Icon :name="filterIcon" :size="18" :color="filterColor" />
            {{ filterTitle }} <span class="section-count">{{ filteredList.length }}</span>
          </h2>
          <button class="btn btn--ghost btn--sm" @click="activeFilter = null"><Icon name="x" :size="14" /> Ver todo</button>
        </div>

        <div v-if="!filteredList.length" class="empty-state glass">
          <Icon name="check-circle" :size="32" color="var(--text-muted)" />
          <p>No hay elementos para este filtro</p>
        </div>

        <!-- Lista filtrada -->
        <div v-else class="fp-list">
          <div v-for="fp in filteredList" :key="fp.formaPagoId" :class="['fp-card glass', cardClass(fp)]" @click="openGestion(fp)">
            <div class="fp-card__left">
              <span class="fp-card__empresa">{{ fp.empresaNombre }}</span>
              <span class="fp-card__contrato">{{ fp.contratoNumeroInterno || fp.contratoCliente }}</span>
              <span class="fp-card__desc">{{ fp.formaPagoDescripcion }}</span>
            </div>
            <div class="fp-card__center">
              <span v-if="fp.estado === 'FACTURADA'" class="fp-card__facturada-badge">Facturada</span>
              <span v-if="fp.vencida" class="fp-card__vencida-badge">Vencida</span>
            </div>
            <div class="fp-card__right">
              <span class="fp-card__valor">{{ fmtCurrencyMoneda(fp.valor, fp.moneda) }}</span>
              <span v-if="fp.valorFacturado && fp.valorFacturado !== fp.valor" class="fp-card__valor-real">Real: {{ fmtCurrencyMoneda(fp.valorFacturado, fp.moneda) }}</span>
              <span v-if="fp.moneda === 'USD'" class="fp-card__cop">≈ {{ fmtCurrency(fp.valorCop) }}</span>
              <span class="fp-card__fecha" :class="{'fp-card__fecha--vencida': fp.vencida}">{{ fmtMesAnio(fp.anio, fp.mes) }}</span>
            </div>
            <div class="fp-card__gestion">
              <Icon v-if="fp.ultimaGestionTipo" name="check-circle" :size="16" color="var(--success)" />
              <Icon v-else name="clock" :size="16" color="var(--text-muted)" style="opacity:0.4" />
            </div>
          </div>
        </div>
      </section>

      <!-- Vista por defecto: Vencidas + Mes actual + Próximos -->
      <template v-if="!loading && !activeFilter">
        <!-- Vencidas -->
        <section v-if="vencidas.length" class="section animate-slideUp delay-1">
          <div class="section-header">
            <h2 class="section-title section-title--error"><Icon name="alert-circle" :size="18" color="var(--error)" /> Vencidas <span class="section-count">{{ vencidas.length }}</span></h2>
          </div>
          <div class="fp-list">
            <div v-for="fp in vencidas" :key="fp.formaPagoId" class="fp-card glass fp-card--vencida" @click="openGestion(fp)">
              <div class="fp-card__left">
                <span class="fp-card__empresa">{{ fp.empresaNombre }}</span>
                <span class="fp-card__contrato">{{ fp.contratoNumeroInterno || fp.contratoCliente }}</span>
                <span class="fp-card__desc">{{ fp.formaPagoDescripcion }}</span>
              </div>
              <div class="fp-card__right">
                <span class="fp-card__valor">{{ fmtCurrencyMoneda(fp.valor, fp.moneda) }}</span>
                <span v-if="fp.moneda === 'USD'" class="fp-card__cop">≈ {{ fmtCurrency(fp.valorCop) }}</span>
                <span class="fp-card__fecha fp-card__fecha--vencida">{{ fmtMesAnio(fp.anio, fp.mes) }}</span>
              </div>
              <div class="fp-card__gestion">
                <Icon v-if="fp.ultimaGestionTipo" name="check-circle" :size="16" color="var(--success)" />
                <Icon v-else name="clock" :size="16" color="var(--text-muted)" style="opacity:0.4" />
              </div>
            </div>
          </div>
        </section>

        <!-- Pendientes del mes -->
        <section v-if="pendientesMes.length" class="section animate-slideUp delay-2">
          <div class="section-header">
            <h2 class="section-title"><Icon name="calendar" :size="18" color="var(--primary)" /> Pendientes {{ mesActualLabel }} <span class="section-count">{{ pendientesMes.length }}</span></h2>
          </div>
          <div class="fp-list">
            <div v-for="fp in pendientesMes" :key="fp.formaPagoId" class="fp-card glass" @click="openGestion(fp)">
              <div class="fp-card__left">
                <span class="fp-card__empresa">{{ fp.empresaNombre }}</span>
                <span class="fp-card__contrato">{{ fp.contratoNumeroInterno || fp.contratoCliente }}</span>
                <span class="fp-card__desc">{{ fp.formaPagoDescripcion }}</span>
              </div>
              <div class="fp-card__right">
                <span class="fp-card__valor">{{ fmtCurrencyMoneda(fp.valor, fp.moneda) }}</span>
                <span v-if="fp.moneda === 'USD'" class="fp-card__cop">≈ {{ fmtCurrency(fp.valorCop) }}</span>
              </div>
              <div class="fp-card__gestion">
                <Icon v-if="fp.ultimaGestionTipo" name="check-circle" :size="16" color="var(--success)" />
                <Icon v-else name="clock" :size="16" color="var(--text-muted)" style="opacity:0.4" />
              </div>
            </div>
          </div>
        </section>

        <!-- Próximos meses -->
        <section v-if="proximosMeses.length" class="section animate-slideUp delay-3">
          <div class="section-header" @click="showProximos = !showProximos" style="cursor:pointer">
            <h2 class="section-title section-title--muted">
              <Icon :name="showProximos ? 'chevron-down' : 'chevron-right'" :size="18" />
              Próximos meses <span class="section-count">{{ proximosMeses.length }}</span>
            </h2>
          </div>
          <div v-if="showProximos" class="fp-list">
            <div v-for="fp in proximosMeses" :key="fp.formaPagoId" class="fp-card glass fp-card--futuro" @click="openGestion(fp)">
              <div class="fp-card__left">
                <span class="fp-card__empresa">{{ fp.empresaNombre }}</span>
                <span class="fp-card__contrato">{{ fp.contratoNumeroInterno || fp.contratoCliente }}</span>
                <span class="fp-card__desc">{{ fp.formaPagoDescripcion }}</span>
              </div>
              <div class="fp-card__right">
                <span class="fp-card__valor">{{ fmtCurrencyMoneda(fp.valor, fp.moneda) }}</span>
                <span class="fp-card__fecha">{{ fmtMesAnio(fp.anio, fp.mes) }}</span>
              </div>
            </div>
          </div>
        </section>

        <!-- Sin pendientes -->
        <div v-if="!vencidas.length && !pendientesMes.length && !proximosMeses.length" class="empty-state glass">
          <Icon name="check-circle" :size="48" color="var(--success)" />
          <p>No hay formas de pago pendientes por facturar</p>
        </div>
      </template>
    </div>

    <!-- Panel de Gestión -->
    <GestionPanel v-if="selectedFp" :forma-pago="selectedFp" @close="selectedFp = null" @updated="onGestionUpdated" />
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import AppLayout from '@/components/layout/AppLayout.vue';
import Icon from '@/components/ui/Icon.vue';
import GestionPanel from '@/components/facturacion/GestionPanel.vue';
import { facturacionService } from '@/services/facturacion.service';

const loading = ref(true);
const kpis = ref({});
const pendientes = ref([]);
const facturadas = ref([]);
const selectedFp = ref(null);
const showProximos = ref(false);
const activeFilter = ref(null);

const now = new Date();
const mesActual = now.getMonth() + 1;
const anioActual = now.getFullYear();

const meses = ['', 'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'];
const mesActualLabel = computed(() => meses[mesActual] + ' ' + anioActual);

// Listas derivadas de pendientes
const vencidas = computed(() => pendientes.value.filter(p => p.vencida));
const pendientesMes = computed(() => pendientes.value.filter(p => !p.vencida && p.anio === anioActual && p.mes === mesActual));
const proximosMeses = computed(() => pendientes.value.filter(p => {
  if (p.vencida) return false;
  if (p.anio === anioActual && p.mes === mesActual) return false;
  return (p.anio * 100 + p.mes) > (anioActual * 100 + mesActual);
}));

// Facturadas del mes
const facturadasMes = computed(() => facturadas.value.filter(f => f.anio === anioActual && f.mes === mesActual));

// Filtro activo por KPI
const filteredList = computed(() => {
  switch (activeFilter.value) {
    case 'vencidas': return vencidas.value;
    case 'pendientesMes': return pendientesMes.value;
    case 'facturadasMes': return facturadasMes.value;
    case 'pendienteTotal': return pendientes.value;
    default: return [];
  }
});

const filterTitle = computed(() => {
  switch (activeFilter.value) {
    case 'vencidas': return 'Vencidas';
    case 'pendientesMes': return 'Pendientes ' + mesActualLabel.value;
    case 'facturadasMes': return 'Facturadas ' + mesActualLabel.value;
    case 'pendienteTotal': return 'Pendiente Total';
    default: return '';
  }
});

const filterTitleClass = computed(() => {
  switch (activeFilter.value) {
    case 'vencidas': return 'section-title--error';
    case 'facturadasMes': return 'section-title--success';
    default: return '';
  }
});

const filterIcon = computed(() => {
  switch (activeFilter.value) {
    case 'vencidas': return 'alert-circle';
    case 'pendientesMes': return 'clock';
    case 'facturadasMes': return 'check-circle';
    case 'pendienteTotal': return 'wallet';
    default: return 'calendar';
  }
});

const filterColor = computed(() => {
  switch (activeFilter.value) {
    case 'vencidas': return 'var(--error)';
    case 'pendientesMes': return 'var(--warning)';
    case 'facturadasMes': return 'var(--success)';
    case 'pendienteTotal': return 'var(--primary)';
    default: return 'var(--primary)';
  }
});

function toggleFilter(filter) {
  activeFilter.value = activeFilter.value === filter ? null : filter;
}

function cardClass(fp) {
  if (fp.vencida) return 'fp-card--vencida';
  if (fp.estado === 'FACTURADA') return 'fp-card--facturada';
  return '';
}

onMounted(async () => { await loadData(); });

async function loadData() {
  loading.value = true;
  try {
    const [kpiData, pendData, factData] = await Promise.all([
      facturacionService.obtenerKpis(),
      facturacionService.obtenerPendientes(),
      facturacionService.obtenerPendientes({ estado: 'FACTURADA' }),
    ]);
    kpis.value = kpiData;
    pendientes.value = pendData;
    facturadas.value = factData;
  } catch (err) { console.error('Error cargando facturación:', err); }
  finally { loading.value = false; }
}

function openGestion(fp) { selectedFp.value = fp; }
async function onGestionUpdated() { await loadData(); }

function fmtCurrency(v) {
  if (v == null) return '$0';
  return new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }).format(v);
}
function fmtCurrencyMoneda(v, moneda) {
  if (v == null) return '$0';
  if (moneda === 'USD') return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD', maximumFractionDigits: 0 }).format(v);
  return fmtCurrency(v);
}
function fmtMesAnio(anio, mes) { return meses[mes] + ' ' + anio; }
</script>

<style scoped>
.facturacion { display: flex; flex-direction: column; gap: var(--space-5); }
.kpi-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: var(--space-4); }
.kpi { padding: var(--space-4); border-radius: var(--radius-lg); display: flex; align-items: center; gap: var(--space-4); transition: all 0.2s; cursor: pointer; border: 1px solid transparent; }
.kpi:hover { border-color: var(--glass-border); background: rgba(255,255,255,0.03); }
.kpi--selected { border-color: var(--primary) !important; background: rgba(0,212,255,0.05) !important; }
.kpi--alert { border-color: rgba(239,68,68,0.3); }
.kpi__icon { width: 40px; height: 40px; border-radius: var(--radius-md); display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.kpi__icon--error { background: var(--error-soft); color: var(--error); }
.kpi__icon--warning { background: var(--warning-soft); color: var(--warning); }
.kpi__icon--success { background: var(--success-soft); color: var(--success); }
.kpi__icon--primary { background: var(--primary-soft); color: var(--primary); }
.kpi__data { display: flex; flex-direction: column; }
.kpi__value { font-family: var(--font-display); font-size: var(--text-2xl); font-weight: 700; }
.kpi__value--error { color: var(--error); } .kpi__value--warning { color: var(--warning); } .kpi__value--success { color: var(--success); } .kpi__value--primary { color: var(--primary); }
.kpi__label { font-size: 10px; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.5px; font-weight: 600; }
.kpi__sub { font-size: var(--text-xs); color: var(--text-tertiary); font-family: var(--font-mono, monospace); margin-top: 2px; }

.loading-state { display: flex; align-items: center; justify-content: center; gap: var(--space-3); padding: var(--space-8); color: var(--text-muted); font-size: var(--text-sm); }
.empty-state { display: flex; flex-direction: column; align-items: center; justify-content: center; gap: var(--space-3); padding: var(--space-10); border-radius: var(--radius-xl); color: var(--text-muted); font-size: var(--text-sm); }

.section { display: flex; flex-direction: column; gap: var(--space-3); }
.section-header { display: flex; justify-content: space-between; align-items: center; }
.section-title { display: flex; align-items: center; gap: var(--space-2); font-family: var(--font-display); font-size: var(--text-sm); font-weight: 600; color: var(--text-primary); margin: 0; }
.section-title--error { color: var(--error); }
.section-title--success { color: var(--success); }
.section-title--muted { color: var(--text-muted); }
.section-count { font-size: var(--text-xs); color: var(--text-muted); font-weight: 400; background: var(--bg-surface); padding: 1px 8px; border-radius: var(--radius-full); margin-left: var(--space-1); }

.fp-list { display: flex; flex-direction: column; gap: var(--space-2); }
.fp-card { display: flex; align-items: center; gap: var(--space-4); padding: var(--space-3) var(--space-4); border-radius: var(--radius-lg); cursor: pointer; transition: all 0.15s; }
.fp-card:hover { background: rgba(255,255,255,0.03); border-color: var(--primary); }
.fp-card--vencida { border-left: 3px solid var(--error); }
.fp-card--facturada { border-left: 3px solid var(--success); opacity: 0.85; }
.fp-card--futuro { opacity: 0.6; }
.fp-card__left { flex: 1; min-width: 0; display: flex; flex-direction: column; gap: 1px; }
.fp-card__empresa { font-size: var(--text-xs); font-weight: 600; color: var(--text-primary); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.fp-card__contrato { font-size: 10px; color: var(--text-muted); }
.fp-card__desc { font-size: 10px; color: var(--text-tertiary); }
.fp-card__center { flex-shrink: 0; }
.fp-card__facturada-badge { font-size: 9px; font-weight: 700; padding: 1px 8px; border-radius: var(--radius-full); background: var(--success-soft); color: var(--success); text-transform: uppercase; }
.fp-card__vencida-badge { font-size: 9px; font-weight: 700; padding: 1px 8px; border-radius: var(--radius-full); background: var(--error-soft); color: var(--error); text-transform: uppercase; }
.fp-card__right { text-align: right; flex-shrink: 0; display: flex; flex-direction: column; gap: 1px; }
.fp-card__valor { font-size: var(--text-xs); font-weight: 700; color: var(--primary); font-family: var(--font-mono, monospace); }
.fp-card__valor-real { font-size: 9px; color: var(--success); font-family: var(--font-mono, monospace); }
.fp-card__cop { font-size: 9px; color: var(--text-muted); font-family: var(--font-mono, monospace); }
.fp-card__fecha { font-size: 10px; color: var(--text-muted); }
.fp-card__fecha--vencida { color: var(--error); font-weight: 600; }
.fp-card__gestion { flex-shrink: 0; }

.btn { display: flex; align-items: center; gap: var(--space-2); padding: var(--space-2) var(--space-4); border-radius: var(--radius-full); font-family: var(--font-body); font-size: var(--text-xs); font-weight: 600; cursor: pointer; transition: all 0.15s; border: 1px solid transparent; }
.btn--sm { padding: var(--space-1) var(--space-3); font-size: 11px; }
.btn--ghost { background: transparent; color: var(--text-secondary); border-color: var(--glass-border); } .btn--ghost:hover { background: var(--glass-hover); }

@media (max-width: 900px) { .kpi-row { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 600px) { .kpi-row { grid-template-columns: 1fr; } }
</style>
