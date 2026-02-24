# Resumen de Contexto y Tareas Pendientes
## Sistema de Gestión Comercial - Frontend Vue 3

**Fecha:** 2026-02-18  
**Próxima Sesión:** Continuación Frontend  
**Repositorio:** https://github.com/hjcuervo/gestion-comercial

---

## 1. Estado Actual del Proyecto

### Backend (Spring Boot) ✅ Completado
- **42 endpoints** funcionales
- Autenticación JWT implementada
- CORS configurado para frontend
- Base de datos Oracle 23c conectada

| Módulo | Endpoints | Estado |
|--------|-----------|--------|
| Auth | 2 | ✅ |
| Empresas | 4 | ✅ |
| Personas | 5 | ✅ |
| Pipelines/Etapas | 8 | ✅ |
| Oportunidades | 6 | ✅ |
| Actividades | 4 | ✅ |
| Compromisos | 7 | ✅ |
| Documentos | 6 | ✅ |

### Frontend (Vue 3) 🚧 En Progreso
- Proyecto Vite + Vue 3 configurado
- **Diseño Aurora Dark** implementado
- Login y Dashboard funcionando
- **Pendiente:** Corregir íconos Material Icons

| Componente | Estado |
|------------|--------|
| Sistema de diseño Aurora | ✅ |
| Tokens CSS | ✅ |
| Login | ✅ |
| Dashboard | ✅ |
| Sidebar | ✅ |
| Header | ✅ |
| Íconos | ⚠️ Fix pendiente |
| Pipeline Kanban | 🚧 Pendiente |
| CRUD Empresas | 🚧 Pendiente |
| CRUD Personas | 🚧 Pendiente |

---

## 2. Tareas Pendientes (Próxima Sesión)

### 2.1 Fix Íconos Material Icons (Prioridad Alta)
**Problema:** Los íconos muestran caracteres extraños ("ó") en lugar de gráficos.

**Solución:**
- Cambiar de `Material Icons Round` a `Material Icons` estándar
- Actualizar `global.css` con la fuente correcta
- Verificar que todos los componentes usen la clase `.material-icons`

**Archivos a modificar:**
```
frontend/src/assets/styles/global.css
frontend/src/components/**/*.vue (verificar clases)
```

### 2.2 Vista Pipeline Kanban (Prioridad Alta)
**Descripción:** Tablero Kanban para visualizar y mover oportunidades entre etapas.

**Funcionalidades:**
- Columnas dinámicas según etapas del pipeline
- Cards de oportunidad con drag & drop
- Filtros por pipeline, empresa, valor
- Quick actions (ver detalle, editar, cerrar)

**Reglas de negocio aplicables:**
| Regla | Validación |
|-------|------------|
| RB-01 | Etapa debe pertenecer al pipeline |
| RB-15 | Solo mover entre etapas del mismo pipeline |
| RB-19 | Oportunidades cerradas no se pueden mover |
| RB-05 | Modo guía permite saltar etapas |

**Endpoints a consumir:**
```
GET  /api/v1/pipelines/activos
GET  /api/v1/pipelines/{id}  (incluye etapas)
GET  /api/v1/oportunidades?pipeline_id={id}
POST /api/v1/oportunidades/{id}/mover-etapa
```

**Componentes a crear:**
```
src/views/PipelineView.vue
src/components/pipeline/KanbanBoard.vue
src/components/pipeline/KanbanColumn.vue
src/components/pipeline/KanbanCard.vue
src/components/pipeline/PipelineSelector.vue
```

### 2.3 CRUD Empresas (Prioridad Media)
**Descripción:** Listado, creación, edición de empresas.

**Funcionalidades:**
- Tabla con búsqueda y paginación
- Modal/drawer para crear/editar
- Filtros por tipo y estado
- Ver oportunidades asociadas

**Endpoints a consumir:**
```
GET    /api/v1/empresas?q={search}&tipo={tipo}&page={n}
GET    /api/v1/empresas/{id}
POST   /api/v1/empresas
PUT    /api/v1/empresas/{id}
```

**Componentes a crear:**
```
src/views/EmpresasView.vue
src/components/empresas/EmpresasList.vue
src/components/empresas/EmpresaForm.vue
src/components/empresas/EmpresaCard.vue
```

### 2.4 CRUD Personas (Prioridad Media)
**Descripción:** Gestión de contactos y su relación con empresas.

**Reglas de negocio aplicables:**
| Regla | Validación |
|-------|------------|
| RB-09 | Una persona puede asociarse a múltiples empresas |

**Endpoints a consumir:**
```
GET    /api/v1/personas?q={search}&empresa_id={id}
GET    /api/v1/personas/{id}
POST   /api/v1/personas
PUT    /api/v1/personas/{id}
POST   /api/v1/personas/{id}/empresas
```

---

## 3. Reglas de Negocio Clave (B-04)

### 3.1 Reglas de Bloqueo (UI debe prevenir/validar)

| RB-ID | Descripción | Acción en UI |
|-------|-------------|--------------|
| **RB-01** | Oportunidad: 1 pipeline, 1 etapa | Filtrar etapas por pipeline seleccionado |
| **RB-04** | Motivo obligatorio si PERDIDA/NO_CONCRETADA | Modal de cierre con campo condicional |
| **RB-14** | Pipeline inmutable después de crear | Deshabilitar campo en edición |
| **RB-15** | Etapa debe ser del mismo pipeline | Validar en drag & drop |
| **RB-19** | Oportunidad cerrada es inmutable | UI en modo solo lectura |
| **RB-20** | Estado inicial siempre ABIERTA | No mostrar selector en creación |

### 3.2 Reglas de Guía (UI puede advertir)

| RB-ID | Descripción | Acción en UI |
|-------|-------------|--------------|
| **RB-05** | Modo guía permite saltar etapas | Permitir drag a cualquier columna |
| **RB-09** | Persona multi-empresa | UI para agregar múltiples asociaciones |

### 3.3 Códigos de Error del Backend

| Código | HTTP | Mensaje | Cuándo mostrar |
|--------|------|---------|----------------|
| `INVALID_STAGE` | 400 | Etapa no pertenece al pipeline | Mover etapa inválida |
| `CLOSE_REASON_REQUIRED` | 400 | Motivo de cierre obligatorio | Cerrar sin motivo |
| `OPPORTUNITY_CLOSED` | 409 | Oportunidad cerrada | Editar oportunidad cerrada |
| `PIPELINE_IMMUTABLE` | 400 | No se puede cambiar pipeline | Intentar cambiar pipeline |

---

## 4. Arquitectura Frontend

### 4.1 Estructura de Carpetas
```
frontend/src/
├── assets/
│   └── styles/
│       ├── tokens.css          # Variables Aurora Dark
│       └── global.css          # Estilos globales
├── components/
│   ├── layout/
│   │   ├── AuroraLayout.vue
│   │   ├── AuroraSidebar.vue
│   │   └── AuroraHeader.vue
│   ├── ui/                     # Componentes reutilizables
│   │   ├── AuroraButton.vue
│   │   ├── AuroraInput.vue
│   │   ├── AuroraCard.vue
│   │   ├── AuroraStatCard.vue
│   │   ├── AuroraTable.vue     # 🚧 Por crear
│   │   ├── AuroraModal.vue     # 🚧 Por crear
│   │   └── AuroraBadge.vue     # 🚧 Por crear
│   ├── pipeline/               # 🚧 Por crear
│   │   ├── KanbanBoard.vue
│   │   ├── KanbanColumn.vue
│   │   └── KanbanCard.vue
│   └── empresas/               # 🚧 Por crear
│       ├── EmpresasList.vue
│       └── EmpresaForm.vue
├── views/
│   ├── LoginView.vue           # ✅
│   ├── DashboardView.vue       # ✅
│   ├── PipelineView.vue        # 🚧 Placeholder
│   ├── EmpresasView.vue        # 🚧 Placeholder
│   └── PersonasView.vue        # 🚧 Placeholder
├── services/
│   ├── api.js                  # ✅ Axios configurado
│   ├── auth.service.js         # ✅
│   ├── empresa.service.js      # 🚧 Por crear
│   ├── persona.service.js      # 🚧 Por crear
│   ├── pipeline.service.js     # 🚧 Por crear
│   └── oportunidad.service.js  # 🚧 Por crear
├── stores/
│   ├── auth.store.js           # ✅
│   └── pipeline.store.js       # 🚧 Por crear
├── router/
│   └── index.js                # ✅
├── App.vue                     # ✅
└── main.js                     # ✅
```

### 4.2 Servicios API a Crear

```javascript
// empresa.service.js
export const empresaService = {
  getAll(params),      // GET /empresas
  getById(id),         // GET /empresas/{id}
  create(data),        // POST /empresas
  update(id, data),    // PUT /empresas/{id}
}

// pipeline.service.js
export const pipelineService = {
  getActivos(),        // GET /pipelines/activos
  getById(id),         // GET /pipelines/{id}
}

// oportunidad.service.js
export const oportunidadService = {
  getAll(params),      // GET /oportunidades
  getById(id),         // GET /oportunidades/{id}
  create(data),        // POST /oportunidades
  update(id, data),    // PUT /oportunidades/{id}
  moverEtapa(id, data),// POST /oportunidades/{id}/mover-etapa
  cerrar(id, data),    // POST /oportunidades/{id}/cerrar
}
```

---

## 5. Diseño Aurora Dark - Referencia

### 5.1 Colores Principales
```css
--aurora-bg-base: #0c0c1e;
--aurora-bg-elevated: #12122a;
--aurora-primary: #7c3aed;
--aurora-secondary: #2dd4bf;
--aurora-success: #22c55e;
--aurora-warning: #eab308;
--aurora-error: #ef4444;
```

### 5.2 Componentes Disponibles

| Componente | Props Principales |
|------------|-------------------|
| `AuroraButton` | variant, size, icon, loading, disabled |
| `AuroraInput` | label, icon, error, clearable |
| `AuroraCard` | title, subtitle, icon, variant |
| `AuroraStatCard` | icon, value, label, trend, color |

### 5.3 Clases de Utilidad
```css
.animate-slideUp    /* Animación entrada desde abajo */
.animate-fadeIn     /* Animación fade */
.text-primary       /* Color texto principal */
.text-secondary     /* Color texto secundario */
.flex, .grid        /* Layout */
.gap-4, .p-4        /* Espaciado */
```

---

## 6. Comandos Útiles

```bash
# Frontend - Desarrollo
cd frontend
npm run dev

# Frontend - Build
npm run build

# Backend - Compilar
cd backend
./mvnw clean package -DskipTests

# Backend - Ejecutar
java -jar target/gestion-comercial-0.1.0.jar

# Git - Commit
git add .
git commit -m "mensaje"
git push origin main
```

---

## 7. Credenciales de Prueba

| Usuario | Contraseña | Rol |
|---------|------------|-----|
| admin | admin123 | Administrador |

---

## 8. URLs de Desarrollo

| Servicio | URL |
|----------|-----|
| Frontend | http://localhost:5173 |
| Backend API | http://localhost:8080/api/v1 |
| Login | POST /api/v1/auth/login |

---

## 9. Checklist Próxima Sesión

- [ ] Fix íconos Material Icons
- [ ] Crear servicios API (empresa, pipeline, oportunidad)
- [ ] Crear componentes UI faltantes (Table, Modal, Badge)
- [ ] Implementar vista Pipeline Kanban
- [ ] Implementar CRUD Empresas
- [ ] Probar flujo completo de oportunidades
- [ ] Commit y push del avance

---

*Documento generado: 2026-02-18*
*Última actualización: Sesión de implementación Frontend*
