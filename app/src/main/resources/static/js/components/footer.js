// footer.js
// Dynamically renders the footer content across all pages

function renderFooter() {
  const footerDiv = document.getElementById("footer");
  if (!footerDiv) return; // Safety check if #footer does not exist

  footerDiv.innerHTML = `
    <footer class="footer">
      <div class="footer-container">

        <!-- Hospital Logo & Copyright -->
        <div class="footer-logo">
          <img src="../assets/images/logo/logo.png" alt="Hospital CMS Logo">
          <p>© Copyright 2025. All Rights Reserved by Hospital CMS.</p>
        </div>

        <!-- Footer Links Section -->
        <div class="footer-links">

          <!-- Company Column -->
          <div class="footer-column">
            <h4>Company</h4>
            <a href="#">About</a>
            <a href="#">Careers</a>
            <a href="#">Press</a>
          </div>

          <!-- Support Column -->
          <div class="footer-column">
            <h4>Support</h4>
            <a href="#">Account</a>
            <a href="#">Help Center</a>
            <a href="#">Contact Us</a>
          </div>

          <!-- Legals Column -->
          <div class="footer-column">
            <h4>Legals</h4>
            <a href="#">Terms & Conditions</a>
            <a href="#">Privacy Policy</a>
            <a href="#">Licensing</a>
          </div>

        </div> <!-- End of footer-links -->

      </div> <!-- End of footer-container -->
    </footer>
  `;
}

// Automatically render the footer when the DOM is ready
document.addEventListener("DOMContentLoaded", renderFooter);
