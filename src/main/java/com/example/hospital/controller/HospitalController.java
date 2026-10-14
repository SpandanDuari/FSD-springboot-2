package com.example.hospital.controller;

import com.example.hospital.api.*;
import com.example.hospital.domain.*;
import com.example.hospital.service.HospitalService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class HospitalController {
    private final HospitalService service;
    public HospitalController(HospitalService service) { this.service = service; }

    @PostMapping("/doctors") @ResponseStatus(HttpStatus.CREATED)
    public Doctor registerDoctor(@Valid @RequestBody DoctorRequest request) {
        return service.registerDoctor(request);
    }
    @GetMapping("/doctors") public List<Doctor> doctors() { return service.doctors(); }
    @PostMapping("/patients") @ResponseStatus(HttpStatus.CREATED)
    public Patient registerPatient(@Valid @RequestBody PatientRequest request) {
        return service.registerPatient(request);
    }
    @GetMapping("/patients") public List<Patient> patients() { return service.patients(); }
    @PostMapping("/appointments") @ResponseStatus(HttpStatus.CREATED)
    public Appointment book(@Valid @RequestBody AppointmentRequest request) {
        return service.book(request);
    }
    @GetMapping("/appointments") public List<Appointment> appointments() { return service.appointments(); }
    @PatchMapping("/appointments/{id}/cancel")
    public Appointment cancel(@PathVariable Long id) { return service.cancel(id); }
}
