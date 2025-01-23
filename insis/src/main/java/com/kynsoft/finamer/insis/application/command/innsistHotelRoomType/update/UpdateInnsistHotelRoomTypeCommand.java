package com.kynsoft.finamer.insis.application.command.innsistHotelRoomType.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateInnsistHotelRoomTypeCommand implements ICommand {

    private UUID id;
    public UUID hotel;
    private String roomTypePrefix;
    private String status;

    public UpdateInnsistHotelRoomTypeCommand(UUID id, UUID hotel, String roomTypePrefix, String status){
        this.id = id;
        this.hotel = hotel;
        this.roomTypePrefix = roomTypePrefix;
        this.status = status;
    }

    public static UpdateInnsistHotelRoomTypeCommand fromRequest(UpdateInnsistHotelRoomTypeRequest request){
        return new UpdateInnsistHotelRoomTypeCommand(request.getId(),
                request.getHotel(),
                request.getRoomTypePrefix(),
                request.getStatus());
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateInnsistHotelRoomTypeMessage(id);
    }
}
