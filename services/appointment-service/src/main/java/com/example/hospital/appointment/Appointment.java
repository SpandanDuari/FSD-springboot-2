package com.example.hospital.appointment;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(indexes = {
    @Index(name="idx_doctor_time_status", columnList="doctorId,appointmentTime,status"),
    @Index(name="idx_patient_time_status", columnList="patientId,appointmentTime,status")
})
public class Appointment {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private Long doctorId; private Long patientId; private LocalDateTime appointmentTime;
    @Enumerated(EnumType.STRING) private Status status;
    protected Appointment() {}
    public Appointment(Long doctorId,Long patientId,LocalDateTime time){this.doctorId=doctorId;this.patientId=patientId;appointmentTime=time;status=Status.BOOKED;}
    public Long getId(){return id;} public Long getDoctorId(){return doctorId;} public Long getPatientId(){return patientId;}
    public LocalDateTime getAppointmentTime(){return appointmentTime;} public Status getStatus(){return status;}
    public void cancel(){status=Status.CANCELLED;} public enum Status{BOOKED,CANCELLED}
}
