# Mirakuru Tracker — Examen Final

Sistema de dominio para el **Equipo Arrow**. Es el *corazón lógico* que permite
**identificar y rastrear** afectados por el suero **Mirakuru**, **clasificar
amenazas** por riesgo, **coordinar misiones** entre los miembros del equipo,
**integrar información de múltiples fuentes externas** y **priorizar objetivos**
con reglas claras y consistentes. Todo el dominio se expone mediante una **API
REST** sin romper los principios arquitectónicos.

Diseñado aplicando **Domain Driven Design (DDD)**, **principios SOLID**,
**servicios de dominio**, **puertos e interfaces** y **Arquitectura Hexagonal**.

> El código está escrito en **inglés** (clases, métodos, variables); la
> documentación y la justificación del diseño están en **español**. Las rutas de
> la API se mantienen en español (`/api/afectados`, `/api/misiones`,
> `/api/equipo`) tal como exige el enunciado.

---

## 1. Mapa de capacidades exigidas → implementación

| Capacidad del enunciado | Implementada en | Estado |
|-------------------------|-----------------|--------|
| Identificar y rastrear afectados por Mirakuru | Aggregate `AffectedIndividual` + CRUD REST | ✅ |
| Clasificar amenazas según su nivel de riesgo | Servicio de dominio `ThreatAssessmentService` → `ThreatLevel` | ✅ |
| Coordinar misiones entre los miembros del equipo | Aggregate `Mission` + `MissionCoordinationService` + roster `TeamMember` | ✅ |
| Integrar información de múltiples fuentes externas | Puerto `ThreatIntelligenceSource` + N adaptadores + `ThreatIntelligenceService` | ✅ |
| Priorizar objetivos con reglas claras | `ThreatAssessmentService.priorityScore` + listado ordenado | ✅ |

---

## 2. Stack tecnológico

| Tecnología      | Versión |
|-----------------|---------|
| Java            | 21      |
| Spring Boot     | 3.5.0   |
| Spring Web      | REST    |
| Bean Validation | jakarta |
| Maven Wrapper   | incluido (`mvnw`) |
| JUnit 5 / MockMvc / AssertJ | tests (24) |

No requiere base de datos: la persistencia y las fuentes externas son
**adaptadores** intercambiables sin tocar el núcleo.

---

## 3. Cómo ejecutar

```bash
# Ejecutar la API (puerto 8080)
./mvnw spring-boot:run        # Linux / Mac / Git Bash
.\mvnw.cmd spring-boot:run    # Windows PowerShell

# Ejecutar las 24 pruebas
./mvnw test

# Empaquetar
./mvnw clean package
```

---

## 4. Arquitectura Hexagonal (Puertos y Adaptadores)

Regla de oro: **las dependencias apuntan hacia adentro**. El dominio no conoce a
la aplicación, la aplicación no conoce a la infraestructura, y **nadie del núcleo
conoce a Spring** (el núcleo no tiene ni una sola anotación de framework; se
"enchufa" desde `BeanConfiguration`).

```
            ADAPTADORES DRIVING (entrada)            NÚCLEO (sin framework)            ADAPTADORES DRIVEN (salida)
        ┌───────────────────────────────┐    ┌──────────────────────────────┐   ┌──────────────────────────────┐
 HTTP ─►│ AffectedController             │    │ APPLICATION                  │   │ InMemoryAffectedRepository    │
 HTTP ─►│ MissionController              │──► │  port.in  (casos de uso)     │ ◄─│ InMemoryMissionRepository     │
 HTTP ─►│ TeamController                 │    │  port.out (repos, intel)     │   │ InMemoryTeamMemberRepository  │
 HTTP ─►│ IntelligenceController         │    │  services (orquestación)     │   ├──────────────────────────────┤
        │ DTOs · Mappers · ErrorHandler  │    │ DOMAIN                       │ ◄─│ ArgusIntelligenceAdapter       │
        └───────────────────────────────┘    │  aggregates · VOs · domain   │   │ StreetInformantIntelligence... │
                                              │  services · exceptions       │   │  (fuentes externas)            │
                                              └──────────────────────────────┘   └──────────────────────────────┘
```

Hay **4 adaptadores driving** (4 controllers) y **5 adaptadores driven** (3
repositorios + 2 fuentes de inteligencia) conectados al **mismo** núcleo: la
prueba de que la arquitectura es reutilizable y desacoplada.

### Estructura de paquetes

```
com.teamarrow.mirakuru
├── domain                         ← Núcleo. Java puro, cero dependencias de framework.
│   ├── model
│   │   ├── AffectedIndividual     ← Aggregate Root (afectado)
│   │   ├── AffectedId, CodeName, MirakuruSaturation, AggressionIndex,
│   │   │   Location, ThreatLevel, AffectedStatus      ← Value Objects / enums
│   │   ├── mission
│   │   │   ├── Mission            ← Aggregate Root (misión)
│   │   │   └── MissionId, MissionStatus
│   │   ├── team
│   │   │   ├── TeamMember         ← Entidad (operativo)
│   │   │   └── TeamMemberId, OperativeRole
│   │   └── intel
│   │       └── IntelReport        ← Value Object (reporte externo)
│   ├── service
│   │   ├── ThreatAssessmentService (+ WeightedThreatAssessmentService)   ← clasifica y prioriza
│   │   └── MissionCoordinationService (+ StandardMissionCoordinationService) ← dotación de misiones
│   └── exception                  ← DomainException, EntityNotFoundException, ...
│
├── application                    ← Casos de uso. Orquesta, no contiene reglas.
│   ├── port.in                    ← Puertos de ENTRADA (un caso de uso por interfaz → ISP)
│   │   ├── Register/Get/List/UpdateAffectedUseCase
│   │   ├── Plan/Assign/Launch/Get/ListMission(s)UseCase
│   │   ├── ListTeamMembersUseCase · GatherIntelligenceUseCase
│   │   └── command                ← *Command (entrada cruda)
│   ├── port.out                   ← Puertos de SALIDA
│   │   ├── AffectedRepository · MissionRepository · TeamMemberRepository
│   │   └── ThreatIntelligenceSource   ← integración con fuentes externas
│   └── service
│       ├── AffectedService · MissionService · TeamService
│       └── ThreatIntelligenceService
│
└── infrastructure                 ← Adaptadores + wiring de Spring.
    ├── config.BeanConfiguration            ← composition root
    └── adapter
        ├── in.web                          ← 4 controllers, DTOs, mappers, manejo de errores
        └── out
            ├── persistence                 ← repos en memoria (afectados, misiones, roster)
            └── intel                        ← fuentes externas simuladas (A.R.G.U.S., informantes)
```

---

## 5. Bloques de DDD y por qué se modelaron así

| Bloque DDD | En el código | Justificación |
|-----------|--------------|---------------|
| **Aggregate Root / Entidad** | `AffectedIndividual`, `Mission`, `TeamMember` | Tienen **identidad** y ciclo de vida propio; igualdad por id. Cada aggregate es la **única puerta** para mutar su estado (factorías + métodos que revelan intención), así sus invariantes nunca se violan. `Mission` expone sus operativos como conjunto **inmutable** para que nadie evada las reglas de dotación. |
| **Value Objects** | `MirakuruSaturation`, `AggressionIndex`, `CodeName`, `Location`, `AffectedId`, `MissionId`, `TeamMemberId`, `IntelReport` | **Inmutables** y **validados en el constructor**: imposible construir uno inválido. Evitan *primitive obsession*. |
| **Servicios de Dominio** | `ThreatAssessmentService`, `MissionCoordinationService` | Lógica *stateless* que combina **varios** conceptos y no pertenece a una sola entidad: clasificar amenaza (saturación + agresividad), priorizar (… + estado) y decidir la dotación de una misión (nivel de amenaza del objetivo → nº de operativos). Son **interfaces** → la política es intercambiable. |
| **Puertos de salida** | `AffectedRepository`, `MissionRepository`, `TeamMemberRepository`, `ThreatIntelligenceSource` | Abstracciones definidas **en términos del dominio**. La tecnología (memoria, BD, API de terceros) es un detalle del adaptador. |
| **Puertos de entrada (casos de uso)** | `*UseCase` | Contrato de lo que el sistema *hace*, desacoplado de *cómo* se invoca. |
| **Application Services** | `AffectedService`, `MissionService`, `TeamService`, `ThreatIntelligenceService` | **Orquestan**: traducen comandos a VOs, dirigen aggregates y servicios de dominio, persisten. Sin reglas de negocio dentro. |
| **DTOs + Mappers (anticorrupción en el borde)** | `*Request`, `*Response`, `AffectedDtoMapper`, `MissionDtoMapper` | El modelo de dominio nunca se serializa directo; el contrato público evoluciona sin arrastrar al dominio. |

---

## 6. Principios SOLID aplicados

- **S — Single Responsibility:** cada clase hace una cosa. Los controllers solo
  traducen HTTP; los application services solo orquestan; los VOs solo custodian
  su invariante. `TeamService` se separó de `MissionService` por esto.
- **O — Open/Closed:** las políticas son **interfaces** (`ThreatAssessmentService`,
  `MissionCoordinationService`). Y **añadir una nueva fuente externa de
  inteligencia es solo un nuevo adaptador** `ThreatIntelligenceSource`: el núcleo
  no cambia (Spring inyecta la lista completa de fuentes).
- **L — Liskov:** cualquier implementación de un repositorio, del servicio de
  amenazas o de una fuente de inteligencia sustituye a otra sin romper clientes.
- **I — Interface Segregation:** los casos de uso son **interfaces de un solo
  método**; cada controller depende solo de las capacidades exactas que usa.
- **D — Dependency Inversion:** el núcleo declara las abstracciones que necesita
  y la infraestructura las implementa. `BeanConfiguration` es el **único** punto
  de acoplamiento con Spring; dominio y aplicación quedan libres de framework.

---

## 7. Reglas de negocio

**Clasificación de amenaza** (`ThreatLevel`), causa biológica con más peso que el
síntoma observable:

```
score = saturación(0-100) * 0.6 + (agresividad(0-10) * 10) * 0.4
≥80 CRITICAL · ≥60 HIGH · ≥35 MODERATE · <35 LOW
```

**Priorización de objetivos** (`priorityScore`): mismo puntaje base atenuado por
estado (`AT_LARGE` ×1.0, `MONITORED` ×0.5, `NEUTRALIZED`/`CURED` → 0).
`GET /api/afectados` devuelve la lista **ordenada por prioridad descendente**.

**Ciclo de vida del afectado:** `AT_LARGE → MONITORED → NEUTRALIZED → CURED`.
`CURED` es **terminal** (modificarlo → 400). El `CodeName` es **único** (→ 409).

**Coordinación de misiones:** la dotación requerida la decide el dominio según el
nivel de amenaza del objetivo (`LOW=1, MODERATE=2, HIGH=3, CRITICAL=4`). Reglas
del aggregate `Mission`: solo se asignan operativos en `PLANNING`, no se permite
asignar dos veces al mismo, y **no se puede lanzar sin la dotación completa**.

**Integración de inteligencia:** `ThreatIntelligenceService` consulta **todas**
las fuentes externas registradas para un afectado y combina los reportes
ordenados por confianza (mayor primero). Hoy hay dos fuentes simuladas
(A.R.G.U.S. y red de informantes); sumar otra es solo un adaptador más.

---

## 8. API REST

Base: `http://localhost:8080`

### Afectados (obligatorios del examen)

| Método | Ruta                  | Acción                        | Éxito |
|--------|-----------------------|-------------------------------|-------|
| POST   | `/api/afectados`      | Registrar un afectado         | 201 + `Location` |
| GET    | `/api/afectados/{id}` | Consultar un afectado         | 200 |
| GET    | `/api/afectados`      | Listar todos (por prioridad)  | 200 |
| PUT    | `/api/afectados/{id}` | Actualizar un afectado        | 200 |

### Misiones, equipo e inteligencia (capacidades adicionales del dominio)

| Método | Ruta                               | Acción |
|--------|------------------------------------|--------|
| POST   | `/api/misiones`                    | Planear una misión contra un objetivo |
| POST   | `/api/misiones/{id}/operativos`    | Asignar un operativo (por code name) |
| POST   | `/api/misiones/{id}/lanzar`        | Lanzar la misión (valida dotación) |
| GET    | `/api/misiones/{id}` · `/api/misiones` | Consultar / listar misiones |
| GET    | `/api/equipo`                      | Listar el roster del equipo |
| GET    | `/api/afectados/{id}/inteligencia` | Inteligencia agregada de fuentes externas |

### Ejemplo de flujo completo

```bash
# 1) Registrar un afectado (CRITICAL)
curl -s -X POST http://localhost:8080/api/afectados -H "Content-Type: application/json" -d '{
  "codeName":"Deathstroke","mirakuruSaturation":95,"aggressionIndex":9,
  "locationSector":"Glades","latitude":40.71,"longitude":-74.0 }'

# 2) Ver inteligencia externa sobre él
curl -s http://localhost:8080/api/afectados/{id}/inteligencia

# 3) Conocer el roster
curl -s http://localhost:8080/api/equipo

# 4) Planear misión (CRITICAL ⇒ requiere 4 operativos)
curl -s -X POST http://localhost:8080/api/misiones -H "Content-Type: application/json" -d '{
  "name":"Operation Shadow","targetAffectedId":"{id}" }'

# 5) Asignar operativos y lanzar
curl -s -X POST http://localhost:8080/api/misiones/{mid}/operativos -H "Content-Type: application/json" -d '{"operativeCodeName":"Green Arrow"}'
curl -s -X POST http://localhost:8080/api/misiones/{mid}/lanzar
```

Respuesta de registro de afectado `201 Created`:

```json
{
  "id": "0f2a...-...",
  "codeName": "Deathstroke",
  "mirakuruSaturation": 95, "aggressionIndex": 9,
  "status": "AT_LARGE", "threatLevel": "CRITICAL", "priorityScore": 93,
  "location": { "sector": "Glades", "latitude": 40.71, "longitude": -74.0 },
  "registeredAt": "2026-06-01T21:00:00Z", "lastUpdatedAt": "2026-06-01T21:00:00Z"
}
```

### Códigos de error (envoltura uniforme `ApiError`)

| Situación | HTTP | Excepción |
|-----------|------|-----------|
| Aggregate inexistente (afectado / misión / operativo) | 404 | `EntityNotFoundException` y subtipos |
| Code name de afectado duplicado | 409 | `DuplicateAffectedException` |
| Regla de negocio violada / id mal formado / misión sin dotación | 400 | `DomainException` |
| Validación del body (Bean Validation) | 400 | `MethodArgumentNotValidException` |

```json
{
  "timestamp": "2026-06-01T21:05:00Z", "status": 400, "error": "Bad Request",
  "message": "Validation failed for the request body", "path": "/api/afectados",
  "fieldErrors": [ { "field": "mirakuruSaturation", "message": "mirakuruSaturation must be at most 100" } ]
}
```

---

## 9. Doble validación: borde vs. dominio

Validación en **dos niveles, a propósito**: (1) **Bean Validation en los DTOs**,
primera línea barata en el borde HTTP (rangos sintácticos, 400 amigables); (2)
**invariantes en los Value Objects del dominio**, la fuente de verdad
autoritativa que garantiza que el modelo **nunca** se construya inválido, sin
importar el punto de entrada. Si mañana el dominio se expone por otro adaptador,
las reglas siguen protegidas porque viven en el núcleo.

---

## 10. Pruebas (24, todas en verde)

```bash
./mvnw test    # Tests run: 24, Failures: 0, Errors: 0 — BUILD SUCCESS
```

- **Unitarias puras** (sin Spring, sin mocks — beneficio de un dominio libre de
  framework): `WeightedThreatAssessmentServiceTest`,
  `StandardMissionCoordinationServiceTest`, `MissionTest`.
- **Integración end-to-end** sobre el wiring real (controller → caso de uso →
  dominio → adaptador): `AffectedControllerIntegrationTest`,
  `MissionFlowIntegrationTest`, `IntelligenceIntegrationTest`, incluyendo los
  caminos de error 404 / 409 / 400.
