package pl.kurs.java.model.command;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pl.kurs.java.validation.annotation.OneOfValues;
import pl.kurs.java.validation.annotation.UniqueEmail;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Builder
public class CreatePatientCommand {

    @NotEmpty(message = "NAME_NOT_EMPTY")
    private String name;
    @NotEmpty(message = "TYPE_NOT_EMPTY")
    @OneOfValues(values = {"Pies", "Kot", "Koń", "Krowa"})
    private String type;
    @NotEmpty(message = "BREED_NOT_EMPTY")
    private String breed;
    @NotEmpty(message = "OWNER_NAME_NOT_EMPTY")
    private String ownerName;
    @NotEmpty(message = "OWNER_SURNAME_NOT_EMPTY")
    private String ownerSurname;
    @NotEmpty(message = "EMAIL_NOT_EMPTY")
    @UniqueEmail
    private String email;

}
