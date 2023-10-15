package api.team.application;

import static api.queryparameter.QueryParameterHandler.handleQueryParameters;

import api.exception.ApiInstanceAlreadyExistsException;
import api.exception.ApiNotFoundException;
import api.responsepage.ResponsePage;
import api.team.domain.Team;
import api.team.domain.TeamRepository;
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

    private final TeamQueryParameter teamQueryParameter;

    @Autowired
    public TeamApplicationService(
            TeamRepository teamRepository,
            TeamMapper teamMapper,
            TeamSortPropertyMapper teamSortPropertyMapper,
            TeamQueryParameter teamQueryParameter) {
        this.teamRepository = teamRepository;
        this.teamMapper = teamMapper;
        this.teamSortPropertyMapper = teamSortPropertyMapper;
        this.teamQueryParameter = teamQueryParameter;
    }

    private void throwApiInstanceAlreadyExistsException(String name) {
        throw ApiInstanceAlreadyExistsException.of("Team", "teamName", name);
    }

    private void checkIfTeamAlreadyExists(String name) {
        if (teamRepository.existsTeamByName(name)) {
            throwApiInstanceAlreadyExistsException(name);
        }
    }

    private void checkIfDifferentTeamAlreadyExists(Team team, String name) {
        teamRepository.findTeamByName(name).ifPresent(foundTeam -> {
            if (!team.getId().equals(foundTeam.getId())) {
                throwApiInstanceAlreadyExistsException(name);
            }
        });
    }

    public TeamDTO createTeam(TeamDTO teamDTO) {
        checkIfTeamAlreadyExists(teamDTO.getTeamName());
        Team team = teamMapper.toTeam(teamDTO);
        teamRepository.save(team);
        return teamMapper.toTeamDTO(team);
    }

    public ResponsePage<TeamDTO> getTeams(
            Pageable pageable, String teamName, MultiValueMap<String, String> parameters) {
        Pageable handledPageable = handleQueryParameters(
                parameters, teamQueryParameter, pageable, teamSortPropertyMapper, TeamDTO.getProperties());
        Page<Team> teams;
        if (teamName != null) {
            teams = teamRepository.findTeamByName(handledPageable, teamName);
        } else {
            teams = teamRepository.findAll(handledPageable);
        }
        return ResponsePage.of(teams.map(teamMapper::toTeamDTO));
    }

    public Team getTeamById(UUID id) {
        return teamRepository.findById(id).orElseThrow(() -> ApiNotFoundException.of("Team", "teamId", id.toString()));
    }

    public TeamDTO getTeam(UUID teamId) {
        return teamMapper.toTeamDTO(getTeamById(teamId));
    }

    public TeamDTO updateTeam(UUID teamId, TeamDTO teamDTO) {
        Team team = getTeamById(teamId);
        checkIfDifferentTeamAlreadyExists(team, teamDTO.getTeamName());
        team.setName(teamDTO.getTeamName());
        teamRepository.save(team);
        return teamMapper.toTeamDTO(team);
    }
}
