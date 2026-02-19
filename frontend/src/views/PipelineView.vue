<template>
  <AppLayout>
    <div class="page-placeholder">
      <span class="material-symbols-outlined">construction</span>
      <h2>Pipeline</h2>
      <p>Esta vista está en construcción</p>
    </div>
  </AppLayout>
</template>

<script setup>
import AppLayout from '@/components/layout/AppLayout.vue';
</script>

<style scoped>
.page-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  color: var(--md-sys-color-on-surface-variant);
  gap: var(--md-sys-spacing-md);
}

.page-placeholder .material-symbols-outlined {
  font-size: 64px;
}

.page-placeholder h2 {
  font: var(--md-sys-typescale-headline-medium);
}

.page-placeholder p {
  font: var(--md-sys-typescale-body-large);
}
</style>
