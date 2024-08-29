package com.kynsoft.finamer.settings.application.command.managePaymentSource.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManagePaymentSourceKafka;
import com.kynsoft.finamer.settings.domain.dto.ManagePaymentSourceDto;
import com.kynsoft.finamer.settings.domain.rules.managePaymentSource.ManagePaymentSourceCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.managePaymentSource.ManagePaymentSourceCodeRule;
import com.kynsoft.finamer.settings.domain.rules.managePaymentSource.ManagePaymentSourceNameMustBeNullRule;
import com.kynsoft.finamer.settings.domain.services.IManagePaymentSourceService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.managePaymentSource.ProducerReplicateManagePaymentSourceService;
import org.springframework.stereotype.Component;

@Component
public class CreateManagePaymentSourceCommandHandler implements ICommandHandler<CreateManagePaymentSourceCommand> {

    private final IManagePaymentSourceService service;
    private final ProducerReplicateManagePaymentSourceService producerReplicateManagePaymentSourceService;

    public CreateManagePaymentSourceCommandHandler(IManagePaymentSourceService service,
                                                   ProducerReplicateManagePaymentSourceService producerReplicateManagePaymentSourceService) {
        this.service = service;
        this.producerReplicateManagePaymentSourceService = producerReplicateManagePaymentSourceService;
    }

    @Override
    public void handle(CreateManagePaymentSourceCommand command) {
        RulesChecker.checkRule(new ManagePaymentSourceCodeRule(command.getCode()));
        RulesChecker.checkRule(new ManagePaymentSourceCodeMustBeUniqueRule(service, command.getCode(), command.getId()));
        RulesChecker.checkRule(new ManagePaymentSourceNameMustBeNullRule(command.getName()));

        service.create( new ManagePaymentSourceDto(
                command.getId(),
                command.getCode(),
                command.getDescription(),
                command.getStatus(),
                command.getName(),
                command.getIsBank(),
                command.getExpense()
        ));
        this.producerReplicateManagePaymentSourceService.create(new ReplicateManagePaymentSourceKafka(command.getId(), command.getCode(), command.getName(), command.getStatus().name(), command.getExpense()));
    }
}
