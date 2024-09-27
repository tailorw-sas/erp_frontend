package com.kynsoft.finamer.payment.application.command.paymentDetail.undoApplication;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.application.command.paymentDetail.undoApplyPayment.UndoApplyPaymentDetailCommand;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.rules.undoApplication.CheckApplyPaymentRule;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import org.springframework.stereotype.Component;

@Component
public class CreateUndoApplicationCommandHandler implements ICommandHandler<CreateUndoApplicationCommand> {

    private final IPaymentDetailService paymentDetailService;

    public CreateUndoApplicationCommandHandler(IPaymentDetailService paymentDetailService) {
        this.paymentDetailService = paymentDetailService;
    }

    @Override
    public void handle(CreateUndoApplicationCommand command) {
        PaymentDetailDto paymentDetailDto = this.paymentDetailService.findById(command.getPaymentDetail());
        RulesChecker.checkRule(new CheckApplyPaymentRule(paymentDetailDto.getApplayPayment()));

        ManageBookingDto bookingDto = paymentDetailDto.getManageBooking();
        paymentDetailDto.setManageBooking(null);
        this.paymentDetailService.update(paymentDetailDto);

        command.getMediator().send(new UndoApplyPaymentDetailCommand(paymentDetailDto.getId(), bookingDto.getId()));
    }
}
