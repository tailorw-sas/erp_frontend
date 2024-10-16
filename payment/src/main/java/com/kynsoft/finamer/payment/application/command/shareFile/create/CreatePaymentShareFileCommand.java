package com.kynsoft.finamer.payment.application.command.shareFile.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreatePaymentShareFileCommand implements ICommand {

    private final UUID id;
    private final UUID paymentId;
    private final String fileName;
    private final byte[] fileData;

    public CreatePaymentShareFileCommand( UUID payment, String fileName, byte[] fileData) {
        this.id = UUID.randomUUID();
        this.paymentId = payment;
        this.fileName = fileName;
        this.fileData = fileData;
    }


    public static CreatePaymentShareFileCommand fromRequest(CreatePaymentShareFileRequest request) {
        return new CreatePaymentShareFileCommand(request.getPaymentId(),  request.getFileName(), request.getFile());
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreatePaymentShareFileMessage(id);
    }
}
