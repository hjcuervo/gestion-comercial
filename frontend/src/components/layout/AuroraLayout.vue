<template>
  <div class="aurora-layout">
    <AuroraSidebar 
      :expanded="sidebarExpanded" 
      :user-name="userName"
      :user-role="userRole"
      @toggle="sidebarExpanded = !sidebarExpanded"
      @logout="handleLogout"
    />
    
    <div :class="['aurora-layout__main', { 'aurora-layout__main--expanded': sidebarExpanded }]">
      <AuroraHeader 
        :title="pageTitle"
        :breadcrumbs="breadcrumbs"
        :notification-count="3"
      >
        <template #actions>
          <slot name="header-actions" />
        </template>
      </AuroraHeader>
      
      <main class="aurora-layout__content">
        <slot />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth.store';
import AuroraSidebar from './AuroraSidebar.vue';
import AuroraHeader from './AuroraHeader.vue';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const sidebarExpanded = ref(true);

const userName = computed(() => authStore.userName || 'Usuario');
const userRole = computed(() => authStore.userRole || 'Admin');

const pageTitles = {
  '/': 'Dashboard',
  '/pipeline': 'Pipeline de Oportunidades',
  '/empresas': 'Empresas',
  '/personas': 'Contactos',
  '/actividades': 'Actividades',
  '/reportes': 'Reportes',
};

const pageTitle = computed(() => {
  return pageTitles[route.path] || route.name || 'Dashboard';
});

const breadcrumbs = computed(() => {
  if (route.path === '/') return [];
  
  const crumbs = [];
  const pathSegments = route.path.split('/').filter(Boolean);
  
  let currentPath = '';
  for (const segment of pathSegments) {
    currentPath += `/${segment}`;
    crumbs.push({
      path: currentPath,
      label: pageTitles[currentPath] || segment.charAt(0).toUpperCase() + segment.slice(1)
    });
  }
  
  return crumbs;
});

const handleLogout = () => {
  authStore.logout();
  router.push('/login');
};
</script>

<style scoped>
.aurora-layout {
  display: flex;
  min-height: 100vh;
}

.aurora-layout__main {
  flex: 1;
  margin-left: var(--aurora-nav-width);
  display: flex;
  flex-direction: column;
  transition: margin-left var(--aurora-transition-base);
}

.aurora-layout__main--expanded {
  margin-left: var(--aurora-nav-expanded);
}

.aurora-layout__content {
  flex: 1;
  padding: var(--aurora-space-6);
  max-width: var(--aurora-content-max-width);
  width: 100%;
  margin: 0 auto;
}
</style>
