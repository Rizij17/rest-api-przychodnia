package pl.kurs.java.model.command;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pl.kurs.java.validation.annotation.UniqueEmail;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
public class UpdatePatientCommand {

    @NotEmpty(message = "NAME_NOT_EMPTY")
    private String name;
    @NotEmpty(message = "OWNER_NAME_NOT_EMPTY")
    private String ownerName;
    @NotEmpty(message = "OWNER_SURNAME_NOT_EMPTY")
    private String ownerSurname;
    @NotEmpty(message = "EMAIL_NOT_EMPTY")
    @UniqueEmail
    private String email;
    @NotNull
    private Integer version;
}
