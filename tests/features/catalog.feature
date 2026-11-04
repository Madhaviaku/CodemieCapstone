Feature: Library catalog administration

  Background:
    Given the catalog application is open

  Scenario: Seeded books are listed
    Then the catalog shows "Effective Java"
    And the catalog reports 12 books

  Scenario: Search finds books by a partial title
    When the administrator searches for "Java"
    Then the catalog shows "Head First Java"
    And the catalog shows "JavaScript: The Good Parts"

  Scenario: The next page shows the remaining books
    When the administrator opens the next page
    Then the catalog shows "The Great Gatsby"

  Scenario: An empty book form explains the missing fields
    When the administrator submits an empty book
    Then the form shows "Title is required"
    And the form shows "ISBN is required"

  Scenario: An administrator can add, update, and remove a book
    When the administrator adds "Selenium Catalog Primer" by "Test Author" in "Testing" with ISBN "9780306406152" and 4 copies
    Then the catalog shows "Book created successfully"
    When the administrator searches for "Selenium Catalog Primer"
    And the administrator changes "Selenium Catalog Primer" to 9 copies
    Then the catalog shows "Book updated successfully"
    When the administrator deletes "Selenium Catalog Primer"
    Then the catalog shows "Removed"
