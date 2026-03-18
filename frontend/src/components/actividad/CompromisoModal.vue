<template>
  <Teleport to="body">
    <div class="modal-overlay" @click.self="$emit('close')">
      <div class="modal glass animate-slideUp">
        <div class="modal__header">
          <h2 class="modal__title">Nuevo Compromiso</h2>
          <button class="modal__close" @click="$emit('close')"><Icon name="x" :size="18" /></button>
        </div>

        <form class="modal__body" @submit.prevent="handleSubmit">
          <div class="field">
            <label class="field__label">Descripción <span class="req">*</span></label>
            <textarea v-model="form.descripcion" class="field__textarea" rows="3" maxlength="500" required placeholder="¿Qué se comprometió a hacer?"></textarea>
            <span class="field__counter">{{ (form.descripcion || '').length }}/500</span>
          </div>

          <div class="field">
            <label class="field__label">Fecha Compromiso <span class="req">*</span></label>
            <input v-model="form.fechaCompromiso" type="date" class="field__input" required />
          </div>

          <div v-if="error" class="modal-error">
            <Icon name="alert-circle" :size="14" /> {{ error }}
          </div>

          <div class="modal__actions">
            <button type="button" class="btn btn--ghost" @click="$emit('close')">Cancelar</button>
            <button type="submit" class="btn btn--primary" :disabled="!isValid || submitting">
              <Icon v-if="submitting" name="loader" :size="14" class="animate-spin" />
              Crear Compromiso
            </button>
          </div>
        </form>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { ref, computed } from 'vue';
import Icon from '@/components/ui/Icon.vue';

const props = defineProps({ actividadId: { type: Number, required: true } });
const emit = defineEmits(['close', 'created']);

const form = ref({ descripcion: '', fechaCompromiso: '' });
const error = ref(null);
const submitting = ref(false);

const isValid = computed(() => form.value.descripcion.trim() && form.value.fechaCompromiso);

async function handleSubmit() {
  if (!isValid.value) return;
  error.value = null;
  submitting.value = true;
  try {
    const { actividadService } = await import('@/services/actividad.service');
    const created = await actividadService.crearCompromiso(props.actividadId, {
      descripcion: form.value.descripcion.trim(),
      fechaCompromiso: form.value.fechaCompromiso,
    });
    emit('created', created);
  } catch (err) {
    error.value = err.response?.data?.message || 'Error al crear compromiso';
  } finally {
    submitting.value = false;
  }
}
</script>

<style scoped>
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.6); backdrop-filter: blur(4px); display: flex; align-items: center; justify-content: center; z-index: var(--z-modal); padding: var(--space-4); }
.modal { width: 100%; max-width: 440px; border-radius: var(--radius-xl); padding: var(--space-6); }
.modal__header { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--space-5); }
.modal__title { font-family: var(--font-display); font-size: var(--text-lg); font-weight: 700; color: var(--text-primary); margin: 0; }
.modal__close { background: none; border: none; color: var(--text-muted); cursor: pointer; padding: 4px; border-radius: var(--radius-sm); transition: all 0.15s; }
.modal__close:hover { color: var(--text-primary); background: var(--glass-hover); }
.modal__body { display: flex; flex-direction: column; gap: var(--space-4); }
.field { display: flex; flex-direction: column; gap: var(--space-1); }
.field__label { font-size: var(--text-xs); font-weight: 600; color: var(--text-secondary); }
.req { color: var(--error); }
.field__input { background: var(--bg-surface); border: 1px solid var(--glass-border); border-radius: var(--radius-md); color: var(--text-primary); font-family: var(--font-body); font-size: var(--text-xs); padding: var(--space-2) var(--space-3); }
.field__input:focus { outline: none; border-color: var(--primary); }
.field__textarea { background: var(--bg-surface); border: 1px solid var(--glass-border); border-radius: var(--radius-md); color: var(--text-primary); font-family: var(--font-body); font-size: var(--text-xs); padding: var(--space-3); resize: vertical; }
.field__textarea:focus { outline: none; border-color: var(--primary); }
.field__counter { font-size: 10px; color: var(--text-muted); text-align: right; }
.modal-error { display: flex; align-items: center; gap: var(--space-2); padding: var(--space-3); background: var(--error-soft); border-radius: var(--radius-md); color: var(--error); font-size: var(--text-xs); }
.modal__actions { display: flex; justify-content: flex-end; gap: var(--space-3); padding-top: var(--space-3); border-top: 1px solid var(--glass-border); }
.btn { display: flex; align-items: center; gap: var(--space-2); padding: var(--space-2) var(--space-5); border-radius: var(--radius-full); font-family: var(--font-body); font-size: var(--text-xs); font-weight: 600; cursor: pointer; transition: all 0.15s; border: 1px solid transparent; }
.btn--ghost { background: transparent; color: var(--text-secondary); border-color: var(--glass-border); }
.btn--ghost:hover { background: var(--glass-hover); }
.btn--primary { background: var(--primary); color: #000; }
.btn--primary:hover { box-shadow: 0 0 20px var(--primary-glow); }
.btn--primary:disabled { opacity: 0.5; cursor: not-allowed; }
</style>
