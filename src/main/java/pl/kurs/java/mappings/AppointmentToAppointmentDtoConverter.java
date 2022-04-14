package pl.kurs.java.mappings;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Service;
import pl.kurs.java.controller.AppointmentController;
import pl.kurs.java.controller.DoctorController;
import pl.kurs.java.controller.PatientController;
import pl.kurs.java.model.Appointment;
import pl.kurs.java.model.dto.AppointmentDto;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class AppointmentToAppointmentDtoConverter implements Converter<Appointment, AppointmentDto> {

    @Override
    public AppointmentDto convert(MappingContext<Appointment, AppointmentDto> mappingContext) {
        Appointment appointment = mappingContext.getSource();
        AppointmentDto appointmentDto = AppointmentDto.builder()
                .id(appointment.getId())
                .doctorId(appointment.getDoctor().getId())
                .patientId(appointment.getPatient().getId())
                .confirmed(appointment.isConfirmed())
                .start(appointment.getStart())
                .version(appointment.getVersion())
                .build();

        appointmentDto.add(linkTo(methodOn(AppointmentController.class).getSingleAppointment(appointment.getId())).withRel("appointment-details"));
        appointmentDto.add(linkTo(methodOn(DoctorController.class).getSingleDoctor(appointment.getDoctor().getId())).withRel("doctor-details"));
        appointmentDto.add(linkTo(methodOn(PatientController.class).getSinglePatient(appointment.getPatient().getId())).withRel("patient-details"));
        return appointmentDto;
    }
}
