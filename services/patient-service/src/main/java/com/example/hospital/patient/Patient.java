package com.example.hospital.patient;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
@Entity public class Patient {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @NotBlank private String name; @Email @NotBlank private String email;
    protected Patient() {} public Patient(String name,String email){this.name=name;this.email=email;}
    public Long getId(){return id;} public String getName(){return name;} public String getEmail(){return email;}
}
