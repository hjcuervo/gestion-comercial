<template>
  <div class="gc-empty">
    <GcIcon :name="icon" :size="32" color="var(--gc-text-3)" />
    <p class="gc-empty__msg">{{ message }}</p>
    <div v-if="$slots.action" class="gc-empty__action"><slot name="action" /></div>
  </div>
</template>

<script setup>
import GcIcon from './GcIcon.vue';

defineProps({
  icon: { type: String, default: 'inbox' },
  message: { type: String, default: 'No hay datos para mostrar' },
});
</script>

<style scoped>
.gc-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--gc-space-3);
  padding: var(--gc-space-12) var(--gc-space-6);
  text-align: center;
}
.gc-empty__msg { font-size: var(--gc-fs-md); color: var(--gc-text-3); }
.gc-empty__action { margin-top: var(--gc-space-2); }
</style>
