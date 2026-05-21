<template>
  <Teleport to="body">
    <div class="panel-overlay" @click.self="$emit('close')">
      <div class="panel glass animate-slideLeft">

        <!-- Header -->
        <div class="panel__header">
          <div>
            <h2 class="panel__title">{{ detalle?.descripcion || 'Cargando...' }}</h2>
            <p class="panel__subtitle">
              {{ detalle?.contratoCliente || fp.empresaNombre }}
              <span v-if="detalle?.contratoNumeroInterno"> · {{ detalle.contratoNumeroInterno }}</span>
            </p>
          </div>
          <button class="panel__close" @click="$emit('close')">
            <Icon name="x" :size="18" />
          </button>
        </div>

        <!-- Loading inicial -->
        <div v-if="loadingDetalle" class="mini-loading">
          <Icon name="loader" :size="20" class="animate-spin" /> Cargando detalle...
        </div>

        <template v-else-if="detalle">
          <!-- Estado actual + flag de riesgo -->
          <div class="panel__estado-row">
            <span :class="['estado-pill', `estado-pill--${detalle.estado.toLowerCase()}`]">
              {{ estadoLabel(detalle.estado) }}
            </span>
            <span v-if="detalle.enRiesgo" class="riesgo-badge">
              <Icon name="alert-circle" :size="12" /> En riesgo
            </span>
            <span v-if="detalle.tipo === 'RECURRENTE'" class="tipo-badge">
              <Icon name="refresh" :size="12" /> Recurrente
            </span>
          </div>

          <!-- Info de montos (3 montos + saldo) -->
          <div class="panel__info">
            <div class="info-grid">
              <div class="info-cell">
                <span class="info-label">Presupuestado</span>
                <span class="info-value">{{ fmtMoneda(detalle.montoPresupuestado, detalle.moneda) }}</span>
                <span class="info-tag">inmutable</span>
              </div>
              <div class="info-cell">
                <span class="info-label">Esperado actual</span>
                <span class="info-value info-value--primary">
                  {{ fmtMoneda(detalle.montoEsperadoActual, detalle.moneda) }}
                </span>
                <span v-if="esperadoDifierePresupuesto" class="info-tag info-tag--warning">
                  ajustado
                </span>
              </div>
              <div class="info-cell">
                <span class="info-label">Facturado acumulado</span>
                <span class="info-value info-value--success">
                  {{ fmtMoneda(detalle.montoFacturadoAcumulado, detalle.moneda) }}
                </span>
              </div>
              <div class="info-cell">
                <span class="info-label">Saldo pendiente</span>
                <span class="info-value info-value--warning">
                  {{ fmtMoneda(detalle.saldoPendiente, detalle.moneda) }}
                </span>
              </div>
            </div>

            <!-- Barra de progreso -->
            <div class="progress-wrap">
              <div class="progress-track">
                <div class="progress-bar" :style="{ width: pctCumplimiento + '%' }"></div>
              </div>
              <span class="progress-label">{{ pctCumplimiento }}% cumplido</span>
            </div>

            <!-- Fechas -->
            <div class="info-row">
              <span class="info-label">Fecha esperada</span>
              <span class="info-value">
                {{ fmtFecha(detalle.fechaEsperadaActual) }}
                <span v-if="fechaDifiereOriginal" class="info-tag info-tag--warning">
                  reprogramada de {{ fmtFecha(detalle.fechaEsperadaOriginal) }}
                </span>
              </span>
            </div>

            <!-- Motivo perdida -->
            <div v-if="detalle.motivoPerdida" class="info-row info-row--error">
              <span class="info-label">Motivo no logrado</span>
              <span class="info-value">{{ detalle.motivoPerdida }}</span>
            </div>
          </div>

          <!-- Acciones según estado -->
          <div class="panel__actions">
            <!-- Aplicar factura (cualquier estado no final) -->
            <button
              v-if="!detalle.estadoFinal"
              class="btn btn--accent btn--full"
              @click="showCruceModal = true"
            >
              <Icon name="check-circle" :size="16" /> Aplicar factura
            </button>

            <!-- Botones de transición agrupados -->
            <div v-if="transicionesDisponibles.length" class="action-row">
              <button
                v-for="t in transicionesDisponibles"
                :key="t.action"
                :class="['btn', 'btn--sm', t.variant || 'btn--ghost']"
                :title="t.title"
                @click="ejecutarTransicion(t)"
              >
                <Icon :name="t.icon" :size="14" />
                {{ t.label }}
              </button>
            </div>
          </div>

          <!-- Tabs bitácoras -->
          <div class="panel__tabs">
            <button
              :class="['tab', tab === 'gestiones' && 'tab--active']"
              @click="tab = 'gestiones'"
            >
              <Icon name="user" :size="14" /> Gestiones
              <span class="tab__count">{{ gestiones.length }}</span>
            </button>
            <button
              :class="['tab', tab === 'eventos' && 'tab--active']"
              @click="tab = 'eventos'"
            >
              <Icon name="activity" :size="14" /> Eventos
              <span class="tab__count">{{ eventos.length }}</span>
            </button>
            <button
              :class="['tab', tab === 'facturas' && 'tab--active']"
              @click="tab = 'facturas'"
            >
              <Icon name="file-text" :size="14" /> Facturas
              <span class="tab__count">{{ aplicacionesActivas.length }}</span>
            </button>
          </div>

          <!-- Tab Gestiones (bitácora humana) -->
          <div v-if="tab === 'gestiones'" class="panel__section">
            <div class="section-header-panel">
              <h3 class="section-title-panel">
                <Icon name="user" :size="14" color="var(--primary)" />
                Bitácora humana
              </h3>
              <button class="btn btn--primary btn--sm" @click="showGestionModal = true">
                <Icon name="plus" :size="14" /> Registrar
              </button>
            </div>
            <div v-if="loadingGestiones" class="mini-loading">
              <Icon name="loader" :size="16" class="animate-spin" />
            </div>
            <div v-else-if="!gestiones.length" class="mini-empty">
              Sin gestiones registradas
            </div>
            <div v-else class="bitacora-list">
              <div v-for="g in gestiones" :key="g.id" class="bitacora-item">
                <div class="bitacora-item__header">
                  <span :class="['gestion-tipo', tipoGestionClass(g.tipoGestion)]">
                    {{ tipoGestionLabel(g.tipoGestion) }}
                  </span>
                  <span class="bitacora-fecha">{{ fmtFecha(g.fechaGestion) }}</span>
                </div>
                <p class="bitacora-desc">{{ g.descripcion }}</p>
              </div>
            </div>
          </div>

          <!-- Tab Eventos (bitácora sistémica) -->
          <div v-if="tab === 'eventos'" class="panel__section">
            <h3 class="section-title-panel">
              <Icon name="activity" :size="14" color="var(--secondary)" />
              Bitácora sistémica
            </h3>
            <div v-if="loadingEventos" class="mini-loading">
              <Icon name="loader" :size="16" class="animate-spin" />
            </div>
            <div v-else-if="!eventos.length" class="mini-empty">
              Sin eventos registrados
            </div>
            <div v-else class="bitacora-list">
              <div v-for="e in eventos" :key="e.id" class="bitacora-item">
                <div class="bitacora-item__header">
                  <span class="evento-tipo">{{ tipoEventoLabel(e.tipoEvento) }}</span>
                  <span class="bitacora-fecha">{{ fmtFechaHora(e.fechaEvento) }}</span>
                </div>
                <div class="evento-diff">
                  <span v-if="e.estadoAnterior && e.estadoNuevo">
                    {{ estadoLabel(e.estadoAnterior) }} → <strong>{{ estadoLabel(e.estadoNuevo) }}</strong>
                  </span>
                  <span v-if="e.montoAnterior != null && e.montoNuevo != null">
                    {{ fmtMoneda(e.montoAnterior, detalle.moneda) }} →
                    <strong>{{ fmtMoneda(e.montoNuevo, detalle.moneda) }}</strong>
                  </span>
                  <span v-if="e.fechaAnterior && e.fechaNueva">
                    {{ fmtFecha(e.fechaAnterior) }} → <strong>{{ fmtFecha(e.fechaNueva) }}</strong>
                  </span>
                </div>
                <p v-if="e.motivo" class="bitacora-desc">{{ e.motivo }}</p>
              </div>
            </div>
          </div>

          <!-- Tab Facturas aplicadas (deriva de eventos FACTURA_REGISTRADA) -->
          <div v-if="tab === 'facturas'" class="panel__section">
            <h3 class="section-title-panel">
              <Icon name="file-text" :size="14" color="var(--success)" />
              Facturas aplicadas
            </h3>
            <div v-if="!aplicacionesActivas.length" class="mini-empty">
              Sin facturas aplicadas todavía
            </div>
            <div v-else class="aplicaciones-list">
              <div v-for="ap in aplicacionesActivas" :key="ap.compromisoFacturaId" class="aplicacion-item">
                <div class="aplicacion-item__info">
                  <span class="aplicacion-monto">
                    {{ fmtMoneda(ap.montoNuevo, detalle.moneda) }}
                  </span>
                  <span class="aplicacion-fecha">{{ fmtFechaHora(ap.fechaEvento) }}</span>
                </div>
                <button
                  class="btn btn--ghost btn--sm"
                  :disabled="detalle.estadoFinal"
                  @click="abrirRevertir(ap)"
                >
                  <Icon name="x" :size="12" /> Revertir
                </button>
              </div>
            </div>
          </div>
        </template>
      </div>
    </div>
  </Teleport>

  <!-- Modal Registrar Gestión -->
  <Teleport to="body">
    <div v-if="showGestionModal" class="modal-overlay" @click.self="showGestionModal = false">
      <div class="modal glass animate-slideUp">
        <div class="modal__header">
          <h2 class="modal__title">Registrar gestión</h2>
          <button class="modal__close" @click="showGestionModal = false">
            <Icon name="x" :size="18" />
          </button>
        </div>
        <form class="modal__body" @submit.prevent="submitGestion">
          <div class="field">
            <label class="field__label">Tipo de gestión <span class="req">*</span></label>
            <select v-model="gestionForm.tipoGestion" class="field__select" required>
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
          <div class="field">
            <label class="field__label">Descripción <span class="req">*</span></label>
            <textarea v-model="gestionForm.descripcion" class="field__textarea" rows="3" required maxlength="2000" placeholder="Detalle de la gestión realizada..."></textarea>
          </div>
          <div class="field">
            <label class="field__label">Fecha <span class="req">*</span></label>
            <input v-model="gestionForm.fechaGestion" type="date" class="field__input" required />
          </div>
          <div v-if="gestionError" class="modal-error">
            <Icon name="alert-circle" :size="14" /> {{ gestionError }}
          </div>
          <div class="modal__actions">
            <button type="button" class="btn btn--ghost" @click="showGestionModal = false">Cancelar</button>
            <button type="submit" class="btn btn--primary" :disabled="gestionSaving">
              {{ gestionSaving ? 'Guardando...' : 'Registrar' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </Teleport>

  <!-- Modal Aplicar Factura -->
  <Teleport to="body">
    <div v-if="showCruceModal" class="modal-overlay" @click.self="showCruceModal = false">
      <div class="modal glass animate-slideUp" style="max-width:560px">
        <div class="modal__header">
          <h2 class="modal__title">Aplicar factura</h2>
          <button class="modal__close" @click="showCruceModal = false">
            <Icon name="x" :size="18" />
          </button>
        </div>
        <div class="modal__body">
          <div class="cruce-tabs">
            <button :class="['cruce-tab', cruceTab === 'existente' && 'cruce-tab--active']" @click="cruceTab = 'existente'">
              Factura existente
            </button>
            <button :class="['cruce-tab', cruceTab === 'nueva' && 'cruce-tab--active']" @click="cruceTab = 'nueva'">
              Registrar nueva
            </button>
          </div>

          <!-- Tab Existente -->
          <div v-if="cruceTab === 'existente'">
            <div v-if="loadingFacturas" class="mini-loading">
              <Icon name="loader" :size="16" class="animate-spin" />
            </div>
            <div v-else-if="!facturas.length" class="mini-empty">
              No hay facturas registradas. Registra una nueva.
            </div>
            <div v-else class="facturas-list">
              <div
                v-for="f in facturas"
                :key="f.id"
                :class="['factura-option', selectedFacturaId === f.id && 'factura-option--selected']"
                @click="selectedFacturaId = f.id"
              >
                <div class="factura-option__left">
                  <span class="factura-option__num">
                    {{ f.prefijo ? f.prefijo + '-' : '' }}{{ f.numeroFactura }}
                  </span>
                  <span class="factura-option__meta">
                    {{ f.empresaNombre }} · {{ fmtFecha(f.fechaEmision) }}
                  </span>
                </div>
                <span class="factura-option__valor">
                  {{ fmtMoneda(f.valorTotal, f.moneda) }}
                </span>
              </div>
            </div>
            <div v-if="selectedFacturaId" class="field" style="margin-top:var(--space-3)">
              <label class="field__label">
                Monto a aplicar a esta forma de pago <span class="req">*</span>
              </label>
              <input
                v-model.number="montoAplicado"
                type="number"
                step="0.01"
                min="0.01"
                class="field__input"
                :placeholder="'Saldo: ' + detalle?.saldoPendiente"
                required
              />
              <span class="field__hint">
                No puede exceder el valor total de la factura (suma de aplicaciones ≤ valor factura, RN-14)
              </span>
            </div>
          </div>

          <!-- Tab Nueva -->
          <form v-if="cruceTab === 'nueva'" @submit.prevent="submitNuevaYAplicar">
            <div class="form-row">
              <div class="field">
                <label class="field__label">Prefijo</label>
                <input v-model="nuevaFactura.prefijo" type="text" class="field__input" maxlength="10" placeholder="Ej: ARQBS" />
              </div>
              <div class="field">
                <label class="field__label">Número factura <span class="req">*</span></label>
                <input v-model="nuevaFactura.numeroFactura" type="text" class="field__input" required maxlength="50" placeholder="Ej: 4521" />
              </div>
            </div>
            <div class="field">
              <label class="field__label">Valor total (incluido IVA) <span class="req">*</span></label>
              <input v-model.number="nuevaFactura.valorTotal" type="number" step="0.01" min="0.01" class="field__input" required />
            </div>
            <div class="field">
              <label class="field__label">Fecha emisión <span class="req">*</span></label>
              <input v-model="nuevaFactura.fechaEmision" type="date" class="field__input" required />
            </div>
            <div class="field">
              <label class="field__label">Monto a aplicar a esta forma de pago <span class="req">*</span></label>
              <input v-model.number="montoAplicado" type="number" step="0.01" min="0.01" class="field__input" required />
            </div>
          </form>

          <div v-if="cruceError" class="modal-error">
            <Icon name="alert-circle" :size="14" /> {{ cruceError }}
          </div>

          <div class="modal__actions">
            <button type="button" class="btn btn--ghost" @click="showCruceModal = false">Cancelar</button>
            <button
              v-if="cruceTab === 'existente'"
              class="btn btn--accent"
              :disabled="!selectedFacturaId || !montoAplicado || cruceSaving"
              @click="aplicarExistente"
            >
              {{ cruceSaving ? 'Aplicando...' : 'Aplicar' }}
            </button>
            <button
              v-else
              class="btn btn--accent"
              :disabled="cruceSaving"
              @click="submitNuevaYAplicar"
            >
              {{ cruceSaving ? 'Procesando...' : 'Registrar y aplicar' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </Teleport>

  <!-- Modal Comando (reprogramar/ajustar-monto/marcar-perdido/revertir) -->
  <Teleport to="body">
    <div v-if="comando.show" class="modal-overlay" @click.self="cerrarComando">
      <div class="modal glass animate-slideUp" style="max-width:480px">
        <div class="modal__header">
          <h2 class="modal__title">{{ comando.titulo }}</h2>
          <button class="modal__close" @click="cerrarComando">
            <Icon name="x" :size="18" />
          </button>
        </div>
        <form class="modal__body" @submit.prevent="submitComando">
          <div v-if="comando.requiereFecha" class="field">
            <label class="field__label">Nueva fecha <span class="req">*</span></label>
            <input v-model="comando.nuevaFecha" type="date" class="field__input" required />
          </div>
          <div v-if="comando.requiereMonto" class="field">
            <label class="field__label">Nuevo monto esperado <span class="req">*</span></label>
            <input v-model.number="comando.nuevoMonto" type="number" step="0.01" min="0.01" class="field__input" required />
          </div>
          <div class="field">
            <label class="field__label">
              Motivo
              <span v-if="comando.motivoRequerido" class="req">*</span>
            </label>
            <textarea
              v-model="comando.motivo"
              class="field__textarea"
              rows="3"
              maxlength="1000"
              :required="comando.motivoRequerido"
              placeholder="Explica el cambio..."
            ></textarea>
          </div>
          <div v-if="comando.error" class="modal-error">
            <Icon name="alert-circle" :size="14" /> {{ comando.error }}
          </div>
          <div class="modal__actions">
            <button type="button" class="btn btn--ghost" @click="cerrarComando">Cancelar</button>
            <button type="submit" class="btn btn--primary" :disabled="comando.saving">
              {{ comando.saving ? 'Procesando...' : 'Confirmar' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue';
import Icon from '@/components/ui/Icon.vue';
import { facturacionService } from '@/services/facturacion.service';

const props = defineProps({
  formaPago: { type: Object, required: true },
});
const emit = defineEmits(['close', 'updated']);

// El item que entra puede ser un CompromisoPeriodoItem (vista resumida) o
// un CompromisoIngresoResponse. Siempre re-cargamos el detalle completo para
// tener todos los campos (montoEsperadoActual, fechaEsperadaOriginal, etc.).
const fp = ref(props.formaPago);
const detalle = ref(null);
const loadingDetalle = ref(true);

// Tabs
const tab = ref('gestiones');

// Bitácoras
const gestiones = ref([]);
const eventos = ref([]);
const loadingGestiones = ref(false);
const loadingEventos = ref(false);

// Modales
const showGestionModal = ref(false);
const showCruceModal = ref(false);

// Form gestión
const gestionForm = ref({
  tipoGestion: '',
  descripcion: '',
  fechaGestion: new Date().toISOString().split('T')[0],
});
const gestionError = ref(null);
const gestionSaving = ref(false);

// Form aplicar factura
const cruceTab = ref('existente');
const facturas = ref([]);
const loadingFacturas = ref(false);
const selectedFacturaId = ref(null);
const montoAplicado = ref(null);
const cruceError = ref(null);
const cruceSaving = ref(false);
const nuevaFactura = ref({
  numeroFactura: '',
  prefijo: '',
  valorTotal: null,
  fechaEmision: new Date().toISOString().split('T')[0],
});

// Comando genérico (reprogramar / ajustar-monto / marcar-perdido / revertir)
const comando = ref(crearComandoVacio());

function crearComandoVacio() {
  return {
    show: false,
    action: null,             // 'reprogramar' | 'ajustarMonto' | 'marcarPerdido' | 'revertir'
    titulo: '',
    requiereFecha: false,
    requiereMonto: false,
    motivoRequerido: false,
    nuevaFecha: null,
    nuevoMonto: null,
    motivo: '',
    aplicacionId: null,       // solo para 'revertir'
    error: null,
    saving: false,
  };
}

// ============================================================
// Computed
// ============================================================

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

// Aplicaciones vigentes derivadas del feed de eventos
// (los eventos FACTURA_REGISTRADA sin FACTURA_REVERTIDA posterior con el mismo compromisoFacturaId)
const aplicacionesActivas = computed(() => {
  const registradas = new Map();  // compromisoFacturaId → evento de registro
  for (const ev of eventos.value) {
    if (ev.tipoEvento === 'FACTURA_REGISTRADA' && ev.compromisoFacturaId) {
      registradas.set(ev.compromisoFacturaId, ev);
    } else if (ev.tipoEvento === 'FACTURA_REVERTIDA' && ev.compromisoFacturaId) {
      registradas.delete(ev.compromisoFacturaId);
    }
  }
  return Array.from(registradas.values());
});

// Botones de transición según estado actual
const transicionesDisponibles = computed(() => {
  if (!detalle.value) return [];
  const estado = detalle.value.estado;
  const acciones = [];

  if (estado === 'PENDIENTE_GESTION') {
    acciones.push({ action: 'iniciarGestion', label: 'Iniciar gestión', icon: 'play', title: 'Pasa a EN_GESTION' });
  }
  if (['EN_GESTION', 'COMPROMETIDO', 'PARCIALMENTE_CUMPLIDO', 'REPROGRAMADO'].includes(estado)) {
    acciones.push({ action: 'confirmar', label: 'Confirmar', icon: 'check', title: 'Marca como COMPROMETIDO' });
  }
  if (['EN_GESTION', 'COMPROMETIDO', 'PARCIALMENTE_CUMPLIDO', 'PENDIENTE_GESTION'].includes(estado)) {
    acciones.push({ action: 'reprogramar', label: 'Reprogramar', icon: 'calendar', title: 'Cambiar fecha esperada' });
  }
  if (!detalle.value.estadoFinal) {
    acciones.push({ action: 'ajustarMonto', label: 'Ajustar monto', icon: 'edit', title: 'Cambiar monto esperado actual' });
    acciones.push({ action: 'marcarPerdido', label: 'No logrado', icon: 'x-circle', variant: 'btn--danger', title: 'Cierra como NO_LOGRADO' });
  }
  if (detalle.value.estadoFinal) {
    acciones.push({ action: 'reactivar', label: 'Reactivar', icon: 'rotate-ccw', title: 'Vuelve a EN_GESTION' });
  }
  return acciones;
});

// ============================================================
// Carga inicial y watchers
// ============================================================

watch(() => props.formaPago, async (nuevo) => {
  fp.value = nuevo;
  await cargarTodo();
});

onMounted(cargarTodo);

async function cargarTodo() {
  loadingDetalle.value = true;
  try {
    // El item de la lista trae 'id' (CompromisoPeriodoItem) o 'id' (CompromisoIngresoResponse)
    detalle.value = await facturacionService.obtenerCompromiso(fp.value.id);
    await Promise.all([cargarGestiones(), cargarEventos()]);
  } catch (err) {
    console.error('Error cargando compromiso:', err);
  } finally {
    loadingDetalle.value = false;
  }
}

async function cargarGestiones() {
  loadingGestiones.value = true;
  try {
    gestiones.value = await facturacionService.listarGestiones(fp.value.id);
  } catch (err) { console.error(err); gestiones.value = []; }
  finally { loadingGestiones.value = false; }
}

async function cargarEventos() {
  loadingEventos.value = true;
  try {
    eventos.value = await facturacionService.listarEventos(fp.value.id);
  } catch (err) { console.error(err); eventos.value = []; }
  finally { loadingEventos.value = false; }
}

watch(showCruceModal, async (v) => {
  if (v) {
    cruceTab.value = 'existente';
    selectedFacturaId.value = null;
    montoAplicado.value = detalle.value?.saldoPendiente || null;
    cruceError.value = null;
    nuevaFactura.value = {
      numeroFactura: '', prefijo: '', valorTotal: null,
      fechaEmision: new Date().toISOString().split('T')[0],
    };
    await cargarFacturas();
  }
});

async function cargarFacturas() {
  loadingFacturas.value = true;
  try {
    const res = await facturacionService.listarFacturas({ page_size: 50 });
    facturas.value = res.data || [];
  } catch (err) { console.error(err); facturas.value = []; }
  finally { loadingFacturas.value = false; }
}

// ============================================================
// Acciones
// ============================================================

async function submitGestion() {
  gestionError.value = null;
  gestionSaving.value = true;
  try {
    await facturacionService.registrarGestion(fp.value.id, gestionForm.value);
    showGestionModal.value = false;
    gestionForm.value = {
      tipoGestion: '', descripcion: '',
      fechaGestion: new Date().toISOString().split('T')[0],
    };
    await cargarGestiones();
    await cargarTodo();
    emit('updated');
  } catch (err) {
    gestionError.value = err.response?.data?.message || 'Error al registrar gestión';
  } finally { gestionSaving.value = false; }
}

async function aplicarExistente() {
  cruceError.value = null;
  cruceSaving.value = true;
  try {
    await facturacionService.aplicarFactura(fp.value.id, {
      facturaId: selectedFacturaId.value,
      montoAplicado: montoAplicado.value,
    });
    showCruceModal.value = false;
    await cargarTodo();
    emit('updated');
  } catch (err) {
    cruceError.value = err.response?.data?.message || 'Error al aplicar factura';
  } finally { cruceSaving.value = false; }
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
    await facturacionService.aplicarFactura(fp.value.id, {
      facturaId: factura.id,
      montoAplicado: montoAplicado.value || nuevaFactura.value.valorTotal,
    });
    showCruceModal.value = false;
    await cargarTodo();
    emit('updated');
  } catch (err) {
    cruceError.value = err.response?.data?.message || 'Error al registrar/aplicar factura';
  } finally { cruceSaving.value = false; }
}

function ejecutarTransicion(t) {
  switch (t.action) {
    case 'iniciarGestion': return invokeSimple('iniciarGestion');
    case 'confirmar':      return invokeSimple('confirmar');
    case 'reactivar':      return invokeSimple('reactivar');
    case 'reprogramar':    return abrirComando('reprogramar');
    case 'ajustarMonto':   return abrirComando('ajustarMonto');
    case 'marcarPerdido':  return abrirComando('marcarPerdido');
  }
}

async function invokeSimple(action) {
  try {
    await facturacionService[action](fp.value.id);
    await cargarTodo();
    emit('updated');
  } catch (err) {
    alert(err.response?.data?.message || `Error al ejecutar ${action}`);
  }
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

function cerrarComando() {
  comando.value = crearComandoVacio();
}

async function submitComando() {
  comando.value.error = null;
  comando.value.saving = true;
  try {
    const id = fp.value.id;
    if (comando.value.action === 'reprogramar') {
      await facturacionService.reprogramar(id, {
        nuevaFecha: comando.value.nuevaFecha,
        motivo: comando.value.motivo || null,
      });
    } else if (comando.value.action === 'ajustarMonto') {
      await facturacionService.ajustarMonto(id, {
        nuevoMonto: comando.value.nuevoMonto,
        motivo: comando.value.motivo || null,
      });
    } else if (comando.value.action === 'marcarPerdido') {
      await facturacionService.marcarPerdido(id, {
        motivo: comando.value.motivo,
      });
    } else if (comando.value.action === 'revertir') {
      await facturacionService.revertirFactura(id, {
        compromisoFacturaId: comando.value.aplicacionId,
        motivo: comando.value.motivo,
      });
    }
    cerrarComando();
    await cargarTodo();
    emit('updated');
  } catch (err) {
    comando.value.error = err.response?.data?.message || 'Error al procesar';
  } finally {
    comando.value.saving = false;
  }
}

// ============================================================
// Helpers de formato y etiquetas
// ============================================================

function fmtMoneda(v, moneda) {
  if (v == null) return '—';
  const m = moneda || 'COP';
  return new Intl.NumberFormat('es-CO', {
    style: 'currency', currency: m, maximumFractionDigits: 0,
  }).format(v);
}

function fmtFecha(iso) {
  if (!iso) return '';
  const [y, m, d] = iso.split('-');
  return `${d}/${m}/${y}`;
}

function fmtFechaHora(iso) {
  if (!iso) return '';
  const d = new Date(iso);
  return d.toLocaleString('es-CO', {
    day: '2-digit', month: '2-digit', year: 'numeric',
    hour: '2-digit', minute: '2-digit',
  });
}

function estadoLabel(estado) {
  return {
    PENDIENTE_GESTION: 'Pendiente',
    EN_GESTION: 'En gestión',
    COMPROMETIDO: 'Comprometido',
    PARCIALMENTE_CUMPLIDO: 'Parcial',
    REPROGRAMADO: 'Reprogramado',
    CUMPLIDO: 'Cumplido',
    NO_LOGRADO: 'No logrado',
  }[estado] || estado;
}

function tipoGestionLabel(tipo) {
  return {
    VALIDACION_SERVICIO: 'Validación servicio',
    SOPORTE_OBTENIDO: 'Soporte obtenido',
    SOLICITUD_EMISION: 'Solicitud emisión',
    FACTURA_CRUZADA: 'Factura cruzada',
    NOVEDAD_APLAZAMIENTO: 'Aplazamiento',
    NOVEDAD_CAMBIO_VALOR: 'Cambio de valor',
    NOVEDAD_SERVICIO_INCOMPLETO: 'Servicio incompleto',
    NOVEDAD_SUSPENSION: 'Suspensión',
    NOVEDAD_OTRA: 'Otra novedad',
    OBSERVACION: 'Observación',
  }[tipo] || tipo;
}

function tipoGestionClass(tipo) {
  if (tipo?.startsWith('NOVEDAD_')) return 'gestion-tipo--novedad';
  if (tipo === 'FACTURA_CRUZADA') return 'gestion-tipo--success';
  if (tipo === 'OBSERVACION') return 'gestion-tipo--muted';
  return 'gestion-tipo--primary';
}

function tipoEventoLabel(tipo) {
  return {
    COMPROMISO_CREADO: 'Compromiso creado',
    GESTION_INICIADA: 'Gestión iniciada',
    GESTION_ACTUALIZADA: 'Gestión actualizada',
    COMPROMISO_CONFIRMADO: 'Compromiso confirmado',
    FACTURA_REGISTRADA: 'Factura aplicada',
    FACTURA_REVERTIDA: 'Factura revertida',
    FECHA_REPROGRAMADA: 'Fecha reprogramada',
    MONTO_AJUSTADO: 'Monto ajustado',
    COMPROMISO_PERDIDO: 'Marcado como no logrado',
    COMPROMISO_REACTIVADO: 'Reactivado',
    INACTIVIDAD_DETECTADA: 'Inactividad detectada',
    DESVIACION_DETECTADA: 'Desviación detectada',
  }[tipo] || tipo;
}
</script>

<style scoped>
/* ===== Panel lateral ===== */
.panel-overlay {
  position: fixed; inset: 0;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(4px);
  z-index: 100;
  display: flex; justify-content: flex-end;
}
.panel {
  width: 520px;
  max-width: 100vw;
  height: 100vh;
  background: var(--bg-elevated);
  padding: var(--space-5);
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.panel__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: var(--space-3);
}
.panel__title {
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0;
}
.panel__subtitle {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
  margin: var(--space-1) 0 0 0;
}
.panel__close {
  width: 32px; height: 32px;
  border-radius: var(--radius-full);
  background: transparent;
  color: var(--text-secondary);
  border: 1px solid var(--glass-border);
  cursor: pointer;
  display: flex; align-items: center; justify-content: center;
}
.panel__close:hover { background: var(--bg-hover); color: var(--text-primary); }

/* Estado row */
.panel__estado-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: var(--space-2);
}
.riesgo-badge {
  display: inline-flex; align-items: center; gap: 4px;
  font-size: 11px;
  color: var(--error);
  background: var(--error-soft);
  padding: 2px 8px;
  border-radius: var(--radius-full);
  font-weight: var(--font-semibold);
}
.tipo-badge {
  display: inline-flex; align-items: center; gap: 4px;
  font-size: 11px;
  color: var(--secondary);
  background: var(--secondary-soft);
  padding: 2px 8px;
  border-radius: var(--radius-full);
}

/* Estado pills */
.estado-pill {
  padding: 2px 10px;
  border-radius: var(--radius-full);
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
}
.estado-pill--pendiente_gestion    { background: var(--bg-surface);     color: var(--text-secondary); }
.estado-pill--en_gestion           { background: var(--primary-soft);   color: var(--primary); }
.estado-pill--comprometido         { background: var(--secondary-soft); color: var(--secondary); }
.estado-pill--parcialmente_cumplido{ background: var(--warning-soft);   color: var(--warning); }
.estado-pill--reprogramado         { background: var(--warning-soft);   color: var(--warning); opacity: 0.85; }
.estado-pill--cumplido             { background: var(--success-soft);   color: var(--success); }
.estado-pill--no_logrado           { background: var(--error-soft);     color: var(--error); }

/* Info */
.panel__info {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
  padding: var(--space-4);
  background: var(--bg-surface);
  border-radius: var(--radius-lg);
}
.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-3);
}
.info-cell {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.info-label {
  font-size: 10px;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}
.info-value {
  font-size: var(--text-sm);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  font-family: var(--font-mono, monospace);
}
.info-value--primary { color: var(--primary); }
.info-value--success { color: var(--success); }
.info-value--warning { color: var(--warning); }
.info-tag {
  font-size: 9px;
  color: var(--text-muted);
  background: var(--bg-deep);
  padding: 1px 6px;
  border-radius: var(--radius-full);
  width: fit-content;
  margin-top: 2px;
}
.info-tag--warning {
  color: var(--warning);
  background: var(--warning-soft);
}

.progress-wrap {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}
.progress-track {
  flex: 1;
  height: 6px;
  background: var(--bg-deep);
  border-radius: var(--radius-full);
  overflow: hidden;
}
.progress-bar {
  height: 100%;
  background: linear-gradient(90deg, var(--primary), var(--success));
  transition: width 0.4s;
}
.progress-label {
  font-size: 11px;
  color: var(--text-tertiary);
  font-family: var(--font-mono, monospace);
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: var(--space-3);
  font-size: var(--text-xs);
}
.info-row--error .info-value { color: var(--error); }

/* Acciones */
.panel__actions {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}
.action-row {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-2);
}

/* Tabs */
.panel__tabs {
  display: flex;
  border-bottom: 1px solid var(--border);
  gap: var(--space-1);
}
.tab {
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-3);
  background: transparent;
  border: none;
  border-bottom: 2px solid transparent;
  color: var(--text-tertiary);
  font-family: var(--font-body);
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  cursor: pointer;
  transition: all 0.15s;
}
.tab:hover { color: var(--text-primary); }
.tab--active {
  color: var(--primary);
  border-bottom-color: var(--primary);
}
.tab__count {
  font-size: 10px;
  background: var(--bg-surface);
  padding: 1px 6px;
  border-radius: var(--radius-full);
  color: var(--text-muted);
}

/* Sección dentro del panel */
.panel__section {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}
.section-header-panel {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.section-title-panel {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0;
}

/* Bitácoras */
.bitacora-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}
.bitacora-item {
  padding: var(--space-3);
  background: var(--bg-surface);
  border-radius: var(--radius-md);
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}
.bitacora-item__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.bitacora-fecha {
  font-size: 10px;
  color: var(--text-muted);
}
.bitacora-desc {
  font-size: var(--text-xs);
  color: var(--text-secondary);
  margin: 0;
  line-height: 1.4;
}
.gestion-tipo {
  font-size: 10px;
  font-weight: var(--font-semibold);
  padding: 1px 8px;
  border-radius: var(--radius-full);
  text-transform: uppercase;
}
.gestion-tipo--primary  { background: var(--primary-soft);   color: var(--primary); }
.gestion-tipo--success  { background: var(--success-soft);   color: var(--success); }
.gestion-tipo--novedad  { background: var(--warning-soft);   color: var(--warning); }
.gestion-tipo--muted    { background: var(--bg-deep);        color: var(--text-muted); }

.evento-tipo {
  font-size: 10px;
  font-weight: var(--font-semibold);
  color: var(--secondary);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}
.evento-diff {
  font-size: 11px;
  color: var(--text-secondary);
  display: flex;
  flex-direction: column;
  gap: 2px;
  font-family: var(--font-mono, monospace);
}
.evento-diff strong { color: var(--text-primary); font-weight: var(--font-semibold); }

/* Aplicaciones */
.aplicaciones-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}
.aplicacion-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-3);
  background: var(--bg-surface);
  border-radius: var(--radius-md);
  border-left: 3px solid var(--success);
}
.aplicacion-item__info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.aplicacion-monto {
  font-size: var(--text-sm);
  font-weight: var(--font-bold);
  color: var(--success);
  font-family: var(--font-mono, monospace);
}
.aplicacion-fecha {
  font-size: 10px;
  color: var(--text-muted);
}

/* Modales */
.modal-overlay {
  position: fixed; inset: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 200;
}
.modal {
  width: 480px;
  max-width: 92vw;
  background: var(--bg-elevated);
  border-radius: var(--radius-xl);
  padding: var(--space-5);
  max-height: 90vh;
  overflow-y: auto;
}
.modal__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-4);
}
.modal__title {
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  margin: 0;
}
.modal__close {
  width: 32px; height: 32px;
  border-radius: var(--radius-full);
  background: transparent;
  color: var(--text-secondary);
  border: 1px solid var(--glass-border);
  cursor: pointer;
  display: flex; align-items: center; justify-content: center;
}
.modal__body {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}
.modal__actions {
  display: flex;
  justify-content: flex-end;
  gap: var(--space-2);
  margin-top: var(--space-3);
}
.modal-error {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  color: var(--error);
  font-size: var(--text-xs);
  padding: var(--space-2) var(--space-3);
  background: var(--error-soft);
  border-radius: var(--radius-md);
}

/* Form fields */
.field { display: flex; flex-direction: column; gap: 4px; }
.field__label { font-size: var(--text-xs); color: var(--text-secondary); font-weight: var(--font-medium); }
.field__hint { font-size: 10px; color: var(--text-muted); margin-top: 2px; }
.req { color: var(--error); }
.field__input,
.field__select,
.field__textarea {
  background: var(--bg-surface);
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  padding: var(--space-2) var(--space-3);
  color: var(--text-primary);
  font-family: var(--font-body);
  font-size: var(--text-sm);
  color-scheme: dark;
}
.field__input:focus,
.field__select:focus,
.field__textarea:focus {
  outline: none;
  border-color: var(--primary);
}
.form-row {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: var(--space-3);
}

/* Tabs en modal aplicar factura */
.cruce-tabs {
  display: flex;
  gap: var(--space-1);
  border-bottom: 1px solid var(--border);
  margin-bottom: var(--space-3);
}
.cruce-tab {
  padding: var(--space-2) var(--space-3);
  background: transparent;
  border: none;
  border-bottom: 2px solid transparent;
  color: var(--text-tertiary);
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  cursor: pointer;
}
.cruce-tab--active {
  color: var(--primary);
  border-bottom-color: var(--primary);
}

.facturas-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
  max-height: 240px;
  overflow-y: auto;
}
.factura-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-3);
  background: var(--bg-surface);
  border: 1px solid transparent;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all 0.15s;
}
.factura-option:hover { background: var(--bg-hover); }
.factura-option--selected {
  border-color: var(--primary);
  background: rgba(0, 212, 255, 0.05);
}
.factura-option__left {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.factura-option__num {
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  font-family: var(--font-mono, monospace);
}
.factura-option__meta {
  font-size: 10px;
  color: var(--text-muted);
}
.factura-option__valor {
  font-size: var(--text-xs);
  font-weight: var(--font-bold);
  color: var(--primary);
  font-family: var(--font-mono, monospace);
}

/* Loading / empty inline */
.mini-loading {
  display: flex; align-items: center; gap: var(--space-2);
  padding: var(--space-4);
  color: var(--text-muted);
  font-size: var(--text-xs);
  justify-content: center;
}
.mini-empty {
  padding: var(--space-5);
  color: var(--text-muted);
  font-size: var(--text-xs);
  text-align: center;
  background: var(--bg-surface);
  border-radius: var(--radius-md);
}

/* Botones */
.btn {
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-4);
  border-radius: var(--radius-full);
  font-family: var(--font-body);
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  cursor: pointer;
  transition: all 0.15s;
  border: 1px solid transparent;
  background: transparent;
  color: var(--text-secondary);
}
.btn:disabled { opacity: 0.5; cursor: not-allowed; }
.btn--sm { padding: var(--space-1) var(--space-3); font-size: 11px; }
.btn--full { width: 100%; justify-content: center; }
.btn--ghost { border-color: var(--glass-border); }
.btn--ghost:hover:not(:disabled) { background: var(--bg-hover); color: var(--text-primary); }
.btn--primary {
  background: var(--primary);
  color: var(--bg-deep);
  font-weight: var(--font-bold);
}
.btn--primary:hover:not(:disabled) { box-shadow: var(--shadow-glow-sm); }
.btn--accent {
  background: linear-gradient(135deg, var(--primary), var(--secondary));
  color: var(--bg-deep);
  font-weight: var(--font-bold);
}
.btn--accent:hover:not(:disabled) { box-shadow: var(--shadow-glow-sm); }
.btn--danger {
  border-color: var(--error);
  color: var(--error);
}
.btn--danger:hover:not(:disabled) {
  background: var(--error-soft);
}
</style>
