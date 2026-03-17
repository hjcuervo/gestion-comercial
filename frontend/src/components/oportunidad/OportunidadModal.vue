<template>
  <Teleport to="body">
    <Transition name="modal">
      <div v-if="visible" class="modal-overlay" @click.self="$emit('close')">
        <div class="modal-container animate-scaleIn">
          <div class="modal-header">
            <h3 class="modal-title gradient-text">{{ isEditing ? 'Editar Oportunidad' : 'Nueva Oportunidad' }}</h3>
            <button class="modal-close" @click="$emit('close')"><Icon name="x" :size="20" /></button>
          </div>

          <div class="modal-body">
            <div class="form-group">
              <Input v-model="form.nombre" label="Nombre de la Oportunidad" placeholder="Ej: Implementación ERP Acme" icon="handshake" :error="errors.nombre" required />
            </div>

            <!-- Empresa -->
            <div class="form-group">
              <label class="form-label">Empresa <span class="required">*</span></label>
              <select v-model="form.empresaId" class="form-select" :disabled="isEditing">
                <option :value="null">Seleccionar empresa...</option>
                <option v-for="e in empresas" :key="e.id" :value="e.id">{{ e.razonSocial }}</option>
              </select>
              <span v-if="errors.empresaId" class="form-error">{{ errors.empresaId }}</span>
            </div>

            <!-- Pipeline + Etapa (solo en creación) -->
            <div v-if="!isEditing" class="form-row">
              <div class="form-group form-group--half">
                <label class="form-label">Pipeline <span class="required">*</span></label>
                <select v-model="form.pipelineId" class="form-select" @change="onPipelineChange">
                  <option :value="null">Seleccionar...</option>
                  <option v-for="p in pipelines" :key="p.id" :value="p.id">{{ p.nombre }}</option>
                </select>
                <span v-if="errors.pipelineId" class="form-error">{{ errors.pipelineId }}</span>
              </div>
              <div class="form-group form-group--half">
                <label class="form-label">Etapa Inicial</label>
                <select v-model="form.etapaId" class="form-select" :disabled="!etapasDisponibles.length">
                  <option :value="null">Primera etapa (auto)</option>
                  <option v-for="et in etapasDisponibles" :key="et.id" :value="et.id">{{ et.nombre }}</option>
                </select>
              </div>
            </div>

            <!-- Valor + Moneda -->
            <div class="form-row">
              <div class="form-group form-group--grow">
                <label class="form-label">Valor Estimado</label>
                <input v-model.number="form.valorEstimado" type="number" class="form-input" placeholder="0" min="0" step="1000" />
              </div>
              <div class="form-group form-group--shrink">
                <label class="form-label">Moneda</label>
                <select v-model="form.moneda" class="form-select">
                  <option value="COP">COP</option>
                  <option value="USD">USD</option>
                  <option value="EUR">EUR</option>
                </select>
              </div>
              <div class="form-group form-group--shrink">
                <label class="form-label">Probabilidad</label>
                <input v-model.number="form.probabilidad" type="number" class="form-input" placeholder="%" min="0" max="100" />
              </div>
            </div>

            <!-- Fecha cierre + Fuente -->
            <div class="form-row">
              <div class="form-group form-group--half">
                <label class="form-label">Fecha Estimada Cierre</label>
                <input v-model="form.fechaEstimadaCierre" type="date" class="form-input" />
              </div>
              <div class="form-group form-group--half">
                <Input v-model="form.fuente" label="Fuente" placeholder="Ej: Referido, Web, Evento" />
              </div>
            </div>

            <div class="form-group">
              <Input v-model="form.tipoServicio" label="Tipo de Servicio" placeholder="Ej: Consultoría, Implementación, Soporte" />
            </div>

            <div v-if="error" class="form-error-banner"><Icon name="alert-circle" :size="16" /> {{ error }}</div>
          </div>

          <div class="modal-footer">
            <Button variant="ghost" @click="$emit('close')">Cancelar</Button>
            <Button variant="primary" icon="check" :loading="saving" @click="handleSubmit">
              {{ isEditing ? 'Guardar Cambios' : 'Crear Oportunidad' }}
            </Button>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { reactive, ref, watch, computed } from 'vue';
import Icon from '@/components/ui/Icon.vue';
import Input from '@/components/ui/Input.vue';
import Button from '@/components/ui/Button.vue';
import { pipelineService } from '@/services/pipeline.service';

const props = defineProps({
  visible: { type: Boolean, default: false },
  oportunidad: { type: Object, default: null },
  empresas: { type: Array, default: () => [] },
  pipelines: { type: Array, default: () => [] },
  pipelinePreseleccionado: { type: Number, default: null },
  saving: { type: Boolean, default: false },
  error: { type: String, default: null },
});

const emit = defineEmits(['close', 'submit']);
const isEditing = computed(() => !!props.oportunidad);
const etapasDisponibles = ref([]);

const form = reactive({
  nombre: '', empresaId: null, pipelineId: null, etapaId: null,
  valorEstimado: null, moneda: 'COP', probabilidad: null,
  fechaEstimadaCierre: '', fuente: '', tipoServicio: '',
});
const errors = reactive({ nombre: '', empresaId: '', pipelineId: '' });

watch(() => props.visible, async (val) => {
  if (val) {
    etapasDisponibles.value = [];
    if (props.oportunidad) {
      form.nombre = props.oportunidad.nombre || '';
      form.empresaId = props.oportunidad.empresaId;
      form.pipelineId = props.oportunidad.pipelineId;
      form.etapaId = props.oportunidad.etapaId;
      form.valorEstimado = props.oportunidad.valorEstimado;
      form.moneda = props.oportunidad.moneda || 'COP';
      form.probabilidad = props.oportunidad.probabilidad;
      form.fechaEstimadaCierre = props.oportunidad.fechaEstimadaCierre || '';
      form.fuente = props.oportunidad.fuente || '';
      form.tipoServicio = props.oportunidad.tipoServicio || '';
    } else {
      Object.assign(form, { nombre: '', empresaId: null, pipelineId: props.pipelinePreseleccionado, etapaId: null, valorEstimado: null, moneda: 'COP', probabilidad: null, fechaEstimadaCierre: '', fuente: '', tipoServicio: '' });
      if (form.pipelineId) await loadEtapas(form.pipelineId);
    }
    Object.assign(errors, { nombre: '', empresaId: '', pipelineId: '' });
  }
});

async function onPipelineChange() {
  form.etapaId = null;
  if (form.pipelineId) await loadEtapas(form.pipelineId);
  else etapasDisponibles.value = [];
}

async function loadEtapas(pipelineId) {
  try {
    etapasDisponibles.value = await pipelineService.listarEtapas(pipelineId);
  } catch { etapasDisponibles.value = []; }
}

function validate() {
  Object.assign(errors, { nombre: '', empresaId: '', pipelineId: '' });
  let valid = true;
  if (!form.nombre.trim()) { errors.nombre = 'Requerido'; valid = false; }
  if (!form.empresaId) { errors.empresaId = 'Selecciona una empresa'; valid = false; }
  if (!isEditing.value && !form.pipelineId) { errors.pipelineId = 'Selecciona un pipeline'; valid = false; }
  return valid;
}

function handleSubmit() {
  if (!validate()) return;
  const payload = {
    nombre: form.nombre.trim(),
    valorEstimado: form.valorEstimado || undefined,
    moneda: form.moneda,
    probabilidad: form.probabilidad,
    fechaEstimadaCierre: form.fechaEstimadaCierre || undefined,
    fuente: form.fuente?.trim() || undefined,
    tipoServicio: form.tipoServicio?.trim() || undefined,
  };
  if (!isEditing.value) {
    payload.empresaId = form.empresaId;
    payload.pipelineId = form.pipelineId;
    payload.etapaId = form.etapaId || undefined;
  }
  emit('submit', payload);
}
</script>

<style scoped>
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.7); backdrop-filter: blur(8px); display: flex; align-items: center; justify-content: center; z-index: 1000; padding: var(--space-4); }
.modal-container { background: var(--bg-elevated); border: 1px solid var(--glass-border); border-radius: var(--radius-xl); width: 100%; max-width: 640px; max-height: 90vh; overflow-y: auto; box-shadow: 0 25px 60px rgba(0,0,0,0.5); }
.modal-header { display: flex; justify-content: space-between; align-items: center; padding: var(--space-6) var(--space-6) 0; }
.modal-title { font-family: var(--font-display); font-size: var(--text-xl); font-weight: 600; margin: 0; }
.modal-close { background: var(--glass-bg); border: 1px solid var(--glass-border); border-radius: var(--radius-sm); color: var(--text-secondary); padding: var(--space-2); cursor: pointer; transition: all 0.2s; display: flex; }
.modal-close:hover { color: var(--text-primary); background: rgba(255,255,255,0.08); }
.modal-body { padding: var(--space-6); display: flex; flex-direction: column; gap: var(--space-5); }
.modal-footer { display: flex; justify-content: flex-end; gap: var(--space-3); padding: 0 var(--space-6) var(--space-6); }
.form-row { display: flex; gap: var(--space-4); }
.form-group--half { flex: 1; }
.form-group--grow { flex: 2; }
.form-group--shrink { flex: 0 0 110px; }
.form-label { display: block; font-family: var(--font-body); font-size: var(--text-sm); font-weight: 500; color: var(--text-secondary); margin-bottom: var(--space-2); }
.required { color: var(--error); }
.form-error { font-size: var(--text-xs); color: var(--error); margin-top: 2px; display: block; }
.form-select { width: 100%; background: var(--bg-surface); border: 1px solid var(--glass-border); border-radius: var(--radius-md); color: var(--text-primary); font-family: var(--font-body); font-size: var(--text-sm); padding: var(--space-3) var(--space-4); box-sizing: border-box; appearance: none; background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 24 24' fill='none' stroke='rgba(255,255,255,0.3)' stroke-width='2'%3E%3Cpath d='M6 9l6 6 6-6'/%3E%3C/svg%3E"); background-repeat: no-repeat; background-position: right 12px center; }
.form-select:focus { outline: none; border-color: var(--primary); box-shadow: 0 0 0 3px var(--primary-soft); }
.form-select:disabled { opacity: 0.4; cursor: not-allowed; }
.form-select option { background: var(--bg-elevated); color: var(--text-primary); }
.form-input { width: 100%; background: var(--bg-surface); border: 1px solid var(--glass-border); border-radius: var(--radius-md); color: var(--text-primary); font-family: var(--font-body); font-size: var(--text-sm); padding: var(--space-3) var(--space-4); box-sizing: border-box; }
.form-input:focus { outline: none; border-color: var(--primary); box-shadow: 0 0 0 3px var(--primary-soft); }
.form-input::placeholder { color: var(--text-muted); }
.form-error-banner { display: flex; align-items: center; gap: var(--space-2); padding: var(--space-3) var(--space-4); background: rgba(244,63,94,0.1); border: 1px solid rgba(244,63,94,0.2); border-radius: var(--radius-md); color: var(--error); font-size: var(--text-sm); }
.modal-enter-active { transition: opacity 0.25s ease; } .modal-leave-active { transition: opacity 0.2s ease; } .modal-enter-from, .modal-leave-to { opacity: 0; }
</style>
