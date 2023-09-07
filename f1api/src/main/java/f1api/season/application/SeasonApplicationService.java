package f1api.season.application;

import static f1api.sort.SortPropertiesHandler.handleSortProperties;

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

@Service
public class SeasonApplicationService {
    private final SeasonRepository seasonRepository;
    private final SeasonMapper seasonMapper;

    private final SeasonSortPropertiesMapper seasonSortMapper;

    @Autowired
    public SeasonApplicationService(
            SeasonRepository seasonRepository, SeasonMapper seasonMapper, SeasonSortPropertiesMapper seasonSortMapper) {
        this.seasonRepository = seasonRepository;
        this.seasonMapper = seasonMapper;
        this.seasonSortMapper = seasonSortMapper;
    }

    public SeasonDTO createSeason(SeasonDTO seasonDTO) {
        if (seasonRepository.existsSeasonByYear(seasonDTO.getSeasonYear())) {
            throw new ApiInstanceAlreadyExistsException(
                    "Season with the seasonYear '" + seasonDTO.getSeasonYear() + "' already exists.");
        }
        Season season = seasonMapper.toSeason(seasonDTO);
        seasonRepository.save(season);
        return seasonMapper.toSeasonDTO(season);
    }

    /*private Page<Season> getSeasonByYear(Pageable pageable, Year year){
        return seasonRepository.findSeasonByYear(pageable, year);
    }*/

    public ResponsePage<SeasonDTO> getSeasons(Pageable pageable, Year seasonYear) {
        Pageable handledPageable = handleSortProperties(pageable, seasonSortMapper);
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
                .orElseThrow(
                        () -> new ApiNotFoundException("Season with the seasonId '" + seasonId + "' does not exist."));
    }
}
