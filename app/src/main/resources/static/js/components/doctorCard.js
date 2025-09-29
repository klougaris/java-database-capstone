// Import necessary functions
import { showBookingOverlay } from './loggedPatient.js';
import { deleteDoctor } from './doctorServices.js';
import { fetchPatientDetails } from './patientServices.js';

/**
 * Creates a DOM element representing a single doctor card
 * @param {Object} doctor - Doctor data (name, specialization, email, availableSlots)
 * @returns {HTMLElement} - Doctor card element
 */
export function createDoctorCard(doctor) {
  const userRole = localStorage.getItem('userRole');
  const token = localStorage.getItem('token');

  // Main doctor card container
  const card = document.createElement('div');
  card.classList.add('doctor-card');

  // Doctor info container
  const infoContainer = document.createElement('div');
  infoContainer.classList.add('doctor-info');

  // Doctor name
  const nameEl = document.createElement('h3');
  nameEl.textContent = doctor.name;
  infoContainer.appendChild(nameEl);

  // Doctor specialization
  const specializationEl = document.createElement('p');
  specializationEl.textContent = `Specialization: ${doctor.specialization}`;
  infoContainer.appendChild(specializationEl);

  // Doctor email
  const emailEl = document.createElement('p');
  emailEl.textContent = `Email: ${doctor.email}`;
  infoContainer.appendChild(emailEl);

  // Available appointment times
  const slotsEl = document.createElement('p');
  slotsEl.textContent = `Available Slots: ${doctor.availableSlots.join(', ')}`;
  infoContainer.appendChild(slotsEl);

  // Actions container
  const actionsContainer = document.createElement('div');
  actionsContainer.classList.add('doctor-actions');

  // === ADMIN ROLE ACTIONS ===
  if (userRole === 'admin') {
    const deleteBtn = document.createElement('button');
    deleteBtn.textContent = 'Delete';
    deleteBtn.classList.add('adminBtn');

    deleteBtn.addEventListener('click', async () => {
      const adminToken = localStorage.getItem('token');
      if (!adminToken) {
        alert('Admin not authenticated');
        return;
      }
      try {
        const result = await deleteDoctor(doctor.id, adminToken);
        if (result.success) {
          alert('Doctor deleted successfully');
          card.remove();
        } else {
          alert('Failed to delete doctor');
        }
      } catch (err) {
        console.error(err);
        alert('Error deleting doctor');
      }
    });

    actionsContainer.appendChild(deleteBtn);
  }

  // === PATIENT (NOT LOGGED-IN) ROLE ACTIONS ===
  if (!userRole || userRole === 'patient') {
    const bookBtn = document.createElement('button');
    bookBtn.textContent = 'Book Now';
    bookBtn.classList.add('book-btn');

    bookBtn.addEventListener('click', () => {
      alert('Please log in to book an appointment');
    });

    actionsContainer.appendChild(bookBtn);
  }

  // === LOGGED-IN PATIENT ROLE ACTIONS ===
  if (userRole === 'loggedPatient') {
    const bookBtn = document.createElement('button');
    bookBtn.textContent = 'Book Now';
    bookBtn.classList.add('book-btn');

    bookBtn.addEventListener('click', async () => {
      if (!token) {
        alert('Session expired. Please log in again.');
        window.location.href = '/';
        return;
      }
      try {
        const patientData = await fetchPatientDetails(token);
        showBookingOverlay(doctor, patientData);
      } catch (err) {
        console.error(err);
        alert('Failed to fetch patient data');
      }
    });

    actionsContainer.appendChild(bookBtn);
  }

  // Append info and actions to card
  card.appendChild(infoContainer);
  card.appendChild(actionsContainer);

  return card;
}
