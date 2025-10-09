// ==========================================================
// File: index.js
// Description: Handles role-based login for Admin and Doctor
// Includes modal interactions, API calls, and localStorage management
// ==========================================================

// Import dependencies
import { openModal } from '../components/modals.js';
import { API_BASE_URL } from '../config/config.js';

// Define API endpoints
const ADMIN_API = API_BASE_URL + '/admin';
const DOCTOR_API = API_BASE_URL + '/doctor/login';

// Ensure DOM is loaded before attaching event listeners
window.onload = function () {
  // Select login buttons
  const adminBtn = document.getElementById('adminBtn');
  const doctorBtn = document.getElementById('doctorBtn');

  // Attach click listeners to open respective login modals
  if (adminBtn) {
    adminBtn.addEventListener('click', () => {
      openModal('adminLogin');
    });
  }

  if (doctorBtn) {
    doctorBtn.addEventListener('click', () => {
      openModal('doctorLogin');
    });
  }
};

/* =========================================
   Admin Login Handler
========================================= */
window.adminLoginHandler = async function () {
  try {
    // Get username and password from modal input fields
    const username = document.getElementById('adminUsername').value.trim();
    const password = document.getElementById('adminPassword').value.trim();

    // Build admin credentials object
    const admin = { username, password };

    // Send POST request to Admin login endpoint
    const response = await fetch(ADMIN_API, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(admin)
    });

    // Check if login was successful
    if (response.ok) {
      const data = await response.json();
      localStorage.setItem('token', data.token);
      // Call helper function to set role and proceed
      window.selectRole('admin');
    } else {
      alert('Invalid credentials!');
    }

  } catch (error) {
    console.error('Admin login error:', error);
    alert('Something went wrong! Please try again later.');
  }
};

/* =========================================
   Doctor Login Handler
========================================= */
window.doctorLoginHandler = async function () {
  try {
    // Get email and password from modal input fields
    const email = document.getElementById('doctorEmail').value.trim();
    const password = document.getElementById('doctorPassword').value.trim();

    // Build doctor credentials object
    const doctor = { email, password };

    // Send POST request to Doctor login endpoint
    const response = await fetch(DOCTOR_API, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(doctor)
    });

    // Check if login was successful
    if (response.ok) {
      const data = await response.json();
      localStorage.setItem('token', data.token);
      // Call helper function to set role and proceed
      window.selectRole('doctor');
    } else {
      alert('Invalid credentials!');
    }

  } catch (error) {
    console.error('Doctor login error:', error);
    alert('Something went wrong! Please try again later.');
  }
};
