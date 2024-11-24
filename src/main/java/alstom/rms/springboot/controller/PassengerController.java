package alstom.rms.springboot.controller;

import alstom.rms.springboot.exception.ResourceNotFoundException;
import alstom.rms.springboot.model.Passenger;
import alstom.rms.springboot.repository.PassengerRepository;
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
    private PassengerRepository passengerRepository;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping
    public List<Passenger> getAllPassengers() {
        return passengerRepository.findAll();
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping
    public Passenger createPassenger(@RequestBody @Valid Passenger passenger) {
        return passengerRepository.save(passenger);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("{id}")
    public ResponseEntity<Passenger> getPassengerById(@PathVariable UUID id) {
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Passenger doesn't exists with id:" + id));
        return ResponseEntity.ok(passenger);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping("{id}")
    public ResponseEntity<Passenger> updatePassengerById(@PathVariable UUID id, @RequestBody Passenger passengerDetails) {
        Passenger updatePassenger = passengerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Passenger doesn't exists with id:" + id));

        if (passengerDetails.getPassengerName() != null) {
            updatePassenger.setPassengerName(passengerDetails.getPassengerName());
        }
        if (passengerDetails.getContactEmail() != null) {
            updatePassenger.setContactEmail(passengerDetails.getContactEmail());
        }
        if (passengerDetails.getContactPhone() != null) {
            updatePassenger.setContactPhone(passengerDetails.getContactPhone());
        }

        passengerRepository.save(updatePassenger);
        return ResponseEntity.ok(updatePassenger);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("{id}")
    public ResponseEntity<String> deletePassengerById(@PathVariable UUID id) {
        Passenger updatePassenger = passengerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Passenger doesn't exists with id:" + id));

        passengerRepository.delete(updatePassenger);
        return ResponseEntity.ok(updatePassenger.getPassengerName() + " passenger data successfully deleted.");
    }

}
