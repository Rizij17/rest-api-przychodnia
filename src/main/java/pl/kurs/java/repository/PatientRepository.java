package pl.kurs.java.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.kurs.java.model.Patient;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Integer> {

    Optional<Patient> findById(int id);

    boolean existsByEmail(String email);

    @Query(value = "select distinct p from Patient p left join fetch p.appointments",
            countQuery = "select count(p) from Patient p")
    Page<Patient> findAllWithAppointments(Pageable pageable);

    @Query("select p from Patient p left join fetch p.appointments where p.id = ?1")
    Optional<Patient> findByIdWithAppointments(int id);
}
