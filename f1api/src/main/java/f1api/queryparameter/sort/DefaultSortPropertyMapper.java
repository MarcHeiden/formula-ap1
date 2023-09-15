package f1api.queryparameter.sort;

import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@Getter
public class DefaultSortPropertyMapper implements SortPropertyMapper {
    private final Map<String, String> sortProperties = Map.of();
}
