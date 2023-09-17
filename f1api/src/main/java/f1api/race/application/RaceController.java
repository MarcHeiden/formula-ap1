package f1api.race.application;

import f1api.responsepage.ResponsePage;
import f1api.validation.OnUpdate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
public class RaceController {
    private final RaceApplicationService raceApplicationService;

    @Autowired
    public RaceController(RaceApplicationService raceApplicationService) {
        this.raceApplicationService = raceApplicationService;
    }

    @GetMapping("/races")
    public ResponsePage<RaceDTO> getRaces(
            Pageable pageable,
            @RequestParam(name = "raceName", required = false) String raceName,
            @RequestParam(name = "date", required = false) LocalDate date,
            @RequestParam MultiValueMap<String, String> parameters) {
        return raceApplicationService.getRaces(pageable, raceName, date, parameters);
    }

    @GetMapping("/races/{raceId}")
    public RaceDTO getRace(@PathVariable @NotNull UUID raceId) {
        return raceApplicationService.getRace(raceId);
    }

    @Validated(OnUpdate.class)
    @PatchMapping("/races/{raceId}")
    public RaceDTO updateRace(@PathVariable @NotNull UUID raceId, @RequestBody @NotNull @Valid RaceDTO raceDTO) {
        return raceApplicationService.updateRace(raceId, raceDTO);
    }
}
