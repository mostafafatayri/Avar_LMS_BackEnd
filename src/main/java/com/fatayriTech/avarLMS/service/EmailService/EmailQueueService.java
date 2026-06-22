package com.fatayriTech.avarLMS.service.EmailService;

import com.fatayriTech.avarLMS.model.EmailQueue;
import com.fatayriTech.avarLMS.repository.SendingEmails.EmailQueueRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailQueueService {

    private final JavaMailSender mailSender;
    private final EmailQueueRepository emailRepo;

    @Transactional
    @Scheduled(fixedDelay = 10000)
    public void processPendingEmails() {
        List<EmailQueue> emails = emailRepo.findTop10ByStatusOrderByIdAsc(0);

        for (EmailQueue email : emails) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom("mostafafatayri@gmail.com");
                message.setTo(email.getToEmail());
                message.setSubject(email.getSubject());
                message.setText(email.getBody());

                mailSender.send(message);

                email.setStatus(1);
                email.setLastAttemptAt(LocalDateTime.now());
                email.setErrorMessage(null);
                emailRepo.save(email);

                System.out.println("Email sent successfully to: " + email.getToEmail());

            } catch (Exception e) {
                email.setRetryCount(email.getRetryCount() == null ? 1 : email.getRetryCount() + 1);
                email.setErrorMessage(e.getMessage());
                email.setLastAttemptAt(LocalDateTime.now());

                if (email.getRetryCount() >= 3) {
                    email.setStatus(2);
                } else {
                    email.setStatus(0);
                }

                emailRepo.save(email);

                System.out.println("Email sending failed to: " + email.getToEmail());
                e.printStackTrace();
            }
        }
    }

    public void queueEmail(String to, String subject, String body) {
        EmailQueue email = new EmailQueue();
        email.setToEmail(to);
        email.setSubject(subject);
        email.setBody(body);
        email.setStatus(0);
        email.setRetryCount(0);
        emailRepo.save(email);
    }
}