package com.kynsoft.notification.application.command.sendMailjetEmail;

import com.kynsoft.notification.domain.dto.MailJetAttachment;
import com.kynsoft.notification.domain.dto.MailJetRecipient;
import com.kynsoft.notification.domain.dto.MailJetVar;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SendMailJetEMailRequest {

    private List<MailJetRecipient> recipientEmail;
    private List<MailJetVar> mailJetVars;
    private List<MailJetAttachment> mailJetAttachments;
    private String subject;
    private int templateId;
}
