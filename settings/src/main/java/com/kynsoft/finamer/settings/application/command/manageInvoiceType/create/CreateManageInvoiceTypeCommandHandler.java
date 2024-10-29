package com.kynsoft.finamer.settings.application.command.manageInvoiceType.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageInvoiceTypeKafka;
import com.kynsoft.finamer.settings.domain.dto.ManageInvoiceTypeDto;
import com.kynsoft.finamer.settings.domain.rules.manageInvoiceType.*;
import com.kynsoft.finamer.settings.domain.rules.managePaymentTransactionType.ManagePaymentTransactionTypeIncomeDefaultMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.services.IManageInvoiceTypeService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageInvoiceType.ProducerReplicateManageInvoiceTypeService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageInvoiceTypeCommandHandler implements ICommandHandler<CreateManageInvoiceTypeCommand> {

    private final IManageInvoiceTypeService service;

    private final ProducerReplicateManageInvoiceTypeService producerReplicateManageInvoiceTypeService;

    public CreateManageInvoiceTypeCommandHandler(IManageInvoiceTypeService service, ProducerReplicateManageInvoiceTypeService producerReplicateManageInvoiceTypeService) {

        this.service = service;
        this.producerReplicateManageInvoiceTypeService = producerReplicateManageInvoiceTypeService;
    }

    @Override
    public void handle(CreateManageInvoiceTypeCommand command) {
        RulesChecker.checkRule(new ManageInvoiceTypeCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManageInvoiceTypeNameMustBeNullRule(command.getName()));
        RulesChecker.checkRule(new ManageInvoiceTypeCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));

        if (command.isInvoice()){
            RulesChecker.checkRule(new ManageInvoiceTypeInvoiceMustBeUniqueRule(this.service, command.getId()));
        }
        if (command.isIncome()){
            RulesChecker.checkRule(new ManageInvoiceTypeIncomeMustBeUniqueRule(this.service, command.getId()));
        }
        if (command.isCredit()){
            RulesChecker.checkRule(new ManageInvoiceTypeCreditMustBeUniqueRule(this.service, command.getId()));
        }

        service.create(new ManageInvoiceTypeDto(
                command.getId(),
                command.getCode(),
                command.getDescription(),
                command.getStatus(),
                command.getName(),
                command.getEnabledToPolicy(),
                command.isIncome(),
                command.isCredit(),
                command.isInvoice()
        ));

        this.producerReplicateManageInvoiceTypeService.create(new ReplicateManageInvoiceTypeKafka(command.getId(), command.getCode(), command.getName(), command.isIncome(), command.isCredit(), command.isInvoice(), command.getStatus().name()));
    }
}
