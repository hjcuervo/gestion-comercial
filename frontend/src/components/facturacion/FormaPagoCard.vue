<template>
  <div :class="['fpcard', item.enRiesgo && 'fpcard--riesgo']" @click="$emit('click', item)">
    <div class="fpcard__left">
      <div class="fpcard__top">
        <span class="fpcard__empresa">{{ item.empresaNombre }}</span>
        <GcIcon v-if="item.enRiesgo" name="alert-circle" :size="12" />
      </div>
      <span class="fpcard__contrato gc-mono">{{ item.contratoNumero || 'Sin nº' }}</span>
      <span class="fpcard__desc">{{ item.descripcion }}</span>
    </div>

    <div class="fpcard__center">
      <GcBadge :tone="estadoTone(item.estado)" :label="estadoLabel(item.estado)" />
      <span class="fpcard__dias" :class="diasClass">
        <GcIcon :name="diasIcon" :size="12" />
        {{ diasLabel }}
      </span>
    </div>

    <div class="fpcard__right">
      <span class="fpcard__valor gc-mono">{{ fmtMoneda(item.montoPresupuestado, item.moneda) }}</span>
      <span v-if="hasFacturado" class="fpcard__sub gc-mono">Facturado: {{ fmtMoneda(item.montoFacturadoAcumulado, item.moneda) }}</span>
      <span v-if="hasSaldo" class="fpcard__sub gc-mono">Saldo: {{ fmtMoneda(item.saldoPendiente, item.moneda) }}</span>
      <span v-if="item.moneda === 'USD' && item.montoPresupuestadoCOP" class="fpcard__cop gc-mono">
        ≈ {{ fmtCop(item.montoPresupuestadoCOP) }}<span v-if="item.trmAplicada"> @ {{ fmtTrm(item.trmAplicada) }}</span>
      </span>
      <span class="fpcard__fecha gc-mono">{{ fmtFecha(item.fechaEsperadaActual) }}</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import GcIcon from '@/components/ui/GcIcon.vue';
import GcBadge from '@/components/ui/GcBadge.vue';

const props = defineProps({
  item: { type: Object, required: true },
  variant: { type: String, default: '' },
});
defineEmits(['click']);

const hasFacturado = computed(() => props.item.montoFacturadoAcumulado != null && Number(props.item.montoFacturadoAcumulado) > 0);
const hasSaldo = computed(() => props.item.saldoPendiente != null && Number(props.item.saldoPendiente) > 0 && Number(props.item.saldoPendiente) !== Number(props.item.montoPresupuestado));

const diasClass = computed(() => {
  const d = props.item.diasVencimiento;
  if (d == null) return '';
  if (d < 0) return 'fpcard__dias--vencido';
  if (d === 0) return 'fpcard__dias--hoy';
  if (d <= 7) return 'fpcard__dias--proximo';
  return '';
});
const diasIcon = computed(() => {
  const d = props.item.diasVencimiento;
  if (d == null) return 'calendar';
  if (d < 0) return 'alert-circle';
  if (d === 0) return 'clock';
  return 'calendar';
});
const diasLabel = computed(() => {
  const d = props.item.diasVencimiento;
  if (d == null) return '';
  if (d < 0) return `Vencido hace ${Math.abs(d)} ${Math.abs(d) === 1 ? 'día' : 'días'}`;
  if (d === 0) return 'Vence hoy';
  if (d === 1) return 'Vence mañana';
  return `Vence en ${d} días`;
});

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

function fmtMoneda(v, moneda) {
  if (v == null) return '—';
  return new Intl.NumberFormat('es-CO', { style: 'currency', currency: moneda || 'COP', maximumFractionDigits: 0 }).format(v);
}
function fmtCop(v) {
  if (v == null) return '$0';
  return new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }).format(v);
}
function fmtTrm(v) { return v == null ? '' : new Intl.NumberFormat('es-CO', { maximumFractionDigits: 2 }).format(v); }
function fmtFecha(iso) { if (!iso) return ''; const [y, m, d] = iso.split('-'); return `${d}/${m}/${y}`; }
</script>

<style scoped>
.fpcard { display: flex; align-items: center; gap: var(--gc-space-4); padding: var(--gc-space-3) var(--gc-space-4); background: var(--gc-surface); border: 1px solid var(--gc-border); border-radius: var(--gc-radius-md); cursor: pointer; transition: background var(--gc-t-fast), border-color var(--gc-t-fast); }
.fpcard:hover { background: var(--gc-surface-2); border-color: var(--gc-border-strong); }
.fpcard--riesgo { border-left: 2px solid var(--gc-danger); }
.fpcard__left { flex: 1; min-width: 0; display: flex; flex-direction: column; gap: 2px; }
.fpcard__top { display: flex; align-items: center; gap: var(--gc-space-2); color: var(--gc-danger); }
.fpcard__empresa { font-size: var(--gc-fs-md); font-weight: var(--gc-fw-medium); color: var(--gc-text); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.fpcard__contrato { font-size: var(--gc-fs-xs); color: var(--gc-text-3); }
.fpcard__desc { font-size: var(--gc-fs-base); color: var(--gc-text-2); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.fpcard__center { display: flex; flex-direction: column; align-items: flex-start; gap: var(--gc-space-1); min-width: 140px; }
.fpcard__dias { display: inline-flex; align-items: center; gap: 4px; font-size: var(--gc-fs-xs); color: var(--gc-text-3); }
.fpcard__dias--vencido { color: var(--gc-danger); }
.fpcard__dias--hoy { color: var(--gc-warning); }
.fpcard__dias--proximo { color: var(--gc-warning); }
.fpcard__right { display: flex; flex-direction: column; align-items: flex-end; gap: 2px; min-width: 140px; }
.fpcard__valor { font-size: var(--gc-fs-md); font-weight: var(--gc-fw-medium); color: var(--gc-text); }
.fpcard__sub { font-size: var(--gc-fs-xs); color: var(--gc-text-3); }
.fpcard__cop { font-size: var(--gc-fs-xs); color: var(--gc-text-3); }
.fpcard__fecha { font-size: var(--gc-fs-xs); color: var(--gc-text-3); }
</style>
