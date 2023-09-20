package f1api.queryparameter;

import static f1api.queryparameter.sort.SortPropertyHandler.handleSortProperties;

import f1api.queryparameter.sort.SortPropertyMapper;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.util.MultiValueMap;

public class QueryParameterHandler {

    public static Pageable handleQueryParameters(
            MultiValueMap<String, String> parameters,
            QueryParameter queryParameter,
            Pageable pageable,
            SortPropertyMapper sortPropertyMapper,
            List<String> dtoProperties) {
        queryParameter.checkIfParametersAreValid(parameters.keySet());
        return handleSortProperties(pageable, sortPropertyMapper, dtoProperties);
    }

    public static Pageable handleQueryParameters(
            MultiValueMap<String, String> parameters,
            QueryParameter queryParameter,
            Pageable pageable,
            SortPropertyMapper sortPropertyMapper,
            List<String> dtoProperties,
            String sortPrefix) {
        queryParameter.checkIfParametersAreValid(parameters.keySet());
        return handleSortProperties(pageable, sortPropertyMapper, dtoProperties, sortPrefix);
    }
}
