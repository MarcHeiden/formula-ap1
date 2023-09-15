package f1api.season.application;

import f1api.driver.application.DriverDTO;
import f1api.engine.application.EngineDTO;
import f1api.responsepage.ResponsePage;
import f1api.team.application.TeamDTO;
import f1api.teamofseason.application.TeamOfSeasonApplicationService;
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
    private final TeamOfSeasonApplicationService teamOfSeasonApplicationService;

    @Autowired
    public SeasonController(
            SeasonApplicationService seasonApplicationService,
            TeamOfSeasonApplicationService teamOfSeasonApplicationService) {
        this.seasonApplicationService = seasonApplicationService;
        this.teamOfSeasonApplicationService = teamOfSeasonApplicationService;
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

    @PatchMapping("/seasons/{seasonId}")
    public SeasonDTO updateSeason(
            @PathVariable @NotNull UUID seasonId, @RequestBody @NotNull @Valid SeasonDTO seasonDTO) {
        return seasonApplicationService.updateSeason(seasonId, seasonDTO);
    }

    @GetMapping("/seasons/{seasonId}/teams")
    public ResponsePage<TeamDTO> getTeamsOfSeason(
            @PathVariable @NotNull UUID seasonId,
            Pageable pageable,
            @RequestParam MultiValueMap<String, String> parameters) {
        return teamOfSeasonApplicationService.getTeamsOfSeason(pageable, seasonId, parameters);
    }

    @GetMapping("/seasons/{seasonId}/teams/{teamId}/drivers")
    public ResponsePage<DriverDTO> getDriversOfTeamOfSeason(
            @PathVariable @NotNull UUID seasonId,
            @PathVariable @NotNull UUID teamId,
            Pageable pageable,
            @RequestParam MultiValueMap<String, String> parameters) {
        return teamOfSeasonApplicationService.getDriversOfTeamOfSeason(pageable, seasonId, teamId, parameters);
    }

    @GetMapping("/seasons/{seasonId}/teams/{teamId}/engine")
    public EngineDTO getEngineOfTeamOfSeason(@PathVariable @NotNull UUID seasonId, @PathVariable @NotNull UUID teamId) {
        return teamOfSeasonApplicationService.getEngineOfTeamOfSeason(seasonId, teamId);
    }

    @GetMapping("/seasons/{seasonId}/drivers")
    public ResponsePage<DriverDTO> getDriversOfSeason(
            @PathVariable @NotNull UUID seasonId,
            Pageable pageable,
            @RequestParam MultiValueMap<String, String> parameters) {
        return teamOfSeasonApplicationService.getDriversOfSeason(pageable, seasonId, parameters);
    }

    @GetMapping("/seasons/{seasonId}/drivers/{driverId}/teams")
    public ResponsePage<TeamDTO> getTeamsOfDriverOfSeason(
            @PathVariable @NotNull UUID seasonId,
            @PathVariable @NotNull UUID driverId,
            Pageable pageable,
            @RequestParam MultiValueMap<String, String> parameters) {
        return teamOfSeasonApplicationService.getTeamsOfDriverOfSeason(pageable, seasonId, driverId, parameters);
    }

    @GetMapping("/seasons/{seasonId}/engines")
    public ResponsePage<EngineDTO> getEnginesOfSeason(
            @PathVariable @NotNull UUID seasonId,
            Pageable pageable,
            @RequestParam MultiValueMap<String, String> parameters) {
        return teamOfSeasonApplicationService.getEnginesOfSeason(pageable, seasonId, parameters);
    }

    @GetMapping("/seasons/{seasonId}/engines/{engineId}/teams")
    public ResponsePage<TeamDTO> getTeamsOfEngineOfSeason(
            @PathVariable @NotNull UUID seasonId,
            @PathVariable @NotNull UUID engineId,
            Pageable pageable,
            @RequestParam MultiValueMap<String, String> parameters) {
        return teamOfSeasonApplicationService.getTeamsOfEngineOfSeason(pageable, seasonId, engineId, parameters);
    }
}
