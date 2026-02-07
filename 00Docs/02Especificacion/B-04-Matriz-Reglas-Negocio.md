# B-04: Matriz de Reglas de Negocio → Validaciones → Endpoints
## Plataforma de Gestión Comercial - Arquitecsoft v0.1

**Fecha:** Febrero 2026  
**Versión:** 1.0  
**Clasificación:** Confidencial

---

## 1. Matriz Principal

| RB-ID | Descripción | Entidad | Endpoint(s) | Tipo | Validación Backend |
|-------|-------------|---------|-------------|------|-------------------|
| **RB-01** | Una oportunidad pertenece a un único pipeline y una única etapa activa | GC_OPORTUNIDAD | `POST /oportunidades`<br>`POST /oportunidades/{id}/mover-etapa` | **Bloqueo** | - FK obligatorias (pipeline_id, etapa_id)<br>- etapa_id debe pertenecer al pipeline_id<br>- No permitir cambio de pipeline después de creación |
| **RB-02** | Los pipelines y etapas son configurables sin límite | GC_PIPELINE<br>GC_ETAPA | `POST /pipelines`<br>`POST /pipelines/{id}/etapas`<br>`PUT /pipelines/{id}`<br>`PUT /pipelines/{pid}/etapas/{eid}` | **Guía** | - No hardcodear etapas en código<br>- Validar nombre único por pipeline<br>- Validar orden único por pipeline |
| **RB-03** | El movimiento entre etapas registra auditoría completa | GC_HISTORIAL_ETAPA | `POST /oportunidades/{id}/mover-etapa`<br>`POST /oportunidades/{id}/cerrar` | **Auditoría** | - Crear registro en GC_HISTORIAL_ETAPA<br>- Registrar: etapa_origen, etapa_destino, estado_origen, estado_destino, usuario, fecha, comentario |
| **RB-04** | Cierre requiere motivo obligatorio si es perdida/no_concretada | GC_OPORTUNIDAD | `POST /oportunidades/{id}/cerrar` | **Bloqueo** | - Si estado_macro IN (PERDIDA, NO_CONCRETADA) → motivo_cierre_id es obligatorio<br>- motivo_cierre_id debe existir en GC_CATALOGO con tipo=MOTIVO_CIERRE |
| **RB-05** | El modo bloqueo por etapa es opcional; por defecto modo guía | GC_ETAPA<br>GC_OPORTUNIDAD | `POST /oportunidades/{id}/mover-etapa` | **Guía** | - Si etapa.modo_bloqueo = FALSE → permitir saltar etapas<br>- Si etapa.modo_bloqueo = TRUE → validar requisitos (futuro) |
| **RB-06** | Toda acción relevante debe quedar auditada | GC_AUDITORIA_EVENTO | Todos los endpoints POST, PUT, DELETE | **Auditoría** | - Registrar: entidad, entidad_id, acción, datos_anteriores, datos_nuevos, usuario_id, ip, fecha |
| **RB-07** | El backend es fuente de verdad para reglas y validaciones | Todas | Todos | **Bloqueo** | - Todas las validaciones en backend<br>- Frontend solo validaciones UX<br>- No confiar en datos del cliente |
| **RB-08** | Documentos físicos en Object Storage, solo metadatos en BD | GC_DOCUMENTO | `POST /oportunidades/{id}/documentos`<br>`POST /actividades/{id}/documentos`<br>`GET /documentos/{id}/download`<br>`DELETE /documentos/{id}` | **Bloqueo** | - Subir archivo a OCI Object Storage<br>- Guardar solo metadatos en BD<br>- Al eliminar: borrar de Storage + BD |
| **RB-09** | Una persona puede relacionarse con múltiples empresas | GC_PERSONA_EMPRESA | `POST /personas/{id}/empresas` | **Guía** | - Permitir múltiples registros persona-empresa<br>- Constraint único: (persona_id, empresa_id)<br>- Cada relación tiene su propio contexto (cargo, email, etc.) |
| **RB-10** | Estados macro de oportunidad: abierta, seguimiento, ganada, perdida, no_concretada | GC_OPORTUNIDAD | `POST /oportunidades`<br>`PUT /oportunidades/{id}`<br>`POST /oportunidades/{id}/cerrar` | **Bloqueo** | - Validar que estado_macro IN (ABIERTA, SEGUIMIENTO, GANADA, PERDIDA, NO_CONCRETADA)<br>- Estados finales (GANADA, PERDIDA, NO_CONCRETADA) solo via /cerrar |

---

## 2. Reglas Adicionales Detectadas

| RB-ID | Descripción | Entidad | Endpoint(s) | Tipo | Validación Backend |
|-------|-------------|---------|-------------|------|-------------------|
| **RB-11** | Usuario autenticado es responsable por defecto en registros | Todas con creado_por | Todos los POST | **Auditoría** | - Tomar usuario_id del JWT<br>- Asignar a creado_por automáticamente |
| **RB-12** | Al menos un responsable PRINCIPAL por oportunidad | GC_OPORTUNIDAD_RESPONSABLE | `POST /oportunidades`<br>`POST /oportunidades/{id}/responsables`<br>`DELETE /oportunidades/{id}/responsables/{rid}` | **Bloqueo** | - Al crear oportunidad: asignar responsable_principal_id o usuario actual<br>- No permitir eliminar último responsable PRINCIPAL |
| **RB-13** | Responsable puede ser usuario interno O persona externa (XOR) | GC_OPORTUNIDAD_RESPONSABLE<br>GC_COMPROMISO_RESPONSABLE<br>GC_ACTIVIDAD_PARTICIPANTE | `POST /oportunidades/{id}/responsables`<br>`POST /actividades/{id}/compromisos`<br>`POST /oportunidades/{id}/actividades` | **Bloqueo** | - Si tipo_responsable = USUARIO → usuario_id obligatorio, persona_id NULL<br>- Si tipo_responsable = PERSONA_EXTERNA → persona_id obligatorio, usuario_id NULL |
| **RB-14** | Pipeline no puede cambiar después de asignado | GC_OPORTUNIDAD | `PUT /oportunidades/{id}` | **Bloqueo** | - No aceptar pipeline_id en el body de actualización<br>- Si se envía, ignorar o retornar error |
| **RB-15** | Etapa destino debe pertenecer al mismo pipeline | GC_OPORTUNIDAD | `POST /oportunidades/{id}/mover-etapa` | **Bloqueo** | - Validar que etapa_destino_id.pipeline_id == oportunidad.pipeline_id |
| **RB-16** | Solo un pipeline default por ámbito | GC_PIPELINE | `POST /pipelines`<br>`PUT /pipelines/{id}` | **Bloqueo** | - Si es_default = TRUE → desactivar otros default del mismo ámbito |
| **RB-17** | Documento debe asociarse a oportunidad O actividad | GC_DOCUMENTO | `POST /oportunidades/{id}/documentos`<br>`POST /actividades/{id}/documentos` | **Bloqueo** | - Al menos uno de oportunidad_id o actividad_id debe ser NOT NULL |
| **RB-18** | Compromiso solo puede crearse desde una actividad | GC_COMPROMISO | `POST /actividades/{id}/compromisos` | **Bloqueo** | - actividad_id es obligatorio (viene del path)<br>- No existe endpoint directo POST /compromisos |
| **RB-19** | Oportunidad cerrada no puede modificarse | GC_OPORTUNIDAD | `PUT /oportunidades/{id}`<br>`POST /oportunidades/{id}/mover-etapa`<br>`POST /oportunidades/{id}/actividades` | **Bloqueo** | - Si estado_macro IN (GANADA, PERDIDA, NO_CONCRETADA) → rechazar modificaciones<br>- Permitir solo lectura y consulta de historial |
| **RB-20** | Estado inicial de oportunidad es ABIERTA | GC_OPORTUNIDAD | `POST /oportunidades` | **Guía** | - Si no se especifica estado_macro → asignar ABIERTA<br>- No permitir crear con estado final |

---

## 3. Matriz por Endpoint

### 3.1 Oportunidades

| Endpoint | Reglas Aplicables | Validaciones |
|----------|-------------------|--------------|
| `POST /oportunidades` | RB-01, RB-10, RB-11, RB-12, RB-20 | - empresa_id existe y activa<br>- pipeline_id existe y activo<br>- etapa_id pertenece al pipeline (o usar primera etapa)<br>- estado_macro = ABIERTA<br>- Asignar responsable principal |
| `GET /oportunidades/{id}` | — | - Verificar existencia |
| `PUT /oportunidades/{id}` | RB-07, RB-14, RB-19 | - No permitir cambiar pipeline_id<br>- No permitir si está cerrada<br>- Validar campos según tipo |
| `POST /oportunidades/{id}/mover-etapa` | RB-01, RB-03, RB-05, RB-15, RB-19 | - Oportunidad no cerrada<br>- etapa_destino pertenece al mismo pipeline<br>- Registrar en historial<br>- Actualizar probabilidad si aplica |
| `POST /oportunidades/{id}/cerrar` | RB-03, RB-04, RB-10 | - estado_macro debe ser final<br>- motivo obligatorio si PERDIDA/NO_CONCRETADA<br>- Registrar en historial<br>- Asignar fecha_cierre |
| `POST /oportunidades/{id}/responsables` | RB-12, RB-13 | - tipo_responsable válido<br>- XOR usuario_id/persona_id<br>- rol válido |
| `POST /oportunidades/{id}/contactos` | RB-09 | - persona_empresa_id existe<br>- Evitar duplicados |

### 3.2 Pipelines y Etapas

| Endpoint | Reglas Aplicables | Validaciones |
|----------|-------------------|--------------|
| `POST /pipelines` | RB-02, RB-16 | - nombre único<br>- Si es_default, desactivar otros |
| `PUT /pipelines/{id}` | RB-02, RB-16 | - Validar transición de estado<br>- Si es_default, desactivar otros |
| `POST /pipelines/{id}/etapas` | RB-02 | - nombre único en el pipeline<br>- orden único en el pipeline<br>- probabilidad 0-100 |
| `PUT /pipelines/{pid}/etapas/{eid}` | RB-02, RB-05 | - Validar pertenencia al pipeline<br>- orden único |

### 3.3 Actividades y Compromisos

| Endpoint | Reglas Aplicables | Validaciones |
|----------|-------------------|--------------|
| `POST /oportunidades/{id}/actividades` | RB-06, RB-11, RB-13, RB-19 | - Oportunidad no cerrada<br>- tipo_actividad_id válido<br>- fecha_hora válida<br>- Participantes XOR validado |
| `POST /actividades/{id}/compromisos` | RB-06, RB-11, RB-13, RB-18 | - actividad existe<br>- fecha_compromiso no pasada (warning)<br>- Responsables XOR validado |
| `PUT /compromisos/{id}` | RB-06 | - Validar transición de estado<br>- Si COMPLETADO, fecha_completado obligatoria |

### 3.4 Documentos

| Endpoint | Reglas Aplicables | Validaciones |
|----------|-------------------|--------------|
| `POST /oportunidades/{id}/documentos` | RB-08, RB-17 | - tipo_documento_id válido<br>- Archivo válido (tipo, tamaño)<br>- Subir a OCI Storage |
| `POST /actividades/{id}/documentos` | RB-08, RB-17 | - tipo_documento_id válido<br>- Archivo válido<br>- Subir a OCI Storage |
| `DELETE /documentos/{id}` | RB-08 | - Eliminar de OCI Storage<br>- Eliminar metadatos de BD |

### 3.5 Empresas y Personas

| Endpoint | Reglas Aplicables | Validaciones |
|----------|-------------------|--------------|
| `POST /empresas` | RB-06, RB-11 | - razon_social obligatoria<br>- tipo válido |
| `PUT /empresas/{id}` | RB-06 | - No permitir cambiar a INACTIVA si tiene oportunidades ABIERTAS |
| `POST /personas` | RB-06, RB-11 | - nombres y apellidos obligatorios |
| `POST /personas/{id}/empresas` | RB-09 | - empresa existe<br>- Combinación persona-empresa única<br>- rol_contacto válido |

---

## 4. Resumen por Tipo de Regla

### 4.1 Reglas de Bloqueo (Rechazan la operación)

| RB-ID | Descripción Corta | Endpoint Principal |
|-------|-------------------|-------------------|
| RB-01 | Oportunidad: 1 pipeline, 1 etapa | POST /oportunidades |
| RB-04 | Motivo obligatorio en cierre perdida | POST /oportunidades/{id}/cerrar |
| RB-07 | Backend es fuente de verdad | Todos |
| RB-08 | Documentos en Object Storage | POST .../documentos |
| RB-10 | Estados macro válidos | POST, PUT /oportunidades |
| RB-12 | Responsable principal obligatorio | POST /oportunidades |
| RB-13 | XOR usuario/persona en responsable | POST .../responsables |
| RB-14 | Pipeline inmutable | PUT /oportunidades/{id} |
| RB-15 | Etapa del mismo pipeline | POST /oportunidades/{id}/mover-etapa |
| RB-16 | Solo un default por ámbito | POST, PUT /pipelines |
| RB-17 | Documento: oportunidad O actividad | POST .../documentos |
| RB-18 | Compromiso requiere actividad | POST /actividades/{id}/compromisos |
| RB-19 | Oportunidad cerrada inmutable | PUT, POST sobre oportunidad cerrada |
| RB-20 | Estado inicial ABIERTA | POST /oportunidades |

### 4.2 Reglas de Guía (Permiten pero advierten/sugieren)

| RB-ID | Descripción Corta | Comportamiento |
|-------|-------------------|----------------|
| RB-02 | Pipelines/etapas configurables | Sin límites hardcoded |
| RB-05 | Modo guía por defecto | Permite saltar etapas |
| RB-09 | Persona multi-empresa | Múltiples asociaciones |

### 4.3 Reglas de Auditoría (Registran sin bloquear)

| RB-ID | Descripción Corta | Tabla Destino |
|-------|-------------------|---------------|
| RB-03 | Historial de movimientos | GC_HISTORIAL_ETAPA |
| RB-06 | Auditoría general | GC_AUDITORIA_EVENTO |
| RB-11 | Usuario autenticado como creador | Campo creado_por |

---

## 5. Matriz de Impacto por Entidad

| Entidad | Reglas de Bloqueo | Reglas de Guía | Reglas de Auditoría |
|---------|-------------------|----------------|---------------------|
| GC_OPORTUNIDAD | RB-01, RB-04, RB-10, RB-12, RB-14, RB-19, RB-20 | RB-05 | RB-03, RB-06, RB-11 |
| GC_PIPELINE | RB-16 | RB-02 | RB-06, RB-11 |
| GC_ETAPA | RB-15 | RB-02, RB-05 | RB-06 |
| GC_OPORTUNIDAD_RESPONSABLE | RB-12, RB-13 | — | RB-06 |
| GC_DOCUMENTO | RB-08, RB-17 | — | RB-06, RB-11 |
| GC_ACTIVIDAD | RB-19 | — | RB-06, RB-11 |
| GC_COMPROMISO | RB-13, RB-18 | — | RB-06, RB-11 |
| GC_PERSONA_EMPRESA | — | RB-09 | RB-06 |
| GC_HISTORIAL_ETAPA | — | — | RB-03 |
| GC_AUDITORIA_EVENTO | — | — | RB-06 |

---

## 6. Códigos de Error por Regla

| RB-ID | Código Error | HTTP Status | Mensaje |
|-------|--------------|-------------|---------|
| RB-01 | `INVALID_STAGE` | 400 | "La etapa no pertenece al pipeline especificado" |
| RB-04 | `CLOSE_REASON_REQUIRED` | 400 | "El motivo de cierre es obligatorio para oportunidades perdidas o no concretadas" |
| RB-12 | `PRINCIPAL_REQUIRED` | 400 | "Debe existir al menos un responsable principal" |
| RB-13 | `INVALID_RESPONSIBLE_TYPE` | 400 | "Debe especificar usuario_id o persona_id según el tipo de responsable" |
| RB-14 | `PIPELINE_IMMUTABLE` | 400 | "No se puede cambiar el pipeline de una oportunidad" |
| RB-15 | `STAGE_WRONG_PIPELINE` | 400 | "La etapa destino no pertenece al pipeline de la oportunidad" |
| RB-16 | `DUPLICATE_DEFAULT` | 409 | "Ya existe un pipeline default para este ámbito" |
| RB-19 | `OPPORTUNITY_CLOSED` | 409 | "No se pueden realizar modificaciones en una oportunidad cerrada" |
| RB-20 | `INVALID_INITIAL_STATE` | 400 | "El estado inicial debe ser ABIERTA" |

---

*Fin del documento B-04 - Matriz de Reglas de Negocio*
