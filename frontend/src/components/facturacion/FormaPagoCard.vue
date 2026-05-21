<template>
  <div
    :class="['fp-card', 'glass', variantClass, item.enRiesgo && 'fp-card--riesgo']"
    @click="$emit('click', item)"
  >
    <!-- Izquierda: empresa, contrato, descripción -->
    <div class="fp-card__left">
      <div class="fp-card__top">
        <span class="fp-card__empresa">{{ item.empresaNombre }}</span>
        <Icon v-if="item.enRiesgo" name="alert-circle" :size="12" color="var(--error)" title="En riesgo" />
      </div>
      <span class="fp-card__contrato">{{ item.contratoNumero || 'Sin nº' }}</span>
      <span class="fp-card__desc">{{ item.descripcion }}</span>
    </div>

    <!-- Centro: estado + chip de fecha -->
    <div class="fp-card__center">
      <span :class="['estado-pill', `estado-pill--${(item.estado || '').toLowerCase()}`]">
        {{ estadoLabel(item.estado) }}
      </span>
      <span class="fp-card__dias" :class="diasClass">
        <Icon :name="diasIcon" :size="12" />
        {{ diasLabel }}
      </span>
    </div>

    <!-- Derecha: montos -->
    <div class="fp-card__right">
      <span class="fp-card__valor">
        {{ fmtMoneda(item.montoPresupuestado, item.moneda) }}
      </span>
      <span v-if="hasFacturado" class="fp-card__facturado">
        Facturado: {{ fmtMoneda(item.montoFacturadoAcumulado, item.moneda) }}
      </span>
      <span v-if="hasSaldo" class="fp-card__saldo">
        Saldo: {{ fmtMoneda(item.saldoPendiente, item.moneda) }}
      </span>
      <span v-if="item.moneda === 'USD' && item.montoPresupuestadoCOP" class="fp-card__cop">
        ≈ {{ fmtCop(item.montoPresupuestadoCOP) }}
        <span v-if="item.trmAplicada" class="fp-card__trm">@ {{ fmtTrm(item.trmAplicada) }}</span>
      </span>
      <span class="fp-card__fecha">{{ fmtFecha(item.fechaEsperadaActual) }}</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import Icon from '@/components/ui/Icon.vue';

const props = defineProps({
  item: { type: Object, required: true },
  // 'vencido' | 'arrastre' | '' (por defecto)
  variant: { type: String, default: '' },
});

defineEmits(['click']);

const variantClass = computed(() => {
  if (props.variant === 'arrastre') return 'fp-card--arrastre';
  if (props.variant === 'vencido') return 'fp-card--vencido';
  // si no se pasa variant, derivar del backend
  if (props.item.diasVencimiento != null && props.item.diasVencimiento < 0) {
    return 'fp-card--vencido';
  }
  return '';
});

const hasFacturado = computed(() =>
  props.item.montoFacturadoAcumulado != null &&
  Number(props.item.montoFacturadoAcumulado) > 0
);

const hasSaldo = computed(() =>
  props.item.saldoPendiente != null &&
  Number(props.item.saldoPendiente) > 0 &&
  Number(props.item.saldoPendiente) !== Number(props.item.montoPresupuestado)
);

const diasClass = computed(() => {
  const d = props.item.diasVencimiento;
  if (d == null) return '';
  if (d < 0) return 'fp-card__dias--vencido';
  if (d === 0) return 'fp-card__dias--hoy';
  if (d <= 7) return 'fp-card__dias--proximo';
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

function fmtMoneda(v, moneda) {
  if (v == null) return '—';
  const m = moneda || 'COP';
  return new Intl.NumberFormat('es-CO', {
    style: 'currency', currency: m, maximumFractionDigits: 0,
  }).format(v);
}

function fmtCop(v) {
  if (v == null) return '$0';
  return new Intl.NumberFormat('es-CO', {
    style: 'currency', currency: 'COP', maximumFractionDigits: 0,
  }).format(v);
}

function fmtTrm(v) {
  if (v == null) return '';
  return new Intl.NumberFormat('es-CO', { maximumFractionDigits: 2 }).format(v);
}

function fmtFecha(iso) {
  if (!iso) return '';
  // iso: "YYYY-MM-DD"
  const [y, m, d] = iso.split('-');
  return `${d}/${m}/${y}`;
}
</script>

<style scoped>
.fp-card {
  display: grid;
  grid-template-columns: 2fr 1.2fr 1.3fr;
  gap: var(--space-4);
  align-items: center;
  padding: var(--space-3) var(--space-4);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all 0.15s;
  border: 1px solid transparent;
}
.fp-card:hover {
  background: rgba(255, 255, 255, 0.03);
  border-color: var(--primary);
}
.fp-card--vencido {
  border-left: 3px solid var(--error);
}
.fp-card--arrastre {
  border-left: 3px solid var(--error);
  background: rgba(244, 63, 94, 0.03);
}
.fp-card--riesgo {
  box-shadow: inset 0 0 0 1px rgba(244, 63, 94, 0.25);
}

/* Izquierda */
.fp-card__left {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}
.fp-card__top {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}
.fp-card__empresa {
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.fp-card__contrato {
  font-size: 11px;
  color: var(--text-muted);
  font-family: var(--font-mono, monospace);
}
.fp-card__desc {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* Centro */
.fp-card__center {
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
  align-items: flex-start;
}
.fp-card__dias {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  color: var(--text-muted);
}
.fp-card__dias--vencido { color: var(--error); font-weight: var(--font-semibold); }
.fp-card__dias--hoy { color: var(--warning); font-weight: var(--font-semibold); }
.fp-card__dias--proximo { color: var(--warning); }

/* Estado pill (mismas reglas que la vista) */
.estado-pill {
  padding: 2px 10px;
  border-radius: var(--radius-full);
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
}
.estado-pill--pendiente_gestion    { background: var(--bg-surface);     color: var(--text-secondary); }
.estado-pill--en_gestion           { background: var(--primary-soft);   color: var(--primary); }
.estado-pill--comprometido         { background: var(--secondary-soft); color: var(--secondary); }
.estado-pill--parcialmente_cumplido{ background: var(--warning-soft);   color: var(--warning); }
.estado-pill--reprogramado         { background: var(--warning-soft);   color: var(--warning); opacity: 0.85; }
.estado-pill--cumplido             { background: var(--success-soft);   color: var(--success); }
.estado-pill--no_logrado           { background: var(--error-soft);     color: var(--error); }

/* Derecha */
.fp-card__right {
  text-align: right;
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}
.fp-card__valor {
  font-size: var(--text-sm);
  font-weight: var(--font-bold);
  color: var(--primary);
  font-family: var(--font-mono, monospace);
}
.fp-card__facturado {
  font-size: 10px;
  color: var(--success);
  font-family: var(--font-mono, monospace);
}
.fp-card__saldo {
  font-size: 10px;
  color: var(--warning);
  font-family: var(--font-mono, monospace);
}
.fp-card__cop {
  font-size: 10px;
  color: var(--text-muted);
  font-family: var(--font-mono, monospace);
}
.fp-card__trm {
  opacity: 0.7;
}
.fp-card__fecha {
  font-size: 10px;
  color: var(--text-muted);
  margin-top: 2px;
}

@media (max-width: 700px) {
  .fp-card {
    grid-template-columns: 1fr;
    gap: var(--space-2);
  }
  .fp-card__center, .fp-card__right { text-align: left; align-items: flex-start; }
}
</style>
