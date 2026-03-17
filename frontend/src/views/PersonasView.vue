<template>
  <AppLayout>
    <div class="personas-view">
      <!-- Header -->
      <section class="personas-view__header animate-slideUp">
        <div class="header-left">
          <h1 class="page-title gradient-text">Contactos</h1>
          <p class="page-subtitle">Gestiona contactos y sus relaciones con empresas</p>
        </div>
        <Button variant="primary" icon="plus" @click="openCreatePersona">Nuevo Contacto</Button>
      </section>

      <!-- Filters -->
      <section class="personas-view__filters animate-slideUp delay-1">
        <div class="search-wrapper">
          <Icon name="search" :size="18" color="var(--text-muted)" />
          <input v-model="searchQuery" type="text" class="search-input" placeholder="Buscar por nombre o apellido..." @input="onSearch" />
          <button v-if="searchQuery" class="search-clear" @click="clearSearch"><Icon name="x" :size="16" /></button>
        </div>
      </section>

      <!-- Content -->
      <section class="personas-view__content animate-slideUp delay-2">
        <div v-if="store.loading && !store.personas.length" class="loading-state">
          <Icon name="loader" :size="32" class="animate-spin" /><p>Cargando contactos...</p>
        </div>

        <div v-else-if="!store.personas.length" class="empty-state glass">
          <div class="empty-icon"><Icon name="people" :size="48" color="var(--primary)" /></div>
          <h3>No hay contactos</h3>
          <p>Registra tu primer contacto para comenzar a gestionar relaciones comerciales.</p>
          <Button variant="primary" icon="plus" @click="openCreatePersona">Crear Contacto</Button>
        </div>

        <div v-else class="master-detail">
          <!-- LEFT: Table -->
          <div class="table-wrapper glass">
            <table class="data-table">
              <thead>
                <tr>
                  <th>Nombre</th>
                  <th>Email</th>
                  <th>Teléfono</th>
                  <th>Empresas</th>
                  <th>Estado</th>
                  <th class="th-actions"></th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="persona in store.personas" :key="persona.id" class="data-row" :class="{ 'data-row--selected': selectedId === persona.id, 'data-row--inactive': !persona.activo }" @click="selectPersona(persona.id)">
                  <td>
                    <div class="cell-persona">
                      <div class="persona-avatar">{{ getInitials(persona.nombres, persona.apellidos) }}</div>
                      <span class="cell-persona__name">{{ persona.nombreCompleto }}</span>
                    </div>
                  </td>
                  <td><span v-if="persona.email" class="cell-email">{{ persona.email }}</span><span v-else class="cell-empty">—</span></td>
                  <td><span v-if="persona.telefono" class="cell-mono">{{ persona.telefono }}</span><span v-else class="cell-empty">—</span></td>
                  <td><span class="badge badge--primary" v-if="persona.empresas?.length">{{ persona.empresas.length }}</span><span v-else class="cell-empty">0</span></td>
                  <td><span class="badge" :class="persona.activo ? 'badge--success' : 'badge--muted'">{{ persona.activo ? 'ACTIVO' : 'INACTIVO' }}</span></td>
                  <td class="td-actions"><Button variant="ghost" icon="settings" icon-only size="sm" @click.stop="openEditPersona(persona)" /></td>
                </tr>
              </tbody>
            </table>
            <div v-if="store.pagination.totalPages > 1" class="pagination">
              <span class="pagination-total">{{ store.pagination.totalItems }} contacto{{ store.pagination.totalItems !== 1 ? 's' : '' }}</span>
              <div class="pagination-controls">
                <button class="pagination-btn" :disabled="store.pagination.page <= 1" @click="store.setPagina(store.pagination.page - 1)"><Icon name="chevron-left" :size="16" /></button>
                <span class="pagination-info">{{ store.pagination.page }} / {{ store.pagination.totalPages }}</span>
                <button class="pagination-btn" :disabled="store.pagination.page >= store.pagination.totalPages" @click="store.setPagina(store.pagination.page + 1)"><Icon name="chevron-right" :size="16" /></button>
              </div>
            </div>
          </div>

          <!-- RIGHT: Detail -->
          <div class="detail-panel" v-if="selectedPersona">
            <Card>
              <template #default>
                <div class="detail-header">
                  <div class="detail-avatar-lg">{{ getInitials(selectedPersona.nombres, selectedPersona.apellidos) }}</div>
                  <div class="detail-header__info">
                    <h2 class="detail-header__name">{{ selectedPersona.nombreCompleto }}</h2>
                    <div class="detail-header__meta">
                      <span v-if="selectedPersona.email" class="detail-meta-item"><Icon name="user" :size="12" /> {{ selectedPersona.email }}</span>
                      <span v-if="selectedPersona.telefono" class="detail-meta-item"><Icon name="bell" :size="12" /> {{ selectedPersona.telefono }}</span>
                    </div>
                  </div>
                </div>
              </template>
            </Card>

            <!-- Empresas Asociadas -->
            <div class="empresas-section">
              <div class="empresas-section__header">
                <h3 class="empresas-section__title"><Icon name="business" :size="18" color="var(--secondary)" /> Empresas Asociadas</h3>
                <Button variant="secondary" icon="plus" size="sm" @click="openAsociarEmpresa">Asociar</Button>
              </div>

              <div v-if="!selectedPersona.empresas?.length" class="empresas-empty glass">
                <p>Este contacto no está asociado a ninguna empresa.</p>
              </div>

              <div v-else class="empresas-list">
                <div v-for="pe in selectedPersona.empresas" :key="pe.id" class="empresa-assoc glass">
                  <div class="empresa-assoc__header">
                    <span class="empresa-assoc__name">{{ pe.empresaRazonSocial }}</span>
                    <div class="empresa-assoc__actions">
                      <span v-if="pe.esContactoPrincipal" class="badge badge--primary">Principal</span>
                      <button class="btn-desasociar" @click="confirmDesasociar(pe)" title="Desasociar de esta empresa">
                        <Icon name="x" :size="14" />
                      </button>
                    </div>
                  </div>
                  <div class="empresa-assoc__details">
                    <span v-if="pe.cargo" class="assoc-tag"><Icon name="user" :size="11" /> {{ pe.cargo }}</span>
                    <span v-if="pe.puesto" class="assoc-tag"><Icon name="business" :size="11" /> {{ pe.puesto }}</span>
                    <span v-if="pe.rolContacto" class="assoc-tag assoc-tag--rol">{{ pe.rolContacto }}</span>
                  </div>
                  <div v-if="pe.emailEmpresarial || pe.telefonoEmpresarial" class="empresa-assoc__contact">
                    <span v-if="pe.emailEmpresarial">{{ pe.emailEmpresarial }}</span>
                    <span v-if="pe.telefonoEmpresarial">{{ pe.telefonoEmpresarial }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- Confirm Desasociar -->
      <Teleport to="body">
        <Transition name="modal">
          <div v-if="showConfirmDesasociar" class="modal-overlay" @click.self="showConfirmDesasociar = false">
            <div class="confirm-dialog animate-scaleIn">
              <div class="confirm-dialog__icon"><Icon name="alert-circle" :size="32" color="var(--warning)" /></div>
              <h3 class="confirm-dialog__title">Desasociar Empresa</h3>
              <p class="confirm-dialog__text">¿Estás seguro de desasociar a <strong>{{ selectedPersona?.nombreCompleto }}</strong> de <strong>{{ desasociarTarget?.empresaRazonSocial }}</strong>?</p>
              <div class="confirm-dialog__actions">
                <Button variant="ghost" @click="showConfirmDesasociar = false">Cancelar</Button>
                <Button variant="danger" icon="x" :loading="store.saving" @click="doDesasociar">Desasociar</Button>
              </div>
            </div>
          </div>
        </Transition>
      </Teleport>

      <Transition name="toast">
        <div v-if="store.error" class="error-toast" @click="store.limpiarError()">
          <Icon name="alert-circle" :size="16" /> {{ store.error }}
          <Icon name="x" :size="14" class="error-toast__close" />
        </div>
      </Transition>

      <PersonaModal :visible="showPersonaModal" :persona="editingPersona" :saving="store.saving" :error="modalError" @close="closePersonaModal" @submit="handlePersonaSubmit" />
      <AsociarEmpresaModal :visible="showAsociarModal" :empresas="store.empresasDisponibles" :loading-empresas="store.loadingEmpresas" :saving="store.saving" :error="modalError" @close="closeAsociarModal" @submit="handleAsociarSubmit" />
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import AppLayout from '@/components/layout/AppLayout.vue';
import Card from '@/components/ui/Card.vue';
import Button from '@/components/ui/Button.vue';
import Icon from '@/components/ui/Icon.vue';
import PersonaModal from '@/components/persona/PersonaModal.vue';
import AsociarEmpresaModal from '@/components/persona/AsociarEmpresaModal.vue';
import { usePersonaStore } from '@/stores/persona.store';

const store = usePersonaStore();
const searchQuery = ref('');
const selectedId = ref(null);
const showPersonaModal = ref(false);
const editingPersona = ref(null);
const showAsociarModal = ref(false);
const showConfirmDesasociar = ref(false);
const desasociarTarget = ref(null);
const modalError = ref(null);
let searchTimeout = null;

const selectedPersona = computed(() => store.personaSeleccionada);
onMounted(() => { store.cargarPersonas(); });

function onSearch() { clearTimeout(searchTimeout); searchTimeout = setTimeout(() => { store.setFiltros({ q: searchQuery.value }); }, 400); }
function clearSearch() { searchQuery.value = ''; store.setFiltros({ q: '' }); }
function selectPersona(id) { selectedId.value = id; store.seleccionarPersona(id); }

function openCreatePersona() { editingPersona.value = null; modalError.value = null; showPersonaModal.value = true; }
function openEditPersona(persona) { editingPersona.value = { ...persona }; modalError.value = null; showPersonaModal.value = true; }
function closePersonaModal() { showPersonaModal.value = false; editingPersona.value = null; modalError.value = null; }

async function handlePersonaSubmit(payload) {
  modalError.value = null;
  try {
    if (editingPersona.value) {
      await store.actualizarPersona(editingPersona.value.id, payload);
      if (selectedId.value === editingPersona.value.id) store.seleccionarPersona(selectedId.value);
    } else {
      const nueva = await store.crearPersona(payload);
      selectPersona(nueva.id);
    }
    closePersonaModal();
  } catch (err) { modalError.value = err.response?.data?.message || 'Error al guardar contacto'; }
}

async function openAsociarEmpresa() { modalError.value = null; await store.cargarEmpresasDisponibles(); showAsociarModal.value = true; }
function closeAsociarModal() { showAsociarModal.value = false; modalError.value = null; }
async function handleAsociarSubmit(payload) {
  modalError.value = null;
  try { await store.asociarEmpresa(selectedId.value, payload); closeAsociarModal(); }
  catch (err) { modalError.value = err.response?.data?.message || 'Error al asociar empresa'; }
}

// Desasociar
function confirmDesasociar(pe) { desasociarTarget.value = pe; showConfirmDesasociar.value = true; }
async function doDesasociar() {
  try {
    await store.desasociarEmpresa(selectedId.value, desasociarTarget.value.empresaId);
    showConfirmDesasociar.value = false;
    desasociarTarget.value = null;
  } catch (err) { /* error already handled in store */ }
}

function getInitials(nombres, apellidos) {
  const n = nombres ? nombres[0] : '';
  const a = apellidos ? apellidos[0] : '';
  return (n + a).toUpperCase() || '?';
}
</script>

<style scoped>
.personas-view { display: flex; flex-direction: column; gap: var(--space-6); }
.personas-view__header { display: flex; justify-content: space-between; align-items: flex-start; gap: var(--space-4); }
.page-title { font-family: var(--font-display); font-size: var(--text-3xl); font-weight: 700; margin: 0; line-height: 1.2; }
.page-subtitle { color: var(--text-tertiary); font-size: var(--text-sm); margin: var(--space-1) 0 0; }
.personas-view__filters { display: flex; gap: var(--space-4); align-items: center; }
.search-wrapper { position: relative; display: flex; align-items: center; flex: 1; min-width: 240px; max-width: 400px; }
.search-wrapper > :first-child { position: absolute; left: var(--space-4); pointer-events: none; }
.search-input { width: 100%; background: var(--glass-bg); border: 1px solid var(--glass-border); border-radius: var(--radius-lg); color: var(--text-primary); font-family: var(--font-body); font-size: var(--text-sm); padding: var(--space-3) var(--space-4) var(--space-3) calc(var(--space-4) + 24px); transition: all 0.2s; box-sizing: border-box; }
.search-input:focus { outline: none; border-color: var(--primary); box-shadow: 0 0 0 3px var(--primary-soft); }
.search-input::placeholder { color: var(--text-muted); }
.search-clear { position: absolute; right: var(--space-3); background: none; border: none; color: var(--text-muted); cursor: pointer; display: flex; padding: var(--space-1); }

.loading-state { display: flex; flex-direction: column; align-items: center; justify-content: center; gap: var(--space-3); padding: var(--space-8); color: var(--text-tertiary); font-size: var(--text-sm); }
.empty-state { display: flex; flex-direction: column; align-items: center; justify-content: center; gap: var(--space-4); padding: calc(var(--space-8) * 2); border-radius: var(--radius-xl); text-align: center; }
.empty-icon { width: 80px; height: 80px; display: flex; align-items: center; justify-content: center; background: var(--primary-soft); border-radius: var(--radius-xl); }
.empty-state h3 { color: var(--text-primary); font-family: var(--font-display); margin: 0; }
.empty-state p { color: var(--text-tertiary); font-size: var(--text-sm); max-width: 320px; margin: 0; }

.master-detail { display: grid; grid-template-columns: 1fr 380px; gap: var(--space-6); }
.table-wrapper { border-radius: var(--radius-xl); overflow: hidden; }
.data-table { width: 100%; border-collapse: collapse; }
.data-table thead th { text-align: left; padding: var(--space-3) var(--space-4); font-family: var(--font-body); font-size: var(--text-xs); font-weight: 600; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.05em; border-bottom: 1px solid var(--glass-border); white-space: nowrap; }
.data-table tbody td { padding: var(--space-3) var(--space-4); font-size: var(--text-sm); color: var(--text-primary); border-bottom: 1px solid rgba(255,255,255,0.03); vertical-align: middle; }
.data-row { transition: background 0.15s; cursor: pointer; }
.data-row:hover { background: rgba(255,255,255,0.02); }
.data-row--selected { background: var(--primary-soft); }
.data-row--inactive { opacity: 0.5; }
.th-actions, .td-actions { text-align: right; width: 50px; }

.cell-persona { display: flex; align-items: center; gap: var(--space-3); }
.persona-avatar { width: 32px; height: 32px; min-width: 32px; border-radius: var(--radius-full); display: flex; align-items: center; justify-content: center; font-family: var(--font-display); font-size: 11px; font-weight: 700; background: var(--secondary-soft); color: var(--secondary); }
.cell-persona__name { font-weight: 600; white-space: nowrap; }
.cell-email { font-size: var(--text-xs); color: var(--text-secondary); }
.cell-mono { font-family: var(--font-mono); font-size: var(--text-xs); color: var(--text-secondary); }
.cell-empty { color: var(--text-muted); font-size: var(--text-xs); }

.badge { display: inline-flex; align-items: center; padding: 1px var(--space-2); border-radius: var(--radius-full); font-size: 10px; font-weight: 600; text-transform: uppercase; letter-spacing: 0.04em; }
.badge--success { background: rgba(16,185,129,0.15); color: var(--success); }
.badge--muted { background: rgba(255,255,255,0.06); color: var(--text-muted); }
.badge--primary { background: var(--primary-soft); color: var(--primary); }

.pagination { display: flex; align-items: center; justify-content: space-between; padding: var(--space-3) var(--space-4); border-top: 1px solid var(--glass-border); }
.pagination-total { font-size: var(--text-xs); color: var(--text-muted); }
.pagination-controls { display: flex; align-items: center; gap: var(--space-3); }
.pagination-btn { background: var(--glass-bg); border: 1px solid var(--glass-border); border-radius: var(--radius-sm); color: var(--text-secondary); padding: var(--space-2); cursor: pointer; display: flex; transition: all 0.15s; }
.pagination-btn:hover:not(:disabled) { background: rgba(255,255,255,0.06); }
.pagination-btn:disabled { opacity: 0.3; cursor: not-allowed; }
.pagination-info { font-size: var(--text-xs); color: var(--text-muted); font-family: var(--font-mono); }

.detail-panel { display: flex; flex-direction: column; gap: var(--space-5); }
.detail-header { display: flex; align-items: center; gap: var(--space-4); }
.detail-avatar-lg { width: 56px; height: 56px; min-width: 56px; border-radius: var(--radius-full); display: flex; align-items: center; justify-content: center; font-family: var(--font-display); font-size: 18px; font-weight: 700; background: var(--secondary-soft); color: var(--secondary); }
.detail-header__name { font-family: var(--font-display); font-size: var(--text-xl); font-weight: 700; color: var(--text-primary); margin: 0; }
.detail-header__meta { display: flex; flex-direction: column; gap: 2px; margin-top: var(--space-1); }
.detail-meta-item { display: flex; align-items: center; gap: 4px; font-size: var(--text-xs); color: var(--text-secondary); }

.empresas-section { display: flex; flex-direction: column; gap: var(--space-3); }
.empresas-section__header { display: flex; justify-content: space-between; align-items: center; }
.empresas-section__title { display: flex; align-items: center; gap: var(--space-2); font-family: var(--font-display); font-size: var(--text-base); font-weight: 600; color: var(--text-primary); margin: 0; }
.empresas-empty { padding: var(--space-6); border-radius: var(--radius-lg); text-align: center; color: var(--text-muted); font-size: var(--text-sm); }
.empresas-empty p { margin: 0; }
.empresas-list { display: flex; flex-direction: column; gap: var(--space-3); }

.empresa-assoc { padding: var(--space-4); border-radius: var(--radius-lg); }
.empresa-assoc__header { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--space-2); }
.empresa-assoc__name { font-weight: 600; font-size: var(--text-sm); color: var(--text-primary); }
.empresa-assoc__actions { display: flex; align-items: center; gap: var(--space-2); }
.btn-desasociar { background: none; border: 1px solid transparent; border-radius: var(--radius-sm); color: var(--text-muted); padding: 4px; cursor: pointer; display: flex; transition: all 0.2s; }
.btn-desasociar:hover { color: var(--error); border-color: rgba(244,63,94,0.3); background: rgba(244,63,94,0.1); }
.empresa-assoc__details { display: flex; flex-wrap: wrap; gap: var(--space-2); }
.assoc-tag { display: flex; align-items: center; gap: 3px; font-size: var(--text-xs); color: var(--text-secondary); background: var(--bg-surface); padding: 2px var(--space-2); border-radius: var(--radius-full); }
.assoc-tag--rol { color: var(--primary); background: var(--primary-soft); font-weight: 600; }
.empresa-assoc__contact { display: flex; flex-direction: column; gap: 2px; margin-top: var(--space-2); padding-top: var(--space-2); border-top: 1px solid var(--glass-border); font-size: var(--text-xs); color: var(--text-muted); }

/* Confirm Dialog */
.confirm-dialog { background: var(--bg-elevated); border: 1px solid var(--glass-border); border-radius: var(--radius-xl); padding: var(--space-8); max-width: 400px; width: 100%; text-align: center; box-shadow: 0 25px 60px rgba(0,0,0,0.5); }
.confirm-dialog__icon { margin-bottom: var(--space-4); }
.confirm-dialog__title { font-family: var(--font-display); font-size: var(--text-lg); font-weight: 600; color: var(--text-primary); margin: 0 0 var(--space-2); }
.confirm-dialog__text { font-size: var(--text-sm); color: var(--text-secondary); margin: 0 0 var(--space-6); line-height: 1.5; }
.confirm-dialog__text strong { color: var(--text-primary); }
.confirm-dialog__actions { display: flex; justify-content: center; gap: var(--space-3); }

.error-toast { position: fixed; bottom: var(--space-6); right: var(--space-6); display: flex; align-items: center; gap: var(--space-3); padding: var(--space-4) var(--space-5); background: rgba(244,63,94,0.15); border: 1px solid rgba(244,63,94,0.3); border-radius: var(--radius-lg); color: var(--error); font-size: var(--text-sm); cursor: pointer; z-index: 900; backdrop-filter: blur(12px); }
.error-toast__close { opacity: 0.5; }
.toast-enter-active { transition: all 0.3s ease; } .toast-leave-active { transition: all 0.2s ease; }
.toast-enter-from { transform: translateY(20px); opacity: 0; } .toast-leave-to { transform: translateY(10px); opacity: 0; }
.modal-enter-active { transition: opacity 0.25s ease; } .modal-leave-active { transition: opacity 0.2s ease; } .modal-enter-from, .modal-leave-to { opacity: 0; }
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.7); backdrop-filter: blur(8px); display: flex; align-items: center; justify-content: center; z-index: 1000; padding: var(--space-4); }

@media (max-width: 1000px) { .master-detail { grid-template-columns: 1fr; } }
</style>
