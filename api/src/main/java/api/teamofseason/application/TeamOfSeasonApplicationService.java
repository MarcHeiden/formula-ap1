package api.teamofseason.application;

import static api.queryparameter.QueryParameterHandler.handleQueryParameters;
import static java.util.Map.entry;

import api.driver.application.DriverApplicationService;
import api.driver.application.DriverDTO;
import api.driver.application.DriverMapper;
import api.driver.domain.Driver;
import api.engine.application.EngineApplicationService;
import api.engine.application.EngineDTO;
import api.engine.application.EngineMapper;
import api.engine.domain.Engine;
import api.exception.ApiInstanceAlreadyExistsException;
import api.exception.ApiInvalidPropertyException;
import api.exception.ApiNotFoundException;
import api.queryparameter.DefaultQueryParameter;
import api.queryparameter.sort.DefaultSortPropertyMapper;
import api.responsepage.ResponsePage;
import api.season.application.SeasonApplicationService;
import api.season.application.SeasonDTO;
import api.season.application.SeasonMapper;
import api.season.application.SeasonSortPropertyMapper;
import api.season.domain.Season;
import api.team.application.TeamApplicationService;
import api.team.application.TeamDTO;
import api.team.application.TeamMapper;
import api.team.application.TeamSortPropertyMapper;
import api.team.domain.Team;
import api.teamofseason.domain.TeamOfSeason;
import api.teamofseason.domain.TeamOfSeasonRepository;
import java.util.Map;
import java.util.Optional;
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
    private final DefaultSortPropertyMapper defaultSortPropertyMapper;
    private final SeasonApplicationService seasonApplicationService;
    private final TeamApplicationService teamApplicationService;
    private final DriverApplicationService driverApplicationService;
    private final EngineApplicationService engineApplicationService;
    private final TeamOfSeasonQueryParameter teamOfSeasonQueryParameter;
    private final DefaultQueryParameter defaultQueryParameter;
    private final TeamMapper teamMapper;
    private final TeamSortPropertyMapper teamSortPropertyMapper;
    private final DriverMapper driverMapper;
    private final EngineMapper engineMapper;
    private final SeasonSortPropertyMapper seasonSortPropertyMapper;
    private final SeasonMapper seasonMapper;

    @Autowired
    public TeamOfSeasonApplicationService(
            TeamOfSeasonRepository teamOfSeasonRepository,
            TeamOfSeasonMapper teamOfSeasonMapper,
            DefaultSortPropertyMapper defaultSortPropertyMapper,
            SeasonApplicationService seasonApplicationService,
            TeamApplicationService teamApplicationService,
            DriverApplicationService driverApplicationService,
            EngineApplicationService engineApplicationService,
            TeamOfSeasonQueryParameter teamOfSeasonQueryParameter,
            DefaultQueryParameter defaultQueryParameter,
            TeamMapper teamMapper,
            TeamSortPropertyMapper teamSortPropertyMapper,
            DriverMapper driverMapper,
            EngineMapper engineMapper,
            SeasonSortPropertyMapper seasonSortPropertyMapper,
            SeasonMapper seasonMapper) {
        this.teamOfSeasonRepository = teamOfSeasonRepository;
        this.teamOfSeasonMapper = teamOfSeasonMapper;
        this.defaultSortPropertyMapper = defaultSortPropertyMapper;
        this.seasonApplicationService = seasonApplicationService;
        this.teamApplicationService = teamApplicationService;
        this.driverApplicationService = driverApplicationService;
        this.engineApplicationService = engineApplicationService;
        this.teamOfSeasonQueryParameter = teamOfSeasonQueryParameter;
        this.defaultQueryParameter = defaultQueryParameter;
        this.teamMapper = teamMapper;
        this.teamSortPropertyMapper = teamSortPropertyMapper;
        this.driverMapper = driverMapper;
        this.engineMapper = engineMapper;
        this.seasonSortPropertyMapper = seasonSortPropertyMapper;
        this.seasonMapper = seasonMapper;
    }

    private void checkIfTeamOfSeasonAlreadyExists(Season season, Team team) {
        if (teamOfSeasonRepository.existsTeamOfSeasonBySeasonAndTeam(season, team)) {
            throw ApiInstanceAlreadyExistsException.of(
                    "TeamOfSeason",
                    Map.ofEntries(
                            entry("seasonId", season.getId().toString()),
                            entry("teamId", team.getId().toString())));
        }
    }

    public Boolean checkIfDriverIsInTeamOfSeason(Driver driver, Team team, Season season) {
        return teamOfSeasonRepository.existsTeamOfSeasonBySeasonAndTeamAndDriversContaining(season, team, driver);
    }

    private Set<Driver> getDrivers(TeamOfSeasonDTO teamOfSeasonDTO) {
        try {
            return teamOfSeasonDTO.getDriverIds().stream()
                    .map(driverApplicationService::getDriverById)
                    .collect(Collectors.toSet());
        } catch (ApiNotFoundException apiNotFoundException) {
            throw new ApiInvalidPropertyException(apiNotFoundException.getMessage());
        }
    }

    private Pageable handleDriverQueryParameters(MultiValueMap<String, String> parameters, Pageable pageable) {
        return handleQueryParameters(
                parameters,
                defaultQueryParameter,
                pageable,
                defaultSortPropertyMapper,
                DriverDTO.getProperties(),
                "driver");
    }

    private Pageable handleTeamQueryParameters(MultiValueMap<String, String> parameters, Pageable pageable) {
        return handleQueryParameters(
                parameters, defaultQueryParameter, pageable, teamSortPropertyMapper, TeamDTO.getProperties(), "team");
    }

    private Pageable handleEngineQueryParameters(MultiValueMap<String, String> parameters, Pageable pageable) {
        return handleQueryParameters(
                parameters,
                defaultQueryParameter,
                pageable,
                defaultSortPropertyMapper,
                EngineDTO.getProperties(),
                "engine");
    }

    private Pageable handleSeasonQueryParameters(MultiValueMap<String, String> parameters, Pageable pageable) {
        return handleQueryParameters(
                parameters,
                defaultQueryParameter,
                pageable,
                seasonSortPropertyMapper,
                SeasonDTO.getProperties(),
                "season");
    }

    private void throwApiNotFoundExceptionForTeamNotInSeason(UUID seasonId, UUID teamId) throws ApiNotFoundException {
        throw ApiNotFoundException.of("Season", "seasonId", seasonId.toString(), "Team", "teamId", teamId.toString());
    }

    public TeamOfSeasonDTO createTeamOfSeason(TeamOfSeasonDTO teamOfSeasonDTO) {
        Season season;
        Team team;
        Engine engine;
        try {
            season = seasonApplicationService.getSeasonById(teamOfSeasonDTO.getSeasonId());
            team = teamApplicationService.getTeamById(teamOfSeasonDTO.getTeamId());
            engine = engineApplicationService.getEngineById(teamOfSeasonDTO.getEngineId());
        } catch (ApiNotFoundException apiNotFoundException) {
            throw new ApiInvalidPropertyException(apiNotFoundException.getMessage());
        }
        Set<Driver> drivers = getDrivers(teamOfSeasonDTO);
        checkIfTeamOfSeasonAlreadyExists(season, team);
        TeamOfSeason teamOfSeason = new TeamOfSeason(season, team, drivers, engine);
        teamOfSeasonRepository.save(teamOfSeason);
        return teamOfSeasonMapper.toTeamOfSeasonDTO(teamOfSeason);
    }

    public ResponsePage<TeamOfSeasonDTO> getTeamsOfSeasons(
            Pageable pageable, UUID seasonId, UUID teamId, MultiValueMap<String, String> parameters) {
        Pageable handledPageable = handleQueryParameters(
                parameters,
                teamOfSeasonQueryParameter,
                pageable,
                defaultSortPropertyMapper,
                TeamOfSeasonDTO.getProperties());
        Page<TeamOfSeason> teamsOfSeasons;
        if (seasonId != null && teamId != null) {
            teamsOfSeasons =
                    teamOfSeasonRepository.findTeamOfSeasonBySeasonIdAndTeamId(handledPageable, seasonId, teamId);
        } else if (seasonId != null) {
            teamsOfSeasons = teamOfSeasonRepository.findTeamOfSeasonsBySeasonId(handledPageable, seasonId);
        } else if (teamId != null) {
            teamsOfSeasons = teamOfSeasonRepository.findTeamOfSeasonsByTeamId(handledPageable, teamId);
        } else {
            teamsOfSeasons = teamOfSeasonRepository.findAll(handledPageable);
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

    public TeamOfSeasonDTO addDriversToTeamOfSeason(UUID teamOfSeasonId, TeamOfSeasonDTO teamOfSeasonDTO) {
        TeamOfSeason teamOfSeason = getTeamOfSeasonById(teamOfSeasonId);
        teamOfSeason.addDrivers(getDrivers(teamOfSeasonDTO));
        teamOfSeasonRepository.save(teamOfSeason);
        return teamOfSeasonMapper.toTeamOfSeasonDTO(teamOfSeason);
    }

    public ResponsePage<TeamDTO> getTeamsOfSeason(
            UUID seasonId, Pageable pageable, MultiValueMap<String, String> parameters) {
        Season season = seasonApplicationService.getSeasonById(seasonId);
        Pageable handledPageable = handleTeamQueryParameters(parameters, pageable);
        return ResponsePage.of(teamOfSeasonRepository
                .findTeamsBySeason(handledPageable, season)
                .map(teamMapper::toTeamDTO));
    }

    public ResponsePage<DriverDTO> getDriversOfTeamOfSeason(
            UUID seasonId, UUID teamId, Pageable pageable, MultiValueMap<String, String> parameters) {
        Season season = seasonApplicationService.getSeasonById(seasonId);
        Team team = teamApplicationService.getTeamById(teamId);
        Pageable handledPageable = handleDriverQueryParameters(parameters, pageable);
        Page<Driver> drivers = teamOfSeasonRepository.findDriversBySeasonAndTeam(handledPageable, season, team);
        if (drivers.isEmpty()) {
            throwApiNotFoundExceptionForTeamNotInSeason(seasonId, teamId);
        }
        return ResponsePage.of(drivers.map(driverMapper::toDriverDTO));
    }

    public Engine getEngineOfTeamOfSeason(Season season, Team team) {
        Optional<Engine> engine = teamOfSeasonRepository.findEngineBySeasonAndTeam(season, team);
        if (engine.isEmpty()) {
            throwApiNotFoundExceptionForTeamNotInSeason(season.getId(), team.getId());
        }
        return engine.get();
    }

    public EngineDTO getEngineOfTeamOfSeason(UUID seasonId, UUID teamId) {
        Season season = seasonApplicationService.getSeasonById(seasonId);
        Team team = teamApplicationService.getTeamById(teamId);
        return engineMapper.toEngineDTO(getEngineOfTeamOfSeason(season, team));
    }

    public ResponsePage<DriverDTO> getDriversOfSeason(
            UUID seasonId, Pageable pageable, MultiValueMap<String, String> parameters) {
        Season season = seasonApplicationService.getSeasonById(seasonId);
        Pageable handledPageable = handleDriverQueryParameters(parameters, pageable);
        return ResponsePage.of(teamOfSeasonRepository
                .findDriversBySeason(handledPageable, season)
                .map(driverMapper::toDriverDTO));
    }

    public ResponsePage<TeamDTO> getTeamsOfDriverOfSeason(
            UUID seasonId, UUID driverId, Pageable pageable, MultiValueMap<String, String> parameters) {
        Season season = seasonApplicationService.getSeasonById(seasonId);
        Driver driver = driverApplicationService.getDriverById(driverId);
        Pageable handledPageable = handleTeamQueryParameters(parameters, pageable);
        Page<Team> teams = teamOfSeasonRepository.findTeamsBySeasonAndDriver(handledPageable, season, driver);
        if (teams.isEmpty()) {
            throw ApiNotFoundException.of(
                    "Season", "seasonId", seasonId.toString(), "Driver", "driverId", driverId.toString());
        }
        return ResponsePage.of(teams.map(teamMapper::toTeamDTO));
    }

    public ResponsePage<EngineDTO> getEnginesOfSeason(
            UUID seasonId, Pageable pageable, MultiValueMap<String, String> parameters) {
        Season season = seasonApplicationService.getSeasonById(seasonId);
        Pageable handledPageable = handleEngineQueryParameters(parameters, pageable);
        return ResponsePage.of(teamOfSeasonRepository
                .findEnginesBySeason(handledPageable, season)
                .map(engineMapper::toEngineDTO));
    }

    public ResponsePage<TeamDTO> getTeamsOfEngineOfSeason(
            UUID seasonId, UUID engineId, Pageable pageable, MultiValueMap<String, String> parameters) {
        Season season = seasonApplicationService.getSeasonById(seasonId);
        Engine engine = engineApplicationService.getEngineById(engineId);
        Pageable handledPageable = handleTeamQueryParameters(parameters, pageable);
        Page<Team> teams = teamOfSeasonRepository.findTeamsBySeasonAndEngine(handledPageable, season, engine);
        if (teams.isEmpty()) {
            throw ApiNotFoundException.of(
                    "Season", "seasonId", seasonId.toString(), "Engine", "engineId", engineId.toString());
        }
        return ResponsePage.of(teams.map(teamMapper::toTeamDTO));
    }

    public ResponsePage<DriverDTO> getDriversOfTeam(
            UUID teamId, Pageable pageable, MultiValueMap<String, String> parameters) {
        Team team = teamApplicationService.getTeamById(teamId);
        Pageable handledPageable = handleDriverQueryParameters(parameters, pageable);
        return ResponsePage.of(
                teamOfSeasonRepository.findDriversByTeam(handledPageable, team).map(driverMapper::toDriverDTO));
    }

    public ResponsePage<SeasonDTO> getSeasonsOfDriverOfTeam(
            UUID teamId, UUID driverId, Pageable pageable, MultiValueMap<String, String> parameters) {
        Team team = teamApplicationService.getTeamById(teamId);
        Driver driver = driverApplicationService.getDriverById(driverId);
        Pageable handledPageable = handleSeasonQueryParameters(parameters, pageable);
        Page<Season> seasons = teamOfSeasonRepository.findSeasonsByTeamAndDriver(handledPageable, team, driver);
        if (seasons.isEmpty()) {
            throw ApiNotFoundException.of(
                    "Team", "teamId", teamId.toString(), "Driver", "driverId", driverId.toString());
        }
        return ResponsePage.of(seasons.map(seasonMapper::toSeasonDTO));
    }

    public ResponsePage<EngineDTO> getEnginesOfTeam(
            UUID teamId, Pageable pageable, MultiValueMap<String, String> parameters) {
        Team team = teamApplicationService.getTeamById(teamId);
        Pageable handledPageable = handleEngineQueryParameters(parameters, pageable);
        return ResponsePage.of(
                teamOfSeasonRepository.findEnginesByTeam(handledPageable, team).map(engineMapper::toEngineDTO));
    }

    public ResponsePage<SeasonDTO> getSeasonsOfEngineOfTeam(
            UUID teamId, UUID engineId, Pageable pageable, MultiValueMap<String, String> parameters) {
        Team team = teamApplicationService.getTeamById(teamId);
        Engine engine = engineApplicationService.getEngineById(engineId);
        Pageable handledPageable = handleSeasonQueryParameters(parameters, pageable);
        Page<Season> seasons = teamOfSeasonRepository.findSeasonsByTeamAndEngine(handledPageable, team, engine);
        if (seasons.isEmpty()) {
            throw ApiNotFoundException.of(
                    "Team", "teamId", teamId.toString(), "Engine", "engineId", engineId.toString());
        }
        return ResponsePage.of(seasons.map(seasonMapper::toSeasonDTO));
    }

    public ResponsePage<SeasonDTO> getSeasonsOfTeam(
            UUID teamId, Pageable pageable, MultiValueMap<String, String> parameters) {
        Team team = teamApplicationService.getTeamById(teamId);
        Pageable handledPageable = handleSeasonQueryParameters(parameters, pageable);
        return ResponsePage.of(
                teamOfSeasonRepository.findSeasonsByTeam(handledPageable, team).map(seasonMapper::toSeasonDTO));
    }

    public ResponsePage<TeamDTO> getTeamsOfDriver(
            UUID driverId, Pageable pageable, MultiValueMap<String, String> parameters) {
        Driver driver = driverApplicationService.getDriverById(driverId);
        Pageable handledPageable = handleTeamQueryParameters(parameters, pageable);
        return ResponsePage.of(teamOfSeasonRepository
                .findTeamsByDriver(handledPageable, driver)
                .map(teamMapper::toTeamDTO));
    }

    public ResponsePage<SeasonDTO> getSeasonsOfDriver(
            UUID driverId, Pageable pageable, MultiValueMap<String, String> parameters) {
        Driver driver = driverApplicationService.getDriverById(driverId);
        Pageable handledPageable = handleSeasonQueryParameters(parameters, pageable);
        return ResponsePage.of(teamOfSeasonRepository
                .findSeasonsByDriver(handledPageable, driver)
                .map(seasonMapper::toSeasonDTO));
    }

    public ResponsePage<TeamDTO> getTeamsOfEngine(
            UUID engineId, Pageable pageable, MultiValueMap<String, String> parameters) {
        Engine engine = engineApplicationService.getEngineById(engineId);
        Pageable handledPageable = handleTeamQueryParameters(parameters, pageable);
        return ResponsePage.of(teamOfSeasonRepository
                .findTeamsByEngine(handledPageable, engine)
                .map(teamMapper::toTeamDTO));
    }

    public ResponsePage<SeasonDTO> getSeasonsOfEngine(
            UUID engineId, Pageable pageable, MultiValueMap<String, String> parameters) {
        Engine engine = engineApplicationService.getEngineById(engineId);
        Pageable handledPageable = handleSeasonQueryParameters(parameters, pageable);
        return ResponsePage.of(teamOfSeasonRepository
                .findSeasonsByEngine(handledPageable, engine)
                .map(seasonMapper::toSeasonDTO));
    }
}
