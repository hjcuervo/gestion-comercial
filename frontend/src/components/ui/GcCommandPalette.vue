<template>
  <Teleport to="body">
    <Transition name="gc-cmdk">
      <div v-if="open" class="gc-cmdk__overlay" @click.self="close">
        <div class="gc-cmdk" role="dialog" aria-modal="true" aria-label="Búsqueda y acciones">
          <div class="gc-cmdk__search">
            <GcIcon name="search" :size="16" />
            <input
              ref="inputRef"
              v-model="query"
              type="text"
              class="gc-cmdk__input"
              placeholder="Ir a módulo…"
              @keydown.down.prevent="move(1)"
              @keydown.up.prevent="move(-1)"
              @keydown.enter.prevent="select()"
              @keydown.esc.prevent="close"
            />
            <span class="gc-cmdk__hint gc-mono">esc</span>
          </div>

          <div class="gc-cmdk__results">
            <GcEmpty v-if="!filtered.length" icon="search-off" message="Sin resultados" />
            <button
              v-for="(item, i) in filtered"
              :key="item.path"
              :class="['gc-cmdk__item', i === activeIndex && 'gc-cmdk__item--active']"
              type="button"
              @click="go(item)"
              @mousemove="activeIndex = i"
            >
              <GcIcon :name="item.icon || 'arrow-right'" :size="16" />
              <span class="gc-cmdk__label">{{ item.label }}</span>
              <span class="gc-cmdk__path gc-mono">{{ item.path }}</span>
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { ref, computed, watch, nextTick } from 'vue';
import GcIcon from '@/components/ui/GcIcon.vue';
import GcEmpty from '@/components/ui/GcEmpty.vue';

const props = defineProps({
  open: { type: Boolean, default: false },
  items: { type: Array, default: () => [] }, // [{ path, label, icon? }]
});
const emit = defineEmits(['close', 'navigate']);

const query = ref('');
const activeIndex = ref(0);
const inputRef = ref(null);

const filtered = computed(() => {
  const q = query.value.trim().toLowerCase();
  if (!q) return props.items;
  return props.items.filter((it) => it.label.toLowerCase().includes(q) || it.path.toLowerCase().includes(q));
});

watch(() => props.open, async (v) => {
  if (v) {
    query.value = '';
    activeIndex.value = 0;
    await nextTick();
    inputRef.value?.focus();
  }
});
watch(filtered, () => { activeIndex.value = 0; });

function move(delta) {
  if (!filtered.value.length) return;
  const n = filtered.value.length;
  activeIndex.value = (activeIndex.value + delta + n) % n;
}
function select() {
  const item = filtered.value[activeIndex.value];
  if (item) go(item);
}
function go(item) {
  emit('navigate', item.path);
  close();
}
function close() { emit('close'); }
</script>

<style scoped>
.gc-cmdk__overlay {
  position: fixed; inset: 0;
  z-index: var(--gc-z-modal);
  display: flex; align-items: flex-start; justify-content: center;
  padding-top: 12vh;
  background: rgba(0, 0, 0, 0.28);
}
.gc-cmdk {
  width: 100%; max-width: 540px;
  background: var(--gc-surface);
  border: 1px solid var(--gc-border-strong);
  border-radius: var(--gc-radius-lg);
  box-shadow: var(--gc-shadow-pop);
  overflow: hidden;
}
.gc-cmdk__search {
  display: flex; align-items: center; gap: var(--gc-space-3);
  padding: var(--gc-space-4);
  border-bottom: 1px solid var(--gc-border);
  color: var(--gc-text-3);
}
.gc-cmdk__input {
  flex: 1; border: none; background: transparent;
  font-family: var(--gc-font-sans); font-size: var(--gc-fs-lg);
  color: var(--gc-text);
}
.gc-cmdk__input:focus { outline: none; }
.gc-cmdk__hint {
  font-size: var(--gc-fs-xs); color: var(--gc-text-3);
  border: 1px solid var(--gc-border); border-radius: var(--gc-radius-sm);
  padding: 1px 6px;
}
.gc-cmdk__results { max-height: 320px; overflow-y: auto; padding: var(--gc-space-2); }
.gc-cmdk__item {
  display: flex; align-items: center; gap: var(--gc-space-3);
  width: 100%; padding: var(--gc-space-3);
  background: transparent; border: none; border-radius: var(--gc-radius-md);
  color: var(--gc-text); cursor: pointer; text-align: left;
}
.gc-cmdk__item--active { background: var(--gc-surface-2); }
.gc-cmdk__label { flex: 1; font-size: var(--gc-fs-md); }
.gc-cmdk__path { font-size: var(--gc-fs-xs); color: var(--gc-text-3); }

.gc-cmdk-enter-active, .gc-cmdk-leave-active { transition: opacity var(--gc-t-normal); }
.gc-cmdk-enter-active .gc-cmdk, .gc-cmdk-leave-active .gc-cmdk { transition: transform var(--gc-t-normal); }
.gc-cmdk-enter-from, .gc-cmdk-leave-to { opacity: 0; }
.gc-cmdk-enter-from .gc-cmdk, .gc-cmdk-leave-to .gc-cmdk { transform: translateY(-8px); }
</style>
