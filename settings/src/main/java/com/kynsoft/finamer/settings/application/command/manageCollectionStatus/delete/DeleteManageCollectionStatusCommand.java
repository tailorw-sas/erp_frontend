package com.kynsoft.finamer.settings.application.command.manageCollectionStatus.delete;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class DeleteManageCollectionStatusCommand implements ICommand {

    private final UUID id;

    @Override
    public ICommandMessage getMessage() {
        return new DeleteManageCollectionStatusMessage(id);
    }
}
