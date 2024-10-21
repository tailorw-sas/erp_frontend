package com.kynsoft.finamer.payment.application.command.paymentDetail.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.services.*;
import org.springframework.stereotype.Component;

@Component
public class UpdatePaymentDetailCommandHandler implements ICommandHandler<UpdatePaymentDetailCommand> {

    private final IPaymentDetailService paymentDetailService;

    public UpdatePaymentDetailCommandHandler(IPaymentDetailService paymentDetailService) {
        this.paymentDetailService = paymentDetailService;
    }

    @Override
    public void handle(UpdatePaymentDetailCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Payment Detail ID cannot be null."));

        PaymentDetailDto paymentDetailDto = this.paymentDetailService.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(paymentDetailDto::setRemark, command.getRemark(), paymentDetailDto.getRemark(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.paymentDetailService.update(paymentDetailDto);
        }

    }

}
