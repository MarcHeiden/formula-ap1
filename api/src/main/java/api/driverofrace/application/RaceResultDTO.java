package api.driverofrace.application;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RaceResultDTO {
    @Min(1)
    private Integer position;

    private Boolean dnf;

    @JsonUnwrapped
    @Valid
    private DriverInfoDTO driverInfoDTO;

    @JsonIgnore
    public Boolean isEmpty() {
        return position == null && dnf == null;
    }

    @Getter
    @JsonIgnore
    private static final Set<String> notNullProperties = Set.of("position", "dnf");

    @Getter
    @JsonIgnore
    private static final List<String> properties = Arrays.stream(RaceResultDTO.class.getDeclaredFields())
            .map(Field::getName)
            .toList();
}
