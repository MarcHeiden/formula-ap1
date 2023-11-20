package api.race.domain;

import api.baseentity.BaseEntity;
import api.season.domain.Season;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Table(
        name = "Race",
        schema = "public",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uniqueRaceOfSeason",
                    columnNames = {"name", "seasonId"})
        })
public class Race extends BaseEntity {
    @Setter
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Setter
    @NotNull
    @Column(name = "date", unique = true, nullable = false)
    private LocalDate date;

    @Setter
    @NotNull
    @Column(name = "time", nullable = false)
    private LocalTime time;

    @Setter
    @NotNull
    @Column(name = "cancelled", nullable = false)
    private Boolean cancelled = false;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "seasonId", referencedColumnName = "id", nullable = false)
    private Season season;
}
