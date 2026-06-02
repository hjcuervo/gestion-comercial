<template>
  <!-- Lista maestra -->
  <Teleport to="#gc-shell-master">
    <div class="emp-master">
      <div class="emp-master__filters">
        <GcInput v-model="search" placeholder="Buscar empresa…" icon="search" @update:modelValue="debouncedReload" />
        <GcButton variant="primary" size="sm" icon="plus" full-width @click="openCreate">Nueva empresa</GcButton>
      </div>
      <div v-if="loading" class="emp-master__state"><GcSpinner :size="20" /></div>
      <GcEmpty v-else-if="!empresas.length" icon="building-off" message="Sin empresas" />
      <div v-else>
        <GcListRow
          v-for="e in empresas"
          :key="e.id"
          :tone="e.estado === 'ACTIVA' ? 'success' : 'neutral'"
          clickable
          :active="String(e.id) === String(selectedId)"
          @click="select(e.id)"
        >
          <div class="emp-master__row">
            <span class="emp-master__name">{{ e.razonSocial }}</span>
            <span class="emp-master__meta gc-mono">{{ e.identificacionTributaria || '—' }}</span>
          </div>
        </GcListRow>
      </div>
    </div>
  </Teleport>

  <!-- Detalle -->
  <div class="emp-surface">
    <div v-if="!selectedId" class="emp-empty"><GcEmpty icon="hand-click" message="Selecciona una empresa de la izquierda" /></div>
    <div v-else-if="loadingDetalle" class="emp-empty"><GcSpinner :size="24" /></div>
    <template v-else-if="empresa">
      <!-- Contactos / oportunidades en el aside -->
      <Teleport to="#gc-shell-aside">
        <div v-if="asideReady" class="emp-aside">
          <div class="emp-aside__sec">
            <h3 class="emp-aside__title">Contactos</h3>
            <GcEmpty v-if="!contactos.length" icon="user-off" message="Sin contactos" />
            <div v-else class="emp-aside__list">
              <GcListRow v-for="p in contactos" :key="p.id" tone="info" clickable @click="$router.push(`/personas/${p.id}`)">
                <div class="emp-aside__row">
                  <span class="emp-aside__rowname">{{ p.nombreCompleto }}</span>
                  <span class="emp-aside__rowmeta">{{ p.email || '—' }}</span>
                </div>
              </GcListRow>
            </div>
          </div>
          <div class="emp-aside__sec">
            <h3 class="emp-aside__title">Oportunidades</h3>
            <GcEmpty v-if="!oportunidades.length" icon="briefcase-off" message="Sin oportunidades" />
            <div v-else class="emp-aside__list">
              <GcListRow v-for="o in oportunidades" :key="o.id" :tone="oppTone(o.estadoMacro)" clickable @click="$router.push(`/oportunidades/${o.id}`)">
                <div class="emp-aside__row">
                  <span class="emp-aside__rowname">{{ o.nombre }}</span>
                  <span class="emp-aside__rowmeta gc-mono">{{ fmtCurrency(o.valorEstimado, o.moneda) }}</span>
                </div>
              </GcListRow>
            </div>
          </div>
        </div>
      </Teleport>

      <header class="emp-head">
        <div class="emp-heading">
          <h1 class="emp-title">{{ empresa.razonSocial }}</h1>
          <GcBadge :tone="empresa.estado === 'ACTIVA' ? 'success' : 'neutral'" :label="empresa.estado === 'ACTIVA' ? 'Activa' : 'Inactiva'" />
        </div>
        <GcButton variant="default" size="sm" icon="edit" @click="openEdit">Editar</GcButton>
      </header>

      <div class="emp-grid">
        <div class="emp-field"><span class="emp-label">Identificación</span><span class="emp-value gc-mono">{{ empresa.tipoDoc || '' }} {{ empresa.identificacionTributaria || '—' }}<template v-if="empresa.dv">-{{ empresa.dv }}</template></span></div>
        <div class="emp-field"><span class="emp-label">País</span><span class="emp-value">{{ empresa.paisNombre || empresa.pais || '—' }}</span></div>
        <div class="emp-field"><span class="emp-label">Departamento</span><span class="emp-value">{{ empresa.departamentoNombre || empresa.departamento || '—' }}</span></div>
        <div class="emp-field"><span class="emp-label">Ciudad</span><span class="emp-value">{{ empresa.ciudadNombre || empresa.ciudad || '—' }}</span></div>
        <div class="emp-field"><span class="emp-label">Dirección</span><span class="emp-value">{{ empresa.direccionFisica || '—' }}</span></div>
        <div class="emp-field"><span class="emp-label">Sitio web</span><a v-if="empresa.sitioWeb" :href="empresa.sitioWeb" target="_blank" rel="noopener" class="emp-link">{{ empresa.sitioWeb }}</a><span v-else class="emp-value">—</span></div>
      </div>
    </template>

    <EmpresaModal :visible="showModal" :empresa="editing" :saving="saving" :error="modalError" @close="showModal = false" @submit="handleSubmit" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, nextTick } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useShell } from '@/composables/useShell';
import { empresaService } from '@/services/empresa.service';
import { personaService } from '@/services/persona.service';
import { oportunidadService } from '@/services/oportunidad.service';
import { formatCurrency } from '@/utils/currency';
import EmpresaModal from '@/components/empresa/EmpresaModal.vue';
import GcInput from '@/components/ui/GcInput.vue';
import GcButton from '@/components/ui/GcButton.vue';
import GcBadge from '@/components/ui/GcBadge.vue';
import GcListRow from '@/components/ui/GcListRow.vue';
import GcEmpty from '@/components/ui/GcEmpty.vue';
import GcSpinner from '@/components/ui/GcSpinner.vue';

const route = useRoute();
const router = useRouter();
const { setRegions } = useShell();
setRegions({ master: true, aside: true });

const empresas = ref([]);
const loading = ref(true);
const search = ref('');
let timer = null;

const empresa = ref(null);
const contactos = ref([]);
const oportunidades = ref([]);
const loadingDetalle = ref(false);
const asideReady = ref(false);

const showModal = ref(false);
const editing = ref(null);
const saving = ref(false);
const modalError = ref(null);

const selectedId = computed(() => route.params.id || null);

function fmtCurrency(v, m) { return formatCurrency(v, m); }
const OPP_TONE = { ABIERTA: 'info', SEGUIMIENTO: 'warning', GANADA: 'success', CONTRATADA: 'accent', PERDIDA: 'danger', NO_CONCRETADA: 'neutral' };
function oppTone(e) { return OPP_TONE[e] || 'neutral'; }

async function reload() {
  loading.value = true;
  try {
    const params = { page: 1, page_size: 50 };
    if (search.value) params.q = search.value;
    const res = await empresaService.listar(params);
    empresas.value = res.data || [];
  } catch (err) { console.error('Error cargando empresas:', err); empresas.value = []; }
  finally { loading.value = false; }
}
function debouncedReload() { clearTimeout(timer); timer = setTimeout(reload, 400); }

function select(id) { router.push(`/empresas/${id}`); }

async function cargarDetalle() {
  if (!selectedId.value) { empresa.value = null; return; }
  loadingDetalle.value = true;
  try {
    empresa.value = await empresaService.obtenerPorId(selectedId.value);
    const [pers, opps] = await Promise.all([
      personaService.listar({ empresa_id: selectedId.value, page_size: 100 }).catch(() => ({ data: [] })),
      oportunidadService.listar({ empresa_id: selectedId.value, page_size: 100 }).catch(() => ({ data: [] })),
    ]);
    contactos.value = pers.data || [];
    oportunidades.value = opps.data || [];
  } catch (err) { console.error('Error cargando detalle empresa:', err); }
  finally { loadingDetalle.value = false; }
}

function openCreate() { editing.value = null; modalError.value = null; showModal.value = true; }
function openEdit() { editing.value = { ...empresa.value }; modalError.value = null; showModal.value = true; }
async function handleSubmit(payload) {
  saving.value = true; modalError.value = null;
  try {
    if (editing.value) await empresaService.actualizar(editing.value.id, payload);
    else await empresaService.crear(payload);
    showModal.value = false;
    await reload();
    if (editing.value) await cargarDetalle();
  } catch (err) { modalError.value = err.response?.data?.message || 'Error al guardar empresa'; }
  finally { saving.value = false; }
}

onMounted(async () => {
  await nextTick();
  asideReady.value = true;
  await reload();
  if (selectedId.value) await cargarDetalle();
});
watch(selectedId, () => cargarDetalle());
</script>

<style scoped>
.emp-master { display: flex; flex-direction: column; }
.emp-master__filters { display: flex; flex-direction: column; gap: var(--gc-space-2); padding: var(--gc-space-3); border-bottom: 1px solid var(--gc-border); }
.emp-master__state { display: flex; justify-content: center; padding: var(--gc-space-8); color: var(--gc-text-3); }
.emp-master__row { display: flex; flex-direction: column; gap: 2px; min-width: 0; }
.emp-master__name { font-size: var(--gc-fs-md); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.emp-master__meta { font-size: var(--gc-fs-xs); color: var(--gc-text-3); }

.emp-surface { padding: var(--gc-space-6); }
.emp-empty { display: flex; justify-content: center; padding: var(--gc-space-12); }
.emp-head { display: flex; align-items: center; justify-content: space-between; margin-bottom: var(--gc-space-2); }
.emp-heading { display: flex; align-items: center; gap: var(--gc-space-3); }
.emp-title { font-size: var(--gc-fs-xl); }
.emp-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: var(--gc-space-4); margin-top: var(--gc-space-5); }
.emp-field { display: flex; flex-direction: column; gap: 2px; }
.emp-label { font-size: var(--gc-fs-xs); text-transform: uppercase; letter-spacing: 0.04em; color: var(--gc-text-3); }
.emp-value { font-size: var(--gc-fs-md); color: var(--gc-text); }
.emp-link { font-size: var(--gc-fs-md); color: var(--gc-info); }
.emp-link:hover { text-decoration: underline; }

.emp-aside { padding: var(--gc-space-5); display: flex; flex-direction: column; gap: var(--gc-space-5); }
.emp-aside__sec { display: flex; flex-direction: column; gap: var(--gc-space-2); }
.emp-aside__title { font-size: var(--gc-fs-xs); text-transform: uppercase; letter-spacing: 0.06em; color: var(--gc-text-3); }
.emp-aside__list { border: 1px solid var(--gc-border); border-radius: var(--gc-radius-lg); overflow: hidden; }
.emp-aside__list > :deep(.gc-row:last-child) { border-bottom: none; }
.emp-aside__row { display: flex; flex-direction: column; gap: 2px; min-width: 0; }
.emp-aside__rowname { font-size: var(--gc-fs-md); }
.emp-aside__rowmeta { font-size: var(--gc-fs-xs); color: var(--gc-text-3); }
</style>
