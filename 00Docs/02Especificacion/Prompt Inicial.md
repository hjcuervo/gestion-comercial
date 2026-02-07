Eres responsable técnico total de implementar la Fase v0.1 (Gestión Comercial) de la Plataforma de Gestión Comercial de Arquitecsoft.

### Restricciones obligatorias (NO negociables)
- Base de datos: Oracle Database
- Infraestructura: OCI
- Frontend: Vue 3 en arquitectura SPA
- Backend: API-only (NO renderiza HTML), recomendado FastAPI
- Arquitectura: monolito modular por dominios/procesos
- Auditoría obligatoria para transiciones y cambios relevantes
- Pipelines dinámicos: global vs por pipeline; modo guía vs modo bloqueo (según la especificación)
- La plataforma es de uso interno (no MVP), enfoque productivo

### Tu responsabilidad
Con base en la especificación funcional adjunta, debes:
1) Proponer el modelo físico Oracle (DDL completo con PK/FK/índices).
2) Proponer el contrato de API (OpenAPI/Swagger) con payloads y códigos de error estructurados.
3) Proponer la estructura del repositorio (backend + frontend) y módulos.
4) Implementar el backend y frontend respetando las reglas de negocio.
5) Incluir auditoría y RBAC (Admin / Comercial / Lectura-KPI).
6) Entregar un plan de despliegue en OCI (alto nivel) y configuración de entornos.

### Entregables mínimos antes del código
- DDL Oracle (tablas + constraints)
- OpenAPI
- ERD (puede ser Mermaid)
- Matriz: RB-XXX -> validaciones y en qué endpoints aplica
- Plan de implementación por iteraciones (orden recomendado)

### Regla de calidad
No inventes entidades o procesos fuera de la especificación. Si detectas ambigüedades, lista "Decisiones pendientes" y asume valores por defecto marcados explícitamente como supuestos.
