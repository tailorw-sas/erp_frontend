package com.kynsoft.finamer.settings.application.command.manageCollectionStatus.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageCollectionStatusDto;
import com.kynsoft.finamer.settings.domain.rules.manageCollectionStatus.ManageCollectionStatusCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.manageCollectionStatus.ManageCollectionStatusCodeSizeRule;
import com.kynsoft.finamer.settings.domain.rules.manageCollectionStatus.ManageCollectionStatusNameMustBeNullRule;
import com.kynsoft.finamer.settings.domain.rules.manageCollectionStatus.ManageCollectionStatusNameMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.services.IManageCollectionStatusService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CreateManageCollectionStatusCommandHandler implements ICommandHandler<CreateManageCollectionStatusCommand> {

    private final IManageCollectionStatusService service;

    public CreateManageCollectionStatusCommandHandler(IManageCollectionStatusService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManageCollectionStatusCommand command) {
        RulesChecker.checkRule(new ManageCollectionStatusCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManageCollectionStatusNameMustBeNullRule(command.getName()));
        RulesChecker.checkRule(new ManageCollectionStatusCodeMustBeUniqueRule(service, command.getCode(), command.getId()));
        RulesChecker.checkRule(new ManageCollectionStatusNameMustBeUniqueRule(service, command.getName(), command.getId()));

        List<ManageCollectionStatusDto> navigate = service.findByIds(command.getNavigate());

        service.create(new ManageCollectionStatusDto(
                command.getId(), command.getCode(), command.getDescription(),
                command.getStatus(), command.getName(), command.getEnabledPayment(),
                command.getIsVisible(), navigate
        ));
    }
}
