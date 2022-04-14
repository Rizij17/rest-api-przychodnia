package pl.kurs.java.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.kurs.java.model.Doctor;

import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Integer> {

    Optional<Doctor> findById(int id);

    boolean existsByNip(String nip);

    @Query(value = "select distinct d from Doctor d left join fetch d.appointments",
            countQuery = "select count(d) from Doctor d")
    Page<Doctor> findAllWithAppointments(Pageable pageable);

    @Query("select d from Doctor d left join fetch d.appointments where d.id = ?1")
    Optional<Doctor> findByIdWithAppointments(int id);
}

