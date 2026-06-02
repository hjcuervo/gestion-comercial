<template>
  <!-- Lista maestra (conmutador + lista del modo activo) -->
  <Teleport to="#gc-shell-master">
    <div class="dir-master">
      <div class="dir-switch">
        <button class="dir-switch__opt" :class="{ 'dir-switch__opt--on': modo === 'empresa' }" @click="switchModo('empresa')">Empresas</button>
        <button class="dir-switch__opt" :class="{ 'dir-switch__opt--on': modo === 'persona' }" @click="switchModo('persona')">Personas</button>
      </div>

      <div class="dir-master__filters">
        <GcInput v-model="search" :placeholder="modo === 'empresa' ? 'Buscar empresa…' : 'Buscar persona…'" icon="search" @update:modelValue="debouncedReload" />
        <GcButton variant="primary" size="sm" icon="plus" full-width @click="nuevo">{{ modo === 'empresa' ? 'Nueva empresa' : 'Nueva persona' }}</GcButton>
      </div>

      <!-- Modo empresa -->
      <template v-if="modo === 'empresa'">
        <div v-if="loadingEmp" class="dir-master__state"><GcSpinner :size="20" /></div>
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
            <div class="dir-master__row">
              <span class="dir-master__name">{{ e.razonSocial }}</span>
              <span class="dir-master__meta gc-mono">{{ e.identificacionTributaria || '—' }}</span>
            </div>
          </GcListRow>
        </div>
      </template>

      <!-- Modo persona -->
      <template v-else>
        <div v-if="loadingPer" class="dir-master__state"><GcSpinner :size="20" /></div>
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
            <div class="dir-master__row">
              <span class="dir-master__name">{{ p.nombreCompleto || (p.nombres + ' ' + p.apellidos) }}</span>
              <span class="dir-master__meta">{{ p.numeroDocumento || '—' }}</span>
            </div>
          </GcListRow>
        </div>
      </template>
    </div>
  </Teleport>

  <!-- Detalle del modo activo -->
  <EmpresaDetalle
    v-if="modo === 'empresa'"
    ref="empRef"
    :id="selectedId"
    @updated="reloadActive"
    @open-persona="openPersona"
  />
  <PersonaDetalle
    v-else
    ref="perRef"
    :id="selectedId"
    @updated="reloadActive"
    @open-empresa="openEmpresa"
  />
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch, nextTick } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useShell } from '@/composables/useShell';
import { empresaService } from '@/services/empresa.service';
import { personaService } from '@/services/persona.service';
import EmpresaDetalle from '@/components/directorio/EmpresaDetalle.vue';
import PersonaDetalle from '@/components/directorio/PersonaDetalle.vue';
import GcInput from '@/components/ui/GcInput.vue';
import GcButton from '@/components/ui/GcButton.vue';
import GcListRow from '@/components/ui/GcListRow.vue';
import GcEmpty from '@/components/ui/GcEmpty.vue';
import GcSpinner from '@/components/ui/GcSpinner.vue';

const route = useRoute();
const router = useRouter();
const { setRegions, reset } = useShell();
setRegions({ master: true, aside: true });

const modo = computed(() => (route.params.modo === 'persona' ? 'persona' : 'empresa'));
const selectedId = computed(() => route.params.id || null);

const empRef = ref(null);
const perRef = ref(null);

const empresas = ref([]);
const personas = ref([]);
const loadingEmp = ref(false);
const loadingPer = ref(false);
const search = ref('');
let timer = null;

async function reloadEmpresas() {
  loadingEmp.value = true;
  try {
    const params = { page: 1, page_size: 50 };
    if (search.value) params.q = search.value;
    const res = await empresaService.listar(params);
    empresas.value = res.data || [];
  } catch (err) { console.error('Error cargando empresas:', err); empresas.value = []; }
  finally { loadingEmp.value = false; }
}

async function reloadPersonas() {
  loadingPer.value = true;
  try {
    const params = { page: 1, page_size: 50 };
    if (search.value) params.q = search.value;
    const res = await personaService.listar(params);
    personas.value = res.data || [];
  } catch (err) { console.error('Error cargando personas:', err); personas.value = []; }
  finally { loadingPer.value = false; }
}

function reloadActive() {
  if (modo.value === 'empresa') reloadEmpresas();
  else reloadPersonas();
}
function debouncedReload() { clearTimeout(timer); timer = setTimeout(reloadActive, 400); }

function switchModo(m) {
  if (m === modo.value) return;
  router.push({ name: 'Directorio', params: { modo: m } });
}
function select(id) { router.push({ name: 'Directorio', params: { modo: modo.value, id: String(id) } }); }

function nuevo() {
  const r = modo.value === 'empresa' ? empRef.value : perRef.value;
  r?.openCreate();
}

// Navegación cruzada (T-DIR.6): saltar a la otra entidad por la relación.
function openPersona(id) { router.push({ name: 'Directorio', params: { modo: 'persona', id: String(id) } }); }
function openEmpresa(id) { router.push({ name: 'Directorio', params: { modo: 'empresa', id: String(id) } }); }

// Al cambiar de modo: limpiar búsqueda y cargar la lista de ese modo.
watch(modo, () => {
  search.value = '';
  reloadActive();
});

onMounted(async () => {
  await nextTick();
  reloadActive();
});
onUnmounted(reset);
</script>

<style scoped>
.dir-master { display: flex; flex-direction: column; }
.dir-switch { display: flex; gap: var(--gc-space-1); padding: var(--gc-space-3) var(--gc-space-3) 0; }
.dir-switch__opt {
  flex: 1; padding: var(--gc-space-2); background: transparent; border: none;
  border-bottom: 2px solid transparent; color: var(--gc-text-2);
  font-size: var(--gc-fs-md); font-weight: var(--gc-fw-medium); cursor: pointer;
  transition: color var(--gc-t-fast), border-color var(--gc-t-fast);
}
.dir-switch__opt:hover { color: var(--gc-text); }
.dir-switch__opt--on { color: var(--gc-text); border-bottom-color: var(--gc-primary); }

.dir-master__filters { display: flex; flex-direction: column; gap: var(--gc-space-2); padding: var(--gc-space-3); border-bottom: 1px solid var(--gc-border); }
.dir-master__state { display: flex; justify-content: center; padding: var(--gc-space-8); color: var(--gc-text-3); }
.dir-master__row { display: flex; flex-direction: column; gap: 2px; min-width: 0; }
.dir-master__name { font-size: var(--gc-fs-md); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.dir-master__meta { font-size: var(--gc-fs-xs); color: var(--gc-text-3); }
</style>
