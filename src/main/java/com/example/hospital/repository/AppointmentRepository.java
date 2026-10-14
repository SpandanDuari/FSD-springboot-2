package com.example.hospital.repository;

import com.example.hospital.domain.*;
import org.springframework.data.jpa.repository.*;
import java.time.LocalDateTime;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    boolean existsByDoctorIdAndAppointmentTimeAndStatus(
            Long doctorId, LocalDateTime appointmentTime, AppointmentStatus status);
    long countByPatientIdAndAppointmentTimeBetweenAndStatus(
            Long patientId, LocalDateTime start, LocalDateTime end, AppointmentStatus status);
}
