package f1api.exception;

import org.springframework.http.HttpStatus;

public class ApiQueryParameterDoesNotExistException extends ApiException {

    public ApiQueryParameterDoesNotExistException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public static ApiQueryParameterDoesNotExistException of(String parameter) {
        String message = "Query parameter '" + parameter + "' does not exist.";
        return new ApiQueryParameterDoesNotExistException(message);
    }
}
