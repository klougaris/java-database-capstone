// Import required utilities
import { openModal } from "../components/modals.js";
import { BASE_API_URL } from "../config/config.js";

// Define API endpoints
const ADMIN_API = `${BASE_API_URL}/admin/login`;
const DOCTOR_API = `${BASE_API_URL}/doctor/login`;

// Ensure DOM elements are ready
window.onload = () => {
  const adminLoginBtn = document.getElementById("adminLogin");
  const doctorLoginBtn = document.getElementById("doctorLogin");

  if (adminLoginBtn) {
    adminLoginBtn.addEventListener("click", () => openModal("adminLogin"));
  }

  if (doctorLoginBtn) {
    doctorLoginBtn.addEventListener("click", () => openModal("doctorLogin"));
  }
};

// Admin login handler
window.adminLoginHandler = async function () {
  try {
    // Step 1: Collect credentials
    const username = document.getElementById("adminUsername")?.value;
    const password = document.getElementById("adminPassword")?.value;

    if (!username || !password) {
      alert("Please enter both username and password.");
      return;
    }

    // Step 2: Create admin object
    const admin = { username, password };

    // Step 3: Send POST request
    const response = await fetch(ADMIN_API, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(admin),
    });

    // Step 4: Handle success
    if (response.ok) {
      const data = await response.json();
      localStorage.setItem("token", data.token);
      selectRole("admin");
    } else {
      // Step 5: Handle invalid credentials
      alert("Invalid admin credentials. Please try again.");
    }
  } catch (error) {
    // Step 6: Handle network/server errors
    console.error("Admin login error:", error);
    alert("Something went wrong. Please try again later.");
  }
};

// Doctor login handler
window.doctorLoginHandler = async function () {
  try {
    // Step 1: Collect credentials
    const email = document.getElementById("doctorEmail")?.value;
    const password = document.getElementById("doctorPassword")?.value;

    if (!email || !password) {
      alert("Please enter both email and password.");
      return;
    }

    // Step 2: Create doctor object
    const doctor = { email, password };

    // Step 3: Send POST request
    const response = await fetch(DOCTOR_API, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(doctor),
    });

    // Step 4: Handle success
    if (response.ok) {
      const data = await response.json();
      localStorage.setItem("token", data.token);
      selectRole("doctor");
    } else {
      // Step 5: Handle invalid credentials
      alert("Invalid doctor credentials. Please try again.");
    }
  } catch (error) {
    // Step 6: Handle network/server errors
    console.error("Doctor login error:", error);
    alert("Something went wrong. Please try again later.");
  }
};
