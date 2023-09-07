package f1api.exception;

import org.springframework.http.HttpStatus;

public class ApiNotFoundException extends ApiException {

    public ApiNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
