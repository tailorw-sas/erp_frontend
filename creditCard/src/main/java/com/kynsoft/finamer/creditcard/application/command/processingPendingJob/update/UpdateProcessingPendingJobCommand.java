package com.kynsoft.finamer.creditcard.application.command.processingPendingJob.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UpdateProcessingPendingJobCommand implements ICommand {

   public ICommandMessage getMessage() {
        return new UpdateProcessingPendingJobCommandMessage();
    }
}
