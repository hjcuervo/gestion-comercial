<template>
  <div class="captura">
    <h3 class="captura__title">Registrar actividad</h3>

    <div class="captura__form">
      <GcSelect
        v-model="form.tipoActividadId"
        label="Tipo de actividad"
        :options="tipoOptions"
        placeholder="Selecciona…"
        :error="errors.tipo"
      />
      <GcInput
        v-model="form.fechaHora"
        label="Fecha y hora"
        type="datetime-local"
        :error="errors.fechaHora"
      />
      <GcInput
        v-model="form.duracionMinutos"
        label="Duración (min)"
        type="number"
        placeholder="Opcional"
        mono
      />
      <GcTextarea
        v-model="form.notas"
        label="Notas"
        placeholder="¿Qué pasó en la interacción?"
        :rows="3"
      />

      <label class="captura__toggle">
        <input type="checkbox" v-model="agendar" />
        <span>Agendar compromiso a continuación</span>
      </label>

      <template v-if="agendar">
        <GcTextarea
          v-model="form.compromisoDescripcion"
          label="Compromiso"
          placeholder="Próximo paso a realizar"
          :rows="2"
          :error="errors.compromisoDescripcion"
        />
        <GcInput
          v-model="form.compromisoFecha"
          label="Fecha del compromiso"
          type="date"
          :error="errors.compromisoFecha"
        />
      </template>

      <div v-if="errorMsg" class="captura__error">
        <GcIcon name="alert-circle" :size="16" />
        <span>{{ errorMsg }}</span>
      </div>

      <GcButton variant="primary" full-width :loading="saving" @click="guardar">
        Guardar actividad
      </GcButton>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, computed } from 'vue';
import { actividadService } from '@/services/actividad.service';
import GcInput from '@/components/ui/GcInput.vue';
import GcSelect from '@/components/ui/GcSelect.vue';
import GcTextarea from '@/components/ui/GcTextarea.vue';
import GcButton from '@/components/ui/GcButton.vue';
import GcIcon from '@/components/ui/GcIcon.vue';

const props = defineProps({
  oportunidadId: { type: [String, Number], required: true },
  tipos: { type: Array, default: () => [] },
});
const emit = defineEmits(['creado']);

const tipoOptions = computed(() =>
  props.tipos.map((t) => ({ value: t.id, label: t.nombre }))
);

function nowLocal() {
  const d = new Date();
  d.setSeconds(0, 0);
  const off = d.getTimezoneOffset();
  const local = new Date(d.getTime() - off * 60000);
  return local.toISOString().slice(0, 16); // YYYY-MM-DDTHH:mm
}

function emptyForm() {
  return {
    tipoActividadId: '',
    fechaHora: nowLocal(),
    duracionMinutos: '',
    notas: '',
    compromisoDescripcion: '',
    compromisoFecha: '',
  };
}

const form = reactive(emptyForm());
const errors = reactive({ tipo: '', fechaHora: '', compromisoDescripcion: '', compromisoFecha: '' });
const agendar = ref(false);
const saving = ref(false);
const errorMsg = ref('');

function clearErrors() {
  errors.tipo = '';
  errors.fechaHora = '';
  errors.compromisoDescripcion = '';
  errors.compromisoFecha = '';
  errorMsg.value = '';
}

function validar() {
  clearErrors();
  let ok = true;
  if (!form.tipoActividadId) { errors.tipo = 'Requerido'; ok = false; }
  if (!form.fechaHora) { errors.fechaHora = 'Requerida'; ok = false; }
  if (agendar.value) {
    if (!form.compromisoDescripcion) { errors.compromisoDescripcion = 'Requerido'; ok = false; }
    if (!form.compromisoFecha) { errors.compromisoFecha = 'Requerida'; ok = false; }
  }
  return ok;
}

async function guardar() {
  if (!validar()) return;
  saving.value = true;
  try {
    const actividad = await actividadService.crear({
      oportunidadId: Number(props.oportunidadId),
      tipoActividadId: Number(form.tipoActividadId),
      fechaHora: form.fechaHora,
      duracionMinutos: form.duracionMinutos ? Number(form.duracionMinutos) : null,
      notas: form.notas || null,
    });

    if (agendar.value && form.compromisoDescripcion && form.compromisoFecha) {
      await actividadService.crearCompromiso(actividad.id, {
        descripcion: form.compromisoDescripcion,
        fechaCompromiso: form.compromisoFecha,
      });
    }

    Object.assign(form, emptyForm());
    agendar.value = false;
    emit('creado');
  } catch (err) {
    errorMsg.value = err.response?.data?.message || 'No se pudo guardar la actividad';
  } finally {
    saving.value = false;
  }
}
</script>

<style scoped>
.captura { display: flex; flex-direction: column; gap: var(--gc-space-4); padding: var(--gc-space-5); }
.captura__title { font-size: var(--gc-fs-lg); }
.captura__form { display: flex; flex-direction: column; gap: var(--gc-space-3); }
.captura__toggle {
  display: flex;
  align-items: center;
  gap: var(--gc-space-2);
  font-size: var(--gc-fs-md);
  color: var(--gc-text-2);
  cursor: pointer;
  padding: var(--gc-space-1) 0;
}
.captura__error {
  display: flex;
  align-items: center;
  gap: var(--gc-space-2);
  padding: var(--gc-space-3);
  background: var(--gc-danger-soft);
  border: 1px solid var(--gc-danger);
  border-radius: var(--gc-radius-md);
  font-size: var(--gc-fs-sm);
  color: var(--gc-danger);
}
</style>
