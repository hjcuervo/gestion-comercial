<template>
  <GcModal :open="true" title="Agregar documento" width="520px" @close="$emit('close')">
    <div class="docmodal">
      <GcSelect
        v-model="form.tipoDocumentoId"
        label="Tipo de documento"
        :options="tipoOptions"
        placeholder="Selecciona…"
      />
      <GcInput v-model="form.nombre" label="Nombre" placeholder="Ej: Contrato firmado, Póliza de cumplimiento" />
      <GcInput v-model="form.url" label="Enlace (URL)" type="url" placeholder="https://drive.google.com/…" />

      <div v-if="errorMsg" class="docmodal__error"><GcIcon name="alert-circle" :size="16" /><span>{{ errorMsg }}</span></div>
    </div>

    <template #footer>
      <GcButton variant="ghost" @click="$emit('close')">Cancelar</GcButton>
      <GcButton variant="primary" :loading="saving" :disabled="!isValid" @click="handleSubmit">Agregar</GcButton>
    </template>
  </GcModal>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { documentoService } from '@/services/documento.service';
import GcModal from '@/components/ui/GcModal.vue';
import GcInput from '@/components/ui/GcInput.vue';
import GcSelect from '@/components/ui/GcSelect.vue';
import GcButton from '@/components/ui/GcButton.vue';
import GcIcon from '@/components/ui/GcIcon.vue';

const props = defineProps({
  oportunidadId: { type: Number, default: null },
});
const emit = defineEmits(['close', 'created']);

const form = ref({ tipoDocumentoId: null, nombre: '', url: '' });
const tiposDocumento = ref([]);
const saving = ref(false);
const errorMsg = ref('');

const tipoOptions = computed(() => tiposDocumento.value.map((t) => ({ value: t.id, label: t.nombre })));
const isValid = computed(() => form.value.tipoDocumentoId && form.value.nombre.trim() && form.value.url.trim());

onMounted(async () => {
  try { tiposDocumento.value = await documentoService.listarTipos(); } catch { tiposDocumento.value = []; }
});

async function handleSubmit() {
  if (!isValid.value) return;
  saving.value = true;
  errorMsg.value = '';
  try {
    const payload = {
      tipoDocumentoId: form.value.tipoDocumentoId,
      nombre: form.value.nombre.trim(),
      url: form.value.url.trim(),
    };
    if (props.oportunidadId) payload.oportunidadId = props.oportunidadId;
    const created = await documentoService.crearEnlace(payload);
    emit('created', created);
  } catch (err) {
    errorMsg.value = err.response?.data?.message || 'No se pudo agregar el documento';
  } finally {
    saving.value = false;
  }
}
</script>

<style scoped>
.docmodal { display: flex; flex-direction: column; gap: var(--gc-space-3); }
.docmodal__error { display: flex; align-items: center; gap: var(--gc-space-2); padding: var(--gc-space-3); background: var(--gc-danger-soft); border: 1px solid var(--gc-danger); border-radius: var(--gc-radius-md); font-size: var(--gc-fs-sm); color: var(--gc-danger); }
</style>
