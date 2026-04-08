package edu.arizona.videoshare.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    public void sendVerificationEmail(
            String email,
            String username,
            String token,
            String code
    ) {

        String verificationLink =
                "http://localhost:8080/verify?token=" + token;

        Context context = new Context();
        context.setVariable("username", username);
        context.setVariable("code", code);
        context.setVariable("verificationLink", verificationLink);

        String html = templateEngine.process(
                "auth/verify-email-sent",
                context
        );

        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Verify your VideoShare account");
            helper.setText(html, true);

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    public void sendVerificationSuccessEmail(String email, String username) {

        Context context = new Context();
        context.setVariable("username", username);

        String html = templateEngine.process("auth/verify-success-email", context);

        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Your VideoShare account has been verified");
            helper.setText(html, true);

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send verification success email", e);
        }
    }

    public void sendPasswordResetEmail(String email, String username, String token) {
        String resetLink = "http://localhost:8080/reset-password?token=" + token;

        Context context = new Context();
        context.setVariable("username", username);
        context.setVariable("resetLink", resetLink);

        String html = templateEngine.process("auth/password-reset-email", context);

        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("Reset your VideoShare password");
            helper.setText(html, true);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    public void sendPasswordResetSuccessEmail(String email, String username) {

        Context context = new Context();
        context.setVariable("username", username);

        String html = templateEngine.process("auth/password-reset-success-email", context);

        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Your VideoShare password was changed");
            helper.setText(html, true);

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send password reset success email", e);
        }
    }
}