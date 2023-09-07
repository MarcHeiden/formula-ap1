package f1api.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class ApiExceptionHandler {
    private final ApiExceptionMapper apiExceptionMapper;

    @Autowired
    public ApiExceptionHandler(ApiExceptionMapper apiExceptionMapper) {
        this.apiExceptionMapper = apiExceptionMapper;
    }

    // Validation Error
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiExceptionInfo> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException methodArgumentNotValidException) {
        HttpStatus httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
        ApiExceptionInfo apiExceptionInfo = new ApiExceptionInfo(httpStatus, "Invalid value for field");
        methodArgumentNotValidException
                .getFieldErrors()
                .forEach(fieldError ->
                        apiExceptionInfo.addValidationError(fieldError.getField(), fieldError.getDefaultMessage()));
        return ResponseEntity.status(httpStatus).body(apiExceptionInfo);
    }

    // Deserialization Error
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiExceptionInfo> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException httpMessageNotReadableException) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ApiExceptionInfo apiExceptionInfo =
                new ApiExceptionInfo(httpStatus, httpMessageNotReadableException.getMessage());
        return ResponseEntity.status(httpStatus).body(apiExceptionInfo);
    }

    // HTTP Method not supported
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiExceptionInfo> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException httpRequestMethodNotSupportedException) {
        HttpStatus httpStatus = HttpStatus.valueOf(
                httpRequestMethodNotSupportedException.getStatusCode().value());
        ApiExceptionInfo apiExceptionInfo =
                new ApiExceptionInfo(httpStatus, httpRequestMethodNotSupportedException.getMessage());
        return ResponseEntity.status(httpStatus).body(apiExceptionInfo);
    }

    // Default Spring Not Found Exception
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiExceptionInfo> handleNoHandlerFoundException(
            NoHandlerFoundException noHandlerFoundException) {
        HttpStatus httpStatus =
                HttpStatus.valueOf(noHandlerFoundException.getStatusCode().value());
        ApiExceptionInfo apiExceptionInfo = new ApiExceptionInfo(httpStatus, noHandlerFoundException.getMessage());
        return ResponseEntity.status(httpStatus).body(apiExceptionInfo);
    }

    // Invalid sort parameter
    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ApiExceptionInfo> handlePropertyReferenceException(
            PropertyReferenceException propertyReferenceException) {
        HttpStatus httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
        ApiExceptionInfo apiExceptionInfo = new ApiExceptionInfo(
                httpStatus, "'" + propertyReferenceException.getPropertyName() + "' is not a valid sort parameter");
        return ResponseEntity.status(httpStatus).body(apiExceptionInfo);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiExceptionInfo> handleApiException(ApiException apiException) {
        return ResponseEntity.status(apiException.getHttpStatus())
                .body(apiExceptionMapper.toApiExceptionInfo(apiException));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiExceptionInfo> handleUncaughtException() {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiExceptionInfo apiExceptionInfo = new ApiExceptionInfo(httpStatus, "Something went wrong");
        return ResponseEntity.status(httpStatus).body(apiExceptionInfo);
    }
}
