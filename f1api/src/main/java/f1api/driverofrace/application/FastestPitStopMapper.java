package f1api.driverofrace.application;

import f1api.driverofrace.domain.FastestPitStop;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FastestPitStopMapper {
    @Mapping(target = "duration", source = "fastestPitStop.durationAsString")
    FastestPitStopDTO toFastestPitStopDTO(FastestPitStop fastestPitStop, DriverInfoDTO driverInfoDTO);

    default FastestPitStop toFastestPitStop(FastestPitStopDTO fastestPitStopDTO) {
        return FastestPitStop.ofDurationString(fastestPitStopDTO.getDuration());
    }
}
