---
name: gestcom-backend
description: Use this skill whenever creating, modifying, or reviewing backend code for the GestCom platform — Spring Boot 3.4 + Java 21 + Oracle 23c. Triggers include writing JPA entities, repositories, services, controllers, DTOs, exception handlers, or DDL for the gestion-comercial project; adding endpoints under /api/v1/; modifying audit fields; working with @OrderBy, @JoinColumn, JOIN FETCH, BusinessException, SecurityUtils, PageResponse, or fixing Hibernate ORA errors. Always load gestcom-context first to align with the broader project conventions and lessons learned.
---

# GestCom — Convenciones Backend

Esta skill define **cómo se construye código backend en GestCom**. Aplica todas las reglas aquí descritas al generar entidades, repositorios, servicios, controladores, DTOs y DDL.

> **Prerequisito:** Cargar primero `gestcom-context`.
>
> **Fuente de verdad:** los patrones aquí descritos se validaron contra el código en `main` (rama actual del repo) el 19-may-2026. Si el código real diverge de esta skill, **gana el código real** — actualizar la skill.

---

## 1. Estructura de paquetes

```
com.arquitecsoft.gestion/
├── GestionApplication.java
├── config/                          (solo HealthController — NO config global)
├── domain/
│   └── {dominio}/                   ej: contrato, oportunidad, facturacion
│       ├── entity/                  entidades JPA
│       ├── repository/              interfaces JpaRepository
│       ├── service/                 lógica de negocio
│       ├── controller/              REST controllers
│       └── dto/                     request/response DTOs (clases POJO)
└── infrastructure/
    ├── config/                      CorsFilter
    ├── dto/                         PageResponse<T> + PageInfo
    ├── exception/                   BusinessException, ErrorResponse, GlobalExceptionHandler
    └── security/                    JwtAuthenticationFilter, JwtService, SecurityConfig, SecurityUtils, AuthenticatedUser
```

**Reglas:**

1. Un dominio nuevo crea su propia carpeta bajo `domain/`. No mezclar entidades de dominios distintos.
2. **Toda la infraestructura transversal vive en `infrastructure/`.** No crear `security/`, `audit/` o `config/` al nivel raíz; tampoco un `dto/` global fuera de `infrastructure/dto/`.
3. El paquete raíz `config/` está reservado para componentes muy específicos (hoy solo `HealthController`).
4. **No existe paquete `audit/`** — la auditoría son 4 columnas por entidad (§7.3 del contexto).

---

## 2. Entidades JPA — Plantilla canónica

Basada en `GcContrato.java` real.

```java
package com.arquitecsoft.gestion.domain.contrato.entity;

import com.arquitecsoft.gestion.domain.empresa.entity.GcEmpresa;
import com.arquitecsoft.gestion.domain.oportunidad.entity.GcOportunidad;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "GC_CONTRATO")
public class GcContrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FKs siempre LAZY por defecto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proceso_contratacion_id")  // SIN nullable=false (lección aprendida)
    private GcProcesoContratacion procesoContratacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oportunidad_id", nullable = false)
    private GcOportunidad oportunidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private GcEmpresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_contrato_id", nullable = false)
    private GcTipoContrato tipoContrato;

    // Campos de negocio
    @Column(name = "numero_contrato_interno", length = 50)
    private String numeroContratoInterno;

    @Column(name = "moneda", length = 3)
    private String moneda = "COP";

    @Column(name = "valor_contrato", precision = 16, scale = 2)
    private BigDecimal valorContrato;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    // Enum anidado + EnumType.STRING (debe haber CHECK constraint en BD)
    @Column(name = "estado", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private EstadoContrato estado = EstadoContrato.VIGENTE;

    // Auditoría (FK a usuario, NO string)
    @Column(name = "creado_por", nullable = false)
    private Long creadoPor;

    @Column(name = "modificado_por")
    private Long modificadoPor;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    // Colecciones: cuidado con MultipleBagFetchException si hay más de una List
    @OneToMany(mappedBy = "contrato", cascade = CascadeType.ALL)
    @OrderBy("fechaCreacion DESC")  // nombre del FIELD Java, no de la columna
    private List<GcContratoModificacion> modificaciones = new ArrayList<>();

    public enum EstadoContrato {
        VIGENTE, SUSPENDIDO, TERMINADO, LIQUIDADO
    }

    public GcContrato() {}

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaModificacion = LocalDateTime.now();
    }

    // Getters / Setters
}
```

**Reglas obligatorias:**

1. Clase: `Gc` + dominio en CamelCase singular.
2. `@Table(name = "GC_*")` siempre con prefijo, incluso para catálogos transversales.
3. FKs con `fetch = FetchType.LAZY` por defecto.
4. **No poner `nullable = false` en FKs salvo que sea estrictamente obligatorio en todos los escenarios de inserción.** (Lección aprendida #2.)
5. **Enums anidados en la entidad** + `@Enumerated(EnumType.STRING)`. Cada valor debe tener su CHECK constraint en Oracle.
6. Auditoría: `creadoPor: Long`, `modificadoPor: Long`, `fechaCreacion: LocalDateTime`, `fechaModificacion: LocalDateTime`. **No usar string** para usuario.
7. `@PrePersist` solo setea timestamps; `creadoPor` se setea desde el service (§4).
8. **`@OrderBy` apunta al nombre del field Java** (`fechaCreacion`), no al nombre de la columna (`fecha_creacion`). Lección aprendida #1.
9. **Máximo una `List` en JOIN FETCH simultáneo** — usar `Set` o `EntityGraph` si se necesitan varias colecciones. Lección aprendida #6.

---

## 3. Repositories

```java
package com.arquitecsoft.gestion.domain.contrato.repository;

import com.arquitecsoft.gestion.domain.contrato.entity.GcContrato;
import com.arquitecsoft.gestion.domain.contrato.entity.GcContrato.EstadoContrato;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GcContratoRepository extends JpaRepository<GcContrato, Long> {

    /**
     * Carga TODAS las relaciones necesarias para serializar el detalle.
     * Si el DTO de respuesta navega a una relación, debe estar en este JOIN FETCH.
     * Cuidado: máximo UNA List collection en JOIN FETCH (MultipleBagFetchException).
     */
    @Query("SELECT c FROM GcContrato c " +
           "JOIN FETCH c.empresa " +
           "JOIN FETCH c.tipoContrato " +
           "LEFT JOIN FETCH c.oportunidad " +
           "LEFT JOIN FETCH c.procesoContratacion " +
           "LEFT JOIN FETCH c.modificaciones " +
           "WHERE c.id = :id")
    Optional<GcContrato> findByIdWithRelations(@Param("id") Long id);

    /**
     * Listado con filtros opcionales — patrón canónico (:param IS NULL OR ...).
     */
    @Query("SELECT c FROM GcContrato c " +
           "JOIN FETCH c.empresa " +
           "JOIN FETCH c.tipoContrato " +
           "WHERE (:estado IS NULL OR c.estado = :estado) AND " +
           "(:empresaId IS NULL OR c.empresa.id = :empresaId)")
    Page<GcContrato> findWithFilters(
            @Param("estado") EstadoContrato estado,
            @Param("empresaId") Long empresaId,
            Pageable pageable);

    List<GcContrato> findByEstado(EstadoContrato estado);
}
```

**Reglas:**

1. Siempre anotar con `@Repository`.
2. **Toda consulta de detalle usa `findByIdWithRelations`** con `JOIN FETCH` (o `LEFT JOIN FETCH` si la relación es opcional) de TODAS las relaciones que el DTO de respuesta serializa. (Lección #4.)
3. **Una sola `List` por JOIN FETCH.** Si necesitas dos colecciones, considera `EntityGraph`, cambia una a `Set`, o haz dos queries separadas.
4. Para filtros opcionales: patrón `(:param IS NULL OR c.campo = :param)` con `@Param`.
5. Pagina con `Pageable` y retorna `Page<Entity>`; el service convierte a `PageResponse<DTO>` (§4).
6. Los enums se pasan como tipo (`EstadoContrato.VIGENTE`) o como string en el JPQL (`c.estado = 'VIGENTE'`). El primero es más type-safe.
7. Métodos derivados (`findByEstado`, `findByEstadoOrderByFechaCreacionDesc`) están bien para queries simples sin relaciones.

---

## 4. Services

```java
package com.arquitecsoft.gestion.domain.contrato.service;

import com.arquitecsoft.gestion.domain.contrato.dto.*;
import com.arquitecsoft.gestion.domain.contrato.entity.*;
import com.arquitecsoft.gestion.domain.contrato.entity.GcContrato.EstadoContrato;
import com.arquitecsoft.gestion.domain.contrato.repository.*;
import com.arquitecsoft.gestion.infrastructure.dto.PageResponse;
import com.arquitecsoft.gestion.infrastructure.exception.BusinessException;
import com.arquitecsoft.gestion.infrastructure.security.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContratoService {

    private final GcContratoRepository contratoRepository;
    private final SecurityUtils securityUtils;
    // otros repos / services inyectados por constructor

    public ContratoService(
            GcContratoRepository contratoRepository,
            SecurityUtils securityUtils) {
        this.contratoRepository = contratoRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional(readOnly = true)
    public ContratoResponse obtenerPorId(Long id) {
        GcContrato contrato = contratoRepository.findByIdWithRelations(id)
            .orElseThrow(() -> new BusinessException(
                "CONTRATO_NOT_FOUND",
                "Contrato no encontrado: " + id));
        return ContratoResponse.fromEntity(contrato);
    }

    @Transactional(readOnly = true)
    public PageResponse<ContratoResponse> listar(String estado, Long empresaId, int page, int pageSize) {
        EstadoContrato estadoEnum = estado != null ? EstadoContrato.valueOf(estado) : null;
        Pageable pageable = PageRequest.of(page - 1, pageSize,  // page es 1-based en API
                Sort.by(Sort.Direction.DESC, "fechaCreacion"));
        Page<GcContrato> p = contratoRepository.findWithFilters(estadoEnum, empresaId, pageable);
        return PageResponse.from(p, ContratoResponse::fromEntity);
    }

    @Transactional
    public ContratoResponse formalizarContrato(FormalizarContratoRequest request) {
        validarReglasFormalizacion(request);

        GcContrato contrato = new GcContrato();
        // mapeo desde DTO...
        contrato.setCreadoPor(securityUtils.getCurrentUserId());

        contrato = contratoRepository.save(contrato);
        // Re-cargar con relaciones para el response
        contrato = contratoRepository.findByIdWithRelations(contrato.getId()).orElse(contrato);
        return ContratoResponse.fromEntity(contrato);
    }

    @Transactional
    public ContratoResponse suspender(Long id) {
        GcContrato c = contratoRepository.findById(id)
            .orElseThrow(() -> new BusinessException("CONTRATO_NOT_FOUND", "Contrato no encontrado"));
        if (c.getEstado() != EstadoContrato.VIGENTE) {
            throw new BusinessException(
                "ESTADO_INVALIDO",
                "Solo se puede suspender un contrato VIGENTE");
        }
        c.setEstado(EstadoContrato.SUSPENDIDO);
        c.setModificadoPor(securityUtils.getCurrentUserId());
        return ContratoResponse.fromEntity(contratoRepository.save(c));
    }

    private void validarReglasFormalizacion(FormalizarContratoRequest request) {
        // RB-XX / RN-XX
    }
}
```

**Reglas:**

1. **Inyección por constructor**, no `@Autowired` en campos. Fields `private final`.
2. **`SecurityUtils` se inyecta** cuando el service necesita el usuario actual (auditoría, validaciones por rol).
3. **Auditoría manual**: `entity.setCreadoPor(securityUtils.getCurrentUserId())` antes del save. `fechaCreacion` se llena por `@PrePersist`.
4. `@Transactional(readOnly = true)` para consultas; `@Transactional` (sin readOnly) para escrituras.
5. **Para errores de negocio: `BusinessException(code, message)`** desde `infrastructure/exception`, NUNCA `RuntimeException` genérica. El `code` es un identificador semántico (`CONTRATO_NOT_FOUND`, `ESTADO_INVALIDO`, `RB_04_VIOLATION`) que el frontend puede usar para diferenciar.
6. Las validaciones de reglas de negocio (RB-XX / RN-XX) viven en el service, **no** en el controller.
7. **Los services exponen DTOs**, nunca entidades JPA.
8. **Después de un `save`** con relaciones que se van a serializar, re-cargar con `findByIdWithRelations` para evitar lazy-load fuera de transacción.
9. **Paginación:** el API recibe `page` 1-based; Spring `PageRequest.of` es 0-based → restar 1. `PageResponse.from(page, Mapper::fromEntity)` arma el response.

---

## 5. Controllers

```java
package com.arquitecsoft.gestion.domain.contrato.controller;

import com.arquitecsoft.gestion.domain.contrato.dto.*;
import com.arquitecsoft.gestion.domain.contrato.service.ContratoService;
import com.arquitecsoft.gestion.infrastructure.dto.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contratos")
public class ContratoController {

    private final ContratoService contratoService;

    public ContratoController(ContratoService contratoService) {
        this.contratoService = contratoService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<ContratoResponse>> listar(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false, name = "empresa_id") Long empresaId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20", name = "page_size") int pageSize) {
        if (pageSize > 100) pageSize = 100;
        return ResponseEntity.ok(contratoService.listar(estado, empresaId, page, pageSize));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContratoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(contratoService.obtenerPorId(id));
    }

    @PostMapping("/formalizar")
    public ResponseEntity<ContratoResponse> formalizar(
            @Valid @RequestBody FormalizarContratoRequest request) {
        ContratoResponse response = contratoService.formalizarContrato(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/suspender")
    public ResponseEntity<ContratoResponse> suspender(@PathVariable Long id) {
        return ResponseEntity.ok(contratoService.suspender(id));
    }
}
```

**Reglas:**

1. Base path: `/api/v1/{recurso-en-plural-español}` (ej. `/contratos`, `/oportunidades`, `/compromisos`).
2. **Listados retornan `PageResponse<DTO>`**, no `List<DTO>`. Detalle retorna `DTO` directo.
3. **Paginación 1-based**: `page` default 1, `page_size` default 20, máximo 100 (normalizar en el controller).
4. **Query params en snake_case**: `empresa_id`, `page_size`. Path params siguen el dominio (`{id}`).
5. Acciones de cambio de estado como sub-recursos: `/{id}/suspender`, `/{id}/terminar`, `/{id}/liquidar`, `/{id}/reiniciar`.
6. Acciones especiales con nombre semántico: `/formalizar`, `/cruzar`, `/anular`, `/revertir`.
7. POST que crea recurso retorna `HttpStatus.CREATED` (201). POST que actualiza/transiciona retorna `200`.
8. `@Valid` sobre `@RequestBody` para activar las validaciones Jakarta del DTO.
9. El controller no lleva lógica de negocio; solo delega al service y normaliza límites de paginación.

---

## 6. DTOs

**Los DTOs son clases POJO**, no records. Esto es la convención actual del proyecto (verificado el 19-may-2026).

### 6.1 DTO Response

```java
package com.arquitecsoft.gestion.domain.contrato.dto;

import com.arquitecsoft.gestion.domain.contrato.entity.GcContrato;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ContratoResponse {

    private Long id;
    private Long empresaId;
    private String empresaNombre;       // relación aplanada a id + nombre
    private Long tipoContratoId;
    private String tipoContratoNombre;
    private String numeroContratoInterno;
    private BigDecimal valorContrato;
    private BigDecimal valorTotal;       // calculado (valorContrato + valorAjuste)
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estado;               // enum serializado como string
    private LocalDateTime fechaCreacion;
    private List<ModificacionResponse> modificaciones;

    public ContratoResponse() {}

    public static ContratoResponse fromEntity(GcContrato c) {
        ContratoResponse r = new ContratoResponse();
        r.setId(c.getId());
        r.setEmpresaId(c.getEmpresa() != null ? c.getEmpresa().getId() : null);
        r.setEmpresaNombre(c.getEmpresa() != null ? c.getEmpresa().getRazonSocial() : null);
        r.setNumeroContratoInterno(c.getNumeroContratoInterno());
        r.setValorContrato(c.getValorContrato());
        r.setValorTotal(c.getValorTotal());
        r.setEstado(c.getEstado() != null ? c.getEstado().name() : null);
        r.setFechaCreacion(c.getFechaCreacion());
        if (c.getModificaciones() != null) {
            r.setModificaciones(c.getModificaciones().stream()
                .map(ModificacionResponse::fromEntity)
                .collect(Collectors.toList()));
        }
        return r;
    }

    /**
     * Versión liviana para listados — sin colecciones.
     */
    public static ContratoResponse fromEntitySimple(GcContrato c) {
        ContratoResponse r = new ContratoResponse();
        r.setId(c.getId());
        r.setEmpresaNombre(c.getEmpresa() != null ? c.getEmpresa().getRazonSocial() : null);
        r.setEstado(c.getEstado() != null ? c.getEstado().name() : null);
        return r;
    }

    // Getters / Setters
}
```

### 6.2 DTO Request

```java
package com.arquitecsoft.gestion.domain.contrato.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public class FormalizarContratoRequest {

    @NotNull(message = "El ID de la oportunidad es requerido")
    private Long oportunidadId;

    @NotNull(message = "El tipo de contrato es requerido")
    private Long tipoContratoId;

    @Size(max = 50)
    private String numeroContratoInterno;

    @Size(max = 1000)
    private String objeto;

    @Size(max = 3)
    private String moneda;

    private BigDecimal valorContrato;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    public FormalizarContratoRequest() {}

    // Getters / Setters
}
```

**Convención de nombres:**
- `*CreateRequest` / `*Request` para creación.
- `*UpdateRequest` para actualización.
- `*Response` para respuesta.
- `*Summary` para listados (versión liviana — opcional, en su lugar se usa `fromEntitySimple`).

**Reglas:**

1. **Clases POJO con constructor vacío + getters/setters.** No usar `record`.
2. **Método estático `fromEntity(Entity)`** en cada Response.
3. Opcionalmente `fromEntitySimple` para versiones livianas sin colecciones (útil en listados).
4. **Aplanar relaciones a `id` + `nombre`** (no exponer objetos completos).
5. **Enums se serializan como string** (`estado.name()` o `estado.toString()`).
6. **`BigDecimal`** para valores monetarios; **`LocalDate`** / **`LocalDateTime`** para fechas.
7. **Validaciones Jakarta** en requests: `@NotNull`, `@NotBlank`, `@Size`, `@Email`, `@Positive`, etc., con mensaje en español.
8. Cuando una entidad tiene un campo calculado (ej. `valorTotal` = `valorContrato + valorAjuste`), exponer el calculado en el response.

---

## 7. Manejo de excepciones

### 7.1 BusinessException

```java
package com.arquitecsoft.gestion.infrastructure.exception;

public class BusinessException extends RuntimeException {
    private final String code;

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() { return code; }
}
```

**Uso:**

```java
throw new BusinessException("CONTRATO_NOT_FOUND", "Contrato no encontrado: " + id);
throw new BusinessException("ESTADO_INVALIDO", "Solo se puede suspender un contrato VIGENTE");
throw new BusinessException("RB_04_MOTIVO_REQUERIDO", "Motivo obligatorio para cierre PERDIDA");
```

**Convención de códigos:**
- `{DOMINIO}_NOT_FOUND` — recurso no encontrado.
- `ESTADO_INVALIDO` — transición de estado no permitida.
- `RB_XX_*` / `RN_XX_*` — violación de regla de negocio específica (refiere a la matriz).
- `VALIDACION_*` — validación de campo más allá de las anotaciones Jakarta.

### 7.2 GlobalExceptionHandler

`@RestControllerAdvice` con handlers para:
- `BusinessException` → 400 (o 404 si el code termina en `_NOT_FOUND`).
- `MethodArgumentNotValidException` → 400 con detalle por campo.
- `Exception` → 500 mostrando la **causa raíz** (la decisión de UX/debugging del proyecto es no envolver excepciones en mensajes genéricos durante desarrollo).

---

## 8. DDL Oracle — Plantilla canónica

```sql
CREATE TABLE GC_CONTRATO (
    id                       NUMBER(19) GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    oportunidad_id           NUMBER(19) NOT NULL,
    proceso_contratacion_id  NUMBER(19),                          -- SIN NOT NULL
    empresa_id               NUMBER(19) NOT NULL,
    tipo_contrato_id         NUMBER(19) NOT NULL,
    numero_contrato_interno  VARCHAR2(50),
    moneda                   VARCHAR2(3)  DEFAULT 'COP',
    valor_contrato           NUMBER(16,2),
    fecha_inicio             DATE,
    fecha_fin                DATE,
    estado                   VARCHAR2(20) NOT NULL,
    creado_por               NUMBER(19)   NOT NULL,                -- FK a usuario
    modificado_por           NUMBER(19),
    fecha_creacion           TIMESTAMP    NOT NULL,
    fecha_modificacion       TIMESTAMP,
    CONSTRAINT FK_GC_CONTRATO_OPP    FOREIGN KEY (oportunidad_id)          REFERENCES GC_OPORTUNIDAD(id),
    CONSTRAINT FK_GC_CONTRATO_PROC   FOREIGN KEY (proceso_contratacion_id) REFERENCES GC_PROCESO_CONTRATACION(id),
    CONSTRAINT FK_GC_CONTRATO_EMP    FOREIGN KEY (empresa_id)              REFERENCES GC_EMPRESA(id),
    CONSTRAINT FK_GC_CONTRATO_TIPO   FOREIGN KEY (tipo_contrato_id)        REFERENCES GC_TIPO_CONTRATO(id),
    CONSTRAINT FK_GC_CONTRATO_USR_C  FOREIGN KEY (creado_por)              REFERENCES GC_USUARIO(id),
    CONSTRAINT FK_GC_CONTRATO_USR_M  FOREIGN KEY (modificado_por)          REFERENCES GC_USUARIO(id),
    CONSTRAINT CK_GC_CONTRATO_ESTADO CHECK (estado IN ('VIGENTE','SUSPENDIDO','TERMINADO','LIQUIDADO'))
);

CREATE INDEX IDX_GC_CONTRATO_OPP    ON GC_CONTRATO(oportunidad_id);
CREATE INDEX IDX_GC_CONTRATO_EMP    ON GC_CONTRATO(empresa_id);
CREATE INDEX IDX_GC_CONTRATO_ESTADO ON GC_CONTRATO(estado);
```

**Reglas:**

1. Tablas con prefijo `GC_` (negocio **y** catálogos).
2. PK siempre `id NUMBER(19) GENERATED BY DEFAULT AS IDENTITY`.
3. FKs nombradas como `FK_{TABLA}_{REFERENCIA}` (versión corta legible).
4. **FKs de auditoría apuntan a `GC_USUARIO(id)`** — `creado_por` y `modificado_por` son `NUMBER(19)`, no `VARCHAR2`.
5. CHECK constraints nombradas como `CK_{TABLA}_{COLUMNA}`.
6. Índices sobre todas las FKs.
7. Índices sobre columnas de estado o filtros frecuentes.
8. **Sincronizar CHECK con enum Java** cuando se agrega un valor (lección #3).
9. **Migraciones Flyway:** archivos en `backend/src/main/resources/db/migration/` como `V{n}__{descripcion}.sql`. Actualmente con deuda (DT-08): solo hay `V1__initial_schema.sql`. Hasta resolver, agregar también un script suelto en `00Docs/` o equivalente, identificando la tarea.

---

## 9. Checklist anti-errores (lecciones aprendidas aplicadas)

Antes de cerrar cualquier cambio backend, verificar:

- [ ] **`@OrderBy` apunta a un field Java existente** en la entidad, no a un nombre de columna.
- [ ] **FKs sin `nullable = false`** salvo que sean obligatorias en TODOS los escenarios de inserción.
- [ ] **Constraints CHECK actualizados** si se agregó un valor a un enum.
- [ ] **`JOIN FETCH` en consultas de detalle** para todas las relaciones serializadas en el response.
- [ ] **Máximo una `List` por JOIN FETCH** (evitar `MultipleBagFetchException`).
- [ ] **`@Transactional` correctamente aplicado** (readOnly en consultas, sin readOnly en escrituras).
- [ ] **DTOs (POJO) separados de entidades** (entidad nunca sale del service).
- [ ] **`fromEntity` cubre todos los campos** del Response.
- [ ] **Auditoría poblada**: `creadoPor` via `securityUtils.getCurrentUserId()` antes del save.
- [ ] **`BusinessException(code, message)` con código semántico**, nunca `RuntimeException` cruda.
- [ ] **Listados devuelven `PageResponse<T>`** con paginación 1-based.
- [ ] **`@Valid` en `@RequestBody`** del controller.
- [ ] **`mvn clean package -DskipTests`** después de cambios en `@OrderBy`, `@JoinColumn` u otras anotaciones JPA críticas.

---

## 10. Cómo entregar archivos

Cuando se generen archivos backend:

1. Cada clase Java en su propio archivo (`.java`).
2. DDL en archivos `.sql` con nombre que indique la tarea (ej. `T1.4_crear_tabla_contrato.sql`). Eventualmente conviene migrarlos a Flyway (`V{n}__{descripcion}.sql`) — ver DT-08.
3. Ruta destino sugerida en el repo, indicada al desarrollador (ej. `backend/src/main/java/com/arquitecsoft/gestion/domain/contrato/entity/GcContrato.java`).
4. Presentar con `present_files`, no pegar inline (a menos que sean fragmentos cortos para discusión).

---

*Esta skill garantiza que el código backend de GestCom mantenga consistencia con los patrones reales del repo y evite los errores ya documentados en la deuda técnica del proyecto.*
