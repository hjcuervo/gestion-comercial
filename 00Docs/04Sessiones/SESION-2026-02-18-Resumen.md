# Resumen de Sesión - 2026-02-18

## Proyecto: Sistema de Gestión Comercial (CRM)

### Contexto
Continuación del desarrollo del backend Spring Boot para el sistema de gestión comercial. Esta sesión se enfocó en completar los CRUDs restantes del backend.

---

## Tareas Completadas

### I-05: CRUD Pipelines y Etapas ✅
**Archivos creados:**
- `domain/pipeline/entity/GcPipeline.java`
- `domain/pipeline/entity/GcEtapa.java`
- `domain/pipeline/repository/GcPipelineRepository.java`
- `domain/pipeline/repository/GcEtapaRepository.java`
- `domain/pipeline/service/PipelineService.java`
- `domain/pipeline/controller/PipelineController.java`
- `domain/pipeline/dto/` (6 DTOs)

**Endpoints:**
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/v1/pipelines` | Listar con filtros |
| GET | `/api/v1/pipelines/activos` | Listar solo activos |
| GET | `/api/v1/pipelines/{id}` | Obtener con etapas |
| POST | `/api/v1/pipelines` | Crear pipeline |
| PUT | `/api/v1/pipelines/{id}` | Actualizar pipeline |
| GET | `/api/v1/pipelines/{id}/etapas` | Listar etapas |
| POST | `/api/v1/pipelines/{id}/etapas` | Crear etapa |
| PUT | `/api/v1/pipelines/{id}/etapas/{etapaId}` | Actualizar etapa |

**Nota:** Se ajustaron las entidades para coincidir con el esquema real de BD (estado como ENUM en lugar de activo booleano).

---

### I-06: CRUD Oportunidades ✅
**Archivos creados:**
- `domain/oportunidad/entity/GcOportunidad.java`
- `domain/oportunidad/repository/GcOportunidadRepository.java`
- `domain/oportunidad/service/OportunidadService.java`
- `domain/oportunidad/controller/OportunidadController.java`
- `domain/oportunidad/dto/` (5 DTOs)

**Endpoints:**
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/v1/oportunidades` | Listar con filtros |
| GET | `/api/v1/oportunidades/{id}` | Obtener por ID |
| POST | `/api/v1/oportunidades` | Crear oportunidad |
| PUT | `/api/v1/oportunidades/{id}` | Actualizar oportunidad |
| POST | `/api/v1/oportunidades/{id}/mover-etapa` | Cambiar etapa |
| POST | `/api/v1/oportunidades/{id}/cerrar` | Cerrar (GANADA/PERDIDA/NO_CONCRETADA) |

**Características:**
- Estados macro: ABIERTA, SEGUIMIENTO, GANADA, PERDIDA, NO_CONCRETADA
- Validación de motivo de cierre para PERDIDA/NO_CONCRETADA
- Actualización automática de probabilidad al mover etapa

---

### I-07: CRUD Actividades y Compromisos ✅
**Archivos creados:**
- `domain/actividad/entity/GcActividad.java`
- `domain/actividad/entity/GcCompromiso.java`
- `domain/actividad/repository/GcActividadRepository.java`
- `domain/actividad/repository/GcCompromisoRepository.java`
- `domain/actividad/service/ActividadService.java`
- `domain/actividad/controller/ActividadController.java`
- `domain/actividad/dto/` (6 DTOs)

**Endpoints Actividades:**
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/v1/actividades` | Listar con filtros |
| GET | `/api/v1/actividades/{id}` | Obtener por ID |
| GET | `/api/v1/actividades/oportunidad/{id}` | Por oportunidad |
| POST | `/api/v1/actividades` | Crear actividad |

**Endpoints Compromisos:**
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/v1/compromisos` | Listar con filtros |
| GET | `/api/v1/compromisos/{id}` | Obtener por ID |
| GET | `/api/v1/compromisos/oportunidad/{id}` | Por oportunidad |
| GET | `/api/v1/compromisos/pendientes` | Solo pendientes |
| POST | `/api/v1/compromisos` | Crear compromiso |
| PUT | `/api/v1/compromisos/{id}` | Actualizar |
| POST | `/api/v1/compromisos/{id}/completar` | Marcar completado |

---

### I-08: CRUD Documentos ✅
**Archivos creados:**
- `domain/documento/entity/GcDocumento.java`
- `domain/documento/repository/GcDocumentoRepository.java`
- `domain/documento/service/DocumentoService.java`
- `domain/documento/controller/DocumentoController.java`
- `domain/documento/dto/` (2 DTOs)

**Endpoints:**
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/v1/documentos` | Listar con filtros |
| GET | `/api/v1/documentos/{id}` | Obtener por ID |
| GET | `/api/v1/documentos/oportunidad/{id}` | Por oportunidad |
| GET | `/api/v1/documentos/actividad/{id}` | Por actividad |
| POST | `/api/v1/documentos` | Crear metadata |
| DELETE | `/api/v1/documentos/{id}` | Eliminar |

**Nota:** Solo guarda metadata. El archivo físico se almacena en OCI Object Storage.

---

## Estado Actual del Proyecto

### Backend (Spring Boot 3.3 + Java 21)

| Módulo | Estado | Endpoints |
|--------|--------|-----------|
| Auth (JWT) | ✅ | 2 |
| Empresas | ✅ | 4 |
| Personas | ✅ | 5 |
| Pipelines/Etapas | ✅ | 8 |
| Oportunidades | ✅ | 6 |
| Actividades | ✅ | 4 |
| Compromisos | ✅ | 7 |
| Documentos | ✅ | 6 |
| **Total** | | **42 endpoints** |

### Estructura del Proyecto
```
backend/
├── src/main/java/com/arquitecsoft/gestion/
│   ├── domain/
│   │   ├── actividad/      (entity, repository, service, controller, dto)
│   │   ├── auth/           (controller, dto, service)
│   │   ├── documento/      (entity, repository, service, controller, dto)
│   │   ├── empresa/        (entity, repository, service, controller, dto)
│   │   ├── oportunidad/    (entity, repository, service, controller, dto)
│   │   ├── persona/        (entity, repository, service, controller, dto)
│   │   ├── pipeline/       (entity, repository, service, controller, dto)
│   │   └── usuario/        (entity, repository)
│   └── infrastructure/
│       ├── dto/            (PageResponse)
│       ├── exception/      (BusinessException, GlobalExceptionHandler)
│       └── security/       (JWT, SecurityConfig, SecurityUtils)
├── src/main/resources/
│   └── application.yml
└── pom.xml
```

### Base de Datos (Oracle 23c)
- **17 tablas** creadas con DDL
- Esquema: `ARQGCDEV`
- Conexión configurada en `application.yml`

---

## Commits Realizados

1. **Commit anterior (sesión previa):**
   ```
   feat: implementación base plataforma gestión comercial
   - Auth JWT, Empresas, Personas
   ```

2. **Commit actual (pendiente):**
   ```
   feat: CRUD Pipelines, Oportunidades, Actividades, Documentos (I-05 a I-08)
   ```

---

## Próximos Pasos

### Backend (Opcional)
- [ ] CRUD Catálogos (tipos de actividad, tipos de documento, motivos de cierre)
- [ ] Reportes y Dashboard endpoints
- [ ] Integración con OCI Object Storage para upload real

### Frontend (Pendiente)
- [ ] Proyecto Vue 3 + Vite
- [ ] Implementación Material Design 3
- [ ] Módulos: Login, Dashboard, Empresas, Personas, Pipeline (Kanban), Oportunidades

---

## Tecnologías Utilizadas

| Capa | Tecnología |
|------|------------|
| Backend | Java 21, Spring Boot 3.3, Spring Security, JWT |
| Base de Datos | Oracle 23c |
| ORM | JPA/Hibernate |
| Build | Maven |
| Documentación | OpenAPI/Swagger |
| Control de Versiones | Git/GitHub |

---

## Configuración para Desarrollo

```bash
# Compilar
./mvnw clean package -DskipTests

# Ejecutar
java -jar target/gestion-comercial-0.1.0.jar

# Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

---

## Notas Importantes

1. **Oracle + JPA:** Usar `Integer` (0/1) en lugar de `Boolean` para campos NUMBER(1)
2. **Estados:** Usar ENUMs (ACTIVO/INACTIVO, ABIERTA/CERRADA) en lugar de booleanos
3. **BCrypt en Oracle:** Usar prefijo `N'...'` en SQL para evitar corrupción de caracteres
4. **Encoding:** Evitar tildes en JSON desde curl en Windows (usar PowerShell o Postman)

---

*Documento generado: 2026-02-18*
