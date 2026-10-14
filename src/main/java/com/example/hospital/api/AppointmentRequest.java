package com.example.hospital.api;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
public record AppointmentRequest(@NotNull Long doctorId, @NotNull Long patientId,
                                 @NotNull @Future LocalDateTime appointmentTime) {}
