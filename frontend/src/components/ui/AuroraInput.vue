<template>
  <div :class="['aurora-input', { 'aurora-input--error': error, 'aurora-input--disabled': disabled, 'aurora-input--focused': focused }]">
    <label v-if="label" :for="id" class="aurora-input__label">
      {{ label }}
      <span v-if="required" class="aurora-input__required">*</span>
    </label>
    
    <div class="aurora-input__wrapper">
      <span v-if="icon" class="material-icons-round aurora-input__icon">{{ icon }}</span>
      
      <input
        :id="id"
        ref="inputRef"
        :type="type"
        :value="modelValue"
        :placeholder="placeholder"
        :disabled="disabled"
        :readonly="readonly"
        class="aurora-input__field"
        @input="$emit('update:modelValue', $event.target.value)"
        @focus="focused = true"
        @blur="focused = false"
      />
      
      <button 
        v-if="clearable && modelValue" 
        type="button"
        class="aurora-input__clear"
        @click="$emit('update:modelValue', '')"
      >
        <span class="material-icons-round">close</span>
      </button>
      
      <button 
        v-if="trailingIcon" 
        type="button"
        class="aurora-input__trailing"
        @click="$emit('trailing-click')"
      >
        <span class="material-icons-round">{{ trailingIcon }}</span>
      </button>
    </div>
    
    <p v-if="error" class="aurora-input__error">{{ error }}</p>
    <p v-else-if="hint" class="aurora-input__hint">{{ hint }}</p>
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
  icon: String,
  trailingIcon: String,
  hint: String,
  error: String,
  disabled: Boolean,
  readonly: Boolean,
  required: Boolean,
  clearable: Boolean,
});

defineEmits(['update:modelValue', 'trailing-click']);

const focused = ref(false);
const inputRef = ref(null);
</script>

<style scoped>
.aurora-input {
  display: flex;
  flex-direction: column;
  gap: var(--aurora-space-2);
  width: 100%;
}

.aurora-input__label {
  font-size: var(--aurora-text-sm);
  font-weight: var(--aurora-font-medium);
  color: var(--aurora-text-secondary);
}

.aurora-input__required {
  color: var(--aurora-error);
  margin-left: 2px;
}

.aurora-input__wrapper {
  position: relative;
  display: flex;
  align-items: center;
  background: var(--aurora-bg-overlay);
  border: 1px solid var(--aurora-border);
  border-radius: var(--aurora-radius-lg);
  transition: all var(--aurora-transition-fast);
  overflow: hidden;
}

.aurora-input--focused .aurora-input__wrapper {
  border-color: var(--aurora-primary);
  box-shadow: 0 0 0 3px rgba(124, 58, 237, 0.15);
}

.aurora-input--error .aurora-input__wrapper {
  border-color: var(--aurora-error);
}

.aurora-input--error.aurora-input--focused .aurora-input__wrapper {
  box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.15);
}

.aurora-input--disabled .aurora-input__wrapper {
  opacity: 0.5;
  cursor: not-allowed;
}

.aurora-input__icon {
  padding-left: var(--aurora-space-4);
  color: var(--aurora-text-tertiary);
  font-size: 20px;
}

.aurora-input--focused .aurora-input__icon {
  color: var(--aurora-primary-light);
}

.aurora-input__field {
  flex: 1;
  padding: var(--aurora-space-3) var(--aurora-space-4);
  border: none;
  background: transparent;
  font-family: var(--aurora-font-family);
  font-size: var(--aurora-text-base);
  color: var(--aurora-text-primary);
  outline: none;
  width: 100%;
}

.aurora-input__field::placeholder {
  color: var(--aurora-text-muted);
}

.aurora-input__field:disabled {
  cursor: not-allowed;
}

.aurora-input__icon + .aurora-input__field {
  padding-left: var(--aurora-space-2);
}

.aurora-input__clear,
.aurora-input__trailing {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--aurora-space-2);
  margin-right: var(--aurora-space-2);
  border: none;
  background: transparent;
  color: var(--aurora-text-tertiary);
  cursor: pointer;
  border-radius: var(--aurora-radius-sm);
  transition: all var(--aurora-transition-fast);
}

.aurora-input__clear:hover,
.aurora-input__trailing:hover {
  background: var(--aurora-bg-overlay);
  color: var(--aurora-text-primary);
}

.aurora-input__clear .material-icons-round,
.aurora-input__trailing .material-icons-round {
  font-size: 18px;
}

.aurora-input__error {
  font-size: var(--aurora-text-sm);
  color: var(--aurora-error);
}

.aurora-input__hint {
  font-size: var(--aurora-text-sm);
  color: var(--aurora-text-muted);
}
</style>
