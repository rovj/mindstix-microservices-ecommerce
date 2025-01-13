package com.techie.microservices.notification.service;

import com.techie.microservices.order.event.OrderPlacedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    @Autowired
    JavaMailSender javaMailSender;
    @KafkaListener(topics = "order-placed", groupId = "notificationService")
    public void listen(@Payload OrderPlacedEvent orderPlacedEvent){
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("rovjrk@gmail.com");
            messageHelper.setTo(orderPlacedEvent.getEmail().toString());
            messageHelper.setSubject(String.format("Your order with order number %s is placed successfully",orderPlacedEvent.getOrderNumber()));
            messageHelper.setText(String.format("""
                    Hi,
                    %s %s
                    
                    Your order with order number %s is placed successfully.
                    
                    Best Regards,
                    Spring Boot Shop
                    """,orderPlacedEvent.getFirstName().toString(),orderPlacedEvent.getLastName().toString(),orderPlacedEvent.getOrderNumber()));

        };
        try{
            javaMailSender.send(messagePreparator);
        }
        catch (Exception e){
            throw new RuntimeException("Something went wrong while sending email to " + orderPlacedEvent.getEmail().toString() + " " + e.getMessage());
        }
    }

}
