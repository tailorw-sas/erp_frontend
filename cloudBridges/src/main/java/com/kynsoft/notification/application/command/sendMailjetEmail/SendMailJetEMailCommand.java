package com.kynsoft.notification.application.command.sendMailjetEmail;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.notification.domain.dto.MailJetAttachment;
import com.kynsoft.notification.domain.dto.MailJetRecipient;
import com.kynsoft.notification.domain.dto.MailJetVar;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SendMailJetEMailCommand implements ICommand {
    private boolean result;
    private  final List<MailJetRecipient> recipientEmail;
    private  final List<MailJetVar> mailJetVars;
    private  final List<MailJetAttachment> mailJetAttachments;
    private  final String subject;
    private  final int templateId;


    public SendMailJetEMailCommand(List<MailJetRecipient> recipientEmail, List<MailJetVar> mailJetVars,
                                   List<MailJetAttachment> mailJetAttachments, String subject, int templateId) {


        this.recipientEmail = recipientEmail;
        this.mailJetVars = mailJetVars;
        this.mailJetAttachments = mailJetAttachments;
        this.subject = subject;
        this.templateId = templateId;
    }

    public static SendMailJetEMailCommand fromRequest(SendMailJetEMailRequest request) {
        return new SendMailJetEMailCommand(request.getRecipientEmail(), request.getMailJetVars(),
                request.getMailJetAttachments(), request.getSubject(), request.getTemplateId());
    }


    @Override
    public ICommandMessage getMessage() {
        return new SendMailjetEmailMessage(result);
    }
}
