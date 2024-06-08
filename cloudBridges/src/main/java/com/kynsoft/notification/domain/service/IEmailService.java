package com.kynsoft.notification.domain.service;

import com.kynsoft.notification.domain.dto.EmailRequest;
import com.mailjet.client.errors.MailjetException;

public interface IEmailService {
//    public boolean sendMail(String toEmail, String subject, String message);
//    public boolean sendMailHtml(String toEmail, String subject, String message);
//    public boolean sendMessageWithAttachment(String toEmail, String subject, String text, MultipartFile file);
//    public boolean sendMessageWithAttachmentArray(String toEmail, String subject, String text, MultipartFile [] file);
    boolean sendEmailMailjet(EmailRequest emailRequest, String mailjetApiKey,String mailjetApiSecret,
                             String fromEmail, String fromName) throws  MailjetException;
}

