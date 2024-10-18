package org.spring.userservice;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordChangeProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendPasswordChangeNotification(String email) {
        String message = String.format("Password changed for user: %s", email);
        kafkaTemplate.send("password-change-topic", message);
    }
}
