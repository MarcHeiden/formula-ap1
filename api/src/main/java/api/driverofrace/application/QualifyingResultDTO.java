package api.driverofrace.application;

import api.validation.OnCreate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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
public class QualifyingResultDTO {
    @Min(1)
    @NotNull
    private Integer position;

    @NotNull(groups = OnCreate.class)
    private UUID teamId;

    @JsonUnwrapped
    @Valid
    private DriverInfoDTO driverInfoDTO;

    @Getter
    @JsonIgnore
    private static final List<String> properties = Arrays.stream(QualifyingResultDTO.class.getDeclaredFields())
            .map(Field::getName)
            .toList();
}
