package f1api.teamofseason.application;

import f1api.driver.domain.Driver;
import f1api.teamofseason.domain.TeamOfSeason;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface TeamOfSeasonMapper {
    @Mapping(target = "teamOfSeasonId", source = "id")
    @Mapping(target = "seasonId", source = "season.id")
    @Mapping(target = "teamId", source = "team.id")
    @Mapping(target = "engineId", source = "engine.id")
    @Mapping(target = "driverIds", source = "drivers", qualifiedByName = "getDriverIds")
    TeamOfSeasonDTO toTeamOfSeasonDTO(TeamOfSeason teamOfSeason);

    @Named("getDriverIds")
    default Set<UUID> getDriverIds(Set<Driver> drivers) {
        return drivers.stream().map(Driver::getId).collect(Collectors.toSet());
    }
}
