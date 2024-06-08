package com.kynsoft.notification.application.query.templateEntity.getById;


import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.notification.application.query.mailjetConfiguration.getById.MailjetConfigurationResponse;
import com.kynsoft.notification.domain.dto.TemplateDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class TemplateEntityResponse implements IResponse {
    private UUID id;
    private String templateCode;
    private String name;
    private String description;
    private LocalDate createdAt;
    private MailjetConfigurationResponse MailjetConfigId;
    public TemplateEntityResponse(TemplateDto dto) {
        this.id = dto.getId();
        this.templateCode = dto.getTemplateCode();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.createdAt = dto.getCreatedAt().toLocalDate();
        this.setMailjetConfigId(new MailjetConfigurationResponse(dto.getMailjetConfigurationDto()));
    }

}