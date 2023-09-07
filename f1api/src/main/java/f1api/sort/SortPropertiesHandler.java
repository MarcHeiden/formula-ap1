package f1api.sort;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class SortPropertiesHandler {
    public static Pageable handleSortProperties(Pageable pageable, SortPropertiesMapper sortPropertiesMapper) {
        return PageRequest.of(
                pageable.getPageNumber(), pageable.getPageSize(), sortPropertiesMapper.map(pageable.getSort()));
    }
}
