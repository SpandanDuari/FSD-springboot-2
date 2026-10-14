package com.example.hospital.service;

import com.example.hospital.api.*;
import com.example.hospital.domain.*;
import com.example.hospital.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.List;

@Service
public class HospitalService {
    private final DoctorRepository doctors;
    private final PatientRepository patients;
    private final AppointmentRepository appointments;
    private final Object bookingLock = new Object();

    public HospitalService(DoctorRepository doctors, PatientRepository patients,
                           AppointmentRepository appointments) {
        this.doctors = doctors; this.patients = patients; this.appointments = appointments;
    }
    public Doctor registerDoctor(DoctorRequest request) {
        return doctors.save(new Doctor(request.name(), request.specialization()));
    }
    public Patient registerPatient(PatientRequest request) {
        return patients.save(new Patient(request.name(), request.email()));
    }
    public List<Doctor> doctors() { return doctors.findAll(); }
    public List<Patient> patients() { return patients.findAll(); }
    public List<Appointment> appointments() { return appointments.findAll(); }

    @Transactional
    public Appointment book(AppointmentRequest request) {
        synchronized (bookingLock) {
            Doctor doctor = doctors.findById(request.doctorId())
                    .orElseThrow(() -> new NotFoundException("Doctor not found: " + request.doctorId()));
            Patient patient = patients.findById(request.patientId())
                    .orElseThrow(() -> new NotFoundException("Patient not found: " + request.patientId()));
            LocalDateTime time = request.appointmentTime();
            if (appointments.existsByDoctorIdAndAppointmentTimeAndStatus(
                    doctor.getId(), time, AppointmentStatus.BOOKED)) {
                throw new BookingConflictException("Doctor already has an appointment at " + time);
            }
            LocalDate date = time.toLocalDate();
            long dailyBookings = appointments.countByPatientIdAndAppointmentTimeBetweenAndStatus(
                    patient.getId(), date.atStartOfDay(), date.plusDays(1).atStartOfDay(),
                    AppointmentStatus.BOOKED);
            if (dailyBookings >= 2) {
                throw new BookingConflictException("Patient cannot book more than two appointments per day");
            }
            return appointments.save(new Appointment(doctor, patient, time));
        }
    }
    @Transactional
    public Appointment cancel(Long id) {
        Appointment appointment = appointments.findById(id)
                .orElseThrow(() -> new NotFoundException("Appointment not found: " + id));
        appointment.cancel();
        return appointments.save(appointment);
    }
}
