package alstom.rms.springboot.repository;

import alstom.rms.springboot.model.Train;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TrainRepository extends JpaRepository<Train, UUID> {
    Train findByTrainNumber(String trainNumber);
}

