package pl.kurs.java.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.java.error.AlreadyTakenException;
import pl.kurs.java.error.EntityNotFoundException;
import pl.kurs.java.error.InvalidDateException;
import pl.kurs.java.model.*;
import pl.kurs.java.model.command.CreateAppointmentCommand;
import pl.kurs.java.model.command.UpdateAppointmentCommand;
import pl.kurs.java.repository.AppointmentRepository;
import pl.kurs.java.repository.ConfirmationTokenRepository;
import pl.kurs.java.repository.DoctorRepository;
import pl.kurs.java.repository.PatientRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final MailService mailService;

    @Transactional(readOnly = true)
    public Optional<Appointment> findById(int id) {
        return appointmentRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Page<Appointment> findAll(Pageable pageable) {
        return appointmentRepository.findAll(pageable);
    }

    @Transactional
    public Appointment save(Appointment appointment) {
        Appointment toSave = appointmentRepository.saveAndFlush(appointment);
        ConfirmationToken appointmentToken = generateVerificationToken(appointment);

        return toSave;
    }

    @Transactional
    public Appointment edit(Appointment toEdit, UpdateAppointmentCommand updateAppointmentCommand) {
        toEdit.setStart(updateAppointmentCommand.getStart());
        toEdit.setVersion(updateAppointmentCommand.getVersion());
        return appointmentRepository.save(toEdit);
    }

    @Transactional
    public void deleteById(int id) {
        appointmentRepository.findById(id).ifPresent(appointmentRepository::delete);
    }

    @Transactional
    public Appointment confirmAppointment(ConfirmationToken confirmationToken) {
        Appointment appointment = confirmationToken.getAppointment();
        if (appointment.getStart().isBefore(LocalDateTime.now())) {
            throw new InvalidDateException("Appointment", "datetime");
        }
        appointment.setConfirmed(true);
        return appointmentRepository.saveAndFlush(appointment);
    }

    @Transactional
    public void cancelAppointment(ConfirmationToken confirmationToken) {
        int appointmentId = confirmationToken.getAppointment().getId();
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment", String.valueOf(appointmentId)));
        appointmentRepository.delete(appointment);
    }

    public boolean existsByDoctorAndDateBetween(LocalDateTime from, LocalDateTime to, Doctor doctor){
        return appointmentRepository.findAll().stream().anyMatch(appointment -> appointment.getDoctor().equals(doctor) && appointment.getStart().isAfter(from) && appointment.getStart().isBefore(to));
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Appointment createAppointment(CreateAppointmentCommand createAppointmentCommand) {
        Doctor doctor = doctorRepository.findById(createAppointmentCommand.getDoctorId())
                .orElseThrow(() -> new EntityNotFoundException("Doctor", Integer.toString(createAppointmentCommand.getDoctorId())));

//        if (appointmentRepository.existByDoctorAndDateBetween(createAppointmentCommand.getStart(), createAppointmentCommand.getStart().plusHours(1), doctor)) {
//            throw new AlreadyTakenException();
//        }

        Patient patient = patientRepository.findById(createAppointmentCommand.getPatientId())
                .orElseThrow(() -> new EntityNotFoundException("Patient", Integer.toString(createAppointmentCommand.getPatientId())));

        Appointment appointment = appointmentRepository.saveAndFlush(Appointment.builder()
                .doctor(doctor)
                .patient(patient)
                .start(createAppointmentCommand.getStart())
                .build());
        return appointment;
    }

    @Transactional
    public ConfirmationToken generateVerificationToken(Appointment appointment) {
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setToken(token);
        confirmationToken.setAppointment(appointment);
        return confirmationTokenRepository.saveAndFlush(confirmationToken);
    }

    @Scheduled(cron = "00 00 23 * * *")
    public void nextDayVisitNotification() {
        List<Appointment> tomorrowAppointments = appointmentRepository.findAll().stream().filter(a -> a.getStart().toLocalDate().equals(LocalDate.now().plusDays(1))).toList();
        for (Appointment tomorrowAppointment : tomorrowAppointments) {
            mailService.sendMail(new NotificationEmail("kliikaKotlet@gmail.com", "Appointment reminder", tomorrowAppointment.getPatient().getEmail(), "Just a friendly reminder about your visit planned for tomorrow at: " + tomorrowAppointment.getStart().toLocalTime()));
        }
    }

}
