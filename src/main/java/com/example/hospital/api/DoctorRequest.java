package com.example.hospital.api;
import jakarta.validation.constraints.NotBlank;
public record DoctorRequest(@NotBlank String name, @NotBlank String specialization) {}
