package api.driverofrace.domain;

import api.baseentity.BaseEntity;
import api.driver.domain.Driver;
import api.race.domain.Race;
import api.team.domain.Team;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Table(
        name = "DriverOfRace",
        schema = "public",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uniqueDriverOfRace",
                    columnNames = {"raceId", "driverId"})
        })
public class DriverOfRace extends BaseEntity {
    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "raceId", referencedColumnName = "id", nullable = false)
    private Race race;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "driverId", referencedColumnName = "id", nullable = false)
    private Driver driver;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "teamId", referencedColumnName = "id", nullable = false)
    private Team team;

    @Setter
    @NotNull
    @AttributeOverride(name = "position", column = @Column(name = "qualifyingResult", nullable = false))
    @Embedded
    private QualifyingResult qualifyingResult;

    @Setter
    @AttributeOverrides({
        @AttributeOverride(name = "position", column = @Column(name = "raceResult")),
        @AttributeOverride(name = "dnf", column = @Column(name = "raceDNF"))
    })
    @Embedded
    private RaceResult raceResult;

    @Setter
    @AttributeOverride(name = "time", column = @Column(name = "fastestLap"))
    @Embedded
    private FastestLap fastestLap;

    @Setter
    @AttributeOverride(name = "duration", column = @Column(name = "fastestPitStop"))
    @Embedded
    private FastestPitStop fastestPitStop;

    @Setter
    @AttributeOverride(name = "speed", column = @Column(name = "topSpeed"))
    @Embedded
    private TopSpeed topSpeed;

    @Setter
    @AttributeOverride(name = "numberOfLaps", column = @Column(name = "leadingLaps"))
    @Embedded
    private LeadingLaps leadingLaps;

    public DriverOfRace(Race race, Driver driver, Team team, QualifyingResult qualifyingResult) {
        this.race = race;
        this.driver = driver;
        this.team = team;
        this.qualifyingResult = qualifyingResult;
    }
}
