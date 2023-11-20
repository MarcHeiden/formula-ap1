package api.exception;

import org.springframework.http.HttpStatus;

/**
 * Abstract base exception for invalid parameter exceptions.
 * HTTP Status Code is 400 Bad Request.
 */
public abstract class ApiInvalidParameterException extends ApiException {
    public ApiInvalidParameterException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
