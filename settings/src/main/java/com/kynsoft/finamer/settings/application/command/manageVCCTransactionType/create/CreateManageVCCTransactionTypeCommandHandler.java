package com.kynsoft.finamer.settings.application.command.manageVCCTransactionType.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.vcc.ReplicateManageVCCTransactionTypeKafka;
import com.kynsoft.finamer.settings.domain.dto.ManageVCCTransactionTypeDto;
import com.kynsoft.finamer.settings.domain.rules.manageVCCTransactionType.*;
import com.kynsoft.finamer.settings.domain.services.IManageVCCTransactionTypeService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageVCCTransactionType.ProducerReplicateManageVCCTransactionTypeService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageVCCTransactionTypeCommandHandler implements ICommandHandler<CreateManageVCCTransactionTypeCommand> {

    private final IManageVCCTransactionTypeService service;

    private final ProducerReplicateManageVCCTransactionTypeService transactionTypeService;

    public CreateManageVCCTransactionTypeCommandHandler(IManageVCCTransactionTypeService service, ProducerReplicateManageVCCTransactionTypeService transactionTypeService) {
        this.service = service;
        this.transactionTypeService = transactionTypeService;
    }

    @Override
    public void handle(CreateManageVCCTransactionTypeCommand command) {
        RulesChecker.checkRule(new ManageVCCTransactionTypeCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManageVCCTransactionTypeCodeMustBeUniqueRule(service, command.getCode(), command.getId()));
        if(command.getIsDefault()) {
            if (command.getSubcategory()) {
                RulesChecker.checkRule(new ManageVCCTransactionTypeSubcategoryMustBeUniqueRule(service, command.getId()));
            } else {
                RulesChecker.checkRule(new ManageVCCTransactionTypeIsDefaultMustBeUniqueRule(service, command.getId()));
            }
        }

        if (command.isManual()){
            RulesChecker.checkRule(new ManageVCCTransactionTypeIsManualMustBeUniqueRule(this.service, command.getId()));
        }
        service.create(new ManageVCCTransactionTypeDto(
                command.getId(),
                command.getCode(),
                command.getDescription(),
                command.getStatus(),
                command.getName(),
                command.getIsActive(),
                command.getNegative(),
                command.getIsDefault(),
                command.getSubcategory(),
                command.getOnlyApplyNet(),
                command.getPolicyCredit(),
                command.getRemarkRequired(),
                command.getMinNumberOfCharacter(),
                command.getDefaultRemark(),
                command.isManual()
        ));

        this.transactionTypeService.create(new ReplicateManageVCCTransactionTypeKafka(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getIsDefault(),
                command.getSubcategory(),
                command.isManual(),
                command.getStatus().name()));
    }
}
