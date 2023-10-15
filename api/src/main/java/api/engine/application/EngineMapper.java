package api.engine.application;

import api.engine.domain.Engine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface EngineMapper {
    @Named("toEngineDTO")
    @Mapping(target = "engineId", source = "id")
    EngineDTO toEngineDTO(Engine engine);

    Engine toEngine(EngineDTO engineDTO);
}
