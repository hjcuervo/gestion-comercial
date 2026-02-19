<template>
  <header class="aurora-header">
    <div class="aurora-header__left">
      <div class="aurora-header__breadcrumb">
        <router-link to="/" class="aurora-header__breadcrumb-item">
          <span class="material-icons-round">home</span>
        </router-link>
        <span v-if="breadcrumbs.length" class="aurora-header__breadcrumb-separator">/</span>
        <template v-for="(crumb, index) in breadcrumbs" :key="crumb.path">
          <router-link 
            v-if="index < breadcrumbs.length - 1"
            :to="crumb.path" 
            class="aurora-header__breadcrumb-item"
          >
            {{ crumb.label }}
          </router-link>
          <span v-else class="aurora-header__breadcrumb-current">{{ crumb.label }}</span>
          <span v-if="index < breadcrumbs.length - 1" class="aurora-header__breadcrumb-separator">/</span>
        </template>
      </div>
      <h1 class="aurora-header__title">{{ title }}</h1>
    </div>
    
    <div class="aurora-header__right">
      <!-- Search -->
      <div class="aurora-header__search">
        <span class="material-icons-round">search</span>
        <input type="text" placeholder="Buscar..." v-model="searchQuery" @input="$emit('search', searchQuery)" />
        <kbd>⌘K</kbd>
      </div>
      
      <!-- Actions -->
      <div class="aurora-header__actions">
        <button class="aurora-header__action" @click="$emit('notification-click')">
          <span class="material-icons-round">notifications</span>
          <span v-if="notificationCount" class="aurora-header__badge">{{ notificationCount }}</span>
        </button>
        
        <slot name="actions" />
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref } from 'vue';

defineProps({
  title: {
    type: String,
    default: 'Dashboard'
  },
  breadcrumbs: {
    type: Array,
    default: () => []
  },
  notificationCount: {
    type: Number,
    default: 0
  }
});

defineEmits(['search', 'notification-click']);

const searchQuery = ref('');
</script>

<style scoped>
.aurora-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: var(--aurora-header-height);
  padding: 0 var(--aurora-space-6);
  background: rgba(12, 12, 30, 0.8);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid var(--aurora-border);
  position: sticky;
  top: 0;
  z-index: var(--aurora-z-sticky);
}

.aurora-header__left {
  display: flex;
  flex-direction: column;
  gap: var(--aurora-space-1);
}

.aurora-header__breadcrumb {
  display: flex;
  align-items: center;
  gap: var(--aurora-space-2);
  font-size: var(--aurora-text-xs);
}

.aurora-header__breadcrumb-item {
  color: var(--aurora-text-tertiary);
  text-decoration: none;
  display: flex;
  align-items: center;
  transition: color var(--aurora-transition-fast);
}

.aurora-header__breadcrumb-item:hover {
  color: var(--aurora-primary-light);
}

.aurora-header__breadcrumb-item .material-icons-round {
  font-size: 16px;
}

.aurora-header__breadcrumb-separator {
  color: var(--aurora-text-muted);
}

.aurora-header__breadcrumb-current {
  color: var(--aurora-text-secondary);
}

.aurora-header__title {
  font-size: var(--aurora-text-xl);
  font-weight: var(--aurora-font-semibold);
  color: var(--aurora-text-primary);
}

.aurora-header__right {
  display: flex;
  align-items: center;
  gap: var(--aurora-space-4);
}

.aurora-header__search {
  display: flex;
  align-items: center;
  gap: var(--aurora-space-2);
  padding: var(--aurora-space-2) var(--aurora-space-4);
  background: var(--aurora-bg-overlay);
  border: 1px solid var(--aurora-border);
  border-radius: var(--aurora-radius-full);
  min-width: 280px;
  transition: all var(--aurora-transition-fast);
}

.aurora-header__search:focus-within {
  border-color: var(--aurora-primary);
  box-shadow: 0 0 0 3px rgba(124, 58, 237, 0.15);
}

.aurora-header__search .material-icons-round {
  font-size: 20px;
  color: var(--aurora-text-tertiary);
}

.aurora-header__search input {
  flex: 1;
  border: none;
  background: transparent;
  font-family: var(--aurora-font-family);
  font-size: var(--aurora-text-sm);
  color: var(--aurora-text-primary);
  outline: none;
}

.aurora-header__search input::placeholder {
  color: var(--aurora-text-muted);
}

.aurora-header__search kbd {
  padding: 2px 8px;
  background: var(--aurora-bg-surface);
  border: 1px solid var(--aurora-border);
  border-radius: var(--aurora-radius-sm);
  font-family: var(--aurora-font-family);
  font-size: var(--aurora-text-xs);
  color: var(--aurora-text-muted);
}

.aurora-header__actions {
  display: flex;
  align-items: center;
  gap: var(--aurora-space-2);
}

.aurora-header__action {
  position: relative;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: 1px solid transparent;
  border-radius: var(--aurora-radius-lg);
  color: var(--aurora-text-secondary);
  cursor: pointer;
  transition: all var(--aurora-transition-fast);
}

.aurora-header__action:hover {
  background: var(--aurora-bg-overlay);
  border-color: var(--aurora-border);
  color: var(--aurora-text-primary);
}

.aurora-header__action .material-icons-round {
  font-size: 22px;
}

.aurora-header__badge {
  position: absolute;
  top: 4px;
  right: 4px;
  min-width: 18px;
  height: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 5px;
  background: var(--aurora-error);
  border-radius: var(--aurora-radius-full);
  font-size: 10px;
  font-weight: var(--aurora-font-bold);
  color: white;
}
</style>
