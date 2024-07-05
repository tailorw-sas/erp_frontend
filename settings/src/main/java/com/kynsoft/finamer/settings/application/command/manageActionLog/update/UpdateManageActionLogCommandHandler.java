package com.kynsoft.finamer.settings.application.command.manageActionLog.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManageActionLogDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManageActionLogService;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class UpdateManageActionLogCommandHandler implements ICommandHandler<UpdateManageActionLogCommand> {

    private final IManageActionLogService service;

    public UpdateManageActionLogCommandHandler(IManageActionLogService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManageActionLogCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manage Action Log ID cannot be null."));

        ManageActionLogDto actionLogDto = service.findById(command.getId());
        ConsumerUpdate update = new ConsumerUpdate();

        updateFields(actionLogDto, command, update);

        if (update.getUpdate() > 0) {
            service.update(actionLogDto);
        }
    }

    private void updateFields(ManageActionLogDto actionLogDto, UpdateManageActionLogCommand command, ConsumerUpdate update) {
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(actionLogDto::setDescription, command.getDescription(), actionLogDto.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(actionLogDto::setName, command.getName(), actionLogDto.getName(), update::setUpdate);
        updateStatus(actionLogDto::setStatus, command.getStatus(), actionLogDto.getStatus(), update::setUpdate);
    }

    private void updateStatus(Consumer<Status> setter, Status newValue, Status oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);
        }
    }
}