package alstom.rms.springboot.controller;

import alstom.rms.springboot.model.Station;
import alstom.rms.springboot.service.StationService;
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
    private StationService stationService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping
    public List<Station> getAllStations() {
        return stationService.getAllStations();
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("{id}")
    public ResponseEntity<Station> getStationById(@PathVariable UUID id) {
        return ResponseEntity.ok(stationService.getStationById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Station createStation(@RequestBody @Valid Station station) {
        return stationService.createStation(station);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{id}")
    public ResponseEntity<Station> updateStationById(@PathVariable UUID id, @RequestBody Station stationDetails) {
        return ResponseEntity.ok(stationService.updateStationById(id, stationDetails));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteStationById(@PathVariable UUID id) {
        stationService.deleteStationById(id);
        return ResponseEntity.ok("Station data successfully deleted.");
    }

}
