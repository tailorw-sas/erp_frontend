package com.kynsoft.finamer.invoicing.application.command.replicate.object;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateReplicateCommand implements ICommand {

    private List<ObjectEnum> objects;

    public CreateReplicateCommand(List<ObjectEnum> objects) {
        this.objects = objects;
    }

    public static CreateReplicateCommand fromRequest(CreateReplicateRequest request) {
        return new CreateReplicateCommand(
                request.getObjects()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateReplicateMessage();
    }
}
