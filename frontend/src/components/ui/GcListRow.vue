<template>
  <div
    class="gc-row"
    :class="[`gc-row--${tone}`, { 'gc-row--clickable': clickable, 'gc-row--active': active }]"
    @click="clickable && $emit('click', $event)"
  >
    <span class="gc-row__marker"></span>
    <div v-if="$slots.lead" class="gc-row__lead"><slot name="lead" /></div>
    <div class="gc-row__body">
      <slot />
    </div>
    <div v-if="$slots.actions" class="gc-row__actions">
      <slot name="actions" />
    </div>
  </div>
</template>

<script setup>
/**
 * GcListRow — renglón de lista. Marca de estado de 2px a la izquierda + datos.
 * Línea fina inferior por defecto. Sin sombras (identidad "instrumento").
 *
 * Slots: lead (icono/avatar opcional), default (contenido), actions (al final).
 * tone colorea la marca lateral: success | warning | danger | info | accent | neutral
 */
defineProps({
  tone: {
    type: String,
    default: 'neutral',
    validator: (v) => ['success', 'warning', 'danger', 'info', 'accent', 'neutral'].includes(v),
  },
  clickable: { type: Boolean, default: false },
  active: { type: Boolean, default: false },
});

defineEmits(['click']);
</script>

<style scoped>
.gc-row {
  position: relative;
  display: flex;
  align-items: center;
  gap: var(--gc-row-gap);
  padding: var(--gc-row-pad-y) var(--gc-row-pad-x);
  background: var(--gc-surface);
  border-bottom: 1px solid var(--gc-border);
  transition: background var(--gc-t-fast);
}
.gc-row__marker {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 2px;
  background: transparent;
}
.gc-row--success .gc-row__marker { background: var(--gc-success); }
.gc-row--warning .gc-row__marker { background: var(--gc-warning); }
.gc-row--danger  .gc-row__marker { background: var(--gc-danger); }
.gc-row--info    .gc-row__marker { background: var(--gc-info); }
.gc-row--accent  .gc-row__marker { background: var(--gc-accent); }
.gc-row--neutral .gc-row__marker { background: var(--gc-border-strong); }

.gc-row__lead { display: flex; align-items: center; color: var(--gc-text-3); }
.gc-row__body { flex: 1; min-width: 0; }
.gc-row__actions { display: flex; align-items: center; gap: var(--gc-space-2); }

.gc-row--clickable { cursor: pointer; }
.gc-row--clickable:hover { background: var(--gc-surface-2); }
.gc-row--active { background: var(--gc-surface-2); }
.gc-row--active .gc-row__marker { background: var(--gc-primary); }
</style>
