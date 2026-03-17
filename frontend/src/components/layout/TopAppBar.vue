<template>
  <header class="top-app-bar">
    <div class="top-app-bar__leading">
      <slot name="leading">
        <h1 class="top-app-bar__title">{{ title }}</h1>
      </slot>
    </div>

    <div class="top-app-bar__trailing">
      <slot name="trailing">
        <div class="top-app-bar__user">
          <span class="top-app-bar__user-name">{{ userName }}</span>
          <div class="top-app-bar__avatar">
            <Icon name="user" :size="18" />
          </div>
        </div>
      </slot>
    </div>
  </header>
</template>

<script setup>
import Icon from '@/components/ui/Icon.vue';

defineProps({
  title: {
    type: String,
    default: ''
  },
  userName: {
    type: String,
    default: ''
  }
});
</script>

<style scoped>
.top-app-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: var(--header-height);
  padding: 0 var(--space-6);
  background: var(--bg-base);
  border-bottom: 1px solid var(--glass-border);
  position: sticky;
  top: 0;
  z-index: var(--z-sticky);
}

.top-app-bar__leading {
  display: flex;
  align-items: center;
  gap: var(--space-4);
}

.top-app-bar__title {
  font-family: var(--font-display);
  font-size: var(--text-lg);
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
}

.top-app-bar__trailing {
  display: flex;
  align-items: center;
  gap: var(--space-4);
}

.top-app-bar__user {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.top-app-bar__user-name {
  font-family: var(--font-body);
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

.top-app-bar__avatar {
  width: 36px;
  height: 36px;
  border-radius: var(--radius-full);
  background: var(--primary-soft);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--primary);
}
</style>
