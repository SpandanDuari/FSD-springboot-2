package com.example.hospital.appointment;
import org.springframework.data.jpa.repository.*;
import java.time.LocalDateTime;
public interface AppointmentRepository extends JpaRepository<Appointment,Long> {
    boolean existsByDoctorIdAndAppointmentTimeAndStatus(Long doctorId,LocalDateTime time,Appointment.Status status);
    long countByPatientIdAndAppointmentTimeBetweenAndStatus(Long patientId,LocalDateTime start,LocalDateTime end,Appointment.Status status);
}
