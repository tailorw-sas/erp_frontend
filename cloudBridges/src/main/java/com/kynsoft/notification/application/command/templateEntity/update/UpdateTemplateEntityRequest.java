package com.kynsoft.notification.application.command.templateEntity.update;

import com.kynsoft.notification.domain.dto.EMailjetType;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateTemplateEntityRequest {

    private String Id;
    private String templateCode;
    private String name;
    private String description;
    private UUID MailjetConfigId;
    private String languageCode;
    private EMailjetType type;
}
