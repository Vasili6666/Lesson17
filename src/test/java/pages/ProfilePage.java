package pages;

import api.BookApiSteps;
import data.UserData;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;

public class ProfilePage {

    private final String USER_NAME = UserData.USER_NAME;
    private final BookApiSteps bookSteps = new BookApiSteps();

    public void openProfilePage() {
        step("UI: Открыть страницу профиля", () -> {
            open("/profile");
            $("#userName-value").shouldBe(visible);
            });
    }

    public void verifyUserName() {
        step("UI: Проверить имя пользователя", () -> {
            $("#userName-value").shouldHave(text(USER_NAME));
           });
    }

    public void verifyBookDisplayed(String bookTitle) {
        step("UI: Проверить отображение книги", () -> {
            $("body").shouldHave(text(bookTitle));
            });
    }

    public void deleteBook() {
        step("UI: Удалить книгу через интерфейс", () -> {
            $("#delete-record-undefined").click();
            $("#closeSmallModal-ok").click();
            });
    }

    public void verifyProfileIsEmpty() {
        step("UI: Проверить что профиль пуст", () -> {
            $(".rt-noData").shouldBe(visible)
                    .shouldHave(text("No rows found"));
            });
    }

    public void verifyBooksCollectionEmptyAPI() {
        step("API: Проверить что коллекция книг пуста после UI удаления", () -> {
            bookSteps.verifyBookDeleted();
        });
    }
}