package com.project.back_end.repo;

import com.project.back_end.models.Prescription;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface PrescriptionRepository extends MongoRepository<Prescription, String> {

  public List<Prescription> findByAppointmentId(Long appointmentId);
  
}

