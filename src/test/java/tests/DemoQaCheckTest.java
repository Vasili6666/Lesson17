package tests;

import api.BookApiSteps;
import data.BookData;
import helpers.WithLogin;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pages.ProfilePage;

public class DemoQaCheckTest extends TestBase {

    @Test
    @WithLogin
    @DisplayName("Проверка удаления книг из профиля")

    void addedDeletedItemTest() {
        BookApiSteps bookSteps = new BookApiSteps();
        ProfilePage profilePage = new ProfilePage();
        BookData bookData = new BookData();


        bookSteps.deleteAllBooks();
        bookSteps.addBook(bookData.getIsbn());
        bookSteps.verifyBookAdded(bookData.getIsbn());


        profilePage.openProfilePage();
        profilePage.verifyUserName();
        profilePage.verifyBookDisplayed(bookData.getBookTitle());
        profilePage.deleteBook();
        profilePage.verifyProfileIsEmpty();
        profilePage.verifyBooksCollectionEmptyAPI();
    }
}
