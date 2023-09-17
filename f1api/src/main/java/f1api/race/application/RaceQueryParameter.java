package f1api.race.application;

import f1api.queryparameter.QueryParameter;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@Getter
public class RaceQueryParameter implements QueryParameter {
    private final Set<String> queryParameters = Set.of("raceName", "date");
}
