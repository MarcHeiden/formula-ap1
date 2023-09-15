package f1api.team.application;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TeamDTO {
    private UUID teamId;

    @NotNull
    private String teamName;

    @Getter
    @JsonIgnore
    private static final List<String> properties =
            Arrays.stream(TeamDTO.class.getDeclaredFields()).map(Field::getName).toList();
}
