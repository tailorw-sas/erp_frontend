package com.tailorw.finamer.scheduler.application.command.schedulerLogProcess.updateLog;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.tailorw.finamer.scheduler.domain.dto.BusinessProcessSchedulerDto;
import com.tailorw.finamer.scheduler.domain.dto.SchedulerLogDto;
import com.tailorw.finamer.scheduler.domain.services.IBusinessProcessSchedulerService;
import com.tailorw.finamer.scheduler.domain.services.ISchedulerLogService;
import com.tailorw.finamer.scheduler.infrastructure.model.enums.ProcessStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UpdateSchedulerLogProcessCommandHandler implements ICommandHandler<UpdateSchedulerLogProcessCommand> {

    private final ISchedulerLogService logService;
    private final IBusinessProcessSchedulerService businessProcessSchedulerService;

    public UpdateSchedulerLogProcessCommandHandler(ISchedulerLogService logService,
                                                   IBusinessProcessSchedulerService businessProcessSchedulerService){
        this.logService = logService;
        this.businessProcessSchedulerService = businessProcessSchedulerService;
    }

    @Override
    public void handle(UpdateSchedulerLogProcessCommand command) {
        SchedulerLogDto log = logService.getById(command.getId());
        setSchedulerLogACompleted(log, command.getCompletedAt(), command.getDetails());
        updateSchedulerAfterExecution(log.getScheduler(), command.getCompletedAt());
    }

    private void updateSchedulerAfterExecution(BusinessProcessSchedulerDto scheduler, LocalDateTime completedAt) {
        scheduler.setInProcess(false);
        scheduler.setLastExecutionDatetime(completedAt);
        businessProcessSchedulerService.update(scheduler);
    }

    private void setSchedulerLogACompleted(SchedulerLogDto schedulerLog, LocalDateTime completedAt, String details){
        schedulerLog.setStatus(ProcessStatus.COMPLETED);
        schedulerLog.setCompletedAt(completedAt);
        schedulerLog.setDetails(details);
        logService.update(schedulerLog);
    }
}
