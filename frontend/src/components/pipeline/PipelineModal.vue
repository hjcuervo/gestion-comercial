<template>
  <GcModal :open="visible" :title="isEditing ? 'Editar pipeline' : 'Nuevo pipeline'" width="520px" @close="$emit('close')">
    <div class="pipmodal">
      <GcInput v-model="form.nombre" label="Nombre" placeholder="Ej: Pipeline Comercial" :error="errors.nombre" />

      <div v-if="!isEditing" class="pipmodal__field">
        <span class="pipmodal__label">Ámbito</span>
        <div class="pipmodal__opts">
          <button class="pipmodal__opt" :class="{ 'pipmodal__opt--on': form.ambito === 'COMERCIAL' }" @click="form.ambito = 'COMERCIAL'">Comercial</button>
          <button class="pipmodal__opt" :class="{ 'pipmodal__opt--on': form.ambito === 'CONTRATACION' }" @click="form.ambito = 'CONTRATACION'">Contratación</button>
        </div>
        <span class="pipmodal__hint">{{ form.ambito === 'COMERCIAL' ? 'Para gestión de oportunidades comerciales' : 'Para formalización de contratos después de adjudicación' }}</span>
      </div>

      <label v-if="isEditing" class="pipmodal__toggle">
        <input type="checkbox" v-model="form.activo" />
        <span>Pipeline activo</span>
      </label>

      <GcTextarea v-model="form.descripcion" label="Descripción" placeholder="Opcional" :rows="3" />

      <div v-if="error" class="pipmodal__error"><GcIcon name="alert-circle" :size="16" /><span>{{ error }}</span></div>
    </div>

    <template #footer>
      <GcButton variant="ghost" @click="$emit('close')">Cancelar</GcButton>
      <GcButton variant="primary" :loading="saving" @click="handleSubmit">{{ isEditing ? 'Guardar' : 'Crear' }}</GcButton>
    </template>
  </GcModal>
</template>

<script setup>
import { reactive, computed, watch } from 'vue';
import GcModal from '@/components/ui/GcModal.vue';
import GcInput from '@/components/ui/GcInput.vue';
import GcTextarea from '@/components/ui/GcTextarea.vue';
import GcButton from '@/components/ui/GcButton.vue';
import GcIcon from '@/components/ui/GcIcon.vue';

const props = defineProps({
  visible: { type: Boolean, default: false },
  pipeline: { type: Object, default: null },
  saving: { type: Boolean, default: false },
  error: { type: String, default: null },
});
const emit = defineEmits(['close', 'submit']);

const isEditing = computed(() => !!props.pipeline);
const form = reactive({ nombre: '', descripcion: '', activo: true, ambito: 'COMERCIAL' });
const errors = reactive({ nombre: '' });

watch(() => props.visible, (val) => {
  if (!val) return;
  if (props.pipeline) {
    form.nombre = props.pipeline.nombre || '';
    form.descripcion = props.pipeline.descripcion || '';
    form.activo = props.pipeline.estado === 'ACTIVO';
    form.ambito = props.pipeline.ambito || 'COMERCIAL';
  } else {
    form.nombre = '';
    form.descripcion = '';
    form.activo = true;
    form.ambito = 'COMERCIAL';
  }
  errors.nombre = '';
});

function validate() {
  errors.nombre = '';
  if (!form.nombre.trim()) { errors.nombre = 'El nombre es requerido'; return false; }
  if (form.nombre.length > 100) { errors.nombre = 'Máximo 100 caracteres'; return false; }
  return true;
}

function handleSubmit() {
  if (!validate()) return;
  const payload = { nombre: form.nombre.trim(), descripcion: form.descripcion?.trim() || undefined };
  if (!isEditing.value) payload.ambito = form.ambito;
  if (isEditing.value) payload.activo = form.activo;
  emit('submit', payload);
}
</script>

<style scoped>
.pipmodal { display: flex; flex-direction: column; gap: var(--gc-space-4); }
.pipmodal__field { display: flex; flex-direction: column; gap: var(--gc-space-2); }
.pipmodal__label { font-size: var(--gc-fs-sm); font-weight: var(--gc-fw-medium); color: var(--gc-text-2); }
.pipmodal__opts { display: flex; gap: var(--gc-space-2); }
.pipmodal__opt { flex: 1; padding: var(--gc-space-2) var(--gc-space-3); background: var(--gc-surface-2); border: 1px solid var(--gc-border); border-radius: var(--gc-radius-md); color: var(--gc-text-2); font-size: var(--gc-fs-md); }
.pipmodal__opt--on { border-color: var(--gc-primary); background: var(--gc-surface); color: var(--gc-text); font-weight: var(--gc-fw-medium); }
.pipmodal__hint { font-size: var(--gc-fs-xs); color: var(--gc-text-3); }
.pipmodal__toggle { display: flex; align-items: center; gap: var(--gc-space-2); font-size: var(--gc-fs-md); color: var(--gc-text-2); cursor: pointer; }
.pipmodal__error { display: flex; align-items: center; gap: var(--gc-space-2); padding: var(--gc-space-3); background: var(--gc-danger-soft); border: 1px solid var(--gc-danger); border-radius: var(--gc-radius-md); font-size: var(--gc-fs-sm); color: var(--gc-danger); }
</style>
