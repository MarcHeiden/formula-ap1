package api.driverofrace.application;

import api.driver.application.DriverMapper;
import api.driverofrace.domain.DriverOfRace;
import api.engine.application.EngineMapper;
import api.engine.domain.Engine;
import api.team.application.TeamMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {DriverMapper.class, TeamMapper.class, EngineMapper.class})
public interface DriverOfRaceMapper {
    @Mapping(target = "driverDTO", source = "driverOfRace.driver", qualifiedByName = "toDriverDTO")
    @Mapping(target = "teamDTO", source = "driverOfRace.team", qualifiedByName = "toTeamDTO")
    @Mapping(target = "engineDTO", source = "engine", qualifiedByName = "toEngineDTO")
    DriverInfoDTO toDriverInfoDTO(DriverOfRace driverOfRace, Engine engine);
}
