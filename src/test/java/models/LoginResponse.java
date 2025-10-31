// Берем готовые от коллеги:
package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginResponse {
    String token, userId, expires;
}

