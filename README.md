# Sistema de Gestión Comercial (CRM)

Sistema integral para la gestión del proceso comercial de ArquitecSoft, incluyendo seguimiento de oportunidades, empresas, contactos y actividades.

## 🚀 Tecnologías

| Capa | Tecnología |
|------|------------|
| **Backend** | Java 21, Spring Boot 3.3 |
| **Seguridad** | Spring Security, JWT |
| **Base de Datos** | Oracle 23c |
| **ORM** | JPA/Hibernate |
| **Build** | Maven |
| **Frontend** | Vue 3, Vite, Material Design 3 *(pendiente)* |

## 📋 Módulos

### Backend API (Completado ✅)

| Módulo | Endpoints | Descripción |
|--------|-----------|-------------|
| **Auth** | 2 | Login JWT, usuario actual |
| **Empresas** | 4 | CRUD empresas/clientes |
| **Personas** | 5 | CRUD contactos + asociación empresa |
| **Pipelines** | 8 | Pipelines y etapas configurables |
| **Oportunidades** | 6 | CRUD + mover etapa + cerrar |
| **Actividades** | 4 | Registro de interacciones |
| **Compromisos** | 7 | Tareas y seguimientos |
| **Documentos** | 6 | Metadata de archivos |
| **Total** | **42** | |

### Frontend (Pendiente 🚧)

- Login y autenticación
- Dashboard con métricas
- Gestión de empresas y contactos
- Pipeline Kanban (drag & drop)
- Detalle de oportunidades

## 🏗️ Estructura del Proyecto

```
gestion-comercial/
├── backend/
│   ├── src/main/java/com/arquitecsoft/gestion/
│   │   ├── domain/
│   │   │   ├── actividad/
│   │   │   ├── auth/
│   │   │   ├── documento/
│   │   │   ├── empresa/
│   │   │   ├── oportunidad/
│   │   │   ├── persona/
│   │   │   ├── pipeline/
│   │   │   └── usuario/
│   │   └── infrastructure/
│   │       ├── dto/
│   │       ├── exception/
│   │       └── security/
│   ├── src/main/resources/
│   │   └── application.yml
│   └── pom.xml
├── frontend/                    # Pendiente
├── docs/
│   ├── A-01-Guia-Tokens-MD3.md
│   ├── A-02-UI-Kit-Componentes.md
│   ├── B-01-Modelo-Conceptual.md
│   ├── B-02-Modelo-Fisico-DDL.md
│   ├── B-03-Contrato-API-OpenAPI.md
│   └── B-04-Matriz-Reglas-Negocio.md
└── README.md
```

## ⚡ Quick Start

### Prerrequisitos

- Java 21+
- Maven 3.9+
- Oracle 23c (o conexión a instancia existente)

### Configuración

1. **Clonar repositorio:**
   ```bash
   git clone https://github.com/hjcuervo/gestion-comercial.git
   cd gestion-comercial/backend
   ```

2. **Configurar base de datos** en `src/main/resources/application.yml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:oracle:thin:@//localhost:1521/FREEPDB1
       username: ARQGCDEV
       password: tu_password
   ```

3. **Compilar:**
   ```bash
   ./mvnw clean package -DskipTests
   ```

4. **Ejecutar:**
   ```bash
   java -jar target/gestion-comercial-0.1.0.jar
   ```

5. **Probar:**
   ```bash
   # Login
   curl -X POST http://localhost:8080/api/v1/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username":"admin","password":"admin123"}'
   
   # Usar el token en requests
   curl -H "Authorization: Bearer <TOKEN>" \
     http://localhost:8080/api/v1/empresas
   ```

## 📡 API Endpoints

### Autenticación
```
POST /api/v1/auth/login          # Obtener JWT
GET  /api/v1/auth/me             # Usuario actual
```

### Empresas
```
GET    /api/v1/empresas          # Listar (filtros: q, tipo, estado)
GET    /api/v1/empresas/{id}     # Obtener por ID
POST   /api/v1/empresas          # Crear
PUT    /api/v1/empresas/{id}     # Actualizar
```

### Personas
```
GET    /api/v1/personas          # Listar (filtros: q, empresa_id)
GET    /api/v1/personas/{id}     # Obtener con empresas
POST   /api/v1/personas          # Crear
PUT    /api/v1/personas/{id}     # Actualizar
POST   /api/v1/personas/{id}/empresas  # Asociar a empresa
```

### Pipelines y Etapas
```
GET    /api/v1/pipelines         # Listar
GET    /api/v1/pipelines/activos # Solo activos
GET    /api/v1/pipelines/{id}    # Con etapas
POST   /api/v1/pipelines         # Crear
PUT    /api/v1/pipelines/{id}    # Actualizar

GET    /api/v1/pipelines/{id}/etapas           # Listar etapas
POST   /api/v1/pipelines/{id}/etapas           # Crear etapa
PUT    /api/v1/pipelines/{id}/etapas/{etapaId} # Actualizar etapa
```

### Oportunidades
```
GET    /api/v1/oportunidades     # Listar (filtros: empresa_id, pipeline_id, etapa_id, estado)
GET    /api/v1/oportunidades/{id}             # Obtener
POST   /api/v1/oportunidades                  # Crear
PUT    /api/v1/oportunidades/{id}             # Actualizar
POST   /api/v1/oportunidades/{id}/mover-etapa # Cambiar etapa
POST   /api/v1/oportunidades/{id}/cerrar      # Cerrar (GANADA/PERDIDA/NO_CONCRETADA)
```

### Actividades
```
GET    /api/v1/actividades                    # Listar
GET    /api/v1/actividades/{id}               # Obtener
GET    /api/v1/actividades/oportunidad/{id}   # Por oportunidad
POST   /api/v1/actividades                    # Crear
```

### Compromisos
```
GET    /api/v1/compromisos                    # Listar
GET    /api/v1/compromisos/{id}               # Obtener
GET    /api/v1/compromisos/oportunidad/{id}   # Por oportunidad
GET    /api/v1/compromisos/pendientes         # Solo pendientes
POST   /api/v1/compromisos                    # Crear
PUT    /api/v1/compromisos/{id}               # Actualizar
POST   /api/v1/compromisos/{id}/completar     # Marcar completado
```

### Documentos
```
GET    /api/v1/documentos                     # Listar
GET    /api/v1/documentos/{id}                # Obtener
GET    /api/v1/documentos/oportunidad/{id}    # Por oportunidad
GET    /api/v1/documentos/actividad/{id}      # Por actividad
POST   /api/v1/documentos                     # Crear metadata
DELETE /api/v1/documentos/{id}                # Eliminar
```

## 🗃️ Modelo de Datos

### Entidades Principales

```
GC_EMPRESA          # Empresas/Clientes
GC_PERSONA          # Contactos
GC_PERSONA_EMPRESA  # Relación N:M persona-empresa
GC_PIPELINE         # Pipelines configurables
GC_ETAPA            # Etapas de cada pipeline
GC_OPORTUNIDAD      # Oportunidades comerciales
GC_ACTIVIDAD        # Actividades realizadas
GC_COMPROMISO       # Tareas y compromisos
GC_DOCUMENTO        # Metadata de archivos
GC_CATALOGO         # Catálogos (tipos, motivos)
GC_USUARIO          # Usuarios del sistema
```

### Estados de Oportunidad

| Estado | Descripción |
|--------|-------------|
| `ABIERTA` | Nueva oportunidad |
| `SEGUIMIENTO` | En proceso activo |
| `GANADA` | Cerrada exitosamente |
| `PERDIDA` | Perdida ante competencia |
| `NO_CONCRETADA` | No se concretó el negocio |

## 🎨 Material Design 3

El frontend debe implementar MD3 siguiendo los documentos:
- `docs/A-01-Guia-Tokens-MD3.md` - Tokens de diseño
- `docs/A-02-UI-Kit-Componentes.md` - Componentes UI

**Colores principales:**
- Primary: `#1A73E8` (Azul corporativo)
- Secondary: `#34A853` (Verde éxito)
- Error: `#EA4335`

## 📝 Documentación

| Documento | Descripción |
|-----------|-------------|
| `B-01-Modelo-Conceptual.md` | Modelo entidad-relación |
| `B-02-Modelo-Fisico-DDL.md` | Scripts DDL Oracle |
| `B-03-Contrato-API-OpenAPI.md` | Especificación OpenAPI |
| `B-04-Matriz-Reglas-Negocio.md` | Reglas de validación |

## 🔒 Seguridad

- Autenticación JWT con expiración de 8 horas
- Endpoints protegidos (excepto `/api/v1/auth/**`)
- Passwords hasheados con BCrypt
- CORS configurado para desarrollo local

## 👥 Equipo

- **ArquitecSoft** - Desarrollo

## 📄 Licencia

Propietario - ArquitecSoft © 2026
