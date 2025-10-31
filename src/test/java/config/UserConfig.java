package config;

import org.aeonbits.owner.Config;

@Config.Sources({
        "classpath:config/user.properties",
})
public interface UserConfig extends Config {
    @Key("userName")
    String getUserName();

    @Key("password")
    String getPassword();
}