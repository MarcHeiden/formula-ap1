package f1api.engine.application;

import static f1api.queryparameter.QueryParameterHandler.handleQueryParameters;

import f1api.engine.domain.Engine;
import f1api.engine.domain.EngineRepository;
import f1api.exception.ApiInstanceAlreadyExistsException;
import f1api.exception.ApiNotFoundException;
import f1api.responsepage.ResponsePage;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

@Service
public class EngineApplicationService {
    private final EngineRepository engineRepository;
    private final EngineMapper engineMapper;
    private final EngineQueryParameter engineQueryParameter;

    @Autowired
    public EngineApplicationService(
            EngineRepository engineRepository, EngineMapper engineMapper, EngineQueryParameter engineQueryParameter) {
        this.engineRepository = engineRepository;
        this.engineMapper = engineMapper;
        this.engineQueryParameter = engineQueryParameter;
    }

    private void checkIfEngineAlreadyExists(String manufacturer) {
        if (engineRepository.existsEngineByManufacturer(manufacturer)) {
            throw ApiInstanceAlreadyExistsException.of("Engine", "manufacturer", manufacturer);
        }
    }

    public EngineDTO createEngine(EngineDTO engineDTO) {
        checkIfEngineAlreadyExists(engineDTO.getManufacturer());
        Engine engine = engineMapper.toEngine(engineDTO);
        engineRepository.save(engine);
        return engineMapper.toEngineDTO(engine);
    }

    public ResponsePage<EngineDTO> getEngines(
            Pageable pageable, String manufacturer, MultiValueMap<String, String> parameters) {
        handleQueryParameters(parameters, engineQueryParameter);
        Page<Engine> engines;
        if (manufacturer != null) {
            engines = engineRepository.findEngineByManufacturer(pageable, manufacturer);
        } else {
            engines = engineRepository.findAll(pageable);
        }
        return ResponsePage.of(engines.map(engineMapper::toEngineDTO));
    }

    public Engine getEngineById(UUID id) {
        return engineRepository
                .findById(id)
                .orElseThrow(() -> ApiNotFoundException.of("Engine", "engineId", id.toString()));
    }

    public EngineDTO getEngine(UUID engineId) {
        return engineMapper.toEngineDTO(getEngineById(engineId));
    }

    public EngineDTO updateEngine(UUID engineId, EngineDTO engineDTO) {
        Engine engine = getEngineById(engineId);
        checkIfEngineAlreadyExists(engineDTO.getManufacturer());
        engine.setManufacturer(engineDTO.getManufacturer());
        engineRepository.save(engine);
        return engineMapper.toEngineDTO(engine);
    }
}
