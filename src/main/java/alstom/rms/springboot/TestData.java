package alstom.rms.springboot;

import alstom.rms.springboot.exception.CustomException;
import alstom.rms.springboot.model.*;
import alstom.rms.springboot.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class TestData {

    @Autowired
    private TrainRepository trainRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private TicketRepository ticketRepository;

    public void setup() {
        setupTrains();
        setupLocations();
        setupStations();
        setupContacts();
        setupPassengers();
        setupSchedules();
        setupTickets();
    }

    private void setupTrains() {
        Train bTrain = new Train();
        bTrain.setTrainNumber("B66");
        bTrain.setSeatCapacity(138);
        saveOrUpdateTrain(bTrain);

        Train cTrain = new Train();
        cTrain.setTrainNumber("C77");
        cTrain.setSeatCapacity(200);
        saveOrUpdateTrain(cTrain);
    }

    @Transactional
    public void saveOrUpdateTrain(Train train) {
        Train existingTrain = trainRepository.findByTrainNumber(train.getTrainNumber());

        if (existingTrain != null) {
            existingTrain.setSeatCapacity(train.getSeatCapacity());
            trainRepository.save(existingTrain);
        } else {
            trainRepository.save(train);
        }
    }

    private void setupLocations() {
        Location bLocation = new Location();
        bLocation.setCity("changwat");
        bLocation.setState("Pathum Thani");
        bLocation.setZipCode("12120");
        saveOrUpdateLocation(bLocation);

        Location cLocation = new Location();
        cLocation.setCity("Rattanakosin");
        cLocation.setState("Bangkok");
        cLocation.setZipCode("10200");
        saveOrUpdateLocation(cLocation);
    }

    @Transactional
    public void saveOrUpdateLocation(Location location) {
        Location existingLocation = locationRepository.findByZipCode(location.getZipCode())
                .orElseThrow(() -> new CustomException("Train doesn't exists with Number:" + location.getZipCode()));

        if (existingLocation != null) {
            existingLocation.setCity(location.getCity());
            existingLocation.setState(location.getState());
            locationRepository.save(existingLocation);
        } else {
            locationRepository.save(location);
        }
    }

    private void setupStations() {
        Location alocation = locationRepository.findByZipCode("12120")
                .orElseThrow(() -> new CustomException("Location doesn't exists with zipcode:" + "12120"));

        if (alocation != null) {
            Station station = new Station();
            station.setStationCode("NY01");
            station.setStationName("New York Central");
            station.setLocation(alocation);

            Contact contact = new Contact();
            contact.setContactPerson("Jane Smith");
            contact.setContactPhone("09876543211");
            contact.setContactEmail("jane.smith01@lausunion.com");

            station.setContacts(Collections.singletonList(contact));
            saveOrUpdateStation(station);
        }

        Location blocation = locationRepository.findByZipCode("10200").
                orElseThrow(() -> new CustomException("Location doesn't exists with zipcode:" + "10200"));
        if (blocation != null) {
            Station station = new Station();
            station.setStationCode("LA01");
            station.setStationName("Los Angeles Union");
            station.setLocation(blocation);

            saveOrUpdateStation(station);
        }
    }

    @Transactional
    public void saveOrUpdateStation(Station station) {
        Station existingStation = stationRepository.findByStationCode(station.getStationCode()).orElse(null);
        if (existingStation != null) {
            existingStation.setStationName(station.getStationName());
            existingStation.setLocation(station.getLocation());
            stationRepository.save(existingStation);
        } else {
            stationRepository.save(station);
        }
    }

    private void setupContacts() {
        Station astation = stationRepository.findByStationCode("NY01").
                orElseThrow(() -> new CustomException("Station doesn't exists with code:" + "NY01"));
        if (astation != null) {
            Contact contact = new Contact();
            contact.setContactPerson("John Doe");
            contact.setContactPhone("1234567890");
            contact.setContactEmail("john.doe@nycentral.com");
            contact.setStation(astation);
            saveOrUpdateContact(contact);
        }

        Station bstation = stationRepository.findByStationCode("LA01")
                .orElseThrow(() -> new CustomException("Station doesn't exists with zipcode:" + "LA01"));
        if (bstation != null) {
            Contact contact = new Contact();
            contact.setContactPerson("Jane Smith");
            contact.setContactPhone("0987654321");
            contact.setContactEmail("jane.smith@lausunion.com");
            contact.setStation(bstation);
            saveOrUpdateContact(contact);
        }
    }

    @Transactional
    public void saveOrUpdateContact(Contact contact) {
        Optional<Contact> existingContact = contactRepository.findByContactEmail(contact.getContactEmail());
        if (existingContact != null) {
            existingContact.get().setContactPerson(contact.getContactPerson());
            existingContact.get().setContactEmail(contact.getContactEmail());
            contactRepository.save(existingContact.get());
        } else {
            contactRepository.save(contact);
        }
    }

    private void setupPassengers() {
        Passenger aPassenger = new Passenger();
        aPassenger.setPassengerName("John Doe");
        aPassenger.setContactPhone("1234567890");
        aPassenger.setContactEmail("john.doe@nycentral.com");
        saveOrUpdatePassenger(aPassenger);

        Passenger bPassenger = new Passenger();
        bPassenger.setPassengerName("Jane Smith");
        bPassenger.setContactPhone("0987654321");
        bPassenger.setContactEmail("jane.smith@lausunion.com");
        saveOrUpdatePassenger(bPassenger);
    }

    @Transactional
    public void saveOrUpdatePassenger(Passenger passenger) {
        Optional<Passenger> existingpassenger = passengerRepository.findByContactEmail(passenger.getContactEmail());
        if (existingpassenger != null) {
            existingpassenger.get().setPassengerName(passenger.getPassengerName());
            existingpassenger.get().setContactEmail(passenger.getContactEmail());
            existingpassenger.get().setContactPhone(passenger.getContactPhone());
            passengerRepository.save(existingpassenger.get());
        } else {
            passengerRepository.save(passenger);
        }
    }

    private void setupSchedules() {
        Station originStation = stationRepository.findByStationCode("LA01").orElse(null);
        Station destinationStation = stationRepository.findByStationCode("NY01").orElse(null);

        Train atrain = trainRepository.findByTrainNumber("B66");
        Schedule aSchedule = new Schedule();
        aSchedule.setTrain(atrain);
        aSchedule.setOriginStation(originStation);
        aSchedule.setDestinationStation(destinationStation);
        aSchedule.setDepartureTime(LocalTime.of(9, 15));
        aSchedule.setArrivalTime(LocalTime.of(12, 45));
        aSchedule.setTicketPrice(700.0);
        aSchedule.setAvailableSeats(20);

        saveOrUpdateSchedule(aSchedule);

        Train btrain = trainRepository.findByTrainNumber("C77");
        Schedule bSchedule = new Schedule();
        bSchedule.setTrain(btrain);
        bSchedule.setOriginStation(originStation);
        bSchedule.setDestinationStation(destinationStation);
        bSchedule.setDepartureTime(LocalTime.of(10, 30));
        bSchedule.setArrivalTime(LocalTime.of(18, 30));
        bSchedule.setTicketPrice(600.0);
        bSchedule.setAvailableSeats(25);

        saveOrUpdateSchedule(bSchedule);
    }

    @Transactional
    public void saveOrUpdateSchedule(Schedule schedule) {
        Schedule existingSchedule = scheduleRepository.findByAllColumns(schedule.getTrain(),
                schedule.getOriginStation(), schedule.getDestinationStation());

        if (existingSchedule != null) {
            updateSchedule(schedule, existingSchedule);

        } else {
            scheduleRepository.save(schedule);
        }
    }

    private void updateSchedule(Schedule schedule, Schedule existingSchedule) {
        if (schedule.getTrain() != null) {
            existingSchedule.setTrain(schedule.getTrain());
        }

        if (schedule.getOriginStation() != null) {
            existingSchedule.setOriginStation(schedule.getOriginStation());
        }

        if (schedule.getDestinationStation() != null) {
            existingSchedule.setDestinationStation(schedule.getDestinationStation());
        }

        if (schedule.getDepartureTime() != null) {
            existingSchedule.setDepartureTime(schedule.getDepartureTime());
        }

        if (schedule.getArrivalTime() != null) {
            existingSchedule.setArrivalTime(schedule.getArrivalTime());
        }
        if (schedule.getTicketPrice() != null) {
            existingSchedule.setTicketPrice(schedule.getTicketPrice());
        }
        if (schedule.getAvailableSeats() != null) {
            existingSchedule.setAvailableSeats(schedule.getAvailableSeats());
        }

        scheduleRepository.save(existingSchedule);
    }

    private void setupTickets() {
        Optional<Passenger> aPassenger = passengerRepository.findByContactEmail("john.doe@nycentral.com");
        Optional<Passenger> bPassenger = passengerRepository.findByContactEmail("jane.smith@lausunion.com");

        Station originStation = stationRepository.findByStationCode("LA01").
                orElseThrow(() -> new CustomException("Station doesn't exists with code:" + "LA01"));
        Station destinationStation = stationRepository.findByStationCode("NY01")
                .orElseThrow(() -> new CustomException("Station doesn't exists with zipcode:" + "NY01"));

        Schedule aSchedule = scheduleRepository
                .findByAllColumns(trainRepository.findByTrainNumber("B66"), originStation, destinationStation);
        if (aPassenger != null && aSchedule != null) {
            Ticket ticket = new Ticket();
            ticket.setSeatNumbers(List.of("1A", "2A"));
            ticket.setPassenger(aPassenger.get());
            ticket.setSchedule(aSchedule);
            ticket.setDepartureDate(LocalDate.now().plusDays(1));
            saveOrUpdateTicket(ticket);
        }

        Schedule bSchedule = scheduleRepository
                .findByAllColumns(trainRepository.findByTrainNumber("C77"), originStation, destinationStation);
        if (bPassenger != null && bSchedule != null) {
            Ticket ticket = new Ticket();
            ticket.setSeatNumbers(List.of("1B", "2B"));
            ticket.setPassenger(bPassenger.get());
            ticket.setSchedule(bSchedule);
            ticket.setDepartureDate(LocalDate.now().plusDays(2));
            saveOrUpdateTicket(ticket);
        }
    }

    @Transactional
    public void saveOrUpdateTicket(Ticket ticket) {
        Ticket existingTicket = null;
        if (existingTicket != null) {
            existingTicket.setPassenger(ticket.getPassenger());
            existingTicket.setSchedule(ticket.getSchedule());
            existingTicket.setDepartureDate(ticket.getDepartureDate());
            ticketRepository.save(existingTicket);
        } else {
            ticketRepository.save(ticket);
        }
    }

}
