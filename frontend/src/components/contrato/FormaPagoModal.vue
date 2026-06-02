<template>
  <GcModal :open="true" title="Nueva forma de pago" width="520px" @close="$emit('close')">
    <div class="fp">
      <GcInput v-model="form.descripcion" label="Descripción" placeholder="Ej: Anticipo 30%, Cuota mensual…" />
      <div class="fp__row">
        <GcInput :model-value="valorDisplay" label="Monto presupuestado" mono placeholder="0" @update:modelValue="onValorInput" />
        <GcInput v-model="form.fechaEstimadaPago" label="Fecha esperada" type="date" />
      </div>
      <div v-if="error" class="fp__error"><GcIcon name="alert-circle" :size="16" /><span>{{ error }}</span></div>
    </div>
    <template #footer>
      <GcButton variant="ghost" @click="$emit('close')">Cancelar</GcButton>
      <GcButton variant="primary" :loading="saving" :disabled="!isValid" @click="submit">Crear</GcButton>
    </template>
  </GcModal>
</template>

<script setup>
import { ref, reactive, computed } from 'vue';
import { formatMoneyInput } from '@/utils/currency';
import GcModal from '@/components/ui/GcModal.vue';
import GcInput from '@/components/ui/GcInput.vue';
import GcButton from '@/components/ui/GcButton.vue';
import GcIcon from '@/components/ui/GcIcon.vue';

const props = defineProps({
  saving: { type: Boolean, default: false },
  error: { type: String, default: null },
});
const emit = defineEmits(['close', 'submit']);

const form = reactive({ descripcion: '', valor: null, fechaEstimadaPago: '' });
const valorDisplay = computed(() => (form.valor == null || form.valor === '' ? '' : formatMoneyInput(form.valor)));
function onValorInput(value) {
  const raw = String(value).replace(/\./g, '').replace(/[^0-9]/g, '');
  const num = parseInt(raw, 10);
  form.valor = isNaN(num) ? null : num;
}
const isValid = computed(() => form.descripcion.trim() && form.valor != null);

function submit() {
  if (!isValid.value) return;
  emit('submit', {
    descripcion: form.descripcion.trim(),
    valor: form.valor,
    fechaEstimadaPago: form.fechaEstimadaPago || '',
  });
}
</script>

<style scoped>
.fp { display: flex; flex-direction: column; gap: var(--gc-space-3); }
.fp__row { display: grid; grid-template-columns: 1fr 1fr; gap: var(--gc-space-3); }
.fp__error { display: flex; align-items: center; gap: var(--gc-space-2); padding: var(--gc-space-3); background: var(--gc-danger-soft); border: 1px solid var(--gc-danger); border-radius: var(--gc-radius-md); font-size: var(--gc-fs-sm); color: var(--gc-danger); }
</style>
