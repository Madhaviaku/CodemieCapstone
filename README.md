# Library Book Management Catalog

Full-stack catalog for library administrators, built in `mm-learning-group-1` from the Cursor Capstone architecture.

Administrators can create, view, search, update, and delete books. The React UI talks to a Spring Boot REST API. The Maven build fails when `docs/api.md` drifts from the API.

## URLs

Start the backend and the frontend before opening these. `http://localhost` by itself will refuse the connection, because nothing listens on port 80.

| What | URL |
| --- | --- |
| Catalog UI | http://localhost:5173/ |
| API base | http://localhost:8080/api |
| List books | http://localhost:8080/api/books?page=0&size=20 |
| Book by ISBN | http://localhost:8080/api/books/9780134685991 |
| Search by title | http://localhost:8080/api/books/search?title=Java&page=0&size=20 |
| OpenAPI UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |
| H2 console | http://localhost:8080/h2-console |
| API markdown | [docs/api.md](docs/api.md) |

H2 console login, for local development only:

- JDBC URL: `jdbc:h2:mem:librarydb`
- User: `sa`
- Password: empty

The UI is allowed to call the API from `http://localhost:5173` and `http://127.0.0.1:5173`. Set `VITE_API_BASE_URL` if the API is not at `http://localhost:8080`.

## What was built

### Backend

Java Spring Boot API in `backend`.

- `GET /api/books` lists books with pagination. Default page size is 20. Maximum size is 100.
- `GET /api/books/{isbn}` returns one book. Hyphens in the ISBN are accepted. The stored value is digits only.
- `GET /api/books/search` searches title with a case-insensitive partial match.
- `POST /api/books` creates a book and returns `201`.
- `PUT /api/books/{isbn}` updates the fields that are sent. The ISBN cannot be changed.
- `DELETE /api/books/{isbn}` removes a book and returns `204`.

Validation:

- ISBN must be 10 or 13 digits, hyphens optional, and unique.
- Title (1–200), author (1–100), and genre (1–50) are required.
- Publisher is optional, maximum 100 characters.
- Publication date is optional and cannot be in the future.
- Quantity defaults to 0 and cannot be negative.

Errors use `code`, `message`, `timestamp`, and `errors` for field failures.

| Status | When |
| --- | --- |
| 400 | Invalid input |
| 404 | Book not found |
| 409 | Duplicate ISBN |
| 500 | Unexpected server error |

The `dev` profile is active by default and loads 12 sample books into an in-memory H2 database. That data is gone when the API stops, and the sample books load again on the next start. Tests use the `test` profile and do not load the sample books.

Create, update, delete, search, and list calls are written to the application log.

### Frontend

React application in `frontend`.

- Catalog table with title, author, genre, year, copies, and ISBN.
- Title search and pagination (10 books per page in the UI).
- Add, view, edit, and delete dialogs.
- Client-side validation, plus the field errors returned by the API.
- Leaving the publication date blank while editing keeps the current date.

### Documentation sync

`ApiDocumentationGenerator` writes `docs/api.md`. `ApiDocumentationSyncVerifier` runs during `mvn verify`, and a unit test compares the committed file with the generator. If they differ, the build fails.

Regenerate the document after an API contract change, then commit it:

From `backend`:

```text
mvn -q compile exec:java@generate-api-docs
```

## Stack

| Layer | Choice |
| --- | --- |
| Backend | Java 17, Spring Boot 3.5.7, Spring Data JPA, Bean Validation |
| API docs | Springdoc OpenAPI and `docs/api.md` |
| Database | H2 in memory for development and tests |
| Frontend | React 19, TypeScript, Vite, Tailwind CSS, Axios |
| Tests | JUnit 5, Mockito, MockMvc, Vitest, React Testing Library |

The project compiles to Java 17 bytecode and runs on the installed JDK (Java 25 on this machine).

## Project layout

```text
mm-learning-group-1/
  README.md
  pom.xml                      Root Maven build for backend and tests
  docs/                        API reference
  backend/                     Spring Boot catalog API
  frontend/                    React catalog UI
  database/                    Schema and seed SQL
  tests/
    features/                  Cucumber scenarios
    step-definitions/          Step definitions
    e2e/                       Selenium page objects and the JUnit suite
  build/                       Scripts and the generated test report
```

## Run the applications

Backend, from `backend`. Java 17 or newer and Maven are required.

```text
mvn spring-boot:run
```

Frontend, from `frontend`. Node.js and npm are required.

```text
npm install
npm run dev
```

Then open http://localhost:5173/.

Other frontend commands:

```text
npm test
npm run build
npm run preview
```

## Tests

Backend unit and API tests, from `backend`:

```text
mvn test
mvn verify
```

Frontend tests, from `frontend`:

```text
npm test
```

Selenium end-to-end tests use JUnit 5, Cucumber, and Chrome in headless mode. Start the API and the UI first, then from `tests`:

```text
mvn test
```

Or, from the repository root:

```text
powershell -File build/run-e2e.ps1
```

The HTML report is written to `build/reports/selenium-test-report.html`. Use `-Dbrowser=edge` or `-Dheadless=false` to change the browser. The scenarios expect the dev sample books at http://localhost:5173/.

## Scope

There is no login. The catalog is an admin-only MVP, as specified in the capstone requirements. ISBN values are checked for length and digits only. There is no external ISBN lookup.
