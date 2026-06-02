<template>
  <div class="emp-surface">
    <div v-if="!id" class="emp-empty"><GcEmpty icon="hand-click" message="Selecciona una empresa de la izquierda" /></div>
    <div v-else-if="loadingDetalle" class="emp-empty"><GcSpinner :size="24" /></div>
    <template v-else-if="empresa">
      <!-- Contactos / oportunidades en el aside -->
      <Teleport to="#gc-shell-aside">
        <div v-if="asideReady" class="emp-aside">
          <div class="emp-aside__sec">
            <ContactosPanel
              :contactos="medios"
              :loading="loadingMedios"
              @create="openContactoCreate"
              @edit="openContactoEdit"
              @principal="marcarPrincipal"
              @eliminar="eliminarContacto"
            />
          </div>
          <div class="emp-aside__sec">
            <div class="emp-aside__head">
              <h3 class="emp-aside__title">Contactos</h3>
              <GcButton variant="default" size="sm" icon="plus" @click="showAsociarPersona = true">Asociar</GcButton>
            </div>
            <GcEmpty v-if="!contactos.length" icon="user-off" message="Sin contactos" />
            <div v-else class="emp-aside__list">
              <GcListRow v-for="p in contactos" :key="p.id" tone="info" clickable @click="$emit('open-persona', p.id)">
                <div class="emp-aside__row">
                  <span class="emp-aside__rowname">{{ p.nombreCompleto }}</span>
                  <span class="emp-aside__rowmeta">{{ p.numeroDocumento || '—' }}</span>
                </div>
                <template #actions>
                  <GcButton variant="ghost" size="sm" icon="unlink" @click.stop="desasociarPersona(p.id)" />
                </template>
              </GcListRow>
            </div>
          </div>
          <div class="emp-aside__sec">
            <h3 class="emp-aside__title">Oportunidades</h3>
            <GcEmpty v-if="!oportunidades.length" icon="briefcase-off" message="Sin oportunidades" />
            <div v-else class="emp-aside__list">
              <GcListRow v-for="o in oportunidades" :key="o.id" :tone="oppTone(o.estadoMacro)" clickable @click="$router.push(`/oportunidades/${o.id}`)">
                <div class="emp-aside__row">
                  <span class="emp-aside__rowname">{{ o.nombre }}</span>
                  <span class="emp-aside__rowmeta gc-mono">{{ fmtCurrency(o.valorEstimado, o.moneda) }}</span>
                </div>
              </GcListRow>
            </div>
          </div>
        </div>
      </Teleport>

      <header class="emp-head">
        <div class="emp-heading">
          <h1 class="emp-title">{{ empresa.razonSocial }}</h1>
          <GcBadge :tone="empresa.estado === 'ACTIVA' ? 'success' : 'neutral'" :label="empresa.estado === 'ACTIVA' ? 'Activa' : 'Inactiva'" />
          <GcBadge v-if="empresa.clasificacion" :tone="clasifTone(empresa.clasificacion)" soft :label="clasifLabel(empresa.clasificacion)" />
        </div>
        <GcButton variant="default" size="sm" icon="edit" @click="openEdit">Editar</GcButton>
      </header>

      <div class="emp-grid">
        <div class="emp-field"><span class="emp-label">Identificación</span><span class="emp-value gc-mono">{{ empresa.tipoDoc || '' }} {{ empresa.identificacionTributaria || '—' }}<template v-if="empresa.dv">-{{ empresa.dv }}</template></span></div>
        <div class="emp-field"><span class="emp-label">País</span><span class="emp-value">{{ empresa.paisNombre || empresa.pais || '—' }}</span></div>
        <div class="emp-field"><span class="emp-label">Departamento</span><span class="emp-value">{{ empresa.departamentoNombre || empresa.departamento || '—' }}</span></div>
        <div class="emp-field"><span class="emp-label">Ciudad</span><span class="emp-value">{{ empresa.ciudadNombre || empresa.ciudad || '—' }}</span></div>
        <div class="emp-field"><span class="emp-label">Dirección</span><span class="emp-value">{{ empresa.direccionFisica || '—' }}</span></div>
        <div class="emp-field"><span class="emp-label">Sitio web</span><a v-if="empresa.sitioWeb" :href="empresa.sitioWeb" target="_blank" rel="noopener" class="emp-link">{{ empresa.sitioWeb }}</a><span v-else class="emp-value">—</span></div>
        <div class="emp-field"><span class="emp-label">Sector</span><span class="emp-value">{{ empresa.sectorNombre || '—' }}</span></div>
        <div class="emp-field"><span class="emp-label">Tamaño</span><span class="emp-value">{{ tamanoLabel(empresa.tamano) }}</span></div>
        <div class="emp-field"><span class="emp-label">Origen</span><span class="emp-value">{{ empresa.origenNombre || '—' }}</span></div>
        <div class="emp-field"><span class="emp-label">Propietario</span><span class="emp-value">{{ propietarioNombre(empresa.propietarioId) }}</span></div>
        <div class="emp-field"><span class="emp-label">Ingresos anuales</span><span class="emp-value gc-mono">{{ empresa.ingresosAnuales != null ? fmtCurrency(empresa.ingresosAnuales, 'COP') : '—' }}</span></div>
        <div class="emp-field emp-field--wide"><span class="emp-label">Descripción</span><span class="emp-value">{{ empresa.descripcion || '—' }}</span></div>
      </div>
    </template>

    <EmpresaModal :visible="showModal" :empresa="editing" :saving="saving" :error="modalError" @close="showModal = false" @submit="handleSubmit" />

    <AsociarPersonaModal :visible="showAsociarPersona" :saving="savingAsocP" :error="asocPError" @close="showAsociarPersona = false" @submit="handleAsociarPersona" />

    <ContactoDrawer
      :open="showContacto"
      owner="empresa"
      :owner-nombre="empresa?.razonSocial || ''"
      :contacto="editingContacto"
      :redes="redes"
      :saving="savingContacto"
      :server-error="contactoError"
      @close="showContacto = false"
      @submit="handleContactoSubmit"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, watch, nextTick } from 'vue';
import { empresaService } from '@/services/empresa.service';
import { personaService } from '@/services/persona.service';
import { oportunidadService } from '@/services/oportunidad.service';
import { contactoService } from '@/services/contacto.service';
import { usuarioService } from '@/services/usuario.service';
import { formatCurrency } from '@/utils/currency';
import EmpresaModal from '@/components/empresa/EmpresaModal.vue';
import AsociarPersonaModal from '@/components/persona/AsociarPersonaModal.vue';
import ContactosPanel from '@/components/contacto/ContactosPanel.vue';
import ContactoDrawer from '@/components/contacto/ContactoDrawer.vue';
import GcButton from '@/components/ui/GcButton.vue';
import GcBadge from '@/components/ui/GcBadge.vue';
import GcListRow from '@/components/ui/GcListRow.vue';
import GcEmpty from '@/components/ui/GcEmpty.vue';
import GcSpinner from '@/components/ui/GcSpinner.vue';

const props = defineProps({
  id: { type: [String, Number], default: null },
});
const emit = defineEmits(['updated', 'open-persona']);

const empresa = ref(null);
const contactos = ref([]);
const oportunidades = ref([]);
const loadingDetalle = ref(false);
const asideReady = ref(false);

const showModal = ref(false);
const editing = ref(null);
const saving = ref(false);
const modalError = ref(null);

// --- Asociar persona (existente o nueva) ---
const showAsociarPersona = ref(false);
const savingAsocP = ref(false);
const asocPError = ref(null);

async function handleAsociarPersona(data) {
  savingAsocP.value = true; asocPError.value = null;
  try {
    let personaId = data.personaId;
    if (data.modo === 'nueva') {
      const creada = await personaService.crear(data.nuevaPersona);
      personaId = creada.id;
    }
    await personaService.asociarEmpresa(personaId, { empresaId: props.id, ...data.rel });
    showAsociarPersona.value = false;
    await cargarDetalle();
  } catch (err) {
    asocPError.value = err.response?.data?.message || 'Error al asociar la persona';
  } finally {
    savingAsocP.value = false;
  }
}

async function desasociarPersona(personaId) {
  if (!confirm('¿Desasociar esta persona de la empresa?')) return;
  try {
    await personaService.desasociarEmpresa(personaId, props.id);
    await cargarDetalle();
  } catch (err) { console.error('Error al desasociar persona:', err); }
}

// --- Contactos (medios) ---
const medios = ref([]);
const loadingMedios = ref(false);
const redes = ref([]);
const showContacto = ref(false);
const editingContacto = ref(null);
const savingContacto = ref(false);
const contactoError = ref('');

function fmtCurrency(v, m) { return formatCurrency(v, m); }
const OPP_TONE = { ABIERTA: 'info', SEGUIMIENTO: 'warning', GANADA: 'success', CONTRATADA: 'accent', PERDIDA: 'danger', NO_CONCRETADA: 'neutral' };
function oppTone(e) { return OPP_TONE[e] || 'neutral'; }

const usuarios = ref([]);
const CLASIF = { PROSPECTO: { tone: 'info', label: 'Prospecto' }, CLIENTE: { tone: 'success', label: 'Cliente' }, ALIADO: { tone: 'accent', label: 'Aliado' } };
function clasifTone(c) { return CLASIF[c]?.tone || 'neutral'; }
function clasifLabel(c) { return CLASIF[c]?.label || c; }
const TAMANO = { MICRO: 'Micro', PEQUENA: 'Pequeña', MEDIANA: 'Mediana', GRANDE: 'Grande' };
function tamanoLabel(t) { return t ? (TAMANO[t] || t) : '—'; }
function propietarioNombre(id) {
  if (!id) return '—';
  const u = usuarios.value.find((x) => String(x.id) === String(id));
  return u ? (u.nombreCompleto || (u.nombres + ' ' + u.apellidos)) : `#${id}`;
}

async function cargarDetalle() {
  if (!props.id) { empresa.value = null; return; }
  loadingDetalle.value = true;
  try {
    empresa.value = await empresaService.obtenerPorId(props.id);
    const [pers, opps] = await Promise.all([
      personaService.listar({ empresa_id: props.id, page_size: 100 }).catch(() => ({ data: [] })),
      oportunidadService.listar({ empresa_id: props.id, page_size: 100 }).catch(() => ({ data: [] })),
    ]);
    contactos.value = pers.data || [];
    oportunidades.value = opps.data || [];
    await cargarMedios();
  } catch (err) { console.error('Error cargando detalle empresa:', err); }
  finally { loadingDetalle.value = false; }
}

async function cargarMedios() {
  if (!props.id) { medios.value = []; return; }
  loadingMedios.value = true;
  try {
    medios.value = await contactoService.listarPorEmpresa(props.id);
  } catch (err) { console.error('Error cargando contactos:', err); medios.value = []; }
  finally { loadingMedios.value = false; }
}

function openCreate() { editing.value = null; modalError.value = null; showModal.value = true; }
function openEdit() { editing.value = { ...empresa.value }; modalError.value = null; showModal.value = true; }
async function handleSubmit(payload) {
  saving.value = true; modalError.value = null;
  try {
    if (editing.value) await empresaService.actualizar(editing.value.id, payload);
    else await empresaService.crear(payload);
    showModal.value = false;
    emit('updated');
    if (editing.value) await cargarDetalle();
  } catch (err) { modalError.value = err.response?.data?.message || 'Error al guardar empresa'; }
  finally { saving.value = false; }
}

function openContactoCreate() { editingContacto.value = null; contactoError.value = ''; showContacto.value = true; }
function openContactoEdit(c) { editingContacto.value = { ...c }; contactoError.value = ''; showContacto.value = true; }

async function handleContactoSubmit({ id, payload }) {
  savingContacto.value = true; contactoError.value = '';
  try {
    if (id) await contactoService.actualizar(id, payload);
    else await contactoService.crearParaEmpresa(props.id, payload);
    showContacto.value = false;
    await cargarMedios();
  } catch (err) { contactoError.value = err.response?.data?.message || 'Error al guardar el contacto'; }
  finally { savingContacto.value = false; }
}

async function marcarPrincipal(c) {
  try { await contactoService.marcarPrincipal(c.id); await cargarMedios(); }
  catch (err) { console.error('Error marcando principal:', err); }
}

async function eliminarContacto(c) {
  try { await contactoService.eliminar(c.id); await cargarMedios(); }
  catch (err) { console.error('Error eliminando contacto:', err); }
}

onMounted(async () => {
  await nextTick();
  asideReady.value = true;
  contactoService.listarRedesSociales().then((r) => { redes.value = r || []; }).catch(() => { redes.value = []; });
  usuarioService.listar().then((u) => { usuarios.value = u || []; }).catch(() => { usuarios.value = []; });
  if (props.id) await cargarDetalle();
});
watch(() => props.id, () => cargarDetalle());

defineExpose({ openCreate });
</script>

<style scoped>
.emp-surface { padding: var(--gc-space-6); }
.emp-empty { display: flex; justify-content: center; padding: var(--gc-space-12); }
.emp-head { display: flex; align-items: center; justify-content: space-between; margin-bottom: var(--gc-space-2); }
.emp-heading { display: flex; align-items: center; gap: var(--gc-space-3); }
.emp-title { font-size: var(--gc-fs-xl); }
.emp-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: var(--gc-space-4); margin-top: var(--gc-space-5); }
.emp-field { display: flex; flex-direction: column; gap: 2px; }
.emp-field--wide { grid-column: 1 / -1; }
.emp-label { font-size: var(--gc-fs-xs); text-transform: uppercase; letter-spacing: 0.04em; color: var(--gc-text-3); }
.emp-value { font-size: var(--gc-fs-md); color: var(--gc-text); }
.emp-link { font-size: var(--gc-fs-md); color: var(--gc-info); }
.emp-link:hover { text-decoration: underline; }

.emp-aside { padding: var(--gc-space-5); display: flex; flex-direction: column; gap: var(--gc-space-5); }
.emp-aside__sec { display: flex; flex-direction: column; gap: var(--gc-space-2); }
.emp-aside__head { display: flex; align-items: center; justify-content: space-between; }
.emp-aside__title { font-size: var(--gc-fs-xs); text-transform: uppercase; letter-spacing: 0.06em; color: var(--gc-text-3); }
.emp-aside__list { border: 1px solid var(--gc-border); border-radius: var(--gc-radius-lg); overflow: hidden; }
.emp-aside__list > :deep(.gc-row:last-child) { border-bottom: none; }
.emp-aside__row { display: flex; flex-direction: column; gap: 2px; min-width: 0; }
.emp-aside__rowname { font-size: var(--gc-fs-md); }
.emp-aside__rowmeta { font-size: var(--gc-fs-xs); color: var(--gc-text-3); }
</style>
