<template>
  <header class="header">
    <div class="header__left">
      <div class="header__breadcrumb">
        <router-link to="/" class="header__breadcrumb-item">
          <Icon name="home" :size="16" />
        </router-link>
        <template v-if="breadcrumbs.length">
          <span class="header__breadcrumb-sep">/</span>
          <template v-for="(crumb, i) in breadcrumbs" :key="crumb.path">
            <router-link 
              v-if="i < breadcrumbs.length - 1"
              :to="crumb.path" 
              class="header__breadcrumb-item"
            >
              {{ crumb.label }}
            </router-link>
            <span v-else class="header__breadcrumb-current">{{ crumb.label }}</span>
            <span v-if="i < breadcrumbs.length - 1" class="header__breadcrumb-sep">/</span>
          </template>
        </template>
      </div>
      <h1 class="header__title">{{ title }}</h1>
    </div>
    
    <div class="header__right">
      <!-- Search -->
      <div class="header__search" :class="{ 'header__search--focused': searchFocused }">
        <Icon name="search" :size="18" class="header__search-icon" />
        <input 
          type="text" 
          placeholder="Buscar..." 
          v-model="searchQuery"
          @focus="searchFocused = true"
          @blur="searchFocused = false"
          @input="$emit('search', searchQuery)"
        />
        <kbd class="header__search-kbd">⌘K</kbd>
      </div>
      
      <!-- Actions -->
      <div class="header__actions">
        <button class="header__action" @click="$emit('notification-click')">
          <Icon name="bell" :size="20" />
          <span v-if="notificationCount" class="header__badge">{{ notificationCount }}</span>
        </button>
        
        <slot name="actions" />
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref } from 'vue';
import Icon from '../ui/Icon.vue';

defineProps({
  title: { type: String, default: 'Dashboard' },
  breadcrumbs: { type: Array, default: () => [] },
  notificationCount: { type: Number, default: 0 }
});

defineEmits(['search', 'notification-click']);

const searchQuery = ref('');
const searchFocused = ref(false);
</script>

<style scoped>
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: var(--header-height);
  padding: 0 var(--space-6);
  background: rgba(13, 15, 20, 0.8);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-bottom: 1px solid var(--border);
  position: sticky;
  top: 0;
  z-index: var(--z-sticky);
}

.header__left {
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
}

.header__breadcrumb {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--text-xs);
}

.header__breadcrumb-item {
  color: var(--text-muted);
  text-decoration: none;
  display: flex;
  align-items: center;
  transition: color var(--duration-fast);
}

.header__breadcrumb-item:hover {
  color: var(--primary);
}

.header__breadcrumb-sep {
  color: var(--text-muted);
}

.header__breadcrumb-current {
  color: var(--text-tertiary);
}

.header__title {
  font-size: var(--text-xl);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  letter-spacing: -0.01em;
}

.header__right {
  display: flex;
  align-items: center;
  gap: var(--space-4);
}

/* Search */
.header__search {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-4);
  background: var(--glass-bg);
  border: 1px solid var(--border);
  border-radius: var(--radius-full);
  min-width: 280px;
  transition: all var(--duration-fast);
}

.header__search--focused {
  border-color: var(--primary);
  box-shadow: 0 0 0 3px var(--primary-soft);
}

.header__search-icon {
  color: var(--text-muted);
  flex-shrink: 0;
}

.header__search--focused .header__search-icon {
  color: var(--primary);
}

.header__search input {
  flex: 1;
  border: none;
  background: transparent;
  font-family: var(--font-body);
  font-size: var(--text-sm);
  color: var(--text-primary);
  outline: none;
}

.header__search input::placeholder {
  color: var(--text-muted);
}

.header__search-kbd {
  padding: 2px 8px;
  background: var(--bg-surface);
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  font-family: var(--font-body);
  font-size: var(--text-xs);
  color: var(--text-muted);
}

/* Actions */
.header__actions {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.header__action {
  position: relative;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: 1px solid transparent;
  border-radius: var(--radius-lg);
  color: var(--text-secondary);
  cursor: pointer;
  transition: all var(--duration-fast);
}

.header__action:hover {
  background: var(--glass-bg);
  border-color: var(--border);
  color: var(--text-primary);
}

.header__badge {
  position: absolute;
  top: 4px;
  right: 4px;
  min-width: 18px;
  height: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 5px;
  background: var(--error);
  border-radius: var(--radius-full);
  font-size: 10px;
  font-weight: var(--font-bold);
  color: white;
}
</style>
