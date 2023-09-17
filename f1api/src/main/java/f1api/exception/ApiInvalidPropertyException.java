package f1api.exception;

import org.springframework.http.HttpStatus;

public class ApiInvalidPropertyException extends ApiException {
    public ApiInvalidPropertyException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
