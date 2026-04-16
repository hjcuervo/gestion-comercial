<template>
  <AppLayout>
    <div class="facturacion">
      <!-- KPIs -->
      <section class="kpi-row animate-slideUp">
        <div class="kpi glass" :class="{'kpi--alert': kpis.vencidasCantidad > 0}">
          <div class="kpi__icon kpi__icon--error"><Icon name="alert-circle" :size="20" /></div>
          <div class="kpi__data">
            <span class="kpi__value kpi__value--error">{{ kpis.vencidasCantidad || 0 }}</span>
            <span class="kpi__label">Vencidas</span>
            <span class="kpi__sub">{{ fmtCurrency(kpis.vencidasValorCop) }}</span>
          </div>
        </div>
        <div class="kpi glass">
          <div class="kpi__icon kpi__icon--warning"><Icon name="clock" :size="20" /></div>
          <div class="kpi__data">
            <span class="kpi__value kpi__value--warning">{{ kpis.pendientesMesCantidad || 0 }}</span>
            <span class="kpi__label">Pendientes este mes</span>
            <span class="kpi__sub">{{ fmtCurrency(kpis.pendientesMesValorCop) }}</span>
          </div>
        </div>
        <div class="kpi glass">
          <div class="kpi__icon kpi__icon--success"><Icon name="check-circle" :size="20" /></div>
          <div class="kpi__data">
            <span class="kpi__value kpi__value--success">{{ kpis.facturadasMesCantidad || 0 }}</span>
            <span class="kpi__label">Facturadas este mes</span>
            <span class="kpi__sub">{{ fmtCurrency(kpis.facturadasMesValorCop) }}</span>
          </div>
        </div>
        <div class="kpi glass">
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
      <div v-if="!loading && !vencidas.length && !pendientesMes.length && !proximosMeses.length" class="empty-state glass">
        <Icon name="check-circle" :size="48" color="var(--success)" />
        <p>No hay formas de pago pendientes por facturar</p>
      </div>
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
const selectedFp = ref(null);
const showProximos = ref(false);

const now = new Date();
const mesActual = now.getMonth() + 1;
const anioActual = now.getFullYear();

const meses = ['', 'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'];
const mesActualLabel = computed(() => meses[mesActual] + ' ' + anioActual);

const vencidas = computed(() => pendientes.value.filter(p => p.vencida));
const pendientesMes = computed(() => pendientes.value.filter(p => !p.vencida && p.anio === anioActual && p.mes === mesActual));
const proximosMeses = computed(() => pendientes.value.filter(p => {
  if (p.vencida) return false;
  if (p.anio === anioActual && p.mes === mesActual) return false;
  const fpPeriod = p.anio * 100 + p.mes;
  const currentPeriod = anioActual * 100 + mesActual;
  return fpPeriod > currentPeriod;
}));

onMounted(async () => { await loadData(); });

async function loadData() {
  loading.value = true;
  try {
    const [kpiData, pendData] = await Promise.all([
      facturacionService.obtenerKpis(),
      facturacionService.obtenerPendientes(),
    ]);
    kpis.value = kpiData;
    pendientes.value = pendData;
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
.kpi { padding: var(--space-4); border-radius: var(--radius-lg); display: flex; align-items: center; gap: var(--space-4); transition: all 0.2s; }
.kpi--alert { border: 1px solid rgba(239,68,68,0.3); }
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
.section-title--muted { color: var(--text-muted); }
.section-count { font-size: var(--text-xs); color: var(--text-muted); font-weight: 400; background: var(--bg-surface); padding: 1px 8px; border-radius: var(--radius-full); margin-left: var(--space-1); }

.fp-list { display: flex; flex-direction: column; gap: var(--space-2); }
.fp-card { display: flex; align-items: center; gap: var(--space-4); padding: var(--space-3) var(--space-4); border-radius: var(--radius-lg); cursor: pointer; transition: all 0.15s; }
.fp-card:hover { background: rgba(255,255,255,0.03); border-color: var(--primary); }
.fp-card--vencida { border-left: 3px solid var(--error); }
.fp-card--futuro { opacity: 0.6; }
.fp-card__left { flex: 1; min-width: 0; display: flex; flex-direction: column; gap: 1px; }
.fp-card__empresa { font-size: var(--text-xs); font-weight: 600; color: var(--text-primary); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.fp-card__contrato { font-size: 10px; color: var(--text-muted); }
.fp-card__desc { font-size: 10px; color: var(--text-tertiary); }
.fp-card__right { text-align: right; flex-shrink: 0; display: flex; flex-direction: column; gap: 1px; }
.fp-card__valor { font-size: var(--text-xs); font-weight: 700; color: var(--primary); font-family: var(--font-mono, monospace); }
.fp-card__cop { font-size: 9px; color: var(--text-muted); font-family: var(--font-mono, monospace); }
.fp-card__fecha { font-size: 10px; color: var(--text-muted); }
.fp-card__fecha--vencida { color: var(--error); font-weight: 600; }
.fp-card__gestion { flex-shrink: 0; }

@media (max-width: 900px) { .kpi-row { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 600px) { .kpi-row { grid-template-columns: 1fr; } }
</style>
