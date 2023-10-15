package api.exception;

public class ApiInvalidQueryParameterException extends ApiInvalidParameterException {

    public ApiInvalidQueryParameterException(String message) {
        super(message);
    }

    public static ApiInvalidQueryParameterException of(String parameter) {
        String message = "Query parameter '" + parameter + "' is invalid.";
        return new ApiInvalidQueryParameterException(message);
    }
}
