package f1api.exception;

import java.util.Map;

public class ApiInstanceAlreadyExistsException extends ApiConflictException {

    public ApiInstanceAlreadyExistsException(String message) {
        super(message);
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

    public static ApiInstanceAlreadyExistsException of(
            String memberInstance,
            String memberInstanceProperty,
            String memberInstancePropertyValue,
            String instance,
            String instanceProperty,
            String instancePropertyValue) {
        String message = memberInstance + " with the " + memberInstanceProperty + " '" + memberInstancePropertyValue
                + "' already exists for the " + instance + " with the " + instanceProperty + " '"
                + instancePropertyValue + "'.";
        return new ApiInstanceAlreadyExistsException(message);
    }
}
