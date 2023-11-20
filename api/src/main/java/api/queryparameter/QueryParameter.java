package api.queryparameter;

import api.exception.ApiInvalidQueryParameterException;
import java.util.List;
import java.util.Set;

/**
 * Provides logic to check if query parameters are valid.
 */
public interface QueryParameter {
    /**
     * @return valid queryParameters set
     */
    Set<String> getQueryParameters();

    /**
     * Checks if given query parameters are defined in the queryParameters set.
     * @param parameters given query parameters
     * @throws ApiInvalidQueryParameterException if a parameter is not defined in the queryParameters set
     */
    default void checkIfParametersAreValid(Set<String> parameters) {
        List.of("pageNumber", "pageSize", "sort").forEach(parameters::remove); // Remove pageable parameters
        parameters.forEach((parameter) -> {
            if (!getQueryParameters().contains(parameter)) {
                throw ApiInvalidQueryParameterException.of(parameter);
            }
        });
    }
}
