package com.kynsoft.notification.infrastructure.service;

import com.kynsoft.notification.domain.dto.EmailRequest;
import com.kynsoft.notification.domain.dto.MailJetAttachment;
import com.kynsoft.notification.domain.dto.MailJetRecipient;
import com.kynsoft.notification.domain.dto.MailJetVar;
import com.kynsoft.notification.domain.service.IEmailService;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.resource.Email;
import org.springframework.stereotype.Service;

//@Profile("mailjet")
@Service
public class EmailServiceMailjet implements IEmailService {

//    @Value("${mailjet.apiKey}")
//    private String mailjetApiKey;
//
//    @Value("${mailjet.apiSecret}")
//    private String mailjetApiSecret;
//
//    @Value("${mailjet.fromEmail}")
//    private String fromEmail;
//
//    @Value("${mailjet.fromName}")
//    private String fromName;

    //private final MailjetClient client;

    public EmailServiceMailjet() {

    }

//    @Override
//    public boolean sendMail(String toEmail, String subject, String message) {
//        try {
//
//            MailjetClient client1 = new MailjetClient(mailjetApiKey, mailjetApiSecret);
//            MailjetRequest request = new MailjetRequest(Email.resource)
//                    .property(Email.FROMEMAIL, fromEmail)
//                    .property(Email.FROMNAME, fromName)
//                    .property(Email.SUBJECT, subject)
//                    .property(Email.TEXTPART, subject)
//                    .property(Email.HTMLPART, "<h3>Dear passenger, welcome to <a href=\"https://www.mailjet.com/\">Mailjet</a>!<br />May the delivery force be with you!")
//                    .property(Email.RECIPIENTS, new JSONArray()
//                            .put(new JSONObject()
//                                    .put("Email", toEmail)));
//            MailjetResponse  response = client1.post(request);
//            System.out.println(response.getStatus());
//            System.out.println(response.getData());
//
//            return response.getStatus() == 200;
//        } catch (MailjetException e) {
//            return false;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public boolean sendMailHtml(String toEmail, String subject, String message) {
//        // Similar a sendMail, pero asegúrate de que el HTML esté bien formado
//        return sendMail(toEmail, subject, message); // Simplificado para el ejemplo
//    }
//
//    @Override
//    public boolean sendMessageWithAttachment(String toEmail, String subject, String text, MultipartFile file) {
//        // Implementación con archivo adjunto usando la API de Mailjet
//        // Mailjet API v3.1 no soporta directamente MultipartFile; necesitarás convertirlo.
//        return false; // Simplificado para el ejemplo
//    }
//
//    @Override
//    public boolean sendMessageWithAttachmentArray(String toEmail, String subject, String text, MultipartFile[] file) {
//        return false;
//    }

    @Override
    public boolean sendEmailMailjet(EmailRequest emailRequest, String mailjetApiKey,String mailjetApiSecret,
                                    String fromEmail, String fromName) {
       try {
           MailjetClient client;
           MailjetRequest request;
           MailjetResponse response;

           client = new MailjetClient(mailjetApiKey, mailjetApiSecret);
           request = new MailjetRequest(Email.resource)
                   .property(Email.FROMEMAIL, fromEmail)
                   .property(Email.FROMNAME, fromName)
                   .property(Email.MJTEMPLATEID, emailRequest.getTemplateId())
                   .property(Email.MJTEMPLATELANGUAGE, true)
                   .property(Email.SUBJECT, emailRequest.getSubject())
                   .property(Email.RECIPIENTS, MailJetRecipient.createRecipientsJsonArray(emailRequest.getRecipientEmail()))
                   .property(Email.ATTACHMENTS, MailJetAttachment.generateAttachments(emailRequest.getMailJetAttachments()))
                  .property(Email.VARS, MailJetVar.createVarsJsonObject(emailRequest.getMailJetVars()))
                   .property(Email.MJEVENTPAYLOAD, "Eticket,1234,row,15,seat,B");
           response = client.post(request);
           return response.getStatus() == 200;
       }catch (Exception ex){
           String e= ex.getMessage();
           return false;
       }
    }

}
