<template>
  <Teleport to="body">
    <Transition name="modal">
      <div v-if="visible" class="modal-overlay" @click.self="$emit('close')">
        <div class="modal-container animate-scaleIn">
          <div class="modal-header">
            <h3 class="modal-title gradient-text">
              {{ isEditing ? 'Editar Etapa' : 'Nueva Etapa' }}
            </h3>
            <button class="modal-close" @click="$emit('close')">
              <Icon name="x" :size="20" />
            </button>
          </div>

          <div class="modal-body">
            <div class="form-group">
              <Input
                v-model="form.nombre"
                label="Nombre de la Etapa"
                placeholder="Ej: Prospección, Negociación, Cierre"
                icon="chart"
                :error="errors.nombre"
                required
              />
            </div>

            <div class="form-row">
              <div class="form-group form-group--half">
                <label class="form-label">Orden</label>
                <input
                  v-model.number="form.orden"
                  type="number"
                  class="form-input"
                  placeholder="1"
                  min="1"
                />
              </div>
              <div class="form-group form-group--half">
                <label class="form-label">Probabilidad (%)</label>
                <input
                  v-model.number="form.probabilidadSugerida"
                  type="number"
                  class="form-input"
                  placeholder="50"
                  min="0"
                  max="100"
                />
                <div class="probability-bar">
                  <div
                    class="probability-fill"
                    :style="{ width: (form.probabilidadSugerida || 0) + '%' }"
                  ></div>
                </div>
              </div>
            </div>

            <div class="form-row">
              <div class="form-group form-group--half">
                <label class="form-label">Color</label>
                <div class="color-picker-wrapper">
                  <input v-model="form.color" type="color" class="color-input-native" />
                  <div class="color-preview" :style="{ background: form.color || '#00d4ff' }"></div>
                  <input
                    v-model="form.color"
                    type="text"
                    class="form-input color-text"
                    placeholder="#00d4ff"
                    maxlength="7"
                  />
                </div>
              </div>
              <div class="form-group form-group--half">
                <label class="form-label">Modo Bloqueo</label>
                <div class="toggle-group">
                  <button class="toggle-btn" :class="{ active: form.modoBloqueo === 0 }" @click="form.modoBloqueo = 0">
                    <Icon name="check-circle" :size="14" /> Abierto
                  </button>
                  <button class="toggle-btn" :class="{ active: form.modoBloqueo === 1 }" @click="form.modoBloqueo = 1">
                    <Icon name="lock" :size="14" /> Bloqueado
                  </button>
                </div>
              </div>
            </div>

            <div v-if="isEditing" class="form-group">
              <label class="form-label">Estado</label>
              <div class="toggle-group">
                <button class="toggle-btn" :class="{ active: form.estado === 'ACTIVA' }" @click="form.estado = 'ACTIVA'">
                  <Icon name="check-circle" :size="14" /> Activa
                </button>
                <button class="toggle-btn" :class="{ active: form.estado === 'INACTIVA' }" @click="form.estado = 'INACTIVA'">
                  <Icon name="x" :size="14" /> Inactiva
                </button>
              </div>
            </div>

            <div v-if="error" class="form-error-banner">
              <Icon name="alert-circle" :size="16" />
              {{ error }}
            </div>
          </div>

          <div class="modal-footer">
            <Button variant="ghost" @click="$emit('close')">Cancelar</Button>
            <Button variant="primary" icon="check" :loading="saving" @click="handleSubmit">
              {{ isEditing ? 'Guardar Cambios' : 'Crear Etapa' }}
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
  etapa: { type: Object, default: null },
  nextOrden: { type: Number, default: 1 },
  saving: { type: Boolean, default: false },
  error: { type: String, default: null },
});

const emit = defineEmits(['close', 'submit']);
const isEditing = computed(() => !!props.etapa);

const form = reactive({
  nombre: '',
  orden: 1,
  probabilidadSugerida: null,
  color: '#00d4ff',
  modoBloqueo: 0,
  estado: 'ACTIVA',
});

const errors = reactive({ nombre: '' });

watch(() => props.visible, (val) => {
  if (val) {
    if (props.etapa) {
      form.nombre = props.etapa.nombre || '';
      form.orden = props.etapa.orden || 1;
      form.probabilidadSugerida = props.etapa.probabilidadSugerida;
      form.color = props.etapa.color || '#00d4ff';
      form.modoBloqueo = props.etapa.modoBloqueo || 0;
      form.estado = props.etapa.estado || 'ACTIVA';
    } else {
      form.nombre = '';
      form.orden = props.nextOrden;
      form.probabilidadSugerida = null;
      form.color = '#00d4ff';
      form.modoBloqueo = 0;
      form.estado = 'ACTIVA';
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
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: var(--space-4);
}

.modal-container {
  background: var(--bg-elevated);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-xl);
  width: 100%;
  max-width: 560px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 25px 60px rgba(0, 0, 0, 0.5);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-6) var(--space-6) 0;
}

.modal-title {
  font-family: var(--font-display);
  font-size: var(--text-xl);
  font-weight: 600;
  margin: 0;
}

.modal-close {
  background: var(--glass-bg);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-sm);
  color: var(--text-secondary);
  padding: var(--space-2);
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
}

.modal-close:hover {
  color: var(--text-primary);
  background: rgba(255, 255, 255, 0.08);
}

.modal-body {
  padding: var(--space-6);
  display: flex;
  flex-direction: column;
  gap: var(--space-5);
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: var(--space-3);
  padding: 0 var(--space-6) var(--space-6);
}

.form-row { display: flex; gap: var(--space-4); }
.form-group--half { flex: 1; }

.form-label {
  display: block;
  font-family: var(--font-body);
  font-size: var(--text-sm);
  font-weight: 500;
  color: var(--text-secondary);
  margin-bottom: var(--space-2);
}

.form-input {
  width: 100%;
  background: var(--bg-surface);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-md);
  color: var(--text-primary);
  font-family: var(--font-body);
  font-size: var(--text-base);
  padding: var(--space-3) var(--space-4);
  transition: border-color 0.2s;
  box-sizing: border-box;
}

.form-input:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 3px var(--primary-soft);
}

.form-input::placeholder { color: var(--text-muted); }

.probability-bar {
  height: 4px;
  background: var(--bg-surface);
  border-radius: 2px;
  margin-top: var(--space-2);
  overflow: hidden;
}

.probability-fill {
  height: 100%;
  background: var(--gradient-primary);
  border-radius: 2px;
  transition: width 0.3s ease;
}

.color-picker-wrapper {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  position: relative;
}

.color-input-native {
  position: absolute;
  width: 36px;
  height: 36px;
  opacity: 0;
  cursor: pointer;
  z-index: 2;
}

.color-preview {
  width: 36px;
  height: 36px;
  min-width: 36px;
  border-radius: var(--radius-sm);
  border: 2px solid var(--glass-border);
  cursor: pointer;
  transition: border-color 0.2s;
}

.color-text {
  flex: 1;
  font-family: var(--font-mono);
  font-size: var(--text-sm);
}

.toggle-group { display: flex; gap: var(--space-2); }

.toggle-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-3);
  background: var(--bg-surface);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-md);
  color: var(--text-secondary);
  font-family: var(--font-body);
  font-size: var(--text-sm);
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.toggle-btn:hover { background: rgba(255, 255, 255, 0.05); }

.toggle-btn.active {
  border-color: var(--primary);
  color: var(--primary);
  background: var(--primary-soft);
}

.form-error-banner {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-3) var(--space-4);
  background: rgba(244, 63, 94, 0.1);
  border: 1px solid rgba(244, 63, 94, 0.2);
  border-radius: var(--radius-md);
  color: var(--error);
  font-size: var(--text-sm);
}

.modal-enter-active { transition: opacity 0.25s ease; }
.modal-leave-active { transition: opacity 0.2s ease; }
.modal-enter-from, .modal-leave-to { opacity: 0; }
</style>
