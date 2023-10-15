package api.season.domain;

import api.baseentity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.Year;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Setter
@Table(name = "Season", schema = "public")
public class Season extends BaseEntity {
    @NotNull
    @Column(name = "year", unique = true, nullable = false)
    private Year year;
}
