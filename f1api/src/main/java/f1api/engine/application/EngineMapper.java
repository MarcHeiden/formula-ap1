package f1api.engine.application;

import f1api.engine.domain.Engine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EngineMapper {
    @Mapping(target = "engineId", source = "id")
    EngineDTO toEngineDTO(Engine engine);

    Engine toEngine(EngineDTO engineDTO);
}
