package com.kynsoft.finamer.invoicing.application.command.manageInvoice.send;

import com.kynsof.share.core.application.mailjet.*;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.services.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@Transactional
public class SendInvoiceCommandHandler implements ICommandHandler<SendInvoiceCommand> {

    private final IManageInvoiceService service;
    private final MailService mailService;

    public SendInvoiceCommandHandler(IManageInvoiceService service, MailService mailService) {
        this.service = service;
        this.mailService = mailService;
    }

    @Override
    @Transactional
    public void handle(SendInvoiceCommand command) {

        ManageInvoiceDto invoice = this.service.findById(command.getInvoice());
        //TODO capturar la forma de envio de la agencia el campo mailinAddress
        //TODO replicar el b2bPartner de la agencia para obtener el método de envio


        //Send Email
        SendMailJetEMailRequest request = new SendMailJetEMailRequest();
        request.setSubject("INVOICE " + invoice.getInvoiceNumber());
        request.setTemplateId(5965446);//Cambiar en configuración

        List<MailJetVar> vars = Arrays.asList(
                new MailJetVar("username", "Niurka"),
                new MailJetVar("otp_token", "5826384")
        );
        request.setMailJetVars(vars);

        MailJetRecipient recipient = new MailJetRecipient("keimermo1989@gmail.com", "Keimer Montes");
        List<MailJetRecipient> recipients = new ArrayList<>();
        recipients.add(recipient);
        request.setRecipientEmail(recipients);

        var nameFile = invoice.getInvoiceNumber() + ".pdf";
        MailJetAttachment attachment = new MailJetAttachment("application/pdf", nameFile, "base64");
        List<MailJetAttachment> attachments = new ArrayList<>();
        attachments.add(attachment);
        request.setMailJetAttachments(attachments);
        try {
            mailService.sendMail(request);
            command.setResult(true);
        } catch (Exception e) {
            command.setResult(false);
        }
    }

}
