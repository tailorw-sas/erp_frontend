package com.kynsoft.finamer.payment.application.command.masterPaymentAttachment.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.rules.masterPaymentAttachment.MasterPaymetAttachmentWhitDefaultTrueMustBeUniqueRule;
import com.kynsoft.finamer.payment.domain.services.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Consumer;

@Component
public class UpdateMasterPaymentAttachmentCommandHandler implements ICommandHandler<UpdateMasterPaymentAttachmentCommand> {

    private final IMasterPaymentAttachmentService masterPaymentAttachmentService;
    private final IManageAttachmentTypeService manageAttachmentTypeService;
    private final IManageResourceTypeService manageResourceTypeService;

    private final IManageEmployeeService manageEmployeeService;
    private final IAttachmentStatusHistoryService attachmentStatusHistoryService;

    private final IPaymentStatusHistoryService paymentAttachmentStatusHistoryService;
    private final IPaymentService paymentService;

    public UpdateMasterPaymentAttachmentCommandHandler(IMasterPaymentAttachmentService masterPaymentAttachmentService,
            IManageAttachmentTypeService manageAttachmentTypeService,
            IManageResourceTypeService manageResourceTypeService,
            IManageEmployeeService manageEmployeeService,
            IAttachmentStatusHistoryService attachmentStatusHistoryService,
            IPaymentStatusHistoryService paymentAttachmentStatusHistoryService,
            IPaymentService paymentService) {
        this.masterPaymentAttachmentService = masterPaymentAttachmentService;
        this.manageAttachmentTypeService = manageAttachmentTypeService;
        this.manageResourceTypeService = manageResourceTypeService;
        this.manageEmployeeService = manageEmployeeService;
        this.attachmentStatusHistoryService = attachmentStatusHistoryService;
        this.paymentAttachmentStatusHistoryService = paymentAttachmentStatusHistoryService;
        this.paymentService = paymentService;
    }

    @Override
    public void handle(UpdateMasterPaymentAttachmentCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Master Payment Attachment ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getEmployee(), "id", "Employee ID cannot be null."));

        ManageEmployeeDto employeeDto = this.manageEmployeeService.findById(command.getEmployee());


        MasterPaymentAttachmentDto masterPaymentAttachmentDto = this.masterPaymentAttachmentService.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(masterPaymentAttachmentDto::setRemark, command.getRemark(), masterPaymentAttachmentDto.getRemark(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(masterPaymentAttachmentDto::setPath, command.getPath(), masterPaymentAttachmentDto.getPath(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(masterPaymentAttachmentDto::setFileName, command.getFileName(), masterPaymentAttachmentDto.getFileName(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(masterPaymentAttachmentDto::setFileWeight, command.getFileWeight(), masterPaymentAttachmentDto.getFileWeight(), update::setUpdate);

        this.updateStatus(masterPaymentAttachmentDto::setStatus, command.getStatus(), masterPaymentAttachmentDto.getStatus(), update::setUpdate);
        this.updateResourceType(masterPaymentAttachmentDto::setResourceType, command.getResourceType(), masterPaymentAttachmentDto.getResourceType().getId(), update::setUpdate);
        this.updateAttachmentType(masterPaymentAttachmentDto::setAttachmentType, command.getAttachmentType(), masterPaymentAttachmentDto.getAttachmentType().getId(), masterPaymentAttachmentDto.getResource().getId(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.masterPaymentAttachmentService.update(masterPaymentAttachmentDto);
            this.deleteAttachmentStatusHistory(employeeDto, masterPaymentAttachmentDto.getResource(), masterPaymentAttachmentDto.getFileName(), masterPaymentAttachmentDto.getAttachmentId());
            if (this.masterPaymentAttachmentService.countByResourceAndAttachmentTypeIsDefault(masterPaymentAttachmentDto.getResource().getId()) == 0) {
                PaymentDto paymentDto = masterPaymentAttachmentDto.getResource();
                paymentDto.setPaymentSupport(false);
                this.paymentService.update(paymentDto);
            }
//            this.createPaymentAttachmentStatusHistory(employeeDto, masterPaymentAttachmentDto.getResource(), masterPaymentAttachmentDto.getFileName());
        }

    }

    //Este es para agregar el History del Payment. Aqui el estado es el del nomenclador Manage Payment Status
//    private void createPaymentAttachmentStatusHistory(ManageEmployeeDto employeeDto, PaymentDto payment, String fileName) {
//
//        PaymentStatusHistoryDto attachmentStatusHistoryDto = new PaymentStatusHistoryDto();
//        attachmentStatusHistoryDto.setId(UUID.randomUUID());
//        attachmentStatusHistoryDto.setDescription("An attachment to the payment was updated. The file name: " + fileName);
//        attachmentStatusHistoryDto.setEmployee(employeeDto);
//        attachmentStatusHistoryDto.setPayment(payment);
//        attachmentStatusHistoryDto.setStatus(payment.getPaymentStatus().getCode() + "-" + payment.getPaymentStatus().getName());
//
//        this.paymentAttachmentStatusHistoryService.create(attachmentStatusHistoryDto);
//    }

    private boolean updateResourceType(Consumer<ResourceTypeDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ResourceTypeDto manageResourceTypeDto = this.manageResourceTypeService.findById(newValue);
            setter.accept(manageResourceTypeDto);
            update.accept(1);

            return true;
        }
        return false;
    }

    private boolean updateAttachmentType(Consumer<AttachmentTypeDto> setter, UUID newValue, UUID oldValue, UUID payment, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            AttachmentTypeDto manageAttachmentTypeDto = this.manageAttachmentTypeService.findById(newValue);
            if (manageAttachmentTypeDto.getDefaults()) {
                RulesChecker.checkRule(new MasterPaymetAttachmentWhitDefaultTrueMustBeUniqueRule(this.masterPaymentAttachmentService, payment, ""));
            }
            setter.accept(manageAttachmentTypeDto);
            update.accept(1);

            return true;
        }
        return false;
    }

    private void updateStatus(Consumer<Status> setter, Status newValue, Status oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);

        }
    }

    private void deleteAttachmentStatusHistory(ManageEmployeeDto employeeDto, PaymentDto payment, String fileName, Long attachmentId) {
        //ManageEmployeeDto employeeDto = employee != null ? this.manageEmployeeService.findById(employee) : null;//Revisar aqui para si el employee no existe ponerle null tambien.
        AttachmentStatusHistoryDto attachmentStatusHistoryDto = new AttachmentStatusHistoryDto();

        attachmentStatusHistoryDto.setId(UUID.randomUUID());
        attachmentStatusHistoryDto.setDescription("An attachment to the payment was update. The file name: " + fileName);
        attachmentStatusHistoryDto.setEmployee(employeeDto);
        attachmentStatusHistoryDto.setPayment(payment);
        attachmentStatusHistoryDto.setStatus(payment.getAttachmentStatus().getCode() + "-" + payment.getAttachmentStatus().getName());
        attachmentStatusHistoryDto.setUpdatedAt(LocalDateTime.now());
        attachmentStatusHistoryDto.setAttachmentId(attachmentId);

        this.attachmentStatusHistoryService.create(attachmentStatusHistoryDto);
    }

}
