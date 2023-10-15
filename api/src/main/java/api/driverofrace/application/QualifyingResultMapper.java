package api.driverofrace.application;

import api.driverofrace.domain.QualifyingResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QualifyingResultMapper {
    @Mapping(target = "position", source = "qualifyingResult.position")
    QualifyingResultDTO toQualifyingResultDTO(QualifyingResult qualifyingResult, DriverInfoDTO driverInfoDTO);

    QualifyingResult toQualifyingResult(QualifyingResultDTO qualifyingResultDTO);
}
