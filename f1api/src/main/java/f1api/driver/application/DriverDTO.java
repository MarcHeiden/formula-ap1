package f1api.driver.application;

import f1api.validation.OnCreate;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
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
}
