package pl.kurs.java.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kurs.java.error.EntityNotFoundException;;
import pl.kurs.java.model.Appointment;
import pl.kurs.java.model.ConfirmationToken;
import pl.kurs.java.model.command.CreateAppointmentCommand;
import pl.kurs.java.model.command.UpdateAppointmentCommand;
import pl.kurs.java.model.dto.AppointmentDto;
import pl.kurs.java.service.AppointmentService;
import pl.kurs.java.service.ConfirmationTokenService;

import javax.validation.Valid;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final ModelMapper modelMapper;
    private final ConfirmationTokenService confirmationTokenService;

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDto> getSingleAppointment(@PathVariable int id) {
        return appointmentService.findById(id)
                .map(appointment -> modelMapper.map(appointment, AppointmentDto.class))
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Appointment", Integer.toString(id)));
    }

    @GetMapping
    public ResponseEntity<Page<AppointmentDto>> getAllAppointments(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(appointmentService.findAll(pageable)
                .map(a -> modelMapper.map(a, AppointmentDto.class)));
    }

//    @PostMapping
//    public ResponseEntity<AppointmentDto> bookAppointment(@RequestBody @Valid CreateAppointmentCommand createAppointmentCommand) {
//        Appointment appointment = appointmentService.save(modelMapper.map(createAppointmentCommand, Appointment.class));
//        return new ResponseEntity<>(modelMapper.map(appointment, AppointmentDto.class), HttpStatus.CREATED);
//    }

    @PostMapping
    public ResponseEntity saveAppointment(@RequestBody @Valid CreateAppointmentCommand createAppointmentCommand) {
        Appointment appointment = appointmentService.createAppointment(createAppointmentCommand);
        return new ResponseEntity<>(modelMapper.map(appointment, AppointmentDto.class), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentDto> editAppointment(@PathVariable int id, @Valid @RequestBody UpdateAppointmentCommand updateAppointmentCommand) {
        Appointment toEdit = appointmentService.findById(id).orElseThrow(() -> new EntityNotFoundException("Appointment", Integer.toString(id)));
        Appointment edited = appointmentService.edit(toEdit, updateAppointmentCommand);
        return new ResponseEntity<>(modelMapper.map(edited, AppointmentDto.class), HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity deleteAppointment(@PathVariable int id) {
        appointmentService.deleteById(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/confirm/{token}")
    public ResponseEntity confirmAppointment(@PathVariable String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("ConfirmationToken", token));
        appointmentService.confirmAppointment(confirmationToken);
        return new ResponseEntity("Appointment confirmed", HttpStatus.OK);
    }

    @GetMapping("/cancel/{token}")
    public ResponseEntity cancelAppointment(@PathVariable String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("ConfirmationToken", token));
        appointmentService.cancelAppointment(confirmationToken);
        return new ResponseEntity("Appointment cancelled", HttpStatus.NO_CONTENT);
    }
}
