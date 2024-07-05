package com.kynsoft.finamer.creditcard.application.command.parameterization.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.creditcard.application.command.parameterization.delete.DeleteParameterizationMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateParameterizationCommand implements ICommand {

    private UUID id;
    private String transactionStatusCode;

    public CreateParameterizationCommand(String transactionStatusCode){
        this.id = UUID.randomUUID();
        this.transactionStatusCode = transactionStatusCode;
    }

    public static CreateParameterizationCommand fromRequest(CreateParameterizationRequest request){
        return new CreateParameterizationCommand(
                request.getTransactionStatusCode()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateParameterizationMessage(id);
    }
}
