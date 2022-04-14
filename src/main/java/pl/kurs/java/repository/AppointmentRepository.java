package pl.kurs.java.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.kurs.java.model.Appointment;
import pl.kurs.java.model.Doctor;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    Optional<Appointment> findById(int id);

    Page<Appointment> findAll(Pageable pageable);

    boolean existsByDoctorAndDateBetween(LocalDateTime start, LocalDateTime end, Doctor doctor);
}
