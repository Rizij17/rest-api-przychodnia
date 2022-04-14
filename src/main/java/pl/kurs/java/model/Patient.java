package pl.kurs.java.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(exclude = {"appointments"})
@Entity
@NoArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"email"}, name = "UC_PATIENT_EMAIL")})
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String type;
    private String breed;
    private String ownerName;
    private String ownerSurname;
    private String email;
    @Version
    private int version;

    @OneToMany(mappedBy = "patient", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Appointment> appointments = new HashSet<>();

    @Builder
    public Patient(String name, String type, String breed, String ownerName, String ownerSurname, String email) {
        this.name = name;
        this.type = type;
        this.breed = breed;
        this.ownerName = ownerName;
        this.ownerSurname = ownerSurname;
        this.email = email;
    }

    public boolean hasAppointment(LocalDateTime start) {
        return appointments.stream()
                .map(Appointment::getStart)
                .anyMatch(appointmentStart -> appointmentStart.isAfter(start) && appointmentStart.plusHours(1).isBefore(start));
    }
}
