package com.kynsoft.finamer.settings.application.command.manageRegion.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageRegionDto;
import com.kynsoft.finamer.settings.domain.rules.manageRegion.ManageRegionCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.manageRegion.ManageRegionCodeSizeRule;
import com.kynsoft.finamer.settings.domain.rules.manageRegion.ManageRegionNameMustBeNullRule;
import com.kynsoft.finamer.settings.domain.services.IManageRegionService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageRegionCommandHandler implements ICommandHandler<CreateManageRegionCommand> {

    private final IManageRegionService service;

    public CreateManageRegionCommandHandler(IManageRegionService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManageRegionCommand command) {
        RulesChecker.checkRule(new ManageRegionCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManageRegionNameMustBeNullRule(command.getName()));
        RulesChecker.checkRule(new ManageRegionCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));

        service.create(new ManageRegionDto(
                command.getId(),
                command.getCode(),
                command.getDescription(),
                command.getStatus(),
                command.getName()
        ));
    }
}
