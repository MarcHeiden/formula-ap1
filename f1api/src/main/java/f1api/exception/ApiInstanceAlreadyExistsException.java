package f1api.exception;

import org.springframework.http.HttpStatus;

public class ApiInstanceAlreadyExistsException extends ApiException {

    public ApiInstanceAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
