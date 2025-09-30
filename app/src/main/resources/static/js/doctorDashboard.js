// Import services/utilities
import { getAllAppointments } from "./appointmentService.js";
import { createPatientRow } from "./patientRow.js";

// DOM references
const tableBody = document.getElementById("patientTableBody");
const searchBar = document.getElementById("searchBar");
const todayBtn = document.getElementById("todayBtn");
const datePicker = document.getElementById("datePicker");

// Initial state
let selectedDate = new Date().toISOString().split("T")[0]; // YYYY-MM-DD
let token = localStorage.getItem("token");
let patientName = null;

// Search bar filter
searchBar?.addEventListener("input", () => {
  const value = searchBar.value.trim();
  patientName = value !== "" ? value : "null";
  loadAppointments();
});

// "Today" button filter
todayBtn?.addEventListener("click", () => {
  selectedDate = new Date().toISOString().split("T")[0];
  if (datePicker) datePicker.value = selectedDate;
  loadAppointments();
});

// Date picker filter
datePicker?.addEventListener("change", () => {
  selectedDate = datePicker.value;
  loadAppointments();
});

/**
 * Load appointments based on date and optional patient name
 */
async function loadAppointments() {
  try {
    const appointments = await getAllAppointments(selectedDate, patientName, token);

    // Clear previous rows
    tableBody.innerHTML = "";

    if (!appointments || appointments.length === 0) {
      const row = document.createElement("tr");
      row.innerHTML = `
        <td colspan="4" style="text-align: center; padding: 1rem;">
          No Appointments found for today.
        </td>`;
      tableBody.appendChild(row);
      return;
    }

    // Render appointments
    appointments.forEach((appointment) => {
      const patient = {
        id: appointment.id,
        name: appointment.name,
        phone: appointment.phone,
        email: appointment.email,
      };
      const row = createPatientRow(patient, appointment);
      tableBody.appendChild(row);
    });
  } catch (error) {
    console.error("Error loading appointments:", error);
    tableBody.innerHTML = `
      <tr>
        <td colspan="4" style="text-align: center; padding: 1rem;">
          Error loading appointments. Try again later.
        </td>
      </tr>`;
  }
}

// On page load
document.addEventListener("DOMContentLoaded", () => {
  if (typeof renderContent === "function") {
    renderContent();
  }
  loadAppointments();
});
