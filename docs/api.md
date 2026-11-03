# Library Book Management API

## Base URL

`http://localhost:8080/api`

## Overview

Administrators manage the library catalog through these REST endpoints. Successful JSON responses use `code`, `message`, `data`, and `timestamp`. Error responses use `code`, `message`, `timestamp`, and `errors` when field validation fails.

ISBN values may include hyphens. The service stores the digits only.

## Endpoints

### GET /api/books

- Description: Retrieve all books with pagination support
- Query parameters:
  - `page` (optional, default `0`)
  - `size` (optional, default `20`, maximum `100`)
- Response: `200 OK`

```json
{
  "code": 200,
  "message": "Books retrieved successfully",
  "data": {
    "content": [],
    "totalElements": 0,
    "totalPages": 0,
    "currentPage": 0,
    "pageSize": 20
  },
  "timestamp": "2026-11-03T00:00:00Z"
}
```

- Errors: `400` when page or size is invalid, `500` for unexpected failures

```text
curl "http://localhost:8080/api/books?page=0&size=20"
```

### GET /api/books/{isbn}

- Description: Retrieve a single book by ISBN
- Path parameter: `isbn` (10 or 13 digits, hyphens optional)
- Response: `200 OK` with the book in `data`
- Errors: `400` for an invalid ISBN, `404` when the book does not exist

```text
curl "http://localhost:8080/api/books/9780134685991"
```

### POST /api/books

- Description: Create a new book
- Request body: `isbn`, `title`, `author`, `publisher`, `publicationDate`, `genre`, `quantityAvailable`
- Response: `201 Created` with the created book
- Errors: `400` for validation issues, `409` when the ISBN already exists

```json
{
  "isbn": "978-0-13-468599-1",
  "title": "Effective Java",
  "author": "Joshua Bloch",
  "publisher": "Addison-Wesley",
  "publicationDate": "2018-01-06",
  "genre": "Programming",
  "quantityAvailable": 5
}
```

```text
curl -X POST http://localhost:8080/api/books -H "Content-Type: application/json" -d "@book.json"
```

### PUT /api/books/{isbn}

- Description: Update an existing book. ISBN cannot be changed. Only fields present in the body are updated.
- Path parameter: `isbn`
- Request body: any of `title`, `author`, `publisher`, `publicationDate`, `genre`, `quantityAvailable`
- Response: `200 OK` with the updated book
- Errors: `400` for an invalid payload, `404` when the book does not exist

```text
curl -X PUT http://localhost:8080/api/books/9780134685991 -H "Content-Type: application/json" -d "@update.json"
```

### DELETE /api/books/{isbn}

- Description: Delete a book by ISBN
- Path parameter: `isbn`
- Response: `204 No Content` with an empty body
- Errors: `404` when the book does not exist

```text
curl -X DELETE http://localhost:8080/api/books/9780134685991
```

### GET /api/books/search

- Description: Search books by partial or full title match, case-insensitive
- Query parameters:
  - `title` (required)
  - `page` (optional, default `0`)
  - `size` (optional, default `20`)
- Response: `200 OK` with the same page shape as GET /api/books
- Errors: `400` when `title` is missing

```text
curl "http://localhost:8080/api/books/search?title=Java&page=0&size=20"
```

## Book fields

| Field | Required on create | Rules |
| --- | --- | --- |
| isbn | Yes | 10 or 13 digits, hyphens optional, unique |
| title | Yes | 1-200 characters |
| author | Yes | 1-100 characters |
| publisher | No | Maximum 100 characters |
| publicationDate | No | ISO date, not in the future |
| genre | Yes | 1-50 characters |
| quantityAvailable | No | Integer >= 0, default 0 |

## Error payload

Validation failures return HTTP 400:

```json
{
  "code": 400,
  "message": "Validation failed",
  "errors": {
    "isbn": "Invalid ISBN format",
    "title": "Title is required"
  },
  "timestamp": "2026-11-03T00:00:00Z"
}
```

Other errors:

- `404`: `Book with ISBN {isbn} not found`
- `409`: `ISBN {isbn} already exists in catalog`
- `500`: `Internal server error`
