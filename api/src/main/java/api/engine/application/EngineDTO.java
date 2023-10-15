package api.engine.application;

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
public class EngineDTO {
    private UUID engineId;

    @NotNull
    private String manufacturer;

    @Getter
    @JsonIgnore
    private static final List<String> properties = Arrays.stream(EngineDTO.class.getDeclaredFields())
            .map(Field::getName)
            .toList();
}
