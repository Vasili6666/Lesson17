package tests;

import models.*;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import java.util.Collections;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static data.UserData.PASSWORD;
import static data.UserData.USER_NAME;
import static io.restassured.RestAssured.given;
import static specs.Specification.*; // ← ИМПОРТИРУЕМ НАШИ СПЕЦИФИКАЦИИ!

public class DemoQaCheckTest extends TestBase {

    @Test
    void checkDemoQaWorkflow() {
        String username = USER_NAME;
        String password = PASSWORD;
        String isbn = "9781449325862";
        String bookTitle = "Git Pocket Guide";

        System.out.println("🚀 ЗАПУСК ПОЛНОГО WORKFLOW DEMOQA");

        // ШАГ 1: API ЛОГИН (СО СПЕЦИФИКАЦИЯМИ!)
        LoginBody loginBody = new LoginBody();
        loginBody.setUserName(username);
        loginBody.setPassword(password);

        LoginResponse loginResponse = given()
                .spec(allRequests) // ← ИСПОЛЬЗУЕМ SPEC ДЛЯ ЗАПРОСА
                .body(loginBody)
                .post("/Account/v1/Login")
                .then()
                .spec(successfulLoginResponse200) // ← ИСПОЛЬЗУЕМ SPEC ДЛЯ ОТВЕТА
                .extract()
                .as(LoginResponse.class);

        String token = loginResponse.getToken();
        String userId = loginResponse.getUserId();
        String expires = loginResponse.getExpires();

        System.out.println("✅ Токен получен: " + token);
        System.out.println("✅ UserId получен: " + userId);

        // ШАГ 2: UI АВТОРИЗАЦИЯ
        open("/favicon.ico");
        getWebDriver().manage().addCookie(new Cookie("userID", userId));
        getWebDriver().manage().addCookie(new Cookie("expires", expires));
        getWebDriver().manage().addCookie(new Cookie("token", token));
        System.out.println("✅ Куки установлены в браузер!");

        // ШАГ 3: УДАЛЕНИЕ ВСЕХ КНИГ (СО СПЕЦИФИКАЦИЕЙ!)
        given()
                .spec(allRequests)
                .header("Authorization", "Bearer " + token)
                .queryParam("UserId", userId)
                .delete("/BookStore/v1/Books")
                .then()
                .spec(successDeleteAllBooksResponse204);
        System.out.println("✅ Все книги удалены из профиля!");

        // ШАГ 4: ПРОВЕРКА ЧТО КНИГ УДАЛЕНЫ (СО СПЕЦИФИКАЦИЕЙ!)
        io.restassured.response.Response userResponse = given()
                .spec(allRequests)
                .header("Authorization", "Bearer " + token)
                .get("/Account/v1/User/" + userId)
                .then()
                .spec(successfulUserResponse200)
                .extract()
                .response();

        int booksCount = userResponse.path("books.size()");
        if (booksCount == 0) {
            System.out.println("✅ Проверка API: коллекция книг пуста");
        } else {
            System.out.println("❌ ОШИБКА: В коллекции осталось " + booksCount + " книг");
        }

        // ШАГ 5: ДОБАВЛЕНИЕ КНИГИ (СО СПЕЦИФИКАЦИЕЙ!)
        AddBookBody addBookRequest = new AddBookBody();
        addBookRequest.setUserId(userId);
        addBookRequest.setCollectionOfIsbns(
                Collections.singletonList(new IsbnBook(isbn))
        );

        given()
                .spec(allRequests)
                .header("Authorization", "Bearer " + token)
                .body(addBookRequest)
                .post("/BookStore/v1/Books")
                .then()
                .spec(successAddBooksResponse201);
        System.out.println("✅ Книга добавлена: " + isbn);

        // ШАГ 6: ПРОВЕРКА ЧТО КНИГА ДОБАВЛЕНА (СО СПЕЦИФИКАЦИЕЙ!)
        io.restassured.response.Response userResponseAfterAdd = given()
                .spec(allRequests)
                .header("Authorization", "Bearer " + token)
                .get("/Account/v1/User/" + userId)
                .then()
                .spec(successfulUserResponse200)
                .extract()
                .response();

        int booksCountAfterAdd = userResponseAfterAdd.path("books.size()");
        String addedBookIsbn = userResponseAfterAdd.path("books[0].isbn");

        if (booksCountAfterAdd == 1 && isbn.equals(addedBookIsbn)) {
            System.out.println("✅ Проверка API: книга успешно добавлена в коллекцию");
        } else {
            System.out.println("❌ ОШИБКА: Книга не добавлена в коллекцию");
        }

        // ШАГ 7: UI ПРОВЕРКИ
        open("/profile");
        $("#userName-value").shouldHave(text(username)); // ← используем переменную
        System.out.println("✅ Имя пользователя корректное: " + username);

        $("body").shouldHave(text(bookTitle));
        System.out.println("✅ Книга '" + bookTitle + "' отображается");

        $("a[href*='book=" + isbn + "']").shouldBe(visible);
        System.out.println("✅ Ссылка на книгу найдена");
        System.out.println("🎉 ВСЕ UI ПРОВЕРКИ УСПЕШНО ЗАВЕРШЕНЫ!");

        // ШАГ 8: УДАЛЕНИЕ КНИГИ (ОЧИСТКА)
        given()
                .spec(allRequests)
                .header("Authorization", "Bearer " + token)
                .body("{\"isbn\": \"" + isbn + "\", \"userId\": \"" + userId + "\"}")
                .delete("/BookStore/v1/Book")
                .then()
                .spec(successDeleteAllBooksResponse204);
        System.out.println("✅ Книга удалена через API (очистка)");

        // ШАГ 9: ПРОВЕРКА ЧТО КНИГА УДАЛЕНА
        io.restassured.response.Response userResponseAfterDelete = given()
                .spec(allRequests)
                .header("Authorization", "Bearer " + token)
                .get("/Account/v1/User/" + userId)
                .then()
                .spec(successfulUserResponse200)
                .extract()
                .response();

        int booksCountAfterDelete = userResponseAfterDelete.path("books.size()");
        if (booksCountAfterDelete == 0) {
            System.out.println("✅ Проверка API: книга успешно удалена из коллекции");
        } else {
            System.out.println("❌ ОШИБКА: Книга не удалена из коллекции");
        }

        // ШАГ 10: UI РАЗЛОГИНИВАНИЕ
        open("/profile");
        $("#submit").click();
        System.out.println("✅ UI разлогинивание выполнено");

        $("#userForm").shouldBe(visible);
        System.out.println("✅ Успешно перешли на страницу логина");

        System.out.println("🎉 ПОЛНЫЙ ЦИКЛ ТЕСТА УСПЕШНО ЗАВЕРШЕН!");
    }
}