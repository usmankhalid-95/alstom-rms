package alstom.rms.springboot.controller;

import alstom.rms.springboot.model.Passenger;
import alstom.rms.springboot.service.PassengerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/passengers")
public class PassengerController {

    @Autowired
    private PassengerService passengerService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping
    public Passenger createPassenger(@RequestBody @Valid Passenger passenger) {
        return passengerService.createPassenger(passenger);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("{id}")
    public ResponseEntity<Passenger> getPassengerById(@PathVariable UUID id) {
        return ResponseEntity.ok(passengerService.getPassengerById(id));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping("{id}")
    public ResponseEntity<Passenger> updatePassengerById(@PathVariable UUID id, @RequestBody Passenger passengerDetails) {
        return ResponseEntity.ok(passengerService.updatePassengerById(id, passengerDetails));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("{id}")
    public ResponseEntity<String> deletePassengerById(@PathVariable UUID id) {
        passengerService.deletePassengerById(id);
        return ResponseEntity.ok("Passenger data successfully deleted.");
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/find-by-phone")
    public ResponseEntity<Passenger> findPassengerByContactPhone(@RequestParam String contactPhone) {
        return ResponseEntity.ok(passengerService.findPassengerByContactPhone(contactPhone));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/find-by-email")
    public ResponseEntity<Passenger> findPassengerByContactEmail(@RequestParam String contactEmail) {
        return ResponseEntity.ok(passengerService.findPassengerByContactEmail(contactEmail));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Passenger> getAllPassengers() {
        return passengerService.getAllPassengers();
    }

}
