package f1api.queryparameter;

import f1api.exception.ApiQueryParameterDoesNotExistException;
import java.util.List;
import java.util.Set;

public interface QueryParameter {
    Set<String> getQueryParameters();

    default void checkIfParametersAreValid(Set<String> parameters) {
        List.of("pageNumber", "pageSize", "sort").forEach(parameters::remove); // Remove pageable parameters
        parameters.forEach((parameter) -> {
            if (!getQueryParameters().contains(parameter)) {
                throw ApiQueryParameterDoesNotExistException.of(parameter);
            }
        });
    }
}
