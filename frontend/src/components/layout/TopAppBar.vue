<template>
  <header class="top-app-bar">
    <div class="top-app-bar__leading">
      <slot name="leading">
        <h1 class="top-app-bar__title">{{ title }}</h1>
      </slot>
    </div>
    
    <div class="top-app-bar__trailing">
      <slot name="trailing">
        <div class="top-app-bar__user">
          <span class="top-app-bar__user-name">{{ userName }}</span>
          <div class="top-app-bar__avatar">
            <span class="material-icons">account_circle</span>
          </div>
        </div>
      </slot>
    </div>
  </header>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    default: ''
  },
  userName: {
    type: String,
    default: ''
  }
});
</script>

<style scoped>
.top-app-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: var(--md-sys-layout-top-app-bar-height);
  padding: 0 var(--md-sys-spacing-lg);
  background-color: var(--md-sys-color-surface);
  border-bottom: 1px solid var(--md-sys-color-outline-variant);
  position: sticky;
  top: 0;
  z-index: 50;
}

.top-app-bar__leading {
  display: flex;
  align-items: center;
  gap: var(--md-sys-spacing-md);
}

.top-app-bar__title {
  font: var(--md-sys-typescale-title-large);
  color: var(--md-sys-color-on-surface);
}

.top-app-bar__trailing {
  display: flex;
  align-items: center;
  gap: var(--md-sys-spacing-md);
}

.top-app-bar__user {
  display: flex;
  align-items: center;
  gap: var(--md-sys-spacing-sm);
}

.top-app-bar__user-name {
  font: var(--md-sys-typescale-label-large);
  color: var(--md-sys-color-on-surface-variant);
}

.top-app-bar__avatar {
  width: 40px;
  height: 40px;
  border-radius: var(--md-sys-shape-corner-full);
  background-color: var(--md-sys-color-primary-container);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--md-sys-color-on-primary-container);
}

.top-app-bar__avatar .material-icons {
  font-size: 32px;
}
</style>
