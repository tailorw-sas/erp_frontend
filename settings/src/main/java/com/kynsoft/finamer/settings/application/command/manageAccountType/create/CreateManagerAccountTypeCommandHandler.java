package com.kynsoft.finamer.settings.application.command.manageAccountType.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ManageAccountTypeKafka;
import com.kynsoft.finamer.settings.domain.dto.ManagerAccountTypeDto;
import com.kynsoft.finamer.settings.domain.rules.managerAccountType.ManagerAccountTypeCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.managerAccountType.ManagerAccountTypeCodeSizeRule;
import com.kynsoft.finamer.settings.domain.rules.managerAccountType.ManagerAccountTypeNameMustBeNullRule;
import com.kynsoft.finamer.settings.domain.services.IManagerAccountTypeService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageAccountType.ProducerReplicateAccountTypeService;
import org.springframework.stereotype.Component;

@Component
public class CreateManagerAccountTypeCommandHandler implements ICommandHandler<CreateManagerAccountTypeCommand> {

    private final IManagerAccountTypeService service;

    private final ProducerReplicateAccountTypeService producerReplicateAccountTypeService;

    public CreateManagerAccountTypeCommandHandler(IManagerAccountTypeService service, ProducerReplicateAccountTypeService producerReplicateAccountTypeService) {
        this.service = service;
        this.producerReplicateAccountTypeService = producerReplicateAccountTypeService;
    }

    @Override
    public void handle(CreateManagerAccountTypeCommand command) {
        RulesChecker.checkRule(new ManagerAccountTypeCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManagerAccountTypeNameMustBeNullRule(command.getName()));
        RulesChecker.checkRule(new ManagerAccountTypeCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));

        service.create(new ManagerAccountTypeDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getDescription(),
                command.getStatus()
        ));

        this.producerReplicateAccountTypeService.replicate(new ManageAccountTypeKafka(
                command.getId(), command.getCode(), command.getName(),
                command.getDescription(), command.getStatus().name()
        ));
    }
}
