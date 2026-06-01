import { createApp } from 'vue';
import { createPinia } from 'pinia';
import App from './App.vue';
import router from './router';

// Iconos (un solo set: Tabler outline, webfont)
import '@tabler/icons-webfont/dist/tabler-icons.min.css';

// --- Estilos ---
// 1) Legacy (sistema "Luxury Tech"): da tema a las pantallas aún NO migradas
//    (layout 'legacy'). Se retira en RF8 cuando se demuelan esas pantallas.
import './assets/styles/tokens.css';
import './assets/styles/global.css';
// 2) Rediseño "Instrumento": tokens --gc-* + base scoped para pantallas nuevas.
//    Va DESPUÉS para que sus variables queden disponibles; no colisiona con las
//    legacy (prefijo --gc-* distinto) y solo estiliza pantallas nuevas.
import './assets/styles/instrument.css';

const app = createApp(App);

app.use(createPinia());
app.use(router);

app.mount('#app');
