<template>
  <AppLayout>
    <div class="detalle">
      <!-- Loading -->
      <div v-if="loading" class="loading-state">
        <Icon name="loader" :size="32" class="animate-spin" /><p>Cargando oportunidad...</p>
      </div>

      <!-- Error -->
      <div v-else-if="error" class="error-state glass">
        <Icon name="alert-circle" :size="40" color="var(--error)" />
        <p>{{ error }}</p>
        <button class="btn-back" @click="goBack"><Icon name="chevron-left" :size="16" /> Volver</button>
      </div>

      <template v-else-if="op">
        <!-- Header -->
        <section class="detalle__header animate-slideUp">
          <button class="btn-back" @click="goBack"><Icon name="chevron-left" :size="16" /> Volver</button>
          <div class="header-main">
            <div class="header-top">
              <h1 class="op-nombre">{{ op.nombre }}</h1>
              <span class="estado-badge" :class="'estado-badge--' + op.estadoMacro.toLowerCase()">{{ estadoLabel }}</span>
            </div>
            <p class="op-empresa">{{ op.empresaNombre }} · {{ op.pipelineNombre }}</p>
          </div>
        </section>

        <!-- Valor y Probabilidad destacados -->
        <section class="highlight-row animate-slideUp delay-1">
          <div class="highlight-card glass">
            <span class="highlight-label">Valor Estimado</span>
            <span class="highlight-value highlight-value--primary">{{ formatCurrencyFull(op.valorEstimado, op.moneda) }}</span>
          </div>
          <div class="highlight-card glass">
            <span class="highlight-label">Probabilidad</span>
            <div class="prob-display">
              <span class="highlight-value">{{ op.probabilidad ?? 0 }}%</span>
              <div class="prob-bar">
                <div class="prob-fill" :style="{ width: (op.probabilidad ?? 0) + '%' }"></div>
              </div>
            </div>
          </div>
          <div class="highlight-card glass">
            <span class="highlight-label">Etapa Actual</span>
            <span class="highlight-value highlight-value--etapa">{{ op.etapaNombre }}</span>
          </div>
          <div class="highlight-card glass">
            <span class="highlight-label">Fecha Est. Cierre</span>
            <span class="highlight-value">{{ formatDateFull(op.fechaEstimadaCierre) || '—' }}</span>
          </div>
        </section>

        <!-- Info Grid -->
        <section class="info-section animate-slideUp delay-2">
          <div class="info-card glass">
            <h3 class="info-card__title"><Icon name="business" :size="16" color="var(--primary)" /> Información General</h3>
            <div class="info-grid">
              <div class="info-item">
                <span class="info-item__label">Empresa</span>
                <span class="info-item__value">{{ op.empresaNombre }}</span>
              </div>
              <div class="info-item">
                <span class="info-item__label">Pipeline</span>
                <span class="info-item__value">{{ op.pipelineNombre }}</span>
              </div>
              <div class="info-item">
                <span class="info-item__label">Moneda</span>
                <span class="info-item__value">{{ op.moneda || 'COP' }}</span>
              </div>
              <div class="info-item">
                <span class="info-item__label">Fuente</span>
                <span class="info-item__value">{{ op.fuente || '—' }}</span>
              </div>
              <div class="info-item">
                <span class="info-item__label">Tipo de Servicio</span>
                <span class="info-item__value">{{ op.tipoServicio || '—' }}</span>
              </div>
              <div class="info-item">
                <span class="info-item__label">Creada</span>
                <span class="info-item__value">{{ formatDateTimeFull(op.fechaCreacion) }}</span>
              </div>
            </div>
          </div>

          <!-- Cierre (solo si está cerrada) -->
          <div v-if="isCerrada" class="info-card glass">
            <h3 class="info-card__title">
              <Icon name="check-circle" :size="16" :color="estadoColor" /> Información de Cierre
            </h3>
            <div class="info-grid">
              <div class="info-item">
                <span class="info-item__label">Estado Final</span>
                <span class="info-item__value">
                  <span class="estado-badge estado-badge--sm" :class="'estado-badge--' + op.estadoMacro.toLowerCase()">{{ estadoLabel }}</span>
                </span>
              </div>
              <div class="info-item">
                <span class="info-item__label">Fecha de Cierre</span>
                <span class="info-item__value">{{ formatDateTimeFull(op.fechaCierre) }}</span>
              </div>
              <div class="info-item info-item--full" v-if="op.comentarioCierre">
                <span class="info-item__label">Comentario de Cierre</span>
                <span class="info-item__value info-item__value--comment">{{ op.comentarioCierre }}</span>
              </div>
            </div>
          </div>
        </section>
      </template>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import AppLayout from '@/components/layout/AppLayout.vue';
import Icon from '@/components/ui/Icon.vue';
import { oportunidadService } from '@/services/oportunidad.service';

const route = useRoute();
const router = useRouter();
const op = ref(null);
const loading = ref(true);
const error = ref(null);

onMounted(async () => {
  const id = route.params.id;
  try {
    op.value = await oportunidadService.obtenerPorId(id);
  } catch (err) {
    error.value = err.response?.data?.message || 'No se pudo cargar la oportunidad';
  } finally {
    loading.value = false;
  }
});

const estadoLabel = computed(() => {
  const map = { ABIERTA: 'Abierta', SEGUIMIENTO: 'Seguimiento', GANADA: 'Ganada', PERDIDA: 'Perdida', NO_CONCRETADA: 'No Concretada' };
  return map[op.value?.estadoMacro] || op.value?.estadoMacro;
});

const estadoColor = computed(() => {
  const map = { ABIERTA: 'var(--primary)', SEGUIMIENTO: 'var(--warning)', GANADA: 'var(--success)', PERDIDA: 'var(--error)', NO_CONCRETADA: 'var(--accent)' };
  return map[op.value?.estadoMacro] || 'var(--text-muted)';
});

const isCerrada = computed(() => {
  return ['GANADA', 'PERDIDA', 'NO_CONCRETADA'].includes(op.value?.estadoMacro);
});

function goBack() {
  router.back();
}

function formatCurrencyFull(value, moneda) {
  const num = Number(value);
  if (!num) return '$0';
  const symbol = moneda === 'USD' ? 'US$' : moneda === 'EUR' ? '€' : '$';
  return `${symbol}${num.toLocaleString('es-CO')}`;
}

function formatDateFull(date) {
  if (!date) return null;
  return new Date(date).toLocaleDateString('es-CO', { day: 'numeric', month: 'long', year: 'numeric' });
}

function formatDateTimeFull(datetime) {
  if (!datetime) return '—';
  return new Date(datetime).toLocaleDateString('es-CO', { day: 'numeric', month: 'long', year: 'numeric', hour: '2-digit', minute: '2-digit' });
}
</script>

<style scoped>
.detalle { display: flex; flex-direction: column; gap: var(--space-6); }

.loading-state, .error-state { display: flex; flex-direction: column; align-items: center; justify-content: center; gap: var(--space-3); padding: var(--space-8); color: var(--text-tertiary); font-size: var(--text-sm); }
.error-state { border-radius: var(--radius-xl); padding: var(--space-10); }

/* Back button */
.btn-back { display: inline-flex; align-items: center; gap: var(--space-1); background: none; border: 1px solid var(--glass-border); border-radius: var(--radius-full); color: var(--text-secondary); font-family: var(--font-body); font-size: var(--text-xs); padding: var(--space-2) var(--space-4); cursor: pointer; transition: all 0.15s; }
.btn-back:hover { background: var(--glass-hover); color: var(--text-primary); border-color: var(--border-light); }

/* Header */
.detalle__header { display: flex; flex-direction: column; gap: var(--space-4); }
.header-top { display: flex; align-items: center; gap: var(--space-4); flex-wrap: wrap; }
.op-nombre { font-family: var(--font-display); font-size: var(--text-3xl); font-weight: 700; color: var(--text-primary); margin: 0; }
.op-empresa { color: var(--text-tertiary); font-size: var(--text-sm); margin: 0; }

/* Estado badge */
.estado-badge { display: inline-flex; align-items: center; padding: var(--space-1) var(--space-4); border-radius: var(--radius-full); font-size: var(--text-xs); font-weight: 600; text-transform: uppercase; letter-spacing: 0.05em; }
.estado-badge--sm { font-size: 10px; padding: 2px 8px; }
.estado-badge--abierta { background: var(--primary-soft); color: var(--primary); }
.estado-badge--seguimiento { background: var(--warning-soft); color: var(--warning); }
.estado-badge--ganada { background: var(--success-soft); color: var(--success); }
.estado-badge--perdida { background: var(--error-soft); color: var(--error); }
.estado-badge--no_concretada { background: var(--accent-soft); color: var(--accent); }

/* Highlight row */
.highlight-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: var(--space-4); }
.highlight-card { border-radius: var(--radius-xl); padding: var(--space-5); display: flex; flex-direction: column; gap: var(--space-2); }
.highlight-label { font-size: var(--text-xs); color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.05em; }
.highlight-value { font-family: var(--font-display); font-size: var(--text-xl); font-weight: 700; color: var(--text-primary); }
.highlight-value--primary { color: var(--primary); }
.highlight-value--etapa { font-size: var(--text-lg); }

/* Probability bar */
.prob-display { display: flex; flex-direction: column; gap: var(--space-2); }
.prob-bar { width: 100%; height: 6px; background: var(--bg-surface); border-radius: var(--radius-full); overflow: hidden; }
.prob-fill { height: 100%; background: var(--gradient-primary); border-radius: var(--radius-full); transition: width 0.6s ease; }

/* Info section */
.info-section { display: grid; grid-template-columns: 1fr 1fr; gap: var(--space-5); }
.info-card { border-radius: var(--radius-xl); padding: var(--space-5); }
.info-card__title { display: flex; align-items: center; gap: var(--space-2); font-family: var(--font-display); font-size: var(--text-sm); font-weight: 600; color: var(--text-primary); margin: 0 0 var(--space-5) 0; }
.info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: var(--space-4); }
.info-item { display: flex; flex-direction: column; gap: 2px; }
.info-item--full { grid-column: 1 / -1; }
.info-item__label { font-size: 10px; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.05em; }
.info-item__value { font-size: var(--text-sm); color: var(--text-primary); font-weight: 500; }
.info-item__value--comment { background: var(--bg-surface); padding: var(--space-3); border-radius: var(--radius-md); font-weight: 400; color: var(--text-secondary); line-height: 1.5; margin-top: var(--space-1); }

@media (max-width: 1100px) {
  .highlight-row { grid-template-columns: repeat(2, 1fr); }
  .info-section { grid-template-columns: 1fr; }
}
@media (max-width: 600px) {
  .highlight-row { grid-template-columns: 1fr; }
  .info-grid { grid-template-columns: 1fr; }
}
</style>
