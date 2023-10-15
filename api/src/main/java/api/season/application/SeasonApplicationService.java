package api.season.application;

import static api.queryparameter.QueryParameterHandler.handleQueryParameters;

import api.exception.ApiInstanceAlreadyExistsException;
import api.exception.ApiNotFoundException;
import api.responsepage.ResponsePage;
import api.season.domain.Season;
import api.season.domain.SeasonRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.Year;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

@Service
public class SeasonApplicationService {
    private final SeasonRepository seasonRepository;
    private final SeasonMapper seasonMapper;

    private final SeasonSortPropertyMapper seasonSortPropertyMapper;

    private final SeasonQueryParameter seasonQueryParameter;

    @Autowired
    public SeasonApplicationService(
            SeasonRepository seasonRepository,
            SeasonMapper seasonMapper,
            SeasonSortPropertyMapper seasonSortPropertyMapper,
            SeasonQueryParameter seasonQueryParameter) {
        this.seasonRepository = seasonRepository;
        this.seasonMapper = seasonMapper;
        this.seasonSortPropertyMapper = seasonSortPropertyMapper;
        this.seasonQueryParameter = seasonQueryParameter;
    }

    private void throwApiInstanceAlreadyExistsException(Year year) {
        throw ApiInstanceAlreadyExistsException.of("Season", "seasonYear", year.toString());
    }

    private void checkIfSeasonAlreadyExists(Year year) {
        if (seasonRepository.existsSeasonByYear(year)) {
            throwApiInstanceAlreadyExistsException(year);
        }
    }

    private void checkIfDifferentSeasonAlreadyExists(Season season, Year year) {
        seasonRepository.findSeasonByYear(year).ifPresent(foundSeason -> {
            if (!season.getId().equals(foundSeason.getId())) {
                throwApiInstanceAlreadyExistsException(year);
            }
        });
    }

    public SeasonDTO createSeason(SeasonDTO seasonDTO) {
        checkIfSeasonAlreadyExists(seasonDTO.getSeasonYear());
        Season season = seasonMapper.toSeason(seasonDTO);
        seasonRepository.save(season);
        return seasonMapper.toSeasonDTO(season);
    }

    public ResponsePage<SeasonDTO> getSeasons(
            Pageable pageable, Year seasonYear, MultiValueMap<String, String> parameters) {
        Pageable handledPageable = handleQueryParameters(
                parameters, seasonQueryParameter, pageable, seasonSortPropertyMapper, SeasonDTO.getProperties());
        Page<Season> seasons;
        if (seasonYear != null) {
            seasons = seasonRepository.findSeasonByYear(handledPageable, seasonYear);
        } else {
            seasons = seasonRepository.findAll(handledPageable);
        }
        return ResponsePage.of(seasons.map(seasonMapper::toSeasonDTO));
    }

    public Season getSeasonById(UUID id) {
        return seasonRepository
                .findById(id)
                .orElseThrow(() -> ApiNotFoundException.of("Season", "seasonId", id.toString()));
    }

    public SeasonDTO getSeason(UUID seasonId) {
        return seasonMapper.toSeasonDTO(getSeasonById(seasonId));
    }

    public SeasonDTO updateSeason(UUID seasonId, @NotNull @Valid SeasonDTO seasonDTO) {
        Season season = getSeasonById(seasonId);
        checkIfDifferentSeasonAlreadyExists(season, seasonDTO.getSeasonYear());
        season.setYear(seasonDTO.getSeasonYear());
        seasonRepository.save(season);
        return seasonMapper.toSeasonDTO(season);
    }
}
