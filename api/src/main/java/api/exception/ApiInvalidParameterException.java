package api.exception;

import org.springframework.http.HttpStatus;

public abstract class ApiInvalidParameterException extends ApiException {
    public ApiInvalidParameterException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
