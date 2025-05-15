package com.kynsoft.finamer.payment.application.command.paymentDetailSplitDeposit.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentTransactionTypeDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentCloseOperationDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.*;
import com.kynsoft.finamer.payment.domain.services.*;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.UUID;

@Component
public class CreatePaymentDetailSplitDepositCommandHandler implements ICommandHandler<CreatePaymentDetailSplitDepositCommand> {

    private final IPaymentDetailService paymentDetailService;
    private final IManagePaymentTransactionTypeService paymentTransactionTypeService;
    private final IPaymentService paymentService;

    private final IManageEmployeeService manageEmployeeService;
    private final IPaymentCloseOperationService paymentCloseOperationService;


    public CreatePaymentDetailSplitDepositCommandHandler(IPaymentDetailService paymentDetailService,
                                                         IManagePaymentTransactionTypeService paymentTransactionTypeService,
                                                         IPaymentService paymentService,
                                                         IManageEmployeeService manageEmployeeService,
                                                         IPaymentCloseOperationService paymentCloseOperationService) {
        this.paymentDetailService = paymentDetailService;
        this.paymentTransactionTypeService = paymentTransactionTypeService;
        this.paymentService = paymentService;

        this.manageEmployeeService = manageEmployeeService;
        this.paymentCloseOperationService = paymentCloseOperationService;
    }

    @Override
    public void handle(CreatePaymentDetailSplitDepositCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getEmployee(), "id", "Employee ID cannot be null."));

        RulesChecker.checkRule(new CheckPaymentDetailAmountSplitGreaterThanZeroRule(command.getAmount()));

        ManagePaymentTransactionTypeDto paymentTransactionTypeDto = this.paymentTransactionTypeService.findById(command.getTransactionType());
        RulesChecker.checkRule(new CheckDepositTransactionTypeRule(paymentTransactionTypeDto.getDeposit()));

        PaymentDetailDto paymentDetailDto = this.paymentDetailService.findById(command.getPaymentDetail());
        RulesChecker.checkRule(new CheckDepositToSplitRule(paymentDetailDto.getTransactionType().getDeposit()));
        RulesChecker.checkRule(new CheckSplitAmountRule(command.getAmount(), paymentDetailDto.getAmount()));
        RulesChecker.checkRule(new CheckPaymentDetailDepositTypeIsApplyRule(paymentDetailDto.getPaymentDetails()));

        ConsumerUpdate updatePayment = new ConsumerUpdate();
        UpdateIfNotNull.updateDouble(paymentDetailDto::setAmount, paymentDetailDto.getAmount() + command.getAmount(), updatePayment::setUpdate);

        PaymentDetailDto split = new PaymentDetailDto(
                command.getId(),
                command.getStatus(),
                paymentDetailDto.getPayment(),
                paymentTransactionTypeDto,
                command.getAmount() * -1,
                command.getRemark(),
                null,
                null,
                null,
                transactionDate(paymentDetailDto.getPayment().getHotel().getId()),
                null,
                null,
                null,
                null,
                null,
                null,
                false
        );
        split.setApplyDepositValue(command.getAmount());
        paymentDetailDto.setApplyDepositValue(paymentDetailDto.getApplyDepositValue() - command.getAmount());
        paymentDetailService.create(split);
        paymentDetailService.update(paymentDetailDto);

        command.setPaymentResponse(this.paymentService.findById(paymentDetailDto.getPayment().getId()));
    }

    private OffsetDateTime transactionDate(UUID hotel) {
        PaymentCloseOperationDto closeOperationDto = this.paymentCloseOperationService.findByHotelId(hotel);

        if (DateUtil.getDateForCloseOperation(closeOperationDto.getBeginDate(), closeOperationDto.getEndDate())) {
            return OffsetDateTime.now(ZoneId.of("UTC"));
        }
        return OffsetDateTime.of(closeOperationDto.getEndDate(), LocalTime.now(ZoneId.of("UTC")), ZoneOffset.UTC);
    }
}
