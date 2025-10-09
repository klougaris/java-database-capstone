// ==========================================================
// File: doctorCard.js
// Description:
//   Creates reusable doctor card components for Admin and Patient dashboards.
//   Each card displays doctor information (name, specialty, email, availability)
//   and provides role-specific actions like "Delete" (Admin) or "Book Now" (Patient).
//
// NOTE:
//   This component uses helper functions imported from service files that will
//   be implemented in the next lab:
//
//   - deleteDoctor() from: /js/services/doctorServices.js
//   - getPatientData() from: /js/services/patientServices.js
//
//   These service modules will handle API interactions and are part of the
//   modular architecture designed for better maintainability and code reuse.
// ==========================================================

// ===== Imports =====
import { deleteDoctor } from "../services/doctorServices.js";
import { getPatientData } from "../services/patientServices.js";
import { showBookingOverlay } from "../loggedPatient.js";

// ===== Main Function =====
export function createDoctorCard(doctor) {
  // Create main card container
  const card = document.createElement("div");
  card.classList.add("doctor-card");

  // Fetch user role from localStorage
  const role = localStorage.getItem("userRole");

  // ===== Doctor Info Section =====
  const infoDiv = document.createElement("div");
  infoDiv.classList.add("doctor-info");

  const name = document.createElement("h3");
  name.textContent = doctor.name || "Unknown Doctor";

  const specialization = document.createElement("p");
  specialization.textContent = `Specialization: ${doctor.specialization || "N/A"}`;

  const email = document.createElement("p");
  email.textContent = `Email: ${doctor.email || "N/A"}`;

  const availability = document.createElement("p");
  const availableTimes = Array.isArray(doctor.availability)
    ? doctor.availability.join(", ")
    : doctor.availability || "Not specified";
  availability.textContent = `Availability: ${availableTimes}`;

  // Append info to info container
  infoDiv.appendChild(name);
  infoDiv.appendChild(specialization);
  infoDiv.appendChild(email);
  infoDiv.appendChild(availability);

  // ===== Actions Section =====
  const actionsDiv = document.createElement("div");
  actionsDiv.classList.add("card-actions");

  // ===== ADMIN ROLE =====
  if (role === "admin") {
    const removeBtn = document.createElement("button");
    removeBtn.textContent = "Delete";
    removeBtn.classList.add("delete-btn");

    removeBtn.addEventListener("click", async () => {
      const confirmDelete = confirm(`Are you sure you want to delete Dr. ${doctor.name}?`);
      if (!confirmDelete) return;

      try {
        const token = localStorage.getItem("token");
        if (!token) {
          alert("Unauthorized. Please log in again.");
          return;
        }

        const success = await deleteDoctor(doctor.id, token);
        if (success) {
          alert(`Doctor ${doctor.name} removed successfully.`);
          card.remove(); // Remove from DOM
        } else {
          alert("Failed to remove doctor. Please try again.");
        }
      } catch (err) {
        console.error("Error deleting doctor:", err);
        alert("An error occurred while deleting the doctor.");
      }
    });

    actionsDiv.appendChild(removeBtn);
  }

  // ===== PATIENT (NOT LOGGED-IN) ROLE =====
  else if (role === "patient") {
    const bookNow = document.createElement("button");
    bookNow.textContent = "Book Now";
    bookNow.classList.add("book-btn");

    bookNow.addEventListener("click", () => {
      alert("Please log in to book an appointment.");
    });

    actionsDiv.appendChild(bookNow);
  }

  // ===== LOGGED-IN PATIENT ROLE =====
  else if (role === "loggedPatient") {
    const bookNow = document.createElement("button");
    bookNow.textContent = "Book Now";
    bookNow.classList.add("book-btn");

    bookNow.addEventListener("click", async (e) => {
      const token = localStorage.getItem("token");
      if (!token) {
        alert("Session expired. Please log in again.");
        window.location.href = "/";
        return;
      }

      try {
        const patientData = await getPatientData(token);
        if (!patientData) {
          alert("Unable to retrieve your account details. Please log in again.");
          return;
        }

        // Show booking overlay
        showBookingOverlay(e, doctor, patientData);
      } catch (error) {
        console.error("Error fetching patient data:", error);
        alert("Unable to load booking form. Try again later.");
      }
    });

    actionsDiv.appendChild(bookNow);
  }

  // ===== Final Assembly =====
  card.appendChild(infoDiv);
  card.appendChild(actionsDiv);

  return card;
}
