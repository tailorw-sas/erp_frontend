package com.kynsoft.finamer.settings.application.command.managePaymentSource.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManagePaymentSourceDto;
import com.kynsoft.finamer.settings.domain.rules.managePaymentSource.ManagePaymentSourceCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.managePaymentSource.ManagePaymentSourceCodeRule;
import com.kynsoft.finamer.settings.domain.rules.managePaymentSource.ManagePaymentSourceNameMustBeNullRule;
import com.kynsoft.finamer.settings.domain.services.IManagePaymentSourceService;
import org.springframework.stereotype.Component;

@Component
public class CreateManagePaymentSourceCommandHandler implements ICommandHandler<CreateManagePaymentSourceCommand> {

    private final IManagePaymentSourceService service;

    public CreateManagePaymentSourceCommandHandler(IManagePaymentSourceService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManagePaymentSourceCommand command) {
        RulesChecker.checkRule(new ManagePaymentSourceCodeRule(command.getCode()));
        RulesChecker.checkRule(new ManagePaymentSourceCodeMustBeUniqueRule(service, command.getCode(), command.getId()));
        RulesChecker.checkRule(new ManagePaymentSourceNameMustBeNullRule(command.getName()));

        service.create( new ManagePaymentSourceDto(
                command.getId(),
                command.getCode(),
                command.getDescription(),
                command.getStatus(),
                command.getName(),
                command.getIsBank()
        ));
    }
}
