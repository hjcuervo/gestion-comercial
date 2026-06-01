# GestCom — Alcance del Rediseño de Interfaz (v1)

**Empresa:** Arquitecsoft SAS
**Producto:** GestCom — Plataforma de Gestión Comercial y Contractual
**Repositorio:** https://github.com/hjcuervo/gestion-comercial
**Tipo de cambio:** Capa de presentación (frontend). **No toca backend, entidades, endpoints ni reglas de negocio.**
**Estado:** Borrador para acordar antes de codificar.

---

## 1. Propósito y origen

El rediseño nace de tres molestias concretas con la interfaz actual ("Luxury Tech", modo oscuro):

1. El modo oscuro es pesado para una jornada completa de uso.
2. El diseño actual ocupa mucho espacio: poca información visible a la vez.
3. Demasiados clics y navegación entre páginas.

Las tres apuntan a lo mismo: GestCom estaba diseñada como **vitrina** y se usa como **herramienta operativa**. Este documento define la transición a una **consola operativa**.

Adicionalmente, hay un objetivo de identidad: que GestCom **no se parezca al aspecto genérico** de una plataforma cualquiera. La diferenciación no se busca con decoración, sino llevando la lógica operativa hasta lo visual (ver §3).

---

## 2. Principios de diseño

1. **La operación primero.** Si una decisión visual no ayuda a operar más rápido o ver más, no entra.
2. **El color solo significa estado.** Verde/ámbar/rojo/azul/violeta se reservan exclusivamente para estados de negocio. El color nunca es decorativo.
3. **Los datos van en monoespaciado.** Valores, fechas, IDs, conteos y códigos de estado usan fuente monoespaciada: alinea cifras, las hace comparables y le da a la pantalla carácter de instrumento, no de página de marketing.
4. **Renglones, no tarjetas.** Las listas son renglones separados por líneas finas (hairline), no tarjetas flotando con sombra. Cabe más información y el ojo recorre una bitácora.
5. **El teclado a la vista.** Atajos y `Ctrl+K` visibles: señalan herramienta para quien la usa todo el día y reducen clics.
6. **Densidad con respiro controlado.** Densidad alta por defecto, con toggle cómodo/compacto.
7. **Sin saltos de contexto.** Selección en lista maestra → detalle en la misma pantalla. Captura inline o en panel contextual, no en páginas nuevas.

---

## 3. Identidad visual

### 3.1 Tipografía

| Uso | Familia (propuesta) | Notas |
|-----|---------------------|-------|
| UI / texto | DM Sans (o Inter) | Pesos 400 y 500 únicamente. |
| Datos | DM Mono (o similar) | Todos los valores, fechas, IDs, conteos, abreviaturas de tipo y estado. |

> **Decisión abierta D-1:** confirmar familias exactas (DM Sans + DM Mono vs. conservar Inter como sans).

### 3.2 Paleta

- **Neutros (fundación clara por defecto):** papel tibio para el fondo, superficies blancas, líneas finas de bajo contraste. Cómoda para jornada completa.
- **Primario / marca: grafito (casi negro).** El botón primario y los énfasis de marca son grafito, **no** un color. Esto libera toda la gama cromática para significar estado y, paradójicamente, se ve más distintivo. (Resuelve el choque "verde de marca vs. verde de estado" del prototipo inicial.)
- **Colores de estado:** se conservan los semánticos ya usados en el proyecto (éxito = verde, advertencia = ámbar, peligro = rojo, info = azul, secundario = violeta), aplicados solo a marcas/indicadores de estado.
- **Modo oscuro suave (opcional, diferido):** misma estructura de tokens, valores invertidos a un slate de bajo contraste (nunca negro puro). Activable por toggle.

### 3.3 Tokens semánticos

Los tokens `--lt-*` pasan de **valores fijos** a **tokens semánticos** que cambian de valor según el tema (claro / oscuro). Tabla objetivo (valores claro de referencia, a afinar en F-UI1):

| Token | Rol | Claro (ref.) |
|-------|-----|--------------|
| `--lt-bg` | Fondo de la app | `#F5F4F1` |
| `--lt-surface` | Superficie (paneles, renglones) | `#FFFFFF` |
| `--lt-surface-2` | Superficie alterna / hover | `#F0EEE9` |
| `--lt-border` | Línea fina por defecto | `rgba(0,0,0,0.08)` |
| `--lt-border-strong` | Línea de énfasis | `rgba(0,0,0,0.14)` |
| `--lt-text` | Texto principal | `#1A1917` |
| `--lt-text-2` | Texto secundario | `#6B6860` |
| `--lt-text-3` | Texto terciario / pistas | `#9E9C96` |
| `--lt-primary` | Primario / marca (grafito) | `#1A1917` |
| `--lt-success` | Estado positivo | `#1D9E75` |
| `--lt-warning` | Estado intermedio | `#EF9F27` |
| `--lt-danger` | Estado negativo | `#E24B4A` |
| `--lt-info` | Estado informativo | `#185FA5` |
| `--lt-accent` | Acento secundario | `#534AB7` |

### 3.4 Componentes base

- **Renglón de lista:** marca de estado de 2px a la izquierda + datos en monoespaciado + título en sans + acción al final. Línea fina entre renglones. Sin sombra.
- **Marca/badge de estado:** texto del color del estado; sin relleno llamativo. El estado se lee por color de la marca lateral o de la etiqueta.
- **Botón primario:** grafito, texto claro. **Secundario:** transparente con borde fino. **Destructivo:** borde/texto rojo.
- **Inputs:** superficie alterna, borde fino, foco en grafito.

---

## 4. Arquitectura de layout (el shell)

### 4.1 Un solo shell

Toda la plataforma vive dentro de un único armazón:

- **Barra superior (fija, ~48–52px):** marca + **navegación de módulos** (Dashboard, Empresas, Personas, Oportunidades, Pipeline, Contratos, Facturación…) + `Ctrl+K` (búsqueda y acciones) + menú de usuario.
- **Lista maestra (izquierda, ~260–300px, colapsable):** entidades del módulo actual. Presente en pantallas centradas en entidad; ausente/colapsada en Tablero y Panel.
- **Superficie de trabajo (centro):** contenido principal; su contenido depende de la **plantilla**.
- **Panel contextual (derecha, variable):** captura rápida / datos relacionados / acciones. Contextual por módulo; puede no existir.

> **Cambio principal frente al estado actual:** la navegación de módulos se traslada del **NavRail vertical** a la **barra superior**, liberando el borde izquierdo para la lista maestra. El `AppLayout`/NavRail actuales se retiran.

### 4.2 Tres plantillas

| Plantilla | Zonas activas | Pantallas |
|-----------|---------------|-----------|
| **Operativo** | Lista maestra + superficie + panel contextual | Oportunidades (+ actividades), Contratos, Empresas, Personas |
| **Tablero** | Solo superficie a todo el ancho | Pipeline Kanban |
| **Panel** | Solo superficie (grilla de indicadores) | Dashboard comercial / contractual |

El panel contextual de la plantilla Operativo es **contextual**: en Oportunidades es el formulario de registro de actividad + programar siguiente; en Contrato son las formas de pago / modificaciones; en una oportunidad cerrada (RB-19, solo lectura) queda vacío o muestra solo histórico.

### 4.3 Implementación en Vue (sin "fingir" navegación)

- **`AppShell.vue`** (absorbe el actual `AppLayout`): barra superior + región de contenido.
- **`meta.layout`** por ruta selecciona la plantilla: `'operativo' | 'tablero' | 'panel'`.
- **Rutas anidadas** para el patrón maestro-detalle:
  - Padre `/oportunidades` → monta shell + lista maestra.
  - Hija `/oportunidades/:id` → pinta el detalle en la superficie (`<router-view>` interno).
  - Seleccionar un renglón navega a la hija; la lista permanece montada. **Se conservan URL profunda y botón "atrás".**
- La lista maestra y el panel contextual se proveen como **slots/componentes** de cada vista.

---

## 5. Qué pasa con lo existente (migración)

- **Backend:** sin cambios. Riesgo contenido a presentación.
- **Vistas actuales** (`OportunidadesListView`, `OportunidadDetalleView`, `ContratosListView`, `ContratoDetalleView`, `EmpresasListView`, `DashboardView`, Kanban): se migran **una por una** a su plantilla. Cada pantalla migrada queda funcional de punta a punta antes de pasar a la siguiente.
- **`AppLayout` / NavRail:** se retiran cuando entre la barra superior (F-UI1).
- **Skill `gestcom-frontend`:** se actualiza al cierre del rediseño (nueva identidad, retiro de la fundación oscura, nuevos tokens, las tres plantillas, patrón de rutas anidadas).

---

## 6. Backlog de rediseño (Fx.y)

### F-UI1 — Fundaciones
- **T-UI1.1** Redefinir tokens `--lt-*` como tokens semánticos (claro por defecto); hoja de tokens central.
- **T-UI1.2** Incorporar tipografías (sans UI + mono datos).
- **T-UI1.3** Construir `AppShell.vue` con barra superior + navegación de módulos + slot de contenido.
- **T-UI1.4** Introducir `meta.layout` y el selector de plantilla en el router.
- **T-UI1.5** Retirar `AppLayout`/NavRail.
- **T-UI1.6** Componentes base: renglón, marca de estado, botones, inputs.

### F-UI2 — Plantilla Operativo + Consola de Actividades (Mundo 1) — *primer entregable funcional*
- **T-UI2.1** Plantilla Operativo (lista maestra + superficie + panel contextual) con rutas anidadas.
- **T-UI2.2** Lista maestra de Oportunidades (búsqueda, filtros, renglones).
- **T-UI2.3** Superficie de Actividades: agrupación Vencidas / Próximas / Completadas, marcar completado inline.
- **T-UI2.4** Panel contextual: registrar actividad + programar siguiente (compromiso).
- **T-UI2.5** Conexión a endpoints reales (oportunidades, actividades, compromisos).

### F-UI3 — Contratos (Mundo 2) a plantilla Operativo
- **T-UI3.1** Lista maestra de Contratos.
- **T-UI3.2** Superficie de detalle (datos + formas de pago + modificaciones, en renglones).
- **T-UI3.3** Panel contextual contractual + acciones de estado (suspender/terminar/liquidar).

### F-UI4 — Tablero y Panel
- **T-UI4.1** Pipeline Kanban a plantilla Tablero.
- **T-UI4.2** Dashboard a plantilla Panel (grilla de indicadores).

### F-UI5 — Empresas / Personas a plantilla Operativo
- **T-UI5.1** Empresas (lista + detalle + contextual: contactos/oportunidades).
- **T-UI5.2** Personas (relación multi-empresa).

### F-UI6 — Refinamientos
- **T-UI6.1** Modo oscuro suave opcional + toggle de tema.
- **T-UI6.2** Toggle de densidad (cómodo/compacto).
- **T-UI6.3** Command palette `Ctrl+K` (saltar a módulo/registro + acciones).
- **T-UI6.4** Actualizar skill `gestcom-frontend`.

---

## 7. Decisiones abiertas

| # | Decisión | Recomendación |
|---|----------|---------------|
| D-1 | Tipografías exactas | DM Sans + DM Mono. |
| D-2 | ¿Modo oscuro suave en este rediseño o diferido? | Diferido a F-UI6; claro como única fundación inicial. |
| D-3 | ¿Navegación de módulos en barra superior horizontal o rail de iconos colapsado? | Barra superior horizontal (como el prototipo) + `Ctrl+K`. |
| D-4 | Alcance del primer entregable | Consola de Actividades (Mundo 1), porque el backend ya está listo. |

---

## 8. Lo que NO cambia

- Stack (Spring Boot 3.4 + Java 21 + Oracle 23c + Vue 3 + Vite + Pinia).
- Modelo de datos, entidades, endpoints, reglas de negocio (RB-XX).
- Convenciones de dominio (prefijos `Gc`/`GC_`/`CAT_`, auditoría, formato de backlog).
- El motor de pipelines y el flujo de negocio crítico (GANADA → contractual → Formalizar → VIGENTE → CONTRATADA).

---

*Documento de alcance del rediseño de UI — v1. Base para acordar antes de codificar.*
