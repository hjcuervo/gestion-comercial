<template>
  <Teleport to="body">
    <div class="panel-overlay" @click.self="$emit('close')">
      <div class="panel glass animate-slideLeft">
        <!-- Header -->
        <div class="panel__header">
          <div>
            <h2 class="panel__title">{{ fp.formaPagoDescripcion }}</h2>
            <p class="panel__subtitle">{{ fp.empresaNombre }} · {{ fp.contratoNumeroInterno || fp.contratoCliente }}</p>
          </div>
          <button class="panel__close" @click="$emit('close')"><Icon name="x" :size="18" /></button>
        </div>

        <!-- Info -->
        <div class="panel__info">
          <div class="info-row">
            <span class="info-label">Valor presupuestado</span>
            <span class="info-value info-value--primary">{{ fmtCurrencyMoneda(fp.valor, fp.moneda) }}</span>
          </div>
          <div v-if="fp.valorFacturado" class="info-row">
            <span class="info-label">Valor facturado</span>
            <span class="info-value info-value--success">{{ fmtCurrencyMoneda(fp.valorFacturado, fp.moneda) }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">Periodo</span>
            <span class="info-value">{{ fmtMesAnio(fp.anio, fp.mes) }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">Estado</span>
            <span :class="['estado-pill', `estado-pill--${fp.estado?.toLowerCase()}`]">{{ fp.estado }}</span>
          </div>
          <div v-if="fp.moneda === 'USD' && fp.valorCop" class="info-row">
            <span class="info-label">Equivalente COP</span>
            <span class="info-value">{{ fmtCurrency(fp.valorCop) }}</span>
          </div>
        </div>

        <!-- Bitácora -->
        <div class="panel__section">
          <div class="section-header-panel">
            <h3 class="section-title-panel"><Icon name="clock" :size="14" color="var(--primary)" /> Bitácora</h3>
            <button class="btn btn--primary btn--sm" @click="showGestionModal = true"><Icon name="plus" :size="14" /> Registrar</button>
          </div>
          <div v-if="loadingGestiones" class="mini-loading"><Icon name="loader" :size="16" class="animate-spin" /></div>
          <div v-else-if="!gestiones.length" class="mini-empty">Sin gestiones registradas</div>
          <div v-else class="gestiones-list">
            <div v-for="g in gestiones" :key="g.id" class="gestion-item">
              <div class="gestion-item__header">
                <span :class="['gestion-tipo', isNovedad(g.tipoGestion) ? 'gestion-tipo--novedad' : 'gestion-tipo--normal']">{{ tipoLabel(g.tipoGestion) }}</span>
                <span class="gestion-fecha">{{ fmtDate(g.fechaGestion) }}</span>
              </div>
              <p class="gestion-desc">{{ g.descripcion }}</p>
            </div>
          </div>
        </div>

        <!-- Factura cruzada -->
        <div v-if="fp.facturaId" class="panel__section">
          <h3 class="section-title-panel"><Icon name="external-link" :size="14" color="var(--success)" /> Factura Cruzada</h3>
          <div class="factura-info">
            <span class="factura-num">Factura #{{ fp.facturaId }}</span>
          </div>
        </div>
      </div>
    </div>
  </Teleport>

  <!-- Modal Registrar Gestión -->
  <Teleport to="body">
    <div v-if="showGestionModal" class="modal-overlay" @click.self="showGestionModal = false">
      <div class="modal glass animate-slideUp">
        <div class="modal__header">
          <h2 class="modal__title">Registrar Gestión</h2>
          <button class="modal__close" @click="showGestionModal = false"><Icon name="x" :size="18" /></button>
        </div>
        <form class="modal__body" @submit.prevent="submitGestion">
          <div class="field">
            <label class="field__label">Tipo de Gestión <span class="req">*</span></label>
            <select v-model="gestionForm.tipoGestion" class="field__select" required>
              <option value="" disabled>Seleccione</option>
              <optgroup label="Gestión Normal">
                <option value="VALIDACION_SERVICIO">Validación de servicio prestado</option>
                <option value="SOPORTE_OBTENIDO">Soporte obtenido (acta/entregable)</option>
                <option value="SOLICITUD_EMISION">Solicitud de emisión en FACTRO</option>
                <option value="OBSERVACION">Observación general</option>
              </optgroup>
              <optgroup label="Novedades">
                <option value="NOVEDAD_APLAZAMIENTO">Aplazamiento</option>
                <option value="NOVEDAD_CAMBIO_VALOR">Cambio de valor</option>
                <option value="NOVEDAD_SERVICIO_INCOMPLETO">Servicio incompleto</option>
                <option value="NOVEDAD_SUSPENSION">Suspensión del contrato</option>
                <option value="NOVEDAD_OTRA">Otra novedad</option>
              </optgroup>
            </select>
          </div>
          <div class="field">
            <label class="field__label">Descripción <span class="req">*</span></label>
            <textarea v-model="gestionForm.descripcion" class="field__textarea" rows="3" required maxlength="1000" placeholder="Detalle de la gestión realizada..."></textarea>
          </div>
          <div class="field">
            <label class="field__label">Fecha <span class="req">*</span></label>
            <input v-model="gestionForm.fechaGestion" type="date" class="field__input" required />
          </div>
          <div v-if="gestionError" class="modal-error"><Icon name="alert-circle" :size="14" /> {{ gestionError }}</div>
          <div class="modal__actions">
            <button type="button" class="btn btn--ghost" @click="showGestionModal = false">Cancelar</button>
            <button type="submit" class="btn btn--primary" :disabled="gestionSaving">{{ gestionSaving ? 'Guardando...' : 'Registrar' }}</button>
          </div>
        </form>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue';
import Icon from '@/components/ui/Icon.vue';
import { facturacionService } from '@/services/facturacion.service';

const props = defineProps({ formaPago: { type: Object, required: true } });
const emit = defineEmits(['close', 'updated']);

const fp = ref(props.formaPago);
const gestiones = ref([]);
const loadingGestiones = ref(false);

const showGestionModal = ref(false);
const gestionForm = ref({ tipoGestion: '', descripcion: '', fechaGestion: new Date().toISOString().split('T')[0] });
const gestionError = ref(null);
const gestionSaving = ref(false);

watch(() => props.formaPago, (v) => { fp.value = v; loadGestiones(); });
onMounted(() => { loadGestiones(); });

async function loadGestiones() {
  loadingGestiones.value = true;
  try { gestiones.value = await facturacionService.listarGestiones(fp.value.formaPagoId); }
  catch (err) { console.error(err); }
  finally { loadingGestiones.value = false; }
}

async function submitGestion() {
  gestionError.value = null;
  gestionSaving.value = true;
  try {
    await facturacionService.registrarGestion(fp.value.formaPagoId, gestionForm.value);
    showGestionModal.value = false;
    gestionForm.value = { tipoGestion: '', descripcion: '', fechaGestion: new Date().toISOString().split('T')[0] };
    await loadGestiones();
    emit('updated');
  } catch (err) {
    gestionError.value = err.response?.data?.message || 'Error al registrar gestión';
  } finally { gestionSaving.value = false; }
}

const meses = ['', 'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'];

const tipoLabels = {
  VALIDACION_SERVICIO: 'Validación servicio', SOPORTE_OBTENIDO: 'Soporte obtenido',
  SOLICITUD_EMISION: 'Solicitud emisión', FACTURA_CRUZADA: 'Factura cruzada',
  NOVEDAD_APLAZAMIENTO: 'Aplazamiento', NOVEDAD_CAMBIO_VALOR: 'Cambio valor',
  NOVEDAD_SERVICIO_INCOMPLETO: 'Servicio incompleto', NOVEDAD_SUSPENSION: 'Suspensión',
  NOVEDAD_OTRA: 'Otra novedad', OBSERVACION: 'Observación',
};

function tipoLabel(t) { return tipoLabels[t] || t; }
function isNovedad(t) { return t?.startsWith('NOVEDAD_'); }
function fmtMesAnio(a, m) { return meses[m] + ' ' + a; }
function fmtDate(d) { if (!d) return '—'; return new Date(d).toLocaleDateString('es-CO', { day: '2-digit', month: 'short', year: 'numeric' }); }
function fmtCurrency(v) { if (v == null) return '$0'; return new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }).format(v); }
function fmtCurrencyMoneda(v, m) { if (v == null) return '$0'; if (m === 'USD') return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD', maximumFractionDigits: 0 }).format(v); return fmtCurrency(v); }
</script>

<style scoped>
.panel-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.5); backdrop-filter: blur(2px); z-index: 1000; display: flex; justify-content: flex-end; }
.panel { width: 480px; max-width: 90vw; height: 100vh; border-radius: 0; padding: var(--space-5); overflow-y: auto; display: flex; flex-direction: column; gap: var(--space-4); animation: slideLeft 0.25s ease; }
@keyframes slideLeft { from { transform: translateX(100%); } to { transform: translateX(0); } }
.panel__header { display: flex; justify-content: space-between; align-items: flex-start; gap: var(--space-3); }
.panel__title { font-family: var(--font-display); font-size: var(--text-lg); font-weight: 700; color: var(--text-primary); margin: 0; }
.panel__subtitle { font-size: var(--text-xs); color: var(--text-muted); margin: var(--space-1) 0 0; }
.panel__close { background: none; border: none; color: var(--text-muted); cursor: pointer; padding: 4px; border-radius: var(--radius-sm); } .panel__close:hover { color: var(--text-primary); background: var(--glass-hover); }
.panel__info { display: flex; flex-direction: column; gap: var(--space-2); padding: var(--space-3); background: var(--bg-surface); border-radius: var(--radius-lg); }
.info-row { display: flex; justify-content: space-between; align-items: center; }
.info-label { font-size: 10px; color: var(--text-muted); text-transform: uppercase; font-weight: 600; }
.info-value { font-size: var(--text-xs); color: var(--text-primary); font-weight: 500; }
.info-value--primary { color: var(--primary); font-weight: 700; font-family: var(--font-mono, monospace); }
.info-value--success { color: var(--success); font-weight: 700; font-family: var(--font-mono, monospace); }
.estado-pill { display: inline-block; padding: 1px 8px; border-radius: var(--radius-full); font-size: 10px; font-weight: 600; }
.estado-pill--pendiente { background: var(--warning-soft); color: var(--warning); }
.estado-pill--facturada { background: var(--success-soft); color: var(--success); }
.panel__section { display: flex; flex-direction: column; gap: var(--space-3); }
.section-header-panel { display: flex; justify-content: space-between; align-items: center; }
.section-title-panel { display: flex; align-items: center; gap: var(--space-2); font-family: var(--font-display); font-size: var(--text-xs); font-weight: 600; color: var(--text-primary); margin: 0; text-transform: uppercase; letter-spacing: 0.5px; }
.mini-loading { display: flex; align-items: center; justify-content: center; padding: var(--space-3); color: var(--text-muted); }
.mini-empty { font-size: var(--text-xs); color: var(--text-muted); padding: var(--space-3); text-align: center; font-style: italic; }
.gestiones-list { display: flex; flex-direction: column; gap: var(--space-2); }
.gestion-item { padding: var(--space-3); background: var(--bg-surface); border-radius: var(--radius-md); }
.gestion-item__header { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--space-1); }
.gestion-tipo { font-size: 9px; font-weight: 700; padding: 1px 6px; border-radius: var(--radius-full); text-transform: uppercase; }
.gestion-tipo--normal { background: var(--primary-soft); color: var(--primary); }
.gestion-tipo--novedad { background: var(--warning-soft); color: var(--warning); }
.gestion-fecha { font-size: 9px; color: var(--text-muted); }
.gestion-desc { font-size: var(--text-xs); color: var(--text-secondary); line-height: 1.5; margin: 0; }
.factura-info { padding: var(--space-3); background: var(--bg-surface); border-radius: var(--radius-md); }
.factura-num { font-size: var(--text-xs); font-weight: 600; color: var(--primary); }
/* Modal */
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.6); backdrop-filter: blur(4px); display: flex; align-items: center; justify-content: center; z-index: 1100; padding: var(--space-4); }
.modal { width: 100%; max-width: 480px; border-radius: var(--radius-xl); padding: var(--space-6); max-height: 90vh; overflow-y: auto; }
.modal__header { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--space-4); }
.modal__title { font-family: var(--font-display); font-size: var(--text-lg); font-weight: 700; color: var(--text-primary); margin: 0; }
.modal__close { background: none; border: none; color: var(--text-muted); cursor: pointer; padding: 4px; border-radius: var(--radius-sm); } .modal__close:hover { color: var(--text-primary); }
.modal__body { display: flex; flex-direction: column; gap: var(--space-4); }
.field { display: flex; flex-direction: column; gap: var(--space-1); }
.field__label { font-size: var(--text-xs); font-weight: 600; color: var(--text-secondary); } .req { color: var(--error); }
.field__input, .field__select { background: var(--bg-surface); border: 1px solid var(--glass-border); border-radius: var(--radius-md); color: var(--text-primary); font-family: var(--font-body); font-size: var(--text-xs); padding: var(--space-2) var(--space-3); }
.field__select { appearance: none; background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 24 24' fill='none' stroke='rgba(255,255,255,0.3)' stroke-width='2'%3E%3Cpath d='M6 9l6 6 6-6'/%3E%3C/svg%3E"); background-repeat: no-repeat; background-position: right 10px center; padding-right: 30px; }
.field__select option, .field__select optgroup { background: var(--bg-elevated); }
.field__input:focus, .field__select:focus { outline: none; border-color: var(--primary); }
.field__textarea { background: var(--bg-surface); border: 1px solid var(--glass-border); border-radius: var(--radius-md); color: var(--text-primary); font-family: var(--font-body); font-size: var(--text-xs); padding: var(--space-3); resize: vertical; } .field__textarea:focus { outline: none; border-color: var(--primary); }
.modal-error { display: flex; align-items: center; gap: var(--space-2); padding: var(--space-3); background: var(--error-soft); border-radius: var(--radius-md); color: var(--error); font-size: var(--text-xs); }
.modal__actions { display: flex; justify-content: flex-end; gap: var(--space-3); padding-top: var(--space-3); border-top: 1px solid var(--glass-border); }
.btn { display: flex; align-items: center; gap: var(--space-2); padding: var(--space-2) var(--space-4); border-radius: var(--radius-full); font-family: var(--font-body); font-size: var(--text-xs); font-weight: 600; cursor: pointer; transition: all 0.15s; border: 1px solid transparent; }
.btn--sm { padding: var(--space-1) var(--space-3); font-size: 11px; }
.btn--ghost { background: transparent; color: var(--text-secondary); border-color: var(--glass-border); } .btn--ghost:hover { background: var(--glass-hover); }
.btn--primary { background: var(--primary); color: #000; } .btn--primary:hover { box-shadow: 0 0 20px var(--primary-glow); } .btn--primary:disabled { opacity: 0.5; cursor: not-allowed; }
</style>
