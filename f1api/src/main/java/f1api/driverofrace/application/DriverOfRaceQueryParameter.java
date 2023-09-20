package f1api.driverofrace.application;

import f1api.queryparameter.QueryParameter;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@Getter
public class DriverOfRaceQueryParameter implements QueryParameter {
    private final Set<String> queryParameters = Set.of("driverId");
}
