package api.team.domain;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, UUID> {
    Boolean existsTeamByName(String name);

    Optional<Team> findTeamByName(String name);

    Page<Team> findTeamByName(Pageable pageable, String name);
}
