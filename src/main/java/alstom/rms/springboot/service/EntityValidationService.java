package alstom.rms.springboot.service;

import alstom.rms.springboot.exception.CustomException;
import alstom.rms.springboot.model.*;
import alstom.rms.springboot.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class EntityValidationService {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private TrainRepository trainRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private LocationRepository locationRepository;

    public Station fetchStationById(UUID id) {
        return stationRepository.findById(id).orElseThrow(() -> new CustomException("Station not found with id: " + id));
    }

    public Train fetchTrainById(UUID id) {
        return trainRepository.findById(id).orElseThrow(() -> new CustomException("Train not found with id: " + id));
    }

    public Ticket fetchTicketById(UUID id) {
        return ticketRepository.findById(id).orElseThrow(() -> new CustomException("Ticket not found with id: " + id));
    }

    public Schedule fetchScheduleById(UUID id) {
        return scheduleRepository.findById(id).orElseThrow(() -> new CustomException("Schedule not found with id: " + id));
    }

    public Location fetchLocationById(UUID id) {
        return locationRepository.findById(id).orElseThrow(() -> new CustomException("Location not found with id: " + id));
    }

    public void validateTicketNumberUniqueness(String ticketNumber) {
        ticketRepository.findByTicketNumber(ticketNumber).ifPresent(ticket -> {
            throw new CustomException("A ticket with the number " + ticketNumber + " already exists.");
        });
    }

    public Contact fetchContactById(UUID id) {
        return contactRepository.findById(id).orElseThrow(() -> new CustomException("Contact not found with ID: " + id));
    }

    public void validateStationCodeUniqueness(String stationCode, UUID currentId) {
        Optional<Station> existingStation = stationRepository.findByStationCode(stationCode);
        existingStation.ifPresent(station -> {
            if (currentId == null || !station.getId().equals(currentId)) {
                throw new CustomException("A station with the station code " + stationCode + " already exists.");
            }
        });
    }

    public void checkContactUniqueness(Contact contact, UUID currentContactId) {
        checkContactEmailUniqueness(contact.getContactEmail(), currentContactId);
        checkContactPhoneUniqueness(contact.getContactPhone(), currentContactId);
    }

    void checkContactEmailUniqueness(String email, UUID currentContactId) {
        Optional<Contact> existingContact = contactRepository.findByContactEmail(email);
        existingContact.ifPresent(contact -> {
            if (currentContactId == null || !contact.getId().equals(currentContactId)) {
                throw new CustomException("A contact with this email already exists.");
            }
        });
    }

    void checkContactPhoneUniqueness(String phone, UUID currentContactId) {
        Optional<Contact> existingContact = contactRepository.findByContactPhone(phone);
        existingContact.ifPresent(contact -> {
            if (currentContactId == null || !contact.getId().equals(currentContactId)) {
                throw new CustomException("A contact with this phone number already exists.");
            }
        });
    }
}
