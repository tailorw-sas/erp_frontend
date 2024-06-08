package com.kynsoft.finamer.settings.application.command.manageActionLog.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageActionLogDto;
import com.kynsoft.finamer.settings.domain.rules.manageActionLog.ManageActionLogCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.manageActionLog.ManageActionLogCodeSizeRule;
import com.kynsoft.finamer.settings.domain.rules.manageActionLog.ManageActionLogNameMustBeNullRule;
import com.kynsoft.finamer.settings.domain.services.IManageActionLogService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageActionLogCommandHandler implements ICommandHandler<CreateManageActionLogCommand> {

    private final IManageActionLogService service;

    public CreateManageActionLogCommandHandler(IManageActionLogService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManageActionLogCommand command) {
        RulesChecker.checkRule(new ManageActionLogCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManageActionLogNameMustBeNullRule(command.getName()));
        RulesChecker.checkRule(new ManageActionLogCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));

        service.create(new ManageActionLogDto(
                command.getId(),
                command.getCode(),
                command.getDescription(),
                command.getStatus(),
                command.getName()
        ));
    }
}
