package com.kynsoft.finamer.settings.application.command.manageClient.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageClientDto;
import com.kynsoft.finamer.settings.domain.rules.manageClient.ManageClientCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.manageClient.ManageClientCodeSizeRule;
import com.kynsoft.finamer.settings.domain.rules.manageClient.ManageClientNameMustBeNullRule;
import com.kynsoft.finamer.settings.domain.services.IManagerClientService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageClientCommandHandler implements ICommandHandler<CreateManageClientCommand> {

    private final IManagerClientService service;

    public CreateManageClientCommandHandler(IManagerClientService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManageClientCommand command) {
        RulesChecker.checkRule(new ManageClientCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManageClientNameMustBeNullRule(command.getName()));
        RulesChecker.checkRule(new ManageClientCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));

        service.create(new ManageClientDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getDescription(),
                command.getStatus()
        ));
    }
}
