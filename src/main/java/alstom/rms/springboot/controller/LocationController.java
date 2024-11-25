package alstom.rms.springboot.controller;

import alstom.rms.springboot.model.Location;
import alstom.rms.springboot.service.LocationService;
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
    private LocationService locationService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping
    public List<Location> getAllLocations() {
        return locationService.getAllLocations();
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("{id}")
    public ResponseEntity<Location> getLocationById(@PathVariable @Valid UUID id) {
        return ResponseEntity.ok(locationService.getLocationById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Location createLocation(@RequestBody @Valid Location location) {
        return locationService.createLocation(location);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{id}")
    public ResponseEntity<Location> updateLocationById(@PathVariable UUID id, @RequestBody Location locationDetails) {
        return ResponseEntity.ok(locationService.updateLocationById(id, locationDetails));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteLocationById(@PathVariable UUID id) {
        locationService.deleteLocationById(id);
        return ResponseEntity.ok("Location data successfully deleted.");
    }

}
