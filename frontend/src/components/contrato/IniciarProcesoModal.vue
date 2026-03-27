<template>
  <Teleport to="body">
    <div class="modal-overlay" @click.self="$emit('close')">
      <div class="modal glass animate-slideUp">
        <div class="modal__header">
          <h2 class="modal__title">Iniciar Proceso de Contratación</h2>
          <button class="modal__close" @click="$emit('close')"><Icon name="x" :size="18" /></button>
        </div>

        <form class="modal__body" @submit.prevent="handleSubmit">
          <p class="modal__desc">Se iniciará el proceso de contratación para la oportunidad <strong>{{ oportunidadNombre }}</strong>. Seleccione el pipeline de contratación a seguir.</p>

          <div class="field">
            <label class="field__label">Pipeline de Contratación <span class="req">*</span></label>
            <div v-if="loadingPipelines" class="loading-inline"><Icon name="loader" :size="14" class="animate-spin" /> Cargando pipelines...</div>
            <select v-else v-model="form.pipelineId" class="field__select" required>
              <option :value="null" disabled>Seleccione un pipeline</option>
              <option v-for="p in pipelines" :key="p.id" :value="p.id">{{ p.nombre }}</option>
            </select>
            <span v-if="!pipelines.length && !loadingPipelines" class="field__hint field__hint--warn">No hay pipelines de contratación configurados. Créelos en Pipeline → Configuración con ámbito "Contratación".</span>
          </div>

          <div class="field">
            <label class="field__label">Responsable</label>
            <input v-model="form.responsable" type="text" class="field__input" maxlength="100" placeholder="Responsable del proceso" />
          </div>

          <div class="field">
            <label class="field__label">Observaciones</label>
            <textarea v-model="form.observaciones" class="field__textarea" rows="2" maxlength="2000" placeholder="Observaciones iniciales (opcional)"></textarea>
          </div>

          <div v-if="error" class="modal-error"><Icon name="alert-circle" :size="14" /> {{ error }}</div>

          <div class="modal__actions">
            <button type="button" class="btn btn--ghost" @click="$emit('close')">Cancelar</button>
            <button type="submit" class="btn btn--primary" :disabled="!form.pipelineId || submitting">
              <Icon v-if="submitting" name="loader" :size="14" class="animate-spin" />
              Iniciar Proceso
            </button>
          </div>
        </form>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import Icon from '@/components/ui/Icon.vue';
import { pipelineService } from '@/services/pipeline.service';
import { procesoContratacionService } from '@/services/procesoContratacion.service';

const props = defineProps({
  oportunidadId: { type: Number, required: true },
  oportunidadNombre: { type: String, default: '' },
});
const emit = defineEmits(['close', 'created']);

const pipelines = ref([]);
const loadingPipelines = ref(true);
const form = ref({ pipelineId: null, responsable: '', observaciones: '' });
const error = ref(null);
const submitting = ref(false);

onMounted(async () => {
  try {
    const all = await pipelineService.listarActivos('CONTRATACION');
    pipelines.value = all;
    if (all.length === 1) form.value.pipelineId = all[0].id;
  } catch (err) { console.error('Error cargando pipelines:', err); }
  finally { loadingPipelines.value = false; }
});

async function handleSubmit() {
  if (!form.value.pipelineId) return;
  error.value = null;
  submitting.value = true;
  try {
    const created = await procesoContratacionService.iniciar({
      oportunidadId: props.oportunidadId,
      pipelineId: form.value.pipelineId,
      responsable: form.value.responsable?.trim() || null,
      observaciones: form.value.observaciones?.trim() || null,
    });
    emit('created', created);
  } catch (err) {
    error.value = err.response?.data?.message || 'Error al iniciar proceso';
  } finally { submitting.value = false; }
}
</script>

<style scoped>
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.6); backdrop-filter: blur(4px); display: flex; align-items: center; justify-content: center; z-index: var(--z-modal); padding: var(--space-4); }
.modal { width: 100%; max-width: 500px; border-radius: var(--radius-xl); padding: var(--space-6); max-height: 90vh; overflow-y: auto; }
.modal__header { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--space-4); }
.modal__title { font-family: var(--font-display); font-size: var(--text-xl); font-weight: 700; color: var(--text-primary); margin: 0; }
.modal__close { background: none; border: none; color: var(--text-muted); cursor: pointer; padding: 4px; border-radius: var(--radius-sm); }
.modal__close:hover { color: var(--text-primary); background: var(--glass-hover); }
.modal__body { display: flex; flex-direction: column; gap: var(--space-4); }
.modal__desc { font-size: var(--text-xs); color: var(--text-secondary); line-height: 1.6; margin: 0; }
.modal__desc strong { color: var(--text-primary); }
.field { display: flex; flex-direction: column; gap: var(--space-1); }
.field__label { font-size: var(--text-xs); font-weight: 600; color: var(--text-secondary); }
.req { color: var(--error); }
.field__input, .field__select { background: var(--bg-surface); border: 1px solid var(--glass-border); border-radius: var(--radius-md); color: var(--text-primary); font-family: var(--font-body); font-size: var(--text-xs); padding: var(--space-2) var(--space-3); }
.field__select { appearance: none; background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 24 24' fill='none' stroke='rgba(255,255,255,0.3)' stroke-width='2'%3E%3Cpath d='M6 9l6 6 6-6'/%3E%3C/svg%3E"); background-repeat: no-repeat; background-position: right 10px center; padding-right: 30px; }
.field__select option { background: var(--bg-elevated); }
.field__input:focus, .field__select:focus { outline: none; border-color: var(--primary); }
.field__textarea { background: var(--bg-surface); border: 1px solid var(--glass-border); border-radius: var(--radius-md); color: var(--text-primary); font-family: var(--font-body); font-size: var(--text-xs); padding: var(--space-3); resize: vertical; }
.field__textarea:focus { outline: none; border-color: var(--primary); }
.field__hint { font-size: 10px; color: var(--text-muted); }
.field__hint--warn { color: var(--warning); }
.loading-inline { display: flex; align-items: center; gap: var(--space-2); color: var(--text-muted); font-size: var(--text-xs); padding: var(--space-2); }
.modal-error { display: flex; align-items: center; gap: var(--space-2); padding: var(--space-3); background: var(--error-soft); border-radius: var(--radius-md); color: var(--error); font-size: var(--text-xs); }
.modal__actions { display: flex; justify-content: flex-end; gap: var(--space-3); padding-top: var(--space-3); border-top: 1px solid var(--glass-border); }
.btn { display: flex; align-items: center; gap: var(--space-2); padding: var(--space-2) var(--space-5); border-radius: var(--radius-full); font-family: var(--font-body); font-size: var(--text-xs); font-weight: 600; cursor: pointer; transition: all 0.15s; border: 1px solid transparent; }
.btn--ghost { background: transparent; color: var(--text-secondary); border-color: var(--glass-border); }
.btn--ghost:hover { background: var(--glass-hover); }
.btn--primary { background: var(--primary); color: #000; }
.btn--primary:hover { box-shadow: 0 0 20px var(--primary-glow); }
.btn--primary:disabled { opacity: 0.5; cursor: not-allowed; }
</style>
