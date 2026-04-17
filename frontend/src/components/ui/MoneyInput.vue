<template>
  <div class="money-input-wrap">
    <span class="money-input__prefix">{{ currencySymbol }}</span>
    <input
      ref="inputRef"
      type="text"
      inputmode="numeric"
      class="money-input"
      :class="inputClass"
      :placeholder="placeholder"
      :required="required"
      :disabled="disabled"
      :value="displayValue"
      @input="onInput"
      @focus="onFocus"
      @blur="onBlur"
    />
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';

const props = defineProps({
  modelValue: { type: [Number, null], default: null },
  currency: { type: String, default: 'COP' },
  placeholder: { type: String, default: '' },
  required: { type: Boolean, default: false },
  disabled: { type: Boolean, default: false },
  inputClass: { type: String, default: '' },
});

const emit = defineEmits(['update:modelValue']);

const inputRef = ref(null);
const isFocused = ref(false);

const currencySymbol = computed(() => props.currency === 'USD' ? 'USD' : '$');

const displayValue = computed(() => {
  if (props.modelValue == null || props.modelValue === '') return '';
  if (isFocused.value) {
    // While editing, show raw number
    return props.modelValue.toString();
  }
  // When not focused, show formatted
  return formatNumber(props.modelValue);
});

function formatNumber(val) {
  if (val == null || isNaN(val)) return '';
  const num = Number(val);
  if (props.currency === 'USD') {
    return new Intl.NumberFormat('en-US', { minimumFractionDigits: 0, maximumFractionDigits: 2 }).format(num);
  }
  return new Intl.NumberFormat('es-CO', { minimumFractionDigits: 0, maximumFractionDigits: 0 }).format(num);
}

function onInput(e) {
  // Remove all non-numeric characters except dot and minus
  let raw = e.target.value.replace(/[^0-9.,\-]/g, '');
  // Replace comma with dot for decimal
  raw = raw.replace(/,/g, '.');
  // Remove multiple dots
  const parts = raw.split('.');
  if (parts.length > 2) raw = parts[0] + '.' + parts.slice(1).join('');

  const num = parseFloat(raw);
  if (!isNaN(num)) {
    emit('update:modelValue', num);
  } else if (raw === '' || raw === '-') {
    emit('update:modelValue', null);
  }
}

function onFocus() { isFocused.value = true; }
function onBlur() { isFocused.value = false; }
</script>

<style scoped>
.money-input-wrap {
  position: relative;
  display: flex;
  align-items: center;
}
.money-input__prefix {
  position: absolute;
  left: 10px;
  font-size: var(--text-xs);
  font-weight: 600;
  color: var(--text-muted);
  pointer-events: none;
  font-family: var(--font-mono, monospace);
}
.money-input {
  width: 100%;
  background: var(--bg-surface);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-md);
  color: var(--text-primary);
  font-family: var(--font-mono, monospace);
  font-size: var(--text-xs);
  font-weight: 600;
  padding: var(--space-2) var(--space-3);
  padding-left: 40px;
  text-align: right;
}
.money-input:focus {
  outline: none;
  border-color: var(--primary);
}
.money-input:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
