package pl.kurs.java.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pl.kurs.java.model.Doctor;


import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class DoctorRepositoryTest {

    @Autowired
    private DoctorRepository doctorRepository;

    @Test
    void itShouldCheckIfNipExists() {
        String nip = "111543";
        Doctor doctor = new Doctor("Test", "Test", "Kardiolog", "Pies", 50, nip,false);
        doctorRepository.save(doctor);

        boolean expected = doctorRepository.existsByNip(nip);

        assertTrue(expected);
    }

    @Test
    void itShouldCheckIfNipDoesNotExists() {
        String nip = "119877";

        boolean expected = doctorRepository.existsByNip(nip);

        assertFalse(expected);
    }
}