package com.example.hospital.service;

import com.example.hospital.api.AppointmentRequest;
import com.example.hospital.domain.*;
import com.example.hospital.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HospitalServiceTest {
    @Mock DoctorRepository doctors;
    @Mock PatientRepository patients;
    @Mock AppointmentRepository appointments;
    HospitalService service;
    Doctor doctor;
    Patient patient;
    LocalDateTime tomorrow = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);

    @BeforeEach
    void setUp() {
        service = new HospitalService(doctors, patients, appointments);
        doctor = new Doctor("Dr. Rao", "Cardiology");
        patient = new Patient("Asha", "asha@example.com");
        setId(doctor, 1L); setId(patient, 1L);
        when(doctors.findById(1L)).thenReturn(Optional.of(doctor));
        when(patients.findById(1L)).thenReturn(Optional.of(patient));
        lenient().when(appointments.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void rejectsDoctorOverlap() {
        when(appointments.existsByDoctorIdAndAppointmentTimeAndStatus(1L, tomorrow, AppointmentStatus.BOOKED))
                .thenReturn(true);
        assertThrows(BookingConflictException.class,
                () -> service.book(new AppointmentRequest(1L, 1L, tomorrow)));
        verify(appointments, never()).save(any());
    }

    @Test
    void rejectsThirdPatientBookingOnSameDay() {
        when(appointments.countByPatientIdAndAppointmentTimeBetweenAndStatus(
                eq(1L), any(), any(), eq(AppointmentStatus.BOOKED))).thenReturn(2L);
        assertThrows(BookingConflictException.class,
                () -> service.book(new AppointmentRequest(1L, 1L, tomorrow)));
        verify(appointments, never()).save(any());
    }

    @Test
    void cancelledAppointmentDoesNotBlockSameSlot() {
        when(appointments.existsByDoctorIdAndAppointmentTimeAndStatus(1L, tomorrow, AppointmentStatus.BOOKED))
                .thenReturn(false);
        Appointment result = service.book(new AppointmentRequest(1L, 1L, tomorrow));
        assertEquals(AppointmentStatus.BOOKED, result.getStatus());
        verify(appointments).save(any(Appointment.class));
    }

    @Test
    void serializesConcurrentBookingsForSameService() throws Exception {
        when(appointments.existsByDoctorIdAndAppointmentTimeAndStatus(1L, tomorrow, AppointmentStatus.BOOKED))
                .thenReturn(false, true);
        when(appointments.countByPatientIdAndAppointmentTimeBetweenAndStatus(
                eq(1L), any(), any(), eq(AppointmentStatus.BOOKED))).thenReturn(0L);
        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            Future<Appointment> first = pool.submit(() -> service.book(new AppointmentRequest(1L, 1L, tomorrow)));
            Future<Appointment> second = pool.submit(() -> service.book(new AppointmentRequest(1L, 1L, tomorrow)));
            int successes = 0;
            int conflicts = 0;
            for (Future<?> result : java.util.List.of(first, second)) {
                try { result.get(); successes++; }
                catch (ExecutionException ex) {
                    if (ex.getCause() instanceof BookingConflictException) conflicts++;
                }
            }
            assertEquals(1, successes);
            assertEquals(1, conflicts);
        } finally {
            pool.shutdownNow();
        }
    }

    private static void setId(Object entity, Long id) {
        try {
            var field = entity.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(entity, id);
        } catch (ReflectiveOperationException ex) {
            throw new AssertionError(ex);
        }
    }
}
