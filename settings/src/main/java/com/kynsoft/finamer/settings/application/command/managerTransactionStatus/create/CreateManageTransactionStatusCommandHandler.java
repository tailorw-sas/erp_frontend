package com.kynsoft.finamer.settings.application.command.managerTransactionStatus.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.vcc.ReplicateManageTransactionStatusKafka;
import com.kynsoft.finamer.settings.domain.dto.ManageTransactionStatusDto;
import com.kynsoft.finamer.settings.domain.rules.manageTransactionStatus.ManageTransactionStatusCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.manageTransactionStatus.ManageTransactionStatusCodeSizeRule;
import com.kynsoft.finamer.settings.domain.rules.manageTransactionStatus.ManageTransactionStatusNameMustBeNullRule;
import com.kynsoft.finamer.settings.domain.services.IManageTransactionStatusService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageTransactionStatus.ProducerReplicateManageTransactionStatusService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CreateManageTransactionStatusCommandHandler implements ICommandHandler<CreateManageTransactionStatusCommand> {

    private final IManageTransactionStatusService service;

    private final ProducerReplicateManageTransactionStatusService producerReplicateManageTransactionStatusService;

    public CreateManageTransactionStatusCommandHandler(IManageTransactionStatusService service, ProducerReplicateManageTransactionStatusService producerReplicateManageTransactionStatusService) {
        this.service = service;
        this.producerReplicateManageTransactionStatusService = producerReplicateManageTransactionStatusService;
    }

    @Override
    public void handle(CreateManageTransactionStatusCommand command) {
        RulesChecker.checkRule(new ManageTransactionStatusCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManageTransactionStatusNameMustBeNullRule(command.getName()));
        //RulesChecker.checkRule(new ManageTransactionStatusNavigateMustBeNullRule(command.getNavigate()));
        RulesChecker.checkRule(new ManageTransactionStatusCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));

        List<ManageTransactionStatusDto> navigate = this.service.findByIds(command.getNavigate().stream().toList());


        service.create(new ManageTransactionStatusDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getDescription(),
                navigate,
                command.getEnablePayment(),
                command.getVisible(),
                command.getStatus()
        ));
        this.producerReplicateManageTransactionStatusService.create(new ReplicateManageTransactionStatusKafka(command.getId(), command.getCode(), command.getName()));
    }
}
