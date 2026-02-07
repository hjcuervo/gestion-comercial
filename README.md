# Plataforma de Gestión Comercial - Arquitecsoft

## 📋 Descripción

Plataforma interna de Arquitecsoft para la gestión del ciclo de vida de oportunidades comerciales, desde la prospección hasta el cierre. Sistema de **pipelines dinámicos** donde las oportunidades avanzan por etapas configurables, con registro de actividades, compromisos y documentos asociados.

---

## 🎯 Alcance v0.1

### Incluye
- **Empresas**: Prospectos, clientes y aliados
- **Personas**: Contactos con relaciones multi-empresa
- **Oportunidades comerciales**: Objeto central del proceso
- **Pipelines dinámicos**: Configurables sin código
- **Actividades y compromisos**: Registro de interacciones
- **Documentos**: Metadatos (archivos en OCI Object Storage)
- **KPIs base**: Oportunidades por etapa, tasas de conversión
- **Módulo de usuarios**: Autenticación JWT + RBAC

### Excluye (v0.1)
- Pipeline de contratación
- Gestión contractual
- EMI y facturación

---

## 🛠️ Stack Tecnológico

### Backend
| Tecnología | Versión |
|------------|---------|
| Java | 21 |
| Spring Boot | 3.3.0 |
| Spring Security + JWT | - |
| Spring Data JPA | - |
| Oracle Database | 23c |
| Flyway | - |

### Frontend
| Tecnología | Versión |
|------------|---------|
| Vue | 3.x |
| Vite | - |
| Vue Router | - |
| Pinia | - |

### Infraestructura
- Oracle Cloud Infrastructure (OCI)
- OCI Object Storage (documentos)
- Docker / Docker Compose

---

## 🎨 Estándar Visual: Material Design 3

Este proyecto implementa **Material Design 3 (Material You)** como estándar visual obligatorio para todas las aplicaciones SPA internas de Arquitecsoft.

### Principios MD3

#### 1. Design Tokens
Sistema de tokens de diseño que definen:
- **Colores**: Paletas tonales (Primary, Secondary, Tertiary, Error, Neutral)
- **Tipografía**: Type scale (Display, Headline, Title, Body, Label)
- **Formas**: Border radius (none, extra-small, small, medium, large, extra-large, full)
- **Elevación**: 6 niveles con shadows y surface tints
- **Espaciado**: Múltiplos de 4dp

#### 2. Componentes UI Kit
Componentes obligatorios del sistema:
- **Buttons**: Filled, Tonal, Outlined, Text, Elevated
- **Text Fields**: Filled (default), Outlined
- **Cards**: Elevated, Filled, Outlined
- **Chips**: Filter, Input, Assist, Suggestion
- **Dialogs**: Basic, Full-screen
- **Data Tables**: Con sorting, selection, pagination
- **Navigation**: Rail (desktop), Drawer (mobile)

#### 3. App Shell
```
┌─────────────────────────────────────────────────┐
│  Top App Bar (64dp)                             │
├────────┬────────────────────────────────────────┤
│  Nav   │                                        │
│  Rail  │         Content Area                   │
│ (80dp) │                                        │
│        │                                        │
└────────┴────────────────────────────────────────┘
```

#### 4. Responsive Breakpoints
| Breakpoint | Rango | Navegación | Columnas |
|------------|-------|------------|----------|
| Compact | 0-599dp | Drawer | 4 |
| Medium | 600-839dp | Rail 80dp | 8 |
| Expanded | 840dp+ | Rail 80dp | 12 |

#### 5. Light/Dark Mode
- Soporte obligatorio para ambos modos
- Tokens de color adaptados automáticamente
- Contraste mínimo WCAG AA (4.5:1 texto, 3:1 componentes)

#### 6. Accesibilidad
- Focus visible en todos los elementos interactivos
- Touch targets mínimo 48x48dp
- Labels ARIA obligatorios
- Navegación completa por teclado

### Documentación MD3
| Documento | Descripción |
|-----------|-------------|
| `A-01-Estandar-Visual-MD3.md` | Patrones visuales y layouts |
| `A-02-Design-Tokens-MD3.md` | Especificación de tokens |
| `A-03-UI-Kit-MD3.md` | Catálogo de componentes |
| `A-04-Estructura-Tecnica-Frontend.md` | Arquitectura frontend |
| `A-05-Checklist-Validacion-MD3.md` | Checklist de validación |

### Prohibiciones
- ❌ Bootstrap, Vuetify, Ant Design, PrimeVue
- ❌ Colores hardcoded (usar tokens)
- ❌ Tamaños en px sin tokens
- ❌ Componentes custom que dupliquen MD3
- ❌ Librerías externas de modales/toasts

---

## 📁 Estructura del Proyecto

```
gestion-comercial/
├── backend/
│   ├── src/main/java/com/arquitecsoft/gestion/
│   │   ├── domain/
│   │   │   ├── auth/          # Autenticación
│   │   │   ├── usuario/       # Usuarios del sistema
│   │   │   ├── empresa/       # Empresas
│   │   │   ├── persona/       # Personas de contacto
│   │   │   ├── oportunidad/   # Oportunidades
│   │   │   ├── pipeline/      # Pipelines y etapas
│   │   │   ├── actividad/     # Actividades
│   │   │   └── documento/     # Documentos
│   │   └── infrastructure/
│   │       ├── security/      # JWT, filtros
│   │       └── exception/     # Manejo de errores
│   └── src/main/resources/
│       └── application.yml
├── frontend/                   # (Pendiente)
├── docs/                       # Documentación
└── docker-compose.yml
```

---

## 🚀 Inicio Rápido

### Requisitos
- Java 21
- Maven 3.9+
- Oracle Database (o acceso a instancia remota)

### Backend

```bash
cd backend

# Configurar variables (o editar application.yml)
export DB_URL=jdbc:oracle:thin:@host:port/service
export DB_USERNAME=usuario
export DB_PASSWORD=password
export JWT_SECRET=clave-secreta-minimo-64-caracteres

# Compilar
./mvnw clean package -DskipTests

# Ejecutar
java -jar target/gestion-comercial-0.1.0.jar
```

### Verificar funcionamiento

```bash
# Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Endpoint protegido (usar token del login)
curl -H "Authorization: Bearer <TOKEN>" \
  http://localhost:8080/api/v1/auth/me
```

---

## 📊 Modelo de Datos

### Entidades Principales
| Entidad | Descripción |
|---------|-------------|
| `GC_USUARIO` | Usuarios del sistema |
| `GC_EMPRESA` | Empresas (prospectos/clientes/aliados) |
| `GC_PERSONA` | Personas de contacto |
| `GC_PIPELINE` | Pipelines configurables |
| `GC_ETAPA` | Etapas de pipeline |
| `GC_OPORTUNIDAD` | Oportunidades comerciales |
| `GC_ACTIVIDAD` | Actividades registradas |
| `GC_COMPROMISO` | Compromisos derivados |
| `GC_DOCUMENTO` | Metadatos de documentos |

### Relaciones N:M
- `GC_PERSONA_EMPRESA`: Persona ↔ Empresa
- `GC_OPORTUNIDAD_RESPONSABLE`: Oportunidad ↔ Usuario/Persona
- `GC_OPORTUNIDAD_CONTACTO`: Oportunidad ↔ PersonaEmpresa
- `GC_ACTIVIDAD_PARTICIPANTE`: Actividad ↔ Usuario/Persona
- `GC_COMPROMISO_RESPONSABLE`: Compromiso ↔ Usuario/Persona

---

## 🔐 API REST

Base URL: `/api/v1`

### Autenticación
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/auth/login` | Iniciar sesión |
| GET | `/auth/me` | Usuario actual |

### Recursos (en desarrollo)
- `/empresas` - CRUD de empresas
- `/personas` - CRUD de personas
- `/pipelines` - Configuración de pipelines
- `/oportunidades` - Gestión de oportunidades
- `/actividades` - Registro de actividades
- `/compromisos` - Gestión de compromisos
- `/documentos` - Gestión de documentos
- `/catalogos` - Catálogos del sistema

### Formato de Errores
```json
{
  "code": "VALIDATION_ERROR",
  "message": "Error de validación",
  "fieldErrors": [
    {"field": "nombre", "message": "El campo es requerido"}
  ],
  "timestamp": "2026-02-07T12:00:00"
}
```

---

## 📝 Reglas de Negocio

| Código | Regla |
|--------|-------|
| RB-01 | Una oportunidad pertenece a un único pipeline y etapa |
| RB-02 | Pipelines y etapas son configurables sin límite |
| RB-03 | Movimientos entre etapas registran auditoría |
| RB-04 | Cierre PERDIDA/NO_CONCRETADA requiere motivo |
| RB-05 | Modo guía por defecto (permite saltar etapas) |
| RB-06 | Toda acción relevante queda auditada |

---

## 👥 Roles

| Rol | Descripción |
|-----|-------------|
| `ADMIN` | Administrador del sistema |
| `COMERCIAL` | Usuario comercial |
| `LECTURA_KPI` | Solo lectura de reportes |

---

## 📚 Documentación Adicional

- `docs/B-01-Modelo-Logico-Datos.md` - Modelo lógico
- `docs/B-02-Modelo-Fisico-DDL.md` - Scripts DDL Oracle
- `docs/B-03-Contrato-API-OpenAPI.md` - Especificación OpenAPI
- `docs/B-04-Matriz-Reglas-Negocio.md` - Matriz de validaciones

---

## 📄 Licencia

Uso interno - Propiedad de Arquitecsoft - Confidencial

---

*Última actualización: Febrero 2026*
