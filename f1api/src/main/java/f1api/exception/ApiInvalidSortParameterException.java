package f1api.exception;

import org.springframework.http.HttpStatus;

public class ApiInvalidSortParameterException extends ApiException {

    public ApiInvalidSortParameterException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
