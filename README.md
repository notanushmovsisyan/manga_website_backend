# Manga Website Backend

The backend for a manga reading website, built as a set of Spring Boot microservices. It handles:

- **User accounts:** sign up, log in, JWT access and refresh tokens
- **The manga catalog:** titles, authors, genres, imported from MyAnimeList
- **Chapters and pages:** page images go straight to AWS S3
- **Reading tracking:** bookmarks and "where did I stop reading"
- **Notifications:** bookmarked users hear about new chapters

---

## Architecture at a glance

```
                        ┌──────────────┐
   client ── login ───▶ │   auth-ms    │──▶ PostgreSQL (users, refresh tokens)
                        └──────┬───────┘
                               │ issues JWT (signed with a shared secret)
                               ▼
     every other service checks the JWT itself, no call back to auth-ms
                               │
   ┌───────────────┬───────────┴────────┬───────────────────┐
   ▼               ▼                    ▼                   ▼
┌──────────┐  ┌────────────┐     ┌─────────────┐     ┌────────────┐
│catalog-ms│  │ content-ms │     │ tracking-ms │     │  notif-ms  │
└────┬─────┘  └─────┬──────┘     └──────┬──────┘     └─────┬──────┘
     │              │                   │                  │
 PostgreSQL     PostgreSQL + S3      DynamoDB          DynamoDB + Redis
 Jikan API          │                   │                  ▲
 (MyAnimeList)      │ chapter published │ bookmark added/  │
                    │                   │ removed          │
                    └──────────▶ Kafka ◀┘──────────────────┘
```

Every service listens on port **8080** in its container and exposes Prometheus metrics at `/actuator/prometheus`. The root `prometheus.yaml` scrapes all five.

---

## The services

| Service | What it does | Storage | Talks to |
|---|---|---|---|
| **auth-ms** | Registers users, logs them in, issues and refreshes JWTs | PostgreSQL (`users`, `refresh_tokens`) | none |
| **catalog-ms** | Stores manga titles, authors, and genres. Imports titles from MyAnimeList through the [Jikan API](https://jikan.moe/) | PostgreSQL (`mangas`, `authors`, `genres` + join tables) | Jikan API |
| **content-ms** | Manages chapters and pages. Hands out pre-signed S3 URLs so uploaders send images straight to S3 | PostgreSQL (`chapters`, `pages`, `outbox_events`) + S3 | Kafka (producer) |
| **tracking-ms** | Stores each user's bookmarks and reading progress | DynamoDB (`manga_tracking`) | Kafka (producer) |
| **notif-ms** | Keeps track of who follows each manga and writes notifications when a new chapter is published | DynamoDB (`notifications-table`) + Redis | Kafka (consumer) |

All services share the same package layout (`org.manga_backend.<service>`):

```
api/          REST controllers, request/response DTOs, exception handlers
domain/       business logic (services) and domain models
persistence/  database entities, repositories, entity <-> model mappers
security/     JWT filter and helpers
config/       Spring beans (security, Kafka, S3, DynamoDB, ...)
```

PostgreSQL schemas are managed with **Liquibase**. The changelogs live in `src/main/resources/db/changelog/`. The DynamoDB tables are created automatically when the service starts.

---

## How the main flows work

### 1. Authentication
1. `POST /api/v1/auth/register` or `/login` returns an `accessToken` and a `refreshToken`.
2. The client sends `Authorization: Bearer <accessToken>` on every request to every service.
3. Each service checks the token's signature itself with the shared `jwt.secret`. The token carries the username and role.
4. When the access token expires, `POST /api/v1/auth/refresh` with the refresh token returns a new one.

**Roles:** `READER` (the default) and `UPLOADER`. Only uploaders can create or change manga and chapters.

### 2. Uploading a chapter (content-ms)
Images never pass through the backend. They go straight to S3:

1. **Uploader** → `POST /api/v1/mangas/{mangaId}/chapters` with the chapter number and file names.
   The service creates the chapter and returns one **pre-signed S3 upload URL** per file. Each URL is valid for 15 minutes.
2. **Uploader** → `PUT` each image to its URL (straight to S3).
3. **Uploader** → `POST /api/v1/chapters/{chapterId}/confirm` with the page list.
   The service checks that the pages are numbered 1, 2, 3, … with no gaps or duplicates, then saves them.

### 3. New chapter notifications (Kafka + transactional outbox)
1. Confirming an upload saves the pages **and** a `CHAPTER_PUBLISHED` row in `outbox_events`, both in the same DB transaction. Either both are saved or neither is, so no event is lost.
2. Every 5 seconds, `OutboxPublisherService` sends unpublished outbox rows to Kafka and marks them as published.
3. **notif-ms** reads the event, looks up the manga's subscribers in Redis (`manga:{id}:subscribers`), and writes one notification per user to DynamoDB. Each notification has an idempotency key, so a re-delivered event doesn't create duplicates.

### 4. Bookmarks → subscriptions
1. A user bookmarks a manga in **tracking-ms**.
2. tracking-ms publishes a `BookmarkAdded` or `BookmarkRemoved` event to the `bookmark-updates` topic. The event type travels in an `eventType` header.
3. **notif-ms** adds the user to, or removes them from, that manga's subscriber set in Redis.

If notif-ms fails to process a message, it retries 3 times, 1 second apart. After that it sends the message to the dead-letter topic `manga-updates-dlq`.

---

## API reference

All paths are relative to the service. 🔒 = needs a valid JWT. 🛠 = needs the `UPLOADER` role.

### auth-ms
| Method | Path | Description |
|---|---|---|
| POST | `/api/v1/auth/register` | Create an account. Body: `{ "username", "password", "role" }` (username 3–20 chars, password ≥ 6 chars) |
| POST | `/api/v1/auth/login` | Body: `{ "username", "password" }` → tokens |
| POST | `/api/v1/auth/refresh` | Body: `{ "refreshToken" }` → new access token |
| GET  | `/api/v1/auth/validate` 🔒 | Returns `{ username, role, status }` for the current token |

### catalog-ms
| Method | Path | Description |
|---|---|---|
| GET  | `/api/v1/mangas` 🔒 | List all manga |
| GET  | `/api/v1/mangas/{id}` 🔒 | Get one manga |
| POST | `/api/v1/mangas` 🔒🛠 | Import from MyAnimeList. Body: `{ "source": "MAL", "externalId": 13 }`. Creating a manga by hand isn't supported yet |
| PUT  | `/api/v1/mangas/{id}` 🔒🛠 | Update a manga |

### content-ms
| Method | Path | Description |
|---|---|---|
| GET    | `/api/v1/mangas/{mangaId}/chapters` 🔒 | List chapters (paged, 10 per page by default) |
| POST   | `/api/v1/mangas/{mangaId}/chapters` 🔒🛠 | Create a chapter and get S3 upload URLs. Body: `{ "chapterNumber": 1, "originalFileNames": ["01.jpg", ...] }` |
| POST   | `/api/v1/chapters/{chapterId}/confirm` 🔒🛠 | Confirm the upload. Body: `[{ "pageNumber": 1, "s3Url": "..." }, ...]` |
| GET    | `/api/v1/chapters/{chapterId}` 🔒 | Get chapter details |
| GET    | `/api/v1/chapters/{chapterId}/pages` 🔒 | Get pages (paged, 3 per page by default) |
| DELETE | `/api/v1/chapters/{chapterId}` 🔒🛠 | Delete the chapter, its pages, and its S3 folder |

### tracking-ms
| Method | Path | Description |
|---|---|---|
| GET    | `/api/v1/users/{userId}/bookmarks` 🔒 | List bookmarks (paged) |
| POST   | `/api/v1/users/{userId}/bookmarks` 🔒 | Add a bookmark. Body: `{ "mangaId" }` |
| DELETE | `/api/v1/users/{userId}/bookmarks` 🔒 | Remove a bookmark. Body: `{ "mangaId" }` |
| GET    | `/api/v1/users/{userId}/history` 🔒 | Reading history (paged, most recent first) |
| PATCH  | `/api/v1/users/{userId}/history` 🔒 | Update progress. Body: `{ "mangaId", "lastReadChapterId", "lastReadChapterNumber" }` |

### notif-ms
| Method | Path | Description |
|---|---|---|
| GET | `/api/v1/users/{userId}/notifications?limit=10&lastNotificationId=...` 🔒 | Get notifications. Pass `lastNotificationId` from the previous response to get the next page |

Each service also serves Swagger UI at `/swagger-ui.html` when it's running.

---

## Running it locally

### Prerequisites
- **Java 21** to run the services (the Docker images use `eclipse-temurin:21-jre`). The code targets Java 17+.
- **Maven** (or the `./mvnw` wrapper in `auth-ms`)
- **Docker**, for the infrastructure:
  - PostgreSQL, one database each for auth, catalog, and content
  - Kafka
  - Redis
  - DynamoDB Local (default `http://localhost:8000`)
  - Prometheus (optional)
- An **S3 bucket** and AWS credentials for content-ms (read from the standard `AWS_ACCESS_KEY_ID` / `AWS_SECRET_ACCESS_KEY` environment variables)

### Build and run a service
```bash
cd auth-ms
./mvnw clean package        # builds target/auth-ms-0.0.1-SNAPSHOT.jar
docker build -t auth-ms .   # packages the jar into a container
```

Each service has a `Dockerfile` that copies `target/<service>-0.0.1-SNAPSHOT.jar` into a Java 21 image. Run `package` before `docker build`.

### Configuration
Set these properties in each service's `application.yaml`, or as environment variables:

| Property | Used by | Notes |
|---|---|---|
| `spring.datasource.url` / `username` / `password` | auth, catalog, content | PostgreSQL connection |
| `jwt.secret` | **all** | Must be the **same** in every service, at least 32 bytes (HS256) |
| `jwt.expiration` | auth (+ others) | Access token lifetime, in ms |
| `jwt.refreshExpirationMs` | auth | Refresh token lifetime, in ms |
| `spring.kafka.bootstrap-servers` | content, tracking, notif | Defaults to `kafka:9092` |
| `spring.kafka.consumer.group-id`, `auto-offset-reset` | notif | Kafka consumer settings |
| `spring.data.redis.host` / `port` | notif | Redis connection |
| `AWS_DYNAMODB_ENDPOINT` | tracking, notif | Defaults to `http://localhost:8000` |
| `dynamodb.table.notifications` | notif | Defaults to `notifications-table` |
| `aws.s3.bucket-name`, `aws.region` | content | Region defaults to `us-east-1` |

> ⚠️ **Never commit real secrets** (DB passwords, `jwt.secret`, AWS keys). Pass them in as environment variables, for example `SPRING_DATASOURCE_PASSWORD` and `JWT_SECRET`, loaded from a secrets manager. The password in `auth-ms/src/main/resources/application.yaml` is only a placeholder.

### Smoke test
With auth-ms running on `localhost:8080`, this script registers a user, logs in, and validates the token. It needs `jq`.
```bash
./auth-ms/test-auth.sh
```

---

## Known gaps / TODO

- **Only `auth-ms` is fully checked in.** The other four services are missing their `pom.xml`, Maven wrapper, and `application.yaml`, so they can't be built from this repo as it stands.
- **No `docker-compose.yml` yet** for starting the services and the infrastructure together.
- **Kafka topic mismatch:** content-ms publishes chapter events to `chapter-published`, but notif-ms listens on `manga-updates`. Until the names match, new-chapter notifications won't arrive.
- `notif-ms/config/WebSecurityConfig` is an empty placeholder.
- Creating a manga by hand in catalog-ms isn't implemented yet. Only MAL import works.
- `test.sh` at the repo root is empty.