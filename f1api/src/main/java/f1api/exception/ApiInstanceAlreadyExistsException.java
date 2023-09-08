package f1api.exception;

import org.springframework.http.HttpStatus;

public class ApiInstanceAlreadyExistsException extends ApiException {

    public ApiInstanceAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public static ApiInstanceAlreadyExistsException of(String instance, String property, String propertyValue) {
        String message = instance + " with the " + property + " '" + propertyValue + "' already exists.";
        return new ApiInstanceAlreadyExistsException(message);
    }
}
