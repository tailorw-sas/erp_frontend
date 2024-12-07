package com.kynsoft.finamer.settings.application.command.managerTimeZone.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageTimeZoneKafka;
import com.kynsoft.finamer.settings.domain.dto.ManagerTimeZoneDto;
import com.kynsoft.finamer.settings.domain.rules.managerTimeZone.ManagerTimeZoneCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.managerTimeZone.ManagerTimeZoneCodeSizeRule;
import com.kynsoft.finamer.settings.domain.rules.managerTimeZone.ManagerTimeZoneNameMustBeNullRule;
import com.kynsoft.finamer.settings.domain.services.IManagerTimeZoneService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageTimeZone.ProducerReplicateManageTimeZoneService;
import org.springframework.stereotype.Component;

@Component
public class CreateManagerTimeZoneCommandHandler implements ICommandHandler<CreateManagerTimeZoneCommand> {

    private final IManagerTimeZoneService service;
    private final ProducerReplicateManageTimeZoneService producerReplicateManageTimeZoneService;

    public CreateManagerTimeZoneCommandHandler(IManagerTimeZoneService service,
                                               ProducerReplicateManageTimeZoneService producerReplicateManageTimeZoneService) {
        this.service = service;
        this.producerReplicateManageTimeZoneService = producerReplicateManageTimeZoneService;
    }

    @Override
    public void handle(CreateManagerTimeZoneCommand command) {
        RulesChecker.checkRule(new ManagerTimeZoneCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManagerTimeZoneNameMustBeNullRule(command.getName()));
        RulesChecker.checkRule(new ManagerTimeZoneCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));

        service.create(new ManagerTimeZoneDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getDescription(),
                command.getElapse(),
                command.getStatus()
        ));
        this.producerReplicateManageTimeZoneService.create(new ReplicateManageTimeZoneKafka(
                command.getId(), 
                command.getCode(), 
                command.getName(), 
                command.getDescription(), 
                command.getElapse(), 
                command.getStatus().name()
        ));
    }
}
