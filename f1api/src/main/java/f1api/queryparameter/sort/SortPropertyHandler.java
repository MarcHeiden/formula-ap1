package f1api.queryparameter.sort;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class SortPropertyHandler {
    public static <T> Pageable handleSortProperties(
            Pageable pageable, SortPropertyMapper sortPropertyMapper, Class<T> DTOClass) {
        return PageRequest.of(
                pageable.getPageNumber(), pageable.getPageSize(), sortPropertyMapper.map(pageable.getSort(), DTOClass));
    }
}
