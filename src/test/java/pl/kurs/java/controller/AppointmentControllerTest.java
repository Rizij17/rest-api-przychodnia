package pl.kurs.java.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.kurs.java.Main;
import pl.kurs.java.error.EntityNotFoundException;
import pl.kurs.java.model.Appointment;
import pl.kurs.java.model.Doctor;
import pl.kurs.java.model.Patient;
import pl.kurs.java.model.command.UpdateAppointmentCommand;
import pl.kurs.java.model.dto.AppointmentDto;
import pl.kurs.java.repository.AppointmentRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
class AppointmentControllerTest {

    @Autowired
    private MockMvc postman;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AppointmentRepository appointmentRepository;

    @Test
    void shouldThrowOptimisticLockException() throws Exception {
        Doctor doctor = new Doctor("Maksymilian", "Gąbka", "Kardiolog", "Pies", 50, "113355", false);
        Patient patient = new Patient("Max", "Pies", "Owczarek", "Kamil", "Banaszewsi", "kamil13@gmail.com");
        Appointment appointment = new Appointment(LocalDateTime.now(), doctor, patient);
        Appointment appointmentSaved = appointmentRepository.save(appointment);
        int appointmentId = appointmentSaved.getId();

        String content = postman.perform(MockMvcRequestBuilders.get("/appointments/" + appointmentId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        AppointmentDto beforeUpdate = objectMapper.readValue(content, AppointmentDto.class);

        String requestJson = objectMapper.writeValueAsString(
                UpdateAppointmentCommand.builder()
                        .start(LocalDateTime.of(2022, 10, 2, 15, 0))
                        .version(beforeUpdate.getVersion())
                        .build());

        String afterFirstEdit = postman.perform(MockMvcRequestBuilders.put("/appointments/" + appointmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        AppointmentDto afterUpdate = objectMapper.readValue(afterFirstEdit, AppointmentDto.class);

        assertEquals(1, afterUpdate.getVersion());

        String secondRequest = objectMapper.writeValueAsString(
                UpdateAppointmentCommand.builder()
                        .start(beforeUpdate.getStart())
                        .version(beforeUpdate.getVersion()));
    }

    @Test
    void shouldNotGetAppointmentWithBadId() throws Exception {
        postman.perform(MockMvcRequestBuilders.get("/appointment/{id}", 100))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof EntityNotFoundException))
                .andExpect(jsonPath("entityName").value("Appointment"))
                .andExpect(jsonPath("entityKey").value(100));
    }
}