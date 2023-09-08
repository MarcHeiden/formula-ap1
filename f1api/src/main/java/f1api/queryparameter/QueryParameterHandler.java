package f1api.queryparameter;

import static f1api.queryparameter.sort.SortPropertyHandler.handleSortProperties;

import f1api.exception.ApiQueryParameterDoesNotExistException;
import f1api.queryparameter.sort.SortPropertyMapper;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.util.MultiValueMap;

public class QueryParameterHandler {

    private static <T> void handleNonExistingQueryParameters(
            MultiValueMap<String, String> parameters, Class<T> DTOClass) {
        List.of("pageNumber", "pageSize", "sort").forEach(parameters::remove); // Remove pageable parameters
        if (!parameters.isEmpty()) {
            // Check if parameter is field in DTO
            List<String> fieldNames = Arrays.stream(DTOClass.getDeclaredFields())
                    .map(Field::getName)
                    .toList();
            parameters.forEach((parameter, parameterValues) -> {
                if (!fieldNames.contains(parameter)) {
                    throw ApiQueryParameterDoesNotExistException.of(parameter);
                }
            });
        }
    }

    public static <T> Pageable handleQueryParameters(
            Pageable pageable,
            SortPropertyMapper sortPropertyMapper,
            MultiValueMap<String, String> parameters,
            Class<T> DTOClass) {
        Pageable handledPageable = handleSortProperties(pageable, sortPropertyMapper, DTOClass);
        handleNonExistingQueryParameters(parameters, DTOClass);
        return handledPageable;
    }

    public static <T> void handleQueryParameters(MultiValueMap<String, String> parameters, Class<T> DTOClass) {
        handleNonExistingQueryParameters(parameters, DTOClass);
    }
}
