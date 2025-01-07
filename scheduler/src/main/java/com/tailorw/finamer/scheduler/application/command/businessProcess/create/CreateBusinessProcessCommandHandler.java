package com.tailorw.finamer.scheduler.application.command.businessProcess.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.tailorw.finamer.scheduler.domain.dto.BusinessProcessDto;
import com.tailorw.finamer.scheduler.domain.rules.SchedulerStatusCodeRule;
import com.tailorw.finamer.scheduler.domain.rules.businessProcess.BusinessProcessCodeRule;
import com.tailorw.finamer.scheduler.domain.services.IBusinessProcessService;
import com.tailorw.finamer.scheduler.infrastructure.model.enums.Status;
import org.springframework.stereotype.Component;

@Component
public class CreateBusinessProcessCommandHandler implements ICommandHandler<CreateBusinessProcessCommand> {

    private final IBusinessProcessService service;

    public CreateBusinessProcessCommandHandler(IBusinessProcessService service){
        this.service = service;
    }

    @Override
    public void handle(CreateBusinessProcessCommand command) {

        RulesChecker.checkRule(new BusinessProcessCodeRule(command.getCode(), service));
        RulesChecker.checkRule(new SchedulerStatusCodeRule(command.getStatus()));

        BusinessProcessDto dto = new BusinessProcessDto(
                command.getId(),
                command.getCode(),
                command.getProcessName(),
                command.getDescription(),
                Status.getCode(command.getStatus()),
                null,
                null
        );
        service.create(dto);

    }
}
