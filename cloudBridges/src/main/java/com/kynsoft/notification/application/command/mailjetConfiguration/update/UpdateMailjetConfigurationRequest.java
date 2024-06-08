package com.kynsoft.notification.application.command.mailjetConfiguration.update;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMailjetConfigurationRequest {
    private String mailjetApiKey;
    private String mailjetApiSecret;
    private String email;
    private String name;
}
