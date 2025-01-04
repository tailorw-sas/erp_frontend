package com.kynsoft.finamer.settings.application.command.manageB2BPartnerType.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ReplicateB2BPartnerTypeKafka;
import com.kynsoft.finamer.settings.domain.dto.ManageB2BPartnerTypeDto;
import com.kynsoft.finamer.settings.domain.rules.managerB2BPartnerType.ManageB2BPartnerTypeCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.managerB2BPartnerType.ManageB2BPartnerTypeCodeSizeRule;
import com.kynsoft.finamer.settings.domain.rules.managerB2BPartnerType.ManageB2BPartnerTypeNameMustBeNullRule;
import com.kynsoft.finamer.settings.domain.services.IManageB2BPartnerTypeService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageB2BPartnerType.ProducerReplicateB2BPartnerTypeService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageB2BPartnerTypeCommandHandler implements ICommandHandler<CreateManageB2BPartnerTypeCommand> {

    private final IManageB2BPartnerTypeService service;
    private final ProducerReplicateB2BPartnerTypeService typeService;

    public CreateManageB2BPartnerTypeCommandHandler(IManageB2BPartnerTypeService service,
                                                    ProducerReplicateB2BPartnerTypeService typeService) {
        this.service = service;
        this.typeService = typeService;
    }

    @Override
    public void handle(CreateManageB2BPartnerTypeCommand command) {
        RulesChecker.checkRule(new ManageB2BPartnerTypeCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManageB2BPartnerTypeNameMustBeNullRule(command.getName()));
        RulesChecker.checkRule(new ManageB2BPartnerTypeCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));

        service.create(new ManageB2BPartnerTypeDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getDescription(),
                command.getStatus()
        ));
        typeService.create(ReplicateB2BPartnerTypeKafka.builder()
                .id(command.getId())
                .code(command.getCode())
                .name(command.getName())
                .description(command.getDescription())
                .status(command.getStatus().name())
                .build()
        );
    }
}
