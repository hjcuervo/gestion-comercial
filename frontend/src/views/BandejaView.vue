<script setup>
import { onMounted, onUnmounted, ref } from 'vue';
import { useShell } from '@/composables/useShell';
import { formatDateTime } from '@/utils/datetime';
import accionService from '@/services/accion.service';
import GcListRow from '@/components/ui/GcListRow.vue';
import GcBadge from '@/components/ui/GcBadge.vue';
import GcButton from '@/components/ui/GcButton.vue';
import GcIcon from '@/components/ui/GcIcon.vue';
import GcModal from '@/components/ui/GcModal.vue';
import GcTextarea from '@/components/ui/GcTextarea.vue';
import GcInput from '@/components/ui/GcInput.vue';
import GcEmpty from '@/components/ui/GcEmpty.vue';
import GcSpinner from '@/components/ui/GcSpinner.vue';

const { setRegions } = useShell();

const TOPE_VISIBLE = 50;

const CANALES = {
  LLAMADA:         { icon: 'phone',          label: 'Llamada' },
  EMAIL:           { icon: 'mail',           label: 'Email' },
  WHATSAPP:        { icon: 'brand-whatsapp', label: 'WhatsApp' },
  RED_SOCIAL:      { icon: 'share',          label: 'Red social' },
  REUNION:         { icon: 'users',          label: 'Reunión' },
  ENVIO_PROPUESTA: { icon: 'file-text',      label: 'Enviar propuesta' },
  TAREA_MANUAL:    { icon: 'checklist',      label: 'Tarea' },
};
const REFERENCIAS = {
  CONTACTO_PERSONA: 'Persona',
  CONTACTO_EMPRESA: 'Empresa',
  OPORTUNIDAD: 'Oportunidad',
};
const PRIORIDAD_TONE = { ALTA: 'warning', MEDIA: 'neutral', BAJA: 'neutral' };

const cola = ref([]);
const totalDisponibles = ref(0);
const loading = ref(false);
const error = ref(null);

const seleccionada = ref(null);
const disposiciones = ref([]);
const cargandoDisp = ref(false);
const accionando = ref(false);

const modalPosponer = ref(false);
const motivoPosponer = ref('');
const fechaPosponer = ref('');
const modalOmitir = ref(false);
const motivoOmitir = ref('');

onMounted(() => {
  setRegions({ master: true, aside: false });
  cargarCola();
});
onUnmounted(() => setRegions({ master: false, aside: false }));

async function cargarCola() {
  loading.value = true;
  error.value = null;
  try {
    const res = await accionService.obtenerCola(TOPE_VISIBLE);
    cola.value = res.data || [];
    totalDisponibles.value = res.pagination ? res.pagination.totalItems : cola.value.length;
    const sigueLaActual = seleccionada.value && cola.value.find((a) => a.id === seleccionada.value.id);
    if (sigueLaActual) {
      seleccionar(sigueLaActual);
    } else if (cola.value.length) {
      seleccionar(cola.value[0]);
    } else {
      seleccionada.value = null;
      disposiciones.value = [];
    }
  } catch (e) {
    error.value = 'No se pudo cargar la cola.';
  } finally {
    loading.value = false;
  }
}

function seleccionar(accion) {
  seleccionada.value = accion;
  cargarDisposiciones(accion.origen);
}

async function cargarDisposiciones(origen) {
  cargandoDisp.value = true;
  try {
    disposiciones.value = await accionService.listarDisposiciones(origen);
  } catch (e) {
    disposiciones.value = [];
  } finally {
    cargandoDisp.value = false;
  }
}

async function completar(disposicion) {
  if (!seleccionada.value) return;
  accionando.value = true;
  error.value = null;
  try {
    await accionService.completar(seleccionada.value.id, disposicion.id);
    await cargarCola(); // auto-avanza a la siguiente
  } catch (e) {
    error.value = 'No se pudo completar la acción.';
  } finally {
    accionando.value = false;
  }
}

async function confirmarPosponer() {
  if (!seleccionada.value || !motivoPosponer.value) return;
  accionando.value = true;
  error.value = null;
  try {
    await accionService.posponer(seleccionada.value.id, motivoPosponer.value, fechaPosponer.value || null);
    modalPosponer.value = false;
    motivoPosponer.value = '';
    fechaPosponer.value = '';
    await cargarCola();
  } catch (e) {
    error.value = 'No se pudo posponer la acción.';
  } finally {
    accionando.value = false;
  }
}

async function confirmarOmitir() {
  if (!seleccionada.value || !motivoOmitir.value) return;
  accionando.value = true;
  error.value = null;
  try {
    await accionService.omitir(seleccionada.value.id, motivoOmitir.value);
    modalOmitir.value = false;
    motivoOmitir.value = '';
    await cargarCola();
  } catch (e) {
    error.value = 'No se pudo omitir la acción.';
  } finally {
    accionando.value = false;
  }
}

function canal(tipo) {
  return CANALES[tipo] || { icon: 'circle', label: tipo };
}
function referenciaLabel(a) {
  // TODO: mostrar el nombre real de la entidad (requiere enriquecer AccionResponse en backend).
  return `${REFERENCIAS[a.referenciaTipo] || a.referenciaTipo} #${a.referenciaId}`;
}
function esVencida(a) {
  return a.vencimiento && new Date(a.vencimiento) < new Date();
}
function enEspera() {
  return Math.max(0, totalDisponibles.value - cola.value.length);
}
</script>

<template>
  <!-- Lista maestra: la cola "Hoy" -->
  <Teleport to="#gc-shell-master">
    <div class="bandeja-master">
      <header class="bandeja-master__head">
        <span class="bandeja-master__titulo">Hoy</span>
        <span class="gc-mono bandeja-master__count">{{ cola.length }}</span>
      </header>

      <GcSpinner v-if="loading" :size="20" />
      <GcEmpty v-else-if="!cola.length" icon="inbox" message="No hay acciones en tu cola." />

      <template v-else>
        <GcListRow
          v-for="a in cola"
          :key="a.id"
          :tone="esVencida(a) ? 'danger' : 'neutral'"
          clickable
          :active="seleccionada && seleccionada.id === a.id"
          @click="seleccionar(a)"
        >
          <template #lead><GcIcon :name="canal(a.tipoAccion).icon" /></template>
          <div class="bandeja-row">
            <span class="bandeja-row__titulo">{{ canal(a.tipoAccion).label }}</span>
            <span class="gc-mono bandeja-row__ref">{{ referenciaLabel(a) }}</span>
          </div>
          <template #actions>
            <span class="gc-mono bandeja-row__venc" :class="{ 'is-vencida': esVencida(a) }">
              {{ a.vencimiento ? formatDateTime(a.vencimiento) : '—' }}
            </span>
          </template>
        </GcListRow>

        <p v-if="enEspera()" class="bandeja-master__espera">
          <span class="gc-mono">{{ enEspera() }}</span> en espera
        </p>
      </template>
    </div>
  </Teleport>

  <!-- Superficie: ejecución de la acción actual -->
  <div class="bandeja-surface">
    <p v-if="error" class="bandeja-error">{{ error }}</p>

    <GcEmpty
      v-if="!seleccionada"
      icon="checklist"
      message="Sin acciones pendientes. Cuando llegue trabajo, aparecerá aquí."
    />

    <div v-else class="ejecucion">
      <header class="ejecucion__head">
        <GcIcon :name="canal(seleccionada.tipoAccion).icon" :size="20" />
        <h2 class="ejecucion__titulo">{{ canal(seleccionada.tipoAccion).label }}</h2>
        <GcBadge :tone="PRIORIDAD_TONE[seleccionada.prioridad] || 'neutral'" soft>
          {{ seleccionada.prioridad }}
        </GcBadge>
      </header>

      <dl class="ejecucion__datos">
        <div><dt>Referencia</dt><dd class="gc-mono">{{ referenciaLabel(seleccionada) }}</dd></div>
        <div>
          <dt>Vence</dt>
          <dd class="gc-mono" :class="{ 'is-vencida': esVencida(seleccionada) }">
            {{ seleccionada.vencimiento ? formatDateTime(seleccionada.vencimiento) : '—' }}
          </dd>
        </div>
      </dl>

      <!-- Disposiciones: el camino rápido -->
      <section class="ejecucion__disposiciones">
        <span class="ejecucion__label">¿Cómo te fue?</span>
        <GcSpinner v-if="cargandoDisp" :size="16" />
        <GcEmpty
          v-else-if="!disposiciones.length"
          icon="alert-circle"
          message="No hay disposiciones configuradas para este tipo de acción."
        />
        <div v-else class="ejecucion__botones">
          <GcButton
            v-for="d in disposiciones"
            :key="d.id"
            variant="primary"
            :disabled="accionando"
            @click="completar(d)"
          >
            {{ d.nombre }}
          </GcButton>
        </div>
      </section>

      <footer class="ejecucion__secundarias">
        <GcButton variant="default" :disabled="accionando" @click="modalPosponer = true">Posponer</GcButton>
        <GcButton variant="ghost" :disabled="accionando" @click="modalOmitir = true">Omitir</GcButton>
      </footer>
    </div>
  </div>

  <!-- Posponer -->
  <GcModal :open="modalPosponer" title="Posponer acción" @close="modalPosponer = false">
    <GcTextarea v-model="motivoPosponer" label="Motivo" placeholder="Por qué se pospone (obligatorio)" />
    <GcInput v-model="fechaPosponer" type="datetime-local" label="Disponible desde (opcional)" />
    <template #footer>
      <GcButton variant="default" @click="modalPosponer = false">Cancelar</GcButton>
      <GcButton variant="primary" :disabled="!motivoPosponer || accionando" @click="confirmarPosponer">
        Posponer
      </GcButton>
    </template>
  </GcModal>

  <!-- Omitir -->
  <GcModal :open="modalOmitir" title="Omitir acción" @close="modalOmitir = false">
    <GcTextarea v-model="motivoOmitir" label="Motivo" placeholder="Por qué no aplica (obligatorio)" />
    <template #footer>
      <GcButton variant="default" @click="modalOmitir = false">Cancelar</GcButton>
      <GcButton variant="danger" :disabled="!motivoOmitir || accionando" @click="confirmarOmitir">
        Omitir
      </GcButton>
    </template>
  </GcModal>
</template>

<style scoped>
.bandeja-master {
  display: flex;
  flex-direction: column;
}
.bandeja-master__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--gc-space-3) var(--gc-space-4);
  border-bottom: 1px solid var(--gc-border);
}
.bandeja-master__titulo {
  font-weight: var(--gc-fw-medium);
  color: var(--gc-text);
}
.bandeja-master__count {
  color: var(--gc-text-2);
  font-size: var(--gc-fs-sm);
}
.bandeja-master__espera {
  padding: var(--gc-space-3) var(--gc-space-4);
  color: var(--gc-text-3);
  font-size: var(--gc-fs-sm);
}

.bandeja-row {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}
.bandeja-row__titulo {
  color: var(--gc-text);
}
.bandeja-row__ref {
  color: var(--gc-text-2);
  font-size: var(--gc-fs-sm);
}
.bandeja-row__venc {
  color: var(--gc-text-3);
  font-size: var(--gc-fs-xs);
}
.is-vencida {
  color: var(--gc-danger);
}

.bandeja-surface {
  max-width: 640px;
  margin: 0 auto;
  padding: var(--gc-space-6) var(--gc-space-4);
}
.bandeja-error {
  color: var(--gc-danger);
  margin-bottom: var(--gc-space-4);
}

.ejecucion__head {
  display: flex;
  align-items: center;
  gap: var(--gc-space-3);
  margin-bottom: var(--gc-space-5);
}
.ejecucion__titulo {
  font-size: var(--gc-fs-xl);
  font-weight: var(--gc-fw-medium);
  margin: 0;
}
.ejecucion__datos {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--gc-space-4);
  padding: var(--gc-space-4) 0;
  border-top: 1px solid var(--gc-border);
  border-bottom: 1px solid var(--gc-border);
  margin-bottom: var(--gc-space-5);
}
.ejecucion__datos dt {
  color: var(--gc-text-3);
  font-size: var(--gc-fs-xs);
  text-transform: uppercase;
  letter-spacing: 0.04em;
  margin-bottom: 2px;
}
.ejecucion__datos dd {
  margin: 0;
  color: var(--gc-text);
}
.ejecucion__label {
  display: block;
  color: var(--gc-text-2);
  margin-bottom: var(--gc-space-3);
}
.ejecucion__botones {
  display: flex;
  flex-wrap: wrap;
  gap: var(--gc-space-2);
}
.ejecucion__secundarias {
  display: flex;
  gap: var(--gc-space-2);
  margin-top: var(--gc-space-6);
  padding-top: var(--gc-space-4);
  border-top: 1px solid var(--gc-border);
}
</style>
