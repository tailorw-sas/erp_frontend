package com.kynsoft.finamer.invoicing.application.command.manageRoomCategory.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomCategoryDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageRoomCategoryService;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageRoomCategoryCommandHandler implements ICommandHandler<UpdateManageRoomCategoryCommand> {

    private final IManageRoomCategoryService service;

    public UpdateManageRoomCategoryCommandHandler(IManageRoomCategoryService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManageRoomCategoryCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manage Room Category ID cannot be null."));

        ManageRoomCategoryDto dto = service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(), update::setUpdate);


    }


}
