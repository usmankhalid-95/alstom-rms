package alstom.rms.springboot.repository;

import alstom.rms.springboot.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    Optional<Ticket> findByTicketNumber(String ticketNumber);

    List<Ticket> findByScheduleIdAndDepartureDate(UUID scheduleId, LocalDate departureDate);

}

