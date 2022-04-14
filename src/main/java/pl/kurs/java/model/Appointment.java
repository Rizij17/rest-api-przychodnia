package pl.kurs.java.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kurs.java.error.AlreadyTakenException;

import javax.persistence.*;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@Entity
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private LocalDateTime start;
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "patient_id")
    private Patient patient;
    private boolean confirmed;
    @Version
    private int version;

    @Builder
    public Appointment(LocalDateTime start, Doctor doctor, Patient patient) {
        if(doctor.isTaken(start) || patient.hasAppointment(start)){
            throw new AlreadyTakenException();
        }
        this.start = start;
        this.doctor = doctor;
        this.patient = patient;
        doctor.getAppointments().add(this);
        patient.getAppointments().add(this);
    }
}
