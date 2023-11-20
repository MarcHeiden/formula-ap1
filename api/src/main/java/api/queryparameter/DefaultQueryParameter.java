package api.queryparameter;

import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link QueryParameter} with an empty queryParameters set that
 * models the case where no query parameters are available.
 */
@Component
@NoArgsConstructor
@Getter
public class DefaultQueryParameter implements QueryParameter {
    private final Set<String> queryParameters = Set.of();
}
