package api.driver.domain;

import api.baseentity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Setter
@Table(
        name = "Driver",
        schema = "public",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uniqueName",
                    columnNames = {"firstName", "lastName"})
        })
public class Driver extends BaseEntity {
    @NotNull
    @Column(name = "firstName", nullable = false)
    private String firstName;

    @NotNull
    @Column(name = "lastName", nullable = false)
    private String lastName;
}
