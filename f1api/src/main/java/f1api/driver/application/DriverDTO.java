package f1api.driver.application;

import com.fasterxml.jackson.annotation.JsonIgnore;
import f1api.validation.OnCreate;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private static final List<String> notNullProperties = List.of("firstName", "lastName");
}
