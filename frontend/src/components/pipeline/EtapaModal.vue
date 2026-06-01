<template>
  <GcModal :open="visible" :title="isEditing ? 'Editar etapa' : 'Nueva etapa'" width="520px" @close="$emit('close')">
    <div class="etapamodal">
      <GcInput v-model="form.nombre" label="Nombre" placeholder="Ej: Calificación" :error="errors.nombre" />

      <div class="etapamodal__row">
        <GcInput v-model.number="form.orden" label="Orden" type="number" mono />
        <GcInput v-model.number="form.probabilidadSugerida" label="Probabilidad sugerida (%)" type="number" mono placeholder="0-100" />
      </div>

      <div class="etapamodal__field">
        <span class="etapamodal__label">Color</span>
        <div class="etapamodal__color">
          <input v-model="form.color" type="color" class="etapamodal__colorpick" />
          <GcInput v-model="form.color" mono placeholder="#1A1917" />
        </div>
      </div>

      <label class="etapamodal__toggle">
        <input type="checkbox" :checked="form.modoBloqueo === 1" @change="form.modoBloqueo = $event.target.checked ? 1 : 0" />
        <span>Etapa bloqueante (requiere acción para avanzar)</span>
      </label>

      <div v-if="isEditing" class="etapamodal__field">
        <span class="etapamodal__label">Estado</span>
        <div class="etapamodal__opts">
          <button class="etapamodal__opt" :class="{ 'etapamodal__opt--on': form.estado === 'ACTIVA' }" @click="form.estado = 'ACTIVA'">Activa</button>
          <button class="etapamodal__opt" :class="{ 'etapamodal__opt--on': form.estado === 'INACTIVA' }" @click="form.estado = 'INACTIVA'">Inactiva</button>
        </div>
      </div>

      <div v-if="error" class="etapamodal__error"><GcIcon name="alert-circle" :size="16" /><span>{{ error }}</span></div>
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
import GcButton from '@/components/ui/GcButton.vue';
import GcIcon from '@/components/ui/GcIcon.vue';

const props = defineProps({
  visible: { type: Boolean, default: false },
  etapa: { type: Object, default: null },
  nextOrden: { type: Number, default: 1 },
  saving: { type: Boolean, default: false },
  error: { type: String, default: null },
});
const emit = defineEmits(['close', 'submit']);

const isEditing = computed(() => !!props.etapa);
const form = reactive({ nombre: '', orden: 1, probabilidadSugerida: null, color: '#1A1917', modoBloqueo: 0, estado: 'ACTIVA' });
const errors = reactive({ nombre: '' });

watch(() => props.visible, (val) => {
  if (!val) return;
  if (props.etapa) {
    form.nombre = props.etapa.nombre || '';
    form.orden = props.etapa.orden || 1;
    form.probabilidadSugerida = props.etapa.probabilidadSugerida;
    form.color = props.etapa.color || '#1A1917';
    form.modoBloqueo = props.etapa.modoBloqueo || 0;
    form.estado = props.etapa.estado || 'ACTIVA';
  } else {
    form.nombre = '';
    form.orden = props.nextOrden;
    form.probabilidadSugerida = null;
    form.color = '#1A1917';
    form.modoBloqueo = 0;
    form.estado = 'ACTIVA';
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
  const payload = {
    nombre: form.nombre.trim(),
    orden: form.orden,
    probabilidadSugerida: form.probabilidadSugerida,
    color: form.color || undefined,
    modoBloqueo: form.modoBloqueo,
  };
  if (isEditing.value) payload.estado = form.estado;
  emit('submit', payload);
}
</script>

<style scoped>
.etapamodal { display: flex; flex-direction: column; gap: var(--gc-space-4); }
.etapamodal__row { display: grid; grid-template-columns: 1fr 1fr; gap: var(--gc-space-3); }
.etapamodal__field { display: flex; flex-direction: column; gap: var(--gc-space-2); }
.etapamodal__label { font-size: var(--gc-fs-sm); font-weight: var(--gc-fw-medium); color: var(--gc-text-2); }
.etapamodal__color { display: flex; align-items: center; gap: var(--gc-space-2); }
.etapamodal__colorpick { width: 40px; height: 34px; padding: 0; border: 1px solid var(--gc-border); border-radius: var(--gc-radius-md); background: transparent; cursor: pointer; flex-shrink: 0; }
.etapamodal__color > :deep(.gc-field) { flex: 1; }
.etapamodal__toggle { display: flex; align-items: center; gap: var(--gc-space-2); font-size: var(--gc-fs-md); color: var(--gc-text-2); cursor: pointer; }
.etapamodal__opts { display: flex; gap: var(--gc-space-2); }
.etapamodal__opt { flex: 1; padding: var(--gc-space-2) var(--gc-space-3); background: var(--gc-surface-2); border: 1px solid var(--gc-border); border-radius: var(--gc-radius-md); color: var(--gc-text-2); font-size: var(--gc-fs-md); }
.etapamodal__opt--on { border-color: var(--gc-primary); background: var(--gc-surface); color: var(--gc-text); font-weight: var(--gc-fw-medium); }
.etapamodal__error { display: flex; align-items: center; gap: var(--gc-space-2); padding: var(--gc-space-3); background: var(--gc-danger-soft); border: 1px solid var(--gc-danger); border-radius: var(--gc-radius-md); font-size: var(--gc-fs-sm); color: var(--gc-danger); }
</style>
