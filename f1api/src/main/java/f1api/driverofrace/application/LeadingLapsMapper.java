package f1api.driverofrace.application;

import f1api.driverofrace.domain.LeadingLaps;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LeadingLapsMapper {
    @Mapping(target = "numberOfLaps", source = "leadingLaps.numberOfLaps")
    LeadingLapsDTO toLeadingLapsDTO(LeadingLaps leadingLaps, DriverInfoDTO driverInfoDTO);

    LeadingLaps toLeadingLaps(LeadingLapsDTO leadingLapsDTO);
}
