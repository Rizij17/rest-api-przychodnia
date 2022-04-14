package pl.kurs.java.model.command;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pl.kurs.java.validation.annotation.OneOfValues;
import pl.kurs.java.validation.annotation.UniqueNip;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
public class UpdatedDoctorCommand {
    @NotEmpty(message = "NAME_NOT_EMPTY")
    private String name;
    @NotEmpty(message = "SURNAME_NOT_EMPTY")
    private String surname;
    @NotEmpty(message = "TYPE_NOT_EMPTY")
    @OneOfValues(values = {"Kardiolog", "Laryngolog", "Endokrynolog"})
    private String type;
    @NotEmpty(message = "ANIMAL_TYPE_NOT_EMPTY")
    @OneOfValues(values = {"Pies", "Kot", "Koń", "Krowa"})
    private String animalType;
    @Min(value = 0, message = "RATE_NOT_NEGATIVE")
    private double rate;
    @NotEmpty(message = "NIP_NOT_EMPTY")
    @UniqueNip
    private String nip;
    @NotNull
    private Integer version;
}
