// ==========================================================
// File: doctorDashboard.js
// Description: Doctor Dashboard functionality for viewing patient appointments
// ==========================================================

// Import required modules
import { getAllAppointments } from './services/appointmentRecordService.js';
import { createPatientRow } from './components/patientRows.js';

// ==========================================================
// Global Variables
// ==========================================================
const tableBody = document.getElementById('patientTableBody');
let selectedDate = new Date().toISOString().split('T')[0]; // YYYY-MM-DD
const token = localStorage.getItem('token');
let patientName = null; // used for search filtering

// ==========================================================
// Search Bar Functionality
// ==========================================================
const searchBar = document.getElementById('searchBar');
if (searchBar) {
  searchBar.addEventListener('input', () => {
    const inputValue = searchBar.value.trim();
    patientName = inputValue !== '' ? inputValue : null;
    loadAppointments();
  });
}

// ==========================================================
// Filter Controls
// ==========================================================

// "Today's Appointments" button
const todayButton = document.getElementById('todayButton');
if (todayButton) {
  todayButton.addEventListener('click', () => {
    selectedDate = new Date().toISOString().split('T')[0];
    const datePicker = document.getElementById('datePicker');
    if (datePicker) datePicker.value = selectedDate;
    loadAppointments();
  });
}

// Date Picker
const datePicker = document.getElementById('datePicker');
if (datePicker) {
  datePicker.addEventListener('change', () => {
    selectedDate = datePicker.value;
    loadAppointments();
  });
}

// ==========================================================
// Function: loadAppointments
// Purpose: Fetch and render appointments based on date and patient name
// ==========================================================
async function loadAppointments() {
  try {
    if (!tableBody) return;

    // Clear previous table content
    tableBody.innerHTML = '';

    // Fetch appointment data from backend
    const appointments = await getAllAppointments(selectedDate, patientName, token);

    if (!appointments || appointments.length === 0) {
      // Show fallback message if no appointments
      const row = document.createElement('tr');
      row.innerHTML = `<td colspan="5" class="noPatientRecord">No Appointments found for ${selectedDate}.</td>`;
      tableBody.appendChild(row);
      return;
    }

    // Render each appointment as a table row
    appointments.forEach((appt) => {
      const patient = {
        id: appt.id,
        name: appt.name,
        phone: appt.phone,
        email: appt.email,
        prescription: appt.prescription || ''
      };
      const tr = createPatientRow(patient);
      tableBody.appendChild(tr);
    });
  } catch (error) {
    console.error('Error loading appointments:', error);
    const row = document.createElement('tr');
    row.innerHTML = `<td colspan="5" class="noPatientRecord">Error loading appointments. Try again later.</td>`;
    tableBody.appendChild(row);
  }
}

// ==========================================================
// Initial Render on Page Load
// ==========================================================
window.addEventListener('DOMContentLoaded', () => {
  if (typeof renderContent === 'function') renderContent(); // optional UI setup
  loadAppointments(); // load today's appointments by default
});
