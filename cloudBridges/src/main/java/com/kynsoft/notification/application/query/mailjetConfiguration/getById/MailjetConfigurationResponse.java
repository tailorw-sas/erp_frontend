package com.kynsoft.notification.application.query.mailjetConfiguration.getById;


import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.notification.domain.dto.MailjetConfigurationDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class MailjetConfigurationResponse implements IResponse {
    private UUID id;
    private String mailjetApiKey;
    private String mailjetApiSecret;
    private String email;
    private String name;
    private LocalDate createdAt;

    public MailjetConfigurationResponse(MailjetConfigurationDto mailjetConfigurationDto) {
        this.id = mailjetConfigurationDto.getId();
        mailjetApiKey = "*******";
        mailjetApiSecret = "*******";
        email = mailjetConfigurationDto.getFromEmail();
        name = mailjetConfigurationDto.getFromName();
        createdAt = mailjetConfigurationDto.getCreatedAt().toLocalDate();
    }

}