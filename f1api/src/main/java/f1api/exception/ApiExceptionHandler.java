package f1api.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
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
        String message = "Invalid value for field";
        ApiExceptionInfo apiExceptionInfo = new ApiExceptionInfo(httpStatus, message);
        methodArgumentNotValidException
                .getFieldErrors()
                .forEach(fieldError ->
                        apiExceptionInfo.addValidationError(fieldError.getField(), fieldError.getDefaultMessage()));
        return ResponseEntity.status(httpStatus).body(apiExceptionInfo);
    }

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

    // Deserialization Error
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiExceptionInfo> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException httpMessageNotReadableException) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        String message = httpMessageNotReadableException.getMessage();
        ApiExceptionInfo apiExceptionInfo = new ApiExceptionInfo(httpStatus, message);
        return ResponseEntity.status(httpStatus).body(apiExceptionInfo);
    }

    // Invalid type for parameter
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiExceptionInfo> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException methodArgumentTypeMismatchException) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        String message = "Invalid value for parameter '" + methodArgumentTypeMismatchException.getName() + "'";
        ApiExceptionInfo apiExceptionInfo = new ApiExceptionInfo(httpStatus, message);
        return ResponseEntity.status(httpStatus).body(apiExceptionInfo);
    }

    // HTTP Method not supported
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiExceptionInfo> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException httpRequestMethodNotSupportedException) {
        HttpStatus httpStatus = HttpStatus.valueOf(
                httpRequestMethodNotSupportedException.getStatusCode().value());
        String message = httpRequestMethodNotSupportedException.getMessage();
        ApiExceptionInfo apiExceptionInfo = new ApiExceptionInfo(httpStatus, message);
        return ResponseEntity.status(httpStatus).body(apiExceptionInfo);
    }

    // Default Spring Not Found Exception
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiExceptionInfo> handleNoHandlerFoundException(
            NoHandlerFoundException noHandlerFoundException) {
        HttpStatus httpStatus =
                HttpStatus.valueOf(noHandlerFoundException.getStatusCode().value());
        String message = noHandlerFoundException.getMessage();
        ApiExceptionInfo apiExceptionInfo = new ApiExceptionInfo(httpStatus, message);
        return ResponseEntity.status(httpStatus).body(apiExceptionInfo);
    }

    // Invalid sort parameter
    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ApiExceptionInfo> handlePropertyReferenceException(
            PropertyReferenceException propertyReferenceException) {
        HttpStatus httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
        String message = "'" + propertyReferenceException.getPropertyName() + "' is not a valid sort parameter.";
        ApiExceptionInfo apiExceptionInfo = new ApiExceptionInfo(httpStatus, message);
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
        String message = "Something went wrong";
        ApiExceptionInfo apiExceptionInfo = new ApiExceptionInfo(httpStatus, message);
        return ResponseEntity.status(httpStatus).body(apiExceptionInfo);
    }
}
