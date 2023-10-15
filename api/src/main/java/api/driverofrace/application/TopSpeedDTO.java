package api.driverofrace.application;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
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
public class TopSpeedDTO {
    @DecimalMin("0.0")
    @NotNull
    private Double speed;

    @JsonUnwrapped
    @Valid
    private DriverInfoDTO driverInfoDTO;

    @Getter
    @JsonIgnore
    private static final List<String> properties = Arrays.stream(TopSpeedDTO.class.getDeclaredFields())
            .map(Field::getName)
            .toList();
}
