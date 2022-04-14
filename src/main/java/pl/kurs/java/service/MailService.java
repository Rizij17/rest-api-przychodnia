package pl.kurs.java.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.logging.log4j.message.SimpleMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pl.kurs.java.model.NotificationEmail;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Data
@Service
@AllArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendMail(NotificationEmail notificationEmail) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setSubject(notificationEmail.getSubject());
            mimeMessageHelper.setFrom(new InternetAddress(notificationEmail.getSender(), "klinikaKotlet@gmail.com"));
            mimeMessageHelper.setTo(notificationEmail.getReceiver());
            mimeMessageHelper.setText(notificationEmail.getContent());

            mailSender.send(mimeMessageHelper.getMimeMessage());

        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
