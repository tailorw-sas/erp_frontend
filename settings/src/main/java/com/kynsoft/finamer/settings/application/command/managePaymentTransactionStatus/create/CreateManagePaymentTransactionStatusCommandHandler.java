package com.kynsoft.finamer.settings.application.command.managePaymentTransactionStatus.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManagePaymentTransactionStatusDto;
import com.kynsoft.finamer.settings.domain.rules.managePaymentTransactionStatus.ManagePaymentTransactionStatusCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.managePaymentTransactionStatus.ManagePaymentTransactionStatusCodeSizeRule;
import com.kynsoft.finamer.settings.domain.services.IManagePaymentTransactionStatusService;
import org.springframework.stereotype.Component;

@Component
public class CreateManagePaymentTransactionStatusCommandHandler implements ICommandHandler<CreateManagePaymentTransactionStatusCommand> {

    private final IManagePaymentTransactionStatusService service;

    public CreateManagePaymentTransactionStatusCommandHandler(IManagePaymentTransactionStatusService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManagePaymentTransactionStatusCommand command) {
        RulesChecker.checkRule(new ManagePaymentTransactionStatusCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManagePaymentTransactionStatusCodeMustBeUniqueRule(service, command.getCode(), command.getId()));

        service.create(new ManagePaymentTransactionStatusDto(
                command.getId(),
                command.getCode(),
                command.getDescription(),
                command.getStatus(),
                command.getName()
        ));
    }
}
