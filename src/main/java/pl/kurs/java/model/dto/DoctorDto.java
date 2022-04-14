package pl.kurs.java.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
@Builder
public class DoctorDto extends RepresentationModel<DoctorDto> {

    private int id;
    private String name;
    private String surname;
    private String type;
    private String animalType;
    private double rate;
    private String nip;
    private int version;
    private boolean fired;
}
