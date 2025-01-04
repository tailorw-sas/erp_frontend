package com.kynsoft.finamer.payment.application.command.paymentDetail.undoReverseTransaction;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.application.command.paymentDetail.undoReverseTransaction.apply.ReverseTransactionApplyPaymentDetailCommand;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import org.springframework.stereotype.Component;


@Component
public class CreateUndoReverseTransactionCommandHandler implements ICommandHandler<CreateUndoReverseTransactionCommand> {

    private final IPaymentDetailService paymentDetailService;
    private final IPaymentService paymentService;

    public CreateUndoReverseTransactionCommandHandler(IPaymentDetailService paymentDetailService, IPaymentService paymentService) {
        this.paymentDetailService = paymentDetailService;
        this.paymentService = paymentService;
    }

    @Override
    public void handle(CreateUndoReverseTransactionCommand command) {
        PaymentDetailDto paymentDetailDto = this.paymentDetailService.findByPaymentDetailId(command.getReverseFromParentId());

        //Aqui recalculo la cabecera del Payment segun el tipo de trx
        if (paymentDetailDto.getTransactionType().getApplyDeposit()) {
            this.calculateReverseApplyDeposit(paymentDetailDto.getPayment(), paymentDetailDto);
        } else if (paymentDetailDto.getTransactionType().getCash()) {
            this.calculateReverseCash(paymentDetailDto.getPayment(), paymentDetailDto.getAmount());
        } else {
            this.calculateReverseOtherDeductions(paymentDetailDto.getPayment(), paymentDetailDto.getAmount());
        }

        //Aqui ejecuto para que se vuelva a aplicar pago.
        command.getMediator().send(new ReverseTransactionApplyPaymentDetailCommand(paymentDetailDto.getId(), command.getEmployee()));
    }

    private void calculateReverseApplyDeposit(PaymentDto paymentDto, PaymentDetailDto newDetailDto) {
        paymentDto.setDepositBalance(paymentDto.getDepositBalance() - newDetailDto.getAmount());
        paymentDto.setNotApplied(paymentDto.getNotApplied() + newDetailDto.getAmount()); // TODO: al hacer un applied deposit el notApplied aumenta.
        paymentDto.setIdentified(paymentDto.getIdentified() + newDetailDto.getAmount());
        paymentDto.setNotIdentified(paymentDto.getPaymentAmount() - paymentDto.getIdentified());

        this.paymentService.update(paymentDto);
    }

    private void calculateReverseOtherDeductions(PaymentDto paymentDto, double amount) {
        paymentDto.setOtherDeductions(paymentDto.getOtherDeductions() + amount);

        this.paymentService.update(paymentDto);
    }

    private void calculateReverseCash(PaymentDto paymentDto, double amount) {
        paymentDto.setIdentified(paymentDto.getIdentified() + amount);
        paymentDto.setNotIdentified(paymentDto.getNotIdentified() - amount);

        paymentDto.setApplied(paymentDto.getApplied() + amount);
        paymentDto.setNotApplied(paymentDto.getNotApplied() - amount);
        paymentDto.setPaymentBalance(paymentDto.getPaymentBalance() - amount);

        this.paymentService.update(paymentDto);
    }

}
