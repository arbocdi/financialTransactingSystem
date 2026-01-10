package kg.arbocdi.fts.core.rest;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;
@Getter
public class ParseException extends RuntimeException{
    private HttpStatusCode statusCode;
    public ParseException(String message, HttpStatusCode statusCode) {
        super(message);
    }
}
