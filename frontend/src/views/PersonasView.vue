<template>
  <!-- Lista maestra -->
  <Teleport to="#gc-shell-master">
    <div class="per-master">
      <div class="per-master__filters">
        <GcInput v-model="search" placeholder="Buscar persona…" icon="search" @update:modelValue="debouncedReload" />
        <GcButton variant="primary" size="sm" icon="plus" full-width @click="openCreate">Nueva persona</GcButton>
      </div>
      <div v-if="loading" class="per-master__state"><GcSpinner :size="20" /></div>
      <GcEmpty v-else-if="!personas.length" icon="user-off" message="Sin personas" />
      <div v-else>
        <GcListRow
          v-for="p in personas"
          :key="p.id"
          :tone="p.activo !== false ? 'success' : 'neutral'"
          clickable
          :active="String(p.id) === String(selectedId)"
          @click="select(p.id)"
        >
          <div class="per-master__row">
            <span class="per-master__name">{{ p.nombreCompleto || (p.nombres + ' ' + p.apellidos) }}</span>
            <span class="per-master__meta">{{ p.email || '—' }}</span>
          </div>
        </GcListRow>
      </div>
    </div>
  </Teleport>

  <!-- Detalle -->
  <div class="per-surface">
    <div v-if="!selectedId" class="per-empty"><GcEmpty icon="hand-click" message="Selecciona una persona de la izquierda" /></div>
    <div v-else-if="loadingDetalle" class="per-empty"><GcSpinner :size="24" /></div>
    <template v-else-if="persona">
      <!-- Empresas asociadas en el aside -->
      <Teleport to="#gc-shell-aside">
        <div v-if="asideReady" class="per-aside">
          <div class="per-aside__head">
            <h3 class="per-aside__title">Empresas asociadas</h3>
            <GcButton variant="default" size="sm" icon="plus" @click="showAsociar = true">Asociar</GcButton>
          </div>
          <GcEmpty v-if="!persona.empresas?.length" icon="building-off" message="Sin empresas asociadas" />
          <div v-else class="per-aside__list">
            <GcListRow v-for="pe in persona.empresas" :key="pe.id" :tone="pe.esContactoPrincipal ? 'accent' : 'neutral'">
              <div class="per-aside__row">
                <span class="per-aside__rowname">{{ pe.empresaRazonSocial }}</span>
                <span class="per-aside__rowmeta">
                  {{ pe.cargo || '—' }}<template v-if="pe.rolContacto"> · {{ rolLabel(pe.rolContacto) }}</template>
                  <template v-if="pe.esContactoPrincipal"> · principal</template>
                </span>
              </div>
              <template #actions>
                <GcButton variant="ghost" size="sm" icon="unlink" @click="desasociar(pe.empresaId)" />
              </template>
            </GcListRow>
          </div>
        </div>
      </Teleport>

      <header class="per-head">
        <div class="per-heading">
          <h1 class="per-title">{{ persona.nombreCompleto || (persona.nombres + ' ' + persona.apellidos) }}</h1>
          <GcBadge :tone="persona.activo !== false ? 'success' : 'neutral'" :label="persona.activo !== false ? 'Activo' : 'Inactivo'" />
        </div>
        <GcButton variant="default" size="sm" icon="edit" @click="openEdit">Editar</GcButton>
      </header>

      <div class="per-grid">
        <div class="per-field"><span class="per-label">Email</span><span class="per-value">{{ persona.email || '—' }}</span></div>
        <div class="per-field"><span class="per-label">Teléfono</span><span class="per-value gc-mono">{{ persona.telefono || '—' }}</span></div>
        <div class="per-field"><span class="per-label">Empresas</span><span class="per-value gc-mono">{{ persona.empresas?.length || 0 }}</span></div>
      </div>
    </template>

    <PersonaModal :visible="showModal" :persona="editing" :saving="saving" :error="modalError" @close="showModal = false" @submit="handleSubmit" />
    <AsociarEmpresaModal :visible="showAsociar" :saving="savingAsoc" :error="asocError" @close="showAsociar = false" @submit="handleAsociar" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, nextTick } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useShell } from '@/composables/useShell';
import { personaService } from '@/services/persona.service';
import PersonaModal from '@/components/persona/PersonaModal.vue';
import AsociarEmpresaModal from '@/components/persona/AsociarEmpresaModal.vue';
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

const personas = ref([]);
const loading = ref(true);
const search = ref('');
let timer = null;

const persona = ref(null);
const loadingDetalle = ref(false);
const asideReady = ref(false);

const showModal = ref(false);
const editing = ref(null);
const saving = ref(false);
const modalError = ref(null);

const showAsociar = ref(false);
const savingAsoc = ref(false);
const asocError = ref(null);

const selectedId = computed(() => route.params.id || null);

const ROL = { DECISOR: 'Decisor', INFLUENCIADOR: 'Influenciador', TECNICO: 'Técnico', USUARIO: 'Usuario', ADMINISTRATIVO: 'Administrativo' };
function rolLabel(r) { return ROL[r] || r; }

async function reload() {
  loading.value = true;
  try {
    const params = { page: 1, page_size: 50 };
    if (search.value) params.q = search.value;
    const res = await personaService.listar(params);
    personas.value = res.data || [];
  } catch (err) { console.error('Error cargando personas:', err); personas.value = []; }
  finally { loading.value = false; }
}
function debouncedReload() { clearTimeout(timer); timer = setTimeout(reload, 400); }
function select(id) { router.push(`/personas/${id}`); }

async function cargarDetalle() {
  if (!selectedId.value) { persona.value = null; return; }
  loadingDetalle.value = true;
  try { persona.value = await personaService.obtenerPorId(selectedId.value); }
  catch (err) { console.error('Error cargando persona:', err); }
  finally { loadingDetalle.value = false; }
}

function openCreate() { editing.value = null; modalError.value = null; showModal.value = true; }
function openEdit() { editing.value = { ...persona.value }; modalError.value = null; showModal.value = true; }
async function handleSubmit(payload) {
  saving.value = true; modalError.value = null;
  try {
    if (editing.value) await personaService.actualizar(editing.value.id, payload);
    else await personaService.crear(payload);
    showModal.value = false;
    await reload();
    if (editing.value) await cargarDetalle();
  } catch (err) { modalError.value = err.response?.data?.message || 'Error al guardar persona'; }
  finally { saving.value = false; }
}

async function handleAsociar(payload) {
  savingAsoc.value = true; asocError.value = null;
  try { await personaService.asociarEmpresa(persona.value.id, payload); showAsociar.value = false; await cargarDetalle(); }
  catch (err) { asocError.value = err.response?.data?.message || 'Error al asociar empresa'; }
  finally { savingAsoc.value = false; }
}
async function desasociar(empresaId) {
  if (!confirm('¿Desasociar esta empresa?')) return;
  try { await personaService.desasociarEmpresa(persona.value.id, empresaId); await cargarDetalle(); }
  catch (err) { console.error('Error al desasociar:', err); }
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
.per-master { display: flex; flex-direction: column; }
.per-master__filters { display: flex; flex-direction: column; gap: var(--gc-space-2); padding: var(--gc-space-3); border-bottom: 1px solid var(--gc-border); }
.per-master__state { display: flex; justify-content: center; padding: var(--gc-space-8); color: var(--gc-text-3); }
.per-master__row { display: flex; flex-direction: column; gap: 2px; min-width: 0; }
.per-master__name { font-size: var(--gc-fs-md); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.per-master__meta { font-size: var(--gc-fs-xs); color: var(--gc-text-3); }

.per-surface { padding: var(--gc-space-6); }
.per-empty { display: flex; justify-content: center; padding: var(--gc-space-12); }
.per-head { display: flex; align-items: center; justify-content: space-between; margin-bottom: var(--gc-space-2); }
.per-heading { display: flex; align-items: center; gap: var(--gc-space-3); }
.per-title { font-size: var(--gc-fs-xl); }
.per-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: var(--gc-space-4); margin-top: var(--gc-space-5); }
.per-field { display: flex; flex-direction: column; gap: 2px; }
.per-label { font-size: var(--gc-fs-xs); text-transform: uppercase; letter-spacing: 0.04em; color: var(--gc-text-3); }
.per-value { font-size: var(--gc-fs-md); color: var(--gc-text); }

.per-aside { padding: var(--gc-space-5); display: flex; flex-direction: column; gap: var(--gc-space-3); }
.per-aside__head { display: flex; align-items: center; justify-content: space-between; }
.per-aside__title { font-size: var(--gc-fs-xs); text-transform: uppercase; letter-spacing: 0.06em; color: var(--gc-text-3); }
.per-aside__list { border: 1px solid var(--gc-border); border-radius: var(--gc-radius-lg); overflow: hidden; }
.per-aside__list > :deep(.gc-row:last-child) { border-bottom: none; }
.per-aside__row { display: flex; flex-direction: column; gap: 2px; min-width: 0; }
.per-aside__rowname { font-size: var(--gc-fs-md); }
.per-aside__rowmeta { font-size: var(--gc-fs-xs); color: var(--gc-text-3); }
</style>
