package alstom.rms.springboot.controller;

import alstom.rms.springboot.exception.ResourceNotFoundException;
import alstom.rms.springboot.model.Schedule;
import alstom.rms.springboot.model.Station;
import alstom.rms.springboot.model.Ticket;
import alstom.rms.springboot.repository.ScheduleRepository;
import alstom.rms.springboot.repository.TicketRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Ticket> getAllTickets() {
        return removeContactDetailsFromTickets(ticketRepository.findAll());
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping
    public Ticket createTicket(@RequestBody @Valid Ticket ticket) {
        if (ticket.getSeatNumbers() == null || ticket.getSeatNumbers().isEmpty()) {
            throw new ResourceNotFoundException("At least one seat must be specified.");
        }

        Schedule schedule = scheduleRepository.findById(ticket.getSchedule().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id: " + ticket.getSchedule().getId()));

        int seatsToBook = ticket.getSeatNumbers().size();
        if (schedule.getAvailableSeats() < seatsToBook) {
            throw new ResourceNotFoundException("Not enough available seats for this booking.");
        }

        schedule.setAvailableSeats(schedule.getAvailableSeats() - seatsToBook);
        scheduleRepository.save(schedule);
        ticket.generateTicketNumber();
        return removeContactDetailsFromTickets(Collections.singletonList(ticketRepository.save(ticket))).get(0);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable UUID id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket doesn't exists with id:" + id));

        return ResponseEntity.ok(removeContactDetailsFromTickets(Collections.singletonList(ticket)).get(0));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping("{id}")
    @Transactional
    public ResponseEntity<Ticket> updateTicketById(@PathVariable UUID id, @RequestBody Ticket ticketDetails) {
        Ticket existingTicket = fetchTicketById(id);

        if (ticketDetails.getSeatNumbers() != null) {
            int seatDifference = ticketDetails.getSeatNumbers().size() - existingTicket.getSeatNumbers().size();
            adjustSeatsInSchedule(existingTicket.getSchedule(), seatDifference);
            existingTicket.setSeatNumbers(ticketDetails.getSeatNumbers());
        }

        if (ticketDetails.getSchedule() != null && !ticketDetails.getSchedule().getId().equals(existingTicket.getSchedule().getId())) {
            restoreSeatsToSchedule(existingTicket.getSchedule(), existingTicket.getSeatNumbers().size());
            adjustSeatsInSchedule(ticketDetails.getSchedule(), -existingTicket.getSeatNumbers().size());
            existingTicket.setSchedule(ticketDetails.getSchedule());
        }

        if (ticketDetails.getPassenger() != null) {
            existingTicket.setPassenger(ticketDetails.getPassenger());
        }
        if (ticketDetails.getDepartureDate() != null) {
            existingTicket.setDepartureDate(ticketDetails.getDepartureDate());
        }

        ticketRepository.save(existingTicket);
        return ResponseEntity.ok(existingTicket);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("{id}")
    @Transactional
    public ResponseEntity<String> deleteTicketById(@PathVariable UUID id) {
        Ticket existingTicket = fetchTicketById(id);
        restoreSeatsToSchedule(existingTicket.getSchedule(), existingTicket.getSeatNumbers().size());
        ticketRepository.delete(existingTicket);
        return ResponseEntity.ok(existingTicket.getTicketNumber() + " ticket data successfully deleted.");
    }

    private List<Ticket> removeContactDetailsFromTickets(List<Ticket> tickets) {
        return tickets.stream()
                .map(ticket -> {
                    Schedule schedule = ticket.getSchedule();
                    Station originStation = schedule.getOriginStation();
                    Station destinationStation = schedule.getDestinationStation();

                    if (originStation != null) {
                        originStation.setContacts(null);
                    }
                    if (destinationStation != null) {
                        destinationStation.setContacts(null);
                    }

                    Schedule modifiedSchedule = new Schedule(
                            schedule.getId(),
                            schedule.getTrain(),
                            originStation,
                            destinationStation,
                            schedule.getDepartureTime(),
                            schedule.getArrivalTime(),
                            schedule.getTicketPrice(),
                            schedule.getAvailableSeats()
                    );

                    return new Ticket(
                            ticket.getId(),
                            ticket.getTicketNumber(),
                            ticket.getSeatNumbers(),
                            ticket.getPassenger(),
                            modifiedSchedule,
                            ticket.getDepartureDate()
                    );
                })
                .collect(Collectors.toList());
    }

    private void adjustSeatsInSchedule(Schedule schedule, int seatDifference) {
        Schedule fetchedSchedule = fetchScheduleById(schedule.getId());
        int updatedSeats = fetchedSchedule.getAvailableSeats() - seatDifference;
        if (updatedSeats < 0) {
            throw new IllegalArgumentException("Not enough available seats in the schedule.");
        }
        fetchedSchedule.setAvailableSeats(updatedSeats);
        scheduleRepository.save(fetchedSchedule);
    }

    private void restoreSeatsToSchedule(Schedule schedule, int seatCount) {
        adjustSeatsInSchedule(schedule, -seatCount);
    }

    private Schedule fetchScheduleById(UUID scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id: " + scheduleId));
    }

    private Ticket fetchTicketById(UUID ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket doesn't exist with id: " + ticketId));
    }

}
