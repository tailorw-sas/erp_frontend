package com.tailorw.finamer.scheduler.application.command.businessProcessScheduler.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.tailorw.finamer.scheduler.domain.dto.BusinessProcessSchedulerDto;
import com.tailorw.finamer.scheduler.domain.services.IBusinessProcessSchedulerService;
import com.tailorw.finamer.scheduler.infrastructure.model.enums.Status;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DeleteBusinessProcessSchedulerCommandHandler implements ICommandHandler<DeleteBusinessProcessSchedulerCommand> {

    private final IBusinessProcessSchedulerService service;

    public DeleteBusinessProcessSchedulerCommandHandler(IBusinessProcessSchedulerService service){
        this.service = service;
    }

    @Override
    public void handle(DeleteBusinessProcessSchedulerCommand command) {
        BusinessProcessSchedulerDto dto = service.findById(command.getId());

        dto.setStatus(Status.DELETED);
        dto.setDeletedAt(LocalDateTime.now());

        service.update(dto);
    }
}
