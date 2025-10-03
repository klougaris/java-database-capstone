package com.project.back_end.services;


@Service
public class PrescriptionService {
    

    private final PrescriptionRepository prescriptionRepository;

    // Constructor injection
    public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    @Transactional
    public ResponseEntity<Map<String, String>> savePrescription(Prescription prescription) {
        Map<String, String> response = new HashMap<>();
        try {
            // Check if a prescription already exists for the appointment
            if (prescriptionRepository.existsByAppointmentId(prescription.getAppointment().getId())) {
                response.put("message", "Prescription already exists for this appointment");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Save the new prescription
            prescriptionRepository.save(prescription);
            response.put("message", "Prescription saved");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "An error occurred while saving the prescription");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Transactional
public ResponseEntity<Map<String, Object>> getPrescription(Long appointmentId) {
    Map<String, Object> response = new HashMap<>();
    try {
        // Attempt to fetch the prescription
        Prescription prescription = prescriptionRepository.findByAppointmentId(appointmentId);

        if (prescription == null) {
            response.put("error", "Prescription not found for this appointment");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // Build response
        response.put("prescription", prescription);
        return ResponseEntity.ok(response);

    } catch (Exception e) {
        e.printStackTrace();
        response.put("error", "An error occurred while retrieving the prescription");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

}
