package api.queryparameter.sort;

import api.exception.ApiInvalidSortPropertyException;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Sort;

/**
 * Provides mapping logic to ensure that entities can be sorted by given sort properties existing
 * in the corresponding DTO.
 */
public interface SortPropertyMapper {

    /**
     * @return sortProperties map where keys are the property names in the DTO and
     * the values the property names in the entity.
     */
    Map<String, String> getSortProperties();

    /**
     * Creates valid sort by checking and mapping sort properties using {@link SortPropertyMapper#getSortProperties()}
     * and adding a sort prefix for JPA queries.
     * @param sort current {@link Sort} instance
     * @param dtoProperties properties of the DTO
     * @param sortPrefix sort prefix to add for JPA queries
     * @return New sort with valid sort properties.
     * @throws ApiInvalidSortPropertyException if sort property does not exist in the
     * dtoProperties or if it is an id.
     */
    default Sort map(Sort sort, List<String> dtoProperties, String sortPrefix) {
        return Sort.by(sort.map(order -> {
                    String property = getSortProperties().get(order.getProperty());
                    if (property == null) {
                        // Throw ApiInvalidSortPropertyException if sort property does not exist
                        // in the dtoProperties or if it is an id.
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
