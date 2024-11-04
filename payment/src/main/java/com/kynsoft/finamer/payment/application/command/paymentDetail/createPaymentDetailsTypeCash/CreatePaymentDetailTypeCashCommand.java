package com.kynsoft.finamer.payment.application.command.paymentDetail.createPaymentDetailsTypeCash;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CreatePaymentDetailTypeCashCommand implements ICommand {

    private PaymentDto paymentCash;
    private UUID booking;
    private UUID id;
    private double invoiceAmount;
    private LocalDateTime invoiceDate;
    private boolean applyPayment;
    private boolean createByCredit;

    public CreatePaymentDetailTypeCashCommand(PaymentDto paymentCash, UUID booking, double invoiceAmount, boolean applyPayment, LocalDateTime invoiceDate, boolean createByCredit) {
        this.id = UUID.randomUUID();
        this.paymentCash = paymentCash;
        this.booking = booking;
        this.invoiceAmount = invoiceAmount;
        this.applyPayment = applyPayment;
        this.invoiceDate = invoiceDate;
        this.createByCredit = createByCredit;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreatePaymentDetailTypeCashMessage(id);
    }
}
