// Берем готовые от коллеги:
package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
public class DeleteBooksBody {
    String userId;
}