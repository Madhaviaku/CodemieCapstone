package com.library.catalog.e2e;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CatalogSteps {

    private CatalogPage catalog() {
        return new CatalogPage(DriverFactory.driver());
    }

    @Given("the catalog application is open")
    public void theCatalogApplicationIsOpen() {
        catalog().open();
    }

    @When("the administrator searches for {string}")
    public void theAdministratorSearchesFor(String title) {
        catalog().search(title);
    }

    @When("the administrator opens the next page")
    public void theAdministratorOpensTheNextPage() {
        catalog().nextPage();
    }

    @When("the administrator submits an empty book")
    public void theAdministratorSubmitsAnEmptyBook() {
        catalog().submitEmptyBook();
    }

    @When("the administrator adds {string} by {string} in {string} with ISBN {string} and {int} copies")
    public void theAdministratorAddsABook(String title, String author, String genre, String isbn, int copies) {
        catalog().addBook(title, author, genre, isbn, copies);
    }

    @When("the administrator changes {string} to {int} copies")
    public void theAdministratorChangesCopies(String title, int copies) {
        catalog().changeCopies(title, copies);
    }

    @When("the administrator deletes {string}")
    public void theAdministratorDeletes(String title) {
        catalog().delete(title);
    }

    @Then("the catalog shows {string}")
    public void theCatalogShows(String text) {
        catalog().shows(text);
    }

    @Then("the form shows {string}")
    public void theFormShows(String text) {
        catalog().shows(text);
    }

    @Then("the catalog reports {int} books")
    public void theCatalogReportsBooks(int count) {
        catalog().bookCountIs(count);
    }
}
