package alstom.rms.springboot.service;

import alstom.rms.springboot.exception.CustomException;
import alstom.rms.springboot.model.Passenger;
import alstom.rms.springboot.repository.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PassengerService {

    @Autowired
    private PassengerRepository passengerRepository;

    public List<Passenger> getAllPassengers() {
        return passengerRepository.findAll();
    }

    public Passenger getPassengerById(UUID id) {
        return passengerRepository.findById(id)
                .orElseThrow(() -> new CustomException("Passenger doesn't exist with id: " + id));
    }

    public Passenger createPassenger(Passenger passenger) {
        checkContactUniqueness(passenger.getContactEmail(), passenger.getContactPhone(), null);
        return passengerRepository.save(passenger);
    }

    public Passenger updatePassengerById(UUID id, Passenger passengerDetails) {
        Passenger existingPassenger = passengerRepository.findById(id)
                .orElseThrow(() -> new CustomException("Passenger doesn't exist with id: " + id));

        updatePassengerDetails(existingPassenger, passengerDetails, id);
        return passengerRepository.save(existingPassenger);
    }

    public void deletePassengerById(UUID id) {
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new CustomException("Passenger doesn't exist with id: " + id));

        passengerRepository.delete(passenger);
    }

    private void updatePassengerDetails(Passenger existingPassenger, Passenger passengerDetails, UUID id) {
        if (passengerDetails.getContactEmail() != null) {
            checkContactEmailUniqueness(passengerDetails.getContactEmail(), id);
            existingPassenger.setContactEmail(passengerDetails.getContactEmail());
        }

        if (passengerDetails.getContactPhone() != null) {
            checkContactPhoneUniqueness(passengerDetails.getContactPhone(), id);
            existingPassenger.setContactPhone(passengerDetails.getContactPhone());
        }

        if (passengerDetails.getPassengerName() != null) {
            existingPassenger.setPassengerName(passengerDetails.getPassengerName());
        }
    }

    private void checkContactUniqueness(String email, String phone, UUID currentPassengerId) {
        checkContactEmailUniqueness(email, currentPassengerId);
        checkContactPhoneUniqueness(phone, currentPassengerId);
    }

    private void checkContactEmailUniqueness(String email, UUID currentPassengerId) {
        Optional<Passenger> existingPassenger = passengerRepository.findByContactEmail(email);
        existingPassenger.ifPresent(passenger -> {
            if (currentPassengerId == null || !passenger.getId().equals(currentPassengerId)) {
                throw new CustomException("Duplicate contactEmail: " + email);
            }
        });
    }

    private void checkContactPhoneUniqueness(String phone, UUID currentPassengerId) {
        Optional<Passenger> existingPassenger = passengerRepository.findByContactPhone(phone);
        existingPassenger.ifPresent(passenger -> {
            if (currentPassengerId == null || !passenger.getId().equals(currentPassengerId)) {
                throw new CustomException("Duplicate contactPhone: " + phone);
            }
        });
    }

    public Passenger findPassengerByContactPhone(String contactPhone) {
        return passengerRepository.findByContactPhone(contactPhone)
                .orElseThrow(() -> new CustomException("No passenger found with contact phone: " + contactPhone));
    }

    public Passenger findPassengerByContactEmail(String contactEmail) {
        return passengerRepository.findByContactEmail(contactEmail)
                .orElseThrow(() -> new CustomException("No passenger found with contact email: " + contactEmail));
    }

}
