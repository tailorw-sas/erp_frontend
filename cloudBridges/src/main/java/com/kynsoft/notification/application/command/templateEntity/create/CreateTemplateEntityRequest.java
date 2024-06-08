package com.kynsoft.notification.application.command.templateEntity.create;

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
}
