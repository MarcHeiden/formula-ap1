package f1api.teamofseason.domain;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamOfSeasonRepository extends JpaRepository<TeamOfSeason, UUID> {
    Boolean existsTeamOfSeasonBySeasonIdAndTeamId(UUID seasonId, UUID teamId);

    Optional<TeamOfSeason> findTeamOfSeasonBySeasonIdAndTeamId(UUID seasonId, UUID teamId);

    Page<TeamOfSeason> findTeamOfSeasonBySeasonIdAndTeamId(Pageable pageable, UUID seasonId, UUID teamId);

    Page<TeamOfSeason> findTeamOfSeasonsBySeasonId(Pageable pageable, UUID seasonId);

    Page<TeamOfSeason> findTeamOfSeasonsByTeamId(Pageable pageable, UUID teamId);
}
