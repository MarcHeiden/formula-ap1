package f1api.driverofrace.application;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LeadingLapsDTO {
    @Min(0)
    @NotNull
    private Integer numberOfLaps;

    @JsonUnwrapped
    @Valid
    private DriverInfoDTO driverInfoDTO;

    @Getter
    @JsonIgnore
    private static final List<String> properties = Arrays.stream(LeadingLapsDTO.class.getDeclaredFields())
            .map(Field::getName)
            .toList();
}
