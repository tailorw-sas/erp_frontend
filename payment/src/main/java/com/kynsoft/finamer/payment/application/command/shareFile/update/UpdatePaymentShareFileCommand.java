package com.kynsoft.finamer.payment.application.command.shareFile.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdatePaymentShareFileCommand implements ICommand {
    private final UUID id;
    private final UUID payment;
    private final String fileName;
    private final String fileUrl;

    public UpdatePaymentShareFileCommand(UUID id, UUID payment, String fileName, String fileUrl) {
        this.id = id;
        this.payment = payment;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
    }


    public static UpdatePaymentShareFileCommand fromRequest(UpdatePaymentShareFileRequest request, UUID id) {
        return new UpdatePaymentShareFileCommand(
                id,
                request.getPayment(),
                request.getFileName(),
                request.getFileUrl()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdatePaymentShareFileMessage(id);
    }
}
