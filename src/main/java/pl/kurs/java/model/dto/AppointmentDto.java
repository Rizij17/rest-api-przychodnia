package pl.kurs.java.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class AppointmentDto extends RepresentationModel<AppointmentDto> {

    private int id;
    private int doctorId;
    private int patientId;
    private LocalDateTime start;
    private boolean confirmed;
    private int version;

}
