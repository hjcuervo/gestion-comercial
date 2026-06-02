<template>
  <GcModal :open="visible" :title="isEditing ? 'Editar oportunidad' : 'Nueva oportunidad'" width="600px" @close="$emit('close')">
    <div class="opmodal">
      <GcInput v-model="form.nombre" label="Nombre de la oportunidad" placeholder="Ej: Implementación ERP Acme" :error="errors.nombre" />

      <div class="opmodal__row">
        <GcSelect
          v-model="form.empresaId"
          label="Empresa"
          :options="empresaOptions"
          placeholder="Selecciona empresa…"
          :error="errors.empresaId"
          :disabled="isEditing"
        />
        <GcSelect
          v-if="!isEditing"
          v-model="form.pipelineId"
          label="Pipeline"
          :options="pipelineOptions"
          placeholder="Selecciona pipeline…"
          :error="errors.pipelineId"
          @update:modelValue="onPipelineChange"
        />
      </div>

      <GcSelect
        v-if="!isEditing && etapaOptions.length"
        v-model="form.etapaId"
        label="Etapa inicial"
        :options="etapaOptions"
        placeholder="(primera etapa por defecto)"
      />

      <div class="opmodal__row">
        <GcInput :model-value="valorDisplay" label="Valor estimado" mono placeholder="0" @update:modelValue="onValorInput" />
        <GcSelect v-model="form.moneda" label="Moneda" :options="['COP', 'USD', 'EUR']" />
      </div>

      <div class="opmodal__row">
        <GcInput v-model.number="form.probabilidad" label="Probabilidad (%)" type="number" mono placeholder="0-100" />
        <GcInput v-model="form.fechaEstimadaCierre" label="Cierre estimado" type="date" />
      </div>

      <GcInput v-model="form.tipoServicio" label="Tipo de servicio" placeholder="Ej: Consultoría, Implementación, Soporte" />
      <GcInput v-model="form.fuente" label="Fuente" placeholder="Ej: Referido, Licitación, Web" />

      <div v-if="error" class="opmodal__error"><GcIcon name="alert-circle" :size="16" /><span>{{ error }}</span></div>
    </div>

    <template #footer>
      <GcButton variant="ghost" @click="$emit('close')">Cancelar</GcButton>
      <GcButton variant="primary" :loading="saving" @click="handleSubmit">{{ isEditing ? 'Guardar' : 'Crear' }}</GcButton>
    </template>
  </GcModal>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue';
import { pipelineService } from '@/services/pipeline.service';
import { formatMoneyInput } from '@/utils/currency';
import GcModal from '@/components/ui/GcModal.vue';
import GcInput from '@/components/ui/GcInput.vue';
import GcSelect from '@/components/ui/GcSelect.vue';
import GcButton from '@/components/ui/GcButton.vue';
import GcIcon from '@/components/ui/GcIcon.vue';

const props = defineProps({
  visible: { type: Boolean, default: false },
  oportunidad: { type: Object, default: null },
  empresas: { type: Array, default: () => [] },
  pipelines: { type: Array, default: () => [] },
  pipelinePreseleccionado: { type: Number, default: null },
  saving: { type: Boolean, default: false },
  error: { type: String, default: null },
});
const emit = defineEmits(['close', 'submit']);

const isEditing = computed(() => !!props.oportunidad);
const etapasDisponibles = ref([]);

const form = reactive({
  nombre: '', empresaId: null, pipelineId: null, etapaId: null,
  valorEstimado: null, moneda: 'COP', probabilidad: null,
  fechaEstimadaCierre: '', fuente: '', tipoServicio: '',
});
const errors = reactive({ nombre: '', empresaId: '', pipelineId: '' });

const empresaOptions = computed(() => props.empresas.map((e) => ({ value: e.id, label: e.razonSocial || e.nombre })));
const pipelineOptions = computed(() => props.pipelines.map((p) => ({ value: p.id, label: p.nombre })));
const etapaOptions = computed(() => etapasDisponibles.value.map((e) => ({ value: e.id, label: e.nombre })));

const valorDisplay = computed(() => (form.valorEstimado == null || form.valorEstimado === '' ? '' : formatMoneyInput(form.valorEstimado)));
function onValorInput(value) {
  const raw = String(value).replace(/\./g, '').replace(/[^0-9]/g, '');
  const num = parseInt(raw, 10);
  form.valorEstimado = isNaN(num) ? null : num;
}

watch(() => props.visible, async (val) => {
  if (!val) return;
  etapasDisponibles.value = [];
  if (props.oportunidad) {
    form.nombre = props.oportunidad.nombre || '';
    form.empresaId = props.oportunidad.empresaId;
    form.pipelineId = props.oportunidad.pipelineId;
    form.etapaId = props.oportunidad.etapaId;
    form.valorEstimado = props.oportunidad.valorEstimado;
    form.moneda = props.oportunidad.moneda || 'COP';
    form.probabilidad = props.oportunidad.probabilidad;
    form.fechaEstimadaCierre = props.oportunidad.fechaEstimadaCierre || '';
    form.fuente = props.oportunidad.fuente || '';
    form.tipoServicio = props.oportunidad.tipoServicio || '';
  } else {
    Object.assign(form, {
      nombre: '', empresaId: null, pipelineId: props.pipelinePreseleccionado, etapaId: null,
      valorEstimado: null, moneda: 'COP', probabilidad: null, fechaEstimadaCierre: '', fuente: '', tipoServicio: '',
    });
    if (form.pipelineId) await loadEtapas(form.pipelineId);
  }
  Object.assign(errors, { nombre: '', empresaId: '', pipelineId: '' });
});

async function onPipelineChange() {
  form.etapaId = null;
  if (form.pipelineId) await loadEtapas(form.pipelineId);
  else etapasDisponibles.value = [];
}
async function loadEtapas(pipelineId) {
  // RB-EI-2: solo etapas activas seleccionables al crear/editar
  try { etapasDisponibles.value = await pipelineService.listarEtapas(pipelineId, { estado: 'ACTIVA' }); }
  catch { etapasDisponibles.value = []; }
}

function validate() {
  Object.assign(errors, { nombre: '', empresaId: '', pipelineId: '' });
  let valid = true;
  if (!form.nombre.trim()) { errors.nombre = 'Requerido'; valid = false; }
  if (!form.empresaId) { errors.empresaId = 'Selecciona una empresa'; valid = false; }
  if (!isEditing.value && !form.pipelineId) { errors.pipelineId = 'Selecciona un pipeline'; valid = false; }
  return valid;
}

function handleSubmit() {
  if (!validate()) return;
  const payload = {
    nombre: form.nombre.trim(),
    valorEstimado: form.valorEstimado || undefined,
    moneda: form.moneda,
    probabilidad: form.probabilidad,
    fechaEstimadaCierre: form.fechaEstimadaCierre || undefined,
    fuente: form.fuente?.trim() || undefined,
    tipoServicio: form.tipoServicio?.trim() || undefined,
  };
  if (!isEditing.value) {
    payload.empresaId = form.empresaId;
    payload.pipelineId = form.pipelineId;
    payload.etapaId = form.etapaId || undefined;
  }
  emit('submit', payload);
}
</script>

<style scoped>
.opmodal { display: flex; flex-direction: column; gap: var(--gc-space-3); }
.opmodal__row { display: grid; grid-template-columns: 1fr 1fr; gap: var(--gc-space-3); }
.opmodal__error { display: flex; align-items: center; gap: var(--gc-space-2); padding: var(--gc-space-3); background: var(--gc-danger-soft); border: 1px solid var(--gc-danger); border-radius: var(--gc-radius-md); font-size: var(--gc-fs-sm); color: var(--gc-danger); }
</style>
