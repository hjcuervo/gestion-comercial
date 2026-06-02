# GestCom — Alcance: Reorganización en Módulos de Dominio (v1)

**Empresa:** Arquitecsoft SAS
**Producto:** GestCom — Plataforma de Gestión Comercial, Contractual y Flujo de Facturación
**Repositorio:** https://github.com/hjcuervo/gestion-comercial
**Tipo de cambio:** Arquitectura de información + capa de presentación (navegación). **No toca modelo de datos, entidades ni reglas de negocio.** Puede requerir endpoints nuevos de KPIs (ver D-6).
**Estado:** Borrador para acordar antes de codificar.

> **Nota de estado (leer primero).** Este alcance asume la arquitectura frontend vigente según `gestcom-frontend.SKILL.md`: identidad **"Instrumento"**, `AppShell` con **barra superior** y las 3 plantillas `Operativo` / `Tablero` / `Panel`. Las instrucciones congeladas del Project mencionan NavRail + "Luxury Tech" (estado anterior). Si `main` todavía no tiene el rediseño "Instrumento" mergeado, esta reorganización debe **integrarse dentro del plan de rediseño (RF)** y no plantearse como capa independiente. **Confirmar antes de avanzar a backlog ejecutable.**

---

## 1. Propósito

Hoy la navegación es **plana**: todas las pantallas son módulos hermanos en la barra superior del `AppShell` (Dashboard, Empresas, Personas, Oportunidades, Pipeline, Contratos, Facturación…). Esto no comunica que la plataforma tiene **tres dominios de negocio** con vida propia.

El requerimiento es pasar a una **navegación de 2 niveles**:

- **Nivel 1 — Módulos de dominio (3):** *Proceso de Venta*, *Contratación*, *Facturación*.
- **Nivel 2 — Pantallas operativas** dentro de cada módulo, con su **Dashboard de KPIs como pantalla de aterrizaje**.

Es un cambio de **cómo se organiza y se usa** la plataforma. No cambia qué hace cada pantalla.

---

## 2. Mapeo a los 3 Mundos

| Módulo (Nivel 1) | Mundo | Dashboard | Pantallas operativas (Nivel 2) |
|------------------|-------|-----------|--------------------------------|
| **Proceso de Venta** | Mundo 1 — Comercial | ✅ **El que ya existe** (KPIs comerciales) | Pipeline (Tablero), Oportunidades / Actividades (Operativo), Empresas (Operativo), Personas (Operativo) |
| **Contratación** | Mundo 2 — Contractual | 🆕 **A definir** (KPIs contractuales) | Contratos (lista + detalle + formas de pago/compromisos + modificaciones) |
| **Facturación** | Mundo 3 — Flujo de Facturación | 🆕 **A definir** (KPIs de facturación) | Tablero de compromisos pendientes, Facturas + cruce, TRM, Export Excel EMI |

> **Importante:** el Dashboard de Venta **existe y se reutiliza**. Los Dashboards de **Contratación** y **Facturación** son **nuevos** y requieren mini-spec de KPIs (D-4, D-5) y, posiblemente, endpoints backend (D-6). El frontend de Mundo 3 está ~25%; su módulo se define ahora aunque sus pantallas operativas se construyan en paralelo (ver dependencia en F4).

---

## 3. Modelo de navegación propuesto

### 3.1 Estructura

```
AppShell (barra superior)
│
├── [Nivel 1] Selector de MÓDULO de dominio
│     ● Proceso de Venta   ● Contratación   ● Facturación
│
└── Al entrar a un módulo:
      ├── Aterriza en su DASHBOARD (plantilla Panel — KPIs)
      └── [Nivel 2] Navegación secundaria del módulo
            (Dashboard + sus pantallas operativas)
```

- Cambiar de módulo en el Nivel 1 → aterriza en el **Dashboard** de ese módulo.
- El Nivel 2 muestra solo las pantallas **del módulo activo** (Dashboard incluido como primera opción).
- Cada pantalla operativa sigue usando su plantilla actual (`Operativo` / `Tablero`); el Dashboard usa `Panel`.

### 3.2 Qué cambia en el `AppShell`

- La barra superior deja de listar **7 pantallas planas** y pasa a listar **3 módulos de dominio**.
- Aparece una **navegación secundaria** (del módulo activo). Recomendación: tira de pestañas/segmentos bajo la barra superior (sobrio, alineado a "Instrumento"). Alternativas en D-2.
- Concepto nuevo de **"módulo activo"**: se deriva de `route.meta.module` (`'venta' | 'contratacion' | 'facturacion'`) vía un composable `useModule` (mismo patrón que `useShell`/`useTheme`).
- `Ctrl+K` (command palette): los comandos se **agrupan por módulo**.

### 3.3 Qué NO cambia

- Backend, entidades, endpoints existentes, reglas de negocio (RB-XX / RN-XX).
- Capa de datos del frontend (`services`, `stores`, `utils`).
- Identidad **"Instrumento"** y tokens `--gc-*`.
- Las **3 plantillas** (`Operativo` / `Tablero` / `Panel`) como capa de render: se reutilizan tal cual.
- El flujo de negocio crítico (GANADA → contractual → Formalizar → VIGENTE → CONTRATADA).

---

## 4. Los tres módulos en detalle

### 4.1 Proceso de Venta (Mundo 1)

- **Dashboard:** el existente (`dashboard.service`). Sin cambios funcionales; solo pasa a ser el aterrizaje del módulo.
- **Operativas:** Pipeline (Tablero), Oportunidades/Actividades (Operativo), Empresas (Operativo), Personas (Operativo). **Ya existen en "Instrumento"; solo se reagrupan** bajo el módulo. Esfuerzo bajo.

### 4.2 Contratación (Mundo 2)

- **Dashboard (nuevo):** KPIs candidatos a confirmar (D-4): contratos VIGENTES, valor total vigente, contratos por estado, próximos a vencer, modificaciones recientes, contratos por tipo.
- **Operativas:** Contratos (lista + detalle + formas de pago/compromisos + modificaciones), acciones de estado (suspender/terminar/liquidar). Ya existen; se reagrupan.
- **Verificar `main` antes de tocar archivos del Mundo 2** (regla del proyecto).

### 4.3 Facturación (Mundo 3)

- **Dashboard (nuevo):** KPIs candidatos a confirmar (D-5): compromisos pendientes del mes, presupuestado vs. facturado, % cumplimiento, facturas registradas/cruzadas, monto consolidado en COP (TRM), top por empresa.
- **Operativas:** tablero de compromisos pendientes, facturas + cruce, TRM editable, export Excel EMI.
- **Dependencia:** el frontend del Mundo 3 está ~25%. El **contenedor del módulo** (Nivel 1/2 + Dashboard) se puede definir ya; las pantallas operativas se completan según avance el Mundo 3.

---

## 5. Backlog propuesto (Fx.y)

> Numeración propia de esta iniciativa. Esfuerzo relativo: S / M / L.

### F1 — Chasis de navegación de 2 niveles *(dependencia de todo lo demás)*
| Tarea | Descripción | Esf. |
|-------|-------------|------|
| T1.1 | Definir `route.meta.module` por ruta + composable `useModule` (módulo activo). | S |
| T1.2 | `AppShell`: Nivel 1 (3 módulos) + navegación secundaria del módulo activo. | M |
| T1.3 | Router: agrupar rutas por módulo y fijar el Dashboard como aterrizaje de cada módulo (ver D-3). | M |
| T1.4 | `Ctrl+K`: agrupar comandos por módulo. | S |
**Aceptación:** cambiar de módulo aterriza en su Dashboard; el Nivel 2 muestra solo las pantallas de ese módulo; rutas profundas + botón "atrás" funcionan; resaltado de módulo activo correcto.

### F2 — Módulo Proceso de Venta *(reubicación, sin rehacer pantallas)* *[dep: F1]*
| Tarea | Descripción | Esf. |
|-------|-------------|------|
| T2.1 | Dashboard existente como aterrizaje del módulo. | S |
| T2.2 | Reubicar Pipeline, Oportunidades/Actividades, Empresas, Personas bajo el módulo. | M |
**Aceptación:** todo Mundo 1 navegable dentro del módulo Venta sin pérdida de funcionalidad.

### F3 — Módulo Contratación *[dep: F1]*
| Tarea | Descripción | Esf. |
|-------|-------------|------|
| T3.1 | Reubicar Contratos bajo el módulo. | M |
| T3.2 | Sub-spec: definir KPIs del Dashboard de Contratación (D-4). | S |
| T3.3 | Implementar Dashboard de Contratación (`Panel` + `GcStat`). Requiere endpoints (D-6). | M |
**Aceptación:** módulo Contratación con su Dashboard y Contratos operativo. *(Verificar `main` antes de tocar Mundo 2.)*

### F4 — Módulo Facturación *[dep: F1; sigue avance del Mundo 3]*
| Tarea | Descripción | Esf. |
|-------|-------------|------|
| T4.1 | Reubicar lo existente de Facturación bajo el módulo. | M |
| T4.2 | Sub-spec: definir KPIs del Dashboard de Facturación (D-5). | S |
| T4.3 | Implementar Dashboard de Facturación. Requiere endpoints (D-6) y avance de Mundo 3. | M |
**Aceptación:** módulo Facturación con su contenedor y Dashboard; pantallas operativas integradas a medida que el Mundo 3 las complete.

### F5 — Transversales y cierre *[dep: F2–F4]*
| Tarea | Descripción | Esf. |
|-------|-------------|------|
| T5.1 | Actualizar `gestcom-frontend.SKILL.md` con el modelo de navegación de 2 niveles. | M |
| T5.2 | QA integral de IA: highlighting de módulo, rutas profundas, `Ctrl+K` agrupado. | S |
**Aceptación:** navegación de 2 niveles estable; skill alineada.

---

## 6. Decisiones abiertas

| # | Decisión | Recomendación |
|---|----------|---------------|
| D-1 | Etiqueta del Módulo 1: "Proceso de Venta" vs. "Comercial" vs. "Gestión Comercial". | "Proceso de Venta" (tu propuesta) o "Comercial" (más corto en barra). |
| D-2 | Forma de la navegación secundaria (Nivel 2): pestañas/segmentos bajo la barra superior, rail lateral, o menú desplegable por módulo. | Pestañas/segmentos bajo la barra superior (sobrio, "Instrumento"). |
| D-3 | Estructura de rutas: prefijo por módulo (`/venta/...`, `/contratacion/...`, `/facturacion/...`) vs. rutas planas + `meta.module`. | Prefijo por módulo (URLs autoexplicativas y agrupables). |
| D-4 | KPIs del Dashboard de **Contratación** (lista en §4.2). | Confirmar/ajustar la lista candidata. |
| D-5 | KPIs del Dashboard de **Facturación** (lista en §4.3). | Confirmar/ajustar la lista candidata. |
| D-6 | ¿Existen endpoints de KPIs para Contratación/Facturación o hay que crearlos en backend? | Verificar; si no existen, esta iniciativa deja de ser "solo frontend" y suma tareas backend. |
| D-7 | ¿El rediseño "Instrumento" está realmente mergeado a `main`? (ver Nota de estado). | Confirmar antes de F1; condiciona si esto es capa propia o parte del RF. |

---

## 7. Lo que NO entra en este alcance

- Cambios al modelo de datos, entidades o reglas de negocio.
- Rehacer pantallas operativas ya existentes (solo se reubican; el re-skin a "Instrumento", si falta, pertenece al plan de rediseño RF).
- Completar el frontend pendiente del Mundo 3 (eso es trabajo del Mundo 3; aquí solo se define su contenedor de módulo y Dashboard).

---

*Documento de alcance — Reorganización en módulos de dominio, v1. Base para acordar antes de codificar.*
