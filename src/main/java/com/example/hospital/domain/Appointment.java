package com.example.hospital.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Appointment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) private Doctor doctor;
    @ManyToOne(optional = false) private Patient patient;
    @Column(nullable = false) private LocalDateTime appointmentTime;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private AppointmentStatus status;

    protected Appointment() {}
    public Appointment(Doctor doctor, Patient patient, LocalDateTime appointmentTime) {
        this.doctor = doctor; this.patient = patient;
        this.appointmentTime = appointmentTime; this.status = AppointmentStatus.BOOKED;
    }
    public Long getId() { return id; }
    public Doctor getDoctor() { return doctor; }
    public Patient getPatient() { return patient; }
    public LocalDateTime getAppointmentTime() { return appointmentTime; }
    public AppointmentStatus getStatus() { return status; }
    public void cancel() { status = AppointmentStatus.CANCELLED; }
}
