package f1api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class ApiException extends RuntimeException {
    private final HttpStatus httpStatus;
    /*private List<ValidationError> validationErrors = new ArrayList<>();*/

    public ApiException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    /*public ApiException(String message, HttpStatus httpStatus, List<ValidationError> validationErrors) {
        this(message, httpStatus);
        this.validationErrors = validationErrors;
    }*/
}
