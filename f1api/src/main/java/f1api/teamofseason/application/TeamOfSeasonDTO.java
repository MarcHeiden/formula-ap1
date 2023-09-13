package f1api.teamofseason.application;

import com.fasterxml.jackson.annotation.JsonIgnore;
import f1api.validation.OnCreate;
import f1api.validation.OnUpdate;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TeamOfSeasonDTO {
    private UUID teamOfSeasonId;

    @NotNull(groups = OnCreate.class)
    private UUID seasonId;

    @NotNull(groups = OnCreate.class)
    private UUID teamId;

    @NotNull(groups = OnCreate.class)
    private UUID engineId;

    @Size(
            min = 2,
            groups = {OnCreate.class, OnUpdate.class})
    @NotNull(groups = OnCreate.class)
    private Set<UUID> driverIds;

    @JsonIgnore
    public Boolean isEmpty() {
        return seasonId == null && teamId == null && engineId == null && driverIds == null;
    }

    @Getter
    @JsonIgnore
    private static final Set<String> notNullProperties = Set.of("seasonId", "teamId", "engineId", "driverIds");
}
