package tests;

import api.BooksApi;
import helpers.WithLogin;
import models.LoginResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static helpers.LoginExtension.getAuthResponse;
import static io.qameta.allure.Allure.step;

public class DemoQaCheckTest extends TestBase {

    private final String TEXT_IN_COLLECTION = "No rows found";

    @Test
    @WithLogin
    @DisplayName("Удаление книги из коллекции пользователя")
    void successfulDeleteBookTest() {
        // Просто получаем authResponse - ВСЁ!
        LoginResponse authResponse = getAuthResponse();

        step("Удаляем все книги из Profile", () ->
                BooksApi.deleteAllBooks(authResponse.getToken(), authResponse.getUserId())
        );

        step("Добавляем книгу в Profile", () ->
                BooksApi.addBooks(authResponse.getToken(), authResponse.getUserId())
        );

        step("Открываем страницу Profile", () ->
                open("/profile")
        );

        step("Кликаем по значку корзины", () ->
                $("#delete-record-undefined").click()
        );

        step("Подтверждаем запрос на удаление", () ->
                $("#closeSmallModal-ok").click()
        );

        step("Проверяем, что книга не отображается в таблице", () ->
                $(".rt-noData").shouldBe(visible)
                        .shouldHave(text(TEXT_IN_COLLECTION))
        );
    }
}