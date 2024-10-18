package org.spring.notificationservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    @KafkaListener(topics = "password-change-topic", groupId = "notification-group")
    public void listen(String message)
    {
        String email = extractEmailFromMessage(message);
        sendEmailNotification(email);
    }

    private String extractEmailFromMessage(String message) {
        return message.split(": ")[1];
    }

    private void sendEmailNotification(String email)
    {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password changed successfully");
        message.setText("Your password has been changed successfully");
        mailSender.send(message);
    }
}
