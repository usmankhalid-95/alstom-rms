package alstom.rms.springboot.controller;

import alstom.rms.springboot.model.Ticket;
import alstom.rms.springboot.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Ticket> getAllTickets() {
        return ticketService.getAllTickets();
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable UUID id) {
        return ResponseEntity.ok(ticketService.getTicketById(id));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping
    public Ticket createTicket(@RequestBody @Valid Ticket ticket) {
        return ticketService.createTicket(ticket);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping("{id}")
    public ResponseEntity<Ticket> updateTicketById(@PathVariable UUID id, @RequestBody Ticket ticketDetails) {
        return ResponseEntity.ok(ticketService.updateTicketById(id, ticketDetails));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteTicketById(@PathVariable UUID id) {
        ticketService.deleteTicketById(id);
        return ResponseEntity.ok("Ticket data successfully deleted.");
    }

}
