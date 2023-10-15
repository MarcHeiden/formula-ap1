package api.driverofrace.application;

import com.fasterxml.jackson.annotation.JsonProperty;
import api.driver.application.DriverDTO;
import api.engine.application.EngineDTO;
import api.team.application.TeamDTO;
import api.validation.OnCreate;
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
