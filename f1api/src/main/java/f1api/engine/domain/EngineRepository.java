package f1api.engine.domain;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EngineRepository extends JpaRepository<Engine, UUID> {
    Boolean existsEngineByManufacturer(String manufacturer);

    Page<Engine> findEngineByManufacturer(Pageable pageable, String manufacturer);
}
