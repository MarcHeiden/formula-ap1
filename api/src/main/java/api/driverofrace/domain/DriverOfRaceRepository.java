package api.driverofrace.domain;

import api.driver.domain.Driver;
import api.race.domain.Race;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverOfRaceRepository extends JpaRepository<DriverOfRace, UUID> {
    Boolean existsDriverOfRaceByRaceAndDriver(Race race, Driver driver);

    Boolean existsDriverOfRaceByRaceAndFastestPitStopIsNotNull(Race race);

    Optional<DriverOfRace> findDriverOfRaceByRaceAndDriver(Race race, Driver driver);

    Optional<DriverOfRace> findDriverOfRaceByRaceAndDriverAndRaceResultIsNotNull(Race race, Driver driver);

    Optional<DriverOfRace> findDriverOfRaceByRaceAndDriverAndFastestLapIsNotNull(Race race, Driver driver);

    Optional<DriverOfRace> findDriverOfRaceByRaceAndDriverAndTopSpeedIsNotNull(Race race, Driver driver);

    Optional<DriverOfRace> findDriverOfRaceByRaceAndDriverAndLeadingLapsIsNotNull(Race race, Driver driver);

    Optional<DriverOfRace> findDriverOfRaceByRaceAndFastestPitStopIsNotNull(Race race);

    Page<DriverOfRace> findDriverOfRacesByRace(Pageable pageable, Race race);

    Page<DriverOfRace> findDriverOfRaceByRaceAndDriverId(Pageable pageable, Race race, UUID driverId);

    Page<DriverOfRace> findDriverOfRacesByRaceAndRaceResultIsNotNull(Pageable pageable, Race race);

    Page<DriverOfRace> findDriverOfRaceByRaceAndDriverIdAndRaceResultIsNotNull(
            Pageable pageable, Race race, UUID driverId);

    Page<DriverOfRace> findDriverOfRacesByRaceAndFastestLapIsNotNull(Pageable pageable, Race race);

    Page<DriverOfRace> findDriverOfRaceByRaceAndDriverIdAndFastestLapIsNotNull(
            Pageable pageable, Race race, UUID driverId);

    Page<DriverOfRace> findDriverOfRacesByRaceAndTopSpeedIsNotNull(Pageable pageable, Race race);

    Page<DriverOfRace> findDriverOfRaceByRaceAndDriverIdAndTopSpeedIsNotNull(
            Pageable pageable, Race race, UUID driverId);

    Page<DriverOfRace> findDriverOfRacesByRaceAndLeadingLapsIsNotNull(Pageable pageable, Race race);

    Page<DriverOfRace> findDriverOfRaceByRaceAndDriverIdAndLeadingLapsIsNotNull(
            Pageable pageable, Race race, UUID driverId);
}
