package api.season.application;

import api.driver.application.DriverDTO;
import api.engine.application.EngineDTO;
import api.race.application.RaceApplicationService;
import api.race.application.RaceDTO;
import api.responsepage.ResponsePage;
import api.team.application.TeamDTO;
import api.teamofseason.application.TeamOfSeasonApplicationService;
import api.validation.OnCreate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.time.LocalDate;
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
    private final RaceApplicationService raceApplicationService;

    @Autowired
    public SeasonController(
            SeasonApplicationService seasonApplicationService,
            TeamOfSeasonApplicationService teamOfSeasonApplicationService,
            RaceApplicationService raceApplicationService) {
        this.seasonApplicationService = seasonApplicationService;
        this.teamOfSeasonApplicationService = teamOfSeasonApplicationService;
        this.raceApplicationService = raceApplicationService;
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

    @Validated(OnCreate.class)
    @PostMapping("/seasons/{seasonId}/races")
    public ResponseEntity<RaceDTO> createRaceOfSeason(
            @PathVariable @NotNull UUID seasonId, @RequestBody @NotNull @Valid RaceDTO raceDTO) {
        RaceDTO newRaceDTO = raceApplicationService.createRaceOfSeason(seasonId, raceDTO);
        URI returnURI = ServletUriComponentsBuilder.fromCurrentRequest()
                .replacePath("/races/{raceId}")
                .buildAndExpand(newRaceDTO.getRaceId())
                .toUri();
        return ResponseEntity.created(returnURI).body(newRaceDTO);
    }

    @GetMapping("/seasons/{seasonId}/races")
    public ResponsePage<RaceDTO> getRacesOfSeason(
            @PathVariable @NotNull UUID seasonId,
            Pageable pageable,
            @RequestParam(name = "raceName", required = false) String raceName,
            @RequestParam(name = "date", required = false) LocalDate date,
            @RequestParam MultiValueMap<String, String> parameters) {
        return raceApplicationService.getRacesOfSeason(seasonId, pageable, raceName, date, parameters);
    }

    @GetMapping("/seasons/{seasonId}/teams")
    public ResponsePage<TeamDTO> getTeamsOfSeason(
            @PathVariable @NotNull UUID seasonId,
            Pageable pageable,
            @RequestParam MultiValueMap<String, String> parameters) {
        return teamOfSeasonApplicationService.getTeamsOfSeason(seasonId, pageable, parameters);
    }

    @GetMapping("/seasons/{seasonId}/teams/{teamId}/drivers")
    public ResponsePage<DriverDTO> getDriversOfTeamOfSeason(
            @PathVariable @NotNull UUID seasonId,
            @PathVariable @NotNull UUID teamId,
            Pageable pageable,
            @RequestParam MultiValueMap<String, String> parameters) {
        return teamOfSeasonApplicationService.getDriversOfTeamOfSeason(seasonId, teamId, pageable, parameters);
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
        return teamOfSeasonApplicationService.getDriversOfSeason(seasonId, pageable, parameters);
    }

    @GetMapping("/seasons/{seasonId}/drivers/{driverId}/teams")
    public ResponsePage<TeamDTO> getTeamsOfDriverOfSeason(
            @PathVariable @NotNull UUID seasonId,
            @PathVariable @NotNull UUID driverId,
            Pageable pageable,
            @RequestParam MultiValueMap<String, String> parameters) {
        return teamOfSeasonApplicationService.getTeamsOfDriverOfSeason(seasonId, driverId, pageable, parameters);
    }

    @GetMapping("/seasons/{seasonId}/engines")
    public ResponsePage<EngineDTO> getEnginesOfSeason(
            @PathVariable @NotNull UUID seasonId,
            Pageable pageable,
            @RequestParam MultiValueMap<String, String> parameters) {
        return teamOfSeasonApplicationService.getEnginesOfSeason(seasonId, pageable, parameters);
    }

    @GetMapping("/seasons/{seasonId}/engines/{engineId}/teams")
    public ResponsePage<TeamDTO> getTeamsOfEngineOfSeason(
            @PathVariable @NotNull UUID seasonId,
            @PathVariable @NotNull UUID engineId,
            Pageable pageable,
            @RequestParam MultiValueMap<String, String> parameters) {
        return teamOfSeasonApplicationService.getTeamsOfEngineOfSeason(seasonId, engineId, pageable, parameters);
    }
}
