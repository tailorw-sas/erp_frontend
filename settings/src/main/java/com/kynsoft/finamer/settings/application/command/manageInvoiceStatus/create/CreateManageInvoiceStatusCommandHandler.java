package com.kynsoft.finamer.settings.application.command.manageInvoiceStatus.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageInvoiceStatusDto;
import com.kynsoft.finamer.settings.domain.rules.manageInvoiceStatus.ManageInvoiceStatusCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.manageInvoiceStatus.ManageInvoiceStatusCodeSizeRule;
import com.kynsoft.finamer.settings.domain.rules.manageInvoiceStatus.ManageInvoiceStatusNameMustBeNullRule;
import com.kynsoft.finamer.settings.domain.services.IManageInvoiceStatusService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageInvoiceStatusCommandHandler implements ICommandHandler<CreateManageInvoiceStatusCommand> {

    private final IManageInvoiceStatusService service;

    public CreateManageInvoiceStatusCommandHandler(IManageInvoiceStatusService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManageInvoiceStatusCommand command) {
        RulesChecker.checkRule(new ManageInvoiceStatusCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManageInvoiceStatusNameMustBeNullRule(command.getName()));
        RulesChecker.checkRule(new ManageInvoiceStatusCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));

        service.create(new ManageInvoiceStatusDto(
                command.getId(),
                command.getCode(),
                command.getDescription(),
                command.getStatus(),
                command.getName(),
                command.getEnabledToPrint(),
                command.getEnabledToPropagate(),
                command.getEnabledToApply(),
                command.getEnabledToPolicy(),
                command.getProcessStatus(),
                command.getNavigate()
        ));
    }
}
