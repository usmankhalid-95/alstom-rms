package alstom.rms.springboot.controller;

import alstom.rms.springboot.exception.ResourceNotFoundException;
import alstom.rms.springboot.model.Location;
import alstom.rms.springboot.repository.LocationRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    @Autowired
    private LocationRepository locationRepository;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("{id}")
    public ResponseEntity<Location> getLocationById(@PathVariable @Valid UUID id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location doesn't exists with id:" + id));
        return ResponseEntity.ok(location);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Location createLocation(@RequestBody @Valid Location location) {
        return locationRepository.save(location);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{id}")
    public ResponseEntity<Location> updateLocationById(@PathVariable UUID id, @RequestBody Location locationDetails) {
        Location updateLocation = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location doesn't exists with id:" + id));

        if (locationDetails.getCity() != null) {
            updateLocation.setCity(locationDetails.getCity());
        }

        if (locationDetails.getState() != null) {
            updateLocation.setState(locationDetails.getState());
        }

        if (locationDetails.getZipCode() != null) {
            updateLocation.setZipCode(locationDetails.getZipCode());
        }

        locationRepository.save(updateLocation);
        return ResponseEntity.ok(updateLocation);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteLocationById(@PathVariable UUID id) {
        Location updateLocation = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location doesn't exists with id:" + id));

        locationRepository.delete(updateLocation);
        return ResponseEntity.ok(updateLocation.getCity() + " location data successfully deleted.");
    }

}
