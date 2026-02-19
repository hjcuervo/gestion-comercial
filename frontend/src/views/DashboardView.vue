<template>
  <AuroraLayout>
    <div class="dashboard">
      <!-- Welcome Section -->
      <section class="dashboard__welcome animate-slideUp">
        <div class="dashboard__welcome-content">
          <h2 class="dashboard__welcome-title">
            Bienvenido de nuevo, <span>{{ firstName }}</span>
          </h2>
          <p class="dashboard__welcome-text">
            Aquí tienes un resumen de tu actividad comercial de hoy
          </p>
        </div>
        <div class="dashboard__welcome-actions">
          <AuroraButton variant="primary" icon="add">
            Nueva Oportunidad
          </AuroraButton>
        </div>
      </section>

      <!-- Stats Grid -->
      <section class="dashboard__stats">
        <AuroraStatCard
          v-for="(stat, index) in stats"
          :key="stat.label"
          :icon="stat.icon"
          :value="stat.value"
          :label="stat.label"
          :trend="stat.trend"
          :trend-up="stat.trendUp"
          :trend-down="stat.trendDown"
          :format="stat.format"
          :color="stat.color"
          class="animate-slideUp"
          :style="{ animationDelay: `${index * 100}ms` }"
        />
      </section>

      <!-- Main Content -->
      <section class="dashboard__content">
        <!-- Recent Opportunities -->
        <AuroraCard 
          title="Oportunidades Recientes" 
          subtitle="Últimas actualizaciones"
          icon="trending_up"
          class="dashboard__opportunities animate-slideUp"
          style="animation-delay: 400ms"
        >
          <template #actions>
            <AuroraButton variant="ghost" size="sm" trailing-icon="arrow_forward">
              Ver todas
            </AuroraButton>
          </template>
          
          <div class="opportunity-list">
            <div 
              v-for="opp in recentOpportunities" 
              :key="opp.id" 
              class="opportunity-item"
            >
              <div class="opportunity-item__info">
                <span class="opportunity-item__name">{{ opp.nombre }}</span>
                <span class="opportunity-item__company">{{ opp.empresa }}</span>
              </div>
              <div class="opportunity-item__meta">
                <span class="opportunity-item__value">{{ formatCurrency(opp.valor) }}</span>
                <span :class="['opportunity-item__stage', `opportunity-item__stage--${opp.etapa.toLowerCase()}`]">
                  {{ opp.etapa }}
                </span>
              </div>
            </div>
          </div>
        </AuroraCard>

        <!-- Upcoming Commitments -->
        <AuroraCard 
          title="Próximos Compromisos" 
          subtitle="Tareas pendientes"
          icon="event"
          class="dashboard__commitments animate-slideUp"
          style="animation-delay: 500ms"
        >
          <div class="commitment-list">
            <div 
              v-for="commitment in upcomingCommitments" 
              :key="commitment.id" 
              class="commitment-item"
            >
              <div :class="['commitment-item__priority', `commitment-item__priority--${commitment.prioridad.toLowerCase()}`]"></div>
              <div class="commitment-item__content">
                <span class="commitment-item__title">{{ commitment.descripcion }}</span>
                <span class="commitment-item__date">
                  <span class="material-icons-round">schedule</span>
                  {{ formatDate(commitment.fecha) }}
                </span>
              </div>
              <AuroraButton variant="ghost" size="sm" icon="check_circle" icon-only />
            </div>
          </div>
        </AuroraCard>
      </section>

      <!-- Quick Actions -->
      <section class="dashboard__quick-actions animate-slideUp" style="animation-delay: 600ms">
        <h3 class="dashboard__section-title">Acciones Rápidas</h3>
        <div class="quick-actions-grid">
          <button 
            v-for="action in quickActions" 
            :key="action.label"
            class="quick-action"
            @click="handleQuickAction(action)"
          >
            <span class="material-icons-round quick-action__icon">{{ action.icon }}</span>
            <span class="quick-action__label">{{ action.label }}</span>
          </button>
        </div>
      </section>
    </div>
  </AuroraLayout>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth.store';
import AuroraLayout from '@/components/layout/AuroraLayout.vue';
import AuroraCard from '@/components/ui/AuroraCard.vue';
import AuroraStatCard from '@/components/ui/AuroraStatCard.vue';
import AuroraButton from '@/components/ui/AuroraButton.vue';

const router = useRouter();
const authStore = useAuthStore();

const firstName = computed(() => {
  const name = authStore.userName || 'Usuario';
  return name.split(' ')[0];
});

const stats = ref([
  { 
    icon: 'account_balance_wallet', 
    value: 450000000, 
    label: 'Valor en Pipeline', 
    trend: '+12.5%',
    trendUp: true,
    format: 'currency',
    color: 'primary'
  },
  { 
    icon: 'handshake', 
    value: 24, 
    label: 'Oportunidades Activas', 
    trend: '+3',
    trendUp: true,
    format: 'number',
    color: 'secondary'
  },
  { 
    icon: 'business', 
    value: 128, 
    label: 'Empresas Registradas', 
    format: 'number',
    color: 'success'
  },
  { 
    icon: 'emoji_events', 
    value: 87, 
    label: 'Tasa de Conversión', 
    trend: '+2.3%',
    trendUp: true,
    format: 'percent',
    color: 'warning'
  },
]);

const recentOpportunities = ref([
  { id: 1, nombre: 'Implementación ERP', empresa: 'Acme Corporation', valor: 150000000, etapa: 'Negociación' },
  { id: 2, nombre: 'Migración Cloud AWS', empresa: 'Tech Solutions', valor: 85000000, etapa: 'Propuesta' },
  { id: 3, nombre: 'App Móvil E-commerce', empresa: 'Retail Plus', valor: 45000000, etapa: 'Calificación' },
  { id: 4, nombre: 'Consultoría Digital', empresa: 'Financial Group', valor: 32000000, etapa: 'Descubrimiento' },
]);

const upcomingCommitments = ref([
  { id: 1, descripcion: 'Enviar propuesta técnica a Acme Corp', fecha: '2026-02-19', prioridad: 'alta' },
  { id: 2, descripcion: 'Reunión de seguimiento con Tech Solutions', fecha: '2026-02-20', prioridad: 'media' },
  { id: 3, descripcion: 'Demo de producto para Retail Plus', fecha: '2026-02-21', prioridad: 'alta' },
  { id: 4, descripcion: 'Llamada de cierre con Financial Group', fecha: '2026-02-22', prioridad: 'baja' },
]);

const quickActions = [
  { icon: 'add_business', label: 'Nueva Empresa', route: '/empresas/nueva' },
  { icon: 'person_add', label: 'Nuevo Contacto', route: '/personas/nuevo' },
  { icon: 'note_add', label: 'Nueva Actividad', route: '/actividades/nueva' },
  { icon: 'assessment', label: 'Ver Reportes', route: '/reportes' },
];

const formatCurrency = (value) => {
  return new Intl.NumberFormat('es-CO', { 
    style: 'currency', 
    currency: 'COP',
    notation: 'compact',
    maximumFractionDigits: 0
  }).format(value);
};

const formatDate = (date) => {
  return new Date(date).toLocaleDateString('es-CO', { 
    weekday: 'short',
    day: 'numeric', 
    month: 'short' 
  });
};

const handleQuickAction = (action) => {
  router.push(action.route);
};
</script>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: var(--aurora-space-6);
}

/* Welcome */
.dashboard__welcome {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--aurora-space-6);
  background: var(--aurora-gradient-card);
  border: 1px solid var(--aurora-border);
  border-radius: var(--aurora-radius-xl);
}

.dashboard__welcome-title {
  font-size: var(--aurora-text-2xl);
  font-weight: var(--aurora-font-semibold);
  color: var(--aurora-text-primary);
  margin-bottom: var(--aurora-space-2);
}

.dashboard__welcome-title span {
  background: var(--aurora-gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.dashboard__welcome-text {
  font-size: var(--aurora-text-sm);
  color: var(--aurora-text-tertiary);
}

/* Stats */
.dashboard__stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--aurora-space-4);
}

@media (max-width: 1200px) {
  .dashboard__stats {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .dashboard__stats {
    grid-template-columns: 1fr;
  }
}

/* Content */
.dashboard__content {
  display: grid;
  grid-template-columns: 1.5fr 1fr;
  gap: var(--aurora-space-4);
}

@media (max-width: 1024px) {
  .dashboard__content {
    grid-template-columns: 1fr;
  }
}

/* Opportunity List */
.opportunity-list {
  display: flex;
  flex-direction: column;
}

.opportunity-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--aurora-space-4) 0;
  border-bottom: 1px solid var(--aurora-border);
}

.opportunity-item:last-child {
  border-bottom: none;
}

.opportunity-item__info {
  display: flex;
  flex-direction: column;
  gap: var(--aurora-space-1);
}

.opportunity-item__name {
  font-size: var(--aurora-text-sm);
  font-weight: var(--aurora-font-medium);
  color: var(--aurora-text-primary);
}

.opportunity-item__company {
  font-size: var(--aurora-text-xs);
  color: var(--aurora-text-tertiary);
}

.opportunity-item__meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: var(--aurora-space-1);
}

.opportunity-item__value {
  font-size: var(--aurora-text-sm);
  font-weight: var(--aurora-font-semibold);
  color: var(--aurora-secondary);
}

.opportunity-item__stage {
  font-size: var(--aurora-text-xs);
  padding: 2px 8px;
  border-radius: var(--aurora-radius-full);
  background: var(--aurora-bg-overlay);
  color: var(--aurora-text-secondary);
}

.opportunity-item__stage--negociación {
  background: var(--aurora-success-bg);
  color: var(--aurora-success);
}

.opportunity-item__stage--propuesta {
  background: var(--aurora-info-bg);
  color: var(--aurora-info);
}

/* Commitment List */
.commitment-list {
  display: flex;
  flex-direction: column;
  gap: var(--aurora-space-3);
}

.commitment-item {
  display: flex;
  align-items: center;
  gap: var(--aurora-space-3);
  padding: var(--aurora-space-3);
  background: var(--aurora-bg-overlay);
  border-radius: var(--aurora-radius-lg);
}

.commitment-item__priority {
  width: 4px;
  height: 36px;
  border-radius: 2px;
  flex-shrink: 0;
}

.commitment-item__priority--alta { background: var(--aurora-error); }
.commitment-item__priority--media { background: var(--aurora-warning); }
.commitment-item__priority--baja { background: var(--aurora-text-muted); }

.commitment-item__content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--aurora-space-1);
  min-width: 0;
}

.commitment-item__title {
  font-size: var(--aurora-text-sm);
  color: var(--aurora-text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.commitment-item__date {
  display: flex;
  align-items: center;
  gap: var(--aurora-space-1);
  font-size: var(--aurora-text-xs);
  color: var(--aurora-text-tertiary);
}

.commitment-item__date .material-icons-round {
  font-size: 14px;
}

/* Quick Actions */
.dashboard__section-title {
  font-size: var(--aurora-text-sm);
  font-weight: var(--aurora-font-semibold);
  color: var(--aurora-text-secondary);
  margin-bottom: var(--aurora-space-4);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.quick-actions-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--aurora-space-3);
}

@media (max-width: 768px) {
  .quick-actions-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

.quick-action {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--aurora-space-2);
  padding: var(--aurora-space-5);
  background: var(--aurora-bg-overlay);
  border: 1px solid var(--aurora-border);
  border-radius: var(--aurora-radius-xl);
  cursor: pointer;
  transition: all var(--aurora-transition-fast);
}

.quick-action:hover {
  background: var(--aurora-gradient-glow);
  border-color: var(--aurora-primary);
  transform: translateY(-2px);
}

.quick-action__icon {
  font-size: 28px;
  color: var(--aurora-primary-light);
}

.quick-action__label {
  font-size: var(--aurora-text-sm);
  font-weight: var(--aurora-font-medium);
  color: var(--aurora-text-secondary);
}

.quick-action:hover .quick-action__label {
  color: var(--aurora-text-primary);
}
</style>
