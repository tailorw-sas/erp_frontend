package com.kynsoft.finamer.payment.application.command.masterPaymentAttachment.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
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
import java.time.LocalDateTime;
import java.util.UUID;

import java.util.function.Consumer;

import org.springframework.stereotype.Component;

@Component
public class UpdateMasterPaymentAttachmentCommandHandler implements ICommandHandler<UpdateMasterPaymentAttachmentCommand> {

    private final IMasterPaymentAttachmentService masterPaymentAttachmentService;
    private final IPaymentService paymentService;
    private final IManageAttachmentTypeService manageAttachmentTypeService;
    private final IManageResourceTypeService manageResourceTypeService;

    private final IManageEmployeeService manageEmployeeService;
    private final IAttachmentStatusHistoryService attachmentStatusHistoryService;

    public UpdateMasterPaymentAttachmentCommandHandler(IMasterPaymentAttachmentService masterPaymentAttachmentService, 
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
    public void handle(UpdateMasterPaymentAttachmentCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Master Payment Attachment ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getResource(), "payment", "Payment ID cannot be null."));

        MasterPaymentAttachmentDto masterPaymentAttachmentDto = this.masterPaymentAttachmentService.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(masterPaymentAttachmentDto::setRemark, command.getRemark(), masterPaymentAttachmentDto.getRemark(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(masterPaymentAttachmentDto::setPath, command.getPath(), masterPaymentAttachmentDto.getPath(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(masterPaymentAttachmentDto::setFileName, command.getFileName(), masterPaymentAttachmentDto.getFileName(), update::setUpdate);

        this.updateStatus(masterPaymentAttachmentDto::setStatus, command.getStatus(), masterPaymentAttachmentDto.getStatus(), update::setUpdate);
        this.updatePayment(masterPaymentAttachmentDto::setResource, command.getResource(), masterPaymentAttachmentDto.getResource().getId(), update::setUpdate);
        this.updateResourceType(masterPaymentAttachmentDto::setResourceType, command.getResourceType(), masterPaymentAttachmentDto.getResource().getId(), update::setUpdate);
        this.updateAttachmentType(masterPaymentAttachmentDto::setAttachmentType, command.getAttachmentType(), masterPaymentAttachmentDto.getResource().getId(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.masterPaymentAttachmentService.update(masterPaymentAttachmentDto);
            this.deleteAttachmentStatusHistory(null, masterPaymentAttachmentDto.getResource(), masterPaymentAttachmentDto.getFileName());
        }

    }

    private boolean updatePayment(Consumer<PaymentDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            PaymentDto paymentDto = this.paymentService.findById(newValue);
            setter.accept(paymentDto);
            update.accept(1);

            return true;
        }
        return false;
    }

    private boolean updateResourceType(Consumer<ManageResourceTypeDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManageResourceTypeDto manageResourceTypeDto = this.manageResourceTypeService.findById(newValue);
            setter.accept(manageResourceTypeDto);
            update.accept(1);

            return true;
        }
        return false;
    }

    private boolean updateAttachmentType(Consumer<ManageAttachmentTypeDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManageAttachmentTypeDto manageAttachmentTypeDto = this.manageAttachmentTypeService.findById(newValue);
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

    private void deleteAttachmentStatusHistory(UUID employee, PaymentDto payment, String fileName) {
        ManageEmployeeDto employeeDto = employee != null ? this.manageEmployeeService.findById(employee) : null;
        AttachmentStatusHistoryDto attachmentStatusHistoryDto = new AttachmentStatusHistoryDto();
        attachmentStatusHistoryDto.setId(UUID.randomUUID());
        attachmentStatusHistoryDto.setDescription("An attachment to the payment was update. The file name: " + fileName);
        attachmentStatusHistoryDto.setEmployee(employeeDto);
        attachmentStatusHistoryDto.setPayment(payment);
        attachmentStatusHistoryDto.setStatus(Status.ACTIVE);
        attachmentStatusHistoryDto.setUpdatedAt(LocalDateTime.now());

        this.attachmentStatusHistoryService.create(attachmentStatusHistoryDto);
    }

}
