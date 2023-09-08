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

    private final SeasonSortPropertyMapper seasonSortMapper;

    @Autowired
    public SeasonApplicationService(
            SeasonRepository seasonRepository, SeasonMapper seasonMapper, SeasonSortPropertyMapper seasonSortMapper) {
        this.seasonRepository = seasonRepository;
        this.seasonMapper = seasonMapper;
        this.seasonSortMapper = seasonSortMapper;
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
        Pageable handledPageable = handleQueryParameters(pageable, seasonSortMapper, parameters, SeasonDTO.class);
        Page<Season> seasons;
        if (seasonYear != null) {
            seasons = seasonRepository.findSeasonByYear(handledPageable, seasonYear);
        } else {
            seasons = seasonRepository.findAll(handledPageable);
        }
        return ResponsePage.of(seasons.map(seasonMapper::toSeasonDTO));
    }

    public SeasonDTO getSeason(UUID seasonId) {
        return seasonRepository
                .findById(seasonId)
                .map(seasonMapper::toSeasonDTO)
                .orElseThrow(() -> ApiNotFoundException.of("Season", "seasonId", seasonId.toString()));
    }
}
