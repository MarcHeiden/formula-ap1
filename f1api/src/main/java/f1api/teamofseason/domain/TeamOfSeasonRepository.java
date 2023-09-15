package f1api.teamofseason.domain;

import f1api.driver.domain.Driver;
import f1api.engine.domain.Engine;
import f1api.season.domain.Season;
import f1api.team.domain.Team;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamOfSeasonRepository extends JpaRepository<TeamOfSeason, UUID> {
    Boolean existsTeamOfSeasonBySeasonIdAndTeamId(UUID seasonId, UUID teamId);

    Page<TeamOfSeason> findTeamOfSeasonBySeasonIdAndTeamId(Pageable pageable, UUID seasonId, UUID teamId);

    Page<TeamOfSeason> findTeamOfSeasonsBySeasonId(Pageable pageable, UUID seasonId);

    Page<TeamOfSeason> findTeamOfSeasonsByTeamId(Pageable pageable, UUID teamId);

    @Query("select teamOfSeason.team from TeamOfSeason teamOfSeason where teamOfSeason.season = :season")
    Page<Team> findTeamsBySeason(Pageable pageable, @Param("season") Season season);

    @Query(
            "select SubQuery.driver from (select teamOfSeason.drivers as driver from TeamOfSeason teamOfSeason where teamOfSeason.season = :season) SubQuery")
    Page<Driver> findDriversBySeason(Pageable pageable, @Param("season") Season season);

    @Query("select teamOfSeason.engine from TeamOfSeason teamOfSeason where teamOfSeason.season = :season")
    Page<Engine> findEnginesBySeason(Pageable pageable, @Param("season") Season season);

    @Query(
            "select SubQuery.driver from (select teamOfSeason.drivers as driver from TeamOfSeason teamOfSeason where teamOfSeason.season = :season and teamOfSeason.team = :team) SubQuery")
    Page<Driver> findDriversBySeasonAndTeam(
            Pageable pageable, @Param("season") Season season, @Param("team") Team team);

    @Query(
            "select teamOfSeason.engine from TeamOfSeason teamOfSeason where teamOfSeason.season = :season and teamOfSeason.team = :team")
    Optional<Engine> findEngineBySeasonAndTeam(@Param("season") Season season, @Param("team") Team team);

    @Query(
            "select teamOfSeason.team from TeamOfSeason teamOfSeason join teamOfSeason.drivers as driver where teamOfSeason.season = :season and driver = :driver")
    Page<Team> findTeamsBySeasonAndDriver(
            Pageable pageable, @Param("season") Season season, @Param("driver") Driver driver);

    @Query(
            "select teamOfSeason.team from TeamOfSeason teamOfSeason where teamOfSeason.season = :season and teamOfSeason.engine = :engine")
    Page<Team> findTeamsBySeasonAndEngine(
            Pageable pageable, @Param("season") Season season, @Param("engine") Engine engine);

    @Query(
            "select SubQuery.driver from (select teamOfSeason.drivers as driver from TeamOfSeason teamOfSeason where teamOfSeason.team = :team) SubQuery")
    Page<Driver> findDriversByTeam(Pageable pageable, @Param("team") Team team);

    @Query("select teamOfSeason.engine from TeamOfSeason teamOfSeason where teamOfSeason.team = :team")
    Page<Engine> findEnginesByTeam(Pageable pageable, @Param("team") Team team);

    @Query("select teamOfSeason.season from TeamOfSeason teamOfSeason where teamOfSeason.team = :team")
    Page<Season> findSeasonsByTeam(Pageable pageable, @Param("team") Team team);

    @Query(
            "select teamOfSeason.season from TeamOfSeason teamOfSeason join teamOfSeason.drivers as driver where teamOfSeason.team = :team and driver = :driver")
    Page<Season> findSeasonsByTeamAndDriver(
            Pageable pageable, @Param("team") Team team, @Param("driver") Driver driver);

    @Query(
            "select teamOfSeason.season from TeamOfSeason teamOfSeason where teamOfSeason.team = :team and teamOfSeason.engine = :engine")
    Page<Season> findSeasonsByTeamAndEngine(
            Pageable pageable, @Param("team") Team team, @Param("engine") Engine engine);

    @Query(
            "select teamOfSeason.team from TeamOfSeason teamOfSeason join teamOfSeason.drivers as driver where driver = :driver")
    Page<Team> findTeamsByDriver(Pageable pageable, @Param("driver") Driver driver);

    @Query(
            "select teamOfSeason.season from TeamOfSeason teamOfSeason join teamOfSeason.drivers as driver where driver = :driver")
    Page<Season> findSeasonsByDriver(Pageable pageable, @Param("driver") Driver driver);

    @Query("select teamOfSeason.team from TeamOfSeason teamOfSeason where teamOfSeason.engine = :engine")
    Page<Team> findTeamsByEngine(Pageable pageable, @Param("engine") Engine engine);

    @Query("select teamOfSeason.season from TeamOfSeason teamOfSeason where teamOfSeason.engine = :engine")
    Page<Season> findSeasonsByEngine(Pageable pageable, @Param("engine") Engine engine);
}
