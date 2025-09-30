// Import required modules
import { openModal, closeModal } from "./modal.js";
import { getDoctors, filterDoctors, saveDoctor } from "./doctorService.js";
import { createDoctorCard } from "./doctorCard.js"; // assumes you have a utility for doctor card creation

// When "Add Doctor" button is clicked, open the modal
document.getElementById("addDoctorBtn")?.addEventListener("click", () => {
  openModal("addDoctor");
});

// When DOM is loaded, load doctor cards
document.addEventListener("DOMContentLoaded", () => {
  loadDoctorCards();

  // Attach filter event listeners
  document.getElementById("searchBar")?.addEventListener("input", filterDoctorsOnChange);
  document.getElementById("timeFilter")?.addEventListener("change", filterDoctorsOnChange);
  document.getElementById("specialtyFilter")?.addEventListener("change", filterDoctorsOnChange);
});

/**
 * Load and render all doctor cards
 */
async function loadDoctorCards() {
  try {
    const doctors = await getDoctors();
    renderDoctorCards(doctors);
  } catch (error) {
    console.error("Error loading doctor cards:", error);
  }
}

/**
 * Filter doctors when input or filter values change
 */
async function filterDoctorsOnChange() {
  try {
    const name = document.getElementById("searchBar")?.value.trim() || null;
    const time = document.getElementById("timeFilter")?.value || null;
    const specialty = document.getElementById("specialtyFilter")?.value || null;

    const data = await filterDoctors(name, time, specialty);

    if (data.doctors && data.doctors.length > 0) {
      renderDoctorCards(data.doctors);
    } else {
      const contentDiv = document.getElementById("content");
      contentDiv.innerHTML = `<p>No doctors found with the given filters.</p>`;
    }
  } catch (error) {
    console.error("Error filtering doctors:", error);
    alert("Failed to filter doctors. Please try again later.");
  }
}

/**
 * Render a list of doctor cards into the content area
 * @param {Array} doctors - List of doctor objects
 */
function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById("content");
  contentDiv.innerHTML = "";
  doctors.forEach((doctor) => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);
  });
}

/**
 * Handle adding a new doctor from the modal form
 */
window.adminAddDoctor = async function () {
  try {
    // Collect form values
    const name = document.getElementById("doctorName")?.value.trim();
    const email = document.getElementById("doctorEmail")?.value.trim();
    const phone = document.getElementById("doctorPhone")?.value.trim();
    const password = document.getElementById("doctorPassword")?.value.trim();
    const specialty = document.getElementById("doctorSpecialty")?.value.trim();
    const availableTimes = document.getElementById("doctorAvailableTimes")?.value.trim();

    // Retrieve token
    const token = localStorage.getItem("token");
    if (!token) {
      alert("Authentication required. Please log in again.");
      return;
    }

    // Build doctor object
    const doctor = {
      name,
