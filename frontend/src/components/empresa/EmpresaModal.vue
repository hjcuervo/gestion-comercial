<template>
  <GcModal :open="visible" :title="isEditing ? 'Editar empresa' : 'Nueva empresa'" width="640px" @close="$emit('close')">
    <div class="empmodal">
      <GcInput v-model="form.razonSocial" label="Razón social" placeholder="Ej: Acme Corporation S.A.S." icon="building" :error="errors.razonSocial" />

      <div class="empmodal__row3">
        <GcSelect v-model="form.tipoDoc" label="Tipo doc." :options="tiposDocOptions" placeholder="—" />
        <GcInput v-model="form.identificacionTributaria" label="Identificación" mono placeholder="900123456" :error="errors.identificacionTributaria" />
        <GcInput v-model.number="form.dv" label="DV" mono placeholder="0" />
      </div>

      <div class="empmodal__row3">
        <GcSelect v-model="form.pais" label="País" :options="paisOptions" placeholder="—" />
        <GcSelect v-model="form.departamento" label="Departamento" :options="deptoOptions" placeholder="—" @update:modelValue="onDepartamentoChange" />
        <GcSelect v-model="form.ciudad" label="Ciudad" :options="ciudadOptions" :placeholder="form.departamento ? '—' : 'Elige depto.'" :disabled="!form.departamento" />
      </div>

      <GcInput v-model="form.direccionFisica" label="Dirección" placeholder="Calle, carrera, número…" />
      <GcInput v-model="form.sitioWeb" label="Sitio web" icon="external-link" placeholder="https://www.empresa.com" :error="errors.sitioWeb" />

      <!-- Datos comerciales (F-RP4) -->
      <div class="empmodal__field">
        <span class="empmodal__label">Clasificación</span>
        <div class="empmodal__toggle">
          <button class="empmodal__opt" :class="{ 'empmodal__opt--on': form.clasificacion === 'PROSPECTO' }" @click="form.clasificacion = 'PROSPECTO'">Prospecto</button>
          <button class="empmodal__opt" :class="{ 'empmodal__opt--on': form.clasificacion === 'CLIENTE' }" @click="form.clasificacion = 'CLIENTE'">Cliente</button>
          <button class="empmodal__opt" :class="{ 'empmodal__opt--on': form.clasificacion === 'ALIADO' }" @click="form.clasificacion = 'ALIADO'">Aliado</button>
        </div>
      </div>

      <div class="empmodal__row3">
        <GcSelect v-model="form.sectorCodigo" label="Sector" :options="sectorOptions" placeholder="—" />
        <GcSelect v-model="form.tamano" label="Tamaño" :options="tamanoOptions" placeholder="—" />
        <GcSelect v-model="form.origenCodigo" label="Origen" :options="origenOptions" placeholder="—" />
      </div>

      <div class="empmodal__row3">
        <GcSelect v-model="form.propietarioId" label="Propietario" :options="propietarioOptions" :placeholder="loadingUsuarios ? 'Cargando…' : '—'" />
        <GcInput v-model.number="form.ingresosAnuales" label="Ingresos anuales" mono placeholder="0" />
        <div></div>
      </div>

      <GcTextarea v-model="form.descripcion" label="Descripción" :rows="2" placeholder="Notas de la cuenta…" />

      <div class="empmodal__field">
        <span class="empmodal__label">Estado</span>
        <div class="empmodal__toggle">
          <button class="empmodal__opt" :class="{ 'empmodal__opt--on': form.estado === 'ACTIVA' }" @click="form.estado = 'ACTIVA'">Activa</button>
          <button class="empmodal__opt" :class="{ 'empmodal__opt--on': form.estado === 'INACTIVA' }" @click="form.estado = 'INACTIVA'">Inactiva</button>
        </div>
      </div>

      <div v-if="error" class="empmodal__error"><GcIcon name="alert-circle" :size="16" /><span>{{ error }}</span></div>
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
import GcModal from '@/components/ui/GcModal.vue';
import GcInput from '@/components/ui/GcInput.vue';
import GcSelect from '@/components/ui/GcSelect.vue';
import GcTextarea from '@/components/ui/GcTextarea.vue';
import GcButton from '@/components/ui/GcButton.vue';
import GcIcon from '@/components/ui/GcIcon.vue';

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
  clasificacion: 'PROSPECTO', sectorCodigo: '', tamano: '', origenCodigo: '', propietarioId: '', ingresosAnuales: null, descripcion: '',
});
const errors = reactive({ razonSocial: '', identificacionTributaria: '', sitioWeb: '' });

const tiposDoc = ref([]);
const paises = ref([]);
const departamentos = ref([]);
const municipios = ref([]);
const sectores = ref([]);
const origenes = ref([]);
const usuarios = ref([]);
const loadingUsuarios = ref(false);

const tamanoOptions = [
  { value: 'MICRO', label: 'Micro' },
  { value: 'PEQUENA', label: 'Pequeña' },
  { value: 'MEDIANA', label: 'Mediana' },
  { value: 'GRANDE', label: 'Grande' },
];
const sectorOptions = computed(() => sectores.value.map((s) => ({ value: s.codigo, label: s.nombre })));
const origenOptions = computed(() => origenes.value.map((o) => ({ value: o.codigo, label: o.nombre })));
const propietarioOptions = computed(() => usuarios.value.map((u) => ({ value: u.id, label: u.nombreCompleto || (u.nombres + ' ' + u.apellidos) })));

// Mapeos por catálogo (campos reales del backend):
//  País → label nombre, value codigo · Departamento/Municipio → label descripcion, value codigo
//  TipoDoc → label nombre/descripcion, value nombre
function optLabel(o) { return o.descripcion ?? o.nombre ?? String(o); }
function optValue(o) { return o.codigo ?? o.nombre ?? o.id ?? o; }
const tiposDocOptions = computed(() => tiposDoc.value.map((t) => ({ value: t.nombre ?? t.codigo ?? t, label: t.nombre ?? t.descripcion ?? t })));
const paisOptions = computed(() => paises.value.map((p) => ({ value: p.codigo ?? p, label: p.nombre ?? p.descripcion ?? p })));
const deptoOptions = computed(() => departamentos.value.map((d) => ({ value: optValue(d), label: optLabel(d) })));
const ciudadOptions = computed(() => municipios.value.map((m) => ({ value: optValue(m), label: optLabel(m) })));

async function cargarCatalogos() {
  try {
    const [td, pa, de, se, or] = await Promise.all([
      empresaService.listarTiposDocumento().catch(() => []),
      empresaService.listarPaises().catch(() => []),
      empresaService.listarDepartamentos().catch(() => []),
      empresaService.listarSectores().catch(() => []),
      empresaService.listarOrigenes().catch(() => []),
    ]);
    tiposDoc.value = td || [];
    paises.value = pa || [];
    departamentos.value = de || [];
    sectores.value = se || [];
    origenes.value = or || [];
  } catch { /* noop */ }
  loadingUsuarios.value = true;
  usuarioService.listar().then((u) => { usuarios.value = u || []; }).catch(() => { usuarios.value = []; }).finally(() => { loadingUsuarios.value = false; });
}
async function cargarMunicipios(depto) {
  if (!depto) { municipios.value = []; return; }
  try { municipios.value = await empresaService.listarMunicipios(depto); } catch { municipios.value = []; }
}
async function onDepartamentoChange() {
  form.ciudad = '';
  await cargarMunicipios(form.departamento);
}

watch(() => props.visible, async (val) => {
  if (!val) return;
  await cargarCatalogos();
  if (props.empresa) {
    Object.assign(form, {
      razonSocial: props.empresa.razonSocial || '',
      tipoDoc: props.empresa.tipoDoc || '',
      identificacionTributaria: props.empresa.identificacionTributaria || '',
      dv: props.empresa.dv ?? 0,
      pais: props.empresa.pais || '',
      departamento: props.empresa.departamento || '',
      ciudad: props.empresa.ciudad || '',
      direccionFisica: props.empresa.direccionFisica || '',
      sitioWeb: props.empresa.sitioWeb || '',
      estado: props.empresa.estado || 'ACTIVA',
      clasificacion: props.empresa.clasificacion || 'PROSPECTO',
      sectorCodigo: props.empresa.sectorCodigo || '',
      tamano: props.empresa.tamano || '',
      origenCodigo: props.empresa.origenCodigo || '',
      propietarioId: props.empresa.propietarioId ?? '',
      ingresosAnuales: props.empresa.ingresosAnuales ?? null,
      descripcion: props.empresa.descripcion || '',
    });
    if (form.departamento) await cargarMunicipios(form.departamento);
  } else {
    Object.assign(form, { razonSocial: '', tipoDoc: '', identificacionTributaria: '', dv: 0, pais: '', departamento: '', ciudad: '', direccionFisica: '', sitioWeb: '', estado: 'ACTIVA', clasificacion: 'PROSPECTO', sectorCodigo: '', tamano: '', origenCodigo: '', propietarioId: '', ingresosAnuales: null, descripcion: '' });
    municipios.value = [];
  }
  errors.razonSocial = ''; errors.identificacionTributaria = ''; errors.sitioWeb = '';
});

function validate() {
  errors.razonSocial = ''; errors.identificacionTributaria = ''; errors.sitioWeb = '';
  let ok = true;
  if (!form.razonSocial.trim()) { errors.razonSocial = 'La razón social es requerida'; ok = false; }
  if (form.sitioWeb && form.sitioWeb.trim()) {
    const urlRegex = /^https?:\/\/([\w-]+\.)+[\w-]+(\/[\w\-./?%&=]*)?$/;
    if (!urlRegex.test(form.sitioWeb.trim())) { errors.sitioWeb = 'Formato: https://www.ejemplo.com'; ok = false; }
  }
  return ok;
}

function handleSubmit() {
  if (!validate()) return;
  emit('submit', {
    razonSocial: form.razonSocial.trim(),
    tipoDoc: form.tipoDoc || null,
    identificacionTributaria: form.identificacionTributaria || null,
    dv: form.dv || 0,
    pais: form.pais || null,
    departamento: form.departamento || null,
    ciudad: form.ciudad || null,
    direccionFisica: form.direccionFisica || null,
    sitioWeb: form.sitioWeb?.trim() || null,
    estado: form.estado,
    clasificacion: form.clasificacion || 'PROSPECTO',
    sectorCodigo: form.sectorCodigo || null,
    tamano: form.tamano || null,
    origenCodigo: form.origenCodigo || null,
    propietarioId: form.propietarioId || null,
    ingresosAnuales: (form.ingresosAnuales === '' || form.ingresosAnuales === null) ? null : Number(form.ingresosAnuales),
    descripcion: form.descripcion?.trim() || null,
  });
}
</script>

<style scoped>
.empmodal { display: flex; flex-direction: column; gap: var(--gc-space-3); }
.empmodal__row3 { display: grid; grid-template-columns: 1fr 1fr 1fr; gap: var(--gc-space-3); }
.empmodal__field { display: flex; flex-direction: column; gap: var(--gc-space-2); }
.empmodal__label { font-size: var(--gc-fs-sm); font-weight: var(--gc-fw-medium); color: var(--gc-text-2); }
.empmodal__toggle { display: flex; gap: var(--gc-space-2); }
.empmodal__opt { flex: 1; padding: var(--gc-space-2) var(--gc-space-3); background: var(--gc-surface-2); border: 1px solid var(--gc-border); border-radius: var(--gc-radius-md); color: var(--gc-text-2); font-size: var(--gc-fs-md); }
.empmodal__opt--on { border-color: var(--gc-primary); background: var(--gc-surface); color: var(--gc-text); font-weight: var(--gc-fw-medium); }
.empmodal__error { display: flex; align-items: center; gap: var(--gc-space-2); padding: var(--gc-space-3); background: var(--gc-danger-soft); border: 1px solid var(--gc-danger); border-radius: var(--gc-radius-md); font-size: var(--gc-fs-sm); color: var(--gc-danger); }
</style>
