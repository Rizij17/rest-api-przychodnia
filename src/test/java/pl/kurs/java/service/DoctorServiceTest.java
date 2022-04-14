package pl.kurs.java.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import pl.kurs.java.model.Doctor;
import pl.kurs.java.repository.DoctorRepository;



import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DoctorServiceTest {

    @InjectMocks
    private DoctorService doctorService;
    @Mock
    private DoctorRepository doctorRepositoryMock;

    private Doctor doctor1, doctor2;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        doctorService = new DoctorService(doctorRepositoryMock);
        doctor1 = Doctor.builder()
                .name("Kamil")
                .surname("Banasik")
                .type("Kardiolog")
                .animalType("Pies")
                .nip("110098")
                .rate(50)
                .build();
        doctor2 = Doctor.builder()
                .name("Agata")
                .surname("Niedziela")
                .type("Laryngolog")
                .animalType("Kot")
                .nip("090807")
                .rate(75)
                .build();
    }

    @Test
    void shouldFireDoctor() {
        doctorService.fire(doctor1);
        assertTrue(doctor1.isFired());
    }

    @Test
    void shouldSaveDoctor() {
        Mockito.when(doctorRepositoryMock.saveAndFlush(doctor1)).thenReturn(doctor1);

        Doctor saved = doctorService.save(doctor1);

        Mockito.verify(doctorRepositoryMock).saveAndFlush(saved);
    }


}