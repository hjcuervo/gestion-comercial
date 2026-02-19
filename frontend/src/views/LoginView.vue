<template>
  <div class="aurora-login">
    <!-- Background Effect -->
    <div class="aurora-login__bg">
      <div class="aurora-login__orb aurora-login__orb--1"></div>
      <div class="aurora-login__orb aurora-login__orb--2"></div>
      <div class="aurora-login__orb aurora-login__orb--3"></div>
    </div>
    
    <!-- Login Card -->
    <div class="aurora-login__card">
      <div class="aurora-login__header">
        <div class="aurora-login__logo">
          <span class="material-icons-round">auto_graph</span>
        </div>
        <h1 class="aurora-login__title">GestCom</h1>
        <p class="aurora-login__subtitle">Sistema de Gestión Comercial</p>
      </div>
      
      <form class="aurora-login__form" @submit.prevent="handleLogin">
        <AuroraInput
          v-model="form.username"
          label="Usuario"
          placeholder="Ingresa tu usuario"
          icon="person"
          :error="errors.username"
          :disabled="authStore.loading"
        />
        
        <AuroraInput
          v-model="form.password"
          label="Contraseña"
          placeholder="Ingresa tu contraseña"
          :type="showPassword ? 'text' : 'password'"
          icon="lock"
          :trailing-icon="showPassword ? 'visibility_off' : 'visibility'"
          :error="errors.password"
          :disabled="authStore.loading"
          @trailing-click="showPassword = !showPassword"
        />
        
        <div v-if="authStore.error" class="aurora-login__error">
          <span class="material-icons-round">error_outline</span>
          {{ authStore.error }}
        </div>
        
        <AuroraButton 
          type="submit" 
          variant="primary"
          size="lg"
          full-width
          :loading="authStore.loading"
          :disabled="!isFormValid"
        >
          Iniciar Sesión
        </AuroraButton>
      </form>
      
      <div class="aurora-login__footer">
        <p>ArquitecSoft © 2026</p>
      </div>
    </div>
    
    <!-- Decorative Elements -->
    <div class="aurora-login__decoration">
      <div class="aurora-login__grid"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth.store';
import AuroraInput from '@/components/ui/AuroraInput.vue';
import AuroraButton from '@/components/ui/AuroraButton.vue';

const router = useRouter();
const authStore = useAuthStore();

const form = reactive({
  username: '',
  password: '',
});

const errors = reactive({
  username: '',
  password: '',
});

const showPassword = ref(false);

const isFormValid = computed(() => {
  return form.username.length > 0 && form.password.length > 0;
});

const handleLogin = async () => {
  errors.username = '';
  errors.password = '';
  authStore.clearError();
  
  if (!form.username) {
    errors.username = 'El usuario es requerido';
    return;
  }
  if (!form.password) {
    errors.password = 'La contraseña es requerida';
    return;
  }
  
  const success = await authStore.login(form.username, form.password);
  if (success) {
    router.push('/');
  }
};
</script>

<style scoped>
.aurora-login {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--aurora-space-6);
  position: relative;
  overflow: hidden;
}

/* Background */
.aurora-login__bg {
  position: absolute;
  inset: 0;
  overflow: hidden;
}

.aurora-login__orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.6;
  animation: float 20s ease-in-out infinite;
}

.aurora-login__orb--1 {
  width: 600px;
  height: 600px;
  background: var(--aurora-primary);
  top: -20%;
  left: -10%;
  animation-delay: 0s;
}

.aurora-login__orb--2 {
  width: 500px;
  height: 500px;
  background: var(--aurora-secondary);
  bottom: -20%;
  right: -10%;
  animation-delay: -7s;
}

.aurora-login__orb--3 {
  width: 400px;
  height: 400px;
  background: var(--aurora-accent-pink);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation-delay: -14s;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) scale(1); }
  25% { transform: translate(20px, -20px) scale(1.05); }
  50% { transform: translate(-10px, 10px) scale(0.95); }
  75% { transform: translate(-20px, -10px) scale(1.02); }
}

/* Card */
.aurora-login__card {
  width: 100%;
  max-width: 420px;
  background: rgba(12, 12, 30, 0.8);
  backdrop-filter: blur(40px);
  border: 1px solid var(--aurora-border);
  border-radius: var(--aurora-radius-2xl);
  padding: var(--aurora-space-10);
  position: relative;
  z-index: 1;
  animation: slideUp 0.6s ease-out;
}

.aurora-login__header {
  text-align: center;
  margin-bottom: var(--aurora-space-8);
}

.aurora-login__logo {
  width: 72px;
  height: 72px;
  margin: 0 auto var(--aurora-space-4);
  background: var(--aurora-gradient-primary);
  border-radius: var(--aurora-radius-xl);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: var(--aurora-shadow-glow-primary);
}

.aurora-login__logo .material-icons-round {
  font-size: 36px;
  color: white;
}

.aurora-login__title {
  font-size: var(--aurora-text-3xl);
  font-weight: var(--aurora-font-bold);
  background: var(--aurora-gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: var(--aurora-space-2);
}

.aurora-login__subtitle {
  font-size: var(--aurora-text-sm);
  color: var(--aurora-text-tertiary);
}

.aurora-login__form {
  display: flex;
  flex-direction: column;
  gap: var(--aurora-space-5);
}

.aurora-login__error {
  display: flex;
  align-items: center;
  gap: var(--aurora-space-2);
  padding: var(--aurora-space-4);
  background: var(--aurora-error-bg);
  border: 1px solid rgba(239, 68, 68, 0.3);
  border-radius: var(--aurora-radius-lg);
  font-size: var(--aurora-text-sm);
  color: var(--aurora-error);
}

.aurora-login__error .material-icons-round {
  font-size: 20px;
}

.aurora-login__footer {
  margin-top: var(--aurora-space-8);
  text-align: center;
  font-size: var(--aurora-text-xs);
  color: var(--aurora-text-muted);
}

/* Decoration */
.aurora-login__decoration {
  position: absolute;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
}

.aurora-login__grid {
  position: absolute;
  inset: 0;
  background-image: 
    linear-gradient(rgba(124, 58, 237, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(124, 58, 237, 0.03) 1px, transparent 1px);
  background-size: 60px 60px;
  mask-image: radial-gradient(ellipse at center, black 0%, transparent 70%);
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
