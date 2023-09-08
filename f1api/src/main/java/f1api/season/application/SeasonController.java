package f1api.season.application;

import f1api.responsepage.ResponsePage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.time.Year;
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
public class SeasonController {
    private final SeasonApplicationService seasonApplicationService;

    @Autowired
    public SeasonController(SeasonApplicationService seasonApplicationService) {
        this.seasonApplicationService = seasonApplicationService;
    }

    @PostMapping("/seasons")
    public ResponseEntity<SeasonDTO> createSeason(@RequestBody @NotNull @Valid SeasonDTO seasonDTO) {
        SeasonDTO newSeasonDTO = seasonApplicationService.createSeason(seasonDTO);
        URI returnURI = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{seasonId}")
                .buildAndExpand(newSeasonDTO.getSeasonId())
                .toUri();
        return ResponseEntity.created(returnURI).body(newSeasonDTO);
    }

    @GetMapping("/seasons")
    public ResponsePage<SeasonDTO> getSeasons(
            Pageable pageable,
            @RequestParam(name = "seasonYear", required = false) Year seasonYear,
            @RequestParam MultiValueMap<String, String> parameters) {
        return seasonApplicationService.getSeasons(pageable, seasonYear, parameters);
    }

    @GetMapping("/seasons/{seasonId}")
    public SeasonDTO getSeason(@PathVariable @NotNull UUID seasonId) {
        return seasonApplicationService.getSeason(seasonId);
    }
}
