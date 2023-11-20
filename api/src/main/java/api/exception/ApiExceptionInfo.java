package api.exception;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Is passed in the response body if an exception occurs.
 */
@Getter
@RequiredArgsConstructor
@JsonPropertyOrder({"httpStatusCode"})
public class ApiExceptionInfo {
    @JsonProperty("httpStatusCode")
    private final HttpStatus httpStatus;

    @JsonGetter("httpStatusCode")
    public int getHttpStatusCode() {
        return httpStatus.value();
    }

    // Get current UTC date and time
    private final Instant timestamp = Instant.now();

    private final String message;

    private record ValidationError(String fieldName, String message) {}

    private final List<ValidationError> validationErrors = new ArrayList<>();

    public void addValidationError(String fieldName, String message) {
        this.validationErrors.add(new ValidationError(fieldName, message));
    }
}
