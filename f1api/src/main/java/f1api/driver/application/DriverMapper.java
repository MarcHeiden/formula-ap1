package f1api.driver.application;

import f1api.driver.domain.Driver;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DriverMapper {
    @Mapping(target = "driverId", source = "id")
    DriverDTO toDriverDTO(Driver driver);

    Driver toDriver(DriverDTO driverDTO);
}
