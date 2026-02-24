<template>
  <div class="login">
    <!-- Animated Background -->
    <div class="login__bg">
      <div class="login__orb login__orb--1"></div>
      <div class="login__orb login__orb--2"></div>
      <div class="login__orb login__orb--3"></div>
      <div class="login__grid"></div>
    </div>
    
    <!-- Login Card -->
    <div class="login__card animate-scaleIn">
      <!-- Logo -->
      <div class="login__header">
        <div class="login__logo">
          <svg width="48" height="48" viewBox="0 0 48 48" fill="none">
            <rect x="4" y="4" width="16" height="16" rx="3" fill="url(#g1)"/>
            <rect x="28" y="4" width="16" height="16" rx="3" fill="url(#g1)" opacity="0.6"/>
            <rect x="4" y="28" width="16" height="16" rx="3" fill="url(#g1)" opacity="0.6"/>
            <rect x="28" y="28" width="16" height="16" rx="3" fill="url(#g1)" opacity="0.3"/>
            <defs>
              <linearGradient id="g1" x1="0" y1="0" x2="48" y2="48">
                <stop stop-color="#00d4ff"/>
                <stop offset="1" stop-color="#a855f7"/>
              </linearGradient>
            </defs>
          </svg>
        </div>
        <h1 class="login__title">GestCom</h1>
        <p class="login__subtitle">Sistema de Gestión Comercial</p>
      </div>
      
      <!-- Form -->
      <form class="login__form" @submit.prevent="handleLogin">
        <Input
          v-model="form.username"
          label="Usuario"
          placeholder="Ingresa tu usuario"
          icon="user"
          :error="errors.username"
          :disabled="authStore.loading"
        />
        
        <Input
          v-model="form.password"
          label="Contraseña"
          placeholder="Ingresa tu contraseña"
          type="password"
          icon="lock"
          :error="errors.password"
          :disabled="authStore.loading"
        />
        
        <div v-if="authStore.error" class="login__error animate-slideUp">
          <Icon name="alert-circle" :size="18" />
          <span>{{ authStore.error }}</span>
        </div>
        
        <Button 
          type="submit" 
          variant="primary"
          size="lg"
          full-width
          :loading="authStore.loading"
          :disabled="!isFormValid"
        >
          Iniciar Sesión
        </Button>
      </form>
      
      <!-- Footer -->
      <div class="login__footer">
        <p>ArquitecSoft © 2026</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth.store';
import Input from '@/components/ui/Input.vue';
import Button from '@/components/ui/Button.vue';
import Icon from '@/components/ui/Icon.vue';

const router = useRouter();
const authStore = useAuthStore();

const form = reactive({ username: '', password: '' });
const errors = reactive({ username: '', password: '' });

const isFormValid = computed(() => form.username.length > 0 && form.password.length > 0);

const handleLogin = async () => {
  errors.username = '';
  errors.password = '';
  authStore.clearError();
  
  if (!form.username) { errors.username = 'El usuario es requerido'; return; }
  if (!form.password) { errors.password = 'La contraseña es requerida'; return; }
  
  const success = await authStore.login(form.username, form.password);
  if (success) router.push('/');
};
</script>

<style scoped>
.login {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--space-6);
  position: relative;
  overflow: hidden;
}

/* Background */
.login__bg {
  position: absolute;
  inset: 0;
  overflow: hidden;
}

.login__orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
  animation: float 25s ease-in-out infinite;
}

.login__orb--1 {
  width: 700px;
  height: 700px;
  background: var(--primary);
  opacity: 0.15;
  top: -25%;
  left: -15%;
}

.login__orb--2 {
  width: 600px;
  height: 600px;
  background: var(--secondary);
  opacity: 0.12;
  bottom: -25%;
  right: -15%;
  animation-delay: -10s;
}

.login__orb--3 {
  width: 400px;
  height: 400px;
  background: var(--accent);
  opacity: 0.08;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation-delay: -5s;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) scale(1); }
  25% { transform: translate(30px, -30px) scale(1.05); }
  50% { transform: translate(-20px, 20px) scale(0.95); }
  75% { transform: translate(-30px, -20px) scale(1.02); }
}

.login__grid {
  position: absolute;
  inset: 0;
  background-image: 
    linear-gradient(rgba(0, 212, 255, 0.02) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 212, 255, 0.02) 1px, transparent 1px);
  background-size: 80px 80px;
  mask-image: radial-gradient(ellipse at center, black 0%, transparent 70%);
}

/* Card */
.login__card {
  width: 100%;
  max-width: 420px;
  background: rgba(13, 15, 20, 0.9);
  backdrop-filter: blur(40px);
  -webkit-backdrop-filter: blur(40px);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-2xl);
  padding: var(--space-10);
  position: relative;
  z-index: 1;
}

.login__card::before {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: inherit;
  padding: 1px;
  background: var(--gradient-primary);
  mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  opacity: 0.3;
  pointer-events: none;
}

/* Header */
.login__header {
  text-align: center;
  margin-bottom: var(--space-8);
}

.login__logo {
  display: flex;
  justify-content: center;
  margin-bottom: var(--space-4);
  filter: drop-shadow(0 0 40px var(--primary-glow));
}

.login__title {
  font-family: var(--font-display);
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -0.02em;
  margin-bottom: var(--space-2);
}

.login__subtitle {
  font-size: var(--text-sm);
  color: var(--text-tertiary);
}

/* Form */
.login__form {
  display: flex;
  flex-direction: column;
  gap: var(--space-5);
}

.login__error {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-4);
  background: var(--error-soft);
  border: 1px solid rgba(244, 63, 94, 0.2);
  border-radius: var(--radius-lg);
  font-size: var(--text-sm);
  color: var(--error);
}

/* Footer */
.login__footer {
  margin-top: var(--space-8);
  text-align: center;
  font-size: var(--text-xs);
  color: var(--text-muted);
}
</style>
