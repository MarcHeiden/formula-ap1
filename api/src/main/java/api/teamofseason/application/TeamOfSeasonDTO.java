package api.teamofseason.application;

import api.validation.OnCreate;
import api.validation.OnUpdate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
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

    @NotEmpty(groups = OnUpdate.class)
    @Size(min = 2, groups = OnCreate.class)
    @NotNull
    private Set<UUID> driverIds;

    @Getter
    @JsonIgnore
    private static final List<String> properties = Arrays.stream(TeamOfSeasonDTO.class.getDeclaredFields())
            .map(Field::getName)
            .toList();
}
