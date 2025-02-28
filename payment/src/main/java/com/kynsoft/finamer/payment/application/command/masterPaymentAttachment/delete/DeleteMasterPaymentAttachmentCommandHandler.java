package com.kynsoft.finamer.payment.application.command.masterPaymentAttachment.delete;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsoft.finamer.payment.domain.dto.AttachmentStatusHistoryDto;
import com.kynsoft.finamer.payment.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentAttachmentStatusDto;
import com.kynsoft.finamer.payment.domain.dto.MasterPaymentAttachmentDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.services.IAttachmentStatusHistoryService;
import com.kynsoft.finamer.payment.domain.services.IManageEmployeeService;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentAttachmentStatusService;
import com.kynsoft.finamer.payment.domain.services.IMasterPaymentAttachmentService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import com.kynsoft.finamer.payment.domain.services.IPaymentStatusHistoryService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class DeleteMasterPaymentAttachmentCommandHandler implements ICommandHandler<DeleteMasterPaymentAttachmentCommand> {

    private final IMasterPaymentAttachmentService masterPaymentAttachmentService;

    private final IManageEmployeeService manageEmployeeService;
    private final IAttachmentStatusHistoryService attachmentStatusHistoryService;

    private final IPaymentStatusHistoryService paymentAttachmentStatusHistoryService;
    private final IPaymentService paymentService;

    private final IManagePaymentAttachmentStatusService attachmentStatusService;

    public DeleteMasterPaymentAttachmentCommandHandler(IMasterPaymentAttachmentService masterPaymentAttachmentService,
            IManageEmployeeService manageEmployeeService,
            IAttachmentStatusHistoryService attachmentStatusHistoryService,
            IPaymentStatusHistoryService paymentAttachmentStatusHistoryService,
            IPaymentService paymentService,
            IManagePaymentAttachmentStatusService attachmentStatusService) {
        this.masterPaymentAttachmentService = masterPaymentAttachmentService;
        this.manageEmployeeService = manageEmployeeService;
        this.attachmentStatusHistoryService = attachmentStatusHistoryService;
        this.paymentAttachmentStatusHistoryService = paymentAttachmentStatusHistoryService;
        this.paymentService = paymentService;
        this.attachmentStatusService = attachmentStatusService;
    }

    @Override
    public void handle(DeleteMasterPaymentAttachmentCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getEmployee(), "id", "Employee ID cannot be null."));

        ManageEmployeeDto employeeDto = this.manageEmployeeService.findById(command.getEmployee());

        MasterPaymentAttachmentDto delete = this.masterPaymentAttachmentService.findById(command.getId());
        long attachmentSize = this.masterPaymentAttachmentService.countByAttachmentResource(delete.getResource().getId());

        masterPaymentAttachmentService.delete(delete);
        if (this.masterPaymentAttachmentService.countByResourceAndAttachmentTypeIsDefault(delete.getResource().getId()) == 0) {
            PaymentDto paymentDto = delete.getResource();
            paymentDto.setPaymentSupport(false);
            if (attachmentSize == 1) {
                ManagePaymentAttachmentStatusDto attachmentStatusDto = this.attachmentStatusService.findByNonNone();
                paymentDto.setHasAttachment(false);
                paymentDto.setAttachmentStatus(attachmentStatusDto);
            }

            this.paymentService.update(paymentDto);
        } else if (attachmentSize == 1) {
            PaymentDto paymentDto = delete.getResource();
            ManagePaymentAttachmentStatusDto attachmentStatusDto = this.attachmentStatusService.findByNonNone();
            paymentDto.setAttachmentStatus(attachmentStatusDto);
            paymentDto.setHasAttachment(false);
            this.paymentService.update(paymentDto);
        }

        deleteAttachmentStatusHistory(employeeDto, delete.getResource(), delete.getFileName(), delete.getAttachmentId());
    }

    private void deleteAttachmentStatusHistory(ManageEmployeeDto employeeDto, PaymentDto payment, String fileName, Long attachmentId) {
        AttachmentStatusHistoryDto attachmentStatusHistoryDto = new AttachmentStatusHistoryDto();

        attachmentStatusHistoryDto.setId(UUID.randomUUID());
        attachmentStatusHistoryDto.setDescription("An attachment to the payment was deleted. The file name: " + fileName);
        attachmentStatusHistoryDto.setEmployee(employeeDto);
        attachmentStatusHistoryDto.setPayment(payment);
        attachmentStatusHistoryDto.setStatus(payment.getAttachmentStatus().getCode() + "-" + payment.getAttachmentStatus().getName());
        attachmentStatusHistoryDto.setUpdatedAt(LocalDateTime.now());
        attachmentStatusHistoryDto.setAttachmentId(attachmentId);

        this.attachmentStatusHistoryService.create(attachmentStatusHistoryDto);
    }
}
