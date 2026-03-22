package com.vaibhav.user_service.mail;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService{
    /**
     * @param to
     * @param subject
     * @param body
     * @return
     */
    private final JavaMailSender javaMailSender;
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);
    @Override
    public String sendEmail(String to, String token) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
            String resetLink =
                    "http://localhost:3000/reset-password?token=" + token;
            messageHelper.setTo(to);
            messageHelper.setSubject("Password Reset Email");
            messageHelper.setText(
                    "Click below to reset password:\n" + resetLink +
                            "\n\nThis link expires in 15 minutes."
            );

            javaMailSender.send(message);

            return "Email Sent successfully to :" + to;
        } catch (Exception e) {
            LOGGER.error("Failed to send email to: {}", to, e);
            return "Failed to send email to: " + to;
        }
    }
}
