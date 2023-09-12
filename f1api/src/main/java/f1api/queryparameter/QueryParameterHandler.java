package f1api.queryparameter;

import static f1api.queryparameter.sort.SortPropertyHandler.handleSortProperties;

import f1api.queryparameter.sort.SortPropertyMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.util.MultiValueMap;

public class QueryParameterHandler {

    public static <T> Pageable handleQueryParameters(
            MultiValueMap<String, String> parameters,
            QueryParameter queryParameter,
            Pageable pageable,
            SortPropertyMapper sortPropertyMapper,
            Class<T> DTOClass) {
        handleQueryParameters(parameters, queryParameter);
        return handleSortProperties(pageable, sortPropertyMapper, DTOClass);
    }

    public static void handleQueryParameters(MultiValueMap<String, String> parameters, QueryParameter queryParameter) {
        queryParameter.checkIfParametersAreValid(parameters.keySet());
    }
}
