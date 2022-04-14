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
import pl.kurs.java.model.Doctor;
import pl.kurs.java.model.command.CreateDoctorCommand;
import pl.kurs.java.model.command.UpdatedDoctorCommand;
import pl.kurs.java.model.dto.AppointmentDto;
import pl.kurs.java.model.dto.DoctorDto;
import pl.kurs.java.service.DoctorService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;
    private final ModelMapper modelMapper;

    @GetMapping("/{id}")
    public ResponseEntity<DoctorDto> getSingleDoctor(@PathVariable int id) {
        return doctorService.findById(id)
                .map(doctor -> modelMapper.map(doctor, DoctorDto.class))
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Doctor", Integer.toString(id)));
    }

    @GetMapping
    public ResponseEntity<Page<DoctorDto>> getAllDoctors(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(doctorService.findAll(pageable)
                .map(doctor -> modelMapper.map(doctor, DoctorDto.class)));
    }

    @GetMapping("/{id}/appointments")
    public ResponseEntity<List<AppointmentDto>> getDoctorAppointments(int id) {
        return ResponseEntity.ok(doctorService.findByIdWithAppointments(id).orElseThrow(() -> new EntityNotFoundException("Doctor", Integer.toString(id)))
                .getAppointments()
                .stream()
                .map(appointment -> modelMapper.map(appointment, AppointmentDto.class))
                .collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<DoctorDto> saveDoctor(@RequestBody @Valid CreateDoctorCommand createDoctorCommand) {
        Doctor doctor = doctorService.save(new Doctor(
                createDoctorCommand.getName(),
                createDoctorCommand.getSurname(),
                createDoctorCommand.getType(),
                createDoctorCommand.getAnimalType(),
                createDoctorCommand.getRate(),
                createDoctorCommand.getNip(),
                createDoctorCommand.isFired()));
        return new ResponseEntity(modelMapper.map(doctor, DoctorDto.class), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<DoctorDto> editDoctor(@PathVariable int id, @RequestBody UpdatedDoctorCommand updatedDoctorCommand) {
        Doctor toEdit = doctorService.findById(id).orElseThrow(() -> new EntityNotFoundException("Doctor", Integer.toString(id)));
        Doctor edited = doctorService.edit(toEdit, updatedDoctorCommand);
        return new ResponseEntity(modelMapper.map(edited, DoctorDto.class), HttpStatus.OK);
    }

    @PutMapping({"/{id}/fire"})
    public ResponseEntity<DoctorDto> fireDoctor(@PathVariable int id) {
        Doctor toFire = doctorService.findById(id).orElseThrow(() -> new EntityNotFoundException("Doctor", Integer.toString(id)));
        Doctor fired = doctorService.fire(toFire);
        return new ResponseEntity(modelMapper.map(fired, DoctorDto.class), HttpStatus.OK);
    }
}
