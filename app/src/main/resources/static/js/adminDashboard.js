// ==========================================================
// File: adminDashboard.js
// Description: Admin Dashboard functionality for managing doctors
// ==========================================================

// Import required modules
import { openModal } from '../components/modals.js';
import { getDoctors, filterDoctors, saveDoctor } from '../services/doctorServices.js';
import { createDoctorCard } from '../components/doctorCard.js';

/* =========================================
   Event Binding: "Add Doctor" Button
========================================= */
const addDoctorBtn = document.getElementById('addDocBtn');
if (addDoctorBtn) {
  addDoctorBtn.addEventListener('click', () => {
    openModal('addDoctor');
  });
}

/* =========================================
   Load Doctor Cards on Page Load
========================================= */
window.addEventListener('DOMContentLoaded', async () => {
  await loadDoctorCards();

  // Event listeners for search and filter
  const searchBar = document.getElementById('searchBar');
  const filterTime = document.getElementById('filterTime');
  const filterSpecialty = document.getElementById('filterSpecialty');

  if (searchBar) searchBar.addEventListener('input', filterDoctorsOnChange);
  if (filterTime) filterTime.addEventListener('change', filterDoctorsOnChange);
  if (filterSpecialty) filterSpecialty.addEventListener('change', filterDoctorsOnChange);
});

/* =========================================
   Function: loadDoctorCards
   Purpose: Fetch and display all doctors
========================================= */
async function loadDoctorCards() {
  try {
    const contentDiv = document.getElementById('content');
    contentDiv.innerHTML = ''; // Clear previous content

    const doctors = await getDoctors();

    if (doctors && doctors.length > 0) {
      doctors.forEach((doctor) => {
        const card = createDoctorCard(doctor);
        contentDiv.appendChild(card);
      });
    } else {
      contentDiv.innerHTML = '<p class="no-doctor-record">No doctors found.</p>';
    }
  } catch (error) {
    console.error('Error loading doctors:', error);
    alert('Failed to load doctor data.');
  }
}

/* =========================================
   Function: filterDoctorsOnChange
   Purpose: Filter doctors based on search and filters
========================================= */
async function filterDoctorsOnChange() {
  try {
    const name = document.getElementById('searchBar')?.value.trim() || null;
    const time = document.getElementById('filterTime')?.value || null;
    const specialty = document.getElementById('filterSpecialty')?.value || null;

    const filteredDoctors = await filterDoctors(name, time, specialty);

    renderDoctorCards(filteredDoctors);
  } catch (error) {
    console.error('Error filtering doctors:', error);
    alert('Failed to filter doctors.');
  }
}

/* =========================================
   Function: renderDoctorCards
   Purpose: Render a list of doctors to the dashboard
========================================= */
function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById('content');
  contentDiv.innerHTML = ''; // Clear previous content

  if (doctors && doctors.length > 0) {
    doctors.forEach((doctor) => {
      const card = createDoctorCard(doctor);
      contentDiv.appendChild(card);
    });
  } else {
    contentDiv.innerHTML = '<p class="no-doctor-record">No doctors found with the given filters.</p>';
  }
}

/* =========================================
   Function: adminAddDoctor
   Purpose: Collect form data and add a new doctor
========================================= */
window.adminAddDoctor = async function () {
  try {
    const name = document.getElementById('doctorName').value.trim();
    const email = document.getElementById('doctorEmail').value.trim();
    const password = document.getElementById('doctorPassword').value.trim();
    const mobile = document.getElementById('doctorMobile').value.trim();
    const specialty = document.getElementById('doctorSpecialty').value;
    const availabilityCheckboxes = document.querySelectorAll('input[name="availability"]:checked');
    const availabilityTimes = Array.from(availabilityCheckboxes).map(cb => cb.value);

    const token = localStorage.getItem('token');
    if (!token) {
      alert('You are not authenticated! Please log in as admin.');
      return;
    }

    const doctor = { name, email, password, mobile, specialty, availability: availabilityTimes };

    const success = await saveDoctor(doctor, token);

    if (success) {
      alert('Doctor added successfully!');
      // Close modal and reload doctor list
      const closeBtn = document.getElementById('closeModal');
      if (closeBtn) closeBtn.click();
      await loadDoctorCards();
    } else {
      alert('Failed to add doctor. Please check the input and try again.');
    }
  } catch (error) {
    console.error('Error adding doctor:', error);
    alert('Something went wrong while adding the doctor.');
  }
};
