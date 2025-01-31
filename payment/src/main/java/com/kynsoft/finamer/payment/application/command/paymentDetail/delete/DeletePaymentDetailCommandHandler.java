package com.kynsoft.finamer.payment.application.command.paymentDetail.delete;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.payment.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentStatusHistoryDto;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckDeletePaymentDetailsApplyPaymentRule;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckDeletePaymentDetailsDepositRule;
import com.kynsoft.finamer.payment.domain.services.IManageEmployeeService;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentStatusService;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import com.kynsoft.finamer.payment.domain.services.IPaymentStatusHistoryService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class DeletePaymentDetailCommandHandler implements ICommandHandler<DeletePaymentDetailCommand> {

    private final IPaymentDetailService service;
    private final IPaymentService paymentService;
    private final IManagePaymentStatusService statusService;
    private final IPaymentStatusHistoryService paymentAttachmentStatusHistoryService;
    private final IManageEmployeeService manageEmployeeService;

    public DeletePaymentDetailCommandHandler(IPaymentDetailService service,
            IPaymentService paymentService,
            IManagePaymentStatusService statusService,
            IPaymentStatusHistoryService paymentAttachmentStatusHistoryService,
            IManageEmployeeService manageEmployeeService) {
        this.service = service;
        this.paymentService = paymentService;
        this.statusService = statusService;
        this.paymentAttachmentStatusHistoryService = paymentAttachmentStatusHistoryService;
        this.manageEmployeeService = manageEmployeeService;
    }

    @Override
    public void handle(DeletePaymentDetailCommand command) {

        PaymentDetailDto delete = this.service.findById(command.getId());

        PaymentDto update = this.paymentService.findById(delete.getPayment().getId());
        //RulesChecker.checkRule(new CheckValidateHourForDeleteRule(delete.getCreatedAt()));
        if (!command.isUndoApplication()) {
            if (delete.getTransactionType().getCash() || delete.getTransactionType().getApplyDeposit()) {
                RulesChecker.checkRule(new CheckDeletePaymentDetailsApplyPaymentRule(delete));
            }
            if (delete.getTransactionType().getDeposit()) {
                RulesChecker.checkRule(new CheckDeletePaymentDetailsDepositRule(delete));
            }
        }

        ConsumerUpdate updatePayment = new ConsumerUpdate();
        //identified and notIdentified
        if (delete.getTransactionType().getCash()) {
            if (!delete.isReverseTransaction()) {
                UpdateIfNotNull.updateDouble(update::setIdentified, update.getIdentified() - delete.getAmount(), updatePayment::setUpdate);
                UpdateIfNotNull.updateDouble(update::setNotIdentified, update.getNotIdentified() + delete.getAmount(), updatePayment::setUpdate);
                UpdateIfNotNull.updateDouble(update::setApplied, update.getApplied() - delete.getAmount(), updatePayment::setUpdate);
                UpdateIfNotNull.updateDouble(update::setNotApplied, update.getNotApplied() + delete.getAmount(), updatePayment::setUpdate);
                update.setPaymentStatus(this.statusService.findByConfirmed());
                ManageEmployeeDto employeeDto = command.getEmployee() != null ? this.manageEmployeeService.findById(command.getEmployee()) : null;
                this.createPaymentAttachmentStatusHistory(employeeDto, update);
                update.setApplyPayment(this.service.countByApplyPaymentAndPaymentId(update.getId()) > 0);

                //El valor del Detalle de tipo cash fue restado al Payment Amount en el create, en el delete debe de ser sumado.
                UpdateIfNotNull.updateDouble(update::setPaymentBalance, update.getPaymentBalance() + delete.getAmount(), updatePayment::setUpdate);

            }
            service.delete(delete);
        }

        //Other Deductions
        if (!delete.getTransactionType().getCash() && !delete.getTransactionType().getDeposit() && !delete.getTransactionType().getApplyDeposit()) {
            if (!delete.isReverseTransaction()) {
                UpdateIfNotNull.updateDouble(update::setOtherDeductions, update.getOtherDeductions() - delete.getAmount(), updatePayment::setUpdate);
                update.setApplyPayment(this.service.countByApplyPaymentAndPaymentId(update.getId()) > 0);
            }
            service.delete(delete);
        }

        //Deposit Amount and Deposit Balance
        if (delete.getTransactionType().getDeposit()) {
            if (!delete.isReverseTransaction()) {
                // Crear regla que valide que el Amount ingresado no debe de ser mayor que el valor del Payment Balance y mayor que cero.
                //Recordar aqui, en los calculos que se realizan, que los deposit estan almacenados en negativo.
                UpdateIfNotNull.updateDouble(update::setDepositAmount, update.getDepositAmount() + delete.getAmount(), updatePayment::setUpdate);
                UpdateIfNotNull.updateDouble(update::setDepositBalance, update.getDepositBalance() + delete.getAmount(), updatePayment::setUpdate);

                UpdateIfNotNull.updateDouble(update::setPaymentBalance, update.getPaymentBalance() - delete.getAmount(), updatePayment::setUpdate);
                UpdateIfNotNull.updateDouble(update::setNotApplied, update.getNotApplied() - delete.getAmount(), updatePayment::setUpdate);
                update.setPaymentStatus(this.statusService.findByConfirmed());
                ManageEmployeeDto employeeDto = command.getEmployee() != null ? this.manageEmployeeService.findById(command.getEmployee()) : null;
                this.createPaymentAttachmentStatusHistory(employeeDto, update);
                update.setApplyPayment(this.service.countByApplyPaymentAndPaymentId(update.getId()) > 0);
                update.setHasDetailTypeDeposit(this.service.countByPaymentDetailIdAndTransactionTypeDeposit(update.getId()) > 0);
            }
            service.delete(delete);
        }

        if (delete.getTransactionType().getApplyDeposit()) {
            if (!delete.isReverseTransaction()) {
                UpdateIfNotNull.updateDouble(update::setDepositBalance, update.getDepositBalance() + delete.getAmount(), updatePayment::setUpdate);
                UpdateIfNotNull.updateDouble(update::setIdentified, update.getIdentified() - delete.getAmount(), updatePayment::setUpdate);
                UpdateIfNotNull.updateDouble(update::setNotIdentified, update.getPaymentAmount() - update.getIdentified(), updatePayment::setUpdate);
                UpdateIfNotNull.updateDouble(update::setApplied, update.getApplied() - delete.getAmount(), updatePayment::setUpdate);
                update.setPaymentStatus(this.statusService.findByConfirmed());
                ManageEmployeeDto employeeDto = command.getEmployee() != null ? this.manageEmployeeService.findById(command.getEmployee()) : null;
                this.createPaymentAttachmentStatusHistory(employeeDto, update);
                update.setApplyPayment(this.service.countByApplyPaymentAndPaymentId(update.getId()) > 0);
            }
            //Para este caso no se elimina, dado que el objeto esta relacionado con otro objeto del mismo tipo, por tanto voy a actuar modificando
            //los valores y poniendo en inactivo el apply deposit que se trata de eliminar.
            PaymentDetailDto parent = this.service.findByPaymentDetailId(delete.getParentId());
            List<PaymentDetailDto> childrens = new ArrayList<>();
            for (PaymentDetailDto children : parent.getChildren()) {
                if (!children.getId().equals(delete.getId())) {
                    childrens.add(children);
                }
            }

            parent.setChildren(childrens);
            parent.setApplyDepositValue(parent.getApplyDepositValue() + delete.getAmount());
            this.service.update(parent);
            this.service.delete(delete);
        }

        paymentService.update(update);
    }

    //Este es para agregar el History del Payment. Aqui el estado es el del nomenclador Manage Payment Status
    private void createPaymentAttachmentStatusHistory(ManageEmployeeDto employeeDto, PaymentDto payment) {

        PaymentStatusHistoryDto attachmentStatusHistoryDto = new PaymentStatusHistoryDto();
        attachmentStatusHistoryDto.setId(UUID.randomUUID());
        attachmentStatusHistoryDto.setDescription("Update Payment.");
        attachmentStatusHistoryDto.setEmployee(employeeDto);
        attachmentStatusHistoryDto.setPayment(payment);
        attachmentStatusHistoryDto.setStatus(payment.getPaymentStatus().getCode() + "-" + payment.getPaymentStatus().getName());

        this.paymentAttachmentStatusHistoryService.create(attachmentStatusHistoryDto);
    }

}
