package pl.kurs.java.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pl.kurs.java.model.Patient;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PatientRepositoryTest {

    @Autowired
    private PatientRepository patientRepository;

    @Test
    void itShouldCheckIfEmailExists() {
        String email = "test@gmail.com";
        Patient patient = new Patient("Test", "Pies", "Buldog", "Adam", "Adamski", email);
        patientRepository.save(patient);

        boolean expected = patientRepository.existsByEmail(email);

        assertTrue(expected);
    }

    @Test
    void itShouldCheckIfEmailDoesNotExists() {
        String email = "test@gmail.com";

        boolean expected = patientRepository.existsByEmail(email);

        assertFalse(expected);
    }
}