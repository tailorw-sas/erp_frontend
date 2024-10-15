package com.kynsoft.finamer.payment.application.command.shareFile.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreatePaymentShareFileCommand implements ICommand {

    private final UUID id;
    private final UUID paymentId;
    private final String fileName;
    private final String fileUrl;

    public CreatePaymentShareFileCommand( UUID payment, String fileName, String fileUrl) {
        this.id = UUID.randomUUID();
        this.paymentId = payment;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
    }


    public static CreatePaymentShareFileCommand fromRequest(CreatePaymentShareFileRequest request) {
        return new CreatePaymentShareFileCommand(request.getPaymentId(),  request.getFileName(), request.getFileUrl());
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreatePaymentShareFileMessage(id);
    }
}
