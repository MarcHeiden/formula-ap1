package f1api.exception;

import java.util.Set;

public class ApiPropertyIsNullException extends ApiInvalidPropertyException {
    public ApiPropertyIsNullException(String message) {
        super(message);
    }

    public static ApiPropertyIsNullException of(Set<String> properties) {
        StringBuilder message = new StringBuilder();
        properties.forEach(property -> message.append(property).append(" or "));
        int startIndex = message.lastIndexOf("or");
        message.delete(startIndex, startIndex + 3).append("must not be null.");
        return new ApiPropertyIsNullException(message.toString());
    }
}
