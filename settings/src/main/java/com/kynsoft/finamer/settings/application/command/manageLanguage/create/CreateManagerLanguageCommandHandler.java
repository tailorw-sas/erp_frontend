package com.kynsoft.finamer.settings.application.command.manageLanguage.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.vcc.ReplicateManageLanguageKafka;
import com.kynsoft.finamer.settings.domain.dto.ManagerLanguageDto;
import com.kynsoft.finamer.settings.domain.rules.managerLanguage.ManageLanguageDefaultMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.managerLanguage.ManagerLanguageCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.managerLanguage.ManagerLanguageCodeSizeRule;
import com.kynsoft.finamer.settings.domain.rules.managerLanguage.ManagerLanguageNameMustBeNullRule;
import com.kynsoft.finamer.settings.domain.services.IManagerLanguageService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageLanguage.ProducerReplicateManageLanguageService;
import org.springframework.stereotype.Component;

@Component
public class CreateManagerLanguageCommandHandler implements ICommandHandler<CreateManagerLanguageCommand> {

    private final IManagerLanguageService service;

    private final ProducerReplicateManageLanguageService producerReplicateManageLanguageService;

    public CreateManagerLanguageCommandHandler(IManagerLanguageService service, ProducerReplicateManageLanguageService producerReplicateManageLanguageService) {
        this.service = service;
        this.producerReplicateManageLanguageService = producerReplicateManageLanguageService;
    }

    @Override
    public void handle(CreateManagerLanguageCommand command) {
        RulesChecker.checkRule(new ManagerLanguageCodeMustBeUniqueRule(service, command.getCode(), command.getId()));
        RulesChecker.checkRule(new ManagerLanguageCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManagerLanguageNameMustBeNullRule(command.getName()));
        if(command.getDefaults() != null) {
            if(command.getDefaults()) {
                RulesChecker.checkRule(new ManageLanguageDefaultMustBeUniqueRule(this.service, command.getId()));
            }
        }
        service.create(new ManagerLanguageDto(
                command.getId(),
                command.getCode(),
                command.getDescription(),
                command.getStatus(),
                command.getName(),
                command.getIsEnabled(),
                command.getDefaults()
        ));
        this.producerReplicateManageLanguageService.create(new ReplicateManageLanguageKafka(command.getId(), command.getCode(), command.getName()));
    }
}
