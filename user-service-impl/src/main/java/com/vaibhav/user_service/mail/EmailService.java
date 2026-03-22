package com.vaibhav.user_service.mail;

public interface EmailService  {
     String sendEmail(String to, String token);
}
