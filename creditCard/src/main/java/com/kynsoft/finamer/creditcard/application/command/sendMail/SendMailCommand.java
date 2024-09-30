package com.kynsoft.finamer.creditcard.application.command.sendMail;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class SendMailCommand implements ICommand {


    private boolean result;
    private String token;

    public SendMailCommand(String token) {
       this.token = token;
    }

    public static SendMailCommand fromRequest(SendMailRequest request) {
        return new SendMailCommand(request.getToken());
    }

    @Override
    public ICommandMessage getMessage() {
        return new SendMailMessage(result);
    }
}