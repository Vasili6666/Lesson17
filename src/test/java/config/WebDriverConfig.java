package config;

import org.aeonbits.owner.Config;

@Config.Sources({
        "classpath:config/${env}.properties" // выбирает local.properties или remote.properties
})
public interface WebDriverConfig extends Config {

    @Key("browser")
    @DefaultValue("chrome")
    String browser();

    @Key("browserVersion")
    @DefaultValue("128.0")
    String browserVersion();

    @Key("isRemote")
    @DefaultValue("false")
    boolean isRemote();

    @Key("remoteUrl")
    @DefaultValue("")
    String remoteUrl();

    @Key("baseUrl")
    @DefaultValue("https://demoqa.com")
    String baseUrl();
}
