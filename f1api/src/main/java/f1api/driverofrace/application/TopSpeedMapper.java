package f1api.driverofrace.application;

import f1api.driverofrace.domain.TopSpeed;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TopSpeedMapper {
    @Mapping(target = "speed", source = "topSpeed.speed")
    TopSpeedDTO toTopSpeedDTO(TopSpeed topSpeed, DriverInfoDTO driverInfoDTO);

    TopSpeed toTopSpeed(TopSpeedDTO topSpeedDTO);
}
