package com.project.back_end.repo;


import com.project.back_end.models.Doctor; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

   public Doctor findByEmail(String email);

   @Query("SELECT d FROM Doctor d WHERE d.name LIKE CONCAT('%', :name, '%')")
   public List<Doctor> findByNameLike(String name);

   @Query("SELECT d FROM Doctor d " +
           "WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "AND LOWER(d.specialty) = LOWER(:specialty)")
   public List<Doctor> findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(String name, String specialty);

   @Query("SELECT d FROM Doctor d WHERE LOWER(d.specialty) = LOWER(:specialty)")
   public List<Doctor> findBySpecialtyIgnoreCase(String specialty);

}
