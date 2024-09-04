package com.kynsoft.finamer.payment.application.command.paymentDetail.createPaymentDetailsTypeApplyDeposit;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreatePaymentDetailTypeApplyDepositCommand implements ICommand {

    private PaymentDto payment;
    private ManageBookingDto booking;
    private PaymentDetailDto parentDetailDto;
    private UUID id;

    private PaymentDetailDto newDetailDto;

    public CreatePaymentDetailTypeApplyDepositCommand(PaymentDto payment, ManageBookingDto booking, PaymentDetailDto parentDetailDto) {
        this.payment = payment;
        this.booking = booking;
        this.parentDetailDto = parentDetailDto;
        this.id = UUID.randomUUID();
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreatePaymentDetailTypeApplyDepositMessage(newDetailDto);
    }
}
