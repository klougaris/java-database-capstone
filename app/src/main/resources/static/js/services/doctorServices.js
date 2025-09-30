// Import API base URL
import { BASE_API_URL } from "../config/config.js";

// Define doctor API endpoint
const DOCTOR_API = `${BASE_API_URL}/doctor`;

/**
 * Fetch the list of all doctors
 */
export async function getDoctors() {
  try {
    const response = await fetch(DOCTOR_API);
    const data = await response.json();
    return data.doctors || [];
  } catch (error) {
    console.error("Error fetching doctors:", error);
    return [];
  }
}

/**
 * Delete a doctor by ID
 * @param {string} doctorId - ID of the doctor
 * @param {string} token - Auth token
 */
export async function deleteDoctor(doctorId, token) {
  try {
    const response = await fetch(`${DOCTOR_API}/${doctorId}/${token}`, {
      method: "DELETE",
    });
    const data = await response.json();
    return {
      success: response.ok,
      message: data.message || "Deletion failed.",
    };
  } catch (error) {
    console.error("Error deleting doctor:", error);
    return {
      success: false,
      message: "Something went wrong. Could not delete doctor.",
    };
  }
}

/**
 * Save (create) a new doctor
 * @param {Object} doctor - Doctor object
 * @param {string} token - Auth token
 */
export async function saveDoctor(doctor, token) {
  try {
    const response = await fetch(`${DOCTOR_API}/${token}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(doctor),
    });
    const data = await response.json();
    return {
      success: response.ok,
      message: data.message || "Doctor save failed.",
    };
  } catch (error) {
    console.error("Error saving doctor:", error);
    return {
      success: false,
      message: "Something went wrong. Could not save doctor.",
    };
  }
}

/**
 * Fetch doctors with filtering
 * @param {string} name - Doctor name
 * @param {string} time - Available time
 * @param {string} specialty - Specialty field
 */
export async function filterDoctors(name, time, specialty) {
  try {
    const response = await fetch(
      `${DOCTOR_API}/filter/${name}/${time}/${specialty}`
    );

    if (response.ok) {
      return await response.json();
    } else {
      console.error("Failed to fetch filtered doctors:", response.status);
      return { doctors: [] };
    }
  } catch (error) {
    console.error("Error filtering doctors:", error);
    alert("Something went wrong while filtering doctors.");
    return { doctors: [] };
  }
}
