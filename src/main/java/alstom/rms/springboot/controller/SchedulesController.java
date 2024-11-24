package alstom.rms.springboot.controller;

import alstom.rms.springboot.exception.ResourceNotFoundException;
import alstom.rms.springboot.model.Schedule;
import alstom.rms.springboot.model.Station;
import alstom.rms.springboot.repository.ScheduleRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/schedules")
public class SchedulesController {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping
    public List<Schedule> getAllSchedules() {
        return removeContactDetailsFromSchedules(scheduleRepository.findAll());
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("{id}")
    public ResponseEntity<Schedule> getScheduleById(@PathVariable UUID id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule doesn't exists with id:" + id));

        return ResponseEntity.ok(removeContactDetailsFromSchedules(Collections.singletonList(schedule)).get(0));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Schedule createSchedule(@RequestBody @Valid Schedule schedule) {
        return removeContactDetailsFromSchedules(Collections.singletonList(scheduleRepository.save(schedule))).get(0);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{id}")
    public ResponseEntity<Schedule> updateScheduleById(@PathVariable UUID id, @RequestBody Schedule scheduleDetails) {
        Schedule updateSchedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule doesn't exists with id:" + id));

        if (scheduleDetails.getTrain() != null) {
            updateSchedule.setTrain(scheduleDetails.getTrain());
        }
        if (scheduleDetails.getOriginStation() != null) {
            updateSchedule.setOriginStation(scheduleDetails.getOriginStation());
        }
        if (scheduleDetails.getDestinationStation() != null) {
            updateSchedule.setDestinationStation(scheduleDetails.getDestinationStation());
        }
        if (scheduleDetails.getDepartureTime() != null) {
            updateSchedule.setDepartureTime(scheduleDetails.getDepartureTime());
        }
        if (scheduleDetails.getArrivalTime() != null) {
            updateSchedule.setArrivalTime(scheduleDetails.getArrivalTime());
        }
        if (scheduleDetails.getTicketPrice() != null) {
            updateSchedule.setTicketPrice(scheduleDetails.getTicketPrice());
        }
        if (scheduleDetails.getAvailableSeats() != null) {
            updateSchedule.setAvailableSeats(scheduleDetails.getAvailableSeats());
        }
        scheduleRepository.save(updateSchedule);
        return ResponseEntity.ok(updateSchedule);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteScheduleById(@PathVariable UUID id) {
        Schedule updateSchedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule doesn't exists with id:" + id));

        scheduleRepository.delete(updateSchedule);
        return ResponseEntity.ok(" schedule data successfully deleted.");
    }

    private List<Schedule> removeContactDetailsFromSchedules(List<Schedule> schedules) {
        return schedules.stream()
                .map(passenger -> {
                    Station originStation = passenger.getOriginStation();
                    Station destinationStation = passenger.getDestinationStation();

                    originStation.setContacts(null);
                    destinationStation.setContacts(null);

                    Schedule modifiedPassenger = new Schedule(
                            passenger.getId(),
                            passenger.getTrain(),
                            originStation,
                            destinationStation,
                            passenger.getDepartureTime(),
                            passenger.getArrivalTime(),
                            passenger.getTicketPrice(),
                            passenger.getAvailableSeats()
                    );

                    return modifiedPassenger;
                })
                .collect(Collectors.toList());
    }

}
