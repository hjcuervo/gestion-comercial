<template>
  <label class="gc-field">
    <span v-if="label" class="gc-field__label">{{ label }}</span>
    <span class="gc-field__control" :class="{ 'gc-field__control--error': error, 'gc-field__control--disabled': disabled }">
      <GcIcon v-if="icon" :name="icon" :size="16" class="gc-field__icon" />
      <input
        class="gc-field__input"
        :class="{ 'gc-field__input--mono': mono }"
        :type="type"
        :value="modelValue"
        :placeholder="placeholder"
        :disabled="disabled"
        @input="$emit('update:modelValue', $event.target.value)"
        @keyup.enter="$emit('enter')"
      />
    </span>
    <span v-if="error" class="gc-field__error">{{ error }}</span>
  </label>
</template>

<script setup>
import GcIcon from './GcIcon.vue';

defineProps({
  modelValue: { type: [String, Number], default: '' },
  label: { type: String, default: '' },
  placeholder: { type: String, default: '' },
  type: { type: String, default: 'text' },
  icon: { type: String, default: '' },
  error: { type: String, default: '' },
  disabled: { type: Boolean, default: false },
  mono: { type: Boolean, default: false }, // datos (IDs, códigos) en monoespaciado
});

defineEmits(['update:modelValue', 'enter']);
</script>

<style scoped>
.gc-field {
  display: flex;
  flex-direction: column;
  gap: var(--gc-space-1);
}
.gc-field__label {
  font-size: var(--gc-fs-sm);
  font-weight: var(--gc-fw-medium);
  color: var(--gc-text-2);
}
.gc-field__control {
  display: flex;
  align-items: center;
  gap: var(--gc-space-2);
  height: 34px;
  padding: 0 var(--gc-space-3);
  background: var(--gc-surface-2);
  border: 1px solid var(--gc-border);
  border-radius: var(--gc-radius-md);
  transition: border-color var(--gc-t-fast), background var(--gc-t-fast);
}
.gc-field__control:focus-within {
  border-color: var(--gc-primary);
  background: var(--gc-surface);
}
.gc-field__control--error { border-color: var(--gc-danger); }
.gc-field__control--disabled { opacity: 0.55; }
.gc-field__icon { color: var(--gc-text-3); }
.gc-field__input {
  flex: 1;
  min-width: 0;
  background: transparent;
  border: none;
  outline: none;
  color: var(--gc-text);
  font-size: var(--gc-fs-md);
}
.gc-field__input--mono { font-family: var(--gc-font-mono); font-variant-numeric: tabular-nums; }
.gc-field__input::placeholder { color: var(--gc-text-3); }
.gc-field__error {
  font-size: var(--gc-fs-sm);
  color: var(--gc-danger);
}
</style>
