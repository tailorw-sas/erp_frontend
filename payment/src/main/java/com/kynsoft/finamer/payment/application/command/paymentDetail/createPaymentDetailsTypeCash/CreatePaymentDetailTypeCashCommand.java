package com.kynsoft.finamer.payment.application.command.paymentDetail.createPaymentDetailsTypeCash;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreatePaymentDetailTypeCashCommand implements ICommand {

    private PaymentDto paymentCash;
    private UUID booking;
    private UUID id;
    private double invoiceAmount;
    private boolean applyPayment;

    public CreatePaymentDetailTypeCashCommand(PaymentDto paymentCash, UUID booking, double invoiceAmount, boolean applyPayment) {
        this.id = UUID.randomUUID();
        this.paymentCash = paymentCash;
        this.booking = booking;
        this.invoiceAmount = invoiceAmount;
        this.applyPayment = applyPayment;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreatePaymentDetailTypeCashMessage(id);
    }
}
