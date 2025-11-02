package config;

import org.aeonbits.owner.Config;

@Config.Sources({
        "classpath:config/local.properties",
        "classpath:config/remote.properties"
})
public interface UserConfig extends Config {

    @Key("userName")
    String getUserName();

    @Key("password")
    String getPassword();
}
