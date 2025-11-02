package config;

import com.codeborne.selenide.WebDriverRunner;
import org.aeonbits.owner.ConfigFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.function.Supplier;

public class WebDriverProvider implements Supplier<WebDriver> {

    private final WebDriverConfig config;

    public WebDriverProvider() {
        // Подключаем Owner с системными свойствами
        this.config = ConfigFactory.create(WebDriverConfig.class, System.getProperties());
    }

    @Override
    public WebDriver get() {
        WebDriver driver = createWebDriver();
        driver.get(config.baseUrl());
        WebDriverRunner.setWebDriver(driver); // чтобы Selenide знал драйвер
        return driver;
    }

    private WebDriver createWebDriver() {
        if (config.isRemote()) {
            return createRemoteWebDriver();
        } else {
            WebDriverManager.chromedriver().setup();
            return new ChromeDriver();
        }
    }

    private WebDriver createRemoteWebDriver() {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setBrowserName(config.browser());
        capabilities.setVersion(config.browserVersion());
        capabilities.setCapability("selenoid:options", Map.of(
                "enableVNC", true,
                "enableVideo", true
        ));
        try {
            return new RemoteWebDriver(new URL(config.remoteUrl()), capabilities);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Неверный URL для RemoteWebDriver: " + config.remoteUrl(), e);
        }
    }
}
