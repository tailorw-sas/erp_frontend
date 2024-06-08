package com.kynsoft.finamer.insis.application.command.test.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.insis.application.command.test.create.CreateTestRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateTestCommand implements ICommand {

    private UUID id;
    private String userName;

    public UpdateTestCommand(UUID id, String userName) {
        this.id = id;
        this.userName = userName;
    }

    public static UpdateTestCommand fromRequest(CreateTestRequest request, UUID id) {
        return new UpdateTestCommand(
                id, 
                request.getUserName());
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateTestMessage(id);
    }
}
