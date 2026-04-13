<template>
  <AppLayout>
    <div class="contratos-list">
      <section class="list-header animate-slideUp">
        <div>
          <h1 class="page-title gradient-text">Contratos</h1>
          <p class="page-subtitle">Gestión de contratos formalizados</p>
        </div>
      </section>

      <section class="list-filters animate-slideUp delay-1">
        <div class="filter-bar glass">
          <div class="search-wrap">
            <Icon name="search" :size="16" class="search-icon" />
            <input v-model="searchQuery" type="text" class="search-input" placeholder="Buscar contrato..." @input="debouncedSearch" />
          </div>
          <select v-model="filtroEstado" class="filter-select" @change="loadContratos(1)">
            <option value="">Todos los estados</option>
            <option value="VIGENTE">Vigentes</option>
            <option value="SUSPENDIDO">Suspendidos</option>
            <option value="TERMINADO">Terminados</option>
            <option value="LIQUIDADO">Liquidados</option>
          </select>
        </div>
      </section>

      <div v-if="loading" class="loading-state">
        <Icon name="loader" :size="32" class="animate-spin" /><p>Cargando contratos...</p>
      </div>

      <section v-else class="list-table animate-slideUp delay-2">
        <div v-if="!contratos.length" class="empty-state glass">
          <Icon name="note-add" :size="40" color="var(--text-muted)" />
          <p>No se encontraron contratos</p>
        </div>
        <div v-else class="table-wrap glass">
          <table class="data-table">
            <thead>
              <tr>
                <th>Contrato</th>
                <th>Empresa</th>
                <th>Tipo</th>
                <th>Estado</th>
                <th class="text-right">Valor</th>
                <th>Inicio</th>
                <th>Fin</th>
                <th>Responsable</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="c in contratos" :key="c.id" class="data-row" @click="goToDetalle(c.id)">
                <td>
                  <span class="cell-name">{{ c.numeroContratoInterno || c.oportunidadNombre }}</span>
                  <span v-if="c.numeroContratoInterno" class="cell-sub">{{ c.oportunidadNombre }}</span>
                </td>
                <td><span class="cell-empresa">{{ c.empresaNombre }}</span></td>
                <td><span class="cell-tipo">{{ c.tipoContratoNombre }}</span></td>
                <td>
                  <span :class="['estado-pill', `estado-pill--${c.estado?.toLowerCase()}`]">{{ estadoLabel(c.estado) }}</span>
                </td>
                <td class="text-right">
                  <span class="cell-valor">{{ fmtCurrency(c.valorContrato, c.moneda) }}</span>
                  <span v-if="c.valorAjuste && c.valorAjuste !== 0" class="cell-ajuste">+{{ fmtCurrency(c.valorAjuste, c.moneda) }}</span>
                </td>
                <td><span class="cell-fecha">{{ fmtDate(c.fechaInicio) }}</span></td>
                <td>
                  <span :class="['cell-fecha', { 'cell-fecha--vencida': isProximoVencer(c) }]">{{ fmtDate(c.fechaFin) }}</span>
                </td>
                <td><span class="cell-responsable">{{ c.responsableGestion || '—' }}</span></td>
              </tr>
            </tbody>
          </table>
        </div>

        <div v-if="totalPages > 1" class="pagination">
          <button class="pag-btn" :disabled="currentPage <= 1" @click="loadContratos(currentPage - 1)">
            <Icon name="chevron-left" :size="16" />
          </button>
          <span class="pag-info">Página {{ currentPage }} de {{ totalPages }}</span>
          <button class="pag-btn" :disabled="currentPage >= totalPages" @click="loadContratos(currentPage + 1)">
            <Icon name="chevron-right" :size="16" />
          </button>
        </div>
      </section>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import AppLayout from '@/components/layout/AppLayout.vue';
import Icon from '@/components/ui/Icon.vue';
import { contratoService } from '@/services/contrato.service';
import { formatCurrency } from '@/utils/currency';

const router = useRouter();
const contratos = ref([]);
const loading = ref(true);
const searchQuery = ref('');
const filtroEstado = ref('');
const currentPage = ref(1);
const totalPages = ref(1);
const pageSize = 20;
let searchTimer = null;

onMounted(() => loadContratos(1));

async function loadContratos(page = 1) {
  loading.value = true;
  try {
    const params = { page, page_size: pageSize };
    if (filtroEstado.value) params.estado = filtroEstado.value;
    const res = await contratoService.listar(params);
    contratos.value = res.data || [];
    currentPage.value = res.page || 1;
    totalPages.value = res.totalPages || 1;
  } catch (err) { console.error('Error cargando contratos:', err); }
  finally { loading.value = false; }
}

function debouncedSearch() {
  clearTimeout(searchTimer);
  searchTimer = setTimeout(() => loadContratos(1), 400);
}

function goToDetalle(id) { router.push(`/contratos/${id}`); }

function estadoLabel(estado) {
  return { VIGENTE: 'Vigente', SUSPENDIDO: 'Suspendido', TERMINADO: 'Terminado', LIQUIDADO: 'Liquidado' }[estado] || estado;
}

function fmtCurrency(v, m) { return formatCurrency(v, m); }

function fmtDate(d) {
  if (!d) return '—';
  return new Date(d).toLocaleDateString('es-CO', { day: '2-digit', month: 'short', year: 'numeric' });
}

function isProximoVencer(c) {
  if (!c.fechaFin || c.estado !== 'VIGENTE') return false;
  const diff = (new Date(c.fechaFin) - new Date()) / 86400000;
  return diff >= 0 && diff <= 30;
}
</script>

<style scoped>
.contratos-list { display: flex; flex-direction: column; gap: var(--space-5); }
.list-header { display: flex; justify-content: space-between; align-items: center; }
.page-title { font-family: var(--font-display); font-size: var(--text-3xl); font-weight: 700; margin: 0; }
.page-subtitle { color: var(--text-tertiary); font-size: var(--text-sm); margin: var(--space-1) 0 0; }

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

.table-wrap { border-radius: var(--radius-xl); overflow: hidden; }
.data-table { width: 100%; border-collapse: collapse; }
.data-table th { font-family: var(--font-body); font-size: 10px; font-weight: 600; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.5px; padding: var(--space-3) var(--space-4); text-align: left; border-bottom: 1px solid var(--glass-border); background: var(--bg-surface); }
.data-table .text-right { text-align: right; }

.data-row { cursor: pointer; transition: background 0.15s; }
.data-row:hover { background: rgba(0,212,255,0.04); }
.data-row td { padding: var(--space-3) var(--space-4); border-bottom: 1px solid rgba(255,255,255,0.03); vertical-align: middle; }

.cell-name { display: block; font-size: var(--text-xs); font-weight: 600; color: var(--text-primary); }
.cell-sub { display: block; font-size: 10px; color: var(--text-muted); margin-top: 1px; }
.cell-empresa { font-size: var(--text-xs); color: var(--text-secondary); }
.cell-tipo { font-size: var(--text-xs); color: var(--text-secondary); }
.cell-valor { display: block; font-family: var(--font-mono, monospace); font-size: var(--text-xs); font-weight: 600; color: var(--primary); }
.cell-ajuste { display: block; font-family: var(--font-mono, monospace); font-size: 9px; color: var(--success); }
.cell-fecha { font-size: var(--text-xs); color: var(--text-tertiary); }
.cell-fecha--vencida { color: var(--warning); font-weight: 600; }
.cell-responsable { font-size: var(--text-xs); color: var(--text-muted); }

.estado-pill { display: inline-block; padding: 2px 8px; border-radius: var(--radius-full); font-size: 10px; font-weight: 600; }
.estado-pill--vigente { background: var(--success-soft); color: var(--success); }
.estado-pill--suspendido { background: var(--warning-soft); color: var(--warning); }
.estado-pill--terminado { background: var(--accent-soft); color: var(--accent); }
.estado-pill--liquidado { background: var(--primary-soft); color: var(--primary); }

.pagination { display: flex; align-items: center; justify-content: center; gap: var(--space-4); padding: var(--space-4) 0; }
.pag-btn { display: flex; align-items: center; justify-content: center; width: 32px; height: 32px; border-radius: var(--radius-full); background: var(--glass-bg); border: 1px solid var(--glass-border); color: var(--text-secondary); cursor: pointer; transition: all 0.15s; }
.pag-btn:hover:not(:disabled) { background: var(--glass-hover); border-color: var(--primary); color: var(--primary); }
.pag-btn:disabled { opacity: 0.3; cursor: not-allowed; }
.pag-info { font-size: var(--text-xs); color: var(--text-muted); font-family: var(--font-mono, monospace); }
</style>
