# B-03: Contrato de API (OpenAPI 3.0)
## Plataforma de Gestión Comercial - Arquitecsoft v0.1

**Fecha:** Febrero 2026  
**Versión API:** 1.0.0  
**Base Path:** `/api/v1`

---

## 1. Especificación OpenAPI

```yaml
openapi: 3.0.3
info:
  title: Gestión Comercial API
  description: |
    API REST para la Plataforma de Gestión Comercial de Arquitecsoft.
    
    ## Autenticación
    La API utiliza JWT Bearer tokens. Incluir en header:
    ```
    Authorization: Bearer <token>
    ```
    
    ## Errores
    Todos los errores siguen el formato estándar con `code`, `message` y `field_errors`.
  version: 1.0.0
  contact:
    name: Arquitecsoft
    email: desarrollo@arquitecsoft.com

servers:
  - url: http://localhost:8080/api/v1
    description: Desarrollo local
  - url: https://gestion.arquitecsoft.com/api/v1
    description: Producción

tags:
  - name: Auth
    description: Autenticación y sesión
  - name: Usuarios
    description: Gestión de usuarios del sistema
  - name: Empresas
    description: Gestión de empresas (prospectos, clientes, aliados)
  - name: Personas
    description: Gestión de personas de contacto
  - name: Pipelines
    description: Configuración de pipelines y etapas
  - name: Oportunidades
    description: Gestión de oportunidades comerciales
  - name: Actividades
    description: Registro de actividades comerciales
  - name: Compromisos
    description: Gestión de compromisos derivados
  - name: Documentos
    description: Gestión de documentos adjuntos
  - name: Catálogos
    description: Catálogos del sistema

# ==============================================================================
# SEGURIDAD
# ==============================================================================
security:
  - bearerAuth: []

components:
  securitySchemes:
    bearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT

  # ============================================================================
  # SCHEMAS - ERRORES
  # ============================================================================
  schemas:
    # --------------------------------------------------------------------------
    # Error Response
    # --------------------------------------------------------------------------
    ErrorResponse:
      type: object
      required:
        - code
        - message
      properties:
        code:
          type: string
          description: Código de error único
          example: "VALIDATION_ERROR"
        message:
          type: string
          description: Mensaje descriptivo del error
          example: "Error de validación en los datos enviados"
        details:
          type: string
          description: Detalles adicionales del error
          example: "El campo 'nombre' es requerido"
        field_errors:
          type: array
          items:
            $ref: '#/components/schemas/FieldError'
        timestamp:
          type: string
          format: date-time
          example: "2026-02-07T10:30:00Z"

    FieldError:
      type: object
      properties:
        field:
          type: string
          example: "email"
        message:
          type: string
          example: "Formato de email inválido"
        rejected_value:
          type: string
          example: "correo-invalido"

    # --------------------------------------------------------------------------
    # Paginación
    # --------------------------------------------------------------------------
    PageInfo:
      type: object
      properties:
        page:
          type: integer
          example: 1
        page_size:
          type: integer
          example: 20
        total_items:
          type: integer
          example: 150
        total_pages:
          type: integer
          example: 8

    # --------------------------------------------------------------------------
    # Auth
    # --------------------------------------------------------------------------
    LoginRequest:
      type: object
      required:
        - username
        - password
      properties:
        username:
          type: string
          example: "jperez"
        password:
          type: string
          format: password
          example: "********"

    LoginResponse:
      type: object
      properties:
        token:
          type: string
          example: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        token_type:
          type: string
          example: "Bearer"
        expires_in:
          type: integer
          description: Segundos hasta expiración
          example: 28800
        user:
          $ref: '#/components/schemas/UsuarioResponse'

    # --------------------------------------------------------------------------
    # Usuario
    # --------------------------------------------------------------------------
    UsuarioResponse:
      type: object
      properties:
        id:
          type: integer
          example: 1
        username:
          type: string
          example: "jperez"
        email:
          type: string
          example: "jperez@arquitecsoft.com"
        nombres:
          type: string
          example: "Juan"
        apellidos:
          type: string
          example: "Pérez"
        rol:
          type: string
          enum: [ADMIN, COMERCIAL, LECTURA_KPI]
          example: "COMERCIAL"
        activo:
          type: boolean
          example: true

    UsuarioCreateRequest:
      type: object
      required:
        - username
        - email
        - password
        - nombres
        - apellidos
        - rol
      properties:
        username:
          type: string
          minLength: 3
          maxLength: 50
          example: "jperez"
        email:
          type: string
          format: email
          example: "jperez@arquitecsoft.com"
        password:
          type: string
          format: password
          minLength: 8
          example: "********"
        nombres:
          type: string
          maxLength: 100
          example: "Juan"
        apellidos:
          type: string
          maxLength: 100
          example: "Pérez"
        rol:
          type: string
          enum: [ADMIN, COMERCIAL, LECTURA_KPI]
          example: "COMERCIAL"

    # --------------------------------------------------------------------------
    # Empresa
    # --------------------------------------------------------------------------
    EmpresaResponse:
      type: object
      properties:
        id:
          type: integer
          example: 1
        tipo:
          type: string
          enum: [EMPRESA, MULTINACIONAL, ALIADO]
          example: "EMPRESA"
        razon_social:
          type: string
          example: "Acme Corporation S.A.S."
        identificacion_tributaria:
          type: string
          example: "900123456-7"
        sitio_web:
          type: string
          example: "https://www.acme.com"
        pais:
          type: string
          example: "Colombia"
        estado:
          type: string
          enum: [ACTIVA, INACTIVA]
          example: "ACTIVA"
        fecha_creacion:
          type: string
          format: date-time
        creado_por:
          $ref: '#/components/schemas/UsuarioResumen'

    EmpresaResumen:
      type: object
      properties:
        id:
          type: integer
          example: 1
        razon_social:
          type: string
          example: "Acme Corporation S.A.S."
        tipo:
          type: string
          example: "EMPRESA"

    EmpresaCreateRequest:
      type: object
      required:
        - tipo
        - razon_social
      properties:
        tipo:
          type: string
          enum: [EMPRESA, MULTINACIONAL, ALIADO]
          example: "EMPRESA"
        razon_social:
          type: string
          maxLength: 200
          example: "Acme Corporation S.A.S."
        identificacion_tributaria:
          type: string
          maxLength: 50
          example: "900123456-7"
        sitio_web:
          type: string
          maxLength: 200
          example: "https://www.acme.com"
        pais:
          type: string
          maxLength: 100
          example: "Colombia"

    EmpresaUpdateRequest:
      type: object
      properties:
        tipo:
          type: string
          enum: [EMPRESA, MULTINACIONAL, ALIADO]
        razon_social:
          type: string
          maxLength: 200
        identificacion_tributaria:
          type: string
          maxLength: 50
        sitio_web:
          type: string
          maxLength: 200
        pais:
          type: string
          maxLength: 100
        estado:
          type: string
          enum: [ACTIVA, INACTIVA]

    EmpresaListResponse:
      type: object
      properties:
        data:
          type: array
          items:
            $ref: '#/components/schemas/EmpresaResponse'
        pagination:
          $ref: '#/components/schemas/PageInfo'

    # --------------------------------------------------------------------------
    # Persona
    # --------------------------------------------------------------------------
    PersonaResponse:
      type: object
      properties:
        id:
          type: integer
          example: 1
        nombres:
          type: string
          example: "María"
        apellidos:
          type: string
          example: "González"
        email:
          type: string
          example: "maria.gonzalez@email.com"
        telefono:
          type: string
          example: "+57 300 123 4567"
        activo:
          type: boolean
          example: true
        empresas:
          type: array
          items:
            $ref: '#/components/schemas/PersonaEmpresaResponse'

    PersonaResumen:
      type: object
      properties:
        id:
          type: integer
          example: 1
        nombre_completo:
          type: string
          example: "María González"
        email:
          type: string
          example: "maria.gonzalez@email.com"

    PersonaCreateRequest:
      type: object
      required:
        - nombres
        - apellidos
      properties:
        nombres:
          type: string
          maxLength: 100
          example: "María"
        apellidos:
          type: string
          maxLength: 100
          example: "González"
        email:
          type: string
          format: email
          example: "maria.gonzalez@email.com"
        telefono:
          type: string
          maxLength: 30
          example: "+57 300 123 4567"

    PersonaUpdateRequest:
      type: object
      properties:
        nombres:
          type: string
          maxLength: 100
        apellidos:
          type: string
          maxLength: 100
        email:
          type: string
          format: email
        telefono:
          type: string
          maxLength: 30
        activo:
          type: boolean

    PersonaEmpresaResponse:
      type: object
      properties:
        id:
          type: integer
          example: 1
        empresa:
          $ref: '#/components/schemas/EmpresaResumen'
        cargo:
          type: string
          example: "Gerente de TI"
        puesto:
          type: string
          example: "Tecnología"
        rol_contacto:
          type: string
          enum: [DECISOR, INFLUENCIADOR, TECNICO, USUARIO, ADMINISTRATIVO]
          example: "DECISOR"
        email_empresarial:
          type: string
          example: "mgonzalez@acme.com"
        telefono_empresarial:
          type: string
          example: "+57 1 234 5678 ext 100"
        es_contacto_principal:
          type: boolean
          example: true

    PersonaEmpresaCreateRequest:
      type: object
      required:
        - empresa_id
      properties:
        empresa_id:
          type: integer
          example: 1
        cargo:
          type: string
          maxLength: 100
          example: "Gerente de TI"
        puesto:
          type: string
          maxLength: 100
          example: "Tecnología"
        rol_contacto:
          type: string
          enum: [DECISOR, INFLUENCIADOR, TECNICO, USUARIO, ADMINISTRATIVO]
          example: "DECISOR"
        email_empresarial:
          type: string
          format: email
          example: "mgonzalez@acme.com"
        telefono_empresarial:
          type: string
          maxLength: 30
          example: "+57 1 234 5678 ext 100"
        es_contacto_principal:
          type: boolean
          default: false

    PersonaListResponse:
      type: object
      properties:
        data:
          type: array
          items:
            $ref: '#/components/schemas/PersonaResponse'
        pagination:
          $ref: '#/components/schemas/PageInfo'

    # --------------------------------------------------------------------------
    # Pipeline
    # --------------------------------------------------------------------------
    PipelineResponse:
      type: object
      properties:
        id:
          type: integer
          example: 1
        nombre:
          type: string
          example: "Pipeline Comercial Principal"
        ambito:
          type: string
          example: "GESTION_COMERCIAL"
        version:
          type: integer
          example: 1
        estado:
          type: string
          enum: [ACTIVO, INACTIVO]
          example: "ACTIVO"
        es_default:
          type: boolean
          example: true
        etapas:
          type: array
          items:
            $ref: '#/components/schemas/EtapaResponse'

    PipelineResumen:
      type: object
      properties:
        id:
          type: integer
          example: 1
        nombre:
          type: string
          example: "Pipeline Comercial Principal"

    PipelineCreateRequest:
      type: object
      required:
        - nombre
      properties:
        nombre:
          type: string
          maxLength: 100
          example: "Pipeline Comercial Principal"
        ambito:
          type: string
          default: "GESTION_COMERCIAL"
          example: "GESTION_COMERCIAL"
        es_default:
          type: boolean
          default: false

    PipelineUpdateRequest:
      type: object
      properties:
        nombre:
          type: string
          maxLength: 100
        estado:
          type: string
          enum: [ACTIVO, INACTIVO]
        es_default:
          type: boolean

    PipelineListResponse:
      type: object
      properties:
        data:
          type: array
          items:
            $ref: '#/components/schemas/PipelineResponse'

    # --------------------------------------------------------------------------
    # Etapa
    # --------------------------------------------------------------------------
    EtapaResponse:
      type: object
      properties:
        id:
          type: integer
          example: 1
        nombre:
          type: string
          example: "Prospección"
        orden:
          type: integer
          example: 1
        probabilidad_sugerida:
          type: integer
          minimum: 0
          maximum: 100
          example: 10
        color:
          type: string
          example: "#1A73E8"
        modo_bloqueo:
          type: boolean
          example: false
        estado:
          type: string
          enum: [ACTIVA, INACTIVA]
          example: "ACTIVA"

    EtapaResumen:
      type: object
      properties:
        id:
          type: integer
          example: 1
        nombre:
          type: string
          example: "Prospección"
        orden:
          type: integer
          example: 1
        color:
          type: string
          example: "#1A73E8"

    EtapaCreateRequest:
      type: object
      required:
        - nombre
        - orden
      properties:
        nombre:
          type: string
          maxLength: 100
          example: "Prospección"
        orden:
          type: integer
          minimum: 1
          example: 1
        probabilidad_sugerida:
          type: integer
          minimum: 0
          maximum: 100
          example: 10
        color:
          type: string
          pattern: '^#[0-9A-Fa-f]{6}$'
          example: "#1A73E8"
        modo_bloqueo:
          type: boolean
          default: false

    EtapaUpdateRequest:
      type: object
      properties:
        nombre:
          type: string
          maxLength: 100
        orden:
          type: integer
          minimum: 1
        probabilidad_sugerida:
          type: integer
          minimum: 0
          maximum: 100
        color:
          type: string
          pattern: '^#[0-9A-Fa-f]{6}$'
        modo_bloqueo:
          type: boolean
        estado:
          type: string
          enum: [ACTIVA, INACTIVA]

    # --------------------------------------------------------------------------
    # Oportunidad
    # --------------------------------------------------------------------------
    OportunidadResponse:
      type: object
      properties:
        id:
          type: integer
          example: 1
        nombre:
          type: string
          example: "Implementación ERP Acme"
        empresa:
          $ref: '#/components/schemas/EmpresaResumen'
        pipeline:
          $ref: '#/components/schemas/PipelineResumen'
        etapa:
          $ref: '#/components/schemas/EtapaResumen'
        estado_macro:
          type: string
          enum: [ABIERTA, SEGUIMIENTO, GANADA, PERDIDA, NO_CONCRETADA]
          example: "ABIERTA"
        valor_estimado:
          type: number
          format: double
          example: 150000000
        moneda:
          type: string
          example: "COP"
        probabilidad:
          type: integer
          example: 30
        fecha_estimada_cierre:
          type: string
          format: date
          example: "2026-06-30"
        fuente:
          type: string
          example: "Referido"
        tipo_servicio:
          type: string
          example: "Consultoría"
        motivo_cierre:
          $ref: '#/components/schemas/CatalogoResumen'
        comentario_cierre:
          type: string
        fecha_cierre:
          type: string
          format: date-time
        responsables:
          type: array
          items:
            $ref: '#/components/schemas/ResponsableResponse'
        contactos:
          type: array
          items:
            $ref: '#/components/schemas/ContactoOportunidadResponse'
        fecha_creacion:
          type: string
          format: date-time
        creado_por:
          $ref: '#/components/schemas/UsuarioResumen'

    OportunidadResumen:
      type: object
      properties:
        id:
          type: integer
          example: 1
        nombre:
          type: string
          example: "Implementación ERP Acme"
        empresa:
          type: string
          example: "Acme Corporation S.A.S."
        etapa:
          type: string
          example: "Prospección"
        estado_macro:
          type: string
          example: "ABIERTA"
        valor_estimado:
          type: number
          example: 150000000
        moneda:
          type: string
          example: "COP"

    OportunidadCreateRequest:
      type: object
      required:
        - nombre
        - empresa_id
        - pipeline_id
      properties:
        nombre:
          type: string
          maxLength: 200
          example: "Implementación ERP Acme"
        empresa_id:
          type: integer
          example: 1
        pipeline_id:
          type: integer
          example: 1
        etapa_id:
          type: integer
          description: "Si no se especifica, usa la primera etapa del pipeline"
          example: 1
        valor_estimado:
          type: number
          format: double
          example: 150000000
        moneda:
          type: string
          enum: [COP, USD, EUR]
          default: "COP"
        probabilidad:
          type: integer
          minimum: 0
          maximum: 100
        fecha_estimada_cierre:
          type: string
          format: date
          example: "2026-06-30"
        fuente:
          type: string
          maxLength: 100
          example: "Referido"
        tipo_servicio:
          type: string
          maxLength: 100
          example: "Consultoría"
        responsable_principal_id:
          type: integer
          description: "ID del usuario responsable principal"
          example: 2
        contacto_principal_id:
          type: integer
          description: "ID de persona_empresa como contacto principal"
          example: 1

    OportunidadUpdateRequest:
      type: object
      properties:
        nombre:
          type: string
          maxLength: 200
        valor_estimado:
          type: number
          format: double
        moneda:
          type: string
          enum: [COP, USD, EUR]
        probabilidad:
          type: integer
          minimum: 0
          maximum: 100
        fecha_estimada_cierre:
          type: string
          format: date
        fuente:
          type: string
          maxLength: 100
        tipo_servicio:
          type: string
          maxLength: 100

    OportunidadListResponse:
      type: object
      properties:
        data:
          type: array
          items:
            $ref: '#/components/schemas/OportunidadResumen'
        pagination:
          $ref: '#/components/schemas/PageInfo'

    # --------------------------------------------------------------------------
    # Mover Etapa
    # --------------------------------------------------------------------------
    MoverEtapaRequest:
      type: object
      required:
        - etapa_destino_id
      properties:
        etapa_destino_id:
          type: integer
          example: 2
        comentario:
          type: string
          maxLength: 500
          example: "Cliente confirmó interés, agendamos demo"

    MoverEtapaResponse:
      type: object
      properties:
        oportunidad_id:
          type: integer
          example: 1
        etapa_anterior:
          $ref: '#/components/schemas/EtapaResumen'
        etapa_nueva:
          $ref: '#/components/schemas/EtapaResumen'
        fecha_movimiento:
          type: string
          format: date-time

    # --------------------------------------------------------------------------
    # Cerrar Oportunidad
    # --------------------------------------------------------------------------
    CerrarOportunidadRequest:
      type: object
      required:
        - estado_macro
      properties:
        estado_macro:
          type: string
          enum: [GANADA, PERDIDA, NO_CONCRETADA]
          example: "GANADA"
        motivo_cierre_id:
          type: integer
          description: "Obligatorio si estado es PERDIDA o NO_CONCRETADA"
          example: 1
        comentario_cierre:
          type: string
          maxLength: 500
          example: "Cliente firmó contrato"

    CerrarOportunidadResponse:
      type: object
      properties:
        oportunidad_id:
          type: integer
          example: 1
        estado_macro:
          type: string
          example: "GANADA"
        fecha_cierre:
          type: string
          format: date-time

    # --------------------------------------------------------------------------
    # Responsable
    # --------------------------------------------------------------------------
    ResponsableResponse:
      type: object
      properties:
        id:
          type: integer
          example: 1
        tipo_responsable:
          type: string
          enum: [USUARIO, PERSONA_EXTERNA]
          example: "USUARIO"
        usuario:
          $ref: '#/components/schemas/UsuarioResumen'
        persona:
          $ref: '#/components/schemas/PersonaResumen'
        rol:
          type: string
          enum: [PRINCIPAL, APOYO, TECNICO]
          example: "PRINCIPAL"
        activo:
          type: boolean
          example: true
        fecha_asignacion:
          type: string
          format: date-time

    ResponsableCreateRequest:
      type: object
      required:
        - tipo_responsable
        - rol
      properties:
        tipo_responsable:
          type: string
          enum: [USUARIO, PERSONA_EXTERNA]
          example: "USUARIO"
        usuario_id:
          type: integer
          description: "Requerido si tipo_responsable = USUARIO"
          example: 2
        persona_id:
          type: integer
          description: "Requerido si tipo_responsable = PERSONA_EXTERNA"
        rol:
          type: string
          enum: [PRINCIPAL, APOYO, TECNICO]
          example: "APOYO"

    UsuarioResumen:
      type: object
      properties:
        id:
          type: integer
          example: 2
        nombre_completo:
          type: string
          example: "Juan Pérez"
        email:
          type: string
          example: "jperez@arquitecsoft.com"

    # --------------------------------------------------------------------------
    # Contacto Oportunidad
    # --------------------------------------------------------------------------
    ContactoOportunidadResponse:
      type: object
      properties:
        id:
          type: integer
          example: 1
        persona:
          $ref: '#/components/schemas/PersonaResumen'
        empresa:
          $ref: '#/components/schemas/EmpresaResumen'
        cargo:
          type: string
          example: "Gerente de TI"
        rol_contacto:
          type: string
          example: "DECISOR"
        es_contacto_principal:
          type: boolean
          example: true

    ContactoOportunidadCreateRequest:
      type: object
      required:
        - persona_empresa_id
      properties:
        persona_empresa_id:
          type: integer
          example: 1
        es_contacto_principal:
          type: boolean
          default: false

    # --------------------------------------------------------------------------
    # Actividad
    # --------------------------------------------------------------------------
    ActividadResponse:
      type: object
      properties:
        id:
          type: integer
          example: 1
        oportunidad:
          $ref: '#/components/schemas/OportunidadResumen'
        tipo_actividad:
          $ref: '#/components/schemas/CatalogoResumen'
        fecha_hora:
          type: string
          format: date-time
          example: "2026-02-15T10:00:00Z"
        duracion_minutos:
          type: integer
          example: 60
        notas:
          type: string
          example: "Se revisaron requerimientos técnicos..."
        participantes:
          type: array
          items:
            $ref: '#/components/schemas/ParticipanteResponse'
        compromisos:
          type: array
          items:
            $ref: '#/components/schemas/CompromisoResumen'
        documentos:
          type: array
          items:
            $ref: '#/components/schemas/DocumentoResumen'
        fecha_creacion:
          type: string
          format: date-time
        creado_por:
          $ref: '#/components/schemas/UsuarioResumen'

    ActividadResumen:
      type: object
      properties:
        id:
          type: integer
          example: 1
        tipo_actividad:
          type: string
          example: "Reunión"
        fecha_hora:
          type: string
          format: date-time

    ActividadCreateRequest:
      type: object
      required:
        - tipo_actividad_id
        - fecha_hora
      properties:
        tipo_actividad_id:
          type: integer
          example: 1
        fecha_hora:
          type: string
          format: date-time
          example: "2026-02-15T10:00:00Z"
        duracion_minutos:
          type: integer
          example: 60
        notas:
          type: string
          maxLength: 2000
          example: "Se revisaron requerimientos técnicos..."
        participantes:
          type: array
          items:
            $ref: '#/components/schemas/ParticipanteCreateRequest'

    ActividadListResponse:
      type: object
      properties:
        data:
          type: array
          items:
            $ref: '#/components/schemas/ActividadResponse'
        pagination:
          $ref: '#/components/schemas/PageInfo'

    # --------------------------------------------------------------------------
    # Participante
    # --------------------------------------------------------------------------
    ParticipanteResponse:
      type: object
      properties:
        id:
          type: integer
          example: 1
        tipo_participante:
          type: string
          enum: [USUARIO, PERSONA_EXTERNA]
          example: "USUARIO"
        usuario:
          $ref: '#/components/schemas/UsuarioResumen'
        persona:
          $ref: '#/components/schemas/PersonaResumen'

    ParticipanteCreateRequest:
      type: object
      required:
        - tipo_participante
      properties:
        tipo_participante:
          type: string
          enum: [USUARIO, PERSONA_EXTERNA]
          example: "USUARIO"
        usuario_id:
          type: integer
        persona_id:
          type: integer

    # --------------------------------------------------------------------------
    # Compromiso
    # --------------------------------------------------------------------------
    CompromisoResponse:
      type: object
      properties:
        id:
          type: integer
          example: 1
        actividad_id:
          type: integer
          example: 1
        descripcion:
          type: string
          example: "Enviar propuesta técnica"
        fecha_compromiso:
          type: string
          format: date
          example: "2026-02-20"
        estado:
          type: string
          enum: [PENDIENTE, EN_PROGRESO, COMPLETADO, CANCELADO]
          example: "PENDIENTE"
        responsables:
          type: array
          items:
            $ref: '#/components/schemas/ResponsableResponse'
        fecha_completado:
          type: string
          format: date-time
        notas_cierre:
          type: string
        fecha_creacion:
          type: string
          format: date-time

    CompromisoResumen:
      type: object
      properties:
        id:
          type: integer
          example: 1
        descripcion:
          type: string
          example: "Enviar propuesta técnica"
        fecha_compromiso:
          type: string
          format: date
        estado:
          type: string
          example: "PENDIENTE"

    CompromisoCreateRequest:
      type: object
      required:
        - descripcion
        - fecha_compromiso
      properties:
        descripcion:
          type: string
          maxLength: 500
          example: "Enviar propuesta técnica"
        fecha_compromiso:
          type: string
          format: date
          example: "2026-02-20"
        responsables:
          type: array
          items:
            $ref: '#/components/schemas/ResponsableCreateRequest'

    CompromisoUpdateRequest:
      type: object
      properties:
        descripcion:
          type: string
          maxLength: 500
        fecha_compromiso:
          type: string
          format: date
        estado:
          type: string
          enum: [PENDIENTE, EN_PROGRESO, COMPLETADO, CANCELADO]
        notas_cierre:
          type: string
          maxLength: 500

    CompromisoListResponse:
      type: object
      properties:
        data:
          type: array
          items:
            $ref: '#/components/schemas/CompromisoResponse'
        pagination:
          $ref: '#/components/schemas/PageInfo'

    # --------------------------------------------------------------------------
    # Documento
    # --------------------------------------------------------------------------
    DocumentoResponse:
      type: object
      properties:
        id:
          type: integer
          example: 1
        tipo_documento:
          $ref: '#/components/schemas/CatalogoResumen'
        nombre:
          type: string
          example: "Propuesta_Acme_v1.pdf"
        nombre_original:
          type: string
          example: "Propuesta Acme v1.pdf"
        extension:
          type: string
          example: "pdf"
        tamano_bytes:
          type: integer
          example: 2048576
        url_descarga:
          type: string
          example: "/api/v1/documentos/1/download"
        fecha_carga:
          type: string
          format: date-time
        cargado_por:
          $ref: '#/components/schemas/UsuarioResumen'

    DocumentoResumen:
      type: object
      properties:
        id:
          type: integer
          example: 1
        nombre:
          type: string
          example: "Propuesta_Acme_v1.pdf"
        tipo_documento:
          type: string
          example: "Propuesta"

    DocumentoUploadRequest:
      type: object
      required:
        - tipo_documento_id
        - archivo
      properties:
        tipo_documento_id:
          type: integer
          example: 1
        nombre:
          type: string
          description: "Nombre personalizado (opcional)"
          example: "Propuesta_Acme_v1"

    DocumentoListResponse:
      type: object
      properties:
        data:
          type: array
          items:
            $ref: '#/components/schemas/DocumentoResponse'

    # --------------------------------------------------------------------------
    # Catálogo
    # --------------------------------------------------------------------------
    CatalogoResponse:
      type: object
      properties:
        id:
          type: integer
          example: 1
        tipo:
          type: string
          example: "TIPO_ACTIVIDAD"
        codigo:
          type: string
          example: "REUNION"
        nombre:
          type: string
          example: "Reunión"
        descripcion:
          type: string
        orden:
          type: integer
          example: 1
        activo:
          type: boolean
          example: true

    CatalogoResumen:
      type: object
      properties:
        id:
          type: integer
          example: 1
        codigo:
          type: string
          example: "REUNION"
        nombre:
          type: string
          example: "Reunión"

    CatalogoListResponse:
      type: object
      properties:
        data:
          type: array
          items:
            $ref: '#/components/schemas/CatalogoResponse'

    # --------------------------------------------------------------------------
    # Historial Etapa
    # --------------------------------------------------------------------------
    HistorialEtapaResponse:
      type: object
      properties:
        id:
          type: integer
          example: 1
        etapa_origen:
          $ref: '#/components/schemas/EtapaResumen'
        etapa_destino:
          $ref: '#/components/schemas/EtapaResumen'
        estado_macro_origen:
          type: string
        estado_macro_destino:
          type: string
        comentario:
          type: string
        fecha_movimiento:
          type: string
          format: date-time
        realizado_por:
          $ref: '#/components/schemas/UsuarioResumen'

    HistorialEtapaListResponse:
      type: object
      properties:
        data:
          type: array
          items:
            $ref: '#/components/schemas/HistorialEtapaResponse'

  # ============================================================================
  # RESPONSES COMUNES
  # ============================================================================
  responses:
    BadRequest:
      description: Error de validación
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/ErrorResponse'
          example:
            code: "VALIDATION_ERROR"
            message: "Error de validación en los datos enviados"
            field_errors:
              - field: "nombre"
                message: "El campo es requerido"
              - field: "email"
                message: "Formato de email inválido"
                rejected_value: "correo-invalido"

    Unauthorized:
      description: No autenticado
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/ErrorResponse'
          example:
            code: "UNAUTHORIZED"
            message: "Token de autenticación inválido o expirado"

    Forbidden:
      description: Sin permisos
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/ErrorResponse'
          example:
            code: "FORBIDDEN"
            message: "No tiene permisos para realizar esta acción"

    NotFound:
      description: Recurso no encontrado
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/ErrorResponse'
          example:
            code: "NOT_FOUND"
            message: "Recurso no encontrado"
            details: "No existe oportunidad con ID 999"

    Conflict:
      description: Conflicto de negocio
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/ErrorResponse'
          example:
            code: "BUSINESS_ERROR"
            message: "No se puede cerrar como PERDIDA sin motivo de cierre"

    InternalError:
      description: Error interno del servidor
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/ErrorResponse'
          example:
            code: "INTERNAL_ERROR"
            message: "Error interno del servidor"

# ==============================================================================
# PATHS
# ==============================================================================
paths:
  # ============================================================================
  # AUTH
  # ============================================================================
  /auth/login:
    post:
      tags: [Auth]
      summary: Iniciar sesión
      security: []
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/LoginRequest'
      responses:
        '200':
          description: Login exitoso
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/LoginResponse'
        '401':
          $ref: '#/components/responses/Unauthorized'

  /auth/me:
    get:
      tags: [Auth]
      summary: Obtener usuario actual
      responses:
        '200':
          description: Usuario actual
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/UsuarioResponse'
        '401':
          $ref: '#/components/responses/Unauthorized'

  # ============================================================================
  # USUARIOS
  # ============================================================================
  /usuarios:
    get:
      tags: [Usuarios]
      summary: Listar usuarios
      parameters:
        - name: activo
          in: query
          schema:
            type: boolean
        - name: rol
          in: query
          schema:
            type: string
            enum: [ADMIN, COMERCIAL, LECTURA_KPI]
      responses:
        '200':
          description: Lista de usuarios
          content:
            application/json:
              schema:
                type: object
                properties:
                  data:
                    type: array
                    items:
                      $ref: '#/components/schemas/UsuarioResponse'
        '401':
          $ref: '#/components/responses/Unauthorized'
        '403':
          $ref: '#/components/responses/Forbidden'

    post:
      tags: [Usuarios]
      summary: Crear usuario
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/UsuarioCreateRequest'
      responses:
        '201':
          description: Usuario creado
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/UsuarioResponse'
        '400':
          $ref: '#/components/responses/BadRequest'
        '401':
          $ref: '#/components/responses/Unauthorized'
        '403':
          $ref: '#/components/responses/Forbidden'

  /usuarios/{id}:
    get:
      tags: [Usuarios]
      summary: Obtener usuario por ID
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: Usuario encontrado
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/UsuarioResponse'
        '404':
          $ref: '#/components/responses/NotFound'

  # ============================================================================
  # EMPRESAS
  # ============================================================================
  /empresas:
    get:
      tags: [Empresas]
      summary: Listar empresas
      parameters:
        - name: q
          in: query
          description: Búsqueda por razón social
          schema:
            type: string
        - name: tipo
          in: query
          schema:
            type: string
            enum: [EMPRESA, MULTINACIONAL, ALIADO]
        - name: estado
          in: query
          schema:
            type: string
            enum: [ACTIVA, INACTIVA]
        - name: page
          in: query
          schema:
            type: integer
            default: 1
        - name: page_size
          in: query
          schema:
            type: integer
            default: 20
            maximum: 100
      responses:
        '200':
          description: Lista de empresas
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/EmpresaListResponse'

    post:
      tags: [Empresas]
      summary: Crear empresa
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/EmpresaCreateRequest'
      responses:
        '201':
          description: Empresa creada
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/EmpresaResponse'
        '400':
          $ref: '#/components/responses/BadRequest'

  /empresas/{id}:
    get:
      tags: [Empresas]
      summary: Obtener empresa por ID
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: Empresa encontrada
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/EmpresaResponse'
        '404':
          $ref: '#/components/responses/NotFound'

    put:
      tags: [Empresas]
      summary: Actualizar empresa
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/EmpresaUpdateRequest'
      responses:
        '200':
          description: Empresa actualizada
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/EmpresaResponse'
        '400':
          $ref: '#/components/responses/BadRequest'
        '404':
          $ref: '#/components/responses/NotFound'

  /empresas/{id}/contactos:
    get:
      tags: [Empresas]
      summary: Listar contactos de una empresa
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: Lista de contactos
          content:
            application/json:
              schema:
                type: object
                properties:
                  data:
                    type: array
                    items:
                      $ref: '#/components/schemas/PersonaEmpresaResponse'

  # ============================================================================
  # PERSONAS
  # ============================================================================
  /personas:
    get:
      tags: [Personas]
      summary: Listar personas
      parameters:
        - name: q
          in: query
          description: Búsqueda por nombre
          schema:
            type: string
        - name: empresa_id
          in: query
          schema:
            type: integer
        - name: page
          in: query
          schema:
            type: integer
            default: 1
        - name: page_size
          in: query
          schema:
            type: integer
            default: 20
      responses:
        '200':
          description: Lista de personas
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PersonaListResponse'

    post:
      tags: [Personas]
      summary: Crear persona
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/PersonaCreateRequest'
      responses:
        '201':
          description: Persona creada
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PersonaResponse'
        '400':
          $ref: '#/components/responses/BadRequest'

  /personas/{id}:
    get:
      tags: [Personas]
      summary: Obtener persona por ID
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: Persona encontrada
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PersonaResponse'
        '404':
          $ref: '#/components/responses/NotFound'

    put:
      tags: [Personas]
      summary: Actualizar persona
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/PersonaUpdateRequest'
      responses:
        '200':
          description: Persona actualizada
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PersonaResponse'
        '400':
          $ref: '#/components/responses/BadRequest'
        '404':
          $ref: '#/components/responses/NotFound'

  /personas/{id}/empresas:
    post:
      tags: [Personas]
      summary: Asociar persona a empresa
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/PersonaEmpresaCreateRequest'
      responses:
        '201':
          description: Asociación creada
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PersonaEmpresaResponse'
        '400':
          $ref: '#/components/responses/BadRequest'
        '404':
          $ref: '#/components/responses/NotFound'

  # ============================================================================
  # PIPELINES
  # ============================================================================
  /pipelines:
    get:
      tags: [Pipelines]
      summary: Listar pipelines
      parameters:
        - name: estado
          in: query
          schema:
            type: string
            enum: [ACTIVO, INACTIVO]
        - name: ambito
          in: query
          schema:
            type: string
            default: GESTION_COMERCIAL
      responses:
        '200':
          description: Lista de pipelines
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PipelineListResponse'

    post:
      tags: [Pipelines]
      summary: Crear pipeline
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/PipelineCreateRequest'
      responses:
        '201':
          description: Pipeline creado
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PipelineResponse'
        '400':
          $ref: '#/components/responses/BadRequest'

  /pipelines/{id}:
    get:
      tags: [Pipelines]
      summary: Obtener pipeline por ID
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: Pipeline encontrado
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PipelineResponse'
        '404':
          $ref: '#/components/responses/NotFound'

    put:
      tags: [Pipelines]
      summary: Actualizar pipeline
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/PipelineUpdateRequest'
      responses:
        '200':
          description: Pipeline actualizado
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PipelineResponse'
        '400':
          $ref: '#/components/responses/BadRequest'
        '404':
          $ref: '#/components/responses/NotFound'

  /pipelines/{id}/etapas:
    post:
      tags: [Pipelines]
      summary: Crear etapa en pipeline
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/EtapaCreateRequest'
      responses:
        '201':
          description: Etapa creada
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/EtapaResponse'
        '400':
          $ref: '#/components/responses/BadRequest'
        '404':
          $ref: '#/components/responses/NotFound'

  /pipelines/{pipeline_id}/etapas/{etapa_id}:
    put:
      tags: [Pipelines]
      summary: Actualizar etapa
      parameters:
        - name: pipeline_id
          in: path
          required: true
          schema:
            type: integer
        - name: etapa_id
          in: path
          required: true
          schema:
            type: integer
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/EtapaUpdateRequest'
      responses:
        '200':
          description: Etapa actualizada
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/EtapaResponse'
        '400':
          $ref: '#/components/responses/BadRequest'
        '404':
          $ref: '#/components/responses/NotFound'

  # ============================================================================
  # OPORTUNIDADES
  # ============================================================================
  /oportunidades:
    get:
      tags: [Oportunidades]
      summary: Listar oportunidades
      parameters:
        - name: q
          in: query
          description: Búsqueda por nombre
          schema:
            type: string
        - name: empresa_id
          in: query
          schema:
            type: integer
        - name: pipeline_id
          in: query
          schema:
            type: integer
        - name: etapa_id
          in: query
          schema:
            type: integer
        - name: estado_macro
          in: query
          schema:
            type: string
            enum: [ABIERTA, SEGUIMIENTO, GANADA, PERDIDA, NO_CONCRETADA]
        - name: responsable_id
          in: query
          description: ID del usuario responsable
          schema:
            type: integer
        - name: fecha_desde
          in: query
          schema:
            type: string
            format: date
        - name: fecha_hasta
          in: query
          schema:
            type: string
            format: date
        - name: page
          in: query
          schema:
            type: integer
            default: 1
        - name: page_size
          in: query
          schema:
            type: integer
            default: 20
        - name: sort_by
          in: query
          schema:
            type: string
            enum: [fecha_creacion, nombre, valor_estimado, fecha_estimada_cierre]
            default: fecha_creacion
        - name: sort_dir
          in: query
          schema:
            type: string
            enum: [asc, desc]
            default: desc
      responses:
        '200':
          description: Lista de oportunidades
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/OportunidadListResponse'

    post:
      tags: [Oportunidades]
      summary: Crear oportunidad
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/OportunidadCreateRequest'
      responses:
        '201':
          description: Oportunidad creada
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/OportunidadResponse'
        '400':
          $ref: '#/components/responses/BadRequest'

  /oportunidades/{id}:
    get:
      tags: [Oportunidades]
      summary: Obtener oportunidad por ID
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: Oportunidad encontrada
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/OportunidadResponse'
        '404':
          $ref: '#/components/responses/NotFound'

    put:
      tags: [Oportunidades]
      summary: Actualizar oportunidad
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/OportunidadUpdateRequest'
      responses:
        '200':
          description: Oportunidad actualizada
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/OportunidadResponse'
        '400':
          $ref: '#/components/responses/BadRequest'
        '404':
          $ref: '#/components/responses/NotFound'

  /oportunidades/{id}/mover-etapa:
    post:
      tags: [Oportunidades]
      summary: Mover oportunidad a otra etapa
      description: |
        Mueve la oportunidad a una nueva etapa dentro del mismo pipeline.
        Registra el movimiento en el historial.
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/MoverEtapaRequest'
      responses:
        '200':
          description: Etapa actualizada
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/MoverEtapaResponse'
        '400':
          $ref: '#/components/responses/BadRequest'
        '404':
          $ref: '#/components/responses/NotFound'
        '409':
          $ref: '#/components/responses/Conflict'

  /oportunidades/{id}/cerrar:
    post:
      tags: [Oportunidades]
      summary: Cerrar oportunidad
      description: |
        Cierra la oportunidad con un estado final (GANADA, PERDIDA, NO_CONCRETADA).
        Si el estado es PERDIDA o NO_CONCRETADA, el motivo_cierre_id es obligatorio.
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CerrarOportunidadRequest'
      responses:
        '200':
          description: Oportunidad cerrada
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/CerrarOportunidadResponse'
        '400':
          $ref: '#/components/responses/BadRequest'
        '404':
          $ref: '#/components/responses/NotFound'
        '409':
          $ref: '#/components/responses/Conflict'

  /oportunidades/{id}/responsables:
    get:
      tags: [Oportunidades]
      summary: Listar responsables de oportunidad
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: Lista de responsables
          content:
            application/json:
              schema:
                type: object
                properties:
                  data:
                    type: array
                    items:
                      $ref: '#/components/schemas/ResponsableResponse'

    post:
      tags: [Oportunidades]
      summary: Agregar responsable a oportunidad
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ResponsableCreateRequest'
      responses:
        '201':
          description: Responsable agregado
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ResponsableResponse'
        '400':
          $ref: '#/components/responses/BadRequest'

  /oportunidades/{id}/contactos:
    get:
      tags: [Oportunidades]
      summary: Listar contactos de oportunidad
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: Lista de contactos
          content:
            application/json:
              schema:
                type: object
                properties:
                  data:
                    type: array
                    items:
                      $ref: '#/components/schemas/ContactoOportunidadResponse'

    post:
      tags: [Oportunidades]
      summary: Agregar contacto a oportunidad
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ContactoOportunidadCreateRequest'
      responses:
        '201':
          description: Contacto agregado
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ContactoOportunidadResponse'
        '400':
          $ref: '#/components/responses/BadRequest'

  /oportunidades/{id}/historial:
    get:
      tags: [Oportunidades]
      summary: Obtener historial de etapas
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: Historial de movimientos
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/HistorialEtapaListResponse'

  # ============================================================================
  # ACTIVIDADES
  # ============================================================================
  /oportunidades/{id}/actividades:
    get:
      tags: [Actividades]
      summary: Listar actividades de oportunidad
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
        - name: tipo_actividad_id
          in: query
          schema:
            type: integer
        - name: fecha_desde
          in: query
          schema:
            type: string
            format: date
        - name: fecha_hasta
          in: query
          schema:
            type: string
            format: date
        - name: page
          in: query
          schema:
            type: integer
            default: 1
        - name: page_size
          in: query
          schema:
            type: integer
            default: 20
      responses:
        '200':
          description: Lista de actividades
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ActividadListResponse'

    post:
      tags: [Actividades]
      summary: Registrar actividad
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ActividadCreateRequest'
      responses:
        '201':
          description: Actividad registrada
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ActividadResponse'
        '400':
          $ref: '#/components/responses/BadRequest'

  /actividades/{id}:
    get:
      tags: [Actividades]
      summary: Obtener actividad por ID
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: Actividad encontrada
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ActividadResponse'
        '404':
          $ref: '#/components/responses/NotFound'

  # ============================================================================
  # COMPROMISOS
  # ============================================================================
  /actividades/{id}/compromisos:
    get:
      tags: [Compromisos]
      summary: Listar compromisos de actividad
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: Lista de compromisos
          content:
            application/json:
              schema:
                type: object
                properties:
                  data:
                    type: array
                    items:
                      $ref: '#/components/schemas/CompromisoResponse'

    post:
      tags: [Compromisos]
      summary: Crear compromiso
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CompromisoCreateRequest'
      responses:
        '201':
          description: Compromiso creado
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/CompromisoResponse'
        '400':
          $ref: '#/components/responses/BadRequest'

  /compromisos:
    get:
      tags: [Compromisos]
      summary: Listar todos los compromisos
      parameters:
        - name: estado
          in: query
          schema:
            type: string
            enum: [PENDIENTE, EN_PROGRESO, COMPLETADO, CANCELADO]
        - name: responsable_id
          in: query
          schema:
            type: integer
        - name: fecha_desde
          in: query
          schema:
            type: string
            format: date
        - name: fecha_hasta
          in: query
          schema:
            type: string
            format: date
        - name: page
          in: query
          schema:
            type: integer
            default: 1
        - name: page_size
          in: query
          schema:
            type: integer
            default: 20
      responses:
        '200':
          description: Lista de compromisos
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/CompromisoListResponse'

  /compromisos/{id}:
    get:
      tags: [Compromisos]
      summary: Obtener compromiso por ID
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: Compromiso encontrado
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/CompromisoResponse'
        '404':
          $ref: '#/components/responses/NotFound'

    put:
      tags: [Compromisos]
      summary: Actualizar compromiso
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CompromisoUpdateRequest'
      responses:
        '200':
          description: Compromiso actualizado
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/CompromisoResponse'
        '400':
          $ref: '#/components/responses/BadRequest'
        '404':
          $ref: '#/components/responses/NotFound'

  # ============================================================================
  # DOCUMENTOS
  # ============================================================================
  /oportunidades/{id}/documentos:
    get:
      tags: [Documentos]
      summary: Listar documentos de oportunidad
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
        - name: tipo_documento_id
          in: query
          schema:
            type: integer
      responses:
        '200':
          description: Lista de documentos
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/DocumentoListResponse'

    post:
      tags: [Documentos]
      summary: Subir documento a oportunidad
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      requestBody:
        required: true
        content:
          multipart/form-data:
            schema:
              type: object
              required:
                - archivo
                - tipo_documento_id
              properties:
                archivo:
                  type: string
                  format: binary
                tipo_documento_id:
                  type: integer
                nombre:
                  type: string
      responses:
        '201':
          description: Documento subido
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/DocumentoResponse'
        '400':
          $ref: '#/components/responses/BadRequest'

  /actividades/{id}/documentos:
    get:
      tags: [Documentos]
      summary: Listar documentos de actividad
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: Lista de documentos
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/DocumentoListResponse'

    post:
      tags: [Documentos]
      summary: Subir documento a actividad
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      requestBody:
        required: true
        content:
          multipart/form-data:
            schema:
              type: object
              required:
                - archivo
                - tipo_documento_id
              properties:
                archivo:
                  type: string
                  format: binary
                tipo_documento_id:
                  type: integer
                nombre:
                  type: string
      responses:
        '201':
          description: Documento subido
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/DocumentoResponse'
        '400':
          $ref: '#/components/responses/BadRequest'

  /documentos/{id}:
    get:
      tags: [Documentos]
      summary: Obtener metadatos de documento
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: Documento encontrado
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/DocumentoResponse'
        '404':
          $ref: '#/components/responses/NotFound'

    delete:
      tags: [Documentos]
      summary: Eliminar documento
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '204':
          description: Documento eliminado
        '404':
          $ref: '#/components/responses/NotFound'

  /documentos/{id}/download:
    get:
      tags: [Documentos]
      summary: Descargar documento
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: Archivo binario
          content:
            application/octet-stream:
              schema:
                type: string
                format: binary
        '404':
          $ref: '#/components/responses/NotFound'

  # ============================================================================
  # CATÁLOGOS
  # ============================================================================
  /catalogos:
    get:
      tags: [Catálogos]
      summary: Listar catálogos por tipo
      parameters:
        - name: tipo
          in: query
          required: true
          schema:
            type: string
            enum: [TIPO_ACTIVIDAD, TIPO_DOCUMENTO, MOTIVO_CIERRE, MONEDA, TIPO_EMPRESA, ROL_CONTACTO]
        - name: activo
          in: query
          schema:
            type: boolean
            default: true
      responses:
        '200':
          description: Lista de valores del catálogo
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/CatalogoListResponse'
```

---

## 2. Resumen de Endpoints

### 2.1 Por Recurso

| Recurso | Endpoints | Métodos |
|---------|-----------|---------|
| Auth | 2 | POST, GET |
| Usuarios | 3 | GET, POST |
| Empresas | 4 | GET, POST, PUT |
| Personas | 4 | GET, POST, PUT |
| Pipelines | 5 | GET, POST, PUT |
| Oportunidades | 10 | GET, POST, PUT |
| Actividades | 3 | GET, POST |
| Compromisos | 5 | GET, POST, PUT |
| Documentos | 6 | GET, POST, DELETE |
| Catálogos | 1 | GET |
| **Total** | **43** | — |

### 2.2 Lista Completa de Endpoints

| Método | Path | Descripción |
|--------|------|-------------|
| POST | `/auth/login` | Iniciar sesión |
| GET | `/auth/me` | Usuario actual |
| GET | `/usuarios` | Listar usuarios |
| POST | `/usuarios` | Crear usuario |
| GET | `/usuarios/{id}` | Obtener usuario |
| GET | `/empresas` | Listar empresas |
| POST | `/empresas` | Crear empresa |
| GET | `/empresas/{id}` | Obtener empresa |
| PUT | `/empresas/{id}` | Actualizar empresa |
| GET | `/empresas/{id}/contactos` | Contactos de empresa |
| GET | `/personas` | Listar personas |
| POST | `/personas` | Crear persona |
| GET | `/personas/{id}` | Obtener persona |
| PUT | `/personas/{id}` | Actualizar persona |
| POST | `/personas/{id}/empresas` | Asociar a empresa |
| GET | `/pipelines` | Listar pipelines |
| POST | `/pipelines` | Crear pipeline |
| GET | `/pipelines/{id}` | Obtener pipeline |
| PUT | `/pipelines/{id}` | Actualizar pipeline |
| POST | `/pipelines/{id}/etapas` | Crear etapa |
| PUT | `/pipelines/{pid}/etapas/{eid}` | Actualizar etapa |
| GET | `/oportunidades` | Listar oportunidades |
| POST | `/oportunidades` | Crear oportunidad |
| GET | `/oportunidades/{id}` | Obtener oportunidad |
| PUT | `/oportunidades/{id}` | Actualizar oportunidad |
| POST | `/oportunidades/{id}/mover-etapa` | Mover etapa |
| POST | `/oportunidades/{id}/cerrar` | Cerrar oportunidad |
| GET | `/oportunidades/{id}/responsables` | Listar responsables |
| POST | `/oportunidades/{id}/responsables` | Agregar responsable |
| GET | `/oportunidades/{id}/contactos` | Listar contactos |
| POST | `/oportunidades/{id}/contactos` | Agregar contacto |
| GET | `/oportunidades/{id}/historial` | Historial etapas |
| GET | `/oportunidades/{id}/actividades` | Listar actividades |
| POST | `/oportunidades/{id}/actividades` | Crear actividad |
| GET | `/actividades/{id}` | Obtener actividad |
| GET | `/actividades/{id}/compromisos` | Compromisos de actividad |
| POST | `/actividades/{id}/compromisos` | Crear compromiso |
| GET | `/compromisos` | Listar compromisos |
| GET | `/compromisos/{id}` | Obtener compromiso |
| PUT | `/compromisos/{id}` | Actualizar compromiso |
| GET | `/oportunidades/{id}/documentos` | Documentos de oportunidad |
| POST | `/oportunidades/{id}/documentos` | Subir a oportunidad |
| GET | `/actividades/{id}/documentos` | Documentos de actividad |
| POST | `/actividades/{id}/documentos` | Subir a actividad |
| GET | `/documentos/{id}` | Obtener metadatos |
| DELETE | `/documentos/{id}` | Eliminar documento |
| GET | `/documentos/{id}/download` | Descargar archivo |
| GET | `/catalogos` | Listar por tipo |

---

## 3. Códigos de Error

### 3.1 Códigos HTTP

| Código | Uso |
|--------|-----|
| 200 | OK - Operación exitosa |
| 201 | Created - Recurso creado |
| 204 | No Content - Eliminación exitosa |
| 400 | Bad Request - Error de validación |
| 401 | Unauthorized - No autenticado |
| 403 | Forbidden - Sin permisos |
| 404 | Not Found - Recurso no existe |
| 409 | Conflict - Error de negocio |
| 500 | Internal Error - Error del servidor |

### 3.2 Códigos de Error de Negocio

| Código | Descripción |
|--------|-------------|
| `VALIDATION_ERROR` | Errores de validación de campos |
| `UNAUTHORIZED` | Token inválido o expirado |
| `FORBIDDEN` | Sin permisos para la acción |
| `NOT_FOUND` | Recurso no encontrado |
| `BUSINESS_ERROR` | Violación de regla de negocio |
| `DUPLICATE_ERROR` | Registro duplicado |
| `INTERNAL_ERROR` | Error interno del servidor |

### 3.3 Ejemplos de Errores

**Validación (400):**
```json
{
  "code": "VALIDATION_ERROR",
  "message": "Error de validación en los datos enviados",
  "field_errors": [
    {
      "field": "nombre",
      "message": "El campo es requerido"
    },
    {
      "field": "email",
      "message": "Formato de email inválido",
      "rejected_value": "correo-invalido"
    }
  ],
  "timestamp": "2026-02-07T10:30:00Z"
}
```

**Regla de Negocio (409):**
```json
{
  "code": "BUSINESS_ERROR",
  "message": "No se puede cerrar como PERDIDA sin motivo de cierre",
  "details": "El campo motivo_cierre_id es obligatorio cuando estado_macro es PERDIDA o NO_CONCRETADA",
  "timestamp": "2026-02-07T10:30:00Z"
}
```

**No Encontrado (404):**
```json
{
  "code": "NOT_FOUND",
  "message": "Recurso no encontrado",
  "details": "No existe oportunidad con ID 999",
  "timestamp": "2026-02-07T10:30:00Z"
}
```

---

*Fin del documento B-03 - Contrato de API OpenAPI*
