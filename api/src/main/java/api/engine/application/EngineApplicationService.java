package api.engine.application;

import static api.queryparameter.QueryParameterHandler.handleQueryParameters;

import api.engine.domain.Engine;
import api.engine.domain.EngineRepository;
import api.exception.ApiInstanceAlreadyExistsException;
import api.exception.ApiNotFoundException;
import api.queryparameter.sort.DefaultSortPropertyMapper;
import api.responsepage.ResponsePage;
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
    private final DefaultSortPropertyMapper defaultSortPropertyMapper;

    @Autowired
    public EngineApplicationService(
            EngineRepository engineRepository,
            EngineMapper engineMapper,
            EngineQueryParameter engineQueryParameter,
            DefaultSortPropertyMapper defaultSortPropertyMapper) {
        this.engineRepository = engineRepository;
        this.engineMapper = engineMapper;
        this.engineQueryParameter = engineQueryParameter;
        this.defaultSortPropertyMapper = defaultSortPropertyMapper;
    }

    private void throwApiInstanceAlreadyExistsException(String manufacturer) {
        throw ApiInstanceAlreadyExistsException.of("Engine", "manufacturer", manufacturer);
    }

    private void checkIfEngineAlreadyExists(String manufacturer) {
        if (engineRepository.existsEngineByManufacturer(manufacturer)) {
            throwApiInstanceAlreadyExistsException(manufacturer);
        }
    }

    private void checkIfDifferentEngineAlreadyExists(Engine engine, String manufacturer) {
        engineRepository.findEngineByManufacturer(manufacturer).ifPresent(foundEngine -> {
            if (!engine.getId().equals(foundEngine.getId())) {
                throwApiInstanceAlreadyExistsException(manufacturer);
            }
        });
    }

    public EngineDTO createEngine(EngineDTO engineDTO) {
        checkIfEngineAlreadyExists(engineDTO.getManufacturer());
        Engine engine = engineMapper.toEngine(engineDTO);
        engineRepository.save(engine);
        return engineMapper.toEngineDTO(engine);
    }

    public ResponsePage<EngineDTO> getEngines(
            Pageable pageable, String manufacturer, MultiValueMap<String, String> parameters) {
        Pageable handledPageable = handleQueryParameters(
                parameters, engineQueryParameter, pageable, defaultSortPropertyMapper, EngineDTO.getProperties());
        Page<Engine> engines;
        if (manufacturer != null) {
            engines = engineRepository.findEngineByManufacturer(handledPageable, manufacturer);
        } else {
            engines = engineRepository.findAll(handledPageable);
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
        checkIfDifferentEngineAlreadyExists(engine, engineDTO.getManufacturer());
        engine.setManufacturer(engineDTO.getManufacturer());
        engineRepository.save(engine);
        return engineMapper.toEngineDTO(engine);
    }
}
