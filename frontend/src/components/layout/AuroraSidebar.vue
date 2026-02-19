<template>
  <aside :class="['aurora-sidebar', { 'aurora-sidebar--expanded': expanded }]">
    <!-- Logo -->
    <div class="aurora-sidebar__logo">
      <div class="aurora-sidebar__logo-icon">
        <span class="material-icons-round">auto_graph</span>
      </div>
      <transition name="fade">
        <span v-if="expanded" class="aurora-sidebar__logo-text">GestCom</span>
      </transition>
    </div>
    
    <!-- Toggle Button -->
    <button class="aurora-sidebar__toggle" @click="$emit('toggle')">
      <span class="material-icons-round">{{ expanded ? 'chevron_left' : 'chevron_right' }}</span>
    </button>
    
    <!-- Navigation -->
    <nav class="aurora-sidebar__nav">
      <div class="aurora-sidebar__nav-section">
        <router-link
          v-for="item in mainItems"
          :key="item.path"
          :to="item.path"
          :class="['aurora-sidebar__item', { 'aurora-sidebar__item--active': isActive(item.path) }]"
        >
          <span class="material-icons-round aurora-sidebar__item-icon">{{ item.icon }}</span>
          <transition name="fade">
            <span v-if="expanded" class="aurora-sidebar__item-label">{{ item.label }}</span>
          </transition>
          <span v-if="item.badge && expanded" class="aurora-sidebar__item-badge">{{ item.badge }}</span>
        </router-link>
      </div>
      
      <div class="aurora-sidebar__divider"></div>
      
      <div class="aurora-sidebar__nav-section">
        <router-link
          v-for="item in secondaryItems"
          :key="item.path"
          :to="item.path"
          :class="['aurora-sidebar__item', { 'aurora-sidebar__item--active': isActive(item.path) }]"
        >
          <span class="material-icons-round aurora-sidebar__item-icon">{{ item.icon }}</span>
          <transition name="fade">
            <span v-if="expanded" class="aurora-sidebar__item-label">{{ item.label }}</span>
          </transition>
        </router-link>
      </div>
    </nav>
    
    <!-- User Section -->
    <div class="aurora-sidebar__footer">
      <div class="aurora-sidebar__user" @click="$emit('user-click')">
        <div class="aurora-sidebar__avatar">
          <span v-if="!userAvatar">{{ userInitials }}</span>
          <img v-else :src="userAvatar" alt="Avatar" />
        </div>
        <transition name="fade">
          <div v-if="expanded" class="aurora-sidebar__user-info">
            <span class="aurora-sidebar__user-name">{{ userName }}</span>
            <span class="aurora-sidebar__user-role">{{ userRole }}</span>
          </div>
        </transition>
      </div>
      
      <button class="aurora-sidebar__logout" @click="$emit('logout')">
        <span class="material-icons-round">logout</span>
        <transition name="fade">
          <span v-if="expanded">Salir</span>
        </transition>
      </button>
    </div>
  </aside>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';

const props = defineProps({
  expanded: Boolean,
  userName: {
    type: String,
    default: 'Usuario'
  },
  userRole: {
    type: String,
    default: 'Admin'
  },
  userAvatar: String,
});

defineEmits(['toggle', 'logout', 'user-click']);

const route = useRoute();

const mainItems = [
  { path: '/', label: 'Dashboard', icon: 'dashboard' },
  { path: '/pipeline', label: 'Pipeline', icon: 'view_kanban', badge: '12' },
  { path: '/empresas', label: 'Empresas', icon: 'business' },
  { path: '/personas', label: 'Contactos', icon: 'people' },
];

const secondaryItems = [
  { path: '/actividades', label: 'Actividades', icon: 'event_note' },
  { path: '/reportes', label: 'Reportes', icon: 'assessment' },
];

const isActive = (path) => {
  if (path === '/') return route.path === '/';
  return route.path.startsWith(path);
};

const userInitials = computed(() => {
  return props.userName
    .split(' ')
    .map(n => n[0])
    .join('')
    .substring(0, 2)
    .toUpperCase();
});
</script>

<style scoped>
.aurora-sidebar {
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  width: var(--aurora-nav-width);
  background: var(--aurora-bg-elevated);
  border-right: 1px solid var(--aurora-border);
  display: flex;
  flex-direction: column;
  z-index: var(--aurora-z-fixed);
  transition: width var(--aurora-transition-base);
  overflow: hidden;
}

.aurora-sidebar--expanded {
  width: var(--aurora-nav-expanded);
}

/* Logo */
.aurora-sidebar__logo {
  display: flex;
  align-items: center;
  gap: var(--aurora-space-3);
  padding: var(--aurora-space-5);
  min-height: 72px;
}

.aurora-sidebar__logo-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--aurora-gradient-primary);
  border-radius: var(--aurora-radius-md);
  flex-shrink: 0;
}

.aurora-sidebar__logo-icon .material-icons-round {
  font-size: 22px;
  color: white;
}

.aurora-sidebar__logo-text {
  font-size: var(--aurora-text-xl);
  font-weight: var(--aurora-font-bold);
  background: var(--aurora-gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  white-space: nowrap;
}

/* Toggle */
.aurora-sidebar__toggle {
  position: absolute;
  top: 24px;
  right: -12px;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--aurora-bg-surface);
  border: 1px solid var(--aurora-border);
  border-radius: var(--aurora-radius-full);
  color: var(--aurora-text-secondary);
  cursor: pointer;
  transition: all var(--aurora-transition-fast);
  z-index: 10;
}

.aurora-sidebar__toggle:hover {
  background: var(--aurora-primary);
  border-color: var(--aurora-primary);
  color: white;
}

.aurora-sidebar__toggle .material-icons-round {
  font-size: 16px;
}

/* Navigation */
.aurora-sidebar__nav {
  flex: 1;
  padding: var(--aurora-space-3);
  display: flex;
  flex-direction: column;
  gap: var(--aurora-space-2);
  overflow-y: auto;
}

.aurora-sidebar__nav-section {
  display: flex;
  flex-direction: column;
  gap: var(--aurora-space-1);
}

.aurora-sidebar__divider {
  height: 1px;
  background: var(--aurora-border);
  margin: var(--aurora-space-3) 0;
}

.aurora-sidebar__item {
  display: flex;
  align-items: center;
  gap: var(--aurora-space-3);
  padding: var(--aurora-space-3);
  border-radius: var(--aurora-radius-lg);
  color: var(--aurora-text-secondary);
  text-decoration: none;
  transition: all var(--aurora-transition-fast);
  position: relative;
}

.aurora-sidebar__item:hover {
  background: var(--aurora-bg-overlay);
  color: var(--aurora-text-primary);
}

.aurora-sidebar__item--active {
  background: var(--aurora-gradient-glow);
  color: var(--aurora-primary-light);
}

.aurora-sidebar__item--active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 24px;
  background: var(--aurora-primary);
  border-radius: 0 2px 2px 0;
}

.aurora-sidebar__item-icon {
  font-size: 22px;
  flex-shrink: 0;
}

.aurora-sidebar__item-label {
  font-size: var(--aurora-text-sm);
  font-weight: var(--aurora-font-medium);
  white-space: nowrap;
}

.aurora-sidebar__item-badge {
  margin-left: auto;
  padding: 2px 8px;
  background: var(--aurora-primary);
  color: white;
  font-size: var(--aurora-text-xs);
  font-weight: var(--aurora-font-semibold);
  border-radius: var(--aurora-radius-full);
}

/* Footer */
.aurora-sidebar__footer {
  padding: var(--aurora-space-3);
  border-top: 1px solid var(--aurora-border);
  display: flex;
  flex-direction: column;
  gap: var(--aurora-space-2);
}

.aurora-sidebar__user {
  display: flex;
  align-items: center;
  gap: var(--aurora-space-3);
  padding: var(--aurora-space-3);
  border-radius: var(--aurora-radius-lg);
  cursor: pointer;
  transition: background var(--aurora-transition-fast);
}

.aurora-sidebar__user:hover {
  background: var(--aurora-bg-overlay);
}

.aurora-sidebar__avatar {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--aurora-gradient-primary);
  border-radius: var(--aurora-radius-md);
  font-size: var(--aurora-text-sm);
  font-weight: var(--aurora-font-semibold);
  color: white;
  flex-shrink: 0;
  overflow: hidden;
}

.aurora-sidebar__avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.aurora-sidebar__user-info {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.aurora-sidebar__user-name {
  font-size: var(--aurora-text-sm);
  font-weight: var(--aurora-font-medium);
  color: var(--aurora-text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.aurora-sidebar__user-role {
  font-size: var(--aurora-text-xs);
  color: var(--aurora-text-tertiary);
}

.aurora-sidebar__logout {
  display: flex;
  align-items: center;
  gap: var(--aurora-space-3);
  padding: var(--aurora-space-3);
  border: none;
  background: transparent;
  border-radius: var(--aurora-radius-lg);
  color: var(--aurora-text-tertiary);
  font-family: var(--aurora-font-family);
  font-size: var(--aurora-text-sm);
  cursor: pointer;
  transition: all var(--aurora-transition-fast);
}

.aurora-sidebar__logout:hover {
  background: var(--aurora-error-bg);
  color: var(--aurora-error);
}

.aurora-sidebar__logout .material-icons-round {
  font-size: 22px;
}

/* Transitions */
.fade-enter-active,
.fade-leave-active {
  transition: opacity var(--aurora-transition-fast);
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
