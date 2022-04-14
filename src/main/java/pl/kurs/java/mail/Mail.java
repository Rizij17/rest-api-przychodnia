package pl.kurs.java.mail;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.kurs.java.model.Appointment;
import pl.kurs.java.model.dto.AppointmentDto;
import pl.kurs.java.service.AppointmentService;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Mail {

    private final JavaMailSender javaMailSender;
    private final AppointmentService appointmentService;
    private final ModelMapper modelMapper;

    @Scheduled(cron = "0 00 23 1/1 * ?")
    public void sendAppointmentMessage() {
        LocalDateTime from = LocalDate.now().atTime(LocalTime.MAX);
        LocalDateTime nextDay = LocalDate.now().plusDays(1).atTime(LocalTime.MAX);
        List<Appointment> appointments = appointmentService.findAllByDateBetween(from, nextDay);

        String subject = "Wizyta w Klinice Kotleta";
        String receiver, text;
        AppointmentDto appointmentDto;

        for (Appointment a : appointments){
            appointmentDto = modelMapper.map(a, AppointmentDto.class);
            receiver = a.getPatient().getEmail();
            text = "Masz nadchodzącą wizytę: " + appointmentDto.toString();
            sendEmail(receiver, subject, text);
        }
    }

    public void sendEmail(String receiver, String subject, String text) {

        try{
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setTo(receiver);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(text, true);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException exc){
        }


    }
}
