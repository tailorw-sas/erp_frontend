package com.kynsoft.finamer.insis.application.command.roomRate.updateResponseImportRoomRate;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.insis.application.command.booking.updateResponseBooking.ErrorResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UpdateResponseImportRoomRateCommand implements ICommand {

    private UUID importProcessId;
    private List<RoomRateResponse> responses;
    private Boolean processed;

    public UpdateResponseImportRoomRateCommand(UUID importProcessId,
                                               List<ErrorResponse> errorResponses,
                                               Boolean processed){
        this.importProcessId = importProcessId;
        this.responses = responses;
        this.processed = processed;
    }

    public static UpdateResponseImportRoomRateCommand fromRequest(UpdateResponseImportRoomRateRequest request){
        return new UpdateResponseImportRoomRateCommand(request.getImportProcessId(), request.getErrorResponses(), request.getProcessed());
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateResponseImportRoomRateMessage(importProcessId);
    }
}
