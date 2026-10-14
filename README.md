# Smart Hospital Appointment Management System

## Architecture

```text
Client (curl/Postman/browser)
        |
        v
REST Controller (/api/doctors, /api/patients, /api/appointments)
        |
        v
HospitalService (validation, business rules, transaction + booking lock)
        |
        v
Spring Data JPA Repositories
        |
        v
H2 database (in-memory; replace datasource URL for production)
```

The package structure follows the three-layer design:

```text
com.example.hospital
├── api          request records and validation
├── controller   REST endpoints and exception mapping
├── domain       Doctor, Patient, Appointment, AppointmentStatus
├── repository   Spring Data JPA repositories
└── service      business rules and transaction boundary
```

## Request flow

For `POST /api/appointments`, JSON is deserialized and validated by the controller. The
controller invokes `HospitalService.book`, which loads the doctor and patient through
repositories, checks the active appointment at the requested slot and the patient's
active daily count, saves the appointment, and returns the saved entity as JSON.

An appointment slot is represented by its `appointmentTime`; therefore two active
appointments at the same slot overlap. Cancellation changes the status to `CANCELLED`.
All availability queries explicitly use `BOOKED`, so cancelled records never block a slot
or count toward the daily limit.

## REST endpoints and examples

```text
POST  /api/doctors
POST  /api/patients
GET   /api/doctors
GET   /api/patients
POST  /api/appointments
GET   /api/appointments
PATCH /api/appointments/{id}/cancel
```

Example:

```bash
curl -X POST http://localhost:8080/api/doctors \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Dr. Rao\",\"specialization\":\"Cardiology\"}"
# 201 {"id":1,"name":"Dr. Rao","specialization":"Cardiology"}

curl -X POST http://localhost:8080/api/patients \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Asha\",\"email\":\"asha@example.com\"}"
# 201 {"id":1,"name":"Asha","email":"asha@example.com"}

curl -X POST http://localhost:8080/api/appointments \
  -H "Content-Type: application/json" \
  -d "{\"doctorId\":1,\"patientId\":1,\"appointmentTime\":\"2099-01-10T10:00:00\"}"
# 201 {"id":1,...,"status":"BOOKED"}

curl -X PATCH http://localhost:8080/api/appointments/1/cancel
# 200 {"id":1,...,"status":"CANCELLED"}
```

Conflicting booking responses are `409 Conflict`, for example:
`{"error":"Doctor already has an appointment at 2099-01-10T10:00"}`.

## Concurrent booking protection

`HospitalService.book` is `@Transactional` and synchronizes the complete
check-then-save sequence on a service-level lock. If two requests arrive at the same
application instance, only one can observe the slot as available and save it; the second
re-checks and receives `409 Conflict`. The database transaction commits the successful
write atomically. For a horizontally scaled deployment, replace the JVM lock with a
database unique constraint/advisory lock or a distributed lock (the H2 demo runs as one
instance).

## Tests and expected results

`HospitalServiceTest` validates doctor overlap rejection, the two-per-day patient limit,
cancelled appointments not blocking a slot, and concurrent serialization. Running
`mvn test` is expected to report **Tests run: 4, Failures: 0, Errors: 0**.

## Spring Boot auto-configuration

`@SpringBootApplication` combines component scanning, auto-configuration, and Java
configuration. Spring Boot detects the Web, JPA, H2, and validation dependencies and
automatically creates the embedded server, MVC infrastructure, Jackson JSON mapper,
DataSource, EntityManager, transaction manager, and repository implementations.
`application.properties` only supplies application-specific H2 and JPA settings.

## Run

```bash
mvn spring-boot:run
```

The H2 console is available at `/h2-console` with JDBC URL
`jdbc:h2:mem:hospital`, user `sa`, and an empty password while the application runs.
