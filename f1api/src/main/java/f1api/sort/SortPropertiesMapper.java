package f1api.sort;

import java.util.Map;
import org.springframework.data.domain.Sort;

public interface SortPropertiesMapper {

    Map<String, String> getSortProperties();

    default Sort map(Sort sort) {
        return Sort.by(sort.map(order -> {
                    String property = getSortProperties().get(order.getProperty());
                    if (property == null) {
                        /*throw new ApiInvalidSortParameterException(
                        "'" + order.getProperty() + "' is not a valid sort parameter");*/
                        property = order.getProperty();
                    }
                    return new Sort.Order(order.getDirection(), property);
                })
                .toList());
    }
}
