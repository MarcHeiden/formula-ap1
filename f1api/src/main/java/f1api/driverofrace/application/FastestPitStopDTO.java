package f1api.driverofrace.application;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FastestPitStopDTO {
    @Pattern(regexp = "^([0-5][0-9])\\.([0-9]{3})$")
    @NotNull
    private String duration;

    @JsonUnwrapped
    @Valid
    private DriverInfoDTO driverInfoDTO;
}
