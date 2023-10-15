package api.driver.application;

import api.driver.domain.Driver;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface DriverMapper {
    @Named("toDriverDTO")
    @Mapping(target = "driverId", source = "id")
    DriverDTO toDriverDTO(Driver driver);

    Driver toDriver(DriverDTO driverDTO);
}
