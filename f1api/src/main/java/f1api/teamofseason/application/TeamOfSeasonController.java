package f1api.teamofseason.application;

import f1api.responsepage.ResponsePage;
import f1api.validation.OnCreate;
import f1api.validation.OnUpdate;
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
public class TeamOfSeasonController {
    private final TeamOfSeasonApplicationService teamOfSeasonApplicationService;

    @Autowired
    public TeamOfSeasonController(TeamOfSeasonApplicationService teamOfSeasonApplicationService) {
        this.teamOfSeasonApplicationService = teamOfSeasonApplicationService;
    }

    @Validated(OnCreate.class)
    @PostMapping("/teamsOfSeasons")
    public ResponseEntity<TeamOfSeasonDTO> createTeamOfSeason(
            @RequestBody @NotNull @Valid TeamOfSeasonDTO teamOfSeasonDTO) {
        TeamOfSeasonDTO newTeamOfSeasonDTO = teamOfSeasonApplicationService.createTeamOfSeason(teamOfSeasonDTO);
        URI returnURI = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{teamOfSeasonId}")
                .buildAndExpand(newTeamOfSeasonDTO.getTeamOfSeasonId())
                .toUri();
        return ResponseEntity.created(returnURI).body(newTeamOfSeasonDTO);
    }

    @GetMapping("/teamsOfSeasons")
    public ResponsePage<TeamOfSeasonDTO> getTeamsOfSeasons(
            Pageable pageable,
            @RequestParam(name = "seasonId", required = false) UUID seasonId,
            @RequestParam(name = "teamId", required = false) UUID teamId,
            @RequestParam MultiValueMap<String, String> parameters) {
        return teamOfSeasonApplicationService.getTeamsOfSeasons(pageable, seasonId, teamId, parameters);
    }

    @GetMapping("/teamsOfSeasons/{teamOfSeasonId}")
    public TeamOfSeasonDTO getTeamOfSeason(@PathVariable @NotNull UUID teamOfSeasonId) {
        return teamOfSeasonApplicationService.getTeamOfSeason(teamOfSeasonId);
    }

    @Validated(OnUpdate.class)
    @PatchMapping("/teamsOfSeasons/{teamOfSeasonId}")
    public TeamOfSeasonDTO updateTeamOfSeason(
            @PathVariable @NotNull UUID teamOfSeasonId, @RequestBody @NotNull @Valid TeamOfSeasonDTO teamOfSeasonDTO) {
        return teamOfSeasonApplicationService.updateTeamOfSeason(teamOfSeasonId, teamOfSeasonDTO);
    }
}
