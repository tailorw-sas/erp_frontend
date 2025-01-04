package com.kynsoft.finamer.invoicing.application.command.manageRoomType.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomTypeDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageHotelService;
import com.kynsoft.finamer.invoicing.domain.services.IManageRoomTypeService;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageRoomTypeCommandHandler implements ICommandHandler<UpdateManageRoomTypeCommand> {

    private final IManageRoomTypeService service;

    private final IManageHotelService hotelService;

    public UpdateManageRoomTypeCommandHandler(IManageRoomTypeService service, IManageHotelService hotelService) {
        this.service = service;
        this.hotelService = hotelService;
    }

    @Override
    public void handle(UpdateManageRoomTypeCommand command) {
        RulesChecker.checkRule(
                new ValidateObjectNotNullRule<>(command.getId(), "id", "Manage Room Type ID cannot be null."));

        ManageRoomTypeDto dto = service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(),
                update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setStatus, command.getStatus(), dto.getStatus(),
                update::setUpdate);

        if (update.getUpdate() > 0) {
            this.service.update(dto);
        }
    }

}
