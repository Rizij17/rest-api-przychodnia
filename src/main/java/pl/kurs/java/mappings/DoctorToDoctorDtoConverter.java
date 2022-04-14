package pl.kurs.java.mappings;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Service;
import pl.kurs.java.controller.DoctorController;
import pl.kurs.java.model.Doctor;
import pl.kurs.java.model.dto.DoctorDto;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class DoctorToDoctorDtoConverter implements Converter<Doctor, DoctorDto> {

    @Override
    public DoctorDto convert(MappingContext<Doctor, DoctorDto> mappingContext) {
        Doctor doctor = mappingContext.getSource();
        DoctorDto doctorDto = DoctorDto.builder()
                .id(doctor.getId())
                .name(doctor.getName())
                .surname(doctor.getSurname())
                .type(doctor.getType())
                .animalType(doctor.getAnimalType())
                .rate(doctor.getRate())
                .nip(doctor.getNip())
                .fired(doctor.isFired())
                .version(doctor.getVersion())
                .build();

        doctorDto.add(linkTo(methodOn(DoctorController.class).getSingleDoctor(doctor.getId())).withRel("doctor-details"));
        doctorDto.add(linkTo(methodOn(DoctorController.class).getDoctorAppointments(doctor.getId())).withRel("doctor-appointments"));
        return doctorDto;
    }
}
