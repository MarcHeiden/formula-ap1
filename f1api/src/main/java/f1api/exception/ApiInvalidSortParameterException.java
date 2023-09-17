package f1api.exception;

public class ApiInvalidSortParameterException extends ApiInvalidParameterException {

    public ApiInvalidSortParameterException(String message) {
        super(message);
    }

    public static ApiInvalidSortParameterException of(String property) {
        String message = "'" + property + "' is not a valid sort property.";
        return new ApiInvalidSortParameterException(message);
    }
}
