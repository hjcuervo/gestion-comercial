---
name: gestcom-backend
description: Use this skill whenever creating, modifying, or reviewing backend code for the GestCom platform — Spring Boot 3.4 + Java 21 + Oracle 23c. Triggers include: writing JPA entities, repositories, services, controllers, DTOs, exception handlers, or DDL for the gestion-comercial project; adding endpoints under /api/v1/; modifying audit fields; working with @OrderBy, @JoinColumn, JOIN FETCH, or fixing Hibernate ORA errors. Always load gestcom-context first to ensure backend decisions align with the broader project conventions and lessons learned.
---

# GestCom — Convenciones Backend

Esta skill define **cómo se construye código backend en GestCom**. Aplica todas las reglas aquí descritas al generar entidades, repositorios, servicios, controladores, DTOs y DDL.

> **Prerequisito:** Cargar primero `gestcom-context` para tener el panorama del proyecto.

---

## 1. Estructura de paquetes (organización por dominio)

```
com.arquitecsoft.gestion/
├── domain/
│   └── {dominio}/                  ej: contrato, oportunidad, empresa
│       ├── entity/                 entidades JPA
│       ├── repository/             interfaces JpaRepository
│       ├── service/                lógica de negocio
│       ├── controller/             REST controllers
│       └── dto/                    request/response DTOs
├── audit/
├── security/
├── config/                         (incluye GlobalExceptionHandler)
└── GestionComercialApplication.java
```

**Regla:** un dominio nuevo crea su propia carpeta bajo `domain/`. **No mezclar entidades de dominios distintos en la misma carpeta.**

---

## 2. Entidades JPA — Plantilla canónica

```java
package com.arquitecsoft.gestion.domain.contrato.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "GC_CONTRATO")
public class GcContrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // FKs
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oportunidad_id", nullable = false)
    private GcOportunidad oportunidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proceso_contratacion_id")  // SIN nullable=false (RECORDAR LECCIÓN APRENDIDA)
    private GcProcesoContratacion procesoContratacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_contrato_id", nullable = false)
    private GcTipoContrato tipoContrato;

    // Campos de negocio
    @Column(name = "numero_interno", length = 50)
    private String numeroInterno;

    @Column(name = "valor", precision = 18, scale = 2, nullable = false)
    private BigDecimal valor;

    @Column(name = "estado", length = 20, nullable = false)
    private String estado;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    // Auditoría (siempre presente)
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @Column(name = "usuario_creacion", length = 50, nullable = false, updatable = false)
    private String usuarioCreacion;

    @Column(name = "usuario_modificacion", length = 50)
    private String usuarioModificacion;

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

1. Nombre de clase: `Gc` + dominio en CamelCase singular.
2. `@Table(name = "GC_*")` siempre con prefijo.
3. FKs con `fetch = FetchType.LAZY` por defecto.
4. **No poner `nullable = false` en FKs salvo que sea estrictamente obligatorio en todos los escenarios de inserción.** (Lección aprendida: bloqueó inserts de `GcContrato`.)
5. Los 4 campos de auditoría son obligatorios y van al final.
6. `@PrePersist` y `@PreUpdate` para sellar timestamps.
7. **Nunca usar `@OrderBy` apuntando a campos que no existen en la entidad.** Verificar el nombre exacto del field, no del column.

---

## 3. Repositories

```java
package com.arquitecsoft.gestion.domain.contrato.repository;

import com.arquitecsoft.gestion.domain.contrato.entity.GcContrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GcContratoRepository extends JpaRepository<GcContrato, Long> {

    // Consulta con JOIN FETCH para evitar lazy-load fuera de transacción
    @Query("SELECT c FROM GcContrato c " +
           "LEFT JOIN FETCH c.oportunidad " +
           "LEFT JOIN FETCH c.procesoContratacion " +
           "LEFT JOIN FETCH c.tipoContrato " +
           "WHERE c.id = :id")
    Optional<GcContrato> findByIdWithRelations(Long id);

    List<GcContrato> findByEstadoOrderByFechaInicioDesc(String estado);
}
```

**Reglas:**

1. Anotar siempre con `@Repository`.
2. **Cualquier endpoint de detalle debe usar `findByIdWithRelations` con `JOIN FETCH` de TODAS las relaciones que el DTO de respuesta va a serializar.** No confiar en la sesión transaccional del controller.
3. Para listados con relaciones, usar `JOIN FETCH` o paginación con consulta separada.
4. Nombres de métodos derivados siguen la convención de Spring Data (`findBy...OrderBy...`).

---

## 4. Services

```java
package com.arquitecsoft.gestion.domain.contrato.service;

import com.arquitecsoft.gestion.domain.contrato.entity.GcContrato;
import com.arquitecsoft.gestion.domain.contrato.repository.GcContratoRepository;
import com.arquitecsoft.gestion.domain.contrato.dto.ContratoCreateRequest;
import com.arquitecsoft.gestion.domain.contrato.dto.ContratoResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContratoService {

    private final GcContratoRepository contratoRepo;
    // Otros repos / services inyectados

    public ContratoService(GcContratoRepository contratoRepo) {
        this.contratoRepo = contratoRepo;
    }

    @Transactional(readOnly = true)
    public ContratoResponse obtener(Long id) {
        GcContrato contrato = contratoRepo.findByIdWithRelations(id)
            .orElseThrow(() -> new RuntimeException("Contrato no encontrado: " + id));
        return ContratoResponse.fromEntity(contrato);
    }

    @Transactional
    public ContratoResponse crear(ContratoCreateRequest request) {
        // Validaciones de reglas de negocio
        validarReglasCreacion(request);
        GcContrato contrato = new GcContrato();
        // Mapeo desde DTO
        contrato.setUsuarioCreacion(obtenerUsuarioActual());
        contratoRepo.save(contrato);
        return ContratoResponse.fromEntity(contrato);
    }

    @Transactional
    public void cambiarEstado(Long id, String nuevoEstado) {
        // Validar transición permitida
        // ...
    }

    private void validarReglasCreacion(ContratoCreateRequest request) {
        // RB-XX
    }
}
```

**Reglas:**

1. Inyección por constructor (no `@Autowired` en campos).
2. `@Transactional(readOnly = true)` en consultas; `@Transactional` (sin readOnly) en escrituras.
3. Las validaciones de reglas de negocio (RB-XX) viven en el service, **no** en el controller.
4. Los services exponen DTOs, **nunca entidades JPA**.
5. Para `not found`, lanzar excepción que el `GlobalExceptionHandler` convierta en 404.

---

## 5. Controllers

```java
package com.arquitecsoft.gestion.domain.contrato.controller;

import com.arquitecsoft.gestion.domain.contrato.service.ContratoService;
import com.arquitecsoft.gestion.domain.contrato.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contratos")
public class ContratoController {

    private final ContratoService service;

    public ContratoController(ContratoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ContratoResponse>> listar(
            @RequestParam(required = false) String estado) {
        return ResponseEntity.ok(service.listar(estado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContratoResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @PostMapping
    public ResponseEntity<ContratoResponse> crear(@Valid @RequestBody ContratoCreateRequest request) {
        return ResponseEntity.ok(service.crear(request));
    }

    @PostMapping("/{id}/suspender")
    public ResponseEntity<ContratoResponse> suspender(@PathVariable Long id) {
        return ResponseEntity.ok(service.cambiarEstado(id, "SUSPENDIDO"));
    }
}
```

**Reglas:**

1. Base path: `/api/v1/{recurso-en-plural}`.
2. Los recursos en el path son **plurales en español** (`/contratos`, `/oportunidades`, `/empresas`).
3. Acciones de cambio de estado como sub-recursos: `/{id}/suspender`, `/{id}/terminar`, `/{id}/liquidar`.
4. Validaciones con `@Valid` sobre los DTOs de request.
5. El controller no lleva lógica de negocio; solo delega al service.
6. Respuesta: siempre `ResponseEntity<DTO>`.

---

## 6. DTOs

**Convención de nombres:**
- `*CreateRequest` para creación.
- `*UpdateRequest` para actualización.
- `*Response` para respuesta.
- `*Summary` para listados (versión liviana).

**Para responses, usar método estático `fromEntity`:**

```java
public record ContratoResponse(
    Long id,
    String numeroInterno,
    BigDecimal valor,
    String estado,
    LocalDate fechaInicio,
    String oportunidadNombre,
    String tipoContratoNombre
) {
    public static ContratoResponse fromEntity(GcContrato c) {
        return new ContratoResponse(
            c.getId(),
            c.getNumeroInterno(),
            c.getValor(),
            c.getEstado(),
            c.getFechaInicio(),
            c.getOportunidad() != null ? c.getOportunidad().getNombre() : null,
            c.getTipoContrato() != null ? c.getTipoContrato().getNombre() : null
        );
    }
}
```

**Reglas:**

1. Preferir `record` para DTOs inmutables (Java 21 lo soporta).
2. Los `Response` aplanan relaciones a sus nombres legibles, no exponen objetos completos.
3. Los `Request` usan `@NotNull`, `@NotBlank`, `@Size`, etc. de Jakarta Validation.

---

## 7. Manejo de excepciones

El `GlobalExceptionHandler` está configurado para **mostrar la causa raíz en desarrollo**. No envolver excepciones en mensajes genéricos.

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAll(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", ex.getClass().getSimpleName());
        body.put("message", ex.getMessage());
        // Causa raíz para debugging
        Throwable root = ex;
        while (root.getCause() != null) root = root.getCause();
        body.put("rootCause", root.getMessage());
        return ResponseEntity.status(500).body(body);
    }
}
```

---

## 8. DDL Oracle — Plantilla canónica

```sql
CREATE TABLE GC_CONTRATO (
    id                       NUMBER(19) GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    oportunidad_id           NUMBER(19) NOT NULL,
    proceso_contratacion_id  NUMBER(19),
    tipo_contrato_id         NUMBER(19) NOT NULL,
    numero_interno           VARCHAR2(50),
    valor                    NUMBER(18,2) NOT NULL,
    estado                   VARCHAR2(20) NOT NULL,
    fecha_inicio             DATE,
    fecha_creacion           TIMESTAMP NOT NULL,
    fecha_modificacion       TIMESTAMP,
    usuario_creacion         VARCHAR2(50) NOT NULL,
    usuario_modificacion     VARCHAR2(50),
    CONSTRAINT FK_GC_CONTRATO_OPP    FOREIGN KEY (oportunidad_id)          REFERENCES GC_OPORTUNIDAD(id),
    CONSTRAINT FK_GC_CONTRATO_PROC   FOREIGN KEY (proceso_contratacion_id) REFERENCES GC_PROCESO_CONTRATACION(id),
    CONSTRAINT FK_GC_CONTRATO_TIPO   FOREIGN KEY (tipo_contrato_id)        REFERENCES GC_TIPO_CONTRATO(id),
    CONSTRAINT CK_GC_CONTRATO_ESTADO CHECK (estado IN ('VIGENTE','SUSPENDIDO','TERMINADO','LIQUIDADO'))
);

CREATE INDEX IDX_GC_CONTRATO_OPP    ON GC_CONTRATO(oportunidad_id);
CREATE INDEX IDX_GC_CONTRATO_ESTADO ON GC_CONTRATO(estado);
```

**Reglas:**

1. Tablas con prefijo `GC_`.
2. PK siempre `id NUMBER(19) GENERATED BY DEFAULT AS IDENTITY`.
3. FKs nombradas como `FK_{TABLA}_{REFERENCIA}` (versión corta legible).
4. CHECK constraints nombradas como `CK_{TABLA}_{COLUMNA}`.
5. Índices sobre todas las FKs.
6. Índices sobre columnas de estado o filtros frecuentes.
7. **Cada vez que se agrega un valor a un enum, generar el `ALTER ... DROP CONSTRAINT` y `ALTER ... ADD CONSTRAINT` correspondiente.**

---

## 9. Checklist anti-errores (lecciones aprendidas)

Antes de cerrar cualquier cambio backend, verificar:

- [ ] **`@OrderBy` apunta a un field que existe en la entidad**, no a un nombre de columna.
- [ ] **FKs sin `nullable = false`** salvo que sean obligatorias en TODOS los escenarios de inserción.
- [ ] **Constraints CHECK actualizados** si se agregó un valor a un enum.
- [ ] **`JOIN FETCH` en consultas de detalle** para todas las relaciones serializadas en el response.
- [ ] **`@Transactional` correctamente aplicado** (readOnly en consultas, sin readOnly en escrituras).
- [ ] **DTOs separados de entidades** (entidad nunca sale del service).
- [ ] **Auditoría poblada** (`fechaCreacion`, `usuarioCreacion`).
- [ ] **`mvn clean package -DskipTests`** después de cambios en `@OrderBy`, `@JoinColumn` u otras anotaciones JPA críticas.

---

## 10. Cómo entregar archivos

Cuando se generen archivos backend:

1. Cada archivo Java en su propia clase / archivo (`.java`).
2. DDL en archivos `.sql` con nombre que indique la tarea (ej. `T1.4_crear_tabla_contrato.sql`).
3. Ruta destino sugerida en el repo, indicada al desarrollador (ej. `backend/src/main/java/com/arquitecsoft/gestion/domain/contrato/entity/GcContrato.java`).
4. Presentar con `present_files`, no pegar inline (a menos que sean fragmentos cortos para discusión).

---

*Esta skill garantiza que el código backend de GestCom mantenga la consistencia y evite los errores ya documentados en la deuda técnica del proyecto.*
