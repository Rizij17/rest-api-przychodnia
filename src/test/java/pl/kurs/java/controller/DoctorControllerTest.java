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
import pl.kurs.java.model.Doctor;
import pl.kurs.java.model.command.CreateDoctorCommand;
import pl.kurs.java.model.command.UpdatedDoctorCommand;
import pl.kurs.java.model.dto.DoctorDto;
import pl.kurs.java.repository.DoctorRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
class DoctorControllerTest {

    @Autowired
    private MockMvc postman;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DoctorRepository doctorRepository;

    @BeforeEach
    void clean() {
        doctorRepository.deleteAll();
    }

    @Test
    void shouldGetSingleDoctor() throws Exception {
        Doctor testDoc = new Doctor("Test", "Testowy", "Laryngolog", "Pies", 50, "112233", false);
        Doctor savedDoc = doctorRepository.save(testDoc);

        postman.perform(MockMvcRequestBuilders.get("/doctors/{id}", savedDoc.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(testDoc.getName()))
                .andExpect(jsonPath("$.surname").value(testDoc.getSurname()))
                .andExpect(jsonPath("$.type").value(testDoc.getType()))
                .andExpect(jsonPath("$.animalType").value(testDoc.getAnimalType()))
                .andExpect(jsonPath("$.rate").value(testDoc.getRate()))
                .andExpect(jsonPath("$.nip").value(testDoc.getNip()))
                .andExpect(jsonPath("$.fired").value(testDoc.isFired()));
    }

    @Test
    void shouldNotGetSingleDoctor() throws Exception {
        postman.perform(MockMvcRequestBuilders.get("/doctors/{id}", 100))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse().getErrorMessage();
    }

    @Test
    void shouldAddDoctor() throws Exception {
        String requestJson = objectMapper.writeValueAsString(
                CreateDoctorCommand.builder()
                        .name("Test")
                        .surname("Testowy")
                        .type("Kardiolog")
                        .animalType("Pies")
                        .rate(50)
                        .nip("112334")
                        .fired(false)
                        .build());

        String response = postman.perform(MockMvcRequestBuilders.post("doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        int id = JsonPath.read(response, "id");

        postman.perform(MockMvcRequestBuilders.get("/doctors/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test"))
                .andExpect(jsonPath("$.surname").value("Testowy"))
                .andExpect(jsonPath("$.type").value("Kardiolog"))
                .andExpect(jsonPath("$.animalType").value("Pies"))
                .andExpect(jsonPath("$.rate").value(50))
                .andExpect(jsonPath("$.nip").value("112334"))
                .andExpect(jsonPath("$.fired").value(false));
    }

    @Test
    void shouldNotAddDoctorWhenNameIsEmpty() throws Exception {
        String requestJson = objectMapper.writeValueAsString(
                CreateDoctorCommand.builder()
                        .surname("Testowy")
                        .type("Kardiolog")
                        .animalType("Pies")
                        .rate(50)
                        .nip("112334")
                        .fired(false)
                        .build());

        postman.perform(MockMvcRequestBuilders.post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].message").value("NAME_NOT_EMPTY"))
                .andExpect(jsonPath("$[0].field").value("name"));
    }

    @Test
    void shouldNotAddDoctorWhenSurnameIsEmpty() throws Exception {
        String requestJson = objectMapper.writeValueAsString(
                CreateDoctorCommand.builder()
                        .name("Testowy")
                        .type("Kardiolog")
                        .animalType("Pies")
                        .rate(50)
                        .nip("112334")
                        .fired(false)
                        .build());

        postman.perform(MockMvcRequestBuilders.post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].message").value("NAME_NOT_EMPTY"))
                .andExpect(jsonPath("$[0].field").value("name"));
    }

    @Test
    void shouldNotAddDoctorWithBadType() throws Exception {
        String requestJson = objectMapper.writeValueAsString(
                CreateDoctorCommand.builder()
                        .name("Test")
                        .surname("Testowy")
                        .type("Kardio")
                        .animalType("Pies")
                        .rate(50)
                        .nip("112334")
                        .fired(false)
                        .build());

        postman.perform(MockMvcRequestBuilders.post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].message").value("VALUE_NOT_ALLOWED"))
                .andExpect(jsonPath("$[0].field").value("type"));
    }

    @Test
    void shouldNotAddDoctorWithBadAnimalType() throws Exception {
        String requestJson = objectMapper.writeValueAsString(
                CreateDoctorCommand.builder()
                        .name("Test")
                        .surname("Testowy")
                        .type("Kardiolog")
                        .animalType("Ryba")
                        .rate(50)
                        .nip("112334")
                        .fired(false)
                        .build());

        postman.perform(MockMvcRequestBuilders.post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].message").value("VALUE_NOT_ALLOWED"))
                .andExpect(jsonPath("$[0].field").value("animalType"));
    }

    @Test
    void shouldThrowOptimisticLockException() throws Exception {
        Doctor doctor = new Doctor("Test", "Testowy", "Laryngolog", "Pies", 50, "234567", false);
        Doctor savedDoctor = doctorRepository.save(doctor);
        int doctorId = savedDoctor.getId();

        String content = postman.perform(MockMvcRequestBuilders.get("/doctors/" + doctorId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        DoctorDto beforeUpdate = objectMapper.readValue(content, DoctorDto.class);

        String requestJson = objectMapper.writeValueAsString(
                UpdatedDoctorCommand.builder()
                        .name("Fafik")
                        .surname(beforeUpdate.getSurname())
                        .animalType(beforeUpdate.getAnimalType())
                        .rate(beforeUpdate.getRate())
                        .nip(beforeUpdate.getNip())
                        .type(beforeUpdate.getType())
                        .version(beforeUpdate.getVersion())
                        .build());

        String afterFirstEdit = postman.perform(MockMvcRequestBuilders.put("/doctors/" + doctorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        DoctorDto afterUpdate = objectMapper.readValue(afterFirstEdit, DoctorDto.class);

        assertEquals(1, afterUpdate.getVersion());

        String secondRequest = objectMapper.writeValueAsString(
                UpdatedDoctorCommand.builder()
                        .name(beforeUpdate.getName())
                        .surname(beforeUpdate.getSurname())
                        .type(beforeUpdate.getType())
                        .nip(beforeUpdate.getNip())
                        .rate(beforeUpdate.getRate())
                        .animalType(beforeUpdate.getAnimalType())
                        .version(beforeUpdate.getVersion()));
    }

    @Test
    void shouldFiredDoctor() throws Exception {
        Doctor testDoc = new Doctor("Test", "Testowy", "Laryngolog", "Pies", 50, "112233", false);
        Doctor savedDoc = doctorRepository.save(testDoc);

        postman.perform(MockMvcRequestBuilders.put("/doctors/{id}/fire", savedDoc.getId()));

        postman.perform(MockMvcRequestBuilders.get("/doctors/{id}", savedDoc.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(testDoc.getName()))
                .andExpect(jsonPath("$.surname").value(testDoc.getSurname()))
                .andExpect(jsonPath("$.type").value(testDoc.getType()))
                .andExpect(jsonPath("$.animalType").value(testDoc.getAnimalType()))
                .andExpect(jsonPath("$.rate").value(testDoc.getRate()))
                .andExpect(jsonPath("$.nip").value(testDoc.getNip()))
                .andExpect(jsonPath("$.fired").value(true));
    }


}