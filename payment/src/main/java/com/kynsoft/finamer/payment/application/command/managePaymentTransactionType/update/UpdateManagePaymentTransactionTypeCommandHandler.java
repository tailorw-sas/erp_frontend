package com.kynsoft.finamer.payment.application.command.managePaymentTransactionType.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentTransactionTypeDto;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentTransactionTypeService;
import org.springframework.stereotype.Component;

@Component
public class UpdateManagePaymentTransactionTypeCommandHandler implements ICommandHandler<UpdateManagePaymentTransactionTypeCommand> {

    private final IManagePaymentTransactionTypeService service;

    public UpdateManagePaymentTransactionTypeCommandHandler(IManagePaymentTransactionTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManagePaymentTransactionTypeCommand command) {
        ManagePaymentTransactionTypeDto dto = this.service.findById(command.getId());
        dto.setApplyDeposit(command.getApplyDeposit());
        dto.setCash(command.getCash());
        dto.setDeposit(command.getDeposit());
        dto.setMinNumberOfCharacter(command.getMinNumberOfCharacter());
        dto.setRemarkRequired(command.getRemarkRequired());
        dto.setName(command.getName());
        dto.setStatus(command.getStatus());
        dto.setDefaultRemark(command.getDefaultRemark());
        dto.setDefaults(command.getDefaults());
        dto.setPaymentInvoice(command.getPaymentInvoice());
        service.update(dto);
    }
}
