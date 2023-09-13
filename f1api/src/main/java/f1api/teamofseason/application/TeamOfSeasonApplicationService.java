package f1api.teamofseason.application;

import static f1api.queryparameter.QueryParameterHandler.handleQueryParameters;
import static java.util.Map.entry;

import f1api.driver.application.DriverApplicationService;
import f1api.driver.domain.Driver;
import f1api.engine.application.EngineApplicationService;
import f1api.engine.domain.Engine;
import f1api.exception.ApiInstanceAlreadyExistsException;
import f1api.exception.ApiNotFoundException;
import f1api.exception.ApiPropertyIsNullException;
import f1api.responsepage.ResponsePage;
import f1api.season.application.SeasonApplicationService;
import f1api.season.domain.Season;
import f1api.team.application.TeamApplicationService;
import f1api.team.domain.Team;
import f1api.teamofseason.domain.TeamOfSeason;
import f1api.teamofseason.domain.TeamOfSeasonRepository;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

@Service
public class TeamOfSeasonApplicationService {
    private final TeamOfSeasonRepository teamOfSeasonRepository;
    private final TeamOfSeasonMapper teamOfSeasonMapper;
    private final SeasonApplicationService seasonApplicationService;
    private final TeamApplicationService teamApplicationService;
    private final DriverApplicationService driverApplicationService;
    private final EngineApplicationService engineApplicationService;
    private final TeamOfSeasonQueryParameter teamOfSeasonQueryParameter;

    @Autowired
    public TeamOfSeasonApplicationService(
            TeamOfSeasonRepository teamOfSeasonRepository,
            TeamOfSeasonMapper teamOfSeasonMapper,
            SeasonApplicationService seasonApplicationService,
            TeamApplicationService teamApplicationService,
            DriverApplicationService driverApplicationService,
            EngineApplicationService engineApplicationService,
            TeamOfSeasonQueryParameter teamOfSeasonQueryParameter) {
        this.teamOfSeasonRepository = teamOfSeasonRepository;
        this.teamOfSeasonMapper = teamOfSeasonMapper;
        this.seasonApplicationService = seasonApplicationService;
        this.teamApplicationService = teamApplicationService;
        this.driverApplicationService = driverApplicationService;
        this.engineApplicationService = engineApplicationService;
        this.teamOfSeasonQueryParameter = teamOfSeasonQueryParameter;
    }

    private void throwApiInstanceAlreadyExistsException(UUID seasonId, UUID teamId) {
        throw ApiInstanceAlreadyExistsException.of(
                "TeamOfSeason",
                Map.ofEntries(entry("seasonId", seasonId.toString()), entry("teamId", teamId.toString())));
    }

    private void checkIfTeamOfSeasonAlreadyExists(UUID seasonId, UUID teamId) {
        if (teamOfSeasonRepository.existsTeamOfSeasonBySeasonIdAndTeamId(seasonId, teamId)) {
            throwApiInstanceAlreadyExistsException(seasonId, teamId);
        }
    }

    private void checkIfDifferentTeamOfSeasonAlreadyExists(TeamOfSeason teamOfSeason, UUID seasonId, UUID teamId) {
        teamOfSeasonRepository
                .findTeamOfSeasonBySeasonIdAndTeamId(seasonId, teamId)
                .ifPresent(foundTeamOfSeason -> {
                    if (!teamOfSeason.getId().equals(foundTeamOfSeason.getId())) {
                        throwApiInstanceAlreadyExistsException(seasonId, teamId);
                    }
                });
    }

    private Set<Driver> getDrivers(TeamOfSeasonDTO teamOfSeasonDTO) {
        return teamOfSeasonDTO.getDriverIds().stream()
                .map(driverApplicationService::getDriverById)
                .collect(Collectors.toSet());
    }

    public TeamOfSeasonDTO createTeamOfSeason(TeamOfSeasonDTO teamOfSeasonDTO) {
        checkIfTeamOfSeasonAlreadyExists(teamOfSeasonDTO.getSeasonId(), teamOfSeasonDTO.getTeamId());
        Season season = seasonApplicationService.getSeasonById(teamOfSeasonDTO.getSeasonId());
        Team team = teamApplicationService.getTeamById(teamOfSeasonDTO.getTeamId());
        Set<Driver> drivers = getDrivers(teamOfSeasonDTO);
        Engine engine = engineApplicationService.getEngineById(teamOfSeasonDTO.getEngineId());
        TeamOfSeason teamOfSeason = new TeamOfSeason(season, team, drivers, engine);
        teamOfSeasonRepository.save(teamOfSeason);
        return teamOfSeasonMapper.toTeamOfSeasonDTO(teamOfSeason);
    }

    public ResponsePage<TeamOfSeasonDTO> getTeamsOfSeasons(
            Pageable pageable, UUID seasonId, UUID teamId, MultiValueMap<String, String> parameters) {
        handleQueryParameters(parameters, teamOfSeasonQueryParameter);
        Page<TeamOfSeason> teamsOfSeasons;
        if (seasonId != null && teamId != null) {
            teamsOfSeasons = teamOfSeasonRepository.findTeamOfSeasonBySeasonIdAndTeamId(pageable, seasonId, teamId);
        } else if (seasonId != null) {
            teamsOfSeasons = teamOfSeasonRepository.findTeamOfSeasonsBySeasonId(pageable, seasonId);
        } else if (teamId != null) {
            teamsOfSeasons = teamOfSeasonRepository.findTeamOfSeasonsByTeamId(pageable, teamId);
        } else {
            teamsOfSeasons = teamOfSeasonRepository.findAll(pageable);
        }
        return ResponsePage.of(teamsOfSeasons.map(teamOfSeasonMapper::toTeamOfSeasonDTO));
    }

    private TeamOfSeason getTeamOfSeasonById(UUID id) {
        return teamOfSeasonRepository
                .findById(id)
                .orElseThrow(() -> ApiNotFoundException.of("TeamOfSeason", "teamOfSeasonId", id.toString()));
    }

    public TeamOfSeasonDTO getTeamOfSeason(UUID teamOfSeasonId) {
        return teamOfSeasonMapper.toTeamOfSeasonDTO(getTeamOfSeasonById(teamOfSeasonId));
    }

    public TeamOfSeasonDTO updateTeamOfSeason(UUID teamOfSeasonId, TeamOfSeasonDTO teamOfSeasonDTO) {
        TeamOfSeason teamOfSeason = getTeamOfSeasonById(teamOfSeasonId);
        if (teamOfSeasonDTO.isEmpty()) {
            throw ApiPropertyIsNullException.of(TeamOfSeasonDTO.getNotNullProperties());
        }
        if (teamOfSeasonDTO.getSeasonId() != null && teamOfSeasonDTO.getTeamId() != null) {
            checkIfDifferentTeamOfSeasonAlreadyExists(
                    teamOfSeason, teamOfSeasonDTO.getSeasonId(), teamOfSeasonDTO.getTeamId());
        }
        if (teamOfSeasonDTO.getSeasonId() != null) {
            teamOfSeason.setSeason(seasonApplicationService.getSeasonById(teamOfSeasonDTO.getSeasonId()));
        }
        if (teamOfSeasonDTO.getTeamId() != null) {
            teamOfSeason.setTeam(teamApplicationService.getTeamById(teamOfSeasonDTO.getTeamId()));
        }
        if (teamOfSeasonDTO.getDriverIds() != null) {
            teamOfSeason.setDrivers(getDrivers(teamOfSeasonDTO));
        }
        if (teamOfSeasonDTO.getEngineId() != null) {
            teamOfSeason.setEngine(engineApplicationService.getEngineById(teamOfSeasonDTO.getEngineId()));
        }
        teamOfSeasonRepository.save(teamOfSeason);
        return teamOfSeasonMapper.toTeamOfSeasonDTO(teamOfSeason);
    }
}
