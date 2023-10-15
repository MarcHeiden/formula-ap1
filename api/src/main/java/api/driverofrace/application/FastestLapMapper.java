package api.driverofrace.application;

import api.driverofrace.domain.FastestLap;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FastestLapMapper {
    @Mapping(target = "time", source = "fastestLap.timeAsString")
    FastestLapDTO toFastestLapDTO(FastestLap fastestLap, DriverInfoDTO driverInfoDTO);

    default FastestLap toFastestLap(FastestLapDTO fastestLapDTO) {
        return FastestLap.ofTimeString(fastestLapDTO.getTime());
    }
}
