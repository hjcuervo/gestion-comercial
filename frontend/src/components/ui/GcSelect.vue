<template>
  <label class="gc-field">
    <span v-if="label" class="gc-field__label">{{ label }}</span>
    <span class="gc-field__control" :class="{ 'gc-field__control--error': error, 'gc-field__control--disabled': disabled }">
      <select
        class="gc-field__select"
        :value="modelValue"
        :disabled="disabled"
        @change="$emit('update:modelValue', $event.target.value)"
      >
        <option v-if="placeholder" value="" disabled>{{ placeholder }}</option>
        <slot>
          <option v-for="opt in normalizedOptions" :key="opt.value" :value="opt.value">
            {{ opt.label }}
          </option>
        </slot>
      </select>
      <GcIcon name="chevron-down" :size="16" class="gc-field__chevron" />
    </span>
    <span v-if="error" class="gc-field__error">{{ error }}</span>
  </label>
</template>

<script setup>
import { computed } from 'vue';
import GcIcon from './GcIcon.vue';

const props = defineProps({
  modelValue: { type: [String, Number], default: '' },
  label: { type: String, default: '' },
  placeholder: { type: String, default: '' },
  // options: array de string u objetos { value, label }
  options: { type: Array, default: () => [] },
  error: { type: String, default: '' },
  disabled: { type: Boolean, default: false },
});

defineEmits(['update:modelValue']);

const normalizedOptions = computed(() =>
  props.options.map((o) =>
    typeof o === 'object' ? { value: o.value, label: o.label ?? o.value } : { value: o, label: o }
  )
);
</script>

<style scoped>
.gc-field { display: flex; flex-direction: column; gap: var(--gc-space-1); }
.gc-field__label { font-size: var(--gc-fs-sm); font-weight: var(--gc-fw-medium); color: var(--gc-text-2); }
.gc-field__control {
  position: relative;
  display: flex;
  align-items: center;
  height: 34px;
  padding: 0 var(--gc-space-3);
  background: var(--gc-surface-2);
  border: 1px solid var(--gc-border);
  border-radius: var(--gc-radius-md);
  transition: border-color var(--gc-t-fast), background var(--gc-t-fast);
}
.gc-field__control:focus-within { border-color: var(--gc-primary); background: var(--gc-surface); }
.gc-field__control--error { border-color: var(--gc-danger); }
.gc-field__control--disabled { opacity: 0.55; }
.gc-field__select {
  flex: 1;
  min-width: 0;
  appearance: none;
  background: transparent;
  border: none;
  outline: none;
  color: var(--gc-text);
  font-size: var(--gc-fs-md);
  padding-right: var(--gc-space-5);
  cursor: pointer;
}
.gc-field__chevron {
  position: absolute;
  right: var(--gc-space-3);
  color: var(--gc-text-3);
  pointer-events: none;
}
.gc-field__error { font-size: var(--gc-fs-sm); color: var(--gc-danger); }
</style>
