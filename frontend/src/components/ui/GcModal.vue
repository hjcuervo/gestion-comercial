<template>
  <Teleport to="body">
    <Transition name="gc-modal">
      <div v-if="open" class="gc-modal__overlay" @click.self="$emit('close')">
        <div class="gc-modal" role="dialog" aria-modal="true" :style="{ maxWidth: width }">
          <header v-if="title || $slots.header" class="gc-modal__header">
            <slot name="header">
              <h2 class="gc-modal__title">{{ title }}</h2>
            </slot>
            <GcButton variant="ghost" size="sm" icon="x" @click="$emit('close')" />
          </header>
          <div class="gc-modal__body">
            <slot />
          </div>
          <footer v-if="$slots.footer" class="gc-modal__footer">
            <slot name="footer" />
          </footer>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import GcButton from './GcButton.vue';

defineProps({
  open: { type: Boolean, default: false },
  title: { type: String, default: '' },
  width: { type: String, default: '520px' },
});

defineEmits(['close']);
</script>

<style scoped>
.gc-modal__overlay {
  position: fixed;
  inset: 0;
  z-index: var(--gc-z-modal);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--gc-space-6);
  background: rgba(0, 0, 0, 0.32);
}
.gc-modal {
  width: 100%;
  background: var(--gc-surface);
  border: 1px solid var(--gc-border-strong);
  border-radius: var(--gc-radius-lg);
  box-shadow: var(--gc-shadow-pop);
  display: flex;
  flex-direction: column;
  max-height: calc(100vh - var(--gc-space-12));
}
.gc-modal__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--gc-space-3);
  padding: var(--gc-space-4) var(--gc-space-5);
  border-bottom: 1px solid var(--gc-border);
}
.gc-modal__title { font-size: var(--gc-fs-lg); }
.gc-modal__body { padding: var(--gc-space-5); overflow-y: auto; }
.gc-modal__footer {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: var(--gc-space-2);
  padding: var(--gc-space-4) var(--gc-space-5);
  border-top: 1px solid var(--gc-border);
}

.gc-modal-enter-active,
.gc-modal-leave-active { transition: opacity var(--gc-t-normal); }
.gc-modal-enter-from,
.gc-modal-leave-to { opacity: 0; }
</style>
