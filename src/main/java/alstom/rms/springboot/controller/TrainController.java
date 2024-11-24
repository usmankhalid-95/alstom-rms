package alstom.rms.springboot.controller;

import alstom.rms.springboot.exception.ResourceNotFoundException;
import alstom.rms.springboot.model.Train;
import alstom.rms.springboot.repository.TrainRepository;
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
    private TrainRepository trainRepository;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping
    public List<Train> getAllTrains() {
        return trainRepository.findAll();
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("{id}")
    public ResponseEntity<Train> getTrainById(@PathVariable UUID id) {
        Train train = trainRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Train doesn't exists with id:" + id));
        return ResponseEntity.ok(train);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Train createTrain(@RequestBody @Valid Train train) {
        return trainRepository.save(train);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{id}")
    public ResponseEntity<Train> updateTrainById(@PathVariable UUID id, @RequestBody Train trainDetails) {
        Train updateTrain = trainRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Train doesn't exists with id:" + id));
        if (trainDetails.getTrainNumber() != null) {
            updateTrain.setTrainNumber(trainDetails.getTrainNumber());
        }
        if (trainDetails.getSeatCapacity() != null) {
            updateTrain.setSeatCapacity(trainDetails.getSeatCapacity());
        }

        trainRepository.save(updateTrain);
        return ResponseEntity.ok(updateTrain);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteTrainById(@PathVariable UUID id) {
        Train updateTrain = trainRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Train doesn't exists with id:" + id));

        trainRepository.delete(updateTrain);
        return ResponseEntity.ok(updateTrain.getTrainNumber() + " train data successfully deleted.");
    }

}
