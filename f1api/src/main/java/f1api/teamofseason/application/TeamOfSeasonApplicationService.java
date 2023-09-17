package f1api.teamofseason.application;

import static f1api.queryparameter.QueryParameterHandler.handleQueryParameters;
import static java.util.Map.entry;

import f1api.driver.application.DriverApplicationService;
import f1api.driver.application.DriverDTO;
import f1api.driver.application.DriverMapper;
import f1api.driver.domain.Driver;
import f1api.engine.application.EngineApplicationService;
import f1api.engine.application.EngineDTO;
import f1api.engine.application.EngineMapper;
import f1api.engine.domain.Engine;
import f1api.exception.ApiInstanceAlreadyExistsException;
import f1api.exception.ApiNotFoundException;
import f1api.queryparameter.DefaultQueryParameter;
import f1api.queryparameter.sort.DefaultSortPropertyMapper;
import f1api.responsepage.ResponsePage;
import f1api.season.application.SeasonApplicationService;
import f1api.season.application.SeasonDTO;
import f1api.season.application.SeasonMapper;
import f1api.season.application.SeasonSortPropertyMapper;
import f1api.season.domain.Season;
import f1api.team.application.TeamApplicationService;
import f1api.team.application.TeamDTO;
import f1api.team.application.TeamMapper;
import f1api.team.application.TeamSortPropertyMapper;
import f1api.team.domain.Team;
import f1api.teamofseason.domain.TeamOfSeason;
import f1api.teamofseason.domain.TeamOfSeasonRepository;
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

    private void checkIfTeamOfSeasonAlreadyExists(UUID seasonId, UUID teamId) {
        if (teamOfSeasonRepository.existsTeamOfSeasonBySeasonIdAndTeamId(seasonId, teamId)) {
            throw ApiInstanceAlreadyExistsException.of(
                    "TeamOfSeason",
                    Map.ofEntries(entry("seasonId", seasonId.toString()), entry("teamId", teamId.toString())));
        }
    }

    private Set<Driver> getDrivers(TeamOfSeasonDTO teamOfSeasonDTO) {
        return teamOfSeasonDTO.getDriverIds().stream()
                .map(driverApplicationService::getDriverById)
                .collect(Collectors.toSet());
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

    public EngineDTO getEngineOfTeamOfSeason(UUID seasonId, UUID teamId) {
        Season season = seasonApplicationService.getSeasonById(seasonId);
        Team team = teamApplicationService.getTeamById(teamId);
        Optional<Engine> engine = teamOfSeasonRepository.findEngineBySeasonAndTeam(season, team);
        if (engine.isEmpty()) {
            throwApiNotFoundExceptionForTeamNotInSeason(seasonId, teamId);
        }
        return engineMapper.toEngineDTO(engine.get());
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
