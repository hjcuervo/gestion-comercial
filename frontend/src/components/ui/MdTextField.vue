<template>
  <div :class="['md-text-field', { 'md-text-field--error': error, 'md-text-field--disabled': disabled }]">
    <div class="md-text-field__container">
      <span v-if="leadingIcon" class="material-icons md-text-field__icon md-text-field__icon--leading">
        {{ leadingIcon }}
      </span>
      
      <input
        :id="id"
        :type="type"
        :value="modelValue"
        :placeholder="placeholder"
        :disabled="disabled"
        :readonly="readonly"
        class="md-text-field__input"
        @input="$emit('update:modelValue', $event.target.value)"
        @focus="focused = true"
        @blur="focused = false"
      />
      
      <label :for="id" class="md-text-field__label" :class="{ 'md-text-field__label--float': modelValue || focused || placeholder }">
        {{ label }}
      </label>
      
      <span v-if="trailingIcon" class="material-icons md-text-field__icon md-text-field__icon--trailing" @click="$emit('trailing-click')">
        {{ trailingIcon }}
      </span>
    </div>
    
    <div v-if="supportingText || error" class="md-text-field__supporting">
      {{ error || supportingText }}
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

defineProps({
  modelValue: [String, Number],
  label: String,
  placeholder: String,
  type: {
    type: String,
    default: 'text'
  },
  id: String,
  leadingIcon: String,
  trailingIcon: String,
  supportingText: String,
  error: String,
  disabled: Boolean,
  readonly: Boolean,
});

defineEmits(['update:modelValue', 'trailing-click']);

const focused = ref(false);
</script>

<style scoped>
.md-text-field {
  display: flex;
  flex-direction: column;
  gap: var(--md-sys-spacing-xs);
  width: 100%;
}

.md-text-field__container {
  position: relative;
  display: flex;
  align-items: center;
  background-color: var(--md-sys-color-surface-container-highest);
  border-radius: var(--md-sys-shape-corner-extra-small) var(--md-sys-shape-corner-extra-small) 0 0;
  border-bottom: 1px solid var(--md-sys-color-on-surface-variant);
  transition: all var(--md-sys-motion-duration-short) var(--md-sys-motion-easing-standard);
}

.md-text-field__container:focus-within {
  border-bottom: 2px solid var(--md-sys-color-primary);
}

.md-text-field--error .md-text-field__container {
  border-bottom-color: var(--md-sys-color-error);
}

.md-text-field--disabled .md-text-field__container {
  opacity: 0.38;
}

.md-text-field__input {
  flex: 1;
  padding: 24px 16px 8px;
  border: none;
  background: transparent;
  font: var(--md-sys-typescale-body-large);
  color: var(--md-sys-color-on-surface);
  outline: none;
}

.md-text-field__input::placeholder {
  color: transparent;
}

.md-text-field__label {
  position: absolute;
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  font: var(--md-sys-typescale-body-large);
  color: var(--md-sys-color-on-surface-variant);
  pointer-events: none;
  transition: all var(--md-sys-motion-duration-short) var(--md-sys-motion-easing-standard);
}

.md-text-field__label--float,
.md-text-field__input:focus + .md-text-field__label {
  top: 8px;
  transform: translateY(0);
  font: var(--md-sys-typescale-body-small);
  color: var(--md-sys-color-primary);
}

.md-text-field--error .md-text-field__label--float {
  color: var(--md-sys-color-error);
}

.md-text-field__icon {
  color: var(--md-sys-color-on-surface-variant);
}

.md-text-field__icon--leading {
  padding-left: 16px;
}

.md-text-field__icon--leading + .md-text-field__input {
  padding-left: 8px;
}

.md-text-field__icon--leading ~ .md-text-field__label {
  left: 52px;
}

.md-text-field__icon--trailing {
  cursor: pointer;
  padding-right: 16px;
}

.md-text-field__supporting {
  padding: 0 16px;
  font: var(--md-sys-typescale-body-small);
  color: var(--md-sys-color-on-surface-variant);
}

.md-text-field--error .md-text-field__supporting {
  color: var(--md-sys-color-error);
}
</style>
