<template>
  <!-- blank: sin chasis (login). app: shell nuevo. legacy: vista trae su layout viejo (transitorio). -->
  <AppShell v-if="layout === 'app'">
    <router-view />
  </AppShell>
  <router-view v-else />
</template>

<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import AppShell from '@/components/layout/AppShell.vue';

const route = useRoute();

/**
 * meta.layout por ruta:
 *  - 'blank'  → login (sin chasis)
 *  - 'app'    → shell nuevo (AppShell) — pantallas ya migradas al rediseño
 *  - 'legacy' → pantalla aún no migrada; trae su propio <AppLayout> (transitorio,
 *               se elimina cuando la pantalla migre a 'app')
 * Default 'app' para que toda pantalla nueva entre al shell sin configurar nada.
 */
const layout = computed(() => route.meta.layout || 'app');
</script>

<style>
#app { min-height: 100vh; }
</style>
