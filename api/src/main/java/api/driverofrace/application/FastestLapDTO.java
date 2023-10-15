package api.driverofrace.application;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
public class FastestLapDTO {
    @Pattern(regexp = "^([0-9]):([0-5][0-9])\\.([0-9]{3})$")
    @NotNull
    private String time;

    @JsonUnwrapped
    @Valid
    private DriverInfoDTO driverInfoDTO;

    @Getter
    @JsonIgnore
    private static final List<String> properties = Arrays.stream(FastestLapDTO.class.getDeclaredFields())
            .map(Field::getName)
            .toList();
}
