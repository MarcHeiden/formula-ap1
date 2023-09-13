package f1api.exception;

import java.util.Set;
import org.springframework.http.HttpStatus;

public class ApiPropertyIsNullException extends ApiException {
    public ApiPropertyIsNullException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    public static ApiPropertyIsNullException of(Set<String> properties) {
        StringBuilder message = new StringBuilder();
        properties.forEach(property -> message.append(property).append(" or "));
        int startIndex = message.lastIndexOf("or");
        message.delete(startIndex, startIndex + 3).append("must not be null.");
        return new ApiPropertyIsNullException(message.toString());
    }
}
