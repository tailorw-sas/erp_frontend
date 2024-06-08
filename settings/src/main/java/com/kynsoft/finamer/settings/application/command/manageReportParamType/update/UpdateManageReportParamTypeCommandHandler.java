package com.kynsoft.finamer.settings.application.command.manageReportParamType.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManageReportParamTypeDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.rules.manageReportParamType.ManageReportParamTypeNameMustBeNullRule;
import com.kynsoft.finamer.settings.domain.rules.manageReportParamType.ManageReportParamTypeNameMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.services.IManageReportParamTypeService;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class UpdateManageReportParamTypeCommandHandler implements ICommandHandler<UpdateManageReportParamTypeCommand> {

    private final IManageReportParamTypeService service;

    public UpdateManageReportParamTypeCommandHandler(IManageReportParamTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManageReportParamTypeCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manage Invoice Transaction Type ID cannot be null."));
        RulesChecker.checkRule(new ManageReportParamTypeNameMustBeUniqueRule(service, command.getName(), command.getId()));
        RulesChecker.checkRule(new ManageReportParamTypeNameMustBeNullRule(command.getName()));

        ManageReportParamTypeDto dto = service.findById(command.getId());
        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setLabel, command.getLabel(), dto.getLabel(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setSource, command.getSource(), dto.getSource(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setHotel, command.getHotel(), dto.getHotel(), update::setUpdate);
        updateStatus(dto::setStatus, command.getStatus(), dto.getStatus(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.service.update(dto);
        }
    }

    private boolean updateStatus(Consumer<Status> setter, Status newValue, Status oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);

            return true;
        }
        return false;
    }
}
