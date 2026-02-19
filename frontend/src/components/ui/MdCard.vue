<template>
  <div :class="['md-card', `md-card--${variant}`]">
    <div v-if="$slots.media" class="md-card__media">
      <slot name="media" />
    </div>
    
    <div v-if="$slots.header || title" class="md-card__header">
      <slot name="header">
        <div class="md-card__header-text">
          <div v-if="title" class="md-card__title">{{ title }}</div>
          <div v-if="subtitle" class="md-card__subtitle">{{ subtitle }}</div>
        </div>
      </slot>
    </div>
    
    <div v-if="$slots.default" class="md-card__content">
      <slot />
    </div>
    
    <div v-if="$slots.actions" class="md-card__actions">
      <slot name="actions" />
    </div>
  </div>
</template>

<script setup>
defineProps({
  variant: {
    type: String,
    default: 'elevated', // elevated, filled, outlined
    validator: (v) => ['elevated', 'filled', 'outlined'].includes(v)
  },
  title: String,
  subtitle: String,
});
</script>

<style scoped>
.md-card {
  display: flex;
  flex-direction: column;
  border-radius: var(--md-sys-shape-corner-medium);
  overflow: hidden;
}

/* Elevated */
.md-card--elevated {
  background-color: var(--md-sys-color-surface-container-low);
  box-shadow: var(--md-sys-elevation-1);
}

/* Filled */
.md-card--filled {
  background-color: var(--md-sys-color-surface-container-highest);
}

/* Outlined */
.md-card--outlined {
  background-color: var(--md-sys-color-surface);
  border: 1px solid var(--md-sys-color-outline-variant);
}

.md-card__media {
  width: 100%;
  overflow: hidden;
}

.md-card__media img {
  width: 100%;
  height: auto;
  display: block;
}

.md-card__header {
  padding: var(--md-sys-spacing-md);
}

.md-card__header-text {
  display: flex;
  flex-direction: column;
  gap: var(--md-sys-spacing-xs);
}

.md-card__title {
  font: var(--md-sys-typescale-title-large);
  color: var(--md-sys-color-on-surface);
}

.md-card__subtitle {
  font: var(--md-sys-typescale-body-medium);
  color: var(--md-sys-color-on-surface-variant);
}

.md-card__content {
  padding: 0 var(--md-sys-spacing-md) var(--md-sys-spacing-md);
}

.md-card__actions {
  display: flex;
  gap: var(--md-sys-spacing-sm);
  padding: var(--md-sys-spacing-md);
  padding-top: 0;
}
</style>
