<template>
  <Teleport to="body">
    <Transition name="modal">
      <div v-if="visible" class="modal-overlay" @click.self="$emit('close')">
        <div class="modal-container animate-scaleIn">
          <div class="modal-header">
            <h3 class="modal-title gradient-text">Asociar a Empresa</h3>
            <button class="modal-close" @click="$emit('close')"><Icon name="x" :size="20" /></button>
          </div>

          <div class="modal-body">
            <!-- Empresa -->
            <div class="form-group">
              <label class="form-label">Empresa <span class="required">*</span></label>
              <select v-model="form.empresaId" class="form-select" :disabled="loadingEmpresas">
                <option :value="null">{{ loadingEmpresas ? 'Cargando...' : 'Seleccionar empresa...' }}</option>
                <option v-for="emp in empresas" :key="emp.id" :value="emp.id">{{ emp.razonSocial }}</option>
              </select>
              <span v-if="errors.empresaId" class="form-error">{{ errors.empresaId }}</span>
            </div>

            <!-- Cargo + Puesto -->
            <div class="form-row">
              <div class="form-group form-group--half">
                <Input v-model="form.cargo" label="Cargo" placeholder="Ej: Gerente Comercial" />
              </div>
              <div class="form-group form-group--half">
                <Input v-model="form.puesto" label="Puesto / Área" placeholder="Ej: Dirección Comercial" />
              </div>
            </div>

            <!-- Rol de Contacto -->
            <div class="form-group">
              <label class="form-label">Rol de Contacto</label>
              <div class="toggle-group toggle-group--wrap">
                <button v-for="rol in roles" :key="rol.value" class="toggle-btn toggle-btn--sm" :class="{ active: form.rolContacto === rol.value }" @click="form.rolContacto = form.rolContacto === rol.value ? '' : rol.value">
                  {{ rol.label }}
                </button>
              </div>
            </div>

            <!-- Email + Teléfono empresarial -->
            <div class="form-row">
              <div class="form-group form-group--half">
                <Input v-model="form.emailEmpresarial" label="Email Empresarial" placeholder="juan@empresa.com" type="email" />
              </div>
              <div class="form-group form-group--half">
                <Input v-model="form.telefonoEmpresarial" label="Teléfono Empresarial" placeholder="+57 2 555 1234" />
              </div>
            </div>

            <!-- Contacto Principal -->
            <div class="form-group">
              <label class="form-label">¿Es contacto principal?</label>
              <div class="toggle-group">
                <button class="toggle-btn" :class="{ active: form.esContactoPrincipal }" @click="form.esContactoPrincipal = true">
                  <Icon name="check-circle" :size="14" /> Sí
                </button>
                <button class="toggle-btn" :class="{ active: !form.esContactoPrincipal }" @click="form.esContactoPrincipal = false">
                  <Icon name="x" :size="14" /> No
                </button>
              </div>
            </div>

            <div v-if="error" class="form-error-banner"><Icon name="alert-circle" :size="16" /> {{ error }}</div>
          </div>

          <div class="modal-footer">
            <Button variant="ghost" @click="$emit('close')">Cancelar</Button>
            <Button variant="primary" icon="check" :loading="saving" @click="handleSubmit">Asociar</Button>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { reactive, watch } from 'vue';
import Icon from '@/components/ui/Icon.vue';
import Input from '@/components/ui/Input.vue';
import Button from '@/components/ui/Button.vue';

const props = defineProps({
  visible: { type: Boolean, default: false },
  empresas: { type: Array, default: () => [] },
  loadingEmpresas: { type: Boolean, default: false },
  saving: { type: Boolean, default: false },
  error: { type: String, default: null },
});

const emit = defineEmits(['close', 'submit']);

const roles = [
  { value: 'DECISOR', label: 'Decisor' },
  { value: 'INFLUENCIADOR', label: 'Influenciador' },
  { value: 'TECNICO', label: 'Técnico' },
  { value: 'USUARIO', label: 'Usuario' },
  { value: 'ADMINISTRATIVO', label: 'Administrativo' },
];

const form = reactive({
  empresaId: null, cargo: '', puesto: '', rolContacto: '',
  emailEmpresarial: '', telefonoEmpresarial: '', esContactoPrincipal: false,
});
const errors = reactive({ empresaId: '' });

watch(() => props.visible, (val) => {
  if (val) {
    Object.assign(form, {
      empresaId: null, cargo: '', puesto: '', rolContacto: '',
      emailEmpresarial: '', telefonoEmpresarial: '', esContactoPrincipal: false,
    });
    errors.empresaId = '';
  }
});

function handleSubmit() {
  errors.empresaId = '';
  if (!form.empresaId) { errors.empresaId = 'Selecciona una empresa'; return; }
  emit('submit', {
    empresaId: form.empresaId,
    cargo: form.cargo?.trim() || undefined,
    puesto: form.puesto?.trim() || undefined,
    rolContacto: form.rolContacto || undefined,
    emailEmpresarial: form.emailEmpresarial?.trim() || undefined,
    telefonoEmpresarial: form.telefonoEmpresarial?.trim() || undefined,
    esContactoPrincipal: form.esContactoPrincipal,
  });
}
</script>

<style scoped>
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.7); backdrop-filter: blur(8px); display: flex; align-items: center; justify-content: center; z-index: 1000; padding: var(--space-4); }
.modal-container { background: var(--bg-elevated); border: 1px solid var(--glass-border); border-radius: var(--radius-xl); width: 100%; max-width: 600px; max-height: 90vh; overflow-y: auto; box-shadow: 0 25px 60px rgba(0,0,0,0.5); }
.modal-header { display: flex; justify-content: space-between; align-items: center; padding: var(--space-6) var(--space-6) 0; }
.modal-title { font-family: var(--font-display); font-size: var(--text-xl); font-weight: 600; margin: 0; }
.modal-close { background: var(--glass-bg); border: 1px solid var(--glass-border); border-radius: var(--radius-sm); color: var(--text-secondary); padding: var(--space-2); cursor: pointer; transition: all 0.2s; display: flex; }
.modal-close:hover { color: var(--text-primary); background: rgba(255,255,255,0.08); }
.modal-body { padding: var(--space-6); display: flex; flex-direction: column; gap: var(--space-5); }
.modal-footer { display: flex; justify-content: flex-end; gap: var(--space-3); padding: 0 var(--space-6) var(--space-6); }
.form-row { display: flex; gap: var(--space-4); }
.form-group--half { flex: 1; }
.form-label { display: block; font-family: var(--font-body); font-size: var(--text-sm); font-weight: 500; color: var(--text-secondary); margin-bottom: var(--space-2); }
.required { color: var(--error); }
.form-error { font-size: var(--text-xs); color: var(--error); margin-top: 2px; display: block; }
.form-select { width: 100%; background: var(--bg-surface); border: 1px solid var(--glass-border); border-radius: var(--radius-md); color: var(--text-primary); font-family: var(--font-body); font-size: var(--text-sm); padding: var(--space-3) var(--space-4); box-sizing: border-box; appearance: none; background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 24 24' fill='none' stroke='rgba(255,255,255,0.3)' stroke-width='2'%3E%3Cpath d='M6 9l6 6 6-6'/%3E%3C/svg%3E"); background-repeat: no-repeat; background-position: right 12px center; }
.form-select:focus { outline: none; border-color: var(--primary); box-shadow: 0 0 0 3px var(--primary-soft); }
.form-select:disabled { opacity: 0.4; cursor: not-allowed; }
.form-select option { background: var(--bg-elevated); color: var(--text-primary); }
.toggle-group { display: flex; gap: var(--space-2); }
.toggle-group--wrap { flex-wrap: wrap; }
.toggle-btn { flex: 1; display: flex; align-items: center; justify-content: center; gap: var(--space-2); padding: var(--space-3); background: var(--bg-surface); border: 1px solid var(--glass-border); border-radius: var(--radius-md); color: var(--text-secondary); font-family: var(--font-body); font-size: var(--text-sm); font-weight: 500; cursor: pointer; transition: all 0.2s; }
.toggle-btn--sm { padding: var(--space-2) var(--space-3); font-size: var(--text-xs); min-width: 0; flex: 0 1 auto; }
.toggle-btn:hover { background: rgba(255,255,255,0.05); }
.toggle-btn.active { border-color: var(--primary); color: var(--primary); background: var(--primary-soft); }
.form-error-banner { display: flex; align-items: center; gap: var(--space-2); padding: var(--space-3) var(--space-4); background: rgba(244,63,94,0.1); border: 1px solid rgba(244,63,94,0.2); border-radius: var(--radius-md); color: var(--error); font-size: var(--text-sm); }
.modal-enter-active { transition: opacity 0.25s ease; }
.modal-leave-active { transition: opacity 0.2s ease; }
.modal-enter-from, .modal-leave-to { opacity: 0; }
</style>
