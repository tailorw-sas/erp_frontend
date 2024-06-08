package com.kynsoft.finamer.settings.application.command.manageRatePlan.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageRatePlanDto;
import com.kynsoft.finamer.settings.domain.rules.manageRatePlan.ManageRatePlanCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.manageRatePlan.ManageRatePlanCodeSizeRule;
import com.kynsoft.finamer.settings.domain.rules.manageRatePlan.ManageRatePlanNameMustBeNullRule;
import com.kynsoft.finamer.settings.domain.services.IManageRatePlanService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageRatePlanCommandHandler implements ICommandHandler<CreateManageRatePlanCommand> {

    private final IManageRatePlanService service;

    public CreateManageRatePlanCommandHandler(IManageRatePlanService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManageRatePlanCommand command) {
        RulesChecker.checkRule(new ManageRatePlanCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManageRatePlanNameMustBeNullRule(command.getName()));
        RulesChecker.checkRule(new ManageRatePlanCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));

        service.create(new ManageRatePlanDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getHotel(),
                command.getDescription(),
                command.getStatus()
        ));
    }
}
