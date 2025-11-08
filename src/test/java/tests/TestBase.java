package tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import config.WebDriverConfig;
import helpers.Attach;
import helpers.CustomAllureListener;
import io.qameta.allure.selenide.AllureSelenide;
import io.restassured.RestAssured;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Map;

import static com.codeborne.selenide.Selenide.closeWebDriver;

public class TestBase {


    private static final WebDriverConfig config =
            ConfigFactory.create(WebDriverConfig.class, System.getProperties());

    @BeforeAll
    static void setup() {


        Configuration.browser = config.browser();
        Configuration.browserVersion = config.browserVersion();
        Configuration.baseUrl = config.baseUrl();
        Configuration.browserSize = "1920x1080";
        Configuration.pageLoadStrategy = "eager";
        Configuration.timeout = 10000;


        if (config.remoteUrl() != null && !config.remoteUrl().isEmpty()) {
            Configuration.remote = config.remoteUrl();
            setupSelenoidCapabilities();
        }


        RestAssured.baseURI = config.baseUrl();
        RestAssured.filters(CustomAllureListener.withCustomTemplates());

        logConfiguration();
    }

    private static void setupSelenoidCapabilities() {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("selenoid:options", Map.of(
                "enableVNC", true,
                "enableVideo", true
        ));
        Configuration.browserCapabilities = capabilities;
    }

    private static void logConfiguration() {
        System.out.println("=== КОНФИГУРАЦИЯ ТЕСТОВ ===");
        System.out.println("Browser: " + Configuration.browser);
        System.out.println("Browser Version: " + Configuration.browserVersion);
        System.out.println("Base URL: " + Configuration.baseUrl);
        System.out.println("Remote: " + Configuration.remote);
        System.out.println("===========================");
    }


    @BeforeEach
    void addAllureListener() {
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());
    }

    @AfterEach
    void addAttachment() {
        Attach.screenshotAs("Last screenshot");
        Attach.pageSource();
        Attach.browserConsoleLogs();

        if (Configuration.remote != null && !Configuration.remote.isEmpty()) {
            Attach.addVideo();
        }

        closeWebDriver();
    }
}
