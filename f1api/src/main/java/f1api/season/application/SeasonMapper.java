package f1api.season.application;

import f1api.season.domain.Season;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SeasonMapper {
    @Mapping(target = "seasonId", source = "id")
    @Mapping(target = "seasonYear", source = "year")
    SeasonDTO toSeasonDTO(Season season);

    @Mapping(target = "year", source = "seasonYear")
    Season toSeason(SeasonDTO seasonDTO);
}
