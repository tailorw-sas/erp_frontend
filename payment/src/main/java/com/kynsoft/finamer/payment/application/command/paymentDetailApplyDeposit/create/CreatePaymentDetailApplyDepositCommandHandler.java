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
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckGreaterThanOrEqualToTheTransactionAmountRule;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckPaymentDetailAmountGreaterThanZeroRule;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentTransactionTypeService;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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

        RulesChecker.checkRule(new CheckPaymentDetailAmountGreaterThanZeroRule(command.getAmount()));

        ManagePaymentTransactionTypeDto paymentTransactionTypeDto = this.paymentTransactionTypeService.findById(command.getTransactionType());
        PaymentDetailDto paymentDetailDto = this.paymentDetailService.findById(command.getPaymentDetail());
        PaymentDto paymentUpdate = paymentDetailDto.getPayment();

        RulesChecker.checkRule(new CheckApplyDepositRule(paymentTransactionTypeDto.getApplyDeposit()));
        RulesChecker.checkRule(new CheckDepositToApplyDepositRule(paymentDetailDto.getTransactionType().getDeposit()));
        RulesChecker.checkRule(new CheckAmountIfDepositBalanceGreaterThanZeroRule(command.getAmount(), paymentUpdate.getDepositBalance()));
        RulesChecker.checkRule(new CheckGreaterThanOrEqualToTheTransactionAmountRule(command.getAmount(), paymentDetailDto.getAmount()));

        ConsumerUpdate updatePayment = new ConsumerUpdate();
        UpdateIfNotNull.updateDouble(paymentUpdate::setDepositBalance, paymentUpdate.getDepositBalance() - command.getAmount(), updatePayment::setUpdate);
        UpdateIfNotNull.updateDouble(paymentUpdate::setIdentified, paymentUpdate.getIdentified() + command.getAmount(), updatePayment::setUpdate);
        UpdateIfNotNull.updateDouble(paymentUpdate::setNotIdentified, paymentUpdate.getPaymentAmount() - paymentUpdate.getIdentified(), updatePayment::setUpdate);

        //TODO: Se debe de validar esta variable para que cumpla con el Close Operation
        LocalDate transactionDate = LocalDate.now();
        PaymentDetailDto children = new PaymentDetailDto(
                command.getId(),
                command.getStatus(),
                paymentUpdate,
                paymentTransactionTypeDto,
                command.getAmount(),
                command.getRemark(),
                null,
                null,
                null,
                transactionDate,
                null,
                null,
                null,
                null,
                null,
                null
        );

        this.paymentDetailService.create(children);

        List<PaymentDetailDto> updateChildrens = new ArrayList<>();
        updateChildrens.addAll(paymentDetailDto.getChildren());
        updateChildrens.add(children);
        paymentDetailDto.setChildren(updateChildrens);
        paymentDetailService.update(paymentDetailDto);

        this.paymentService.update(paymentUpdate);
        command.setPaymentResponse(paymentUpdate);

    }
}
