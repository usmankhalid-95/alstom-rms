package alstom.rms.springboot.service;

import alstom.rms.springboot.exception.CustomException;
import alstom.rms.springboot.model.Train;
import alstom.rms.springboot.repository.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TrainService {

    @Autowired
    private TrainRepository trainRepository;

    @Autowired
    private EntityValidationService entityValidationService;

    public List<Train> getAllTrains() {
        return trainRepository.findAll();
    }

    public Train getTrainById(UUID id) {
        return entityValidationService.fetchTrainById(id);
    }

    public Train createTrain(Train train) {
        if (trainRepository.findByTrainNumber(train.getTrainNumber()) != null) {
            throw new CustomException("A train with the train number " + train.getTrainNumber() + " already exists.");
        }
        return trainRepository.save(train);
    }

    public Train updateTrainById(UUID id, Train trainDetails) {
        Train updateTrain = entityValidationService.fetchTrainById(id);

        if (trainDetails.getTrainNumber() != null &&
                !trainDetails.getTrainNumber().equals(updateTrain.getTrainNumber())) {

            Train existingTrain = trainRepository.findByTrainNumber(trainDetails.getTrainNumber());
            if (existingTrain != null && !existingTrain.getId().equals(id)) {
                throw new CustomException("A train with the train number " + trainDetails.getTrainNumber() + " already exists.");
            }

            updateTrain.setTrainNumber(trainDetails.getTrainNumber());
        }

        if (trainDetails.getSeatCapacity() != null)
            updateTrain.setSeatCapacity(trainDetails.getSeatCapacity());

        return trainRepository.save(updateTrain);
    }

    public void deleteTrainById(UUID id) {
        Train train = entityValidationService.fetchTrainById(id);
        trainRepository.delete(train);
    }

}
