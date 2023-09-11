package f1api.driver.domain;

import f1api.baseentity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    public Driver(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
