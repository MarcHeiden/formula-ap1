package api.driver.domain;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverRepository extends JpaRepository<Driver, UUID> {
    Boolean existsDriverByFirstNameAndLastName(String firstName, String lastName);

    Optional<Driver> findDriverByFirstNameAndLastName(String firstName, String lastName);

    Page<Driver> findDriverByFirstNameAndLastName(Pageable pageable, String firstName, String lastName);

    Page<Driver> findDriversByFirstName(Pageable pageable, String firstName);

    Page<Driver> findDriversByLastName(Pageable pageable, String lastName);
}
