package tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import helpers.Attach;
import helpers.CustomAllureListener;
import io.qameta.allure.selenide.AllureSelenide;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Map;

import static com.codeborne.selenide.Selenide.closeWebDriver;

public class TestBase {

    @BeforeAll
    static void setup() {
        // Прямое использование System.getProperty() с дефолтными значениями
        Configuration.baseUrl = getProperty("url", "https://demoqa.com");
        Configuration.browser = getProperty("browser", "chrome");
        Configuration.browserSize = getProperty("windowSize", "1920x1080");
        Configuration.browserVersion = getProperty("browser.version", "119.0");
        Configuration.pageLoadStrategy = "eager";
        Configuration.timeout = 10000;

        // RestAssured
        RestAssured.baseURI = getProperty("url", "https://demoqa.com");
        RestAssured.filters(CustomAllureListener.withCustomTemplates());

        // Remote WebDriver
        String remoteUrl = getProperty("remoteUrl",
                getProperty("remote.web.driver", null)); // Двойной приоритет
        if (remoteUrl != null && !remoteUrl.isEmpty()) {
            Configuration.remote = remoteUrl;

            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability("selenoid:options", Map.<String, Object>of(
                    "enableVNC", true,
                    "enableVideo", true
            ));
            Configuration.browserCapabilities = capabilities;
        }

        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());

        logConfiguration();
    }

    // Универсальный метод для получения системных свойств
    private static String getProperty(String name, String defaultValue) {
        return System.getProperty(name, defaultValue);
    }

    // Перегруженный метод для случаев без дефолтного значения
    private static String getProperty(String name) {
        return System.getProperty(name);
    }

    private static void logConfiguration() {
        System.out.println("=== КОНФИГУРАЦИЯ ТЕСТОВ ===");
        System.out.println("Browser: " + Configuration.browser);
        System.out.println("Browser Size: " + Configuration.browserSize);
        System.out.println("Base URL: " + Configuration.baseUrl);
        System.out.println("Remote: " + Configuration.remote);
        System.out.println("===========================");
    }

    @AfterEach
    void addAttachment() {
        Attach.screenshotAs("Last screenshot");
        Attach.pageSource();
        Attach.browserConsoleLogs();

        if (Configuration.remote != null) {
            Attach.addVideo();
        }

        closeWebDriver();
    }
}