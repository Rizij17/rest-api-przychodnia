package pl.kurs.java.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
@Builder
public class PatientDto extends RepresentationModel<PatientDto> {

    private int id;
    private String name;
    private String type;
    private String breed;
    private String ownerName;
    private String ownerSurname;
    private String email;
    private int version;
}
