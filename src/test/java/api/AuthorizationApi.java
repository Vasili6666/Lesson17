package api;

import models.LoginBody;
import models.LoginResponse;
import static io.restassured.RestAssured.given;
import static specs.Specification.allRequests;
import static specs.Specification.successfulLoginResponse200;

public class AuthorizationApi {

    public static LoginResponse authResponse(String username, String password) {
        LoginBody loginBody = new LoginBody();
        loginBody.setUserName(username);
        loginBody.setPassword(password);

        return given()
                .spec(allRequests)
                .body(loginBody)
                .post("/Account/v1/Login")
                .then()
                .spec(successfulLoginResponse200)
                .extract()
                .as(LoginResponse.class);
    }
}