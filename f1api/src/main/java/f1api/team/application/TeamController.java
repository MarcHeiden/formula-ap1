package f1api.team.application;

import f1api.driver.application.DriverDTO;
import f1api.engine.application.EngineDTO;
import f1api.responsepage.ResponsePage;
import f1api.season.application.SeasonDTO;
import f1api.teamofseason.application.TeamOfSeasonApplicationService;
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
public class TeamController {
    private final TeamApplicationService teamApplicationService;
    private final TeamOfSeasonApplicationService teamOfSeasonApplicationService;

    @Autowired
    public TeamController(
            TeamApplicationService teamApplicationService,
            TeamOfSeasonApplicationService teamOfSeasonApplicationService) {
        this.teamApplicationService = teamApplicationService;
        this.teamOfSeasonApplicationService = teamOfSeasonApplicationService;
    }

    @PostMapping("/teams")
    public ResponseEntity<TeamDTO> createTeam(@RequestBody @NotNull @Valid TeamDTO teamDTO) {
        TeamDTO newTeamDTO = teamApplicationService.createTeam(teamDTO);
        URI returnURI = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{teamId}")
                .buildAndExpand(newTeamDTO.getTeamId())
                .toUri();
        return ResponseEntity.created(returnURI).body(newTeamDTO);
    }

    @GetMapping("/teams")
    public ResponsePage<TeamDTO> getTeams(
            Pageable pageable,
            @RequestParam(name = "teamName", required = false) String teamName,
            @RequestParam MultiValueMap<String, String> parameters) {
        return teamApplicationService.getTeams(pageable, teamName, parameters);
    }

    @GetMapping("/teams/{teamId}")
    public TeamDTO getTeam(@PathVariable @NotNull UUID teamId) {
        return teamApplicationService.getTeam(teamId);
    }

    @PatchMapping("/teams/{teamId}")
    public TeamDTO updateTeam(@PathVariable @NotNull UUID teamId, @RequestBody @NotNull @Valid TeamDTO teamDTO) {
        return teamApplicationService.updateTeam(teamId, teamDTO);
    }

    @GetMapping("/teams/{teamId}/drivers")
    public ResponsePage<DriverDTO> getDriversOfTeam(
            @PathVariable @NotNull UUID teamId,
            Pageable pageable,
            @RequestParam MultiValueMap<String, String> parameters) {
        return teamOfSeasonApplicationService.getDriversOfTeam(pageable, teamId, parameters);
    }

    @GetMapping("/teams/{teamId}/drivers/{driverId}/seasons")
    public ResponsePage<SeasonDTO> getSeasonsOfDriverOfTeam(
            @PathVariable @NotNull UUID teamId,
            @PathVariable @NotNull UUID driverId,
            Pageable pageable,
            @RequestParam MultiValueMap<String, String> parameters) {
        return teamOfSeasonApplicationService.getSeasonsOfDriverOfTeam(pageable, teamId, driverId, parameters);
    }

    @GetMapping("/teams/{teamId}/engines")
    public ResponsePage<EngineDTO> getEnginesOfTeam(
            @PathVariable @NotNull UUID teamId,
            Pageable pageable,
            @RequestParam MultiValueMap<String, String> parameters) {
        return teamOfSeasonApplicationService.getEnginesOfTeam(pageable, teamId, parameters);
    }

    @GetMapping("/teams/{teamId}/engines/{engineId}/seasons")
    public ResponsePage<SeasonDTO> getSeasonsOfEngineOfTeam(
            @PathVariable @NotNull UUID teamId,
            @PathVariable @NotNull UUID engineId,
            Pageable pageable,
            @RequestParam MultiValueMap<String, String> parameters) {
        return teamOfSeasonApplicationService.getSeasonsOfEngineOfTeam(pageable, teamId, engineId, parameters);
    }

    @GetMapping("/teams/{teamId}/seasons")
    public ResponsePage<SeasonDTO> getSeasonsOfTeam(
            @PathVariable @NotNull UUID teamId,
            Pageable pageable,
            @RequestParam MultiValueMap<String, String> parameters) {
        return teamOfSeasonApplicationService.getSeasonsOfTeam(pageable, teamId, parameters);
    }
}
