package alstom.rms.springboot.service;

import alstom.rms.springboot.exception.CustomException;
import alstom.rms.springboot.model.Location;
import alstom.rms.springboot.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    public Location getLocationById(UUID id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new CustomException("Location doesn't exist with id: " + id));
    }

    public Location createLocation(Location location) {
        checkZipCodeUniqueness(location.getZipCode(), null);
        return locationRepository.save(location);
    }

    public Location updateLocationById(UUID id, Location locationDetails) {
        Location existingLocation = locationRepository.findById(id)
                .orElseThrow(() -> new CustomException("Location doesn't exist with id: " + id));

        updateLocationDetails(locationDetails, existingLocation);
        return locationRepository.save(existingLocation);
    }

    public void deleteLocationById(UUID id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new CustomException("Location doesn't exist with id: " + id));

        locationRepository.delete(location);
    }

    private void updateLocationDetails(Location locationDetails, Location existingLocation) {
        if (locationDetails.getCity() != null) {
            existingLocation.setCity(locationDetails.getCity());
        }

        if (locationDetails.getState() != null) {
            existingLocation.setState(locationDetails.getState());
        }

        if (locationDetails.getZipCode() != null && !locationDetails.getZipCode().equals(existingLocation.getZipCode())) {
            checkZipCodeUniqueness(locationDetails.getZipCode(), existingLocation.getId());
            existingLocation.setZipCode(locationDetails.getZipCode());
        }

    }

    private void checkZipCodeUniqueness(String zipCode, UUID currentLocationId) {
        Optional<Location> existingLocation = locationRepository.findByZipCode(zipCode);
        existingLocation.ifPresent(location -> {
            if (currentLocationId == null || !location.getId().equals(currentLocationId)) {
                throw new CustomException("A location with zip code " + zipCode + " already exists.");
            }
        });
    }

}
