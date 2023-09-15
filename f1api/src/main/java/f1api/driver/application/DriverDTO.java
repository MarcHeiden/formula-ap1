package f1api.driver.application;

import com.fasterxml.jackson.annotation.JsonIgnore;
import f1api.validation.OnCreate;
import jakarta.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DriverDTO {
    private UUID driverId;

    @NotNull(groups = OnCreate.class)
    private String firstName;

    @NotNull(groups = OnCreate.class)
    private String lastName;

    @JsonIgnore
    public Boolean isEmpty() {
        return firstName == null && lastName == null;
    }

    @Getter
    @JsonIgnore
    private static final Set<String> notNullProperties = Set.of("firstName", "lastName");

    @Getter
    @JsonIgnore
    private static final List<String> properties = Arrays.stream(DriverDTO.class.getDeclaredFields())
            .map(Field::getName)
            .toList();
}
