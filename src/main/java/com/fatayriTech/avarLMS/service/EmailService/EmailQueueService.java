package com.fatayriTech.avarLMS.service.EmailService;
import com.fatayriTech.avarLMS.model.EmailQueue; //  .model.EmailQueue;
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
    @Scheduled(fixedDelay = 1000000000) // every 10 seconds 10000
    public void processPendingEmails() {
        List<EmailQueue> emails = emailRepo.findTop10ByStatusOrderByIdAsc(0); // status 0 = pending

        for (EmailQueue email : emails) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(email.getToEmail());
                message.setSubject(email.getSubject());
                message.setText(email.getBody());

                mailSender.send(message);

                email.setStatus(1);
                email.setLastAttemptAt(LocalDateTime.now());
                emailRepo.save(email);

            } catch (Exception e) {
                email.setRetryCount(email.getRetryCount() + 1);
                ///email.setErrorMessage("the mail was not s");
                email.setLastAttemptAt(LocalDateTime.now());

                if (email.getRetryCount() >= 3) {
                    email.setStatus(2); // Poisoned
                } else {
                    email.setStatus(0); // Still pending, retry later
                }

                System.out.println("the email was not sent, check the logs");
                emailRepo.save(email);
            }
        }
    }



    public void queueEmail(String to, String subject, String body) {
        EmailQueue email = new EmailQueue();
        email.setToEmail(to);
        email.setSubject(subject);
        email.setBody(body);
        email.setStatus(0); // pending
        emailRepo.save(email);
    }
}
