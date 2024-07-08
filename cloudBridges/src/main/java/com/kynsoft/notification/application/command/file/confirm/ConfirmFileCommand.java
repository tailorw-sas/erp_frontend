package com.kynsoft.notification.application.command.file.confirm;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ConfirmFileCommand implements ICommand {
    private final List<UUID> ids;
    private  Boolean response;

    public ConfirmFileCommand(List<UUID> ids) {
        this.ids = ids;
    }

    public static ConfirmFileCommand fromRequest(ConfirmFileRequest request) {
        return new ConfirmFileCommand(request.getIds());
    }

    @Override
    public ICommandMessage getMessage() {
        return new ConfirmFileMessage(response);
    }
}
