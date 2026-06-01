<template>
  <div class="login">
    <div class="login__panel">
      <div class="login__header">
        <span class="login__mark"></span>
        <h1 class="login__title">GestCom</h1>
        <p class="login__subtitle">Plataforma de Gestión Comercial y Contractual</p>
      </div>

      <form class="login__form" @submit.prevent="handleLogin">
        <GcInput
          v-model="form.username"
          label="Usuario"
          placeholder="Ingresa tu usuario"
          icon="user"
          :error="errors.username"
          :disabled="authStore.loading"
          @enter="handleLogin"
        />
        <GcInput
          v-model="form.password"
          label="Contraseña"
          placeholder="Ingresa tu contraseña"
          type="password"
          icon="lock"
          :error="errors.password"
          :disabled="authStore.loading"
          @enter="handleLogin"
        />

        <div v-if="authStore.error" class="login__error">
          <GcIcon name="alert-circle" :size="16" />
          <span>{{ authStore.error }}</span>
        </div>

        <GcButton
          type="submit"
          variant="primary"
          size="lg"
          full-width
          :loading="authStore.loading"
          :disabled="!isFormValid"
        >
          Iniciar sesión
        </GcButton>
      </form>

      <p class="login__footer">Arquitecsoft SAS © 2026</p>
    </div>
  </div>
</template>

<script setup>
import { reactive, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth.store';
import GcInput from '@/components/ui/GcInput.vue';
import GcButton from '@/components/ui/GcButton.vue';
import GcIcon from '@/components/ui/GcIcon.vue';

const router = useRouter();
const authStore = useAuthStore();

const form = reactive({ username: '', password: '' });
const errors = reactive({ username: '', password: '' });

const isFormValid = computed(() => form.username.length > 0 && form.password.length > 0);

async function handleLogin() {
  errors.username = '';
  errors.password = '';
  authStore.clearError();

  if (!form.username) { errors.username = 'El usuario es requerido'; return; }
  if (!form.password) { errors.password = 'La contraseña es requerida'; return; }

  const success = await authStore.login(form.username, form.password);
  if (success) router.push('/');
}
</script>

<style scoped>
.login {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--gc-space-6);
  background: var(--gc-bg);
}
.login__panel {
  width: 100%;
  max-width: 380px;
  background: var(--gc-surface);
  border: 1px solid var(--gc-border);
  border-radius: var(--gc-radius-lg);
  padding: var(--gc-space-10) var(--gc-space-8);
}
.login__header { margin-bottom: var(--gc-space-8); }
.login__mark {
  display: block;
  width: 28px;
  height: 28px;
  border-radius: var(--gc-radius-md);
  background: var(--gc-primary);
  margin-bottom: var(--gc-space-4);
}
.login__title { font-size: var(--gc-fs-2xl); letter-spacing: -0.02em; }
.login__subtitle {
  margin-top: var(--gc-space-1);
  font-size: var(--gc-fs-md);
  color: var(--gc-text-2);
}
.login__form { display: flex; flex-direction: column; gap: var(--gc-space-4); }
.login__error {
  display: flex;
  align-items: center;
  gap: var(--gc-space-2);
  padding: var(--gc-space-3);
  background: var(--gc-danger-soft);
  border: 1px solid var(--gc-danger);
  border-radius: var(--gc-radius-md);
  font-size: var(--gc-fs-sm);
  color: var(--gc-danger);
}
.login__footer {
  margin-top: var(--gc-space-8);
  text-align: center;
  font-size: var(--gc-fs-xs);
  color: var(--gc-text-3);
}
</style>
