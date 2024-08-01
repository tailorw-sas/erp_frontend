package com.kynsoft.finamer.invoicing.application.command.manageAttachment.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateAttachmentCommand implements ICommand {

    private UUID id;

    private String filename;
    private String file;
    private String remark;
    private UUID type;
    private UUID invoice;
    private String employee;
    private UUID employeeId;
    private UUID paymentResourceType;

    public CreateAttachmentCommand(

            String filename,
            String file,
            String remark,
            UUID type,
            UUID invoice ,
         String employee,
     UUID employeeId,
     UUID paymentResourceType
    ) {
        this.id = UUID.randomUUID();

        this.file = file;
        this.filename = filename;
        this.remark = remark;
        this.type = type;
        this.invoice = invoice;
        this.employee = employee;
        this.employeeId =employeeId;
        this.paymentResourceType = paymentResourceType;
    }

    public static CreateAttachmentCommand fromRequest(CreateAttachmentRequest request) {
        return new CreateAttachmentCommand(
                request.getFilename(), request.getFile(), request.getRemark(), request.getType(), request.getInvoice(), request.getEmployee(), request.getEmployeeId(), request.getPaymentResourceType());
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateAttachmentMessage(id);
    }
}
