package helpers;

import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.Cookie;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(LoginExtension.class)
public @interface WithLogin {
    String username() default "";
    String password() default "";
}