<template>
  <Layout>
    <div class="dashboard">
      <!-- Welcome -->
      <section class="dashboard__welcome animate-slideUp">
        <div class="dashboard__welcome-content">
          <h2 class="dashboard__welcome-title">
            Bienvenido, <span class="gradient-text">{{ firstName }}</span>
          </h2>
          <p class="dashboard__welcome-text">
            Aquí tienes un resumen de tu actividad comercial
          </p>
        </div>
        <Button variant="primary" icon="plus">
          Nueva Oportunidad
        </Button>
      </section>

      <!-- Stats -->
      <section class="dashboard__stats">
        <StatCard
          v-for="(stat, i) in stats"
          :key="stat.label"
          v-bind="stat"
          class="animate-slideUp"
          :style="{ animationDelay: `${(i + 1) * 80}ms` }"
        />
      </section>

      <!-- Content Grid -->
      <section class="dashboard__content">
        <!-- Opportunities -->
        <Card 
          title="Oportunidades Recientes" 
          subtitle="Últimas actualizaciones"
          icon="trending-up"
          class="dashboard__card animate-slideUp"
          style="animation-delay: 400ms"
        >
          <template #actions>
            <Button variant="ghost" size="sm" trailing-icon="arrow-right">
              Ver todas
            </Button>
          </template>
          
          <div class="opp-list">
            <div v-for="opp in opportunities" :key="opp.id" class="opp-item">
              <div class="opp-item__info">
                <span class="opp-item__name">{{ opp.nombre }}</span>
                <span class="opp-item__company">{{ opp.empresa }}</span>
              </div>
              <div class="opp-item__meta">
                <span class="opp-item__value">{{ formatCurrency(opp.valor) }}</span>
                <span :class="['opp-item__stage', `opp-item__stage--${opp.etapaClass}`]">
                  {{ opp.etapa }}
                </span>
              </div>
            </div>
          </div>
        </Card>

        <!-- Commitments -->
        <Card 
          title="Próximos Compromisos" 
          subtitle="Tareas pendientes"
          icon="calendar"
          class="dashboard__card animate-slideUp"
          style="animation-delay: 500ms"
        >
          <div class="commit-list">
            <div v-for="item in commitments" :key="item.id" class="commit-item">
              <div :class="['commit-item__bar', `commit-item__bar--${item.prioridad}`]"></div>
              <div class="commit-item__content">
                <span class="commit-item__title">{{ item.descripcion }}</span>
                <span class="commit-item__date">
                  <Icon name="clock" :size="14" />
                  {{ formatDate(item.fecha) }}
                </span>
              </div>
              <Button variant="ghost" size="sm" icon="check" icon-only />
            </div>
          </div>
        </Card>
      </section>

      <!-- Quick Actions -->
      <section class="dashboard__actions animate-slideUp" style="animation-delay: 600ms">
        <h3 class="dashboard__section-title">Acciones Rápidas</h3>
        <div class="actions-grid">
          <button 
            v-for="action in quickActions" 
            :key="action.label"
            class="action-card"
            @click="$router.push(action.route)"
          >
            <div class="action-card__icon">
              <Icon :name="action.icon" :size="28" />
            </div>
            <span class="action-card__label">{{ action.label }}</span>
          </button>
        </div>
      </section>
    </div>
  </Layout>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useAuthStore } from '@/stores/auth.store';
import Layout from '@/components/layout/Layout.vue';
import Card from '@/components/ui/Card.vue';
import StatCard from '@/components/ui/StatCard.vue';
import Button from '@/components/ui/Button.vue';
import Icon from '@/components/ui/Icon.vue';

const authStore = useAuthStore();

const firstName = computed(() => (authStore.userName || 'Usuario').split(' ')[0]);

const stats = ref([
  { icon: 'wallet', value: 450000000, label: 'Valor en Pipeline', trend: '+12.5%', trendUp: true, format: 'currency', color: 'primary' },
  { icon: 'handshake', value: 24, label: 'Oportunidades Activas', trend: '+3', trendUp: true, format: 'number', color: 'secondary' },
  { icon: 'business', value: 128, label: 'Empresas Registradas', format: 'number', color: 'success' },
  { icon: 'trophy', value: 87, label: 'Tasa de Conversión', trend: '+2.3%', trendUp: true, format: 'percent', color: 'warning' },
]);

const opportunities = ref([
  { id: 1, nombre: 'Implementación ERP', empresa: 'Acme Corporation', valor: 150000000, etapa: 'Negociación', etapaClass: 'negotiation' },
  { id: 2, nombre: 'Migración Cloud AWS', empresa: 'Tech Solutions', valor: 85000000, etapa: 'Propuesta', etapaClass: 'proposal' },
  { id: 3, nombre: 'App Móvil E-commerce', empresa: 'Retail Plus', valor: 45000000, etapa: 'Calificación', etapaClass: 'qualification' },
  { id: 4, nombre: 'Consultoría Digital', empresa: 'Financial Group', valor: 32000000, etapa: 'Descubrimiento', etapaClass: 'discovery' },
]);

const commitments = ref([
  { id: 1, descripcion: 'Enviar propuesta técnica a Acme Corp', fecha: '2026-02-25', prioridad: 'alta' },
  { id: 2, descripcion: 'Reunión de seguimiento con Tech Solutions', fecha: '2026-02-26', prioridad: 'media' },
  { id: 3, descripcion: 'Demo de producto para Retail Plus', fecha: '2026-02-27', prioridad: 'alta' },
  { id: 4, descripcion: 'Llamada de cierre con Financial Group', fecha: '2026-02-28', prioridad: 'baja' },
]);

const quickActions = [
  { icon: 'building-add', label: 'Nueva Empresa', route: '/empresas/nueva' },
  { icon: 'person-add', label: 'Nuevo Contacto', route: '/personas/nuevo' },
  { icon: 'note-add', label: 'Nueva Actividad', route: '/actividades/nueva' },
  { icon: 'bar-chart', label: 'Ver Reportes', route: '/reportes' },
];

const formatCurrency = (val) => new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', notation: 'compact', maximumFractionDigits: 0 }).format(val);
const formatDate = (date) => new Date(date).toLocaleDateString('es-CO', { weekday: 'short', day: 'numeric', month: 'short' });
</script>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: var(--space-6);
}

/* Welcome */
.dashboard__welcome {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--space-6);
  background: var(--gradient-card);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-xl);
  backdrop-filter: blur(20px);
}

.dashboard__welcome-title {
  font-size: var(--text-2xl);
  font-weight: var(--font-semibold);
  margin-bottom: var(--space-1);
}

.dashboard__welcome-text {
  font-size: var(--text-sm);
  color: var(--text-tertiary);
}

/* Stats */
.dashboard__stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--space-4);
}

@media (max-width: 1200px) {
  .dashboard__stats { grid-template-columns: repeat(2, 1fr); }
}

@media (max-width: 768px) {
  .dashboard__stats { grid-template-columns: 1fr; }
}

/* Content */
.dashboard__content {
  display: grid;
  grid-template-columns: 1.5fr 1fr;
  gap: var(--space-4);
}

@media (max-width: 1024px) {
  .dashboard__content { grid-template-columns: 1fr; }
}

/* Opportunity List */
.opp-list {
  display: flex;
  flex-direction: column;
}

.opp-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--space-4) 0;
  border-bottom: 1px solid var(--border);
  transition: background var(--duration-fast);
}

.opp-item:last-child { border-bottom: none; }

.opp-item:hover {
  background: var(--glass-bg);
  margin: 0 calc(var(--space-4) * -1);
  padding-left: var(--space-4);
  padding-right: var(--space-4);
  border-radius: var(--radius-md);
}

.opp-item__info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.opp-item__name {
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--text-primary);
}

.opp-item__company {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
}

.opp-item__meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: var(--space-1);
}

.opp-item__value {
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--primary);
}

.opp-item__stage {
  font-size: var(--text-xs);
  padding: 3px 10px;
  border-radius: var(--radius-full);
  font-weight: var(--font-medium);
}

.opp-item__stage--negotiation { background: var(--success-soft); color: var(--success); }
.opp-item__stage--proposal { background: var(--primary-soft); color: var(--primary); }
.opp-item__stage--qualification { background: var(--warning-soft); color: var(--warning); }
.opp-item__stage--discovery { background: var(--secondary-soft); color: var(--secondary); }

/* Commitment List */
.commit-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.commit-item {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-3);
  background: var(--glass-bg);
  border-radius: var(--radius-lg);
  transition: all var(--duration-fast);
}

.commit-item:hover {
  background: var(--glass-hover);
}

.commit-item__bar {
  width: 4px;
  height: 40px;
  border-radius: 2px;
  flex-shrink: 0;
}

.commit-item__bar--alta { background: var(--error); }
.commit-item__bar--media { background: var(--warning); }
.commit-item__bar--baja { background: var(--text-muted); }

.commit-item__content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.commit-item__title {
  font-size: var(--text-sm);
  color: var(--text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.commit-item__date {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  font-size: var(--text-xs);
  color: var(--text-tertiary);
}

/* Quick Actions */
.dashboard__section-title {
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--text-secondary);
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: var(--space-4);
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--space-3);
}

@media (max-width: 768px) {
  .actions-grid { grid-template-columns: repeat(2, 1fr); }
}

.action-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-6);
  background: var(--glass-bg);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-xl);
  cursor: pointer;
  transition: all var(--duration-fast) var(--ease-out);
}

.action-card:hover {
  background: var(--primary-soft);
  border-color: var(--primary);
  transform: translateY(-4px);
  box-shadow: var(--shadow-glow-sm);
}

.action-card__icon {
  color: var(--primary);
}

.action-card:hover .action-card__icon {
  color: var(--text-primary);
}

.action-card__label {
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--text-secondary);
}

.action-card:hover .action-card__label {
  color: var(--text-primary);
}
</style>
