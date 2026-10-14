CREATE DATABASE IF NOT EXISTS hospital_doctors;
CREATE DATABASE IF NOT EXISTS hospital_patients;
CREATE DATABASE IF NOT EXISTS hospital_appointments;
GRANT ALL PRIVILEGES ON hospital_doctors.* TO 'hospital'@'%';
GRANT ALL PRIVILEGES ON hospital_patients.* TO 'hospital'@'%';
GRANT ALL PRIVILEGES ON hospital_appointments.* TO 'hospital'@'%';
FLUSH PRIVILEGES;
