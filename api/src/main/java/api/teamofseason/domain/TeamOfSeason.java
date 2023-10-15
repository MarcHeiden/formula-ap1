package api.teamofseason.domain;

import api.baseentity.BaseEntity;
import api.driver.domain.Driver;
import api.engine.domain.Engine;
import api.season.domain.Season;
import api.team.domain.Team;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Table(
        name = "TeamOfSeason",
        schema = "public",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uniqueTeamOfSeason",
                    columnNames = {"seasonId", "teamId"})
        })
public class TeamOfSeason extends BaseEntity {
    @NotNull
    /*@OnDelete(action = OnDeleteAction.CASCADE)*/
    @ManyToOne(optional = false)
    @JoinColumn(name = "seasonId", referencedColumnName = "id", nullable = false)
    private Season season;

    @NotNull
    /*@OnDelete(action = OnDeleteAction.CASCADE)*/
    @ManyToOne(optional = false)
    @JoinColumn(name = "teamId", referencedColumnName = "id", nullable = false)
    private Team team;

    @Size(min = 2)
    @ManyToMany()
    private Set<Driver> drivers;

    @NotNull
    /*@OnDelete(action = OnDeleteAction.CASCADE)*/
    @ManyToOne(optional = false)
    @JoinColumn(name = "engineId", referencedColumnName = "id", nullable = false)
    private Engine engine;

    public void addDrivers(Set<Driver> drivers) {
        this.drivers.addAll(drivers);
    }
}
