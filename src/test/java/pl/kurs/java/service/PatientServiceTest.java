package pl.kurs.java.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import pl.kurs.java.model.Patient;
import pl.kurs.java.repository.PatientRepository;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PatientServiceTest {

    @InjectMocks
    private PatientService patientService;
    @Mock
    private PatientRepository patientRepositoryMock;

    private Patient patient1, patient2;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        patientService = new PatientService(patientRepositoryMock);
        patient1 = Patient.builder()
                .name("Fafik")
                .type("Pies")
                .breed("Owczarek")
                .ownerName("Katarzyna")
                .ownerSurname("Wawelska")
                .email("kasia123@gmail.com")
                .build();
        patient2 = Patient.builder()
                .name("Misia")
                .type("Kot")
                .breed("Dachowiec")
                .ownerName("Agata")
                .ownerSurname("Młynarska")
                .email("mlynarska.a@gmail.com")
                .build();
    }

    @Test
    void shouldSavePatient() {
        Mockito.when(patientRepositoryMock.saveAndFlush(patient1)).thenReturn(patient1);

        Patient saved = patientService.save(patient1);

        Mockito.verify(patientRepositoryMock).saveAndFlush(saved);
    }

}