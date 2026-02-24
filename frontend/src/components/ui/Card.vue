<template>
  <div :class="['card', `card--${variant}`, { 'card--hoverable': hoverable }]">
    <div v-if="$slots.header || title" class="card__header">
      <slot name="header">
        <div class="card__header-content">
          <div v-if="icon" class="card__icon">
            <Icon :name="icon" :size="22" />
          </div>
          <div class="card__titles">
            <h3 v-if="title" class="card__title">{{ title }}</h3>
            <p v-if="subtitle" class="card__subtitle">{{ subtitle }}</p>
          </div>
        </div>
        <div v-if="$slots.actions" class="card__actions">
          <slot name="actions" />
        </div>
      </slot>
    </div>
    
    <div v-if="$slots.default" class="card__content" :class="{ 'card__content--no-header': !$slots.header && !title }">
      <slot />
    </div>
    
    <div v-if="$slots.footer" class="card__footer">
      <slot name="footer" />
    </div>
  </div>
</template>

<script setup>
import Icon from './Icon.vue';

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
});
</script>

<style scoped>
.card {
  background: var(--glass-bg);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-xl);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  overflow: hidden;
  transition: all var(--duration-base) var(--ease-out);
}

/* Variants */
.card--gradient {
  background: var(--gradient-card);
}

.card--bordered {
  border-color: var(--border-light);
}

/* Hoverable */
.card--hoverable {
  cursor: pointer;
}

.card--hoverable:hover {
  transform: translateY(-4px);
  border-color: var(--primary);
  box-shadow: var(--shadow-lg), 0 0 40px var(--primary-soft);
}

/* Header */
.card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--space-5);
  border-bottom: 1px solid var(--border);
}

.card__header-content {
  display: flex;
  align-items: center;
  gap: var(--space-4);
}

.card__icon {
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--primary-soft);
  border-radius: var(--radius-md);
  color: var(--primary);
}

.card__titles {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.card__title {
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
}

.card__subtitle {
  font-size: var(--text-sm);
  color: var(--text-tertiary);
}

/* Content */
.card__content {
  padding: var(--space-5);
}

.card__content--no-header {
  padding-top: var(--space-5);
}

.card__header + .card__content {
  padding-top: var(--space-4);
}

/* Footer */
.card__footer {
  padding: var(--space-4) var(--space-5);
  border-top: 1px solid var(--border);
  background: rgba(0, 0, 0, 0.2);
}
</style>
