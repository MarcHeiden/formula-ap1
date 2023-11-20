package api.queryparameter.sort;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Provides wrapper functions that invoke the {@link SortPropertyMapper#map(Sort, List, String)} method
 * for the {@link Sort} object of the given {@link Pageable} instance and return a new Pageable instance that
 * can be passed to repository methods.
 */
public class SortPropertyHandler {
    public static Pageable handleSortProperties(
            Pageable pageable, SortPropertyMapper sortPropertyMapper, List<String> dtoProperties) {
        return PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sortPropertyMapper.map(pageable.getSort(), dtoProperties, ""));
    }

    public static Pageable handleSortProperties(
            Pageable pageable, SortPropertyMapper sortPropertyMapper, List<String> dtoProperties, String sortPrefix) {
        sortPrefix += ".";
        return PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sortPropertyMapper.map(pageable.getSort(), dtoProperties, sortPrefix));
    }
}
