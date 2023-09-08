package f1api.queryparameter.sort;

import f1api.exception.ApiInvalidSortParameterException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Sort;

public interface SortPropertyMapper {

    Map<String, String> getSortProperties();

    default <T> Sort map(Sort sort, Class<T> DTOClass) {
        return Sort.by(sort.map(order -> {
                    String property = getSortProperties().get(order.getProperty());
                    if (property == null) {
                        // Check if sort property is field in DTO
                        List<String> fieldNames = Arrays.stream(DTOClass.getDeclaredFields())
                                .map(Field::getName)
                                .toList();
                        if (fieldNames.contains(order.getProperty())) {
                            property = order.getProperty();
                        } else {
                            throw ApiInvalidSortParameterException.of(order.getProperty());
                        }
                    }
                    return new Sort.Order(order.getDirection(), property);
                })
                .toList());
    }
}
