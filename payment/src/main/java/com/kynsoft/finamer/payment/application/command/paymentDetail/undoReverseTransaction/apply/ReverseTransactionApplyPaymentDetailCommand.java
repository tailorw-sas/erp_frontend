package com.kynsoft.finamer.payment.application.command.paymentDetail.undoReverseTransaction.apply;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ReverseTransactionApplyPaymentDetailCommand implements ICommand {

    private UUID paymentDetail;
    private UUID employee;

    private PaymentDto paymentResponse;

    public ReverseTransactionApplyPaymentDetailCommand(UUID paymentDetail, UUID employee) {
        this.paymentDetail = paymentDetail;
        this.employee = employee;
    }

    @Override
    public ICommandMessage getMessage() {
        return new ReverseTransactionApplyPaymentDetailMessage(paymentResponse);
    }
}
