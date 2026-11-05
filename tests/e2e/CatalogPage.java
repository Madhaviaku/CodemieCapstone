package com.library.catalog.e2e;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CatalogPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public CatalogPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void open() {
        String url = System.getProperty("catalog.url", "http://localhost:5173/");
        driver.get(url);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Book catalog']")));
        waitUntilSettled();
    }

    public void shows(String text) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(normalize-space(.)," + literal(text) + ")]")));
    }

    public void bookCountIs(int count) {
        shows(count == 1 ? "1 book" : count + " books");
    }

    public void search(String title) {
        WebElement input = driver.findElement(By.cssSelector("input[placeholder='Search by title, author, or ISBN']"));
        input.clear();
        input.sendKeys(title);
        driver.findElement(By.xpath("//button[normalize-space()='Search']")).click();
        waitUntilSettled();
    }

    public void nextPage() {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[normalize-space()='Next']"))).click();
        waitUntilSettled();
    }

    public void submitEmptyBook() {
        openAddForm();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[normalize-space()='Add to catalog']"))).click();
    }

    public void addBook(String title, String author, String genre, String isbn, int copies) {
        openAddForm();
        fill("ISBN", isbn);
        fill("Title", title);
        fill("Author", author);
        fill("Genre", genre);
        fill("Copies available", Integer.toString(copies));
        driver.findElement(By.xpath("//button[normalize-space()='Add to catalog']")).click();
        waitUntilSettled();
    }

    public void changeCopies(String title, int copies) {
        row(title).findElement(By.xpath(".//button[normalize-space()='Edit']")).click();
        fill("Copies available", Integer.toString(copies));
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[normalize-space()='Save changes']"))).click();
        waitUntilSettled();
    }

    public void delete(String title) {
        row(title).findElement(By.xpath(".//button[normalize-space()='Delete']")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[normalize-space()='Remove book']"))).click();
        waitUntilSettled();
    }

    private void openAddForm() {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[normalize-space()='Add book']"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[normalize-space()='Add a book']")));
    }

    private void fill(String label, String value) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//label[span[normalize-space()=" + literal(label) + "]]//input")));
        input.clear();
        input.sendKeys(value);
    }

    private WebElement row(String title) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//tr[td[normalize-space()=" + literal(title) + "]]")));
    }

    private void waitUntilSettled() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[contains(text(),'Loading catalog')]")));
    }

    private static String literal(String value) {
        if (!value.contains("'")) {
            return "'" + value + "'";
        }
        return "concat('" + value.replace("'", "', \"'\", '") + "')";
    }
}
