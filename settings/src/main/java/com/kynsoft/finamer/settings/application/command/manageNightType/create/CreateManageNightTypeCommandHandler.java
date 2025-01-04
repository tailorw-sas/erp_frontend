package com.kynsoft.finamer.settings.application.command.manageNightType.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageNightTypeKafka;
import com.kynsoft.finamer.settings.domain.dto.ManageNightTypeDto;
import com.kynsoft.finamer.settings.domain.rules.manageNightType.ManageNightTypeCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.manageNightType.ManageNightTypeCodeSizeRule;
import com.kynsoft.finamer.settings.domain.rules.manageNightType.ManageNightTypeNameMustBeNullRule;
import com.kynsoft.finamer.settings.domain.services.IManageNightTypeService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageNightType.ProducerReplicateManageNightTypeService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageNightTypeCommandHandler implements ICommandHandler<CreateManageNightTypeCommand> {

    private final IManageNightTypeService service;

    private final ProducerReplicateManageNightTypeService producerReplicateManageNightTypeService;

    public CreateManageNightTypeCommandHandler(IManageNightTypeService service,
                                               ProducerReplicateManageNightTypeService producerReplicateManageNightTypeService) {

        this.service = service;
        this.producerReplicateManageNightTypeService = producerReplicateManageNightTypeService;
    }

    @Override
    public void handle(CreateManageNightTypeCommand command) {
        RulesChecker.checkRule(new ManageNightTypeCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManageNightTypeNameMustBeNullRule(command.getName()));
        RulesChecker.checkRule(new ManageNightTypeCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));

        service.create(new ManageNightTypeDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getStatus(),
                command.getDescription()
        ));

        this.producerReplicateManageNightTypeService.create(new ReplicateManageNightTypeKafka(command.getId(), command.getCode(), command.getName(), command.getStatus().name()));
    }
}
