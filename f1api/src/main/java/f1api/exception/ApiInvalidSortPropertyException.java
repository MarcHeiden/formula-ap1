package f1api.exception;

public class ApiInvalidSortPropertyException extends ApiInvalidParameterException {

    public ApiInvalidSortPropertyException(String message) {
        super(message);
    }

    public static ApiInvalidSortPropertyException of(String property) {
        String message = "'" + property + "' is not a valid sort property.";
        return new ApiInvalidSortPropertyException(message);
    }
}
