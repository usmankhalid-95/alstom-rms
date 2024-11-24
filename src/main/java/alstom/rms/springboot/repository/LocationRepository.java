package alstom.rms.springboot.repository;

import alstom.rms.springboot.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LocationRepository extends JpaRepository<Location, UUID> {
    Optional<Location> findByZipCode(String zipCode);

}

