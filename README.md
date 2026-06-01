# Mirakuru Tracker — Examen Final

Sistema de dominio para el **Equipo Arrow**. Permite identificar, rastrear,
clasificar y **priorizar** a los individuos afectados por el suero **Mirakuru**,
expuesto a través de una **API REST**.

El examen pide diseñar el *corazón lógico* del sistema aplicando **Domain Driven
Design (DDD)**, **principios SOLID**, **servicios de dominio**, **puertos e
interfaces** y **Arquitectura Hexagonal**, y luego exponerlo mediante una API
REST **sin romper** esos principios arquitectónicos.

> El código está escrito en **inglés** (clases, métodos, variables); la
> documentación y la justificación del diseño están en **español**. Las rutas de
> la API se mantienen en español (`/api/afectados`) tal como exige el enunciado.

---

## 1. Stack tecnológico

| Tecnología      | Versión |
|-----------------|---------|
| Java            | 21      |
| Spring Boot     | 3.5.0   |
| Spring Web      | REST    |
| Bean Validation | jakarta |
| Maven Wrapper   | incluido (`mvnw`) |
| JUnit 5 / MockMvc | tests |

No requiere base de datos: la persistencia es un adaptador **en memoria**
(intercambiable sin tocar el núcleo).

---

## 2. Cómo ejecutar

```bash
# Ejecutar la API (puerto 8080)
./mvnw spring-boot:run        # Linux / Mac / Git Bash
.\mvnw.cmd spring-boot:run    # Windows PowerShell

# Ejecutar las pruebas
./mvnw test

# Empaquetar
./mvnw clean package
```

---

## 3. Arquitectura Hexagonal (Puertos y Adaptadores)

La regla de oro: **las dependencias apuntan hacia adentro**. El dominio no
conoce a la aplicación, la aplicación no conoce a la infraestructura, y **nadie
del núcleo conoce a Spring**.

```
                     ┌─────────────────────────────────────────────┐
                     │            INFRASTRUCTURE (adapters)          │
                     │                                               │
   HTTP  ───────►  ┌─┴─ AffectedController  (driving / in-adapter)   │
                   │   GlobalExceptionHandler, DTOs, Mapper          │
                   │            │                                    │
                   │            ▼  depende de los PUERTOS DE ENTRADA │
        ┌──────────┼───────────────────────────────────────────┐   │
        │ APPLICATION                                           │   │
        │   port.in  (use cases)   AffectedService              │   │
        │   port.out (repository)        │                      │   │
        └──────────┼─────────────────────┼─────────────────────┘   │
                   │                      ▼  usa el dominio          │
        ┌──────────┼───────────────────────────────────────────┐   │
        │ DOMAIN (Java puro, sin frameworks)                    │   │
        │   AffectedIndividual (aggregate root)                 │   │
        │   Value Objects · ThreatAssessmentService · Exceptions│   │
        └───────────────────────────────────────────────────────┘   │
                   ▲                                                 │
                   │  implementa el PUERTO DE SALIDA                 │
                   └─ InMemoryAffectedRepository (driven / out-adapter)
                     └─────────────────────────────────────────────┘
```

### Estructura de paquetes

```
com.teamarrow.mirakuru
├── domain                         ← Núcleo. Java puro, cero dependencias de framework.
│   ├── model
│   │   ├── AffectedIndividual     ← Aggregate Root (Entidad)
│   │   ├── AffectedId             ← Value Object (identidad)
│   │   ├── CodeName               ← Value Object
│   │   ├── MirakuruSaturation     ← Value Object (0–100)
│   │   ├── AggressionIndex        ← Value Object (0–10)
│   │   ├── Location               ← Value Object
│   │   ├── ThreatLevel            ← Enum (LOW…CRITICAL)
│   │   └── AffectedStatus         ← Enum (ciclo de vida)
│   ├── service
│   │   ├── ThreatAssessmentService          ← Servicio de Dominio (interfaz)
│   │   └── WeightedThreatAssessmentService  ← implementación
│   └── exception                  ← DomainException + subtipos
│
├── application                    ← Casos de uso. Orquesta, no contiene reglas.
│   ├── port.in                    ← Puertos de ENTRADA (un caso de uso por interfaz)
│   │   ├── RegisterAffectedUseCase / GetAffectedUseCase
│   │   ├── ListAffectedUseCase    / UpdateAffectedUseCase
│   │   └── command                ← RegisterAffectedCommand / UpdateAffectedCommand
│   ├── port.out
│   │   └── AffectedRepository      ← Puerto de SALIDA
│   └── service
│       └── AffectedService         ← implementa los 4 casos de uso
│
└── infrastructure                 ← Adaptadores + wiring de Spring.
    ├── config.BeanConfiguration            ← composition root (wiring del núcleo)
    └── adapter
        ├── in.web                          ← Controller, DTOs, Mapper, manejo de errores
        └── out.persistence                 ← InMemoryAffectedRepository
```

---

## 4. Bloques de DDD y por qué se modelaron así

| Bloque DDD            | En el código                        | Justificación |
|-----------------------|-------------------------------------|---------------|
| **Aggregate Root / Entidad** | `AffectedIndividual` | Tiene **identidad** (`AffectedId`) y ciclo de vida propio; dos afectados con los mismos datos siguen siendo personas distintas → igualdad por id. Es la **única puerta** para mutar su estado (`register`, `updateProfile`), por lo que sus invariantes nunca se violan. |
| **Value Objects**     | `MirakuruSaturation`, `AggressionIndex`, `CodeName`, `Location`, `AffectedId` | Son **inmutables** y se **validan en el constructor**, así que es imposible construir uno inválido. Evitan *primitive obsession*: el sistema habla de un `MirakuruSaturation`, no de un `int` anónimo. |
| **Servicio de Dominio** | `ThreatAssessmentService` (+ `WeightedThreatAssessmentService`) | La clasificación y priorización combinan **varios** conceptos (saturación + agresividad + estado) y no pertenecen naturalmente a una sola entidad. Por DDD, esa lógica *stateless* vive en un Servicio de Dominio. |
| **Puerto de salida (Repository)** | `AffectedRepository` | Abstracción de persistencia definida **en términos del dominio**; la tecnología concreta es un detalle del adaptador. |
| **Puertos de entrada (Use Cases)** | `RegisterAffectedUseCase`, etc. | Definen el contrato de lo que el sistema *hace*, desacoplado de *cómo* se invoca (HTTP hoy, colas o gRPC mañana). |
| **Application Service** | `AffectedService` | **Orquesta**: traduce comandos a Value Objects, invoca al aggregate y al servicio de dominio, persiste. No contiene reglas de negocio. |
| **DTOs / Anti-corruption en el borde** | `*Request`, `*Response`, `AffectedDtoMapper` | El modelo de dominio nunca se serializa directamente; el contrato público de la API puede evolucionar sin arrastrar al dominio. |

---

## 5. Principios SOLID aplicados

- **S — Single Responsibility:** cada clase hace una cosa. El controller solo
  traduce HTTP; `AffectedService` solo orquesta; los Value Objects solo
  custodian su invariante; `WeightedThreatAssessmentService` solo calcula
  amenaza.
- **O — Open/Closed:** la regla de clasificación es una **interfaz**
  (`ThreatAssessmentService`). Cambiar la fórmula = nueva implementación, **sin
  tocar** el aggregate ni los casos de uso.
- **L — Liskov:** cualquier `AffectedRepository` o `ThreatAssessmentService`
  puede sustituir a otro sin romper a sus clientes (los tests usan el repo en
  memoria; producción podría usar JPA).
- **I — Interface Segregation:** los casos de uso son **interfaces de un solo
  método**. El controller depende solo de la capacidad exacta que usa, no de un
  "servicio gordo".
- **D — Dependency Inversion:** el núcleo declara las **abstracciones** que
  necesita (`AffectedRepository`) y la infraestructura las implementa. El
  `BeanConfiguration` es el único punto donde se "enchufa" Spring, manteniendo
  el dominio y la aplicación **libres de framework**.

---

## 6. Reglas de negocio

**Clasificación de amenaza** (`ThreatLevel`) a partir de un puntaje ponderado
donde la causa biológica pesa más que el síntoma observable:

```
score = saturación(0-100) * 0.6 + (agresividad(0-10) * 10) * 0.4
```

| score      | ThreatLevel |
|------------|-------------|
| ≥ 80       | CRITICAL    |
| ≥ 60       | HIGH        |
| ≥ 35       | MODERATE    |
| < 35       | LOW         |

**Priorización de objetivos** (`priorityScore`): usa el mismo puntaje base pero
lo atenúa por el **estado**, de modo que los ya contenidos caen al fondo de la
cola. `GET /api/afectados` devuelve la lista **ordenada por prioridad
descendente**.

- Un afectado `AT_LARGE` (a la fuga) usa multiplicador `1.0`.
- Un afectado `MONITORED` usa `0.5`.
- `NEUTRALIZED` o `CURED` → prioridad `0` (ya no son amenaza activa).

**Ciclo de vida** (`AffectedStatus`): `AT_LARGE → MONITORED → NEUTRALIZED →
CURED`. Un individuo **`CURED` es terminal**: intentar modificarlo lanza
`DomainException` (HTTP 400). El `CodeName` debe ser **único** (HTTP 409 si se
duplica).

---

## 7. API REST

Base: `http://localhost:8080/api/afectados`

| Método | Ruta                  | Acción                        | Éxito |
|--------|-----------------------|-------------------------------|-------|
| POST   | `/api/afectados`      | Registrar un afectado         | 201 + `Location` |
| GET    | `/api/afectados/{id}` | Consultar un afectado         | 200 |
| GET    | `/api/afectados`      | Listar todos (por prioridad)  | 200 |
| PUT    | `/api/afectados/{id}` | Actualizar un afectado        | 200 |

### Ejemplos

**Registrar**

```bash
curl -i -X POST http://localhost:8080/api/afectados \
  -H "Content-Type: application/json" \
  -d '{
        "codeName": "Deathstroke",
        "mirakuruSaturation": 95,
        "aggressionIndex": 9,
        "locationSector": "Glades",
        "latitude": 40.71,
        "longitude": -74.0
      }'
```

Respuesta `201 Created`:

```json
{
  "id": "0f2a...-...-...",
  "codeName": "Deathstroke",
  "mirakuruSaturation": 95,
  "aggressionIndex": 9,
  "status": "AT_LARGE",
  "threatLevel": "CRITICAL",
  "priorityScore": 93,
  "location": { "sector": "Glades", "latitude": 40.71, "longitude": -74.0 },
  "registeredAt": "2026-06-01T21:00:00Z",
  "lastUpdatedAt": "2026-06-01T21:00:00Z"
}
```

**Consultar por id**

```bash
curl http://localhost:8080/api/afectados/{id}
```

**Listar todos** (ordenados por prioridad de combate)

```bash
curl http://localhost:8080/api/afectados
```

**Actualizar**

```bash
curl -X PUT http://localhost:8080/api/afectados/{id} \
  -H "Content-Type: application/json" \
  -d '{
        "codeName": "Deathstroke",
        "mirakuruSaturation": 20,
        "aggressionIndex": 1,
        "locationSector": "Safehouse",
        "latitude": 40.71,
        "longitude": -74.0,
        "status": "NEUTRALIZED"
      }'
```

### Códigos de error (envoltura uniforme `ApiError`)

| Situación                         | HTTP | Excepción |
|-----------------------------------|------|-----------|
| Afectado inexistente              | 404  | `AffectedNotFoundException` |
| Code name duplicado               | 409  | `DuplicateAffectedException` |
| Regla de negocio violada / id mal formado | 400 | `DomainException` |
| Validación del body (Bean Validation) | 400 | `MethodArgumentNotValidException` |

```json
{
  "timestamp": "2026-06-01T21:05:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for the request body",
  "path": "/api/afectados",
  "fieldErrors": [
    { "field": "mirakuruSaturation", "message": "mirakuruSaturation must be at most 100" }
  ]
}
```

---

## 8. Doble validación: borde vs. dominio

Existe validación en **dos niveles, a propósito**:

1. **Bean Validation en los DTOs** — primera línea barata en el borde HTTP
   (payload bien formado, rangos sintácticos). Devuelve 400 amigables.
2. **Invariantes en los Value Objects del dominio** — fuente de verdad
   autoritativa. Garantizan que el modelo **nunca** pueda construirse en estado
   inválido, sin importar el punto de entrada (HTTP, una cola, un test).

Si mañana se expone el dominio por otro adaptador, las reglas siguen
protegidas porque viven en el núcleo, no en el controller.

---

## 9. Cómo escala al resto del sistema

El enunciado describe un sistema mayor (coordinar misiones, integrar fuentes
externas de inteligencia). La arquitectura hexagonal lo soporta **sin
reescrituras**:

- **Coordinar misiones** → nuevo *aggregate* `Mission` con sus propios casos de
  uso y puerto `MissionRepository`, en su bounded context.
- **Integrar fuentes externas** → un **puerto de salida** `ThreatIntelPort` con
  adaptadores (REST de terceros, colas, etc.); el núcleo solo conoce la
  interfaz.
- **Priorizar objetivos** → ya implementado vía `ThreatAssessmentService` y el
  orden del listado; la fórmula es intercambiable (Open/Closed).

Este examen implementa por completo el aggregate **`AffectedIndividual`** (el
exigido por la API), dejando el diseño listo para crecer.

---

## 10. Pruebas

```bash
./mvnw test
```

- `WeightedThreatAssessmentServiceTest` — pruebas **unitarias puras** del
  servicio de dominio (sin Spring, sin mocks: beneficio directo de un dominio
  libre de framework).
- `AffectedControllerIntegrationTest` — prueba **end-to-end** de los 4 endpoints
  sobre el wiring real (controller → caso de uso → dominio → repositorio),
  incluyendo casos de error 404 / 409 / 400.
```
Tests run: 12, Failures: 0, Errors: 0, Skipped: 0 — BUILD SUCCESS
```
