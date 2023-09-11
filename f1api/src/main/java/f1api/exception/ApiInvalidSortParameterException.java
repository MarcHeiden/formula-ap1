package f1api.exception;

import org.springframework.http.HttpStatus;

public class ApiInvalidSortParameterException extends ApiException {

    public ApiInvalidSortParameterException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public static ApiInvalidSortParameterException of(String property) {
        String message = "'" + property + "' is not a valid sort property.";
        return new ApiInvalidSortParameterException(message);
    }
}
