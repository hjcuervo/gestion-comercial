<template>
  <nav class="nav-rail">
    <div class="nav-rail__logo">
      <span class="logo-text gradient-text">GC</span>
    </div>

    <div class="nav-rail__items">
      <router-link
        v-for="item in items"
        :key="item.path"
        :to="item.path"
        class="nav-rail__item"
        :class="{ 'nav-rail__item--active': isActive(item.path) }"
      >
        <div class="nav-rail__item-icon">
          <Icon :name="item.icon" :size="20" />
        </div>
        <span class="nav-rail__item-label">{{ item.label }}</span>
      </router-link>
    </div>

    <div class="nav-rail__footer">
      <button class="nav-rail__item" @click="$emit('logout')">
        <div class="nav-rail__item-icon">
          <Icon name="logout" :size="20" />
        </div>
        <span class="nav-rail__item-label">Salir</span>
      </button>
    </div>
  </nav>
</template>

<script setup>
import { useRoute } from 'vue-router';
import Icon from '@/components/ui/Icon.vue';

const route = useRoute();

defineProps({
  items: {
    type: Array,
    default: () => []
  }
});

defineEmits(['toggle-drawer', 'logout']);

const isActive = (path) => {
  if (path === '/') return route.path === '/';
  return route.path.startsWith(path);
};
</script>

<style scoped>
.nav-rail {
  display: flex;
  flex-direction: column;
  width: var(--sidebar-width);
  height: 100vh;
  background: var(--bg-elevated);
  border-right: 1px solid var(--glass-border);
  padding: var(--space-4) 0;
  position: fixed;
  left: 0;
  top: 0;
  z-index: var(--z-fixed);
}

.nav-rail__logo {
  display: flex;
  justify-content: center;
  align-items: center;
  padding-bottom: var(--space-6);
}

.logo-text {
  font-family: var(--font-display);
  font-size: var(--text-xl);
  font-weight: 800;
  letter-spacing: -0.02em;
}

.nav-rail__items {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-1);
  padding: 0 var(--space-2);
}

.nav-rail__item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  padding: var(--space-2) var(--space-1);
  width: 100%;
  text-decoration: none;
  color: var(--text-muted);
  border: none;
  background: transparent;
  cursor: pointer;
  border-radius: var(--radius-md);
  transition: all var(--duration-fast) var(--ease-out);
}

.nav-rail__item:hover {
  color: var(--text-secondary);
  background: var(--glass-hover);
}

.nav-rail__item--active {
  color: var(--primary);
}

.nav-rail__item-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 28px;
  border-radius: var(--radius-full);
  transition: background-color var(--duration-fast) var(--ease-out);
}

.nav-rail__item--active .nav-rail__item-icon {
  background: var(--primary-soft);
}

.nav-rail__item-label {
  font-family: var(--font-body);
  font-size: 11px;
  font-weight: 500;
  line-height: 1;
  white-space: nowrap;
}

.nav-rail__footer {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0 var(--space-2);
  border-top: 1px solid var(--glass-border);
  padding-top: var(--space-4);
}
</style>
