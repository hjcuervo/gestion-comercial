<template>
  <aside :class="['sidebar', { 'sidebar--expanded': expanded }]">
    <!-- Logo -->
    <div class="sidebar__logo">
      <div class="sidebar__logo-mark">
        <svg width="28" height="28" viewBox="0 0 28 28" fill="none">
          <rect x="2" y="2" width="10" height="10" rx="2" fill="url(#logo-gradient)"/>
          <rect x="16" y="2" width="10" height="10" rx="2" fill="url(#logo-gradient)" opacity="0.6"/>
          <rect x="2" y="16" width="10" height="10" rx="2" fill="url(#logo-gradient)" opacity="0.6"/>
          <rect x="16" y="16" width="10" height="10" rx="2" fill="url(#logo-gradient)" opacity="0.3"/>
          <defs>
            <linearGradient id="logo-gradient" x1="0" y1="0" x2="28" y2="28">
              <stop stop-color="var(--primary)"/>
              <stop offset="1" stop-color="var(--secondary)"/>
            </linearGradient>
          </defs>
        </svg>
      </div>
      <transition name="fade">
        <span v-if="expanded" class="sidebar__logo-text">GestCom</span>
      </transition>
    </div>
    
    <!-- Toggle -->
    <button class="sidebar__toggle" @click="$emit('toggle')">
      <Icon :name="expanded ? 'chevron-left' : 'chevron-right'" :size="16" />
    </button>
    
    <!-- Navigation -->
    <nav class="sidebar__nav">
      <div class="sidebar__nav-group">
        <router-link
          v-for="item in mainNav"
          :key="item.path"
          :to="item.path"
          :class="['sidebar__item', { 'sidebar__item--active': isActive(item.path) }]"
        >
          <div class="sidebar__item-icon">
            <Icon :name="item.icon" :size="22" />
          </div>
          <transition name="fade">
            <span v-if="expanded" class="sidebar__item-label">{{ item.label }}</span>
          </transition>
          <transition name="fade">
            <span v-if="item.badge && expanded" class="sidebar__item-badge">{{ item.badge }}</span>
          </transition>
          <div v-if="isActive(item.path)" class="sidebar__item-indicator"></div>
        </router-link>
      </div>
      
      <div class="sidebar__divider"></div>
      
      <div class="sidebar__nav-group">
        <router-link
          v-for="item in secondaryNav"
          :key="item.path"
          :to="item.path"
          :class="['sidebar__item', { 'sidebar__item--active': isActive(item.path) }]"
        >
          <div class="sidebar__item-icon">
            <Icon :name="item.icon" :size="22" />
          </div>
          <transition name="fade">
            <span v-if="expanded" class="sidebar__item-label">{{ item.label }}</span>
          </transition>
        </router-link>
      </div>
    </nav>
    
    <!-- Footer -->
    <div class="sidebar__footer">
      <div class="sidebar__user" @click="$emit('user-click')">
        <div class="sidebar__avatar">
          {{ userInitials }}
        </div>
        <transition name="fade">
          <div v-if="expanded" class="sidebar__user-info">
            <span class="sidebar__user-name">{{ userName }}</span>
            <span class="sidebar__user-role">{{ userRole }}</span>
          </div>
        </transition>
      </div>
      
      <button class="sidebar__logout" @click="$emit('logout')">
        <Icon name="logout" :size="20" />
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
import Icon from '../ui/Icon.vue';

const props = defineProps({
  expanded: Boolean,
  userName: { type: String, default: 'Usuario' },
  userRole: { type: String, default: 'Administrador' }
});

defineEmits(['toggle', 'logout', 'user-click']);

const route = useRoute();

const mainNav = [
  { path: '/', label: 'Dashboard', icon: 'dashboard' },
  { path: '/pipeline', label: 'Pipeline', icon: 'pipeline', badge: '12' },
  { path: '/empresas', label: 'Empresas', icon: 'business' },
  { path: '/personas', label: 'Contactos', icon: 'people' },
];

const secondaryNav = [
  { path: '/actividades', label: 'Actividades', icon: 'calendar' },
  { path: '/reportes', label: 'Reportes', icon: 'chart' },
];

const isActive = (path) => {
  if (path === '/') return route.path === '/';
  return route.path.startsWith(path);
};

const userInitials = computed(() => {
  return props.userName.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase();
});
</script>

<style scoped>
.sidebar {
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  width: var(--sidebar-width);
  background: var(--bg-elevated);
  border-right: 1px solid var(--border);
  display: flex;
  flex-direction: column;
  z-index: var(--z-fixed);
  transition: width var(--duration-base) var(--ease-out);
  overflow: hidden;
}

.sidebar--expanded {
  width: var(--sidebar-expanded);
}

/* Logo */
.sidebar__logo {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-5);
  min-height: 72px;
}

.sidebar__logo-mark {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.sidebar__logo-text {
  font-family: var(--font-display);
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  white-space: nowrap;
}

/* Toggle */
.sidebar__toggle {
  position: absolute;
  top: 24px;
  right: -12px;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-surface);
  border: 1px solid var(--border);
  border-radius: var(--radius-full);
  color: var(--text-tertiary);
  cursor: pointer;
  transition: all var(--duration-fast);
  z-index: 10;
}

.sidebar__toggle:hover {
  background: var(--primary);
  border-color: var(--primary);
  color: var(--bg-deep);
}

/* Navigation */
.sidebar__nav {
  flex: 1;
  padding: var(--space-3);
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
  overflow-y: auto;
}

.sidebar__nav-group {
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
}

.sidebar__divider {
  height: 1px;
  background: var(--border);
  margin: var(--space-3) var(--space-2);
}

.sidebar__item {
  position: relative;
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-3);
  border-radius: var(--radius-lg);
  color: var(--text-secondary);
  text-decoration: none;
  transition: all var(--duration-fast);
}

.sidebar__item:hover {
  background: var(--glass-bg);
  color: var(--text-primary);
}

.sidebar__item--active {
  background: var(--primary-soft);
  color: var(--primary);
}

.sidebar__item-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  flex-shrink: 0;
}

.sidebar__item-label {
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  white-space: nowrap;
}

.sidebar__item-badge {
  margin-left: auto;
  padding: 2px 8px;
  background: var(--gradient-primary);
  color: var(--bg-deep);
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  border-radius: var(--radius-full);
}

.sidebar__item-indicator {
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 20px;
  background: var(--primary);
  border-radius: 0 2px 2px 0;
}

/* Footer */
.sidebar__footer {
  padding: var(--space-3);
  border-top: 1px solid var(--border);
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.sidebar__user {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-3);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: background var(--duration-fast);
}

.sidebar__user:hover {
  background: var(--glass-bg);
}

.sidebar__avatar {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--gradient-primary);
  border-radius: var(--radius-md);
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--bg-deep);
  flex-shrink: 0;
}

.sidebar__user-info {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.sidebar__user-name {
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.sidebar__user-role {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
}

.sidebar__logout {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-3);
  border: none;
  background: transparent;
  border-radius: var(--radius-lg);
  color: var(--text-tertiary);
  font-family: var(--font-body);
  font-size: var(--text-sm);
  cursor: pointer;
  transition: all var(--duration-fast);
}

.sidebar__logout:hover {
  background: var(--error-soft);
  color: var(--error);
}

/* Transitions */
.fade-enter-active, .fade-leave-active {
  transition: opacity var(--duration-fast);
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>
