package com.kynsoft.finamer.invoicing.application.command.parameterization.delete;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class DeleteParameterizationCommand implements ICommand {

    private final UUID id;

    @Override
    public ICommandMessage getMessage() {
        return new DeleteParameterizationMessage(id);
    }
}
