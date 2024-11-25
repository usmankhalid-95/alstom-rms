package alstom.rms.springboot.controller;

import alstom.rms.springboot.model.Train;
import alstom.rms.springboot.service.TrainService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/trains")
public class TrainController {

    @Autowired
    private TrainService trainService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping
    public List<Train> getAllTrains() {
        return trainService.getAllTrains();
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("{id}")
    public ResponseEntity<Train> getTrainById(@PathVariable UUID id) {
        return ResponseEntity.ok(trainService.getTrainById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Train createTrain(@RequestBody @Valid Train train) {
        return trainService.createTrain(train);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{id}")
    public ResponseEntity<Train> updateTrainById(@PathVariable UUID id, @RequestBody Train trainDetails) {
        return ResponseEntity.ok(trainService.updateTrainById(id, trainDetails));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteTrainById(@PathVariable UUID id) {
        trainService.deleteTrainById(id);
        return ResponseEntity.ok("Train data successfully deleted.");
    }

}
