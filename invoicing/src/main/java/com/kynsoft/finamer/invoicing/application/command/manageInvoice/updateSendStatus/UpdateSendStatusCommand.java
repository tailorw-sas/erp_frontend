package com.kynsoft.finamer.invoicing.application.command.manageInvoice.updateSendStatus;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsof.share.core.domain.response.UploadFileResponse;
import lombok.*;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSendStatusCommand implements ICommand {
    private Map<UUID, UploadFileResponse> invoiceResponses;

    public static UpdateSendStatusCommand fromRequest(UpdateSendStatusRequest request) {
        return UpdateSendStatusCommand.builder()
                .invoiceResponses(request.getInvoiceResponses())
                .build();
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateSendStatusMessage();
    }
}
