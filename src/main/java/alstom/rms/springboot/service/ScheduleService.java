package alstom.rms.springboot.service;

import alstom.rms.springboot.exception.CustomException;
import alstom.rms.springboot.model.Schedule;
import alstom.rms.springboot.model.Station;
import alstom.rms.springboot.repository.ScheduleRepository;
import alstom.rms.springboot.repository.StationRepository;
import alstom.rms.springboot.repository.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private TrainRepository trainRepository;

    public List<Schedule> getAllSchedules() {
        return removeContactDetailsFromSchedules(scheduleRepository.findAll());
    }

    public Schedule getScheduleById(UUID id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new CustomException("Schedule doesn't exist with id:" + id));
    }

    public Schedule createSchedule(Schedule schedule) {
        validateSchedule(schedule, null);
        return removeContactDetailsFromSchedules(Collections.singletonList(scheduleRepository.save(schedule))).get(0);
    }

    public Schedule updateScheduleById(UUID id, Schedule scheduleDetails) {
        Schedule existingSchedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new CustomException("Schedule doesn't exist with id: " + id));

        updateScheduleFields(existingSchedule, scheduleDetails);
        validateSchedule(existingSchedule, id);

        return scheduleRepository.save(existingSchedule);
    }

    public void deleteScheduleById(UUID id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new CustomException("Schedule doesn't exist with id:" + id));

        scheduleRepository.delete(schedule);
    }

    private List<Schedule> removeContactDetailsFromSchedules(List<Schedule> schedules) {
        return schedules.stream()
                .map(this::removeContactDetailsFromSchedule)
                .collect(Collectors.toList());
    }

    private Schedule removeContactDetailsFromSchedule(Schedule schedule) {
        Station originStation = removeContactsFromStation(schedule.getOriginStation());
        Station destinationStation = removeContactsFromStation(schedule.getDestinationStation());

        schedule.setOriginStation(originStation);
        schedule.setDestinationStation(destinationStation);
        return schedule;
    }

    private Station removeContactsFromStation(Station station) {
        station.setContacts(null);
        return station;
    }

    private void updateScheduleFields(Schedule existingSchedule, Schedule scheduleDetails) {
        if (scheduleDetails.getOriginStation().getId().equals(scheduleDetails.getDestinationStation().getId()))
            throw new CustomException("Origin station and Destination should be different.");

        if (scheduleDetails.getTrain() != null) existingSchedule.setTrain(scheduleDetails.getTrain());
        if (scheduleDetails.getOriginStation() != null)
            existingSchedule.setOriginStation(scheduleDetails.getOriginStation());
        if (scheduleDetails.getDestinationStation() != null)
            existingSchedule.setDestinationStation(scheduleDetails.getDestinationStation());
        if (scheduleDetails.getDepartureTime() != null)
            existingSchedule.setDepartureTime(scheduleDetails.getDepartureTime());
        if (scheduleDetails.getArrivalTime() != null) existingSchedule.setArrivalTime(scheduleDetails.getArrivalTime());
        if (scheduleDetails.getTicketPrice() != null) existingSchedule.setTicketPrice(scheduleDetails.getTicketPrice());
        if (scheduleDetails.getAvailableSeats() != null)
            existingSchedule.setAvailableSeats(scheduleDetails.getAvailableSeats());
    }

    private void validateSchedule(Schedule schedule, UUID currentScheduleId) {
        if (!trainRepository.existsById(schedule.getTrain().getId())) {
            throw new CustomException("Train with id " + schedule.getTrain().getId() + " does not exist.");
        }

        if (!stationRepository.existsById(schedule.getOriginStation().getId())) {
            throw new CustomException("Origin station with id " + schedule.getOriginStation().getId() + " does not exist.");
        }

        if (!stationRepository.existsById(schedule.getDestinationStation().getId())) {
            throw new CustomException("Destination station with id " + schedule.getDestinationStation().getId() + " does not exist.");
        }

        Schedule existingSchedule = scheduleRepository.findByAllColumns(
                schedule.getTrain(), schedule.getOriginStation(), schedule.getDestinationStation());
        if (existingSchedule != null && (currentScheduleId == null || !existingSchedule.getId().equals(currentScheduleId))) {
            throw new CustomException("Schedule with the same train, origin station, and destination station already exists.");
        }
    }

}
