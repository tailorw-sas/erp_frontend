package com.kynsoft.notification.domain.dto;

import lombok.Getter;

@Getter
public enum MailjetTemplateEnum {

    OTP(5964805),
    WELCOM(5965446),
    EMAIL_CONFIRMATION_CITE(22222);


    private final int templateId;

    MailjetTemplateEnum(int templateId) {
        this.templateId = templateId;
    }
}
