package com.tailorw.finamer.scheduler.application.command.businessProcess.delete;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.tailorw.finamer.scheduler.domain.dto.BusinessProcessDto;
import com.tailorw.finamer.scheduler.domain.rules.businessProcess.BusinessProcessHasSchedulersRule;
import com.tailorw.finamer.scheduler.domain.services.IBusinessProcessSchedulerService;
import com.tailorw.finamer.scheduler.domain.services.IBusinessProcessService;
import com.tailorw.finamer.scheduler.infrastructure.model.enums.Status;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DeleteBusinessProcessHandler implements ICommandHandler<DeleteBusinessProcessCommand> {

    private final IBusinessProcessService service;
    private final IBusinessProcessSchedulerService businessProcessSchedulerService;

    public DeleteBusinessProcessHandler(IBusinessProcessService service,
                                        IBusinessProcessSchedulerService businessProcessSchedulerService){
        this.service = service;
        this.businessProcessSchedulerService = businessProcessSchedulerService;
    }

    @Override
    public void handle(DeleteBusinessProcessCommand command) {

        BusinessProcessDto dto = service.findById(command.getId());

        RulesChecker.checkRule(new BusinessProcessHasSchedulersRule(command.getId(), service));

        dto.setStatus(Status.DELETED);
        dto.setDeletedAt(LocalDateTime.now());

        service.update(dto);
    }
}
