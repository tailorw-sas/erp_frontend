package com.kynsoft.finamer.invoicing.application.command.manageInvoice.updateSendStatus;

import com.kynsof.share.core.domain.response.UploadFileResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSendStatusRequest {
    private Map<UUID, UploadFileResponse> invoiceResponses;
}
