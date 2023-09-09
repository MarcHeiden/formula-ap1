package f1api.team.application;

import f1api.team.domain.Team;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TeamMapper {
    @Mapping(target = "teamId", source = "id")
    @Mapping(target = "teamName", source = "name")
    TeamDTO toTeamDTO(Team team);

    @Mapping(target = "name", source = "teamName")
    Team toTeam(TeamDTO teamDTO);
}
