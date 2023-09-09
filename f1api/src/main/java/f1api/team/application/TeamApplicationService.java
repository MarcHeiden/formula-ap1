package f1api.team.application;

import static f1api.queryparameter.QueryParameterHandler.handleQueryParameters;

import f1api.exception.ApiInstanceAlreadyExistsException;
import f1api.exception.ApiNotFoundException;
import f1api.responsepage.ResponsePage;
import f1api.team.domain.Team;
import f1api.team.domain.TeamRepository;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

@Service
public class TeamApplicationService {
    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;
    private final TeamSortPropertyMapper teamSortPropertyMapper;

    @Autowired
    public TeamApplicationService(
            TeamRepository teamRepository, TeamMapper teamMapper, TeamSortPropertyMapper teamSortPropertyMapper) {
        this.teamRepository = teamRepository;
        this.teamMapper = teamMapper;
        this.teamSortPropertyMapper = teamSortPropertyMapper;
    }

    private void checkIfTeamAlreadyExists(String name) {
        if (teamRepository.existsTeamByName(name)) {
            throw ApiInstanceAlreadyExistsException.of("Team", "teamName", name);
        }
    }

    public TeamDTO createTeam(TeamDTO teamDTO) {
        checkIfTeamAlreadyExists(teamDTO.getTeamName());
        Team team = teamMapper.toTeam(teamDTO);
        teamRepository.save(team);
        return teamMapper.toTeamDTO(team);
    }

    public ResponsePage<TeamDTO> getTeams(
            Pageable pageable, String teamName, MultiValueMap<String, String> parameters) {
        Pageable handledPageable = handleQueryParameters(pageable, teamSortPropertyMapper, parameters, TeamDTO.class);
        Page<Team> teams;
        if (teamName != null) {
            teams = teamRepository.findTeamByName(handledPageable, teamName);
        } else {
            teams = teamRepository.findAll(handledPageable);
        }
        return ResponsePage.of(teams.map(teamMapper::toTeamDTO));
    }

    private Team getTeamById(UUID id) {
        return teamRepository.findById(id).orElseThrow(() -> ApiNotFoundException.of("Team", "teamId", id.toString()));
    }

    public TeamDTO getTeam(UUID teamId) {
        return teamMapper.toTeamDTO(getTeamById(teamId));
    }

    public TeamDTO updateTeam(UUID teamId, TeamDTO teamDTO) {
        Team team = getTeamById(teamId);
        checkIfTeamAlreadyExists(teamDTO.getTeamName());
        team.setName(teamDTO.getTeamName());
        teamRepository.save(team);
        return teamMapper.toTeamDTO(team);
    }
}
