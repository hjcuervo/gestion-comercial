<template>
  <AppLayout>
    <div class="oportunidades-list">
      <!-- Header -->
      <section class="list-header animate-slideUp">
        <div>
          <h1 class="page-title gradient-text">Oportunidades</h1>
          <p class="page-subtitle">Listado de oportunidades comerciales</p>
        </div>
        <button class="btn btn--primary" @click="openCreateOportunidad"><Icon name="plus" :size="14" /> Nueva Oportunidad</button>
      </section>

      <!-- Filtros -->
      <section class="list-filters animate-slideUp delay-1">
        <div class="filter-bar glass">
          <div class="search-wrap">
            <Icon name="search" :size="16" class="search-icon" />
            <input
              v-model="searchQuery"
              type="text"
              class="search-input"
              placeholder="Buscar oportunidad..."
              @input="debouncedSearch"
            />
          </div>
          <select v-model="filtroEstado" class="filter-select" @change="loadOportunidades(1)">
            <option value="">Todos los estados</option>
            <option value="ABIERTA">Abiertas</option>
            <option value="SEGUIMIENTO">En Seguimiento</option>
            <option value="GANADA">Ganadas</option>
            <option value="PERDIDA">Perdidas</option>
            <option value="NO_CONCRETADA">No Concretadas</option>
          </select>
          <select v-model="filtroPipeline" class="filter-select" @change="loadOportunidades(1)">
            <option value="">Todos los pipelines</option>
            <option v-for="p in pipelines" :key="p.id" :value="p.id">{{ p.nombre }}</option>
          </select>
        </div>
      </section>

      <!-- Loading -->
      <div v-if="loading" class="loading-state">
        <Icon name="loader" :size="32" class="animate-spin" /><p>Cargando oportunidades...</p>
      </div>

      <!-- Tabla -->
      <section v-else class="list-table animate-slideUp delay-2">
        <div v-if="!oportunidades.length" class="empty-state glass">
          <Icon name="wallet" :size="40" color="var(--text-muted)" />
          <p>No se encontraron oportunidades</p>
        </div>
        <div v-else class="table-wrap glass">
          <table class="data-table">
            <thead>
              <tr>
                <th>Oportunidad</th>
                <th>Empresa</th>
                <th>Etapa</th>
                <th>Estado</th>
                <th class="text-right">Valor</th>
                <th class="text-center">Prob.</th>
                <th>Fecha Creación</th>
                <th>Cierre Est.</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="op in oportunidades"
                :key="op.id"
                class="data-row"
                @click="goToDetalle(op.id)"
              >
                <td>
                  <span class="cell-name">{{ op.nombre }}</span>
                </td>
                <td>
                  <span class="cell-empresa">{{ op.empresaNombre }}</span>
                </td>
                <td>
                  <span class="cell-etapa">{{ op.etapaNombre }}</span>
                </td>
                <td>
                  <span :class="['estado-pill', `estado-pill--${op.estadoMacro?.toLowerCase()}`]">
                    {{ estadoLabel(op.estadoMacro) }}
                  </span>
                </td>
                <td class="text-right">
                  <span class="cell-valor">{{ formatCurrency(op.valorEstimado) }}</span>
                  <span class="cell-moneda">{{ op.moneda }}</span>
                </td>
                <td class="text-center">
                  <span v-if="op.probabilidad != null" class="cell-prob">{{ op.probabilidad }}%</span>
                  <span v-else class="cell-muted">—</span>
                </td>
                <td>
                  <span class="cell-fecha">{{ formatDate(op.fechaCreacion) }}</span>
                </td>
                <td>
                  <span :class="['cell-fecha', { 'cell-fecha--vencida': isVencida(op) }]">
                    {{ formatDateShort(op.fechaEstimadaCierre) || '—' }}
                  </span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Paginación -->
        <div v-if="totalPages > 1" class="pagination">
          <button class="pag-btn" :disabled="currentPage <= 1" @click="loadOportunidades(currentPage - 1)">
            <Icon name="chevron-left" :size="16" />
          </button>
          <span class="pag-info">Página {{ currentPage }} de {{ totalPages }}</span>
          <button class="pag-btn" :disabled="currentPage >= totalPages" @click="loadOportunidades(currentPage + 1)">
            <Icon name="chevron-right" :size="16" />
          </button>
        </div>
      </section>
    </div>

    <!-- Modal -->
    <OportunidadModal :visible="showOpModal" :oportunidad="null" :empresas="empresas" :pipelines="pipelines" :saving="saving" :error="modalError" @close="showOpModal = false" @submit="handleCreateOportunidad" />
  </AppLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import AppLayout from '@/components/layout/AppLayout.vue';
import Icon from '@/components/ui/Icon.vue';
import OportunidadModal from '@/components/oportunidad/OportunidadModal.vue';
import { oportunidadService } from '@/services/oportunidad.service';
import { pipelineService } from '@/services/pipeline.service';
import { empresaService } from '@/services/empresa.service';
import { formatCurrency } from '@/utils/currency';

const router = useRouter();
const oportunidades = ref([]);
const pipelines = ref([]);
const empresas = ref([]);
const loading = ref(true);
const searchQuery = ref('');
const filtroEstado = ref('');
const filtroPipeline = ref('');
const currentPage = ref(1);
const totalPages = ref(1);
const pageSize = 15;
let searchTimer = null;

// Modal
const showOpModal = ref(false);
const saving = ref(false);
const modalError = ref(null);

onMounted(async () => {
  try { pipelines.value = await pipelineService.listarActivos(); } catch {}
  try { const res = await empresaService.listar({ page_size: 100 }); empresas.value = res.data || []; } catch {}
  await loadOportunidades(1);
});

async function loadOportunidades(page = 1) {
  loading.value = true;
  try {
    const params = { page, page_size: pageSize };
    if (searchQuery.value.trim()) params.q = searchQuery.value.trim();
    if (filtroEstado.value) params.estado = filtroEstado.value;
    if (filtroPipeline.value) params.pipeline_id = filtroPipeline.value;

    const res = await oportunidadService.listar(params);
    oportunidades.value = res.data || [];
    currentPage.value = res.page || 1;
    totalPages.value = res.totalPages || 1;
  } catch (err) {
    console.error('Error cargando oportunidades:', err);
  } finally {
    loading.value = false;
  }
}

function debouncedSearch() {
  clearTimeout(searchTimer);
  searchTimer = setTimeout(() => loadOportunidades(1), 400);
}

function openCreateOportunidad() { modalError.value = null; showOpModal.value = true; }

async function handleCreateOportunidad(payload) {
  saving.value = true;
  modalError.value = null;
  try {
    await oportunidadService.crear(payload);
    showOpModal.value = false;
    await loadOportunidades(currentPage.value);
  } catch (err) {
    modalError.value = err.response?.data?.message || 'Error al crear oportunidad';
  } finally {
    saving.value = false;
  }
}

function goToDetalle(id) {
  router.push(`/oportunidades/${id}`);
}

function estadoLabel(estado) {
  const map = { ABIERTA: 'Abierta', SEGUIMIENTO: 'Seguimiento', GANADA: 'Ganada', PERDIDA: 'Perdida', NO_CONCRETADA: 'No Concretada' };
  return map[estado] || estado;
}

function isVencida(op) {
  if (!op.fechaEstimadaCierre || op.estadoMacro === 'GANADA' || op.estadoMacro === 'PERDIDA' || op.estadoMacro === 'NO_CONCRETADA') return false;
  return new Date(op.fechaEstimadaCierre) < new Date();
}

function formatCurrency(value) {
  const num = Number(value);
  if (!num) return '$0';
  if (num >= 1000000000) return `$${(num / 1000000000).toFixed(1)}B`;
  if (num >= 1000000) return `$${(num / 1000000).toFixed(0)}M`;
  if (num >= 1000) return `$${(num / 1000).toFixed(0)}K`;
  return `$${num.toLocaleString()}`;
}

function formatDate(dt) {
  if (!dt) return '—';
  return new Date(dt).toLocaleDateString('es-CO', { day: '2-digit', month: 'short', year: 'numeric' });
}

function formatDateShort(date) {
  if (!date) return null;
  return new Date(date).toLocaleDateString('es-CO', { day: '2-digit', month: 'short', year: 'numeric' });
}
</script>

<style scoped>
.oportunidades-list { display: flex; flex-direction: column; gap: var(--space-5); }

.list-header { display: flex; justify-content: space-between; align-items: center; }
.btn { display: flex; align-items: center; gap: var(--space-2); padding: var(--space-2) var(--space-5); border-radius: var(--radius-full); font-family: var(--font-body); font-size: var(--text-xs); font-weight: 600; cursor: pointer; transition: all 0.15s; border: 1px solid transparent; }
.btn--primary { background: var(--primary); color: #000; }
.btn--primary:hover { box-shadow: 0 0 20px var(--primary-glow); }
.page-title { font-family: var(--font-display); font-size: var(--text-3xl); font-weight: 700; margin: 0; }
.page-subtitle { color: var(--text-tertiary); font-size: var(--text-sm); margin: var(--space-1) 0 0; }

/* Filters */
.filter-bar { display: flex; gap: var(--space-3); padding: var(--space-3) var(--space-4); border-radius: var(--radius-lg); align-items: center; flex-wrap: wrap; }
.search-wrap { position: relative; flex: 1; min-width: 200px; }
.search-icon { position: absolute; left: 10px; top: 50%; transform: translateY(-50%); color: var(--text-muted); pointer-events: none; }
.search-input { width: 100%; background: var(--bg-surface); border: 1px solid var(--glass-border); border-radius: var(--radius-md); color: var(--text-primary); font-family: var(--font-body); font-size: var(--text-xs); padding: var(--space-2) var(--space-3) var(--space-2) 34px; }
.search-input::placeholder { color: var(--text-muted); }
.search-input:focus { outline: none; border-color: var(--primary); }
.filter-select { background: var(--bg-surface); border: 1px solid var(--glass-border); border-radius: var(--radius-md); color: var(--text-primary); font-family: var(--font-body); font-size: var(--text-xs); padding: var(--space-2) var(--space-4); appearance: none; background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 24 24' fill='none' stroke='rgba(255,255,255,0.3)' stroke-width='2'%3E%3Cpath d='M6 9l6 6 6-6'/%3E%3C/svg%3E"); background-repeat: no-repeat; background-position: right 8px center; padding-right: 28px; }
.filter-select:focus { outline: none; border-color: var(--primary); }
.filter-select option { background: var(--bg-elevated); }

.loading-state { display: flex; flex-direction: column; align-items: center; justify-content: center; gap: var(--space-3); padding: var(--space-8); color: var(--text-tertiary); font-size: var(--text-sm); }

.empty-state { display: flex; flex-direction: column; align-items: center; justify-content: center; gap: var(--space-3); padding: var(--space-10); border-radius: var(--radius-xl); color: var(--text-muted); font-size: var(--text-sm); }

/* Table */
.table-wrap { border-radius: var(--radius-xl); overflow: hidden; }
.data-table { width: 100%; border-collapse: collapse; }
.data-table th { font-family: var(--font-body); font-size: 10px; font-weight: 600; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.5px; padding: var(--space-3) var(--space-4); text-align: left; border-bottom: 1px solid var(--glass-border); background: var(--bg-surface); }
.data-table .text-right { text-align: right; }
.data-table .text-center { text-align: center; }

.data-row { cursor: pointer; transition: background 0.15s; }
.data-row:hover { background: rgba(0, 212, 255, 0.04); }
.data-row td { padding: var(--space-3) var(--space-4); border-bottom: 1px solid rgba(255,255,255,0.03); vertical-align: middle; }

.cell-name { font-size: var(--text-xs); font-weight: 600; color: var(--text-primary); }
.cell-empresa { font-size: var(--text-xs); color: var(--text-secondary); }
.cell-etapa { font-size: var(--text-xs); color: var(--text-secondary); }
.cell-valor { font-family: var(--font-mono); font-size: var(--text-xs); font-weight: 600; color: var(--primary); }
.cell-moneda { font-size: 9px; color: var(--text-muted); margin-left: 4px; }
.cell-prob { font-family: var(--font-mono); font-size: var(--text-xs); color: var(--text-secondary); }
.cell-muted { color: var(--text-muted); font-size: var(--text-xs); }
.cell-fecha { font-size: var(--text-xs); color: var(--text-tertiary); }
.cell-fecha--vencida { color: var(--error); font-weight: 600; }

/* Estado pill */
.estado-pill { display: inline-block; padding: 2px 8px; border-radius: var(--radius-full); font-size: 10px; font-weight: 600; }
.estado-pill--abierta { background: var(--primary-soft); color: var(--primary); }
.estado-pill--seguimiento { background: var(--warning-soft); color: var(--warning); }
.estado-pill--ganada { background: var(--success-soft); color: var(--success); }
.estado-pill--perdida { background: var(--error-soft); color: var(--error); }
.estado-pill--no_concretada { background: var(--accent-soft); color: var(--accent); }

/* Pagination */
.pagination { display: flex; align-items: center; justify-content: center; gap: var(--space-4); padding: var(--space-4) 0; }
.pag-btn { display: flex; align-items: center; justify-content: center; width: 32px; height: 32px; border-radius: var(--radius-full); background: var(--glass-bg); border: 1px solid var(--glass-border); color: var(--text-secondary); cursor: pointer; transition: all 0.15s; }
.pag-btn:hover:not(:disabled) { background: var(--glass-hover); border-color: var(--primary); color: var(--primary); }
.pag-btn:disabled { opacity: 0.3; cursor: not-allowed; }
.pag-info { font-size: var(--text-xs); color: var(--text-muted); font-family: var(--font-mono); }
</style>
