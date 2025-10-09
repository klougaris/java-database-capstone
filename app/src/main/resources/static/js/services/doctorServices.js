// ==========================================================
// File: doctorServices.js
// Description:
//   This service module handles all API interactions related to doctor data.
//   It includes functions for fetching, filtering, adding, and deleting doctors.
//
//   By organizing all doctor-related API calls here, we maintain clean separation
//   between UI logic (in dashboard files) and backend communication logic.
//
// ==========================================================

// ===== Imports =====
import { API_BASE_URL } from "../config/config.js";

// ===== Constants =====
const DOCTOR_API = `${API_BASE_URL}/doctor`;

// ==========================================================
// Function: getDoctors
// Purpose : Retrieve all doctors from the backend API
// ==========================================================
export async function getDoctors() {
  try {
    const response = await fetch(`${DOCTOR_API}`);
    if (!response.ok) throw new Error(`HTTP Error! Status: ${response.status}`);

    const data = await response.json();
    return data.doctors || []; // Expecting the API to return { doctors: [...] }
  } catch (error) {
    console.error("Error fetching doctors:", error);
    return []; // Return empty list on failure
  }
}

// ==========================================================
// Function: deleteDoctor
// Purpose : Delete a specific doctor using their ID and admin token
// ==========================================================
export async function deleteDoctor(id, token) {
  try {
    const response = await fetch(`${DOCTOR_API}/delete/${id}/${token}`, {
      method: "DELETE",
    });

    if (!response.ok) {
      const errorData = await response.json();
      return {
        success: false,
        message: errorData.message || "Failed to delete doctor.",
      };
    }

    const result = await response.json();
    return {
      success: result.success || true,
      message: result.message || "Doctor deleted successfully.",
    };
  } catch (error) {
    console.error("Error deleting doctor:", error);
    return {
      success: false,
      message: "An error occurred while deleting the doctor.",
    };
  }
}

// ==========================================================
// Function: saveDoctor
// Purpose : Save (create) a new doctor via POST request
// ==========================================================
export async function saveDoctor(doctor, token) {
  try {
    const response = await fetch(`${DOCTOR_API}/save/${token}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(doctor),
    });

    const data = await response.json();
    return {
      success: data.success || false,
      message: data.message || "Unable to add doctor.",
    };
  } catch (error) {
    console.error("Error saving doctor:", error);
    return {
      success: false,
      message: "An error occurred while saving the doctor.",
    };
  }
}

// ==========================================================
// Function: filterDoctors
// Purpose : Fetch doctors based on filter criteria (name, time, specialty)
// ==========================================================
export async function filterDoctors(name = "", time = "", specialty = "") {
  try {
    const queryParams = new URLSearchParams({
      name,
      time,
      specialty,
    });

    const response = await fetch(`${DOCTOR_API}/filter?${queryParams.toString()}`);

    if (!response.ok) {
      console.error("Failed to filter doctors:", response.status);
      return [];
    }

    const data = await response.json();
    return data.doctors || [];
  } catch (error) {
    console.error("Error filtering doctors:", error);
    alert("Unable to fetch filtered doctors. Please try again later.");
    return [];
  }
}
