package pl.kurs.java.model.command;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


import javax.validation.constraints.Future;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class  CreateAppointmentCommand {

    @NotEmpty(message = "DATE_NOT_EMPTY")
    @Future
    private LocalDateTime start;
    @NotNull(message = "DOCTOR_ID_NOT_NULL")
    private Integer doctorId;
    @NotNull(message = "DOCTOR_ID_NOT_NULL")
    private Integer patientId;

}
