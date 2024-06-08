package com.kynsoft.finamer.insis.application.command.test.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.insis.application.command.test.create.CreateTestMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateTestCommand implements ICommand {

    private UUID id;
    private String userName;

    public CreateTestCommand(String userName) {
        this.id = UUID.randomUUID();
        this.userName = userName;
    }

    public static CreateTestCommand fromRequest(CreateTestRequest request) {
        return new CreateTestCommand(
                request.getUserName()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateTestMessage(id);
    }
}
