<template>
  <Teleport to="body">
    <Transition name="modal">
      <div v-if="visible" class="modal-overlay" @click.self="$emit('close')">
        <div class="modal-container animate-scaleIn">
          <div class="modal-header">
            <h3 class="modal-title gradient-text">{{ isEditing ? 'Editar Contacto' : 'Nuevo Contacto' }}</h3>
            <button class="modal-close" @click="$emit('close')"><Icon name="x" :size="20" /></button>
          </div>
          <div class="modal-body">
            <div class="form-row">
              <div class="form-group form-group--half">
                <Input v-model="form.nombres" label="Nombres" placeholder="Ej: Juan Carlos" icon="user" :error="errors.nombres" required />
              </div>
              <div class="form-group form-group--half">
                <Input v-model="form.apellidos" label="Apellidos" placeholder="Ej: Pérez García" :error="errors.apellidos" required />
              </div>
            </div>
            <div class="form-row">
              <div class="form-group form-group--half">
                <Input v-model="form.email" label="Email Personal" placeholder="juan@email.com" type="email" :error="errors.email" />
              </div>
              <div class="form-group form-group--half">
                <Input v-model="form.telefono" label="Teléfono" placeholder="+57 300 123 4567" />
              </div>
            </div>
            <div v-if="isEditing" class="form-group">
              <label class="form-label">Estado</label>
              <div class="toggle-group">
                <button class="toggle-btn" :class="{ active: form.activo }" @click="form.activo = true"><Icon name="check-circle" :size="14" /> Activo</button>
                <button class="toggle-btn" :class="{ active: !form.activo }" @click="form.activo = false"><Icon name="x" :size="14" /> Inactivo</button>
              </div>
            </div>
            <div v-if="error" class="form-error-banner"><Icon name="alert-circle" :size="16" /> {{ error }}</div>
          </div>
          <div class="modal-footer">
            <Button variant="ghost" @click="$emit('close')">Cancelar</Button>
            <Button variant="primary" icon="check" :loading="saving" @click="handleSubmit">{{ isEditing ? 'Guardar Cambios' : 'Crear Contacto' }}</Button>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { reactive, watch, computed } from 'vue';
import Icon from '@/components/ui/Icon.vue';
import Input from '@/components/ui/Input.vue';
import Button from '@/components/ui/Button.vue';

const props = defineProps({ visible: { type: Boolean, default: false }, persona: { type: Object, default: null }, saving: { type: Boolean, default: false }, error: { type: String, default: null } });
const emit = defineEmits(['close', 'submit']);
const isEditing = computed(() => !!props.persona);
const form = reactive({ nombres: '', apellidos: '', email: '', telefono: '', activo: true });
const errors = reactive({ nombres: '', apellidos: '', email: '' });

watch(() => props.visible, (val) => {
  if (val) {
    if (props.persona) { form.nombres = props.persona.nombres || ''; form.apellidos = props.persona.apellidos || ''; form.email = props.persona.email || ''; form.telefono = props.persona.telefono || ''; form.activo = props.persona.activo !== false; }
    else { Object.assign(form, { nombres: '', apellidos: '', email: '', telefono: '', activo: true }); }
    Object.assign(errors, { nombres: '', apellidos: '', email: '' });
  }
});

function validate() {
  Object.assign(errors, { nombres: '', apellidos: '', email: '' });
  let valid = true;
  if (!form.nombres.trim()) { errors.nombres = 'Requerido'; valid = false; }
  if (!form.apellidos.trim()) { errors.apellidos = 'Requerido'; valid = false; }
  if (form.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) { errors.email = 'Email inválido'; valid = false; }
  return valid;
}

function handleSubmit() {
  if (!validate()) return;
  const payload = { nombres: form.nombres.trim(), apellidos: form.apellidos.trim(), email: form.email?.trim() || undefined, telefono: form.telefono?.trim() || undefined };
  if (isEditing.value) payload.activo = form.activo;
  emit('submit', payload);
}
</script>

<style scoped>
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.7); backdrop-filter: blur(8px); display: flex; align-items: center; justify-content: center; z-index: 1000; padding: var(--space-4); }
.modal-container { background: var(--bg-elevated); border: 1px solid var(--glass-border); border-radius: var(--radius-xl); width: 100%; max-width: 560px; max-height: 90vh; overflow-y: auto; box-shadow: 0 25px 60px rgba(0,0,0,0.5); }
.modal-header { display: flex; justify-content: space-between; align-items: center; padding: var(--space-6) var(--space-6) 0; }
.modal-title { font-family: var(--font-display); font-size: var(--text-xl); font-weight: 600; margin: 0; }
.modal-close { background: var(--glass-bg); border: 1px solid var(--glass-border); border-radius: var(--radius-sm); color: var(--text-secondary); padding: var(--space-2); cursor: pointer; transition: all 0.2s; display: flex; }
.modal-close:hover { color: var(--text-primary); background: rgba(255,255,255,0.08); }
.modal-body { padding: var(--space-6); display: flex; flex-direction: column; gap: var(--space-5); }
.modal-footer { display: flex; justify-content: flex-end; gap: var(--space-3); padding: 0 var(--space-6) var(--space-6); }
.form-row { display: flex; gap: var(--space-4); }
.form-group--half { flex: 1; }
.form-label { display: block; font-family: var(--font-body); font-size: var(--text-sm); font-weight: 500; color: var(--text-secondary); margin-bottom: var(--space-2); }
.toggle-group { display: flex; gap: var(--space-2); }
.toggle-btn { flex: 1; display: flex; align-items: center; justify-content: center; gap: var(--space-2); padding: var(--space-3); background: var(--bg-surface); border: 1px solid var(--glass-border); border-radius: var(--radius-md); color: var(--text-secondary); font-family: var(--font-body); font-size: var(--text-sm); font-weight: 500; cursor: pointer; transition: all 0.2s; }
.toggle-btn:hover { background: rgba(255,255,255,0.05); }
.toggle-btn.active { border-color: var(--primary); color: var(--primary); background: var(--primary-soft); }
.form-error-banner { display: flex; align-items: center; gap: var(--space-2); padding: var(--space-3) var(--space-4); background: rgba(244,63,94,0.1); border: 1px solid rgba(244,63,94,0.2); border-radius: var(--radius-md); color: var(--error); font-size: var(--text-sm); }
.modal-enter-active { transition: opacity 0.25s ease; } .modal-leave-active { transition: opacity 0.2s ease; } .modal-enter-from, .modal-leave-to { opacity: 0; }
</style>
