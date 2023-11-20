package api.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * Catches all thrown exceptions and creates and returns a corresponding
 * {@link ApiExceptionInfo} instance from the caught exception.
 */
@ControllerAdvice
public class ApiExceptionHandler {
    private final ApiExceptionMapper apiExceptionMapper;

    @Autowired
    public ApiExceptionHandler(ApiExceptionMapper apiExceptionMapper) {
        this.apiExceptionMapper = apiExceptionMapper;
    }

    /**
     * Handles {@link MethodArgumentNotValidException}, which is thrown when validation fails.
     * HTTP Status Code is 422 Unprocessable Entity.
     * @param methodArgumentNotValidException
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiExceptionInfo> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException methodArgumentNotValidException) {
        HttpStatus httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
        String message = "Invalid value for field";
        ApiExceptionInfo apiExceptionInfo = new ApiExceptionInfo(httpStatus, message);
        methodArgumentNotValidException
                .getFieldErrors()
                .forEach(fieldError ->
                        apiExceptionInfo.addValidationError(fieldError.getField(), fieldError.getDefaultMessage()));
        return ResponseEntity.status(httpStatus).body(apiExceptionInfo);
    }

    /**
     * Handles {@link ConstraintViolationException}, which is thrown when validation fails.
     * HTTP Status Code is 422 Unprocessable Entity.
     * @param constraintViolationException
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiExceptionInfo> handleConstraintValidationException(
            ConstraintViolationException constraintViolationException) {
        HttpStatus httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
        String message = "Invalid value for field";
        ApiExceptionInfo apiExceptionInfo = new ApiExceptionInfo(httpStatus, message);
        constraintViolationException.getConstraintViolations().forEach(constraintViolation -> {
            String[] splitPropertyPath =
                    constraintViolation.getPropertyPath().toString().split("\\.");
            apiExceptionInfo.addValidationError(
                    splitPropertyPath[splitPropertyPath.length - 1], constraintViolation.getMessage());
        });
        return ResponseEntity.status(httpStatus).body(apiExceptionInfo);
    }

    /**
     * Handles {@link HttpMessageNotReadableException}, which is thrown when the request body can not be deserialized.
     * HTTP Status Code is 400 Bad Request.
     * @param httpMessageNotReadableException
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiExceptionInfo> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException httpMessageNotReadableException) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        String message = httpMessageNotReadableException.getMessage();
        ApiExceptionInfo apiExceptionInfo = new ApiExceptionInfo(httpStatus, message);
        return ResponseEntity.status(httpStatus).body(apiExceptionInfo);
    }

    /**
     * Handles {@link MethodArgumentTypeMismatchException}, which is thrown when controller parameters
     * have the wrong type.
     * HTTP Status Code is 400 Bad Request.
     * @param methodArgumentTypeMismatchException
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiExceptionInfo> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException methodArgumentTypeMismatchException) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        String message = "Invalid value for parameter '" + methodArgumentTypeMismatchException.getName() + "'";
        ApiExceptionInfo apiExceptionInfo = new ApiExceptionInfo(httpStatus, message);
        return ResponseEntity.status(httpStatus).body(apiExceptionInfo);
    }

    /**
     * Handles {@link HttpRequestMethodNotSupportedException}.
     * HTTP Status Code is defined by {@link HttpRequestMethodNotSupportedException}.
     * @param httpRequestMethodNotSupportedException
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiExceptionInfo> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException httpRequestMethodNotSupportedException) {
        HttpStatus httpStatus = HttpStatus.valueOf(
                httpRequestMethodNotSupportedException.getStatusCode().value());
        String message = httpRequestMethodNotSupportedException.getMessage();
        ApiExceptionInfo apiExceptionInfo = new ApiExceptionInfo(httpStatus, message);
        return ResponseEntity.status(httpStatus).body(apiExceptionInfo);
    }

    /**
     * Handles {@link NoHandlerFoundException}, the default Spring Not Found Exception.
     * HTTP Status Code is defined by {@link NoHandlerFoundException}.
     * @param noHandlerFoundException
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiExceptionInfo> handleNoHandlerFoundException(
            NoHandlerFoundException noHandlerFoundException) {
        HttpStatus httpStatus =
                HttpStatus.valueOf(noHandlerFoundException.getStatusCode().value());
        String message = noHandlerFoundException.getMessage();
        ApiExceptionInfo apiExceptionInfo = new ApiExceptionInfo(httpStatus, message);
        return ResponseEntity.status(httpStatus).body(apiExceptionInfo);
    }

    /**
     * Handles {@link PropertyReferenceException}, which is thrown when the sort property is invalid.
     * HTTP Status Code is 400 Bad Request.
     * @param propertyReferenceException
     */
    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ApiExceptionInfo> handlePropertyReferenceException(
            PropertyReferenceException propertyReferenceException) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        String message = "'" + propertyReferenceException.getPropertyName() + "' is not a valid sort property.";
        ApiExceptionInfo apiExceptionInfo = new ApiExceptionInfo(httpStatus, message);
        return ResponseEntity.status(httpStatus).body(apiExceptionInfo);
    }

    /**
     * Handles {@link MissingServletRequestParameterException}, which is thrown when a required parameter
     * is not specified.
     * HTTP Status Code is 400 Bad Request.
     * @param missingServletRequestParameterException
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiExceptionInfo> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException missingServletRequestParameterException) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        String message = "Query parameter '" + missingServletRequestParameterException.getParameterName()
                + "' must not be null.";
        ApiExceptionInfo apiExceptionInfo = new ApiExceptionInfo(httpStatus, message);
        return ResponseEntity.status(httpStatus).body(apiExceptionInfo);
    }

    /**
     * Handles {@link ApiException}.
     * HTTP Status Code is defined by {@link ApiException}.
     * @param apiException
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiExceptionInfo> handleApiException(ApiException apiException) {
        return ResponseEntity.status(apiException.getHttpStatus())
                .body(apiExceptionMapper.toApiExceptionInfo(apiException));
    }

    /**
     * Handles all exceptions that are not handled by a specific handler.
     * HTTP Status Code is 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiExceptionInfo> handleUncaughtException() {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "Something went wrong";
        ApiExceptionInfo apiExceptionInfo = new ApiExceptionInfo(httpStatus, message);
        return ResponseEntity.status(httpStatus).body(apiExceptionInfo);
    }
}
