<template>
  <GcModal :open="visible" title="Asociar persona" width="560px" @close="$emit('close')">
    <div class="asoc">
      <div class="asoc__field">
        <span class="asoc__label">Persona</span>
        <div class="asoc__modo">
          <button class="asoc__modoopt" :class="{ 'asoc__modoopt--on': modo === 'existente' }" @click="modo = 'existente'">Existente</button>
          <button class="asoc__modoopt" :class="{ 'asoc__modoopt--on': modo === 'nueva' }" @click="modo = 'nueva'">Nueva</button>
        </div>
      </div>

      <GcSelect
        v-if="modo === 'existente'"
        v-model="form.personaId"
        label="Selecciona la persona"
        :options="personaOptions"
        :placeholder="loadingPersonas ? 'Cargando…' : 'Selecciona…'"
        :error="errors.personaId"
      />

      <div v-else class="asoc__row">
        <GcInput v-model="form.nombres" label="Nombres" placeholder="Ej: Juan Carlos" icon="user" :error="errors.nombres" />
        <GcInput v-model="form.apellidos" label="Apellidos" placeholder="Ej: Pérez García" :error="errors.apellidos" />
      </div>

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
        <span>Es contacto principal de la empresa</span>
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
import { personaService } from '@/services/persona.service';
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

const modo = ref('existente');
const form = reactive({
  personaId: '', nombres: '', apellidos: '',
  cargo: '', puesto: '', rolContacto: '', emailEmpresarial: '', telefonoEmpresarial: '', esContactoPrincipal: false,
});
const errors = reactive({ personaId: '', nombres: '', apellidos: '' });

const personas = ref([]);
const loadingPersonas = ref(false);

const personaOptions = computed(() =>
  personas.value.map((p) => ({ value: p.id, label: p.nombreCompleto || (p.nombres + ' ' + p.apellidos) }))
);

function resetForm() {
  modo.value = 'existente';
  Object.assign(form, { personaId: '', nombres: '', apellidos: '', cargo: '', puesto: '', rolContacto: '', emailEmpresarial: '', telefonoEmpresarial: '', esContactoPrincipal: false });
  errors.personaId = ''; errors.nombres = ''; errors.apellidos = '';
}

watch(() => props.visible, async (val) => {
  if (!val) return;
  resetForm();
  loadingPersonas.value = true;
  try { const res = await personaService.listar({ page_size: 200 }); personas.value = res.data || []; }
  catch { personas.value = []; }
  finally { loadingPersonas.value = false; }
});

function validate() {
  errors.personaId = ''; errors.nombres = ''; errors.apellidos = '';
  let ok = true;
  if (modo.value === 'existente') {
    if (!form.personaId) { errors.personaId = 'Selecciona una persona'; ok = false; }
  } else {
    if (!form.nombres.trim()) { errors.nombres = 'Requerido'; ok = false; }
    if (!form.apellidos.trim()) { errors.apellidos = 'Requerido'; ok = false; }
  }
  return ok;
}

function handleSubmit() {
  if (!validate()) return;
  const rel = {
    cargo: form.cargo?.trim() || undefined,
    puesto: form.puesto?.trim() || undefined,
    rolContacto: form.rolContacto || undefined,
    emailEmpresarial: form.emailEmpresarial?.trim() || undefined,
    telefonoEmpresarial: form.telefonoEmpresarial?.trim() || undefined,
    esContactoPrincipal: form.esContactoPrincipal,
  };
  if (modo.value === 'existente') {
    emit('submit', { modo: 'existente', personaId: form.personaId, rel });
  } else {
    emit('submit', { modo: 'nueva', nuevaPersona: { nombres: form.nombres.trim(), apellidos: form.apellidos.trim() }, rel });
  }
}
</script>

<style scoped>
.asoc { display: flex; flex-direction: column; gap: var(--gc-space-3); }
.asoc__row { display: grid; grid-template-columns: 1fr 1fr; gap: var(--gc-space-3); }
.asoc__field { display: flex; flex-direction: column; gap: var(--gc-space-2); }
.asoc__label { font-size: var(--gc-fs-sm); font-weight: var(--gc-fw-medium); color: var(--gc-text-2); }
.asoc__modo { display: flex; gap: var(--gc-space-2); }
.asoc__modoopt { flex: 1; padding: var(--gc-space-2) var(--gc-space-3); background: var(--gc-surface-2); border: 1px solid var(--gc-border); border-radius: var(--gc-radius-md); color: var(--gc-text-2); font-size: var(--gc-fs-md); }
.asoc__modoopt--on { border-color: var(--gc-primary); background: var(--gc-surface); color: var(--gc-text); font-weight: var(--gc-fw-medium); }
.asoc__roles { display: flex; flex-wrap: wrap; gap: var(--gc-space-2); }
.asoc__role { padding: 4px 10px; background: var(--gc-surface-2); border: 1px solid var(--gc-border); border-radius: var(--gc-radius-full); font-size: var(--gc-fs-sm); color: var(--gc-text-2); }
.asoc__role--on { border-color: var(--gc-primary); background: var(--gc-primary); color: var(--gc-primary-text); }
.asoc__toggle { display: flex; align-items: center; gap: var(--gc-space-2); font-size: var(--gc-fs-md); color: var(--gc-text-2); cursor: pointer; }
.asoc__error { display: flex; align-items: center; gap: var(--gc-space-2); padding: var(--gc-space-3); background: var(--gc-danger-soft); border: 1px solid var(--gc-danger); border-radius: var(--gc-radius-md); font-size: var(--gc-fs-sm); color: var(--gc-danger); }
</style>
