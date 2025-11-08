package api;

import helpers.LoginExtension;
import models.LoginResponse;
import org.junit.jupiter.api.Assertions;

import static io.qameta.allure.Allure.step;

public class BookApiSteps {

    private final BooksApi booksApi = new BooksApi();
    private final LoginResponse authResponse = LoginExtension.getAuthResponse();

    public void deleteAllBooks() {
        step("API: Удалить все книги из профиля", () -> {
            booksApi.deleteAllBooks(authResponse.getToken(), authResponse.getUserId());
        });
    }

    public void addBook(String isbn) {
        step("API: Добавить книгу в профиль", () -> {
            booksApi.addBook(authResponse.getToken(), authResponse.getUserId(), isbn);
        });
    }

    public void verifyBookAdded(String expectedIsbn) {
        step("API: Проверить что книга добавлена", () -> {
            var userResponse = booksApi.getUserInfo(authResponse.getToken(), authResponse.getUserId());
            int booksCount = userResponse.path("books.size()");
            String actualIsbn = userResponse.path("books[0].isbn");

            Assertions.assertEquals(1, booksCount, "Должна быть одна книга в коллекции");
            Assertions.assertEquals(expectedIsbn, actualIsbn, "ISBN книги должен совпадать");
        });
    }

    public void verifyBookDeleted() {
        step("API: Проверить что книга удалена", () -> {
            var userResponse = booksApi.getUserInfo(authResponse.getToken(), authResponse.getUserId());
            int booksCount = userResponse.path("books.size()");
            Assertions.assertEquals(0, booksCount, "Книга должна быть удалена из коллекции");
        });
    }
}
