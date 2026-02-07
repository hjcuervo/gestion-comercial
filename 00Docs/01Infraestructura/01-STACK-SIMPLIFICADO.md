# Stack Tecnológico - Plataforma Gestión Comercial
## Arquitecsoft - Versión Simplificada

**Fecha:** Febrero 2026  
**Usuarios:** ~5 (uso interno)  
**Criticidad:** Herramienta de apoyo

---

## Arquitectura

```
┌──────────────────────────────────────────────┐
│            VM en OCI (1 OCPU, 8GB)           │
│                                              │
│   ┌────────────────────────────────────┐     │
│   │          Docker Compose            │     │
│   │                                    │     │
│   │   ┌──────────┐   ┌─────────────┐  │     │
│   │   │  Nginx   │   │ Spring Boot │  │     │
│   │   │  + Vue   │──▶│    API      │  │     │
│   │   │  :80     │   │   :8080     │  │     │
│   │   └──────────┘   └──────┬──────┘  │     │
│   │                         │         │     │
│   └─────────────────────────┼─────────┘     │
│                             │               │
└─────────────────────────────┼───────────────┘
                              │
              ┌───────────────▼────────────────┐
              │      Oracle DB Standard        │
              │  arqsmallpldbs01-public-phx     │
              │  .infra.arqbs.com:1621         │
              └────────────────────────────────┘
```

---

## Decisiones Tecnológicas

### Frontend
| Aspecto | Decisión |
|---------|----------|
| Framework | Vue 3 + Vite |
| UI | PrimeVue |
| State | Pinia (solo si es necesario) |
| HTTP | Axios |

### Backend
| Aspecto | Decisión |
|---------|----------|
| Lenguaje | Java 21 |
| Framework | Spring Boot 3.3 |
| Auth | JWT simple (8 horas expiración) |
| Docs API | SpringDoc OpenAPI |

### Base de Datos
| Aspecto | Decisión |
|---------|----------|
| Motor | Oracle Database Standard |
| Conexión | JDBC directa (sin wallet) |
| Host | arqsmallpldbs01-public-phx.infra.arqbs.com |
| Puerto | 1621 |
| Servicio | CDBSMALL_ARQSMALLPLDBS01_COMMSRV.paas.oracle.com |
| Esquema DEV | ARQGCDEV |
| Migraciones | Flyway |
| Pool | HikariCP (default Spring Boot) |

### Infraestructura
| Aspecto | Decisión |
|---------|----------|
| Servidor | 1 VM OCI (VM.Standard.E4.Flex) |
| Contenedores | Docker Compose |
| SSL | Let's Encrypt (Certbot) |
| Deploy | Script bash manual |

### Ambientes
| Ambiente | Uso |
|----------|-----|
| Local | Desarrollo (docker-compose up) |
| Producción | VM en OCI |

---

## Estructura de Proyecto

```
gestion-comercial/
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   ├── views/
│   │   ├── services/
│   │   ├── stores/
│   │   ├── router/
│   │   ├── App.vue
│   │   └── main.ts
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── package.json
│   └── vite.config.ts
├── backend/
│   ├── src/main/java/com/arquitecsoft/gestion/
│   │   ├── config/
│   │   ├── auth/
│   │   ├── [modulos]/
│   │   └── GestionApplication.java
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   ├── application-prod.yml
│   │   └── db/migration/
│   ├── Dockerfile
│   └── pom.xml
├── docker-compose.yml
├── docker-compose.prod.yml
├── deploy.sh
├── .env.example
└── README.md
```

---

## Qué NO incluye (y por qué)

| Excluido | Razón |
|----------|-------|
| Oracle Wallet / mTLS | No es ATP, conexión JDBC directa |
| Kubernetes/OKE | Overkill para 5 usuarios |
| Load Balancer | Una VM es suficiente |
| WAF | Red interna, riesgo bajo |
| CI/CD complejo | Deploy manual es viable |
| Múltiples ambientes | Local + prod suficiente |
| APM/Tracing distribuido | Logs simples bastan |
| Vault para secretos | .env en servidor es aceptable |

---

## Costo Mensual Estimado

| Recurso | Costo USD |
|---------|-----------|
| VM E4.Flex (1 OCPU, 8GB) | $7-15 |
| Oracle DB | Ya existente |
| Block Storage (50GB) | $2 |
| **Total** | **~$10-20/mes** |
