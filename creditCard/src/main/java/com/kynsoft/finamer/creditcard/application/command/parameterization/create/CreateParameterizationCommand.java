package com.kynsoft.finamer.creditcard.application.command.parameterization.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateParameterizationCommand implements ICommand {

    private UUID id;
    private String transactionStatusCode;
    private String transactionCategory;
    private String transactionSubCategory;

    public CreateParameterizationCommand(String transactionStatusCode, String transactionCategory, String transactionSubCategory){
        this.id = UUID.randomUUID();
        this.transactionStatusCode = transactionStatusCode;
        this.transactionCategory = transactionCategory;
        this.transactionSubCategory = transactionSubCategory;
    }

    public static CreateParameterizationCommand fromRequest(CreateParameterizationRequest request){
        return new CreateParameterizationCommand(
                request.getTransactionStatusCode(),
                request.getTransactionCategory(),
                request.getTransactionSubCategory()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateParameterizationMessage(id);
    }
}
