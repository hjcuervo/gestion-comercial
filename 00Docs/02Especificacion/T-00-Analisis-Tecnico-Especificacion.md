# T-00: Análisis Técnico de la Especificación
## Plataforma de Gestión Comercial - Arquitecsoft

**Fecha:** Febrero 2026  
**Versión Especificación:** v0.1  
**Clasificación:** Confidencial

---

## 1. Resumen Técnico del Alcance v0.1

La fase de Gestión Comercial constituye el **módulo inicial** de la plataforma, centrado en el seguimiento del ciclo de vida de oportunidades comerciales desde la prospección hasta el cierre.

**Núcleo funcional:** Un sistema de **pipelines dinámicos** donde las oportunidades avanzan por etapas configurables, con registro de actividades, compromisos y documentos asociados.

**Flujo principal:**
```
Empresa → Persona(s) de contacto → Oportunidad → Pipeline/Etapas → Actividades → Cierre
```

**Características clave:**
- Pipelines 100% configurables sin código
- Empresas pueden ser prospectos, clientes o aliados (no toda empresa es cliente)
- Personas con relaciones multi-empresa
- Auditoría obligatoria en todas las transiciones
- Documentos almacenados externamente (OCI Object Storage), solo metadatos en BD
- Modo guía por defecto (no bloquea avance entre etapas)

**Fuera de alcance v0.1:** Contratación, gestión contractual, facturación.

---

## 2. Entidades Funcionales Detectadas

| # | Entidad | Descripción | Relaciones Clave |
|---|---------|-------------|------------------|
| 1 | **Empresa** | Organización (prospecto/cliente/aliado) | Tiene N personas, tiene N oportunidades |
| 2 | **Persona** | Individuo con relación multi-empresa | Pertenece a N empresas |
| 3 | **Oportunidad** | Objeto central del proceso comercial | Pertenece a 1 empresa, 1 pipeline, 1 etapa |
| 4 | **Pipeline** | Flujo configurable de etapas | Tiene N etapas ordenadas |
| 5 | **Etapa** | Fase dentro de un pipeline | Pertenece a 1 pipeline |
| 6 | **Actividad** | Interacción registrada (reunión, llamada, etc.) | Pertenece a 1 oportunidad |
| 7 | **Compromiso** | Tarea derivada de una actividad | Pertenece a 1 actividad |
| 8 | **Documento** | Archivo adjunto (metadatos) | Asociado a oportunidad/actividad |

**Catálogos detectados:**
- Tipos de actividad (7 valores)
- Tipos de documento (8 valores)
- Motivos de cierre (7 valores)

---

## 3. Reglas de Negocio Detectadas

| Código | Regla | Implicación Técnica |
|--------|-------|---------------------|
| **RB-01** | Una oportunidad pertenece a un único pipeline y una única etapa activa | FK obligatorias, constraint de unicidad |
| **RB-02** | Los pipelines y etapas son configurables sin límite | No hardcodear etapas, CRUD completo de configuración |
| **RB-03** | El movimiento entre etapas registra auditoría completa | Tabla de historial de transiciones |
| **RB-04** | Cierre requiere motivo obligatorio si es perdida/no_concretada | Validación condicional en backend |
| **RB-05** | Modo bloqueo por etapa es opcional; por defecto modo guía | Flag en etapa, validación flexible |

**Reglas implícitas detectadas:**

| Código | Regla Implícita | Fuente |
|--------|-----------------|--------|
| **RB-06** | Toda acción relevante debe quedar auditada (fecha, usuario, entidad, estados, comentario) | BE-003 |
| **RB-07** | El backend es fuente de verdad para reglas de negocio y validaciones | BE-002 |
| **RB-08** | Documentos físicos en Object Storage, solo metadatos en BD | BE-005, Sección 8 |
| **RB-09** | Una persona puede relacionarse con múltiples empresas | Sección 3.2 |
| **RB-10** | Estados macro de oportunidad: abierta, seguimiento, ganada, perdida, no_concretada | Sección 3.3 |

---

## 4. Supuestos Técnicos a Asumir

### Stack Tecnológico (DECISIÓN CONFIRMADA)
1. **Backend:** Java 21 + Spring Boot 3.3 (se mantiene el stack actual, NO migrar a Python/FastAPI)
2. **Migraciones:** Flyway (no Alembic)
3. **ORM:** Spring Data JPA + Hibernate (no SQLAlchemy)

### Autenticación y Autorización (DECISIÓN CONFIRMADA)
4. Se implementará autenticación JWT (mencionado en FE-004)
5. **Se creará módulo de administración de usuarios** con roles (RBAC)
6. El usuario autenticado será el "responsable" por defecto en registros de auditoría

### Modelo de Datos
7. La relación Persona-Empresa es N:M (requiere tabla intermedia)
8. **La tabla intermedia Persona-Empresa incluye:** cargo, teléfono, email, rol, puesto (DECISIÓN CONFIRMADA)
9. El historial de movimientos de etapa es una tabla separada (no se sobrescribe la etapa actual)
10. Los catálogos (tipos de actividad, documento, motivos) serán tablas configurables
11. La auditoría se implementará como campos en entidades + tabla de eventos

### Responsables (DECISIÓN CONFIRMADA)
12. **El responsable de oportunidad/compromiso puede ser:** usuario interno, persona externa o ambos
13. Se requiere diseño flexible que permita asignar responsables de ambos tipos

### Pipelines
14. Una oportunidad NO puede cambiar de pipeline una vez asignada (solo de etapa)
15. Las etapas tienen un orden secuencial pero el modo guía permite saltar etapas
16. Un pipeline puede tener múltiples versiones (campo versión existe)

### Documentos
17. La integración con OCI Object Storage se implementará en fase posterior; inicialmente se puede usar almacenamiento local o mock
18. Un documento puede asociarse a oportunidad O actividad (no ambos obligatorios)

### Generales
19. El sistema será single-tenant (una instancia = una empresa Arquitecsoft)
20. Los KPIs se calcularán en tiempo real o con queries; no hay requerimiento de precálculo
21. No hay requerimiento de notificaciones o alertas en esta versión

---

## 5. Ambigüedades Detectadas

### ✅ Resueltas

| # | Ambigüedad | Resolución |
|---|------------|------------|
| **A-01** | Relación Persona-Empresa | **Incluye:** cargo, teléfono, email, rol, puesto |
| **A-02** | Responsable de oportunidad | **Puede ser:** usuario interno, persona externa o ambos |
| **A-03** | Responsable de compromiso | **Puede ser:** usuario interno, persona externa o ambos |
| **A-04** | Usuarios del sistema | **Se creará módulo de administración de usuarios** |

### Pendientes - Media Prioridad (afectan funcionalidad)

| # | Ambigüedad | Pregunta | Impacto |
|---|------------|----------|---------|
| **A-05** | Contactos de oportunidad | ¿Una oportunidad puede tener múltiples personas de contacto? | Cardinalidad |
| **A-06** | Estado de pipeline | ¿Qué valores puede tener? (activo/inactivo?) | Catálogo |
| **A-07** | Estado de etapa | ¿Mismo caso? ¿Se pueden desactivar etapas? | Lógica de negocio |
| **A-08** | Ámbito de pipeline | Solo dice "gestión_comercial", ¿habrá otros ámbitos? | Diseño a futuro |
| **A-09** | Moneda | ¿Hay catálogo de monedas? ¿Conversión? | Catálogo |
| **A-10** | Fuente de oportunidad | ¿Es texto libre o catálogo? | Validación |

### Pendientes - Baja Prioridad (detalles de implementación)

| # | Ambigüedad | Pregunta |
|---|------------|----------|
| **A-11** | Tipo_servicio en oportunidad | ¿Es catálogo? ¿Cuáles son los valores? |
| **A-12** | Probabilidad sugerida vs probabilidad | ¿La etapa sugiere y el usuario puede modificar en oportunidad? |
| **A-13** | Versión de pipeline | ¿Cómo se maneja versionamiento? ¿Oportunidades migran? |
| **A-14** | Documentos en actividad | La API no incluye endpoint para documentos en actividad |

---

## Resumen Ejecutivo

| Aspecto | Cantidad |
|---------|----------|
| Entidades principales | 8 + Usuario (nuevo) |
| Catálogos | 3 |
| Reglas de negocio explícitas | 5 |
| Reglas de negocio implícitas | 5 |
| Supuestos técnicos | 21 |
| Ambigüedades resueltas | 4 |
| Ambigüedades pendientes media | 6 |
| Ambigüedades pendientes baja | 4 |

### Decisiones Confirmadas

| Tema | Decisión |
|------|----------|
| Stack Backend | **Java 21 + Spring Boot 3.3** (se mantiene) |
| Usuarios | **Crear módulo de administración de usuarios** |
| Responsables | **Pueden ser usuarios internos, personas externas o ambos** |
| Relación Persona-Empresa | **Incluye:** cargo, teléfono, email, rol, puesto |

**Siguiente paso recomendado:** Ejecutar T-01 (Diseño del modelo de datos) con estas decisiones incorporadas.

---

*Fin del análisis T-00 - Actualizado con decisiones confirmadas*
