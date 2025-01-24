package com.kynsoft.finamer.insis.application.command.manageRoomType.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.insis.application.command.manageRoomType.create.CreateRoomTypeMessage;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class UpdateRoomTypeCommand implements ICommand {

    private UUID id;
    private String name;
    private String status;
    private UUID hotelId;
    private LocalDateTime updatedAt;

    public UpdateRoomTypeCommand(UUID id, String name, String status, UUID hotelId){
        this.id = id;
        this.name = name;
        this.status = status;
        this.hotelId = hotelId;
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateRoomTypeMessage(id);
    }
}
