<template>
  <!-- Lista maestra teletransportada a la zona izquierda del shell -->
  <Teleport to="#gc-shell-master">
    <OportunidadMasterList :selected-id="selectedId" @select="goTo" />
  </Teleport>

  <!-- Superficie central: detalle de la oportunidad seleccionada -->
  <div class="actividades-surface">
    <router-view v-if="selectedId" />
    <GcEmpty
      v-else
      icon="hand-click"
      message="Selecciona una oportunidad de la izquierda para ver y registrar su actividad"
    />
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useShell } from '@/composables/useShell';
import OportunidadMasterList from '@/components/actividad/OportunidadMasterList.vue';
import GcEmpty from '@/components/ui/GcEmpty.vue';

const route = useRoute();
const router = useRouter();
const { setRegions } = useShell();

const selectedId = computed(() => route.params.oportunidadId || null);

function goTo(id) {
  router.push(`/actividades/${id}`);
}

onMounted(() => setRegions({ master: true }));
onUnmounted(() => setRegions({ master: false, aside: false }));
</script>

<style scoped>
.actividades-surface { min-height: 100%; }
</style>
