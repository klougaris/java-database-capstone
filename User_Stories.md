# User Story Template
**Title:**
_As a [user role], I want [feature/goal], so that [reason]._
**Acceptance Criteria:**
1. [Criteria 1]
2. [Criteria 2]
3. [Criteria 3]
**Priority:** [High/Medium/Low]
**Story Points:** [Estimated Effort in Points]
**Notes:**
- [Additional information or edge cases]
# User Stories for Portal Application

## Admin User Stories

### 1. Admin Login
**Title:**
_As an admin, I want to log into the portal with my username and password, so that I can manage the platform securely._
**Acceptance Criteria:**
1. Admin can enter username and password.
2. Login validates credentials against the database.
3. Successful login redirects to the admin dashboard.
4. Unsuccessful login shows an error message.
**Priority:** High
**Story Points:** 3
**Notes:**
- Consider lockout after multiple failed login attempts.

### 2. Admin Logout
**Title:**
_As an admin, I want to log out of the portal, so that system access is protected when I leave._
**Acceptance Criteria:**
1. Admin can click a logout button.
2. Session is terminated upon logout.
3. Admin is redirected to the login page.
**Priority:** High
**Story Points:** 2
**Notes:**
- Ensure logout invalidates all active sessions for security.

### 3. Add Doctor Profile
**Title:**
_As an admin, I want to add doctors to the portal, so that they can be managed and assigned appointments._
**Acceptance Criteria:**
1. Admin can enter doctor details (name, specialization, contact info).
2. Doctor profile is saved to the database.
3. Admin receives confirmation of successful addition.
**Priority:** Medium
**Story Points:** 3
**Notes:**
- Validate that email and contact info are unique.

### 4. Delete Doctor Profile
**Title:**
_As an admin, I want to delete a doctor's profile from the portal, so that outdated or incorrect profiles are removed._
**Acceptance Criteria:**
1. Admin can select a doctor to delete.
2. Confirmation prompt appears before deletion.
3. Doctor’s profile is removed from the database upon confirmation.
**Priority:** Medium
**Story Points:** 3
**Notes:**
- Consider removing or reassigning the doctor's future appointments.

### 5. Run Stored Procedure for Appointment Statistics
**Title:**
_As an admin, I want to run a stored procedure in the MySQL CLI to get the number of appointments per month, so that I can track usage statistics._
**Acceptance Criteria:**
1. Admin can execute the stored procedure from the MySQL CLI.
2. Output shows the number of appointments grouped by month.
3. Results are accurate and up-to-date.
**Priority:** Medium
**Story Points:** 2
**Notes:**
- Ensure the procedure handles months with zero appointments correctly.

---

## Patient User Stories

### 1. View Doctors Without Logging In
**Title:**
_As a patient, I want to view a list of doctors without logging in, so that I can explore options before registering._
**Acceptance Criteria:**
1. Patient can access a page listing all available doctors.
2. No login or registration is required to view the list.
3. Each doctor entry shows name, specialization, and contact info.
**Priority:** Medium
**Story Points:** 2
**Notes:**
- Consider sorting or filtering options by specialization or location.

### 2. Patient Sign-Up
**Title:**
_As a patient, I want to sign up using my email and password, so that I can book appointments._
**Acceptance Criteria:**
1. Patient can enter required registration details (name, email, password, contact info).
2. Email is validated for uniqueness.
3. Account is created successfully and patient is redirected to login page.
**Priority:** High
**Story Points:** 3
**Notes:**
- Consider email verification before first login.

### 3. Patient Login
**Title:**
_As a patient, I want to log into the portal, so that I can manage my bookings._
**Acceptance Criteria:**
1. Patient can enter email and password to log in.
2. Login validates credentials against the database.
3. Successful login redirects to patient dashboard.
4. Unsuccessful login shows an error message.
**Priority:** High
**Story Points:** 3
**Notes:**
- Include lockout mechanism after multiple failed login attempts.

### 4. Patient Logout
**Title:**
_As a patient, I want to log out of the portal, so that my account is secure._
**Acceptance Criteria:**
1. Patient can click a logout button.
2. Session is terminated upon logout.
3. Patient is redirected to the login page.
**Priority:** High
**Story Points:** 2
**Notes:**
- Ensure logout invalidates all active sessions.

### 5. Book an Hour-Long Appointment
**Title:**
_As a patient, I want to book an hour-long appointment with a doctor, so that I can consult with them._
**Acceptance Criteria:**
1. Patient can select a doctor and choose an available time slot.
2. Appointment duration is set to one hour by default.
3. Appointment is saved in the database and confirmation is sent to the patient.
**Priority:** High
**Story Points:** 3
**Notes:**
- Prevent double-booking for the same doctor/time slot.

### 6. View Upcoming Appointments
**Title:**
_As a patient, I want to view my upcoming appointments, so that I can prepare accordingly._
**Acceptance Criteria:**
1. Patient can access a list of all upcoming appointments.
2. Each appointment shows doctor name, date, time, and location.
3. Patient can cancel or reschedule appointments if allowed.
**Priority:** Medium
**Story Points:** 2
**Notes:**
- Consider showing reminders or notifications for upcoming appointments.

---

## Doctor User Stories

### 1. Doctor Login
**Title:**
_As a doctor, I want to log into the portal, so that I can manage my appointments._
**Acceptance Criteria:**
1. Doctor can enter email/username and password to log in.
2. Login validates credentials against the database.
3. Successful login redirects to the doctor dashboard.
4. Unsuccessful login shows an error message.
**Priority:** High
**Story Points:** 3
**Notes:**
- Include lockout mechanism after multiple failed login attempts.

### 2. Doctor Logout
**Title:**
_As a doctor, I want to log out of the portal, so that my data is protected._
**Acceptance Criteria:**
1. Doctor can click a logout button.
2. Session is terminated upon logout.
3. Doctor is redirected to the login page.
**Priority:** High
**Story Points:** 2
**Notes:**
- Ensure logout invalidates all active sessions for security.

### 3. View Appointment Calendar
**Title:**
_As a doctor, I want to view my appointment calendar, so that I can stay organized._
**Acceptance Criteria:**
1. Doctor can access a calendar view of all appointments.
2. Appointments are displayed by date and time.
3. Past, current, and upcoming appointments are clearly differentiated.
**Priority:** Medium
**Story Points:** 3
**Notes:**
- Include filtering options by day, week, or month.

### 4. Mark Unavailability
**Title:**
_As a doctor, I want to mark my unavailability, so that patients see only the available slots._
**Acceptance Criteria:**
1. Doctor can select time slots as unavailable in the calendar.
2. Unavailable slots are blocked for booking by patients.
3. Changes are updated in real-time for patient visibility.
**Priority:** Medium
**Story Points:** 3
**Notes:**
- Include recurring unavailability options (e.g., weekly off).

### 5. Update Profile Information
**Title:**
_As a doctor, I want to update my profile with specialization and contact information, so that patients have up-to-date information._
**Acceptance Criteria:**
1. Doctor can edit profile fields such as specialization, contact number, and email.
2. Updated information is saved in the database.
3. Changes are immediately reflected in patient views.
**Priority:** Medium
**Story Points:** 2
**Notes:**
- Validate that contact information is in correct format.

### 6. View Patient Details for Appointments
**Title:**
_As a doctor, I want to view patient details for upcoming appointments, so that I can be prepared._
**Acceptance Criteria:**
1. Doctor can click on an appointment to see patient name, contact info, and appointment reason.
2. Only upcoming appointment details are visible.
3. Sensitive information is protected and accessible only to the assigned doctor.
**Priority:** Medium
**Story Points:** 3
**Notes:**
- Include a privacy warning or consent notice if necessary.
