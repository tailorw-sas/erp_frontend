package com.kynsoft.finamer.payment.application.command.masterPaymentAttachment.delete;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsoft.finamer.payment.domain.dto.AttachmentStatusHistoryDto;
import com.kynsoft.finamer.payment.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.payment.domain.dto.MasterPaymentAttachmentDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.services.IAttachmentStatusHistoryService;
import com.kynsoft.finamer.payment.domain.services.IManageEmployeeService;
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

    public DeleteMasterPaymentAttachmentCommandHandler(IMasterPaymentAttachmentService masterPaymentAttachmentService,
            IManageEmployeeService manageEmployeeService,
            IAttachmentStatusHistoryService attachmentStatusHistoryService,
            IPaymentStatusHistoryService paymentAttachmentStatusHistoryService,
            IPaymentService paymentService) {
        this.masterPaymentAttachmentService = masterPaymentAttachmentService;
        this.manageEmployeeService = manageEmployeeService;
        this.attachmentStatusHistoryService = attachmentStatusHistoryService;
        this.paymentAttachmentStatusHistoryService = paymentAttachmentStatusHistoryService;
        this.paymentService = paymentService;
    }

    @Override
    public void handle(DeleteMasterPaymentAttachmentCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getEmployee(), "id", "Employee ID cannot be null."));

        ManageEmployeeDto employeeDto = this.manageEmployeeService.findById(command.getEmployee());

        MasterPaymentAttachmentDto delete = this.masterPaymentAttachmentService.findById(command.getId());

        masterPaymentAttachmentService.delete(delete);
        if (this.masterPaymentAttachmentService.countByResourceAndAttachmentTypeIsDefault(delete.getResource().getId()) == 0) {
            PaymentDto paymentDto = delete.getResource();
            paymentDto.setPaymentSupport(false);
            this.paymentService.update(paymentDto);
        }
        deleteAttachmentStatusHistory(employeeDto, delete.getResource(), delete.getFileName(), delete.getAttachmentId());
//        createPaymentAttachmentStatusHistory(employeeDto, delete);
    }

    //Este es para agregar el History del Payment. Aqui el estado es el del nomenclador Manage Payment Status
//    private void createPaymentAttachmentStatusHistory(ManageEmployeeDto employeeDto, MasterPaymentAttachmentDto delete) {
//
//        PaymentStatusHistoryDto attachmentStatusHistoryDto = new PaymentStatusHistoryDto();
//        attachmentStatusHistoryDto.setId(UUID.randomUUID());
//        attachmentStatusHistoryDto.setDescription("An attachment to the payment was deleted. The file name: " + delete.getFileName());
//        attachmentStatusHistoryDto.setEmployee(employeeDto);
//        attachmentStatusHistoryDto.setPayment(delete.getResource());
//        attachmentStatusHistoryDto.setStatus(delete.getResource().getPaymentStatus().getCode() + "-" + delete.getResource().getPaymentStatus().getName());
//
//        this.paymentAttachmentStatusHistoryService.create(attachmentStatusHistoryDto);
//    }
    private void deleteAttachmentStatusHistory(ManageEmployeeDto employeeDto, PaymentDto payment, String fileName, Long attachmentId) {
        //ManageEmployeeDto employeeDto = employee != null ? this.manageEmployeeService.findById(employee) : null;
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
