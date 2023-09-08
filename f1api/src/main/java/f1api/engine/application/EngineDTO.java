package f1api.engine.application;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class EngineDTO {
    private UUID engineId;

    @NotNull
    private String manufacturer;
}
