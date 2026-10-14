package com.example.hospital.repository;

import com.example.hospital.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PatientRepository extends JpaRepository<Patient, Long> {}
