# Verification Evidence

## Automated tests

Command:

```text
mvn test
```

Actual result:

```text
Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Covered cases:

| Test | Expected | Actual |
|---|---|---|
| `rejectsDoctorOverlap` | `BookingConflictException`, no save | Passed |
| `rejectsThirdPatientBookingOnSameDay` | `BookingConflictException`, no save | Passed |
| `cancelledAppointmentDoesNotBlockSameSlot` | New `BOOKED` appointment can be saved | Passed |
| `serializesConcurrentBookingsForSameService` | One success and one conflict | Passed |

## REST execution smoke test

The application was started with `mvn spring-boot:run` on port `8081` because port
`8080` was already occupied by another local process. The verified output was:

```json
{
  "doctor": {"id": 1, "name": "Dr. Rao", "specialization": "Cardiology"},
  "patient": {"id": 1, "name": "Asha", "email": "asha@example.com"},
  "firstBooking": {
    "id": 1,
    "appointmentTime": "2026-10-15T10:00:00",
    "status": "BOOKED"
  },
  "duplicateStatus": 409,
  "cancelledStatus": "CANCELLED",
  "rebookingStatus": "BOOKED"
}
```

This demonstrates that an active duplicate is rejected, while a cancelled slot is
available for a future booking.
