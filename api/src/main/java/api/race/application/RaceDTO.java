package api.race.application;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import api.season.application.SeasonDTO;
import api.validation.OnCreate;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RaceDTO {
    private UUID raceId;

    @NotNull(groups = OnCreate.class)
    private String raceName;

    @NotNull(groups = OnCreate.class)
    private LocalDate date;

    @Pattern(regexp = "^([0-1][0-9]|2[0-3])(:[0-5][0-9]){2}Z?$")
    @NotNull(groups = OnCreate.class)
    private String time;

    private Boolean cancelled;

    @JsonProperty("season")
    private SeasonDTO seasonDTO;

    @JsonIgnore
    public Boolean isEmptyOnUpdate() {
        return raceName == null && date == null && time == null && cancelled == null;
    }

    @Getter
    @JsonIgnore
    private static final Set<String> notNullPropertiesOnUpdate = Set.of("raceName", "date", "time", "cancelled");

    @Getter
    @JsonIgnore
    private static final List<String> properties =
            Arrays.stream(RaceDTO.class.getDeclaredFields()).map(Field::getName).toList();
}
