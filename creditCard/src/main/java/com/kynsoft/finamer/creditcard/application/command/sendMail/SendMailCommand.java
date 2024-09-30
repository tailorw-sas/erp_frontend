package com.kynsoft.finamer.creditcard.application.command.sendMail;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class SendMailCommand implements ICommand {


    private boolean result;
    private UUID transactionUuid;

    public SendMailCommand(UUID transactionUuid) {
       this.transactionUuid = transactionUuid;
    }

    public static SendMailCommand fromRequest(SendMailRequest request) {
        return new SendMailCommand(request.getTransactionUuid());
    }

    @Override
    public ICommandMessage getMessage() {
        return new SendMailMessage(result);
    }
}