package com.kynsoft.finamer.payment.application.command.paymentDetail.delete;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckDeletePaymentDetailsApplyPaymentRule;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckDeletePaymentDetailsDepositRule;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentStatusService;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class DeletePaymentDetailCommandHandler implements ICommandHandler<DeletePaymentDetailCommand> {

    private final IPaymentDetailService service;
    private final IPaymentService paymentService;
    private final IManagePaymentStatusService statusService;

    public DeletePaymentDetailCommandHandler(IPaymentDetailService service,
            IPaymentService paymentService,
            IManagePaymentStatusService statusService) {
        this.service = service;
        this.paymentService = paymentService;
        this.statusService = statusService;
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

                //El valor del Detalle de tipo cash fue restado al Payment Amount en el create, en el delete debe de ser sumado.
                UpdateIfNotNull.updateDouble(update::setPaymentBalance, update.getPaymentBalance() + delete.getAmount(), updatePayment::setUpdate);

            }
            service.delete(delete);
        }

        //Other Deductions
        if (!delete.getTransactionType().getCash() && !delete.getTransactionType().getDeposit() && !delete.getTransactionType().getApplyDeposit()) {
            if (!delete.isReverseTransaction()) {
                UpdateIfNotNull.updateDouble(update::setOtherDeductions, update.getOtherDeductions() - delete.getAmount(), updatePayment::setUpdate);
                service.delete(delete);
            }
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
                service.delete(delete);
            }
        }

        if (delete.getTransactionType().getApplyDeposit()) {
            if (!delete.isReverseTransaction()) {
                UpdateIfNotNull.updateDouble(update::setDepositBalance, update.getDepositBalance() + delete.getAmount(), updatePayment::setUpdate);
                UpdateIfNotNull.updateDouble(update::setIdentified, update.getIdentified() - delete.getAmount(), updatePayment::setUpdate);
                UpdateIfNotNull.updateDouble(update::setNotIdentified, update.getPaymentAmount() - update.getIdentified(), updatePayment::setUpdate);
                UpdateIfNotNull.updateDouble(update::setApplied, update.getApplied() - delete.getAmount(), updatePayment::setUpdate);
                update.setPaymentStatus(this.statusService.findByConfirmed());
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
            //service.delete(delete);
        }

        paymentService.update(update);
    }

}
