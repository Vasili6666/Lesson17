package api;

import io.restassured.response.Response;
import models.AddBookBody;
import models.IsbnBook;

import java.util.Collections;

import static io.restassured.RestAssured.given;
import static specs.Specification.*;

public class BooksApi {

    public static void deleteAllBooks(String token, String userId) {
        given()
                .spec(allRequests)
                .header("Authorization", "Bearer " + token)
                .queryParam("UserId", userId)
                .delete("/BookStore/v1/Books")
                .then()
                .spec(successDeleteAllBooksResponse204);
    }

   /* public static void addBooks(String token, String userId) {
        addBook(token, userId, "9781449325862"); // Git Pocket Guide по умолчанию
    }*/

    public static void addBook(String token, String userId, String isbn) {
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
    }

    /*public static void deleteBook(String token, String userId, String isbn) {
        given()
                .spec(allRequests)
                .header("Authorization", "Bearer " + token)
                .body("{\"isbn\": \"" + isbn + "\", \"userId\": \"" + userId + "\"}")
                .delete("/BookStore/v1/Book")
                .then()
                .spec(successDeleteAllBooksResponse204);
    }*/

    public static Response getUserInfo(String token, String userId) {
        return given()
                .spec(allRequests)
                .header("Authorization", "Bearer " + token)
                .get("/Account/v1/User/" + userId)
                .then()
                .spec(successfulUserResponse200)
                .extract()
                .response();
    }
}