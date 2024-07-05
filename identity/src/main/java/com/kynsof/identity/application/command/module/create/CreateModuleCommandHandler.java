package com.kynsof.identity.application.command.module.create;

import com.kynsof.identity.domain.dto.ModuleDto;
import com.kynsof.identity.domain.interfaces.service.IModuleService;
import com.kynsof.identity.domain.rules.module.ModuleCodeMustBeUniqueRule;
import com.kynsof.identity.domain.rules.module.ModuleDescriptionMustBeNullRule;
import com.kynsof.identity.domain.rules.module.ModuleNameMustBeNullRule;
import com.kynsof.identity.domain.rules.module.ModuleNameMustBeUniqueRule;
import com.kynsof.identity.infrastructure.services.kafka.producer.module.ProducerCreateModuleEventService;
import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ReplicateModuleKafka;
import org.springframework.stereotype.Component;

@Component
public class CreateModuleCommandHandler implements ICommandHandler<CreateModuleCommand> {

    private final IModuleService service;

    private final ProducerCreateModuleEventService createModuleEventService;

    public CreateModuleCommandHandler(IModuleService service, ProducerCreateModuleEventService createModuleEventService) {
        this.service = service;
        this.createModuleEventService = createModuleEventService;
    }

    @Override
    public void handle(CreateModuleCommand command) {
        RulesChecker.checkRule(new ModuleNameMustBeNullRule(command.getName()));
        RulesChecker.checkRule(new ModuleDescriptionMustBeNullRule(command.getDescription()));
        RulesChecker.checkRule(new ModuleNameMustBeUniqueRule(this.service, command.getName(), command.getId()));
        RulesChecker.checkRule(new ModuleCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));

        service.create(new ModuleDto(
                command.getId(),
                command.getName(),
                null,
                command.getDescription(),
                command.getStatus(),
                command.getCode())
        );
        this.createModuleEventService.create(new ReplicateModuleKafka(command.getId(), command.getCode(), command.getName(), command.getStatus().name()));
    }
}
