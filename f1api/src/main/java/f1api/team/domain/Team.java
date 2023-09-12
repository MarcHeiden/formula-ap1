package f1api.team.domain;

import f1api.baseentity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Table(name = "Team", schema = "public")
public class Team extends BaseEntity {
    @Setter
    @NotNull
    @Column(name = "name", unique = true, nullable = false)
    private String name;
}
