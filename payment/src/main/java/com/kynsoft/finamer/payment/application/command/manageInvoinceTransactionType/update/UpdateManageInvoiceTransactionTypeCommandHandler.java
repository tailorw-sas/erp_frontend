package com.kynsoft.finamer.payment.application.command.manageInvoinceTransactionType.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.payment.domain.dto.ManageInvoiceTransactionTypeDto;
import com.kynsoft.finamer.payment.domain.services.IManageInvoiceTransactionTypeService;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageInvoiceTransactionTypeCommandHandler implements ICommandHandler<UpdateManageInvoiceTransactionTypeCommand> {

    private final IManageInvoiceTransactionTypeService service;

    public UpdateManageInvoiceTransactionTypeCommandHandler(IManageInvoiceTransactionTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManageInvoiceTransactionTypeCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manage Invoice Transaction Type ID cannot be null."));

        ManageInvoiceTransactionTypeDto dto = service.findById(command.getId());
        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(), update::setUpdate);
        this.service.update(dto);
    }
}
