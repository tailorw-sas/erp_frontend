package com.kynsoft.finamer.settings.application.command.manageCollectionStatus.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.vcc.ReplicateManageCollectionStatusKafka;
import com.kynsoft.finamer.settings.domain.dto.ManageCollectionStatusDto;
import com.kynsoft.finamer.settings.domain.rules.manageCollectionStatus.ManageCollectionStatusCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.manageCollectionStatus.ManageCollectionStatusCodeSizeRule;
import com.kynsoft.finamer.settings.domain.rules.manageCollectionStatus.ManageCollectionStatusNameMustBeNullRule;
import com.kynsoft.finamer.settings.domain.rules.manageCollectionStatus.ManageCollectionStatusNameMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.services.IManageCollectionStatusService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageCollectionStatus.ProducerReplicateManageCollectionStatusService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CreateManageCollectionStatusCommandHandler implements ICommandHandler<CreateManageCollectionStatusCommand> {

    private final IManageCollectionStatusService service;

    private final ProducerReplicateManageCollectionStatusService producerService;

    public CreateManageCollectionStatusCommandHandler(IManageCollectionStatusService service, ProducerReplicateManageCollectionStatusService producerService) {
        this.service = service;
        this.producerService = producerService;
    }

    @Override
    public void handle(CreateManageCollectionStatusCommand command) {
        RulesChecker.checkRule(new ManageCollectionStatusCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManageCollectionStatusNameMustBeNullRule(command.getName()));
        RulesChecker.checkRule(new ManageCollectionStatusCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));
        RulesChecker.checkRule(new ManageCollectionStatusNameMustBeUniqueRule(this.service, command.getName(), command.getId()));

        List<ManageCollectionStatusDto> navigate = this.service.findByIds(command.getNavigate());

        this.service.create(new ManageCollectionStatusDto(
                command.getId(), command.getCode(), command.getDescription(),
                command.getStatus(), command.getName(), command.getEnabledPayment(),
                command.getIsVisible(), navigate
        ));
        this.producerService.create(new ReplicateManageCollectionStatusKafka(command.getId(), command.getCode(), command.getName()));
    }
}
