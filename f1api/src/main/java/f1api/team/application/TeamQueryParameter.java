package f1api.team.application;

import f1api.queryparameter.QueryParameter;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@Getter
public class TeamQueryParameter implements QueryParameter {
    private final Set<String> queryParameters = Set.of("teamName");
}
