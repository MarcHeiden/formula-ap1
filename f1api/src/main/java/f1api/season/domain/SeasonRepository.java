package f1api.season.domain;

import java.time.Year;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeasonRepository extends JpaRepository<Season, UUID> {

    Boolean existsSeasonByYear(Year year);

    Page<Season> findSeasonByYear(Pageable pageable, Year year);
}
