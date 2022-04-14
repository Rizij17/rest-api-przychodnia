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
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"nip"}, name = "UC_DOCTOR_NIP")})
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BigInteger")
    private int id;
    private String name;
    private String surname;
    private String type;
    private String animalType;
    private double rate;
    private String nip;
    private boolean fired;
    @Version
    private int version;

    @OneToMany(mappedBy = "doctor", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Appointment> appointments = new HashSet<>();

    @Builder
    public Doctor(String name, String surname, String type, String animalType, double rate, String nip, boolean fired) {
        this.name = name;
        this.surname = surname;
        this.type = type;
        this.animalType = animalType;
        this.rate = rate;
        this.nip = nip;
        this.fired = fired;
    }

    public boolean isTaken(LocalDateTime start){
        return appointments.stream()
                .map(Appointment::getStart)
                .anyMatch(appointmentStart -> appointmentStart.isAfter(start) && appointmentStart.plusHours(1).isBefore(start));
    }
}
