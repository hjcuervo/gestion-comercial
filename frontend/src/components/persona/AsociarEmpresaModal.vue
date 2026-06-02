<template>
  <GcModal :open="visible" title="Asociar empresa" width="560px" @close="$emit('close')">
    <div class="asoc">
      <GcSelect v-model="form.empresaId" label="Empresa" :options="empresaOptions" :placeholder="loadingEmpresas ? 'Cargando…' : 'Selecciona…'" :error="errors.empresaId" />

      <div class="asoc__row">
        <GcInput v-model="form.cargo" label="Cargo" placeholder="Ej: Gerente Comercial" />
        <GcInput v-model="form.puesto" label="Puesto / Área" placeholder="Ej: Dirección Comercial" />
      </div>

      <div class="asoc__field">
        <span class="asoc__label">Rol de contacto</span>
        <div class="asoc__roles">
          <button v-for="rol in roles" :key="rol.value" class="asoc__role" :class="{ 'asoc__role--on': form.rolContacto === rol.value }" @click="form.rolContacto = form.rolContacto === rol.value ? '' : rol.value">{{ rol.label }}</button>
        </div>
      </div>

      <div class="asoc__row">
        <GcInput v-model="form.emailEmpresarial" label="Email empresarial" type="email" placeholder="juan@empresa.com" />
        <GcInput v-model="form.telefonoEmpresarial" label="Teléfono empresarial" placeholder="+57 2 555 1234" />
      </div>

      <label class="asoc__toggle">
        <input type="checkbox" v-model="form.esContactoPrincipal" />
        <span>Es contacto principal</span>
      </label>

      <div v-if="error" class="asoc__error"><GcIcon name="alert-circle" :size="16" /><span>{{ error }}</span></div>
    </div>
    <template #footer>
      <GcButton variant="ghost" @click="$emit('close')">Cancelar</GcButton>
      <GcButton variant="primary" :loading="saving" @click="handleSubmit">Asociar</GcButton>
    </template>
  </GcModal>
</template>

<script setup>
import { reactive, ref, computed, watch } from 'vue';
import { empresaService } from '@/services/empresa.service';
import GcModal from '@/components/ui/GcModal.vue';
import GcInput from '@/components/ui/GcInput.vue';
import GcSelect from '@/components/ui/GcSelect.vue';
import GcButton from '@/components/ui/GcButton.vue';
import GcIcon from '@/components/ui/GcIcon.vue';

const props = defineProps({
  visible: { type: Boolean, default: false },
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

const form = reactive({ empresaId: '', cargo: '', puesto: '', rolContacto: '', emailEmpresarial: '', telefonoEmpresarial: '', esContactoPrincipal: false });
const errors = reactive({ empresaId: '' });
const empresas = ref([]);
const loadingEmpresas = ref(false);

const empresaOptions = computed(() => empresas.value.map((e) => ({ value: e.id, label: e.razonSocial })));

watch(() => props.visible, async (val) => {
  if (!val) return;
  Object.assign(form, { empresaId: '', cargo: '', puesto: '', rolContacto: '', emailEmpresarial: '', telefonoEmpresarial: '', esContactoPrincipal: false });
  errors.empresaId = '';
  loadingEmpresas.value = true;
  try { const res = await empresaService.listar({ page_size: 200, estado: 'ACTIVA' }); empresas.value = res.data || []; }
  catch { empresas.value = []; }
  finally { loadingEmpresas.value = false; }
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
.asoc { display: flex; flex-direction: column; gap: var(--gc-space-3); }
.asoc__row { display: grid; grid-template-columns: 1fr 1fr; gap: var(--gc-space-3); }
.asoc__field { display: flex; flex-direction: column; gap: var(--gc-space-2); }
.asoc__label { font-size: var(--gc-fs-sm); font-weight: var(--gc-fw-medium); color: var(--gc-text-2); }
.asoc__roles { display: flex; flex-wrap: wrap; gap: var(--gc-space-2); }
.asoc__role { padding: 4px 10px; background: var(--gc-surface-2); border: 1px solid var(--gc-border); border-radius: var(--gc-radius-full); font-size: var(--gc-fs-sm); color: var(--gc-text-2); }
.asoc__role--on { border-color: var(--gc-primary); background: var(--gc-primary); color: var(--gc-primary-text); }
.asoc__toggle { display: flex; align-items: center; gap: var(--gc-space-2); font-size: var(--gc-fs-md); color: var(--gc-text-2); cursor: pointer; }
.asoc__error { display: flex; align-items: center; gap: var(--gc-space-2); padding: var(--gc-space-3); background: var(--gc-danger-soft); border: 1px solid var(--gc-danger); border-radius: var(--gc-radius-md); font-size: var(--gc-fs-sm); color: var(--gc-danger); }
</style>
