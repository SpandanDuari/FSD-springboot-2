package com.example.hospital.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Doctor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank private String name;
    @NotBlank private String specialization;

    protected Doctor() {}
    public Doctor(String name, String specialization) {
        this.name = name; this.specialization = specialization;
    }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getSpecialization() { return specialization; }
}
