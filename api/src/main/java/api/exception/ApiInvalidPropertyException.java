package api.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when a property in a DTO is set to an invalid value.
 * HTTP Status Code is 422 Unprocessable Entity.
 */
public class ApiInvalidPropertyException extends ApiException {
    public ApiInvalidPropertyException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
