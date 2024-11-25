package alstom.rms.springboot.controller;

import alstom.rms.springboot.model.Schedule;
import alstom.rms.springboot.service.ScheduleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/schedules")
public class SchedulesController {

    @Autowired
    private ScheduleService scheduleService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping
    public List<Schedule> getAllSchedules() {
        return scheduleService.getAllSchedules();
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("{id}")
    public ResponseEntity<Schedule> getScheduleById(@PathVariable UUID id) {
        return ResponseEntity.ok(scheduleService.getScheduleById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Schedule createSchedule(@RequestBody @Valid Schedule schedule) {
        return scheduleService.createSchedule(schedule);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{id}")
    public ResponseEntity<Schedule> updateScheduleById(@PathVariable UUID id, @RequestBody @Valid Schedule scheduleDetails) {
        return ResponseEntity.ok(scheduleService.updateScheduleById(id, scheduleDetails));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteScheduleById(@PathVariable UUID id) {
        scheduleService.deleteScheduleById(id);
        return ResponseEntity.ok("Schedule data successfully deleted.");
    }

}
