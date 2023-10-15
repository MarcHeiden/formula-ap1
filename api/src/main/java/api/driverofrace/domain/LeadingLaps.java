package api.driverofrace.domain;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Min;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode
@Embeddable
public class LeadingLaps {
    @Min(0)
    private Integer numberOfLaps;
}
