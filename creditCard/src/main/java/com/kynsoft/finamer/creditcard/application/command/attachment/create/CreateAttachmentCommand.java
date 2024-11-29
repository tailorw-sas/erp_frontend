package com.kynsoft.finamer.creditcard.application.command.attachment.create;

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
    private Long transaction;
    private String employee;
    private UUID employeeId;
    private UUID paymentResourceType;
    private UUID hotelPayment;

    public CreateAttachmentCommand(
            String filename, String file, String remark, UUID type, Long transaction,
            String employee, UUID employeeId, UUID paymentResourceType, UUID hotelPayment) {

        this.id = UUID.randomUUID();
        this.file = file;
        this.filename = filename;
        this.remark = remark;
        this.type = type;
        this.transaction = transaction;
        this.employee = employee;
        this.employeeId =employeeId;
        this.paymentResourceType = paymentResourceType;
        this.hotelPayment = hotelPayment;
    }

    public static CreateAttachmentCommand fromRequest(CreateAttachmentRequest request) {
        return new CreateAttachmentCommand(
                request.getFilename(), request.getFile(), request.getRemark(),
                request.getType(), request.getTransaction(), request.getEmployee(),
                request.getEmployeeId(), request.getPaymentResourceType(), request.getHotelPayment());
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateAttachmentMessage(id);
    }
}
