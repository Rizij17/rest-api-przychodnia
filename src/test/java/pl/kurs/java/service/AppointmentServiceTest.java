package pl.kurs.java.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import pl.kurs.java.model.Appointment;
import pl.kurs.java.model.Doctor;
import pl.kurs.java.model.Patient;
import pl.kurs.java.repository.AppointmentRepository;
import pl.kurs.java.repository.ConfirmationTokenRepository;
import pl.kurs.java.repository.DoctorRepository;
import pl.kurs.java.repository.PatientRepository;

import java.time.LocalDateTime;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AppointmentServiceTest {

    @InjectMocks
    private AppointmentService appointmentService;
    @Mock
    private AppointmentRepository appointmentRepositoryMock;
    @Mock
    private DoctorRepository doctorRepositoryMock;
    @Mock
    private PatientRepository patientRepositoryMock;
    @Mock
    private ConfirmationTokenRepository confirmationTokenRepositoryMock;

    private Appointment appointment1, appointment2;

    @BeforeEach
    void init(){
        MockitoAnnotations.openMocks(this);
        appointmentService = new AppointmentService(appointmentRepositoryMock, doctorRepositoryMock, patientRepositoryMock, confirmationTokenRepositoryMock);
        Doctor doctor1 = Doctor.builder()
                .name("Kamil")
                .surname("Banasik")
                .type("Kardiolog")
                .animalType("Pies")
                .nip("110098")
                .rate(50)
                .build();
        Doctor doctor2 = Doctor.builder()
                .name("Agata")
                .surname("Niedziela")
                .type("Laryngolog")
                .animalType("Kot")
                .nip("090807")
                .rate(75)
                .build();
        Patient patient1 = Patient.builder()
                .name("Fafik")
                .type("Pies")
                .breed("Owczarek")
                .ownerName("Katarzyna")
                .ownerSurname("Wawelska")
                .email("kasia123@gmail.com")
                .build();
       Patient patient2 = Patient.builder()
                .name("Misia")
                .type("Kot")
                .breed("Dachowiec")
                .ownerName("Agata")
                .ownerSurname("Młynarska")
                .email("mlynarska.a@gmail.com")
                .build();

       appointment1 = new Appointment(LocalDateTime.of(2022,4,17,15,00), doctor1, patient2);
       appointment2 = new Appointment(LocalDateTime.of(2022,4,16,10,30), doctor2, patient1);
    }

    @Test
    void shouldSaveAppointment(){
        Mockito.when(appointmentRepositoryMock.saveAndFlush(appointment1)).thenReturn(appointment1);

        Appointment saved = appointmentService.save(appointment1);

        Mockito.verify(appointmentRepositoryMock).saveAndFlush(saved);
    }





}