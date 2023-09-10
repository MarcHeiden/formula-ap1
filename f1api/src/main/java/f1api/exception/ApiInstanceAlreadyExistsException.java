package f1api.exception;

import java.util.Map;
import org.springframework.http.HttpStatus;

public class ApiInstanceAlreadyExistsException extends ApiException {

    public ApiInstanceAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public static ApiInstanceAlreadyExistsException of(String instance, String property, String propertyValue) {
        String message = instance + " with the " + property + " '" + propertyValue + "' already exists.";
        return new ApiInstanceAlreadyExistsException(message);
    }

    public static ApiInstanceAlreadyExistsException of(String instance, Map<String, String> properties) {
        StringBuilder message = new StringBuilder();
        message.append(instance).append(" with ");
        properties.forEach((property, propertyValue) ->
                message.append(property).append(" '").append(propertyValue).append("', "));
        message.deleteCharAt(message.lastIndexOf(",")).append("already exists.");
        return new ApiInstanceAlreadyExistsException(message.toString());
    }
}
