// ==========================================================
// File: header.js
// Description: Dynamically renders the header based on user role and login state.
// ==========================================================

function renderHeader() {
  const headerDiv = document.getElementById("header");
  if (!headerDiv) return;

  // Handle homepage/root page
  if (window.location.pathname.endsWith("/")) {
    localStorage.removeItem("userRole");
    localStorage.removeItem("token");
    headerDiv.innerHTML = `
      <header class="header">
        <div class="logo-section">
          <img src="../assets/images/logo/logo.png" alt="Hospital CMS Logo" class="logo-img">
          <span class="logo-title">Hospital CMS</span>
        </div>
      </header>`;
    return;
  }

  const role = localStorage.getItem("userRole");
  const token = localStorage.getItem("token");

  // Session expired or invalid login
  if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
    localStorage.removeItem("userRole");
    alert("Session expired or invalid login. Please log in again.");
    window.location.href = "/";
    return;
  }

  // Initialize header content
  let headerContent = `
    <header class="header">
      <div class="logo-section">
        <img src="../assets/images/logo/logo.png" alt="Hospital CMS Logo" class="logo-img">
        <span class="logo-title">Hospital CMS</span>
      </div>
      <nav>
  `;

  // Role-based header buttons
  if (role === "admin") {
    headerContent += `
      <button id="addDocBtn" class="adminBtn" onclick="openModal('addDoctor')">Add Doctor</button>
      <a href="#" onclick="logout()">Logout</a>`;
  } else if (role === "doctor") {
    headerContent += `
      <button class="adminBtn" onclick="selectRole('doctor')">Home</button>
      <a href="#" onclick="logout()">Logout</a>`;
  } else if (role === "patient") {
    headerContent += `
      <button id="patientLogin" class="adminBtn">Login</button>
      <button id="patientSignup" class="adminBtn">Sign Up</button>`;
  } else if (role === "loggedPatient") {
    headerContent += `
      <button id="home" class="adminBtn" onclick="window.location.href='/pages/loggedPatientDashboard.html'">Home</button>
      <button id="patientAppointments" class="adminBtn" onclick="window.location.href='/pages/patientAppointments.html'">Appointments</button>
      <a href="#" onclick="logoutPatient()">Logout</a>`;
  }

  // Close nav and header
  headerContent += `</nav></header>`;

  // Inject header into DOM
  headerDiv.innerHTML = headerContent;

  // Attach event listeners for dynamically created buttons
  attachHeaderButtonListeners();
}

// ==========================================================
// Attach event listeners for header buttons dynamically
// ==========================================================
function attachHeaderButtonListeners() {
  const patientLoginBtn = document.getElementById("patientLogin");
  const patientSignupBtn = document.getElementById("patientSignup");
  const addDoctorBtn = document.getElementById("addDocBtn");

  if (patientLoginBtn) {
    patientLoginBtn.addEventListener("click", () => openModal("patientLogin"));
  }
  if (patientSignupBtn) {
    patientSignupBtn.addEventListener("click", () => openModal("patientSignup"));
  }
  if (addDoctorBtn) {
    addDoctorBtn.addEventListener("click", () => openModal("addDoctor"));
  }
}

// ==========================================================
// Logout function for admin/doctor/loggedPatient
// ==========================================================
function logout() {
  localStorage.removeItem("token");
  localStorage.removeItem("userRole");
  window.location.href = "/";
}

// ==========================================================
// Logout function for logged-in patient
// ==========================================================
function logoutPatient() {
  localStorage.removeItem("token");
  localStorage.setItem("userRole", "patient");
  window.location.href = "/pages/patientDashboard.html";
}

// Render the header on page load
renderHeader();
