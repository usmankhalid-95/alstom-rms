package alstom.rms.springboot.controller;

import alstom.rms.springboot.exception.ResourceNotFoundException;
import alstom.rms.springboot.model.Location;
import alstom.rms.springboot.model.Station;
import alstom.rms.springboot.repository.LocationRepository;
import alstom.rms.springboot.repository.StationRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stations")
public class StationController {

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LocationRepository locationRepository;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping
    public List<Station> getAllStations() {
        return stationRepository.findAll();
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("{id}")
    public ResponseEntity<Station> getStationById(@PathVariable UUID id) {
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Station doesn't exists with id:" + id));
        return ResponseEntity.ok(station);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Station createStation(@RequestBody @Valid Station station) {
        if (station.getLocation().getId() == null) {
            throw new ResourceNotFoundException("location id cannot be null");
        }

        UUID locationId = UUID.fromString(String.valueOf(station.getLocation().getId()));
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with ID: " + locationId));
        station.setLocation(location);

        return stationRepository.save(station);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{id}")
    public ResponseEntity<Station> updateStationById(@PathVariable UUID id, @RequestBody Station stationDetails) {
        Station updateStation = stationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Station doesn't exists with id:" + id));

        if (stationDetails.getStationCode() != null) {
            updateStation.setStationCode(stationDetails.getStationCode());
        }
        if (stationDetails.getStationName() != null) {
            updateStation.setStationName(stationDetails.getStationName());
        }
        if (stationDetails.getLocation() != null) {
            updateStation.setLocation(stationDetails.getLocation());
        }

        stationRepository.save(updateStation);
        return ResponseEntity.ok(updateStation);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteStationById(@PathVariable UUID id) {
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Station doesn't exists with id:" + id));

        stationRepository.delete(station);
        return ResponseEntity.ok(station.getStationName() + " station data successfully deleted.");
    }

}
