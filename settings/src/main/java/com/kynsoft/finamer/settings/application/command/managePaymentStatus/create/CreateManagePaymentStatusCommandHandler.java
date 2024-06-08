package com.kynsoft.finamer.settings.application.command.managePaymentStatus.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManagerPaymentStatusDto;
import com.kynsoft.finamer.settings.domain.rules.managerPaymentStatus.ManagePaymentStatusCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.managerPaymentStatus.ManagePaymentStatusNameCantBeNullRule;
import com.kynsoft.finamer.settings.domain.rules.managerPaymentStatus.ManagerPaymentStatusCodeSizeRule;
import com.kynsoft.finamer.settings.domain.services.IManagerPaymentStatusService;
import org.springframework.stereotype.Component;

@Component
public class CreateManagePaymentStatusCommandHandler implements ICommandHandler<CreateManagePaymentStatusCommand> {
    private final IManagerPaymentStatusService service;

    public CreateManagePaymentStatusCommandHandler(final IManagerPaymentStatusService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManagePaymentStatusCommand command) {
        RulesChecker.checkRule(new ManagerPaymentStatusCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManagePaymentStatusCodeMustBeUniqueRule(service, command.getCode(), command.getId()));
        RulesChecker.checkRule(new ManagePaymentStatusNameCantBeNullRule(command.getName()));

        service.create(new ManagerPaymentStatusDto(command.getId(), command.getCode(), command.getName(), command.getStatus(), command.getCollected(), command.getDescription()));
    }
}
