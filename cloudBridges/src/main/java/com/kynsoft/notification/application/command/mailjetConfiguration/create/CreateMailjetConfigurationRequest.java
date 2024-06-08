package com.kynsoft.notification.application.command.mailjetConfiguration.create;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMailjetConfigurationRequest {

    private String mailjetApiKey;
    private String mailjetApiSecret;
    private String email;
    private String name;
}
