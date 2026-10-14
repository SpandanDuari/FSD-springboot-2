package com.example.hospital.doctor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/doctors")
public class DoctorController {
    private final DoctorRepository repository;
    public DoctorController(DoctorRepository repository){this.repository=repository;}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public Doctor register(@Valid @RequestBody DoctorRequest request) {
        return repository.save(new Doctor(request.name(), request.specialization()));
    }
    @GetMapping public List<Doctor> findAll(){return repository.findAll();}
    public record DoctorRequest(String name, String specialization) {}
}
