<template>
  <AppLayout>
    <div class="contrato-detalle">
      <div v-if="loading" class="loading-state"><Icon name="loader" :size="32" class="animate-spin" /><p>Cargando contrato...</p></div>

      <template v-else-if="contrato">
        <!-- Header -->
        <section class="detalle-header animate-slideUp">
          <div class="header-left">
            <button class="back-btn" @click="$router.push('/contratos')"><Icon name="chevron-left" :size="18" /> Contratos</button>
            <h1 class="page-title gradient-text">{{ contrato.numeroContratoInterno || contrato.oportunidadNombre }}</h1>
            <p class="page-subtitle">{{ contrato.empresaNombre }} · {{ contrato.tipoContratoNombre }}</p>
          </div>
          <span :class="['estado-badge', `estado-badge--${contrato.estado?.toLowerCase()}`]">{{ estadoLabel }}</span>
        </section>

        <!-- KPIs -->
        <section class="kpi-row animate-slideUp delay-1">
          <div class="kpi glass"><span class="kpi__label">Valor Contrato</span><span class="kpi__value kpi__value--primary">{{ fmtCurrency(contrato.valorContrato) }}</span></div>
          <div class="kpi glass"><span class="kpi__label">Ajustes</span><span class="kpi__value" :class="contrato.valorAjuste > 0 ? 'kpi__value--success' : ''">{{ fmtCurrency(contrato.valorAjuste) }}</span></div>
          <div class="kpi glass"><span class="kpi__label">Valor Total</span><span class="kpi__value kpi__value--accent">{{ fmtCurrency(contrato.valorTotal) }}</span></div>
          <div class="kpi glass"><span class="kpi__label">Días restantes</span><span class="kpi__value" :class="diasRestantes < 30 ? 'kpi__value--warning' : ''">{{ diasRestantesLabel }}</span></div>
        </section>

        <!-- Actions -->
        <section v-if="contrato.estado === 'VIGENTE' || contrato.estado === 'SUSPENDIDO' || contrato.estado === 'TERMINADO'" class="actions-row animate-slideUp delay-1">
          <button v-if="contrato.estado === 'VIGENTE'" class="btn btn--warning btn--sm" @click="cambiarEstado('suspender')"><Icon name="pause" :size="14" /> Suspender</button>
          <button v-if="contrato.estado === 'SUSPENDIDO'" class="btn btn--success btn--sm" @click="cambiarEstado('reiniciar')"><Icon name="play" :size="14" /> Reiniciar</button>
          <button v-if="contrato.estado === 'VIGENTE'" class="btn btn--accent btn--sm" @click="cambiarEstado('terminar')"><Icon name="check-circle" :size="14" /> Terminar</button>
          <button v-if="contrato.estado === 'TERMINADO'" class="btn btn--primary btn--sm" @click="cambiarEstado('liquidar')"><Icon name="trophy" :size="14" /> Liquidar</button>
        </section>

        <!-- Datos generales -->
        <section class="info-section animate-slideUp delay-2">
          <div class="info-card glass">
            <h3 class="section-title"><Icon name="note-add" :size="16" color="var(--primary)" /> Datos del Contrato</h3>
            <div class="info-grid">
              <div class="info-field"><span class="info-label">Nº Interno</span><span class="info-value">{{ contrato.numeroContratoInterno || '—' }}</span></div>
              <div class="info-field"><span class="info-label">Nº Cliente</span><span class="info-value">{{ contrato.numeroContratoCliente || '—' }}</span></div>
              <div class="info-field"><span class="info-label">Moneda</span><span class="info-value">{{ contrato.moneda }}</span></div>
              <div class="info-field"><span class="info-label">Fecha Inicio</span><span class="info-value">{{ fmtDate(contrato.fechaInicio) }}</span></div>
              <div class="info-field"><span class="info-label">Fecha Fin</span><span class="info-value">{{ fmtDate(contrato.fechaFin) }}</span></div>
              <div class="info-field"><span class="info-label">Responsable</span><span class="info-value">{{ contrato.responsableGestion || '—' }}</span></div>
              <div class="info-field"><span class="info-label">Interventor</span><span class="info-value">{{ contrato.interventor || '—' }}</span></div>
              <div class="info-field"><span class="info-label">Oportunidad</span><span class="info-value info-value--link" @click="$router.push(`/oportunidades/${contrato.oportunidadId}`)">{{ contrato.oportunidadNombre }}</span></div>
              <div v-if="contrato.empresaFacturacionNombre" class="info-field"><span class="info-label">Empresa Facturación</span><span class="info-value">{{ contrato.empresaFacturacionNombre }}</span></div>
            </div>
            <div v-if="contrato.objeto" class="info-objeto"><span class="info-label">Objeto</span><p class="info-text">{{ contrato.objeto }}</p></div>
            <div v-if="contrato.observaciones" class="info-objeto"><span class="info-label">Observaciones</span><p class="info-text">{{ contrato.observaciones }}</p></div>
          </div>
        </section>

        <!-- Documentos del contrato -->
        <section class="doc-section animate-slideUp delay-2">
          <div class="section-header">
            <h3 class="section-title"><Icon name="external-link" :size="16" color="var(--secondary)" /> Documentos <span class="section-count">{{ documentos.length }}</span></h3>
            <button class="btn btn--ghost btn--sm" @click="showDocModal = true; docModalTarget = { contratoId: Number(contrato.id) }"><Icon name="plus" :size="14" /> Agregar</button>
          </div>
          <div v-if="!documentos.length" class="mini-empty">Sin documentos asociados. Agregue contratos firmados, pólizas, etc.</div>
          <div v-else class="docs-list">
            <div v-for="doc in documentos" :key="doc.id" class="doc-card glass">
              <div class="doc-card__icon"><Icon name="external-link" :size="16" /></div>
              <div class="doc-card__info">
                <a :href="doc.urlStorage" target="_blank" rel="noopener" class="doc-card__name">{{ doc.nombre }}</a>
                <span class="doc-card__meta">{{ getTipoDocNombre(doc.tipoDocumentoId) }} · {{ fmtDate(doc.fechaCarga) }}</span>
              </div>
              <button class="doc-card__delete" @click="eliminarDocumento(doc.id)" title="Eliminar"><Icon name="x" :size="14" /></button>
            </div>
          </div>
        </section>

        <!-- Formas de Pago -->
        <section class="fp-section animate-slideUp delay-3">
          <div class="section-header">
            <h3 class="section-title"><Icon name="wallet" :size="16" color="var(--success)" /> Formas de Pago</h3>
            <button v-if="contrato.estado === 'VIGENTE'" class="btn btn--ghost btn--sm" @click="showFpModal = true"><Icon name="plus" :size="14" /> Agregar</button>
          </div>
          <div v-if="!formasPago.length" class="mini-empty">Sin formas de pago registradas</div>
          <div v-else class="fp-table-wrap glass">
            <table class="data-table">
              <thead><tr><th>Descripción</th><th class="text-right">Valor</th><th>Fecha Estimada</th><th>Estado</th></tr></thead>
              <tbody>
                <tr v-for="fp in formasPago" :key="fp.id">
                  <td><span class="cell-name">{{ fp.descripcion }}</span></td>
                  <td class="text-right"><span class="cell-valor">{{ fmtCurrency(fp.valor) }}</span></td>
                  <td><span class="cell-fecha">{{ fmtDate(fp.fechaEstimadaPago) }}</span></td>
                  <td><span :class="['estado-pill', `estado-pill--${fp.estado?.toLowerCase()}`]">{{ fp.estado }}</span></td>
                </tr>
              </tbody>
            </table>
            <div class="fp-summary">
              <span>Total: <strong>{{ fmtCurrency(sumaFormasPago) }}</strong></span>
              <span>Pendiente: <strong>{{ fmtCurrency((contrato.valorTotal || 0) - sumaFormasPago) }}</strong></span>
            </div>
          </div>
        </section>

        <!-- Modificaciones -->
        <section class="mod-section animate-slideUp delay-3">
          <div class="section-header">
            <h3 class="section-title"><Icon name="edit" :size="16" color="var(--secondary)" /> Modificaciones</h3>
            <button v-if="contrato.estado === 'VIGENTE'" class="btn btn--ghost btn--sm" @click="showModModal = true"><Icon name="plus" :size="14" /> Registrar</button>
          </div>
          <div v-if="!modificaciones.length" class="mini-empty">Sin modificaciones registradas</div>
          <div v-else class="mod-list">
            <div v-for="mod in modificaciones" :key="mod.id" class="mod-card glass">
              <div class="mod-header">
                <span :class="['mod-tipo', `mod-tipo--${mod.tipoModificacion?.toLowerCase()}`]">{{ mod.tipoModificacion }}</span>
                <span class="mod-fecha">{{ fmtDate(mod.fechaModificacion) }}</span>
              </div>
              <div class="mod-body">
                <span v-if="mod.modificaValor" class="mod-detail"><Icon name="trending-up" :size="12" /> Valor: {{ fmtCurrency(mod.valorModificacion) }}</span>
                <span v-if="mod.modificaTiempo" class="mod-detail"><Icon name="clock" :size="12" /> Nueva fecha fin: {{ fmtDate(mod.nuevaFechaFin) }}</span>
                <p v-if="mod.observaciones" class="mod-obs">{{ mod.observaciones }}</p>
              </div>
              <!-- Documentos de la modificación -->
              <div class="mod-docs">
                <div class="mod-docs__header">
                  <span class="mod-docs__title"><Icon name="external-link" :size="12" /> Documentos</span>
                  <button class="mod-docs__add" @click="showDocModal = true; docModalTarget = { modificacionId: mod.id }" title="Agregar documento soporte">
                    <Icon name="plus" :size="12" /> Agregar
                  </button>
                </div>
                <div v-if="modDocs[mod.id]?.length" class="mod-docs__list">
                  <div v-for="doc in modDocs[mod.id]" :key="doc.id" class="mod-doc-item">
                    <a :href="doc.urlStorage" target="_blank" rel="noopener" class="mod-doc-item__name"><Icon name="external-link" :size="10" /> {{ doc.nombre }}</a>
                    <button class="mod-doc-item__del" @click="eliminarDocMod(doc.id, mod.id)"><Icon name="x" :size="12" /></button>
                  </div>
                </div>
                <span v-else class="mod-docs__empty">Sin documentos soporte</span>
              </div>
            </div>
          </div>
        </section>
      </template>

      <!-- Modal Documento (reutilizable) -->
      <DocumentoModal v-if="showDocModal"
        :contrato-id="docModalTarget.contratoId || null"
        :modificacion-id="docModalTarget.modificacionId || null"
        @close="showDocModal = false; docModalTarget = {}"
        @created="onDocCreated" />

      <!-- Modal Forma de Pago -->
      <Teleport to="body">
        <div v-if="showFpModal" class="modal-overlay" @click.self="showFpModal = false">
          <div class="modal glass animate-slideUp">
            <div class="modal__header"><h2 class="modal__title">Agregar Forma de Pago</h2><button class="modal__close" @click="showFpModal = false"><Icon name="x" :size="18" /></button></div>
            <form class="modal__body" @submit.prevent="submitFp">
              <div class="field"><label class="field__label">Descripción <span class="req">*</span></label><input v-model="fpForm.descripcion" type="text" class="field__input" required maxlength="500" placeholder="Ej: Pago mes 1" /></div>
              <div class="form-row">
                <div class="field"><label class="field__label">Valor <span class="req">*</span></label><input v-model="fpForm.valor" type="number" step="0.01" class="field__input" required /></div>
                <div class="field"><label class="field__label">Fecha Estimada</label><input v-model="fpForm.fechaEstimadaPago" type="date" class="field__input" /></div>
              </div>
              <div v-if="fpError" class="modal-error"><Icon name="alert-circle" :size="14" /> {{ fpError }}</div>
              <div class="modal__actions">
                <button type="button" class="btn btn--ghost" @click="showFpModal = false">Cancelar</button>
                <button type="submit" class="btn btn--primary" :disabled="fpSaving">{{ fpSaving ? 'Guardando...' : 'Agregar' }}</button>
              </div>
            </form>
          </div>
        </div>
      </Teleport>

      <!-- Modal Modificación -->
      <Teleport to="body">
        <div v-if="showModModal" class="modal-overlay" @click.self="showModModal = false">
          <div class="modal glass animate-slideUp">
            <div class="modal__header"><h2 class="modal__title">Registrar Modificación</h2><button class="modal__close" @click="showModModal = false"><Icon name="x" :size="18" /></button></div>
            <form class="modal__body" @submit.prevent="submitMod">
              <div class="form-row">
                <div class="field"><label class="field__label">Tipo <span class="req">*</span></label>
                  <select v-model="modForm.tipoModificacion" class="field__select" required>
                    <option value="" disabled>Seleccione</option>
                    <option value="ADICION">Adición</option><option value="PRORROGA">Prórroga</option><option value="SUSPENSION">Suspensión</option><option value="REINICIO">Reinicio</option><option value="OTRO">Otro</option>
                  </select>
                </div>
                <div class="field"><label class="field__label">Fecha</label><input v-model="modForm.fechaModificacion" type="date" class="field__input" required /></div>
              </div>
              <div class="form-row">
                <div class="field"><label class="field__label"><input type="checkbox" v-model="modForm.modificaValor" /> Modifica valor</label>
                  <input v-if="modForm.modificaValor" v-model="modForm.valorModificacion" type="number" step="0.01" class="field__input" placeholder="Valor" /></div>
                <div class="field"><label class="field__label"><input type="checkbox" v-model="modForm.modificaTiempo" /> Modifica tiempo</label>
                  <input v-if="modForm.modificaTiempo" v-model="modForm.nuevaFechaFin" type="date" class="field__input" /></div>
              </div>
              <div class="field"><label class="field__label">Observaciones</label><textarea v-model="modForm.observaciones" class="field__textarea" rows="2" maxlength="1000"></textarea></div>
              <div v-if="modError" class="modal-error"><Icon name="alert-circle" :size="14" /> {{ modError }}</div>
              <div class="modal__actions">
                <button type="button" class="btn btn--ghost" @click="showModModal = false">Cancelar</button>
                <button type="submit" class="btn btn--primary" :disabled="modSaving">{{ modSaving ? 'Guardando...' : 'Registrar' }}</button>
              </div>
            </form>
          </div>
        </div>
      </Teleport>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import AppLayout from '@/components/layout/AppLayout.vue';
import Icon from '@/components/ui/Icon.vue';
import DocumentoModal from '@/components/documento/DocumentoModal.vue';
import { contratoService } from '@/services/contrato.service';
import { documentoService } from '@/services/documento.service';
import { formatCurrency } from '@/utils/currency';

const route = useRoute();
const contrato = ref(null);
const formasPago = ref([]);
const modificaciones = ref([]);
const loading = ref(true);

// Documentos del contrato
const documentos = ref([]);
const tiposDocumento = ref([]);
const showDocModal = ref(false);
const docModalTarget = ref({}); // { contratoId } o { modificacionId }

// Documentos por modificación
const modDocs = reactive({});

// Modals
const showFpModal = ref(false);
const fpForm = ref({ descripcion: '', valor: null, fechaEstimadaPago: '' });
const fpError = ref(null);
const fpSaving = ref(false);

const showModModal = ref(false);
const modForm = ref({ tipoModificacion: '', fechaModificacion: '', modificaValor: false, valorModificacion: null, modificaTiempo: false, nuevaFechaFin: '', observaciones: '' });
const modError = ref(null);
const modSaving = ref(false);

const estadoLabel = computed(() => ({ VIGENTE: 'Vigente', SUSPENDIDO: 'Suspendido', TERMINADO: 'Terminado', LIQUIDADO: 'Liquidado' }[contrato.value?.estado] || contrato.value?.estado));
const diasRestantes = computed(() => { if (!contrato.value?.fechaFin) return 999; return Math.floor((new Date(contrato.value.fechaFin) - new Date()) / 86400000); });
const diasRestantesLabel = computed(() => { if (!contrato.value?.fechaFin) return '—'; const d = diasRestantes.value; return d < 0 ? `${Math.abs(d)}d vencido` : `${d} días`; });
const sumaFormasPago = computed(() => formasPago.value.reduce((sum, fp) => sum + (fp.valor || 0), 0));

onMounted(async () => {
  try {
    contrato.value = await contratoService.obtenerPorId(route.params.id);
    formasPago.value = contrato.value.formasPago || [];
    modificaciones.value = contrato.value.modificaciones || [];
    if (!formasPago.value.length) { try { formasPago.value = await contratoService.listarFormasPago(route.params.id); } catch {} }
    if (!modificaciones.value.length) { try { modificaciones.value = await contratoService.listarModificaciones(route.params.id); } catch {} }
  } catch (err) { console.error('Error cargando contrato:', err); }
  finally { loading.value = false; }

  try { tiposDocumento.value = await documentoService.listarTipos(); } catch {}
  await loadDocumentos();
  await loadAllModDocs();
});

function fmtCurrency(v) { return formatCurrency(v, contrato.value?.moneda || 'COP'); }
function fmtDate(d) { if (!d) return '—'; return new Date(d).toLocaleDateString('es-CO', { day: '2-digit', month: 'short', year: 'numeric' }); }
function getTipoDocNombre(tipoId) { const t = tiposDocumento.value.find(t => t.id === tipoId); return t ? t.nombre : 'Documento'; }

// Documentos del contrato
async function loadDocumentos() {
  if (!contrato.value) return;
  try { documentos.value = await documentoService.listarPorContrato(contrato.value.id); } catch {}
}

// Documentos de cada modificación
async function loadAllModDocs() {
  for (const mod of modificaciones.value) {
    try { modDocs[mod.id] = await documentoService.listarPorModificacion(mod.id); }
    catch { modDocs[mod.id] = []; }
  }
}

async function loadModDocs(modId) {
  try { modDocs[modId] = await documentoService.listarPorModificacion(modId); }
  catch { modDocs[modId] = []; }
}

async function onDocCreated() {
  showDocModal.value = false;
  const target = docModalTarget.value;
  docModalTarget.value = {};
  if (target.modificacionId) await loadModDocs(target.modificacionId);
  else await loadDocumentos();
}

async function eliminarDocumento(id) {
  if (!confirm('¿Eliminar este documento?')) return;
  try { await documentoService.eliminar(id); await loadDocumentos(); } catch {}
}

async function eliminarDocMod(docId, modId) {
  if (!confirm('¿Eliminar este documento?')) return;
  try { await documentoService.eliminar(docId); await loadModDocs(modId); } catch {}
}

async function cambiarEstado(accion) {
  if (!confirm(`¿Está seguro de ${accion} este contrato?`)) return;
  try { contrato.value = await contratoService[accion](contrato.value.id); } catch (err) { alert(err.response?.data?.message || 'Error'); }
}

async function submitFp() {
  fpError.value = null; fpSaving.value = true;
  try {
    const created = await contratoService.crearFormaPago(contrato.value.id, { descripcion: fpForm.value.descripcion, valor: fpForm.value.valor, fechaEstimadaPago: fpForm.value.fechaEstimadaPago || null });
    formasPago.value.push(created);
    showFpModal.value = false;
    fpForm.value = { descripcion: '', valor: null, fechaEstimadaPago: '' };
  } catch (err) { fpError.value = err.response?.data?.message || 'Error'; }
  finally { fpSaving.value = false; }
}

async function submitMod() {
  modError.value = null; modSaving.value = true;
  try {
    const created = await contratoService.crearModificacion(contrato.value.id, {
      tipoModificacion: modForm.value.tipoModificacion, fechaModificacion: modForm.value.fechaModificacion,
      modificaValor: modForm.value.modificaValor, valorModificacion: modForm.value.modificaValor ? modForm.value.valorModificacion : null,
      modificaTiempo: modForm.value.modificaTiempo, nuevaFechaFin: modForm.value.modificaTiempo ? modForm.value.nuevaFechaFin : null,
      observaciones: modForm.value.observaciones || null,
    });
    modificaciones.value.unshift(created);
    modDocs[created.id] = [];
    showModModal.value = false;
    modForm.value = { tipoModificacion: '', fechaModificacion: '', modificaValor: false, valorModificacion: null, modificaTiempo: false, nuevaFechaFin: '', observaciones: '' };
    contrato.value = await contratoService.obtenerPorId(route.params.id);
  } catch (err) { modError.value = err.response?.data?.message || 'Error'; }
  finally { modSaving.value = false; }
}
</script>

<style scoped>
.contrato-detalle { display: flex; flex-direction: column; gap: var(--space-5); }
.loading-state { display: flex; flex-direction: column; align-items: center; justify-content: center; gap: var(--space-3); padding: var(--space-8); color: var(--text-tertiary); }
.detalle-header { display: flex; justify-content: space-between; align-items: flex-start; gap: var(--space-4); }
.back-btn { display: flex; align-items: center; gap: var(--space-1); background: none; border: none; color: var(--text-muted); font-size: var(--text-xs); cursor: pointer; padding: 0; margin-bottom: var(--space-2); } .back-btn:hover { color: var(--primary); }
.page-title { font-family: var(--font-display); font-size: var(--text-2xl); font-weight: 700; margin: 0; }
.page-subtitle { font-size: var(--text-xs); color: var(--text-tertiary); margin: var(--space-1) 0 0; }
.estado-badge { padding: var(--space-1) var(--space-4); border-radius: var(--radius-full); font-size: var(--text-xs); font-weight: 700; }
.estado-badge--vigente { background: var(--success-soft); color: var(--success); } .estado-badge--suspendido { background: var(--warning-soft); color: var(--warning); } .estado-badge--terminado { background: var(--accent-soft); color: var(--accent); } .estado-badge--liquidado { background: var(--primary-soft); color: var(--primary); }
.kpi-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: var(--space-4); }
.kpi { padding: var(--space-4); border-radius: var(--radius-lg); display: flex; flex-direction: column; gap: var(--space-1); }
.kpi__label { font-size: 10px; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.5px; font-weight: 600; }
.kpi__value { font-family: var(--font-display); font-size: var(--text-xl); font-weight: 700; color: var(--text-primary); }
.kpi__value--primary { color: var(--primary); } .kpi__value--success { color: var(--success); } .kpi__value--accent { color: var(--accent); } .kpi__value--warning { color: var(--warning); }
.actions-row { display: flex; gap: var(--space-3); }
.info-card { padding: var(--space-5); border-radius: var(--radius-xl); }
.section-title { display: flex; align-items: center; gap: var(--space-2); font-family: var(--font-display); font-size: var(--text-sm); font-weight: 600; color: var(--text-primary); margin: 0; }
.section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--space-3); }
.section-count { font-size: var(--text-xs); color: var(--text-muted); font-weight: 400; background: var(--bg-surface); padding: 1px 8px; border-radius: var(--radius-full); margin-left: var(--space-1); }
.info-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: var(--space-4); margin-top: var(--space-4); }
.info-field { display: flex; flex-direction: column; gap: 2px; }
.info-label { font-size: 10px; color: var(--text-muted); text-transform: uppercase; font-weight: 600; letter-spacing: 0.3px; }
.info-value { font-size: var(--text-xs); color: var(--text-primary); } .info-value--link { color: var(--primary); cursor: pointer; } .info-value--link:hover { text-decoration: underline; }
.info-objeto { margin-top: var(--space-4); } .info-text { font-size: var(--text-xs); color: var(--text-secondary); line-height: 1.6; margin: var(--space-1) 0 0; }
.mini-empty { font-size: var(--text-xs); color: var(--text-muted); padding: var(--space-4); text-align: center; }
/* Docs */
.docs-list { display: flex; flex-direction: column; gap: var(--space-2); }
.doc-card { display: flex; align-items: center; gap: var(--space-3); padding: var(--space-3) var(--space-4); border-radius: var(--radius-lg); transition: background 0.15s; } .doc-card:hover { background: rgba(255,255,255,0.02); }
.doc-card__icon { width: 32px; height: 32px; border-radius: var(--radius-md); background: var(--secondary-soft); color: var(--secondary); display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.doc-card__info { flex: 1; min-width: 0; }
.doc-card__name { display: block; font-size: var(--text-xs); font-weight: 600; color: var(--primary); text-decoration: none; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; } .doc-card__name:hover { text-decoration: underline; }
.doc-card__meta { display: block; font-size: 10px; color: var(--text-muted); margin-top: 1px; }
.doc-card__delete { background: none; border: none; color: var(--text-muted); cursor: pointer; padding: 4px; border-radius: var(--radius-sm); transition: all 0.15s; display: flex; flex-shrink: 0; } .doc-card__delete:hover { color: var(--error); background: var(--error-soft); }
/* Tables */
.fp-table-wrap { border-radius: var(--radius-lg); overflow: hidden; }
.data-table { width: 100%; border-collapse: collapse; }
.data-table th { font-size: 10px; font-weight: 600; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.5px; padding: var(--space-3) var(--space-4); text-align: left; border-bottom: 1px solid var(--glass-border); background: var(--bg-surface); }
.data-table .text-right { text-align: right; }
.data-table td { padding: var(--space-3) var(--space-4); border-bottom: 1px solid rgba(255,255,255,0.03); }
.cell-name { font-size: var(--text-xs); color: var(--text-primary); } .cell-valor { font-family: var(--font-mono, monospace); font-size: var(--text-xs); font-weight: 600; color: var(--primary); } .cell-fecha { font-size: var(--text-xs); color: var(--text-tertiary); }
.estado-pill { display: inline-block; padding: 2px 8px; border-radius: var(--radius-full); font-size: 10px; font-weight: 600; } .estado-pill--pendiente { background: var(--warning-soft); color: var(--warning); } .estado-pill--facturada { background: var(--success-soft); color: var(--success); }
.fp-summary { display: flex; justify-content: space-between; padding: var(--space-3) var(--space-4); font-size: var(--text-xs); color: var(--text-muted); border-top: 1px solid var(--glass-border); } .fp-summary strong { color: var(--text-primary); font-family: var(--font-mono, monospace); }
/* Modificaciones */
.mod-list { display: flex; flex-direction: column; gap: var(--space-3); }
.mod-card { padding: var(--space-4); border-radius: var(--radius-lg); }
.mod-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--space-2); }
.mod-tipo { font-size: 10px; font-weight: 700; padding: 2px 8px; border-radius: var(--radius-full); text-transform: uppercase; }
.mod-tipo--adicion { background: var(--success-soft); color: var(--success); } .mod-tipo--prorroga { background: var(--primary-soft); color: var(--primary); } .mod-tipo--suspension { background: var(--warning-soft); color: var(--warning); } .mod-tipo--reinicio { background: var(--accent-soft); color: var(--accent); } .mod-tipo--otro { background: var(--glass-bg); color: var(--text-secondary); }
.mod-fecha { font-size: 10px; color: var(--text-muted); }
.mod-body { display: flex; flex-direction: column; gap: var(--space-1); }
.mod-detail { display: flex; align-items: center; gap: var(--space-2); font-size: var(--text-xs); color: var(--text-secondary); }
.mod-obs { font-size: var(--text-xs); color: var(--text-muted); margin: var(--space-1) 0 0; line-height: 1.5; }
/* Docs dentro de modificación */
.mod-docs { margin-top: var(--space-3); padding-top: var(--space-3); border-top: 1px solid rgba(255,255,255,0.04); }
.mod-docs__header { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--space-2); }
.mod-docs__title { font-size: 10px; font-weight: 600; color: var(--text-muted); display: flex; align-items: center; gap: 4px; }
.mod-docs__add { display: flex; align-items: center; gap: 3px; background: none; border: none; color: var(--text-muted); font-family: var(--font-body); font-size: 10px; cursor: pointer; padding: 2px 6px; border-radius: var(--radius-full); transition: all 0.15s; } .mod-docs__add:hover { color: var(--primary); background: var(--primary-soft); }
.mod-docs__list { display: flex; flex-direction: column; gap: var(--space-1); }
.mod-doc-item { display: flex; align-items: center; justify-content: space-between; padding: 2px var(--space-2); border-radius: var(--radius-sm); transition: background 0.15s; } .mod-doc-item:hover { background: rgba(255,255,255,0.02); }
.mod-doc-item__name { font-size: 11px; color: var(--primary); text-decoration: none; display: flex; align-items: center; gap: 4px; } .mod-doc-item__name:hover { text-decoration: underline; }
.mod-doc-item__del { background: none; border: none; color: var(--text-muted); cursor: pointer; padding: 2px; border-radius: var(--radius-sm); display: flex; } .mod-doc-item__del:hover { color: var(--error); }
.mod-docs__empty { font-size: 10px; color: var(--text-muted); font-style: italic; }
/* Modals */
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.6); backdrop-filter: blur(4px); display: flex; align-items: center; justify-content: center; z-index: 1000; padding: var(--space-4); }
.modal { width: 100%; max-width: 520px; border-radius: var(--radius-xl); padding: var(--space-6); max-height: 90vh; overflow-y: auto; }
.modal__header { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--space-4); }
.modal__title { font-family: var(--font-display); font-size: var(--text-lg); font-weight: 700; color: var(--text-primary); margin: 0; }
.modal__close { background: none; border: none; color: var(--text-muted); cursor: pointer; padding: 4px; border-radius: var(--radius-sm); } .modal__close:hover { color: var(--text-primary); background: var(--glass-hover); }
.modal__body { display: flex; flex-direction: column; gap: var(--space-4); }
.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: var(--space-3); }
.field { display: flex; flex-direction: column; gap: var(--space-1); }
.field__label { font-size: var(--text-xs); font-weight: 600; color: var(--text-secondary); display: flex; align-items: center; gap: var(--space-2); } .field__label input[type="checkbox"] { accent-color: var(--primary); }
.req { color: var(--error); }
.field__input, .field__select { background: var(--bg-surface); border: 1px solid var(--glass-border); border-radius: var(--radius-md); color: var(--text-primary); font-family: var(--font-body); font-size: var(--text-xs); padding: var(--space-2) var(--space-3); }
.field__select { appearance: none; background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 24 24' fill='none' stroke='rgba(255,255,255,0.3)' stroke-width='2'%3E%3Cpath d='M6 9l6 6 6-6'/%3E%3C/svg%3E"); background-repeat: no-repeat; background-position: right 10px center; padding-right: 30px; } .field__select option { background: var(--bg-elevated); }
.field__input:focus, .field__select:focus { outline: none; border-color: var(--primary); }
.field__textarea { background: var(--bg-surface); border: 1px solid var(--glass-border); border-radius: var(--radius-md); color: var(--text-primary); font-family: var(--font-body); font-size: var(--text-xs); padding: var(--space-3); resize: vertical; } .field__textarea:focus { outline: none; border-color: var(--primary); }
.modal-error { display: flex; align-items: center; gap: var(--space-2); padding: var(--space-3); background: var(--error-soft); border-radius: var(--radius-md); color: var(--error); font-size: var(--text-xs); }
.modal__actions { display: flex; justify-content: flex-end; gap: var(--space-3); padding-top: var(--space-3); border-top: 1px solid var(--glass-border); }
.btn { display: flex; align-items: center; gap: var(--space-2); padding: var(--space-2) var(--space-4); border-radius: var(--radius-full); font-family: var(--font-body); font-size: var(--text-xs); font-weight: 600; cursor: pointer; transition: all 0.15s; border: 1px solid transparent; }
.btn--sm { padding: var(--space-1) var(--space-3); font-size: 11px; }
.btn--ghost { background: transparent; color: var(--text-secondary); border-color: var(--glass-border); } .btn--ghost:hover { border-color: var(--primary); color: var(--primary); }
.btn--primary { background: var(--primary); color: #000; } .btn--primary:hover { box-shadow: 0 0 20px var(--primary-glow); } .btn--primary:disabled { opacity: 0.5; cursor: not-allowed; }
.btn--success { background: var(--success-soft); color: var(--success); border-color: rgba(16,185,129,0.3); }
.btn--warning { background: var(--warning-soft); color: var(--warning); border-color: rgba(245,158,11,0.3); }
.btn--accent { background: var(--accent-soft); color: var(--accent); border-color: rgba(168,85,247,0.3); }
@media (max-width: 900px) { .kpi-row { grid-template-columns: repeat(2, 1fr); } .info-grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 600px) { .kpi-row { grid-template-columns: 1fr; } .info-grid { grid-template-columns: 1fr; } }
</style>
