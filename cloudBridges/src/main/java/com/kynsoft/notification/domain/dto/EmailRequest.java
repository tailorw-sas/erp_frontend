package com.kynsoft.notification.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
public class EmailRequest {
    private List<MailJetRecipient> recipientEmail;
    private List<MailJetVar> mailJetVars;
    private List<MailJetAttachment> mailJetAttachments;
    private String subject;
    private int templateId;
}
