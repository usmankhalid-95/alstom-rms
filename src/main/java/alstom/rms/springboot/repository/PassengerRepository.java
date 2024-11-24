package alstom.rms.springboot.repository;

import alstom.rms.springboot.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PassengerRepository extends JpaRepository<Passenger, UUID> {
    Passenger findByContactEmail(String email);

}

