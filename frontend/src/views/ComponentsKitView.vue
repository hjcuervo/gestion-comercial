<template>
  <div class="kit">
    <header class="kit__head">
      <h1 class="kit__title">Biblioteca base — Instrumento</h1>
      <p class="kit__note">Página temporal de verificación (RF1). Quitar antes del merge.</p>
    </header>

    <!-- Indicadores -->
    <section class="kit__sec">
      <h2 class="kit__h2">GcStatStrip / GcStat</h2>
      <GcStatStrip>
        <GcStat label="Oportunidades" value="142" delta="+8" delta-tone="positive" />
        <GcStat label="Contratos vigentes" value="37" />
        <GcStat label="Por facturar" value="$ 89.4M" delta="-3" delta-tone="negative" />
        <GcStat label="TRM mes" value="3,985.10" />
      </GcStatStrip>
    </section>

    <!-- Botones -->
    <section class="kit__sec">
      <h2 class="kit__h2">GcButton</h2>
      <div class="kit__row">
        <GcButton variant="primary">Primario</GcButton>
        <GcButton variant="default" icon="plus">Default</GcButton>
        <GcButton variant="ghost" icon="dots">Ghost</GcButton>
        <GcButton variant="danger" icon="trash">Eliminar</GcButton>
        <GcButton variant="primary" :loading="true">Cargando</GcButton>
        <GcButton variant="default" disabled>Deshabilitado</GcButton>
      </div>
    </section>

    <!-- Campos -->
    <section class="kit__sec">
      <h2 class="kit__h2">Campos</h2>
      <div class="kit__grid">
        <GcInput v-model="texto" label="Texto" placeholder="Escribe algo" icon="search" />
        <GcInput v-model="codigo" label="Código (mono)" placeholder="FAC-0001" mono />
        <GcSelect v-model="estado" label="Estado" :options="estadoOpts" placeholder="Selecciona…" />
        <GcInput v-model="conError" label="Con error" :error="'Campo requerido'" />
      </div>
      <GcTextarea v-model="notas" label="Notas" placeholder="Observaciones…" :rows="3" />
    </section>

    <!-- Badges -->
    <section class="kit__sec">
      <h2 class="kit__h2">GcBadge</h2>
      <div class="kit__row">
        <GcBadge tone="success" label="Vigente" />
        <GcBadge tone="warning" label="Suspendido" />
        <GcBadge tone="danger" label="Terminado" />
        <GcBadge tone="info" label="En curso" />
        <GcBadge tone="accent" label="Modificado" />
        <GcBadge tone="neutral" label="Borrador" />
        <GcBadge tone="success" label="Facturada" soft />
      </div>
    </section>

    <!-- Renglones -->
    <section class="kit__sec">
      <h2 class="kit__h2">GcListRow</h2>
      <div class="kit__rows">
        <GcListRow v-for="r in filas" :key="r.id" :tone="r.tone" clickable>
          <template #lead><GcIcon name="file-text" :size="16" /></template>
          <div class="kit__rowmain">
            <span class="kit__rowtitle">{{ r.titulo }}</span>
            <span class="kit__rowmeta gc-mono">{{ r.id }} · {{ r.fecha }}</span>
          </div>
          <template #actions>
            <GcBadge :tone="r.tone" :label="r.estado" />
            <GcButton variant="ghost" size="sm" icon="chevron-right" />
          </template>
        </GcListRow>
      </div>
    </section>

    <!-- Overlays + estados -->
    <section class="kit__sec">
      <h2 class="kit__h2">Overlays / estados</h2>
      <div class="kit__row">
        <GcButton variant="default" @click="modal = true">Abrir GcModal</GcButton>
        <GcButton variant="default" @click="drawer = true">Abrir GcDrawer</GcButton>
        <GcSpinner :size="22" />
      </div>
      <GcEmpty icon="inbox" message="No se encontraron registros">
        <template #action><GcButton variant="primary" icon="plus">Crear el primero</GcButton></template>
      </GcEmpty>
    </section>

    <GcModal :open="modal" title="Ejemplo de modal" @close="modal = false">
      <p>Contenido del diálogo. Backdrop sutil, borde fino, sombra mínima.</p>
      <template #footer>
        <GcButton variant="ghost" @click="modal = false">Cancelar</GcButton>
        <GcButton variant="primary" @click="modal = false">Aceptar</GcButton>
      </template>
    </GcModal>

    <GcDrawer :open="drawer" title="Panel contextual" @close="drawer = false">
      <div class="kit__grid">
        <GcInput v-model="texto" label="Campo en cajón" placeholder="…" />
        <GcTextarea v-model="notas" label="Notas" :rows="4" />
      </div>
      <template #footer>
        <GcButton variant="ghost" @click="drawer = false">Cerrar</GcButton>
        <GcButton variant="primary" @click="drawer = false">Guardar</GcButton>
      </template>
    </GcDrawer>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import GcButton from '@/components/ui/GcButton.vue';
import GcInput from '@/components/ui/GcInput.vue';
import GcSelect from '@/components/ui/GcSelect.vue';
import GcTextarea from '@/components/ui/GcTextarea.vue';
import GcBadge from '@/components/ui/GcBadge.vue';
import GcListRow from '@/components/ui/GcListRow.vue';
import GcStat from '@/components/ui/GcStat.vue';
import GcStatStrip from '@/components/ui/GcStatStrip.vue';
import GcModal from '@/components/ui/GcModal.vue';
import GcDrawer from '@/components/ui/GcDrawer.vue';
import GcEmpty from '@/components/ui/GcEmpty.vue';
import GcSpinner from '@/components/ui/GcSpinner.vue';
import GcIcon from '@/components/ui/GcIcon.vue';

const texto = ref('');
const codigo = ref('');
const estado = ref('');
const conError = ref('');
const notas = ref('');
const modal = ref(false);
const drawer = ref(false);

const estadoOpts = [
  { value: 'VIGENTE', label: 'Vigente' },
  { value: 'SUSPENDIDO', label: 'Suspendido' },
  { value: 'TERMINADO', label: 'Terminado' },
  { value: 'LIQUIDADO', label: 'Liquidado' },
];

const filas = [
  { id: 'CTR-0026', titulo: 'Contrato de soporte anual', fecha: '2026-05-12', estado: 'Vigente', tone: 'success' },
  { id: 'CTR-0031', titulo: 'Orden de servicio QA', fecha: '2026-05-09', estado: 'Suspendido', tone: 'warning' },
  { id: 'CTR-0018', titulo: 'Consultoría arquitectura', fecha: '2026-04-28', estado: 'Terminado', tone: 'danger' },
];
</script>

<style scoped>
.kit { padding: var(--gc-space-8); display: flex; flex-direction: column; gap: var(--gc-space-8); }
.kit__title { font-size: var(--gc-fs-2xl); }
.kit__note { font-size: var(--gc-fs-sm); color: var(--gc-text-3); margin-top: var(--gc-space-1); }
.kit__sec { display: flex; flex-direction: column; gap: var(--gc-space-3); }
.kit__h2 {
  font-size: var(--gc-fs-xs);
  text-transform: uppercase;
  letter-spacing: 0.06em;
  color: var(--gc-text-3);
}
.kit__row { display: flex; flex-wrap: wrap; align-items: center; gap: var(--gc-space-3); }
.kit__grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: var(--gc-space-4);
}
.kit__rows {
  border: 1px solid var(--gc-border);
  border-radius: var(--gc-radius-lg);
  overflow: hidden;
}
.kit__rows > :deep(.gc-row:last-child) { border-bottom: none; }
.kit__rowmain { display: flex; flex-direction: column; gap: 2px; }
.kit__rowtitle { font-size: var(--gc-fs-md); color: var(--gc-text); }
.kit__rowmeta { font-size: var(--gc-fs-xs); color: var(--gc-text-3); }
</style>
