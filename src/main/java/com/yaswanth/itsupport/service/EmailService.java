package com.yaswanth.itsupport.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(
            String toEmail,
            String username,
            String resetLink) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);
        message.setSubject("AI IT Support - Password Reset");

        message.setText(
                "Hello " + username + ",\n\n"
                + "We received a request to reset your AI IT Support password.\n\n"
                + "Click the link below to reset your password:\n\n"
                + resetLink + "\n\n"
                + "This link will expire in 15 minutes.\n\n"
                + "If you did not request a password reset, you can safely ignore this email.\n\n"
                + "Regards,\n"
                + "AI IT Support Team"
        );

        mailSender.send(message);
    }
}