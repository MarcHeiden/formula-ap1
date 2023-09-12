package f1api.driver.domain;

import f1api.baseentity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Table(
        name = "Driver",
        schema = "public",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uniqueName",
                    columnNames = {"firstName", "lastName"})
        })
public class Driver extends BaseEntity {
    @Setter
    @NotNull
    @Column(name = "firstName", nullable = false)
    private String firstName;

    @Setter
    @NotNull
    @Column(name = "lastName", nullable = false)
    private String lastName;
}
