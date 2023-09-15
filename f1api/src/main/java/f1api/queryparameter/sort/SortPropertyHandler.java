package f1api.queryparameter.sort;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
