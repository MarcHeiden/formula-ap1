package f1api.exception;

import org.springframework.http.HttpStatus;

public class ApiInvalidQueryParameterException extends ApiException {

    public ApiInvalidQueryParameterException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public static ApiInvalidQueryParameterException of(String parameter) {
        String message = "Query parameter '" + parameter + "' is invalid.";
        return new ApiInvalidQueryParameterException(message);
    }
}
