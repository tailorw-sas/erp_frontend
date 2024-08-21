package com.kynsoft.finamer.payment.application.command.replicate.delete.objects;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteReplicateCommand implements ICommand {

    private List<DeleteObjectEnum> objects;

    public DeleteReplicateCommand(List<DeleteObjectEnum> objects) {
        this.objects = objects;
    }

    public static DeleteReplicateCommand fromRequest(DeleteReplicateRequest request) {
        return new DeleteReplicateCommand(
                request.getObjects()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new DeleteReplicateMessage();
    }
}
