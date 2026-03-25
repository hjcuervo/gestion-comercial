<template>
  <Teleport to="body">
    <Transition name="modal">
      <div v-if="visible" class="modal-overlay" @click.self="$emit('close')">
        <div class="modal-container animate-scaleIn">
          <div class="modal-header">
            <h3 class="modal-title gradient-text">
              {{ isEditing ? 'Editar Pipeline' : 'Nuevo Pipeline' }}
            </h3>
            <button class="modal-close" @click="$emit('close')">
              <Icon name="x" :size="20" />
            </button>
          </div>

          <div class="modal-body">
            <div class="form-group">
              <Input
                v-model="form.nombre"
                label="Nombre del Pipeline"
                placeholder="Ej: Pipeline de Ventas B2B"
                icon="pipeline"
                :error="errors.nombre"
                required
              />
            </div>

            <!-- Ámbito (solo en creación) -->
            <div v-if="!isEditing" class="form-group">
              <label class="form-label">Ámbito <span class="required">*</span></label>
              <div class="toggle-group">
                <button
                  class="toggle-btn"
                  :class="{ active: form.ambito === 'COMERCIAL' }"
                  @click="form.ambito = 'COMERCIAL'"
                  type="button"
                >
                  <Icon name="handshake" :size="16" />
                  Comercial
                </button>
                <button
                  class="toggle-btn"
                  :class="{ active: form.ambito === 'CONTRATACION' }"
                  @click="form.ambito = 'CONTRATACION'"
                  type="button"
                >
                  <Icon name="note-add" :size="16" />
                  Contratación
                </button>
              </div>
              <span class="form-hint">{{ form.ambito === 'COMERCIAL' ? 'Para gestión de oportunidades comerciales' : 'Para formalización de contratos después de adjudicación' }}</span>
            </div>

            <!-- Ámbito (solo lectura en edición) -->
            <div v-if="isEditing" class="form-group">
              <label class="form-label">Ámbito</label>
              <span class="ambito-display">{{ form.ambito === 'CONTRATACION' ? 'Contratación' : 'Comercial' }}</span>
            </div>

            <div class="form-group">
              <label class="form-label">Descripción</label>
              <textarea
                v-model="form.descripcion"
                class="form-textarea"
                placeholder="Describe el propósito de este pipeline..."
                rows="3"
                maxlength="500"
              ></textarea>
              <span class="form-hint">{{ (form.descripcion || '').length }}/500</span>
            </div>

            <div v-if="isEditing" class="form-group">
              <label class="form-label">Estado</label>
              <div class="toggle-group">
                <button class="toggle-btn" :class="{ active: form.activo }" @click="form.activo = true" type="button">
                  <Icon name="check-circle" :size="16" /> Activo
                </button>
                <button class="toggle-btn" :class="{ active: !form.activo }" @click="form.activo = false" type="button">
                  <Icon name="x" :size="16" /> Inactivo
                </button>
              </div>
            </div>

            <div v-if="error" class="form-error-banner">
              <Icon name="alert-circle" :size="16" /> {{ error }}
            </div>
          </div>

          <div class="modal-footer">
            <Button variant="ghost" @click="$emit('close')">Cancelar</Button>
            <Button variant="primary" icon="check" :loading="saving" @click="handleSubmit">
              {{ isEditing ? 'Guardar Cambios' : 'Crear Pipeline' }}
            </Button>
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
  if (val) {
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
  }
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
    descripcion: form.descripcion?.trim() || undefined,
  };
  if (!isEditing.value) payload.ambito = form.ambito;
  if (isEditing.value) payload.activo = form.activo;
  emit('submit', payload);
}
</script>

<style scoped>
.modal-overlay { position: fixed; inset: 0; background: rgba(0, 0, 0, 0.7); backdrop-filter: blur(8px); display: flex; align-items: center; justify-content: center; z-index: 1000; padding: var(--space-4); }
.modal-container { background: var(--bg-elevated); border: 1px solid var(--glass-border); border-radius: var(--radius-xl); width: 100%; max-width: 520px; max-height: 90vh; overflow-y: auto; box-shadow: 0 25px 60px rgba(0, 0, 0, 0.5); }
.modal-header { display: flex; justify-content: space-between; align-items: center; padding: var(--space-6) var(--space-6) 0; }
.modal-title { font-family: var(--font-display); font-size: var(--text-xl); font-weight: 600; margin: 0; }
.modal-close { background: var(--glass-bg); border: 1px solid var(--glass-border); border-radius: var(--radius-sm); color: var(--text-secondary); padding: var(--space-2); cursor: pointer; transition: all 0.2s; display: flex; align-items: center; }
.modal-close:hover { color: var(--text-primary); background: rgba(255, 255, 255, 0.08); }
.modal-body { padding: var(--space-6); display: flex; flex-direction: column; gap: var(--space-5); }
.modal-footer { display: flex; justify-content: flex-end; gap: var(--space-3); padding: 0 var(--space-6) var(--space-6); }

.form-label { display: block; font-family: var(--font-body); font-size: var(--text-sm); font-weight: 500; color: var(--text-secondary); margin-bottom: var(--space-2); }
.required { color: var(--error); }
.form-textarea { width: 100%; background: var(--bg-surface); border: 1px solid var(--glass-border); border-radius: var(--radius-md); color: var(--text-primary); font-family: var(--font-body); font-size: var(--text-base); padding: var(--space-3) var(--space-4); resize: vertical; transition: border-color 0.2s; box-sizing: border-box; }
.form-textarea:focus { outline: none; border-color: var(--primary); box-shadow: 0 0 0 3px var(--primary-soft); }
.form-textarea::placeholder { color: var(--text-muted); }
.form-hint { font-size: var(--text-xs); color: var(--text-muted); margin-top: var(--space-1); display: block; }

.toggle-group { display: flex; gap: var(--space-2); }
.toggle-btn { flex: 1; display: flex; align-items: center; justify-content: center; gap: var(--space-2); padding: var(--space-3) var(--space-4); background: var(--bg-surface); border: 1px solid var(--glass-border); border-radius: var(--radius-md); color: var(--text-secondary); font-family: var(--font-body); font-size: var(--text-sm); font-weight: 500; cursor: pointer; transition: all 0.2s; }
.toggle-btn:hover { background: rgba(255, 255, 255, 0.05); }
.toggle-btn.active { border-color: var(--primary); color: var(--primary); background: var(--primary-soft); }

.ambito-display { font-size: var(--text-sm); color: var(--text-primary); font-weight: 500; padding: var(--space-2) var(--space-4); background: var(--bg-surface); border-radius: var(--radius-md); display: inline-block; }

.form-error-banner { display: flex; align-items: center; gap: var(--space-2); padding: var(--space-3) var(--space-4); background: rgba(244, 63, 94, 0.1); border: 1px solid rgba(244, 63, 94, 0.2); border-radius: var(--radius-md); color: var(--error); font-size: var(--text-sm); }

.modal-enter-active { transition: opacity 0.25s ease; }
.modal-leave-active { transition: opacity 0.2s ease; }
.modal-enter-from, .modal-leave-to { opacity: 0; }
</style>
