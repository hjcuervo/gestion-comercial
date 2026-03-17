<template>
  <Teleport to="body">
    <Transition name="modal">
      <div v-if="visible" class="modal-overlay" @click.self="$emit('close')">
        <div class="modal-container animate-scaleIn">
          <div class="modal-header">
            <h3 class="modal-title gradient-text">Cerrar Oportunidad</h3>
            <button class="modal-close" @click="$emit('close')"><Icon name="x" :size="20" /></button>
          </div>

          <div class="modal-body">
            <p class="modal-subtitle">¿Cómo se resolvió <strong>{{ oportunidadNombre }}</strong>?</p>

            <div class="form-group">
              <div class="estado-options">
                <button class="estado-btn estado-btn--ganada" :class="{ active: form.estadoMacro === 'GANADA' }" @click="form.estadoMacro = 'GANADA'">
                  <Icon name="trophy" :size="20" /> Ganada
                </button>
                <button class="estado-btn estado-btn--perdida" :class="{ active: form.estadoMacro === 'PERDIDA' }" @click="form.estadoMacro = 'PERDIDA'">
                  <Icon name="trending-down" :size="20" /> Perdida
                </button>
                <button class="estado-btn estado-btn--no-concretada" :class="{ active: form.estadoMacro === 'NO_CONCRETADA' }" @click="form.estadoMacro = 'NO_CONCRETADA'">
                  <Icon name="x" :size="20" /> No Concretada
                </button>
              </div>
              <span v-if="errors.estadoMacro" class="form-error">{{ errors.estadoMacro }}</span>
            </div>

            <div class="form-group">
              <label class="form-label">Comentario de Cierre</label>
              <textarea v-model="form.comentario" class="form-textarea" placeholder="Describe el resultado..." rows="3" maxlength="500"></textarea>
              <span class="form-hint">{{ (form.comentario || '').length }}/500</span>
            </div>

            <div v-if="error" class="form-error-banner"><Icon name="alert-circle" :size="16" /> {{ error }}</div>
          </div>

          <div class="modal-footer">
            <Button variant="ghost" @click="$emit('close')">Cancelar</Button>
            <Button :variant="form.estadoMacro === 'GANADA' ? 'success' : 'danger'" icon="check" :loading="saving" @click="handleSubmit">
              Confirmar Cierre
            </Button>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { reactive, watch } from 'vue';
import Icon from '@/components/ui/Icon.vue';
import Button from '@/components/ui/Button.vue';

const props = defineProps({
  visible: { type: Boolean, default: false },
  oportunidadNombre: { type: String, default: '' },
  saving: { type: Boolean, default: false },
  error: { type: String, default: null },
});

const emit = defineEmits(['close', 'submit']);
const form = reactive({ estadoMacro: '', comentario: '' });
const errors = reactive({ estadoMacro: '' });

watch(() => props.visible, (val) => {
  if (val) { form.estadoMacro = ''; form.comentario = ''; errors.estadoMacro = ''; }
});

function handleSubmit() {
  errors.estadoMacro = '';
  if (!form.estadoMacro) { errors.estadoMacro = 'Selecciona un resultado'; return; }
  emit('submit', {
    estadoMacro: form.estadoMacro,
    comentario: form.comentario?.trim() || undefined,
  });
}
</script>

<style scoped>
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.7); backdrop-filter: blur(8px); display: flex; align-items: center; justify-content: center; z-index: 1000; padding: var(--space-4); }
.modal-container { background: var(--bg-elevated); border: 1px solid var(--glass-border); border-radius: var(--radius-xl); width: 100%; max-width: 480px; box-shadow: 0 25px 60px rgba(0,0,0,0.5); }
.modal-header { display: flex; justify-content: space-between; align-items: center; padding: var(--space-6) var(--space-6) 0; }
.modal-title { font-family: var(--font-display); font-size: var(--text-xl); font-weight: 600; margin: 0; }
.modal-close { background: var(--glass-bg); border: 1px solid var(--glass-border); border-radius: var(--radius-sm); color: var(--text-secondary); padding: var(--space-2); cursor: pointer; transition: all 0.2s; display: flex; }
.modal-close:hover { color: var(--text-primary); background: rgba(255,255,255,0.08); }
.modal-body { padding: var(--space-6); display: flex; flex-direction: column; gap: var(--space-5); }
.modal-subtitle { font-size: var(--text-sm); color: var(--text-secondary); margin: 0; }
.modal-subtitle strong { color: var(--text-primary); }
.modal-footer { display: flex; justify-content: flex-end; gap: var(--space-3); padding: 0 var(--space-6) var(--space-6); }

.estado-options { display: flex; gap: var(--space-3); }
.estado-btn { flex: 1; display: flex; flex-direction: column; align-items: center; gap: var(--space-2); padding: var(--space-4); background: var(--bg-surface); border: 2px solid var(--glass-border); border-radius: var(--radius-lg); color: var(--text-secondary); font-family: var(--font-body); font-size: var(--text-xs); font-weight: 600; cursor: pointer; transition: all 0.2s; }
.estado-btn:hover { background: rgba(255,255,255,0.04); }
.estado-btn--ganada.active { border-color: var(--success); color: var(--success); background: var(--success-soft); }
.estado-btn--perdida.active { border-color: var(--error); color: var(--error); background: var(--error-soft); }
.estado-btn--no-concretada.active { border-color: var(--warning); color: var(--warning); background: var(--warning-soft); }

.form-label { display: block; font-family: var(--font-body); font-size: var(--text-sm); font-weight: 500; color: var(--text-secondary); margin-bottom: var(--space-2); }
.form-textarea { width: 100%; background: var(--bg-surface); border: 1px solid var(--glass-border); border-radius: var(--radius-md); color: var(--text-primary); font-family: var(--font-body); font-size: var(--text-sm); padding: var(--space-3) var(--space-4); resize: vertical; box-sizing: border-box; }
.form-textarea:focus { outline: none; border-color: var(--primary); box-shadow: 0 0 0 3px var(--primary-soft); }
.form-textarea::placeholder { color: var(--text-muted); }
.form-hint { font-size: var(--text-xs); color: var(--text-muted); text-align: right; display: block; margin-top: 2px; }
.form-error { font-size: var(--text-xs); color: var(--error); margin-top: 2px; display: block; text-align: center; }
.form-error-banner { display: flex; align-items: center; gap: var(--space-2); padding: var(--space-3) var(--space-4); background: rgba(244,63,94,0.1); border: 1px solid rgba(244,63,94,0.2); border-radius: var(--radius-md); color: var(--error); font-size: var(--text-sm); }
.modal-enter-active { transition: opacity 0.25s ease; } .modal-leave-active { transition: opacity 0.2s ease; } .modal-enter-from, .modal-leave-to { opacity: 0; }
</style>
