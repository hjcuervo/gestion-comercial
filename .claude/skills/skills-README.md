# GestCom — Skills de Claude

Esta carpeta contiene las **skills** que Claude carga automáticamente cuando se trabaja en el proyecto GestCom. Las skills son instrucciones especializadas que garantizan consistencia entre sesiones, evitan errores ya documentados y aplican las convenciones del proyecto sin necesidad de recordárselas en cada chat.

---

## Skills incluidas

| # | Skill | Cuándo se activa | Propósito |
|---|-------|------------------|-----------|
| 1 | **gestcom-context** | Cualquier tarea de GestCom | Contexto base: 4 mundos, stack, glosario, estado actual, lecciones aprendidas. Es la skill **raíz** — Claude la carga primero. |
| 2 | **gestcom-backend** | Tareas Spring Boot / Java / Oracle | Convenciones de entidades JPA, repos, services, controllers, DTOs y DDL. Incluye el checklist anti-errores. |
| 3 | **gestcom-frontend** | Tareas Vue 3 / Vite / Pinia | Sistema de diseño "Luxury Tech", estructura de vistas, services, stores, router y NavRail. |

---

## Cómo se activan

Claude lee la descripción (frontmatter `description`) de cada `SKILL.md` para decidir cuál cargar. Si la tarea coincide con una skill, lee el cuerpo del archivo completo **antes** de empezar a trabajar.

**Patrón de uso típico:**
- "Ayúdame con GestCom..." → carga `gestcom-context`.
- "Crea la entidad GcFactura..." → carga `gestcom-context` + `gestcom-backend`.
- "Diseña la vista de facturas..." → carga `gestcom-context` + `gestcom-frontend`.
- "Modifica el endpoint de contratos y la vista..." → carga las tres.

---

## Estado congelado

Estas skills reflejan el estado del proyecto en **Mayo 2026**, congelando:

- Mundo 1 (Comercial) ✅ implementado.
- Mundo 2 (Contractual) 🟡 en F5 completo, F6 pendiente, issues técnicos abiertos.
- Mundos 3 y 4 🔲 sin especificar.

**Cuando se especifique e implemente el Mundo 3, las skills deben actualizarse** para reflejar las nuevas convenciones (entidades de facturación, endpoints, vistas, etc.).

---

## Cómo evolucionarlas

1. Las skills viven bajo control de versiones en este repo (`.claude/skills/`).
2. Se modifican como cualquier otro archivo del proyecto (PRs, code review).
3. Cuando una decisión de diseño cambie (ej. se elimina `nullable = false` de un FK), actualizar la skill correspondiente para que la lección quede grabada.
4. Cuando surjan nuevas skills (ej. `gestcom-database` para DDL más complejo, o `gestcom-spec` para plantillas de especificación), agregarlas como nuevas carpetas hermanas.

---

## Compatibilidad

Estas skills funcionan con Claude en cualquiera de sus interfaces (Claude.ai, Claude Code, API). El formato `SKILL.md` con frontmatter YAML es el estándar oficial.

---

## Próximas skills sugeridas (no construidas aún)

- **gestcom-database** — convenciones DDL avanzadas, migraciones, sincronización entidad↔tabla.
- **gestcom-spec** — plantilla de documento de alcance, formato de backlog Fx.y, formato de reglas RB-XX.

Estas se construirán cuando el proyecto las necesite.

---

*Última actualización: Mayo 2026.*
