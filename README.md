# Mixuk — Backend

Spring Boot API dla aplikacji miksu energetycznego UK.

## Endpointy

- `GET /api/dayMix` — miks energetyczny dla 3 dni (dzisiaj, jutro, pojutrze)
- `GET /api/window?hours=N` — optymalne okno ładowania (N = 1..6 godzin)

## Uruchomienie lokalne

Wymagane: Java 21, PostgreSQL.

Domyślne wartości połączenia (localhost:5432, baza `mixuk`, user `postgres`) można nadpisać przez zmienne środowiskowe:

```bash
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
./mvnw spring-boot:run
```

Aplikacja słucha na porcie 8080.

## Testy

```bash
./mvnw test
```

## Docker

```bash
docker build -t mixuk .
docker run -p 8080:8080 -e DB_USERNAME=... -e DB_PASSWORD=... mixuk
```

## Deploy

Wdrożone na Renderze: [link po deployu]

Zmienne środowiskowe wymagane na produkcji:
- `DATABASE_URL` — pełny JDBC URL Postgresa
- `DB_USERNAME`, `DB_PASSWORD`
- `CORS_ALLOWED_ORIGINS` — URL frontendu (comma-separated jeśli wiele)
- `PORT` — ustawiane automatycznie przez Render