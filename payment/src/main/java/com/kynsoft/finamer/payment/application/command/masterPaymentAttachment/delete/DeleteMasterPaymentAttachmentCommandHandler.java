package com.kynsoft.finamer.payment.application.command.masterPaymentAttachment.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.AttachmentStatusHistoryDto;
import com.kynsoft.finamer.payment.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.payment.domain.dto.MasterPaymentAttachmentDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.services.IAttachmentStatusHistoryService;
import com.kynsoft.finamer.payment.domain.services.IManageEmployeeService;
import com.kynsoft.finamer.payment.domain.services.IMasterPaymentAttachmentService;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class DeleteMasterPaymentAttachmentCommandHandler implements ICommandHandler<DeleteMasterPaymentAttachmentCommand> {

    private final IMasterPaymentAttachmentService masterPaymentAttachmentService;

    private final IManageEmployeeService manageEmployeeService;
    private final IAttachmentStatusHistoryService attachmentStatusHistoryService;

    public DeleteMasterPaymentAttachmentCommandHandler(IMasterPaymentAttachmentService masterPaymentAttachmentService,
                                                       IManageEmployeeService manageEmployeeService,
                                                       IAttachmentStatusHistoryService attachmentStatusHistoryService) {
        this.masterPaymentAttachmentService = masterPaymentAttachmentService;
        this.manageEmployeeService = manageEmployeeService;
        this.attachmentStatusHistoryService = attachmentStatusHistoryService;
    }

    @Override
    public void handle(DeleteMasterPaymentAttachmentCommand command) {
        MasterPaymentAttachmentDto delete = this.masterPaymentAttachmentService.findById(command.getId());

        masterPaymentAttachmentService.delete(delete);
        this.deleteAttachmentStatusHistory(null, delete.getResource(), delete.getFileName());
    }

    private void deleteAttachmentStatusHistory(UUID employee, PaymentDto payment, String fileName) {
        ManageEmployeeDto employeeDto = employee != null ? this.manageEmployeeService.findById(employee) : null;
        AttachmentStatusHistoryDto attachmentStatusHistoryDto = new AttachmentStatusHistoryDto();
        attachmentStatusHistoryDto.setId(UUID.randomUUID());
        attachmentStatusHistoryDto.setDescription("An attachment to the payment was deleted. The file name: " + fileName);
        attachmentStatusHistoryDto.setEmployee(employeeDto);
        attachmentStatusHistoryDto.setPayment(payment);
        attachmentStatusHistoryDto.setStatus(Status.ACTIVE);
        attachmentStatusHistoryDto.setUpdatedAt(LocalDateTime.now());

        this.attachmentStatusHistoryService.create(attachmentStatusHistoryDto);
    }
}
