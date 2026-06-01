# GestCom — Plan de Trabajo: Rediseño Frontend Completo (v1)

**Empresa:** Arquitecsoft SAS · **Producto:** GestCom
**Repositorio:** https://github.com/hjcuervo/gestion-comercial
**Premisa:** Reemplazo **total** de la capa de presentación (cambio de fondo). **Sin convivencia** con el sistema actual. Backend, modelo de datos, endpoints y reglas de negocio **no se tocan**.
**Identidad objetivo:** "Instrumento" — fundación clara, primario grafito, color solo para estado, datos en monoespaciado, renglones en vez de tarjetas, teclado a la vista. (Ver `GestCom_Alcance_Rediseno_UI_v1.md`.)

---

## 1. Estado actual real (inventario del repo)

**Capa de datos (se reutiliza, no se toca):**
- `services/`: actividad, api, auth, contrato, dashboard, documento, empresa, facturacion, oportunidad, persona, pipeline, procesoContratacion.
- `stores/`: auth, empresa, oportunidad, persona, pipeline.
- `utils/`: currency, pagination.

**Capa de presentación (se reemplaza/elimina):**
- **Layout (9, duplicados):** `AppLayout`, `AuroraHeader`, `AuroraLayout`, `AuroraSidebar`, `Header`, `Layout`, `NavRail`, `Sidebar`, `TopAppBar`.
- **UI (13, con triplicados):** `AuroraButton`/`Button`/`MdButton`, `AuroraCard`/`Card`/`MdCard`, `AuroraInput`/`Input`/`MdTextField`, `AuroraStatCard`/`StatCard`, `Icon`, `MoneyInput`.
- **Estilos:** `assets/styles/tokens.css` (oscuro "Luxury Tech"), `global.css`, `style.css` (duplicado).
- **Vistas (10):** Login, Dashboard, Empresas, Personas, Pipeline, OportunidadDetalle, OportunidadesList (huérfana, sin ruta), ContratosList, ContratoDetalle, Facturacion.
- **Modales/cards por dominio:** actividad, contrato, documento, empresa, facturacion, oportunidad, persona, pipeline.
- **Restos:** `HelloWorld.vue`, `router/index.js.old`.

**Cableado actual:** `App.vue` es solo `<router-view/>`; cada vista se auto-envuelve en `<AppLayout>`. No hay `/oportunidades` lista (solo `/oportunidades/:id`).

---

## 2. Arquitectura objetivo

- **Shell único** (`AppShell.vue`) montado desde `App.vue` según `route.meta.layout` (`'blank'` para login, `'app'` por defecto). Las vistas **dejan de auto-envolverse**.
- **3 plantillas por presencia de slots** del shell: Operativo (`#master` + default + `#aside`), Tablero (solo default), Panel (solo default, grilla).
- **Tokens globales** en `:root` (claro "instrumento"), reemplazando `tokens.css`. Fuentes DM Sans + DM Mono.
- **Biblioteca de componentes nueva** (prefijo `Gc`), una sola por tipo.
- **Iconos:** Tabler outline (webfont), un solo set.
- **Datos:** servicios/stores/utils intactos.

---

## 3. Estrategia de ejecución

1. **Rama dedicada** `feature/rediseno-instrumento`. Todo el rediseño vive ahí; `main` intacto.
2. **Migración vertical:** se migra pantalla por pantalla a la nueva arquitectura; cada pantalla queda funcional contra el backend antes de pasar a la siguiente.
3. **Demolición progresiva:** al migrar una pantalla, se eliminan los componentes viejos que deja de usar. Lo que quede sin referencias se borra en la fase final.
4. **Cutover de `App.vue`** al inicio (RF1): el shell se monta globalmente; las vistas aún no migradas se tocan para quitarles su `<AppLayout>` propio a medida que entran.
5. **Merge final:** cuando todas las pantallas estén migradas y la demolición completa, se etiqueta `main` (respaldo) y se mergea la rama.

> Durante la rama puede haber estados transitorios imperfectos; eso es aceptable porque `main` sigue estable y solo se integra el resultado terminado.

---

## 4. Mapa de demolición

| Se elimina | Reemplazado por |
|------------|-----------------|
| `AppLayout`, `AuroraLayout`, `Layout`, `AuroraHeader`, `Header`, `TopAppBar`, `AuroraSidebar`, `Sidebar`, `NavRail` | `AppShell.vue` (uno solo) |
| `Button`, `MdButton`, `AuroraButton` | `GcButton.vue` |
| `Input`, `MdTextField`, `AuroraInput` | `GcInput` / `GcSelect` / `GcTextarea` |
| `Card`, `MdCard`, `AuroraCard`, `StatCard`, `AuroraStatCard` | `GcStat` / `GcStatStrip` (los datos van en renglones, no en cards) |
| `MoneyInput` | `GcMoneyInput` (reusa `utils/currency`) |
| `Icon` | `GcIcon` (Tabler webfont) |
| `tokens.css` (oscuro), `style.css` | `instrument.css` (tokens globales) + `global.css` (reset mínimo) |
| `HelloWorld.vue`, `router/index.js.old` | — (borrar) |
| Modales por dominio | Re-skin o conversión a `GcDrawer`/inline según pantalla |

---

## 5. Biblioteca de componentes (nueva)

| Componente | Rol |
|-----------|-----|
| `AppShell` | Shell: barra superior + zonas por slots. (Ya construido en F-UI1; pasa a global.) |
| `GcButton` | Botón (variantes: default, primary grafito, danger). |
| `GcInput`, `GcSelect`, `GcTextarea`, `GcMoneyInput` | Campos. |
| `GcBadge` | Marca de estado (color = estado). |
| `GcListRow` | Renglón de lista/bitácora (con marca de estado lateral). |
| `GcStat`, `GcStatStrip` | Franja compacta de métricas. |
| `GcModal`, `GcDrawer` | Diálogo / cajón lateral para formularios puntuales. |
| `GcIcon` | Icono Tabler outline. |
| `GcEmpty`, `GcSpinner` | Estados vacío / carga. |

---

## 6. Plan por fases

> Nomenclatura: `RF` = Rediseño-Fase; tareas `RFx.y`. Esfuerzo relativo: S / M / L.

### RF1 — Fundaciones y chasis  *(dependencia de todo lo demás)*
| Tarea | Descripción | Esf. |
|-------|-------------|------|
| RF1.1 | Crear rama `feature/rediseno-instrumento`; etiquetar estado actual de `main`. | S |
| RF1.2 | `instrument.css` como **tokens globales** en `:root` (claro); eliminar referencias a `tokens.css`. | M |
| RF1.3 | `global.css` → reset mínimo + tipografía base global (DM Sans/Mono). Borrar `style.css`. | S |
| RF1.4 | Integrar Tabler icons (webfont) + `GcIcon`. | S |
| RF1.5 | `App.vue`: resolución de layout por `meta.layout` (`blank` / `app`) montando `AppShell`. | M |
| RF1.6 | `AppShell` global (barra superior + zonas por slots) — adaptado de F-UI1. | M |
| RF1.7 | Biblioteca base: `GcButton`, `GcInput`, `GcSelect`, `GcTextarea`, `GcBadge`, `GcListRow`, `GcStat(Strip)`, `GcModal`, `GcDrawer`, `GcEmpty`, `GcSpinner`. | L |
| RF1.8 | `router/index.js`: limpieza, `meta.layout` por ruta; borrar `index.js.old`. | S |
| RF1.9 | Migrar **Login** (plantilla `blank`) como primera validación del chasis. | S |
**Aceptación:** la app arranca con la nueva barra superior, login funciona end-to-end, biblioteca base renderiza en una página de prueba.

### RF2 — Plantilla Operativo + Consola de Actividades (Mundo 1)  *[dep: RF1]*
| Tarea | Descripción | Esf. |
|-------|-------------|------|
| RF2.1 | Vista `/actividades` con plantilla Operativo (rutas anidadas: lista persiste, `:id` en el centro). | M |
| RF2.2 | `#master`: lista maestra de oportunidades (búsqueda, filtro, renglones) — usa `oportunidad.service`. | M |
| RF2.3 | Centro: bitácora de actividades + compromisos agrupados (Vencidas/Próximas/Completadas) — `actividad.service`. | L |
| RF2.4 | `#aside`: captura — registrar actividad (`{oportunidadId,tipoActividadId,fechaHora,notas}`) y encadenar compromiso (`crearCompromiso(actividadId,{descripcion,fechaCompromiso})`). | L |
| RF2.5 | Marcar compromiso completado (`actualizarCompromiso`) inline. | M |
| RF2.6 | Eliminar `ActividadModal`, `CompromisoModal` y componentes de actividad superados. | S |
**Aceptación:** registrar actividad, programar compromiso y completarlo, sin recargar página, contra backend real.

### RF3 — Comercial: Pipeline + Oportunidad detalle  *[dep: RF1, RF2]*
| Tarea | Descripción | Esf. |
|-------|-------------|------|
| RF3.1 | Pipeline Kanban → plantilla Tablero (a todo el ancho). | L |
| RF3.2 | Oportunidad detalle → superficie Operativo (resumen + actividades + proceso contractual). | L |
| RF3.3 | Re-skin/Drawer de `OportunidadModal`, `CerrarOportunidadModal`, `EtapaModal`, `PipelineModal`. | M |
| RF3.4 | Resolver IA: la lista huérfana `OportunidadesListView` se absorbe en Operativo o se elimina. | S |
| RF3.5 | Demolición de layout/UI viejos que estas vistas dejen de usar. | S |
**Aceptación:** mover etapas en el Kanban, abrir detalle, cerrar oportunidad — todo en la nueva identidad.

### RF4 — Dashboard (Panel)  *[dep: RF1]*
| Tarea | Descripción | Esf. |
|-------|-------------|------|
| RF4.1 | Dashboard → plantilla Panel (grilla de indicadores con `GcStat`). | L |
| RF4.2 | Sustituir KPI cards/gradientes por franja de indicadores e instrumentos sobrios. | M |
**Aceptación:** KPIs reales del `dashboard.service` en la nueva estética.

### RF5 — Contractual: Contratos lista + detalle (Mundo 2)  *[dep: RF1]*
| Tarea | Descripción | Esf. |
|-------|-------------|------|
| RF5.1 | Contratos lista → Operativo (`#master` contratos). | M |
| RF5.2 | Contrato detalle → superficie (datos + formas de pago + modificaciones en renglones). | L |
| RF5.3 | `#aside` contractual + acciones de estado (suspender/terminar/liquidar). | M |
| RF5.4 | Re-skin/Drawer de `FormalizarContratoModal`, `IniciarProcesoModal`. | M |
**Aceptación:** ciclo de estados del contrato y gestión de formas de pago en la nueva UI. *(Verificar repo antes de tocar archivos del Mundo 2.)*

### RF6 — Directorio: Empresas + Personas  *[dep: RF1]*
| Tarea | Descripción | Esf. |
|-------|-------------|------|
| RF6.1 | Empresas → Operativo (`#master` empresas; `#aside` contactos/oportunidades). | M |
| RF6.2 | Personas → Operativo (relación multi-empresa). | M |
| RF6.3 | Re-skin/Drawer de `EmpresaModal`, `PersonaModal`, `AsociarEmpresaModal`, `DocumentoModal`. | M |
**Aceptación:** CRUD de empresas/personas en la nueva UI.

### RF7 — Facturación (Mundo 3, en curso)  *[dep: RF1]*
| Tarea | Descripción | Esf. |
|-------|-------------|------|
| RF7.1 | Re-skin de lo existente de Facturación a shell + componentes nuevos. | M |
| RF7.2 | Alinear el frontend pendiente del Mundo 3 a la nueva arquitectura (no rehacer en el sistema viejo). | L |
**Nota:** Mundo 3 frontend está ~25%; lo nuevo se construye ya en la identidad "instrumento".

### RF8 — Demolición final, cutover y transversales  *[dep: RF2–RF7]*
| Tarea | Descripción | Esf. |
|-------|-------------|------|
| RF8.1 | Borrar TODO lo legado sin referencias (9 layouts, UI duplicada, `tokens.css`, `HelloWorld`, etc.). | M |
| RF8.2 | `Ctrl+K` command palette (saltar a módulo/registro + acciones). | M |
| RF8.3 | Toggle de densidad (cómodo/compacto). | S |
| RF8.4 | (Opcional) Modo oscuro suave por toggle, reusando tokens. | M |
| RF8.5 | QA integral; etiquetar `main`; **merge** de la rama. | M |
| RF8.6 | Reescribir skill `gestcom-frontend` (nueva identidad, shell, plantillas, componentes, Tabler, sin fundación oscura). | M |
**Aceptación:** no queda código del sistema viejo; `main` actualizado; skill alineada.

---

## 7. Orden y razón

RF1 primero (todo depende del chasis). RF2 (Actividades) como primer vertical porque su backend está listo y valida la plantilla Operativo. RF3 completa el núcleo comercial. RF4–RF6 son independientes entre sí (pueden reordenarse según prioridad de negocio). RF7 sigue el avance del Mundo 3. RF8 cierra (demolición + transversales + merge).

---

## 8. Validación / QA por fase

- Backend local en `http://localhost:8080/api/v1`; login de pruebas `admin/admin123`.
- Colección Postman existente para smoke (contratoId=26, empresaId=41).
- Por pantalla migrada: cargar datos reales, ejecutar la acción principal (crear/mover/cerrar/etc.), verificar que el `GlobalExceptionHandler` siga exponiendo causa raíz.
- Revisar consola del navegador sin errores y rutas profundas + botón "atrás" funcionando (rutas anidadas).

---

## 9. Riesgos y mitigaciones

| Riesgo | Mitigación |
|--------|------------|
| Romper `main` durante el rediseño | Rama dedicada + tag de respaldo previo al merge. |
| Estados visuales rotos en tránsito | Aislados en la rama; se integra solo el resultado terminado. |
| Divergencia repo vs. local en Mundo 2 | Verificar archivo en GitHub antes de modificar (regla del proyecto). |
| Pérdida de funcionalidad al borrar componentes | Borrar solo tras confirmar cero referencias (grep) y pantalla migrada. |
| Iconografía inconsistente | Un solo set: Tabler outline vía `GcIcon`. |

---

## 10. Decisiones abiertas

| # | Decisión | Recomendación |
|---|----------|---------------|
| P-1 | Nombre de la rama | `feature/rediseno-instrumento`. |
| P-2 | Iconos | Tabler outline (webfont), como el prototipo. |
| P-3 | IA de "Oportunidad detalle" vs. "Actividades" | Detalle como superficie central del Operativo; Actividades es su pestaña principal. |
| P-4 | Modo oscuro suave | Diferido a RF8.4 (opcional). |
| P-5 | Facturación (Mundo 3 en curso) | Construir lo pendiente directamente en la nueva identidad (RF7). |

---

## 11. Lo que NO cambia

Stack (Spring Boot 3.4 + Java 21 + Oracle 23c + Vue 3 + Vite + Pinia) · modelo de datos, entidades, endpoints, reglas de negocio (RB-XX) · servicios, stores y utils del frontend · flujo de negocio crítico (GANADA → contractual → Formalizar → VIGENTE → CONTRATADA).

---

*Plan de trabajo del rediseño frontend — v1. Base para ejecutar por fases en rama dedicada.*
