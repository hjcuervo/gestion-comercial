<template>
  <Teleport to="body">
    <Transition name="gc-drawer">
      <div v-if="open" class="gc-drawer__overlay" @click.self="$emit('close')">
        <aside class="gc-drawer" role="dialog" aria-modal="true" :style="{ width }">
          <header class="gc-drawer__header">
            <div class="gc-drawer__heading">
              <h2 class="gc-drawer__title">{{ title }}</h2>
              <p v-if="subtitle" class="gc-drawer__subtitle">{{ subtitle }}</p>
            </div>
            <GcButton variant="ghost" size="sm" icon="x" @click="$emit('close')" />
          </header>
          <div class="gc-drawer__body">
            <slot />
          </div>
          <footer v-if="$slots.footer" class="gc-drawer__footer">
            <slot name="footer" />
          </footer>
        </aside>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import GcButton from './GcButton.vue';

defineProps({
  open: { type: Boolean, default: false },
  title: { type: String, default: '' },
  subtitle: { type: String, default: '' },
  width: { type: String, default: '420px' },
});

defineEmits(['close']);
</script>

<style scoped>
.gc-drawer__overlay {
  position: fixed;
  inset: 0;
  z-index: var(--gc-z-drawer);
  display: flex;
  justify-content: flex-end;
  background: rgba(0, 0, 0, 0.28);
}
.gc-drawer {
  height: 100%;
  max-width: 92vw;
  background: var(--gc-surface);
  border-left: 1px solid var(--gc-border-strong);
  box-shadow: var(--gc-shadow-drawer);
  display: flex;
  flex-direction: column;
}
.gc-drawer__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--gc-space-3);
  padding: var(--gc-space-4) var(--gc-space-5);
  border-bottom: 1px solid var(--gc-border);
}
.gc-drawer__heading { display: flex; flex-direction: column; gap: 2px; min-width: 0; }
.gc-drawer__title { font-size: var(--gc-fs-lg); }
.gc-drawer__subtitle { font-size: var(--gc-fs-sm); color: var(--gc-text-2); }
.gc-drawer__body { flex: 1; padding: var(--gc-space-5); overflow-y: auto; }
.gc-drawer__footer {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: var(--gc-space-2);
  padding: var(--gc-space-4) var(--gc-space-5);
  border-top: 1px solid var(--gc-border);
}

.gc-drawer-enter-active,
.gc-drawer-leave-active { transition: opacity var(--gc-t-normal); }
.gc-drawer-enter-active .gc-drawer,
.gc-drawer-leave-active .gc-drawer { transition: transform var(--gc-t-normal); }
.gc-drawer-enter-from,
.gc-drawer-leave-to { opacity: 0; }
.gc-drawer-enter-from .gc-drawer,
.gc-drawer-leave-to .gc-drawer { transform: translateX(100%); }
</style>
