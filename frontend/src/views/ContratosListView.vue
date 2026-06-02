<template>
  <!-- Lista maestra teletransportada a la zona izquierda del shell -->
  <Teleport to="#gc-shell-master">
    <div class="ctr-master">
      <div class="ctr-master__filters">
        <GcInput v-model="searchQuery" placeholder="Buscar contrato…" icon="search" @update:modelValue="debouncedSearch" />
        <GcSelect v-model="filtroEstado" :options="estadoOptions" @update:modelValue="() => loadContratos(1)" />
      </div>

      <div v-if="loading" class="ctr-master__state"><GcSpinner :size="20" /></div>
      <GcEmpty v-else-if="!contratos.length" icon="file-off" message="Sin contratos" />
      <div v-else>
        <GcListRow
          v-for="c in contratos"
          :key="c.id"
          :tone="estadoTone(c.estado)"
          clickable
          :active="String(c.id) === String(selectedId)"
          @click="goTo(c.id)"
        >
          <div class="ctr-master__row">
            <span class="ctr-master__name">{{ c.numeroContratoInterno || c.oportunidadNombre || ('Contrato #' + c.id) }}</span>
            <span class="ctr-master__meta">{{ c.empresaNombre || '—' }}</span>
            <span class="ctr-master__foot">
              <GcBadge :tone="estadoTone(c.estado)" :label="estadoLabel(c.estado)" />
              <span class="ctr-master__value gc-mono">{{ fmtCurrency(c.valorTotal ?? c.valorContrato, c.moneda) }}</span>
            </span>
          </div>
        </GcListRow>

        <div v-if="totalPages > 1" class="ctr-master__pager">
          <GcButton variant="ghost" size="sm" icon="chevron-left" :disabled="currentPage <= 1" @click="loadContratos(currentPage - 1)" />
          <span class="ctr-master__pageinfo gc-mono">{{ currentPage }} / {{ totalPages }}</span>
          <GcButton variant="ghost" size="sm" icon="chevron-right" :disabled="currentPage >= totalPages" @click="loadContratos(currentPage + 1)" />
        </div>
      </div>
    </div>
  </Teleport>

  <!-- Superficie central: detalle del contrato seleccionado -->
  <div class="ctr-surface">
    <router-view v-if="selectedId" />
    <GcEmpty v-else icon="hand-click" message="Selecciona un contrato de la izquierda para ver su detalle" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useShell } from '@/composables/useShell';
import { contratoService } from '@/services/contrato.service';
import { extractPagination } from '@/utils/pagination';
import { formatCurrency } from '@/utils/currency';
import GcInput from '@/components/ui/GcInput.vue';
import GcSelect from '@/components/ui/GcSelect.vue';
import GcListRow from '@/components/ui/GcListRow.vue';
import GcBadge from '@/components/ui/GcBadge.vue';
import GcButton from '@/components/ui/GcButton.vue';
import GcEmpty from '@/components/ui/GcEmpty.vue';
import GcSpinner from '@/components/ui/GcSpinner.vue';

const route = useRoute();
const router = useRouter();
const { setRegions } = useShell();

const contratos = ref([]);
const loading = ref(true);
const searchQuery = ref('');
const filtroEstado = ref('');
const currentPage = ref(1);
const totalPages = ref(1);
const pageSize = 20;
let searchTimer = null;

const selectedId = computed(() => route.params.id || null);

const estadoOptions = [
  { value: '', label: 'Todos los estados' },
  { value: 'VIGENTE', label: 'Vigentes' },
  { value: 'SUSPENDIDO', label: 'Suspendidos' },
  { value: 'TERMINADO', label: 'Terminados' },
  { value: 'LIQUIDADO', label: 'Liquidados' },
];

const TONE = { VIGENTE: 'success', SUSPENDIDO: 'warning', TERMINADO: 'danger', LIQUIDADO: 'neutral' };
function estadoTone(e) { return TONE[e] || 'neutral'; }
function estadoLabel(e) { return { VIGENTE: 'Vigente', SUSPENDIDO: 'Suspendido', TERMINADO: 'Terminado', LIQUIDADO: 'Liquidado' }[e] || e; }
function fmtCurrency(v, m) { return formatCurrency(v, m); }

async function loadContratos(page = 1) {
  loading.value = true;
  try {
    const params = { page, page_size: pageSize };
    if (filtroEstado.value) params.estado = filtroEstado.value;
    const res = await contratoService.listar(params);
    contratos.value = res.data || [];
    const pag = extractPagination(res);
    currentPage.value = pag.page;
    totalPages.value = pag.totalPages;
  } catch (err) {
    console.error('Error cargando contratos:', err);
    contratos.value = [];
  } finally {
    loading.value = false;
  }
}

function debouncedSearch() {
  clearTimeout(searchTimer);
  searchTimer = setTimeout(() => loadContratos(1), 400);
}

function goTo(id) { router.push(`/contratos/${id}`); }

onMounted(() => {
  setRegions({ master: true });
  loadContratos(1);
});
</script>

<style scoped>
.ctr-master { display: flex; flex-direction: column; height: 100%; }
.ctr-master__filters { display: flex; flex-direction: column; gap: var(--gc-space-2); padding: var(--gc-space-3); border-bottom: 1px solid var(--gc-border); }
.ctr-master__state { display: flex; justify-content: center; padding: var(--gc-space-8); color: var(--gc-text-3); }
.ctr-master__row { display: flex; flex-direction: column; gap: 4px; min-width: 0; }
.ctr-master__name { font-size: var(--gc-fs-md); color: var(--gc-text); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.ctr-master__meta { font-size: var(--gc-fs-xs); color: var(--gc-text-3); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.ctr-master__foot { display: flex; align-items: center; justify-content: space-between; gap: var(--gc-space-2); }
.ctr-master__value { font-size: var(--gc-fs-xs); color: var(--gc-text-2); }
.ctr-master__pager { display: flex; align-items: center; justify-content: center; gap: var(--gc-space-2); padding: var(--gc-space-3); }
.ctr-master__pageinfo { font-size: var(--gc-fs-xs); color: var(--gc-text-3); }
.ctr-surface { min-height: 100%; }
</style>
