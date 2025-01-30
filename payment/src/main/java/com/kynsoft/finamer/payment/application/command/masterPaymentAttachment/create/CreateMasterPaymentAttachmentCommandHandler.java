package com.kynsoft.finamer.payment.application.command.masterPaymentAttachment.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.rules.masterPaymentAttachment.MasterPaymetAttachmentWhitDefaultTrueMustBeUniqueRule;
import com.kynsoft.finamer.payment.domain.services.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CreateMasterPaymentAttachmentCommandHandler implements ICommandHandler<CreateMasterPaymentAttachmentCommand> {

    private final IMasterPaymentAttachmentService masterPaymentAttachmentService;
    private final IPaymentService paymentService;
    private final IManageAttachmentTypeService manageAttachmentTypeService;
    private final IManageResourceTypeService manageResourceTypeService;

    private final IManageEmployeeService manageEmployeeService;
    private final IAttachmentStatusHistoryService attachmentStatusHistoryService;

    private final IPaymentStatusHistoryService paymentAttachmentStatusHistoryService;
    private final IManagePaymentAttachmentStatusService attachmentStatusService;

    public CreateMasterPaymentAttachmentCommandHandler(IMasterPaymentAttachmentService masterPaymentAttachmentService,
            IPaymentService paymentService,
            IManageAttachmentTypeService manageAttachmentTypeService,
            IManageResourceTypeService manageResourceTypeService,
            IManageEmployeeService manageEmployeeService,
            IAttachmentStatusHistoryService attachmentStatusHistoryService,
            IPaymentStatusHistoryService paymentAttachmentStatusHistoryService,
            IManagePaymentAttachmentStatusService attachmentStatusService) {
        this.masterPaymentAttachmentService = masterPaymentAttachmentService;
        this.paymentService = paymentService;
        this.manageAttachmentTypeService = manageAttachmentTypeService;
        this.manageResourceTypeService = manageResourceTypeService;
        this.manageEmployeeService = manageEmployeeService;
        this.attachmentStatusHistoryService = attachmentStatusHistoryService;
        this.paymentAttachmentStatusHistoryService = paymentAttachmentStatusHistoryService;
        this.attachmentStatusService = attachmentStatusService;
    }

    @Override
    public void handle(CreateMasterPaymentAttachmentCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getEmployee(), "id", "Employee ID cannot be null."));

        ManageEmployeeDto employeeDto = this.manageEmployeeService.findById(command.getEmployee());
        PaymentDto resource = this.paymentService.findById(command.getResource());
        AttachmentTypeDto manageAttachmentTypeDto = this.manageAttachmentTypeService.findById(command.getAttachmentType());
        ResourceTypeDto manageResourceTypeDto = this.manageResourceTypeService.findById(command.getResourceType());

        boolean paymentSupport = false;
        if (manageAttachmentTypeDto.getDefaults()) {
            RulesChecker.checkRule(new MasterPaymetAttachmentWhitDefaultTrueMustBeUniqueRule(this.masterPaymentAttachmentService, resource.getId(), command.getFileName()));
            paymentSupport = true;
        }

        Long attachmentId = this.masterPaymentAttachmentService.create(new MasterPaymentAttachmentDto(
                command.getId(),
                command.getStatus() != null ? command.getStatus() : Status.ACTIVE,
                resource,
                manageResourceTypeDto,
                manageAttachmentTypeDto,
                command.getFileName(),
                command.getFileWeight(),
                command.getPath(),
                command.getRemark(),
                0L
        ));

        String statusHistory = resource.getAttachmentStatus().getCode() + "-" + resource.getAttachmentStatus().getName();
        if (paymentSupport) {
            resource.setPaymentSupport(true);// Si paso la regla es porque no tenia Payment Support agregados.
            ManagePaymentAttachmentStatusDto attachmentStatusSupport = this.attachmentStatusService.findBySupported();
            resource.setAttachmentStatus(attachmentStatusSupport);
            statusHistory = attachmentStatusSupport.getCode() + "-" + attachmentStatusSupport.getName();
            resource.setHasAttachment(true);
            this.paymentService.update(resource);
        } else {
            ManagePaymentAttachmentStatusDto attachmentOtherSupport = this.attachmentStatusService.findByOtherSupported();
            resource.setAttachmentStatus(attachmentOtherSupport);
            statusHistory = attachmentOtherSupport.getCode() + "-" + attachmentOtherSupport.getName();
            resource.setHasAttachment(true);
            this.paymentService.update(resource);
        }
        this.updateAttachmentStatusHistory(employeeDto, resource, command.getFileName(), attachmentId, statusHistory);
//        this.createPaymentAttachmentStatusHistory(employeeDto, resource, command.getFileName());
    }

    //Este es para agregar el History del Payment. Aqui el estado es el del nomenclador Manage Payment Status
//    private void createPaymentAttachmentStatusHistory(ManageEmployeeDto employeeDto, PaymentDto payment, String fileName) {
//
//        PaymentStatusHistoryDto attachmentStatusHistoryDto = new PaymentStatusHistoryDto();
//        attachmentStatusHistoryDto.setId(UUID.randomUUID());
//        attachmentStatusHistoryDto.setDescription("An attachment to the payment was created. The file name: " + fileName);
//        attachmentStatusHistoryDto.setEmployee(employeeDto);
//        attachmentStatusHistoryDto.setPayment(payment);
//        attachmentStatusHistoryDto.setStatus(payment.getPaymentStatus().getCode() + "-" + payment.getPaymentStatus().getName());
//
//        this.paymentAttachmentStatusHistoryService.create(attachmentStatusHistoryDto);
//    }

    private void updateAttachmentStatusHistory(ManageEmployeeDto employeeDto, PaymentDto payment, String fileName, Long attachmentId, String statusHistory) {

        AttachmentStatusHistoryDto attachmentStatusHistoryDto = new AttachmentStatusHistoryDto();
        attachmentStatusHistoryDto.setId(UUID.randomUUID());
        attachmentStatusHistoryDto.setDescription("An attachment to the payment was inserted. The file name: " + fileName);
        attachmentStatusHistoryDto.setEmployee(employeeDto);
        attachmentStatusHistoryDto.setPayment(payment);
        attachmentStatusHistoryDto.setStatus(statusHistory);
        //attachmentStatusHistoryDto.setStatus(payment.getAttachmentStatus().getCode() + "-" + payment.getAttachmentStatus().getName());
        attachmentStatusHistoryDto.setAttachmentId(attachmentId);

        this.attachmentStatusHistoryService.create(attachmentStatusHistoryDto);
    }
}
