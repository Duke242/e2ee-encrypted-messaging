# End-to-End Encrypted Messenger

![CI](https://github.com/Duke242/e2ee-encrypted-messaging/actions/workflows/ci.yml/badge.svg)

A messaging app where messages are encrypted in the browser before they leave it. The server stores and relays only ciphertext plus each user's public key.

| Layer | Stack |
| --- | --- |
| Backend | Java 17, Spring Boot 3, Spring Security (stateless JWT), Spring Data JPA, PostgreSQL |
| Frontend | React, TypeScript, Vite, Tailwind CSS, Web Crypto |
| Tests | JUnit 5, Mockito, AssertJ, MockMvc, embedded-server tests, H2 |
| Delivery | Docker (multi-stage, non-root), docker-compose, GitHub Actions |

## Run it locally

You need Docker. The API refuses to start without a strong JWT key, so generate one:

```bash
export JWT_SECRET="$(openssl rand -base64 64 | tr -d '\n')"
docker compose up --build
```

The API listens on `http://localhost:8080`, and `GET /actuator/health` reports `UP` once it's ready. Then start the frontend:

```bash
cd frontend && npm ci && npm run dev
```

### Configuration

Every secret and environment-specific value comes from an environment variable. Nothing sensitive is committed.

| Variable | Purpose | Default |
| --- | --- | --- |
| `JWT_SECRET` | Base64 HS512 key, at least 64 bytes once decoded | none (required) |
| `DATABASE_URL` | JDBC URL | `jdbc:postgresql://localhost:5432/secure_chat` |
| `DATABASE_USERNAME` / `DATABASE_PASSWORD` | Database credentials | `postgres` / empty |
| `CORS_ALLOWED_ORIGINS` | Comma-separated frontend origins | `http://localhost:5173` |
| `PORT` | HTTP port | `8080` |

## Security model

- **Identity comes from the token, never the request.** The signed-in user is read from the verified JWT. A `senderId` in a request body is ignored, so nobody can send as someone else.
- **Users can only touch their own data.** Reading conversations, and editing, deleting or rotating the public key of a profile, all return `403` for anyone but the owner. Public-key ownership matters most: swapping another user's key would let an attacker read that user's new messages.
- **Correct status codes.** Missing, forged or expired tokens and wrong passwords return `401`. Ownership violations return `403`.
- **No secrets in responses.** Password hashes are never serialized.

## Tests

```bash
cd backend && ./mvnw verify
```

29 tests, no database or Docker needed (H2 runs in memory):

- **Unit tests** (Mockito): token issue and validation, including forged, expired and malformed tokens; startup refusal on a missing or weak key; password hashing; duplicate-email rules; conversation grouping and ordering.
- **API integration tests** (MockMvc): sign-up and sign-in, 401 handling, and every ownership rule above, run through the real Spring Security filter chain.
- **Embedded-server tests**: run the app on a real port, catching what MockMvc can't, such as a `403` being rewritten to `401` by the servlet error dispatch, and the exact JSON on the wire.

The authorization rules were built test-first. The failing tests are committed ahead of each fix, so the history shows each bug before and after.

## CI

Every push and pull request runs three jobs in GitHub Actions:

1. **Backend tests:** `mvn verify` on JDK 17.
2. **Frontend build:** TypeScript type-check and production build.
3. **Docker smoke test:** builds the image, boots PostgreSQL and the API with docker-compose, waits for the container health check, then checks sign-up and that an unauthenticated request gets `401`.
