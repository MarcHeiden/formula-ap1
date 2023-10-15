package api.queryparameter.sort;

import api.exception.ApiInvalidSortPropertyException;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Sort;

public interface SortPropertyMapper {

    Map<String, String> getSortProperties();

    default Sort map(Sort sort, List<String> dtoProperties, String sortPrefix) {
        return Sort.by(sort.map(order -> {
                    String property = getSortProperties().get(order.getProperty());
                    if (property == null) {
                        // Check if sort property is property in DTO and is not an id
                        if (order.getProperty().toLowerCase().matches("^.*id$")
                                || !dtoProperties.contains(order.getProperty())) {
                            throw ApiInvalidSortPropertyException.of(order.getProperty());
                        } else {
                            property = order.getProperty();
                        }
                    }
                    property = sortPrefix + property;
                    return new Sort.Order(order.getDirection(), property);
                })
                .toList());
    }
}
