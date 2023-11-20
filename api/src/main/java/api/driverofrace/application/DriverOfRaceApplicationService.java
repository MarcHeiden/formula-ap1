package api.driverofrace.application;

import api.driver.application.DriverApplicationService;
import api.driver.domain.Driver;
import api.driverofrace.domain.DriverOfRace;
import api.driverofrace.domain.DriverOfRaceRepository;
import api.exception.*;
import api.queryparameter.QueryParameterHandler;
import api.queryparameter.sort.DefaultSortPropertyMapper;
import api.race.application.RaceApplicationService;
import api.race.domain.Race;
import api.responsepage.ResponsePage;
import api.team.application.TeamApplicationService;
import api.team.domain.Team;
import api.teamofseason.application.TeamOfSeasonApplicationService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

@Service
public class DriverOfRaceApplicationService {
    private final DriverOfRaceRepository driverOfRaceRepository;
    private final DriverOfRaceMapper driverOfRaceMapper;
    private final DriverApplicationService driverApplicationService;
    private final RaceApplicationService raceApplicationService;
    private final TeamApplicationService teamApplicationService;
    private final TeamOfSeasonApplicationService teamOfSeasonApplicationService;
    private final QualifyingResultMapper qualifyingResultMapper;
    private final DriverOfRaceQueryParameter driverOfRaceQueryParameter;
    private final DefaultSortPropertyMapper defaultSortPropertyMapper;
    private final RaceResultMapper raceResultMapper;
    private final FastestLapMapper fastestLapMapper;
    private final TopSpeedMapper topSpeedMapper;
    private final LeadingLapsMapper leadingLapsMapper;
    private final FastestPitStopMapper fastestPitStopMapper;

    @Autowired
    public DriverOfRaceApplicationService(
            DriverOfRaceRepository driverOfRaceRepository,
            DriverOfRaceMapper driverOfRaceMapper,
            DriverApplicationService driverApplicationService,
            RaceApplicationService raceApplicationService,
            TeamApplicationService teamApplicationService,
            TeamOfSeasonApplicationService teamOfSeasonApplicationService,
            QualifyingResultMapper qualifyingResultMapper,
            DriverOfRaceQueryParameter driverOfRaceQueryParameter,
            DefaultSortPropertyMapper defaultSortPropertyMapper,
            RaceResultMapper raceResultMapper,
            FastestLapMapper fastestLapMapper,
            TopSpeedMapper topSpeedMapper,
            LeadingLapsMapper leadingLapsMapper,
            FastestPitStopMapper fastestPitStopMapper) {
        this.driverOfRaceRepository = driverOfRaceRepository;
        this.driverOfRaceMapper = driverOfRaceMapper;
        this.driverApplicationService = driverApplicationService;
        this.raceApplicationService = raceApplicationService;
        this.teamApplicationService = teamApplicationService;
        this.teamOfSeasonApplicationService = teamOfSeasonApplicationService;
        this.qualifyingResultMapper = qualifyingResultMapper;
        this.driverOfRaceQueryParameter = driverOfRaceQueryParameter;
        this.defaultSortPropertyMapper = defaultSortPropertyMapper;
        this.raceResultMapper = raceResultMapper;
        this.fastestLapMapper = fastestLapMapper;
        this.topSpeedMapper = topSpeedMapper;
        this.leadingLapsMapper = leadingLapsMapper;
        this.fastestPitStopMapper = fastestPitStopMapper;
    }

    private void throwApiInstanceAlreadyExistsException(Race race, Driver driver, String attribute) {
        throw new ApiInstanceAlreadyExistsException("Driver with the driverId '" + driver.getId() + "' already has a "
                + attribute + " for the Race with the raceId '" + race.getId() + "'.");
    }

    private void throwApiNotFoundException(Race race, Driver driver, String attribute) {
        throw new ApiNotFoundException("Driver with the driverId '" + driver.getId() + "' has no " + attribute
                + " for the Race with the raceId '" + race.getId() + "'.");
    }

    private void checkIfDriverOfRaceAlreadyExists(Race race, Driver driver) {
        if (driverOfRaceRepository.existsDriverOfRaceByRaceAndDriver(race, driver)) {
            throwApiInstanceAlreadyExistsException(race, driver, "qualifying result");
        }
    }

    private Pageable handleQueryParameters(
            MultiValueMap<String, String> parameters,
            Pageable pageable,
            List<String> dtoProperties,
            String sortPrefix) {
        return QueryParameterHandler.handleQueryParameters(
                parameters, driverOfRaceQueryParameter, pageable, defaultSortPropertyMapper, dtoProperties, sortPrefix);
    }

    private Driver getDriverById(UUID driverId) {
        try {
            return driverApplicationService.getDriverById(driverId);
        } catch (ApiNotFoundException apiNotFoundException) {
            throw new ApiInvalidPropertyException(apiNotFoundException.getMessage());
        }
    }

    private DriverOfRace getDriverOfRaceByRaceAndDriver(Race race, Driver driver) {
        Optional<DriverOfRace> driverOfRace = driverOfRaceRepository.findDriverOfRaceByRaceAndDriver(race, driver);
        if (driverOfRace.isEmpty()) {
            throwApiNotFoundException(race, driver, "qualifying result");
        }
        return driverOfRace.get();
    }

    private DriverOfRace getDriverOfRaceByRaceAndDriverAndRaceResultIsNotNull(Race race, Driver driver) {
        Optional<DriverOfRace> driverOfRace =
                driverOfRaceRepository.findDriverOfRaceByRaceAndDriverAndRaceResultIsNotNull(race, driver);
        if (driverOfRace.isEmpty()) {
            throwApiNotFoundException(race, driver, "race result");
        }
        return driverOfRace.get();
    }

    private DriverOfRace getDriverOfRaceByRaceAndDriverAndFastestLapIsNotNull(Race race, Driver driver) {
        Optional<DriverOfRace> driverOfRace =
                driverOfRaceRepository.findDriverOfRaceByRaceAndDriverAndFastestLapIsNotNull(race, driver);
        if (driverOfRace.isEmpty()) {
            throwApiNotFoundException(race, driver, "fastest lap");
        }
        return driverOfRace.get();
    }

    private DriverOfRace getDriverOfRaceByRaceAndDriverAndTopSpeedIsNotNull(Race race, Driver driver) {
        Optional<DriverOfRace> driverOfRace =
                driverOfRaceRepository.findDriverOfRaceByRaceAndDriverAndTopSpeedIsNotNull(race, driver);
        if (driverOfRace.isEmpty()) {
            throwApiNotFoundException(race, driver, "top speed");
        }
        return driverOfRace.get();
    }

    private DriverOfRace getDriverOfRaceByRaceAndDriverAndLeadingLapsIsNotNull(Race race, Driver driver) {
        Optional<DriverOfRace> driverOfRace =
                driverOfRaceRepository.findDriverOfRaceByRaceAndDriverAndLeadingLapsIsNotNull(race, driver);
        if (driverOfRace.isEmpty()) {
            throwApiNotFoundException(race, driver, "leading laps");
        }
        return driverOfRace.get();
    }

    private DriverOfRace getDriverOfRaceByRaceAndFastestPitStopIsNotNull(Race race) {
        Optional<DriverOfRace> driverOfRace =
                driverOfRaceRepository.findDriverOfRaceByRaceAndFastestPitStopIsNotNull(race);
        if (driverOfRace.isEmpty()) {
            throw new ApiNotFoundException("Race with the raceId '" + race.getId() + "' has no fastest pit stop.");
        }
        return driverOfRace.get();
    }

    private DriverOfRace getDriverOfRaceByRaceAndDriverAndRaceResultIsNotNullOrThrowApiConflictException(
            Race race, Driver driver) {
        try {
            return getDriverOfRaceByRaceAndDriverAndRaceResultIsNotNull(race, driver);
        } catch (ApiNotFoundException apiNotFoundException) {
            throw new ApiConflictException(apiNotFoundException.getMessage());
        }
    }

    private DriverInfoDTO toDriverInfoDTO(DriverOfRace driverOfRace) {
        return driverOfRaceMapper.toDriverInfoDTO(
                driverOfRace,
                teamOfSeasonApplicationService.getEngineOfTeamOfSeason(
                        driverOfRace.getRace().getSeason(), driverOfRace.getTeam()));
    }

    private QualifyingResultDTO toQualifyingResultDTO(DriverOfRace driverOfRace) {
        return qualifyingResultMapper.toQualifyingResultDTO(
                driverOfRace.getQualifyingResult(), toDriverInfoDTO(driverOfRace));
    }

    private RaceResultDTO toRaceResultDTO(DriverOfRace driverOfRace) {
        return raceResultMapper.toRaceResultDTO(driverOfRace.getRaceResult(), toDriverInfoDTO(driverOfRace));
    }

    private FastestLapDTO toFastestLapDTO(DriverOfRace driverOfRace) {
        return fastestLapMapper.toFastestLapDTO(driverOfRace.getFastestLap(), toDriverInfoDTO(driverOfRace));
    }

    private TopSpeedDTO toTopSpeedDTO(DriverOfRace driverOfRace) {
        return topSpeedMapper.toTopSpeedDTO(driverOfRace.getTopSpeed(), toDriverInfoDTO(driverOfRace));
    }

    private LeadingLapsDTO toLeadingLapsDTO(DriverOfRace driverOfRace) {
        return leadingLapsMapper.toLeadingLapsDTO(driverOfRace.getLeadingLaps(), toDriverInfoDTO(driverOfRace));
    }

    private FastestPitStopDTO toFastestPitStopDTO(DriverOfRace driverOfRace) {
        return fastestPitStopMapper.toFastestPitStopDTO(
                driverOfRace.getFastestPitStop(), toDriverInfoDTO(driverOfRace));
    }

    public QualifyingResultDTO createQualifyingResultForDriverOfRace(
            UUID raceId, QualifyingResultDTO qualifyingResultDTO) {
        Race race = raceApplicationService.getRaceById(raceId);
        Driver driver = getDriverById(qualifyingResultDTO.getDriverInfoDTO().getDriverId());
        Team team;
        try {
            team = teamApplicationService.getTeamById(qualifyingResultDTO.getTeamId());
        } catch (ApiNotFoundException apiNotFoundException) {
            throw new ApiInvalidPropertyException(apiNotFoundException.getMessage());
        }
        checkIfDriverOfRaceAlreadyExists(race, driver);
        if (!teamOfSeasonApplicationService.checkIfDriverIsInTeamOfSeason(driver, team, race.getSeason())) {
            throw new ApiInvalidPropertyException("Driver with the driverId '" + driver.getId()
                    + "' is not in the Team with the teamId '" + team.getId() + "' of the Season with the seasonId '"
                    + race.getSeason().getId() + "'.");
        }
        DriverOfRace driverOfRace =
                new DriverOfRace(race, driver, team, qualifyingResultMapper.toQualifyingResult(qualifyingResultDTO));
        driverOfRaceRepository.save(driverOfRace);
        return toQualifyingResultDTO(driverOfRace);
    }

    public ResponsePage<QualifyingResultDTO> getQualifyingResultsOfRace(
            UUID raceId, Pageable pageable, UUID driverId, MultiValueMap<String, String> parameters) {
        Race race = raceApplicationService.getRaceById(raceId);
        Pageable handledPageable =
                handleQueryParameters(parameters, pageable, QualifyingResultDTO.getProperties(), "qualifyingResult");
        Page<DriverOfRace> driversOfRace;
        if (driverId != null) {
            driversOfRace = driverOfRaceRepository.findDriverOfRaceByRaceAndDriverId(handledPageable, race, driverId);
        } else {
            driversOfRace = driverOfRaceRepository.findDriverOfRacesByRace(handledPageable, race);
        }
        return ResponsePage.of(driversOfRace.map(this::toQualifyingResultDTO));
    }

    public QualifyingResultDTO updateQualifyingResultForDriverOfRace(
            UUID raceId, UUID driverId, QualifyingResultDTO qualifyingResultDTO) {
        Race race = raceApplicationService.getRaceById(raceId);
        Driver driver = driverApplicationService.getDriverById(driverId);
        DriverOfRace driverOfRace = getDriverOfRaceByRaceAndDriver(race, driver);
        driverOfRace.setQualifyingResult(qualifyingResultMapper.toQualifyingResult(qualifyingResultDTO));
        driverOfRaceRepository.save(driverOfRace);
        return toQualifyingResultDTO(driverOfRace);
    }

    /**
     * Checks if either postion or dnf of the raceResultDTO is defined and whether
     * the postion is set to null if dnf is set to true.
     * @param raceResultDTO
     * @throws ApiInvalidPropertyException if validation failed
     */
    private void validateRaceResultDTO(RaceResultDTO raceResultDTO) {
        if (raceResultDTO.isEmpty()) {
            throw ApiPropertyIsNullException.of(RaceResultDTO.getEmptyProperties());
        }
        if (raceResultDTO.getPosition() != null && (raceResultDTO.getDnf() != null && raceResultDTO.getDnf())) {
            throw new ApiInvalidPropertyException("Driver can not have a position if dnf is set to true.");
        }
    }

    /**
     * Checks in addition to {@link DriverOfRaceApplicationService#validateRaceResultDTO(RaceResultDTO)} whether
     * a position is defined if dnf of the raceResultDTO is set to false.
     * @param raceResultDTO
     * @throws ApiInvalidPropertyException if validation failed
     */
    private void validateRaceResultDTOOnCreate(RaceResultDTO raceResultDTO) {
        validateRaceResultDTO(raceResultDTO);
        if (raceResultDTO.getPosition() == null && !raceResultDTO.getDnf()) {
            throw new ApiInvalidPropertyException("Driver must have a position if dnf is set to false.");
        }
    }

    /**
     * Checks in addition to {@link DriverOfRaceApplicationService#validateRaceResultDTO(RaceResultDTO)} whether
     * a position is defined in the raceResultDTO or the corresponding driverOfRace object
     * if dnf of the raceResultDTO is set to false.
     * @param raceResultDTO
     * @param driverOfRace instance for which the raceResult is to be updated
     * @throws ApiInvalidPropertyException if validation failed
     */
    private void validateRaceResultDTOOnUpdate(RaceResultDTO raceResultDTO, DriverOfRace driverOfRace) {
        validateRaceResultDTO(raceResultDTO);
        if (raceResultDTO.getPosition() == null
                && driverOfRace.getRaceResult().getPosition() == null
                && !raceResultDTO.getDnf()) {
            throw new ApiInvalidPropertyException("Driver must have a position if dnf is set to false.");
        }
    }

    /**
     * If dnf of the raceResultDTO is set to null it is interpreted as if it is set to false.
     * @param raceResultDTO
     */
    private void setRaceResultDTODnfToFalseByDefault(RaceResultDTO raceResultDTO) {
        if (raceResultDTO.getDnf() == null) {
            raceResultDTO.setDnf(false);
        }
    }

    public RaceResultDTO createRaceResultForDriverOfRace(UUID raceId, RaceResultDTO raceResultDTO) {
        Race race = raceApplicationService.getRaceById(raceId);
        Driver driver = getDriverById(raceResultDTO.getDriverInfoDTO().getDriverId());
        DriverOfRace driverOfRace;
        try {
            driverOfRace = getDriverOfRaceByRaceAndDriver(race, driver);
        } catch (ApiNotFoundException apiNotFoundException) {
            throw new ApiConflictException(apiNotFoundException.getMessage());
        }
        if (driverOfRace.getRaceResult() != null) {
            throwApiInstanceAlreadyExistsException(race, driver, "race result");
        }
        validateRaceResultDTOOnCreate(raceResultDTO);
        setRaceResultDTODnfToFalseByDefault(raceResultDTO);
        driverOfRace.setRaceResult(raceResultMapper.toRaceResult(raceResultDTO));
        driverOfRaceRepository.save(driverOfRace);
        return toRaceResultDTO(driverOfRace);
    }

    public ResponsePage<RaceResultDTO> getRaceResultsOfRace(
            UUID raceId, Pageable pageable, UUID driverId, MultiValueMap<String, String> parameters) {
        Race race = raceApplicationService.getRaceById(raceId);
        Pageable handledPageable =
                handleQueryParameters(parameters, pageable, RaceResultDTO.getProperties(), "raceResult");
        Page<DriverOfRace> driversOfRace;
        if (driverId != null) {
            driversOfRace = driverOfRaceRepository.findDriverOfRaceByRaceAndDriverIdAndRaceResultIsNotNull(
                    handledPageable, race, driverId);
        } else {
            driversOfRace = driverOfRaceRepository.findDriverOfRacesByRaceAndRaceResultIsNotNull(handledPageable, race);
        }
        return ResponsePage.of(driversOfRace.map(this::toRaceResultDTO));
    }

    public RaceResultDTO updateRaceResultForDriverOfRace(UUID raceId, UUID driverId, RaceResultDTO raceResultDTO) {
        Race race = raceApplicationService.getRaceById(raceId);
        Driver driver = driverApplicationService.getDriverById(driverId);
        DriverOfRace driverOfRace = getDriverOfRaceByRaceAndDriverAndRaceResultIsNotNull(race, driver);
        validateRaceResultDTOOnUpdate(raceResultDTO, driverOfRace);
        setRaceResultDTODnfToFalseByDefault(raceResultDTO);
        // Set position to old position if no new postion is defined and dnf is set to false
        if (raceResultDTO.getPosition() == null && !raceResultDTO.getDnf()) {
            raceResultDTO.setPosition(driverOfRace.getRaceResult().getPosition());
        }
        driverOfRace.setRaceResult(raceResultMapper.toRaceResult(raceResultDTO));
        driverOfRaceRepository.save(driverOfRace);
        return toRaceResultDTO(driverOfRace);
    }

    public FastestLapDTO createFastestLapForDriverOfRace(UUID raceId, FastestLapDTO fastestLapDTO) {
        Race race = raceApplicationService.getRaceById(raceId);
        Driver driver = getDriverById(fastestLapDTO.getDriverInfoDTO().getDriverId());
        DriverOfRace driverOfRace =
                getDriverOfRaceByRaceAndDriverAndRaceResultIsNotNullOrThrowApiConflictException(race, driver);
        if (driverOfRace.getFastestLap() != null) {
            throwApiInstanceAlreadyExistsException(race, driver, "fastest lap");
        }
        driverOfRace.setFastestLap(fastestLapMapper.toFastestLap(fastestLapDTO));
        driverOfRaceRepository.save(driverOfRace);
        return toFastestLapDTO(driverOfRace);
    }

    public ResponsePage<FastestLapDTO> getFastestLapsOfRace(
            UUID raceId, Pageable pageable, UUID driverId, MultiValueMap<String, String> parameters) {
        Race race = raceApplicationService.getRaceById(raceId);
        Pageable handledPageable =
                handleQueryParameters(parameters, pageable, FastestLapDTO.getProperties(), "fastestLap");
        Page<DriverOfRace> driversOfRace;
        if (driverId != null) {
            driversOfRace = driverOfRaceRepository.findDriverOfRaceByRaceAndDriverIdAndFastestLapIsNotNull(
                    handledPageable, race, driverId);
        } else {
            driversOfRace = driverOfRaceRepository.findDriverOfRacesByRaceAndFastestLapIsNotNull(handledPageable, race);
        }
        return ResponsePage.of(driversOfRace.map(this::toFastestLapDTO));
    }

    public FastestLapDTO updateFastestLapForDriverOfRace(UUID raceId, UUID driverId, FastestLapDTO fastestLapDTO) {
        Race race = raceApplicationService.getRaceById(raceId);
        Driver driver = driverApplicationService.getDriverById(driverId);
        DriverOfRace driverOfRace = getDriverOfRaceByRaceAndDriverAndFastestLapIsNotNull(race, driver);
        driverOfRace.setFastestLap(fastestLapMapper.toFastestLap(fastestLapDTO));
        driverOfRaceRepository.save(driverOfRace);
        return toFastestLapDTO(driverOfRace);
    }

    public TopSpeedDTO createTopSpeedForDriverOfRace(UUID raceId, TopSpeedDTO topSpeedDTO) {
        Race race = raceApplicationService.getRaceById(raceId);
        Driver driver = getDriverById(topSpeedDTO.getDriverInfoDTO().getDriverId());
        DriverOfRace driverOfRace =
                getDriverOfRaceByRaceAndDriverAndRaceResultIsNotNullOrThrowApiConflictException(race, driver);
        if (driverOfRace.getTopSpeed() != null) {
            throwApiInstanceAlreadyExistsException(race, driver, "top speed");
        }
        driverOfRace.setTopSpeed(topSpeedMapper.toTopSpeed(topSpeedDTO));
        driverOfRaceRepository.save(driverOfRace);
        return toTopSpeedDTO(driverOfRace);
    }

    public ResponsePage<TopSpeedDTO> getTopSpeedsOfRace(
            UUID raceId, Pageable pageable, UUID driverId, MultiValueMap<String, String> parameters) {
        Race race = raceApplicationService.getRaceById(raceId);
        Pageable handledPageable = handleQueryParameters(parameters, pageable, TopSpeedDTO.getProperties(), "topSpeed");
        Page<DriverOfRace> driversOfRace;
        if (driverId != null) {
            driversOfRace = driverOfRaceRepository.findDriverOfRaceByRaceAndDriverIdAndTopSpeedIsNotNull(
                    handledPageable, race, driverId);
        } else {
            driversOfRace = driverOfRaceRepository.findDriverOfRacesByRaceAndTopSpeedIsNotNull(handledPageable, race);
        }
        return ResponsePage.of(driversOfRace.map(this::toTopSpeedDTO));
    }

    public TopSpeedDTO updateTopSpeedForDriverOfRace(UUID raceId, UUID driverId, TopSpeedDTO topSpeedDTO) {
        Race race = raceApplicationService.getRaceById(raceId);
        Driver driver = driverApplicationService.getDriverById(driverId);
        DriverOfRace driverOfRace = getDriverOfRaceByRaceAndDriverAndTopSpeedIsNotNull(race, driver);
        driverOfRace.setTopSpeed(topSpeedMapper.toTopSpeed(topSpeedDTO));
        driverOfRaceRepository.save(driverOfRace);
        return toTopSpeedDTO(driverOfRace);
    }

    public LeadingLapsDTO createLeadingLapsForDriverOfRace(UUID raceId, LeadingLapsDTO leadingLapsDTO) {
        Race race = raceApplicationService.getRaceById(raceId);
        Driver driver = getDriverById(leadingLapsDTO.getDriverInfoDTO().getDriverId());
        DriverOfRace driverOfRace =
                getDriverOfRaceByRaceAndDriverAndRaceResultIsNotNullOrThrowApiConflictException(race, driver);
        if (driverOfRace.getLeadingLaps() != null) {
            throwApiInstanceAlreadyExistsException(race, driver, "leading laps");
        }
        driverOfRace.setLeadingLaps(leadingLapsMapper.toLeadingLaps(leadingLapsDTO));
        driverOfRaceRepository.save(driverOfRace);
        return toLeadingLapsDTO(driverOfRace);
    }

    public ResponsePage<LeadingLapsDTO> getLeadingLapsOfRace(
            UUID raceId, Pageable pageable, UUID driverId, MultiValueMap<String, String> parameters) {
        Race race = raceApplicationService.getRaceById(raceId);
        Pageable handledPageable =
                handleQueryParameters(parameters, pageable, LeadingLapsDTO.getProperties(), "leadingLaps");
        Page<DriverOfRace> driversOfRace;
        if (driverId != null) {
            driversOfRace = driverOfRaceRepository.findDriverOfRaceByRaceAndDriverIdAndLeadingLapsIsNotNull(
                    handledPageable, race, driverId);
        } else {
            driversOfRace =
                    driverOfRaceRepository.findDriverOfRacesByRaceAndLeadingLapsIsNotNull(handledPageable, race);
        }
        return ResponsePage.of(driversOfRace.map(this::toLeadingLapsDTO));
    }

    public LeadingLapsDTO updateLeadingLapsForDriverOfRace(UUID raceId, UUID driverId, LeadingLapsDTO leadingLapsDTO) {
        Race race = raceApplicationService.getRaceById(raceId);
        Driver driver = driverApplicationService.getDriverById(driverId);
        DriverOfRace driverOfRace = getDriverOfRaceByRaceAndDriverAndLeadingLapsIsNotNull(race, driver);
        driverOfRace.setLeadingLaps(leadingLapsMapper.toLeadingLaps(leadingLapsDTO));
        driverOfRaceRepository.save(driverOfRace);
        return toLeadingLapsDTO(driverOfRace);
    }

    public FastestPitStopDTO createFastestPitStopOfRace(UUID raceId, FastestPitStopDTO fastestPitStopDTO) {
        Race race = raceApplicationService.getRaceById(raceId);
        Driver driver = getDriverById(fastestPitStopDTO.getDriverInfoDTO().getDriverId());
        DriverOfRace driverOfRace =
                getDriverOfRaceByRaceAndDriverAndRaceResultIsNotNullOrThrowApiConflictException(race, driver);
        if (driverOfRaceRepository.existsDriverOfRaceByRaceAndFastestPitStopIsNotNull(race)) {
            throw new ApiInstanceAlreadyExistsException(
                    "Race with the raceId '" + raceId + "' already has a fastest pit stop.");
        }
        driverOfRace.setFastestPitStop(fastestPitStopMapper.toFastestPitStop(fastestPitStopDTO));
        driverOfRaceRepository.save(driverOfRace);
        return toFastestPitStopDTO(driverOfRace);
    }

    public FastestPitStopDTO getFastestPitStopOfRace(UUID raceId) {
        Race race = raceApplicationService.getRaceById(raceId);
        return toFastestPitStopDTO(getDriverOfRaceByRaceAndFastestPitStopIsNotNull(race));
    }

    public FastestPitStopDTO updateFastestPitStopOfRace(UUID raceId, FastestPitStopDTO fastestPitStopDTO) {
        Race race = raceApplicationService.getRaceById(raceId);
        DriverOfRace driverOfRace = getDriverOfRaceByRaceAndFastestPitStopIsNotNull(race);
        driverOfRace.setFastestPitStop(fastestPitStopMapper.toFastestPitStop(fastestPitStopDTO));
        driverOfRaceRepository.save(driverOfRace);
        return toFastestPitStopDTO(driverOfRace);
    }
}
