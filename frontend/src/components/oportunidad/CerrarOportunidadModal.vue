<template>
  <GcModal :open="visible" title="Cerrar oportunidad" width="520px" @close="$emit('close')">
    <div class="cerrar">
      <p class="cerrar__nombre">{{ oportunidadNombre }}</p>

      <div class="cerrar__field">
        <span class="cerrar__label">Resultado</span>
        <div class="cerrar__opts">
          <button class="cerrar__opt" :class="{ 'cerrar__opt--on cerrar__opt--ganada': form.estadoMacro === 'GANADA' }" @click="form.estadoMacro = 'GANADA'">
            <GcIcon name="trophy" :size="16" /> Ganada
          </button>
          <button class="cerrar__opt" :class="{ 'cerrar__opt--on cerrar__opt--perdida': form.estadoMacro === 'PERDIDA' }" @click="form.estadoMacro = 'PERDIDA'">
            <GcIcon name="circle-x" :size="16" /> Perdida
          </button>
          <button class="cerrar__opt" :class="{ 'cerrar__opt--on cerrar__opt--noconc': form.estadoMacro === 'NO_CONCRETADA' }" @click="form.estadoMacro = 'NO_CONCRETADA'">
            <GcIcon name="ban" :size="16" /> No concretada
          </button>
        </div>
        <span v-if="errors.estadoMacro" class="cerrar__err">{{ errors.estadoMacro }}</span>
      </div>

      <GcSelect
        v-if="form.estadoMacro === 'GANADA' && pipelinesContratacion.length"
        v-model="form.pipelineContratacionId"
        label="Pipeline de contratación"
        :options="pipelineOptions"
        placeholder="Selecciona…"
        :error="errors.pipeline"
      />

      <GcTextarea v-model="form.comentario" label="Comentario" placeholder="Describe el resultado…" :rows="3" />

      <div v-if="error" class="cerrar__error"><GcIcon name="alert-circle" :size="16" /><span>{{ error }}</span></div>
    </div>

    <template #footer>
      <GcButton variant="ghost" @click="$emit('close')">Cancelar</GcButton>
      <GcButton :variant="form.estadoMacro === 'GANADA' ? 'primary' : 'danger'" :loading="saving" @click="handleSubmit">Cerrar oportunidad</GcButton>
    </template>
  </GcModal>
</template>

<script setup>
import { reactive, ref, computed, watch } from 'vue';
import { pipelineService } from '@/services/pipeline.service';
import GcModal from '@/components/ui/GcModal.vue';
import GcSelect from '@/components/ui/GcSelect.vue';
import GcTextarea from '@/components/ui/GcTextarea.vue';
import GcButton from '@/components/ui/GcButton.vue';
import GcIcon from '@/components/ui/GcIcon.vue';

const props = defineProps({
  visible: { type: Boolean, default: false },
  oportunidadNombre: { type: String, default: '' },
  saving: { type: Boolean, default: false },
  error: { type: String, default: null },
});
const emit = defineEmits(['close', 'submit']);

const form = reactive({ estadoMacro: '', comentario: '', pipelineContratacionId: null });
const errors = reactive({ estadoMacro: '', pipeline: '' });
const pipelinesContratacion = ref([]);

const pipelineOptions = computed(() => pipelinesContratacion.value.map((p) => ({ value: p.id, label: p.nombre })));

watch(() => props.visible, async (val) => {
  if (!val) return;
  form.estadoMacro = '';
  form.comentario = '';
  form.pipelineContratacionId = null;
  errors.estadoMacro = '';
  errors.pipeline = '';
  try {
    pipelinesContratacion.value = await pipelineService.listarActivos('CONTRATACION');
    if (pipelinesContratacion.value.length === 1) form.pipelineContratacionId = pipelinesContratacion.value[0].id;
  } catch { pipelinesContratacion.value = []; }
});

function handleSubmit() {
  errors.estadoMacro = '';
  errors.pipeline = '';
  if (!form.estadoMacro) { errors.estadoMacro = 'Selecciona un resultado'; return; }
  if (form.estadoMacro === 'GANADA' && !form.pipelineContratacionId && pipelinesContratacion.value.length > 0) {
    errors.pipeline = 'Seleccione el pipeline de contratación';
    return;
  }
  const payload = { estadoMacro: form.estadoMacro, comentario: form.comentario?.trim() || undefined };
  if (form.estadoMacro === 'GANADA' && form.pipelineContratacionId) payload.pipelineContratacionId = form.pipelineContratacionId;
  emit('submit', payload);
}
</script>

<style scoped>
.cerrar { display: flex; flex-direction: column; gap: var(--gc-space-4); }
.cerrar__nombre { font-size: var(--gc-fs-md); color: var(--gc-text-2); }
.cerrar__field { display: flex; flex-direction: column; gap: var(--gc-space-2); }
.cerrar__label { font-size: var(--gc-fs-sm); font-weight: var(--gc-fw-medium); color: var(--gc-text-2); }
.cerrar__opts { display: flex; flex-direction: column; gap: var(--gc-space-2); }
.cerrar__opt {
  display: flex; align-items: center; gap: var(--gc-space-2);
  padding: var(--gc-space-3);
  background: var(--gc-surface-2);
  border: 1px solid var(--gc-border);
  border-radius: var(--gc-radius-md);
  color: var(--gc-text-2);
  font-size: var(--gc-fs-md);
  text-align: left;
}
.cerrar__opt:hover { border-color: var(--gc-border-strong); color: var(--gc-text); }
.cerrar__opt--on { color: var(--gc-text); font-weight: var(--gc-fw-medium); }
.cerrar__opt--ganada.cerrar__opt--on { border-color: var(--gc-success); background: var(--gc-success-soft); }
.cerrar__opt--perdida.cerrar__opt--on { border-color: var(--gc-danger); background: var(--gc-danger-soft); }
.cerrar__opt--noconc.cerrar__opt--on { border-color: var(--gc-text-3); background: var(--gc-neutral-soft); }
.cerrar__err { font-size: var(--gc-fs-sm); color: var(--gc-danger); }
.cerrar__error { display: flex; align-items: center; gap: var(--gc-space-2); padding: var(--gc-space-3); background: var(--gc-danger-soft); border: 1px solid var(--gc-danger); border-radius: var(--gc-radius-md); font-size: var(--gc-fs-sm); color: var(--gc-danger); }
</style>
