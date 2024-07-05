package com.kynsoft.finamer.invoicing.application.command.manageRoomType.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomTypeDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import com.kynsoft.finamer.invoicing.domain.services.IManageHotelService;
import com.kynsoft.finamer.invoicing.domain.services.IManageRoomTypeService;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Consumer;

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
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manage Room Type ID cannot be null."));

        ManageRoomTypeDto dto = service.findById(command.getId());


        ManageHotelDto hotelDto = hotelService.findById(command.getManageHotel());

        ConsumerUpdate update = new ConsumerUpdate();


        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(), update::setUpdate);


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

    private boolean updateHotel(Consumer<ManageHotelDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManageHotelDto hotelDto = hotelService.findById(newValue);
            setter.accept(hotelDto);
            update.accept(1);

            return true;
        }
        return false;
    }
}
