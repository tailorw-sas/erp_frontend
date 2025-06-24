package com.kynsoft.finamer.insis.application.command.roomRate.importRoomRate;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ImportRoomRateCommand implements ICommand {

    public UUID id;
    public UUID userId;
    public List<UUID> roomRates;

    public ImportRoomRateCommand(UUID id, UUID userId, List<UUID> roomRates){
        this.id = id;
        this.userId = userId;
        this.roomRates = roomRates;
    }

    public static ImportRoomRateCommand fromRequest(ImportRoomRateRequest request){
        return new ImportRoomRateCommand(request.id,
                request.getUserId(),
                request.getRoomRates());
    }

    @Override
    public ICommandMessage getMessage() {
        return new ImportRoomRateMessage(id);
    }
}
