package api.race.application;

import api.race.domain.Race;
import api.season.application.SeasonMapper;
import api.season.domain.Season;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = SeasonMapper.class)
public interface RaceMapper {
    @Mapping(target = "raceId", source = "id")
    @Mapping(target = "raceName", source = "name")
    @Mapping(target = "time", source = "time", qualifiedByName = "getTimeStringFromTime")
    @Mapping(target = "seasonDTO", source = "season", qualifiedByName = "toSeasonDTO")
    RaceDTO toRaceDTO(Race race);

    @Mapping(target = "name", source = "raceDTO.raceName")
    @Mapping(target = "date", source = "raceDTO.date")
    @Mapping(target = "time", source = "raceDTO.time", qualifiedByName = "getTimeFromTimeString")
    @Mapping(target = "cancelled", source = "raceDTO.cancelled")
    Race toRace(RaceDTO raceDTO, Season season);

    @Named("getTimeFromTimeString")
    default LocalTime getTimeFromTimeString(String timeString) {
        // remove UTC indication if present
        if (timeString.endsWith("Z")) {
            timeString = timeString.replace("Z", "");
        }
        return LocalTime.parse(timeString);
    }

    @Named("getTimeStringFromTime")
    default String getTimeStringFromTime(LocalTime time) {
        return time.format(DateTimeFormatter.ISO_TIME) + "Z";
    }
}
