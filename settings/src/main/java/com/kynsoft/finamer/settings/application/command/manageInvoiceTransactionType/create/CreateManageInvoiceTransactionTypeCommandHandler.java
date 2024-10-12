package com.kynsoft.finamer.settings.application.command.manageInvoiceTransactionType.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageInvoiceTransactionTypeKafka;
import com.kynsoft.finamer.settings.domain.dto.ManageInvoiceTransactionTypeDto;
import com.kynsoft.finamer.settings.domain.rules.manageInvoiceTransactionType.ManageInvoiceTransactionTypeCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.manageInvoiceTransactionType.ManageInvoiceTransactionTypeCodeSizeRule;
import com.kynsoft.finamer.settings.domain.rules.manageInvoiceTransactionType.ManageInvoiceTransactionTypeDefaultMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.manageInvoiceTransactionType.ManageInvoiceTransactionTypeNameMustBeNullRule;
import com.kynsoft.finamer.settings.domain.services.IManageInvoiceTransactionTypeService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageInvoiceTransactionType.ProducerReplicateManageInvoiceTransactionTypeService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageInvoiceTransactionTypeCommandHandler implements ICommandHandler<CreateManageInvoiceTransactionTypeCommand> {

    private final IManageInvoiceTransactionTypeService service;

    private final ProducerReplicateManageInvoiceTransactionTypeService producerReplicateManageInvoiceTransactionTypeService;

    public CreateManageInvoiceTransactionTypeCommandHandler(IManageInvoiceTransactionTypeService service, ProducerReplicateManageInvoiceTransactionTypeService producerReplicateManageInvoiceTransactionTypeService) {
        this.service = service;
        this.producerReplicateManageInvoiceTransactionTypeService = producerReplicateManageInvoiceTransactionTypeService;
    }

    @Override
    public void handle(CreateManageInvoiceTransactionTypeCommand command) {
        RulesChecker.checkRule(new ManageInvoiceTransactionTypeCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManageInvoiceTransactionTypeNameMustBeNullRule(command.getName()));
        RulesChecker.checkRule(new ManageInvoiceTransactionTypeCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));
        if(command.isDefaults()) {
            RulesChecker.checkRule(new ManageInvoiceTransactionTypeDefaultMustBeUniqueRule(this.service, command.getId()));
        }
        service.create(new ManageInvoiceTransactionTypeDto(
                command.getId(),
                command.getCode(),
                command.getDescription(),
                command.getStatus(),
                command.getName(),
                command.getIsAgencyRateAmount(),
                command.getIsNegative(),
                command.getIsPolicyCredit(),
                command.getIsRemarkRequired(),
                command.getMinNumberOfCharacters(),
                command.getDefaultRemark(),
                command.isDefaults()
        ));

        this.producerReplicateManageInvoiceTransactionTypeService.create(new ReplicateManageInvoiceTransactionTypeKafka(command.getId(), command.getCode(), command.getName(), command.isDefaults()));
    }
}
