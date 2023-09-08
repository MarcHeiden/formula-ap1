package f1api.engine.application;

import f1api.responsepage.ResponsePage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Validated
@RestController
public class EngineController {
    private final EngineApplicationService engineApplicationService;

    @Autowired
    public EngineController(EngineApplicationService engineApplicationService) {
        this.engineApplicationService = engineApplicationService;
    }

    @PostMapping("/engines")
    public ResponseEntity<EngineDTO> createEngine(@RequestBody @NotNull @Valid EngineDTO engineDTO) {
        EngineDTO newEngineDTO = this.engineApplicationService.createEngine(engineDTO);
        URI returnURI = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{engineId}")
                .buildAndExpand(newEngineDTO.getEngineId())
                .toUri();
        return ResponseEntity.created(returnURI).body(newEngineDTO);
    }

    @GetMapping("/engines")
    public ResponsePage<EngineDTO> getEngines(
            Pageable pageable,
            @RequestParam(name = "manufacturer", required = false) String manufacturer,
            @RequestParam MultiValueMap<String, String> parameters) {
        return engineApplicationService.getEngines(pageable, manufacturer, parameters);
    }

    @GetMapping("/engines/{engineId}")
    public EngineDTO getEngine(@PathVariable @NotNull UUID engineId) {
        return engineApplicationService.getEngine(engineId);
    }

    @PatchMapping("/engines/{engineId}")
    public EngineDTO updateEngine(
            @PathVariable @NotNull UUID engineId, @RequestBody @NotNull @Valid EngineDTO engineDTO) {
        return engineApplicationService.updateEngine(engineId, engineDTO);
    }
}
