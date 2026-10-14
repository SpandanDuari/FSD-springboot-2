package com.example.hospital.appointment;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.time.*;
import java.util.List;
@RestController @RequestMapping("/api/appointments")
public class AppointmentController {
    private final AppointmentRepository repository;
    private final Object bookingLock = new Object();
    public AppointmentController(AppointmentRepository repository){this.repository=repository;}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) @Transactional
    public Appointment book(@Valid @RequestBody BookingRequest request) {
        synchronized (bookingLock) {
            if (repository.existsByDoctorIdAndAppointmentTimeAndStatus(request.doctorId(),request.appointmentTime(),Appointment.Status.BOOKED))
                throw new Conflict("Doctor already has an active appointment at this time");
            LocalDate date=request.appointmentTime().toLocalDate();
            if (repository.countByPatientIdAndAppointmentTimeBetweenAndStatus(request.patientId(),date.atStartOfDay(),date.plusDays(1).atStartOfDay(),Appointment.Status.BOOKED)>=2)
                throw new Conflict("Patient cannot book more than two appointments per day");
            return repository.save(new Appointment(request.doctorId(),request.patientId(),request.appointmentTime()));
        }
    }
    @GetMapping public List<Appointment> all(){return repository.findAll();}
    @PatchMapping("/{id}/cancel") @Transactional public Appointment cancel(@PathVariable Long id){
        Appointment a=repository.findById(id).orElseThrow(()->new NotFound("Appointment not found"));
        a.cancel(); return repository.save(a);
    }
    public record BookingRequest(Long doctorId,Long patientId,@Future LocalDateTime appointmentTime){}
    @ResponseStatus(HttpStatus.CONFLICT) public static class Conflict extends RuntimeException { Conflict(String m){super(m);} }
    @ResponseStatus(HttpStatus.NOT_FOUND) public static class NotFound extends RuntimeException { NotFound(String m){super(m);} }
}
