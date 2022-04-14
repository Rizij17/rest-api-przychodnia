package pl.kurs.java.mappings;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Service;
import pl.kurs.java.controller.PatientController;
import pl.kurs.java.model.Patient;
import pl.kurs.java.model.dto.PatientDto;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class PatientToPatientDtoConverter implements Converter<Patient, PatientDto> {

    @Override
    public PatientDto convert(MappingContext<Patient, PatientDto> mappingContext) {
        Patient patient = mappingContext.getSource();
        PatientDto patientDto = PatientDto.builder()
                .name(patient.getName())
                .type(patient.getType())
                .breed(patient.getBreed())
                .ownerName(patient.getOwnerName())
                .ownerSurname(patient.getOwnerSurname())
                .email(patient.getEmail())
                .version(patient.getVersion())
                .build();

        patientDto.add(linkTo(methodOn(PatientController.class).getSinglePatient(patient.getId())).withRel("patient-details"));
        patientDto.add(linkTo(methodOn(PatientController.class).getPatientAppointments(patient.getId())).withRel("patient-appointments"));
        return patientDto;
    }
}
