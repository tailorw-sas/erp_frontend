package com.kynsoft.finamer.insis.application.command.innsistHotelRoomType.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateInnsistHotelRoomTypeCommand implements ICommand {
    private UUID id;
    public UUID hotel;
    private String roomTypePrefix;
    private String status;

    public CreateInnsistHotelRoomTypeCommand(UUID hotel, String roomTypePrefix, String status){
        this.id = UUID.randomUUID();
        this.hotel = hotel;
        this.roomTypePrefix = roomTypePrefix;
        this.status = status;
    }

    public static CreateInnsistHotelRoomTypeCommand fromRequest(CreateInnsistHotelRoomTypeRequest request){
        return new CreateInnsistHotelRoomTypeCommand(
                request.hotel,
                request.getRoomTypePrefix(),
                request.getStatus()
        );
    }
    @Override
    public ICommandMessage getMessage() {
        return new CreateInnsistHotelRoomTypeMessage(id);
    }
}
