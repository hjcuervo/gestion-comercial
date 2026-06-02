<template>
  <label class="gc-field">
    <span v-if="label" class="gc-field__label">{{ label }}</span>
    <span class="gc-field__control" :class="{ 'gc-field__control--error': error, 'gc-field__control--disabled': disabled }">
      <textarea
        class="gc-field__textarea"
        :value="modelValue"
        :placeholder="placeholder"
        :rows="rows"
        :disabled="disabled"
        @input="$emit('update:modelValue', $event.target.value)"
      ></textarea>
    </span>
    <span v-if="error" class="gc-field__error">{{ error }}</span>
  </label>
</template>

<script setup>
defineProps({
  modelValue: { type: String, default: '' },
  label: { type: String, default: '' },
  placeholder: { type: String, default: '' },
  rows: { type: [Number, String], default: 3 },
  error: { type: String, default: '' },
  disabled: { type: Boolean, default: false },
});

defineEmits(['update:modelValue']);
</script>

<style scoped>
.gc-field { display: flex; flex-direction: column; gap: var(--gc-space-1); }
.gc-field__label { font-size: var(--gc-fs-sm); font-weight: var(--gc-fw-medium); color: var(--gc-text-2); }
.gc-field__control {
  padding: var(--gc-space-2) var(--gc-space-3);
  background: var(--gc-surface-2);
  border: 1px solid var(--gc-border);
  border-radius: var(--gc-radius-md);
  transition: border-color var(--gc-t-fast), background var(--gc-t-fast);
}
.gc-field__control:focus-within { border-color: var(--gc-primary); background: var(--gc-surface); }
.gc-field__control--error { border-color: var(--gc-danger); }
.gc-field__control--disabled { opacity: 0.55; }
.gc-field__textarea {
  width: 100%;
  background: transparent;
  border: none;
  outline: none;
  resize: vertical;
  color: var(--gc-text);
  font-family: var(--gc-font-sans);
  font-size: var(--gc-fs-md);
  line-height: var(--gc-lh-normal);
}
.gc-field__textarea::placeholder { color: var(--gc-text-3); }
.gc-field__error { font-size: var(--gc-fs-sm); color: var(--gc-danger); }
</style>
