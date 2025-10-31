package tests;

import api.BooksApi;
import helpers.WithLogin;
import models.LoginResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static helpers.LoginExtension.getAuthResponse;
import static io.qameta.allure.Allure.step;

public class DemoQaCheckTest extends TestBase {

    private final String ISBN = "9781449325862";
    private final String BOOK_TITLE = "Git Pocket Guide";
    private final String USER_NAME = "basil8";

    @Test
    //@Tag("API+UI")
    @WithLogin
    @DisplayName("Полный workflow: авторизация, работа с книгами, UI проверки")
    void fullDemoQaWorkflowTest() {
        // ШАГ 1: API ЛОГИН (автоматически через @WithLogin)
        LoginResponse authResponse = step("ШАГ 1: API Логин и получение токена", () ->
                getAuthResponse()
        );

        // ШАГ 2: Подстановка в куки через UI (автоматически в LoginExtension)
        step("ШАГ 2: Установка авторизационных кук в браузер", () -> {
            // Куки уже установлены в LoginExtension, можно добавить проверку
            open("/profile");
            $("#userName-value").shouldBe(visible);
        });

        // ШАГ 3: УДАЛЕНИЕ ВСЕХ КНИГ
        step("ШАГ 3: Удаление всех книг через API", () ->
                BooksApi.deleteAllBooks(authResponse.getToken(), authResponse.getUserId())
        );

        // ШАГ 4: ПРОВЕРКА ЧТО КНИГ УДАЛЕНЫ
        step("ШАГ 4: Проверка что коллекция книг пуста", () -> {
            var userResponse = BooksApi.getUserInfo(authResponse.getToken(), authResponse.getUserId());
            int booksCount = userResponse.path("books.size()");
            if (booksCount == 0) {
                System.out.println("✅ Коллекция книг пуста");
            } else {
                throw new AssertionError("❌ В коллекции осталось " + booksCount + " книг");
            }
        });

        // ШАГ 5: ДОБАВЛЕНИЕ КНИГИ
        step("ШАГ 5: Добавление книги в коллекцию", () ->
                BooksApi.addBook(authResponse.getToken(), authResponse.getUserId(), ISBN)
        );

        // ШАГ 6: ПРОВЕРКА ЧТО КНИГА ДОБАВЛЕНА
        step("ШАГ 6: Проверка добавления книги через API", () -> {
            var userResponse = BooksApi.getUserInfo(authResponse.getToken(), authResponse.getUserId());
            int booksCount = userResponse.path("books.size()");
            String addedBookIsbn = userResponse.path("books[0].isbn");

            if (booksCount == 1 && ISBN.equals(addedBookIsbn)) {
                System.out.println("✅ Книга успешно добавлена в коллекцию");
            } else {
                throw new AssertionError("❌ Книга не добавлена в коллекцию");
            }
        });

        // ШАГ 7: UI ПРОВЕРКИ
        step("ШАГ 7: UI проверки отображения книги", () -> {
            open("/profile");

            // Проверка имени пользователя
            $("#userName-value").shouldHave(text(USER_NAME));
            System.out.println("✅ Имя пользователя корректное: " + USER_NAME);

            // Проверка отображения книги
            $("body").shouldHave(text(BOOK_TITLE));
            System.out.println("✅ Книга '" + BOOK_TITLE + "' отображается");

            // Проверка ссылки на книгу
            $("a[href*='book=" + ISBN + "']").shouldBe(visible);
            System.out.println("✅ Ссылка на книгу найдена");
        });

        // ШАГ 8: УДАЛЕНИЕ КНИГИ (UI или API)
        step("ШАГ 8: Удаление книги через UI", () -> {
            open("/profile");
            $("#delete-record-undefined").click();
            $("#closeSmallModal-ok").click();
            System.out.println("✅ Книга удалена через UI");
        });

        // ШАГ 9: ПРОВЕРКА ЧТО КНИГА УДАЛЕНА
        step("ШАГ 9: Проверка удаления книги", () -> {
            // Проверка через UI
            $(".rt-noData").shouldBe(visible)
                    .shouldHave(text("No rows found"));
            System.out.println("✅ UI: Книга удалена из таблицы");

            // Дополнительная проверка через API
            var userResponse = BooksApi.getUserInfo(authResponse.getToken(), authResponse.getUserId());
            int booksCount = userResponse.path("books.size()");
            if (booksCount == 0) {
                System.out.println("✅ API: Коллекция книг пуста");
            } else {
                throw new AssertionError("❌ API: В коллекции осталось " + booksCount + " книг");
            }
        });

        System.out.println("🎉 ВСЕ ШАГИ УСПЕШНО ВЫПОЛНЕНЫ!");
    }
}