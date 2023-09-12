package f1api.season.application;

import static f1api.queryparameter.QueryParameterHandler.handleQueryParameters;

import f1api.exception.ApiInstanceAlreadyExistsException;
import f1api.exception.ApiNotFoundException;
import f1api.responsepage.ResponsePage;
import f1api.season.domain.Season;
import f1api.season.domain.SeasonRepository;
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

    public SeasonDTO createSeason(SeasonDTO seasonDTO) {
        if (seasonRepository.existsSeasonByYear(seasonDTO.getSeasonYear())) {
            throw ApiInstanceAlreadyExistsException.of(
                    "Season", "seasonYear", seasonDTO.getSeasonYear().toString());
        }
        Season season = seasonMapper.toSeason(seasonDTO);
        seasonRepository.save(season);
        return seasonMapper.toSeasonDTO(season);
    }

    public ResponsePage<SeasonDTO> getSeasons(
            Pageable pageable, Year seasonYear, MultiValueMap<String, String> parameters) {
        Pageable handledPageable = handleQueryParameters(
                parameters, seasonQueryParameter, pageable, seasonSortPropertyMapper, SeasonDTO.class);
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
}
