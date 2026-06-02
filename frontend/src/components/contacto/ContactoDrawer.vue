<template>
  <GcDrawer
    :open="open"
    :title="esEdicion ? 'Editar contacto' : 'Nuevo contacto'"
    :subtitle="subtitle"
    @close="$emit('close')"
  >
    <div class="cd-form">
      <!-- Tipo de canal (no editable en edición) -->
      <GcSelect
        v-model="form.tipoCanal"
        label="Tipo de canal"
        :options="tipoCanalOptions"
        :disabled="esEdicion"
        :error="errors.tipoCanal"
      />

      <!-- Categoría: solo para personas -->
      <GcSelect
        v-if="owner === 'persona'"
        v-model="form.categoria"
        label="Categoría"
        placeholder="Selecciona…"
        :options="categoriaOptions"
        :error="errors.categoria"
      />

      <GcInput v-model="form.etiqueta" label="Etiqueta" placeholder="Ej: Conmutador, Celular ventas…" :error="errors.etiqueta" />

      <!-- ===== Teléfono / Celular ===== -->
      <template v-if="esTelefono">
        <div class="cd-row">
          <GcSelect
            v-model="form.indicativoPais"
            label="Indicativo"
            :options="indicativoOptions"
            :error="errors.indicativoPais"
          />
          <GcInput v-model="form.valor" label="Número" placeholder="300 123 4567" mono :error="errors.valor" />
        </div>
        <GcInput v-model="form.extension" label="Extensión (opcional)" placeholder="Ej: 101" mono />
        <label class="cd-check">
          <input type="checkbox" v-model="form.esWhatsapp" />
          <span>Tiene WhatsApp</span>
        </label>
      </template>

      <!-- ===== Email ===== -->
      <template v-else-if="esEmail">
        <GcInput v-model="form.valor" label="Correo electrónico" type="email" placeholder="nombre@dominio.com" :error="errors.valor" />
        <label class="cd-check">
          <input type="checkbox" v-model="form.enviable" />
          <span>Habilitado para envío de correo</span>
        </label>
      </template>

      <!-- ===== Red social ===== -->
      <template v-else-if="esRedSocial">
        <GcSelect
          v-model="form.redSocialCodigo"
          label="Red social"
          placeholder="Selecciona…"
          :options="redSocialOptions"
          :error="errors.redSocialCodigo"
        />
        <GcInput v-model="form.valor" label="Usuario / nombre visible" placeholder="@usuario o nombre" :error="errors.valor" />
        <GcInput v-model="form.url" label="Enlace" placeholder="https://…" mono :error="errors.url" />
      </template>

      <!-- Principal + observaciones -->
      <label class="cd-check">
        <input type="checkbox" v-model="form.esPrincipal" />
        <span>Marcar como principal de su tipo</span>
      </label>

      <GcTextarea v-model="form.observaciones" label="Observaciones (opcional)" :rows="2" />

      <p v-if="serverError" class="cd-error">{{ serverError }}</p>
    </div>

    <template #footer>
      <GcButton variant="ghost" size="sm" @click="$emit('close')">Cancelar</GcButton>
      <GcButton variant="primary" size="sm" :loading="saving" @click="submit">
        {{ esEdicion ? 'Guardar' : 'Agregar' }}
      </GcButton>
    </template>
  </GcDrawer>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue';
import GcDrawer from '@/components/ui/GcDrawer.vue';
import GcButton from '@/components/ui/GcButton.vue';
import GcInput from '@/components/ui/GcInput.vue';
import GcSelect from '@/components/ui/GcSelect.vue';
import GcTextarea from '@/components/ui/GcTextarea.vue';

const props = defineProps({
  open: { type: Boolean, default: false },
  owner: { type: String, required: true }, // 'empresa' | 'persona'
  ownerNombre: { type: String, default: '' },
  contacto: { type: Object, default: null }, // si viene, es edición
  redes: { type: Array, default: () => [] }, // catálogo de redes sociales
  saving: { type: Boolean, default: false },
  serverError: { type: String, default: '' },
});

const emit = defineEmits(['close', 'submit']);

const esEdicion = computed(() => !!props.contacto?.id);
const subtitle = computed(() => props.ownerNombre || (props.owner === 'persona' ? 'Persona' : 'Empresa'));

const tipoCanalOptions = [
  { value: 'TELEFONO', label: 'Teléfono fijo' },
  { value: 'CELULAR', label: 'Celular' },
  { value: 'EMAIL', label: 'Correo electrónico' },
  { value: 'RED_SOCIAL', label: 'Red social' },
];
const categoriaOptions = [
  { value: 'PERSONAL', label: 'Personal' },
  { value: 'EMPRESARIAL', label: 'Empresarial' },
  { value: 'OTRO', label: 'Otro' },
];
// Indicativos frecuentes; el backend acepta cualquiera vía libphonenumber.
const indicativoOptions = [
  { value: '+57', label: '🇨🇴 +57' },
  { value: '+1', label: '🇺🇸 +1' },
  { value: '+52', label: '🇲🇽 +52' },
  { value: '+51', label: '🇵🇪 +51' },
  { value: '+593', label: '🇪🇨 +593' },
  { value: '+34', label: '🇪🇸 +34' },
];

const redSocialOptions = computed(() =>
  props.redes.map((r) => ({ value: r.codigo, label: r.nombre }))
);

const esTelefono = computed(() => form.tipoCanal === 'TELEFONO' || form.tipoCanal === 'CELULAR');
const esEmail = computed(() => form.tipoCanal === 'EMAIL');
const esRedSocial = computed(() => form.tipoCanal === 'RED_SOCIAL');

const form = reactive(emptyForm());
const errors = reactive({});

function emptyForm() {
  return {
    tipoCanal: 'CELULAR',
    categoria: '',
    etiqueta: '',
    valor: '',
    indicativoPais: '+57',
    extension: '',
    esWhatsapp: false,
    enviable: true,
    redSocialCodigo: '',
    url: '',
    esPrincipal: false,
    observaciones: '',
  };
}

function clearErrors() {
  Object.keys(errors).forEach((k) => delete errors[k]);
}

// Carga el contacto al abrir (edición) o resetea (alta)
watch(
  () => [props.open, props.contacto],
  () => {
    clearErrors();
    if (!props.open) return;
    const base = emptyForm();
    if (props.contacto) {
      Object.assign(base, {
        tipoCanal: props.contacto.tipoCanal ?? base.tipoCanal,
        categoria: props.contacto.categoria ?? '',
        etiqueta: props.contacto.etiqueta ?? '',
        valor: props.contacto.valor ?? '',
        indicativoPais: props.contacto.indicativoPais ?? '+57',
        extension: props.contacto.extension ?? '',
        esWhatsapp: !!props.contacto.esWhatsapp,
        enviable: props.contacto.enviable !== false,
        redSocialCodigo: props.contacto.redSocialCodigo ?? '',
        url: props.contacto.url ?? '',
        esPrincipal: !!props.contacto.esPrincipal,
        observaciones: props.contacto.observaciones ?? '',
      });
    }
    Object.assign(form, base);
  },
  { immediate: true }
);

function validar() {
  clearErrors();
  if (!form.valor || !form.valor.trim()) errors.valor = 'Requerido';
  if (props.owner === 'persona' && !form.categoria) errors.categoria = 'Requerida';
  if (esTelefono.value && !form.indicativoPais) errors.indicativoPais = 'Requerido';
  if (esRedSocial.value) {
    if (!form.redSocialCodigo) errors.redSocialCodigo = 'Selecciona una red';
    if (!form.url || !/^https?:\/\//.test(form.url.trim())) errors.url = 'Enlace http(s) válido';
  }
  return Object.keys(errors).length === 0;
}

function submit() {
  if (!validar()) return;
  // Construir payload limpio según el tipo de canal
  const payload = {
    tipoCanal: form.tipoCanal,
    etiqueta: form.etiqueta?.trim() || null,
    valor: form.valor?.trim(),
    esPrincipal: form.esPrincipal,
    observaciones: form.observaciones?.trim() || null,
  };
  if (props.owner === 'persona') payload.categoria = form.categoria;
  if (esTelefono.value) {
    payload.indicativoPais = form.indicativoPais;
    payload.extension = form.extension?.trim() || null;
    payload.esWhatsapp = form.esWhatsapp;
  } else if (esEmail.value) {
    payload.enviable = form.enviable;
  } else if (esRedSocial.value) {
    payload.redSocialCodigo = form.redSocialCodigo;
    payload.url = form.url?.trim();
  }
  emit('submit', { id: props.contacto?.id || null, payload });
}
</script>

<style scoped>
.cd-form { display: flex; flex-direction: column; gap: var(--gc-space-4); }
.cd-row { display: grid; grid-template-columns: 110px 1fr; gap: var(--gc-space-3); }
.cd-check { display: flex; align-items: center; gap: var(--gc-space-2); font-size: var(--gc-fs-sm); color: var(--gc-text-2); cursor: pointer; }
.cd-check input { accent-color: var(--gc-primary); width: 15px; height: 15px; }
.cd-error { font-size: var(--gc-fs-sm); color: var(--gc-danger); margin: 0; }
</style>
