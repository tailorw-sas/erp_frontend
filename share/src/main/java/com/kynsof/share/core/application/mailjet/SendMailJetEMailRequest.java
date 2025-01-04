package com.kynsof.share.core.application.mailjet;


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
