<template>
  <div :class="['input-group', { 'input-group--error': error, 'input-group--disabled': disabled, 'input-group--focused': focused }]">
    <label v-if="label" :for="id" class="input-group__label">
      {{ label }}
      <span v-if="required" class="input-group__required">*</span>
    </label>
    
    <div class="input-group__wrapper">
      <Icon v-if="icon" :name="icon" :size="20" class="input-group__icon" />
      
      <input
        :id="id"
        ref="inputRef"
        :type="computedType"
        :value="modelValue"
        :placeholder="placeholder"
        :disabled="disabled"
        :readonly="readonly"
        class="input-group__field"
        @input="$emit('update:modelValue', $event.target.value)"
        @focus="focused = true"
        @blur="focused = false"
      />
      
      <button 
        v-if="clearable && modelValue" 
        type="button"
        class="input-group__action"
        @click="$emit('update:modelValue', '')"
      >
        <Icon name="x" :size="16" />
      </button>
      
      <button 
        v-if="type === 'password'" 
        type="button"
        class="input-group__action"
        @click="showPassword = !showPassword"
      >
        <Icon :name="showPassword ? 'eye-off' : 'eye'" :size="18" />
      </button>
    </div>
    
    <p v-if="error" class="input-group__error">{{ error }}</p>
    <p v-else-if="hint" class="input-group__hint">{{ hint }}</p>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import Icon from './Icon.vue';

const props = defineProps({
  modelValue: [String, Number],
  label: String,
  placeholder: String,
  type: {
    type: String,
    default: 'text'
  },
  id: String,
  icon: String,
  hint: String,
  error: String,
  disabled: Boolean,
  readonly: Boolean,
  required: Boolean,
  clearable: Boolean,
});

defineEmits(['update:modelValue']);

const focused = ref(false);
const showPassword = ref(false);
const inputRef = ref(null);

const computedType = computed(() => {
  if (props.type === 'password' && showPassword.value) return 'text';
  return props.type;
});
</script>

<style scoped>
.input-group {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
  width: 100%;
}

.input-group__label {
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--text-secondary);
}

.input-group__required {
  color: var(--error);
  margin-left: 2px;
}

.input-group__wrapper {
  position: relative;
  display: flex;
  align-items: center;
  background: var(--glass-bg);
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  transition: all var(--duration-fast) var(--ease-out);
  overflow: hidden;
}

.input-group--focused .input-group__wrapper {
  border-color: var(--primary);
  box-shadow: 0 0 0 3px var(--primary-soft);
}

.input-group--error .input-group__wrapper {
  border-color: var(--error);
}

.input-group--error.input-group--focused .input-group__wrapper {
  box-shadow: 0 0 0 3px var(--error-soft);
}

.input-group--disabled .input-group__wrapper {
  opacity: 0.5;
  cursor: not-allowed;
}

.input-group__icon {
  padding-left: var(--space-4);
  color: var(--text-muted);
  flex-shrink: 0;
  transition: color var(--duration-fast);
}

.input-group--focused .input-group__icon {
  color: var(--primary);
}

.input-group__field {
  flex: 1;
  padding: var(--space-4);
  border: none;
  background: transparent;
  font-family: var(--font-body);
  font-size: var(--text-base);
  color: var(--text-primary);
  outline: none;
  width: 100%;
}

.input-group__field::placeholder {
  color: var(--text-muted);
}

.input-group__field:disabled {
  cursor: not-allowed;
}

.input-group__icon + .input-group__field {
  padding-left: var(--space-2);
}

.input-group__action {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--space-2);
  margin-right: var(--space-2);
  border: none;
  background: transparent;
  color: var(--text-muted);
  cursor: pointer;
  border-radius: var(--radius-sm);
  transition: all var(--duration-fast);
}

.input-group__action:hover {
  background: var(--glass-hover);
  color: var(--text-primary);
}

.input-group__error {
  font-size: var(--text-sm);
  color: var(--error);
}

.input-group__hint {
  font-size: var(--text-sm);
  color: var(--text-muted);
}
</style>
