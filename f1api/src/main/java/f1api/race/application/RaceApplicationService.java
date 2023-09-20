package f1api.race.application;

import static f1api.queryparameter.QueryParameterHandler.handleQueryParameters;

import f1api.exception.ApiInstanceAlreadyExistsException;
import f1api.exception.ApiInvalidPropertyException;
import f1api.exception.ApiNotFoundException;
import f1api.exception.ApiPropertyIsNullException;
import f1api.race.domain.Race;
import f1api.race.domain.RaceRepository;
import f1api.responsepage.ResponsePage;
import f1api.season.application.SeasonApplicationService;
import f1api.season.domain.Season;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

@Service
public class RaceApplicationService {
    private final RaceRepository raceRepository;
    private final RaceMapper raceMapper;
    private final RaceSortPropertyMapper raceSortPropertyMapper;
    private final RaceQueryParameter raceQueryParameter;
    private final SeasonApplicationService seasonApplicationService;

    @Autowired
    public RaceApplicationService(
            RaceRepository raceRepository,
            RaceMapper raceMapper,
            RaceSortPropertyMapper raceSortPropertyMapper,
            RaceQueryParameter raceQueryParameter,
            SeasonApplicationService seasonApplicationService) {
        this.raceRepository = raceRepository;
        this.raceMapper = raceMapper;
        this.raceSortPropertyMapper = raceSortPropertyMapper;
        this.raceQueryParameter = raceQueryParameter;
        this.seasonApplicationService = seasonApplicationService;
    }

    private void throwApiInstanceAlreadyExistsException(LocalDate date) {
        throw ApiInstanceAlreadyExistsException.of("Race", "date", date.toString());
    }

    private void throwApiInstanceAlreadyExistsException(String name, Season season) {
        throw ApiInstanceAlreadyExistsException.of(
                "Race", "raceName", name, "Season", "seasonId", season.getId().toString());
    }

    private void checkIfRaceAlreadyExistsByDate(LocalDate date) {
        if (raceRepository.existsRaceByDate(date)) {
            throwApiInstanceAlreadyExistsException(date);
        }
    }

    private void checkIfRaceAlreadyExistsByNameAndSeason(String name, Season season) {
        if (raceRepository.existsRaceByNameAndSeason(name, season)) {
            throwApiInstanceAlreadyExistsException(name, season);
        }
    }

    private void checkIfRaceAlreadyExists(LocalDate date, String name, Season season) {
        checkIfRaceAlreadyExistsByDate(date);
        checkIfRaceAlreadyExistsByNameAndSeason(name, season);
    }

    private void checkIfDifferentRaceAlreadyExistsByDate(Race race, LocalDate date) {
        raceRepository.findRaceByDate(date).ifPresent(foundRace -> {
            if (!race.getId().equals(foundRace.getId())) {
                throwApiInstanceAlreadyExistsException(date);
            }
        });
    }

    private void checkIfDifferentRaceAlreadyExistsByNameAndSeason(Race race, String name, Season season) {
        raceRepository.findRaceByNameAndSeason(name, season).ifPresent(foundRace -> {
            if (!race.getId().equals(foundRace.getId())) {
                throwApiInstanceAlreadyExistsException(name, season);
            }
        });
    }

    private Pageable handleRaceQueryParameters(MultiValueMap<String, String> parameters, Pageable pageable) {
        return handleQueryParameters(
                parameters, raceQueryParameter, pageable, raceSortPropertyMapper, RaceDTO.getProperties());
    }

    private void checkIfDateYearEqualsSeasonYear(LocalDate date, Season season) {
        if (date.getYear() != season.getYear().getValue()) {
            throw new ApiInvalidPropertyException(
                    "Year of date '" + date + "' does not match seasonYear '" + season.getYear() + "'.");
        }
    }

    public RaceDTO createRaceOfSeason(UUID seasonId, RaceDTO raceDTO) {
        Season season = seasonApplicationService.getSeasonById(seasonId);
        checkIfRaceAlreadyExists(raceDTO.getDate(), raceDTO.getRaceName(), season);
        checkIfDateYearEqualsSeasonYear(raceDTO.getDate(), season);
        // cancelled == false by default
        if (raceDTO.getCancelled() == null) {
            raceDTO.setCancelled(false);
        }
        Race race = raceMapper.toRace(raceDTO, season);
        raceRepository.save(race);
        return raceMapper.toRaceDTO(race);
    }

    public ResponsePage<RaceDTO> getRacesOfSeason(
            UUID seasonId,
            Pageable pageable,
            String raceName,
            LocalDate date,
            MultiValueMap<String, String> parameters) {
        Season season = seasonApplicationService.getSeasonById(seasonId);
        Pageable handledPageable = handleRaceQueryParameters(parameters, pageable);
        Page<Race> races;
        if (raceName != null && date != null) {
            races = raceRepository.findRaceBySeasonAndNameAndDate(handledPageable, season, raceName, date);
        } else if (raceName != null) {
            races = raceRepository.findRaceBySeasonAndName(handledPageable, season, raceName);
        } else if (date != null) {
            races = raceRepository.findRaceBySeasonAndDate(handledPageable, season, date);
        } else {
            races = raceRepository.findRacesBySeason(handledPageable, season);
        }
        return ResponsePage.of(races.map(raceMapper::toRaceDTO));
    }

    public ResponsePage<RaceDTO> getRaces(
            Pageable pageable, String raceName, LocalDate date, MultiValueMap<String, String> parameters) {
        Pageable handledPageable = handleRaceQueryParameters(parameters, pageable);
        Page<Race> races;
        if (raceName != null && date != null) {
            races = raceRepository.findRaceByNameAndDate(handledPageable, raceName, date);
        } else if (raceName != null) {
            races = raceRepository.findRacesByName(handledPageable, raceName);
        } else if (date != null) {
            races = raceRepository.findRaceByDate(handledPageable, date);
        } else {
            races = raceRepository.findAll(handledPageable);
        }
        return ResponsePage.of(races.map(raceMapper::toRaceDTO));
    }

    public Race getRaceById(UUID id) {
        return raceRepository.findById(id).orElseThrow(() -> ApiNotFoundException.of("Race", "raceId", id.toString()));
    }

    public RaceDTO getRace(UUID raceId) {
        return raceMapper.toRaceDTO(getRaceById(raceId));
    }

    public RaceDTO updateRace(UUID raceId, RaceDTO raceDTO) {
        Race race = getRaceById(raceId);
        if (raceDTO.isEmptyOnUpdate()) {
            throw ApiPropertyIsNullException.of(RaceDTO.getNotNullPropertiesOnUpdate());
        }
        if (raceDTO.getRaceName() != null) {
            checkIfDateYearEqualsSeasonYear(raceDTO.getDate(), race.getSeason());
            checkIfDifferentRaceAlreadyExistsByNameAndSeason(race, raceDTO.getRaceName(), race.getSeason());
            race.setName(raceDTO.getRaceName());
        }
        if (raceDTO.getDate() != null) {
            checkIfDifferentRaceAlreadyExistsByDate(race, raceDTO.getDate());
            race.setDate(raceDTO.getDate());
        }
        if (raceDTO.getTime() != null) {
            race.setTime(raceMapper.getTimeFromTimeString(raceDTO.getTime()));
        }
        if (raceDTO.getCancelled() != null) {
            race.setCancelled(raceDTO.getCancelled());
        }
        raceRepository.save(race);
        return raceMapper.toRaceDTO(race);
    }
}
