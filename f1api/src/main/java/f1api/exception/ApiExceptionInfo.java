package f1api.exception;

import com.fasterxml.jackson.annotation.JsonGetter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class ApiExceptionInfo {
    private final HttpStatus httpStatus;
    // Get current UTC date and time
    private final Instant timestamp = Instant.now();
    private final String message;

    private record ValidationError(String fieldName, String message) {}

    private final List<ValidationError> validationErrors = new ArrayList<>();

    @JsonGetter("httpStatus")
    public int getHttpStatusValue() {
        return httpStatus.value();
    }

    public void addValidationError(String fieldName, String message) {
        this.validationErrors.add(new ValidationError(fieldName, message));
    }
}
