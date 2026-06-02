<template>
  <GcModal :open="visible" :title="isEditing ? 'Editar persona' : 'Nueva persona'" width="600px" @close="$emit('close')">
    <div class="permodal">
      <div class="permodal__row">
        <GcInput v-model="form.nombres" label="Nombres" placeholder="Ej: Juan Carlos" icon="user" :error="errors.nombres" />
        <GcInput v-model="form.apellidos" label="Apellidos" placeholder="Ej: Pérez García" :error="errors.apellidos" />
      </div>

      <div class="permodal__row">
        <GcSelect v-model="form.tipoDocumento" label="Tipo de documento" :options="tiposDocOptions" placeholder="—" />
        <GcInput v-model="form.numeroDocumento" label="Número de documento" mono placeholder="Ej: 1020304050" />
      </div>

      <div class="permodal__row">
        <GcSelect v-model="form.origenCodigo" label="Origen" :options="origenOptions" placeholder="—" />
        <GcSelect v-model="form.propietarioId" label="Propietario" :options="propietarioOptions" :placeholder="loadingUsuarios ? 'Cargando…' : '—'" />
      </div>

      <div class="permodal__row">
        <GcSelect v-model="form.reportaAId" label="Reporta a" :options="reportaOptions" :placeholder="loadingPersonas ? 'Cargando…' : '—'" />
        <GcInput v-model="form.idioma" label="Idioma" placeholder="Ej: Español" />
      </div>

      <GcTextarea v-model="form.notas" label="Notas" :rows="2" placeholder="Observaciones, contexto…" />

      <div v-if="isEditing" class="permodal__field">
        <span class="permodal__label">Estado</span>
        <div class="permodal__toggle">
          <button class="permodal__opt" :class="{ 'permodal__opt--on': form.activo }" @click="form.activo = true">Activo</button>
          <button class="permodal__opt" :class="{ 'permodal__opt--on': !form.activo }" @click="form.activo = false">Inactivo</button>
        </div>
      </div>

      <div v-if="error" class="permodal__error"><GcIcon name="alert-circle" :size="16" /><span>{{ error }}</span></div>
    </div>
    <template #footer>
      <GcButton variant="ghost" @click="$emit('close')">Cancelar</GcButton>
      <GcButton variant="primary" :loading="saving" @click="handleSubmit">{{ isEditing ? 'Guardar' : 'Crear' }}</GcButton>
    </template>
  </GcModal>
</template>

<script setup>
import { reactive, ref, computed, watch } from 'vue';
import { empresaService } from '@/services/empresa.service';
import { usuarioService } from '@/services/usuario.service';
import { personaService } from '@/services/persona.service';
import GcModal from '@/components/ui/GcModal.vue';
import GcInput from '@/components/ui/GcInput.vue';
import GcSelect from '@/components/ui/GcSelect.vue';
import GcTextarea from '@/components/ui/GcTextarea.vue';
import GcButton from '@/components/ui/GcButton.vue';
import GcIcon from '@/components/ui/GcIcon.vue';

const props = defineProps({
  visible: { type: Boolean, default: false },
  persona: { type: Object, default: null },
  saving: { type: Boolean, default: false },
  error: { type: String, default: null },
});
const emit = defineEmits(['close', 'submit']);

const isEditing = computed(() => !!props.persona);

const form = reactive({
  nombres: '', apellidos: '', tipoDocumento: '', numeroDocumento: '',
  origenCodigo: '', propietarioId: '', reportaAId: '', idioma: '', notas: '', activo: true,
});
const errors = reactive({ nombres: '', apellidos: '' });

const tiposDoc = ref([]);
const origenes = ref([]);
const usuarios = ref([]);
const personas = ref([]);
const loadingUsuarios = ref(false);
const loadingPersonas = ref(false);

const tiposDocOptions = computed(() => tiposDoc.value.map((t) => ({ value: t.nombre ?? t.codigo ?? t, label: t.nombre ?? t.descripcion ?? t })));
const origenOptions = computed(() => origenes.value.map((o) => ({ value: o.codigo, label: o.nombre })));
const propietarioOptions = computed(() => usuarios.value.map((u) => ({ value: u.id, label: u.nombreCompleto || (u.nombres + ' ' + u.apellidos) })));
// "Reporta a": excluye a la persona en edición (no puede reportarse a sí misma)
const reportaOptions = computed(() =>
  personas.value
    .filter((p) => !props.persona || String(p.id) !== String(props.persona.id))
    .map((p) => ({ value: p.id, label: p.nombreCompleto || (p.nombres + ' ' + p.apellidos) }))
);

async function cargarCatalogos() {
  try {
    const [td, or] = await Promise.all([
      empresaService.listarTiposDocumento().catch(() => []),
      empresaService.listarOrigenes().catch(() => []),
    ]);
    tiposDoc.value = td || [];
    origenes.value = or || [];
  } catch { /* noop */ }
  loadingUsuarios.value = true;
  usuarioService.listar().then((u) => { usuarios.value = u || []; }).catch(() => { usuarios.value = []; }).finally(() => { loadingUsuarios.value = false; });
  loadingPersonas.value = true;
  personaService.listar({ page_size: 200 }).then((r) => { personas.value = r.data || []; }).catch(() => { personas.value = []; }).finally(() => { loadingPersonas.value = false; });
}

watch(() => props.visible, async (val) => {
  if (!val) return;
  await cargarCatalogos();
  if (props.persona) {
    Object.assign(form, {
      nombres: props.persona.nombres || '',
      apellidos: props.persona.apellidos || '',
      tipoDocumento: props.persona.tipoDocumento || '',
      numeroDocumento: props.persona.numeroDocumento || '',
      origenCodigo: props.persona.origenCodigo || '',
      propietarioId: props.persona.propietarioId ?? '',
      reportaAId: props.persona.reportaAId ?? '',
      idioma: props.persona.idioma || '',
      notas: props.persona.notas || '',
      activo: props.persona.activo !== false,
    });
  } else {
    Object.assign(form, { nombres: '', apellidos: '', tipoDocumento: '', numeroDocumento: '', origenCodigo: '', propietarioId: '', reportaAId: '', idioma: '', notas: '', activo: true });
  }
  errors.nombres = ''; errors.apellidos = '';
});

function validate() {
  errors.nombres = ''; errors.apellidos = '';
  let ok = true;
  if (!form.nombres.trim()) { errors.nombres = 'Requerido'; ok = false; }
  if (!form.apellidos.trim()) { errors.apellidos = 'Requerido'; ok = false; }
  return ok;
}

function handleSubmit() {
  if (!validate()) return;
  const payload = {
    nombres: form.nombres.trim(),
    apellidos: form.apellidos.trim(),
    tipoDocumento: form.tipoDocumento || null,
    numeroDocumento: form.numeroDocumento?.trim() || null,
    origenCodigo: form.origenCodigo || null,
    propietarioId: form.propietarioId || null,
    reportaAId: form.reportaAId || null,
    idioma: form.idioma?.trim() || null,
    notas: form.notas?.trim() || null,
  };
  if (isEditing.value) payload.activo = form.activo;
  emit('submit', payload);
}
</script>

<style scoped>
.permodal { display: flex; flex-direction: column; gap: var(--gc-space-3); }
.permodal__row { display: grid; grid-template-columns: 1fr 1fr; gap: var(--gc-space-3); }
.permodal__field { display: flex; flex-direction: column; gap: var(--gc-space-2); }
.permodal__label { font-size: var(--gc-fs-sm); font-weight: var(--gc-fw-medium); color: var(--gc-text-2); }
.permodal__toggle { display: flex; gap: var(--gc-space-2); }
.permodal__opt { flex: 1; padding: var(--gc-space-2) var(--gc-space-3); background: var(--gc-surface-2); border: 1px solid var(--gc-border); border-radius: var(--gc-radius-md); color: var(--gc-text-2); font-size: var(--gc-fs-md); }
.permodal__opt--on { border-color: var(--gc-primary); background: var(--gc-surface); color: var(--gc-text); font-weight: var(--gc-fw-medium); }
.permodal__error { display: flex; align-items: center; gap: var(--gc-space-2); padding: var(--gc-space-3); background: var(--gc-danger-soft); border: 1px solid var(--gc-danger); border-radius: var(--gc-radius-md); font-size: var(--gc-fs-sm); color: var(--gc-danger); }
</style>
