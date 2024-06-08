package com.kynsoft.finamer.settings.application.command.managerMessage.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class UpdateManagerMessageCommand implements ICommand {

    private UUID id;
    private String description;
    private Status status;
    private String name;
    private String type;
    private UUID language;

    public static UpdateManagerMessageCommand fromRequest(UpdateManagerMessageRequest request, UUID id){
        return new UpdateManagerMessageCommand(
                id,
                request.getDescription(),
                request.getStatus(),
                request.getName(),
                request.getType(),
                request.getLanguage()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManagerMessageMessage(id);
    }
}
