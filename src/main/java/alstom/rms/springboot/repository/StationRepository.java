package alstom.rms.springboot.repository;

import alstom.rms.springboot.model.Location;
import alstom.rms.springboot.model.Station;
import alstom.rms.springboot.model.Train;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StationRepository extends JpaRepository<Station, UUID> {
    Optional<Station> findByStationCode(String stationCode);

}

