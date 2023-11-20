package api.responsepage;

import java.util.List;
import org.springframework.data.domain.Page;

/**
 * Serves as wrapper of the {@link Page} class to
 * exclude unneeded properties of a page instance.
 * @param totalElements
 * @param totalPages
 * @param pageNumber
 * @param pageSize
 * @param content
 * @param <T>
 */
public record ResponsePage<T>(long totalElements, int totalPages, int pageNumber, int pageSize, List<T> content) {
    public static <T> ResponsePage<T> of(Page<T> page) {
        return new ResponsePage<>(
                page.getTotalElements(), page.getTotalPages(), page.getNumber(), page.getSize(), page.getContent());
    }
}
