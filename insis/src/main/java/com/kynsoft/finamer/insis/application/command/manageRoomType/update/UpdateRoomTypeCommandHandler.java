package com.kynsoft.finamer.insis.application.command.manageRoomType.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.insis.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.insis.domain.dto.ManageRoomTypeDto;
import com.kynsoft.finamer.insis.domain.services.IManageHotelService;
import com.kynsoft.finamer.insis.domain.services.IManageRoomTypeService;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Consumer;

@Component
public class UpdateRoomTypeCommandHandler implements ICommandHandler<UpdateRoomTypeCommand> {

    private final IManageRoomTypeService service;
    private final IManageHotelService hotelService;

    public UpdateRoomTypeCommandHandler(IManageRoomTypeService service,
                                        IManageHotelService hotelService){
        this.service = service;
        this.hotelService = hotelService;
    }

    @Override
    public void handle(UpdateRoomTypeCommand command) {
        ManageRoomTypeDto dto = service.findById(command.getId());
        ManageHotelDto hotelDto = hotelService.findById(command.getHotelId());
        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setStatus, command.getStatus(), dto.getStatus(), update::setUpdate);
        dto.setUpdatedAt(command.getUpdatedAt());
        updateHotel(dto::setHotel, command.getHotelId(), hotelDto.getId(), update::setUpdate);

        service.update(dto);
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
