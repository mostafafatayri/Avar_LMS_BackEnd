package com.fatayriTech.avarLMS.repository.SendingEmails;

import com.fatayriTech.avarLMS.model.EmailQueue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailQueueRepository extends JpaRepository<EmailQueue, Long> {
    List<EmailQueue> findTop10ByStatusOrderByIdAsc(int status);
}

