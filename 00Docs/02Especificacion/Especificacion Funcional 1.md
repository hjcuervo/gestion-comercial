# ESPECIFICACIÓN FUNCIONAL PARA DESARROLLO

## Plataforma de Gestión Comercial – Gestión Comercial (v0.1)

> **Documento Claude-ready**
> Uso interno – Propiedad de Arquitecsoft – Confidencial

---

## 1. Contexto y alcance

Este documento define los **requerimientos funcionales y técnicos** para la **fase de Gestión Comercial**, que constituye el **inicio del proceso transversal** de la Plataforma de Gestión Comercial de Arquitecsoft.

### Incluye

* Empresas (prospectos, clientes, aliados)
* Personas y relaciones multi-empresa
* Oportunidades comerciales
* Pipelines dinámicos configurables
* Actividades, compromisos y evidencias
* Documentos asociados
* Cierre de oportunidades
* KPIs base de gestión comercial

### Excluye (en esta versión)

* Pipeline de contratación
* Gestión contractual
* EMI y facturación

---

## 2. Stack tecnológico obligatorio

### Backend

* Python + FastAPI
* Oracle Database
* SQLAlchemy + Alembic (adaptado a Oracle)

### Frontend

* Vue 3
* Arquitectura SPA

### Infraestructura

* Oracle Cloud Infrastructure (OCI)
* OCI Object Storage para documentos

### Principios técnicos

* Arquitectura **monolito modular por dominios**
* APIs REST
* Auditoría obligatoria de eventos

## Criterios técnicos explícitos – Arquitectura SPA (Frontend / Backend)

### Frontend – Vue 3 (Single Page Application)

**FE-001 Separación de responsabilidades**
- El frontend **no renderiza** ni depende de páginas HTML generadas por el backend.
- El frontend consume exclusivamente **APIs REST**.
- El backend **no entrega vistas**, solo datos (JSON).

**FE-002 Navegación y estado**
- La navegación se gestiona con **Vue Router**.
- El estado global de la aplicación se gestiona con **Pinia** (o equivalente aprobado).

**FE-003 Comunicación con backend**
- Todas las operaciones de negocio (crear oportunidad, mover etapas, registrar actividades, adjuntar documentos, cerrar oportunidades) se realizan mediante llamadas HTTP a la API.
- El frontend maneja estados de carga y error, mostrando los mensajes retornados por la API.
- El frontend no implementa reglas de negocio críticas; solo validaciones de experiencia de usuario.

**FE-004 Autenticación**
- El frontend opera con autenticación basada en **tokens** (por ejemplo JWT).
- El frontend no almacena secretos; únicamente tokens de sesión según prácticas seguras.

**FE-005 UI orientada a flujos**
- La interfaz debe permitir visualizar y operar **flujos de proceso** (pipelines, etapas, timelines) sin recargar la página.
- El usuario navega el proceso, no pantallas rígidas.

---

### Backend – FastAPI + Oracle Database

**BE-001 Backend API-only**
- El backend expone exclusivamente **APIs REST**.
- No se renderizan vistas HTML ni plantillas de frontend.

**BE-002 Fuente de verdad**
- El backend es la **fuente de verdad** para:
  - reglas de negocio
  - validaciones
  - consistencia de datos
- El backend valida permisos (RBAC) y reglas del pipeline (incluyendo modo guía / modo bloqueo).

**BE-003 Auditoría obligatoria**
- Toda acción relevante debe quedar auditada:
  - fecha y hora
  - usuario
  - entidad afectada
  - estado o etapa origen y destino
  - comentario o motivo (cuando aplique)

**BE-004 Contrato de API**
- La API debe retornar errores estructurados, por ejemplo:
  - `code`
  - `message`
  - `details` / `field_errors`
- Esto permite al frontend mostrar mensajes claros y consistentes.

**BE-005 Gestión de documentos**
- Los documentos se almacenan en **OCI Object Storage**.
- En la base de datos Oracle solo se almacenan metadatos:
  - tipo de documento
  - nombre
  - referencia a oportunidad/actividad
  - ubicación en storage
  - usuario y fecha de carga

---

## 3. Entidades funcionales principales

### 3.1 Empresa

**Descripción:** Organización registrada en el sistema. No toda empresa es cliente.

**Campos mínimos:**

* id
* tipo (empresa / multinacional / aliado)
* razón social / nombre
* identificación tributaria
* sitio web
* país
* estado (activa/inactiva)

---

### 3.2 Persona

**Descripción:** Individuo que puede relacionarse con múltiples empresas y oportunidades.

**Campos mínimos:**

* id
* nombres
* apellidos
* email (opcional)
* teléfono (opcional)

---

### 3.3 Oportunidad Comercial

**Descripción:** Objeto central del proceso comercial.

**Campos obligatorios:**

* id
* nombre
* empresa principal
* pipeline_id
* etapa_id
* estado_macro (abierta / seguimiento / ganada / perdida / no_concretada)
* responsable
* fecha_creación

**Campos opcionales:**

* valor_estimado
* moneda
* probabilidad
* fecha_estimada_cierre
* fuente
* tipo_servicio

---

## 4. Pipeline dinámico

### 4.1 Pipeline

**Campos:**

* id
* nombre
* ámbito (gestión_comercial)
* versión
* estado

### 4.2 Etapa

**Campos:**

* id
* pipeline_id
* nombre
* orden
* probabilidad_sugerida
* estado

---

## 5. Catálogos globales

### 5.1 Tipos de actividad

* Reunión
* Llamada
* Visita
* Demo
* Presentación
* Envío de propuesta
* Seguimiento

### 5.2 Tipos de documento

* Propuesta
* Estimación económica
* RFP
* Acta
* Cotización
* Contrato borrador
* Orden de compra / servicio
* Otros

### 5.3 Motivos de cierre

* Precio
* Competencia
* Sin presupuesto
* Alcance no alineado
* Cliente desistió
* No respuesta
* Otro

---

## 6. Reglas de negocio (RB)

### RB-01

Una oportunidad pertenece a un único pipeline y una única etapa activa.

### RB-02

Los pipelines y etapas son configurables sin límite.

### RB-03

El movimiento entre etapas registra auditoría completa.

### RB-04

El cierre de oportunidad requiere motivo obligatorio si es perdida o no concretada.

### RB-05

El modo bloqueo por etapa es opcional; por defecto el sistema opera en modo guía.

---

## 7. Actividades y compromisos

### Actividad

**Campos:**

* id
* oportunidad_id
* tipo_actividad
* fecha_hora
* notas

### Compromiso

**Campos:**

* id
* actividad_id
* descripción
* responsable
* fecha_compromiso
* estado

---

## 8. Documentos

**Reglas:**

* Los documentos se almacenan en OCI Object Storage
* En BD solo se guardan metadatos

**Campos mínimos:**

* id
* oportunidad_id
* tipo_documento
* nombre
* url_storage
* fecha

---

## 9. APIs mínimas requeridas

### Empresas

* POST /empresas
* GET /empresas/{id}

### Personas

* POST /personas

### Oportunidades

* POST /oportunidades
* GET /oportunidades/{id}
* PUT /oportunidades/{id}

### Pipeline

* POST /pipelines
* POST /pipelines/{id}/etapas
* POST /oportunidades/{id}/mover-etapa

### Actividades

* POST /oportunidades/{id}/actividades

### Cierre

* POST /oportunidades/{id}/cerrar

---

## 10. KPIs base

* Oportunidades por etapa
* Tiempo promedio por etapa
* Tasa de conversión
* Actividades por oportunidad

---

## 11. Criterio de aceptación global

La implementación se considera correcta cuando:

* El pipeline es configurable sin código
* Las oportunidades siguen flujos completos
* Toda transición queda auditada
* Los KPIs reflejan el estado real del proceso comercial

---

**Fin del documento – v0.1**
