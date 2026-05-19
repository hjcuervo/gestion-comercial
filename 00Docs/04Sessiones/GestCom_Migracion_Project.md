# GestCom — Resumen de Migración del Project

**Fecha de generación:** 6 de mayo de 2026
**Propósito:** Snapshot completo del estado del Project para reconstruir contexto en una conversación nueva o migrar a otro entorno (otro Project, otra cuenta, otro modelo).

---

## 1. Propósito del proyecto

**GestCom** es la plataforma interna de **gestión comercial y contractual** de **Arquitecsoft SAS**, desarrollada por Hector como desarrollador principal. Cubre el ciclo completo desde la identificación de una oportunidad comercial hasta el control del proceso de facturación de los contratos vigentes.

- **Repositorio:** `https://github.com/hjcuervo/gestion-comercial`
- **Uso:** interno (no MVP, enfoque productivo).
- **Infraestructura objetivo:** OCI (Oracle Cloud Infrastructure).
- **Arquitectura:** monolito modular organizado **por dominios** (no por capas técnicas), backend API-only, frontend SPA.
- **Idioma del dominio:** español (entidades, campos, mensajes UI). Convenciones técnicas estándar (CamelCase, etc.) pero los nombres del dominio van en español: `GcOportunidad`, `etapa`, `formaPago`, etc.

La plataforma se organiza en **mundos funcionales**. La versión original contemplaba 4 mundos; la realidad actual del repo (mayo 2026) consolidó esto en **3 mundos** (ver §4 "Decisiones clave").

---

## 2. Instrucciones personalizadas del Project (verbatim)

> Trabajamos en GestCom, plataforma de gestión comercial y contractual de Arquitecsoft SAS.
> Desarrollador principal: Hector. Repositorio: https://github.com/hjcuervo/gestion-comercial.
>
> Stack (no negociable):
> - Backend: Spring Boot 3.4 + Java 21 + Oracle 23c (JPA/Hibernate).
> - Frontend: Vue 3 (Composition API) + Vite + Pinia.
> - Autenticación: JWT con RBAC (Admin / Comercial / Lectura-KPI).
> - Sistema de diseño: "Luxury Tech" (cyan #00D4FF + violeta #A855F7).
>
> Archivos del Project que debes consultar:
> - gestcom-context.SKILL.md — SIEMPRE PRIMERO. Visión de los 4 mundos, glosario, lecciones aprendidas.
> - gestcom-backend.SKILL.md — Para tareas Spring Boot, Java, Oracle, JPA, DDL, endpoints.
> - gestcom-frontend.SKILL.md — Para tareas Vue, Vite, Pinia, vistas, services, NavRail.
> - Estado_arte_04052026.md — Para entender qué está hecho, qué está pendiente y por dónde seguir.
>
> Convenciones obligatorias:
> - Idioma del dominio en español. Prefijo Gc en entidades JPA, GC_ en tablas Oracle, CAT_ en catálogos transversales.
> - Backlog en formato Fx.y (fases F1, F2... y tareas T1.1, T1.2...).
> - Reglas de negocio en formato RB-XX.
> - Auditoría obligatoria en todas las entidades de negocio (fechaCreacion, fechaModificacion, usuarioCreacion, usuarioModificacion).
> - Endpoints REST bajo /api/v1/{recurso-en-plural-en-español}.
>
> Cómo trabajamos juntos:
> - Para features nuevos: primero especificar (documento de alcance + backlog), luego codificar. Nunca saltar a código sin especificación.
> - Cambios mínimos cuando se pide una corrección — no reescribir archivos completos sin pedirlo.
> - Entregar archivos con present_files (descargables), no pegarlos inline en el chat salvo fragmentos cortos.
> - Verificar el estado del repo en GitHub antes de proponer cambios sobre archivos del Mundo 2.
> - GlobalExceptionHandler debe seguir mostrando la causa raíz en desarrollo (no envolver excepciones en mensajes genéricos).
>
> Estado actual congelado a Mayo 2026:
> - Mundo 1 (Comercial): completo (~95%). Solo mejoras menores pendientes (reportes exportables, filtros persistentes).
> - Mundo 2 (Contractual): F1 a F5 completas. Pendiente F6 Dashboard + 2 issues técnicos abiertos (I-01 ORA-00904 por @OrderBy, I-02 repo desincronizado).
> - Mundo 3 (Flujo de Facturación): sin especificar. Requiere documento de alcance antes de codificar.
> - Mundo 4 (Facturación): sin especificar. Requiere documento de alcance antes de codificar.
>
> Antes de cualquier respuesta sustantiva en este Project, lee gestcom-context.SKILL.md.
> Para tareas de código, lee también la skill correspondiente (backend o frontend).

> ⚠️ **Nota importante sobre desincronización:** Las instrucciones del Project (arriba) están **desactualizadas** respecto al estado real del repo a mayo 2026. La conversación de cierre de issues técnicos del 5 de mayo de 2026 documentó los siguientes cambios reales:
> - El **"Mundo 4" ya no existe como módulo independiente**. Su alcance se fusionó en el Mundo 3.
> - La plataforma se organiza en **3 mundos**, no 4.
> - El **Mundo 3 SÍ tiene especificación funcional formal** versionada en el repo (`00Docs/02Especificacion/GestCom_Mundo3_Especificacion_1.md`), backend al ~85%, frontend al ~25%.
> - **F6 (Dashboard contractual) nunca se ejecutó como tal** y se reemplazó por el dashboard integrado del Mundo 3.
> - **I-02 está cerrado** (repo sí está sincronizado). **I-01 sigue abierto pero el archivo culpable cambió** (ya no es `GcContratoModificacion`; podría estar en `GcCompromisoIngreso` o en una query JPQL).

---

## 3. Knowledge base (archivos del Project)

| # | Archivo | Tamaño/scope | Qué aporta |
|---|---------|--------------|-----------|
| 1 | **`gestcom-context.SKILL.md`** | Skill raíz — siempre primero | Contexto base: identidad del proyecto (Arquitecsoft, Hector, repo), stack no negociable, descripción de los 4 mundos (legacy), arquitectura monolito modular por dominios, flujo crítico Pipeline Comercial → Contractual → Contrato, glosario de dominio (Oportunidad, Pipeline, Etapa, Empresa, Persona, Actividad, Compromiso, Proceso de Contratación, Contrato, Forma de Pago, Modificación, Empresa de facturación), convenciones globales de naming (`GC_*`, `Gc*`, `CAT_*`), estructura del repo, estado al congelar (mayo 2026), preferencias de Hector (cambios mínimos, archivos descargables, especificar antes de codificar, errores con causa raíz, formato `Fx.y` y `RB-XX`), y **5 lecciones aprendidas costosas** (ver §6). |
| 2 | **`gestcom-backend.SKILL.md`** | Convenciones backend Spring Boot | Estructura de paquetes por dominio (`domain/{dominio}/{entity,repository,service,controller,dto}`), plantilla canónica de entidad JPA con `@Entity`, FKs LAZY, 4 campos de auditoría obligatorios, `@PrePersist`/`@PreUpdate`, plantilla de repositories con `JOIN FETCH`, plantilla de services con `@Transactional` (readOnly en consultas, sin readOnly en escrituras), plantilla de controllers `/api/v1/{recurso-plural}`, convención de DTOs (`*CreateRequest`, `*UpdateRequest`, `*Response`, `*Summary`, preferir `record`, método estático `fromEntity`), GlobalExceptionHandler que muestra causa raíz, plantilla DDL Oracle con prefijos `GC_`, `FK_*`, `CK_*`, `IDX_*`, **checklist anti-errores** de 8 puntos antes de cerrar un cambio backend, convenciones para entrega de archivos. |
| 3 | **`gestcom-frontend.SKILL.md`** | Convenciones frontend Vue 3 | Stack: Composition API con `<script setup>`, **prohibido Vuex y Tailwind**, prohibido frameworks UI prefabricados. Estructura de carpetas (`views/`, `components/{common,pipeline,{dominio}}/`, `services/`, `stores/`, `router/`, `layouts/`, `assets/icons/`). **Sistema de diseño "Luxury Tech"** completo: tabla de tokens CSS (`--lt-cyan`, `--lt-violet`, `--lt-bg-deep`, `--lt-bg-card`, etc.), tipografía Inter, espaciado en múltiplos de 4px, radios (botones 6px, cards 10px, modales 12px), botones (primario/secundario/destructivo), badges de estado. Plantilla canónica de vista de listado y detalle, patrón de modales (estado local, eventos `@guardar`/`@cerrar`). Convención de services HTTP (constante `RESOURCE`, `.then(r => r.data)`, sin manejo de errores en service). Pinia solo para estado global persistente. Patrón para router y NavRail. Formatters comunes (`formatearMoneda`, `formatearFecha`). Tabla de reglas RB → implementación UI. Checklist de 10 puntos al crear/modificar vista. |
| 4 | **`Estado_arte_04052026.md`** | Línea base completa del proyecto | Documento maestro con: visión global (4 mundos en esta versión), arquitectura y stack, **detalle exhaustivo del Mundo 1** (completo: 7 entidades, funcionalidades, 5 reglas de negocio críticas RB-01/04/14/15/19, 3 pendientes menores), **detalle del Mundo 2** (al 70%: F1-F5 completas con desglose por tarea, F6 pendiente con T6.1/T6.2/T6.3, 2 issues abiertos I-01/I-02), **alcance no especificado del Mundo 3** (esbozo de flujo, 7 preguntas pendientes Q1-Q7, fases F7-F9 a planificar) y **Mundo 4** (registro de facturas reales, 4 preguntas pendientes, fases F10-F12). Resumen ejecutivo de avance. **Plan recomendado por bloques** (A: cierre Mundo 2; B: especificación Mundo 3; C: implementación Mundo 3; D: Mundo 4; E: mejoras transversales). Documentos de referencia existentes y control de cambios. |
| 5 | **`skills-README.md`** | Índice de las 3 skills | Tabla resumen de cuándo se activa cada skill, patrón de uso ("`Crea la entidad GcFactura`" → context + backend; "`Diseña la vista de facturas`" → context + frontend), nota de estado congelado a mayo 2026, cómo evolucionar las skills, próximas skills sugeridas (`gestcom-database`, `gestcom-spec`). |

---

## 4. Decisiones clave (cronológico)

### 4.1 De arquitectura general
- **Stack final** (febrero 2026, no negociable): Spring Boot 3.4 + Java 21 + Oracle 23c + Vue 3 + Vite + Pinia. Descartados: Vuex, MyBatis, Quasar, MySQL, frameworks UI prefabricados, Tailwind.
- **Monolito modular por dominio**, no por capas. Backend API-only, frontend SPA. JWT + RBAC con 3 roles (Admin / Comercial / Lectura-KPI).
- **Pipelines dinámicos parametrizables** con campo `ambito` (`COMERCIAL` o `CONTRATACION`).
- **Modo guía por defecto** (no bloquea avance entre etapas).
- **Auditoría obligatoria** (4 campos en cada entidad de negocio).

### 4.2 Del Mundo 2 (contractual)
- **El contrato no existe durante el pipeline contractual.** Lo que existe es un `GcProcesoContratacion`. Solo al completar el pipeline nace `GC_CONTRATO` directamente en estado **VIGENTE** (se eliminó el estado `EN_PROCESO`).
- **En el pipeline contractual se usan etapas, no estados.**
- **Botón "Cerrar" del pipeline comercial:** GANADA → dispara formalización; PERDIDA / NO_CONCRETADA → permanece como "Cerrar".
- **Botón "Formalizar Contrato":** visible en cualquier etapa del pipeline contractual mientras no exista contrato; desaparece una vez creado.
- **Al ganar, la oportunidad se mueve al pipeline contractual** (no se duplica, no se mantiene en ambos).
- **Estados válidos del contrato:** VIGENTE → SUSPENDIDO ↔ VIGENTE → TERMINADO → LIQUIDADO.
- **Una oportunidad CONTRATADA / PERDIDA / NO_CONCRETADA es inmutable** (RB-19).

### 4.3 Del Mundo 3 (rediseño abril-mayo 2026)
Decisiones tomadas en sesión específica de rediseño del módulo de facturación:

- **GestCom no emite facturas.** Las emite **FACTRO** (sistema externo conectado a la DIAN). GestCom indica qué facturar, registra gestión, cruza con facturas FACTRO.
- **Renombre `GcContratoFormaPago` → `GcCompromisoIngreso`** para alinear con el lenguaje de la spec ("los compromisos viven hasta resolverse, el periodo es una vista, no un contenedor").
- **Relación N:N Compromiso ↔ Factura** vía tabla de unión `GC_COMPROMISO_FACTURA` con `montoAplicado` por factura. Soporta facturación múltiple parcial (presupuesto 10K → facturas 3K+2K+5K) y facturas que cubren varios compromisos.
- **Separación bitácora vs. eventos:** mantener `TipoGestion` actual para notas libres del usuario + nueva tabla `GC_COMPROMISO_EVENTO` con campos estructurados (`montoAnterior`, `montoNuevo`, `fechaAnterior`, `fechaNueva`, `motivo`) para eventos de máquina de estado.
- **Valor presupuestado vs. valor facturado:** el compromiso nace con presupuesto y al cruzar con factura se registra el valor real (pueden diferir por contratos variables, ajustes IPC, servicios parciales).
- **TRM mensual** (USD → COP) en tabla `GC_TRM` independiente. Si no hay TRM del mes, se usa la del mes anterior más reciente.
- **Excel EMI** generable desde la plataforma con 4 hojas (Tabla COP, Tabla USD, COL, PERU) más hoja nueva "Resumen Facturado".
- **Mundo 4 absorbido por el Mundo 3.** El cruce con FACTRO + KPIs + Excel EMI cubre lo que era el Mundo 4 ("registro de facturas reales"). La plataforma queda en 3 mundos.

### 4.4 De infraestructura de prompting
- **Skills oficiales del proyecto** creadas en mayo 2026: 3 skills (`gestcom-context`, `gestcom-backend`, `gestcom-frontend`) viviendo bajo `.claude/skills/` en el repo, versionadas como cualquier otro archivo. Próximas sugeridas (no construidas): `gestcom-database`, `gestcom-spec`.

---

## 5. Trabajo en progreso (a 5-6 de mayo de 2026)

### 5.1 Bloqueante actual: Issues técnicos del Mundo 2

| ID | Estado | Descripción |
|----|--------|-------------|
| **I-01** | 🟡 Abierto, redefinido | Error `ORA-00904: "M1_0"."FECHAMODIFICACION"` por caché stale de Hibernate. **Originalmente** se atribuyó a `@OrderBy("fechaModificacion DESC")` en `GcContrato.modificaciones`. **Auditoría del 5 de mayo:** ese `@OrderBy` ya está corregido a `fechaCreacion DESC` (field que sí existe). El error ahora puede provenir de: (a) un `@OrderBy` en otra entidad cuyo recorrido pase por `GcContratoModificacion` (la columna se llama `fecha_modificacion` pero el field se llama `fechaModificacionContrato`), (b) una query JPQL con `c.fechaModificacion`, o (c) algo en `GcCompromisoIngreso` o sus consumidores. **Resolverlo requiere stack trace en runtime.** |
| **I-02** | ✅ Cerrado | "Repo desincronizado" — verificación del 5 de mayo confirmó que el repo sí estaba sincronizado, era percepción. |

### 5.2 Mundo 3 — Estado real (al 5 de mayo)

- **Especificación funcional v3** publicada en `00Docs/02Especificacion/GestCom_Mundo3_Especificacion_1.md` (~13 KB). Versión más reciente que las v1/v2 anteriores.
- **Backend al ~85%:** entidades nuevas creadas (`GcTrm`, `GcFactura`, `GcCompromisoIngreso` renombrado, `GcCompromisoFactura`, `GcCompromisoEvento`), services y controllers parciales. `ContratoController` limpiado de endpoints de formas de pago (que migrarán a `CompromisoIngresoController` bajo `/api/v1/compromisos`).
- **Frontend al ~25%:** falta tablero de pendientes del mes, panel de gestión por compromiso, modales de registrar gestión / registrar factura / cruzar factura, vista de TRM, botón de exportar Excel EMI.

### 5.3 Plan recomendado vigente (Bloques A-E)

- **Bloque A (cierre Mundo 2):** resolver I-01 con stack trace + completar F6 si aún se considera necesario tras la fusión Mundo 3+4.
- **Bloque B (no aplica):** la especificación del Mundo 3 ya existe.
- **Bloque C (implementación Mundo 3):** terminar el 15% backend pendiente y el 75% frontend pendiente.
- **Bloque D (no aplica):** Mundo 4 absorbido.
- **Bloque E (mejoras transversales):** reportes exportables, refinamiento dashboard general, tests automatizados.

---

## 6. Contexto recurrente (preferencias, terminología, restricciones)

### 6.1 Preferencias de Hector (cómo trabajar con él)
1. **Cambios mínimos.** Cuando pide una corrección, aplicarla aislada — nunca reescribir archivos completos sin pedirlo.
2. **Archivos descargables, no inline.** Entregar con `present_files`. Solo pegar inline si son fragmentos cortos para discusión.
3. **Verificar el repo antes de modificar.** Especialmente para archivos del Mundo 2 / 3 que pueden haber cambiado entre sesiones. Usar URLs raw de GitHub.
4. **Especificar antes de codificar.** Para nuevos features grandes: primero documento de alcance + backlog `Fx.y`, nunca saltar a código.
5. **Errores con causa raíz.** El `GlobalExceptionHandler` está configurado para mostrar la causa raíz en desarrollo. Mantener ese comportamiento; no envolver excepciones en mensajes genéricos.
6. **Backlog formato `Fx.y`.** Tareas numeradas como `T1.1`, `T1.2` dentro de fases `F1`, `F2`. Estado con emojis: 🔲 pendiente, 🟡 en curso, ✅ completado.
7. **Reglas de negocio formato `RB-XX`.** Documentadas en matriz que las cruza con endpoints donde aplican.
8. **Comparte URLs raw de GitHub** para revisión de archivos. Prefiere recibir archivos para descarga directa.
9. **Debug por screenshots y terminal output.** Pasa pantallazos de consola y output del terminal cuando algo falla.

### 6.2 Terminología del dominio (glosario)

| Término | Significado |
|---------|-------------|
| **Mundo** | Dominio funcional grande (Comercial / Contractual / Facturación). Se usan también para versionado conceptual. |
| **Oportunidad** | Núcleo del proceso comercial. Pertenece a 1 empresa, 1 pipeline, 1 etapa. |
| **Pipeline** | Flujo configurable de etapas con `ambito` (COMERCIAL / CONTRATACION). |
| **Etapa** | Fase ordenada dentro de un pipeline. |
| **Empresa** | Organización (prospecto / cliente / aliado comercial). |
| **Persona** | Contacto individual con relación multi-empresa. |
| **Actividad** | Interacción registrada (reunión, llamada, propuesta). |
| **Compromiso** | Tarea derivada de una actividad. |
| **Proceso de Contratación** | Pipeline intermedio entre oportunidad ganada y contrato. Entidad `GcProcesoContratacion`. |
| **Contrato** | Documento formal. Tipos: Contrato Formal, Orden de Compra, Orden de Servicio. |
| **Forma de Pago** *(legacy)* | Cuota o hito de facturación. **Renombrado a Compromiso de Ingreso** en el Mundo 3. |
| **Compromiso de Ingreso** | Cuota o hito de facturación con autonomía. Entidad `GcCompromisoIngreso`. Vive hasta resolverse. |
| **Modificación** | Adición, prórroga o suspensión sobre un contrato. |
| **Empresa de facturación** | Filial específica que factura, distinta a la empresa cliente principal. |
| **FACTRO** | Sistema externo (no parte de GestCom) que emite las facturas electrónicas a la DIAN. |
| **EMI** | Excel mensual de seguimiento de flujo de caja. Se genera desde GestCom con conversión TRM. |
| **TRM** | Tasa Representativa del Mercado. USD → COP, mensual, configurable. |
| **Bitácora** | Registro de gestión libre del usuario sobre un compromiso (`GcFormaPagoGestion`). |
| **Eventos** | Log inmutable de máquina de estado del compromiso (`GcCompromisoEvento`), separado de la bitácora. |

### 6.3 Convenciones obligatorias
- **Naming:** `Gc{Dominio}` para entidades JPA, `GC_*` para tablas Oracle, `CAT_*` para catálogos transversales, `Cat{Nombre}` para entidades de catálogo. Constraints: `FK_{TABLA}_{REF}`, `CK_{TABLA}_{COL}`, `IDX_{TABLA}_{COL}`.
- **Endpoints REST:** `/api/v1/{recurso-en-plural-en-español}` (ej. `/api/v1/contratos`, `/api/v1/oportunidades`). Sub-recursos para cambios de estado: `/{id}/suspender`, `/{id}/terminar`, `/{id}/liquidar`.
- **Idioma:** UI, mensajes, nombres de campos del dominio en español. Código (clases Java, métodos, variables) sigue convenciones técnicas estándar.
- **Auditoría:** los 4 campos `fechaCreacion`, `fechaModificacion`, `usuarioCreacion`, `usuarioModificacion` van **al final** de la entidad, sellados con `@PrePersist` y `@PreUpdate`.
- **DTOs:** sufijos `*CreateRequest`, `*UpdateRequest`, `*Response`, `*Summary`. Preferir `record` (Java 21). Método estático `fromEntity` en los Response.
- **Sin Vuex, sin Tailwind, sin Material Icons, sin frameworks UI prefabricados.**

### 6.4 Sistema de diseño — "Luxury Tech" (NO navy/teal/orange ni IBM Plex)

> ⚠️ **Discrepancia con el prompt de la solicitud:** el prompt menciona "design system de ArquitectSOFT, paleta navy/teal/orange, IBM Plex". **Esa descripción no corresponde al sistema real de GestCom.** El sistema real, definido en `gestcom-frontend.SKILL.md` y construido en marzo 2026, es:

- **Estética:** "Luxury Tech" — sofisticada, premium, dark mode con glassmorphism y acentos neón sutiles. No genérico de SaaS.
- **Paleta:**
  - Primario: **cyan eléctrico `#00D4FF`** (acción primaria, énfasis, brand).
  - Secundario: **violeta vivo `#A855F7`** (acento, gradientes).
  - Fondos: deep `#0A0A14` / card `#13131F` / elevated `#1B1B2A`.
  - Bordes: `#2A2A3D`. Texto primario `#EAEAF2`, muted `#8888A0`.
  - Semánticos: success `#22C55E`, warning `#F59E0B`, danger `#EF4444`, info `#3B82F6`.
  - Gradiente hero: `linear-gradient(135deg, #00D4FF 0%, #A855F7 100%)`.
- **Tipografía:** **Inter** (según `gestcom-frontend.SKILL.md`) — pesos 400/500/600/700, tamaños base 14px texto / 13px tablas / 12px etiquetas / 16-24px títulos. (Nota histórica: en el prototipo inicial de marzo 2026 se usó **Outfit** + **JetBrains Mono**; la skill consolidada estandariza Inter como tipografía actual.)
- **Iconos:** SVG personalizados en `assets/icons/`. **No** Material Icons, **no** librerías de iconos externas.
- **Espaciado:** múltiplos de 4px. Radios: botones 6px, cards 10px, modales 12px.
- **Tokens CSS:** prefijo `--lt-*` (`--lt-cyan`, `--lt-violet`, `--lt-bg-deep`, etc.).
- **Badges de estado:** clases `.badge-{estado-en-minúscula}` con fondo de opacidad reducida del color del estado y texto en color sólido.

### 6.5 Lecciones aprendidas (deuda anti-patrones)
1. **`@OrderBy` debe apuntar a un field existente en la entidad** (no en la tabla). Hibernate no valida en tiempo de compilación; un error aquí provoca `ORA-00904` con alias `M1_0` que despistan.
2. **`nullable = false` en FKs bloquea inserts** cuando la FK no siempre se popula. Caso real: `proceso_contratacion_id` en `GcContrato` se quitó porque hay escenarios donde el contrato se crea sin proceso intermedio.
3. **Constraints CHECK de Oracle deben sincronizarse con los enums Java.** Al agregar un valor (ej. `CONTRATADA` al estado de oportunidad), hay que `ALTER` el constraint o el insert falla.
4. **Lazy-load fuera de transacción explota.** Toda consulta de detalle que se serializa después de cerrar la transacción debe usar `JOIN FETCH` explícito en JPQL. Caso real: `GcContratoRepository.findByIdWithRelations` faltaba `JOIN FETCH c.oportunidad`.
5. **Caché de Hibernate puede enmascarar correcciones.** Después de cambios en `@OrderBy`, `@JoinColumn` u otras anotaciones JPA críticas, correr `mvn clean package -DskipTests` antes de probar.
6. **GlobalExceptionHandler debe mostrar la causa raíz en desarrollo.** Switching del genérico "Error interno del servidor" a la cadena completa de `Throwable.getCause()` aceleró el debugging significativamente.
7. **El nombre del field en Java vs. el nombre de la columna en Oracle pueden divergir.** El field `fechaModificacionContrato` puede mapear a la columna `fecha_modificacion`. Las queries JPQL usan el field, no la columna.

---

## 7. Prompt de arranque recomendado para una conversación nueva

Pegar este prompt al inicio de una conversación nueva (en el mismo Project, otro Project, o incluso otro entorno con las skills en knowledge base):

```
Soy Hector, desarrollador principal de GestCom — la plataforma interna de gestión
comercial y contractual de Arquitecsoft SAS.

Repo: https://github.com/hjcuervo/gestion-comercial
Stack: Spring Boot 3.4 + Java 21 + Oracle 23c (backend) y Vue 3 Composition API +
Vite + Pinia (frontend). JWT + RBAC con 3 roles: Admin / Comercial / Lectura-KPI.
Sistema de diseño: "Luxury Tech" (cyan #00D4FF + violeta #A855F7, dark mode con
glassmorphism, tokens CSS con prefijo --lt-*). Sin Vuex, sin Tailwind, sin frameworks
UI prefabricados.

Antes de responder cualquier cosa sustantiva, lee en este orden:
1. gestcom-context.SKILL.md (skill raíz, contexto base, glosario, lecciones aprendidas).
2. Si la tarea es backend, lee también gestcom-backend.SKILL.md.
3. Si la tarea es frontend, lee también gestcom-frontend.SKILL.md.
4. Si necesitas el estado del proyecto, lee Estado_arte_04052026.md (con la salvedad
   de la §0 de actualización del 5 de mayo: 3 mundos en lugar de 4, Mundo 3 con
   especificación formal y código avanzado, I-02 cerrado, I-01 redefinido).

Estado real a hoy (mayo 2026):
- Mundo 1 (Comercial): completo (~95%).
- Mundo 2 (Contractual): backend y frontend funcionales, con un issue abierto
  I-01 (ORA-00904 por @OrderBy / field name mismatch que requiere stack trace en
  runtime para localizar). I-02 ya cerrado.
- Mundo 3 (Flujo de Facturación): especificación v3 publicada en el repo, backend
  al ~85%, frontend al ~25%. Renombre clave: GcContratoFormaPago → GcCompromisoIngreso.
  Tabla de unión N:N GC_COMPROMISO_FACTURA. Bitácora (GcFormaPagoGestion) separada
  de eventos de sistema (GcCompromisoEvento). TRM mensual en GC_TRM. Excel EMI
  generable. GestCom NO emite facturas — eso lo hace FACTRO (sistema externo DIAN).
- Mundo 4: absorbido por el Mundo 3 — ya no existe como módulo separado.

Cómo trabajamos:
- Cambios mínimos: corrección puntual, no reescritura.
- Archivos descargables (present_files), no inline salvo fragmentos.
- Especificar antes de codificar para features nuevos (alcance + backlog Fx.y antes
  de tocar código).
- Verificar el estado del repo en GitHub antes de proponer cambios sobre archivos
  del Mundo 2 o 3.
- Reglas de negocio formato RB-XX, backlog formato Fx.y, estados con emojis
  (🔲 pendiente / 🟡 en curso / ✅ completo).
- Errores: GlobalExceptionHandler debe seguir mostrando causa raíz; no envolver
  en mensajes genéricos durante desarrollo.

Convenciones obligatorias:
- Naming: Gc* en entidades JPA, GC_* en tablas, CAT_* en catálogos. Endpoints
  /api/v1/{recurso-plural-español}. Idioma del dominio en español.
- Auditoría obligatoria (fechaCreacion, fechaModificacion, usuarioCreacion,
  usuarioModificacion) al final de cada entidad de negocio.
- Sufijos DTO: *CreateRequest / *UpdateRequest / *Response / *Summary. Preferir
  record (Java 21). Método fromEntity en Response.
- DDL: PK NUMBER(19) GENERATED BY DEFAULT AS IDENTITY, FKs nombradas FK_*, CHECKs
  CK_*, índices IDX_*.

Lecciones aprendidas no negociables:
1. @OrderBy apunta al field Java, no a la columna Oracle.
2. nullable=false en FKs solo si la FK siempre se popula.
3. Sincronizar constraints CHECK con cada nuevo valor de enum.
4. JOIN FETCH explícito en consultas de detalle (lazy-load fuera de transacción
   explota al serializar).
5. mvn clean package -DskipTests después de cambios en @OrderBy / @JoinColumn /
   anotaciones JPA críticas.

Hoy quiero trabajar en: [describir aquí la tarea concreta de la sesión].
```

---

## 8. Anexo — Discrepancias del prompt con el estado real

El prompt de migración mencionó dos cosas que **no corresponden** al estado real del Project; quedan documentadas para que la nueva conversación no se confunda:

| Mencionado en el prompt | Realidad del Project |
|-------------------------|----------------------|
| "Design system de ArquitectSOFT, paleta navy/teal/orange" | El sistema es **"Luxury Tech"** con paleta **cyan #00D4FF + violeta #A855F7** sobre fondos dark (`#0A0A14` / `#13131F`). |
| "IBM Plex" | La tipografía oficial en `gestcom-frontend.SKILL.md` es **Inter**. (Históricamente, el prototipo de marzo 2026 usó Outfit + JetBrains Mono, pero la skill consolida Inter.) |
| "4 mundos" (en las instrucciones del Project) | La realidad consolidada a mayo 2026 son **3 mundos**: el Mundo 4 quedó absorbido por el Mundo 3. |

Si en la nueva conversación se pide trabajar con navy/teal/orange o IBM Plex, hay que detenerse y aclarar con Hector si quiere mantener el Luxury Tech (lo construido) o si efectivamente quiere repaletizar la plataforma.

---

*Documento generado el 6 de mayo de 2026 como punto de partida para reconstruir contexto en una conversación nueva o migrar el Project.*
