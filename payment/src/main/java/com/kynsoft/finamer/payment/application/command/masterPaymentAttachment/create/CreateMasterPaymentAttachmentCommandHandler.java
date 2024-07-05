package com.kynsoft.finamer.payment.application.command.masterPaymentAttachment.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.AttachmentStatusHistoryDto;
import com.kynsoft.finamer.payment.domain.dto.ManageAttachmentTypeDto;
import com.kynsoft.finamer.payment.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.payment.domain.dto.ManageResourceTypeDto;
import com.kynsoft.finamer.payment.domain.dto.MasterPaymentAttachmentDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.services.IAttachmentStatusHistoryService;
import com.kynsoft.finamer.payment.domain.services.IManageAttachmentTypeService;
import com.kynsoft.finamer.payment.domain.services.IManageEmployeeService;
import com.kynsoft.finamer.payment.domain.services.IManageResourceTypeService;
import com.kynsoft.finamer.payment.domain.services.IMasterPaymentAttachmentService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class CreateMasterPaymentAttachmentCommandHandler implements ICommandHandler<CreateMasterPaymentAttachmentCommand> {

    private final IMasterPaymentAttachmentService masterPaymentAttachmentService;
    private final IPaymentService paymentService;
    private final IManageAttachmentTypeService manageAttachmentTypeService;
    private final IManageResourceTypeService manageResourceTypeService;

    private final IManageEmployeeService manageEmployeeService;
    private final IAttachmentStatusHistoryService attachmentStatusHistoryService;

    public CreateMasterPaymentAttachmentCommandHandler(IMasterPaymentAttachmentService masterPaymentAttachmentService,
            IPaymentService paymentService,
            IManageAttachmentTypeService manageAttachmentTypeService,
            IManageResourceTypeService manageResourceTypeService,
            IManageEmployeeService manageEmployeeService,
            IAttachmentStatusHistoryService attachmentStatusHistoryService) {
        this.masterPaymentAttachmentService = masterPaymentAttachmentService;
        this.paymentService = paymentService;
        this.manageAttachmentTypeService = manageAttachmentTypeService;
        this.manageResourceTypeService = manageResourceTypeService;
        this.manageEmployeeService = manageEmployeeService;
        this.attachmentStatusHistoryService = attachmentStatusHistoryService;
    }

    @Override
    public void handle(CreateMasterPaymentAttachmentCommand command) {

        PaymentDto resource = this.paymentService.findById(command.getResource());
        ManageAttachmentTypeDto manageAttachmentTypeDto = this.manageAttachmentTypeService.findById(command.getAttachmentType());
        ManageResourceTypeDto manageResourceTypeDto = this.manageResourceTypeService.findById(command.getResourceType());

        this.masterPaymentAttachmentService.create(new MasterPaymentAttachmentDto(
                command.getId(),
                command.getStatus(),
                resource,
                manageResourceTypeDto,
                manageAttachmentTypeDto,
                command.getFileName(),
                command.getPath(),
                command.getRemark()
        ));
        this.createAttachmentStatusHistory(null, resource, command.getFileName());
    }

    private void createAttachmentStatusHistory(UUID employee, PaymentDto payment, String fileName) {
        ManageEmployeeDto employeeDto = employee != null ? this.manageEmployeeService.findById(employee) : null;
        AttachmentStatusHistoryDto attachmentStatusHistoryDto = new AttachmentStatusHistoryDto();
        attachmentStatusHistoryDto.setId(UUID.randomUUID());
        attachmentStatusHistoryDto.setDescription("An attachment to the payment was inserted. The file name: " + fileName);
        attachmentStatusHistoryDto.setEmployee(employeeDto);
        attachmentStatusHistoryDto.setPayment(payment);
        attachmentStatusHistoryDto.setStatus(Status.ACTIVE);

        this.attachmentStatusHistoryService.create(attachmentStatusHistoryDto);
    }
}
