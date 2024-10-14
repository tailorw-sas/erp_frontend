package com.kynsoft.finamer.payment.application.command.paymentDetail.undoApplication;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.application.command.paymentDetail.delete.DeletePaymentDetailCommand;
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
        //Comprobar que la fecha sea del dia actual
        //Comprobar que el paymentDetails sea de tipo Apply Deposit o Cash
        //RulesChecker.checkRule(new CheckIsTypeCashRule(paymentDetailDto));
        ManageBookingDto bookingDto = paymentDetailDto.getManageBooking();
        paymentDetailDto.setManageBooking(null);
        this.paymentDetailService.update(paymentDetailDto);
        if (!paymentDetailDto.isReverseTransaction()) {
            command.getMediator().send(new UndoApplyPaymentDetailCommand(paymentDetailDto.getId(), bookingDto.getId()));
        }
        command.getMediator().send(new DeletePaymentDetailCommand(command.getPaymentDetail(), null, true));//TODO: es posible que tengamos que agregar el employee a la peticion.
    }
}
