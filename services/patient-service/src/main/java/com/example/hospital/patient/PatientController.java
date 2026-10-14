package com.example.hospital.patient;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/patients")
public class PatientController {
    private final PatientRepository repository;
    public PatientController(PatientRepository repository){this.repository=repository;}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public Patient register(@Valid @RequestBody PatientRequest request) {
        return repository.save(new Patient(request.name(), request.email()));
    }
    @GetMapping public List<Patient> findAll(){return repository.findAll();}
    public record PatientRequest(String name, String email) {}
}
