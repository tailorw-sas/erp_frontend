package com.kynsoft.finamer.insis.application.command.booking.updateResponseBooking;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UpdateResponseImportBookingCommand implements ICommand {

    private UUID importProcessId;
    private List<ErrorResponse> errorResponses;

    public UpdateResponseImportBookingCommand(UUID importProcessId, List<ErrorResponse> errorResponses){
        this.importProcessId = importProcessId;
        this.errorResponses = errorResponses;
    }

    public static UpdateResponseImportBookingCommand fromRequest(UpdateResponseImportBookingRequest request){
        return new UpdateResponseImportBookingCommand(request.getImportProcessId(), request.getErrorResponses());
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateResponseImportBookingMessage(importProcessId);
    }
}
