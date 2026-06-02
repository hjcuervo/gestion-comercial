<template>
  <div class="cp">
    <div class="cp__head">
      <h3 class="cp__title">Medios de contacto</h3>
      <GcButton variant="default" size="sm" icon="plus" @click="openCreate">Agregar</GcButton>
    </div>

    <div v-if="loading" class="cp__state"><GcSpinner :size="18" /></div>
    <GcEmpty v-else-if="!contactos.length" icon="address-book" message="Sin medios de contacto" />

    <div v-else class="cp__list">
      <GcListRow
        v-for="c in contactos"
        :key="c.id"
        :tone="toneFor(c)"
      >
        <template #lead>
          <GcIcon :name="iconFor(c)" :size="18" />
        </template>

        <div class="cp__row">
          <div class="cp__line">
            <span class="cp__valor gc-mono">{{ displayValor(c) }}</span>
            <GcBadge v-if="c.esPrincipal" tone="accent" soft label="Principal" />
          </div>
          <div class="cp__meta">
            <span v-if="c.etiqueta">{{ c.etiqueta }}</span>
            <span v-if="c.categoria" class="cp__sep">· {{ categoriaLabel(c.categoria) }}</span>
            <span v-if="c.tipoCanal === 'EMAIL' && !c.enviable" class="cp__sep cp__warn">· no enviable</span>
          </div>
        </div>

        <template #actions>
          <!-- Teléfono / Celular -->
          <GcButton v-if="esTelefono(c)" variant="ghost" size="sm" icon="phone" title="Llamar" @click="llamar(c)" />
          <GcButton v-if="esTelefono(c) && c.esWhatsapp" variant="ghost" size="sm" icon="brand-whatsapp" title="WhatsApp" @click="abrirWhatsapp(c)" />
          <!-- Email -->
          <GcButton v-if="c.tipoCanal === 'EMAIL'" variant="ghost" size="sm" icon="mail" title="Enviar correo" :disabled="!c.enviable" @click="enviarCorreo(c)" />
          <!-- Red social -->
          <GcButton v-if="c.tipoCanal === 'RED_SOCIAL'" variant="ghost" size="sm" :icon="c.redSocialIcono || 'link'" title="Abrir" @click="abrirUrl(c)" />
          <!-- Comunes -->
          <GcButton v-if="!c.esPrincipal" variant="ghost" size="sm" icon="star" title="Marcar principal" @click="$emit('principal', c)" />
          <GcButton variant="ghost" size="sm" icon="edit" title="Editar" @click="openEdit(c)" />
          <GcButton variant="ghost" size="sm" icon="trash" title="Eliminar" @click="$emit('eliminar', c)" />
        </template>
      </GcListRow>
    </div>
  </div>
</template>

<script setup>
import { contactoService } from '@/services/contacto.service';
import GcButton from '@/components/ui/GcButton.vue';
import GcListRow from '@/components/ui/GcListRow.vue';
import GcBadge from '@/components/ui/GcBadge.vue';
import GcIcon from '@/components/ui/GcIcon.vue';
import GcEmpty from '@/components/ui/GcEmpty.vue';
import GcSpinner from '@/components/ui/GcSpinner.vue';

const props = defineProps({
  contactos: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
});

const emit = defineEmits(['create', 'edit', 'principal', 'eliminar']);

const TONE = { TELEFONO: 'info', CELULAR: 'info', EMAIL: 'accent', RED_SOCIAL: 'success' };
const ICON = { TELEFONO: 'phone', CELULAR: 'phone', EMAIL: 'mail', RED_SOCIAL: 'world' };
const CAT = { PERSONAL: 'Personal', EMPRESARIAL: 'Empresarial', OTRO: 'Otro' };

function toneFor(c) { return c.activo === false ? 'neutral' : (TONE[c.tipoCanal] || 'neutral'); }
function iconFor(c) {
  if (c.tipoCanal === 'RED_SOCIAL') return c.redSocialIcono || 'world';
  return ICON[c.tipoCanal] || 'point';
}
function categoriaLabel(c) { return CAT[c] || c; }
function esTelefono(c) { return c.tipoCanal === 'TELEFONO' || c.tipoCanal === 'CELULAR'; }
function displayValor(c) {
  if (esTelefono(c)) return c.numeroE164 || c.valor;
  return c.valor;
}

function openCreate() { emit('create'); }
function openEdit(c) { emit('edit', c); }

// ---- Acciones externas ----
async function llamar(c) {
  // El payload del backend deja el dato listo para la plataforma externa.
  // Por ahora se inicia la marcación nativa (tel:); F-CT6 cablea la plataforma real.
  const data = await contactoService.payloadLlamada(c.id);
  if (data?.numeroE164) {
    const tel = data.extension ? `${data.numeroE164},${data.extension}` : data.numeroE164;
    window.open(`tel:${tel}`, '_self');
  }
}

function abrirWhatsapp(c) {
  if (c.whatsappLink) window.open(c.whatsappLink, '_blank', 'noopener');
}

async function enviarCorreo(c) {
  const data = await contactoService.payloadCorreo(c.id);
  if (data?.email) window.open(`mailto:${data.email}`, '_self');
}

function abrirUrl(c) {
  if (c.url) window.open(c.url, '_blank', 'noopener');
}
</script>

<style scoped>
.cp { display: flex; flex-direction: column; gap: var(--gc-space-2); }
.cp__head { display: flex; align-items: center; justify-content: space-between; }
.cp__title { font-size: var(--gc-fs-xs); text-transform: uppercase; letter-spacing: 0.06em; color: var(--gc-text-3); }
.cp__state { display: flex; justify-content: center; padding: var(--gc-space-5); color: var(--gc-text-3); }
.cp__list { border: 1px solid var(--gc-border); border-radius: var(--gc-radius-lg); overflow: hidden; }
.cp__list > :deep(.gc-row:last-child) { border-bottom: none; }
.cp__row { display: flex; flex-direction: column; gap: 2px; min-width: 0; }
.cp__line { display: flex; align-items: center; gap: var(--gc-space-2); min-width: 0; }
.cp__valor { font-size: var(--gc-fs-md); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.cp__meta { font-size: var(--gc-fs-xs); color: var(--gc-text-3); display: flex; gap: 4px; flex-wrap: wrap; }
.cp__sep { color: var(--gc-text-3); }
.cp__warn { color: var(--gc-warning); }
</style>
