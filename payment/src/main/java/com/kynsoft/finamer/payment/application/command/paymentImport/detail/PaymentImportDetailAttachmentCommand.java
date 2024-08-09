package com.kynsoft.finamer.payment.application.command.paymentImport.detail;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class PaymentImportDetailAttachmentCommand implements ICommand {
    private byte[] file;
    private String fileName;
    private List<UUID> paymentIds;
    private UUID employee;
    private String fileLength;
    @Override
    public ICommandMessage getMessage() {
        return null;
    }
}
