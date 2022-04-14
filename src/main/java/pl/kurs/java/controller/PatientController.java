package pl.kurs.java.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kurs.java.error.EntityNotFoundException;
import pl.kurs.java.model.Patient;
import pl.kurs.java.model.command.CreatePatientCommand;
import pl.kurs.java.model.command.UpdatePatientCommand;
import pl.kurs.java.model.dto.AppointmentDto;
import pl.kurs.java.model.dto.PatientDto;
import pl.kurs.java.service.PatientService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final ModelMapper modelMapper;

    @GetMapping("/{id}")
    public ResponseEntity<PatientDto> getSinglePatient(@PathVariable int id) {
        return patientService.findById(id)
                .map(patient -> modelMapper.map(patient, PatientDto.class))
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Patient", Integer.toString(id)));
    }

    @GetMapping
    public ResponseEntity<Page<PatientDto>> getAllPatients(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(patientService.findAll(pageable)
                .map(patient -> modelMapper.map(patient, PatientDto.class)));
    }

    @GetMapping("/{id}/appointments")
    public ResponseEntity<List<AppointmentDto>> getPatientAppointments(@PathVariable int id) {
        return ResponseEntity.ok(patientService.findByIdWithAppointments(id).orElseThrow(() -> new EntityNotFoundException("Patient", Integer.toString(id)))
                .getAppointments()
                .stream()
                .map(appointment -> modelMapper.map(appointment, AppointmentDto.class))
                .collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<PatientDto> savePatient(@RequestBody @Valid CreatePatientCommand createPatientCommand) {
        Patient patient = patientService.save(new Patient(
                createPatientCommand.getName(),
                createPatientCommand.getType(),
                createPatientCommand.getBreed(),
                createPatientCommand.getOwnerName(),
                createPatientCommand.getOwnerSurname(),
                createPatientCommand.getEmail()));
        return new ResponseEntity(modelMapper.map(patient, PatientDto.class), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientDto> editPatient(@PathVariable int id, @RequestBody UpdatePatientCommand updatePatientCommand) {
        Patient toEdit = patientService.findById(id).orElseThrow(() -> new EntityNotFoundException("Patient", Integer.toString(id)));
        Patient edited = patientService.edit(toEdit, updatePatientCommand);
        return new ResponseEntity(modelMapper.map(edited, PatientDto.class), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deletePatient(@PathVariable int id) {
        patientService.deleteById(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
