<template>
  <svg
    :width="size"
    :height="size"
    viewBox="0 0 24 24"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
    :class="['icon', { 'icon--spin': spin }]"
    :style="{ color: color }"
  >
    <component :is="iconComponent" />
  </svg>
</template>

<script setup>
import { computed, h } from 'vue';

const props = defineProps({
  name: {
    type: String,
    required: true
  },
  size: {
    type: [Number, String],
    default: 24
  },
  color: {
    type: String,
    default: 'currentColor'
  },
  spin: Boolean
});

// SVG Path definitions for each icon
const icons = {
  // Navigation
  dashboard: () => h('g', [
    h('rect', { x: '3', y: '3', width: '7', height: '7', rx: '1.5', stroke: 'currentColor', 'stroke-width': '1.5' }),
    h('rect', { x: '14', y: '3', width: '7', height: '7', rx: '1.5', stroke: 'currentColor', 'stroke-width': '1.5' }),
    h('rect', { x: '3', y: '14', width: '7', height: '7', rx: '1.5', stroke: 'currentColor', 'stroke-width': '1.5' }),
    h('rect', { x: '14', y: '14', width: '7', height: '7', rx: '1.5', stroke: 'currentColor', 'stroke-width': '1.5' })
  ]),
  
  pipeline: () => h('g', [
    h('rect', { x: '2', y: '4', width: '5', height: '16', rx: '1', stroke: 'currentColor', 'stroke-width': '1.5' }),
    h('rect', { x: '9.5', y: '4', width: '5', height: '12', rx: '1', stroke: 'currentColor', 'stroke-width': '1.5' }),
    h('rect', { x: '17', y: '4', width: '5', height: '8', rx: '1', stroke: 'currentColor', 'stroke-width': '1.5' })
  ]),
  
  business: () => h('g', [
    h('path', { d: 'M3 21V7a2 2 0 012-2h6a2 2 0 012 2v14', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round' }),
    h('path', { d: 'M13 21V11a2 2 0 012-2h4a2 2 0 012 2v10', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round' }),
    h('path', { d: 'M1 21h22', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round' }),
    h('rect', { x: '6', y: '9', width: '2', height: '2', rx: '0.5', fill: 'currentColor' }),
    h('rect', { x: '6', y: '13', width: '2', height: '2', rx: '0.5', fill: 'currentColor' }),
    h('rect', { x: '16', y: '13', width: '2', height: '2', rx: '0.5', fill: 'currentColor' })
  ]),
  
  people: () => h('g', [
    h('circle', { cx: '9', cy: '7', r: '3', stroke: 'currentColor', 'stroke-width': '1.5' }),
    h('path', { d: 'M3 21v-2a4 4 0 014-4h4a4 4 0 014 4v2', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round' }),
    h('circle', { cx: '17', cy: '8', r: '2.5', stroke: 'currentColor', 'stroke-width': '1.5' }),
    h('path', { d: 'M17 13.5a3 3 0 013 3V21', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round' })
  ]),
  
  calendar: () => h('g', [
    h('rect', { x: '3', y: '4', width: '18', height: '18', rx: '2', stroke: 'currentColor', 'stroke-width': '1.5' }),
    h('path', { d: 'M3 10h18', stroke: 'currentColor', 'stroke-width': '1.5' }),
    h('path', { d: 'M8 2v4M16 2v4', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round' }),
    h('rect', { x: '7', y: '14', width: '3', height: '3', rx: '0.5', fill: 'currentColor' })
  ]),
  
  chart: () => h('g', [
    h('path', { d: 'M3 3v18h18', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round' }),
    h('path', { d: 'M7 14l4-4 4 2 5-6', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }),
    h('circle', { cx: '7', cy: '14', r: '1.5', fill: 'currentColor' }),
    h('circle', { cx: '11', cy: '10', r: '1.5', fill: 'currentColor' }),
    h('circle', { cx: '15', cy: '12', r: '1.5', fill: 'currentColor' }),
    h('circle', { cx: '20', cy: '6', r: '1.5', fill: 'currentColor' })
  ]),

  // Actions
  plus: () => h('g', [
    h('path', { d: 'M12 5v14M5 12h14', stroke: 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round' })
  ]),
  
  search: () => h('g', [
    h('circle', { cx: '10', cy: '10', r: '6', stroke: 'currentColor', 'stroke-width': '1.5' }),
    h('path', { d: 'M14.5 14.5L20 20', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round' })
  ]),
  
  bell: () => h('g', [
    h('path', { d: 'M18 8A6 6 0 106 8c0 7-3 9-3 9h18s-3-2-3-9', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }),
    h('path', { d: 'M13.73 21a2 2 0 01-3.46 0', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round' })
  ]),
  
  logout: () => h('g', [
    h('path', { d: 'M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round' }),
    h('path', { d: 'M16 17l5-5-5-5M21 12H9', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' })
  ]),
  
  settings: () => h('g', [
    h('circle', { cx: '12', cy: '12', r: '3', stroke: 'currentColor', 'stroke-width': '1.5' }),
    h('path', { d: 'M19.4 15a1.65 1.65 0 00.33 1.82l.06.06a2 2 0 010 2.83 2 2 0 01-2.83 0l-.06-.06a1.65 1.65 0 00-1.82-.33 1.65 1.65 0 00-1 1.51V21a2 2 0 01-2 2 2 2 0 01-2-2v-.09A1.65 1.65 0 009 19.4a1.65 1.65 0 00-1.82.33l-.06.06a2 2 0 01-2.83 0 2 2 0 010-2.83l.06-.06a1.65 1.65 0 00.33-1.82 1.65 1.65 0 00-1.51-1H3a2 2 0 01-2-2 2 2 0 012-2h.09A1.65 1.65 0 004.6 9a1.65 1.65 0 00-.33-1.82l-.06-.06a2 2 0 010-2.83 2 2 0 012.83 0l.06.06a1.65 1.65 0 001.82.33H9a1.65 1.65 0 001-1.51V3a2 2 0 012-2 2 2 0 012 2v.09a1.65 1.65 0 001 1.51 1.65 1.65 0 001.82-.33l.06-.06a2 2 0 012.83 0 2 2 0 010 2.83l-.06.06a1.65 1.65 0 00-.33 1.82V9a1.65 1.65 0 001.51 1H21a2 2 0 012 2 2 2 0 01-2 2h-.09a1.65 1.65 0 00-1.51 1z', stroke: 'currentColor', 'stroke-width': '1.5' })
  ]),

  // Sidebar
  menu: () => h('g', [
    h('path', { d: 'M3 12h18M3 6h18M3 18h18', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round' })
  ]),
  
  'chevron-left': () => h('path', { d: 'M15 18l-6-6 6-6', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }),
  
  'chevron-right': () => h('path', { d: 'M9 18l6-6-6-6', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }),
  
  'chevron-down': () => h('path', { d: 'M6 9l6 6 6-6', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }),

  // Stats
  wallet: () => h('g', [
    h('rect', { x: '2', y: '6', width: '20', height: '14', rx: '2', stroke: 'currentColor', 'stroke-width': '1.5' }),
    h('path', { d: 'M2 10h20', stroke: 'currentColor', 'stroke-width': '1.5' }),
    h('circle', { cx: '16', cy: '14', r: '2', stroke: 'currentColor', 'stroke-width': '1.5' })
  ]),
  
  handshake: () => h('g', [
    h('path', { d: 'M11 17l-2-2m6 0l2-2m-8 0l-4 4m14-4l-4 4', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }),
    h('path', { d: 'M7 11l3-3 2 2 5-5', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }),
    h('path', { d: 'M3 14l4-4M21 10l-4 4', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round' })
  ]),
  
  trophy: () => h('g', [
    h('path', { d: 'M6 9H3a1 1 0 01-1-1V5a1 1 0 011-1h3M18 9h3a1 1 0 001-1V5a1 1 0 00-1-1h-3', stroke: 'currentColor', 'stroke-width': '1.5' }),
    h('path', { d: 'M6 4h12v5a6 6 0 11-12 0V4z', stroke: 'currentColor', 'stroke-width': '1.5' }),
    h('path', { d: 'M12 15v3M9 21h6', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round' })
  ]),
  
  'trending-up': () => h('g', [
    h('path', { d: 'M23 6l-9.5 9.5-5-5L1 18', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }),
    h('path', { d: 'M17 6h6v6', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' })
  ]),
  
  'trending-down': () => h('g', [
    h('path', { d: 'M23 18l-9.5-9.5-5 5L1 6', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }),
    h('path', { d: 'M17 18h6v-6', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' })
  ]),

  // Form
  user: () => h('g', [
    h('circle', { cx: '12', cy: '8', r: '4', stroke: 'currentColor', 'stroke-width': '1.5' }),
    h('path', { d: 'M4 21v-2a4 4 0 014-4h8a4 4 0 014 4v2', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round' })
  ]),
  
  lock: () => h('g', [
    h('rect', { x: '3', y: '11', width: '18', height: '11', rx: '2', stroke: 'currentColor', 'stroke-width': '1.5' }),
    h('path', { d: 'M7 11V7a5 5 0 0110 0v4', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round' }),
    h('circle', { cx: '12', cy: '16', r: '1.5', fill: 'currentColor' })
  ]),
  
  eye: () => h('g', [
    h('path', { d: 'M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z', stroke: 'currentColor', 'stroke-width': '1.5' }),
    h('circle', { cx: '12', cy: '12', r: '3', stroke: 'currentColor', 'stroke-width': '1.5' })
  ]),
  
  'eye-off': () => h('g', [
    h('path', { d: 'M17.94 17.94A10.07 10.07 0 0112 20c-7 0-11-8-11-8a18.45 18.45 0 015.06-5.94M9.9 4.24A9.12 9.12 0 0112 4c7 0 11 8 11 8a18.5 18.5 0 01-2.16 3.19m-6.72-1.07a3 3 0 11-4.24-4.24', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round' }),
    h('path', { d: 'M1 1l22 22', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round' })
  ]),

  // Misc
  clock: () => h('g', [
    h('circle', { cx: '12', cy: '12', r: '9', stroke: 'currentColor', 'stroke-width': '1.5' }),
    h('path', { d: 'M12 6v6l4 2', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round' })
  ]),
  
  check: () => h('path', { d: 'M5 12l5 5L20 7', stroke: 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }),
  
  'check-circle': () => h('g', [
    h('circle', { cx: '12', cy: '12', r: '9', stroke: 'currentColor', 'stroke-width': '1.5' }),
    h('path', { d: 'M8 12l3 3 5-6', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' })
  ]),
  
  x: () => h('path', { d: 'M18 6L6 18M6 6l12 12', stroke: 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round' }),
  
  'arrow-right': () => h('g', [
    h('path', { d: 'M5 12h14M13 5l7 7-7 7', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' })
  ]),
  
  'external-link': () => h('g', [
    h('path', { d: 'M18 13v6a2 2 0 01-2 2H5a2 2 0 01-2-2V8a2 2 0 012-2h6', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round' }),
    h('path', { d: 'M15 3h6v6M10 14L21 3', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' })
  ]),
  
  loader: () => h('g', [
    h('path', { d: 'M12 2v4M12 18v4M4.93 4.93l2.83 2.83M16.24 16.24l2.83 2.83M2 12h4M18 12h4M4.93 19.07l2.83-2.83M16.24 7.76l2.83-2.83', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round' })
  ]),
  
  'alert-circle': () => h('g', [
    h('circle', { cx: '12', cy: '12', r: '9', stroke: 'currentColor', 'stroke-width': '1.5' }),
    h('path', { d: 'M12 8v4M12 16h.01', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round' })
  ]),
  
  home: () => h('g', [
    h('path', { d: 'M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2V9z', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }),
    h('path', { d: 'M9 22V12h6v10', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' })
  ]),
  
  // Additional icons for forms and actions
  'person-add': () => h('g', [
    h('circle', { cx: '9', cy: '7', r: '4', stroke: 'currentColor', 'stroke-width': '1.5' }),
    h('path', { d: 'M3 21v-2a4 4 0 014-4h4a4 4 0 014 4v2', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round' }),
    h('path', { d: 'M19 8v6M16 11h6', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round' })
  ]),
  
  'building-add': () => h('g', [
    h('path', { d: 'M3 21V7a2 2 0 012-2h8a2 2 0 012 2v14', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round' }),
    h('path', { d: 'M1 21h14', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round' }),
    h('rect', { x: '6', y: '9', width: '2', height: '2', rx: '0.5', fill: 'currentColor' }),
    h('rect', { x: '10', y: '9', width: '2', height: '2', rx: '0.5', fill: 'currentColor' }),
    h('rect', { x: '6', y: '13', width: '2', height: '2', rx: '0.5', fill: 'currentColor' }),
    h('path', { d: 'M19 12v6M16 15h6', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round' })
  ]),
  
  'note-add': () => h('g', [
    h('path', { d: 'M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8l-6-6z', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }),
    h('path', { d: 'M14 2v6h6', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }),
    h('path', { d: 'M12 12v6M9 15h6', stroke: 'currentColor', 'stroke-width': '1.5', 'stroke-linecap': 'round' })
  ]),
  
  'bar-chart': () => h('g', [
    h('rect', { x: '3', y: '12', width: '4', height: '9', rx: '1', stroke: 'currentColor', 'stroke-width': '1.5' }),
    h('rect', { x: '10', y: '8', width: '4', height: '13', rx: '1', stroke: 'currentColor', 'stroke-width': '1.5' }),
    h('rect', { x: '17', y: '3', width: '4', height: '18', rx: '1', stroke: 'currentColor', 'stroke-width': '1.5' })
  ])
};

const iconComponent = computed(() => {
  return icons[props.name] || icons['alert-circle'];
});
</script>

<style scoped>
.icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.icon--spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
