package com.kynsoft.finamer.payment.application.command.paymentDetail.delete;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.payment.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentStatusHistoryDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckValidateHourForDeleteRule;
import com.kynsoft.finamer.payment.domain.services.IManageEmployeeService;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import com.kynsoft.finamer.payment.domain.services.IPaymentStatusHistoryService;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class DeletePaymentDetailCommandHandler implements ICommandHandler<DeletePaymentDetailCommand> {

    private final IPaymentDetailService service;
    private final IPaymentService paymentService;

    private final IManageEmployeeService manageEmployeeService;

    private final IPaymentStatusHistoryService paymentAttachmentStatusHistoryService;

    public DeletePaymentDetailCommandHandler(IPaymentDetailService service,
            IPaymentService paymentService,
            IManageEmployeeService manageEmployeeService,
            IPaymentStatusHistoryService paymentAttachmentStatusHistoryService) {
        this.service = service;
        this.paymentService = paymentService;
        this.manageEmployeeService = manageEmployeeService;
        this.paymentAttachmentStatusHistoryService = paymentAttachmentStatusHistoryService;
    }

    @Override
    public void handle(DeletePaymentDetailCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getEmployee(), "id", "Employee ID cannot be null."));
        ManageEmployeeDto employeeDto = this.manageEmployeeService.findById(command.getEmployee());

        PaymentDetailDto delete = this.service.findById(command.getId());

        PaymentDto update = delete.getPayment();
        RulesChecker.checkRule(new CheckValidateHourForDeleteRule(delete.getCreatedAt()));

        ConsumerUpdate updatePayment = new ConsumerUpdate();
        String msg = "";
        //identified and notIdentified
        if (delete.getTransactionType().getCash()) {
            UpdateIfNotNull.updateDouble(update::setIdentified, update.getIdentified() - delete.getAmount(), updatePayment::setUpdate);
            UpdateIfNotNull.updateDouble(update::setNotIdentified, update.getNotIdentified() + delete.getAmount(), updatePayment::setUpdate);
            //El valor del Detalle de tipo cash fue restado al Payment Amount en el create, en el delete debe de ser sumado.
            UpdateIfNotNull.updateDouble(update::setPaymentBalance, update.getPaymentBalance() + delete.getAmount(), updatePayment::setUpdate);

            service.delete(delete);
            msg = "Deleting Payment Detail with ID: ";
        }

        //Other Deductions
        if (!delete.getTransactionType().getCash() && !delete.getTransactionType().getDeposit() && !delete.getTransactionType().getApplyDeposit()) {
            UpdateIfNotNull.updateDouble(update::setOtherDeductions, update.getOtherDeductions() - delete.getAmount(), updatePayment::setUpdate);
            service.delete(delete);
            msg = "Deleting Payment Detail with ID: ";
        }

        //Deposit Amount and Deposit Balance
        if (delete.getTransactionType().getDeposit()) {
            // Crear regla que valide que el Amount ingresado no debe de ser mayor que el valor del Payment Balance y mayor que cero.
            UpdateIfNotNull.updateDouble(update::setDepositAmount, update.getDepositAmount() + delete.getAmount(), updatePayment::setUpdate);
            UpdateIfNotNull.updateDouble(update::setDepositBalance, update.getDepositBalance() + delete.getAmount(), updatePayment::setUpdate);
            UpdateIfNotNull.updateDouble(update::setPaymentBalance, update.getPaymentBalance() - delete.getAmount(), updatePayment::setUpdate);
            service.delete(delete);
            msg = "Deleting Deposit Detail with ID: ";
        }

        if (delete.getTransactionType().getApplyDeposit()) {
            UpdateIfNotNull.updateDouble(update::setDepositBalance, update.getDepositBalance() + delete.getAmount(), updatePayment::setUpdate);
            UpdateIfNotNull.updateDouble(update::setIdentified, update.getIdentified() - delete.getAmount(), updatePayment::setUpdate);
            UpdateIfNotNull.updateDouble(update::setNotIdentified, update.getPaymentAmount() - update.getIdentified(), updatePayment::setUpdate);
            //Para este caso no se elimina, dado que el objeto esta relacionado con otro objeto del mismo tipo, por tanto voy a actuar modificando
            //los valores y poniendo en inactivo el apply deposit que se trata de eliminar.
            delete.setStatus(Status.INACTIVE);
            service.update(delete);
            msg = "Deleting Apply Deposit Detail with ID: ";
        }

        paymentService.update(update);
//        createPaymentAttachmentStatusHistory(employeeDto, update, delete.getPaymentDetailId(), msg);
    }

    //Este es para agregar el History del Payment. Aqui el estado es el del nomenclador Manage Payment Status
//    private void createPaymentAttachmentStatusHistory(ManageEmployeeDto employeeDto, PaymentDto payment, Long paymentDetail, String msg) {
//
//        PaymentStatusHistoryDto attachmentStatusHistoryDto = new PaymentStatusHistoryDto();
//        attachmentStatusHistoryDto.setId(UUID.randomUUID());
//        attachmentStatusHistoryDto.setDescription(msg + paymentDetail);
//        attachmentStatusHistoryDto.setEmployee(employeeDto);
//        attachmentStatusHistoryDto.setPayment(payment);
//        attachmentStatusHistoryDto.setStatus(payment.getPaymentStatus().getCode() + "-" + payment.getPaymentStatus().getName());
//
//        this.paymentAttachmentStatusHistoryService.create(attachmentStatusHistoryDto);
//    }

}
