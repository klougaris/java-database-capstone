## MySQL Database Design

## 1 Table: `patients`
Stores information about all registered patients.

### Columns
| Column         | Data Type        | Constraints                                  |
|----------------|------------------|-----------------------------------------------|
| `patient_id`   | INT UNSIGNED     | **PRIMARY KEY**, AUTO_INCREMENT               |
| `first_name`   | VARCHAR(50)      | NOT NULL                                      |
| `last_name`    | VARCHAR(50)      | NOT NULL                                      |
| `date_of_birth`| DATE             | NOT NULL                                      |
| `gender`       | ENUM('M','F','Other') | NOT NULL                               |
| `email`        | VARCHAR(100)     | UNIQUE, NULLABLE                              |
| `phone`        | VARCHAR(20)      | NOT NULL                                      |
| `address`      | TEXT             | NULLABLE                                      |
| `created_at`   | TIMESTAMP        | DEFAULT CURRENT_TIMESTAMP                     |

### SQL
```sql
CREATE TABLE patients (
    patient_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender ENUM('M','F','Other') NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20) NOT NULL,
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```
---

## 2 Table: `Doctors`
Stores information about all registered patients.

### Columns
| Column         | Data Type        | Constraints                                  |
|----------------|------------------|-----------------------------------------------|
| `doctor_id`   | INT UNSIGNED     | **PRIMARY KEY**, AUTO_INCREMENT               |
| `first_name`   | VARCHAR(50)      | NOT NULL                                      |
| `last_name`    | VARCHAR(50)      | NOT NULL                                      |
| `specialty`   | VARCHAR(50)             | NOT NULL                                      |
| `email`        | VARCHAR(100)     | UNIQUE, NOT NULL                              |
| `phone`        | VARCHAR(20)      | NOT NULL                                      |
| `clinic_location_id`      | INT UNSIGNED             | FOREIGN KEY   -> clinic_locations(location_id)                                  |
| `created_at`   | TIMESTAMP        | DEFAULT CURRENT_TIMESTAMP                     |

### SQL
```sql
CREATE TABLE doctors (
    doctor_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    specialty VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    clinic_location_id INT UNSIGNED,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (clinic_location_id)
        REFERENCES clinic_locations(location_id)
        ON DELETE SET NULL
);
```
## 3 Table: `Appointments`
Tracks scheduled appointments between patients and doctors.

### Columns
| Column             | Data Type                                                     | Constraints                                              |
| ------------------ | ------------------------------------------------------------- | -------------------------------------------------------- |
| `appointment_id`   | INT UNSIGNED                                                  | **PRIMARY KEY**, AUTO_INCREMENT                          |
| `patient_id`       | INT UNSIGNED                                                  | **FOREIGN KEY → patients(patient_id)** ON DELETE CASCADE |
| `doctor_id`        | INT UNSIGNED                                                  | **FOREIGN KEY → doctors(doctor_id)** ON DELETE RESTRICT  |
| `appointment_date` | DATE                                                          | NOT NULL                                                 |
| `appointment_time` | TIME                                                          | NOT NULL                                                 |
| `status`           | ENUM('Scheduled','Completed','Cancelled') DEFAULT 'Scheduled' |                                                          |
| `notes`            | TEXT                                                          | NULLABLE                                                 |
| `created_at`       | TIMESTAMP                                                     | DEFAULT CURRENT_TIMESTAMP                                |

### SQL
```sql
CREATE TABLE appointments (
    appointment_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    patient_id INT UNSIGNED NOT NULL,
    doctor_id INT UNSIGNED NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    status ENUM('Scheduled','Completed','Cancelled') DEFAULT 'Scheduled',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id)
        REFERENCES patients(patient_id)
        ON DELETE CASCADE,
    FOREIGN KEY (doctor_id)
        REFERENCES doctors(doctor_id)
        ON DELETE RESTRICT
    -- Optional: prevent overlapping doctor appointments
    -- UNIQUE (doctor_id, appointment_date, appointment_time)
);
```
## 4 Table: `Admin`
Stores clinic system administrators
### Columns
| Column       | Data Type    | Constraints                           |
| ------------ | ------------ | ------------------------------------- |
| `admin_id`   | INT UNSIGNED | **PRIMARY KEY**, AUTO_INCREMENT       |
| `username`   | VARCHAR(50)  | UNIQUE, NOT NULL                      |
| `email`      | VARCHAR(100) | UNIQUE, NOT NULL                      |
| `password`   | VARCHAR(255) | NOT NULL (store **hashed** passwords) |
| `created_at` | TIMESTAMP    | DEFAULT CURRENT_TIMESTAMP             |

### SQL
```sql
CREATE TABLE admin (
    admin_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 5 Table: Clinic_Locations
Stores the locations of clinic branches

### Columns
| Column        | Data Type    | Constraints                     |
| ------------- | ------------ | ------------------------------- |
| `location_id` | INT UNSIGNED | **PRIMARY KEY**, AUTO_INCREMENT |
| `name`        | VARCHAR(100) | NOT NULL                        |
| `address`     | TEXT         | NOT NULL                        |
| `phone`       | VARCHAR(20)  | NULLABLE                        |

### SQL
```sql
CREATE TABLE clinic_locations (
    location_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address TEXT NOT NULL,
    phone VARCHAR(20)
);
```
## 6 Table: Payments
Tracks patient payments for appointments

### Columns
| Column           | Data Type                                           | Constraints                                                      |
| ---------------- | --------------------------------------------------- | ---------------------------------------------------------------- |
| `payment_id`     | INT UNSIGNED                                        | **PRIMARY KEY**, AUTO_INCREMENT                                  |
| `appointment_id` | INT UNSIGNED                                        | **FOREIGN KEY → appointments(appointment_id)** ON DELETE CASCADE |
| `amount`         | DECIMAL(10,2)                                       | NOT NULL                                                         |
| `payment_date`   | DATETIME                                            | DEFAULT CURRENT_TIMESTAMP                                        |
| `payment_method` | ENUM('Cash','Credit Card','Insurance') NOT NULL     |                                                                  |
| `status`         | ENUM('Paid','Pending','Refunded') DEFAULT 'Pending' |                                                                  |

### SQL
```sql
CREATE TABLE payments (
    payment_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    appointment_id INT UNSIGNED NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    payment_method ENUM('Cash','Credit Card','Insurance') NOT NULL,
    status ENUM('Paid','Pending','Refunded') DEFAULT 'Pending',
    FOREIGN KEY (appointment_id)
        REFERENCES appointments(appointment_id)
        ON DELETE CASCADE
);
```

##  Design & Constraints Considerations
- **Email/Phone Validation:** Handled at the application layer.
- **Deleting Patients:** `appointments` are set to CASCADE, so deleting a patient deletes their appointments.
- **Deleting Doctors:** Set to RESTRICT if appointments exist.
- **No Overlaps:** Application logic or a UNIQUE index should prevent double-booking for a doctor at the same date/time.
- **Security:** Store hashed passwords (e.g., bcrypt) in the `admin` table.



## MongoDB Collection Design
