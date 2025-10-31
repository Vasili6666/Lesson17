package tests;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

public class DemoQaCheckTest {
    @Test
    void checkDemoQaWorkflow() {
        String username = "basil8";
        String password = "Basil1982!";
        String isbn = "9781449325862";
        String bookTitle = "Git Pocket Guide";

        System.out.println("🚀 ЗАПУСК ПОЛНОГО WORKFLOW DEMOQA");

        // ШАГ 1: API ЛОГИН
        io.restassured.response.Response loginResponse = given()
                .contentType(JSON)
                .body("{\"userName\": \"" + username + "\", \"password\": \"" + password + "\"}")
                .post("https://demoqa.com/Account/v1/Login")
                .then()
                .statusCode(200)
                .extract()
                .response();

        String token = loginResponse.path("token");
        String userId = loginResponse.path("userId");
        String expires = loginResponse.path("expires");

        System.out.println("✅ Токен получен: " + token);
        System.out.println("✅ UserId получен: " + userId);

        // ШАГ 2: UI АВТОРИЗАЦИЯ
        open("https://demoqa.com/favicon.ico");
        getWebDriver().manage().addCookie(new Cookie("userID", userId));
        getWebDriver().manage().addCookie(new Cookie("expires", expires));
        getWebDriver().manage().addCookie(new Cookie("token", token));
        System.out.println("✅ Куки установлены в браузер!");

        // ШАГ 3: УДАЛЕНИЕ ВСЕХ КНИГ
        given()
                .contentType(JSON)
                .header("Authorization", "Bearer " + token)
                .queryParam("UserId", userId)
                .delete("https://demoqa.com/BookStore/v1/Books")
                .then()
                .statusCode(204);
        System.out.println("✅ Все книги удалены из профиля!");

        // ШАГ 4: ПРОВЕРКА ЧТО КНИГ УДАЛЕНЫ
        io.restassured.response.Response userResponse = given()
                .header("Authorization", "Bearer " + token)
                .get("https://demoqa.com/Account/v1/User/" + userId)
                .then()
                .statusCode(200)
                .extract()
                .response();

        int booksCount = userResponse.path("books.size()");
        if (booksCount == 0) {
            System.out.println("✅ Проверка API: коллекция книг пуста");
        } else {
            System.out.println("❌ ОШИБКА: В коллекции осталось " + booksCount + " книг");
        }

        // ШАГ 5: ДОБАВЛЕНИЕ КНИГИ
        given()
                .contentType(JSON)
                .header("Authorization", "Bearer " + token)
                .body("{\"userId\": \"" + userId + "\", \"collectionOfIsbns\": [{\"isbn\": \"" + isbn + "\"}]}")
                .post("https://demoqa.com/BookStore/v1/Books")
                .then()
                .statusCode(201);
        System.out.println("✅ Книга добавлена: " + isbn);

        // ШАГ 6: ПРОВЕРКА ЧТО КНИГА ДОБАВЛЕНА
        io.restassured.response.Response userResponseAfterAdd = given()
                .header("Authorization", "Bearer " + token)
                .get("https://demoqa.com/Account/v1/User/" + userId)
                .then()
                .statusCode(200)
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
        open("https://demoqa.com/profile");
        $("#userName-value").shouldHave(text("basil8"));
        System.out.println("✅ Имя пользователя корректное: basil8");

        $("body").shouldHave(text(bookTitle));
        System.out.println("✅ Книга '" + bookTitle + "' отображается");

        $("a[href*='book=" + isbn + "']").shouldBe(visible);
        System.out.println("✅ Ссылка на книгу найдена");
        System.out.println("🎉 ВСЕ UI ПРОВЕРКИ УСПЕШНО ЗАВЕРШЕНЫ!");

        // ШАГ 8: УДАЛЕНИЕ КНИГИ (ОЧИСТКА)
        given()
                .contentType(JSON)
                .header("Authorization", "Bearer " + token)
                .body("{\"isbn\": \"" + isbn + "\", \"userId\": \"" + userId + "\"}")
                .delete("https://demoqa.com/BookStore/v1/Book")
                .then()
                .statusCode(204);
        System.out.println("✅ Книга удалена через API (очистка)");

        // ШАГ 9: ПРОВЕРКА ЧТО КНИГА УДАЛЕНА
        io.restassured.response.Response userResponseAfterDelete = given()
                .header("Authorization", "Bearer " + token)
                .get("https://demoqa.com/Account/v1/User/" + userId)
                .then()
                .statusCode(200)
                .extract()
                .response();

        int booksCountAfterDelete = userResponseAfterDelete.path("books.size()");
        if (booksCountAfterDelete == 0) {
            System.out.println("✅ Проверка API: книга успешно удалена из коллекции");
        } else {
            System.out.println("❌ ОШИБКА: Книга не удалена из коллекции");
        }

        // ШАГ 10: UI РАЗЛОГИНИВАНИЕ И ЗАКРЫТИЕ БРАУЗЕРА
        open("https://demoqa.com/profile");

        // Нажимаем кнопку Log out через UI
        $("#submit").click();
        System.out.println("✅ UI разлогинивание выполнено");

        // Проверяем что мы на странице логина (после выхода)
        $("#userForm").shouldBe(visible);
        System.out.println("✅ Успешно перешли на страницу логина");

        // Закрываем браузер
        closeWebDriver();
        System.out.println("✅ Браузер закрыт");

        System.out.println("🎉 ПОЛНЫЙ ЦИКЛ ТЕСТА УСПЕШНО ЗАВЕРШЕН!");
    }
}