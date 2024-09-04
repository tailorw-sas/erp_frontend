package com.kynsoft.finamer.invoicing.application.command.parameterization.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateParameterizationCommand implements ICommand {

    private UUID id;
    private String sent;
    private String reconciled;
    private String processed;
    private String canceled;
    private String pending;

    public CreateParameterizationCommand(String sent, String reconciled, String processed, String canceled, String pending) {
        this.id = UUID.randomUUID();
        this.sent = sent;
        this.reconciled = reconciled;
        this.processed = processed;
        this.canceled = canceled;
        this.pending = pending;
    }

    public static CreateParameterizationCommand fromRequest(CreateParameterizationRequest request){
        return new CreateParameterizationCommand(
                request.getSent(), request.getReconciled(), request.getProcessed(), request.getCanceled(), request.getPending()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateParameterizationMessage(id);
    }
}
