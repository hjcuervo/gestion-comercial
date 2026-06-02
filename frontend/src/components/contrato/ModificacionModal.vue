<template>
  <GcModal :open="true" title="Nueva modificación" width="560px" @close="$emit('close')">
    <div class="mod">
      <div class="mod__row">
        <GcSelect v-model="form.tipoModificacion" label="Tipo" :options="tipoOptions" placeholder="Selecciona…" />
        <GcInput v-model="form.fechaModificacion" label="Fecha" type="date" />
      </div>

      <label class="mod__toggle">
        <input type="checkbox" v-model="form.modificaValor" />
        <span>Modifica el valor del contrato</span>
      </label>
      <GcInput v-if="form.modificaValor" :model-value="valorDisplay" label="Valor de la modificación" mono placeholder="0" @update:modelValue="onValorInput" />

      <label class="mod__toggle">
        <input type="checkbox" v-model="form.modificaTiempo" />
        <span>Modifica la fecha de fin</span>
      </label>
      <GcInput v-if="form.modificaTiempo" v-model="form.nuevaFechaFin" label="Nueva fecha de fin" type="date" />

      <GcTextarea v-model="form.observaciones" label="Observaciones" placeholder="Opcional" :rows="3" />

      <div v-if="error" class="mod__error"><GcIcon name="alert-circle" :size="16" /><span>{{ error }}</span></div>
    </div>
    <template #footer>
      <GcButton variant="ghost" @click="$emit('close')">Cancelar</GcButton>
      <GcButton variant="primary" :loading="saving" :disabled="!isValid" @click="submit">Crear</GcButton>
    </template>
  </GcModal>
</template>

<script setup>
import { reactive, computed } from 'vue';
import { formatMoneyInput } from '@/utils/currency';
import GcModal from '@/components/ui/GcModal.vue';
import GcInput from '@/components/ui/GcInput.vue';
import GcSelect from '@/components/ui/GcSelect.vue';
import GcTextarea from '@/components/ui/GcTextarea.vue';
import GcButton from '@/components/ui/GcButton.vue';
import GcIcon from '@/components/ui/GcIcon.vue';

const props = defineProps({
  saving: { type: Boolean, default: false },
  error: { type: String, default: null },
});
const emit = defineEmits(['close', 'submit']);

// GcContratoModificacion.TipoModificacion (contexto del proyecto)
const tipoOptions = [
  { value: 'ADICION', label: 'Adición' },
  { value: 'PRORROGA', label: 'Prórroga' },
  { value: 'SUSPENSION', label: 'Suspensión' },
  { value: 'REINICIO', label: 'Reinicio' },
  { value: 'OTRO', label: 'Otro' },
];

const form = reactive({
  tipoModificacion: '', fechaModificacion: '',
  modificaValor: false, valorModificacion: null,
  modificaTiempo: false, nuevaFechaFin: '',
  observaciones: '',
});

const valorDisplay = computed(() => (form.valorModificacion == null || form.valorModificacion === '' ? '' : formatMoneyInput(form.valorModificacion)));
function onValorInput(value) {
  const raw = String(value).replace(/\./g, '').replace(/[^0-9]/g, '');
  const num = parseInt(raw, 10);
  form.valorModificacion = isNaN(num) ? null : num;
}

const isValid = computed(() => form.tipoModificacion && form.fechaModificacion);

function submit() {
  if (!isValid.value) return;
  emit('submit', {
    tipoModificacion: form.tipoModificacion,
    fechaModificacion: form.fechaModificacion,
    modificaValor: form.modificaValor,
    valorModificacion: form.modificaValor ? form.valorModificacion : null,
    modificaTiempo: form.modificaTiempo,
    nuevaFechaFin: form.modificaTiempo ? form.nuevaFechaFin : null,
    observaciones: form.observaciones || null,
  });
}
</script>

<style scoped>
.mod { display: flex; flex-direction: column; gap: var(--gc-space-3); }
.mod__row { display: grid; grid-template-columns: 1fr 1fr; gap: var(--gc-space-3); }
.mod__toggle { display: flex; align-items: center; gap: var(--gc-space-2); font-size: var(--gc-fs-md); color: var(--gc-text-2); cursor: pointer; }
.mod__error { display: flex; align-items: center; gap: var(--gc-space-2); padding: var(--gc-space-3); background: var(--gc-danger-soft); border: 1px solid var(--gc-danger); border-radius: var(--gc-radius-md); font-size: var(--gc-fs-sm); color: var(--gc-danger); }
</style>
