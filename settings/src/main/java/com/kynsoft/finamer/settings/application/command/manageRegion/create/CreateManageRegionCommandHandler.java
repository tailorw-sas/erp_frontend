package com.kynsoft.finamer.settings.application.command.manageRegion.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ManageRegionKafka;
import com.kynsoft.finamer.settings.domain.dto.ManageRegionDto;
import com.kynsoft.finamer.settings.domain.rules.manageRegion.ManageRegionCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.manageRegion.ManageRegionCodeSizeRule;
import com.kynsoft.finamer.settings.domain.rules.manageRegion.ManageRegionNameMustBeNullRule;
import com.kynsoft.finamer.settings.domain.services.IManageRegionService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageRegion.ProducerReplicateManageRegionService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageRegionCommandHandler implements ICommandHandler<CreateManageRegionCommand> {

    private final IManageRegionService service;

    private final ProducerReplicateManageRegionService producer;

    public CreateManageRegionCommandHandler(IManageRegionService service, ProducerReplicateManageRegionService producer) {
        this.service = service;
        this.producer = producer;
    }

    @Override
    public void handle(CreateManageRegionCommand command) {
        RulesChecker.checkRule(new ManageRegionCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManageRegionNameMustBeNullRule(command.getName()));
        RulesChecker.checkRule(new ManageRegionCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));

        service.create(new ManageRegionDto(
                command.getId(),
                command.getCode(),
                command.getDescription(),
                command.getStatus(),
                command.getName()
        ));

        this.producer.create(new ManageRegionKafka(
                command.getId(), command.getCode(), command.getName()
        ));
    }
}
