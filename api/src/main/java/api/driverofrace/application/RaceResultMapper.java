package api.driverofrace.application;

import api.driverofrace.domain.RaceResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RaceResultMapper {
    @Mapping(target = "position", source = "raceResult.position")
    @Mapping(target = "dnf", source = "raceResult.dnf")
    RaceResultDTO toRaceResultDTO(RaceResult raceResult, DriverInfoDTO driverInfoDTO);

    RaceResult toRaceResult(RaceResultDTO raceResultDTO);
}
