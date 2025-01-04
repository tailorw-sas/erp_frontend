package com.kynsoft.notification.application.command.templateEntity.create;

import com.kynsof.share.core.domain.EMailjetType;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateTemplateEntityRequest {

    private String templateCode;
    private String name;
    private String description;
    private UUID MailjetConfigId;
    private String languageCode;
    private EMailjetType type;
}
