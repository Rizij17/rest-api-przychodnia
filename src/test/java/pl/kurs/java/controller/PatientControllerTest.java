package pl.kurs.java.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.kurs.java.Main;

import pl.kurs.java.error.EntityNotFoundException;
import pl.kurs.java.model.Patient;
import pl.kurs.java.model.command.CreatePatientCommand;
import pl.kurs.java.model.command.UpdatePatientCommand;
import pl.kurs.java.model.dto.PatientDto;
import pl.kurs.java.repository.PatientRepository;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
class PatientControllerTest {

    @Autowired
    private MockMvc postman;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PatientRepository patientRepository;

    @BeforeEach
    void clean() {
        patientRepository.deleteAll();
    }

    @Test
    void shouldGetSinglePatient() throws Exception {
        Patient testPatient = new Patient("Test", "Pies", "Testowa", "Testowy", "Testowy", "test@gmail.com");
        Patient savedPatient = patientRepository.save(testPatient);

        postman.perform(MockMvcRequestBuilders.get("/patients/{id}", savedPatient.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(testPatient.getName()))
                .andExpect(jsonPath("$.type").value(testPatient.getType()))
                .andExpect(jsonPath("$.breed").value(testPatient.getBreed()))
                .andExpect(jsonPath("$.ownerName").value(testPatient.getOwnerName()))
                .andExpect(jsonPath("$.ownerSurname").value(testPatient.getOwnerSurname()))
                .andExpect(jsonPath("$.email").value(testPatient.getEmail()));
    }

    @Test
    void shouldNotGetPatientWithBadId() throws Exception {
        postman.perform(MockMvcRequestBuilders.get("/patients/{id}",100))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof EntityNotFoundException))
                .andExpect(jsonPath("entityName").value("Patient"))
                .andExpect(jsonPath("entityKey").value(100));
    }

    @Test
    void shouldAddPatient() throws Exception {
        String requestJson = objectMapper.writeValueAsString(
                CreatePatientCommand.builder()
                        .name("Test")
                        .type("Pies")
                        .breed("Psia")
                        .ownerName("Tes")
                        .ownerSurname("Towy")
                        .email("test@gmail.com")
                        .build());

        String response = postman.perform(MockMvcRequestBuilders.post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        int id = JsonPath.read(response, "id");

        postman.perform(MockMvcRequestBuilders.get("/patients/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test"))
                .andExpect(jsonPath("$.type").value("Pies"))
                .andExpect(jsonPath("$.breed").value("Psia"))
                .andExpect(jsonPath("$.ownerName").value("Tes"))
                .andExpect(jsonPath("$.ownerSurname").value("Towy"))
                .andExpect(jsonPath("$.email").value("test@gmail.com"));
    }

    @Test
    void shouldNotAddPatientWhenNameIsEmpty() throws Exception {
        String requestJson = objectMapper.writeValueAsString(
                CreatePatientCommand.builder()
                        .type("Pies")
                        .breed("Psia")
                        .ownerName("T")
                        .ownerSurname("T")
                        .email("t@gmail.com")
                        .build());

        postman.perform(MockMvcRequestBuilders.post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].message").value("NAME_NOT_EMPTY"))
                .andExpect(jsonPath("$[0].field").value("name"));
    }

    @Test
    void shouldNotAddPatientWithBadType() throws Exception {
        String requestJson = objectMapper.writeValueAsString(
                CreatePatientCommand.builder()
                        .name("Test")
                        .type("Robak")
                        .breed("Psia")
                        .ownerName("Tes")
                        .ownerSurname("Towy")
                        .email("test@gmail.com")
                        .build());

        postman.perform(MockMvcRequestBuilders.post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].message").value("VALUE_NOT_ALLOWED"))
                .andExpect(jsonPath("$[0].field").value("type"));
    }

    @Test
    void shouldEditPatient() throws Exception {
        Patient testPatient = new Patient("Test", "Pies", "Testowa", "Testowy", "Testowy", "test@gmail.com");
        Patient savedPatient = patientRepository.save(testPatient);
        int patientId = savedPatient.getId();

        String requestJson = objectMapper.writeValueAsString(
                UpdatePatientCommand.builder()
                        .name("edited")
                        .ownerName("edited")
                        .ownerSurname("edited")
                        .email("edited@gmail.com")
                        .version(savedPatient.getVersion())
                        .build());

        postman.perform(MockMvcRequestBuilders.put("/patients/{id}", patientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());

        postman.perform(MockMvcRequestBuilders.get("/patients/" + patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("edited"))
                .andExpect(jsonPath("$.ownerName").value("edited"))
                .andExpect(jsonPath("$.ownerSurname").value("edited"))
                .andExpect(jsonPath("$.email").value("edited@gmail.com"));
    }

    @Test
    void shouldThrowOptimisticLockException() throws Exception {
        Patient testPatient = new Patient("Test", "Pies", "Testowa", "Testowy", "Testowy", "test@gmail.com");
        Patient savedPatient = patientRepository.save(testPatient);
        int patientId = savedPatient.getId();

        String content = postman.perform(MockMvcRequestBuilders.get("/patients/" + Integer.toString(patientId)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        PatientDto beforeUpdate = objectMapper.readValue(content, PatientDto.class);

        String requestJson = objectMapper.writeValueAsString(
                UpdatePatientCommand.builder()
                        .name("Fafik")
                        .ownerName(beforeUpdate.getOwnerName())
                        .ownerSurname(beforeUpdate.getOwnerSurname())
                        .email(beforeUpdate.getEmail())
                        .version(beforeUpdate.getVersion())
                        .build());

        String afterFirstEdit = postman.perform(MockMvcRequestBuilders.put("/patients/" + Integer.toString(patientId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        PatientDto afterUpdate = objectMapper.readValue(afterFirstEdit, PatientDto.class);

        assertEquals(1, afterUpdate.getVersion());

        String secondRequest = objectMapper.writeValueAsString(
                UpdatePatientCommand.builder()
                        .name(beforeUpdate.getName())
                        .ownerSurname(beforeUpdate.getOwnerSurname())
                        .email(beforeUpdate.getEmail())
                        .version(beforeUpdate.getVersion()));
    }

    @Test
    void shouldDeletePatient() throws Exception {
        Patient testPatient = new Patient("Test", "Pies", "Testowa", "Testowy", "Testowy", "test@gmail.com");
        Patient savedPatient = patientRepository.save(testPatient);
        int patientId = savedPatient.getId();

        postman.perform(MockMvcRequestBuilders.delete("/patients/{id}", patientId))
                .andExpect(status().isNoContent());

        postman.perform(MockMvcRequestBuilders.get("/doctors/{id}", patientId))
                .andExpect(status().isNotFound());
    }


}