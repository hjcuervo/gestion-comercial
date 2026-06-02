<template>
  <Teleport to="body">
    <div class="modal-overlay" @click.self="$emit('close')">
      <div class="modal glass animate-slideUp">
        <div class="modal__header">
          <h2 class="modal__title">Formalizar Contrato</h2>
          <button class="modal__close" @click="$emit('close')"><GcIcon name="x" :size="18" /></button>
        </div>

        <form class="modal__body" @submit.prevent="handleSubmit">
          <p class="modal__desc">Formalizar contrato para la oportunidad <strong>{{ oportunidadNombre }}</strong>. Al confirmar, se creará el contrato y la oportunidad saldrá del pipeline.</p>

          <div class="form-row">
            <div class="field">
              <label class="field__label">Tipo de Contrato <span class="req">*</span></label>
              <div v-if="loadingTipos" class="loading-inline"><GcSpinner :size="14" /> Cargando...</div>
              <select v-else v-model="form.tipoContratoId" class="field__select" required>
                <option :value="null" disabled>Seleccione</option>
                <option v-for="t in tiposContrato" :key="t.id" :value="t.id">{{ t.nombre }}</option>
              </select>
            </div>
            <div class="field">
              <label class="field__label">Moneda</label>
              <select v-model="form.moneda" class="field__select">
                <option value="COP">COP</option><option value="USD">USD</option><option value="EUR">EUR</option>
              </select>
            </div>
          </div>

          <div class="form-row">
            <div class="field">
              <label class="field__label">Nº Contrato Interno</label>
              <input v-model="form.numeroContratoInterno" type="text" class="field__input" maxlength="50" placeholder="Ej: ARQ-2026-001" />
            </div>
            <div class="field">
              <label class="field__label">Nº Contrato Cliente</label>
              <input v-model="form.numeroContratoCliente" type="text" class="field__input" maxlength="100" placeholder="Número asignado por el cliente" />
            </div>
          </div>

          <div class="field">
            <label class="field__label">Objeto del Contrato</label>
            <textarea v-model="form.objeto" class="field__textarea" rows="2" maxlength="1000" placeholder="Objeto del contrato según documento formal"></textarea>
          </div>

          <div class="form-row">
            <div class="field">
              <label class="field__label">Valor del Contrato</label>
              <input v-model="form.valorContrato" type="number" step="0.01" class="field__input" placeholder="Valor base" />
            </div>
          </div>

          <div class="form-row">
            <div class="field">
              <label class="field__label">Fecha Inicio</label>
              <input v-model="form.fechaInicio" type="date" class="field__input" />
            </div>
            <div class="field">
              <label class="field__label">Fecha Fin</label>
              <input v-model="form.fechaFin" type="date" class="field__input" />
            </div>
          </div>

          <div class="form-row">
            <div class="field">
              <label class="field__label">Responsable de Gestión</label>
              <input v-model="form.responsableGestion" type="text" class="field__input" maxlength="100" placeholder="Responsable interno" />
            </div>
            <div class="field">
              <label class="field__label">Interventor</label>
              <input v-model="form.interventor" type="text" class="field__input" maxlength="200" placeholder="Interventor del cliente" />
            </div>
          </div>

          <div class="field">
            <label class="field__label">Empresa de Facturación (si es diferente)</label>
            <select v-model="form.empresaFacturacionId" class="field__select">
              <option :value="null">Misma empresa del contrato</option>
              <option v-for="e in empresas" :key="e.id" :value="e.id">{{ e.razonSocial }}</option>
            </select>
            <span class="field__hint">Seleccione si la facturación va a una filial diferente</span>
          </div>

          <div class="field">
            <label class="field__label">Observaciones</label>
            <textarea v-model="form.observaciones" class="field__textarea" rows="2" maxlength="2000" placeholder="Observaciones del contrato (opcional)"></textarea>
          </div>

          <div v-if="error" class="modal-error"><GcIcon name="alert-circle" :size="14" /> {{ error }}</div>

          <div class="modal__actions">
            <button type="button" class="btn btn--ghost" @click="$emit('close')">Cancelar</button>
            <button type="submit" class="btn btn--primary" :disabled="!isValid || submitting">
              <GcSpinner v-if="submitting" :size="14" />
              Formalizar Contrato
            </button>
          </div>
        </form>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import GcIcon from '@/components/ui/GcIcon.vue';
import GcSpinner from '@/components/ui/GcSpinner.vue';
import { contratoService } from '@/services/contrato.service';
import { empresaService } from '@/services/empresa.service';

const props = defineProps({
  oportunidadId: { type: Number, required: true },
  oportunidadNombre: { type: String, default: '' },
  monedaDefault: { type: String, default: 'COP' },
  valorDefault: { type: Number, default: null },
});
const emit = defineEmits(['close', 'created']);

const tiposContrato = ref([]);
const loadingTipos = ref(true);
const empresas = ref([]);
const form = ref({
  tipoContratoId: null,
  moneda: props.monedaDefault,
  numeroContratoInterno: '',
  numeroContratoCliente: '',
  objeto: '',
  valorContrato: props.valorDefault,
  fechaInicio: '',
  fechaFin: '',
  responsableGestion: '',
  interventor: '',
  empresaFacturacionId: null,
  observaciones: '',
});
const error = ref(null);
const submitting = ref(false);

const isValid = computed(() => form.value.tipoContratoId);

onMounted(async () => {
  try { tiposContrato.value = await contratoService.listarTipos(); } catch {}
  finally { loadingTipos.value = false; }
  try { const res = await empresaService.listar({ page_size: 100 }); empresas.value = res.data || []; } catch {}
});

async function handleSubmit() {
  if (!isValid.value) return;
  error.value = null;
  submitting.value = true;
  try {
    const payload = {
      oportunidadId: props.oportunidadId,
      tipoContratoId: form.value.tipoContratoId,
      moneda: form.value.moneda,
      numeroContratoInterno: form.value.numeroContratoInterno?.trim() || null,
      numeroContratoCliente: form.value.numeroContratoCliente?.trim() || null,
      objeto: form.value.objeto?.trim() || null,
      valorContrato: form.value.valorContrato || null,
      fechaInicio: form.value.fechaInicio || null,
      fechaFin: form.value.fechaFin || null,
      responsableGestion: form.value.responsableGestion?.trim() || null,
      interventor: form.value.interventor?.trim() || null,
      empresaFacturacionId: form.value.empresaFacturacionId || null,
      observaciones: form.value.observaciones?.trim() || null,
    };
    const created = await contratoService.formalizarContrato(payload);
    emit('created', created);
  } catch (err) {
    error.value = err.response?.data?.message || 'Error al formalizar contrato';
  } finally { submitting.value = false; }
}
</script>

<style scoped>
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.6); backdrop-filter: blur(4px); display: flex; align-items: center; justify-content: center; z-index: var(--z-modal); padding: var(--space-4); }
.modal { width: 100%; max-width: 600px; border-radius: var(--radius-xl); padding: var(--space-6); max-height: 90vh; overflow-y: auto; }
.modal__header { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--space-4); }
.modal__title { font-family: var(--font-display); font-size: var(--text-xl); font-weight: 700; color: var(--text-primary); margin: 0; }
.modal__close { background: none; border: none; color: var(--text-muted); cursor: pointer; padding: 4px; border-radius: var(--radius-sm); }
.modal__close:hover { color: var(--text-primary); background: var(--glass-hover); }
.modal__body { display: flex; flex-direction: column; gap: var(--space-4); }
.modal__desc { font-size: var(--text-xs); color: var(--text-secondary); line-height: 1.6; margin: 0; }
.modal__desc strong { color: var(--text-primary); }
.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: var(--space-3); }
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
.loading-inline { display: flex; align-items: center; gap: var(--space-2); color: var(--text-muted); font-size: var(--text-xs); padding: var(--space-2); }
.modal-error { display: flex; align-items: center; gap: var(--space-2); padding: var(--space-3); background: var(--error-soft); border-radius: var(--radius-md); color: var(--error); font-size: var(--text-xs); }
.modal__actions { display: flex; justify-content: flex-end; gap: var(--space-3); padding-top: var(--space-3); border-top: 1px solid var(--glass-border); }
.btn { display: flex; align-items: center; gap: var(--space-2); padding: var(--space-2) var(--space-5); border-radius: var(--radius-full); font-family: var(--font-body); font-size: var(--text-xs); font-weight: 600; cursor: pointer; transition: all 0.15s; border: 1px solid transparent; }
.btn--ghost { background: transparent; color: var(--text-secondary); border-color: var(--glass-border); }
.btn--primary { background: var(--primary); color: #000; }
.btn--primary:hover { box-shadow: 0 0 20px var(--primary-glow); }
.btn--primary:disabled { opacity: 0.5; cursor: not-allowed; }
</style>
