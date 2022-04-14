package pl.kurs.java.model.command;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class UpdateAppointmentCommand {
    @NotNull(message = "DATE_NOT_EMPTY")
    private LocalDateTime start;
    @NotNull(message = "VERSION_NOT_NULL")
    private Integer version;
}
