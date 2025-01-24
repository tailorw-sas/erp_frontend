package com.tailorw.finamer.scheduler.application.command.businessProcess.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.tailorw.finamer.scheduler.domain.dto.BusinessProcessDto;
import com.tailorw.finamer.scheduler.domain.rules.SchedulerStatusCodeRule;
import com.tailorw.finamer.scheduler.domain.rules.businessProcess.BusinessProcessCodeRule;
import com.tailorw.finamer.scheduler.domain.services.IBusinessProcessService;
import com.tailorw.finamer.scheduler.infrastructure.model.enums.Status;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UpdateBusinessProcessCommandHandler implements ICommandHandler<UpdateBusinessProcessCommand> {

    private final IBusinessProcessService service;

    public UpdateBusinessProcessCommandHandler(IBusinessProcessService service){
        this.service = service;
    }

    @Override
    public void handle(UpdateBusinessProcessCommand command) {
        BusinessProcessDto businessProcessDto = service.findById(command.getId());

        RulesChecker.checkRule(new SchedulerStatusCodeRule(command.getStatus()));

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(businessProcessDto::setProcessName, command.getProcessName(), businessProcessDto.getProcessName(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(businessProcessDto::setDescription, command.getDescription(), businessProcessDto.getDescription(), update::setUpdate);

        businessProcessDto.setStatus(Status.getCode(command.getStatus()));
        businessProcessDto.setUpdatedAt(LocalDateTime.now());

        service.update(businessProcessDto);
    }
}
