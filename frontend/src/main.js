import { createApp } from 'vue';
import { createPinia } from 'pinia';
import App from './App.vue';
import router from './router';

// Iconos (un solo set: Tabler outline, webfont)
import '@tabler/icons-webfont/dist/tabler-icons.min.css';

// Estilos: identidad "Instrumento" (tokens --gc-* + reset global + base).
// El sistema legacy "Luxury Tech" (tokens.css + global.css) fue eliminado en RF8.
import './assets/styles/instrument.css';

const app = createApp(App);

app.use(createPinia());
app.use(router);

app.mount('#app');
