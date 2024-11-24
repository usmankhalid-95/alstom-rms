package alstom.rms.springboot.repository;

import alstom.rms.springboot.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    Ticket findByTicketNumber(String trainNumber);
}

