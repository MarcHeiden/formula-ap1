package f1api.driver.application;

import f1api.responsepage.ResponsePage;
import f1api.season.application.SeasonDTO;
import f1api.team.application.TeamDTO;
import f1api.teamofseason.application.TeamOfSeasonApplicationService;
import f1api.validation.OnCreate;
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
public class DriverController {
    private final DriverApplicationService driverApplicationService;
    private final TeamOfSeasonApplicationService teamOfSeasonApplicationService;

    @Autowired
    public DriverController(
            DriverApplicationService driverApplicationService,
            TeamOfSeasonApplicationService teamOfSeasonApplicationService) {
        this.driverApplicationService = driverApplicationService;
        this.teamOfSeasonApplicationService = teamOfSeasonApplicationService;
    }

    @Validated(OnCreate.class)
    @PostMapping("/drivers")
    public ResponseEntity<DriverDTO> createDriver(@RequestBody @NotNull @Valid DriverDTO driverDTO) {
        DriverDTO newDriverDTO = driverApplicationService.createDriver(driverDTO);
        URI returnURI = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{driverId}")
                .buildAndExpand(newDriverDTO.getDriverId())
                .toUri();
        return ResponseEntity.created(returnURI).body(newDriverDTO);
    }

    @GetMapping("/drivers")
    public ResponsePage<DriverDTO> getDrivers(
            Pageable pageable,
            @RequestParam(name = "firstName", required = false) String firstName,
            @RequestParam(name = "lastName", required = false) String lastName,
            @RequestParam MultiValueMap<String, String> parameters) {
        return driverApplicationService.getDrivers(pageable, firstName, lastName, parameters);
    }

    @GetMapping("/drivers/{driverId}")
    public DriverDTO getDriver(@PathVariable @NotNull UUID driverId) {
        return driverApplicationService.getDriver(driverId);
    }

    @PatchMapping("/drivers/{driverId}")
    public DriverDTO updateDriver(@PathVariable @NotNull UUID driverId, @RequestBody @NotNull DriverDTO driverDTO) {
        return driverApplicationService.updateDriver(driverId, driverDTO);
    }

    @GetMapping("/drivers/{driverId}/teams")
    public ResponsePage<TeamDTO> getTeamsOfDriver(
            @PathVariable @NotNull UUID driverId,
            Pageable pageable,
            @RequestParam MultiValueMap<String, String> parameters) {
        return teamOfSeasonApplicationService.getTeamsOfDriver(driverId, pageable, parameters);
    }

    @GetMapping("/drivers/{driverId}/seasons")
    public ResponsePage<SeasonDTO> getSeasonsOfDriver(
            @PathVariable @NotNull UUID driverId,
            Pageable pageable,
            @RequestParam MultiValueMap<String, String> parameters) {
        return teamOfSeasonApplicationService.getSeasonsOfDriver(driverId, pageable, parameters);
    }
}
