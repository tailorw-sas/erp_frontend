package com.kynsof.identity.application.command.module.update;

import com.kynsof.identity.domain.dto.ModuleDto;
import com.kynsof.identity.domain.dto.enumType.ModuleStatus;
import com.kynsof.identity.domain.interfaces.service.IModuleService;
import com.kynsof.identity.domain.rules.module.ModuleNameMustBeUniqueRule;
import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class UpdateModuleCommandHandler implements ICommandHandler<UpdateModuleCommand> {

    private final IModuleService service;

    public UpdateModuleCommandHandler(IModuleService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateModuleCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Module ID cannot be null."));

        ModuleDto module = this.service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(module::setDescription, command.getDescription(), module.getDescription(), update::setUpdate);

        if (UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(module::setName, command.getName(), module.getName(), update::setUpdate)) {
            RulesChecker.checkRule(new ModuleNameMustBeUniqueRule(this.service, command.getName(), command.getId()));
        }

        this.updateStatus(module::setStatus, command.getStatus(), module.getStatus(), update::setUpdate);
       // UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(module::setImage, command.getImage(), module.getImage(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.service.update(module);
        }

    }
    private boolean updateStatus(Consumer<ModuleStatus> setter, ModuleStatus newValue, ModuleStatus oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);

            return true;
        }
        return false;
    }
}
