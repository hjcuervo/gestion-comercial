<template>
  <Teleport to="body">
    <div class="modal-overlay" @click.self="$emit('close')">
      <div class="modal glass animate-slideUp">
        <div class="modal__header">
          <h2 class="modal__title">Registrar Actividad</h2>
          <button class="modal__close" @click="$emit('close')"><Icon name="x" :size="18" /></button>
        </div>

        <form class="modal__body" @submit.prevent="handleSubmit">
          <!-- Tipo de actividad (from DB) -->
          <div class="field">
            <label class="field__label">Tipo de Actividad <span class="req">*</span></label>
            <div v-if="loadingTipos" class="loading-tipos">
              <Icon name="loader" :size="14" class="animate-spin" /> Cargando tipos...
            </div>
            <div v-else-if="tiposActividad.length" class="tipo-grid">
              <button
                v-for="tipo in tiposActividad"
                :key="tipo.id"
                type="button"
                :class="['tipo-btn', { 'tipo-btn--active': form.tipoActividadId === tipo.id }]"
                @click="form.tipoActividadId = tipo.id"
              >
                <Icon :name="tipo.icono || 'note-add'" :size="18" />
                <span>{{ tipo.nombre }}</span>
              </button>
            </div>
            <div v-else class="no-tipos">No hay tipos de actividad configurados</div>
          </div>

          <!-- Fecha y hora -->
          <div class="field-row">
            <div class="field">
              <label class="field__label">Fecha y Hora <span class="req">*</span></label>
              <input v-model="form.fechaHora" type="datetime-local" class="field__input" required />
            </div>
            <div class="field">
              <label class="field__label">Duración (min)</label>
              <input v-model.number="form.duracionMinutos" type="number" class="field__input" min="0" max="480" placeholder="30" />
            </div>
          </div>

          <!-- Notas -->
          <div class="field">
            <label class="field__label">Notas</label>
            <textarea v-model="form.notas" class="field__textarea" rows="4" maxlength="2000" placeholder="Descripción de la actividad, temas tratados, resultados..."></textarea>
            <span class="field__counter">{{ (form.notas || '').length }}/2000</span>
          </div>

          <!-- Error -->
          <div v-if="error" class="modal-error">
            <Icon name="alert-circle" :size="14" /> {{ error }}
          </div>

          <!-- Actions -->
          <div class="modal__actions">
            <button type="button" class="btn btn--ghost" @click="$emit('close')">Cancelar</button>
            <button type="submit" class="btn btn--primary" :disabled="!isValid || submitting">
              <Icon v-if="submitting" name="loader" :size="14" class="animate-spin" />
              Registrar Actividad
            </button>
          </div>
        </form>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import Icon from '@/components/ui/Icon.vue';
import { actividadService } from '@/services/actividad.service';

const props = defineProps({ oportunidadId: { type: Number, required: true } });
const emit = defineEmits(['close', 'created']);

const tiposActividad = ref([]);
const loadingTipos = ref(true);

const form = ref({
  tipoActividadId: null,
  fechaHora: '',
  duracionMinutos: null,
  notas: '',
});

const error = ref(null);
const submitting = ref(false);

const isValid = computed(() => form.value.tipoActividadId && form.value.fechaHora);

onMounted(async () => {
  try {
    tiposActividad.value = await actividadService.listarTipos();
    if (tiposActividad.value.length) {
      form.value.tipoActividadId = tiposActividad.value[0].id;
    }
  } catch (err) {
    console.error('Error cargando tipos de actividad:', err);
  } finally {
    loadingTipos.value = false;
  }
});

async function handleSubmit() {
  if (!isValid.value) return;
  error.value = null;
  submitting.value = true;
  try {
    const payload = {
      oportunidadId: props.oportunidadId,
      tipoActividadId: form.value.tipoActividadId,
      fechaHora: form.value.fechaHora + ':00',
      duracionMinutos: form.value.duracionMinutos || null,
      notas: form.value.notas || null,
    };
    const created = await actividadService.crear(payload);
    emit('created', created);
  } catch (err) {
    error.value = err.response?.data?.message || 'Error al registrar actividad';
  } finally {
    submitting.value = false;
  }
}
</script>

<style scoped>
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.6); backdrop-filter: blur(4px); display: flex; align-items: center; justify-content: center; z-index: var(--z-modal); padding: var(--space-4); }
.modal { width: 100%; max-width: 540px; border-radius: var(--radius-xl); padding: var(--space-6); max-height: 90vh; overflow-y: auto; }
.modal__header { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--space-5); }
.modal__title { font-family: var(--font-display); font-size: var(--text-xl); font-weight: 700; color: var(--text-primary); margin: 0; }
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

.field-row { display: grid; grid-template-columns: 1fr 1fr; gap: var(--space-4); }

/* Tipo grid */
.loading-tipos { display: flex; align-items: center; gap: var(--space-2); color: var(--text-muted); font-size: var(--text-xs); padding: var(--space-3); }
.no-tipos { color: var(--text-muted); font-size: var(--text-xs); padding: var(--space-3); }
.tipo-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: var(--space-2); }
.tipo-btn { display: flex; flex-direction: column; align-items: center; gap: 4px; padding: var(--space-3) var(--space-2); border-radius: var(--radius-md); background: var(--bg-surface); border: 1px solid var(--glass-border); color: var(--text-muted); font-family: var(--font-body); font-size: 10px; font-weight: 500; cursor: pointer; transition: all 0.15s; }
.tipo-btn:hover { border-color: var(--text-muted); color: var(--text-secondary); }
.tipo-btn--active { border-color: var(--primary); color: var(--primary); background: var(--primary-soft); }

.modal-error { display: flex; align-items: center; gap: var(--space-2); padding: var(--space-3); background: var(--error-soft); border-radius: var(--radius-md); color: var(--error); font-size: var(--text-xs); }

.modal__actions { display: flex; justify-content: flex-end; gap: var(--space-3); padding-top: var(--space-3); border-top: 1px solid var(--glass-border); }
.btn { display: flex; align-items: center; gap: var(--space-2); padding: var(--space-2) var(--space-5); border-radius: var(--radius-full); font-family: var(--font-body); font-size: var(--text-xs); font-weight: 600; cursor: pointer; transition: all 0.15s; border: 1px solid transparent; }
.btn--ghost { background: transparent; color: var(--text-secondary); border-color: var(--glass-border); }
.btn--ghost:hover { background: var(--glass-hover); }
.btn--primary { background: var(--primary); color: #000; }
.btn--primary:hover { box-shadow: 0 0 20px var(--primary-glow); }
.btn--primary:disabled { opacity: 0.5; cursor: not-allowed; }
</style>
