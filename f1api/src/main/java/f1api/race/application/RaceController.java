package f1api.race.application;

import f1api.driverofrace.application.*;
import f1api.responsepage.ResponsePage;
import f1api.validation.OnCreate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.time.LocalDate;
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
public class RaceController {
    private final RaceApplicationService raceApplicationService;
    private final DriverOfRaceApplicationService driverOfRaceApplicationService;

    @Autowired
    public RaceController(
            RaceApplicationService raceApplicationService,
            DriverOfRaceApplicationService driverOfRaceApplicationService) {
        this.raceApplicationService = raceApplicationService;
        this.driverOfRaceApplicationService = driverOfRaceApplicationService;
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

    @PatchMapping("/races/{raceId}")
    public RaceDTO updateRace(@PathVariable @NotNull UUID raceId, @RequestBody @NotNull @Valid RaceDTO raceDTO) {
        return raceApplicationService.updateRace(raceId, raceDTO);
    }

    @Validated(OnCreate.class)
    @PostMapping("/races/{raceId}/qualifying")
    public ResponseEntity<QualifyingResultDTO> createQualifyingResultForDriverOfRace(
            @PathVariable @NotNull UUID raceId, @RequestBody @NotNull @Valid QualifyingResultDTO qualifyingResultDTO) {
        QualifyingResultDTO newQualifyingResultDTO =
                driverOfRaceApplicationService.createQualifyingResultForDriverOfRace(raceId, qualifyingResultDTO);
        URI returnURI = ServletUriComponentsBuilder.fromCurrentRequest()
                .queryParam(
                        "driverId",
                        newQualifyingResultDTO.getDriverInfoDTO().getDriverDTO().getDriverId())
                .build()
                .toUri();
        return ResponseEntity.created(returnURI).body(newQualifyingResultDTO);
    }

    @GetMapping("/races/{raceId}/qualifying")
    public ResponsePage<QualifyingResultDTO> getQualifyingResultsOfRace(
            @PathVariable @NotNull UUID raceId,
            Pageable pageable,
            @RequestParam(name = "driverId", required = false) UUID driverId,
            @RequestParam MultiValueMap<String, String> parameters) {
        return driverOfRaceApplicationService.getQualifyingResultsOfRace(raceId, pageable, driverId, parameters);
    }

    @PatchMapping("/races/{raceId}/qualifying")
    public QualifyingResultDTO updateQualifyingResultForDriverOfRace(
            @PathVariable @NotNull UUID raceId,
            @RequestParam(name = "driverId") UUID driverId,
            @RequestBody @NotNull @Valid QualifyingResultDTO qualifyingResultDTO) {
        return driverOfRaceApplicationService.updateQualifyingResultForDriverOfRace(
                raceId, driverId, qualifyingResultDTO);
    }

    @Validated(OnCreate.class)
    @PostMapping("/races/{raceId}/result")
    public ResponseEntity<RaceResultDTO> createRaceResultForDriverOfRace(
            @PathVariable @NotNull UUID raceId, @RequestBody @NotNull @Valid RaceResultDTO raceResultDTO) {
        RaceResultDTO newRaceResultDTO =
                driverOfRaceApplicationService.createRaceResultForDriverOfRace(raceId, raceResultDTO);
        URI returnURI = ServletUriComponentsBuilder.fromCurrentRequest()
                .queryParam(
                        "driverId",
                        newRaceResultDTO.getDriverInfoDTO().getDriverDTO().getDriverId())
                .build()
                .toUri();
        return ResponseEntity.created(returnURI).body(newRaceResultDTO);
    }

    @GetMapping("/races/{raceId}/result")
    public ResponsePage<RaceResultDTO> getRaceResultsOfRace(
            @PathVariable @NotNull UUID raceId,
            Pageable pageable,
            @RequestParam(name = "driverId", required = false) UUID driverId,
            @RequestParam MultiValueMap<String, String> parameters) {
        return driverOfRaceApplicationService.getRaceResultsOfRace(raceId, pageable, driverId, parameters);
    }

    @PatchMapping("/races/{raceId}/result")
    public RaceResultDTO updateRaceResultForDriverOfRace(
            @PathVariable @NotNull UUID raceId,
            @RequestParam(name = "driverId") UUID driverId,
            @RequestBody @NotNull @Valid RaceResultDTO raceResultDTO) {
        return driverOfRaceApplicationService.updateRaceResultForDriverOfRace(raceId, driverId, raceResultDTO);
    }

    @Validated(OnCreate.class)
    @PostMapping("/races/{raceId}/fastestLaps")
    public ResponseEntity<FastestLapDTO> createFastestLapForDriverOfRace(
            @PathVariable @NotNull UUID raceId, @RequestBody @NotNull @Valid FastestLapDTO fastestLapDTO) {
        FastestLapDTO newFastestLapDTO =
                driverOfRaceApplicationService.createFastestLapForDriverOfRace(raceId, fastestLapDTO);
        URI returnURI = ServletUriComponentsBuilder.fromCurrentRequest()
                .queryParam(
                        "driverId",
                        newFastestLapDTO.getDriverInfoDTO().getDriverDTO().getDriverId())
                .build()
                .toUri();
        return ResponseEntity.created(returnURI).body(newFastestLapDTO);
    }

    @GetMapping("/races/{raceId}/fastestLaps")
    public ResponsePage<FastestLapDTO> getFastestLapsOfRace(
            @PathVariable @NotNull UUID raceId,
            Pageable pageable,
            @RequestParam(name = "driverId", required = false) UUID driverId,
            @RequestParam MultiValueMap<String, String> parameters) {
        return driverOfRaceApplicationService.getFastestLapsOfRace(raceId, pageable, driverId, parameters);
    }

    @PatchMapping("/races/{raceId}/fastestLaps")
    public FastestLapDTO updateFastestLapForDriverOfRace(
            @PathVariable @NotNull UUID raceId,
            @RequestParam(name = "driverId") UUID driverId,
            @RequestBody @NotNull @Valid FastestLapDTO fastestLapDTO) {
        return driverOfRaceApplicationService.updateFastestLapForDriverOfRace(raceId, driverId, fastestLapDTO);
    }

    @Validated(OnCreate.class)
    @PostMapping("/races/{raceId}/topSpeeds")
    public ResponseEntity<TopSpeedDTO> createTopSpeedForDriverOfRace(
            @PathVariable @NotNull UUID raceId, @RequestBody @NotNull @Valid TopSpeedDTO topSpeedDTO) {
        TopSpeedDTO newTopSpeedDTO = driverOfRaceApplicationService.createTopSpeedForDriverOfRace(raceId, topSpeedDTO);
        URI returnURI = ServletUriComponentsBuilder.fromCurrentRequest()
                .queryParam(
                        "driverId",
                        newTopSpeedDTO.getDriverInfoDTO().getDriverDTO().getDriverId())
                .build()
                .toUri();
        return ResponseEntity.created(returnURI).body(newTopSpeedDTO);
    }

    @GetMapping("/races/{raceId}/topSpeeds")
    public ResponsePage<TopSpeedDTO> getTopSpeedsOfRace(
            @PathVariable @NotNull UUID raceId,
            Pageable pageable,
            @RequestParam(name = "driverId", required = false) UUID driverId,
            @RequestParam MultiValueMap<String, String> parameters) {
        return driverOfRaceApplicationService.getTopSpeedsOfRace(raceId, pageable, driverId, parameters);
    }

    @PatchMapping("/races/{raceId}/topSpeeds")
    public TopSpeedDTO updateTopSpeedForDriverOfRace(
            @PathVariable @NotNull UUID raceId,
            @RequestParam(name = "driverId") UUID driverId,
            @RequestBody @NotNull @Valid TopSpeedDTO topSpeedDTO) {
        return driverOfRaceApplicationService.updateTopSpeedForDriverOfRace(raceId, driverId, topSpeedDTO);
    }

    @Validated(OnCreate.class)
    @PostMapping("/races/{raceId}/leadingLaps")
    public ResponseEntity<LeadingLapsDTO> createLeadingLapsForDriverOfRace(
            @PathVariable @NotNull UUID raceId, @RequestBody @NotNull @Valid LeadingLapsDTO leadingLapsDTO) {
        LeadingLapsDTO newLeadingLapsDTO =
                driverOfRaceApplicationService.createLeadingLapsForDriverOfRace(raceId, leadingLapsDTO);
        URI returnURI = ServletUriComponentsBuilder.fromCurrentRequest()
                .queryParam(
                        "driverId",
                        newLeadingLapsDTO.getDriverInfoDTO().getDriverDTO().getDriverId())
                .build()
                .toUri();
        return ResponseEntity.created(returnURI).body(newLeadingLapsDTO);
    }

    @GetMapping("/races/{raceId}/leadingLaps")
    public ResponsePage<LeadingLapsDTO> getLeadingLapsOfRace(
            @PathVariable @NotNull UUID raceId,
            Pageable pageable,
            @RequestParam(name = "driverId", required = false) UUID driverId,
            @RequestParam MultiValueMap<String, String> parameters) {
        return driverOfRaceApplicationService.getLeadingLapsOfRace(raceId, pageable, driverId, parameters);
    }

    @PatchMapping("/races/{raceId}/leadingLaps")
    public LeadingLapsDTO updateLeadingLapsForDriverOfRace(
            @PathVariable @NotNull UUID raceId,
            @RequestParam(name = "driverId") UUID driverId,
            @RequestBody @NotNull @Valid LeadingLapsDTO leadingLapsDTO) {
        return driverOfRaceApplicationService.updateLeadingLapsForDriverOfRace(raceId, driverId, leadingLapsDTO);
    }

    @Validated(OnCreate.class)
    @PostMapping("/races/{raceId}/fastestPitStop")
    public ResponseEntity<FastestPitStopDTO> createFastestPitStopOfRace(
            @PathVariable @NotNull UUID raceId, @RequestBody @NotNull @Valid FastestPitStopDTO fastestPitStopDTO) {
        FastestPitStopDTO newFastestPitStopDTO =
                driverOfRaceApplicationService.createFastestPitStopOfRace(raceId, fastestPitStopDTO);
        URI returnURI = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
        return ResponseEntity.created(returnURI).body(newFastestPitStopDTO);
    }

    @GetMapping("/races/{raceId}/fastestPitStop")
    public FastestPitStopDTO getFastestPitStopOfRace(@PathVariable @NotNull UUID raceId) {
        return driverOfRaceApplicationService.getFastestPitStopOfRace(raceId);
    }

    @PatchMapping("/races/{raceId}/fastestPitStop")
    public FastestPitStopDTO updateFastestPitStopOfRace(
            @PathVariable @NotNull UUID raceId, @RequestBody @NotNull @Valid FastestPitStopDTO fastestPitStopDTO) {
        return driverOfRaceApplicationService.updateFastestPitStopOfRace(raceId, fastestPitStopDTO);
    }
}
