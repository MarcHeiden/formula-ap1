package f1api.driverofrace.application;

import com.fasterxml.jackson.annotation.JsonProperty;
import f1api.driver.application.DriverDTO;
import f1api.engine.application.EngineDTO;
import f1api.team.application.TeamDTO;
import f1api.validation.OnCreate;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DriverInfoDTO {
    @NotNull(groups = OnCreate.class)
    private UUID driverId;

    @JsonProperty("driver")
    private DriverDTO driverDTO;

    @JsonProperty("team")
    private TeamDTO teamDTO;

    @JsonProperty("engine")
    private EngineDTO engineDTO;
}
