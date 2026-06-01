<template>
  <div class="op-master">
    <div class="op-master__filters">
      <GcInput
        v-model="search"
        placeholder="Buscar oportunidad…"
        icon="search"
        @update:modelValue="debouncedReload"
      />
      <GcSelect
        v-model="filtroEstado"
        :options="estadoOptions"
        placeholder=""
        @update:modelValue="reload"
      />
    </div>

    <div v-if="loading" class="op-master__state">
      <GcSpinner :size="20" />
    </div>

    <GcEmpty
      v-else-if="!oportunidades.length"
      icon="search-off"
      message="Sin oportunidades"
    />

    <div v-else class="op-master__list">
      <GcListRow
        v-for="op in oportunidades"
        :key="op.id"
        :tone="estadoTone(op.estadoMacro)"
        clickable
        :active="String(op.id) === String(selectedId)"
        @click="$emit('select', op.id)"
      >
        <div class="op-master__row">
          <span class="op-master__name">{{ op.nombre }}</span>
          <span class="op-master__meta">
            {{ op.empresaNombre || '—' }}<template v-if="op.etapaNombre"> · {{ op.etapaNombre }}</template>
          </span>
          <span class="op-master__foot">
            <GcBadge :tone="estadoTone(op.estadoMacro)" :label="estadoLabel(op.estadoMacro)" />
            <span class="op-master__value gc-mono">{{ fmtCurrency(op.valorEstimado, op.moneda) }}</span>
          </span>
        </div>
      </GcListRow>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { oportunidadService } from '@/services/oportunidad.service';
import { formatCurrency as fmtCurrency } from '@/utils/currency';
import GcInput from '@/components/ui/GcInput.vue';
import GcSelect from '@/components/ui/GcSelect.vue';
import GcListRow from '@/components/ui/GcListRow.vue';
import GcBadge from '@/components/ui/GcBadge.vue';
import GcEmpty from '@/components/ui/GcEmpty.vue';
import GcSpinner from '@/components/ui/GcSpinner.vue';

defineProps({
  selectedId: { type: [String, Number], default: null },
});
defineEmits(['select']);

const oportunidades = ref([]);
const loading = ref(true);
const search = ref('');
const filtroEstado = ref('');
let timer = null;

const estadoOptions = [
  { value: '', label: 'Todos los estados' },
  { value: 'ABIERTA', label: 'Abierta' },
  { value: 'SEGUIMIENTO', label: 'Seguimiento' },
  { value: 'GANADA', label: 'Ganada' },
  { value: 'CONTRATADA', label: 'Contratada' },
  { value: 'PERDIDA', label: 'Perdida' },
  { value: 'NO_CONCRETADA', label: 'No concretada' },
];

const TONE = {
  ABIERTA: 'info',
  SEGUIMIENTO: 'warning',
  GANADA: 'success',
  CONTRATADA: 'accent',
  PERDIDA: 'danger',
  NO_CONCRETADA: 'neutral',
};
function estadoTone(e) { return TONE[e] || 'neutral'; }
function estadoLabel(e) {
  return (estadoOptions.find((o) => o.value === e) || {}).label || e;
}

async function reload() {
  loading.value = true;
  try {
    const params = { page: 1, page_size: 50 };
    if (search.value) params.q = search.value;
    if (filtroEstado.value) params.estado = filtroEstado.value;
    const res = await oportunidadService.listar(params);
    oportunidades.value = res.data || [];
  } catch (err) {
    console.error('Error cargando oportunidades:', err);
    oportunidades.value = [];
  } finally {
    loading.value = false;
  }
}

function debouncedReload() {
  clearTimeout(timer);
  timer = setTimeout(reload, 400);
}

onMounted(reload);
defineExpose({ reload });
</script>

<style scoped>
.op-master { display: flex; flex-direction: column; height: 100%; }
.op-master__filters {
  display: flex;
  flex-direction: column;
  gap: var(--gc-space-2);
  padding: var(--gc-space-3);
  border-bottom: 1px solid var(--gc-border);
}
.op-master__state { display: flex; justify-content: center; padding: var(--gc-space-8); color: var(--gc-text-3); }
.op-master__list { flex: 1; }
.op-master__row { display: flex; flex-direction: column; gap: 4px; min-width: 0; }
.op-master__name {
  font-size: var(--gc-fs-md);
  color: var(--gc-text);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.op-master__meta {
  font-size: var(--gc-fs-xs);
  color: var(--gc-text-3);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.op-master__foot { display: flex; align-items: center; justify-content: space-between; gap: var(--gc-space-2); }
.op-master__value { font-size: var(--gc-fs-xs); color: var(--gc-text-2); }
</style>
