package f1api.driverofrace.domain;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Min;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode
@Embeddable
public class RaceResult {
    @Min(1)
    private Integer position;

    private Boolean dnf;
}
