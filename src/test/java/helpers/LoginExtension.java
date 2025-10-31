package helpers;

import api.AuthorizationApi;
import models.LoginResponse;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static data.UserData.PASSWORD;
import static data.UserData.USER_NAME;
import static io.qameta.allure.Allure.step;

public class LoginExtension implements BeforeEachCallback {

    private static LoginResponse authResponse;

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        WithLogin withLogin = context.getRequiredTestMethod().getAnnotation(WithLogin.class);

        if (withLogin != null) {
            String username = withLogin.username().isEmpty() ? USER_NAME : withLogin.username();
            String password = withLogin.password().isEmpty() ? PASSWORD : withLogin.password();

            step("ШАГ 1: API Логин - получение токена авторизации", () -> {
                authResponse = AuthorizationApi.authResponse(username, password);
                System.out.println("✅ Токен получен: " + authResponse.getToken());
                System.out.println("✅ UserId получен: " + authResponse.getUserId());
            });

            step("ШАГ 2: Установка авторизационных кук в браузер", () -> {
                open("/favicon.ico");
                getWebDriver().manage().addCookie(new Cookie("userID", authResponse.getUserId()));
                getWebDriver().manage().addCookie(new Cookie("token", authResponse.getToken()));
                getWebDriver().manage().addCookie(new Cookie("expires", authResponse.getExpires()));
                System.out.println("✅ Авторизационные куки установлены в браузер");
            });
        }
    }

    public static LoginResponse getAuthResponse() {
        return authResponse;
    }
}