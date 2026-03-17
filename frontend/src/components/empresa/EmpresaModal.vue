<template>
  <Teleport to="body">
    <Transition name="modal">
      <div v-if="visible" class="modal-overlay" @click.self="$emit('close')">
        <div class="modal-container animate-scaleIn">
          <div class="modal-header">
            <h3 class="modal-title gradient-text">{{ isEditing ? 'Editar Empresa' : 'Nueva Empresa' }}</h3>
            <button class="modal-close" @click="$emit('close')"><Icon name="x" :size="20" /></button>
          </div>

          <div class="modal-body">
            <!-- Razón Social -->
            <div class="form-group">
              <Input v-model="form.razonSocial" label="Razón Social" placeholder="Ej: Acme Corporation S.A.S." icon="business" :error="errors.razonSocial" required />
            </div>

            <!-- Tipo Documento + Identificación + DV -->
            <div class="form-row form-row--id">
              <div class="form-group form-group--tipo-doc">
                <label class="form-label">Tipo Doc</label>
                <select v-model="form.tipoDoc" class="form-select">
                  <option value="">Seleccionar...</option>
                  <option v-for="td in store.tiposDocumento" :key="td.nombre" :value="td.nombre">{{ td.nombre }}</option>
                </select>
              </div>
              <div class="form-group form-group--nid">
                <label class="form-label">Nº Identificación</label>
                <input v-model="form.identificacionTributaria" type="text" class="form-input" placeholder="Ej: 900123456" @input="onlyNumbers($event, 'identificacionTributaria')" maxlength="50" />
                <span v-if="errors.identificacionTributaria" class="form-error">{{ errors.identificacionTributaria }}</span>
              </div>
              <div class="form-group form-group--dv">
                <label class="form-label">DV</label>
                <input v-model.number="form.dv" type="text" class="form-input form-input--center" placeholder="0" maxlength="1" @input="onlyNumbers($event, 'dv')" />
                <span v-if="errors.dv" class="form-error">{{ errors.dv }}</span>
              </div>
            </div>

            <!-- País + Departamento -->
            <div class="form-row">
              <div class="form-group form-group--half">
                <label class="form-label">País</label>
                <select v-model="form.pais" class="form-select">
                  <option value="">Seleccionar...</option>
                  <option v-for="p in store.paises" :key="p.codigo" :value="p.codigo">{{ p.nombre }}</option>
                </select>
              </div>
              <div class="form-group form-group--half">
                <label class="form-label">Departamento</label>
                <select v-model="form.departamento" class="form-select" @change="onDepartamentoChange">
                  <option value="">Seleccionar...</option>
                  <option v-for="d in store.departamentos" :key="d.codigo" :value="d.codigo">{{ d.descripcion }}</option>
                </select>
              </div>
            </div>

            <!-- Ciudad + Dirección -->
            <div class="form-row">
              <div class="form-group form-group--half">
                <label class="form-label">Ciudad</label>
                <select v-model="form.ciudad" class="form-select" :disabled="!form.departamento">
                  <option value="">{{ form.departamento ? 'Seleccionar...' : 'Seleccione departamento' }}</option>
                  <option v-for="m in store.municipios" :key="m.codigo" :value="m.codigo">{{ m.descripcion }}</option>
                </select>
              </div>
              <div class="form-group form-group--half">
                <Input v-model="form.direccionFisica" label="Dirección" placeholder="Calle, carrera, número..." />
              </div>
            </div>

            <!-- Sitio Web -->
            <div class="form-group">
              <Input v-model="form.sitioWeb" label="Sitio Web" placeholder="https://www.empresa.com" icon="external-link" :error="errors.sitioWeb" />
              <span v-if="urlValidationMsg" class="form-hint" :class="{ 'form-hint--ok': urlIsValid, 'form-hint--err': !urlIsValid }">{{ urlValidationMsg }}</span>
            </div>

            <!-- Estado (solo edición) -->
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

            <div v-if="error" class="form-error-banner"><Icon name="alert-circle" :size="16" /> {{ error }}</div>
          </div>

          <div class="modal-footer">
            <Button variant="ghost" @click="$emit('close')">Cancelar</Button>
            <Button variant="primary" icon="check" :loading="saving" @click="handleSubmit">
              {{ isEditing ? 'Guardar Cambios' : 'Crear Empresa' }}
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
import { useEmpresaStore } from '@/stores/empresa.store';

const store = useEmpresaStore();

const props = defineProps({
  visible: { type: Boolean, default: false },
  empresa: { type: Object, default: null },
  saving: { type: Boolean, default: false },
  error: { type: String, default: null },
});

const emit = defineEmits(['close', 'submit']);
const isEditing = computed(() => !!props.empresa);

const form = reactive({
  razonSocial: '', tipoDoc: '', identificacionTributaria: '', dv: 0,
  pais: '', departamento: '', ciudad: '', direccionFisica: '', sitioWeb: '', estado: 'ACTIVA',
});
const errors = reactive({ razonSocial: '', identificacionTributaria: '', dv: '', sitioWeb: '' });

const urlValidationMsg = ref('');
const urlIsValid = ref(false);

watch(() => props.visible, async (val) => {
  if (val) {
    await store.cargarCatalogos();
    if (props.empresa) {
      form.razonSocial = props.empresa.razonSocial || '';
      form.tipoDoc = props.empresa.tipoDoc || '';
      form.identificacionTributaria = props.empresa.identificacionTributaria || '';
      form.dv = props.empresa.dv ?? 0;
      form.pais = props.empresa.pais || '';
      form.departamento = props.empresa.departamento || '';
      form.ciudad = props.empresa.ciudad || '';
      form.direccionFisica = props.empresa.direccionFisica || '';
      form.sitioWeb = props.empresa.sitioWeb || '';
      form.estado = props.empresa.estado || 'ACTIVA';
      if (form.departamento) await store.cargarMunicipios(form.departamento);
    } else {
      Object.assign(form, {
        razonSocial: '', tipoDoc: '', identificacionTributaria: '', dv: 0,
        pais: '', departamento: '', ciudad: '', direccionFisica: '', sitioWeb: '', estado: 'ACTIVA',
      });
      store.municipios = [];
    }
    resetErrors();
    urlValidationMsg.value = '';
  }
});

// Cascade: departamento → municipios
async function onDepartamentoChange() {
  form.ciudad = '';
  if (form.departamento) {
    await store.cargarMunicipios(form.departamento);
  } else {
    store.municipios = [];
  }
}

// Only allow numbers
function onlyNumbers(event, field) {
  const cleaned = event.target.value.replace(/[^0-9]/g, '');
  event.target.value = cleaned;
  form[field] = field === 'dv' ? (cleaned ? parseInt(cleaned) : 0) : cleaned;
}

// URL validation
watch(() => form.sitioWeb, (val) => {
  if (!val || val.trim() === '') {
    urlValidationMsg.value = '';
    urlIsValid.value = false;
    return;
  }
  const urlRegex = /^https?:\/\/([\w-]+\.)+[\w-]+(\/[\w\-./?%&=]*)?$/;
  if (urlRegex.test(val.trim())) {
    urlValidationMsg.value = 'URL con formato válido';
    urlIsValid.value = true;
  } else {
    urlValidationMsg.value = 'Formato esperado: https://www.ejemplo.com';
    urlIsValid.value = false;
  }
});

function resetErrors() {
  errors.razonSocial = '';
  errors.identificacionTributaria = '';
  errors.dv = '';
  errors.sitioWeb = '';
}

function validate() {
  resetErrors();
  let valid = true;

  if (!form.razonSocial.trim()) { errors.razonSocial = 'La razón social es requerida'; valid = false; }
  else if (form.razonSocial.length > 200) { errors.razonSocial = 'Máximo 200 caracteres'; valid = false; }

  if (form.identificacionTributaria && !/^[0-9]*$/.test(form.identificacionTributaria)) {
    errors.identificacionTributaria = 'Solo números'; valid = false;
  }

  if (form.dv === null || form.dv === undefined || form.dv === '') {
    errors.dv = 'Requerido'; valid = false;
  } else if (form.dv < 0 || form.dv > 9) {
    errors.dv = '0-9'; valid = false;
  }

  if (form.sitioWeb && form.sitioWeb.trim() !== '') {
    const urlRegex = /^https?:\/\/([\w-]+\.)+[\w-]+(\/[\w\-./?%&=]*)?$/;
    if (!urlRegex.test(form.sitioWeb.trim())) {
      errors.sitioWeb = 'URL inválida. Use https://www.ejemplo.com'; valid = false;
    }
  }

  return valid;
}

function handleSubmit() {
  if (!validate()) return;
  const payload = {
    razonSocial: form.razonSocial.trim(),
    tipoDoc: form.tipoDoc || undefined,
    identificacionTributaria: form.identificacionTributaria || undefined,
    dv: form.dv,
    pais: form.pais || undefined,
    departamento: form.departamento || undefined,
    ciudad: form.ciudad || undefined,
    direccionFisica: form.direccionFisica?.trim() || undefined,
    sitioWeb: form.sitioWeb?.trim() || undefined,
  };
  if (isEditing.value) payload.estado = form.estado;
  emit('submit', payload);
}
</script>

<style scoped>
.modal-overlay {
  position: fixed; inset: 0; background: rgba(0, 0, 0, 0.7); backdrop-filter: blur(8px);
  display: flex; align-items: center; justify-content: center; z-index: 1000; padding: var(--space-4);
}
.modal-container {
  background: var(--bg-elevated); border: 1px solid var(--glass-border); border-radius: var(--radius-xl);
  width: 100%; max-width: 640px; max-height: 90vh; overflow-y: auto; box-shadow: 0 25px 60px rgba(0, 0, 0, 0.5);
}
.modal-header { display: flex; justify-content: space-between; align-items: center; padding: var(--space-6) var(--space-6) 0; }
.modal-title { font-family: var(--font-display); font-size: var(--text-xl); font-weight: 600; margin: 0; }
.modal-close {
  background: var(--glass-bg); border: 1px solid var(--glass-border); border-radius: var(--radius-sm);
  color: var(--text-secondary); padding: var(--space-2); cursor: pointer; transition: all 0.2s; display: flex;
}
.modal-close:hover { color: var(--text-primary); background: rgba(255, 255, 255, 0.08); }
.modal-body { padding: var(--space-6); display: flex; flex-direction: column; gap: var(--space-5); }
.modal-footer { display: flex; justify-content: flex-end; gap: var(--space-3); padding: 0 var(--space-6) var(--space-6); }

.form-row { display: flex; gap: var(--space-4); }
.form-group--half { flex: 1; }

.form-row--id { display: flex; gap: var(--space-3); }
.form-group--tipo-doc { flex: 0 0 140px; }
.form-group--nid { flex: 1; }
.form-group--dv { flex: 0 0 60px; }

.form-label {
  display: block; font-family: var(--font-body); font-size: var(--text-sm); font-weight: 500;
  color: var(--text-secondary); margin-bottom: var(--space-2);
}

.form-select {
  width: 100%; background: var(--bg-surface); border: 1px solid var(--glass-border);
  border-radius: var(--radius-md); color: var(--text-primary); font-family: var(--font-body);
  font-size: var(--text-sm); padding: var(--space-3) var(--space-4); transition: border-color 0.2s;
  box-sizing: border-box; appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 24 24' fill='none' stroke='rgba(255,255,255,0.3)' stroke-width='2'%3E%3Cpath d='M6 9l6 6 6-6'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 12px center;
}
.form-select:focus { outline: none; border-color: var(--primary); box-shadow: 0 0 0 3px var(--primary-soft); }
.form-select:disabled { opacity: 0.4; cursor: not-allowed; }
.form-select option { background: var(--bg-elevated); color: var(--text-primary); }

.form-input {
  width: 100%; background: var(--bg-surface); border: 1px solid var(--glass-border);
  border-radius: var(--radius-md); color: var(--text-primary); font-family: var(--font-body);
  font-size: var(--text-sm); padding: var(--space-3) var(--space-4); transition: border-color 0.2s;
  box-sizing: border-box;
}
.form-input:focus { outline: none; border-color: var(--primary); box-shadow: 0 0 0 3px var(--primary-soft); }
.form-input::placeholder { color: var(--text-muted); }
.form-input--center { text-align: center; font-family: var(--font-mono); font-weight: 600; }

.form-error { font-size: var(--text-xs); color: var(--error); margin-top: 2px; display: block; }

.form-hint { font-size: var(--text-xs); margin-top: 2px; display: block; }
.form-hint--ok { color: var(--success); }
.form-hint--err { color: var(--warning); }

.toggle-group { display: flex; gap: var(--space-2); }
.toggle-btn {
  flex: 1; display: flex; align-items: center; justify-content: center; gap: var(--space-2);
  padding: var(--space-3) var(--space-3); background: var(--bg-surface); border: 1px solid var(--glass-border);
  border-radius: var(--radius-md); color: var(--text-secondary); font-family: var(--font-body);
  font-size: var(--text-sm); font-weight: 500; cursor: pointer; transition: all 0.2s;
}
.toggle-btn:hover { background: rgba(255, 255, 255, 0.05); }
.toggle-btn.active { border-color: var(--primary); color: var(--primary); background: var(--primary-soft); }

.form-error-banner {
  display: flex; align-items: center; gap: var(--space-2); padding: var(--space-3) var(--space-4);
  background: rgba(244, 63, 94, 0.1); border: 1px solid rgba(244, 63, 94, 0.2);
  border-radius: var(--radius-md); color: var(--error); font-size: var(--text-sm);
}

.modal-enter-active { transition: opacity 0.25s ease; }
.modal-leave-active { transition: opacity 0.2s ease; }
.modal-enter-from, .modal-leave-to { opacity: 0; }
</style>
