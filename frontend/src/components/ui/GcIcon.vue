<template>
  <i
    :class="['gc-icon', 'ti', `ti-${name}`]"
    :style="iconStyle"
    aria-hidden="true"
  ></i>
</template>

<script setup>
import { computed } from 'vue';

/**
 * GcIcon — wrapper sobre la webfont de Tabler (outline).
 * Uso: <GcIcon name="search" :size="16" />
 * El `name` corresponde al id de Tabler SIN el prefijo `ti-`
 * (ej. "search", "chevron-right", "alert-circle").
 * Requiere que main.js importe '@tabler/icons-webfont/dist/tabler-icons.css'.
 */
const props = defineProps({
  name: { type: String, required: true },
  size: { type: [Number, String], default: 18 },
  color: { type: String, default: 'currentColor' },
});

const iconStyle = computed(() => ({
  fontSize: typeof props.size === 'number' ? `${props.size}px` : props.size,
  color: props.color,
  lineHeight: 1,
}));
</script>

<style scoped>
.gc-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
</style>
