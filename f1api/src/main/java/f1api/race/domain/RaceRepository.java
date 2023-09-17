package f1api.race.domain;

import f1api.season.domain.Season;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RaceRepository extends JpaRepository<Race, UUID> {
    Boolean existsRaceByDate(LocalDate date);

    Boolean existsRaceByNameAndSeason(String name, Season season);

    Optional<Race> findRaceByDate(LocalDate date);

    Optional<Race> findRaceByNameAndSeason(String name, Season season);

    Page<Race> findRacesBySeason(Pageable pageable, Season season);

    Page<Race> findRaceBySeasonAndNameAndDate(Pageable pageable, Season season, String name, LocalDate date);

    Page<Race> findRaceBySeasonAndName(Pageable pageable, Season season, String name);

    Page<Race> findRaceBySeasonAndDate(Pageable pageable, Season season, LocalDate date);

    Page<Race> findRaceByNameAndDate(Pageable pageable, String name, LocalDate date);

    Page<Race> findRacesByName(Pageable pageable, String name);

    Page<Race> findRaceByDate(Pageable pageable, LocalDate date);
}
