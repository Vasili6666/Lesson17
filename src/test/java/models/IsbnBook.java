
package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class IsbnBook {
    public IsbnBook(String isbn) {
        this.isbn = isbn;
    }
    private String isbn;
}
