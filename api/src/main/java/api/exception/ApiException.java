package api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Abstract base exception that is extended by all project specific exceptions.
 */
@Getter
public abstract class ApiException extends RuntimeException {
    private final HttpStatus httpStatus;

    public ApiException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
