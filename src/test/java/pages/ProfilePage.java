package pages;

import api.BookApiSteps;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;

public class ProfilePage {

    private final String USER_NAME = "basil8";
    private final BookApiSteps bookSteps = new BookApiSteps();

    public void openProfilePage() {
        step("UI: Открыть страницу профиля", () -> {
            open("/profile");
            $("#userName-value").shouldBe(visible);
            System.out.println("✅ Страница профиля открыта");
        });
    }

    public void verifyUserName() {
        step("UI: Проверить имя пользователя", () -> {
            $("#userName-value").shouldHave(text(USER_NAME));
            System.out.println("✅ Имя пользователя корректное: " + USER_NAME);
        });
    }

    public void verifyBookDisplayed(String bookTitle) {
        step("UI: Проверить отображение книги", () -> {
            $("body").shouldHave(text(bookTitle));
            System.out.println("✅ Книга '" + bookTitle + "' отображается");
        });
    }

    public void deleteBook() {
        step("UI: Удалить книгу через интерфейс", () -> {
            $("#delete-record-undefined").click();
            $("#closeSmallModal-ok").click();
            System.out.println("✅ Книга удалена через UI");
        });
    }

    public void verifyProfileIsEmpty() {
        step("UI: Проверить что профиль пуст", () -> {
            $(".rt-noData").shouldBe(visible)
                    .shouldHave(text("No rows found"));
            System.out.println("✅ UI: Профиль пуст - книга удалена");
        });
    }

    public void verifyBooksCollectionEmptyAPI() {
        step("API: Проверить что коллекция книг пуста после UI удаления", () -> {
            bookSteps.verifyBookDeleted();
        });
    }
}