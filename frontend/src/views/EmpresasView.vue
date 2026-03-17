<template>
  <AppLayout>
    <div class="empresas-view">
      <!-- Header -->
      <section class="empresas-view__header animate-slideUp">
        <div class="header-left">
          <h1 class="page-title gradient-text">Empresas</h1>
          <p class="page-subtitle">Gestiona tu cartera de empresas y clientes</p>
        </div>
        <Button variant="primary" icon="plus" @click="openCreate">Nueva Empresa</Button>
      </section>

      <!-- Filters -->
      <section class="empresas-view__filters animate-slideUp delay-1">
        <div class="search-wrapper">
          <Icon name="search" :size="18" color="var(--text-muted)" />
          <input v-model="searchQuery" type="text" class="search-input" placeholder="Buscar por razón social..." @input="onSearch" />
          <button v-if="searchQuery" class="search-clear" @click="clearSearch"><Icon name="x" :size="16" /></button>
        </div>
        <div class="filter-chips">
          <button class="chip" :class="{ active: filtroEstado === '' }" @click="setEstadoFilter('')">Todos</button>
          <button class="chip" :class="{ active: filtroEstado === 'ACTIVA' }" @click="setEstadoFilter('ACTIVA')">
            <span class="chip-dot chip-dot--activo"></span> Activas
          </button>
          <button class="chip" :class="{ active: filtroEstado === 'INACTIVA' }" @click="setEstadoFilter('INACTIVA')">
            <span class="chip-dot chip-dot--inactivo"></span> Inactivas
          </button>
        </div>
      </section>

      <!-- Content -->
      <section class="empresas-view__content animate-slideUp delay-2">
        <div v-if="store.loading && !store.empresas.length" class="loading-state">
          <Icon name="loader" :size="32" class="animate-spin" /><p>Cargando empresas...</p>
        </div>

        <div v-else-if="!store.empresas.length" class="empty-state glass">
          <div class="empty-icon"><Icon name="business" :size="48" color="var(--primary)" /></div>
          <h3>No hay empresas</h3>
          <p>Registra tu primera empresa para comenzar a gestionar clientes.</p>
          <Button variant="primary" icon="plus" @click="openCreate">Crear Empresa</Button>
        </div>

        <div v-else class="table-wrapper glass">
          <table class="data-table">
            <thead>
              <tr>
                <th>Razón Social</th>
                <th>Identificación</th>
                <th>Ubicación</th>
                <th>Sitio Web</th>
                <th>Estado</th>
                <th class="th-actions">Acciones</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="empresa in store.empresas" :key="empresa.id" class="data-row" :class="{ 'data-row--inactive': empresa.estado === 'INACTIVA' }">
                <td>
                  <div class="cell-empresa">
                    <div class="empresa-avatar">{{ getInitials(empresa.razonSocial) }}</div>
                    <div class="cell-empresa__info">
                      <span class="cell-empresa__name">{{ empresa.razonSocial }}</span>
                      <span v-if="empresa.direccionFisica" class="cell-empresa__sub">{{ empresa.direccionFisica }}</span>
                    </div>
                  </div>
                </td>
                <td>
                  <div v-if="empresa.identificacionTributaria" class="cell-id">
                    <span v-if="empresa.tipoDoc" class="cell-id__tipo">{{ empresa.tipoDoc }}</span>
                    <span class="cell-id__num">{{ empresa.identificacionTributaria }}</span>
                    <span v-if="empresa.dv != null" class="cell-id__dv">-{{ empresa.dv }}</span>
                  </div>
                  <span v-else class="cell-empty">—</span>
                </td>
                <td>
                  <div class="cell-location">
                    <span v-if="empresa.pais" class="cell-location__pais">{{ empresa.pais }}</span>
                    <span v-if="empresa.departamento" class="cell-location__dept">{{ empresa.departamento }}</span>
                    <span v-if="empresa.ciudad" class="cell-location__city">{{ empresa.ciudad }}</span>
                    <span v-if="!empresa.pais && !empresa.departamento && !empresa.ciudad" class="cell-empty">—</span>
                  </div>
                </td>
                <td>
                  <a v-if="empresa.sitioWeb" :href="empresa.sitioWeb" target="_blank" rel="noopener" class="cell-link">
                    {{ formatUrl(empresa.sitioWeb) }}
                    <Icon name="external-link" :size="12" />
                  </a>
                  <span v-else class="cell-empty">—</span>
                </td>
                <td>
                  <span class="badge" :class="empresa.estado === 'ACTIVA' ? 'badge--success' : 'badge--muted'">{{ empresa.estado }}</span>
                </td>
                <td class="td-actions">
                  <Button variant="ghost" icon="settings" icon-only size="sm" @click="openEdit(empresa)" />
                </td>
              </tr>
            </tbody>
          </table>

          <div v-if="store.pagination.totalPages > 1" class="pagination">
            <span class="pagination-total">{{ store.pagination.totalItems }} empresa{{ store.pagination.totalItems !== 1 ? 's' : '' }}</span>
            <div class="pagination-controls">
              <button class="pagination-btn" :disabled="store.pagination.page <= 1" @click="store.setPagina(store.pagination.page - 1)"><Icon name="chevron-left" :size="16" /></button>
              <span class="pagination-info">{{ store.pagination.page }} / {{ store.pagination.totalPages }}</span>
              <button class="pagination-btn" :disabled="store.pagination.page >= store.pagination.totalPages" @click="store.setPagina(store.pagination.page + 1)"><Icon name="chevron-right" :size="16" /></button>
            </div>
          </div>
        </div>
      </section>

      <Transition name="toast">
        <div v-if="store.error" class="error-toast" @click="store.limpiarError()">
          <Icon name="alert-circle" :size="16" /> {{ store.error }}
          <Icon name="x" :size="14" class="error-toast__close" />
        </div>
      </Transition>

      <EmpresaModal :visible="showModal" :empresa="editingEmpresa" :saving="store.saving" :error="modalError" @close="closeModal" @submit="handleSubmit" />
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import AppLayout from '@/components/layout/AppLayout.vue';
import Button from '@/components/ui/Button.vue';
import Icon from '@/components/ui/Icon.vue';
import EmpresaModal from '@/components/empresa/EmpresaModal.vue';
import { useEmpresaStore } from '@/stores/empresa.store';

const store = useEmpresaStore();
const searchQuery = ref('');
const filtroEstado = ref('');
const showModal = ref(false);
const editingEmpresa = ref(null);
const modalError = ref(null);
let searchTimeout = null;

onMounted(() => { store.cargarEmpresas(); });

function onSearch() { clearTimeout(searchTimeout); searchTimeout = setTimeout(() => { store.setFiltros({ q: searchQuery.value }); }, 400); }
function clearSearch() { searchQuery.value = ''; store.setFiltros({ q: '' }); }
function setEstadoFilter(estado) { filtroEstado.value = estado; store.setFiltros({ estado }); }

function openCreate() { editingEmpresa.value = null; modalError.value = null; showModal.value = true; }
function openEdit(empresa) { editingEmpresa.value = { ...empresa }; modalError.value = null; showModal.value = true; }
function closeModal() { showModal.value = false; editingEmpresa.value = null; modalError.value = null; }

async function handleSubmit(payload) {
  modalError.value = null;
  try {
    if (editingEmpresa.value) { await store.actualizarEmpresa(editingEmpresa.value.id, payload); }
    else { await store.crearEmpresa(payload); }
    closeModal();
  } catch (err) { modalError.value = err.response?.data?.message || 'Error al guardar empresa'; }
}

function getInitials(name) { return name ? name.split(' ').slice(0, 2).map(w => w[0]).join('').toUpperCase() : '?'; }
function formatUrl(url) { return url ? url.replace(/^https?:\/\/(www\.)?/, '').replace(/\/$/, '') : ''; }
</script>

<style scoped>
.empresas-view { display: flex; flex-direction: column; gap: var(--space-6); }
.empresas-view__header { display: flex; justify-content: space-between; align-items: flex-start; gap: var(--space-4); }
.page-title { font-family: var(--font-display); font-size: var(--text-3xl); font-weight: 700; margin: 0; line-height: 1.2; }
.page-subtitle { color: var(--text-tertiary); font-size: var(--text-sm); margin: var(--space-1) 0 0; }

.empresas-view__filters { display: flex; gap: var(--space-4); align-items: center; flex-wrap: wrap; }
.search-wrapper { position: relative; display: flex; align-items: center; flex: 1; min-width: 240px; max-width: 400px; }
.search-wrapper > :first-child { position: absolute; left: var(--space-4); pointer-events: none; }
.search-input { width: 100%; background: var(--glass-bg); border: 1px solid var(--glass-border); border-radius: var(--radius-lg); color: var(--text-primary); font-family: var(--font-body); font-size: var(--text-sm); padding: var(--space-3) var(--space-4) var(--space-3) calc(var(--space-4) + 24px); transition: all 0.2s; box-sizing: border-box; }
.search-input:focus { outline: none; border-color: var(--primary); box-shadow: 0 0 0 3px var(--primary-soft); }
.search-input::placeholder { color: var(--text-muted); }
.search-clear { position: absolute; right: var(--space-3); background: none; border: none; color: var(--text-muted); cursor: pointer; display: flex; padding: var(--space-1); }
.filter-chips { display: flex; gap: var(--space-2); }
.chip { display: flex; align-items: center; gap: var(--space-2); padding: var(--space-2) var(--space-4); background: var(--glass-bg); border: 1px solid var(--glass-border); border-radius: var(--radius-full); color: var(--text-secondary); font-family: var(--font-body); font-size: var(--text-xs); font-weight: 500; cursor: pointer; transition: all 0.2s; white-space: nowrap; }
.chip:hover { background: rgba(255, 255, 255, 0.06); }
.chip.active { border-color: var(--primary); color: var(--primary); background: var(--primary-soft); }
.chip-dot { width: 6px; height: 6px; border-radius: 50%; }
.chip-dot--activo { background: var(--success); }
.chip-dot--inactivo { background: var(--text-muted); }

.loading-state { display: flex; flex-direction: column; align-items: center; justify-content: center; gap: var(--space-3); padding: var(--space-8); color: var(--text-tertiary); font-size: var(--text-sm); }
.empty-state { display: flex; flex-direction: column; align-items: center; justify-content: center; gap: var(--space-4); padding: calc(var(--space-8) * 2); border-radius: var(--radius-xl); text-align: center; }
.empty-icon { width: 80px; height: 80px; display: flex; align-items: center; justify-content: center; background: var(--primary-soft); border-radius: var(--radius-xl); }
.empty-state h3 { color: var(--text-primary); font-family: var(--font-display); margin: 0; }
.empty-state p { color: var(--text-tertiary); font-size: var(--text-sm); max-width: 320px; margin: 0; }

.table-wrapper { border-radius: var(--radius-xl); overflow: hidden; }
.data-table { width: 100%; border-collapse: collapse; }
.data-table thead th { text-align: left; padding: var(--space-4) var(--space-5); font-family: var(--font-body); font-size: var(--text-xs); font-weight: 600; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.05em; border-bottom: 1px solid var(--glass-border); white-space: nowrap; }
.data-table tbody td { padding: var(--space-4) var(--space-5); font-size: var(--text-sm); color: var(--text-primary); border-bottom: 1px solid rgba(255, 255, 255, 0.03); vertical-align: middle; }
.data-row { transition: background 0.15s; }
.data-row:hover { background: rgba(255, 255, 255, 0.02); }
.data-row--inactive { opacity: 0.5; }
.th-actions, .td-actions { text-align: right; width: 60px; }

.cell-empresa { display: flex; align-items: center; gap: var(--space-3); }
.empresa-avatar { width: 36px; height: 36px; min-width: 36px; border-radius: var(--radius-md); display: flex; align-items: center; justify-content: center; font-family: var(--font-display); font-size: 12px; font-weight: 700; background: var(--primary-soft); color: var(--primary); }
.cell-empresa__info { display: flex; flex-direction: column; min-width: 0; }
.cell-empresa__name { font-weight: 600; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.cell-empresa__sub { font-size: var(--text-xs); color: var(--text-muted); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }

.cell-id { display: flex; align-items: center; gap: 4px; font-family: var(--font-mono); font-size: var(--text-xs); }
.cell-id__tipo { color: var(--secondary); font-weight: 600; }
.cell-id__num { color: var(--text-secondary); }
.cell-id__dv { color: var(--text-muted); }

.cell-location { display: flex; align-items: center; gap: 4px; font-size: var(--text-xs); color: var(--text-secondary); }
.cell-location__pais { font-weight: 600; }
.cell-location__dept::before, .cell-location__city::before { content: '·'; margin-right: 4px; color: var(--text-muted); }

.cell-link { display: flex; align-items: center; gap: 4px; color: var(--primary); font-size: var(--text-xs); text-decoration: none; transition: opacity 0.15s; }
.cell-link:hover { opacity: 0.8; }

.cell-empty { color: var(--text-muted); font-size: var(--text-xs); }

.badge { display: inline-flex; align-items: center; padding: 1px var(--space-2); border-radius: var(--radius-full); font-size: 10px; font-weight: 600; text-transform: uppercase; letter-spacing: 0.04em; }
.badge--success { background: rgba(16, 185, 129, 0.15); color: var(--success); }
.badge--muted { background: rgba(255, 255, 255, 0.06); color: var(--text-muted); }

.pagination { display: flex; align-items: center; justify-content: space-between; padding: var(--space-4) var(--space-5); border-top: 1px solid var(--glass-border); }
.pagination-total { font-size: var(--text-xs); color: var(--text-muted); }
.pagination-controls { display: flex; align-items: center; gap: var(--space-3); }
.pagination-btn { background: var(--glass-bg); border: 1px solid var(--glass-border); border-radius: var(--radius-sm); color: var(--text-secondary); padding: var(--space-2); cursor: pointer; display: flex; transition: all 0.15s; }
.pagination-btn:hover:not(:disabled) { background: rgba(255, 255, 255, 0.06); color: var(--text-primary); }
.pagination-btn:disabled { opacity: 0.3; cursor: not-allowed; }
.pagination-info { font-size: var(--text-xs); color: var(--text-muted); font-family: var(--font-mono); }

.error-toast { position: fixed; bottom: var(--space-6); right: var(--space-6); display: flex; align-items: center; gap: var(--space-3); padding: var(--space-4) var(--space-5); background: rgba(244, 63, 94, 0.15); border: 1px solid rgba(244, 63, 94, 0.3); border-radius: var(--radius-lg); color: var(--error); font-size: var(--text-sm); cursor: pointer; z-index: 900; backdrop-filter: blur(12px); }
.error-toast__close { opacity: 0.5; }
.toast-enter-active { transition: all 0.3s ease; }
.toast-leave-active { transition: all 0.2s ease; }
.toast-enter-from { transform: translateY(20px); opacity: 0; }
.toast-leave-to { transform: translateY(10px); opacity: 0; }

@media (max-width: 1100px) {
  .data-table { font-size: var(--text-xs); }
  .data-table thead th, .data-table tbody td { padding: var(--space-3); }
}
</style>
