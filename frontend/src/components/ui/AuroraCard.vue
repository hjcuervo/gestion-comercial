<template>
  <div :class="['aurora-card', `aurora-card--${variant}`, { 'aurora-card--hoverable': hoverable, 'aurora-card--glow': glow }]">
    <div v-if="$slots.header || title" class="aurora-card__header">
      <slot name="header">
        <div class="aurora-card__header-content">
          <div v-if="icon" class="aurora-card__icon">
            <span class="material-icons-round">{{ icon }}</span>
          </div>
          <div class="aurora-card__titles">
            <h3 v-if="title" class="aurora-card__title">{{ title }}</h3>
            <p v-if="subtitle" class="aurora-card__subtitle">{{ subtitle }}</p>
          </div>
        </div>
        <div v-if="$slots.actions" class="aurora-card__actions">
          <slot name="actions" />
        </div>
      </slot>
    </div>
    
    <div v-if="$slots.default" class="aurora-card__content">
      <slot />
    </div>
    
    <div v-if="$slots.footer" class="aurora-card__footer">
      <slot name="footer" />
    </div>
  </div>
</template>

<script setup>
defineProps({
  variant: {
    type: String,
    default: 'default',
    validator: (v) => ['default', 'gradient', 'bordered'].includes(v)
  },
  title: String,
  subtitle: String,
  icon: String,
  hoverable: Boolean,
  glow: Boolean,
});
</script>

<style scoped>
.aurora-card {
  background: var(--aurora-bg-overlay);
  border: 1px solid var(--aurora-border);
  border-radius: var(--aurora-radius-xl);
  backdrop-filter: blur(20px);
  overflow: hidden;
  transition: all var(--aurora-transition-base);
}

/* Variants */
.aurora-card--gradient {
  background: var(--aurora-gradient-card);
}

.aurora-card--bordered {
  border: 1px solid var(--aurora-border-light);
}

/* Hoverable */
.aurora-card--hoverable {
  cursor: pointer;
}

.aurora-card--hoverable:hover {
  transform: translateY(-4px);
  border-color: var(--aurora-primary);
  box-shadow: var(--aurora-shadow-lg);
}

/* Glow Effect */
.aurora-card--glow {
  position: relative;
}

.aurora-card--glow::before {
  content: '';
  position: absolute;
  inset: -1px;
  background: var(--aurora-gradient-primary);
  border-radius: inherit;
  z-index: -1;
  opacity: 0;
  transition: opacity var(--aurora-transition-base);
}

.aurora-card--glow:hover::before {
  opacity: 0.5;
}

/* Header */
.aurora-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--aurora-space-5);
  border-bottom: 1px solid var(--aurora-border);
}

.aurora-card__header-content {
  display: flex;
  align-items: center;
  gap: var(--aurora-space-4);
}

.aurora-card__icon {
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--aurora-gradient-glow);
  border-radius: var(--aurora-radius-md);
}

.aurora-card__icon .material-icons-round {
  font-size: 22px;
  color: var(--aurora-primary-light);
}

.aurora-card__titles {
  display: flex;
  flex-direction: column;
  gap: var(--aurora-space-1);
}

.aurora-card__title {
  font-size: var(--aurora-text-base);
  font-weight: var(--aurora-font-semibold);
  color: var(--aurora-text-primary);
}

.aurora-card__subtitle {
  font-size: var(--aurora-text-sm);
  color: var(--aurora-text-tertiary);
}

/* Content */
.aurora-card__content {
  padding: var(--aurora-space-5);
}

.aurora-card__header + .aurora-card__content {
  padding-top: var(--aurora-space-4);
}

/* Footer */
.aurora-card__footer {
  padding: var(--aurora-space-4) var(--aurora-space-5);
  border-top: 1px solid var(--aurora-border);
  background: rgba(0, 0, 0, 0.2);
}
</style>
