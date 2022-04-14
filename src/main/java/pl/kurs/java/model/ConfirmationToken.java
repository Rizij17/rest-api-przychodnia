package pl.kurs.java.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String token;
    @OneToOne
    private Appointment appointment;

    public ConfirmationToken(String token, Appointment appointment) {
        this.token = token;
        this.appointment = appointment;
    }
}
