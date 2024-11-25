package alstom.rms.springboot.service;

import alstom.rms.springboot.exception.CustomException;
import alstom.rms.springboot.model.Schedule;
import alstom.rms.springboot.model.Ticket;
import alstom.rms.springboot.repository.PassengerRepository;
import alstom.rms.springboot.repository.ScheduleRepository;
import alstom.rms.springboot.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private EntityValidationService entityValidationService;

    public List<Ticket> getAllTickets() {
        return removeContactDetailsFromTickets(ticketRepository.findAll());
    }

    public Ticket getTicketById(UUID id) {
        return removeContactDetailsFromTickets(Collections.singletonList(entityValidationService.fetchTicketById(id))).get(0);
    }

    public Ticket createTicket(Ticket ticket) {
        entityValidationService.validateTicketNumberUniqueness(ticket.getTicketNumber());

        Schedule schedule = entityValidationService.fetchScheduleById(ticket.getSchedule().getId());

        checkSeatAndDepartureDateUniqueness(ticket.getSeatNumbers(), ticket.getDepartureDate(), schedule.getId());

        passengerRepository.findById(ticket.getPassenger().getId())
                .orElseThrow(() -> new CustomException("Passenger not found with id: " + ticket.getPassenger().getId()));

        int seatsToBook = ticket.getSeatNumbers().size();
        if (schedule.getAvailableSeats() < seatsToBook) {
            throw new CustomException("Not enough available seats for this booking.");
        }

        schedule.setAvailableSeats(schedule.getAvailableSeats() - seatsToBook);
        scheduleRepository.save(schedule);

        ticket.generateTicketNumber();
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket updateTicketById(UUID id, Ticket ticketDetails) {
        Ticket existingTicket = entityValidationService.fetchTicketById(id);

        if (ticketDetails.getTicketNumber() != null &&
                !ticketDetails.getTicketNumber().equals(existingTicket.getTicketNumber())) {
            throw new CustomException("Ticket number cannot be modified.");
        }

        if (ticketDetails.getSeatNumbers() != null) {
            checkSeatAndDepartureDateUniqueness(ticketDetails.getSeatNumbers(), ticketDetails.getDepartureDate(), existingTicket.getSchedule().getId());
            int seatDifference = ticketDetails.getSeatNumbers().size() - existingTicket.getSeatNumbers().size();
            adjustSeatsInSchedule(existingTicket.getSchedule(), seatDifference);
            existingTicket.setSeatNumbers(ticketDetails.getSeatNumbers());
        }

        if (ticketDetails.getSchedule() != null &&
                !ticketDetails.getSchedule().getId().equals(existingTicket.getSchedule().getId())) {
            restoreSeatsToSchedule(existingTicket.getSchedule(), existingTicket.getSeatNumbers().size());
            adjustSeatsInSchedule(ticketDetails.getSchedule(), -existingTicket.getSeatNumbers().size());
            existingTicket.setSchedule(ticketDetails.getSchedule());
        }

        if (ticketDetails.getPassenger() != null) {
            passengerRepository.findById(ticketDetails.getPassenger().getId())
                    .orElseThrow(() -> new CustomException("Passenger not found with id: " + ticketDetails.getPassenger().getId()));
            existingTicket.setPassenger(ticketDetails.getPassenger());
        }

        if (ticketDetails.getDepartureDate() != null) {
            existingTicket.setDepartureDate(ticketDetails.getDepartureDate());
        }

        return ticketRepository.save(existingTicket);
    }

    @Transactional
    public void deleteTicketById(UUID id) {
        Ticket existingTicket = entityValidationService.fetchTicketById(id);
        restoreSeatsToSchedule(existingTicket.getSchedule(), existingTicket.getSeatNumbers().size());
        ticketRepository.delete(existingTicket);
    }

    private void adjustSeatsInSchedule(Schedule schedule, int seatDifference) {
        Schedule fetchedSchedule = entityValidationService.fetchScheduleById(schedule.getId());
        int updatedSeats = fetchedSchedule.getAvailableSeats() - seatDifference;
        if (updatedSeats < 0) {
            throw new CustomException("Not enough available seats in the schedule.");
        }
        fetchedSchedule.setAvailableSeats(updatedSeats);
        scheduleRepository.save(fetchedSchedule);
    }

    private void restoreSeatsToSchedule(Schedule schedule, int seatCount) {
        adjustSeatsInSchedule(schedule, -seatCount);
    }

    private List<Ticket> removeContactDetailsFromTickets(List<Ticket> tickets) {
        tickets.stream().forEach(ticket -> {
            ticket.getSchedule().getOriginStation().setContacts(null);
            ticket.getSchedule().getDestinationStation().setContacts(null);
        });
        return tickets;
    }

    private void checkSeatAndDepartureDateUniqueness(List<String> seatNumbers, LocalDate departureDate, UUID scheduleId) {
        List<Ticket> existingTickets = ticketRepository.findByScheduleIdAndDepartureDate(scheduleId, departureDate);
        for (Ticket existingTicket : existingTickets) {
            List<String> conflictingSeats = seatNumbers.stream()
                    .filter(existingTicket.getSeatNumbers()::contains)
                    .collect(Collectors.toList());

            if (!conflictingSeats.isEmpty()) {
                String conflictingSeatsList = String.join(" ", conflictingSeats);
                throw new CustomException("The seat numbers " + conflictingSeatsList + " are already booked for this schedule and departure date.");
            }
        }
    }

}
