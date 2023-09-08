package f1api.exception;

import org.springframework.http.HttpStatus;

public class ApiNotFoundException extends ApiException {

    public ApiNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public static ApiNotFoundException of(String instance, String property, String propertyValue) {
        String message = instance + " with the " + property + " '" + propertyValue + "' does not exist.";
        return new ApiNotFoundException(message);
    }
}
