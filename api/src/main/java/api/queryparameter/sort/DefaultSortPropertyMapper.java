package api.queryparameter.sort;

import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link SortPropertyMapper} with an empty sortProperties map that
 * models the case where all property names in an entity and the corresponding DTO match.
 */
@Component
@NoArgsConstructor
@Getter
public class DefaultSortPropertyMapper implements SortPropertyMapper {
    private final Map<String, String> sortProperties = Map.of();
}
