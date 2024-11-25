package alstom.rms.springboot.repository;

import alstom.rms.springboot.model.Schedule;
import alstom.rms.springboot.model.Station;
import alstom.rms.springboot.model.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {

    @Query("SELECT s FROM Schedule s WHERE s.train = :train AND s.originStation = :originStation AND s.destinationStation = :destinationStation")
    Schedule findByAllColumns(
            @Param("train") Train train,
            @Param("originStation") Station originStation,
            @Param("destinationStation") Station destinationStation);

}

