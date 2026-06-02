<template>
  <GcModal :open="visible" :title="isEditing ? 'Editar persona' : 'Nueva persona'" width="560px" @close="$emit('close')">
    <div class="permodal">
      <div class="permodal__row">
        <GcInput v-model="form.nombres" label="Nombres" placeholder="Ej: Juan Carlos" icon="user" :error="errors.nombres" />
        <GcInput v-model="form.apellidos" label="Apellidos" placeholder="Ej: Pérez García" :error="errors.apellidos" />
      </div>
      <div class="permodal__row">
        <GcInput v-model="form.email" label="Email personal" type="email" placeholder="juan@email.com" :error="errors.email" />
        <GcInput v-model="form.telefono" label="Teléfono" placeholder="+57 300 123 4567" />
      </div>
      <div v-if="isEditing" class="permodal__field">
        <span class="permodal__label">Estado</span>
        <div class="permodal__toggle">
          <button class="permodal__opt" :class="{ 'permodal__opt--on': form.activo }" @click="form.activo = true">Activo</button>
          <button class="permodal__opt" :class="{ 'permodal__opt--on': !form.activo }" @click="form.activo = false">Inactivo</button>
        </div>
      </div>
      <div v-if="error" class="permodal__error"><GcIcon name="alert-circle" :size="16" /><span>{{ error }}</span></div>
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
  persona: { type: Object, default: null },
  saving: { type: Boolean, default: false },
  error: { type: String, default: null },
});
const emit = defineEmits(['close', 'submit']);

const isEditing = computed(() => !!props.persona);
const form = reactive({ nombres: '', apellidos: '', email: '', telefono: '', activo: true });
const errors = reactive({ nombres: '', apellidos: '', email: '' });

watch(() => props.visible, (val) => {
  if (!val) return;
  if (props.persona) {
    form.nombres = props.persona.nombres || '';
    form.apellidos = props.persona.apellidos || '';
    form.email = props.persona.email || '';
    form.telefono = props.persona.telefono || '';
    form.activo = props.persona.activo !== false;
  } else {
    Object.assign(form, { nombres: '', apellidos: '', email: '', telefono: '', activo: true });
  }
  errors.nombres = ''; errors.apellidos = ''; errors.email = '';
});

function validate() {
  errors.nombres = ''; errors.apellidos = ''; errors.email = '';
  let ok = true;
  if (!form.nombres.trim()) { errors.nombres = 'Requerido'; ok = false; }
  if (!form.apellidos.trim()) { errors.apellidos = 'Requerido'; ok = false; }
  if (form.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) { errors.email = 'Email inválido'; ok = false; }
  return ok;
}

function handleSubmit() {
  if (!validate()) return;
  const payload = { nombres: form.nombres.trim(), apellidos: form.apellidos.trim(), email: form.email?.trim() || undefined, telefono: form.telefono?.trim() || undefined };
  if (isEditing.value) payload.activo = form.activo;
  emit('submit', payload);
}
</script>

<style scoped>
.permodal { display: flex; flex-direction: column; gap: var(--gc-space-3); }
.permodal__row { display: grid; grid-template-columns: 1fr 1fr; gap: var(--gc-space-3); }
.permodal__field { display: flex; flex-direction: column; gap: var(--gc-space-2); }
.permodal__label { font-size: var(--gc-fs-sm); font-weight: var(--gc-fw-medium); color: var(--gc-text-2); }
.permodal__toggle { display: flex; gap: var(--gc-space-2); }
.permodal__opt { flex: 1; padding: var(--gc-space-2) var(--gc-space-3); background: var(--gc-surface-2); border: 1px solid var(--gc-border); border-radius: var(--gc-radius-md); color: var(--gc-text-2); font-size: var(--gc-fs-md); }
.permodal__opt--on { border-color: var(--gc-primary); background: var(--gc-surface); color: var(--gc-text); font-weight: var(--gc-fw-medium); }
.permodal__error { display: flex; align-items: center; gap: var(--gc-space-2); padding: var(--gc-space-3); background: var(--gc-danger-soft); border: 1px solid var(--gc-danger); border-radius: var(--gc-radius-md); font-size: var(--gc-fs-sm); color: var(--gc-danger); }
</style>
