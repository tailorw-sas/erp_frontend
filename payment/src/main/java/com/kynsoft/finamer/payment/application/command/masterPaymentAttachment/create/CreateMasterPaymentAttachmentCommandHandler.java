package com.kynsoft.finamer.payment.application.command.masterPaymentAttachment.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsoft.finamer.payment.domain.dto.AttachmentStatusHistoryDto;
import com.kynsoft.finamer.payment.domain.dto.AttachmentTypeDto;
import com.kynsoft.finamer.payment.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.payment.domain.dto.ResourceTypeDto;
import com.kynsoft.finamer.payment.domain.dto.MasterPaymentAttachmentDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.rules.masterPaymentAttachment.MasterPaymetAttachmentWhitDefaultTrueMustBeUniqueRule;
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
        AttachmentTypeDto manageAttachmentTypeDto = this.manageAttachmentTypeService.findById(command.getAttachmentType());
        ResourceTypeDto manageResourceTypeDto = this.manageResourceTypeService.findById(command.getResourceType());

        if(manageAttachmentTypeDto.getDefaults()) {
            RulesChecker.checkRule(new MasterPaymetAttachmentWhitDefaultTrueMustBeUniqueRule(this.masterPaymentAttachmentService, resource.getId()));
        }

        this.updateAttachmentStatusHistory(command.getEmployee(), resource, command.getFileName());
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
//        this.updateAttachmentStatusHistory(command.getEmployee(), resource, command.getFileName());
    }

    private void updateAttachmentStatusHistory(UUID employee, PaymentDto payment, String fileName) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(employee, "id", "Employee ID cannot be null."));

        ManageEmployeeDto employeeDto = this.manageEmployeeService.findById(employee);

        AttachmentStatusHistoryDto attachmentStatusHistoryDto = new AttachmentStatusHistoryDto();
        attachmentStatusHistoryDto.setId(UUID.randomUUID());
        attachmentStatusHistoryDto.setDescription("An attachment to the payment was inserted. The file name: " + fileName);
        attachmentStatusHistoryDto.setEmployee(employeeDto);
        attachmentStatusHistoryDto.setPayment(payment);
        attachmentStatusHistoryDto.setStatus(Status.ACTIVE);

        this.attachmentStatusHistoryService.create(attachmentStatusHistoryDto);
    }
}
