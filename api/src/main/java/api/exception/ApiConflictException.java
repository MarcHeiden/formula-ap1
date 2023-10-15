package api.exception;

import org.springframework.http.HttpStatus;

public class ApiConflictException extends ApiException {
    public ApiConflictException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
