package f1api.team.application;

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
public class TeamController {
    private final TeamApplicationService teamApplicationService;

    @Autowired
    public TeamController(TeamApplicationService teamApplicationService) {
        this.teamApplicationService = teamApplicationService;
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
}
