package com.kynsoft.finamer.invoicing.domain.event.createAttachment;

import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class CreateAttachmentEvent extends ApplicationEvent {
    private final UUID invoiceId;
    private final String employee;
    private final UUID employeeId;
    private final String fileName;
    private final String remarks;
    private final byte[] file;
    public CreateAttachmentEvent(Object source, UUID invoiceId, String employee,
                                 UUID employeeId, String fileName,
                                 String remarks, byte[] file) {
        super(source);
        this.invoiceId = invoiceId;
        this.employee = employee;
        this.employeeId = employeeId;
        this.fileName = fileName;
        this.remarks = remarks;
        this.file = file;
    }
}
