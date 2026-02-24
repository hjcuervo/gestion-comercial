# GestCom - Sistema de Gestión Comercial
## Plataforma B2B para Gestión de Oportunidades Comerciales

**Versión:** 0.1.0  
**Fecha:** Febrero 2026  
**Cliente:** ArquitecSoft  

---

## 📋 Tabla de Contenidos

1. [Descripción General](#descripción-general)
2. [Tecnologías](#tecnologías)
3. [Estructura del Proyecto](#estructura-del-proyecto)
4. [Instalación y Ejecución](#instalación-y-ejecución)
5. [Sistema de Diseño Frontend](#sistema-de-diseño-frontend)
6. [Componentes UI](#componentes-ui)
7. [Guía de Desarrollo Frontend](#guía-de-desarrollo-frontend)
8. [API Backend](#api-backend)
9. [Reglas de Negocio](#reglas-de-negocio)
10. [Credenciales de Prueba](#credenciales-de-prueba)

---

## Descripción General

GestCom es una plataforma de gestión comercial diseñada para equipos de ventas B2B. Permite gestionar el ciclo completo de oportunidades comerciales, desde la prospección hasta el cierre.

### Funcionalidades Principales

- 🏢 **Gestión de Empresas** - CRUD completo de empresas cliente
- 👥 **Gestión de Contactos** - Personas asociadas a empresas
- 📊 **Pipeline de Ventas** - Tablero Kanban configurable
- 💼 **Oportunidades** - Seguimiento del ciclo de ventas
- 📅 **Actividades** - Registro de interacciones
- ✅ **Compromisos** - Tareas y seguimientos
- 📄 **Documentos** - Adjuntos por oportunidad/actividad
- 📈 **Reportes** - Análisis y métricas

---

## Tecnologías

### Backend
| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| Java | 21 | Runtime |
| Spring Boot | 3.4.x | Framework |
| Spring Security | 6.x | Autenticación JWT |
| Oracle Database | 23c | Base de datos |
| Maven | 3.9+ | Build tool |

### Frontend
| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| Vue.js | 3.x | Framework UI |
| Vite | 5.x | Build tool |
| Vue Router | 4.x | Routing |
| Pinia | 2.x | State management |
| Axios | 1.x | HTTP client |

---

## Estructura del Proyecto

```
gestion-comercial/
├── backend/
│   ├── src/main/java/com/arquitecsoft/gestion/
│   │   ├── modules/
│   │   │   ├── auth/
│   │   │   ├── empresa/
│   │   │   ├── persona/
│   │   │   ├── pipeline/
│   │   │   ├── oportunidad/
│   │   │   ├── actividad/
│   │   │   ├── compromiso/
│   │   │   └── documento/
│   │   └── infrastructure/
│   │       ├── config/
│   │       ├── security/
│   │       └── exception/
│   └── pom.xml
│
├── frontend/
│   ├── src/
│   │   ├── assets/styles/
│   │   │   ├── tokens.css       # Design tokens
│   │   │   └── global.css       # Global styles
│   │   ├── components/
│   │   │   ├── ui/              # Componentes reutilizables
│   │   │   └── layout/          # Layout components
│   │   ├── views/               # Vistas/páginas
│   │   ├── services/            # API services
│   │   ├── stores/              # Pinia stores
│   │   └── router/              # Vue Router
│   ├── package.json
│   └── vite.config.js
│
├── docs/                        # Documentación
└── README.md
```

---

## Instalación y Ejecución

### Requisitos Previos

- Java 21+
- Node.js 20+
- Oracle Database 23c
- Maven 3.9+

### Backend

```bash
cd backend

# Compilar
./mvnw clean package -DskipTests

# Ejecutar
java -jar target/gestion-comercial-0.1.0.jar
```

El backend estará disponible en `http://localhost:8080`

### Frontend

```bash
cd frontend

# Instalar dependencias
npm install

# Ejecutar en desarrollo
npm run dev

# Build para producción
npm run build
```

El frontend estará disponible en `http://localhost:5173`

---

## Sistema de Diseño Frontend

### 🎨 Filosofía de Diseño

El frontend sigue las directrices del **skill frontend-design** para crear interfaces distintivas y profesionales que evitan la estética genérica de "AI slop".

#### Principios Clave

1. **Tipografía Distintiva** - Usar fuentes con personalidad, no genéricas
2. **Iconos SVG Personalizados** - No depender de fuentes de iconos externas
3. **Paleta de Colores Única** - Colores con intención, no predeterminados
4. **Efectos de Profundidad** - Glass morphism, shadows, gradients
5. **Animaciones con Propósito** - Micro-interacciones que mejoran UX
6. **Diseño Memorable** - Cada interfaz debe tener carácter propio

### 🎯 Estética: "Luxury Tech"

GestCom utiliza una estética **Luxury Tech** - sofisticada, premium, con toques de glassmorphism oscuro y acentos de color neón sutil.

---

### 📐 Design Tokens

Todos los valores de diseño están centralizados en `src/assets/styles/tokens.css`:

#### Tipografía

```css
/* Fuentes */
--font-display: 'Outfit', sans-serif;    /* Títulos */
--font-body: 'Outfit', sans-serif;       /* Texto */
--font-mono: 'JetBrains Mono', monospace; /* Código */

/* Tamaños */
--text-xs: 0.75rem;    /* 12px */
--text-sm: 0.875rem;   /* 14px */
--text-base: 1rem;     /* 16px */
--text-lg: 1.125rem;   /* 18px */
--text-xl: 1.25rem;    /* 20px */
--text-2xl: 1.5rem;    /* 24px */
--text-3xl: 2rem;      /* 32px */
--text-4xl: 2.5rem;    /* 40px */
```

#### Colores

```css
/* Backgrounds */
--bg-deep: #08090d;
--bg-base: #0d0f14;
--bg-elevated: #13161d;
--bg-surface: #1a1d26;

/* Primary - Electric Cyan */
--primary: #00d4ff;
--primary-soft: rgba(0, 212, 255, 0.15);
--primary-glow: rgba(0, 212, 255, 0.4);

/* Secondary - Vivid Violet */
--secondary: #a855f7;
--secondary-soft: rgba(168, 85, 247, 0.15);

/* Accent - Warm Coral */
--accent: #ff6b6b;

/* Semantic */
--success: #10b981;
--warning: #f59e0b;
--error: #f43f5e;

/* Text */
--text-primary: #ffffff;
--text-secondary: rgba(255, 255, 255, 0.7);
--text-tertiary: rgba(255, 255, 255, 0.5);
--text-muted: rgba(255, 255, 255, 0.3);
```

#### Gradientes

```css
/* Gradiente principal */
--gradient-primary: linear-gradient(135deg, #00d4ff 0%, #a855f7 100%);

/* Glass effect */
--glass-bg: rgba(255, 255, 255, 0.02);
--glass-border: rgba(255, 255, 255, 0.06);
```

#### Espaciado

```css
--space-1: 0.25rem;   /* 4px */
--space-2: 0.5rem;    /* 8px */
--space-3: 0.75rem;   /* 12px */
--space-4: 1rem;      /* 16px */
--space-5: 1.25rem;   /* 20px */
--space-6: 1.5rem;    /* 24px */
--space-8: 2rem;      /* 32px */
```

#### Border Radius

```css
--radius-sm: 8px;
--radius-md: 12px;
--radius-lg: 16px;
--radius-xl: 20px;
--radius-2xl: 28px;
--radius-full: 9999px;
```

---

## Componentes UI

### Iconos (`Icon.vue`)

Los iconos son **SVG inline** para garantizar renderizado consistente.

```vue
<Icon name="dashboard" :size="24" color="var(--primary)" />
```

**Iconos disponibles:**
- Navigation: `dashboard`, `pipeline`, `business`, `people`, `calendar`, `chart`
- Actions: `plus`, `search`, `bell`, `logout`, `settings`, `menu`
- Arrows: `chevron-left`, `chevron-right`, `chevron-down`, `arrow-right`
- Stats: `wallet`, `handshake`, `trophy`, `trending-up`, `trending-down`
- Form: `user`, `lock`, `eye`, `eye-off`
- Misc: `clock`, `check`, `check-circle`, `x`, `alert-circle`, `home`, `loader`
- Add: `person-add`, `building-add`, `note-add`, `bar-chart`

### Botones (`Button.vue`)

```vue
<!-- Primary con icono -->
<Button variant="primary" icon="plus">Nueva Oportunidad</Button>

<!-- Secondary -->
<Button variant="secondary">Cancelar</Button>

<!-- Ghost -->
<Button variant="ghost" size="sm" trailing-icon="arrow-right">Ver más</Button>

<!-- Solo icono -->
<Button variant="ghost" icon="check" icon-only />

<!-- Con loading -->
<Button variant="primary" :loading="isLoading">Guardar</Button>
```

**Props:**
| Prop | Tipo | Default | Opciones |
|------|------|---------|----------|
| variant | String | 'primary' | primary, secondary, ghost, danger, success |
| size | String | 'md' | sm, md, lg |
| icon | String | - | Nombre del icono |
| trailingIcon | String | - | Icono al final |
| iconOnly | Boolean | false | Solo mostrar icono |
| loading | Boolean | false | Estado de carga |
| disabled | Boolean | false | Deshabilitado |
| fullWidth | Boolean | false | Ancho completo |

### Inputs (`Input.vue`)

```vue
<Input
  v-model="email"
  label="Correo electrónico"
  placeholder="usuario@empresa.com"
  icon="user"
  :error="errors.email"
  required
/>

<!-- Password con toggle -->
<Input
  v-model="password"
  label="Contraseña"
  type="password"
  icon="lock"
/>
```

**Props:**
| Prop | Tipo | Default |
|------|------|---------|
| modelValue | String/Number | - |
| label | String | - |
| placeholder | String | - |
| type | String | 'text' |
| icon | String | - |
| hint | String | - |
| error | String | - |
| disabled | Boolean | false |
| required | Boolean | false |
| clearable | Boolean | false |

### Cards (`Card.vue`)

```vue
<Card 
  title="Oportunidades Recientes" 
  subtitle="Últimas actualizaciones"
  icon="trending-up"
>
  <template #actions>
    <Button variant="ghost" size="sm">Ver todas</Button>
  </template>
  
  <!-- Contenido -->
  <div>...</div>
  
  <template #footer>
    <span>Pie de card</span>
  </template>
</Card>
```

### Stat Cards (`StatCard.vue`)

```vue
<StatCard
  icon="wallet"
  :value="450000000"
  label="Valor en Pipeline"
  trend="+12.5%"
  trend-up
  format="currency"
  color="primary"
/>
```

**Props:**
| Prop | Tipo | Default | Opciones |
|------|------|---------|----------|
| icon | String | required | Nombre del icono |
| value | String/Number | required | Valor a mostrar |
| label | String | required | Etiqueta |
| trend | String | - | Texto de tendencia |
| trendUp | Boolean | false | Tendencia positiva |
| trendDown | Boolean | false | Tendencia negativa |
| format | String | 'number' | number, currency, percent |
| color | String | 'primary' | primary, secondary, success, warning, error |

---

## Guía de Desarrollo Frontend

### ⚠️ Reglas Obligatorias

#### 1. NO usar fuentes de iconos externas
```vue
<!-- ❌ INCORRECTO -->
<span class="material-icons">dashboard</span>

<!-- ✅ CORRECTO -->
<Icon name="dashboard" />
```

#### 2. NO usar colores hardcodeados
```css
/* ❌ INCORRECTO */
.element { color: #00d4ff; }

/* ✅ CORRECTO */
.element { color: var(--primary); }
```

#### 3. NO usar tipografía genérica
```css
/* ❌ INCORRECTO */
font-family: Arial, sans-serif;
font-family: 'Inter', sans-serif;
font-family: 'Roboto', sans-serif;

/* ✅ CORRECTO */
font-family: var(--font-display);
font-family: var(--font-body);
```

#### 4. Usar componentes UI existentes
```vue
<!-- ❌ INCORRECTO - crear botón desde cero -->
<button class="my-button">Click</button>

<!-- ✅ CORRECTO - usar componente -->
<Button variant="primary">Click</Button>
```

#### 5. Aplicar animaciones de entrada
```vue
<!-- En elementos que aparecen -->
<div class="animate-slideUp" style="animation-delay: 100ms">
  Contenido animado
</div>
```

### Clases de Animación Disponibles

```css
.animate-fadeIn    /* Fade in */
.animate-slideUp   /* Slide desde abajo */
.animate-slideIn   /* Slide desde izquierda */
.animate-scaleIn   /* Scale con bounce */
.animate-pulse     /* Pulso continuo */
.animate-spin      /* Rotación continua */

/* Delays */
.delay-1 { animation-delay: 50ms; }
.delay-2 { animation-delay: 100ms; }
.delay-3 { animation-delay: 150ms; }
.delay-4 { animation-delay: 200ms; }
.delay-5 { animation-delay: 250ms; }
.delay-6 { animation-delay: 300ms; }
```

### Clases Utilitarias

```css
/* Glass effect */
.glass { 
  background: var(--glass-bg);
  border: 1px solid var(--glass-border);
  backdrop-filter: blur(20px);
}

/* Gradient text */
.gradient-text {
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

/* Glow effect */
.glow { box-shadow: var(--shadow-glow-sm); }
.glow-hover:hover { box-shadow: var(--shadow-glow); }
```

### Estructura de una Nueva Vista

```vue
<template>
  <Layout>
    <div class="my-view">
      <!-- Header section con animación -->
      <section class="my-view__header animate-slideUp">
        <h2>Título</h2>
        <Button variant="primary" icon="plus">Acción</Button>
      </section>

      <!-- Content con delay -->
      <section class="my-view__content animate-slideUp delay-2">
        <Card title="Card Title" icon="business">
          <!-- contenido -->
        </Card>
      </section>
    </div>
  </Layout>
</template>

<script setup>
import Layout from '@/components/layout/Layout.vue';
import Card from '@/components/ui/Card.vue';
import Button from '@/components/ui/Button.vue';
// ...
</script>

<style scoped>
.my-view {
  display: flex;
  flex-direction: column;
  gap: var(--space-6);
}

.my-view__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
```

### Agregar Nuevos Iconos

Para agregar un nuevo icono, editar `src/components/ui/Icon.vue`:

```javascript
const icons = {
  // ... iconos existentes
  
  'nuevo-icono': () => h('g', [
    h('path', { 
      d: 'M...', // SVG path
      stroke: 'currentColor', 
      'stroke-width': '1.5',
      'stroke-linecap': 'round'
    })
  ]),
};
```

---

## API Backend

### Base URL
```
http://localhost:8080/api/v1
```

### Autenticación
```bash
POST /auth/login
{
  "username": "admin",
  "password": "admin123"
}

# Response
{
  "token": "eyJhbG...",
  "tipo": "Bearer"
}
```

### Headers Requeridos
```
Authorization: Bearer {token}
Content-Type: application/json
```

### Endpoints Principales

| Módulo | Endpoints |
|--------|-----------|
| Auth | POST /login, GET /me |
| Empresas | GET, POST, PUT /empresas |
| Personas | GET, POST, PUT /personas |
| Pipelines | GET, POST, PUT /pipelines |
| Oportunidades | GET, POST, PUT, /mover-etapa, /cerrar |
| Actividades | GET, POST, PUT /actividades |
| Compromisos | GET, POST, PUT /compromisos |
| Documentos | GET, POST, DELETE /documentos |

---

## Reglas de Negocio

Ver documento completo: `docs/B-04-Matriz-Reglas-Negocio.md`

### Reglas Clave para Frontend

| ID | Regla | Impacto UI |
|----|-------|------------|
| RB-01 | Oportunidad: 1 pipeline, 1 etapa | Filtrar etapas por pipeline |
| RB-04 | Motivo obligatorio si PERDIDA | Modal cierre condicional |
| RB-14 | Pipeline inmutable post-creación | Campo disabled en edición |
| RB-15 | Etapa debe ser del mismo pipeline | Validar en drag & drop |
| RB-19 | Oportunidad cerrada inmutable | UI solo lectura |

---

## Credenciales de Prueba

| Usuario | Contraseña | Rol |
|---------|------------|-----|
| admin | admin123 | Administrador |

---

## Changelog

### v0.1.0 (2026-02-24)
- ✅ Backend: 42 endpoints funcionales
- ✅ Frontend: Sistema de diseño "Luxury Tech"
- ✅ Login y Dashboard implementados
- ✅ Iconos SVG personalizados
- 🚧 Pipeline Kanban (pendiente)
- 🚧 CRUD Empresas (pendiente)
- 🚧 CRUD Personas (pendiente)

---

## Licencia

Proyecto privado - ArquitecSoft © 2026
