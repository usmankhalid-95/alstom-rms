package alstom.rms.springboot.service;

import alstom.rms.springboot.exception.CustomException;
import alstom.rms.springboot.model.Location;
import alstom.rms.springboot.model.Station;
import alstom.rms.springboot.repository.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class StationService {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private EntityValidationService entityValidationService;

    public List<Station> getAllStations() {
        return stationRepository.findAll();
    }

    public Station getStationById(UUID id) {
        return entityValidationService.fetchStationById(id);
    }

    public Station createStation(Station station) {
        if (station.getLocation().getId() == null) {
            throw new CustomException("Location id cannot be null");
        }

        entityValidationService.validateStationCodeUniqueness(station.getStationCode(), null);

        Location location = entityValidationService.fetchLocationById(station.getLocation().getId());
        station.setLocation(location);

        return stationRepository.save(station);
    }

    public Station updateStationById(UUID id, Station stationDetails) {
        Station updateStation = entityValidationService.fetchStationById(id);

        if (stationDetails.getStationCode() != null && !stationDetails.getStationCode().equals(updateStation.getStationCode())) {
            entityValidationService.validateStationCodeUniqueness(stationDetails.getStationCode(), id);
            updateStation.setStationCode(stationDetails.getStationCode());
        }

        if (stationDetails.getLocation() != null) {
            Location location = entityValidationService.fetchLocationById(stationDetails.getLocation().getId());
            updateStation.setLocation(location);
        }

        if (stationDetails.getStationName() != null) {
            updateStation.setStationName(stationDetails.getStationName());
        }

        return stationRepository.save(updateStation);
    }

    public void deleteStationById(UUID id) {
        Station station = entityValidationService.fetchStationById(id);
        stationRepository.delete(station);
    }
}
