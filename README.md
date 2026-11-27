# Family Planner

A family planning app: calendar, tasks, and chores with allowance.

- **backend/** – Spring Boot 3.5 on Java 25 (Web, Security + JWT, Data JPA, Validation, Flyway, Springdoc, PostgreSQL)
- **frontend/** – React + Vite + TypeScript, React Router, TanStack Query
- **docker-compose.yml** – local PostgreSQL

## Prerequisites

JDK 25, Maven 3.9+, Node 20+, Docker (for Postgres and Testcontainers).

## Run locally

```bash
docker compose up -d                  # Postgres on :5432
cd backend && mvn spring-boot:run     # API on :8080 (Flyway migrates on startup)
cd frontend && npm install && npm run dev   # UI on :5173, proxies /api to :8080
```

Swagger UI: http://localhost:8080/swagger-ui.html

Config via env vars: `DB_URL`, `DB_USER`, `DB_PASSWORD`, `JWT_SECRET` (set a strong value, ≥32 bytes, outside dev).

## Test / build

```bash
cd backend && mvn verify      # integration tests use Testcontainers; skipped if Docker is unavailable
cd frontend && npm test && npm run build
```

## Milestone 1 (done)

- JWT auth (stateless, `Authorization: Bearer <token>`) with `PARENT` / `CHILD` roles
- `Family` and `Member` (role, color) with Flyway migration `V1`
- Endpoints:
  - `POST /api/auth/register` – create a family and its first parent
  - `POST /api/auth/login`
  - `GET /api/me`, `GET /api/family`
  - `POST /api/family/members` – parent only
- Frontend: register/login, protected dashboard listing members, parent-only add-member form

## Planned domain (later milestones)

`Event` (calendar), `Task`, `Chore`, `ChoreCompletion` (parent approval), `LedgerEntry` (allowance balance from approved chores/payouts). Each gets its own Flyway migration (`V2+`).
