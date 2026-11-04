# Test execution report

**Status:** Passed  
**Suite:** Library catalog administration  
**Runner:** JUnit 5, Cucumber, Selenium  
**Date:** 4 November 2026, 11:52 IST  
**Elapsed:** 33.10 seconds  
**Result:** 5 run, 5 passed, 0 failed, 0 skipped

The scenarios ran in headless Chrome against http://localhost:5173/, with the API on http://localhost:8080.

| Scenario | Status |
| --- | --- |
| Seeded books are listed | Passed |
| Search finds books by a partial title | Passed |
| The next page shows the remaining books | Passed |
| An empty book form explains the missing fields | Passed |
| An administrator can add, update, and remove a book | Passed |

## What was checked

- The seeded catalog shows Effective Java and reports 12 books.
- A search for Java returns Head First Java and JavaScript: The Good Parts.
- The next page shows The Great Gatsby.
- Submitting an empty book shows "Title is required" and "ISBN is required".
- A new book can be created, changed from 4 copies to 9, and removed.

## Reports

- HTML: [build/reports/selenium-test-report.html](build/reports/selenium-test-report.html)
- JSON: [build/reports/selenium-test-report.json](build/reports/selenium-test-report.json)
- Surefire: `tests/target/surefire-reports/com.library.catalog.e2e.CatalogE2ETest.txt`

## Note

An earlier attempt failed before the browser started because the cached ChromeDriver supported Chrome 154 and the installed browser was Chrome 156. The run above used a matching driver and completed with no failures.
