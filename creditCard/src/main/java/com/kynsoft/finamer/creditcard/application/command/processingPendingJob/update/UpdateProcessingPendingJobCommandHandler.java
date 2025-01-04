package com.kynsoft.finamer.creditcard.application.command.processingPendingJob.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.infrastructure.services.ProcessingPendingJobServiceImpl;
import org.springframework.stereotype.Component;

@Component
public class UpdateProcessingPendingJobCommandHandler implements ICommandHandler<UpdateProcessingPendingJobCommand> {

    private final ProcessingPendingJobServiceImpl pendingJobService;

    public UpdateProcessingPendingJobCommandHandler(ProcessingPendingJobServiceImpl pendingJobService){
        this.pendingJobService = pendingJobService;
    }

    @Override
    public void handle(UpdateProcessingPendingJobCommand command) {
        pendingJobService.checkIsProcessedAndCallTransactionStatus();
    }
}
