package com.kynsoft.finamer.payment.application.command.payment.changeAttachmentStatus;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.AttachmentStatusHistoryDto;
import com.kynsoft.finamer.payment.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.payment.domain.dto.MasterPaymentAttachmentDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.services.IAttachmentStatusHistoryService;
import com.kynsoft.finamer.payment.domain.services.IManageEmployeeService;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentAttachmentStatusService;
import com.kynsoft.finamer.payment.domain.services.IMasterPaymentAttachmentService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ChangeAttachmentStatusCommandHandler implements ICommandHandler<ChangeAttachmentStatusCommand> {

    private final IPaymentService paymentService;
    private final IManagePaymentAttachmentStatusService paymentAttachmentStatusService;
    private final IManageEmployeeService employeeService;
    private final IMasterPaymentAttachmentService masterPaymentAttachmentService;
    private final IAttachmentStatusHistoryService attachmentStatusHistoryService;

    public ChangeAttachmentStatusCommandHandler(IPaymentService paymentService, 
                                                IManagePaymentAttachmentStatusService paymentAttachmentStatusService,
                                                IManageEmployeeService employeeService,
                                                IMasterPaymentAttachmentService masterPaymentAttachmentService,
                                                IAttachmentStatusHistoryService attachmentStatusHistoryService) {
        this.paymentService = paymentService;
        this.paymentAttachmentStatusService = paymentAttachmentStatusService;
        this.employeeService = employeeService;
        this.masterPaymentAttachmentService = masterPaymentAttachmentService;
        this.attachmentStatusHistoryService = attachmentStatusHistoryService;
    }

    @Override
    public void handle(ChangeAttachmentStatusCommand command) {

        PaymentDto paymentDto = this.paymentService.findById(command.getPayment());
        ManageEmployeeDto employeeDto = this.employeeService.findById(command.getEmployee());

        switch (command.getAttachmentStatus()) {
            case ATTACHMENT_WITH_ERROR -> {
                paymentDto.setEAttachment(command.getAttachmentStatus());
                paymentDto.setAttachmentStatus(this.paymentAttachmentStatusService.findByPwaWithOutAttachment());
                this.createAttachmentStatusHistory(employeeDto, paymentDto);
            }
            case ATTACHMENT_WITHOUT_ERROR -> {
                paymentDto.setEAttachment(command.getAttachmentStatus());
                paymentDto.setAttachmentStatus(this.paymentAttachmentStatusService.findByPatWithAttachment());
                this.createAttachmentStatusHistory(employeeDto, paymentDto);
            }
            case NONE -> {
                paymentDto.setEAttachment(command.getAttachmentStatus());
                paymentDto.setAttachmentStatus(this.paymentAttachmentStatusService.findByNonNone());
                this.createAttachmentStatusHistory(employeeDto, paymentDto);
            }
            default -> {
            }
        }

        this.paymentService.update(paymentDto);
        command.setPaymentResponse(paymentDto);
    }

    
    //Este metodo es para agregar el history del Attachemnt. Aqui el estado es el del nomenclador Manage Payment Attachment Status
    private void createAttachmentStatusHistory(ManageEmployeeDto employeeDto, PaymentDto payment) {
        List<MasterPaymentAttachmentDto> list = masterPaymentAttachmentService.findAllByPayment(payment.getId());
        for (MasterPaymentAttachmentDto attachment : list) {

            AttachmentStatusHistoryDto attachmentStatusHistoryDto = new AttachmentStatusHistoryDto();
            attachmentStatusHistoryDto.setId(UUID.randomUUID());
            attachmentStatusHistoryDto.setDescription("An attachment to the payment was inserted. The file name: " + attachment.getFileName());
            attachmentStatusHistoryDto.setEmployee(employeeDto);
            attachmentStatusHistoryDto.setPayment(payment);
            attachmentStatusHistoryDto.setStatus(payment.getAttachmentStatus().getCode() + "-" + payment.getAttachmentStatus().getName());
            attachmentStatusHistoryDto.setAttachmentId(attachment.getAttachmentId());

            this.attachmentStatusHistoryService.create(attachmentStatusHistoryDto);

        }
    }
}
