<template>
  <GcDrawer :open="true" :title="detalle?.descripcion || 'Cargando…'" :subtitle="subtitle" width="560px" @close="$emit('close')">
    <div v-if="loadingDetalle" class="gp__loading"><GcSpinner :size="20" /></div>

    <template v-else-if="detalle">
      <!-- Estado + flags -->
      <div class="gp__estado">
        <GcBadge :tone="estadoTone(detalle.estado)" :label="estadoLabel(detalle.estado)" />
        <span v-if="detalle.enRiesgo" class="gp__flag gp__flag--riesgo"><GcIcon name="alert-circle" :size="12" /> En riesgo</span>
        <span v-if="detalle.tipo === 'RECURRENTE'" class="gp__flag"><GcIcon name="refresh" :size="12" /> Recurrente</span>
      </div>

      <!-- Montos -->
      <div class="gp__montos">
        <div class="gp__cell"><span class="gp__lbl">Presupuestado</span><span class="gp__val gc-mono">{{ fmtMoneda(detalle.montoPresupuestado, detalle.moneda) }}</span><span class="gp__tag">inmutable</span></div>
        <div class="gp__cell"><span class="gp__lbl">Esperado actual</span><span class="gp__val gp__val--primary gc-mono">{{ fmtMoneda(detalle.montoEsperadoActual, detalle.moneda) }}</span><span v-if="esperadoDifierePresupuesto" class="gp__tag gp__tag--warn">ajustado</span></div>
        <div class="gp__cell"><span class="gp__lbl">Facturado</span><span class="gp__val gp__val--ok gc-mono">{{ fmtMoneda(detalle.montoFacturadoAcumulado, detalle.moneda) }}</span></div>
        <div class="gp__cell"><span class="gp__lbl">Saldo pendiente</span><span class="gp__val gp__val--warn gc-mono">{{ fmtMoneda(detalle.saldoPendiente, detalle.moneda) }}</span></div>
      </div>

      <div class="gp__progress">
        <div class="gp__track"><div class="gp__bar" :style="{ width: pctCumplimiento + '%' }"></div></div>
        <span class="gp__pct gc-mono">{{ pctCumplimiento }}% cumplido</span>
      </div>

      <div class="gp__fecharow">
        <span class="gp__lbl">Fecha esperada</span>
        <span class="gp__val gc-mono">{{ fmtFecha(detalle.fechaEsperadaActual) }}<span v-if="fechaDifiereOriginal" class="gp__tag gp__tag--warn">reprog. de {{ fmtFecha(detalle.fechaEsperadaOriginal) }}</span></span>
      </div>
      <div v-if="detalle.motivoPerdida" class="gp__motivo"><span class="gp__lbl">Motivo no logrado</span><span>{{ detalle.motivoPerdida }}</span></div>

      <!-- Acciones -->
      <div class="gp__actions">
        <GcButton v-if="!detalle.estadoFinal" variant="primary" full-width icon="circle-check" @click="showCruceModal = true">Aplicar factura</GcButton>
        <GcButton v-if="detalle.estadoFinal" variant="default" full-width icon="rotate" @click="ejecutarTransicion({ action: 'reactivar' })">Reactivar compromiso</GcButton>
        <div v-if="!detalle.estadoFinal && (puedeReprogramar || puedeAjustarMonto)" class="gp__ajustes">
          <span class="gp__ajusteslbl">Ajustes excepcionales</span>
          <div class="gp__ajustesrow">
            <button v-if="puedeReprogramar" class="gp__textbtn" @click="abrirComando('reprogramar')"><GcIcon name="calendar" :size="14" /> Reprogramar fecha</button>
            <button v-if="puedeAjustarMonto" class="gp__textbtn" @click="abrirComando('ajustarMonto')"><GcIcon name="edit" :size="14" /> Ajustar monto</button>
          </div>
        </div>
      </div>

      <!-- Tabs -->
      <div class="gp__tabs">
        <button :class="['gp__tab', tab === 'gestiones' && 'gp__tab--on']" @click="tab = 'gestiones'">Gestiones <span class="gc-mono">{{ gestiones.length }}</span></button>
        <button :class="['gp__tab', tab === 'eventos' && 'gp__tab--on']" @click="tab = 'eventos'">Eventos <span class="gc-mono">{{ eventos.length }}</span></button>
        <button :class="['gp__tab', tab === 'facturas' && 'gp__tab--on']" @click="tab = 'facturas'">Facturas <span class="gc-mono">{{ aplicacionesActivas.length }}</span></button>
      </div>

      <!-- Gestiones -->
      <div v-if="tab === 'gestiones'" class="gp__section">
        <div class="gp__sechead">
          <h3 class="gp__h3">Bitácora humana</h3>
          <GcButton variant="default" size="sm" icon="plus" @click="showGestionModal = true">Registrar</GcButton>
        </div>
        <div v-if="loadingGestiones" class="gp__mini"><GcSpinner :size="16" /></div>
        <GcEmpty v-else-if="!gestiones.length" icon="notes" message="Sin gestiones registradas" />
        <div v-else class="gp__bitacora">
          <div v-for="g in gestiones" :key="g.id" class="gp__bitem">
            <div class="gp__bhead">
              <GcBadge :tone="tipoGestionTone(g.tipoGestion)" :label="tipoGestionLabel(g.tipoGestion)" />
              <span class="gp__bfecha gc-mono">{{ fmtFecha(g.fechaGestion) }}</span>
            </div>
            <p class="gp__bdesc">{{ g.descripcion }}</p>
          </div>
        </div>
      </div>

      <!-- Eventos -->
      <div v-if="tab === 'eventos'" class="gp__section">
        <h3 class="gp__h3">Bitácora sistémica</h3>
        <div v-if="loadingEventos" class="gp__mini"><GcSpinner :size="16" /></div>
        <GcEmpty v-else-if="!eventos.length" icon="activity" message="Sin eventos registrados" />
        <div v-else class="gp__bitacora">
          <div v-for="e in eventos" :key="e.id" class="gp__bitem">
            <div class="gp__bhead">
              <span class="gp__evtipo">{{ tipoEventoLabel(e.tipoEvento) }}</span>
              <span class="gp__bfecha gc-mono">{{ fmtFechaHora(e.fechaEvento) }}</span>
            </div>
            <div class="gp__evdiff gc-mono">
              <span v-if="e.estadoAnterior && e.estadoNuevo">{{ estadoLabel(e.estadoAnterior) }} → <strong>{{ estadoLabel(e.estadoNuevo) }}</strong></span>
              <span v-if="e.montoAnterior != null && e.montoNuevo != null">{{ fmtMoneda(e.montoAnterior, detalle.moneda) }} → <strong>{{ fmtMoneda(e.montoNuevo, detalle.moneda) }}</strong></span>
              <span v-if="e.fechaAnterior && e.fechaNueva">{{ fmtFecha(e.fechaAnterior) }} → <strong>{{ fmtFecha(e.fechaNueva) }}</strong></span>
            </div>
            <p v-if="e.motivo" class="gp__bdesc">{{ e.motivo }}</p>
          </div>
        </div>
      </div>

      <!-- Facturas -->
      <div v-if="tab === 'facturas'" class="gp__section">
        <h3 class="gp__h3">Facturas aplicadas</h3>
        <GcEmpty v-if="!aplicacionesActivas.length" icon="file-off" message="Sin facturas aplicadas todavía" />
        <div v-else class="gp__aplic">
          <div v-for="ap in aplicacionesActivas" :key="ap.compromisoFacturaId" class="gp__apitem">
            <div class="gp__apinfo">
              <span class="gp__apmonto gc-mono">{{ fmtMoneda(ap.montoNuevo, detalle.moneda) }}</span>
              <span class="gp__apfecha gc-mono">{{ fmtFechaHora(ap.fechaEvento) }}</span>
            </div>
            <GcButton variant="ghost" size="sm" icon="x" :disabled="detalle.estadoFinal" @click="abrirRevertir(ap)">Revertir</GcButton>
          </div>
        </div>
      </div>
    </template>

    <!-- Modal Registrar Gestión -->
    <GcModal v-if="showGestionModal" :open="true" title="Registrar gestión" width="480px" @close="showGestionModal = false">
      <div class="gpm">
        <div class="gpm__field">
          <label class="gpm__label">Tipo de gestión *</label>
          <select v-model="gestionForm.tipoGestion" class="gpm__select">
            <option value="" disabled>Seleccione</option>
            <optgroup label="Gestión normal">
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
        <GcTextarea v-model="gestionForm.descripcion" label="Descripción *" :rows="3" placeholder="Detalle de la gestión realizada…" />
        <GcInput v-model="gestionForm.fechaGestion" label="Fecha *" type="date" />
        <div v-if="gestionError" class="gpm__error"><GcIcon name="alert-circle" :size="14" /> {{ gestionError }}</div>
      </div>
      <template #footer>
        <GcButton variant="ghost" @click="showGestionModal = false">Cancelar</GcButton>
        <GcButton variant="primary" :loading="gestionSaving" @click="submitGestion">Registrar</GcButton>
      </template>
    </GcModal>

    <!-- Modal Aplicar Factura -->
    <GcModal v-if="showCruceModal" :open="true" title="Aplicar factura" width="560px" @close="showCruceModal = false">
      <div class="gpm">
        <div class="gpm__tabs">
          <button :class="['gpm__tab', cruceTab === 'existente' && 'gpm__tab--on']" @click="cruceTab = 'existente'">Factura existente</button>
          <button :class="['gpm__tab', cruceTab === 'nueva' && 'gpm__tab--on']" @click="cruceTab = 'nueva'">Registrar nueva</button>
        </div>

        <div v-if="cruceTab === 'existente'">
          <div v-if="loadingFacturas" class="gp__mini"><GcSpinner :size="16" /></div>
          <GcEmpty v-else-if="!facturas.length" icon="file-off" message="No hay facturas registradas. Registra una nueva." />
          <div v-else class="gpm__facturas">
            <div v-for="f in facturas" :key="f.id" :class="['gpm__factura', selectedFacturaId === f.id && 'gpm__factura--on']" @click="selectedFacturaId = f.id">
              <div class="gpm__fleft">
                <span class="gpm__fnum gc-mono">{{ f.prefijo ? f.prefijo + '-' : '' }}{{ f.numeroFactura }}</span>
                <span class="gpm__fmeta">{{ f.empresaNombre }} · {{ fmtFecha(f.fechaEmision) }}</span>
              </div>
              <div class="gpm__fright">
                <span class="gpm__fvalor gc-mono">{{ fmtMoneda(f.valorTotal, f.moneda) }}</span>
                <span v-if="f.saldoDisponible != null && Number(f.saldoDisponible) !== Number(f.valorTotal)" class="gpm__fsaldo gc-mono">Saldo: {{ fmtMoneda(f.saldoDisponible, f.moneda) }}</span>
              </div>
            </div>
          </div>
          <div v-if="selectedFacturaId" class="gpm__field" style="margin-top: var(--gc-space-3)">
            <label class="gpm__label">Monto a aplicar a esta forma de pago *</label>
            <input v-model.number="montoAplicado" type="number" step="0.01" min="0.01" class="gpm__input" :placeholder="'Saldo: ' + detalle?.saldoPendiente" />
            <span class="gpm__hint">No puede exceder el valor total de la factura (RN-14)</span>
          </div>
        </div>

        <div v-if="cruceTab === 'nueva'">
          <div class="gpm__row">
            <GcInput v-model="nuevaFactura.prefijo" label="Prefijo" placeholder="Ej: ARQBS" />
            <GcInput v-model="nuevaFactura.numeroFactura" label="Número factura *" placeholder="Ej: 4521" />
          </div>
          <div class="gpm__field">
            <label class="gpm__label">Valor total (incluido IVA) *</label>
            <input v-model.number="nuevaFactura.valorTotal" type="number" step="0.01" min="0.01" class="gpm__input" />
          </div>
          <GcInput v-model="nuevaFactura.fechaEmision" label="Fecha emisión *" type="date" />
          <div class="gpm__field">
            <label class="gpm__label">Monto a aplicar a esta forma de pago *</label>
            <input v-model.number="montoAplicado" type="number" step="0.01" min="0.01" class="gpm__input" />
          </div>
        </div>

        <div v-if="cruceError" class="gpm__error"><GcIcon name="alert-circle" :size="14" /> {{ cruceError }}</div>
      </div>
      <template #footer>
        <GcButton variant="ghost" @click="showCruceModal = false">Cancelar</GcButton>
        <GcButton v-if="cruceTab === 'existente'" variant="primary" :disabled="!selectedFacturaId || !montoAplicado" :loading="cruceSaving" @click="aplicarExistente">Aplicar</GcButton>
        <GcButton v-else variant="primary" :loading="cruceSaving" @click="submitNuevaYAplicar">Registrar y aplicar</GcButton>
      </template>
    </GcModal>

    <!-- Modal Comando -->
    <GcModal v-if="comando.show" :open="true" :title="comando.titulo" width="460px" @close="cerrarComando">
      <div class="gpm">
        <GcInput v-if="comando.requiereFecha" v-model="comando.nuevaFecha" label="Nueva fecha *" type="date" />
        <div v-if="comando.requiereMonto" class="gpm__field">
          <label class="gpm__label">Nuevo monto esperado *</label>
          <input v-model.number="comando.nuevoMonto" type="number" step="0.01" min="0.01" class="gpm__input" />
        </div>
        <GcTextarea v-model="comando.motivo" :label="comando.motivoRequerido ? 'Motivo *' : 'Motivo'" :rows="3" placeholder="Motivo del ajuste…" />
        <div v-if="comando.error" class="gpm__error"><GcIcon name="alert-circle" :size="14" /> {{ comando.error }}</div>
      </div>
      <template #footer>
        <GcButton variant="ghost" @click="cerrarComando">Cancelar</GcButton>
        <GcButton variant="primary" :loading="comando.saving" @click="submitComando">Confirmar</GcButton>
      </template>
    </GcModal>
  </GcDrawer>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue';
import { facturacionService } from '@/services/facturacion.service';
import GcDrawer from '@/components/ui/GcDrawer.vue';
import GcModal from '@/components/ui/GcModal.vue';
import GcButton from '@/components/ui/GcButton.vue';
import GcBadge from '@/components/ui/GcBadge.vue';
import GcInput from '@/components/ui/GcInput.vue';
import GcTextarea from '@/components/ui/GcTextarea.vue';
import GcIcon from '@/components/ui/GcIcon.vue';
import GcEmpty from '@/components/ui/GcEmpty.vue';
import GcSpinner from '@/components/ui/GcSpinner.vue';

const props = defineProps({
  formaPago: { type: Object, required: true },
});
const emit = defineEmits(['close', 'updated']);

const fp = ref(props.formaPago);
const detalle = ref(null);
const loadingDetalle = ref(true);

const tab = ref('gestiones');

const gestiones = ref([]);
const eventos = ref([]);
const loadingGestiones = ref(false);
const loadingEventos = ref(false);

const showGestionModal = ref(false);
const showCruceModal = ref(false);

const gestionForm = ref({ tipoGestion: '', descripcion: '', fechaGestion: new Date().toISOString().split('T')[0] });
const gestionError = ref(null);
const gestionSaving = ref(false);

const cruceTab = ref('existente');
const facturas = ref([]);
const loadingFacturas = ref(false);
const selectedFacturaId = ref(null);
const montoAplicado = ref(null);
const cruceError = ref(null);
const cruceSaving = ref(false);
const nuevaFactura = ref({ numeroFactura: '', prefijo: '', valorTotal: null, fechaEmision: new Date().toISOString().split('T')[0] });

const comando = ref(crearComandoVacio());
function crearComandoVacio() {
  return { show: false, action: null, titulo: '', requiereFecha: false, requiereMonto: false, motivoRequerido: false, nuevaFecha: null, nuevoMonto: null, motivo: '', aplicacionId: null, error: null, saving: false };
}

const subtitle = computed(() => {
  if (!detalle.value) return fp.value?.empresaNombre || '';
  const base = detalle.value.contratoCliente || fp.value.empresaNombre;
  return detalle.value.contratoNumeroInterno ? `${base} · ${detalle.value.contratoNumeroInterno}` : base;
});

const pctCumplimiento = computed(() => {
  if (!detalle.value?.porcentajeCumplimiento) return 0;
  return Math.round(Number(detalle.value.porcentajeCumplimiento) * 100);
});
const esperadoDifierePresupuesto = computed(() => {
  if (!detalle.value) return false;
  return Number(detalle.value.montoEsperadoActual) !== Number(detalle.value.montoPresupuestado);
});
const fechaDifiereOriginal = computed(() => {
  if (!detalle.value) return false;
  return detalle.value.fechaEsperadaActual !== detalle.value.fechaEsperadaOriginal;
});

const aplicacionesActivas = computed(() => {
  const registradas = new Map();
  for (const ev of eventos.value) {
    if (ev.tipoEvento === 'FACTURA_REGISTRADA' && ev.compromisoFacturaId) {
      registradas.set(ev.compromisoFacturaId, ev);
    } else if (ev.tipoEvento === 'FACTURA_REVERTIDA' && ev.compromisoFacturaId) {
      registradas.delete(ev.compromisoFacturaId);
    }
  }
  return Array.from(registradas.values());
});

const puedeReprogramar = computed(() => {
  if (!detalle.value) return false;
  return ['PENDIENTE_GESTION', 'EN_GESTION', 'COMPROMETIDO', 'PARCIALMENTE_CUMPLIDO', 'REPROGRAMADO'].includes(detalle.value.estado);
});
const puedeAjustarMonto = computed(() => {
  if (!detalle.value) return false;
  return !detalle.value.estadoFinal;
});

watch(() => props.formaPago, async (nuevo) => { fp.value = nuevo; await cargarTodo(); });
onMounted(cargarTodo);

async function cargarTodo() {
  loadingDetalle.value = true;
  try {
    detalle.value = await facturacionService.obtenerCompromiso(fp.value.id);
    await Promise.all([cargarGestiones(), cargarEventos()]);
  } catch (err) { console.error('Error cargando compromiso:', err); }
  finally { loadingDetalle.value = false; }
}
async function cargarGestiones() {
  loadingGestiones.value = true;
  try { gestiones.value = await facturacionService.listarGestiones(fp.value.id); }
  catch (err) { console.error(err); gestiones.value = []; }
  finally { loadingGestiones.value = false; }
}
async function cargarEventos() {
  loadingEventos.value = true;
  try { eventos.value = await facturacionService.listarEventos(fp.value.id); }
  catch (err) { console.error(err); eventos.value = []; }
  finally { loadingEventos.value = false; }
}

watch(showCruceModal, async (v) => {
  if (v) {
    cruceTab.value = 'existente';
    selectedFacturaId.value = null;
    montoAplicado.value = detalle.value?.saldoPendiente || null;
    cruceError.value = null;
    nuevaFactura.value = { numeroFactura: '', prefijo: '', valorTotal: null, fechaEmision: new Date().toISOString().split('T')[0] };
    await cargarFacturas();
  }
});
async function cargarFacturas() {
  loadingFacturas.value = true;
  try { const res = await facturacionService.listarFacturas({ page_size: 50, disponibles: true }); facturas.value = res.data || []; }
  catch (err) { console.error(err); facturas.value = []; }
  finally { loadingFacturas.value = false; }
}

async function submitGestion() {
  gestionError.value = null;
  gestionSaving.value = true;
  try {
    await facturacionService.registrarGestion(fp.value.id, gestionForm.value);
    showGestionModal.value = false;
    gestionForm.value = { tipoGestion: '', descripcion: '', fechaGestion: new Date().toISOString().split('T')[0] };
    await cargarGestiones();
    await cargarTodo();
    emit('updated');
  } catch (err) { gestionError.value = err.response?.data?.message || 'Error al registrar gestión'; }
  finally { gestionSaving.value = false; }
}

async function aplicarExistente() {
  cruceError.value = null;
  cruceSaving.value = true;
  try {
    await facturacionService.aplicarFactura(fp.value.id, { facturaId: selectedFacturaId.value, montoAplicado: montoAplicado.value });
    showCruceModal.value = false;
    await cargarTodo();
    emit('updated');
  } catch (err) { cruceError.value = err.response?.data?.message || 'Error al aplicar factura'; }
  finally { cruceSaving.value = false; }
}

async function submitNuevaYAplicar() {
  cruceError.value = null;
  cruceSaving.value = true;
  try {
    const factura = await facturacionService.crearFactura({
      empresaId: detalle.value?.empresaId || fp.value.empresaId,
      numeroFactura: nuevaFactura.value.numeroFactura.trim(),
      prefijo: nuevaFactura.value.prefijo?.trim() || null,
      fechaEmision: nuevaFactura.value.fechaEmision,
      valorTotal: nuevaFactura.value.valorTotal,
      moneda: detalle.value?.moneda || 'COP',
    });
    await facturacionService.aplicarFactura(fp.value.id, { facturaId: factura.id, montoAplicado: montoAplicado.value || nuevaFactura.value.valorTotal });
    showCruceModal.value = false;
    await cargarTodo();
    emit('updated');
  } catch (err) { cruceError.value = err.response?.data?.message || 'Error al registrar/aplicar factura'; }
  finally { cruceSaving.value = false; }
}

function ejecutarTransicion(t) {
  switch (t.action) {
    case 'reactivar': return invokeSimple('reactivar');
    case 'reprogramar': return abrirComando('reprogramar');
    case 'ajustarMonto': return abrirComando('ajustarMonto');
  }
}
async function invokeSimple(action) {
  try { await facturacionService[action](fp.value.id); await cargarTodo(); emit('updated'); }
  catch (err) { alert(err.response?.data?.message || `Error al ejecutar ${action}`); }
}

function abrirComando(action) {
  comando.value = crearComandoVacio();
  comando.value.show = true;
  comando.value.action = action;
  if (action === 'reprogramar') {
    comando.value.titulo = 'Reprogramar fecha esperada';
    comando.value.requiereFecha = true;
    comando.value.nuevaFecha = detalle.value?.fechaEsperadaActual;
  } else if (action === 'ajustarMonto') {
    comando.value.titulo = 'Ajustar monto esperado';
    comando.value.requiereMonto = true;
    comando.value.nuevoMonto = detalle.value?.montoEsperadoActual;
  } else if (action === 'marcarPerdido') {
    comando.value.titulo = 'Marcar como No logrado';
    comando.value.motivoRequerido = true;
  }
}
function abrirRevertir(aplicacion) {
  comando.value = crearComandoVacio();
  comando.value.show = true;
  comando.value.action = 'revertir';
  comando.value.titulo = 'Revertir aplicación de factura';
  comando.value.motivoRequerido = true;
  comando.value.aplicacionId = aplicacion.compromisoFacturaId;
}
function cerrarComando() { comando.value = crearComandoVacio(); }

async function submitComando() {
  comando.value.error = null;
  comando.value.saving = true;
  try {
    const id = fp.value.id;
    if (comando.value.action === 'reprogramar') {
      await facturacionService.reprogramar(id, { nuevaFecha: comando.value.nuevaFecha, motivo: comando.value.motivo || null });
    } else if (comando.value.action === 'ajustarMonto') {
      await facturacionService.ajustarMonto(id, { nuevoMonto: comando.value.nuevoMonto, motivo: comando.value.motivo || null });
    } else if (comando.value.action === 'marcarPerdido') {
      await facturacionService.marcarPerdido(id, { motivo: comando.value.motivo });
    } else if (comando.value.action === 'revertir') {
      await facturacionService.revertirFactura(id, { compromisoFacturaId: comando.value.aplicacionId, motivo: comando.value.motivo });
    }
    cerrarComando();
    await cargarTodo();
    emit('updated');
  } catch (err) { comando.value.error = err.response?.data?.message || 'Error al procesar'; }
  finally { comando.value.saving = false; }
}

function fmtMoneda(v, moneda) {
  if (v == null) return '—';
  return new Intl.NumberFormat('es-CO', { style: 'currency', currency: moneda || 'COP', maximumFractionDigits: 0 }).format(v);
}
function fmtFecha(iso) { if (!iso) return ''; const [y, m, d] = iso.split('-'); return `${d}/${m}/${y}`; }
function fmtFechaHora(iso) {
  if (!iso) return '';
  return new Date(iso).toLocaleString('es-CO', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' });
}

const ESTADO_TONE = {
  PENDIENTE_GESTION: 'neutral', EN_GESTION: 'info', COMPROMETIDO: 'info',
  PARCIALMENTE_CUMPLIDO: 'warning', REPROGRAMADO: 'warning', CUMPLIDO: 'success', NO_LOGRADO: 'danger',
};
function estadoTone(e) { return ESTADO_TONE[e] || 'neutral'; }
function estadoLabel(estado) {
  return { PENDIENTE_GESTION: 'Pendiente', EN_GESTION: 'En gestión', COMPROMETIDO: 'Comprometido', PARCIALMENTE_CUMPLIDO: 'Parcial', REPROGRAMADO: 'Reprogramado', CUMPLIDO: 'Cumplido', NO_LOGRADO: 'No logrado' }[estado] || estado;
}
function tipoGestionLabel(tipo) {
  return { VALIDACION_SERVICIO: 'Validación servicio', SOPORTE_OBTENIDO: 'Soporte obtenido', SOLICITUD_EMISION: 'Solicitud emisión', FACTURA_CRUZADA: 'Factura cruzada', NOVEDAD_APLAZAMIENTO: 'Aplazamiento', NOVEDAD_CAMBIO_VALOR: 'Cambio de valor', NOVEDAD_SERVICIO_INCOMPLETO: 'Servicio incompleto', NOVEDAD_SUSPENSION: 'Suspensión', NOVEDAD_OTRA: 'Otra novedad', OBSERVACION: 'Observación' }[tipo] || tipo;
}
function tipoGestionTone(tipo) {
  if (tipo?.startsWith('NOVEDAD_')) return 'warning';
  if (tipo === 'FACTURA_CRUZADA') return 'success';
  if (tipo === 'OBSERVACION') return 'neutral';
  return 'info';
}
function tipoEventoLabel(tipo) {
  return { COMPROMISO_CREADO: 'Compromiso creado', GESTION_INICIADA: 'Gestión iniciada', GESTION_ACTUALIZADA: 'Gestión actualizada', COMPROMISO_CONFIRMADO: 'Compromiso confirmado', FACTURA_REGISTRADA: 'Factura aplicada', FACTURA_REVERTIDA: 'Factura revertida', FECHA_REPROGRAMADA: 'Fecha reprogramada', MONTO_AJUSTADO: 'Monto ajustado', COMPROMISO_PERDIDO: 'Marcado como no logrado', COMPROMISO_REACTIVADO: 'Reactivado', INACTIVIDAD_DETECTADA: 'Inactividad detectada', DESVIACION_DETECTADA: 'Desviación detectada' }[tipo] || tipo;
}
</script>

<style scoped>
.gp__loading { display: flex; justify-content: center; padding: var(--gc-space-8); color: var(--gc-text-3); }
.gp__estado { display: flex; align-items: center; gap: var(--gc-space-2); flex-wrap: wrap; margin-bottom: var(--gc-space-4); }
.gp__flag { display: inline-flex; align-items: center; gap: 4px; font-size: var(--gc-fs-xs); color: var(--gc-text-3); }
.gp__flag--riesgo { color: var(--gc-danger); }

.gp__montos { display: grid; grid-template-columns: 1fr 1fr; gap: var(--gc-space-3); margin-bottom: var(--gc-space-4); }
.gp__cell { display: flex; flex-direction: column; gap: 2px; padding: var(--gc-space-3); background: var(--gc-surface-2); border-radius: var(--gc-radius-md); }
.gp__lbl { font-size: var(--gc-fs-xs); text-transform: uppercase; letter-spacing: 0.04em; color: var(--gc-text-3); }
.gp__val { font-size: var(--gc-fs-md); font-weight: var(--gc-fw-medium); color: var(--gc-text); }
.gp__val--primary { color: var(--gc-text); }
.gp__val--ok { color: var(--gc-success); }
.gp__val--warn { color: var(--gc-warning); }
.gp__tag { font-size: var(--gc-fs-xs); color: var(--gc-text-3); }
.gp__tag--warn { color: var(--gc-warning); }

.gp__progress { display: flex; align-items: center; gap: var(--gc-space-3); margin-bottom: var(--gc-space-3); }
.gp__track { flex: 1; height: 6px; background: var(--gc-surface-2); border-radius: var(--gc-radius-full); overflow: hidden; }
.gp__bar { height: 100%; background: var(--gc-success); border-radius: var(--gc-radius-full); transition: width var(--gc-t-normal); }
.gp__pct { font-size: var(--gc-fs-xs); color: var(--gc-text-3); }

.gp__fecharow { display: flex; align-items: center; justify-content: space-between; padding: var(--gc-space-2) 0; }
.gp__motivo { display: flex; flex-direction: column; gap: 2px; padding: var(--gc-space-3); background: var(--gc-danger-soft); border-radius: var(--gc-radius-md); font-size: var(--gc-fs-base); color: var(--gc-text-2); }

.gp__actions { display: flex; flex-direction: column; gap: var(--gc-space-3); margin: var(--gc-space-4) 0; }
.gp__ajustes { display: flex; flex-direction: column; gap: var(--gc-space-2); }
.gp__ajusteslbl { font-size: var(--gc-fs-xs); text-transform: uppercase; letter-spacing: 0.04em; color: var(--gc-text-3); }
.gp__ajustesrow { display: flex; gap: var(--gc-space-3); }
.gp__textbtn { display: inline-flex; align-items: center; gap: 4px; background: transparent; border: none; color: var(--gc-text-2); font-size: var(--gc-fs-sm); cursor: pointer; }
.gp__textbtn:hover { color: var(--gc-text); }

.gp__tabs { display: flex; gap: var(--gc-space-4); border-bottom: 1px solid var(--gc-border); margin-bottom: var(--gc-space-3); }
.gp__tab { display: flex; align-items: center; gap: var(--gc-space-2); padding: var(--gc-space-2) 0; background: transparent; border: none; font-size: var(--gc-fs-md); color: var(--gc-text-3); cursor: pointer; }
.gp__tab--on { color: var(--gc-text); font-weight: var(--gc-fw-medium); box-shadow: inset 0 -2px 0 var(--gc-primary); }
.gp__tab span { font-size: var(--gc-fs-xs); }

.gp__section { display: flex; flex-direction: column; gap: var(--gc-space-3); }
.gp__sechead { display: flex; align-items: center; justify-content: space-between; }
.gp__h3 { font-size: var(--gc-fs-xs); text-transform: uppercase; letter-spacing: 0.06em; color: var(--gc-text-3); }
.gp__mini { display: flex; justify-content: center; padding: var(--gc-space-4); }

.gp__bitacora { display: flex; flex-direction: column; gap: var(--gc-space-2); }
.gp__bitem { padding: var(--gc-space-3); background: var(--gc-surface-2); border-radius: var(--gc-radius-md); }
.gp__bhead { display: flex; align-items: center; justify-content: space-between; margin-bottom: var(--gc-space-1); }
.gp__bfecha { font-size: var(--gc-fs-xs); color: var(--gc-text-3); }
.gp__bdesc { font-size: var(--gc-fs-base); color: var(--gc-text-2); }
.gp__evtipo { font-size: var(--gc-fs-sm); font-weight: var(--gc-fw-medium); color: var(--gc-text); }
.gp__evdiff { font-size: var(--gc-fs-sm); color: var(--gc-text-2); }

.gp__aplic { display: flex; flex-direction: column; gap: var(--gc-space-2); }
.gp__apitem { display: flex; align-items: center; justify-content: space-between; padding: var(--gc-space-3); background: var(--gc-surface-2); border-radius: var(--gc-radius-md); }
.gp__apinfo { display: flex; flex-direction: column; gap: 2px; }
.gp__apmonto { font-size: var(--gc-fs-md); font-weight: var(--gc-fw-medium); color: var(--gc-text); }
.gp__apfecha { font-size: var(--gc-fs-xs); color: var(--gc-text-3); }

/* Modales internos */
.gpm { display: flex; flex-direction: column; gap: var(--gc-space-3); }
.gpm__field { display: flex; flex-direction: column; gap: var(--gc-space-1); }
.gpm__label { font-size: var(--gc-fs-sm); font-weight: var(--gc-fw-medium); color: var(--gc-text-2); }
.gpm__select, .gpm__input { height: 38px; padding: 0 var(--gc-space-3); background: var(--gc-surface); border: 1px solid var(--gc-border); border-radius: var(--gc-radius-md); color: var(--gc-text); font-size: var(--gc-fs-md); }
.gpm__hint { font-size: var(--gc-fs-xs); color: var(--gc-text-3); }
.gpm__row { display: grid; grid-template-columns: 1fr 1fr; gap: var(--gc-space-3); }
.gpm__tabs { display: flex; gap: var(--gc-space-2); }
.gpm__tab { flex: 1; padding: var(--gc-space-2); background: var(--gc-surface-2); border: 1px solid var(--gc-border); border-radius: var(--gc-radius-md); color: var(--gc-text-2); font-size: var(--gc-fs-sm); cursor: pointer; }
.gpm__tab--on { border-color: var(--gc-primary); background: var(--gc-surface); color: var(--gc-text); font-weight: var(--gc-fw-medium); }
.gpm__facturas { display: flex; flex-direction: column; gap: var(--gc-space-2); max-height: 240px; overflow-y: auto; }
.gpm__factura { display: flex; align-items: center; justify-content: space-between; padding: var(--gc-space-3); background: var(--gc-surface-2); border: 1px solid var(--gc-border); border-radius: var(--gc-radius-md); cursor: pointer; }
.gpm__factura--on { border-color: var(--gc-primary); box-shadow: inset 0 0 0 1px var(--gc-primary); }
.gpm__fleft { display: flex; flex-direction: column; gap: 2px; }
.gpm__fnum { font-size: var(--gc-fs-md); font-weight: var(--gc-fw-medium); color: var(--gc-text); }
.gpm__fmeta { font-size: var(--gc-fs-xs); color: var(--gc-text-3); }
.gpm__fright { display: flex; flex-direction: column; align-items: flex-end; gap: 2px; }
.gpm__fvalor { font-size: var(--gc-fs-md); color: var(--gc-text); }
.gpm__fsaldo { font-size: var(--gc-fs-xs); color: var(--gc-text-3); }
.gpm__error { display: flex; align-items: center; gap: var(--gc-space-2); padding: var(--gc-space-3); background: var(--gc-danger-soft); border: 1px solid var(--gc-danger); border-radius: var(--gc-radius-md); font-size: var(--gc-fs-sm); color: var(--gc-danger); }
</style>
