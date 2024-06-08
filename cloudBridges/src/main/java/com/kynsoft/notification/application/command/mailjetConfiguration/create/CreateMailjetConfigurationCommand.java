package com.kynsoft.notification.application.command.mailjetConfiguration.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateMailjetConfigurationCommand implements ICommand {
    private UUID id;
    private final String mailjetApiKey;
    private final String mailjetApiSecret;
    private final String fromEmail;
    private final String fromName;


    public CreateMailjetConfigurationCommand(String mailjetApiKey, String mailjetApiSecret, String fromEmail, String fromName) {

        this.mailjetApiKey = mailjetApiKey;
        this.mailjetApiSecret = mailjetApiSecret;
        this.fromEmail = fromEmail;
        this.fromName = fromName;
    }

    public static CreateMailjetConfigurationCommand fromRequest(CreateMailjetConfigurationRequest request) {
        return new CreateMailjetConfigurationCommand(request.getMailjetApiKey(), request.getMailjetApiSecret(),
                request.getEmail(), request.getName());
    }


    @Override
    public ICommandMessage getMessage() {
        return new CreateMailjetConfigurationMessage(id);
    }
}
