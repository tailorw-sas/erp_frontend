package com.kynsoft.finamer.payment.application.command.paymentDetailApplyDeposit.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentTransactionTypeDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckAmountIfDepositBalanceGreaterThanZeroRule;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckApplyDepositRule;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckDepositToApplyDepositRule;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentTransactionTypeService;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import org.springframework.stereotype.Component;

@Component
public class CreatePaymentDetailApplyDepositCommandHandler implements ICommandHandler<CreatePaymentDetailApplyDepositCommand> {

    private final IPaymentDetailService paymentDetailService;
    private final IManagePaymentTransactionTypeService paymentTransactionTypeService;
    private final IPaymentService paymentService;

    public CreatePaymentDetailApplyDepositCommandHandler(IPaymentDetailService paymentDetailService,
                                             IManagePaymentTransactionTypeService paymentTransactionTypeService,
                                             IPaymentService paymentService) {
        this.paymentDetailService = paymentDetailService;
        this.paymentTransactionTypeService = paymentTransactionTypeService;
        this.paymentService = paymentService;
    }

    @Override
    public void handle(CreatePaymentDetailApplyDepositCommand command) {

        ManagePaymentTransactionTypeDto paymentTransactionTypeDto = this.paymentTransactionTypeService.findById(command.getTransactionType());
        PaymentDetailDto paymentDetailDto = this.paymentDetailService.findById(command.getPaymentDetail());
        PaymentDto paymentUpdate = paymentDetailDto.getPayment();

        RulesChecker.checkRule(new CheckApplyDepositRule(paymentTransactionTypeDto.getApplyDeposit()));
        RulesChecker.checkRule(new CheckDepositToApplyDepositRule(paymentDetailDto.getTransactionType().getDeposit()));
        RulesChecker.checkRule(new CheckAmountIfDepositBalanceGreaterThanZeroRule(command.getAmount(), paymentUpdate.getDepositBalance()));

        ConsumerUpdate updatePayment = new ConsumerUpdate();
        UpdateIfNotNull.updateDouble(paymentUpdate::setDepositBalance, paymentUpdate.getDepositBalance() - command.getAmount(), updatePayment::setUpdate);
        UpdateIfNotNull.updateDouble(paymentUpdate::setIdentified, paymentUpdate.getIdentified() + command.getAmount(), updatePayment::setUpdate);
        UpdateIfNotNull.updateDouble(paymentUpdate::setNotIdentified, paymentUpdate.getPaymentAmount() - paymentUpdate.getIdentified(), updatePayment::setUpdate);

        this.paymentDetailService.create(new PaymentDetailDto(
                command.getId(),
                command.getStatus(),
                paymentUpdate,
                paymentTransactionTypeDto,
                command.getAmount(),
                command.getRemark(),
                paymentDetailDto
        ));

        if (updatePayment.getUpdate() > 0) {
            this.paymentService.update(paymentUpdate);
        }

    }
}
