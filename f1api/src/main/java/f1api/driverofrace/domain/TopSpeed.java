package f1api.driverofrace.domain;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode
@Embeddable
public class TopSpeed {
    @DecimalMin("0.0")
    private Double speed;
}
