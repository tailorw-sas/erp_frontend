package com.kynsoft.finamer.payment.infrastructure.excel.event.createAttachment;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;
import java.util.UUID;

@Getter
public class CreateAttachmentEvent extends ApplicationEvent {
    private final UUID paymentIds;
    private final byte[] file;
    private final UUID employeeId;
    private final String fileName;
    private final String fileSize;
    public CreateAttachmentEvent(Object source, UUID paymentIds, byte[] file, UUID employeeId, String fileName, String fileSize) {
        super(source);
        this.paymentIds = paymentIds;
        this.file = file;
        this.employeeId = employeeId;
        this.fileName = fileName;
        this.fileSize = fileSize;
    }
}
