package com.kynsoft.finamer.settings.application.command.manageBank.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ManageBankKafka;
import com.kynsoft.finamer.settings.domain.dto.ManagerBankDto;
import com.kynsoft.finamer.settings.domain.rules.managerBank.ManagerBankCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.managerBank.ManagerBankCodeSizeRule;
import com.kynsoft.finamer.settings.domain.rules.managerBank.ManagerBankNameMustBeNullRule;
import com.kynsoft.finamer.settings.domain.services.IManagerBankService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageBank.ProducerReplicateBankService;
import org.springframework.stereotype.Component;

@Component
public class CreateManagerBankCommandHandler implements ICommandHandler<CreateManagerBankCommand> {

    private final IManagerBankService service;

    private final ProducerReplicateBankService producerReplicateBankService;

    public CreateManagerBankCommandHandler(IManagerBankService service, ProducerReplicateBankService producerReplicateBankService) {
        this.service = service;
        this.producerReplicateBankService = producerReplicateBankService;
    }

    @Override
    public void handle(CreateManagerBankCommand command) {
        RulesChecker.checkRule(new ManagerBankCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManagerBankNameMustBeNullRule(command.getName()));
        RulesChecker.checkRule(new ManagerBankCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));

        service.create(new ManagerBankDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getDescription(),
                command.getStatus()
        ));

        this.producerReplicateBankService.replicate(new ManageBankKafka(
                command.getId(), command.getCode(), command.getName(),
                command.getDescription(), command.getStatus().name()
        ));
    }
}
