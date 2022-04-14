package pl.kurs.java.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.kurs.java.model.Appointment;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    Optional<Appointment> findById(int id);

    Page<Appointment> findAll(Pageable pageable);

//    boolean existByDoctorAndDateBetween(LocalDateTime start, LocalDateTime plusHours, Doctor doctor);
}
