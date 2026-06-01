<template>
  <div class="cgroup">
    <div class="cgroup__head">
      <GcBadge :tone="tone" :label="titulo" />
      <span class="cgroup__count gc-mono">{{ items.length }}</span>
    </div>
    <div class="cgroup__rows">
      <GcListRow v-for="c in items" :key="c.id" :tone="tone">
        <div class="cgroup__main">
          <span class="cgroup__desc">{{ c.descripcion }}</span>
          <span class="cgroup__fecha gc-mono">{{ fmtDate(c.fechaCompromiso) }}</span>
        </div>
        <template #actions>
          <GcBadge v-if="esCerrado(c.estado)" :tone="estadoTone(c.estado)" :label="estadoLabel(c.estado)" />
          <GcButton
            v-else
            variant="ghost"
            size="sm"
            icon="check"
            @click="$emit('completar', c.id)"
          >
            Completar
          </GcButton>
        </template>
      </GcListRow>
    </div>
  </div>
</template>

<script setup>
import { formatDate as fmtDate } from '@/utils/datetime';
import GcListRow from '@/components/ui/GcListRow.vue';
import GcBadge from '@/components/ui/GcBadge.vue';
import GcButton from '@/components/ui/GcButton.vue';

defineProps({
  titulo: { type: String, required: true },
  tone: { type: String, default: 'neutral' },
  items: { type: Array, default: () => [] },
});
defineEmits(['completar']);

function esCerrado(e) { return e === 'COMPLETADO' || e === 'CANCELADO'; }
function estadoTone(e) { return e === 'CANCELADO' ? 'neutral' : 'success'; }
function estadoLabel(e) { return e === 'CANCELADO' ? 'Cancelado' : 'Completado'; }
</script>

<style scoped>
.cgroup { display: flex; flex-direction: column; gap: var(--gc-space-2); }
.cgroup__head { display: flex; align-items: center; gap: var(--gc-space-2); }
.cgroup__count { font-size: var(--gc-fs-xs); color: var(--gc-text-3); }
.cgroup__rows {
  border: 1px solid var(--gc-border);
  border-radius: var(--gc-radius-lg);
  overflow: hidden;
}
.cgroup__rows > :deep(.gc-row:last-child) { border-bottom: none; }
.cgroup__main { display: flex; flex-direction: column; gap: 2px; min-width: 0; }
.cgroup__desc { font-size: var(--gc-fs-md); color: var(--gc-text); }
.cgroup__fecha { font-size: var(--gc-fs-xs); color: var(--gc-text-3); }
</style>
