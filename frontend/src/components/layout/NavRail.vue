<template>
  <nav class="nav-rail">
    <div class="nav-rail__header">
      <button class="nav-rail__menu-btn" @click="$emit('toggle-drawer')">
        <span class="material-icons">menu</span>
      </button>
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
          <span class="material-icons">{{ item.icon }}</span>
        </div>
        <span class="nav-rail__item-label">{{ item.label }}</span>
      </router-link>
    </div>
    
    <div class="nav-rail__footer">
      <button class="nav-rail__item" @click="$emit('logout')">
        <div class="nav-rail__item-icon">
          <span class="material-icons">logout</span>
        </div>
        <span class="nav-rail__item-label">Salir</span>
      </button>
    </div>
  </nav>
</template>

<script setup>
import { useRoute } from 'vue-router';

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
  width: var(--md-sys-layout-nav-rail-width);
  height: 100vh;
  background-color: var(--md-sys-color-surface-container);
  padding: var(--md-sys-spacing-md) 0;
  position: fixed;
  left: 0;
  top: 0;
  z-index: 100;
}

.nav-rail__header {
  display: flex;
  justify-content: center;
  padding-bottom: var(--md-sys-spacing-md);
}

.nav-rail__menu-btn {
  width: 56px;
  height: 56px;
  border: none;
  background: transparent;
  border-radius: var(--md-sys-shape-corner-large);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--md-sys-color-on-surface);
  transition: background-color var(--md-sys-motion-duration-short) var(--md-sys-motion-easing-standard);
}

.nav-rail__menu-btn:hover {
  background-color: var(--md-sys-color-surface-container-high);
}

.nav-rail__items {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--md-sys-spacing-xs);
  padding: 0 var(--md-sys-spacing-sm);
}

.nav-rail__item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--md-sys-spacing-xs);
  padding: var(--md-sys-spacing-xs);
  width: 100%;
  text-decoration: none;
  color: var(--md-sys-color-on-surface-variant);
  border: none;
  background: transparent;
  cursor: pointer;
  border-radius: var(--md-sys-shape-corner-large);
  transition: all var(--md-sys-motion-duration-short) var(--md-sys-motion-easing-standard);
}

.nav-rail__item:hover {
  background-color: var(--md-sys-color-surface-container-high);
}

.nav-rail__item--active {
  color: var(--md-sys-color-on-secondary-container);
}

.nav-rail__item-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 56px;
  height: 32px;
  border-radius: var(--md-sys-shape-corner-full);
  transition: background-color var(--md-sys-motion-duration-short) var(--md-sys-motion-easing-standard);
}

.nav-rail__item--active .nav-rail__item-icon {
  background-color: var(--md-sys-color-secondary-container);
}

.nav-rail__item-label {
  font: var(--md-sys-typescale-label-medium);
}

.nav-rail__footer {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0 var(--md-sys-spacing-sm);
}
</style>
