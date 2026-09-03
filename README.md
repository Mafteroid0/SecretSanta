# Secret Santa

A web application for organizing anonymous gift exchanges. Users can create events, invite participants, maintain wishlists and run a draw. Each participant receives exactly one person and cannot receive themselves. 

## Tech Stack

* Java 17
* Spring Boot 4.1.0
* Spring Web MVC
* Spring Security
* Spring Data JPA
* Hibernate
* PostgreSQL 17
* Flyway
* Thymeleaf
* Gradle
* Docker Compose
* JUnit 5 and Mockito
* HTML, CSS, JavaScript and Bootstrap

## Running Locally

Requirements:

* JDK 17 or newer
* Docker with Docker Compose


Clone the repository:

```bash
git clone https://github.com/Mafteroid0/SecretSanta.git
cd SecretSanta
```

Start PostgreSQL:

```bash
docker compose up -d
```

Start the application on macOS or Linux:

```bash
./gradlew bootRun
```

Start the application on Windows:

```powershell
.\gradlew.bat bootRun
```

Open:

```text
http://localhost:8080
```

Local PostgreSQL configuration:

| Parameter | Value               |
| --------- | ------------------- |
| Host      | `localhost`         |
| Port      | `5433`              |
| Database  | `secret_santa`      |
| Username  | `secret_santa_user` |
| Password  | `secret`            |

Stop PostgreSQL:

```bash
docker compose down
```

Delete PostgreSQL together with all local data:

```bash
docker compose down -v
```

## Configuration

Main settings are located in `src/main/resources/application.yml`.

| Property                       | Default                                         |
| ------------------------------ | ----------------------------------------------- |
| `app.storage.path`             | `./uploads`                                     |
| `spring.datasource.url`        | `jdbc:postgresql://localhost:5433/secret_santa` |
| `spring.datasource.username`   | `secret_santa_user`                             |
| `spring.datasource.password`   | `secret`                                        |
| Maximum image size             | `5 MB`                                          |
| Maximum multipart request size | `6 MB`                                          |

The image storage path can be changed with:

```bash
APP_STORAGE_PATH=/path/to/uploads
```
## Web Routes

| Method | Path                  | Description                |
| ------ | --------------------- | -------------------------- |
| `GET`  | `/`                   | Landing page               |
| `GET`  | `/login`              | Login page                 |
| `POST` | `/login`              | Login processing           |
| `GET`  | `/register`           | Registration page          |
| `POST` | `/register`           | Account creation           |
| `GET`  | `/logout`             | Logout                     |
| `GET`  | `/home`               | Redirect to the event list |
| `GET`  | `/games`              | Current user's events      |
| `GET`  | `/create`             | Event creation page        |
| `GET`  | `/room`               | Redirect to the event list |
| `GET`  | `/room/{eventId}`     | Event room                 |
| `GET`  | `/join/{eventId}`     | Invitation page            |
| `GET`  | `/profile`            | Current user's profile     |
| `GET`  | `/profile/{username}` | Another user's profile     |

## REST API

All API endpoints require an authenticated session.

### Events

| Method   | Path                             | Description                   | Response |
| -------- | -------------------------------- | ----------------------------- | -------- |
| `GET`    | `/api/v1/events`                 | Get the current user's events | `200`    |
| `GET`    | `/api/v1/events/{eventId}`       | Get event information         | `200`    |
| `POST`   | `/api/v1/events`                 | Create an event               | `201`    |
| `POST`   | `/api/v1/events/{eventId}/join`  | Join an event                 | `204`    |
| `POST`   | `/api/v1/events/{eventId}/start` | Start the draw                | `200`    |
| `DELETE` | `/api/v1/events/{eventId}`       | Delete an event               | `204`    |

Create event request:

```json
{
  "name": "New Year 2027",
  "deadline": "2026-12-20T18:00:00"
}
```

The deadline is interpreted in the `Europe/Moscow` time zone. The event creator is automatically added as its first participant.

### Participants

| Method   | Path                                                  | Description                      | Response |
| -------- | ----------------------------------------------------- | -------------------------------- | -------- |
| `GET`    | `/api/v1/events/{eventId}/participants`               | Get event participants           | `200`    |
| `GET`    | `/api/v1/events/{eventId}/participants/me/assignment` | Get the current user's recipient | `200`    |
| `DELETE` | `/api/v1/events/{eventId}/participants/me`            | Leave an event                   | `204`    |

Participants can leave only before the draw. The event owner cannot leave their own event.

### Users and Avatars

| Method   | Path                            | Description               | Response |
| -------- | ------------------------------- | ------------------------- | -------- |
| `GET`    | `/api/v1/users/me`              | Get the current user      | `200`    |
| `PATCH`  | `/api/v1/users/me`              | Change the display name   | `200`    |
| `POST`   | `/api/v1/users/me/avatar`       | Upload an avatar          | `200`    |
| `GET`    | `/api/v1/users/{userId}/avatar` | Get a user avatar         | `200`    |
| `DELETE` | `/api/v1/users/me/avatar`       | Delete the current avatar | `204`    |

Update profile request:

```json
{
  "displayName": "Dan"
}
```

Avatar uploads use `multipart/form-data` with a field named `file`.

### Wishlists

| Method   | Path                                       | Description                     | Response |
| -------- | ------------------------------------------ | ------------------------------- | -------- |
| `GET`    | `/api/v1/users/me/wishlist`                | Get the current user's wishlist | `200`    |
| `GET`    | `/api/v1/users/{username}/wishlist`        | Get another user's wishlist     | `200`    |
| `POST`   | `/api/v1/users/me/wishlist`                | Add an item                     | `201`    |
| `DELETE` | `/api/v1/users/me/wishlist/{itemId}`       | Delete an item                  | `204`    |
| `POST`   | `/api/v1/users/me/wishlist/import`         | Import a wishlist               | `201`    |
| `POST`   | `/api/v1/users/me/wishlist/{itemId}/image` | Upload an item image            | `204`    |
| `GET`    | `/api/v1/wishlist/{itemId}/image`          | Get an item image               | `200`    |
| `DELETE` | `/api/v1/users/me/wishlist/{itemId}/image` | Delete an item image            | `204`    |

Create item request:

```json
{
  "name": "Mechanical keyboard",
  "description": "Compact layout with tactile switches"
}
```

Import request:

```json
{
  "url": "https://example.com/public-wishlist"
}
```

## Draw Logic

The participant list is shuffled, and every participant is assigned to the next person in the list. The last participant is assigned to the first one.

This guarantees that:

* nobody is assigned to themselves;
* every participant has one recipient;
* every recipient is assigned exactly once.

At least two participants are required.

`EventDeadlineScheduler` checks expired events every 10 seconds and starts eligible events automatically.

## Error Responses

The REST API uses `application/problem+json`.

| Status            | Meaning                                  |
| ----------------- | ---------------------------------------- |
| `400 Bad Request` | Invalid request or operation             |
| `403 Forbidden`   | Insufficient permissions                 |
| `404 Not Found`   | Resource not found                       |
| `409 Conflict`    | Duplicate participation or invalid state |

## Tests

Run tests on macOS or Linux:

```bash
./gradlew clean test
```

Run tests on Windows:

```powershell
.\gradlew.bat clean test
```

## In Development

* Wishlist import adapters
## Planned

* Gift reservations
* Deadline notifications
* Email invitations
