package tests;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import java.time.Duration;

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

        // ШАГ 1: API ЛОГИН - ПОЛУЧАЕМ ТОКЕН И USER_ID
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

        // ШАГ 2: UI АВТОРИЗАЦИЯ - УСТАНАВЛИВАЕМ КУКИ В БРАУЗЕР
        open("https://demoqa.com/favicon.ico"); // Открываем любую страницу чтобы запустить браузер

        // Устанавливаем куки для авторизации
        getWebDriver().manage().addCookie(new Cookie("userID", userId));
        getWebDriver().manage().addCookie(new Cookie("expires", expires));
        getWebDriver().manage().addCookie(new Cookie("token", token));

        System.out.println("✅ Куки установлены в браузер!");

        // ШАГ 3: УДАЛЯЕМ ВСЕ КНИГИ ИЗ ПРОФИЛЯ
        given()
                .contentType(JSON)
                .header("Authorization", "Bearer " + token)
                .queryParam("UserId", userId)
                .delete("https://demoqa.com/BookStore/v1/Books")
                .then()
                .statusCode(204);

        System.out.println("✅ Все книги удалены из профиля!");

        // ШАГ 4: ДОБАВЛЕНИЕ КНИГИ ЧЕРЕЗ API
        String isbn = "9781449325862";

        given()
                .contentType(JSON)
                .header("Authorization", "Bearer " + token)
                .body("{\"userId\": \"" + userId + "\", \"collectionOfIsbns\": [{\"isbn\": \"" + isbn + "\"}]}")
                .post("https://demoqa.com/BookStore/v1/Books")
                .then()
                .statusCode(201);

        System.out.println("✅ Книга добавлена: " + isbn);

        // ШАГ 5: UI ПРОВЕРКИ
        open("https://demoqa.com/profile");

        // 5.1 Проверяем имя пользователя (Selenide сам ждет до 4 секунд)
        $("#userName-value").shouldHave(text("basil8"));
        System.out.println("✅ Имя пользователя корректное: basil8");

        // 5.2 Проверяем книгу на странице
        $("body").shouldHave(text("Git Pocket Guide"));
        System.out.println("✅ Книга 'Git Pocket Guide' отображается");

        // 5.3 Проверяем ссылку на книгу
        $("a[href*='book=9781449325862']").shouldBe(visible);
        System.out.println("✅ Ссылка на книгу найдена");

        System.out.println("🎉 ВСЕ UI ПРОВЕРКИ УСПЕШНО ЗАВЕРШЕНЫ!");
    }
}