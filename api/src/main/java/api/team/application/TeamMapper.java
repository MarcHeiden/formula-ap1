package api.team.application;

import api.team.domain.Team;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface TeamMapper {
    @Named("toTeamDTO")
    @Mapping(target = "teamId", source = "id")
    @Mapping(target = "teamName", source = "name")
    TeamDTO toTeamDTO(Team team);

    @Mapping(target = "name", source = "teamName")
    Team toTeam(TeamDTO teamDTO);
}
