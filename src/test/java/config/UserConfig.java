package config;

import org.aeonbits.owner.Config;

@Config.Sources({
        "classpath:config/local.properties",
        "classpath:config/remote.properties"
})
public interface UserConfig extends Config {

    @Key("userName")
    @DefaultValue("basil8") // дефолтное значение, если в properties не указано
    String getUserName();

    @Key("password")
    @DefaultValue("Basil1982!") // дефолтное значение
    String getPassword();
}
