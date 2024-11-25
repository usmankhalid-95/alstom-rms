package alstom.rms.springboot.repository;

import alstom.rms.springboot.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PassengerRepository extends JpaRepository<Passenger, UUID> {
    Optional<Passenger> findByContactEmail(String email);

    Optional<Passenger> findByContactPhone(String phone);
}
