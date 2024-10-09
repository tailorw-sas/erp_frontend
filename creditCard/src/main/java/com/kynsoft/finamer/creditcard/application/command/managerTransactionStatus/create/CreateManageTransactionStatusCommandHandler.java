package com.kynsoft.finamer.creditcard.application.command.managerTransactionStatus.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ManageTransactionStatusDto;
import com.kynsoft.finamer.creditcard.domain.rules.manageTransactionStatus.ManageTransactionStatusCodeMustBeUniqueRule;
import com.kynsoft.finamer.creditcard.domain.rules.manageTransactionStatus.ManageTransactionStatusCodeSizeRule;
import com.kynsoft.finamer.creditcard.domain.rules.manageTransactionStatus.ManageTransactionStatusNameMustBeNullRule;
import com.kynsoft.finamer.creditcard.domain.services.IManageTransactionStatusService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CreateManageTransactionStatusCommandHandler implements ICommandHandler<CreateManageTransactionStatusCommand> {

    private final IManageTransactionStatusService service;

    public CreateManageTransactionStatusCommandHandler(IManageTransactionStatusService service) {
        this.service = service;
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
    }
}
