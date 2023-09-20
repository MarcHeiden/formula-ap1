package f1api.queryparameter;

import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@Getter
public class DefaultQueryParameter implements QueryParameter {
    private final Set<String> queryParameters = Set.of();
}
